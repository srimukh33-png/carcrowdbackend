package com.carcrowd.service;

import com.carcrowd.dto.customer.*;
import com.carcrowd.entity.PhoneVerificationToken;
import com.carcrowd.entity.User;
import com.carcrowd.exception.BadRequestException;
import com.carcrowd.exception.ResourceNotFoundException;
import com.carcrowd.repository.PhoneVerificationTokenRepository;
import com.carcrowd.repository.UserRepository;
import com.carcrowd.security.GoogleTokenVerifierService;
import com.carcrowd.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service
public class CustomerAuthService {

    private static final Logger log = LoggerFactory.getLogger(CustomerAuthService.class);

    private final GoogleTokenVerifierService googleVerifier;
    private final UserRepository userRepository;
    private final PhoneVerificationTokenRepository tokenRepository;
    private final JwtTokenProvider tokenProvider;
    private final boolean isDevOrTest;
    private final SecureRandom secureRandom = new SecureRandom();

    public CustomerAuthService(GoogleTokenVerifierService googleVerifier,
                               UserRepository userRepository,
                               PhoneVerificationTokenRepository tokenRepository,
                               JwtTokenProvider tokenProvider,
                               Environment environment) {
        this.googleVerifier = googleVerifier;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.tokenProvider = tokenProvider;

        String[] activeProfiles = environment.getActiveProfiles();
        this.isDevOrTest = Arrays.stream(activeProfiles)
            .anyMatch(p -> p.equalsIgnoreCase("test") || p.equalsIgnoreCase("h2") || p.equalsIgnoreCase("dev"));
    }

    @Transactional
    public AuthResult processGoogleAuth(GoogleAuthRequest request) {
        GoogleUserPayload payload = googleVerifier.verifyToken(request.getIdToken());

        String sub = payload.getSub();
        if (sub == null || sub.isBlank()) {
            throw new BadRequestException("Google Identity token missing stable sub identifier");
        }

        // 1. Identify customer strictly by Google's stable subject ID (sub)
        Optional<User> existingUserOpt = userRepository.findByGoogleSub(sub);

        User user;
        if (existingUserOpt.isPresent()) {
            user = existingUserOpt.get();
            // Update profile metadata if changed
            if (payload.getName() != null && !payload.getName().isBlank()) {
                user.setFullName(payload.getName());
            }
            if (payload.getPicture() != null && !payload.getPicture().isBlank()) {
                user.setProfilePictureUrl(payload.getPicture());
            }
            user.setUpdatedAt(LocalDateTime.now());
            user = userRepository.save(user);
            log.info("Returning customer authenticated via Google sub: {}", sub);
        } else {
            // New user registration
            String name = (payload.getName() != null && !payload.getName().isBlank())
                ? payload.getName()
                : "CarCrowd User";
            user = new User(sub, payload.getEmail(), name, payload.getPicture());
            user = userRepository.save(user);
            log.info("New customer registered with Google sub: {} / email: {}", sub, payload.getEmail());
        }

        // Generate customer JWT token with ROLE_CUSTOMER and userId claim
        String sessionToken = tokenProvider.generateToken(user.getEmail(), "ROLE_CUSTOMER", user.getId());

        CustomerProfileDTO profileDTO = toProfileDTO(user);
        return new AuthResult(sessionToken, profileDTO);
    }

    @Transactional(readOnly = true)
    public User getAuthenticatedUserEntity(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("Customer authentication required");
        }

        String principal = authentication.getName();
        // 1. Try finding by email
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(principal);
        if (userOpt.isPresent()) {
            return userOpt.get();
        }

        // 2. Try finding by Google Sub
        userOpt = userRepository.findByGoogleSub(principal);
        if (userOpt.isPresent()) {
            return userOpt.get();
        }

        throw new ResourceNotFoundException("Customer account not found for principal: " + principal);
    }

    @Transactional(readOnly = true)
    public CustomerProfileDTO getProfile(Authentication authentication) {
        User user = getAuthenticatedUserEntity(authentication);
        return toProfileDTO(user);
    }

    @Transactional
    public CustomerProfileDTO savePhoneNumber(Authentication authentication, UpdatePhoneRequest request) {
        User user = getAuthenticatedUserEntity(authentication);
        String normalizedPhone = normalizePhone(request.getPhoneNumber());

        if (normalizedPhone.length() != 10) {
            throw new BadRequestException("Please enter a valid 10-digit mobile number.");
        }

        // Check if phone number is already verified and linked to another customer
        if (userRepository.existsByPhoneNumberAndPhoneVerifiedTrue(normalizedPhone)
            && !normalizedPhone.equals(user.getPhoneNumber())) {
            throw new BadRequestException("This mobile number is already linked to another CarCrowd account.");
        }

        user.setPhoneNumber(normalizedPhone);
        user.setPhoneVerified(true);
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        log.info("Phone number updated for customer ID: {}", user.getId());
        return toProfileDTO(savedUser);
    }

    @Transactional
    public OtpSentResponse initiatePhoneVerification(Authentication authentication, UpdatePhoneRequest request) {
        savePhoneNumber(authentication, request);
        String normalizedPhone = normalizePhone(request.getPhoneNumber());
        return new OtpSentResponse(normalizedPhone, "Phone number updated successfully.", 0, null);
    }

    @Transactional
    public CustomerProfileDTO verifyPhoneOtp(Authentication authentication, VerifyOtpRequest request) {
        if (request != null && request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            return savePhoneNumber(authentication, new UpdatePhoneRequest(request.getPhoneNumber()));
        }
        User user = getAuthenticatedUserEntity(authentication);
        user.setPhoneVerified(true);
        user.setUpdatedAt(LocalDateTime.now());
        return toProfileDTO(userRepository.save(user));
    }

    public static String normalizePhone(String rawPhone) {
        if (rawPhone == null) return "";
        String clean = rawPhone.replaceAll("[^0-9]", "");
        if (clean.length() == 12 && clean.startsWith("91")) {
            clean = clean.substring(2);
        } else if (clean.length() == 11 && clean.startsWith("0")) {
            clean = clean.substring(1);
        }
        return clean;
    }

    public CustomerProfileDTO toProfileDTO(User user) {
        return new CustomerProfileDTO(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getProfilePictureUrl(),
            user.getPhoneNumber(),
            user.isPhoneVerified(),
            user.isProfileComplete()
        );
    }

    public static record AuthResult(String token, CustomerProfileDTO profile) {}
}
