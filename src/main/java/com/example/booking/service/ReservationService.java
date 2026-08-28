package com.example.booking.service;

import com.example.booking.dto.auth.UserSummaryDto;
import com.example.booking.dto.reservation.*;
import com.example.booking.dto.resource.ResourceResponse;
import com.example.booking.entity.*;
import com.example.booking.exception.*;
import com.example.booking.repository.ReservationRepository;
import com.example.booking.repository.ReservationSpecification;
import com.example.booking.repository.ResourceRepository;
import com.example.booking.repository.UserRepository;
import com.example.booking.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "startTime", "endTime", "price", "status");

    public ReservationService(
            ReservationRepository reservationRepository,
            ResourceRepository resourceRepository,
            UserRepository userRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new UnauthorizedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUser();
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));
    }

    @Transactional(readOnly = true)
    public PagedResponse<ReservationResponse> getReservations(
            ReservationStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    ) {
        User currentUser = getAuthenticatedUser();
        Long userIdFilter = (currentUser.getRole() == Role.ADMIN) ? null : currentUser.getId();


        Pageable validatedPageable = validatePageable(pageable);

        Specification<Reservation> spec = ReservationSpecification.filterReservations(
                userIdFilter, status, minPrice, maxPrice
        );

        Page<Reservation> reservationPage = reservationRepository.findAll(spec, validatedPageable);

        List<ReservationResponse> content = reservationPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PagedResponse.<ReservationResponse>builder()
                .content(content)
                .page(reservationPage.getNumber())
                .size(reservationPage.getSize())
                .totalElements(reservationPage.getTotalElements())
                .totalPages(reservationPage.getTotalPages())
                .last(reservationPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id) {
        User currentUser = getAuthenticatedUser();
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with ID: " + id));


        if (currentUser.getRole() != Role.ADMIN && !reservation.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Access Denied: You do not have permission to view this reservation");
        }

        return mapToResponse(reservation);
    }

    public ReservationResponse createReservation(ReservationCreateRequest request) {
        User currentUser = getAuthenticatedUser();


        if (request.getStartTime().isAfter(request.getEndTime()) || request.getStartTime().isEqual(request.getEndTime())) {
            throw new BadRequestException("startTime must be before endTime");
        }


        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + request.getResourceId()));

        Reservation reservation = Reservation.builder()
                .user(currentUser)
                .resource(resource)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .price(request.getPrice())
                .status(ReservationStatus.PENDING)
                .build();

        Reservation saved = reservationRepository.save(reservation);
        return mapToResponse(saved);
    }

    public ReservationResponse updateReservation(Long id, ReservationUpdateRequest request) {
        User currentUser = getAuthenticatedUser();
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with ID: " + id));


        if (currentUser.getRole() != Role.ADMIN && !reservation.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Access Denied: You do not have permission to update this reservation");
        }


        if (request.getStartTime().isAfter(request.getEndTime()) || request.getStartTime().isEqual(request.getEndTime())) {
            throw new BadRequestException("startTime must be before endTime");
        }


        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + request.getResourceId()));

        reservation.setResource(resource);
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setPrice(request.getPrice());
        reservation.setStatus(request.getStatus());

        Reservation updated = reservationRepository.save(reservation);
        return mapToResponse(updated);
    }

    public void deleteReservation(Long id) {
        User currentUser = getAuthenticatedUser();
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with ID: " + id));

        if (currentUser.getRole() != Role.ADMIN && !reservation.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Access Denied: You do not have permission to delete this reservation");
        }

        reservationRepository.delete(reservation);
    }

    private Pageable validatePageable(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return pageable;
        }

        for (Sort.Order order : pageable.getSort()) {
            if (!ALLOWED_SORT_FIELDS.contains(order.getProperty())) {
                throw new BadRequestException("Invalid sort field: '" + order.getProperty() + "'. Allowed fields: " + ALLOWED_SORT_FIELDS);
            }
        }
        return pageable;
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
        UserSummaryDto userSummary = UserSummaryDto.builder()
                .id(reservation.getUser().getId())
                .username(reservation.getUser().getUsername())
                .role(reservation.getUser().getRole())
                .build();

        ResourceResponse resourceResponse = ResourceResponse.builder()
                .id(reservation.getResource().getId())
                .name(reservation.getResource().getName())
                .description(reservation.getResource().getDescription())
                .location(reservation.getResource().getLocation())
                .available(reservation.getResource().getAvailable() != null ? reservation.getResource().getAvailable() : reservation.getResource().isAvailable())
                .build();

        return ReservationResponse.builder()
                .id(reservation.getId())
                .user(userSummary)
                .resource(resourceResponse)
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .price(reservation.getPrice())
                .status(reservation.getStatus())
                .build();
    }
}
