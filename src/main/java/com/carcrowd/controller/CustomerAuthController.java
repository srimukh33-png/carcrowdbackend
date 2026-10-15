package com.carcrowd.controller;

import com.carcrowd.dto.customer.*;
import com.carcrowd.service.CustomerAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class CustomerAuthController {

    public static final String COOKIE_NAME = "carcrowd_customer_session";

    private final CustomerAuthService authService;

    public CustomerAuthController(CustomerAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/google")
    public ResponseEntity<CustomerProfileDTO> googleAuth(
            @Valid @RequestBody GoogleAuthRequest request,
            HttpServletRequest httpRequest
    ) {
        CustomerAuthService.AuthResult result = authService.processGoogleAuth(request);

        boolean isHttps = httpRequest.isSecure() || "https".equalsIgnoreCase(httpRequest.getHeader("X-Forwarded-Proto"));

        ResponseCookie sessionCookie = ResponseCookie.from(COOKIE_NAME, result.token())
            .httpOnly(true)
            .secure(isHttps)
            .path("/")
            .sameSite("Lax")
            .maxAge(86400 * 7) // 7 days
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, sessionCookie.toString())
            .body(result.profile());
    }

    @GetMapping("/auth/me")
    public ResponseEntity<CustomerProfileDTO> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(authService.getProfile(authentication));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest httpRequest) {
        boolean isHttps = httpRequest.isSecure() || "https".equalsIgnoreCase(httpRequest.getHeader("X-Forwarded-Proto"));

        ResponseCookie clearCookie = ResponseCookie.from(COOKIE_NAME, "")
            .httpOnly(true)
            .secure(isHttps)
            .path("/")
            .sameSite("Lax")
            .maxAge(0)
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
            .body(Map.of("message", "Logged out successfully"));
    }

    @PostMapping("/profile/phone")
    public ResponseEntity<CustomerProfileDTO> updatePhone(
            @Valid @RequestBody UpdatePhoneRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(authService.savePhoneNumber(authentication, request));
    }

    @PostMapping("/profile/phone/verify")
    public ResponseEntity<CustomerProfileDTO> verifyPhone(
            @RequestBody(required = false) VerifyOtpRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(authService.verifyPhoneOtp(authentication, request));
    }
}
