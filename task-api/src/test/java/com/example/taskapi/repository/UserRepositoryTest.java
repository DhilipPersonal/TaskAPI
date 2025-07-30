package com.example.taskapi.repository;

import com.example.taskapi.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindUser() {
        User user = User.builder()
                .email("test@example.com")
                .username("testuser")
                .passwordHash("dummyhash")
                .role("USER")
                .accountStatus("active")
                .build();
        User saved = userRepository.save(user);
        assertThat(saved.getId()).isNotNull();
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
    }
}
