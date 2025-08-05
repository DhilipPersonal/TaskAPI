package com.example.taskapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "blacklisted_tokens", indexes = {
    @Index(name = "idx_blacklisted_token_jti", columnList = "jti"),
    @Index(name = "idx_blacklisted_token_expiry", columnList = "expiryDate")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistedToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String jti; // JWT ID
    
    @Column(nullable = false)
    private String tokenType; // ACCESS or REFRESH
    
    @Column(nullable = false)
    private Instant expiryDate;
    
    @Column(nullable = false)
    private Instant blacklistedAt;
    
    @Column(length = 100)
    private String reason; // logout, revoked, etc.
    
    public BlacklistedToken(String jti, String tokenType, Instant expiryDate, String reason) {
        this.jti = jti;
        this.tokenType = tokenType;
        this.expiryDate = expiryDate;
        this.blacklistedAt = Instant.now();
        this.reason = reason;
    }
}
