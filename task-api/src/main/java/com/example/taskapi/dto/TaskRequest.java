package com.example.taskapi.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TaskRequest {
    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDateTime dueDate;
    private UUID assignedTo;
    private UUID projectId;
    private UUID parentTaskId;
    private List<UUID> dependencies;
    private String recurrence;

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public UUID getAssignedTo() { return assignedTo; }
    public void setAssignedTo(UUID assignedTo) { this.assignedTo = assignedTo; }
    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }
    public UUID getParentTaskId() { return parentTaskId; }
    public void setParentTaskId(UUID parentTaskId) { this.parentTaskId = parentTaskId; }
    public List<UUID> getDependencies() { return dependencies; }
    public void setDependencies(List<UUID> dependencies) { this.dependencies = dependencies; }
    public String getRecurrence() { return recurrence; }
    public void setRecurrence(String recurrence) { this.recurrence = recurrence; }
}
