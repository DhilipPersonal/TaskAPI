package com.example.taskapi.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "notification_preferences", schema = "taskapp")
public class NotificationPreference {
    @Id
    private UUID userId;

    @Column(nullable = false)
    private boolean taskComment = true;

    @Column(nullable = false)
    private boolean taskAssigned = true;

    @Column(nullable = false)
    private boolean general = true;

    // Getters and setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public boolean isTaskComment() { return taskComment; }
    public void setTaskComment(boolean taskComment) { this.taskComment = taskComment; }
    public boolean isTaskAssigned() { return taskAssigned; }
    public void setTaskAssigned(boolean taskAssigned) { this.taskAssigned = taskAssigned; }
    public boolean isGeneral() { return general; }
    public void setGeneral(boolean general) { this.general = general; }
}
