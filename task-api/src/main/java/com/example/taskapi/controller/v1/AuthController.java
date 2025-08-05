package com.example.taskapi.controller.v1;

import com.example.taskapi.dto.AuthRequest;
import com.example.taskapi.dto.AuthResponse;
import com.example.taskapi.dto.RefreshTokenRequest;
import com.example.taskapi.exception.TokenRefreshException;
import com.example.taskapi.model.RefreshToken;
import com.example.taskapi.security.JwtUtil;
import com.example.taskapi.service.RefreshTokenService;
import com.example.taskapi.service.TokenBlacklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    @Operation(summary = "User Login", description = "Authenticate user and return JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            log.info("User {} successfully authenticated", loginRequest.getUsername());
        
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        String role = roles.isEmpty() ? "USER" : roles.get(0).replace("ROLE_", "");
        
        // Create refresh token in database
        RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(userDetails.getUsername());
        
        // Generate JWT tokens
        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername(), role);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername(), role, refreshTokenEntity.getJti());
        
        log.info("Generated tokens for user {}: access token and refresh token with jti {}", 
                userDetails.getUsername(), refreshTokenEntity.getJti());

        return ResponseEntity.ok(new AuthResponse(
                accessToken,
                refreshToken,
                userDetails.getUsername(),
                role
        ));
        } catch (Exception e) {
            log.warn("Authentication failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Refresh token cannot be blank");
        }
        
        try {
            // Validate token format
            if (!jwtUtil.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token format");
            }
            
            // Check token type
            if (jwtUtil.getTokenTypeFromToken(refreshToken) != JwtUtil.TokenType.REFRESH) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is not a refresh token");
            }
            
            // Get token details
            String jti = jwtUtil.getJtiFromToken(refreshToken);
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            String role = jwtUtil.getRoleFromToken(refreshToken);
            
            // Verify token exists in database and is not expired/revoked
            RefreshToken tokenEntity = refreshTokenService.findByJti(jti)
                    .orElseThrow(() -> new TokenRefreshException(refreshToken, "Refresh token not found in database"));
            
            refreshTokenService.verifyExpiration(tokenEntity);
            
            // Create new refresh token (one-time use)
            RefreshToken newRefreshTokenEntity = refreshTokenService.createRefreshToken(username);
            
            // Revoke old token
            refreshTokenService.revokeToken(tokenEntity.getToken());
            log.info("Refresh token with jti {} revoked during refresh operation", jti);
            
            // Generate new tokens
            String newAccessToken = jwtUtil.generateAccessToken(username, role);
            String newRefreshToken = jwtUtil.generateRefreshToken(username, role, newRefreshTokenEntity.getJti());
            
            log.info("Token refresh successful for user {}: new access token and refresh token with jti {} issued", 
                    username, newRefreshTokenEntity.getJti());
            
            return ResponseEntity.ok(new AuthResponse(
                    newAccessToken,
                    newRefreshToken,
                    username,
                    role
            ));
        } catch (TokenRefreshException e) {
            log.warn("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            log.error("Token refresh error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not process refresh token: " + e.getMessage());
        }
    }
    
    @Operation(summary = "User Logout", description = "Revoke refresh token and invalidate user session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request, HttpServletRequest httpRequest) {
        String refreshToken = request.getRefreshToken();
        
        // Extract access token from Authorization header
        String authHeader = httpRequest.getHeader("Authorization");
        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }
        
        try {
            // Blacklist access token if present
            if (accessToken != null) {
                tokenBlacklistService.blacklistToken(accessToken, "logout");
            }
            
            // Blacklist refresh token if present
            if (refreshToken != null) {
                tokenBlacklistService.blacklistToken(refreshToken, "logout");
                // Also revoke from refresh token service
                refreshTokenService.revokeToken(refreshToken);
            }
            
            // Extract token details for logging/auditing
            String username = "unknown";
            try {
                if (accessToken != null) {
                    username = jwtUtil.getUsernameFromToken(accessToken);
                } else if (refreshToken != null) {
                    username = jwtUtil.getUsernameFromToken(refreshToken);
                }
                log.info("User {} logged out successfully", username);
            } catch (Exception e) {
                log.warn("Could not parse token during logout", e);
            }
            
            // Clear security context
            SecurityContextHolder.clearContext();
            
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            // Even if token operations fail, we consider the logout successful
            SecurityContextHolder.clearContext();
            log.warn("Logout completed with warnings: {}", e.getMessage());
            return ResponseEntity.ok("Logged out successfully");
        }
    }
}
