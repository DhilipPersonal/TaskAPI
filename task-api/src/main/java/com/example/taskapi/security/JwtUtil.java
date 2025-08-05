package com.example.taskapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    @Value("${app.jwtSecret}")
    private String jwtSecret;
    
    @Value("${app.accessTokenExpirationMs:900000}") // 15 minutes by default
    private long accessTokenExpirationMs;
    
    @Value("${app.refreshTokenExpirationMs:604800000}") // 7 days by default
    private long refreshTokenExpirationMs;
    
    private SecretKey key;
    
    public enum TokenType {
        ACCESS,
        REFRESH
    }
    
    @PostConstruct
    public void init() {
        if (jwtSecret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long");
        }
        key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateAccessToken(String username, String role) {
        return generateToken(username, role, TokenType.ACCESS, UUID.randomUUID().toString());
    }
    
    public String generateRefreshToken(String username, String role, String jti) {
        return generateToken(username, role, TokenType.REFRESH, jti);
    }
    
    private String generateToken(String username, String role, TokenType tokenType, String jti) {
        long expirationTime = tokenType == TokenType.ACCESS ? accessTokenExpirationMs : refreshTokenExpirationMs;
        
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("type", tokenType.name())
                .setId(jti) // Set JWT ID for token blacklisting
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }
    
    public String getJtiFromToken(String token) {
        return getClaims(token).getId();
    }
    
    public TokenType getTokenTypeFromToken(String token) {
        String type = getClaims(token).get("type", String.class);
        return TokenType.valueOf(type);
    }
    
    public Instant getExpirationFromToken(String token) {
        return getClaims(token).getExpiration().toInstant();
    }
    
    public Date getIssuedAtFromToken(String token) {
        return getClaims(token).getIssuedAt();
    }
    
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            
            // Check if token is expired
            if (claims.getExpiration().before(new Date())) {
                return false;
            }
            
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
