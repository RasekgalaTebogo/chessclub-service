
# ♟️ Chess Club Administration App

This is a Spring Boot application designed to help a local chess club manage its members and rankings.

## 📋 Project Overview

The chess club needs a basic CRUD system to manage members and automatically adjust their rankings based on match results.

### Member Details

Each member has:
- Name and surname
- Email address
- Birthday
- Number of club games played
- Current rank (1 = best, N = lowest)

### Ranking Rules

- ✅ **Higher-ranked player wins**: No change
- ⚖️ **Draw**:
    - If adjacent ranks (e.g., 10 vs 11): No change
    - If not adjacent (e.g., 10 vs 15): Lower-ranked player moves up one rank
- 🔽 **Lower-ranked player wins**:
    - Higher-ranked player moves one rank down
    - Lower-ranked player moves up by half the difference (rounded down)

---

## 🧪 Technologies Used

- Java 21
- Spring Boot 3+
- Spring Data JPA
- H2 In-Memory Database
- Bean Validation (Jakarta)
- SpringDoc OpenAPI (Swagger)
- Lombok
- MapStruct
- Docker

---

## 🚀 Running the App

### Prerequisites
- Java 21+
- Maven
- Docker (Optional)

### Option 1: Run with Maven

```bash
 ./mvnw clean install
```
```bash
   ./mvnw spring-boot:run
```

### Option 2: Run with docker

```bash
   docker build -t chessclub-service .
```
```bash
   docker run -p 8080:8080 chessclub-service
```

## API Documentation

http://localhost:8080/swagger-ui.html
