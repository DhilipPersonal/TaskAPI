package com.example.taskapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ResetPasswordRequest {
    @NotBlank
    @Email
    private String email;
    // getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
