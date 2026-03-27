package com.im.webhook.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Webhook事件实体
 * 记录触发的Webhook事件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookEvent {
    
    /** 事件唯一标识 */
    private String eventId;
    
    /** Webhook配置ID */
    private String webhookId;
    
    /** 应用ID */
    private String appId;
    
    /** 事件类型 */
    private String eventType;
    
    /** 事件数据 */
    private Map<String, Object> payload;
    
    /** 事件状态 */
    private EventStatus status;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 发送时间 */
    private LocalDateTime sentAt;
    
    /** 完成时间 */
    private LocalDateTime completedAt;
    
    /** HTTP响应状态码 */
    private Integer httpStatusCode;
    
    /** 响应内容 */
    private String responseBody;
    
    /** 响应时间（毫秒） */
    private Long responseTimeMs;
    
    /** 重试次数 */
    private Integer retryCount;
    
    /** 错误信息 */
    private String errorMessage;
    
    /** 请求签名 */
    private String signature;
    
    /** 请求ID（用于追踪） */
    private String requestId;
    
    /**
     * 事件状态枚举
     */
    public enum EventStatus {
        PENDING("待发送"),
        SENDING("发送中"),
        SUCCESS("成功"),
        FAILED("失败"),
        RETRYING("重试中"),
        CANCELLED("已取消"),
        TIMEOUT("超时");
        
        private final String label;
        
        EventStatus(String label) {
            this.label = label;
        }
        
        public String getLabel() { return label; }
        
        /**
         * 是否为终态
         */
        public boolean isTerminal() {
            return this == SUCCESS || this == FAILED || this == CANCELLED || this == TIMEOUT;
        }
        
        /**
         * 是否可以重试
         */
        public boolean canRetry() {
            return this == FAILED || this == TIMEOUT;
        }
    }
    
    /**
     * 预定义事件类型
     */
    public static class EventType {
        // 消息相关
        public static final String MESSAGE_RECEIVED = "message.received";
        public static final String MESSAGE_SENT = "message.sent";
        public static final String MESSAGE_READ = "message.read";
        public static final String MESSAGE_RECALL = "message.recall";
        public static final String MESSAGE_REACTION = "message.reaction";
        
        // 群组相关
        public static final String GROUP_CREATED = "group.created";
        public static final String GROUP_UPDATED = "group.updated";
        public static final String GROUP_MEMBER_JOINED = "group.member.joined";
        public static final String GROUP_MEMBER_LEFT = "group.member.left";
        public static final String GROUP_MEMBER_KICKED = "group.member.kicked";
        
        // 用户相关
        public static final String USER_ONLINE = "user.online";
        public static final String USER_OFFLINE = "user.offline";
        public static final String USER_PROFILE_UPDATED = "user.profile.updated";
        public static final String USER_FRIEND_ADDED = "user.friend.added";
        public static final String USER_FRIEND_REMOVED = "user.friend.removed";
        
        // 通话相关
        public static final String CALL_INITIATED = "call.initiated";
        public static final String CALL_ACCEPTED = "call.accepted";
        public static final String CALL_REJECTED = "call.rejected";
        public static final String CALL_ENDED = "call.ended";
        public static final String CALL_RECORDING = "call.recording";
        
        // 系统相关
        public static final String SYSTEM_MAINTENANCE = "system.maintenance";
        public static final String SYSTEM_ALERT = "system.alert";
        
        /**
         * 获取所有事件类型
         */
        public static String[] getAllTypes() {
            return new String[]{
                MESSAGE_RECEIVED, MESSAGE_SENT, MESSAGE_READ, MESSAGE_RECALL, MESSAGE_REACTION,
                GROUP_CREATED, GROUP_UPDATED, GROUP_MEMBER_JOINED, GROUP_MEMBER_LEFT, GROUP_MEMBER_KICKED,
                USER_ONLINE, USER_OFFLINE, USER_PROFILE_UPDATED, USER_FRIEND_ADDED, USER_FRIEND_REMOVED,
                CALL_INITIATED, CALL_ACCEPTED, CALL_REJECTED, CALL_ENDED, CALL_RECORDING,
                SYSTEM_MAINTENANCE, SYSTEM_ALERT
            };
        }
    }
}
