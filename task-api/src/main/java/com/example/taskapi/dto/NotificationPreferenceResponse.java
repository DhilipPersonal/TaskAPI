package com.example.taskapi.dto;

import java.util.UUID;

public class NotificationPreferenceResponse {
    private UUID userId;
    private boolean taskComment;
    private boolean taskAssigned;
    private boolean general;

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public boolean isTaskComment() { return taskComment; }
    public void setTaskComment(boolean taskComment) { this.taskComment = taskComment; }
    public boolean isTaskAssigned() { return taskAssigned; }
    public void setTaskAssigned(boolean taskAssigned) { this.taskAssigned = taskAssigned; }
    public boolean isGeneral() { return general; }
    public void setGeneral(boolean general) { this.general = general; }
}
