package com.example.bank_management.controller;

import com.example.bank_management.dto.CustomerDto;
import com.example.bank_management.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll(); // clean db before each test
    }

    @Test
    void testCreateAndGetCustomer() throws Exception {
        // Create DTO
        CustomerDto dto = new CustomerDto();
        dto.setName("John Doe");
        dto.setPan("ABCD1234");
        dto.setEmail("john@example.com");
        dto.setPhone("9876543210");

        // Convert to JSON
        String json = objectMapper.writeValueAsString(dto);

        // POST: Create customer
        String response = mockMvc.perform(post("/api/v1/customers/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").exists())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        CustomerDto saved = objectMapper.readValue(response, CustomerDto.class);

        // GET: Retrieve customer by ID
        mockMvc.perform(get("/api/v1/customers/" + saved.getCustomerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));

        // Verify in DB
        assertThat(customerRepository.findAll()).hasSize(1);
    }

    @Test
    void testValidationFailure() throws Exception {
        // Missing name should fail
        CustomerDto dto = new CustomerDto();
        dto.setPan("INVALID123");
        dto.setEmail("wrong-email-format");
        dto.setPhone("12345");

        mockMvc.perform(post("/api/v1/customers/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
