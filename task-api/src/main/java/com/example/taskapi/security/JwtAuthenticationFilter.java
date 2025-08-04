package com.example.taskapi.security;

import com.example.taskapi.service.TokenBlacklistService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filter that intercepts all requests to validate JWT tokens and set authentication
 */
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, 
            @NonNull HttpServletResponse response, 
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            String jwt = parseJwt(request);
            String requestPath = request.getRequestURI();
            String clientIp = getClientIP(request);
            
            if (jwt == null) {
                // No JWT token found, continue with filter chain
                if (!isPublicEndpoint(requestPath)) {
                    log.debug("No JWT token found in request to protected path {} from IP {}", requestPath, clientIp);
                }
                filterChain.doFilter(request, response);
                return;
            }
            
            // Validate token format and expiration
            if (!jwtUtil.validateToken(jwt)) {
                log.warn("Invalid JWT token in request to {} from IP {}", requestPath, clientIp);
                filterChain.doFilter(request, response);
                return;
            }
            
            // Check if token is an access token (not a refresh token)
            if (jwtUtil.getTokenTypeFromToken(jwt) != JwtUtil.TokenType.ACCESS) {
                log.warn("Attempted to use a non-access token for authentication to {} from IP {}", requestPath, clientIp);
                filterChain.doFilter(request, response);
                return;
            }
            
            // Check if token has been blacklisted
            if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
                log.warn("Attempted to use a blacklisted token to {} from IP {}", requestPath, clientIp);
                filterChain.doFilter(request, response);
                return;
            }
            
            String username = jwtUtil.getUsernameFromToken(jwt);
            String role = jwtUtil.getRoleFromToken(jwt);
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
            );
            
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            log.debug("User {} with role {} authenticated successfully for path {} from IP {}", 
                    username, role, requestPath, clientIp);
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }

    private String parseJwt(@NonNull HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        
        return null;
    }
    
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    
    private boolean isPublicEndpoint(String path) {
        // Enhanced logging for debugging
        log.info("Checking if path '{}' is public", path);
        
        boolean isPublic = path.startsWith("/api/v1/auth/") || 
               path.contains("/api/v1/users/register") || 
               path.contains("/api/v1/users/create") || 
               path.contains("/api/v1/users/test-json") || 
               path.contains("/api/v1/users/test-register") || 
               path.startsWith("/api/v1/test/public") || 
               path.startsWith("/swagger-ui/") || 
               path.startsWith("/v3/api-docs/") || 
               path.equals("/actuator/health");
               
        log.info("Path '{}' is public: {}", path, isPublic);
        if (!isPublic && path.contains("register")) {
            log.warn("Registration path '{}' was NOT identified as public - this may cause 401/403 errors", path);
        }
        return isPublic;
    }
}
