package com.example.taskapi.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for testing authentication and authorization
 */
@RestController
@RequestMapping("/api/v1/test")
@Tag(name = "Testing", description = "Endpoints for testing authentication and authorization")
public class TestController {

    @Operation(summary = "Public Endpoint", description = "Accessible without authentication")
    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("Public endpoint - Anyone can access");
    }

    @Operation(summary = "Protected Endpoint", description = "Requires JWT authentication")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/protected")
    public ResponseEntity<Map<String, Object>> protectedEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Protected endpoint - Authentication required");
        response.put("username", auth.getName());
        response.put("authorities", auth.getAuthorities());
        return ResponseEntity.ok(response);
    }

    /**
     * Admin endpoint that requires ADMIN role
     * @return Admin message
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok("Admin endpoint - ADMIN role required");
    }

    /**
     * User endpoint that requires USER role
     * @return User message
     */
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> userEndpoint() {
        return ResponseEntity.ok("User endpoint - USER role required");
    }
}
