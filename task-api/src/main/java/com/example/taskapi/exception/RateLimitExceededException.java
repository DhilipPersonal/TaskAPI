package com.example.taskapi.exception;

public class RateLimitExceededException extends RuntimeException {
    
    private final String category;
    private final long retryAfterSeconds;
    
    public RateLimitExceededException(String message, String category, long retryAfterSeconds) {
        super(message);
        this.category = category;
        this.retryAfterSeconds = retryAfterSeconds;
    }
    
    public String getCategory() {
        return category;
    }
    
    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
