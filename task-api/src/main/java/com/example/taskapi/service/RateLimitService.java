package com.example.taskapi.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RateLimitService {
    
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    // Different rate limits for different endpoints
    private static final int AUTH_REQUESTS_PER_MINUTE = 5;
    private static final int API_REQUESTS_PER_MINUTE = 100;
    private static final int ADMIN_REQUESTS_PER_MINUTE = 200;
    
    /**
     * Check if request is allowed for authentication endpoints
     */
    public boolean isAllowedForAuth(String clientId) {
        return isAllowed(clientId, "auth", AUTH_REQUESTS_PER_MINUTE);
    }
    
    /**
     * Check if request is allowed for general API endpoints
     */
    public boolean isAllowedForApi(String clientId) {
        return isAllowed(clientId, "api", API_REQUESTS_PER_MINUTE);
    }
    
    /**
     * Check if request is allowed for admin endpoints
     */
    public boolean isAllowedForAdmin(String clientId) {
        return isAllowed(clientId, "admin", ADMIN_REQUESTS_PER_MINUTE);
    }
    
    /**
     * Generic rate limiting check
     */
    private boolean isAllowed(String clientId, String category, int requestsPerMinute) {
        String bucketKey = clientId + ":" + category;
        Bucket bucket = buckets.computeIfAbsent(bucketKey, key -> createBucket(requestsPerMinute));
        
        boolean allowed = bucket.tryConsume(1);
        
        if (!allowed) {
            log.warn("Rate limit exceeded for client {} in category {}", clientId, category);
        }
        
        return allowed;
    }
    
    /**
     * Create a bucket with specified rate limit
     */
    private Bucket createBucket(int requestsPerMinute) {
        Bandwidth limit = Bandwidth.classic(requestsPerMinute, Refill.intervally(requestsPerMinute, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
    
    /**
     * Get remaining tokens for a client and category
     */
    public long getRemainingTokens(String clientId, String category) {
        String bucketKey = clientId + ":" + category;
        Bucket bucket = buckets.get(bucketKey);
        return bucket != null ? bucket.getAvailableTokens() : 0;
    }
    
    /**
     * Reset rate limit for a client (admin function)
     */
    public void resetRateLimit(String clientId) {
        buckets.entrySet().removeIf(entry -> entry.getKey().startsWith(clientId + ":"));
        log.info("Rate limit reset for client {}", clientId);
    }
    
    /**
     * Get rate limit statistics
     */
    public Map<String, Long> getRateLimitStats() {
        Map<String, Long> stats = new ConcurrentHashMap<>();
        buckets.forEach((key, bucket) -> {
            stats.put(key, bucket.getAvailableTokens());
        });
        return stats;
    }
}
