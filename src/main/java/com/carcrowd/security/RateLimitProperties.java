package com.carcrowd.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for Admin and Team Member login rate limiting.
 * Supports independently configurable account and IP thresholds and windows.
 */
@Component
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

    public static class Policy {
        private int maxAttempts = 5;
        private int windowMinutes = 15;

        public Policy() {}

        public Policy(int maxAttempts, int windowMinutes) {
            this.maxAttempts = maxAttempts;
            this.windowMinutes = windowMinutes;
        }

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public int getWindowMinutes() {
            return windowMinutes;
        }

        public void setWindowMinutes(int windowMinutes) {
            this.windowMinutes = windowMinutes;
        }

        public long getWindowSeconds() {
            return (long) windowMinutes * 60;
        }
    }

    private Policy adminAccount = new Policy(5, 15);
    private Policy adminIp = new Policy(20, 15);
    private Policy memberAccount = new Policy(5, 15);
    private Policy memberIp = new Policy(20, 15);
    private List<String> trustedProxies = new ArrayList<>(List.of("127.0.0.1", "0:0:0:0:0:0:0:1"));

    public Policy getAdminAccount() {
        return adminAccount;
    }

    public void setAdminAccount(Policy adminAccount) {
        this.adminAccount = adminAccount;
    }

    public Policy getAdminIp() {
        return adminIp;
    }

    public void setAdminIp(Policy adminIp) {
        this.adminIp = adminIp;
    }

    public Policy getMemberAccount() {
        return memberAccount;
    }

    public void setMemberAccount(Policy memberAccount) {
        this.memberAccount = memberAccount;
    }

    public Policy getMemberIp() {
        return memberIp;
    }

    public void setMemberIp(Policy memberIp) {
        this.memberIp = memberIp;
    }

    public List<String> getTrustedProxies() {
        return trustedProxies;
    }

    public void setTrustedProxies(List<String> trustedProxies) {
        this.trustedProxies = trustedProxies;
    }
}
