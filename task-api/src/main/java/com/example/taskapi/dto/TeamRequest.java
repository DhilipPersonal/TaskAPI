package com.example.taskapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for team create/update requests. Validation enforced.
 */
public class TeamRequest {
    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String description;

    private Set<UUID> memberIds;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Set<UUID> getMemberIds() { return memberIds; }
    public void setMemberIds(Set<UUID> memberIds) { this.memberIds = memberIds; }
}
