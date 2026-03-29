package com.im.server.webhook;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Webhook事件模型
 * 定义所有可触发Webhook的事件类型
 */
public class WebhookEvent {

    private String eventId;          // 事件唯一ID
    private String eventType;        // 事件类型
    private long timestamp;         // 事件时间戳
    private String source;           // 事件来源服务
    private Map<String, Object> data; // 事件数据
    private Map<String, String> headers; // 扩展头信息
    private int retryCount;          // 重试次数
    private String status;           // 事件状态
    private Long deliveryId;         // 投递ID

    public WebhookEvent() {
        this.eventId = UUID.randomUUID().toString().replace("-", "");
        this.timestamp = Instant.now().toEpochMilli();
        this.retryCount = 0;
        this.status = "PENDING";
        this.data = new HashMap<>();
        this.headers = new HashMap<>();
    }

    public WebhookEvent(String eventType, Map<String, Object> data) {
        this();
        this.eventType = eventType;
        this.data = data;
    }

    // ==================== 事件类型定义 ====================

    // 消息事件
    public static final String MESSAGE_SEND = "im.message.send";
    public static final String MESSAGE_SEND_AFTER = "im.message.send.after";
    public static final String MESSAGE_RECEIVE = "im.message.receive";
    public static final String MESSAGE_RECALL = "im.message.recall";
    public static final String MESSAGE_DELETE = "im.message.delete";
    public static final String MESSAGE_READ = "im.message.read";

    // 用户事件
    public static final String USER_REGISTER = "im.user.register";
    public static final String USER_LOGIN = "im.user.login";
    public static final String USER_LOGOUT = "im.user.logout";
    public static final String USER_ONLINE = "im.user.online";
    public static final String USER_OFFLINE = "im.user.offline";
    public static final String USER_UPDATE = "im.user.update";

    // 好友事件
    public static final String FRIEND_REQUEST = "im.friend.request";
    public static final String FRIEND_ACCEPT = "im.friend.accept";
    public static final String FRIEND_REJECT = "im.friend.reject";
    public static final String FRIEND_DELETE = "im.friend.delete";

    // 群组事件
    public static final String GROUP_CREATE = "im.group.create";
    public static final String GROUP_UPDATE = "im.group.update";
    public static final String GROUP_DISMISS = "im.group.dismiss";
    public static final String GROUP_MEMBER_JOIN = "im.group.member.join";
    public static final String GROUP_MEMBER_LEAVE = "im.group.member.leave";
    public static final String GROUP_MEMBER_REMOVE = "im.group.member.remove";

    // 文件事件
    public static final String FILE_UPLOAD = "im.file.upload";
    public static final String FILE_DOWNLOAD = "im.file.download";
    public static final String FILE_DELETE = "im.file.delete";

    // 推送事件
    public static final String PUSH_SEND = "im.push.send";
    public static final String PUSH_RECEIVE = "im.push.receive";

    // 通话事件
    public static final String CALL_START = "im.call.start";
    public static final String CALL_END = "im.call.end";
    public static final String CALL_REJECT = "im.call.reject";

    // 事件状态
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_DELIVERED = "DELIVERED";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_RETRYING = "RETRYING";
    public static final String STATUS_EXPIRED = "EXPIRED";

    // ==================== 工厂方法 ====================

    public static WebhookEvent messageSend(Long senderId, String conversationId, String messageId, String content) {
        Map<String, Object> data = new HashMap<>();
        data.put("senderId", senderId);
        data.put("conversationId", conversationId);
        data.put("messageId", messageId);
        data.put("content", content);
        data.put("timestamp", System.currentTimeMillis());
        return new WebhookEvent(MESSAGE_SEND, data);
    }

    public static WebhookEvent messageRecall(String messageId, Long operatorId, String conversationId) {
        Map<String, Object> data = new HashMap<>();
        data.put("messageId", messageId);
        data.put("operatorId", operatorId);
        data.put("conversationId", conversationId);
        data.put("timestamp", System.currentTimeMillis());
        return new WebhookEvent(MESSAGE_RECALL, data);
    }

    public static WebhookEvent userRegister(Long userId, String username, String phone) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("username", username);
        data.put("phone", phone);
        data.put("timestamp", System.currentTimeMillis());
        return new WebhookEvent(USER_REGISTER, data);
    }

    public static WebhookEvent userLogin(Long userId, String deviceId, String ip) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("deviceId", deviceId);
        data.put("ip", ip);
        data.put("timestamp", System.currentTimeMillis());
        return new WebhookEvent(USER_LOGIN, data);
    }

    public static WebhookEvent friendRequest(Long fromUserId, Long toUserId, String message) {
        Map<String, Object> data = new HashMap<>();
        data.put("fromUserId", fromUserId);
        data.put("toUserId", toUserId);
        data.put("message", message);
        data.put("timestamp", System.currentTimeMillis());
        return new WebhookEvent(FRIEND_REQUEST, data);
    }

    public static WebhookEvent friendAccept(Long userId1, Long userId2) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId1", userId1);
        data.put("userId2", userId2);
        data.put("timestamp", System.currentTimeMillis());
        return new WebhookEvent(FRIEND_ACCEPT, data);
    }

    public static WebhookEvent groupCreate(Long creatorId, String groupId, String groupName) {
        Map<String, Object> data = new HashMap<>();
        data.put("creatorId", creatorId);
        data.put("groupId", groupId);
        data.put("groupName", groupName);
        data.put("timestamp", System.currentTimeMillis());
        return new WebhookEvent(GROUP_CREATE, data);
    }

    public static WebhookEvent groupMemberJoin(String groupId, Long memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("groupId", groupId);
        data.put("memberId", memberId);
        data.put("timestamp", System.currentTimeMillis());
        return new WebhookEvent(GROUP_MEMBER_JOIN, data);
    }

    public static WebhookEvent fileUpload(String fileId, Long uploaderId, String fileName, long fileSize) {
        Map<String, Object> data = new HashMap<>();
        data.put("fileId", fileId);
        data.put("uploaderId", uploaderId);
        data.put("fileName", fileName);
        data.put("fileSize", fileSize);
        data.put("timestamp", System.currentTimeMillis());
        return new WebhookEvent(FILE_UPLOAD, data);
    }

    public static WebhookEvent callStart(Long callerId, Long calleeId, String callType) {
        Map<String, Object> data = new HashMap<>();
        data.put("callerId", callerId);
        data.put("calleeId", calleeId);
        data.put("callType", callType);
        data.put("timestamp", System.currentTimeMillis());
        return new WebhookEvent(CALL_START, data);
    }

    public static WebhookEvent callEnd(String callId, Long callerId, Long calleeId, long duration) {
        Map<String, Object> data = new HashMap<>();
        data.put("callId", callId);
        data.put("callerId", callerId);
        data.put("calleeId", calleeId);
        data.put("duration", duration);
        data.put("timestamp", System.currentTimeMillis());
        return new WebhookEvent(CALL_END, data);
    }

    // ==================== Getters and Setters ====================

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public WebhookEvent addData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public WebhookEvent addHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public WebhookEvent incrementRetry() {
        this.retryCount++;
        return this;
    }

    // ==================== JSON序列化 ====================

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"eventId\":\"").append(eventId).append("\",");
        sb.append("\"eventType\":\"").append(eventType).append("\",");
        sb.append("\"timestamp\":").append(timestamp).append(",");
        sb.append("\"source\":\"").append(source != null ? source : "").append("\",");
        sb.append("\"retryCount\":").append(retryCount).append(",");
        sb.append("\"status\":\"").append(status).append("\",");
        sb.append("\"data\":{");
        int i = 0;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("\"").append(value.toString().replace("\"", "\\\"")).append("\"");
            } else if (value instanceof Number) {
                sb.append(value);
            } else if (value != null) {
                sb.append("\"").append(value.toString().replace("\"", "\\\"")).append("\"");
            } else {
                sb.append("null");
            }
            i++;
        }
        sb.append("}}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "WebhookEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                ", retryCount=" + retryCount +
                ", status='" + status + '\'' +
                '}';
    }
}
