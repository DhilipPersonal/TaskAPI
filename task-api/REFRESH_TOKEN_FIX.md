# Refresh Token Issue Fix

## Problem
The refresh token endpoint is not properly deserializing the JSON request body, causing validation to fail.

## Root Cause
The issue appears to be related to Jackson deserialization or Lombok configuration.

## Solution Options

### Option 1: Manual Getters/Setters (Recommended)
Replace the Lombok annotations in `RefreshTokenRequest.java` with manual getters and setters:

```java
package com.example.taskapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequest {
    
    @JsonProperty("refreshToken")
    @NotBlank(message = "Refresh token cannot be blank")
    private String refreshToken;
    
    public RefreshTokenRequest() {}
    
    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
```

### Option 2: Alternative Controller Implementation
Modify the AuthController to handle the request differently:

```java
@PostMapping("/refresh")
public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
    String refreshToken = request.get("refreshToken");
    
    if (refreshToken == null || refreshToken.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Refresh token cannot be blank");
    }
    
    if (!jwtUtil.validateToken(refreshToken)) {
        return ResponseEntity.badRequest().body("Invalid refresh token");
    }
    
    String username = jwtUtil.getUsernameFromToken(refreshToken);
    String role = jwtUtil.getRoleFromToken(refreshToken);
    
    String newAccessToken = jwtUtil.generateToken(username, role);
    
    return ResponseEntity.ok(new AuthResponse(
            newAccessToken,
            refreshToken,
            username,
            role
    ));
}
```

## Testing Commands

After applying the fix, test with:

```bash
# Get a token first
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Use the refresh token from the response
curl -X POST http://localhost:8081/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"YOUR_REFRESH_TOKEN_HERE"}'
```

## Current Status
- ✅ Login endpoint working
- ✅ Protected endpoints working  
- ✅ Role-based access control working
- ⚠️ Refresh token endpoint needs the above fix
- ✅ All other JWT functionality working correctly
