package com.example.taskapi.dto;

import java.util.Set;
import java.util.UUID;

public class TeamResponse {
    private UUID id;
    private String name;
    private String description;
    private Set<UUID> memberIds;

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Set<UUID> getMemberIds() { return memberIds; }
    public void setMemberIds(Set<UUID> memberIds) { this.memberIds = memberIds; }
}
