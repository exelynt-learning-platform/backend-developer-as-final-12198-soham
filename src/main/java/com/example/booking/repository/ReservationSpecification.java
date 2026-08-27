package com.example.booking.repository;

import com.example.booking.entity.Reservation;
import com.example.booking.entity.ReservationStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ReservationSpecification {

    public static Specification<Reservation> filterReservations(
            Long userId,
            ReservationStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {
        return (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();

            if (userId != null) {
                predicates.getExpressions().add(
                        criteriaBuilder.equal(root.get("user").get("id"), userId)
                );
            }

            if (status != null) {
                predicates.getExpressions().add(
                        criteriaBuilder.equal(root.get("status"), status)
                );
            }

            if (minPrice != null) {
                predicates.getExpressions().add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice)
                );
            }

            if (maxPrice != null) {
                predicates.getExpressions().add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice)
                );
            }

            return predicates;
        };
    }
}
