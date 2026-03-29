package com.im.system.repository;

import com.im.system.entity.BotWebSocketEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BotWebSocketEventRepository extends JpaRepository<BotWebSocketEventEntity, UUID> {

    // Basic CRUD and query methods
    List<BotWebSocketEventEntity> findByBotId(UUID botId);
    List<BotWebSocketEventEntity> findBySessionId(UUID sessionId);
    List<BotWebSocketEventEntity> findByEventType(String eventType);
    List<BotWebSocketEventEntity> findByEventTypeAndBotId(String eventType, UUID botId);
    List<BotWebSocketEventEntity> findByStatus(String status);
    List<BotWebSocketEventEntity> findByStatusAndNextRetryAtBefore(String status, LocalDateTime before);
    List<BotWebSocketEventEntity> findByBotIdAndStatus(UUID botId, String status);
    List<BotWebSocketEventEntity> findBySessionIdAndStatus(UUID sessionId, String status);
    Optional<BotWebSocketEventEntity> findByIdAndBotId(UUID id, UUID botId);
    List<BotWebSocketEventEntity> findBySourceMessageId(UUID sourceMessageId);
    List<BotWebSocketEventEntity> findBySourceConversationId(UUID sourceConversationId);
    List<BotWebSocketEventEntity> findBySourceUserId(UUID sourceUserId);
    List<BotWebSocketEventEntity> findByWebhookUrl(String webhookUrl);
    List<BotWebSocketEventEntity> findByWebhookUrlAndStatus(String webhookUrl, String status);
    List<BotWebSocketEventEntity> findByTagsContaining(String tag);
    List<BotWebSocketEventEntity> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<BotWebSocketEventEntity> findByUpdatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<BotWebSocketEventEntity> findByProcessedAtBetween(LocalDateTime start, LocalDateTime end);
    List<BotWebSocketEventEntity> findByDeliveredAtBetween(LocalDateTime start, LocalDateTime end);
    List<BotWebSocketEventEntity> findByAcknowledgedAtBetween(LocalDateTime start, LocalDateTime end);
    List<BotWebSocketEventEntity> findByPriority(Integer priority);
    List<BotWebSocketEventEntity> findByPriorityGreaterThan(Integer priority);
    List<BotWebSocketEventEntity> findByPriorityLessThan(Integer priority);
    List<BotWebSocketEventEntity> findByDeliveryAttemptsLessThan(Integer maxAttempts);
    List<BotWebSocketEventEntity> findByDeliveryAttemptsGreaterThan(Integer minAttempts);
    List<BotWebSocketEventEntity> findByNextRetryAtBefore(LocalDateTime before);
    List<BotWebSocketEventEntity> findByNextRetryAtAfter(LocalDateTime after);
    List<BotWebSocketEventEntity> findBySourceDeviceId(String sourceDeviceId);
    List<BotWebSocketEventEntity> findBySourceDeviceIdAndBotId(String sourceDeviceId, UUID botId);
    List<BotWebSocketEventEntity> findByBotIdAndSourceConversationId(UUID botId, UUID sourceConversationId);
    List<BotWebSocketEventEntity> findByBotIdAndSourceMessageId(UUID botId, UUID sourceMessageId);
    List<BotWebSocketEventEntity> findByBotIdAndSourceUserId(UUID botId, UUID sourceUserId);
    List<BotWebSocketEventEntity> findByBotIdAndSourceDeviceId(UUID botId, String sourceDeviceId);
    List<BotWebSocketEventEntity> findByBotIdAndEventTypeAndSourceConversationId(UUID botId, String eventType, UUID sourceConversationId);
    List<BotWebSocketEventEntity> findByBotIdAndEventTypeAndSourceMessageId(UUID botId, String eventType, UUID sourceMessageId);
    List<BotWebSocketEventEntity> findByBotIdAndEventTypeAndSourceUserId(UUID botId, String eventType, UUID sourceUserId);
    List<BotWebSocketEventEntity> findByBotIdAndEventTypeAndSourceDeviceId(UUID botId, String eventType, String sourceDeviceId);
    List<BotWebSocketEventEntity> findByBotIdAndEventTypeAndStatus(UUID botId, String eventType, String status);
    List<BotWebSocketEventEntity> findByBotIdAndEventTypeAndPriority(UUID botId, String eventType, Integer priority);
    List<BotWebSocketEventEntity> findByBotIdAndEventTypeAndDeliveryAttemptsLessThan(UUID botId, String eventType, Integer maxAttempts);
    List<BotWebSocketEventEntity> findByBotIdAndEventTypeAndNextRetryAtBefore(UUID botId, String eventType, LocalDateTime before);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndStatus(UUID botId, UUID sessionId, String status);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndEventType(UUID botId, UUID sessionId, String eventType);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndEventTypeAndStatus(UUID botId, UUID sessionId, String eventType, String status);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndSourceConversationId(UUID botId, UUID sessionId, UUID sourceConversationId);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndSourceMessageId(UUID botId, UUID sessionId, UUID sourceMessageId);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndSourceUserId(UUID botId, UUID sessionId, UUID sourceUserId);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndSourceDeviceId(UUID botId, UUID sessionId, String sourceDeviceId);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndPriority(UUID botId, UUID sessionId, Integer priority);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndDeliveryAttemptsLessThan(UUID botId, UUID sessionId, Integer maxAttempts);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndNextRetryAtBefore(UUID botId, UUID sessionId, LocalDateTime before);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndCreatedAtBetween(UUID botId, UUID sessionId, LocalDateTime start, LocalDateTime end);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndUpdatedAtBetween(UUID botId, UUID sessionId, LocalDateTime start, LocalDateTime end);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndProcessedAtBetween(UUID botId, UUID sessionId, LocalDateTime start, LocalDateTime end);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndDeliveredAtBetween(UUID botId, UUID sessionId, LocalDateTime start, LocalDateTime end);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndAcknowledgedAtBetween(UUID botId, UUID sessionId, LocalDateTime start, LocalDateTime end);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndTagsContaining(UUID botId, UUID sessionId, String tag);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndWebhookUrl(UUID botId, UUID sessionId, String webhookUrl);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndWebhookUrlAndStatus(UUID botId, UUID sessionId, String webhookUrl, String status);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndEventSubtype(UUID botId, UUID sessionId, String eventSubtype);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndEventSubtypeAndStatus(UUID botId, UUID sessionId, String eventSubtype, String status);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndEventSubtypeAndPriority(UUID botId, UUID sessionId, String eventSubtype, Integer priority);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndEventSubtypeAndDeliveryAttemptsLessThan(UUID botId, UUID sessionId, String eventSubtype, Integer maxAttempts);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndEventSubtypeAndNextRetryAtBefore(UUID botId, UUID sessionId, String eventSubtype, LocalDateTime before);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndEventSubtypeAndCreatedAtBetween(UUID botId, UUID sessionId, String eventSubtype, LocalDateTime start, LocalDateTime end);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndEventSubtypeAndUpdatedAtBetween(UUID botId, UUID sessionId, String eventSubtype, LocalDateTime start, LocalDateTime end);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndEventSubtypeAndProcessedAtBetween(UUID botId, UUID sessionId, String eventSubtype, LocalDateTime start, LocalDateTime end);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndEventSubtypeAndDeliveredAtBetween(UUID botId, UUID sessionId, String eventSubtype, LocalDateTime start, LocalDateTime end);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndEventSubtypeAndAcknowledgedAtBetween(UUID botId, UUID sessionId, String eventSubtype, LocalDateTime start, LocalDateTime end);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndEventSubtypeAndTagsContaining(UUID botId, UUID sessionId, String eventSubtype, String tag);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndEventSubtypeAndWebhookUrl(UUID botId, UUID sessionId, String eventSubtype, String webhookUrl);
    List<BotWebSocketEventEntity> findByBotIdAndSessionIdAndEventSubtypeAndWebhookUrlAndStatus(UUID botId, UUID sessionId, String eventSubtype, String webhookUrl, String status);

    // Custom query methods
    @Query("SELECT e FROM BotWebSocketEventEntity e WHERE e.botId = :botId AND e.status IN :statuses ORDER BY e.priority DESC, e.createdAt ASC")
    List<BotWebSocketEventEntity> findPendingEventsByBotId(@Param("botId") UUID botId, @Param("statuses") List<String> statuses);

    @Query("SELECT e FROM BotWebSocketEventEntity e WHERE e.status = 'PENDING' AND e.nextRetryAt IS NULL OR e.nextRetryAt <= :now ORDER BY e.priority DESC, e.createdAt ASC")
    List<BotWebSocketEventEntity> findReadyForProcessing(@Param("now") LocalDateTime now);

    @Query("SELECT e FROM BotWebSocketEventEntity e WHERE e.status = 'DELIVERED' AND e.acknowledgedAt IS NULL AND e.updatedAt <= :threshold ORDER BY e.priority DESC, e.createdAt ASC")
    List<BotWebSocketEventEntity> findUnacknowledgedDeliveredEvents(@Param("threshold") LocalDateTime threshold);

    @Query("SELECT e FROM BotWebSocketEventEntity e WHERE e.status = 'FAILED' AND e.deliveryAttempts < e.maxAttempts AND e.nextRetryAt <= :now ORDER BY e.priority DESC, e.createdAt ASC")
    List<BotWebSocketEventEntity> findRetryableFailedEvents(@Param("now") LocalDateTime now);

    @Query("SELECT e FROM BotWebSocketEventEntity e WHERE e.botId = :botId AND e.eventType = :eventType AND e.status = 'PENDING' ORDER BY e.priority DESC, e.createdAt ASC")
    List<BotWebSocketEventEntity> findPendingEventsByBotAndType(@Param("botId") UUID botId, @Param("eventType") String eventType);

    @Query("SELECT e FROM BotWebSocketEventEntity e WHERE e.sessionId = :sessionId AND e.eventType IN :eventTypes AND e.status = 'DELIVERED' ORDER BY e.createdAt DESC")
    List<BotWebSocketEventEntity> findDeliveredEventsBySessionAndTypes(@Param("sessionId") UUID sessionId, @Param("eventTypes") List<String> eventTypes);

    @Query("SELECT COUNT(e) FROM BotWebSocketEventEntity e WHERE e.botId = :botId AND e.status = :status")
    Long countByBotIdAndStatus(@Param("botId") UUID botId, @Param("status") String status);

    @Query("SELECT COUNT(e) FROM BotWebSocketEventEntity e WHERE e.sessionId = :sessionId AND e.status = :status")
    Long countBySessionIdAndStatus(@Param("sessionId") UUID sessionId, @Param("status") String status);

    @Query("SELECT COUNT(e) FROM BotWebSocketEventEntity e WHERE e.botId = :botId AND e.eventType = :eventType")
    Long countByBotIdAndEventType(@Param("botId") UUID botId, @Param("eventType") String eventType);

    @Query("SELECT COUNT(e) FROM BotWebSocketEventEntity e WHERE e.botId = :botId AND e.status = 'FAILED' AND e.deliveryAttempts >= e.maxAttempts")
    Long countPermanentlyFailedEventsByBot(@Param("botId") UUID botId);

    @Modifying
    @Query("UPDATE BotWebSocketEventEntity e SET e.status = :newStatus, e.deliveryAttempts = e.deliveryAttempts + 1, e.updatedAt = :now, e.nextRetryAt = :nextRetryAt WHERE e.id IN :ids")
    int markEventsForRetry(@Param("ids") List<UUID> ids, @Param("newStatus") String newStatus, @Param("now") LocalDateTime now, @Param("nextRetryAt") LocalDateTime nextRetryAt);

    @Modifying
    @Query("UPDATE BotWebSocketEventEntity e SET e.status = :newStatus, e.deliveredAt = :deliveredAt, e.updatedAt = :now WHERE e.id IN :ids")
    int markEventsAsDelivered(@Param("ids") List<UUID> ids, @Param("newStatus") String newStatus, @Param("deliveredAt") LocalDateTime deliveredAt, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE BotWebSocketEventEntity e SET e.status = :newStatus, e.acknowledgedAt = :acknowledgedAt, e.updatedAt = :now WHERE e.id IN :ids")
    int markEventsAsAcknowledged(@Param("ids") List<UUID> ids, @Param("newStatus") String newStatus, @Param("acknowledgedAt") LocalDateTime acknowledgedAt, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE BotWebSocketEventEntity e SET e.status = :newStatus, e.processedAt = :processedAt, e.updatedAt = :now WHERE e.id IN :ids")
    int markEventsAsProcessed(@Param("ids") List<UUID> ids, @Param("newStatus") String newStatus, @Param("processedAt") LocalDateTime processedAt, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE BotWebSocketEventEntity e SET e.status = :newStatus, e.errorMessage = :errorMessage, e.updatedAt = :now WHERE e.id IN :ids")
    int markEventsAsFailed(@Param("ids") List<UUID> ids, @Param("newStatus") String newStatus, @Param("errorMessage") String errorMessage, @Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM BotWebSocketEventEntity e WHERE e.botId = :botId AND e.status = 'DELIVERED' AND e.acknowledgedAt IS NOT NULL AND e.acknowledgedAt <= :threshold")
    int deleteOldAcknowledgedEvents(@Param("botId") UUID botId, @Param("threshold") LocalDateTime threshold);

    @Modifying
    @Query("DELETE FROM BotWebSocketEventEntity e WHERE e.status = 'FAILED' AND e.deliveryAttempts >= e.maxAttempts AND e.updatedAt <= :threshold")
    int deletePermanentlyFailedEvents(@Param("threshold") LocalDateTime threshold);

    @Modifying
    @Query("UPDATE BotWebSocketEventEntity e SET e.webhookResponseCode = :responseCode, e.webhookResponseBody = :responseBody, e.updatedAt = :now WHERE e.id = :id")
    int updateWebhookResponse(@Param("id") UUID id, @Param("responseCode") Integer responseCode, @Param("responseBody") String responseBody, @Param("now") LocalDateTime now);

    @Query("SELECT DISTINCT e.eventType FROM BotWebSocketEventEntity e WHERE e.botId = :botId")
    List<String> findDistinctEventTypesByBotId(@Param("botId") UUID botId);

    @Query("SELECT DISTINCT e.eventSubtype FROM BotWebSocketEventEntity e WHERE e.botId = :botId AND e.eventSubtype IS NOT NULL")
    List<String> findDistinctEventSubtypesByBotId(@Param("botId") UUID botId);

    @Query("SELECT e.status, COUNT(e) FROM BotWebSocketEventEntity e WHERE e.botId = :botId GROUP BY e.status")
    List<Object[]> countEventsByStatusForBot(@Param("botId") UUID botId);

    @Query("SELECT DATE(e.createdAt), COUNT(e) FROM BotWebSocketEventEntity e WHERE e.botId = :botId AND e.createdAt >= :startDate GROUP BY DATE(e.createdAt) ORDER BY DATE(e.createdAt)")
    List<Object[]> countEventsByDayForBot(@Param("botId") UUID botId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT e.eventType, COUNT(e) FROM BotWebSocketEventEntity e WHERE e.botId = :botId GROUP BY e.eventType")
    List<Object[]> countEventsByTypeForBot(@Param("botId") UUID botId);

    @Query("SELECT e.priority, COUNT(e) FROM BotWebSocketEventEntity e WHERE e.botId = :botId GROUP BY e.priority")
    List<Object[]> countEventsByPriorityForBot(@Param("botId") UUID botId);

    @Query("SELECT e FROM BotWebSocketEventEntity e WHERE e.botId = :botId AND e.tags LIKE %:tag%")
    List<BotWebSocketEventEntity> findEventsByBotAndTag(@Param("botId") UUID botId, @Param("tag") String tag);

    @Query("SELECT e FROM BotWebSocketEventEntity e WHERE e.botId = :botId AND e.sourceMessageId = :messageId ORDER BY e.createdAt DESC")
    List<BotWebSocketEventEntity> findEventsByBotAndMessage(@Param("botId") UUID botId, @Param("messageId") UUID messageId);

    @Query("SELECT e FROM BotWebSocketEventEntity e WHERE e.botId = :botId AND e.sourceConversationId = :conversationId ORDER BY e.createdAt DESC")
    List<BotWebSocketEventEntity> findEventsByBotAndConversation(@Param("botId") UUID botId, @Param("conversationId") UUID conversationId);

    @Query("SELECT e FROM BotWebSocketEventEntity e WHERE e.botId = :botId AND e.sourceUserId = :userId ORDER BY e.createdAt DESC")
    List<BotWebSocketEventEntity> findEventsByBotAndUser(@Param("botId") UUID botId, @Param("userId") UUID userId);

    @Query("SELECT e FROM BotWebSocketEventEntity e WHERE e.botId = :botId AND e.sourceDeviceId = :deviceId ORDER BY e.createdAt DESC")
    List<BotWebSocketEventEntity> findEventsByBotAndDevice(@Param("botId") UUID botId, @Param("deviceId") String deviceId);

    @Query("SELECT e FROM BotWebSocketEventEntity e WHERE e.botId = :botId AND e.sessionId = :sessionId ORDER BY e.createdAt DESC")
    List<BotWebSocketEventEntity> findEventsByBotAndSession(@Param("botId") UUID botId, @Param("sessionId") UUID sessionId);

    @Query("SELECT e FROM BotWebSocketEventEntity e WHERE e.botId = :botId AND e.webhookUrl IS NOT NULL AND e.status = 'PENDING' ORDER BY e.priority DESC, e.createdAt ASC")
    List<BotWebSocketEventEntity> findPendingWebhookEventsByBot(@Param("botId") UUID botId);

    @Query("SELECT e FROM BotWebSocketEventEntity e WHERE e.botId = :botId AND e.webhookUrl IS NOT NULL AND e.status = 'DELIVERED' AND e.acknowledgedAt IS NULL ORDER BY e.deliveredAt ASC")
    List<BotWebSocketEventEntity> findUnacknowledgedWebhookEventsByBot(@Param("botId") UUID botId);

    @Query("SELECT e FROM BotWebSocketEventEntity e WHERE e.botId = :botId AND e.webhookUrl IS NOT NULL AND e.status = 'FAILED' AND e.deliveryAttempts < e.maxAttempts ORDER BY e.nextRetryAt ASC")
    List<BotWebSocketEventEntity> findRetryableWebhookEventsByBot(@Param("botId") UUID botId);

    @Query("SELECT e FROM BotWebSocketEventEntity e WHERE e.botId = :botId AND (e.webhookUrl IS NULL OR e.webhookUrl = '') AND e.status = 'PENDING' ORDER BY e.priority DESC, e.createdAt ASC")
    List<BotWebSocketEventEntity> findPendingDirectEventsByBot(@Param("botId") UUID botId);
}