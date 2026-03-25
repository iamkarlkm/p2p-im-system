package com.im.backend.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/actuator/metrics")
@RequiredArgsConstructor
public class MetricsEndpoint {
    
    private final MeterRegistry meterRegistry;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> listMetrics() {
        Map<String, Object> response = new HashMap<>();
        List<String> metricNames = new ArrayList<>();
        
        meterRegistry.getMeters().forEach(meter -> {
            String name = meter.getId().getName();
            if (!metricNames.contains(name)) {
                metricNames.add(name);
            }
        });
        
        response.put("metrics", metricNames);
        response.put("count", metricNames.size());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{metricName}")
    public ResponseEntity<Map<String, Object>> getMetric(@PathVariable String metricName) {
        Map<String, Object> response = new HashMap<>();
        response.put("name", metricName);
        
        List<Map<String, Object>> measurements = new ArrayList<>();
        
        meterRegistry.getMeters().stream()
                .filter(meter -> meter.getId().getName().equals(metricName))
                .forEach(meter -> {
                    Map<String, Object> measurement = new HashMap<>();
                    measurement.put("type", meter.getClass().getSimpleName());
                    
                    List<Map<String, String>> tags = new ArrayList<>();
                    for (Tag tag : meter.getId().getTags()) {
                        Map<String, String> tagMap = new HashMap<>();
                        tagMap.put(tag.getKey(), tag.getValue());
                        tags.add(tagMap);
                    }
                    measurement.put("tags", tags);
                    
                    meter.measure().forEach(m -> {
                        measurement.put(m.getStatistic().toString(), m.getValue());
                    });
                    
                    measurements.add(measurement);
                });
        
        if (measurements.isEmpty()) {
            response.put("status", "not_found");
            return ResponseEntity.status(404).body(response);
        }
        
        response.put("measurements", measurements);
        response.put("status", "found");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/jvm")
    public ResponseEntity<Map<String, Object>> getJvmMetrics() {
        Map<String, Object> jvmMetrics = new HashMap<>();
        
        Runtime runtime = Runtime.getRuntime();
        jvmMetrics.put("total_memory", runtime.totalMemory());
        jvmMetrics.put("free_memory", runtime.freeMemory());
        jvmMetrics.put("max_memory", runtime.maxMemory());
        jvmMetrics.put("used_memory", runtime.totalMemory() - runtime.freeMemory());
        jvmMetrics.put("available_processors", runtime.availableProcessors());
        
        Map<String, Object> memoryUsage = new HashMap<>();
        memoryUsage.put("heap_used", runtime.totalMemory() - runtime.freeMemory());
        memoryUsage.put("heap_max", runtime.maxMemory());
        memoryUsage.put("usage_percent", 
                (double) (runtime.totalMemory() - runtime.freeMemory()) / runtime.maxMemory() * 100);
        
        jvmMetrics.put("memory_usage", memoryUsage);
        
        return ResponseEntity.ok(jvmMetrics);
    }
    
    @GetMapping("/system")
    public ResponseEntity<Map<String, Object>> getSystemMetrics() {
        Map<String, Object> systemMetrics = new HashMap<>();
        
        com.sun.management.OperatingSystemMXBean osBean = 
                (com.sun.management.OperatingSystemMXBean) 
                java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        
        systemMetrics.put("os_name", System.getProperty("os.name"));
        systemMetrics.put("os_version", System.getProperty("os.version"));
        systemMetrics.put("os_arch", System.getProperty("os.arch"));
        systemMetrics.put("cpu_load", osBean.getSystemCpuLoad() * 100);
        systemMetrics.put("process_cpu_load", osBean.getProcessCpuLoad() * 100);
        systemMetrics.put("total_physical_memory", osBean.getTotalPhysicalMemorySize());
        systemMetrics.put("free_physical_memory", osBean.getFreePhysicalMemorySize());
        systemMetrics.put("available_processors", osBean.getAvailableProcessors());
        
        return ResponseEntity.ok(systemMetrics);
    }
    
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getMetricsSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        summary.put("timestamp", System.currentTimeMillis());
        summary.put("uptime_seconds", java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime() / 1000);
        
        Map<String, Object> jvm = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        jvm.put("memory_used_mb", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024);
        jvm.put("memory_max_mb", runtime.maxMemory() / 1024 / 1024);
        jvm.put("processors", runtime.availableProcessors());
        summary.put("jvm", jvm);
        
        Map<String, Object> system = new HashMap<>();
        com.sun.management.OperatingSystemMXBean osBean = 
                (com.sun.management.OperatingSystemMXBean) 
                java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        system.put("cpu_load_percent", Math.round(osBean.getSystemCpuLoad() * 10000) / 100.0);
        system.put("available_processors", osBean.getAvailableProcessors());
        summary.put("system", system);
        
        Map<String, Integer> meterCounts = new HashMap<>();
        meterRegistry.getMeters().forEach(meter -> {
            String type = meter.getClass().getSimpleName();
            meterCounts.put(type, meterCounts.getOrDefault(type, 0) + 1);
        });
        summary.put("meter_counts", meterCounts);
        summary.put("total_meters", meterRegistry.getMeters().size());
        
        return ResponseEntity.ok(summary);
    }
}
