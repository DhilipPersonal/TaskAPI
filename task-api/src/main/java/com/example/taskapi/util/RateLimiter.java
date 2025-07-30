package com.example.taskapi.util;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {
    private final Map<String, RequestInfo> requestCounts = new ConcurrentHashMap<>();
    private final long windowSizeMs;
    private final int maxRequests;

    public RateLimiter(long windowSizeMs, int maxRequests) {
        this.windowSizeMs = windowSizeMs;
        this.maxRequests = maxRequests;
    }

    public synchronized boolean isAllowed(String key) {
        long now = Instant.now().toEpochMilli();
        RequestInfo info = requestCounts.getOrDefault(key, new RequestInfo(0, now));
        if (now - info.windowStart > windowSizeMs) {
            info = new RequestInfo(0, now);
        }
        if (info.count < maxRequests) {
            info.count++;
            requestCounts.put(key, info);
            return true;
        }
        return false;
    }

    private static class RequestInfo {
        int count;
        long windowStart;
        RequestInfo(int count, long windowStart) {
            this.count = count;
            this.windowStart = windowStart;
        }
    }
}
