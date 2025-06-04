package com.project.api_test.controller;

import com.project.api_rate_limiter.annotation.RateLimit;
import com.project.api_rate_limiter.annotation.RateLimitType;
import com.project.api_rate_limiter.model.ApiKey;
import com.project.api_rate_limiter.service.ApiKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * Test controller for demonstrating various rate limiting features.
 */
@RestController
@RequestMapping("/api/v1")
public class RateLimitTestController {

    @Autowired
    private ApiKeyService apiKeyService;
    
    /**
     * Default rate limiting test.
     * Uses the global rate limit configuration.
     */
    @GetMapping("/default")
    public ResponseEntity<Map<String, Object>> testDefaultRateLimit() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This endpoint uses the default global rate limit");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint-specific rate limiting test.
     * Uses the rate limit specified in the annotation.
     */
    @GetMapping("/endpoint-specific")
    @RateLimit(limit = 5, timeWindowSeconds = 60, type = RateLimitType.ENDPOINT_BASED)
    public ResponseEntity<Map<String, Object>> testEndpointSpecificRateLimit() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This endpoint has a specific rate limit of 5 requests per minute");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    /**
     * IP-based rate limiting test.
     * Limits requests based on client IP address.
     */
    @GetMapping("/ip-based")
    @RateLimit(limit = 3, timeWindowSeconds = 60, type = RateLimitType.IP_BASED)
    public ResponseEntity<Map<String, Object>> testIpBasedRateLimit() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This endpoint is limited to 3 requests per minute per IP address");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    /**
     * User-based rate limiting test.
     * Limits requests based on authenticated user.
     */
    @GetMapping("/user-based")
    @RateLimit(limit = 2, timeWindowSeconds = 60, type = RateLimitType.USER_BASED)
    public ResponseEntity<Map<String, Object>> testUserBasedRateLimit(Principal principal) {
        Map<String, Object> response = new HashMap<>();
        String username = principal != null ? principal.getName() : "anonymous";
        
        response.put("message", "This endpoint is limited to 2 requests per minute per user");
        response.put("user", username);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    /**
     * API key-based rate limiting test.
     * Limits requests based on API key from header.
     */
    @GetMapping("/api-key-based")
    @RateLimit(limit = 10, timeWindowSeconds = 60, type = RateLimitType.API_KEY_BASED)
    public ResponseEntity<Map<String, Object>> testApiKeyBasedRateLimit() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This endpoint is limited by API key (send X-API-Key header)");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    /**
     * HTTP method-specific rate limiting test.
     * Different rate limits for different HTTP methods.
     */
    @GetMapping("/method-specific")
    @RateLimit(limit = 5, timeWindowSeconds = 60, methods = {"GET"}, type = RateLimitType.METHOD_BASED)
    public ResponseEntity<Map<String, Object>> testMethodSpecificRateLimitGet() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "GET method is limited to 5 requests per minute");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/method-specific")
    @RateLimit(limit = 3, timeWindowSeconds = 60, methods = {"POST"}, type = RateLimitType.METHOD_BASED)
    public ResponseEntity<Map<String, Object>> testMethodSpecificRateLimitPost() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "POST method is limited to 3 requests per minute");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/method-specific")
    @RateLimit(limit = 2, timeWindowSeconds = 60, methods = {"PUT"}, type = RateLimitType.METHOD_BASED)
    public ResponseEntity<Map<String, Object>> testMethodSpecificRateLimitPut() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "PUT method is limited to 2 requests per minute");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/method-specific")
    @RateLimit(limit = 1, timeWindowSeconds = 60, methods = {"DELETE"}, type = RateLimitType.METHOD_BASED)
    public ResponseEntity<Map<String, Object>> testMethodSpecificRateLimitDelete() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "DELETE method is limited to 1 request per minute");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    /**
     * DDoS protection test.
     * If too many requests are made, the IP will be temporarily banned.
     */
    @GetMapping("/ddos-protection")
    @RateLimit(limit = 5, timeWindowSeconds = 60, type = RateLimitType.IP_BASED, 
              ddosProtection = true, ddosThreshold = 10, ddosBanDurationSeconds = 120)
    public ResponseEntity<Map<String, Object>> testDdosProtection() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This endpoint has DDoS protection (10+ requests will ban your IP for 2 minutes)");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Generates an API key for testing.
     */
    @PostMapping("/generate-api-key")
    public ResponseEntity<ApiKey> generateApiKey() {
        ApiKey apiKey = apiKeyService.generateApiKey(
                "test-user", 
                5,  // Rate limit of 5 requests
                60, // Per minute
                1   // Expires in 1 day
        );
        return ResponseEntity.ok(apiKey);
    }
    
    /**
     * Revokes an API key.
     */
    @PostMapping("/revoke-api-key")
    public ResponseEntity<Map<String, Object>> revokeApiKey(String key) {
        boolean revoked = apiKeyService.revokeApiKey(key);
        
        Map<String, Object> response = new HashMap<>();
        response.put("revoked", revoked);
        response.put("message", revoked ? "API key revoked successfully" : "API key not found or already revoked");
        
        return ResponseEntity.ok(response);
    }
} 