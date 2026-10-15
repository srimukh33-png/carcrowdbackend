package com.carcrowd.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    // Secure 256-bit default fallback secret key if not provided via env
    private static final String DEFAULT_SECRET = "CarCrowdSecureProductionJwtSecretKey2025ForAdminAuthenticationAndRoleAuthorization!";

    private final SecretKey key;
    private final long validityInMilliseconds;

    public JwtTokenProvider(
        @Value("${app.jwt.secret:" + DEFAULT_SECRET + "}") String secret,
        @Value("${app.jwt.expiration-ms:86400000}") long validityInMilliseconds
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.validityInMilliseconds = validityInMilliseconds;
    }

    public String generateToken(String email, String role) {
        return generateToken(email, role, null);
    }

    public String generateToken(String email, String role, Long userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        JwtBuilder builder = Jwts.builder()
            .subject(email)
            .claim("role", role)
            .issuedAt(now)
            .expiration(validity)
            .signWith(key);

        if (userId != null) {
            builder.claim("userId", userId);
        }

        return builder.compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return claims.getSubject();
    }

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return claims.get("role", String.class);
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        Object uid = claims.get("userId");
        if (uid instanceof Number) {
            return ((Number) uid).longValue();
        }
        return null;
    }
}
