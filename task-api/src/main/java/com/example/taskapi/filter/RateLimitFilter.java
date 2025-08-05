package com.example.taskapi.filter;

import com.example.taskapi.service.RateLimitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1) // Execute before other filters
public class RateLimitFilter extends OncePerRequestFilter {
    
    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, 
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        String clientId = getClientIdentifier(request);
        
        // Skip rate limiting for health checks and public endpoints
        if (shouldSkipRateLimit(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        boolean allowed = false;
        String category = "api";
        
        // Apply different rate limits based on endpoint
        if (requestPath.startsWith("/api/v1/auth/")) {
            allowed = rateLimitService.isAllowedForAuth(clientId);
            category = "auth";
        } else if (requestPath.startsWith("/api/v1/admin/")) {
            allowed = rateLimitService.isAllowedForAdmin(clientId);
            category = "admin";
        } else if (requestPath.startsWith("/api/v1/")) {
            allowed = rateLimitService.isAllowedForApi(clientId);
            category = "api";
        } else {
            // For non-API endpoints, allow through
            allowed = true;
        }
        
        if (!allowed) {
            handleRateLimitExceeded(response, clientId, category);
            return;
        }
        
        // Add rate limit headers
        addRateLimitHeaders(response, clientId, category);
        
        filterChain.doFilter(request, response);
    }
    
    private String getClientIdentifier(HttpServletRequest request) {
        // Try to get authenticated user first
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Could extract username from JWT token here
            // For now, fall back to IP
        }
        
        // Fall back to IP address
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    
    private boolean shouldSkipRateLimit(String path) {
        return path.equals("/actuator/health") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/") ||
               path.equals("/favicon.ico");
    }
    
    private void handleRateLimitExceeded(HttpServletResponse response, String clientId, String category) 
            throws IOException {
        response.setStatus(429); // Too Many Requests
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Rate limit exceeded");
        errorResponse.put("message", "Too many requests. Please try again later.");
        errorResponse.put("category", category);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        // Add retry-after header (60 seconds)
        response.setHeader("Retry-After", "60");
        
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        
        log.warn("Rate limit exceeded for client {} in category {}", clientId, category);
    }
    
    private void addRateLimitHeaders(HttpServletResponse response, String clientId, String category) {
        try {
            long remainingTokens = rateLimitService.getRemainingTokens(clientId, category);
            response.setHeader("X-RateLimit-Remaining", String.valueOf(remainingTokens));
            response.setHeader("X-RateLimit-Category", category);
        } catch (Exception e) {
            log.debug("Failed to add rate limit headers: {}", e.getMessage());
        }
    }
}
