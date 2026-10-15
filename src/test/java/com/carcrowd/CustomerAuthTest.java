package com.carcrowd;

import com.carcrowd.dto.customer.GoogleUserPayload;
import com.carcrowd.dto.customer.OtpSentResponse;
import com.carcrowd.entity.Car;
import com.carcrowd.entity.CarBrand;
import com.carcrowd.entity.User;
import com.carcrowd.repository.CarBrandRepository;
import com.carcrowd.repository.CarRepository;
import com.carcrowd.repository.InterestRegistrationRepository;
import com.carcrowd.repository.UserRepository;
import com.carcrowd.security.GoogleTokenVerifierService;
import com.carcrowd.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CustomerAuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterestRegistrationRepository interestRepository;

    @Autowired
    private CarBrandRepository brandRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @MockBean
    private GoogleTokenVerifierService googleVerifier;

    private Car testCar;

    @BeforeEach
    public void setup() {
        // Setup mock verification behavior
        Mockito.when(googleVerifier.verifyToken("valid-google-id-token"))
            .thenReturn(new GoogleUserPayload(
                "google-sub-123456",
                "rajesh.kumar@example.com",
                true,
                "Rajesh Kumar",
                "https://lh3.googleusercontent.com/photo.jpg"
            ));

        Mockito.when(googleVerifier.verifyToken("invalid-google-token"))
            .thenThrow(new com.carcrowd.exception.BadRequestException("Invalid or expired Google identity token"));

        if (carRepository.count() == 0) {
            CarBrand brand = brandRepository.save(new CarBrand("Tata", null, true));
            testCar = carRepository.save(new Car(brand, "Nexon", null, "SUV", "Description", "Petrol", "Manual", "5 Seater", true));
        } else {
            testCar = carRepository.findAll().get(0);
        }
    }

    @Test
    public void testSuccessfulGoogleSignInCreatesAccountAndSetsHttpOnlyCookie() throws Exception {
        String payload = """
            {
                "idToken": "valid-google-id-token"
            }
            """;

        MvcResult result = mockMvc.perform(post("/api/auth/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(cookie().exists("carcrowd_customer_session"))
            .andExpect(cookie().httpOnly("carcrowd_customer_session", true))
            .andExpect(jsonPath("$.email").value("rajesh.kumar@example.com"))
            .andExpect(jsonPath("$.fullName").value("Rajesh Kumar"))
            .andExpect(jsonPath("$.profileComplete").value(false))
            .andReturn();

        // Verify account exists in DB by Google sub
        User savedUser = userRepository.findByGoogleSub("google-sub-123456").orElse(null);
        assertNotNull(savedUser);
        assertEquals("rajesh.kumar@example.com", savedUser.getEmail());
    }

    @Test
    public void testInvalidGoogleTokenRejected() throws Exception {
        String payload = """
            {
                "idToken": "invalid-google-token"
            }
            """;

        mockMvc.perform(post("/api/auth/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("Invalid or expired Google identity token")));
    }

    @Test
    public void testGetProfileWithAndWithoutCookie() throws Exception {
        // Without cookie -> 401
        mockMvc.perform(get("/api/auth/me"))
            .andExpect(status().isUnauthorized());

        // Create user & token
        User user = new User("sub-profile-test", "profile.test@example.com", "Profile Test", null);
        user = userRepository.save(user);

        String token = tokenProvider.generateToken(user.getEmail(), "ROLE_CUSTOMER", user.getId());
        Cookie sessionCookie = new Cookie("carcrowd_customer_session", token);

        // With cookie -> 200
        mockMvc.perform(get("/api/auth/me").cookie(sessionCookie))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("profile.test@example.com"))
            .andExpect(jsonPath("$.profileComplete").value(false));
    }

    @Test
    public void testCustomerCannotAccessAdminApis() throws Exception {
        User user = new User("sub-customer-probe", "probe@example.com", "Probe User", null);
        user = userRepository.save(user);

        String customerToken = tokenProvider.generateToken(user.getEmail(), "ROLE_CUSTOMER", user.getId());
        Cookie sessionCookie = new Cookie("carcrowd_customer_session", customerToken);

        // Accessing /api/admin/dashboard with customer session must be forbidden (403)
        mockMvc.perform(get("/api/admin/dashboard").cookie(sessionCookie))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testDirectPhoneSaveFlow() throws Exception {
        User user = new User("sub-phone-test", "phone.test@example.com", "Phone Tester", null);
        user = userRepository.save(user);

        String token = tokenProvider.generateToken(user.getEmail(), "ROLE_CUSTOMER", user.getId());
        Cookie sessionCookie = new Cookie("carcrowd_customer_session", token);

        // 1. Submit invalid phone number (less than 10 digits) -> 400
        String invalidPhonePayload = """
            {
                "phoneNumber": "12345"
            }
            """;

        mockMvc.perform(post("/api/profile/phone")
                .cookie(sessionCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPhonePayload))
            .andExpect(status().isBadRequest());

        // 2. Submit valid 10-digit phone number -> 200, marked verified & complete immediately without OTP
        String phonePayload = """
            {
                "phoneNumber": "9876543210"
            }
            """;

        mockMvc.perform(post("/api/profile/phone")
                .cookie(sessionCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(phonePayload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.phoneNumber").value("9876543210"))
            .andExpect(jsonPath("$.phoneVerified").value(true))
            .andExpect(jsonPath("$.profileComplete").value(true));

        // 3. Verify user in database is updated
        User updated = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals("9876543210", updated.getPhoneNumber());
        assertTrue(updated.isPhoneVerified());
        assertTrue(updated.isProfileComplete());
    }

    @Test
    public void testInterestRegistrationRequiresAuthAndCompleteProfile() throws Exception {
        Long carId = testCar.getId();

        // 1. Unauthenticated -> 401
        Map<String, Object> payload = new HashMap<>();
        payload.put("fullName", "Unauthorized Visitor");
        payload.put("phoneNumber", "9876500001");
        payload.put("email", "unauth@example.com");
        payload.put("age", 28);
        payload.put("city", "Bengaluru");
        payload.put("purchaseTimeline", "1–3 months");
        payload.put("consent", true);

        mockMvc.perform(post("/api/cars/" + carId + "/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isUnauthorized());

        // 2. Authenticated customer attempting to submit for another customer -> 403 Forbidden
        User incompleteUser = new User("sub-incomplete", "incomplete@example.com", "Incomplete User", null);
        incompleteUser = userRepository.save(incompleteUser);

        String incompleteToken = tokenProvider.generateToken(incompleteUser.getEmail(), "ROLE_CUSTOMER", incompleteUser.getId());
        Cookie incompleteCookie = new Cookie("carcrowd_customer_session", incompleteToken);

        // payload has email "unauth@example.com", different from incompleteUser's email
        mockMvc.perform(post("/api/cars/" + carId + "/interests")
                .cookie(incompleteCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value(containsString("cannot submit requests on behalf of another customer")));

        // 3. Authenticated customer provides their own name and phone number -> 201 Created without OTP requirement
        Map<String, Object> ownPayload = new HashMap<>(payload);
        ownPayload.put("fullName", "Corrected Full Name");
        ownPayload.put("phoneNumber", "+91 9876500002");
        ownPayload.put("email", incompleteUser.getEmail());

        mockMvc.perform(post("/api/cars/" + carId + "/interests")
                .cookie(incompleteCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ownPayload)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true));

        User refreshedUser = userRepository.findById(incompleteUser.getId()).orElse(null);
        assertNotNull(refreshedUser);
        assertEquals("Corrected Full Name", refreshedUser.getFullName());
        assertEquals("9876500002", refreshedUser.getPhoneNumber());
        assertFalse(refreshedUser.isPhoneVerified()); // Not marked verified without SMS OTP process

        // 4. Authenticated buyer with own email -> 201 Created
        User verifiedUser = new User("sub-verified-buyer", "buyer@example.com", "Verified Buyer", null);
        verifiedUser.setPhoneNumber("9876511111");
        verifiedUser.setPhoneVerified(true);
        verifiedUser = userRepository.save(verifiedUser);

        String verifiedToken = tokenProvider.generateToken(verifiedUser.getEmail(), "ROLE_CUSTOMER", verifiedUser.getId());
        Cookie verifiedCookie = new Cookie("carcrowd_customer_session", verifiedToken);

        Map<String, Object> buyerPayload = new HashMap<>(payload);
        buyerPayload.put("fullName", "Verified Buyer");
        buyerPayload.put("phoneNumber", "+91 9876511111");
        buyerPayload.put("email", verifiedUser.getEmail());

        long initialCount = interestRepository.countActiveByCarId(carId);

        mockMvc.perform(post("/api/cars/" + carId + "/interests")
                .cookie(verifiedCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buyerPayload)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.duplicate").value(false))
            .andExpect(jsonPath("$.currentBuyerCount").value((int)(initialCount + 1)));

        // 5. Repeat submission by same customer -> duplicate=true without inflating count
        mockMvc.perform(post("/api/cars/" + carId + "/interests")
                .cookie(verifiedCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buyerPayload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.duplicate").value(true))
            .andExpect(jsonPath("$.currentBuyerCount").value((int)(initialCount + 1)));
    }

    @Test
    public void testLogoutClearsSessionCookie() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
            .andExpect(status().isOk())
            .andExpect(cookie().exists("carcrowd_customer_session"))
            .andExpect(cookie().maxAge("carcrowd_customer_session", 0));
    }
}
