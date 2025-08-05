package com.example.taskapi.security;

import com.example.taskapi.dto.AuthRequest;
import com.example.taskapi.dto.AuthResponse;
import com.example.taskapi.dto.RefreshTokenRequest;
import com.example.taskapi.model.RefreshToken;
import com.example.taskapi.entity.User;
import com.example.taskapi.repository.RefreshTokenRepository;

import java.util.UUID;
import com.example.taskapi.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Collections;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    private User testUser;
    private UserDetails userDetails;
    private Authentication authentication;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        // Set up test user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setPasswordHash("password");
        testUser.setEmail("test@example.com");
        testUser.setRole("USER");

        // Set up UserDetails
        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("testuser")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        // Set up Authentication
        authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // Set up RefreshToken
        refreshToken = new RefreshToken();
        refreshToken.setId(1L);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setJti(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusSeconds(604800)); // 7 days
        refreshToken.setRevoked(false);
    }

    @Test
    void testLoginSuccess() throws Exception {
        // Arrange
        AuthRequest authRequest = new AuthRequest("testuser", "password");
        String accessToken = "test-access-token";
        String refreshTokenStr = "test-refresh-token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(refreshTokenService.createRefreshToken(anyString())).thenReturn(refreshToken);
        when(jwtUtil.generateAccessToken(anyString(), anyString())).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(anyString(), anyString(), anyString())).thenReturn(refreshTokenStr);

        // Act
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        AuthResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponse.class);
        
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(refreshTokenStr, response.getRefreshToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("USER", response.getRole());
        
        verify(refreshTokenService).createRefreshToken("testuser");
        verify(jwtUtil).generateAccessToken("testuser", "USER");
        verify(jwtUtil).generateRefreshToken("testuser", "USER", refreshToken.getJti());
    }

    @Test
    void testRefreshTokenSuccess() throws Exception {
        // Arrange
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken("valid-refresh-token");
        
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";
        String jti = UUID.randomUUID().toString();
        
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.getTokenTypeFromToken(anyString())).thenReturn(JwtUtil.TokenType.REFRESH);
        when(jwtUtil.getJtiFromToken(anyString())).thenReturn(jti);
        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn("testuser");
        when(jwtUtil.getRoleFromToken(anyString())).thenReturn("USER");
        
        when(refreshTokenService.findByJti(jti)).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.createRefreshToken("testuser")).thenReturn(refreshToken);
        doNothing().when(refreshTokenService).verifyExpiration(any(RefreshToken.class));
        doNothing().when(refreshTokenService).revokeToken(anyString());
        
        when(jwtUtil.generateAccessToken("testuser", "USER")).thenReturn(newAccessToken);
        when(jwtUtil.generateRefreshToken("testuser", "USER", refreshToken.getJti())).thenReturn(newRefreshToken);

        // Act
        MvcResult result = mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        AuthResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponse.class);
        
        assertEquals(newAccessToken, response.getAccessToken());
        assertEquals(newRefreshToken, response.getRefreshToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("USER", response.getRole());
        
        verify(refreshTokenService).findByJti(jti);
        verify(refreshTokenService).verifyExpiration(refreshToken);
        verify(refreshTokenService).revokeToken(anyString());
        verify(refreshTokenService).createRefreshToken("testuser");
    }

    @Test
    void testRefreshTokenInvalidType() throws Exception {
        // Arrange
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken("invalid-type-token");
        
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.getTokenTypeFromToken(anyString())).thenReturn(JwtUtil.TokenType.ACCESS);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLogoutSuccess() throws Exception {
        // Arrange
        RefreshTokenRequest logoutRequest = new RefreshTokenRequest();
        logoutRequest.setRefreshToken("valid-refresh-token");
        
        doNothing().when(refreshTokenService).revokeToken(anyString());

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isOk());
        
        verify(refreshTokenService).revokeToken("valid-refresh-token");
    }

    @Test
    void testLogoutWithNullToken() throws Exception {
        // Arrange
        RefreshTokenRequest logoutRequest = new RefreshTokenRequest();
        logoutRequest.setRefreshToken(null);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isOk());
        
        verify(refreshTokenService, never()).revokeToken(anyString());
    }
}
