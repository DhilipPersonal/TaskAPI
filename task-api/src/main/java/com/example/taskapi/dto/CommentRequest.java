package com.example.taskapi.dto;

import java.util.UUID;

/**
 * DTO for comment create/update requests.
 */
public class CommentRequest {
    private UUID taskId;
    private UUID authorId;
    private String content;
    public UUID getTaskId() { return taskId; }
    public void setTaskId(UUID taskId) { this.taskId = taskId; }
    public UUID getAuthorId() { return authorId; }
    public void setAuthorId(UUID authorId) { this.authorId = authorId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
