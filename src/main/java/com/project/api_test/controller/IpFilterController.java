package com.project.api_test.controller;

import com.project.api_rate_limiter.annotation.RateLimit;
import com.project.api_rate_limiter.annotation.RateLimitType;
import com.project.api_rate_limiter.service.IpFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Controller for managing IP whitelist and blacklist.
 */
@RestController
@RequestMapping("/api/ip-filter")
public class IpFilterController {

    @Autowired
    private IpFilterService ipFilterService;
    
    /**
     * Get all whitelisted IP addresses.
     */
    @GetMapping("/whitelist")
    public ResponseEntity<Set<String>> getWhitelist() {
        return ResponseEntity.ok(ipFilterService.getWhitelistedIps());
    }
    
    /**
     * Get all blacklisted IP addresses.
     */
    @GetMapping("/blacklist")
    public ResponseEntity<Set<String>> getBlacklist() {
        return ResponseEntity.ok(ipFilterService.getBlacklistedIps());
    }
    
    /**
     * Add an IP address to the whitelist.
     */
    @PostMapping("/whitelist/{ipAddress}")
    @RateLimit(limit = 10, timeWindowSeconds = 60, type = RateLimitType.IP_BASED)
    public ResponseEntity<Map<String, Object>> addToWhitelist(@PathVariable String ipAddress) {
        boolean added = ipFilterService.addToWhitelist(ipAddress);
        
        Map<String, Object> response = new HashMap<>();
        response.put("ip", ipAddress);
        response.put("added", added);
        response.put("message", added ? 
                "IP address added to whitelist" : 
                "IP address already in whitelist");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Remove an IP address from the whitelist.
     */
    @DeleteMapping("/whitelist/{ipAddress}")
    @RateLimit(limit = 10, timeWindowSeconds = 60, type = RateLimitType.IP_BASED)
    public ResponseEntity<Map<String, Object>> removeFromWhitelist(@PathVariable String ipAddress) {
        boolean removed = ipFilterService.removeFromWhitelist(ipAddress);
        
        Map<String, Object> response = new HashMap<>();
        response.put("ip", ipAddress);
        response.put("removed", removed);
        response.put("message", removed ? 
                "IP address removed from whitelist" : 
                "IP address not found in whitelist");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Add an IP address to the blacklist.
     */
    @PostMapping("/blacklist/{ipAddress}")
    @RateLimit(limit = 10, timeWindowSeconds = 60, type = RateLimitType.IP_BASED)
    public ResponseEntity<Map<String, Object>> addToBlacklist(@PathVariable String ipAddress) {
        boolean added = ipFilterService.addToBlacklist(ipAddress);
        
        Map<String, Object> response = new HashMap<>();
        response.put("ip", ipAddress);
        response.put("added", added);
        response.put("message", added ? 
                "IP address added to blacklist" : 
                "IP address already in blacklist");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Remove an IP address from the blacklist.
     */
    @DeleteMapping("/blacklist/{ipAddress}")
    @RateLimit(limit = 10, timeWindowSeconds = 60, type = RateLimitType.IP_BASED)
    public ResponseEntity<Map<String, Object>> removeFromBlacklist(@PathVariable String ipAddress) {
        boolean removed = ipFilterService.removeFromBlacklist(ipAddress);
        
        Map<String, Object> response = new HashMap<>();
        response.put("ip", ipAddress);
        response.put("removed", removed);
        response.put("message", removed ? 
                "IP address removed from blacklist" : 
                "IP address not found in blacklist");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get the current status of an IP address.
     */
    @GetMapping("/status/{ipAddress}")
    public ResponseEntity<Map<String, Object>> getIpStatus(@PathVariable String ipAddress) {
        boolean whitelisted = ipFilterService.isWhitelisted(ipAddress);
        boolean blacklisted = ipFilterService.isBlacklisted(ipAddress);
        
        Map<String, Object> response = new HashMap<>();
        response.put("ip", ipAddress);
        response.put("whitelisted", whitelisted);
        response.put("blacklisted", blacklisted);
        
        String status;
        if (whitelisted) {
            status = "Whitelisted: This IP bypasses rate limiting";
        } else if (blacklisted) {
            status = "Blacklisted: This IP is blocked from accessing the API";
        } else {
            status = "Normal: Standard rate limiting applied";
        }
        
        response.put("status", status);
        return ResponseEntity.ok(response);
    }
} 