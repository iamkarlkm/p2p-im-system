package com.im.webhook.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Webhook回调投递记录
 * 记录每次回调投递的详细情况
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookDelivery {
    
    /** 投递ID */
    private String deliveryId;
    
    /** 关联事件ID */
    private String eventId;
    
    /** Webhook配置ID */
    private String webhookId;
    
    /** 投递尝试次数 */
    private Integer attemptNumber;
    
    /** 请求方法 */
    private String requestMethod;
    
    /** 请求URL */
    private String requestUrl;
    
    /** 请求头 */
    private Map<String, String> requestHeaders;
    
    /** 请求体 */
    private String requestBody;
    
    /** 请求签名 */
    private String requestSignature;
    
    /** 响应状态码 */
    private Integer responseStatusCode;
    
    /** 响应头 */
    private Map<String, String> responseHeaders;
    
    /** 响应体 */
    private String responseBody;
    
    /** 响应时间（毫秒） */
    private Long responseTimeMs;
    
    /** 错误类型 */
    private ErrorType errorType;
    
    /** 错误详情 */
    private String errorDetails;
    
    /** 投递状态 */
    private DeliveryStatus status;
    
    /** 投递时间 */
    private LocalDateTime deliveredAt;
    
    /** 下次重试时间 */
    private LocalDateTime nextRetryAt;
    
    /** IP地址 */
    private String sourceIp;
    
    /**
     * 投递状态枚举
     */
    public enum DeliveryStatus {
        SUCCESS("成功", true),
        FAILED_RETRYABLE("失败-可重试", false),
        FAILED_FINAL("失败-最终", true),
        TIMEOUT("超时", false),
        NETWORK_ERROR("网络错误", false),
        INVALID_RESPONSE("无效响应", false);
        
        private final String label;
        private final boolean terminal;
        
        DeliveryStatus(String label, boolean terminal) {
            this.label = label;
            this.terminal = terminal;
        }
        
        public String getLabel() { return label; }
        public boolean isTerminal() { return terminal; }
    }
    
    /**
     * 错误类型枚举
     */
    public enum ErrorType {
        NONE(null),
        NETWORK_TIMEOUT("网络超时"),
        CONNECTION_REFUSED("连接被拒绝"),
        DNS_RESOLUTION_FAILED("DNS解析失败"),
        SSL_HANDSHAKE_FAILED("SSL握手失败"),
        INVALID_URL("无效URL"),
        HTTP_ERROR("HTTP错误"),
        RATE_LIMITED("请求频率限制"),
        SERVER_ERROR("服务器错误"),
        UNKNOWN("未知错误");
        
        private final String description;
        
        ErrorType(String description) {
            this.description = description;
        }
        
        public String getDescription() { return description; }
        
        /**
         * 根据HTTP状态码判断错误类型
         */
        public static ErrorType fromHttpStatus(int statusCode) {
            if (statusCode == 429) return RATE_LIMITED;
            if (statusCode >= 500) return SERVER_ERROR;
            if (statusCode >= 400) return HTTP_ERROR;
            return NONE;
        }
        
        /**
         * 根据异常判断错误类型
         */
        public static ErrorType fromException(Throwable e) {
            String message = e.getMessage();
            if (message == null) return UNKNOWN;
            
            String lowerMsg = message.toLowerCase();
            if (lowerMsg.contains("timeout")) return NETWORK_TIMEOUT;
            if (lowerMsg.contains("refused")) return CONNECTION_REFUSED;
            if (lowerMsg.contains("dns") || lowerMsg.contains("unknown host")) return DNS_RESOLUTION_FAILED;
            if (lowerMsg.contains("ssl") || lowerMsg.contains("certificate")) return SSL_HANDSHAKE_FAILED;
            
            return UNKNOWN;
        }
    }
    
    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return status == DeliveryStatus.SUCCESS;
    }
    
    /**
     * 判断是否需要重试
     */
    public boolean shouldRetry() {
        return status == DeliveryStatus.FAILED_RETRYABLE || 
               status == DeliveryStatus.TIMEOUT ||
               status == DeliveryStatus.NETWORK_ERROR;
    }
}
