package com.example.taskapi.dto;

/**
 * @deprecated Use {@link AuthResponse} instead
 */
@Deprecated
public class AuthenticationResponse extends AuthResponse {
    public AuthenticationResponse(String accessToken, String refreshToken) {
        super(accessToken, refreshToken, null, null);
    }
    
    public AuthenticationResponse(String accessToken, String refreshToken, String username, String role) {
        super(accessToken, refreshToken, username, role);
    }
}
