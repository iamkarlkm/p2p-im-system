package com.im.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheHealthIndicator implements HealthIndicator {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public Health health() {
        try {
            String testKey = "health:check:" + System.currentTimeMillis();
            String testValue = "healthy";
            
            redisTemplate.opsForValue().set(testKey, testValue, 5, TimeUnit.SECONDS);
            Object retrieved = redisTemplate.opsForValue().get(testKey);
            
            if (testValue.equals(retrieved)) {
                redisTemplate.delete(testKey);
                return Health.up()
                        .withDetail("cache", "Redis")
                        .withDetail("connection", "active")
                        .withDetail("read_write", "success")
                        .build();
            } else {
                return Health.down()
                        .withDetail("cache", "Redis")
                        .withDetail("issue", "Read/write mismatch")
                        .build();
            }
        } catch (Exception e) {
            log.error("Cache health check failed", e);
            return Health.down(e)
                    .withDetail("cache", "Redis")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
