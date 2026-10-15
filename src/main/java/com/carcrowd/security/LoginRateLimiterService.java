package com.carcrowd.security;

import com.carcrowd.exception.TooManyRequestsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Production-ready thread-safe rate limiter service for Admin and Team Member login flows.
 * Provides independent sliding-window IP limiting and progressive account lockouts.
 */
@Service
public class LoginRateLimiterService {

    private static final Logger log = LoggerFactory.getLogger(LoginRateLimiterService.class);

    public enum PolicyType {
        ADMIN,
        MEMBER
    }

    public static class AccountAttemptInfo {
        private final int failedAttempts;
        private final Instant lockoutUntil;
        private final Instant lastAttemptAt;

        public AccountAttemptInfo(int failedAttempts, Instant lockoutUntil, Instant lastAttemptAt) {
            this.failedAttempts = failedAttempts;
            this.lockoutUntil = lockoutUntil;
            this.lastAttemptAt = lastAttemptAt;
        }

        public int getFailedAttempts() {
            return failedAttempts;
        }

        public Instant getLockoutUntil() {
            return lockoutUntil;
        }

        public Instant getLastAttemptAt() {
            return lastAttemptAt;
        }
    }

    private final RateLimitProperties properties;
    private final Map<String, AccountAttemptInfo> accountAttemptsCache = new ConcurrentHashMap<>();
    private final Map<String, ConcurrentLinkedDeque<Instant>> ipAttemptsCache = new ConcurrentHashMap<>();

    public LoginRateLimiterService(RateLimitProperties properties) {
        this.properties = properties;
    }

    /**
     * Verifies that the login attempt is allowed for both the given IP and the account email.
     * Throws TooManyRequestsException with a Retry-After duration if limits are exceeded.
     */
    public void checkLoginAllowed(PolicyType policyType, String email, String ip) {
        RateLimitProperties.Policy accountPolicy = getAccountPolicy(policyType);
        RateLimitProperties.Policy ipPolicy = getIpPolicy(policyType);

        String normalizedIp = sanitizeIpKey(ip);
        String ipKey = policyType.name() + ":IP:" + normalizedIp;

        // 1. Evaluate IP sliding window rate limit
        ConcurrentLinkedDeque<Instant> timestamps = ipAttemptsCache.computeIfAbsent(ipKey, k -> new ConcurrentLinkedDeque<>());
        Instant now = Instant.now();
        Instant windowStart = now.minusSeconds(ipPolicy.getWindowSeconds());

        // Remove expired entries from the head of the deque
        while (!timestamps.isEmpty() && timestamps.peekFirst().isBefore(windowStart)) {
            timestamps.pollFirst();
        }

        if (timestamps.size() >= ipPolicy.getMaxAttempts()) {
            Instant oldestInWindow = timestamps.peekFirst();
            long retryAfterSeconds = ipPolicy.getWindowSeconds();
            if (oldestInWindow != null) {
                Instant unlockTime = oldestInWindow.plusSeconds(ipPolicy.getWindowSeconds());
                retryAfterSeconds = Math.max(1, Duration.between(now, unlockTime).getSeconds());
            }
            long remainingMinutes = Math.max(1, (retryAfterSeconds + 59) / 60);
            log.warn("Rate limit exceeded for IP: {} (policy: {}). Retry after: {}s", normalizedIp, policyType, retryAfterSeconds);
            throw new TooManyRequestsException(
                "Too many login attempts from this IP address. Please try again in " + remainingMinutes + " minutes.",
                retryAfterSeconds
            );
        }

        // Record the request under the IP sliding window
        timestamps.addLast(now);

        // 2. Evaluate Account Lockout
        if (email != null && !email.isBlank()) {
            String accountKey = policyType.name() + ":ACC:" + email.trim().toLowerCase();
            AccountAttemptInfo info = accountAttemptsCache.get(accountKey);

            if (info != null && info.getLockoutUntil() != null) {
                if (now.isBefore(info.getLockoutUntil())) {
                    long retryAfterSeconds = Math.max(1, Duration.between(now, info.getLockoutUntil()).getSeconds());
                    long remainingMinutes = Math.max(1, (retryAfterSeconds + 59) / 60);
                    log.warn("Login attempt blocked for locked account: {} (policy: {}). Retry after: {}s", email, policyType, retryAfterSeconds);
                    throw new TooManyRequestsException(
                        "Too many failed login attempts. Account is temporarily locked. Please try again in " + remainingMinutes + " minutes.",
                        retryAfterSeconds
                    );
                } else {
                    // Lockout period has elapsed: reset lock
                    accountAttemptsCache.remove(accountKey);
                }
            }
        }
    }

    /**
     * Records a failed authentication attempt for the given account.
     */
    public void recordFailedAttempt(PolicyType policyType, String email, String ip) {
        if (email == null || email.isBlank()) {
            return;
        }

        RateLimitProperties.Policy accountPolicy = getAccountPolicy(policyType);
        String accountKey = policyType.name() + ":ACC:" + email.trim().toLowerCase();

        accountAttemptsCache.compute(accountKey, (k, existing) -> {
            Instant now = Instant.now();
            if (existing == null) {
                return new AccountAttemptInfo(1, null, now);
            }

            // If an earlier lockout expired, or attempts are outside the rolling window, restart counter
            if (existing.getLockoutUntil() != null && !now.isBefore(existing.getLockoutUntil())) {
                return new AccountAttemptInfo(1, null, now);
            }
            if (existing.getLastAttemptAt() != null &&
                Duration.between(existing.getLastAttemptAt(), now).getSeconds() > accountPolicy.getWindowSeconds()) {
                return new AccountAttemptInfo(1, null, now);
            }

            int newAttempts = existing.getFailedAttempts() + 1;
            Instant lockout = null;
            if (newAttempts >= accountPolicy.getMaxAttempts()) {
                lockout = now.plusSeconds(accountPolicy.getWindowSeconds());
                log.warn("Account temporarily locked due to {} consecutive failed attempts: {} (policy: {})",
                    newAttempts, email, policyType);
            }

            return new AccountAttemptInfo(newAttempts, lockout, now);
        });
    }

    /**
     * Resets failed login counters for the given account upon successful authentication.
     */
    public void recordSuccessfulLogin(PolicyType policyType, String email) {
        if (email == null || email.isBlank()) {
            return;
        }
        String accountKey = policyType.name() + ":ACC:" + email.trim().toLowerCase();
        accountAttemptsCache.remove(accountKey);
    }

    // ==========================================
    // Backward Compatibility Helpers (Default to ADMIN policy)
    // ==========================================

    public void checkLoginAllowed(String email) {
        checkLoginAllowed(PolicyType.ADMIN, email, "127.0.0.1");
    }

    public void recordFailedAttempt(String email) {
        recordFailedAttempt(PolicyType.ADMIN, email, "127.0.0.1");
    }

    public void recordSuccessfulLogin(String email) {
        recordSuccessfulLogin(PolicyType.ADMIN, email);
    }

    public void clearAll() {
        accountAttemptsCache.clear();
        ipAttemptsCache.clear();
    }

    /**
     * Scheduled background task to evict stale entries from memory.
     */
    @Scheduled(fixedRateString = "${app.rate-limit.cleanup-interval-ms:60000}")
    public void cleanupExpiredEntries() {
        Instant now = Instant.now();

        // Evict expired account lockouts and old attempt records
        accountAttemptsCache.entrySet().removeIf(entry -> {
            AccountAttemptInfo info = entry.getValue();
            if (info.getLockoutUntil() != null) {
                return !now.isBefore(info.getLockoutUntil());
            }
            return info.getLastAttemptAt() != null && Duration.between(info.getLastAttemptAt(), now).toMinutes() > 30;
        });

        // Evict old IP timestamps
        ipAttemptsCache.entrySet().removeIf(entry -> {
            ConcurrentLinkedDeque<Instant> deque = entry.getValue();
            Instant cutoff = now.minusSeconds(1800);
            deque.removeIf(ts -> ts.isBefore(cutoff));
            return deque.isEmpty();
        });
    }

    private RateLimitProperties.Policy getAccountPolicy(PolicyType policyType) {
        return (policyType == PolicyType.MEMBER)
            ? properties.getMemberAccount()
            : properties.getAdminAccount();
    }

    private RateLimitProperties.Policy getIpPolicy(PolicyType policyType) {
        return (policyType == PolicyType.MEMBER)
            ? properties.getMemberIp()
            : properties.getAdminIp();
    }

    private String sanitizeIpKey(String ip) {
        if (ip == null || ip.isBlank()) {
            return "0.0.0.0";
        }
        return ip.trim();
    }
}
