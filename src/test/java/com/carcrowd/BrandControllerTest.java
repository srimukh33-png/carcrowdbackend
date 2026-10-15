package com.carcrowd;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetActiveBrands() throws Exception {
        mockMvc.perform(get("/api/brands")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(20))))
            .andExpect(jsonPath("$[*].name", hasItems("Tata", "Hyundai", "Maruti Suzuki", "Mahindra", "Kia", "BMW", "Mercedes-Benz", "Land Rover")));
    }

    @Test
    public void testGetBrandCars() throws Exception {
        mockMvc.perform(get("/api/brands/1/cars")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThan(0))))
            .andExpect(jsonPath("$[0].brandName").isNotEmpty());
    }

    @Test
    public void testGetNonExistentBrand() throws Exception {
        mockMvc.perform(get("/api/brands/999999")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
