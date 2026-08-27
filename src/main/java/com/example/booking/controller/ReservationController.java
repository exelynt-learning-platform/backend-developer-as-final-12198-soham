package com.example.booking.controller;

import com.example.booking.dto.reservation.*;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/reservations")
@Tag(name = "Reservations", description = "Endpoints for managing resource reservations")
@SecurityRequirement(name = "BearerAuth")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    @Operation(summary = "Get reservations with optional filtering, pagination, and sorting (ADMIN sees all, USER sees own)")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PagedResponse<ReservationResponse>> getReservations(
            @Parameter(description = "Filter by reservation status (PENDING, CONFIRMED, CANCELLED)")
            @RequestParam(required = false) ReservationStatus status,

            @Parameter(description = "Filter by minimum price")
            @RequestParam(required = false) BigDecimal minPrice,

            @Parameter(description = "Filter by maximum price")
            @RequestParam(required = false) BigDecimal maxPrice,

            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        PagedResponse<ReservationResponse> response = reservationService.getReservations(
                status, minPrice, maxPrice, pageable
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reservation by ID (Enforces user ownership for USER role)")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new reservation (Authenticated user bound automatically from token)")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationCreateRequest request) {
        ReservationResponse response = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing reservation (Enforces user ownership for USER role)")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ReservationResponse> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationUpdateRequest request
    ) {
        return ResponseEntity.ok(reservationService.updateReservation(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete/Cancel a reservation by ID (Enforces user ownership for USER role)")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
