package com.example.booking.dto.reservation;

import com.example.booking.entity.ReservationStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservationUpdateRequest {

    @NotNull(message = "Resource ID is required")
    private Long resourceId;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Price must not be negative")
    private BigDecimal price;

    @NotNull(message = "Reservation status is required")
    private ReservationStatus status;

    public ReservationUpdateRequest() {
    }

    public ReservationUpdateRequest(Long resourceId, LocalDateTime startTime, LocalDateTime endTime, BigDecimal price, ReservationStatus status) {
        this.resourceId = resourceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.status = status;
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

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public static ReservationUpdateRequestBuilder builder() {
        return new ReservationUpdateRequestBuilder();
    }

    public static class ReservationUpdateRequestBuilder {
        private Long resourceId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private BigDecimal price;
        private ReservationStatus status;

        ReservationUpdateRequestBuilder() {
        }

        public ReservationUpdateRequestBuilder resourceId(Long resourceId) {
            this.resourceId = resourceId;
            return this;
        }

        public ReservationUpdateRequestBuilder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public ReservationUpdateRequestBuilder endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public ReservationUpdateRequestBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public ReservationUpdateRequestBuilder status(ReservationStatus status) {
            this.status = status;
            return this;
        }

        public ReservationUpdateRequest build() {
            return new ReservationUpdateRequest(resourceId, startTime, endTime, price, status);
        }
    }
}
