package com.im.backend.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsCollector {
    
    private final MeterRegistry meterRegistry;
    private final ConcurrentHashMap<String, AtomicLong> gaugeMap = new ConcurrentHashMap<>();
    
    private Counter messageSentCounter;
    private Counter messageReceivedCounter;
    private Counter userOnlineCounter;
    private Counter userOfflineCounter;
    private Timer messageProcessingTimer;
    private Timer databaseQueryTimer;
    private Counter apiRequestCounter;
    private Counter apiErrorCounter;
    
    @PostConstruct
    public void init() {
        messageSentCounter = Counter.builder("im.messages.sent")
                .description("Total messages sent")
                .tag("type", "all")
                .register(meterRegistry);
        
        messageReceivedCounter = Counter.builder("im.messages.received")
                .description("Total messages received")
                .tag("type", "all")
                .register(meterRegistry);
        
        userOnlineCounter = Counter.builder("im.users.online")
                .description("Total users online events")
                .register(meterRegistry);
        
        userOfflineCounter = Counter.builder("im.users.offline")
                .description("Total users offline events")
                .register(meterRegistry);
        
        messageProcessingTimer = Timer.builder("im.message.processing.time")
                .description("Time spent processing messages")
                .publishPercentiles(0.5, 0.9, 0.95, 0.99)
                .register(meterRegistry);
        
        databaseQueryTimer = Timer.builder("im.database.query.time")
                .description("Time spent on database queries")
                .publishPercentiles(0.5, 0.9, 0.95, 0.99)
                .register(meterRegistry);
        
        apiRequestCounter = Counter.builder("im.api.requests")
                .description("Total API requests")
                .tag("type", "all")
                .register(meterRegistry);
        
        apiErrorCounter = Counter.builder("im.api.errors")
                .description("Total API errors")
                .tag("type", "all")
                .register(meterRegistry);
        
        initGauges();
    }
    
    private void initGauges() {
        AtomicLong activeConnections = new AtomicLong(0);
        gaugeMap.put("active_connections", activeConnections);
        Gauge.builder("im.connections.active", activeConnections, AtomicLong::get)
                .description("Active connections count")
                .register(meterRegistry);
        
        AtomicLong queuedMessages = new AtomicLong(0);
        gaugeMap.put("queued_messages", queuedMessages);
        Gauge.builder("im.messages.queued", queuedMessages, AtomicLong::get)
                .description("Queued messages count")
                .register(meterRegistry);
        
        AtomicLong activeConversations = new AtomicLong(0);
        gaugeMap.put("active_conversations", activeConversations);
        Gauge.builder("im.conversations.active", activeConversations, AtomicLong::get)
                .description("Active conversations count")
                .register(meterRegistry);
        
        AtomicLong totalUsers = new AtomicLong(0);
        gaugeMap.put("total_users", totalUsers);
        Gauge.builder("im.users.total", totalUsers, AtomicLong::get)
                .description("Total users count")
                .register(meterRegistry);
    }
    
    public void recordMessageSent(String messageType) {
        messageSentCounter.increment();
        Counter.builder("im.messages.sent.by.type")
                .tag("type", messageType)
                .register(meterRegistry)
                .increment();
    }
    
    public void recordMessageReceived(String messageType) {
        messageReceivedCounter.increment();
        Counter.builder("im.messages.received.by.type")
                .tag("type", messageType)
                .register(meterRegistry)
                .increment();
    }
    
    public void recordUserOnline() {
        userOnlineCounter.increment();
        updateActiveUsersGauge(1);
    }
    
    public void recordUserOffline() {
        userOfflineCounter.increment();
        updateActiveUsersGauge(-1);
    }
    
    public void recordMessageProcessingTime(long durationMs) {
        messageProcessingTimer.record(durationMs, TimeUnit.MILLISECONDS);
    }
    
    public void recordDatabaseQueryTime(long durationMs) {
        databaseQueryTimer.record(durationMs, TimeUnit.MILLISECONDS);
    }
    
    public void recordApiRequest(String endpoint, String method) {
        apiRequestCounter.increment();
        Counter.builder("im.api.requests.by.endpoint")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .register(meterRegistry)
                .increment();
    }
    
    public void recordApiError(String endpoint, String method, String errorType) {
        apiErrorCounter.increment();
        Counter.builder("im.api.errors.by.endpoint")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .tag("error_type", errorType)
                .register(meterRegistry)
                .increment();
    }
    
    public void updateActiveConnections(int delta) {
        AtomicLong gauge = gaugeMap.get("active_connections");
        if (gauge != null) {
            gauge.addAndGet(delta);
        }
    }
    
    public void updateQueuedMessages(int delta) {
        AtomicLong gauge = gaugeMap.get("queued_messages");
        if (gauge != null) {
            gauge.addAndGet(delta);
        }
    }
    
    public void updateActiveConversations(int delta) {
        AtomicLong gauge = gaugeMap.get("active_conversations");
        if (gauge != null) {
            gauge.addAndGet(delta);
        }
    }
    
    private void updateActiveUsersGauge(int delta) {
        AtomicLong gauge = gaugeMap.computeIfAbsent("active_users", k -> new AtomicLong(0));
        gauge.addAndGet(delta);
        
        Gauge.builder("im.users.active", gauge, AtomicLong::get)
                .description("Active users count")
                .register(meterRegistry);
    }
    
    public void setTotalUsers(long count) {
        AtomicLong gauge = gaugeMap.get("total_users");
        if (gauge != null) {
            gauge.set(count);
        }
    }
    
    public void recordWebSocketEvent(String eventType) {
        Counter.builder("im.websocket.events")
                .tag("event_type", eventType)
                .register(meterRegistry)
                .increment();
    }
    
    public void recordKafkaEvent(String topic, String eventType) {
        Counter.builder("im.kafka.events")
                .tag("topic", topic)
                .tag("event_type", eventType)
                .register(meterRegistry)
                .increment();
    }
    
    public void recordCacheHit(String cacheName) {
        Counter.builder("im.cache.hits")
                .tag("cache_name", cacheName)
                .register(meterRegistry)
                .increment();
    }
    
    public void recordCacheMiss(String cacheName) {
        Counter.builder("im.cache.misses")
                .tag("cache_name", cacheName)
                .register(meterRegistry)
                .increment();
    }
}
