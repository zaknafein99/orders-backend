# Orders Backend

A Kotlin Spring Boot backend for the Orders Management System.

## Setup

1. Build the project:
```bash
./gradlew build
```

2. Run the application:
```bash
./gradlew bootRun
```

## Docker Setup

1. Build the Docker image:
```bash
docker build -t orders-backend .
```

2. Run with Docker Compose:
```bash
docker-compose up
```

## API Documentation

The API documentation is available at:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Development

This project uses:
- Kotlin
- Spring Boot
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL
- Gradle for build management 