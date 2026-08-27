package com.example.booking.dto.reservation;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservationCreateRequest {

    @NotNull(message = "Resource ID is required")
    private Long resourceId;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Price must not be negative")
    private BigDecimal price;

    public ReservationCreateRequest() {
    }

    public ReservationCreateRequest(Long resourceId, LocalDateTime startTime, LocalDateTime endTime, BigDecimal price) {
        this.resourceId = resourceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
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

    public static ReservationCreateRequestBuilder builder() {
        return new ReservationCreateRequestBuilder();
    }

    public static class ReservationCreateRequestBuilder {
        private Long resourceId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private BigDecimal price;

        ReservationCreateRequestBuilder() {
        }

        public ReservationCreateRequestBuilder resourceId(Long resourceId) {
            this.resourceId = resourceId;
            return this;
        }

        public ReservationCreateRequestBuilder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public ReservationCreateRequestBuilder endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public ReservationCreateRequestBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public ReservationCreateRequest build() {
            return new ReservationCreateRequest(resourceId, startTime, endTime, price);
        }
    }
}
