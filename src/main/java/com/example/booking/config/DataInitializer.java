package com.example.booking.config;

import com.example.booking.entity.*;
import com.example.booking.repository.ReservationRepository;
import com.example.booking.repository.ResourceRepository;
import com.example.booking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(DataInitializer.class.getName());

    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            ResourceRepository resourceRepository,
            ReservationRepository reservationRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
        this.reservationRepository = reservationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing seed data...");

        // Seed Admin user
        User admin = userRepository.findByUsername("admin").orElseGet(() -> {
            User newAdmin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            return userRepository.save(newAdmin);
        });

        // Seed Regular user
        User user = userRepository.findByUsername("user").orElseGet(() -> {
            User newUser = User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.USER)
                    .build();
            return userRepository.save(newUser);
        });

        // Seed Sample Resources if empty
        if (resourceRepository.count() == 0) {
            Resource r1 = resourceRepository.save(Resource.builder()
                    .name("Conference Room A")
                    .description("Large conference room with 4K projector and video conferencing setup")
                    .location("Building 1, Floor 3")
                    .available(true)
                    .build());

            Resource r2 = resourceRepository.save(Resource.builder()
                    .name("Meeting Pod 2B")
                    .description("Quiet 4-person meeting pod with whiteboard")
                    .location("Building 2, Floor 1")
                    .available(true)
                    .build());

            Resource r3 = resourceRepository.save(Resource.builder()
                    .name("Executive Boardroom")
                    .description("High-end boardroom with executive seating for 20")
                    .location("Building 1, Floor 5")
                    .available(true)
                    .build());

            logger.info("Sample resources seeded.");

            // Seed Sample Reservations if empty
            if (reservationRepository.count() == 0) {
                reservationRepository.save(Reservation.builder()
                        .user(user)
                        .resource(r1)
                        .startTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0))
                        .endTime(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0))
                        .price(new BigDecimal("500.00"))
                        .status(ReservationStatus.CONFIRMED)
                        .build());

                reservationRepository.save(Reservation.builder()
                        .user(user)
                        .resource(r2)
                        .startTime(LocalDateTime.now().plusDays(2).withHour(14).withMinute(0).withSecond(0).withNano(0))
                        .endTime(LocalDateTime.now().plusDays(2).withHour(15).withMinute(0).withSecond(0).withNano(0))
                        .price(new BigDecimal("250.50"))
                        .status(ReservationStatus.PENDING)
                        .build());

                logger.info("Sample reservations seeded.");
            }
        }

        logger.info("Data initialization completed.");
    }
}
