package com.im.system.service;

import com.im.system.entity.BotWebSocketEventEntity;
import com.im.system.repository.BotWebSocketEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BotWebSocketEventService {

    private final BotWebSocketEventRepository botWebSocketEventRepository;

    @Autowired
    public BotWebSocketEventService(BotWebSocketEventRepository botWebSocketEventRepository) {
        this.botWebSocketEventRepository = botWebSocketEventRepository;
    }

    // Basic CRUD operations
    public BotWebSocketEventEntity createEvent(BotWebSocketEventEntity event) {
        return botWebSocketEventRepository.save(event);
    }

    public Optional<BotWebSocketEventEntity> getEventById(UUID id) {
        return botWebSocketEventRepository.findById(id);
    }

    public Optional<BotWebSocketEventEntity> getEventByIdAndBotId(UUID id, UUID botId) {
        return botWebSocketEventRepository.findByIdAndBotId(id, botId);
    }

    public List<BotWebSocketEventEntity> getAllEvents() {
        return botWebSocketEventRepository.findAll();
    }

    public BotWebSocketEventEntity updateEvent(BotWebSocketEventEntity event) {
        return botWebSocketEventRepository.save(event);
    }

    public void deleteEvent(UUID id) {
        botWebSocketEventRepository.deleteById(id);
    }

    public void deleteEventByIdAndBotId(UUID id, UUID botId) {
        Optional<BotWebSocketEventEntity> event = botWebSocketEventRepository.findByIdAndBotId(id, botId);
        event.ifPresent(e -> botWebSocketEventRepository.delete(e));
    }

    // Query operations
    public List<BotWebSocketEventEntity> getEventsByBotId(UUID botId) {
        return botWebSocketEventRepository.findByBotId(botId);
    }

    public List<BotWebSocketEventEntity> getEventsBySessionId(UUID sessionId) {
        return botWebSocketEventRepository.findBySessionId(sessionId);
    }

    public List<BotWebSocketEventEntity> getEventsByEventType(String eventType) {
        return botWebSocketEventRepository.findByEventType(eventType);
    }

    public List<BotWebSocketEventEntity> getEventsByStatus(String status) {
        return botWebSocketEventRepository.findByStatus(status);
    }

    public List<BotWebSocketEventEntity> getEventsByBotIdAndStatus(UUID botId, String status) {
        return botWebSocketEventRepository.findByBotIdAndStatus(botId, status);
    }

    public List<BotWebSocketEventEntity> getEventsBySessionIdAndStatus(UUID sessionId, String status) {
        return botWebSocketEventRepository.findBySessionIdAndStatus(sessionId, status);
    }

    // Event creation helper methods
    public BotWebSocketEventEntity createMessageEvent(UUID botId, UUID sessionId, UUID sourceMessageId,
                                                      UUID sourceUserId, UUID sourceConversationId,
                                                      String payload, String metadata) {
        BotWebSocketEventEntity event = new BotWebSocketEventEntity();
        event.setBotId(botId);
        event.setSessionId(sessionId);
        event.setEventType("MESSAGE_RECEIVED");
        event.setEventSubtype("TEXT");
        event.setPayload(payload);
        event.setMetadata(metadata);
        event.setSourceMessageId(sourceMessageId);
        event.setSourceUserId(sourceUserId);
        event.setSourceConversationId(sourceConversationId);
        event.setPriority(1);
        return botWebSocketEventRepository.save(event);
    }

    public BotWebSocketEventEntity createMessageEditedEvent(UUID botId, UUID sessionId, UUID sourceMessageId,
                                                            UUID sourceUserId, UUID sourceConversationId,
                                                            String oldContent, String newContent) {
        BotWebSocketEventEntity event = new BotWebSocketEventEntity();
        event.setBotId(botId);
        event.setSessionId(sessionId);
        event.setEventType("MESSAGE_EDITED");
        event.setEventSubtype("TEXT");
        event.setPayload(newContent);
        event.setMetadata("{\"old_content\": \"" + oldContent + "\"}");
        event.setSourceMessageId(sourceMessageId);
        event.setSourceUserId(sourceUserId);
        event.setSourceConversationId(sourceConversationId);
        event.setPriority(2);
        return botWebSocketEventRepository.save(event);
    }

    public BotWebSocketEventEntity createMessageDeletedEvent(UUID botId, UUID sessionId, UUID sourceMessageId,
                                                             UUID sourceUserId, UUID sourceConversationId) {
        BotWebSocketEventEntity event = new BotWebSocketEventEntity();
        event.setBotId(botId);
        event.setSessionId(sessionId);
        event.setEventType("MESSAGE_DELETED");
        event.setPayload("Message deleted");
        event.setSourceMessageId(sourceMessageId);
        event.setSourceUserId(sourceUserId);
        event.setSourceConversationId(sourceConversationId);
        event.setPriority(2);
        return botWebSocketEventRepository.save(event);
    }

    public BotWebSocketEventEntity createConversationCreatedEvent(UUID botId, UUID sessionId,
                                                                  UUID sourceConversationId, UUID sourceUserId,
                                                                  String conversationName) {
        BotWebSocketEventEntity event = new BotWebSocketEventEntity();
        event.setBotId(botId);
        event.setSessionId(sessionId);
        event.setEventType("CONVERSATION_CREATED");
        event.setPayload(conversationName);
        event.setSourceConversationId(sourceConversationId);
        event.setSourceUserId(sourceUserId);
        event.setPriority(3);
        return botWebSocketEventRepository.save(event);
    }

    public BotWebSocketEventEntity createUserJoinedEvent(UUID botId, UUID sessionId, UUID sourceConversationId,
                                                         UUID sourceUserId, String username) {
        BotWebSocketEventEntity event = new BotWebSocketEventEntity();
        event.setBotId(botId);
        event.setSessionId(sessionId);
        event.setEventType("USER_JOINED");
        event.setPayload(username);
        event.setSourceConversationId(sourceConversationId);
        event.setSourceUserId(sourceUserId);
        event.setPriority(3);
        return botWebSocketEventRepository.save(event);
    }

    public BotWebSocketEventEntity createUserLeftEvent(UUID botId, UUID sessionId, UUID sourceConversationId,
                                                       UUID sourceUserId, String username) {
        BotWebSocketEventEntity event = new BotWebSocketEventEntity();
        event.setBotId(botId);
        event.setSessionId(sessionId);
        event.setEventType("USER_LEFT");
        event.setPayload(username);
        event.setSourceConversationId(sourceConversationId);
        event.setSourceUserId(sourceUserId);
        event.setPriority(3);
        return botWebSocketEventRepository.save(event);
    }

    public BotWebSocketEventEntity createWebhookEvent(UUID botId, String webhookUrl, String eventType,
                                                      String payload, String metadata) {
        BotWebSocketEventEntity event = new BotWebSocketEventEntity();
        event.setBotId(botId);
        event.setEventType(eventType);
        event.setWebhookUrl(webhookUrl);
        event.setPayload(payload);
        event.setMetadata(metadata);
        event.setPriority(1);
        return botWebSocketEventRepository.save(event);
    }

    public BotWebSocketEventEntity createCustomEvent(UUID botId, UUID sessionId, String eventType,
                                                     String eventSubtype, String payload, String metadata,
                                                     Integer priority, String tags) {
        BotWebSocketEventEntity event = new BotWebSocketEventEntity();
        event.setBotId(botId);
        event.setSessionId(sessionId);
        event.setEventType(eventType);
        event.setEventSubtype(eventSubtype);
        event.setPayload(payload);
        event.setMetadata(metadata);
        event.setPriority(priority != null ? priority : 1);
        event.setTags(tags);
        return botWebSocketEventRepository.save(event);
    }

    // Processing methods
    public List<BotWebSocketEventEntity> getReadyForProcessing() {
        return botWebSocketEventRepository.findReadyForProcessing(LocalDateTime.now());
    }

    public List<BotWebSocketEventEntity> getPendingEventsByBotId(UUID botId) {
        return botWebSocketEventRepository.findPendingEventsByBotId(botId, List.of("PENDING"));
    }

    public List<BotWebSocketEventEntity> getRetryableFailedEvents() {
        return botWebSocketEventRepository.findRetryableFailedEvents(LocalDateTime.now());
    }

    @Transactional
    public void markEventsForRetry(List<UUID> eventIds, LocalDateTime nextRetryAt) {
        botWebSocketEventRepository.markEventsForRetry(eventIds, "PENDING", LocalDateTime.now(), nextRetryAt);
    }

    @Transactional
    public void markEventsAsDelivered(List<UUID> eventIds) {
        botWebSocketEventRepository.markEventsAsDelivered(eventIds, "DELIVERED", LocalDateTime.now(), LocalDateTime.now());
    }

    @Transactional
    public void markEventsAsAcknowledged(List<UUID> eventIds) {
        botWebSocketEventRepository.markEventsAsAcknowledged(eventIds, "ACKNOWLEDGED", LocalDateTime.now(), LocalDateTime.now());
    }

    @Transactional
    public void markEventsAsProcessed(List<UUID> eventIds) {
        botWebSocketEventRepository.markEventsAsProcessed(eventIds, "PROCESSED", LocalDateTime.now(), LocalDateTime.now());
    }

    @Transactional
    public void markEventsAsFailed(List<UUID> eventIds, String errorMessage) {
        botWebSocketEventRepository.markEventsAsFailed(eventIds, "FAILED", errorMessage, LocalDateTime.now());
    }

    @Transactional
    public void markEventAsFailed(UUID eventId, String errorMessage) {
        markEventsAsFailed(List.of(eventId), errorMessage);
    }

    @Transactional
    public void updateWebhookResponse(UUID eventId, Integer responseCode, String responseBody) {
        botWebSocketEventRepository.updateWebhookResponse(eventId, responseCode, responseBody, LocalDateTime.now());
    }

    // Cleanup methods
    @Transactional
    public int cleanupOldAcknowledgedEvents(UUID botId, LocalDateTime threshold) {
        return botWebSocketEventRepository.deleteOldAcknowledgedEvents(botId, threshold);
    }

    @Transactional
    public int cleanupPermanentlyFailedEvents(LocalDateTime threshold) {
        return botWebSocketEventRepository.deletePermanentlyFailedEvents(threshold);
    }

    // Statistics methods
    public Long countEventsByBotIdAndStatus(UUID botId, String status) {
        return botWebSocketEventRepository.countByBotIdAndStatus(botId, status);
    }

    public Long countEventsBySessionIdAndStatus(UUID sessionId, String status) {
        return botWebSocketEventRepository.countBySessionIdAndStatus(sessionId, status);
    }

    public Long countPermanentlyFailedEventsByBot(UUID botId) {
        return botWebSocketEventRepository.countPermanentlyFailedEventsByBot(botId);
    }

    public List<Object[]> getEventStatusCountsForBot(UUID botId) {
        return botWebSocketEventRepository.countEventsByStatusForBot(botId);
    }

    public List<Object[]> getEventTypeCountsForBot(UUID botId) {
        return botWebSocketEventRepository.countEventsByTypeForBot(botId);
    }

    public List<Object[]> getEventDayCountsForBot(UUID botId, LocalDateTime startDate) {
        return botWebSocketEventRepository.countEventsByDayForBot(botId, startDate);
    }

    // Advanced query methods
    public List<BotWebSocketEventEntity> getEventsByBotAndTag(UUID botId, String tag) {
        return botWebSocketEventRepository.findEventsByBotAndTag(botId, tag);
    }

    public List<BotWebSocketEventEntity> getEventsByBotAndMessage(UUID botId, UUID messageId) {
        return botWebSocketEventRepository.findEventsByBotAndMessage(botId, messageId);
    }

    public List<BotWebSocketEventEntity> getEventsByBotAndConversation(UUID botId, UUID conversationId) {
        return botWebSocketEventRepository.findEventsByBotAndConversation(botId, conversationId);
    }

    public List<BotWebSocketEventEntity> getEventsByBotAndUser(UUID botId, UUID userId) {
        return botWebSocketEventRepository.findEventsByBotAndUser(botId, userId);
    }

    public List<BotWebSocketEventEntity> getEventsByBotAndDevice(UUID botId, String deviceId) {
        return botWebSocketEventRepository.findEventsByBotAndDevice(botId, deviceId);
    }

    public List<BotWebSocketEventEntity> getEventsByBotAndSession(UUID botId, UUID sessionId) {
        return botWebSocketEventRepository.findEventsByBotAndSession(botId, sessionId);
    }

    public List<BotWebSocketEventEntity> getPendingWebhookEventsByBot(UUID botId) {
        return botWebSocketEventRepository.findPendingWebhookEventsByBot(botId);
    }

    public List<BotWebSocketEventEntity> getUnacknowledgedWebhookEventsByBot(UUID botId) {
        return botWebSocketEventRepository.findUnacknowledgedWebhookEventsByBot(botId);
    }

    public List<BotWebSocketEventEntity> getRetryableWebhookEventsByBot(UUID botId) {
        return botWebSocketEventRepository.findRetryableWebhookEventsByBot(botId);
    }

    public List<BotWebSocketEventEntity> getPendingDirectEventsByBot(UUID botId) {
        return botWebSocketEventRepository.findPendingDirectEventsByBot(botId);
    }

    public List<String> getDistinctEventTypesByBot(UUID botId) {
        return botWebSocketEventRepository.findDistinctEventTypesByBotId(botId);
    }

    public List<String> getDistinctEventSubtypesByBot(UUID botId) {
        return botWebSocketEventRepository.findDistinctEventSubtypesByBotId(botId);
    }

    // Batch operations
    @Transactional
    public void processBatchOfEvents(List<BotWebSocketEventEntity> events, Runnable processor) {
        for (BotWebSocketEventEntity event : events) {
            try {
                processor.run();
                markEventsAsProcessed(List.of(event.getId()));
            } catch (Exception e) {
                markEventAsFailed(event.getId(), e.getMessage());
            }
        }
    }

    @Transactional
    public void retryFailedEvents(LocalDateTime retryThreshold) {
        List<BotWebSocketEventEntity> failedEvents = getRetryableFailedEvents();
        List<UUID> eventIds = failedEvents.stream()
                .map(BotWebSocketEventEntity::getId)
                .toList();
        
        if (!eventIds.isEmpty()) {
            markEventsForRetry(eventIds, retryThreshold);
        }
    }

    // Health check methods
    public boolean isBotEventQueueHealthy(UUID botId) {
        Long pendingCount = countEventsByBotIdAndStatus(botId, "PENDING");
        Long failedCount = countPermanentlyFailedEventsByBot(botId);
        
        // If there are more than 100 pending events or more than 10 permanently failed events
        return pendingCount < 100 && failedCount < 10;
    }

    public String getBotEventQueueStatus(UUID botId) {
        Long pending = countEventsByBotIdAndStatus(botId, "PENDING");
        Long delivered = countEventsByBotIdAndStatus(botId, "DELIVERED");
        Long acknowledged = countEventsByBotIdAndStatus(botId, "ACKNOWLEDGED");
        Long failed = countEventsByBotIdAndStatus(botId, "FAILED");
        Long processed = countEventsByBotIdAndStatus(botId, "PROCESSED");
        
        return String.format("Pending: %d, Delivered: %d, Acknowledged: %d, Failed: %d, Processed: %d",
                pending, delivered, acknowledged, failed, processed);
    }
}