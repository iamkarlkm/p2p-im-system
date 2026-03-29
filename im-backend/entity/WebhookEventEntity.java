package com.im.system.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Webhook 事件实体
 * 存储订阅的事件、投递状态、重试记录等
 */
@Entity
@Table(name = "webhook_events")
public class WebhookEventEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    // 事件基本信息
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;
    
    @Column(name = "event_subtype", length = 100)
    private String eventSubtype;
    
    @Column(name = "event_version", nullable = false, length = 20)
    private String eventVersion = "1.0";
    
    @Column(name = "source_system", nullable = false, length = 100)
    private String sourceSystem = "im-system";
    
    @Column(name = "source_component", length = 100)
    private String sourceComponent;
    
    // 事件时间戳
    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime eventTimestamp;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // 事件数据
    @Column(name = "event_data", columnDefinition = "TEXT")
    private String eventData;
    
    @Column(name = "event_metadata", columnDefinition = "TEXT")
    private String eventMetadata;
    
    // 投递信息
    @Column(name = "subscription_id")
    private UUID subscriptionId;
    
    @Column(name = "webhook_url", length = 500)
    private String webhookUrl;
    
    @Column(name = "webhook_secret", length = 500)
    private String webhookSecret;
    
    @Column(name = "webhook_headers", columnDefinition = "TEXT")
    private String webhookHeaders;
    
    // 投递状态
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 50)
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;
    
    @Column(name = "delivery_attempts")
    private Integer deliveryAttempts = 0;
    
    @Column(name = "max_delivery_attempts")
    private Integer maxDeliveryAttempts = 5;
    
    @Column(name = "next_delivery_attempt")
    private LocalDateTime nextDeliveryAttempt;
    
    @Column(name = "last_delivery_attempt")
    private LocalDateTime lastDeliveryAttempt;
    
    @Column(name = "last_delivery_response", length = 1000)
    private String lastDeliveryResponse;
    
    @Column(name = "last_delivery_status_code")
    private Integer lastDeliveryStatusCode;
    
    // 优先级
    @Column(name = "priority")
    private Integer priority = 5; // 1=最高, 10=最低
    
    @Column(name = "retry_backoff_multiplier")
    private Double retryBackoffMultiplier = 2.0;
    
    @Column(name = "retry_initial_delay_seconds")
    private Integer retryInitialDelaySeconds = 5;
    
    // 回调信息
    @Column(name = "callback_url", length = 500)
    private String callbackUrl;
    
    @Column(name = "callback_headers", columnDefinition = "TEXT")
    private String callbackHeaders;
    
    @Column(name = "callback_data", columnDefinition = "TEXT")
    private String callbackData;
    
    @Column(name = "callback_response", columnDefinition = "TEXT")
    private String callbackResponse;
    
    // 错误处理
    @Column(name = "error_message", length = 2000)
    private String errorMessage;
    
    @Column(name = "error_stack_trace", columnDefinition = "TEXT")
    private String errorStackTrace;
    
    @Column(name = "error_count")
    private Integer errorCount = 0;
    
    // 元数据
    @Column(name = "tags", length = 1000)
    private String tags;
    
    @Column(name = "created_by")
    private UUID createdBy;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "processing_node", length = 100)
    private String processingNode;
    
    // 死信队列相关
    @Column(name = "dead_lettered")
    private Boolean deadLettered = false;
    
    @Column(name = "dead_letter_reason", length = 500)
    private String deadLetterReason;
    
    @Column(name = "dead_lettered_at")
    private LocalDateTime deadLetteredAt;
    
    @Column(name = "archived")
    private Boolean archived = false;
    
    @Column(name = "archived_at")
    private LocalDateTime archivedAt;
    
    @Column(name = "ttl_days")
    private Integer ttlDays = 30;
    
    @Column(name = "ttl_expires_at")
    private LocalDateTime ttlExpiresAt;
    
    // 统计字段
    @Column(name = "delivery_latency_ms")
    private Long deliveryLatencyMs;
    
    @Column(name = "delivery_success_count")
    private Integer deliverySuccessCount = 0;
    
    @Column(name = "delivery_failure_count")
    private Integer deliveryFailureCount = 0;
    
    // 分区/分片字段
    @Column(name = "partition_key", length = 100)
    private String partitionKey;
    
    @Column(name = "shard_id", length = 50)
    private String shardId;
    
    // 事件投递状态枚举
    public enum DeliveryStatus {
        PENDING("待投递"),
        QUEUED("已排队"),
        PROCESSING("处理中"),
        DELIVERING("投递中"),
        DELIVERED("已投递"),
        FAILED("投递失败"),
        RETRYING("重试中"),
        DEAD_LETTER("死信"),
        EXPIRED("已过期"),
        CANCELLED("已取消");
        
        private final String description;
        
        DeliveryStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 构造函数
    public WebhookEventEntity() {
        this.createdAt = LocalDateTime.now();
        this.eventTimestamp = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public WebhookEventEntity(String eventType, String eventData) {
        this();
        this.eventType = eventType;
        this.eventData = eventData;
    }
    
    // Getters and Setters (篇幅限制，只展示部分关键方法)
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
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
    
    public String getEventVersion() {
        return eventVersion;
    }
    
    public void setEventVersion(String eventVersion) {
        this.eventVersion = eventVersion;
    }
    
    public String getSourceSystem() {
        return sourceSystem;
    }
    
    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }
    
    public String getSourceComponent() {
        return sourceComponent;
    }
    
    public void setSourceComponent(String sourceComponent) {
        this.sourceComponent = sourceComponent;
    }
    
    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }
    
    public void setEventTimestamp(LocalDateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getEventData() {
        return eventData;
    }
    
    public void setEventData(String eventData) {
        this.eventData = eventData;
    }
    
    public String getEventMetadata() {
        return eventMetadata;
    }
    
    public void setEventMetadata(String eventMetadata) {
        this.eventMetadata = eventMetadata;
    }
    
    public UUID getSubscriptionId() {
        return subscriptionId;
    }
    
    public void setSubscriptionId(UUID subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
    
    public String getWebhookUrl() {
        return webhookUrl;
    }
    
    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }
    
    public String getWebhookSecret() {
        return webhookSecret;
    }
    
    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }
    
    public String getWebhookHeaders() {
        return webhookHeaders;
    }
    
    public void setWebhookHeaders(String webhookHeaders) {
        this.webhookHeaders = webhookHeaders;
    }
    
    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }
    
    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
    
    public Integer getDeliveryAttempts() {
        return deliveryAttempts;
    }
    
    public void setDeliveryAttempts(Integer deliveryAttempts) {
        this.deliveryAttempts = deliveryAttempts;
    }
    
    public Integer getMaxDeliveryAttempts() {
        return maxDeliveryAttempts;
    }
    
    public void setMaxDeliveryAttempts(Integer maxDeliveryAttempts) {
        this.maxDeliveryAttempts = maxDeliveryAttempts;
    }
    
    public LocalDateTime getNextDeliveryAttempt() {
        return nextDeliveryAttempt;
    }
    
    public void setNextDeliveryAttempt(LocalDateTime nextDeliveryAttempt) {
        this.nextDeliveryAttempt = nextDeliveryAttempt;
    }
    
    public LocalDateTime getLastDeliveryAttempt() {
        return lastDeliveryAttempt;
    }
    
    public void setLastDeliveryAttempt(LocalDateTime lastDeliveryAttempt) {
        this.lastDeliveryAttempt = lastDeliveryAttempt;
    }
    
    public String getLastDeliveryResponse() {
        return lastDeliveryResponse;
    }
    
    public void setLastDeliveryResponse(String lastDeliveryResponse) {
        this.lastDeliveryResponse = lastDeliveryResponse;
    }
    
    public Integer getLastDeliveryStatusCode() {
        return lastDeliveryStatusCode;
    }
    
    public void setLastDeliveryStatusCode(Integer lastDeliveryStatusCode) {
        this.lastDeliveryStatusCode = lastDeliveryStatusCode;
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    public Double getRetryBackoffMultiplier() {
        return retryBackoffMultiplier;
    }
    
    public void setRetryBackoffMultiplier(Double retryBackoffMultiplier) {
        this.retryBackoffMultiplier = retryBackoffMultiplier;
    }
    
    public Integer getRetryInitialDelaySeconds() {
        return retryInitialDelaySeconds;
    }
    
    public void setRetryInitialDelaySeconds(Integer retryInitialDelaySeconds) {
        this.retryInitialDelaySeconds = retryInitialDelaySeconds;
    }
    
    public String getCallbackUrl() {
        return callbackUrl;
    }
    
    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
    
    public String getCallbackHeaders() {
        return callbackHeaders;
    }
    
    public void setCallbackHeaders(String callbackHeaders) {
        this.callbackHeaders = callbackHeaders;
    }
    
    public String getCallbackData() {
        return callbackData;
    }
    
    public void setCallbackData(String callbackData) {
        this.callbackData = callbackData;
    }
    
    public String getCallbackResponse() {
        return callbackResponse;
    }
    
    public void setCallbackResponse(String callbackResponse) {
        this.callbackResponse = callbackResponse;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getErrorStackTrace() {
        return errorStackTrace;
    }
    
    public void setErrorStackTrace(String errorStackTrace) {
        this.errorStackTrace = errorStackTrace;
    }
    
    public Integer getErrorCount() {
        return errorCount;
    }
    
    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public UUID getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getProcessingNode() {
        return processingNode;
    }
    
    public void setProcessingNode(String processingNode) {
        this.processingNode = processingNode;
    }
    
    public Boolean getDeadLettered() {
        return deadLettered;
    }
    
    public void setDeadLettered(Boolean deadLettered) {
        this.deadLettered = deadLettered;
    }
    
    public String getDeadLetterReason() {
        return deadLetterReason;
    }
    
    public void setDeadLetterReason(String deadLetterReason) {
        this.deadLetterReason = deadLetterReason;
    }
    
    public LocalDateTime getDeadLetteredAt() {
        return deadLetteredAt;
    }
    
    public void setDeadLetteredAt(LocalDateTime deadLetteredAt) {
        this.deadLetteredAt = deadLetteredAt;
    }
    
    public Boolean getArchived() {
        return archived;
    }
    
    public void setArchived(Boolean archived) {
        this.archived = archived;
    }
    
    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }
    
    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }
    
    public Integer getTtlDays() {
        return ttlDays;
    }
    
    public void setTtlDays(Integer ttlDays) {
        this.ttlDays = ttlDays;
    }
    
    public LocalDateTime getTtlExpiresAt() {
        return ttlExpiresAt;
    }
    
    public void setTtlExpiresAt(LocalDateTime ttlExpiresAt) {
        this.ttlExpiresAt = ttlExpiresAt;
    }
    
    public Long getDeliveryLatencyMs() {
        return deliveryLatencyMs;
    }
    
    public void setDeliveryLatencyMs(Long deliveryLatencyMs) {
        this.deliveryLatencyMs = deliveryLatencyMs;
    }
    
    public Integer getDeliverySuccessCount() {
        return deliverySuccessCount;
    }
    
    public void setDeliverySuccessCount(Integer deliverySuccessCount) {
        this.deliverySuccessCount = deliverySuccessCount;
    }
    
    public Integer getDeliveryFailureCount() {
        return deliveryFailureCount;
    }
    
    public void setDeliveryFailureCount(Integer deliveryFailureCount) {
        this.deliveryFailureCount = deliveryFailureCount;
    }
    
    public String getPartitionKey() {
        return partitionKey;
    }
    
    public void setPartitionKey(String partitionKey) {
        this.partitionKey = partitionKey;
    }
    
    public String getShardId() {
        return shardId;
    }
    
    public void setShardId(String shardId) {
        this.shardId = shardId;
    }
    
    // 实用方法
    public void incrementDeliveryAttempts() {
        this.deliveryAttempts = (this.deliveryAttempts == null ? 0 : this.deliveryAttempts) + 1;
    }
    
    public void incrementDeliverySuccess() {
        this.deliverySuccessCount = (this.deliverySuccessCount == null ? 0 : this.deliverySuccessCount) + 1;
    }
    
    public void incrementDeliveryFailure() {
        this.deliveryFailureCount = (this.deliveryFailureCount == null ? 0 : this.deliveryFailureCount) + 1;
    }
    
    public void incrementErrorCount() {
        this.errorCount = (this.errorCount == null ? 0 : this.errorCount) + 1;
    }
    
    public boolean isRetryable() {
        return deliveryAttempts < maxDeliveryAttempts;
    }
    
    public long calculateNextRetryDelay() {
        int baseDelay = retryInitialDelaySeconds != null ? retryInitialDelaySeconds : 5;
        double multiplier = retryBackoffMultiplier != null ? retryBackoffMultiplier : 2.0;
        int attempts = deliveryAttempts != null ? deliveryAttempts : 0;
        return (long) (baseDelay * Math.pow(multiplier, attempts));
    }
    
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "WebhookEventEntity{" +
                "id=" + id +
                ", eventType='" + eventType + '\'' +
                ", deliveryStatus=" + deliveryStatus +
                ", deliveryAttempts=" + deliveryAttempts +
                ", priority=" + priority +
                '}';
    }
}