# JWT Authentication Testing Guide

This document provides detailed instructions for testing the JWT authentication system implemented in the Task API project.

## Prerequisites

1. Java 21 installed and configured
2. PostgreSQL database running (or configured in Docker)
3. Maven for building the project
4. Postman for API testing (collection and environment provided)

## Setup

1. Ensure your database is running and properly configured in `application.yml`
2. Build and run the application:

```bash
# Navigate to the project directory
cd /Users/dhilipelango/APITask/task-api

# Build the project
./mvnw clean package -DskipTests

# Run the application
java -jar target/task-api-0.0.1-SNAPSHOT.jar
```

Alternatively, you can run it directly with Maven:

```bash
./mvnw spring-boot:run
```

## Test Users

The application automatically creates two test users in development mode:

1. **Admin User**
   - Username: `admin`
   - Password: `admin123`
   - Role: `ADMIN`

2. **Regular User**
   - Username: `user`
   - Password: `user123`
   - Role: `USER`

## Testing with Postman

1. Import the provided Postman collection and environment:
   - Collection: `src/main/resources/postman/Task-API-JWT-Auth.postman_collection.json`
   - Environment: `src/main/resources/postman/Task-API-Local.postman_environment.json`

2. Select the `Task API - Local` environment in Postman

3. Follow these steps to test the authentication flow:

### Authentication Flow Testing

1. **Login as Admin**
   - Use the "Login (Admin)" request
   - This will automatically store the access and refresh tokens in your environment variables

2. **Access Protected Endpoints**
   - Try the "Protected Endpoint" request
   - Try the "Admin Endpoint" request (should work with admin token)
   - Try the "User Endpoint" request (should fail with admin token)

3. **Login as Regular User**
   - Use the "Login (User)" request
   - This will update your tokens in the environment

4. **Test Role-Based Access**
   - Try the "Protected Endpoint" request (should work)
   - Try the "Admin Endpoint" request (should fail with user token)
   - Try the "User Endpoint" request (should work with user token)

5. **Test Token Refresh**
   - Use the "Refresh Token" request to get a new access token
   - Verify that the new token works with appropriate endpoints

6. **Register a New User**
   - Use the "Register New User" request to create a new account
   - Login with the new credentials to verify

### Admin Operations Testing

1. **Get All Users**
   - Login as admin
   - Use the "Get All Users" request to see all users in the system

2. **Get User by ID**
   - From the previous response, copy a user ID
   - Set it as the `user_id` environment variable
   - Use the "Get User by ID" request

3. **Delete User**
   - Use the "Delete User" request to remove a user
   - Verify deletion by trying to get all users again

## Testing with cURL

If you prefer using cURL for testing, here are some example commands:

### Login

```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Access Protected Endpoint

```bash
curl -X GET http://localhost:8081/api/v1/test/protected \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

### Refresh Token

```bash
curl -X POST http://localhost:8081/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"YOUR_REFRESH_TOKEN_HERE"}'
```

## Expected Behavior

1. **Public Endpoints**
   - Should be accessible without authentication
   - Examples: `/api/v1/auth/login`, `/api/v1/users/register`, `/api/v1/test/public`

2. **Protected Endpoints**
   - Should require a valid JWT token
   - Should return 401 Unauthorized if no token or invalid token is provided

3. **Role-Based Endpoints**
   - Admin endpoints should only be accessible with ADMIN role
   - User endpoints should only be accessible with USER role
   - Should return 403 Forbidden if authenticated but lacking required role

4. **Account Lockout**
   - After 5 failed login attempts, the account should be locked for 15 minutes
   - Login attempts during lockout should return an appropriate error message

## Troubleshooting

1. **Invalid Token Errors**
   - Check that the JWT secret in `application.yml` matches what's used in `JwtUtil.java`
   - Verify token expiration time (default is 24 hours)

2. **Role-Based Access Issues**
   - Ensure roles in the database have the correct format
   - Check that `CustomUserDetailsService` is correctly mapping roles to authorities

3. **Database Connection Issues**
   - Verify PostgreSQL is running and accessible
   - Check connection settings in `application.yml`

4. **Build Issues**
   - Ensure Java 21 is properly configured
   - Check for any Maven dependency conflicts

## Security Considerations

1. The JWT secret should be environment-specific and not committed to version control
2. In production, consider shorter token expiration times
3. Implement token blacklisting for logout functionality
4. Use HTTPS in production environments
5. Consider implementing rate limiting for login attempts

## Implementation Details

The JWT authentication system consists of these key components:

1. **JwtUtil**: Handles token generation, validation, and claim extraction
2. **JwtAuthenticationFilter**: Intercepts requests to validate JWT tokens
3. **CustomUserDetailsService**: Loads user details from the database
4. **SecurityConfig**: Configures security settings and authentication providers
5. **AuthController**: Provides endpoints for login and token refresh
6. **DTOs**: `AuthRequest`, `AuthResponse`, and `RefreshTokenRequest`

For more details, see the code documentation in each class.
