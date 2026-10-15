package com.carcrowd.security;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Resolves the client IP address securely.
 * To prevent IP spoofing, forwarded headers (X-Forwarded-For, Forwarded) are ONLY trusted
 * if the direct remote connection originates from an explicitly configured trusted proxy.
 */
@Component
public class ClientIpResolver {

    private static final Logger log = LoggerFactory.getLogger(ClientIpResolver.class);

    // Matches valid IPv4 (e.g. 192.168.1.1) and IPv6 (e.g. 2001:db8::1 or ::1) characters
    private static final Pattern VALID_IP_PATTERN = Pattern.compile("^[0-9a-fA-F.:]{3,45}$");

    private final RateLimitProperties properties;

    public ClientIpResolver(RateLimitProperties properties) {
        this.properties = properties;
    }

    public String resolveClientIp(HttpServletRequest request) {
        if (request == null) {
            return "0.0.0.0";
        }

        String rawRemoteAddr = request.getRemoteAddr();
        final String effectiveRemoteAddr = (rawRemoteAddr == null || rawRemoteAddr.isBlank()) ? "0.0.0.0" : rawRemoteAddr.trim();

        List<String> trustedProxies = properties.getTrustedProxies();
        boolean isProxyTrusted = trustedProxies != null && trustedProxies.stream()
            .anyMatch(p -> p.equalsIgnoreCase(effectiveRemoteAddr) || "*".equals(p));

        if (!isProxyTrusted) {
            // Direct client connection or untrusted upstream: do not trust forwarded headers
            return sanitizeIp(effectiveRemoteAddr);
        }

        // 1. Check X-Forwarded-For (take the first valid IP from the left)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            String[] ips = xForwardedFor.split(",");
            for (String ipPart : ips) {
                String candidate = ipPart.trim();
                if (isValidIp(candidate)) {
                    return candidate;
                }
            }
        }

        // 2. Check RFC 7239 Forwarded header (e.g. "for=192.0.2.60;proto=http")
        String forwarded = request.getHeader("Forwarded");
        if (forwarded != null && !forwarded.isBlank()) {
            for (String element : forwarded.split(";")) {
                String trimmed = element.trim();
                if (trimmed.toLowerCase().startsWith("for=")) {
                    String candidate = trimmed.substring(4).trim();
                    candidate = candidate.replaceAll("[\"\\[\\]]", "");
                    int colonIndex = candidate.indexOf(':');
                    if (colonIndex > 0 && candidate.indexOf(':', colonIndex + 1) == -1) {
                        candidate = candidate.substring(0, colonIndex); // strip IPv4 port
                    }
                    if (isValidIp(candidate)) {
                        return candidate;
                    }
                }
            }
        }

        return sanitizeIp(effectiveRemoteAddr);
    }

    private boolean isValidIp(String ip) {
        if (ip == null || ip.isBlank() || ip.length() > 45) {
            return false;
        }
        return VALID_IP_PATTERN.matcher(ip).matches();
    }

    private String sanitizeIp(String ip) {
        if (isValidIp(ip)) {
            return ip;
        }
        log.warn("Malformed remote address received: [{}], defaulting to 0.0.0.0", ip);
        return "0.0.0.0";
    }
}
