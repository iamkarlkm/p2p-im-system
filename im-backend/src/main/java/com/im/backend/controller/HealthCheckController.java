package com.im.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/actuator")
@RequiredArgsConstructor
public class HealthCheckController {
    
    private final DatabaseHealthIndicator databaseHealthIndicator;
    private final CacheHealthIndicator cacheHealthIndicator;
    private final KafkaHealthIndicator kafkaHealthIndicator;
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> healthStatus = new HashMap<>();
        healthStatus.put("status", "UP");
        healthStatus.put("timestamp", System.currentTimeMillis());
        
        Map<String, Object> components = new HashMap<>();
        
        Health dbHealth = databaseHealthIndicator.health();
        components.put("database", Map.of(
                "status", dbHealth.getStatus().getCode(),
                "details", dbHealth.getDetails()
        ));
        
        Health cacheHealth = cacheHealthIndicator.health();
        components.put("cache", Map.of(
                "status", cacheHealth.getStatus().getCode(),
                "details", cacheHealth.getDetails()
        ));
        
        Health kafkaHealth = kafkaHealthIndicator.health();
        components.put("kafka", Map.of(
                "status", kafkaHealth.getStatus().getCode(),
                "details", kafkaHealth.getDetails()
        ));
        
        healthStatus.put("components", components);
        
        boolean allUp = components.values().stream()
                .allMatch(c -> ((Map<?, ?>) c).get("status").equals("UP"));
        
        healthStatus.put("status", allUp ? "UP" : "DOWN");
        
        return ResponseEntity.ok(healthStatus);
    }
    
    @GetMapping("/health/live")
    public ResponseEntity<Map<String, String>> liveness() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
    
    @GetMapping("/health/ready")
    public ResponseEntity<Map<String, Object>> readiness() {
        Map<String, Object> readiness = new HashMap<>();
        
        boolean dbReady = databaseHealthIndicator.health().getStatus().getCode().equals("UP");
        boolean cacheReady = cacheHealthIndicator.health().getStatus().getCode().equals("UP");
        
        readiness.put("database", dbReady);
        readiness.put("cache", cacheReady);
        readiness.put("status", (dbReady && cacheReady) ? "READY" : "NOT_READY");
        
        if (dbReady && cacheReady) {
            return ResponseEntity.ok(readiness);
        } else {
            return ResponseEntity.status(503).body(readiness);
        }
    }
    
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "im-backend");
        info.put("version", "1.0.0");
        info.put("java_version", System.getProperty("java.version"));
        info.put("java_vendor", System.getProperty("java.vendor"));
        info.put("os_name", System.getProperty("os.name"));
        info.put("os_version", System.getProperty("os.version"));
        info.put("available_processors", Runtime.getRuntime().availableProcessors());
        
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> memory = new HashMap<>();
        memory.put("total_memory", runtime.totalMemory());
        memory.put("free_memory", runtime.freeMemory());
        memory.put("max_memory", runtime.maxMemory());
        memory.put("used_memory", runtime.totalMemory() - runtime.freeMemory());
        info.put("memory", memory);
        
        return ResponseEntity.ok(info);
    }
}
