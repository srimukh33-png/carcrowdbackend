package com.carcrowd;

import com.carcrowd.dto.admin.*;
import com.carcrowd.dto.member.MemberNoteRequest;
import com.carcrowd.entity.*;
import com.carcrowd.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TeamMemberManagementTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private InterestRegistrationRepository registrationRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarBrandRepository brandRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private AdminUser adminUser;
    private AdminUser memberOne;
    private AdminUser memberTwo;
    private InterestRegistration leadA;
    private InterestRegistration leadB;

    @BeforeEach
    public void setup() {
        // Ensure Primary Admin exists
        adminUser = adminUserRepository.findByEmailIgnoreCase("admin@carcrowd.in")
            .orElseGet(() -> adminUserRepository.save(new AdminUser(
                "admin@carcrowd.in",
                passwordEncoder.encode("Admin@CarCrowd2025"),
                "Operations Admin",
                "9876500000",
                "ROLE_ADMIN",
                true
            )));
        adminUser.setActive(true);
        adminUser.setRole("ROLE_ADMIN");
        adminUserRepository.save(adminUser);

        // Ensure Member 1 exists
        memberOne = adminUserRepository.findByEmailIgnoreCase("member.one@carcrowd.in")
            .orElseGet(() -> adminUserRepository.save(new AdminUser(
                "member.one@carcrowd.in",
                passwordEncoder.encode("Password123!"),
                "Agent Sarah",
                "9811122233",
                "ROLE_MEMBER",
                true
            )));
        memberOne.setActive(true);
        memberOne.setRole("ROLE_MEMBER");
        adminUserRepository.save(memberOne);

        // Ensure Member 2 exists
        memberTwo = adminUserRepository.findByEmailIgnoreCase("member.two@carcrowd.in")
            .orElseGet(() -> adminUserRepository.save(new AdminUser(
                "member.two@carcrowd.in",
                passwordEncoder.encode("Password123!"),
                "Agent Alex",
                "9844455566",
                "ROLE_MEMBER",
                true
            )));
        memberTwo.setActive(true);
        memberTwo.setRole("ROLE_MEMBER");
        adminUserRepository.save(memberTwo);

        // Ensure a car exists
        CarBrand brand = brandRepository.findAll().stream().findFirst().orElseGet(() ->
            brandRepository.save(new CarBrand("Tata", null, true))
        );
        Car car = carRepository.findAll().stream().findFirst().orElseGet(() ->
            carRepository.save(new Car(brand, "Nexon", "https://img.jpg", "SUV", "Description", "Petrol", "Manual", "5", true))
        );

        // Ensure Lead A assigned to Member 1
        leadA = findByIdAndPhoneNumber(1L, "9812345678").orElse(null);
        if (leadA == null) {
            leadA = new InterestRegistration(car, "Customer Alpha", "9812345678", "alpha@example.com", 30, "Bengaluru", "Within 30 days", true);
            leadA = registrationRepository.save(leadA);
        }
        leadA.setAssignedMember(memberOne);
        leadA.setLeadStatus(LeadStatus.JOINED);
        leadA = registrationRepository.save(leadA);

        // Ensure Lead B assigned to Member 2
        leadB = findByIdAndPhoneNumber(2L, "9898765432").orElse(null);
        if (leadB == null) {
            leadB = new InterestRegistration(car, "Customer Beta", "9898765432", "beta@example.com", 32, "Bengaluru", "Immediate (< 15 days)", true);
            leadB = registrationRepository.save(leadB);
        }
        leadB.setAssignedMember(memberTwo);
        leadB.setLeadStatus(LeadStatus.JOINED);
        leadB = registrationRepository.save(leadB);
    }

    private java.util.Optional<InterestRegistration> findByIdAndPhoneNumber(Long id, String phone) {
        return registrationRepository.findById(id);
    }

    // 1. Admin can create a new team member
    @Test
    @WithMockUser(username = "admin@carcrowd.in", roles = "ADMIN")
    public void testAdminCanCreateMember() throws Exception {
        String uniqueEmail = "new.agent." + System.currentTimeMillis() + "@carcrowd.in";
        CreateMemberRequest req = new CreateMemberRequest(
            "David Miller",
            uniqueEmail,
            "9877788899",
            "SecurePass123",
            "MEMBER",
            true
        );

        mockMvc.perform(post("/api/admin/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.fullName").value("David Miller"))
            .andExpect(jsonPath("$.email").value(uniqueEmail))
            .andExpect(jsonPath("$.role").value("MEMBER"))
            .andExpect(jsonPath("$.active").value(true));
    }

    // 2. Duplicate email is rejected
    @Test
    @WithMockUser(username = "admin@carcrowd.in", roles = "ADMIN")
    public void testDuplicateEmailRejected() throws Exception {
        CreateMemberRequest req = new CreateMemberRequest(
            "Duplicate Name",
            "member.one@carcrowd.in", // already exists
            "9811122233",
            "Password123!",
            "MEMBER",
            true
        );

        mockMvc.perform(post("/api/admin/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    // 3. Member cannot access Admin APIs
    @Test
    @WithMockUser(username = "member.one@carcrowd.in", roles = "MEMBER")
    public void testMemberCannotAccessAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/admin/members"))
            .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/leads"))
            .andExpect(status().isForbidden());
    }

    // 4. Member can access their own assigned requests
    @Test
    @WithMockUser(username = "member.one@carcrowd.in", roles = "MEMBER")
    public void testMemberCanViewOwnAssignedRequests() throws Exception {
        mockMvc.perform(get("/api/member/requests"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", not(empty())))
            .andExpect(jsonPath("$.content[0].assignedMemberEmail").value("member.one@carcrowd.in"));
    }

    // 5. Member cannot view another member's customer request (403 Forbidden)
    @Test
    @WithMockUser(username = "member.one@carcrowd.in", roles = "MEMBER")
    public void testMemberCannotViewOtherMemberCustomerRequest() throws Exception {
        // leadB is assigned to memberTwo, memberOne must receive 403 Forbidden
        mockMvc.perform(get("/api/member/requests/" + leadB.getId()))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message", containsString("not assigned to you")));
    }

    // 6. Member cannot update another member's customer request status (403 Forbidden)
    @Test
    @WithMockUser(username = "member.one@carcrowd.in", roles = "MEMBER")
    public void testMemberCannotUpdateOtherMemberRequestStatus() throws Exception {
        UpdateLeadStatusRequest req = new UpdateLeadStatusRequest(LeadStatus.CONTACTED, "Attempted unauthorized update");

        mockMvc.perform(patch("/api/member/requests/" + leadB.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message", containsString("not assigned to you")));
    }

    // 7. Member CAN update their own assigned customer request status
    @Test
    @WithMockUser(username = "member.one@carcrowd.in", roles = "MEMBER")
    public void testMemberCanUpdateOwnRequestStatus() throws Exception {
        leadA.setLeadStatus(LeadStatus.JOINED);
        registrationRepository.save(leadA);

        UpdateLeadStatusRequest req = new UpdateLeadStatusRequest(LeadStatus.CONTACTED, "Spoke with customer over phone");

        mockMvc.perform(patch("/api/member/requests/" + leadA.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.leadStatus").value("CONTACTED"));
    }

    // 8. Member CAN add note to their own assigned request
    @Test
    @WithMockUser(username = "member.one@carcrowd.in", roles = "MEMBER")
    public void testMemberCanAddNoteToOwnRequest() throws Exception {
        MemberNoteRequest noteReq = new MemberNoteRequest("Customer prefers white color and delivery in Bangalore");

        mockMvc.perform(post("/api/member/requests/" + leadA.getId() + "/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noteReq)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.internalNotes", containsString("Customer prefers white color")));
    }

    // 9. Admin assigns and reassigns a request
    @Test
    @WithMockUser(username = "admin@carcrowd.in", roles = "ADMIN")
    public void testAdminCanAssignAndReassignRequest() throws Exception {
        AssignRequestPayload assignPayload = new AssignRequestPayload(memberTwo.getId());

        mockMvc.perform(patch("/api/admin/requests/" + leadA.getId() + "/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignPayload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.assignedMemberId").value(memberTwo.getId()))
            .andExpect(jsonPath("$.assignedMemberName").value(memberTwo.getFullName()));
    }

    // 10. Inactive member cannot receive assignments
    @Test
    @WithMockUser(username = "admin@carcrowd.in", roles = "ADMIN")
    public void testCannotAssignToInactiveMember() throws Exception {
        memberTwo.setActive(false);
        adminUserRepository.save(memberTwo);

        AssignRequestPayload assignPayload = new AssignRequestPayload(memberTwo.getId());

        mockMvc.perform(patch("/api/admin/requests/" + leadA.getId() + "/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignPayload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("inactive")));
    }

    // 11. Deleting a member with active assignments is blocked
    @Test
    @WithMockUser(username = "admin@carcrowd.in", roles = "ADMIN")
    public void testDeleteMemberWithAssignedLeadsIsBlocked() throws Exception {
        // memberOne has leadA assigned, deleting must fail
        mockMvc.perform(delete("/api/admin/members/" + memberOne.getId()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("assigned customer request")));
    }

    // 12. Cannot delete the only active admin
    @Test
    @WithMockUser(username = "admin@carcrowd.in", roles = "ADMIN")
    public void testCannotDeleteLastActiveAdmin() throws Exception {
        // Ensure no other admin is active so adminUser is strictly the only active admin
        adminUserRepository.findAll().stream()
            .filter(u -> u.isAdmin() && !u.getId().equals(adminUser.getId()))
            .forEach(u -> {
                u.setActive(false);
                adminUserRepository.save(u);
            });

        mockMvc.perform(delete("/api/admin/members/" + adminUser.getId()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("administrator")));
    }

    // 13. Inactive member cannot log in
    @Test
    public void testInactiveMemberCannotLogin() throws Exception {
        memberOne.setActive(false);
        adminUserRepository.save(memberOne);

        com.carcrowd.dto.admin.AdminLoginRequest loginReq = new com.carcrowd.dto.admin.AdminLoginRequest();
        loginReq.setEmail("member.one@carcrowd.in");
        loginReq.setPassword("Password123!");

        mockMvc.perform(post("/api/admin/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("disabled")));
    }
}
