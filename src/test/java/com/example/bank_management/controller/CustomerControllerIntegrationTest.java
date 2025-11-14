package com.example.bank_management.controller;

import com.example.bank_management.dto.CustomerDto;
import com.example.bank_management.entity.CustomerEntity;
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

import java.util.Optional;

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
    void cleanDatabase() {
        customerRepository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrieveCustomerSuccessfully() throws Exception {
        // Arrange
        CustomerDto dto = new CustomerDto();
        dto.setName("John Doe");
        dto.setPan("ABCDE1234F");
        dto.setEmail("john@example.com");
        dto.setPhone("9876543210");

        // Act → POST /create
        String response = mockMvc.perform(post("/api/v1/customers/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").exists())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.pan").value("ABCDE1234F"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        CustomerDto saved = objectMapper.readValue(response, CustomerDto.class);

        // Assert DB state
        Optional<CustomerEntity> entity = customerRepository.findById(saved.getCustomerId());
        assertThat(entity).isPresent();
        assertThat(entity.get().getName()).isEqualTo("John Doe");

        // Act → GET /{id}
        mockMvc.perform(get("/api/v1/customers/" + saved.getCustomerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void shouldReturnBadRequestForInvalidCustomerData() throws Exception {
        CustomerDto invalid = new CustomerDto();
        invalid.setName("12##"); // invalid chars
        invalid.setPan("ABC");   // too short
        invalid.setEmail("wrong-format");
        invalid.setPhone("1111111111"); // invalid Indian number

        mockMvc.perform(post("/api/v1/customers/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    //    @Test
//    void shouldReturnAllCustomers() throws Exception {
//        // Setup data
//        customerRepository.save(new CustomerEntity() {{
//            setName("Alice");
//            setPan("AAA1111A");
//            setEmail("alice@mail.com");
//            setPhone("9876501234");
//        }});
//        customerRepository.save(new CustomerEntity() {{
//            setName("Bob");
//            setPan("BBB2222B");
//            setEmail("bob@mail.com");
//            setPhone("9876505678");
//        }});
//
//        // GET /all
//        mockMvc.perform(get("/api/v1/customers/"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(2))
//                .andExpect(jsonPath("$[0].name").value("Alice"))
//                .andExpect(jsonPath("$[1].name").value("Bob"));
//    }

//    @Test
//    void shouldUpdateCustomerSuccessfully() throws Exception {
//        // Insert existing customer
//        CustomerEntity entity = new CustomerEntity();
//        entity.setName("Old Name");
//        entity.setPan("PAN123456");
//        entity.setEmail("old@mail.com");
//        entity.setPhone("9876543210");
//        CustomerEntity saved = customerRepository.save(entity);
//
//        // Update DTO
//        CustomerDto updatedDto = new CustomerDto();
//        updatedDto.setName("Updated Name");
//        updatedDto.setEmail("new@mail.com");
//
//        // PUT /{id}
//        mockMvc.perform(put("/api/v1/customers/" + saved.getCustomerId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updatedDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("Updated Name"))
//                .andExpect(jsonPath("$.email").value("new@mail.com"));
//
//        // Verify in DB
//        CustomerEntity updatedEntity = customerRepository.findById(saved.getCustomerId()).orElseThrow();
//        assertThat(updatedEntity.getName()).isEqualTo("Updated Name");
//        assertThat(updatedEntity.getEmail()).isEqualTo("new@mail.com");
//    }

//    @Test
//    void shouldReturnNotFoundForNonExistentCustomer() throws Exception {
//        mockMvc.perform(get("/api/v1/customers/9999"))
//                .andExpect(status().isNotFound());
//    }
}




//package com.example.bank_management.controller;
//
//import com.example.bank_management.dto.CustomerDto;
//import com.example.bank_management.repository.CustomerRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//class CustomerControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private CustomerRepository customerRepository;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setUp() {
//        customerRepository.deleteAll(); // clean db before each test
//    }
//
//    @Test
//    void testCreateAndGetCustomer() throws Exception {
//        // Create DTO
//        CustomerDto dto = new CustomerDto();
//        dto.setName("John Doe");
//        dto.setPan("ABCD1234");
//        dto.setEmail("john@example.com");
//        dto.setPhone("9876543210");
//
//        // Convert to JSON
//        String json = objectMapper.writeValueAsString(dto);
//
//        // POST: Create customer
//        String response = mockMvc.perform(post("/api/v1/customers/")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.customerId").exists())
//                .andExpect(jsonPath("$.name").value("John Doe"))
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        CustomerDto saved = objectMapper.readValue(response, CustomerDto.class);
//
//        // GET: Retrieve customer by ID
//        mockMvc.perform(get("/api/v1/customers/" + saved.getCustomerId()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("John Doe"));
//
//        // Verify in DB
//        assertThat(customerRepository.findAll()).hasSize(1);
//    }
//
//    @Test
//    void testValidationFailure() throws Exception {
//        // Missing name should fail
//        CustomerDto dto = new CustomerDto();
//        dto.setPan("INVALID123");
//        dto.setEmail("wrong-email-format");
//        dto.setPhone("12345");
//
//        mockMvc.perform(post("/api/v1/customers/")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest());
//    }
//}
