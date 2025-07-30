package com.example.taskapi.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for project create/update requests. Validation enforced.
 */
public class ProjectRequest {
    @jakarta.validation.constraints.NotBlank
@jakarta.validation.constraints.Size(max = 255)
private String name;
    @jakarta.validation.constraints.Size(max = 255)
private String description;
    @jakarta.validation.constraints.NotNull
private String status;
    @jakarta.validation.constraints.NotNull
private LocalDateTime startDate;
    @jakarta.validation.constraints.NotNull
private LocalDateTime endDate;
    private Set<UUID> memberIds;
    private UUID ownerId;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public Set<UUID> getMemberIds() { return memberIds; }
    public void setMemberIds(Set<UUID> memberIds) { this.memberIds = memberIds; }
    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }
}
