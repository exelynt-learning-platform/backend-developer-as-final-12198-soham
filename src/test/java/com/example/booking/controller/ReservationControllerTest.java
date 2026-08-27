package com.example.booking.controller;

import com.example.booking.dto.auth.LoginRequest;
import com.example.booking.dto.reservation.ReservationCreateRequest;
import com.example.booking.dto.reservation.ReservationUpdateRequest;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.entity.Role;
import com.example.booking.entity.User;
import com.example.booking.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String userToken;
    private String user2Token;

    @BeforeEach
    void setUp() throws Exception {
        // Ensure user2 exists for cross-user security isolation testing
        if (!userRepository.existsByUsername("user2")) {
            userRepository.save(User.builder()
                    .username("user2")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.USER)
                    .build());
        }

        adminToken = obtainToken("admin", "admin123");
        userToken = obtainToken("user", "user123");
        user2Token = obtainToken("user2", "user123");
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
    @DisplayName("Should create reservation successfully for authenticated user (identity from SecurityContext)")
    void createReservation_Success() throws Exception {
        ReservationCreateRequest request = ReservationCreateRequest.builder()
                .resourceId(1L)
                .startTime(LocalDateTime.now().plusDays(5).withHour(10).withMinute(0))
                .endTime(LocalDateTime.now().plusDays(5).withHour(12).withMinute(0))
                .price(new BigDecimal("350.00"))
                .build();

        mockMvc.perform(post("/reservations")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.user.username", is("user")))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.price", is(350.00)));
    }

    @Test
    @DisplayName("Should reject reservation creation when startTime is after or equal to endTime")
    void createReservation_InvalidTimeRange() throws Exception {
        ReservationCreateRequest request = ReservationCreateRequest.builder()
                .resourceId(1L)
                .startTime(LocalDateTime.now().plusDays(5).withHour(12).withMinute(0))
                .endTime(LocalDateTime.now().plusDays(5).withHour(10).withMinute(0)) // Invalid: endTime before startTime
                .price(new BigDecimal("350.00"))
                .build();

        mockMvc.perform(post("/reservations")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("startTime must be before endTime")));
    }

    @Test
    @DisplayName("Should reject reservation creation when price is negative")
    void createReservation_NegativePrice() throws Exception {
        ReservationCreateRequest request = ReservationCreateRequest.builder()
                .resourceId(1L)
                .startTime(LocalDateTime.now().plusDays(5).withHour(10).withMinute(0))
                .endTime(LocalDateTime.now().plusDays(5).withHour(12).withMinute(0))
                .price(new BigDecimal("-100.00"))
                .build();

        mockMvc.perform(post("/reservations")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.price", notNullValue()));
    }

    @Test
    @DisplayName("Should reject reservation creation when resource does not exist")
    void createReservation_NonexistentResource() throws Exception {
        ReservationCreateRequest request = ReservationCreateRequest.builder()
                .resourceId(9999L)
                .startTime(LocalDateTime.now().plusDays(5).withHour(10).withMinute(0))
                .endTime(LocalDateTime.now().plusDays(5).withHour(12).withMinute(0))
                .price(new BigDecimal("350.00"))
                .build();

        mockMvc.perform(post("/reservations")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Resource not found")));
    }

    @Test
    @DisplayName("USER cannot access another user's reservation (403 Forbidden)")
    void getReservationById_OwnershipIsolation_Forbidden() throws Exception {
        // User 1 creates a reservation
        ReservationCreateRequest request = ReservationCreateRequest.builder()
                .resourceId(1L)
                .startTime(LocalDateTime.now().plusDays(10).withHour(10).withMinute(0))
                .endTime(LocalDateTime.now().plusDays(10).withHour(11).withMinute(0))
                .price(new BigDecimal("200.00"))
                .build();

        MvcResult createResult = mockMvc.perform(post("/reservations")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long reservationId = jsonNode.get("id").asLong();

        // User 2 attempts to retrieve User 1's reservation
        mockMvc.perform(get("/reservations/" + reservationId)
                        .header("Authorization", user2Token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)));
    }

    @Test
    @DisplayName("ADMIN can access any user's reservation")
    void getReservationById_Admin_Success() throws Exception {
        // User 1 creates a reservation
        ReservationCreateRequest request = ReservationCreateRequest.builder()
                .resourceId(1L)
                .startTime(LocalDateTime.now().plusDays(12).withHour(10).withMinute(0))
                .endTime(LocalDateTime.now().plusDays(12).withHour(11).withMinute(0))
                .price(new BigDecimal("400.00"))
                .build();

        MvcResult createResult = mockMvc.perform(post("/reservations")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long reservationId = jsonNode.get("id").asLong();

        // ADMIN retrieves User 1's reservation
        mockMvc.perform(get("/reservations/" + reservationId)
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) reservationId)));
    }

    @Test
    @DisplayName("Filtering reservations by status, minPrice, and maxPrice")
    void getReservations_Filtering() throws Exception {
        mockMvc.perform(get("/reservations?status=CONFIRMED&minPrice=100&maxPrice=1000")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()));
    }

    @Test
    @DisplayName("Pagination and sorting parameters test")
    void getReservations_PaginationAndSorting() throws Exception {
        mockMvc.perform(get("/reservations?page=0&size=5&sort=price,desc")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(5)))
                .andExpect(jsonPath("$.content", notNullValue()));
    }

    @Test
    @DisplayName("Invalid sort property should return 400 Bad Request")
    void getReservations_InvalidSortField() throws Exception {
        mockMvc.perform(get("/reservations?sort=invalidField,asc")
                        .header("Authorization", adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid sort field")));
    }
}
