package com.example.booking.config;

import com.example.booking.entity.*;
import com.example.booking.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final ResourceRepository resourceRepo;
    private final ReservationRepository reservationRepo;
    private final PasswordEncoder encoder;

    public DataInitializer(UserRepository userRepo,
                           ResourceRepository resourceRepo,
                           ReservationRepository reservationRepo,
                           PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.resourceRepo = resourceRepo;
        this.reservationRepo = reservationRepo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {

        User user = userRepo.findByUsername("user").orElseGet(() ->
                userRepo.save(User.builder()
                        .username("user")
                        .password(encoder.encode("user123"))
                        .role(Role.USER)
                        .build()));

        userRepo.findByUsername("admin").orElseGet(() ->
                userRepo.save(User.builder()
                        .username("admin")
                        .password(encoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .build()));

        if (resourceRepo.count() == 0) {
            Resource r1 = resourceRepo.save(resource(
                    "Conference Room A",
                    "Large conference room with 4K projector and video conferencing setup",
                    "Building 1, Floor 3"));

            Resource r2 = resourceRepo.save(resource(
                    "Meeting Pod 2B",
                    "Quiet 4-person meeting pod with whiteboard",
                    "Building 2, Floor 1"));

            resourceRepo.save(resource(
                    "Executive Boardroom",
                    "High-end boardroom with executive seating for 20",
                    "Building 1, Floor 5"));

            if (reservationRepo.count() == 0) {
                reservationRepo.save(reservation(user, r1, 1, 10, 12,
                        "500.00", ReservationStatus.CONFIRMED));

                reservationRepo.save(reservation(user, r2, 2, 14, 15,
                        "250.50", ReservationStatus.PENDING));
            }
        }
    }

    private Resource resource(String name, String description, String location) {
        return Resource.builder()
                .name(name)
                .description(description)
                .location(location)
                .available(true)
                .build();
    }

    private Reservation reservation(User user, Resource resource,
                                    int days, int startHour, int endHour,
                                    String price, ReservationStatus status) {

        LocalDateTime date = LocalDateTime.now().plusDays(days);

        return Reservation.builder()
                .user(user)
                .resource(resource)
                .startTime(date.withHour(startHour).withMinute(0).withSecond(0).withNano(0))
                .endTime(date.withHour(endHour).withMinute(0).withSecond(0).withNano(0))
                .price(new BigDecimal(price))
                .status(status)
                .build();
    }
}