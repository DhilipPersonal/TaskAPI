package com.example.taskapi.service;

import java.util.UUID;

import com.example.taskapi.dto.UserRegistrationRequest;
import com.example.taskapi.dto.AuthenticationRequest;
import com.example.taskapi.dto.AuthenticationResponse;
import com.example.taskapi.entity.User;
import com.example.taskapi.repository.UserRepository;
import com.example.taskapi.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {

    // --- User Management Methods ---
    public User getUserProfile(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public User updateUserProfile(String username, String firstName, String lastName, String avatarUrl) {
        User user = getUserProfile(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAvatarUrl(avatarUrl);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = getUserProfile(username);
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        if (!isPasswordStrong(newPassword)) {
            throw new IllegalArgumentException("New password does not meet strength requirements");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found for email"));
        if (!isPasswordStrong(newPassword)) {
            throw new IllegalArgumentException("New password does not meet strength requirements");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    public void changeUserRole(UUID userId, String newRole) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(newRole);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void updateAvatarUrl(String username, String avatarUrl) {
        User user = getUserProfile(username);
        user.setAvatarUrl(avatarUrl);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public User registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }
        if (!isPasswordStrong(request.getPassword())) {
            throw new IllegalArgumentException("Password does not meet strength requirements");
        }
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .accountStatus("active")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }

    private boolean isPasswordStrong(String password) {
        // At least 8 chars, 1 upper, 1 lower, 1 digit
        return password != null && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        
        // Check if account is locked
        if (user.getLockoutUntil() != null && user.getLockoutUntil().isAfter(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("Account is locked. Try again after " + user.getLockoutUntil());
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            // Increment failed attempts
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            // Lock after 5 failed attempts for 15 minutes
            if (attempts >= 5) {
                user.setLockoutUntil(java.time.LocalDateTime.now().plusMinutes(15));
                user.setFailedLoginAttempts(0); // reset attempts after lockout
            }
            userRepository.save(user);
            throw new IllegalArgumentException("Invalid username or password");
        }
        // Successful login: reset attempts and lockout
        user.setFailedLoginAttempts(0);
        user.setLockoutUntil(null);
        user.setLastLogin(java.time.LocalDateTime.now());
        userRepository.save(user);
        String accessToken = jwtUtil.generateToken(user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new AuthenticationResponse(accessToken, refreshToken);
    }
}
