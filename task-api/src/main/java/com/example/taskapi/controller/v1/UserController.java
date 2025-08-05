package com.example.taskapi.controller.v1;

import com.example.taskapi.dto.UserProfileResponse;
import com.example.taskapi.dto.ChangePasswordRequest;
import com.example.taskapi.dto.ResetPasswordRequest;
import com.example.taskapi.dto.UserRoleChangeRequest;

import com.example.taskapi.dto.UserRegistrationRequest;
import com.example.taskapi.entity.User;
import com.example.taskapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/test-json")
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> testJson(@RequestBody UserRegistrationRequest request) {
        return ResponseEntity.ok("Received: " + request.getUsername() + ", " + request.getEmail());
    }

    @PostMapping("/test-register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> testRegister(@RequestBody UserRegistrationRequest request) {
        try {
            UserProfileResponse userProfile = userService.registerUserAndGetProfile(request);
            return new ResponseEntity<>(userProfile, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration Error: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request) {
        try {
            UserProfileResponse userProfile = userService.registerUserAndGetProfile(request);
            return new ResponseEntity<>(userProfile, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/create")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> createNewUser(@RequestBody UserRegistrationRequest request) {
        try {
            UserProfileResponse userProfile = userService.registerUserAndGetProfile(request);
            return new ResponseEntity<>(userProfile, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Login endpoint moved to AuthController

    // Get current user profile
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile(@org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        User user = userService.getUserProfile(principal.getUsername());
        UserProfileResponse resp = mapToProfileResponse(user);
        return ResponseEntity.ok(resp);
    }

    // Update current user profile
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @RequestBody UserProfileResponse req) {
        User user = userService.updateUserProfile(principal.getUsername(), req.getFirstName(), req.getLastName(), req.getAvatarUrl());
        return ResponseEntity.ok(mapToProfileResponse(user));
    }

    // Change password
    @PostMapping("/me/change-password")
    public ResponseEntity<Void> changePassword(
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @RequestBody ChangePasswordRequest req) {
        userService.changePassword(principal.getUsername(), req.getOldPassword(), req.getNewPassword());
        return ResponseEntity.ok().build();
    }

    // Reset password (by email, e.g. forgot password)
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest req) {
        // In production, this would send an email with a token; here, just reset for demo
        userService.resetPassword(req.getEmail(), "NewTempPassword123"); // Replace with secure logic
        return ResponseEntity.ok().build();
    }

    // Admin: delete user
    @DeleteMapping("/{userId}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable java.util.UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // Admin: change user role
    @PostMapping("/{userId}/role")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changeUserRole(@PathVariable java.util.UUID userId, @RequestBody UserRoleChangeRequest req) {
        userService.changeUserRole(userId, req.getRole());
        return ResponseEntity.ok().build();
    }

    // Update avatar URL (for demo, not file upload)
    @PostMapping("/me/avatar")
    public ResponseEntity<Void> updateAvatar(
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @RequestBody String avatarUrl) {
        userService.updateAvatarUrl(principal.getUsername(), avatarUrl);
        return ResponseEntity.ok().build();
    }

    private UserProfileResponse mapToProfileResponse(User user) {
        UserProfileResponse resp = new UserProfileResponse();
        resp.setId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setEmail(user.getEmail());
        resp.setFirstName(user.getFirstName());
        resp.setLastName(user.getLastName());
        resp.setAvatarUrl(user.getAvatarUrl());
        resp.setRole(user.getRole());
        resp.setAccountStatus(user.getAccountStatus());
        resp.setLastLogin(user.getLastLogin());
        resp.setCreatedAt(user.getCreatedAt());
        resp.setUpdatedAt(user.getUpdatedAt());
        return resp;
    }
}
