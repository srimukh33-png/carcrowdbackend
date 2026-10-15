package com.carcrowd;

import com.carcrowd.dto.admin.UpdateLeadStatusRequest;
import com.carcrowd.entity.Car;
import com.carcrowd.entity.InterestRegistration;
import com.carcrowd.entity.LeadStatus;
import com.carcrowd.entity.User;
import com.carcrowd.repository.CarRepository;
import com.carcrowd.repository.InterestRegistrationRepository;
import com.carcrowd.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CustomerRequestTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private InterestRegistrationRepository registrationRepository;

    private User customerA;
    private User customerB;
    private Car testCar;
    private InterestRegistration regA;
    private InterestRegistration regB;

    @BeforeEach
    public void setupData() {
        customerA = userRepository.findByEmailIgnoreCase("customer.a@example.com")
            .orElseGet(() -> {
                User u = new User("sub-cust-a", "customer.a@example.com", "Customer Alpha", null);
                u.setPhoneNumber("9811111111");
                u.setPhoneVerified(true);
                return userRepository.save(u);
            });

        customerB = userRepository.findByEmailIgnoreCase("customer.b@example.com")
            .orElseGet(() -> {
                User u = new User("sub-cust-b", "customer.b@example.com", "Customer Beta", null);
                u.setPhoneNumber("9822222222");
                u.setPhoneVerified(true);
                return userRepository.save(u);
            });

        testCar = carRepository.findAll().get(0);

        regA = registrationRepository.findByCarIdAndUserId(testCar.getId(), customerA.getId())
            .orElseGet(() -> {
                InterestRegistration r = new InterestRegistration(
                    testCar,
                    customerA.getFullName(),
                    customerA.getPhoneNumber(),
                    customerA.getEmail(),
                    29,
                    "Bangalore",
                    "1–3 months",
                    true
                );
                r.setUser(customerA);
                r.setLeadStatus(LeadStatus.JOINED);
                return registrationRepository.save(r);
            });

        regB = registrationRepository.findByCarIdAndUserId(testCar.getId(), customerB.getId())
            .orElseGet(() -> {
                InterestRegistration r = new InterestRegistration(
                    testCar,
                    customerB.getFullName(),
                    customerB.getPhoneNumber(),
                    customerB.getEmail(),
                    34,
                    "Bangalore",
                    "Immediate (< 15 days)",
                    true
                );
                r.setUser(customerB);
                r.setLeadStatus(LeadStatus.JOINED);
                return registrationRepository.save(r);
            });

        regA.setLeadStatus(LeadStatus.JOINED);
        regA = registrationRepository.save(regA);

        regB.setLeadStatus(LeadStatus.JOINED);
        regB = registrationRepository.save(regB);
    }

    @Test
    @WithMockUser(username = "customer.a@example.com", roles = "CUSTOMER")
    public void testCustomerCanOnlyViewOwnRequests() throws Exception {
        mockMvc.perform(get("/api/my-requests"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", not(empty())))
            .andExpect(jsonPath("$[0].modelName").value(testCar.getModelName()))
            .andExpect(jsonPath("$[0].status").value("JOINED"))
            .andExpect(jsonPath("$[0].statusLabel").value("Request Submitted"));
    }

    @Test
    @WithMockUser(username = "customer.a@example.com", roles = "CUSTOMER")
    public void testCustomerCanViewOwnRequestDetailAndTimeline() throws Exception {
        mockMvc.perform(get("/api/my-requests/" + regA.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(regA.getId()))
            .andExpect(jsonPath("$.fullName").value(customerA.getFullName()))
            .andExpect(jsonPath("$.status").value("JOINED"))
            .andExpect(jsonPath("$.timeline", not(empty())))
            .andExpect(jsonPath("$.timeline[0].status").value("JOINED"))
            .andExpect(jsonPath("$.timeline[0].completed").value(true));
    }

    @Test
    @WithMockUser(username = "customer.a@example.com", roles = "CUSTOMER")
    public void testCustomerCannotViewAnotherCustomerRequest() throws Exception {
        // Customer A trying to access Customer B's request
        mockMvc.perform(get("/api/my-requests/" + regB.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testUnauthenticatedUserCannotAccessMyRequests() throws Exception {
        mockMvc.perform(get("/api/my-requests"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "customer.a@example.com", roles = "CUSTOMER")
    public void testCustomerRoleCannotAccessAdminLeads() throws Exception {
        mockMvc.perform(get("/api/admin/leads"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@carcrowd.in", roles = "ADMIN")
    public void testValidStatusTransitionsAndTerminalStateEnforcement() throws Exception {
        // 1. Reset lead to JOINED
        regA.setLeadStatus(LeadStatus.JOINED);
        registrationRepository.save(regA);

        // 2. JOINED -> CONTACTED
        UpdateLeadStatusRequest reqContact = new UpdateLeadStatusRequest(LeadStatus.CONTACTED, "Phone call connected with customer");
        mockMvc.perform(patch("/api/admin/leads/" + regA.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reqContact)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.leadStatus").value("CONTACTED"));

        // 3. CONTACTED -> WAITING (Interest confirmed)
        UpdateLeadStatusRequest reqWaiting = new UpdateLeadStatusRequest(LeadStatus.WAITING, "Customer confirmed purchase preference. Waiting for quota.");
        mockMvc.perform(patch("/api/admin/leads/" + regA.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reqWaiting)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.leadStatus").value("WAITING"));

        // 4. WAITING -> BOUGHT (Customer bought the car!)
        UpdateLeadStatusRequest reqBought = new UpdateLeadStatusRequest(LeadStatus.BOUGHT, "Customer bought the car with group discount");
        mockMvc.perform(patch("/api/admin/leads/" + regA.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reqBought)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.leadStatus").value("BOUGHT"));

        // 5. BOUGHT is terminal: attempt to move BOUGHT -> JOINED must be rejected (400 Bad Request)
        UpdateLeadStatusRequest reqInvalid = new UpdateLeadStatusRequest(LeadStatus.JOINED, "Trying to reset bought lead");
        mockMvc.perform(patch("/api/admin/leads/" + regA.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reqInvalid)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("terminal")));
    }

    @Test
    @WithMockUser(username = "admin@carcrowd.in", roles = "ADMIN")
    public void testCancellationTransitionAndTerminalEnforcement() throws Exception {
        // Reset lead to CONTACTED
        regB.setLeadStatus(LeadStatus.CONTACTED);
        registrationRepository.save(regB);

        // CONTACTED -> CANCELLED
        UpdateLeadStatusRequest reqCancel = new UpdateLeadStatusRequest(LeadStatus.CANCELLED, "Customer bought vehicle from other showroom");
        mockMvc.perform(patch("/api/admin/leads/" + regB.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reqCancel)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.leadStatus").value("CANCELLED"));

        // CANCELLED is terminal: attempt to move CANCELLED -> WAITING must be rejected
        UpdateLeadStatusRequest reqInvalid = new UpdateLeadStatusRequest(LeadStatus.WAITING, "Trying to resurrect cancelled lead");
        mockMvc.perform(patch("/api/admin/leads/" + regB.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reqInvalid)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("terminal")));
    }

    @Test
    @WithMockUser(username = "customer.a@example.com", roles = "CUSTOMER")
    public void testCustomerTimelineInWaitingAndBoughtStates() throws Exception {
        // Set lead to WAITING
        regA.setLeadStatus(LeadStatus.WAITING);
        registrationRepository.save(regA);

        mockMvc.perform(get("/api/my-requests/" + regA.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("WAITING"))
            .andExpect(jsonPath("$.timeline[0].status").value("JOINED"))
            .andExpect(jsonPath("$.timeline[0].completed").value(true))
            .andExpect(jsonPath("$.timeline[1].status").value("CONTACTED"))
            .andExpect(jsonPath("$.timeline[1].completed").value(true))
            .andExpect(jsonPath("$.timeline[2].status").value("WAITING"))
            .andExpect(jsonPath("$.timeline[2].completed").value(false))
            .andExpect(jsonPath("$.timeline[2].current").value(true))
            .andExpect(jsonPath("$.timeline[3].status").value("BOUGHT"))
            .andExpect(jsonPath("$.timeline[3].completed").value(false))
            .andExpect(jsonPath("$.timeline[3].current").value(false));

        // Now transition lead to BOUGHT
        regA.setLeadStatus(LeadStatus.BOUGHT);
        registrationRepository.save(regA);

        mockMvc.perform(get("/api/my-requests/" + regA.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("BOUGHT"))
            .andExpect(jsonPath("$.timeline[0].completed").value(true))
            .andExpect(jsonPath("$.timeline[1].completed").value(true))
            .andExpect(jsonPath("$.timeline[2].status").value("WAITING"))
            .andExpect(jsonPath("$.timeline[2].completed").value(true))
            .andExpect(jsonPath("$.timeline[3].status").value("BOUGHT"))
            .andExpect(jsonPath("$.timeline[3].completed").value(true))
            .andExpect(jsonPath("$.timeline[3].current").value(true));
    }

    @Test
    @WithMockUser(username = "customer.a@example.com", roles = "CUSTOMER")
    public void testCustomerCanCancelOwnRequestAndBuyerCountIsReduced() throws Exception {
        // Ensure regA is JOINED
        regA.setLeadStatus(LeadStatus.JOINED);
        regA = registrationRepository.save(regA);

        long countBefore = registrationRepository.countActiveByCarId(testCar.getId());

        // Verify car detail API before cancel
        mockMvc.perform(get("/api/cars/" + testCar.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.buyerCount").value((int) countBefore));

        // Customer cancels request
        java.util.Map<String, String> cancelPayload = java.util.Map.of("reason", "Changed mind about timing");
        mockMvc.perform(patch("/api/my-requests/" + regA.getId() + "/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelPayload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELLED"))
            .andExpect(jsonPath("$.statusLabel").value("Request Cancelled"));

        // Verify buyer count for car is reduced by 1
        long countAfter = registrationRepository.countActiveByCarId(testCar.getId());
        org.junit.jupiter.api.Assertions.assertEquals(countBefore - 1, countAfter);

        // Verify public car detail endpoint reflects reduced buyer count
        mockMvc.perform(get("/api/cars/" + testCar.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.buyerCount").value((int) (countBefore - 1)));

        // Verify public car count endpoint reflects reduced buyer count
        mockMvc.perform(get("/api/cars/" + testCar.getId() + "/interests/count"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.count").value((int) (countBefore - 1)));
    }

    @Test
    @WithMockUser(username = "admin@carcrowd.in", roles = "ADMIN")
    public void testAdminCancellingRequestReducesBuyerCount() throws Exception {
        // Ensure regB is JOINED
        regB.setLeadStatus(LeadStatus.JOINED);
        regB = registrationRepository.save(regB);

        long countBefore = registrationRepository.countActiveByCarId(testCar.getId());

        // Admin cancels lead
        UpdateLeadStatusRequest cancelReq = new UpdateLeadStatusRequest(LeadStatus.CANCELLED, "Customer opted out");
        mockMvc.perform(patch("/api/admin/leads/" + regB.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelReq)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.leadStatus").value("CANCELLED"));

        // Buyer count is reduced by 1
        long countAfterCancel = registrationRepository.countActiveByCarId(testCar.getId());
        org.junit.jupiter.api.Assertions.assertEquals(countBefore - 1, countAfterCancel);

        mockMvc.perform(get("/api/cars/" + testCar.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.buyerCount").value((int) countAfterCancel));
    }
}
