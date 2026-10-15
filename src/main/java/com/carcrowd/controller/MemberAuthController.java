package com.carcrowd.controller;

import com.carcrowd.dto.admin.AdminLoginRequest;
import com.carcrowd.dto.admin.AdminLoginResponse;
import com.carcrowd.security.ClientIpResolver;
import com.carcrowd.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member/auth")
public class MemberAuthController {

    private final AdminAuthService adminAuthService;
    private final ClientIpResolver clientIpResolver;

    public MemberAuthController(AdminAuthService adminAuthService, ClientIpResolver clientIpResolver) {
        this.adminAuthService = adminAuthService;
        this.clientIpResolver = clientIpResolver;
    }

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request,
                                                    HttpServletRequest httpRequest) {
        String clientIp = clientIpResolver.resolveClientIp(httpRequest);
        return ResponseEntity.ok(adminAuthService.loginMember(request, clientIp));
    }
}
