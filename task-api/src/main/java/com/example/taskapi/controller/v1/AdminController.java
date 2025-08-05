package com.example.taskapi.controller.v1;

import com.example.taskapi.entity.User;
import com.example.taskapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for admin-only operations
 * All endpoints in this controller require ADMIN role
 */
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    /**
     * Get all users in the system
     * @return List of all users
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    /**
     * Get user by ID
     * @param userId User ID
     * @return User details
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable UUID userId) {
        return ResponseEntity.ok(userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId)));
    }

    /**
     * Delete user by ID
     * @param userId User ID
     * @return No content response
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userRepository.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * System health check endpoint
     * @return System status
     */
    @GetMapping("/system/health")
    public ResponseEntity<String> systemHealth() {
        return ResponseEntity.ok("System is healthy");
    }
}
