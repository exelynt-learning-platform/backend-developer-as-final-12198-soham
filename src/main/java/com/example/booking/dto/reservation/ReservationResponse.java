package com.example.booking.dto.reservation;

import com.example.booking.dto.auth.UserSummaryDto;
import com.example.booking.dto.resource.ResourceResponse;
import com.example.booking.entity.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservationResponse {

    private Long id;
    private UserSummaryDto user;
    private ResourceResponse resource;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal price;
    private ReservationStatus status;

    public ReservationResponse() {
    }

    public ReservationResponse(Long id, UserSummaryDto user, ResourceResponse resource, LocalDateTime startTime, LocalDateTime endTime, BigDecimal price, ReservationStatus status) {
        this.id = id;
        this.user = user;
        this.resource = resource;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserSummaryDto getUser() {
        return user;
    }

    public void setUser(UserSummaryDto user) {
        this.user = user;
    }

    public ResourceResponse getResource() {
        return resource;
    }

    public void setResource(ResourceResponse resource) {
        this.resource = resource;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public static ReservationResponseBuilder builder() {
        return new ReservationResponseBuilder();
    }

    public static class ReservationResponseBuilder {
        private Long id;
        private UserSummaryDto user;
        private ResourceResponse resource;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private BigDecimal price;
        private ReservationStatus status;

        ReservationResponseBuilder() {
        }

        public ReservationResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ReservationResponseBuilder user(UserSummaryDto user) {
            this.user = user;
            return this;
        }

        public ReservationResponseBuilder resource(ResourceResponse resource) {
            this.resource = resource;
            return this;
        }

        public ReservationResponseBuilder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public ReservationResponseBuilder endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public ReservationResponseBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public ReservationResponseBuilder status(ReservationStatus status) {
            this.status = status;
            return this;
        }

        public ReservationResponse build() {
            return new ReservationResponse(id, user, resource, startTime, endTime, price, status);
        }
    }
}
