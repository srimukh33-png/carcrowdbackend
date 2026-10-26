package com.carcrowd;

import com.carcrowd.dto.admin.CreateCarRequest;
import com.carcrowd.dto.admin.UpdateCarRequest;
import com.carcrowd.entity.Car;
import com.carcrowd.entity.CarBrand;
import com.carcrowd.entity.InterestRegistration;
import com.carcrowd.repository.CarBrandRepository;
import com.carcrowd.repository.CarRepository;
import com.carcrowd.repository.InterestRegistrationRepository;
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

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminCarManagementTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarBrandRepository brandRepository;

    @Autowired
    private InterestRegistrationRepository interestRepository;

    private String adminToken;

    @BeforeEach
    public void setUp() throws Exception {
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
    public void testAdminGetCarsWithPaginationAndFilters() throws Exception {
        mockMvc.perform(get("/api/admin/cars")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.totalElements", greaterThan(0)))
                .andExpect(jsonPath("$.content[0].modelName").isNotEmpty())
                .andExpect(jsonPath("$.content[0].brandName").isNotEmpty());
    }

    @Test
    public void testAdminCreateCarSuccess() throws Exception {
        CarBrand brand = brandRepository.findAllByActiveTrueOrderByNameAsc().get(0);
        String uniqueModelName = "Test-Model-" + System.currentTimeMillis();

        CreateCarRequest request = new CreateCarRequest(
                brand.getId(),
                uniqueModelName,
                "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&w=800&q=80",
                "Compact SUV",
                "Brand new electric test SUV with panoramic sunroof.",
                "All-Electric",
                "Automatic",
                "5 Seater",
                true
        );

        MvcResult result = mockMvc.perform(post("/api/admin/cars")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.modelName").value(uniqueModelName))
                .andExpect(jsonPath("$.brandId").value(brand.getId()))
                .andExpect(jsonPath("$.active").value(true))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        Long createdId = json.get("id").asLong();

        // Verify car is present in public API
        mockMvc.perform(get("/api/cars/" + createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelName").value(uniqueModelName));
    }

    @Test
    public void testAdminCreateCarDuplicateFails() throws Exception {
        CarBrand brand = brandRepository.findAllByActiveTrueOrderByNameAsc().get(0);
        String modelName = "Duplicate-Test-" + System.currentTimeMillis();

        CreateCarRequest request = new CreateCarRequest(
                brand.getId(),
                modelName,
                "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&w=800&q=80",
                "Sedan",
                "Description",
                "Petrol",
                "Manual",
                "5 Seater",
                true
        );

        // First creation succeeds
        mockMvc.perform(post("/api/admin/cars")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Duplicate creation fails with 400 Bad Request
        mockMvc.perform(post("/api/admin/cars")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    public void testAdminCreateCarValidationErrors() throws Exception {
        // Missing required fields
        CreateCarRequest invalidRequest = new CreateCarRequest();
        invalidRequest.setModelName(""); // Blank

        mockMvc.perform(post("/api/admin/cars")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAdminUpdateCarSuccess() throws Exception {
        CarBrand brand = brandRepository.findAllByActiveTrueOrderByNameAsc().get(0);
        String modelName = "Update-Target-" + System.currentTimeMillis();

        CreateCarRequest createRequest = new CreateCarRequest(
                brand.getId(),
                modelName,
                "https://example.com/initial.jpg",
                "Sedan",
                "Initial description",
                "Petrol",
                "Manual",
                "5 Seater",
                true
        );

        MvcResult createResult = mockMvc.perform(post("/api/admin/cars")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        Long carId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        // Update car
        UpdateCarRequest updateRequest = new UpdateCarRequest(
                brand.getId(),
                modelName + "-Updated",
                "https://example.com/updated.jpg",
                "SUV",
                "Updated description",
                "Hybrid",
                "Automatic",
                "7 Seater",
                true
        );

        mockMvc.perform(put("/api/admin/cars/" + carId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelName").value(modelName + "-Updated"))
                .andExpect(jsonPath("$.bodyType").value("SUV"))
                .andExpect(jsonPath("$.seating").value("7 Seater"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/updated.jpg"));
    }

    @Test
    public void testAdminUpdateCarStatusToggle() throws Exception {
        CarBrand brand = brandRepository.findAllByActiveTrueOrderByNameAsc().get(0);
        String modelName = "Status-Toggle-" + System.currentTimeMillis();

        CreateCarRequest createRequest = new CreateCarRequest(
                brand.getId(),
                modelName,
                "https://example.com/car.jpg",
                "Hatchback",
                "Description",
                "Petrol",
                "Manual",
                "5 Seater",
                true
        );

        MvcResult createResult = mockMvc.perform(post("/api/admin/cars")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        Long carId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        // Deactivate via PATCH
        mockMvc.perform(patch("/api/admin/cars/" + carId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("active", false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        // Inactive car should no longer be in public API
        mockMvc.perform(get("/api/cars/" + carId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAdminDeleteCarWithNoReferencesPhysicalDelete() throws Exception {
        CarBrand brand = brandRepository.findAllByActiveTrueOrderByNameAsc().get(0);
        String modelName = "Zero-Ref-" + System.currentTimeMillis();

        CreateCarRequest createRequest = new CreateCarRequest(
                brand.getId(),
                modelName,
                "https://example.com/car.jpg",
                "Sedan",
                "Description",
                "Petrol",
                "Manual",
                "5 Seater",
                true
        );

        MvcResult createResult = mockMvc.perform(post("/api/admin/cars")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        Long carId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        // Delete car
        mockMvc.perform(delete("/api/admin/cars/" + carId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.physicallyDeleted").value(true))
                .andExpect(jsonPath("$.deactivated").value(false))
                .andExpect(jsonPath("$.referencedLeadsCount").value(0));

        // Car record is completely gone from DB
        assertFalse(carRepository.findById(carId).isPresent());
    }

    @Test
    public void testAdminDeleteCarWithReferencesSoftDeletes() throws Exception {
        CarBrand brand = brandRepository.findAllByActiveTrueOrderByNameAsc().get(0);
        String modelName = "Referenced-Car-" + System.currentTimeMillis();

        Car car = carRepository.save(new Car(
                brand,
                modelName,
                "https://example.com/ref.jpg",
                "SUV",
                "Description",
                "Diesel",
                "Automatic",
                "5 Seater",
                true
        ));

        // Attach an active customer registration to this car
        InterestRegistration registration = new InterestRegistration(
                car,
                "Test Buyer",
                "9876543210",
                "buyer@test.com",
                28,
                "Bengaluru",
                "Within 30 days",
                true
        );
        interestRepository.save(registration);

        // Verify count
        assertEquals(1, interestRepository.countByCarId(car.getId()));

        // Delete request from admin
        mockMvc.perform(delete("/api/admin/cars/" + car.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.physicallyDeleted").value(false))
                .andExpect(jsonPath("$.deactivated").value(true))
                .andExpect(jsonPath("$.referencedLeadsCount").value(1));

        // Car record must still exist in DB to preserve customer reference integrity
        Car refreshed = carRepository.findById(car.getId()).orElse(null);
        assertNotNull(refreshed);
        assertFalse(refreshed.isActive()); // Safely deactivated

        // Customer request is intact
        assertEquals(1, interestRepository.countByCarId(car.getId()));

        // Inactive car is NOT accessible in public catalogue
        mockMvc.perform(get("/api/cars/" + car.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUnauthorizedUserCannotAccessAdminCars() throws Exception {
        mockMvc.perform(get("/api/admin/cars"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/admin/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetAdminBrands() throws Exception {
        mockMvc.perform(get("/api/admin/brands")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].name").isNotEmpty());
    }
}
