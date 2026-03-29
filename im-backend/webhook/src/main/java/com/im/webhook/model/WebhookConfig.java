package com.im.webhook.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Webhook配置实体
 * 存储Webhook端点的配置信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookConfig {
    
    /** Webhook唯一标识 */
    private String webhookId;
    
    /** 所属应用ID */
    private String appId;
    
    /** Webhook名称 */
    private String name;
    
    /** 回调URL */
    private String callbackUrl;
    
    /** 密钥（用于签名验证） */
    private String secret;
    
    /** 订阅的事件类型列表 */
    private List<String> eventTypes;
    
    /** 自定义请求头 */
    private Map<String, String> headers;
    
    /** 重试策略配置 */
    private RetryPolicy retryPolicy;
    
    /** 超时时间（秒） */
    private Integer timeoutSeconds;
    
    /** 状态：ACTIVE, PAUSED, DISABLED */
    private WebhookStatus status;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 最后触发时间 */
    private LocalDateTime lastTriggeredAt;
    
    /** 触发次数统计 */
    private Long triggerCount;
    
    /** 失败次数统计 */
    private Long failureCount;
    
    /** 描述 */
    private String description;
    
    /**
     * Webhook状态枚举
     */
    public enum WebhookStatus {
        ACTIVE("活跃", "正常接收回调"),
        PAUSED("暂停", "临时停止回调"),
        DISABLED("禁用", "完全停用"),
        ERROR("错误", "连续失败超过阈值");
        
        private final String label;
        private final String description;
        
        WebhookStatus(String label, String description) {
            this.label = label;
            this.description = description;
        }
        
        public String getLabel() { return label; }
        public String getDescription() { return description; }
    }
    
    /**
     * 重试策略配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RetryPolicy {
        /** 最大重试次数 */
        @Builder.Default
        private Integer maxRetries = 3;
        
        /** 重试间隔（秒）- 指数退避基数 */
        @Builder.Default
        private Integer retryIntervalSeconds = 5;
        
        /** 最大重试间隔（秒） */
        @Builder.Default
        private Integer maxRetryIntervalSeconds = 300;
        
        /** 重试策略：FIXED-固定间隔, EXPONENTIAL-指数退避 */
        @Builder.Default
        private RetryStrategy strategy = RetryStrategy.EXPONENTIAL;
        
        /** 仅对特定HTTP状态码重试 */
        private List<Integer> retryHttpStatusCodes;
        
        /**
         * 重试策略枚举
         */
        public enum RetryStrategy {
            FIXED("固定间隔"),
            EXPONENTIAL("指数退避"),
            LINEAR("线性递增");
            
            private final String label;
            
            RetryStrategy(String label) {
                this.label = label;
            }
            
            public String getLabel() { return label; }
        }
        
        /**
         * 计算第n次重试的等待时间
         */
        public long calculateRetryDelay(int retryCount) {
            switch (strategy) {
                case FIXED:
                    return retryIntervalSeconds * 1000L;
                case EXPONENTIAL:
                    long delay = (long) (retryIntervalSeconds * Math.pow(2, retryCount - 1));
                    return Math.min(delay, maxRetryIntervalSeconds * 1000L);
                case LINEAR:
                    return retryIntervalSeconds * retryCount * 1000L;
                default:
                    return retryIntervalSeconds * 1000L;
            }
        }
    }
}
