package com.project.api_rate_limiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class ApiRateLimiterApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiRateLimiterApplication.class, args);
	}

}
