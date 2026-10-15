package com.carcrowd;

import com.carcrowd.dto.admin.AdminLoginRequest;
import com.carcrowd.entity.AdminUser;
import com.carcrowd.entity.User;
import com.carcrowd.repository.AdminUserRepository;
import com.carcrowd.repository.UserRepository;
import com.carcrowd.security.JwtTokenProvider;
import com.carcrowd.security.LoginRateLimiterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminEndpointSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginRateLimiterService rateLimiter;

    private AdminUser activeAdmin;
    private AdminUser activeMember;
    private AdminUser disabledAdmin;
    private User customerUser;

    private String adminToken;
    private String memberToken;
    private String customerToken;

    @BeforeEach
    public void setup() {
        rateLimiter.clearAll();

        // 1. Setup Active Admin
        activeAdmin = adminUserRepository.findByEmailIgnoreCase("sec.admin@carcrowd.in")
            .orElseGet(() -> adminUserRepository.save(new AdminUser(
                "sec.admin@carcrowd.in",
                passwordEncoder.encode("SecureAdminPass123!"),
                "Security Admin",
                "ROLE_ADMIN",
                true
            )));
        activeAdmin.setActive(true);
        activeAdmin = adminUserRepository.save(activeAdmin);
        adminToken = tokenProvider.generateToken(activeAdmin.getEmail(), activeAdmin.getRole());

        // 2. Setup Active Member
        activeMember = adminUserRepository.findByEmailIgnoreCase("sec.member@carcrowd.in")
            .orElseGet(() -> adminUserRepository.save(new AdminUser(
                "sec.member@carcrowd.in",
                passwordEncoder.encode("SecureMemberPass123!"),
                "Security Member",
                "ROLE_MEMBER",
                true
            )));
        activeMember.setActive(true);
        activeMember = adminUserRepository.save(activeMember);
        memberToken = tokenProvider.generateToken(activeMember.getEmail(), activeMember.getRole());

        // 3. Setup Disabled Admin
        disabledAdmin = adminUserRepository.findByEmailIgnoreCase("sec.disabled@carcrowd.in")
            .orElseGet(() -> adminUserRepository.save(new AdminUser(
                "sec.disabled@carcrowd.in",
                passwordEncoder.encode("SecureDisabledPass123!"),
                "Disabled Admin",
                "ROLE_ADMIN",
                false
            )));
        disabledAdmin.setActive(false);
        disabledAdmin = adminUserRepository.save(disabledAdmin);

        // 4. Setup Customer
        customerUser = userRepository.findByEmailIgnoreCase("sec.customer@example.com")
            .orElseGet(() -> {
                User u = new User("sub-sec-cust", "sec.customer@example.com", "Security Customer", null);
                u.setPhoneNumber("9999988888");
                u.setPhoneVerified(true);
                return userRepository.save(u);
            });
        customerToken = tokenProvider.generateToken(customerUser.getEmail(), "ROLE_CUSTOMER", customerUser.getId());
    }

    // ==========================================
    // 1. Unauthenticated Access Tests (Expect 401)
    // ==========================================

    @Test
    public void testUnauthenticatedCannotAccessAdminDashboard() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    public void testUnauthenticatedCannotAccessAdminLeads() throws Exception {
        mockMvc.perform(get("/api/admin/leads"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    public void testUnauthenticatedCannotAccessAdminMembers() throws Exception {
        mockMvc.perform(get("/api/admin/members"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    public void testUnauthenticatedCannotAccessAdminCarDemand() throws Exception {
        mockMvc.perform(get("/api/admin/car-demand"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    public void testUnauthenticatedCannotAccessMemberRequests() throws Exception {
        mockMvc.perform(get("/api/member/requests"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    // ==========================================
    // 2. Customer Access Tests (Expect 403 Forbidden on Admin & Member APIs)
    // ==========================================

    @Test
    public void testCustomerForbiddenFromAdminDashboard() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard")
                .header("Authorization", "Bearer " + customerToken))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    public void testCustomerForbiddenFromAdminLeads() throws Exception {
        mockMvc.perform(get("/api/admin/leads")
                .header("Authorization", "Bearer " + customerToken))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    public void testCustomerForbiddenFromAdminMembers() throws Exception {
        mockMvc.perform(get("/api/admin/members")
                .header("Authorization", "Bearer " + customerToken))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    public void testCustomerForbiddenFromAdminCarDemand() throws Exception {
        mockMvc.perform(get("/api/admin/car-demand")
                .header("Authorization", "Bearer " + customerToken))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    public void testCustomerForbiddenFromMemberRequests() throws Exception {
        mockMvc.perform(get("/api/member/requests")
                .header("Authorization", "Bearer " + customerToken))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    // ==========================================
    // 3. Team Member Access Tests (Member role cannot access Admin APIs)
    // ==========================================

    @Test
    public void testMemberForbiddenFromAdminDashboard() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard")
                .header("Authorization", "Bearer " + memberToken))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    public void testMemberForbiddenFromAdminMembers() throws Exception {
        mockMvc.perform(get("/api/admin/members")
                .header("Authorization", "Bearer " + memberToken))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    public void testMemberForbiddenFromAdminCarDemand() throws Exception {
        mockMvc.perform(get("/api/admin/car-demand")
                .header("Authorization", "Bearer " + memberToken))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    public void testMemberForbiddenFromAdminLeads() throws Exception {
        mockMvc.perform(get("/api/admin/leads")
                .header("Authorization", "Bearer " + memberToken))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    public void testMemberCanAccessMemberOwnRequests() throws Exception {
        mockMvc.perform(get("/api/member/requests")
                .header("Authorization", "Bearer " + memberToken))
            .andExpect(status().isOk());
    }

    // ==========================================
    // 4. Admin Access Tests (Admin role succeeds)
    // ==========================================

    @Test
    public void testAdminCanAccessAdminDashboard() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    public void testAdminCanAccessAdminLeads() throws Exception {
        mockMvc.perform(get("/api/admin/leads")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    public void testAdminCanAccessAdminMembers() throws Exception {
        mockMvc.perform(get("/api/admin/members")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    public void testAdminCanAccessAdminCarDemand() throws Exception {
        mockMvc.perform(get("/api/admin/cars/demand")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    // ==========================================
    // 5. Token Tampering & Missing Role Claim (Vulnerability Fix Verification)
    // ==========================================

    @Test
    public void testTokenWithMissingRoleClaimCannotAccessAdminEndpoints() throws Exception {
        // Generate token with null role
        String tokenWithNoRole = tokenProvider.generateToken("attacker@example.com", null);

        mockMvc.perform(get("/api/admin/dashboard")
                .header("Authorization", "Bearer " + tokenWithNoRole))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void testTokenWithEmptyRoleClaimCannotAccessAdminEndpoints() throws Exception {
        String tokenWithEmptyRole = tokenProvider.generateToken("attacker@example.com", "   ");

        mockMvc.perform(get("/api/admin/dashboard")
                .header("Authorization", "Bearer " + tokenWithEmptyRole))
            .andExpect(status().isUnauthorized());
    }

    // ==========================================
    // 6. Disabled Account Token Revocation (Vulnerability Fix Verification)
    // ==========================================

    @Test
    public void testDisabledAdminAccountTokenIsRejectedImmediately() throws Exception {
        String disabledToken = tokenProvider.generateToken(disabledAdmin.getEmail(), disabledAdmin.getRole());

        // Even with a valid cryptographic JWT signature, disabled account must be denied access
        mockMvc.perform(get("/api/admin/dashboard")
                .header("Authorization", "Bearer " + disabledToken))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDisabledAdminCannotLogin() throws Exception {
        AdminLoginRequest loginReq = new AdminLoginRequest(disabledAdmin.getEmail(), "SecureDisabledPass123!");

        mockMvc.perform(post("/api/admin/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Admin account is disabled"));
    }

    // ==========================================
    // 7. Malformed / Bogus Token Tests
    // ==========================================

    @Test
    public void testMalformedJwtTokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard")
                .header("Authorization", "Bearer malformed.jwt.token.here"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void testBogusSecretTokenIsRejected() throws Exception {
        // Token signed with arbitrary key
        String bogusToken = io.jsonwebtoken.Jwts.builder()
            .subject("fake.admin@carcrowd.in")
            .claim("role", "ROLE_ADMIN")
            .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor("TotallyDifferentSecretKeyThatDoesNotMatch12345!".getBytes()))
            .compact();

        mockMvc.perform(get("/api/admin/dashboard")
                .header("Authorization", "Bearer " + bogusToken))
            .andExpect(status().isUnauthorized());
    }

    // ==========================================
    // 8. Admin Login Rate Limiting (Brute-Force Protection)
    // ==========================================

    @Test
    public void testAdminLoginRateLimitingBlocksAfterMaxFailedAttempts() throws Exception {
        String targetEmail = "brute.target@carcrowd.in";
        AdminLoginRequest badLogin = new AdminLoginRequest(targetEmail, "WrongPassword123!");

        // 5 consecutive failed attempts
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/admin/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isBadRequest());
        }

        // 6th attempt should be blocked by rate limiter with 429 Too Many Requests
        mockMvc.perform(post("/api/admin/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badLogin)))
            .andExpect(status().isTooManyRequests())
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Too many failed login attempts")));
    }
}
