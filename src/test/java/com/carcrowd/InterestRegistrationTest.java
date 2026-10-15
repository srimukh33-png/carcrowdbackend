package com.carcrowd;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "anand.verma@example.com", roles = "CUSTOMER")
public class InterestRegistrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.carcrowd.repository.UserRepository userRepository;

    @org.junit.jupiter.api.BeforeEach
    public void setupCustomer() {
        userRepository.findByEmailIgnoreCase("anand.verma@example.com").ifPresentOrElse(
            u -> {
                u.setPhoneNumber("9988776655");
                u.setPhoneVerified(true);
                userRepository.save(u);
            },
            () -> {
                com.carcrowd.entity.User user = new com.carcrowd.entity.User(
                    "google-sub-anand",
                    "anand.verma@example.com",
                    "Anand Verma",
                    null
                );
                user.setPhoneNumber("9988776655");
                user.setPhoneVerified(true);
                userRepository.save(user);
            }
        );
    }

    @Test
    public void testSuccessfulRegistrationAndCountConsistency() throws Exception {
        Long carId = 2L;

        // 1. Fetch initial count
        String countResponse = mockMvc.perform(get("/api/cars/" + carId + "/interests/count"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        long initialCount = objectMapper.readTree(countResponse).get("count").asLong();

        // 2. Submit new interest registration
        Map<String, Object> payload = new HashMap<>();
        payload.put("fullName", "Anand Verma");
        payload.put("phoneNumber", "+91 9988776655");
        payload.put("email", "anand.verma@example.com");
        payload.put("age", 32);
        payload.put("city", "Bangalore");
        payload.put("purchaseTimeline", "1–3 months");
        payload.put("consent", true);

        mockMvc.perform(post("/api/cars/" + carId + "/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.duplicate", is(false)))
            .andExpect(jsonPath("$.currentBuyerCount", is((int) (initialCount + 1))));

        // 3. Verify authoritative count increased by exactly 1
        mockMvc.perform(get("/api/cars/" + carId + "/interests/count"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.count", is((int) (initialCount + 1))));

        // 4. Test duplicate submission with different spacing / +91 prefix
        Map<String, Object> duplicatePayload = new HashMap<>();
        duplicatePayload.put("fullName", "Anand Verma");
        duplicatePayload.put("phoneNumber", "9988776655"); // same normalized 10 digits
        duplicatePayload.put("email", "ANAND.VERMA@example.com"); // same email uppercase
        duplicatePayload.put("age", 32);
        duplicatePayload.put("city", "Bangalore");
        duplicatePayload.put("purchaseTimeline", "Within 1 month");
        duplicatePayload.put("consent", true);

        mockMvc.perform(post("/api/cars/" + carId + "/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicatePayload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.duplicate", is(true)))
            .andExpect(jsonPath("$.currentBuyerCount", is((int) (initialCount + 1)))); // Count MUST NOT increment
    }

    @Test
    public void testInvalidEmailValidation() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("fullName", "Rajesh Gowda");
        payload.put("phoneNumber", "9876543210");
        payload.put("email", "not-an-email"); // invalid email
        payload.put("age", 25);
        payload.put("city", "Bangalore");
        payload.put("consent", true);

        mockMvc.perform(post("/api/cars/1/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.email").isNotEmpty());
    }

    @Test
    public void testInvalidPhoneNumberValidation() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("fullName", "Rajesh Gowda");
        payload.put("phoneNumber", "12345"); // invalid phone number (too short, doesn't match Indian prefix)
        payload.put("email", "rajesh@example.com");
        payload.put("age", 25);
        payload.put("city", "Bangalore");
        payload.put("consent", true);

        mockMvc.perform(post("/api/cars/1/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.phoneNumber").isNotEmpty());
    }

    @Test
    public void testAgeBelow18Validation() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("fullName", "Minor User");
        payload.put("phoneNumber", "9876543211");
        payload.put("email", "minor@example.com");
        payload.put("age", 17); // under 18
        payload.put("city", "Bangalore");
        payload.put("consent", true);

        mockMvc.perform(post("/api/cars/1/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.age").isNotEmpty());
    }

    @Test
    public void testMissingConsentValidation() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("fullName", "Vikram Rao");
        payload.put("phoneNumber", "9876543212");
        payload.put("email", "vikram@example.com");
        payload.put("age", 29);
        payload.put("city", "Bangalore");
        payload.put("consent", false); // missing consent

        mockMvc.perform(post("/api/cars/1/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.consent").isNotEmpty());
    }

    @Test
    public void testRegistrationForNonExistentCar() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("fullName", "Sunil Shetty");
        payload.put("phoneNumber", "9876543213");
        payload.put("email", "anand.verma@example.com");
        payload.put("age", 40);
        payload.put("city", "Bangalore");
        payload.put("consent", true);

        mockMvc.perform(post("/api/cars/999999/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    public void testBlankFullNameValidation() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("fullName", "   "); // blank name
        payload.put("phoneNumber", "9876543210");
        payload.put("email", "anand.verma@example.com");
        payload.put("age", 25);
        payload.put("city", "Bangalore");
        payload.put("consent", true);

        mockMvc.perform(post("/api/cars/1/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.fullName").isNotEmpty());
    }

    @Test
    public void testTooShortFullNameValidation() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("fullName", "A"); // less than 2 characters
        payload.put("phoneNumber", "9876543210");
        payload.put("email", "anand.verma@example.com");
        payload.put("age", 25);
        payload.put("city", "Bangalore");
        payload.put("consent", true);

        mockMvc.perform(post("/api/cars/1/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.fullName").isNotEmpty());
    }

    @Test
    public void testFullNameWithInvalidCharactersValidation() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("fullName", "John123#"); // numbers and invalid symbols
        payload.put("phoneNumber", "9876543210");
        payload.put("email", "anand.verma@example.com");
        payload.put("age", 25);
        payload.put("city", "Bangalore");
        payload.put("consent", true);

        mockMvc.perform(post("/api/cars/1/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.fullName").isNotEmpty());
    }

    @Test
    public void testValidFullNameWithPunctuationSucceeds() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("fullName", "Mary-Jane O'Connor");
        payload.put("phoneNumber", "+91 9876543210");
        payload.put("email", "anand.verma@example.com");
        payload.put("age", 30);
        payload.put("city", "Bangalore");
        payload.put("consent", true);

        mockMvc.perform(post("/api/cars/1/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    public void testValidPhoneNumberWithSpacesAndDashesNormalizesCorrectly() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("fullName", "Rohan Mehra");
        payload.put("phoneNumber", "+91-98765-43210"); // formatted phone number
        payload.put("email", "anand.verma@example.com");
        payload.put("age", 35);
        payload.put("city", "Bangalore");
        payload.put("consent", true);

        mockMvc.perform(post("/api/cars/3/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    public void testCannotSubmitOnBehalfOfOtherCustomer() throws Exception {
        // Authenticated as anand.verma@example.com, but payload specifies victim@example.com
        Map<String, Object> payload = new HashMap<>();
        payload.put("fullName", "Impersonator");
        payload.put("phoneNumber", "+91 9876543210");
        payload.put("email", "victim@example.com"); // different from authenticated user
        payload.put("age", 29);
        payload.put("city", "Bangalore");
        payload.put("consent", true);

        mockMvc.perform(post("/api/cars/1/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("cannot submit requests on behalf of another customer")));
    }

    @Test
    @WithMockUser(username = "admin@carcrowd.in", roles = "ADMIN")
    public void testPersistedNameAndPhoneVisibleInAdminConsole() throws Exception {
        mockMvc.perform(get("/api/admin/leads?search=Anand"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].fullName").value("Anand Verma"))
            .andExpect(jsonPath("$.content[0].phoneNumber").value("9988776655"));
    }
}
