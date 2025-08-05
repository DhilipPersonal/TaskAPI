package com.example.taskapi.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitingConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimitingInterceptor())
                .addPathPatterns("/api/v1/auth/login", "/api/v1/auth/refresh");
    }

    // Inner class for the interceptor
    public static class RateLimitingInterceptor implements HandlerInterceptor {
        // Store IP addresses and their request timestamps
        private final Map<String, RequestTracker> ipTrackers = new ConcurrentHashMap<>();
        
        // Maximum requests allowed per minute
        private static final int MAX_REQUESTS = 5;
        
        // Window size in seconds
        private static final int WINDOW_SIZE_SECONDS = 60;

        @Override
        public boolean preHandle(
                @NonNull HttpServletRequest request, 
                @NonNull HttpServletResponse response, 
                @NonNull Object handler) throws Exception {
            
            String ip = getClientIP(request);
            // Get or create a request tracker for this IP
            RequestTracker tracker = ipTrackers.computeIfAbsent(ip, k -> new RequestTracker());
            
            // Check if rate limit is exceeded
            if (tracker.isRateLimitExceeded()) {
                response.setStatus(429); // Too Many Requests
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Rate limit exceeded. Try again later.\"}");
                return false;
            }
            
            // Record this request
            tracker.recordRequest();
            return true;
        }

        private String getClientIP(HttpServletRequest request) {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        }
        
        // Helper class to track requests
        private static class RequestTracker {
            private final long[] requestTimestamps = new long[MAX_REQUESTS];
            private int currentIndex = 0;
            
            public synchronized boolean isRateLimitExceeded() {
                long now = Instant.now().toEpochMilli();
                long windowStart = now - Duration.ofSeconds(WINDOW_SIZE_SECONDS).toMillis();
                
                // Check if we've made MAX_REQUESTS in the time window
                for (int i = 0; i < MAX_REQUESTS; i++) {
                    if (requestTimestamps[i] > windowStart) {
                        continue;
                    }
                    // Found a slot that's outside the current time window
                    return false;
                }
                
                // All slots are within the time window, rate limit exceeded
                return true;
            }
            
            public synchronized void recordRequest() {
                requestTimestamps[currentIndex] = Instant.now().toEpochMilli();
                currentIndex = (currentIndex + 1) % MAX_REQUESTS;
            }
        }
    }
}
