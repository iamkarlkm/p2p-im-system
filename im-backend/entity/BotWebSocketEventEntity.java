package com.im.system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bot_websocket_events")
public class BotWebSocketEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "bot_id", nullable = false)
    private UUID botId;

    @Column(name = "session_id")
    private UUID sessionId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "event_subtype", length = 50)
    private String eventSubtype;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "delivery_attempts")
    private Integer deliveryAttempts = 0;

    @Column(name = "max_attempts")
    private Integer maxAttempts = 5;

    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "webhook_url", length = 500)
    private String webhookUrl;

    @Column(name = "webhook_response_code")
    private Integer webhookResponseCode;

    @Column(name = "webhook_response_body", columnDefinition = "TEXT")
    private String webhookResponseBody;

    @Column(name = "priority")
    private Integer priority = 1;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "source_user_id")
    private UUID sourceUserId;

    @Column(name = "source_device_id")
    private String sourceDeviceId;

    @Column(name = "source_message_id")
    private UUID sourceMessageId;

    @Column(name = "source_conversation_id")
    private UUID sourceConversationId;

    @Column(name = "tags", length = 200)
    private String tags;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public BotWebSocketEventEntity() {}

    public BotWebSocketEventEntity(UUID botId, String eventType, String payload) {
        this.botId = botId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBotId() {
        return botId;
    }

    public void setBotId(UUID botId) {
        this.botId = botId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventSubtype() {
        return eventSubtype;
    }

    public void setEventSubtype(String eventSubtype) {
        this.eventSubtype = eventSubtype;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDeliveryAttempts() {
        return deliveryAttempts;
    }

    public void setDeliveryAttempts(Integer deliveryAttempts) {
        this.deliveryAttempts = deliveryAttempts;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public LocalDateTime getNextRetryAt() {
        return nextRetryAt;
    }

    public void setNextRetryAt(LocalDateTime nextRetryAt) {
        this.nextRetryAt = nextRetryAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public LocalDateTime getAcknowledgedAt() {
        return acknowledgedAt;
    }

    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public Integer getWebhookResponseCode() {
        return webhookResponseCode;
    }

    public void setWebhookResponseCode(Integer webhookResponseCode) {
        this.webhookResponseCode = webhookResponseCode;
    }

    public String getWebhookResponseBody() {
        return webhookResponseBody;
    }

    public void setWebhookResponseBody(String webhookResponseBody) {
        this.webhookResponseBody = webhookResponseBody;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public UUID getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(UUID sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public String getSourceDeviceId() {
        return sourceDeviceId;
    }

    public void setSourceDeviceId(String sourceDeviceId) {
        this.sourceDeviceId = sourceDeviceId;
    }

    public UUID getSourceMessageId() {
        return sourceMessageId;
    }

    public void setSourceMessageId(UUID sourceMessageId) {
        this.sourceMessageId = sourceMessageId;
    }

    public UUID getSourceConversationId() {
        return sourceConversationId;
    }

    public void setSourceConversationId(UUID sourceConversationId) {
        this.sourceConversationId = sourceConversationId;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}