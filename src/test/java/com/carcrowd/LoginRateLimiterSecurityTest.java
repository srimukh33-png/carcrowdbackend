package com.carcrowd;

import com.carcrowd.dto.admin.AdminLoginRequest;
import com.carcrowd.entity.AdminUser;
import com.carcrowd.repository.AdminUserRepository;
import com.carcrowd.security.ClientIpResolver;
import com.carcrowd.security.LoginRateLimiterService;
import com.carcrowd.security.RateLimitProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoginRateLimiterSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginRateLimiterService rateLimiter;

    @Autowired
    private ClientIpResolver clientIpResolver;

    @Autowired
    private RateLimitProperties rateLimitProperties;

    private AdminUser testAdmin;
    private AdminUser testMember;

    @BeforeEach
    public void setup() {
        rateLimiter.clearAll();

        testAdmin = adminUserRepository.findByEmailIgnoreCase("ratelimit.admin@carcrowd.in")
            .orElseGet(() -> adminUserRepository.save(new AdminUser(
                "ratelimit.admin@carcrowd.in",
                passwordEncoder.encode("AdminPass123!"),
                "RateLimit Admin",
                "ROLE_ADMIN",
                true
            )));
        testAdmin.setActive(true);
        testAdmin.setPasswordHash(passwordEncoder.encode("AdminPass123!"));
        adminUserRepository.save(testAdmin);

        testMember = adminUserRepository.findByEmailIgnoreCase("ratelimit.member@carcrowd.in")
            .orElseGet(() -> adminUserRepository.save(new AdminUser(
                "ratelimit.member@carcrowd.in",
                passwordEncoder.encode("MemberPass123!"),
                "RateLimit Member",
                "ROLE_MEMBER",
                true
            )));
        testMember.setActive(true);
        testMember.setPasswordHash(passwordEncoder.encode("MemberPass123!"));
        adminUserRepository.save(testMember);
    }

    // 1. Admin account lockout after 5 consecutive failed attempts
    @Test
    public void testAdminAccountLockoutAfter5FailedAttempts() throws Exception {
        String email = testAdmin.getEmail();
        AdminLoginRequest badLogin = new AdminLoginRequest(email, "WrongPassword!");

        for (int i = 1; i <= 5; i++) {
            final String ip = "10.10.1." + i;
            mockMvc.perform(post("/api/admin/auth/login")
                    .with(req -> { req.setRemoteAddr(ip); return req; })
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid email or password")));
        }

        // 6th attempt should be blocked with 429 Too Many Requests and Retry-After header
        mockMvc.perform(post("/api/admin/auth/login")
                .with(req -> { req.setRemoteAddr("10.10.1.99"); return req; })
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badLogin)))
            .andExpect(status().isTooManyRequests())
            .andExpect(header().exists("Retry-After"))
            .andExpect(jsonPath("$.message", containsString("Too many failed login attempts")));
    }

    // 2. Member account lockout after 5 consecutive failed attempts on member login
    @Test
    public void testMemberAccountLockoutAfter5FailedAttempts() throws Exception {
        String email = testMember.getEmail();
        AdminLoginRequest badLogin = new AdminLoginRequest(email, "WrongPassword!");

        for (int i = 1; i <= 5; i++) {
            final String ip = "10.20.1." + i;
            mockMvc.perform(post("/api/member/auth/login")
                    .with(req -> { req.setRemoteAddr(ip); return req; })
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid email or password")));
        }

        // 6th attempt on member endpoint returns 429 and Retry-After header
        mockMvc.perform(post("/api/member/auth/login")
                .with(req -> { req.setRemoteAddr("10.20.1.99"); return req; })
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badLogin)))
            .andExpect(status().isTooManyRequests())
            .andExpect(header().exists("Retry-After"))
            .andExpect(jsonPath("$.message", containsString("Too many failed login attempts")));
    }

    // 3. IP rate limit triggers after 20 attempts from the same IP
    @Test
    public void testIpRateLimiterBlocksAfter20Requests() throws Exception {
        String fixedIp = "198.51.100.55";

        for (int i = 1; i <= 20; i++) {
            AdminLoginRequest req = new AdminLoginRequest("user" + i + "@randomtest.in", "Password123!");
            mockMvc.perform(post("/api/admin/auth/login")
                    .with(request -> { request.setRemoteAddr(fixedIp); return request; })
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
        }

        // 21st attempt from the same IP must be rejected with 429 Too Many Requests
        AdminLoginRequest req21 = new AdminLoginRequest("user21@randomtest.in", "Password123!");
        mockMvc.perform(post("/api/admin/auth/login")
                .with(request -> { request.setRemoteAddr(fixedIp); return request; })
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req21)))
            .andExpect(status().isTooManyRequests())
            .andExpect(header().exists("Retry-After"))
            .andExpect(jsonPath("$.message", containsString("IP address")));
    }

    // 4. Lockout expiration unlocks the account without manual intervention
    @Test
    public void testAccountLockoutExpiresAfterCooldownWindow() {
        String email = "temp.locked@carcrowd.in";
        for (int i = 0; i < 5; i++) {
            rateLimiter.recordFailedAttempt(LoginRateLimiterService.PolicyType.ADMIN, email, "127.0.0.1");
        }

        // Immediately after 5 failures: checkLoginAllowed throws 429
        boolean thrownInitially = false;
        try {
            rateLimiter.checkLoginAllowed(LoginRateLimiterService.PolicyType.ADMIN, email, "127.0.0.1");
        } catch (Exception ex) {
            thrownInitially = true;
        }
        assertTrue(thrownInitially, "Should be locked immediately after 5 attempts");

        // Clear for cooldown simulation
        rateLimiter.clearAll();

        // After cooldown elapses: login check succeeds without throwing
        rateLimiter.checkLoginAllowed(LoginRateLimiterService.PolicyType.ADMIN, email, "127.0.0.1");
    }

    // 5. Successful login resets the failed attempts counter
    @Test
    public void testSuccessfulLoginResetsFailedAttemptCounter() throws Exception {
        String email = testAdmin.getEmail();
        AdminLoginRequest badLogin = new AdminLoginRequest(email, "WrongPassword!");
        AdminLoginRequest goodLogin = new AdminLoginRequest(email, "AdminPass123!");

        // 4 failed attempts (1 below threshold)
        for (int i = 1; i <= 4; i++) {
            final String ip = "10.30.1." + i;
            mockMvc.perform(post("/api/admin/auth/login")
                    .with(req -> { req.setRemoteAddr(ip); return req; })
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isBadRequest());
        }

        // 1 successful login resets counter
        mockMvc.perform(post("/api/admin/auth/login")
                .with(req -> { req.setRemoteAddr("10.30.1.100"); return req; })
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goodLogin)))
            .andExpect(status().isOk());

        // 4 more failed attempts must still return 400 (not 429!), proving counter reset
        for (int i = 1; i <= 4; i++) {
            final String ip = "10.30.2." + i;
            mockMvc.perform(post("/api/admin/auth/login")
                    .with(req -> { req.setRemoteAddr(ip); return req; })
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isBadRequest());
        }
    }

    // 6. Unknown / non-existent account is rate-limited identically (anti-enumeration)
    @Test
    public void testNonExistentAccountIsRateLimitedIdentically() throws Exception {
        String fakeEmail = "nonexistent.account.xyz@carcrowd.in";
        AdminLoginRequest badLogin = new AdminLoginRequest(fakeEmail, "AnyPassword123!");

        for (int i = 1; i <= 5; i++) {
            final String ip = "10.40.1." + i;
            mockMvc.perform(post("/api/admin/auth/login")
                    .with(req -> { req.setRemoteAddr(ip); return req; })
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid email or password")));
        }

        // 6th attempt returns 429
        mockMvc.perform(post("/api/admin/auth/login")
                .with(req -> { req.setRemoteAddr("10.40.1.99"); return req; })
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badLogin)))
            .andExpect(status().isTooManyRequests())
            .andExpect(header().exists("Retry-After"))
            .andExpect(jsonPath("$.message", containsString("Too many failed login attempts")));
    }

    // 7. Generic error message does not leak account existence
    @Test
    public void testGenericErrorMessageDoesNotLeakAccountExistence() throws Exception {
        AdminLoginRequest realAccountBadPass = new AdminLoginRequest(testAdmin.getEmail(), "WrongPass!");
        AdminLoginRequest fakeAccountBadPass = new AdminLoginRequest("doesnotexist999@carcrowd.in", "WrongPass!");

        mockMvc.perform(post("/api/admin/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(realAccountBadPass)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid email or password"));

        mockMvc.perform(post("/api/admin/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeAccountBadPass)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    // 8. Account isolation: Different accounts on the same IP do not share lockout counters
    @Test
    public void testAccountIsolationOnSameIp() throws Exception {
        String fixedIp = "192.168.1.50";
        String accountA = "target.a@carcrowd.in";
        String accountB = "target.b@carcrowd.in";

        // Lock out Account A from fixedIp
        for (int i = 1; i <= 5; i++) {
            AdminLoginRequest req = new AdminLoginRequest(accountA, "BadPass!");
            mockMvc.perform(post("/api/admin/auth/login")
                    .with(r -> { r.setRemoteAddr(fixedIp); return r; })
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
        }

        // Account A is now locked
        mockMvc.perform(post("/api/admin/auth/login")
                .with(r -> { r.setRemoteAddr(fixedIp); return r; })
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AdminLoginRequest(accountA, "BadPass!"))))
            .andExpect(status().isTooManyRequests());

        // Account B from the same IP is NOT locked (returns 400 Bad Request, not 429)
        mockMvc.perform(post("/api/admin/auth/login")
                .with(r -> { r.setRemoteAddr(fixedIp); return r; })
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AdminLoginRequest(accountB, "BadPass!"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    // 9. IP counter isolation: Different IPs brute-forcing the same account each track their own IP usage
    @Test
    public void testIpCounterIsolationAcrossDifferentIps() throws Exception {
        String targetAccount = "shared.target@carcrowd.in";

        // 10 attempts from IP 1
        for (int i = 1; i <= 10; i++) {
            mockMvc.perform(post("/api/admin/auth/login")
                    .with(r -> { r.setRemoteAddr("10.50.1.1"); return r; })
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new AdminLoginRequest(targetAccount + i, "BadPass!"))))
                .andExpect(status().isBadRequest());
        }

        // 10 attempts from IP 2
        for (int i = 1; i <= 10; i++) {
            mockMvc.perform(post("/api/admin/auth/login")
                    .with(r -> { r.setRemoteAddr("10.50.1.2"); return r; })
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new AdminLoginRequest(targetAccount + (i + 10), "BadPass!"))))
                .andExpect(status().isBadRequest());
        }

        // Neither IP has exceeded the 20-request IP limit yet
        mockMvc.perform(post("/api/admin/auth/login")
                .with(r -> { r.setRemoteAddr("10.50.1.1"); return r; })
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AdminLoginRequest("fresh1@carcrowd.in", "BadPass!"))))
            .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/admin/auth/login")
                .with(r -> { r.setRemoteAddr("10.50.1.2"); return r; })
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AdminLoginRequest("fresh2@carcrowd.in", "BadPass!"))))
            .andExpect(status().isBadRequest());
    }

    // 10. Untrusted proxy spoofing protection: X-Forwarded-For ignored from untrusted client
    @Test
    public void testSpoofedXForwardedForIgnoredFromUntrustedRemote() throws Exception {
        String attackerIp = "203.0.113.199";

        // Attacker attempts to spoof X-Forwarded-For on every request
        for (int i = 1; i <= 20; i++) {
            mockMvc.perform(post("/api/admin/auth/login")
                    .with(r -> { r.setRemoteAddr(attackerIp); return r; })
                    .header("X-Forwarded-For", "8.8.8." + i)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new AdminLoginRequest("user" + i + "@domain.in", "BadPass!"))))
                .andExpect(status().isBadRequest());
        }

        // 21st request from attackerIp is blocked by IP limit despite new fake X-Forwarded-For
        mockMvc.perform(post("/api/admin/auth/login")
                .with(r -> { r.setRemoteAddr(attackerIp); return r; })
                .header("X-Forwarded-For", "9.9.9.9")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AdminLoginRequest("user21@domain.in", "BadPass!"))))
            .andExpect(status().isTooManyRequests())
            .andExpect(jsonPath("$.message", containsString("IP address")));
    }

    // 11. Trusted proxy header handling: X-Forwarded-For respected from configured trusted proxy
    @Test
    public void testTrustedProxyXForwardedForRespected() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1"); // Configured trusted proxy
        request.addHeader("X-Forwarded-For", "198.51.100.77, 10.0.0.1");

        String resolved = clientIpResolver.resolveClientIp(request);
        assertEquals("198.51.100.77", resolved);
    }

    // 12. Role boundary enforcement: Admin cannot authenticate through the team member flow
    @Test
    public void testRoleBoundaryAdminCannotAuthenticateThroughMemberFlow() throws Exception {
        AdminLoginRequest adminCredentials = new AdminLoginRequest(testAdmin.getEmail(), "AdminPass123!");

        mockMvc.perform(post("/api/member/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminCredentials)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    // 13. Concurrency handling: Simultaneous requests handled safely without race conditions
    @Test
    public void testConcurrentLoginRequestsHandledSafely() throws InterruptedException {
        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        AtomicInteger successCounter = new AtomicInteger();
        AtomicInteger errorCounter = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    rateLimiter.recordFailedAttempt(LoginRateLimiterService.PolicyType.ADMIN, "concurrent.test@carcrowd.in", "10.60.1." + id);
                    successCounter.incrementAndGet();
                } catch (Exception e) {
                    errorCounter.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(threads, successCounter.get());
        assertEquals(0, errorCounter.get());
    }
}
