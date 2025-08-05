package com.example.taskapi.repository;

import com.example.taskapi.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    
    Optional<BlacklistedToken> findByJti(String jti);
    
    boolean existsByJti(String jti);
    
    @Modifying
    @Query("DELETE FROM BlacklistedToken bt WHERE bt.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") Instant now);
    
    @Query("SELECT COUNT(bt) FROM BlacklistedToken bt WHERE bt.expiryDate >= :now")
    long countActiveBlacklistedTokens(@Param("now") Instant now);
}
