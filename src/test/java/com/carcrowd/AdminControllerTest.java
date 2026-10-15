package com.carcrowd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;

    @BeforeEach
    public void setUp() throws Exception {
        // Authenticate as default seeded admin
        String loginPayload = """
            {
                "email": "admin@carcrowd.in",
                "password": "Admin@CarCrowd2025"
            }
            """;

        MvcResult result = mockMvc.perform(post("/api/admin/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload))
            .andExpect(status().isOk())
            .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        this.adminToken = json.get("token").asText();
    }

    @Test
    public void testAdminLoginSuccess() {
        assertFalse(adminToken.isEmpty());
    }

    @Test
    public void testAdminLoginFailureWithBadPassword() throws Exception {
        String badPayload = """
            {
                "email": "admin@carcrowd.in",
                "password": "WrongPassword123!"
            }
            """;

        mockMvc.perform(post("/api/admin/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badPayload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    public void testUnauthorizedAccessRejected() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/admin/leads"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetDashboardMetrics() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalLeads", greaterThan(0)))
            .andExpect(jsonPath("$.recentRegistrations", not(empty())))
            .andExpect(jsonPath("$.popularCars", not(empty())))
            .andExpect(jsonPath("$.demandByCity").isNotEmpty());
    }

    @Test
    public void testGetCustomerLeadsPaginationAndFilter() throws Exception {
        mockMvc.perform(get("/api/admin/leads?page=0&size=10")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(lessThanOrEqualTo(10))))
            .andExpect(jsonPath("$.totalElements", greaterThan(0)));

        // Filter by city
        mockMvc.perform(get("/api/admin/leads?city=Bengaluru&page=0&size=10")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[*].city", everyItem(equalToIgnoringCase("Bengaluru"))));
    }

    @Test
    public void testGetLeadDetailsAndUpdateStatus() throws Exception {
        // Fetch first JOINED lead
        MvcResult listResult = mockMvc.perform(get("/api/admin/leads?status=JOINED&page=0&size=1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andReturn();

        JsonNode listJson = objectMapper.readTree(listResult.getResponse().getContentAsString());
        long leadId = listJson.get("content").get(0).get("id").asLong();

        // View Details
        mockMvc.perform(get("/api/admin/leads/" + leadId)
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(leadId))
            .andExpect(jsonPath("$.fullName").isNotEmpty())
            .andExpect(jsonPath("$.phoneNumber").isNotEmpty());

        // Update status to CONTACTED
        String statusUpdate = """
            {
                "status": "CONTACTED",
                "note": "Called lead to confirm dealership test drive preference."
            }
            """;

        mockMvc.perform(patch("/api/admin/leads/" + leadId + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(statusUpdate))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.leadStatus").value("CONTACTED"))
            .andExpect(jsonPath("$.activities", not(empty())));
    }

    @Test
    public void testUpdateFollowUpAndAddActivity() throws Exception {
        MvcResult listResult = mockMvc.perform(get("/api/admin/leads?page=0&size=1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andReturn();

        JsonNode listJson = objectMapper.readTree(listResult.getResponse().getContentAsString());
        long leadId = listJson.get("content").get(0).get("id").asLong();

        // Update follow-up
        String followUpPayload = """
            {
                "nextFollowUpDate": "2026-11-15",
                "internalNotes": "Customer requested callback after budget allocation."
            }
            """;

        mockMvc.perform(patch("/api/admin/leads/" + leadId + "/follow-up")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(followUpPayload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nextFollowUpDate").value("2026-11-15"))
            .andExpect(jsonPath("$.internalNotes").value("Customer requested callback after budget allocation."));

        // Add custom activity
        String activityPayload = """
            {
                "activityType": "PHONE_CALL",
                "description": "Discussed bulk booking discount tier for Tata Curvv."
            }
            """;

        mockMvc.perform(post("/api/admin/leads/" + leadId + "/activities")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(activityPayload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.activityType").value("PHONE_CALL"))
            .andExpect(jsonPath("$.description").value("Discussed bulk booking discount tier for Tata Curvv."));
    }

    @Test
    public void testGetCarDemandAnalytics() throws Exception {
        mockMvc.perform(get("/api/admin/cars/demand")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", not(empty())))
            .andExpect(jsonPath("$[0].modelName").isNotEmpty())
            .andExpect(jsonPath("$[0].totalInterestedBuyers", greaterThan(0)));
    }

    @Test
    public void testCsvExport() throws Exception {
        MvcResult csvResult = mockMvc.perform(get("/api/admin/leads/export")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", containsString("text/csv")))
            .andReturn();

        String csvContent = csvResult.getResponse().getContentAsString();
        assertTrue(csvContent.contains("Lead ID,Full Name,Phone Number,Email,Brand,Model"));
        assertTrue(csvContent.contains("admin@carcrowd.in") || csvContent.contains("buyer.b"));
    }

    @Test
    public void testPublicApiDoesNotExposeLeadDetails() throws Exception {
        // Public car detail endpoint
        MvcResult carResult = mockMvc.perform(get("/api/cars/1"))
            .andExpect(status().isOk())
            .andReturn();

        String carResponse = carResult.getResponse().getContentAsString();
        assertFalse(carResponse.contains("phoneNumber"));
        assertFalse(carResponse.contains("admin@carcrowd.in"));
        assertFalse(carResponse.contains("internalNotes"));
    }
}
