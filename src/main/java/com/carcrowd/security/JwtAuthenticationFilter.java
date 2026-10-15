package com.carcrowd.security;

import com.carcrowd.entity.AdminUser;
import com.carcrowd.repository.AdminUserRepository;
import com.carcrowd.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider tokenProvider;
    private final AdminUserRepository adminUserRepository;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider,
                                   AdminUserRepository adminUserRepository,
                                   UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.adminUserRepository = adminUserRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
            String email = tokenProvider.getEmailFromToken(token);
            String role = tokenProvider.getRoleFromToken(token);

            // Security Hardening: Never default missing/empty role to ROLE_ADMIN!
            if (!StringUtils.hasText(role) || !StringUtils.hasText(email)) {
                log.warn("Rejected JWT token with missing or blank role/subject claim");
                filterChain.doFilter(request, response);
                return;
            }

            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }

            // Security Hardening: Validate account is active and not disabled/deleted in database
            if ("ROLE_ADMIN".equals(role) || "ROLE_MEMBER".equals(role)) {
                Optional<AdminUser> adminOpt = adminUserRepository.findByEmailIgnoreCase(email);
                if (adminOpt.isEmpty() || !adminOpt.get().isActive()) {
                    log.warn("Access denied: Token presented for disabled or non-existent administrative account: {}", email);
                    filterChain.doFilter(request, response);
                    return;
                }
                // Ensure authority reflects actual authoritative role in database
                role = adminOpt.get().getRole();
            } else if ("ROLE_CUSTOMER".equals(role)) {
                if (!userRepository.existsByEmailIgnoreCase(email) && !userRepository.existsByGoogleSub(email)) {
                    log.warn("Access denied: Token presented for non-existent customer: {}", email);
                    filterChain.doFilter(request, response);
                    return;
                }
            } else {
                log.warn("Access denied: Unrecognized role '{}' in token for subject: {}", role, email);
                filterChain.doFilter(request, response);
                return;
            }

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(authority));
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }

        // Resolve customer HttpOnly cookie
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("carcrowd_customer_session".equals(cookie.getName()) && StringUtils.hasText(cookie.getValue())) {
                    return cookie.getValue().trim();
                }
            }
        }

        return null;
    }
}
