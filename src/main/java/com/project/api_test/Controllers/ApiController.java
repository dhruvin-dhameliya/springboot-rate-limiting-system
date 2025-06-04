package com.project.api_test.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.api_rate_limiter.annotation.RateLimit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/hello")
    @RateLimit
    public String helloEndpoint() {
        return "Say Hello - default rate limiting settings";
    }

    @GetMapping("/limited")
    @RateLimit(value = "limited-endpoint", limit = 5, timeWindowSeconds = 30)
    public String limitedEndpoint() {
        return "This endpoint is limited to 5 requests per 30 seconds";
    }

    @PostMapping("/login")
    @RateLimit(value = "login", limit = 3, timeWindowSeconds = 60)
    public LoginResponse login(@RequestBody LoginRequest request) {
        return new LoginResponse("token-" + System.currentTimeMillis(), "User logged in successfully");
    }

    @GetMapping("/users/{userId}")
    @RateLimit(key = "#userId", value = "users", limit = 3, timeWindowSeconds = 60)
    public UserResponse getUserDetails(@PathVariable String userId) {
        return new UserResponse(userId, "Test User", "user@test.com");
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private String token;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private String id;
        private String name;
        private String email;
    }
} 