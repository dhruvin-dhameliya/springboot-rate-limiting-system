package com.project.api_rate_limiter.algorithm;

public interface RateLimitAlgorithm {
    // key to identify the client/endpoint IP/user-ID/API endpoint
    boolean allowRequest(String key, int maxRequests, int timeWindowSeconds);
    long getWaitTimeSeconds(String key);
} 