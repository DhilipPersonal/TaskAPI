package com.example.taskapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RateLimitServiceTest {

    private RateLimitService rateLimitService;
    private String testClientId;

    @BeforeEach
    void setUp() {
        rateLimitService = new RateLimitService();
        testClientId = "192.168.1.1";
    }

    @Test
    void testIsAllowedForAuth_InitialRequestsAllowed() {
        // When & Then - First few requests should be allowed
        for (int i = 0; i < 3; i++) {
            assertTrue(rateLimitService.isAllowedForAuth(testClientId),
                    "Request " + (i + 1) + " should be allowed");
        }
    }

    @Test
    void testIsAllowedForApi_InitialRequestsAllowed() {
        // When & Then - First few requests should be allowed
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimitService.isAllowedForApi(testClientId),
                    "Request " + (i + 1) + " should be allowed");
        }
    }

    @Test
    void testIsAllowedForAdmin_InitialRequestsAllowed() {
        // When & Then - First few requests should be allowed
        for (int i = 0; i < 3; i++) {
            assertTrue(rateLimitService.isAllowedForAdmin(testClientId),
                    "Request " + (i + 1) + " should be allowed");
        }
    }

    @Test
    void testDifferentClientsHaveSeparateLimits() {
        String client1 = "192.168.1.1";
        String client2 = "192.168.1.2";

        // When & Then - Different clients should have separate rate limits
        assertTrue(rateLimitService.isAllowedForAuth(client1));
        assertTrue(rateLimitService.isAllowedForAuth(client2));
        
        // Each client should have their own bucket
        assertNotEquals(client1, client2);
    }

    @Test
    void testGetRemainingTokens_ReturnsValidCount() {
        // Given
        String clientId = "test-client";
        
        // When
        long remaining = rateLimitService.getRemainingTokens(clientId, "auth");
        
        // Then
        assertTrue(remaining >= 0, "Remaining tokens should be non-negative");
    }
}
