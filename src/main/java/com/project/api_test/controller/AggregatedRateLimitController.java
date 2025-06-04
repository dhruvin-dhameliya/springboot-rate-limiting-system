package com.project.api_test.controller;

import com.project.api_rate_limiter.annotation.RateLimit;
import com.project.api_rate_limiter.annotation.RateLimitType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * This controller demonstrates applying rate limiting at the class level.
 * All endpoints in this controller will share the same rate limit.
 */
@RestController
@RequestMapping("/api/aggregated")
@RateLimit(limit = 20, timeWindowSeconds = 60, type = RateLimitType.IP_BASED)
public class AggregatedRateLimitController {

    /**
     * This endpoint inherits the rate limit from the controller class.
     */
    @GetMapping("/endpoint1")
    public ResponseEntity<Map<String, Object>> endpoint1() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This endpoint inherits the class-level rate limit (20 req/min)");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    /**
     * This endpoint inherits the rate limit from the controller class.
     */
    @GetMapping("/endpoint2")
    public ResponseEntity<Map<String, Object>> endpoint2() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This endpoint also inherits the class-level rate limit (20 req/min)");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    /**
     * This endpoint overrides the class-level rate limit with its own configuration.
     */
    @GetMapping("/override")
    @RateLimit(limit = 3, timeWindowSeconds = 60, type = RateLimitType.IP_BASED)
    public ResponseEntity<Map<String, Object>> overrideLimit() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This endpoint overrides the class-level rate limit with 3 req/min");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
} 