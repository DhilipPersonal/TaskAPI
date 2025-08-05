# JWT Authentication Guide

This document provides instructions on how to test and use the JWT authentication system implemented in the Task API.

## Authentication Flow

1. **Login**: Send credentials to `/api/v1/auth/login` to receive JWT tokens
2. **Use Access Token**: Include the access token in the Authorization header for protected requests
3. **Refresh Token**: When the access token expires, use the refresh token to get a new one via `/api/v1/auth/refresh`

## Testing Endpoints

### Authentication Endpoints

- **Login**: `POST /api/v1/auth/login`
  ```json
  {
    "username": "admin",
    "password": "admin123"
  }
  ```
  Response:
  ```json
  {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "admin",
    "role": "ADMIN"
  }
  ```

- **Refresh Token**: `POST /api/v1/auth/refresh`
  ```json
  {
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
  ```

### Test Endpoints

- **Public**: `GET /api/v1/test/public`
  - No authentication required
  - Returns a public message

- **Protected**: `GET /api/v1/test/protected`
  - Requires valid JWT token
  - Returns user information

- **Admin Only**: `GET /api/v1/test/admin`
  - Requires valid JWT token with ADMIN role
  - Returns admin message

- **User Only**: `GET /api/v1/test/user`
  - Requires valid JWT token with USER role
  - Returns user message

### Admin Endpoints

- **Get All Users**: `GET /api/v1/admin/users`
  - Requires ADMIN role
  - Returns list of all users

- **Get User by ID**: `GET /api/v1/admin/users/{userId}`
  - Requires ADMIN role
  - Returns user details

- **Delete User**: `DELETE /api/v1/admin/users/{userId}`
  - Requires ADMIN role
  - Deletes user

## Testing with cURL

### Login and get tokens:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Access protected endpoint:

```bash
curl -X GET http://localhost:8080/api/v1/test/protected \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

### Access admin endpoint:

```bash
curl -X GET http://localhost:8080/api/v1/admin/users \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

### Refresh token:

```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"YOUR_REFRESH_TOKEN_HERE"}'
```

## Security Notes

1. The JWT secret key is configured in `application.yml` under `app.jwtSecret`
2. Access tokens expire after 24 hours (configurable in `JwtUtil.java`)
3. Refresh tokens use the same expiration time but could be configured differently
4. User accounts will be locked after 5 failed login attempts for 15 minutes
5. Role-based access control is enforced using Spring Security's `@PreAuthorize` annotation

## Implementation Details

- `JwtUtil`: Handles token generation, validation, and extraction of claims
- `JwtAuthenticationFilter`: Intercepts requests to validate JWT tokens
- `JwtAuthenticationEntryPoint`: Handles unauthorized access attempts
- `CustomUserDetailsService`: Loads user details from the database
- `SecurityConfig`: Configures security settings and authentication providers
- `AuthController`: Provides endpoints for login and token refresh
