package com.carcrowd.controller;

import com.carcrowd.dto.admin.AdminLoginRequest;
import com.carcrowd.dto.admin.AdminLoginResponse;
import com.carcrowd.dto.admin.AdminUserDTO;
import com.carcrowd.security.ClientIpResolver;
import com.carcrowd.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;
    private final ClientIpResolver clientIpResolver;

    public AdminAuthController(AdminAuthService adminAuthService, ClientIpResolver clientIpResolver) {
        this.adminAuthService = adminAuthService;
        this.clientIpResolver = clientIpResolver;
    }

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request,
                                                    HttpServletRequest httpRequest) {
        String clientIp = clientIpResolver.resolveClientIp(httpRequest);
        return ResponseEntity.ok(adminAuthService.login(request, clientIp));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<AdminUserDTO> getCurrentUser() {
        return ResponseEntity.ok(adminAuthService.getCurrentUser());
    }
}
