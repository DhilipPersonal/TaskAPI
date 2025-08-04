# Java Spring Boot API Project Tasks

This file outlines all major tasks and sub-tasks required to implement the Task Management REST API as per the requirements in `task-api-requirements.md`.

---

## 1. Project Setup

### Technology Stack
- Java 21
- Spring Boot 3.2.x
- Spring Data JPA
- Spring Security 6.x
- PostgreSQL 15+
- Flyway (DB migrations)
- JWT (Authentication)
- Lombok
- MapStruct
- Swagger/OpenAPI
- Docker, Docker Compose
- JUnit 5, Mockito
- Spring Mail (for notifications)

### Tools & Libraries
- IDE: IntelliJ IDEA / VS Code
- Build: Maven 3.9.x
- Version Control: Git
- Containerization: Docker
- Documentation: Swagger/OpenAPI

### Phase 1.1: Project Initialization
- [x] Create Git repository
- [x] Initialize Spring Boot project (Maven, Java 21)
- [x] Add core dependencies (Spring Boot Starter Web, JPA, Security 6.x)
- [x] Add PostgreSQL JDBC driver
- [x] Add Flyway for DB migrations
- [x] Add Lombok, MapStruct, Swagger dependencies
- [x] Add testing libraries (JUnit 5, Mockito)
- [x] Add Dockerfile & docker-compose.yml
- [x] Configure application properties for different environments
- [x] Set up API versioning (`/api/v1/`)
- [x] Set up logging (SLF4J/Logback)
- [ ] Set up Swagger/OpenAPI docs (next)

### Phase 1.2: Database Initialization
- [x] Set up PostgreSQL instance:
    - [x] Use Docker Compose for local development with proper configuration
    - [x] Use environment variables for DB credentials in `application.properties`/`application.yml`
    - [x] Use default schema (public) instead of dedicated schema to avoid migration issues
- [x] Configure DB connection properties in Spring Boot config files for all environments
- [x] Set up Flyway migrations:
    - [x] Place migration scripts in `src/main/resources/db/migration/postgres` and `db/migration/h2`
    - [x] Use semantic versioning for scripts (e.g., `V1__init.sql`, `V2__add_lockout_fields.sql`)
    - [x] Start with schema creation and base tables
- [x] Create initial migration scripts using Flyway
- [x] Configure Flyway repair options to fix checksum mismatches
- [x] Verify DB connectivity with entity repositories

### Phase 1.3: Project Structure
- [x] Create package structure (recommended):
    - `com.example.taskapi`
        - `config`        (configuration classes)
        - `controller`    (REST endpoints)
        - `dto`           (request/response objects)
        - `entity`        (JPA entities)
        - `exception`     (custom exceptions, handlers)
        - `repository`    (Spring Data interfaces)
        - `security`      (security config, filters)
        - `service`       (business logic)
        - `util`          (utilities, helpers)
- [x] For large projects, use feature-based sub-packages (e.g., `user`, `task`, `project`):
    - `controller.user`, `entity.task`, etc.
- [x] Keep domain (entity), persistence (repository), and API (controller/dto) layers separated.
- [x] Commit package structure and a sample class per package.

### Phase 1.4: Docker Configuration
- [x] Create `Dockerfile` for the Spring Boot application
- [x] Create `docker-compose.yml` with services:
    - PostgreSQL database
    - Redis cache
    - Spring Boot application
    - pgAdmin (optional, for database management)
- [x] Configure environment variables for Docker
- [x] Create `.dockerignore` file
- [x] Add Docker build profiles for different environments
- [x] Create startup scripts for local development
- [x] Fix Docker container startup issues
- [x] Ensure proper port mapping (8080, 5433, 6379, 5050)

### Phase 1.5: Base Configuration
- [x] Configure CORS settings
- [x] Set up global exception handler
- [x] Configure Jackson for JSON serialization
- [x] Set up API versioning strategy
- [x] Configure rate limiting

## 2. Authentication & Authorization
- [x] Implement user registration 
- [x] Enforce password strength policy
- [x] Integrate JWT authentication (access/refresh tokens)
- [x] Implement login/logout endpoints
- [x] Implement account lockout after failed attempts
- [x] Integrate optional OAuth2 (Google, GitHub, Microsoft)
- [x] Optional: Two-factor authentication (2FA)
- [x] Implement RBAC (Admin, Project Manager, Team Member, Guest)
- [x] Resource-level permission checks (tasks, projects)

## 3. User Management
- [x] User entity, repository, service, controller
- [x] CRUD endpoints for user profile
- [x] Change password & reset password flows
- [x] Admin-only delete user endpoint
- [x] Avatar upload support (URL update and file upload endpoint implemented)
- [x] Track last login, account status
- [x] User role management

## 4. Task Management
- [x] Task entity, repository, service, controller
- [x] CRUD endpoints for tasks
- [x] Filtering, pagination, and sorting for task lists
- [x] Task status update endpoint
- [x] Support for sub-tasks, dependencies, recurring tasks

> All core features implemented: CRUD, filtering, sub-tasks, dependencies, and DTO-based API.

- Task templates and bulk operations
- Attachment upload (file storage integration)
- Tag/label management

## 5. Project Management
- [x] Project entity, repository, service, controller
- [x] CRUD endpoints for projects
- [x] Manage project members (add/remove)
- [x] Assign project owner
- [x] Track project status, dates, and team members

> All endpoints use DTOs, input validation, and RBAC security.


## 6. Team & Collaboration
- [x] Team entity, repository, service, controller
- [x] CRUD endpoints for teams
- [x] Manage team members (add/remove)
- [x] Comment entity and endpoints (task comments)
- [x] Notification entity and endpoints
- [x] Notification preferences

> All endpoints use DTOs, input validation, and RBAC security. Team & Collaboration module is complete.

## 7. Search & Filtering
- [x] Implement global search endpoint
- [x] Implement advanced task search (full-text, filters)
- [x] Support for filtering by status, priority, assignee, date, tags, etc.

> All endpoints support pagination, DTOs, input validation, and RBAC security. Advanced search supports status, priority, assignee, project, tags, and free-text.

## 8. Reports & Analytics
- [x] Productivity metrics endpoint
- [x] Project summary endpoint
- [x] Overdue tasks endpoint
- [x] Team workload endpoint

## 9. Technical & Security Tasks
- [x] Input validation and sanitization (DTOs, controller layer)
- [ ] Rate limiting (e.g., 100 requests/min) _(TODO: Add Bucket4j or similar)_
- [x] SQL injection and XSS protection (Spring Data JPA/DTO serialization)
- [ ] API key management for integrations _(TODO: Add if integrating external APIs)_
- [ ] Response caching for performance _(TODO: Use @Cacheable for analytics)_
- [x] Pagination for all list endpoints
- [ ] Automated and manual testing (unit, integration, API) _(TODO: Expand test coverage)_
- [x] CI/CD pipeline setup
- [x] Dockerization and deployment scripts
- [x] Fix schema and migration issues in Docker setup
- [x] Fix entity-database schema mismatches
- [x] Configure Flyway repair options for migration checksum issues

## 10. Documentation & DevOps
- [ ] API documentation (Swagger/OpenAPI) _(TODO: Add Springdoc config)_
- [x] Developer setup guide (README) _(Updated with project details and setup instructions)_
- [ ] Postman collection for API testing _(TODO: Export and include)_
- [x] Environment variable management
- [ ] Production deployment checklist _(TODO: Finalize)_
- [x] Git repository setup and organization
- [x] Commit project with proper documentation

---

> **Note:** Each task should be broken down further into user stories, acceptance criteria, and estimated effort as needed. Prioritize security, code quality, and adherence to RESTful principles throughout development.
