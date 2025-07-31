# TaskAPI - Project Management API

A Spring Boot REST API for project and task management with team collaboration features.

## Features

- Project creation and management
- Task assignment and tracking
- Team collaboration
- User notifications
- Comment system
- Authentication and authorization

## Technology Stack

- Java 21
- Spring Boot 3.2.5
- PostgreSQL 15 (Production)
- H2 Database (Testing)
- Flyway for database migrations
- Docker and Docker Compose for containerization
- Spring Security with Basic Auth
- Redis for caching
- Swagger/OpenAPI for documentation

## Getting Started

### Prerequisites

- Java 21
- Docker and Docker Compose
- Maven

### Running the Application

```bash
# Build the application
cd task-api
mvn clean package -DskipTests

# Start the Docker containers
docker compose up -d
```

The API will be available at http://localhost:8080

## API Documentation

Swagger UI is available at http://localhost:8080/swagger-ui.html

## Database Management

PgAdmin is available at http://localhost:5050 with the following credentials:
- Email: admin@admin.com
- Password: admin
