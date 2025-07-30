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
- [ ] Set up PostgreSQL instance:
    - Use Docker Compose for local development. Example service:
      ```yaml
      services:
        db:
          image: postgres:15
          environment:
            POSTGRES_DB: taskdb
            POSTGRES_USER: taskuser
            POSTGRES_PASSWORD: taskpass
          ports:
            - "5432:5432"
          volumes:
            - db-data:/var/lib/postgresql/data
      volumes:
        db-data:
      ```
    - Use environment variables for DB credentials in `application.properties`/`application.yml`.
    - Recommended: Use a dedicated schema (e.g., `taskapp`).
- [ ] Configure DB connection properties in Spring Boot config files for all environments.
- [x] Set up Flyway migrations:
    - [x] Place migration scripts in `src/main/resources/db/migration`.
    - [x] Use semantic versioning for scripts (e.g., `V1__init.sql`, `V2__add_user_table.sql`).
    - [x] Start with schema creation and base tables.
- [x] Create initial migration scripts using Flyway.
- [ ] Verify DB connectivity with a test entity/repository.

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

### Phase 1.5: Base Configuration
- [ ] Configure CORS settings
- [ ] Set up global exception handler
- [ ] Configure Jackson for JSON serialization
- [ ] Set up API versioning strategy
- [ ] Configure rate limiting

## 2. Authentication & Authorization
- Implement user registration (with email verification)
- Enforce password strength policy
- Integrate JWT authentication (access/refresh tokens)
- Implement login/logout endpoints
- Implement account lockout after failed attempts
- Integrate optional OAuth2 (Google, GitHub, Microsoft)
- Optional: Two-factor authentication (2FA)
- Implement RBAC (Admin, Project Manager, Team Member, Guest)
- Resource-level permission checks (tasks, projects)

## 3. User Management
- User entity, repository, service, controller
- CRUD endpoints for user profile
- Change password & reset password flows
- Admin-only delete user endpoint
- Avatar upload support
- Track last login, account status
- User role management

## 4. Task Management
- Task entity, repository, service, controller
- CRUD endpoints for tasks
- Filtering, pagination, and sorting for task lists
- Task status update endpoint
- Support for sub-tasks, dependencies, recurring tasks
- Task templates and bulk operations
- Attachment upload (file storage integration)
- Tag/label management

## 5. Project Management
- Project entity, repository, service, controller
- CRUD endpoints for projects
- Manage project members (add/remove)
- Assign project owner
- Track project status, dates, and team members

## 6. Team & Collaboration
- Team entity, repository, service, controller
- CRUD endpoints for teams
- Manage team members (add/remove)
- Comment entity and endpoints (task comments)
- Notification entity and endpoints
- Notification preferences

## 7. Search & Filtering
- Implement global search endpoint
- Implement advanced task search (full-text, filters)
- Support for filtering by status, priority, assignee, date, tags, etc.

## 8. Reports & Analytics
- Productivity metrics endpoint
- Project summary endpoint
- Overdue tasks endpoint
- Team workload endpoint

## 9. Technical & Security Tasks
- Input validation and sanitization
- Rate limiting (e.g., 100 requests/min)
- SQL injection and XSS protection
- API key management for integrations
- Response caching for performance
- Pagination for all list endpoints
- Automated and manual testing (unit, integration, API)
- CI/CD pipeline setup
- Dockerization and deployment scripts

## 10. Documentation & DevOps
- API documentation (Swagger/OpenAPI)
- Developer setup guide (README)
- Postman collection for API testing
- Environment variable management
- Production deployment checklist

---

> **Note:** Each task should be broken down further into user stories, acceptance criteria, and estimated effort as needed. Prioritize security, code quality, and adherence to RESTful principles throughout development.
