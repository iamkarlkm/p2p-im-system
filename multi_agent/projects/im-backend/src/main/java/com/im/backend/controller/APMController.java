package com.im.backend.controller;

import com.im.backend.service.MetricsCollector;
import io.micrometer.core.instrument.MeterRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import java.io.StringWriter;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/apm")
@RequiredArgsConstructor
public class APMController {
    
    private final MeterRegistry meterRegistry;
    private final MetricsCollector metricsCollector;
    private final Tracer tracer;
    
    @GetMapping("/metrics")
    public ResponseEntity<String> getMetrics() {
        metricsCollector.recordApiRequest("/apm/metrics", "GET");
        
        try {
            StringWriter writer = new StringWriter();
            io.micrometer.prometheus.PrometheusMeterRegistry prometheusRegistry = 
                    (io.micrometer.prometheus.PrometheusMeterRegistry) meterRegistry;
            prometheusRegistry.scrape(writer);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(TextFormat.CONTENT_TYPE_004))
                    .body(writer.toString());
        } catch (Exception e) {
            metricsCollector.recordApiError("/apm/metrics", "GET", "metrics_error");
            log.error("Failed to generate metrics", e);
            return ResponseEntity.internalServerError()
                    .body("Error generating metrics: " + e.getMessage());
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        metricsCollector.recordApiRequest("/apm/health", "GET");
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "healthy");
        health.put("service", "im-backend");
        health.put("timestamp", System.currentTimeMillis());
        
        Span span = tracer.spanBuilder("apm.health.check").startSpan();
        try {
            span.setAttribute("check_type", "full");
            span.setAttribute("service", "im-backend");
            
            health.put("tracing_enabled", true);
            health.put("metrics_enabled", true);
            
            Runtime runtime = Runtime.getRuntime();
            Map<String, Object> jvm = new HashMap<>();
            jvm.put("total_memory", runtime.totalMemory());
            jvm.put("free_memory", runtime.freeMemory());
            jvm.put("max_memory", runtime.maxMemory());
            jvm.put("available_processors", runtime.availableProcessors());
            health.put("jvm", jvm);
            
            return ResponseEntity.ok(health);
        } finally {
            span.end();
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        metricsCollector.recordApiRequest("/apm/stats", "GET");
        
        Span span = tracer.spanBuilder("apm.stats.collect").startSpan();
        try {
            Map<String, Object> stats = new HashMap<>();
            
            Map<String, Object> messageStats = new HashMap<>();
            messageStats.put("total_sent", getCounterValue("im.messages.sent"));
            messageStats.put("total_received", getCounterValue("im.messages.received"));
            stats.put("messages", messageStats);
            
            Map<String, Object> userStats = new HashMap<>();
            userStats.put("total_online", getCounterValue("im.users.online"));
            userStats.put("total_offline", getCounterValue("im.users.offline"));
            stats.put("users", userStats);
            
            Map<String, Object> apiStats = new HashMap<>();
            apiStats.put("total_requests", getCounterValue("im.api.requests"));
            apiStats.put("total_errors", getCounterValue("im.api.errors"));
            stats.put("api", apiStats);
            
            return ResponseEntity.ok(stats);
        } finally {
            span.end();
        }
    }
    
    @PostMapping("/test-trace")
    public ResponseEntity<Map<String, Object>> testTrace(@RequestBody Map<String, String> request) {
        String operation = request.get("operation");
        if (operation == null) {
            operation = "test_operation";
        }
        
        Span span = tracer.spanBuilder("apm.test.trace")
                .setAttribute("operation", operation)
                .setAttribute("test", true)
                .startSpan();
        
        try {
            span.addEvent("Starting test operation");
            
            Thread.sleep(100);
            
            Span childSpan = tracer.spanBuilder("apm.test.child.operation")
                    .setParent(span)
                    .startSpan();
            
            try {
                childSpan.setAttribute("child", true);
                Thread.sleep(50);
                
                Map<String, Object> result = new HashMap<>();
                result.put("status", "success");
                result.put("operation", operation);
                result.put("trace_id", span.getSpanContext().getTraceId());
                result.put("span_id", span.getSpanContext().getSpanId());
                result.put("timestamp", System.currentTimeMillis());
                
                return ResponseEntity.ok(result);
            } finally {
                childSpan.end();
            }
        } catch (InterruptedException e) {
            span.recordException(e);
            Thread.currentThread().interrupt();
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Trace test interrupted");
            return ResponseEntity.internalServerError().body(error);
        } finally {
            span.end();
        }
    }
    
    @PostMapping("/record-metric")
    public ResponseEntity<Map<String, Object>> recordMetric(@RequestBody Map<String, Object> request) {
        String metricName = (String) request.get("metric_name");
        String metricType = (String) request.get("metric_type");
        Object value = request.get("value");
        
        if (metricName == null || metricType == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "metric_name and metric_type are required");
            return ResponseEntity.badRequest().body(error);
        }
        
        Span span = tracer.spanBuilder("apm.metric.record")
                .setAttribute("metric_name", metricName)
                .setAttribute("metric_type", metricType)
                .startSpan();
        
        try {
            log.info("Recording custom metric: {}={} type={}", metricName, value, metricType);
            
            switch (metricType.toLowerCase()) {
                case "counter":
                    if (value instanceof Number) {
                        metricsCollector.recordMessageSent(metricName);
                    }
                    break;
                case "gauge":
                    if (value instanceof Number) {
                        metricsCollector.updateActiveConnections(((Number) value).intValue());
                    }
                    break;
                default:
                    log.warn("Unknown metric type: {}", metricType);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("metric_name", metricName);
            result.put("metric_type", metricType);
            result.put("value", value);
            
            return ResponseEntity.ok(result);
        } finally {
            span.end();
        }
    }
    
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("tracing_enabled", true);
        config.put("metrics_enabled", true);
        config.put("prometheus_endpoint", "/apm/metrics");
        config.put("health_endpoint", "/apm/health");
        config.put("stats_endpoint", "/apm/stats");
        config.put("service_name", "im-backend");
        
        return ResponseEntity.ok(config);
    }
    
    private double getCounterValue(String counterName) {
        try {
            return meterRegistry.counter(counterName).count();
        } catch (Exception e) {
            log.warn("Failed to get counter value for {}", counterName, e);
            return 0.0;
        }
    }
}
