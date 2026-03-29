package com.im.system.controller;

import com.im.system.entity.BotWebSocketEventEntity;
import com.im.system.service.BotWebSocketEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bot-websocket-events")
@CrossOrigin(origins = "*")
public class BotWebSocketEventController {

    private final BotWebSocketEventService botWebSocketEventService;

    @Autowired
    public BotWebSocketEventController(BotWebSocketEventService botWebSocketEventService) {
        this.botWebSocketEventService = botWebSocketEventService;
    }

    // Basic CRUD operations
    @PostMapping
    public ResponseEntity<BotWebSocketEventEntity> createEvent(@RequestBody BotWebSocketEventEntity event) {
        BotWebSocketEventEntity createdEvent = botWebSocketEventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BotWebSocketEventEntity> getEventById(@PathVariable UUID id) {
        return botWebSocketEventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/bot/{botId}")
    public ResponseEntity<BotWebSocketEventEntity> getEventByIdAndBotId(@PathVariable UUID id, @PathVariable UUID botId) {
        return botWebSocketEventService.getEventByIdAndBotId(id, botId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BotWebSocketEventEntity> updateEvent(@PathVariable UUID id, @RequestBody BotWebSocketEventEntity event) {
        event.setId(id);
        BotWebSocketEventEntity updatedEvent = botWebSocketEventService.updateEvent(event);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        botWebSocketEventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/bot/{botId}")
    public ResponseEntity<Void> deleteEventByIdAndBotId(@PathVariable UUID id, @PathVariable UUID botId) {
        botWebSocketEventService.deleteEventByIdAndBotId(id, botId);
        return ResponseEntity.noContent().build();
    }

    // Query operations
    @GetMapping("/bot/{botId}")
    public ResponseEntity<List<BotWebSocketEventEntity>> getEventsByBotId(
            @PathVariable UUID botId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String eventType) {
        
        List<BotWebSocketEventEntity> events;
        if (status != null) {
            events = botWebSocketEventService.getEventsByBotIdAndStatus(botId, status);
        } else if (eventType != null) {
            events = botWebSocketEventService.getEventsByEventType(eventType);
        } else {
            events = botWebSocketEventService.getEventsByBotId(botId);
        }
        return ResponseEntity.ok(events);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<BotWebSocketEventEntity>> getEventsBySessionId(
            @PathVariable UUID sessionId,
            @RequestParam(required = false) String status) {
        
        List<BotWebSocketEventEntity> events = status != null 
                ? botWebSocketEventService.getEventsBySessionIdAndStatus(sessionId, status)
                : botWebSocketEventService.getEventsBySessionId(sessionId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/type/{eventType}")
    public ResponseEntity<List<BotWebSocketEventEntity>> getEventsByEventType(
            @PathVariable String eventType,
            @RequestParam(required = false) String status) {
        
        List<BotWebSocketEventEntity> events = botWebSocketEventService.getEventsByEventType(eventType);
        if (status != null) {
            events = events.stream()
                    .filter(e -> status.equals(e.getStatus()))
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(events);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BotWebSocketEventEntity>> getEventsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(botWebSocketEventService.getEventsByStatus(status));
    }

    @GetMapping("/bot/{botId}/status/{status}")
    public ResponseEntity<List<BotWebSocketEventEntity>> getEventsByBotIdAndStatus(
            @PathVariable UUID botId,
            @PathVariable String status) {
        return ResponseEntity.ok(botWebSocketEventService.getEventsByBotIdAndStatus(botId, status));
    }

    // Processing endpoints
    @GetMapping("/ready")
    public ResponseEntity<List<BotWebSocketEventEntity>> getReadyForProcessing() {
        return ResponseEntity.ok(botWebSocketEventService.getReadyForProcessing());
    }

    @GetMapping("/bot/{botId}/pending")
    public ResponseEntity<List<BotWebSocketEventEntity>> getPendingEventsByBotId(@PathVariable UUID botId) {
        return ResponseEntity.ok(botWebSocketEventService.getPendingEventsByBotId(botId));
    }

    @GetMapping("/retryable")
    public ResponseEntity<List<BotWebSocketEventEntity>> getRetryableFailedEvents() {
        return ResponseEntity.ok(botWebSocketEventService.getRetryableFailedEvents());
    }

    @PostMapping("/batch/retry")
    public ResponseEntity<Void> markEventsForRetry(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<String> eventIds = (List<String>) request.get("eventIds");
        String nextRetryAt = (String) request.get("nextRetryAt");
        
        botWebSocketEventService.markEventsForRetry(
                eventIds.stream().map(UUID::fromString).collect(Collectors.toList()),
                LocalDateTime.parse(nextRetryAt)
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/batch/delivered")
    public ResponseEntity<Void> markEventsAsDelivered(@RequestBody Map<String, List<String>> request) {
        List<UUID> eventIds = request.get("eventIds").stream().map(UUID::fromString).collect(Collectors.toList());
        botWebSocketEventService.markEventsAsDelivered(eventIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/batch/acknowledged")
    public ResponseEntity<Void> markEventsAsAcknowledged(@RequestBody Map<String, List<String>> request) {
        List<UUID> eventIds = request.get("eventIds").stream().map(UUID::fromString).collect(Collectors.toList());
        botWebSocketEventService.markEventsAsAcknowledged(eventIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/batch/processed")
    public ResponseEntity<Void> markEventsAsProcessed(@RequestBody Map<String, List<String>> request) {
        List<UUID> eventIds = request.get("eventIds").stream().map(UUID::fromString).collect(Collectors.toList());
        botWebSocketEventService.markEventsAsProcessed(eventIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/batch/failed")
    public ResponseEntity<Void> markEventsAsFailed(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<String> eventIds = (List<String>) request.get("eventIds");
        String errorMessage = (String) request.get("errorMessage");
        
        botWebSocketEventService.markEventsAsFailed(
                eventIds.stream().map(UUID::fromString).collect(Collectors.toList()),
                errorMessage
        );
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/webhook-response")
    public ResponseEntity<Void> updateWebhookResponse(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> request) {
        Integer responseCode = (Integer) request.get("responseCode");
        String responseBody = (String) request.get("responseBody");
        
        botWebSocketEventService.updateWebhookResponse(id, responseCode, responseBody);
        return ResponseEntity.ok().build();
    }

    // Cleanup endpoints
    @DeleteMapping("/cleanup/acknowledged")
    public ResponseEntity<Map<String, Integer>> cleanupOldAcknowledgedEvents(
            @RequestParam UUID botId,
            @RequestParam String threshold) {
        int deletedCount = botWebSocketEventService.cleanupOldAcknowledgedEvents(
                botId, LocalDateTime.parse(threshold)
        );
        Map<String, Integer> response = new HashMap<>();
        response.put("deletedCount", deletedCount);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/cleanup/failed")
    public ResponseEntity<Map<String, Integer>> cleanupPermanentlyFailedEvents(
            @RequestParam String threshold) {
        int deletedCount = botWebSocketEventService.cleanupPermanentlyFailedEvents(
                LocalDateTime.parse(threshold)
        );
        Map<String, Integer> response = new HashMap<>();
        response.put("deletedCount", deletedCount);
        return ResponseEntity.ok(response);
    }

    // Statistics endpoints
    @GetMapping("/bot/{botId}/status/{status}/count")
    public ResponseEntity<Map<String, Long>> countEventsByBotIdAndStatus(
            @PathVariable UUID botId,
            @PathVariable String status) {
        Long count = botWebSocketEventService.countEventsByBotIdAndStatus(botId, status);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bot/{botId}/failed/permanent/count")
    public ResponseEntity<Map<String, Long>> countPermanentlyFailedEventsByBot(@PathVariable UUID botId) {
        Long count = botWebSocketEventService.countPermanentlyFailedEventsByBot(botId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bot/{botId}/stats/status")
    public ResponseEntity<List<Map<String, Object>>> getEventStatusCountsForBot(@PathVariable UUID botId) {
        List<Object[]> results = botWebSocketEventService.getEventStatusCountsForBot(botId);
        List<Map<String, Object>> response = results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("status", row[0]);
            map.put("count", row[1]);
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bot/{botId}/stats/type")
    public ResponseEntity<List<Map<String, Object>>> getEventTypeCountsForBot(@PathVariable UUID botId) {
        List<Object[]> results = botWebSocketEventService.getEventTypeCountsForBot(botId);
        List<Map<String, Object>> response = results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("eventType", row[0]);
            map.put("count", row[1]);
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bot/{botId}/stats/daily")
    public ResponseEntity<List<Map<String, Object>>> getEventDayCountsForBot(
            @PathVariable UUID botId,
            @RequestParam String startDate) {
        List<Object[]> results = botWebSocketEventService.getEventDayCountsForBot(
                botId, LocalDateTime.parse(startDate)
        );
        List<Map<String, Object>> response = results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("date", row[0]);
            map.put("count", row[1]);
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Health check endpoints
    @GetMapping("/bot/{botId}/health")
    public ResponseEntity<Map<String, Boolean>> isBotEventQueueHealthy(@PathVariable UUID botId) {
        boolean healthy = botWebSocketEventService.isBotEventQueueHealthy(botId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("healthy", healthy);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bot/{botId}/queue-status")
    public ResponseEntity<Map<String, String>> getBotEventQueueStatus(@PathVariable UUID botId) {
        String status = botWebSocketEventService.getBotEventQueueStatus(botId);
        Map<String, String> response = new HashMap<>();
        response.put("status", status);
        return ResponseEntity.ok(response);
    }

    // Additional query endpoints
    @GetMapping("/bot/{botId}/event-types")
    public ResponseEntity<List<String>> getDistinctEventTypesByBot(@PathVariable UUID botId) {
        return ResponseEntity.ok(botWebSocketEventService.getDistinctEventTypesByBot(botId));
    }

    @GetMapping("/bot/{botId}/event-subtypes")
    public ResponseEntity<List<String>> getDistinctEventSubtypesByBot(@PathVariable UUID botId) {
        return ResponseEntity.ok(botWebSocketEventService.getDistinctEventSubtypesByBot(botId));
    }

    @PostMapping("/retry-failed")
    public ResponseEntity<Void> retryFailedEvents(@RequestBody Map<String, String> request) {
        String retryThreshold = request.get("retryThreshold");
        botWebSocketEventService.retryFailedEvents(LocalDateTime.parse(retryThreshold));
        return ResponseEntity.ok().build();
    }
}