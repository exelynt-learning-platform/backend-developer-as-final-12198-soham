# Resource Booking System RESTful API

A production-ready RESTful Resource Booking System backend built with **Java 17**, **Spring Boot 3.3.2**, **Spring Security**, **JWT Authentication**, **Spring Data JPA (MySQL)**, **Jakarta Validation**, **Swagger / OpenAPI 3**, and **JUnit 5**.

---

## 1. Project Overview

The Resource Booking System enables organizations to manage bookable assets (such as conference rooms, equipment, and meeting pods) and allow users to create and manage reservations. The system enforces strict role-based access control (RBAC) and user ownership security to guarantee data privacy.

---

## 2. Features

- **Stateless Authentication**: JWT token-based authentication using Spring Security and `OncePerRequestFilter`.
- **Role-Based Authorization**:
  - `ADMIN`: Full CRUD permissions on resources and all user reservations.
  - `USER`: Read access to resources, full management of **only their own** reservations.
- **Reservation Ownership Security**:
  - Reservation requests derive user identity strictly from the authenticated Spring Security context (`SecurityContextHolder`). Client-supplied `userId` payloads are rejected/ignored.
  - Cross-user data isolation ensures User A can never view, update, or delete User B's reservations (returns `403 Forbidden`).
- **Dynamic Filtering, Pagination & Sorting**:
  - Filter reservations by `status` (`PENDING`, `CONFIRMED`, `CANCELLED`), `minPrice`, and `maxPrice` via Spring Data JPA Specifications.
  - Pageable support for offset pagination (`page`, `size`) and field sorting (`sort=price,desc`, `sort=startTime,asc`).
- **Business Rule Validation**:
  - Enforces `startTime < endTime` (returns `400 Bad Request`).
  - Enforces non-negative `BigDecimal` pricing (`@DecimalMin("0.00")`).
  - Enforces resource existence checks before booking.
- **Swagger / OpenAPI Documentation**: Interactive API documentation at `/swagger-ui.html` with built-in JWT Bearer token authorization button.
- **Automated Data Seeding**: Automatically seeds default `ADMIN` and `USER` accounts alongside sample resources on initial startup.

---

## 3. Technology Stack

- **Core Framework**: Java 17+, Spring Boot 3.3.2
- **Web Layer**: Spring MVC, Jakarta Bean Validation
- **Security**: Spring Security, JWT (io.jsonwebtoken:jjwt-api:0.12.5), BCrypt Password Hashing
- **Data Persistence**: Spring Data JPA, Hibernate, MySQL 8.0 (Production / Dev), H2 In-Memory (Test Scope)
- **API Documentation**: Springdoc OpenAPI v2.5.0 / Swagger UI
- **Testing**: JUnit 5, Mockito, MockMvc, Spring Security Test
- **Build Tool**: Apache Maven

---

## 4. Project Structure

```
d:\ReservationSystem\
├── pom.xml
├── README.md
├── .env.example
├── .gitignore
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           └── booking
    │   │               ├── ReservationSystemApplication.java
    │   │               ├── config
    │   │               │   ├── DataInitializer.java
    │   │               │   └── OpenApiConfig.java
    │   │               ├── controller
    │   │               │   ├── AuthController.java
    │   │               │   ├── ReservationController.java
    │   │               │   └── ResourceController.java
    │   │               ├── dto
    │   │               │   ├── auth
    │   │               │   │   ├── LoginRequest.java
    │   │               │   │   ├── LoginResponse.java
    │   │               │   │   └── UserSummaryDto.java
    │   │               │   ├── reservation
    │   │               │   │   ├── PagedResponse.java
    │   │               │   │   ├── ReservationCreateRequest.java
    │   │               │   │   ├── ReservationResponse.java
    │   │               │   │   └── ReservationUpdateRequest.java
    │   │               │   └── resource
    │   │               │       ├── ResourceRequest.java
    │   │               │       └── ResourceResponse.java
    │   │               ├── entity
    │   │               │   ├── Reservation.java
    │   │               │   ├── ReservationStatus.java
    │   │               │   ├── Resource.java
    │   │               │   ├── Role.java
    │   │               │   └── User.java
    │   │               ├── exception
    │   │               │   ├── BadRequestException.java
    │   │               │   ├── ErrorResponse.java
    │   │               │   ├── ForbiddenException.java
    │   │               │   ├── GlobalExceptionHandler.java
    │   │               │   ├── ReservationNotFoundException.java
    │   │               │   ├── ResourceNotFoundException.java
    │   │               │   └── UnauthorizedException.java
    │   │               ├── repository
    │   │               │   ├── ReservationRepository.java
    │   │               │   ├── ReservationSpecification.java
    │   │               │   ├── ResourceRepository.java
    │   │               │   └── UserRepository.java
    │   │               ├── security
    │   │               │   ├── CustomAccessDeniedHandler.java
    │   │               │   ├── CustomUserDetails.java
    │   │               │   ├── CustomUserDetailsService.java
    │   │               │   ├── JwtAuthenticationEntryPoint.java
    │   │               │   ├── JwtAuthenticationFilter.java
    │   │               │   ├── JwtService.java
    │   │               │   └── SecurityConfig.java
    │   │               └── service
    │   │                   ├── AuthService.java
    │   │                   ├── ReservationService.java
    │   │                   └── ResourceService.java
    │   └── resources
    │       └── application.properties
    └── test
        ├── java
        │   └── com
        │       └── example
        │           └── booking
        │               ├── controller
        │               │   ├── AuthControllerTest.java
        │               │   ├── ReservationControllerTest.java
        │               │   └── ResourceControllerTest.java
        └── resources
            └── application-test.properties
```

---

## 5. Prerequisites

- **Java**: JDK 17 or higher
- **Maven**: 3.8+
- **Database**: MySQL 8.0+

---

## 6. MySQL Database Setup

1. Start your local MySQL server on port `3306`.
2. Create the database (or allow Spring Boot auto-creation):
   ```sql
   CREATE DATABASE IF NOT EXISTS reservation_db;
   ```

---

## 7. Environment Variables

You can configure application secrets via environment variables or default properties:

| Variable | Description | Default Value |
| :--- | :--- | :--- |
| `DB_URL` | MySQL JDBC URL | `jdbc:mysql://localhost:3306/reservation_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` |
| `DB_USERNAME` | MySQL Username | `root` |
| `DB_PASSWORD` | MySQL Password | `root` |
| `JWT_SECRET` | HMAC-SHA256 Secret Key | `404E635266556A586E3272357538782F413F4428472B4B6250655368566D5970` |
| `JWT_EXPIRATION` | Token expiration in ms | `86400000` (24 Hours) |

---

## 8. How to Run

### Clean and Build
```bash
mvn clean install
```

### Run Application
```bash
mvn spring-boot:run
```

The server will start on `http://localhost:8080`.

---

## 9. Seed Credentials

On startup, the system seeds the following default accounts (passwords encoded with BCrypt):

| Username | Password | Role | Permissions |
| :--- | :--- | :--- | :--- |
| `admin` | `admin123` | `ADMIN` | Full Resource & Reservation CRUD |
| `user` | `user123` | `USER` | Read Resources, Manage Own Reservations |

---

## 10. Authentication Flow

1. Send `POST /auth/login` with username & password.
2. Server verifies credentials via `AuthenticationManager` + `BCryptPasswordEncoder`.
3. Server generates JWT containing claims (`sub`, `role`, `iat`, `exp`).
4. Include token in subsequent requests:
   ```http
   Authorization: Bearer <JWT_TOKEN>
   ```

---

## 11. API Endpoint Matrix

| Method | Endpoint | Allowed Roles | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/login` | Public | Authenticate user & get JWT |
| `GET` | `/resources` | ADMIN, USER | List all resources |
| `GET` | `/resources/{id}` | ADMIN, USER | Get resource by ID |
| `POST` | `/resources` | ADMIN | Create new resource |
| `PUT` | `/resources/{id}` | ADMIN | Update resource |
| `DELETE` | `/resources/{id}` | ADMIN | Delete resource |
| `GET` | `/reservations` | ADMIN, USER | List reservations (ADMIN: all, USER: own) |
| `GET` | `/reservations/{id}` | ADMIN, USER | Get reservation by ID (Enforces ownership) |
| `POST` | `/reservations` | ADMIN, USER | Create reservation (Bound to token user) |
| `PUT` | `/reservations/{id}` | ADMIN, USER | Update reservation (Enforces ownership) |
| `DELETE` | `/reservations/{id}` | ADMIN, USER | Delete reservation (Enforces ownership) |

---

## 12. Swagger / OpenAPI Documentation

- **Swagger UI URL**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

To test protected endpoints in Swagger UI:
1. Click **Authorize** button at top right.
2. Enter your token in format: `Bearer <YOUR_JWT_TOKEN>`
3. Click **Authorize**.

---

## 13. Example API Requests

### 1. User Login
`POST /auth/login`
```json
{
  "username": "user",
  "password": "user123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer"
}
```

### 2. Create Reservation (User)
`POST /reservations`
`Authorization: Bearer <USER_JWT>`
```json
{
  "resourceId": 1,
  "startTime": "2026-09-01T10:00:00",
  "endTime": "2026-09-01T12:00:00",
  "price": 500.00
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "user": {
    "id": 2,
    "username": "user",
    "role": "USER"
  },
  "resource": {
    "id": 1,
    "name": "Conference Room A",
    "description": "Large conference room",
    "location": "Building 1",
    "available": true
  },
  "startTime": "2026-09-01T10:00:00",
  "endTime": "2026-09-01T12:00:00",
  "price": 500.00,
  "status": "PENDING"
}
```

### 3. Reservation Filtering, Pagination & Sorting
`GET /reservations?status=CONFIRMED&minPrice=100&maxPrice=1000&page=0&size=10&sort=price,desc`
`Authorization: Bearer <ADMIN_JWT>`

---

## 14. Testing

To run all automated integration and security tests against the embedded test H2 database:

```bash
mvn clean test
```

### Verified Test Coverage:
- **Authentication**: Successful login, invalid password, unknown user.
- **Role Permissions**: ADMIN resource CRUD vs USER 403 Forbidden.
- **Ownership Isolation**: USER creating reservation, retrieving own, 403 when USER tries accessing another user's reservation.
- **Validation**: Rejection of negative price (`400`), rejection of `startTime >= endTime` (`400`), nonexistent resource (`404`).
- **Filtering & Pagination**: Status, price range combinations, page/size metadata, safe property sorting.

---

## 15. Security Notes

- **Password Safety**: All passwords stored using BCrypt hash. Passwords are never returned in DTOs.
- **Stateless Security**: `SessionCreationPolicy.STATELESS` enabled; no HTTP session created.
- **Zero-Trust Client Identity**: User identity for reservation creation and ownership checks comes exclusively from the authenticated JWT token stored in `SecurityContextHolder`.
