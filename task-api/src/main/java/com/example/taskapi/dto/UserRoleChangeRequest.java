package com.example.taskapi.dto;

import jakarta.validation.constraints.NotBlank;

public class UserRoleChangeRequest {
    @NotBlank
    private String role;
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
