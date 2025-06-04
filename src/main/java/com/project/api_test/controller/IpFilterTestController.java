package com.project.api_test.controller;

import com.project.api_rate_limiter.annotation.RateLimit;
import com.project.api_rate_limiter.annotation.RateLimitType;
import com.project.api_rate_limiter.service.IpFilterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Test controller for IP whitelist and blacklist functionality.
 */
@RestController
@RequestMapping("/api/test/ip-filter")
public class IpFilterTestController {
    
    @Autowired
    private IpFilterService ipFilterService;
    
    /**
     * Test endpoint with very strict rate limit (1 req/min).
     * Whitelisted IPs will bypass this limit.
     */
    @GetMapping("/strict-limit")
    @RateLimit(limit = 1, timeWindowSeconds = 60, type = RateLimitType.IP_BASED)
    public ResponseEntity<Map<String, Object>> testStrictLimit(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        boolean whitelisted = ipFilterService.isWhitelisted(clientIp);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This endpoint has a strict rate limit (1 req/min)");
        response.put("ip", clientIp);
        response.put("whitelisted", whitelisted);
        response.put("note", whitelisted ? 
                "Your IP is whitelisted, bypassing rate limits" : 
                "Your IP is subject to the 1 req/min limit");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * This endpoint should be inaccessible to blacklisted IPs.
     */
    @GetMapping("/blacklist-test")
    public ResponseEntity<Map<String, Object>> testBlacklist(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        boolean blacklisted = ipFilterService.isBlacklisted(clientIp);
        
        // Note: Blacklisted IPs should be blocked by the filter before reaching this point
        // This is just for demonstration purposes
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "If you see this, your IP is not blacklisted");
        response.put("ip", clientIp);
        response.put("blacklisted", blacklisted); // Should always be false if this code executes
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Add the current client IP to the whitelist.
     */
    @GetMapping("/whitelist-me")
    public ResponseEntity<Map<String, Object>> whitelistMe(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        boolean added = ipFilterService.addToWhitelist(clientIp);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", added ? 
                "Your IP has been added to the whitelist" : 
                "Your IP was already whitelisted");
        response.put("ip", clientIp);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Remove the current client IP from the whitelist.
     */
    @GetMapping("/unwhitelist-me")
    public ResponseEntity<Map<String, Object>> unwhitelistMe(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        boolean removed = ipFilterService.removeFromWhitelist(clientIp);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", removed ? 
                "Your IP has been removed from the whitelist" : 
                "Your IP was not in the whitelist");
        response.put("ip", clientIp);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    // Helper method to get client IP
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
} 