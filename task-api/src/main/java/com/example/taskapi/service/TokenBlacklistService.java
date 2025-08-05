package com.example.taskapi.service;

import com.example.taskapi.entity.BlacklistedToken;
import com.example.taskapi.repository.BlacklistedTokenRepository;
import com.example.taskapi.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {
    
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final JwtUtil jwtUtil;
    
    /**
     * Blacklist a token by its JTI
     */
    @Transactional
    public void blacklistToken(String token, String reason) {
        try {
            String jti = jwtUtil.getJtiFromToken(token);
            String tokenType = jwtUtil.getTokenTypeFromToken(token).name();
            Instant expiryDate = jwtUtil.getExpirationFromToken(token);
            
            if (!blacklistedTokenRepository.existsByJti(jti)) {
                BlacklistedToken blacklistedToken = new BlacklistedToken(jti, tokenType, expiryDate, reason);
                blacklistedTokenRepository.save(blacklistedToken);
                log.info("Token with JTI {} blacklisted. Reason: {}", jti, reason);
            }
        } catch (Exception e) {
            log.error("Failed to blacklist token: {}", e.getMessage());
        }
    }
    
    /**
     * Check if a token is blacklisted
     */
    public boolean isTokenBlacklisted(String token) {
        try {
            String jti = jwtUtil.getJtiFromToken(token);
            return blacklistedTokenRepository.existsByJti(jti);
        } catch (Exception e) {
            log.error("Failed to check token blacklist status: {}", e.getMessage());
            return true; // Assume blacklisted if we can't verify
        }
    }
    
    /**
     * Cleanup expired blacklisted tokens - runs every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    @Transactional
    public void cleanupExpiredBlacklistedTokens() {
        try {
            long countBefore = blacklistedTokenRepository.countActiveBlacklistedTokens(Instant.now());
            blacklistedTokenRepository.deleteExpiredTokens(Instant.now());
            long countAfter = blacklistedTokenRepository.countActiveBlacklistedTokens(Instant.now());
            
            if (countBefore > countAfter) {
                log.info("Cleaned up {} expired blacklisted tokens", countBefore - countAfter);
            }
        } catch (Exception e) {
            log.error("Failed to cleanup expired blacklisted tokens: {}", e.getMessage());
        }
    }
    
    /**
     * Get statistics about blacklisted tokens
     */
    public long getActiveBlacklistedTokensCount() {
        return blacklistedTokenRepository.countActiveBlacklistedTokens(Instant.now());
    }
}
