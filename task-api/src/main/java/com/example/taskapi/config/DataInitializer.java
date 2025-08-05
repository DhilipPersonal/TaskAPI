package com.example.taskapi.config;

import com.example.taskapi.entity.User;
import com.example.taskapi.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Initializes test data for development environment
 */
@Configuration
@RequiredArgsConstructor
@Profile("dev") // Only run in dev profile
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        log.info("Initializing test data...");
        
        // Create admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User adminUser = new User();
            adminUser.setId(UUID.randomUUID());
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@example.com");
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setPasswordHash(passwordEncoder.encode("admin123"));
            adminUser.setRole("ADMIN");
            adminUser.setAccountStatus("active");
            adminUser.setCreatedAt(LocalDateTime.now());
            adminUser.setUpdatedAt(LocalDateTime.now());
            adminUser.setFailedLoginAttempts(0);
            
            userRepository.save(adminUser);
            log.info("Created admin user: {}", adminUser.getUsername());
        }
        
        // Create regular user if not exists
        if (!userRepository.existsByUsername("user")) {
            User regularUser = new User();
            regularUser.setId(UUID.randomUUID());
            regularUser.setUsername("user");
            regularUser.setEmail("user@example.com");
            regularUser.setFirstName("Regular");
            regularUser.setLastName("User");
            regularUser.setPasswordHash(passwordEncoder.encode("user123"));
            regularUser.setRole("USER");
            regularUser.setAccountStatus("active");
            regularUser.setCreatedAt(LocalDateTime.now());
            regularUser.setUpdatedAt(LocalDateTime.now());
            regularUser.setFailedLoginAttempts(0);
            
            userRepository.save(regularUser);
            log.info("Created regular user: {}", regularUser.getUsername());
        }
        
        log.info("Test data initialization complete");
    }
}
