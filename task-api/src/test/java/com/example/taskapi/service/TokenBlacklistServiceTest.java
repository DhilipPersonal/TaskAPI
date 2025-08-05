package com.example.taskapi.service;

import com.example.taskapi.entity.BlacklistedToken;
import com.example.taskapi.repository.BlacklistedTokenRepository;
import com.example.taskapi.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    private String testToken;
    private String testJti;
    private Instant testExpiresAt;

    @BeforeEach
    void setUp() {
        testToken = "test.jwt.token";
        testJti = "test-jti-123";
        testExpiresAt = Instant.now().plusSeconds(3600);
    }

    @Test
    void testBlacklistToken_Success() {
        // Given
        when(jwtUtil.getJtiFromToken(testToken)).thenReturn(testJti);
        when(jwtUtil.getTokenTypeFromToken(testToken)).thenReturn(JwtUtil.TokenType.ACCESS);
        when(jwtUtil.getExpirationFromToken(testToken)).thenReturn(testExpiresAt);
        when(blacklistedTokenRepository.existsByJti(testJti)).thenReturn(false);
        when(blacklistedTokenRepository.save(any(BlacklistedToken.class)))
                .thenReturn(new BlacklistedToken());

        // When
        tokenBlacklistService.blacklistToken(testToken, "Test reason");

        // Then
        verify(blacklistedTokenRepository, times(1)).save(any(BlacklistedToken.class));
    }

    @Test
    void testIsTokenBlacklisted_TokenExists() {
        // Given
        when(jwtUtil.getJtiFromToken(testToken)).thenReturn(testJti);
        when(blacklistedTokenRepository.existsByJti(testJti)).thenReturn(true);

        // When
        boolean result = tokenBlacklistService.isTokenBlacklisted(testToken);

        // Then
        assertTrue(result);
        verify(blacklistedTokenRepository, times(1)).existsByJti(testJti);
    }

    @Test
    void testIsTokenBlacklisted_TokenNotExists() {
        // Given
        when(jwtUtil.getJtiFromToken(testToken)).thenReturn(testJti);
        when(blacklistedTokenRepository.existsByJti(testJti)).thenReturn(false);

        // When
        boolean result = tokenBlacklistService.isTokenBlacklisted(testToken);

        // Then
        assertFalse(result);
        verify(blacklistedTokenRepository, times(1)).existsByJti(testJti);
    }

    @Test
    void testCleanupExpiredTokens() {
        // Given
        when(blacklistedTokenRepository.countActiveBlacklistedTokens(any()))
                .thenReturn(10L).thenReturn(5L);

        // When
        tokenBlacklistService.cleanupExpiredBlacklistedTokens();

        // Then
        verify(blacklistedTokenRepository, times(1))
                .deleteExpiredTokens(any());
    }
}
