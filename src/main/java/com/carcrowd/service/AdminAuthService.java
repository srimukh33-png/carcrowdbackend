package com.carcrowd.service;

import com.carcrowd.dto.admin.AdminLoginRequest;
import com.carcrowd.dto.admin.AdminLoginResponse;
import com.carcrowd.dto.admin.AdminUserDTO;
import com.carcrowd.entity.AdminUser;
import com.carcrowd.exception.BadRequestException;
import com.carcrowd.exception.ResourceNotFoundException;
import com.carcrowd.repository.AdminUserRepository;
import com.carcrowd.security.ClientIpResolver;
import com.carcrowd.security.JwtTokenProvider;
import com.carcrowd.security.LoginRateLimiterService;
import com.carcrowd.security.LoginRateLimiterService.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminAuthService {

    private static final Logger log = LoggerFactory.getLogger(AdminAuthService.class);

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final LoginRateLimiterService rateLimiter;
    private final ClientIpResolver clientIpResolver;

    public AdminAuthService(AdminUserRepository adminUserRepository,
                            PasswordEncoder passwordEncoder,
                            JwtTokenProvider tokenProvider,
                            LoginRateLimiterService rateLimiter,
                            ClientIpResolver clientIpResolver) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.rateLimiter = rateLimiter;
        this.clientIpResolver = clientIpResolver;
    }

    @Transactional(readOnly = true)
    public AdminLoginResponse login(AdminLoginRequest request) {
        return login(request, "127.0.0.1");
    }

    @Transactional(readOnly = true)
    public AdminLoginResponse login(AdminLoginRequest request, String clientIp) {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            throw new BadRequestException("Email and password are required");
        }

        String normalizedEmail = request.getEmail().toLowerCase().trim();
        rateLimiter.checkLoginAllowed(PolicyType.ADMIN, normalizedEmail, clientIp);

        AdminUser admin = adminUserRepository.findByEmailIgnoreCase(normalizedEmail).orElse(null);

        // Fail generic error to prevent email enumeration
        if (admin == null || !passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
            rateLimiter.recordFailedAttempt(PolicyType.ADMIN, normalizedEmail, clientIp);
            log.warn("Failed admin login attempt for email: {} from IP: {}", normalizedEmail, clientIp);
            throw new BadRequestException("Invalid email or password");
        }

        if (!admin.isActive()) {
            rateLimiter.recordFailedAttempt(PolicyType.ADMIN, normalizedEmail, clientIp);
            throw new BadRequestException("Admin account is disabled");
        }

        rateLimiter.recordSuccessfulLogin(PolicyType.ADMIN, normalizedEmail);

        String token = tokenProvider.generateToken(admin.getEmail(), admin.getRole());
        AdminUserDTO userDTO = new AdminUserDTO(
            admin.getId(),
            admin.getEmail(),
            admin.getFullName(),
            admin.getRole(),
            admin.isActive(),
            admin.getCreatedAt()
        );

        log.info("Admin user logged in successfully: {} from IP: {}", admin.getEmail(), clientIp);
        return new AdminLoginResponse(token, "Bearer", userDTO);
    }

    @Transactional(readOnly = true)
    public AdminLoginResponse loginMember(AdminLoginRequest request) {
        return loginMember(request, "127.0.0.1");
    }

    @Transactional(readOnly = true)
    public AdminLoginResponse loginMember(AdminLoginRequest request, String clientIp) {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            throw new BadRequestException("Email and password are required");
        }

        String normalizedEmail = request.getEmail().toLowerCase().trim();
        rateLimiter.checkLoginAllowed(PolicyType.MEMBER, normalizedEmail, clientIp);

        AdminUser member = adminUserRepository.findByEmailIgnoreCase(normalizedEmail).orElse(null);

        // Strict role boundary enforcement: Admins cannot authenticate through the team member flow
        if (member == null || !"ROLE_MEMBER".equalsIgnoreCase(member.getRole()) ||
            !passwordEncoder.matches(request.getPassword(), member.getPasswordHash())) {
            rateLimiter.recordFailedAttempt(PolicyType.MEMBER, normalizedEmail, clientIp);
            log.warn("Failed member login attempt for email: {} from IP: {}", normalizedEmail, clientIp);
            throw new BadRequestException("Invalid email or password");
        }

        if (!member.isActive()) {
            rateLimiter.recordFailedAttempt(PolicyType.MEMBER, normalizedEmail, clientIp);
            throw new BadRequestException("Account is disabled");
        }

        rateLimiter.recordSuccessfulLogin(PolicyType.MEMBER, normalizedEmail);

        String token = tokenProvider.generateToken(member.getEmail(), member.getRole());
        AdminUserDTO userDTO = new AdminUserDTO(
            member.getId(),
            member.getEmail(),
            member.getFullName(),
            member.getRole(),
            member.isActive(),
            member.getCreatedAt()
        );

        log.info("Team member logged in successfully: {} from IP: {}", member.getEmail(), clientIp);
        return new AdminLoginResponse(token, "Bearer", userDTO);
    }

    @Transactional(readOnly = true)
    public AdminUserDTO getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            throw new ResourceNotFoundException("No authenticated admin user found in security context");
        }

        String email = auth.getName();
        AdminUser admin = adminUserRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new ResourceNotFoundException("Admin user not found: " + email));

        return new AdminUserDTO(
            admin.getId(),
            admin.getEmail(),
            admin.getFullName(),
            admin.getRole(),
            admin.isActive(),
            admin.getCreatedAt()
        );
    }
}
