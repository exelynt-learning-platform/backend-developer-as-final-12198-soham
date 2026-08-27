package com.example.booking.controller;

import com.example.booking.dto.auth.LoginRequest;
import com.example.booking.dto.resource.ResourceRequest;
import com.example.booking.security.JwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = obtainToken("admin", "admin123");
        userToken = obtainToken("user", "user123");
    }

    private String obtainToken(String username, String password) throws Exception {
        LoginRequest request = LoginRequest.builder()
                .username(username)
                .password(password)
                .build();

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return "Bearer " + jsonNode.get("token").asText();
    }

    @Test
    @DisplayName("Should allow both ADMIN and USER to retrieve resources")
    void getResources_Success() throws Exception {
        mockMvc.perform(get("/resources")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        mockMvc.perform(get("/resources")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("Should allow ADMIN to create resource")
    void createResource_Admin_Success() throws Exception {
        ResourceRequest request = ResourceRequest.builder()
                .name("New Auditorium")
                .description("Main Auditorium")
                .location("Building A")
                .available(true)
                .build();

        mockMvc.perform(post("/resources")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("New Auditorium")));
    }

    @Test
    @DisplayName("Should deny USER from creating resource with 403 Forbidden")
    void createResource_User_Forbidden() throws Exception {
        ResourceRequest request = ResourceRequest.builder()
                .name("Unauthorized Resource")
                .description("Desc")
                .location("Loc")
                .available(true)
                .build();

        mockMvc.perform(post("/resources")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)));
    }

    @Test
    @DisplayName("Should allow ADMIN to update resource")
    void updateResource_Admin_Success() throws Exception {
        ResourceRequest request = ResourceRequest.builder()
                .name("Updated Room A")
                .description("Updated description")
                .location("Building 1, Floor 3")
                .available(true)
                .build();

        mockMvc.perform(put("/resources/1")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Room A")));
    }

    @Test
    @DisplayName("Should deny USER from updating resource with 403 Forbidden")
    void updateResource_User_Forbidden() throws Exception {
        ResourceRequest request = ResourceRequest.builder()
                .name("Updated Room A")
                .description("Updated description")
                .location("Building 1, Floor 3")
                .available(true)
                .build();

        mockMvc.perform(put("/resources/1")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow ADMIN to delete resource")
    void deleteResource_Admin_Success() throws Exception {
        // First create a resource to delete
        ResourceRequest createReq = ResourceRequest.builder()
                .name("Temporary Room")
                .description("To be deleted")
                .location("Building 9")
                .available(true)
                .build();

        MvcResult createResult = mockMvc.perform(post("/resources")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long createdId = jsonNode.get("id").asLong();

        mockMvc.perform(delete("/resources/" + createdId)
                        .header("Authorization", adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should deny USER from deleting resource with 403 Forbidden")
    void deleteResource_User_Forbidden() throws Exception {
        mockMvc.perform(delete("/resources/1")
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }
}
