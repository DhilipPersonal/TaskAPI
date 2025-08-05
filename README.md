# TaskAPI - Complete Task Management System

A comprehensive, production-ready task management REST API built with Spring Boot 3.2.5, featuring JWT authentication, rate limiting, caching, file uploads, and comprehensive API documentation.

## 🚀 Features Implemented

### ✅ Core Features
- **JWT Authentication & Authorization** with token blacklisting
- **Rate Limiting** with Bucket4j (different limits for auth, API, and admin endpoints)
- **Response Caching** with Redis and fallback in-memory cache
- **File Upload & Attachment System** with validation and security
- **Comprehensive Error Handling** with structured error responses
- **API Documentation** with Swagger/OpenAPI 3.0 and interactive UI
- **Database Migrations** with Flyway (PostgreSQL & H2 support)
- **Docker Support** with multi-container setup

### 🔐 Security Features
- JWT token blacklisting on logout
- Rate limiting by IP address
- Role-based access control (USER, ADMIN)
- Secure file upload validation
- CORS configuration
- Security headers

### 📊 Performance Features
- Redis caching for analytics endpoints
- Connection pooling with HikariCP
- Optimized database queries
- Efficient rate limiting with token buckets

## 🛠 Tech Stack

- **Framework**: Spring Boot 3.2.5
- **Java Version**: 21
- **Database**: PostgreSQL (production), H2 (testing)
- **Cache**: Redis
- **Authentication**: JWT with io.jsonwebtoken
- **Rate Limiting**: Bucket4j
- **Documentation**: Springdoc OpenAPI
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose

## 🚀 Quick Start

### Prerequisites
- Java 21 (Amazon Corretto recommended)
- Maven 3.6+
- Docker & Docker Compose (optional)
- PostgreSQL (for production)
- Redis (for caching)

### 1. Clone and Setup
```bash
git clone <repository-url>
cd APITask
```

### 2. Run with Docker (Recommended)
```bash
docker-compose up --build
```
This starts:
- PostgreSQL database on port 5432
- Redis cache on port 6379
- TaskAPI application on port 8080

### 3. Run Locally (Development)
```bash
# Set Java 21 (if multiple versions installed)
export JAVA_HOME=/Users/dhilipelango/Library/Java/JavaVirtualMachines/corretto-21.0.4/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

# Run with test profile (uses H2 database)
cd task-api
mvn spring-boot:run -Dspring-boot.run.profiles=test -Dspring-boot.run.arguments=--server.port=8081
```

The API will be available at http://localhost:8080

## API Documentation

Swagger UI is available at http://localhost:8080/swagger-ui.html

## Database Management

PgAdmin is available at http://localhost:5050 with the following credentials:
- Email: admin@admin.com
- Password: admin
