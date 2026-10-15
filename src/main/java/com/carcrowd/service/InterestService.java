package com.carcrowd.service;

import com.carcrowd.dto.InterestRequestDTO;
import com.carcrowd.dto.InterestResponseDTO;
import com.carcrowd.entity.Car;
import com.carcrowd.entity.InterestRegistration;
import com.carcrowd.entity.LeadActivity;
import com.carcrowd.entity.LeadStatus;
import com.carcrowd.entity.User;
import com.carcrowd.exception.BadRequestException;
import com.carcrowd.exception.ForbiddenException;
import com.carcrowd.exception.ResourceNotFoundException;
import com.carcrowd.repository.CarRepository;
import com.carcrowd.repository.InterestRegistrationRepository;
import com.carcrowd.repository.LeadActivityRepository;
import com.carcrowd.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class InterestService {

    private static final Logger log = LoggerFactory.getLogger(InterestService.class);

    private final CarRepository carRepository;
    private final InterestRegistrationRepository interestRepository;
    private final UserRepository userRepository;
    private final LeadActivityRepository activityRepository;

    public InterestService(CarRepository carRepository,
                           InterestRegistrationRepository interestRepository,
                           UserRepository userRepository,
                           LeadActivityRepository activityRepository) {
        this.carRepository = carRepository;
        this.interestRepository = interestRepository;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional
    public InterestResponseDTO registerInterest(Long carId, InterestRequestDTO request) {
        return registerInterest(carId, request, null);
    }

    @Transactional
    public InterestResponseDTO registerInterest(Long carId, InterestRequestDTO request, Authentication authentication) {
        if (request == null) {
            throw new BadRequestException("Request body cannot be empty");
        }

        // 1. Verify that the car exists and is active
        Car car = carRepository.findByIdAndActiveTrue(carId)
            .orElseThrow(() -> new ResourceNotFoundException("Vehicle model not found with ID: " + carId));

        // 2. Validate full name and mobile number on the backend
        if (!StringUtils.hasText(request.getFullName()) || request.getFullName().trim().length() < 2) {
            throw new BadRequestException("Full name is required and must be at least 2 characters.");
        }
        String customerName = request.getFullName().trim();

        if (!StringUtils.hasText(request.getPhoneNumber())) {
            throw new BadRequestException("Mobile number is required.");
        }
        String normalizedPhone = normalizePhoneNumber(request.getPhoneNumber());
        if (normalizedPhone.length() != 10) {
            throw new BadRequestException("Phone number must be a valid 10-digit mobile number, optionally prefixed with +91");
        }

        // 3. Authenticated customer identification & ownership enforcement
        User authenticatedUser = null;
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String principal = authentication.getName();
            authenticatedUser = userRepository.findByEmailIgnoreCase(principal)
                .or(() -> userRepository.findByGoogleSub(principal))
                .orElse(null);

            if (authenticatedUser != null) {
                // Ensure customers can submit requests only for themselves
                if (StringUtils.hasText(request.getEmail()) &&
                    !request.getEmail().trim().equalsIgnoreCase(authenticatedUser.getEmail().trim())) {
                    throw new ForbiddenException("You cannot submit requests on behalf of another customer.");
                }

                // Update customer profile with the customer's validated full name and mobile number
                authenticatedUser.setFullName(customerName);
                authenticatedUser.setPhoneNumber(normalizedPhone);
                // Do not mark phone verified unless an actual verification process exists
                authenticatedUser.setUpdatedAt(LocalDateTime.now());
                userRepository.save(authenticatedUser);
            }
        }

        String normalizedEmail = (authenticatedUser != null && authenticatedUser.getEmail() != null)
            ? normalizeEmail(authenticatedUser.getEmail())
            : normalizeEmail(request.getEmail());

        // 4. Check for existing registration for this car (deduplication & reactivation rule)
        InterestRegistration existing = null;
        if (authenticatedUser != null) {
            existing = interestRepository.findByCarIdAndUserId(carId, authenticatedUser.getId()).orElse(null);
        }
        if (existing == null && StringUtils.hasText(normalizedPhone)) {
            existing = interestRepository.findByCarIdAndPhoneNumber(carId, normalizedPhone).orElse(null);
        }
        if (existing == null && StringUtils.hasText(normalizedEmail)) {
            existing = interestRepository.findByCarIdAndEmail(carId, normalizedEmail).orElse(null);
        }

        if (existing != null) {
            if (existing.getLeadStatus() == LeadStatus.CANCELLED) {
                // Reactivate cancelled registration
                log.info("Reactivating previously cancelled registration (ID: {}) for carId: {}", existing.getId(), carId);
                existing.setLeadStatus(LeadStatus.JOINED);
                existing.setFullName(customerName);
                existing.setPhoneNumber(normalizedPhone);
                existing.setEmail(normalizedEmail);
                existing.setAge(request.getAge());
                existing.setCity(request.getCity().trim());
                if (request.getPurchaseTimeline() != null) {
                    existing.setPurchaseTimeline(request.getPurchaseTimeline().trim());
                }
                existing.setConsent(request.isConsent());
                existing.setTermsVersion(StringUtils.hasText(request.getTermsVersion()) ? request.getTermsVersion() : "2026-10-14");
                existing.setTermsAcceptedAt(LocalDateTime.now());
                existing.setUpdatedAt(LocalDateTime.now());
                if (authenticatedUser != null && existing.getUser() == null) {
                    existing.setUser(authenticatedUser);
                }
                interestRepository.save(existing);

                activityRepository.save(new LeadActivity(
                    existing.getId(),
                    (authenticatedUser != null ? authenticatedUser.getEmail() : null),
                    customerName,
                    "REQUEST_REACTIVATED",
                    "CANCELLED",
                    "JOINED",
                    "Request re-activated. Customer re-joined group for " + car.getModelName()
                ));

                long updatedCount = interestRepository.countActiveByCarId(carId);
                return new InterestResponseDTO(
                    true,
                    "Welcome back! Your request has been re-activated and you have re-joined the buying crowd for " + car.getModelName() + ".",
                    car.getId(),
                    car.getModelName(),
                    updatedCount,
                    false
                );
            } else {
                long currentCount = interestRepository.countActiveByCarId(carId);
                log.info("Duplicate active interest registration detected for carId: {}. Preserving current count: {}", carId, currentCount);
                return new InterestResponseDTO(
                    true,
                    "You are already part of the buying crowd for " + car.getModelName() + "! Your reservation remains active.",
                    car.getId(),
                    car.getModelName(),
                    currentCount,
                    true // duplicate flag
                );
            }
        }

        // 5. Persist new registration with validated and normalized data
        InterestRegistration registration = new InterestRegistration(
            car,
            customerName,
            normalizedPhone,
            normalizedEmail,
            request.getAge(),
            request.getCity().trim(),
            request.getPurchaseTimeline() != null ? request.getPurchaseTimeline().trim() : null,
            request.isConsent()
        );
        registration.setTermsVersion(StringUtils.hasText(request.getTermsVersion()) ? request.getTermsVersion() : "2026-10-14");
        registration.setTermsAcceptedAt(LocalDateTime.now());
        if (authenticatedUser != null) {
            registration.setUser(authenticatedUser);
        }

        try {
            interestRepository.save(registration);
            activityRepository.save(new LeadActivity(
                registration.getId(),
                (authenticatedUser != null ? authenticatedUser.getEmail() : null),
                customerName,
                "REQUEST_CREATED",
                null,
                "JOINED",
                "Request submitted. Customer joined group for " + car.getModelName()
            ));
        } catch (DataIntegrityViolationException ex) {
            log.warn("Concurrent duplicate submission caught via unique constraint for carId: {}", carId);
            long count = interestRepository.countActiveByCarId(carId);
            return new InterestResponseDTO(
                true,
                "Your interest is already registered for " + car.getModelName() + ".",
                car.getId(),
                car.getModelName(),
                count,
                true
            );
        }

        // 6. Query updated authoritative count
        long updatedCount = interestRepository.countActiveByCarId(carId);
        log.info("New interest successfully registered for carId: {}. Updated buyer count: {}", carId, updatedCount);

        return new InterestResponseDTO(
            true,
            "Interest registered successfully! Welcome to the crowd for " + car.getModelName() + ".",
            car.getId(),
            car.getModelName(),
            updatedCount,
            false
        );
    }

    @Transactional(readOnly = true)
    public long getInterestCount(Long carId) {
        if (!carRepository.existsById(carId)) {
            throw new ResourceNotFoundException("Vehicle model not found with ID: " + carId);
        }
        return interestRepository.countActiveByCarId(carId);
    }

    /**
     * Normalizes Indian mobile phone numbers:
     * - Strips whitespace, dashes, braces, non-digit characters.
     * - Strips leading +91 or 91 country code and leading 0.
     * - Returns standard 10-digit number.
     */
    public String normalizePhoneNumber(String phone) {
        if (phone == null) return "";
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.startsWith("91") && digits.length() == 12) {
            digits = digits.substring(2);
        } else if (digits.startsWith("0") && digits.length() == 11) {
            digits = digits.substring(1);
        }
        return digits;
    }

    /**
     * Normalizes email address by trimming and converting to lowercase.
     */
    public String normalizeEmail(String email) {
        if (email == null) return "";
        return email.trim().toLowerCase();
    }
}
