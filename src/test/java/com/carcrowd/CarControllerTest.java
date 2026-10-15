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
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllCars() throws Exception {
        mockMvc.perform(get("/api/cars")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(10))))
            .andExpect(jsonPath("$[0].modelName").isNotEmpty())
            .andExpect(jsonPath("$[0].buyerCount").isNumber());
    }

    @Test
    public void testGetCarById() throws Exception {
        mockMvc.perform(get("/api/cars/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.modelName").isNotEmpty())
            .andExpect(jsonPath("$.buyerCount").isNumber())
            .andExpect(jsonPath("$.description").isNotEmpty());
    }

    @Test
    public void testGetNonExistentCar() throws Exception {
        mockMvc.perform(get("/api/cars/999999")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }
}
