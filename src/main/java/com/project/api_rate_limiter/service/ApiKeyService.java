package com.project.api_rate_limiter.service;

import com.project.api_rate_limiter.model.ApiKey;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ApiKeyService {
    
    private final Map<String, ApiKey> apiKeys = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    public ApiKey generateApiKey(String owner, int rateLimit, int timeWindowSeconds, int expiryDays) {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String key = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        
        LocalDateTime expiresAt = expiryDays > 0 
                ? LocalDateTime.now().plusDays(expiryDays) 
                : null;
        
        ApiKey apiKey = new ApiKey(
                key,
                owner,
                rateLimit,
                timeWindowSeconds,
                true,
                LocalDateTime.now(),
                expiresAt
        );
        
        apiKeys.put(key, apiKey);
        return apiKey;
    }

    public boolean validateApiKey(String key) {
        ApiKey apiKey = apiKeys.get(key);
        if (apiKey == null || !apiKey.isEnabled()) {
            return false;
        }

        return apiKey.getExpiresAt() == null || !apiKey.getExpiresAt().isBefore(LocalDateTime.now()); // Key has expired
    }

    public ApiKey getApiKey(String key) {
        return apiKeys.get(key);
    }

    public boolean revokeApiKey(String key) {
        ApiKey apiKey = apiKeys.get(key);
        if (apiKey != null) {
            apiKey.setEnabled(false);
            return true;
        }
        return false;
    }
} 