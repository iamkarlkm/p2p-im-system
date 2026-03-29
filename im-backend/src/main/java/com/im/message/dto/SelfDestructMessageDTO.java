package com.im.message.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.im.message.model.SelfDestructMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 阅后即焚消息数据传输对象
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SelfDestructMessageDTO {

    private String id;
    private String conversationId;
    private String senderId;
    private String senderName;
    private String senderAvatar;
    private String receiverId;
    private String messageContent;
    private String contentType;
    private Integer durationSeconds;
    private Boolean isRead;
    private String readAt;
    private Integer remainingSeconds;
    private Boolean isDestroyed;
    private String destroyedAt;
    private Boolean screenshotDetected;
    private Integer screenshotCount;
    private Boolean allowForward;
    private Boolean allowScreenshot;
    private Boolean blurPreview;
    private String notificationMessage;
    private String createdAt;
    private Boolean canRead;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderAvatar() { return senderAvatar; }
    public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public String getReadAt() { return readAt; }
    public void setReadAt(String readAt) { this.readAt = readAt; }

    public Integer getRemainingSeconds() { return remainingSeconds; }
    public void setRemainingSeconds(Integer remainingSeconds) { this.remainingSeconds = remainingSeconds; }

    public Boolean getIsDestroyed() { return isDestroyed; }
    public void setIsDestroyed(Boolean isDestroyed) { this.isDestroyed = isDestroyed; }

    public String getDestroyedAt() { return destroyedAt; }
    public void setDestroyedAt(String destroyedAt) { this.destroyedAt = destroyedAt; }

    public Boolean getScreenshotDetected() { return screenshotDetected; }
    public void setScreenshotDetected(Boolean screenshotDetected) { this.screenshotDetected = screenshotDetected; }

    public Integer getScreenshotCount() { return screenshotCount; }
    public void setScreenshotCount(Integer screenshotCount) { this.screenshotCount = screenshotCount; }

    public Boolean getAllowForward() { return allowForward; }
    public void setAllowForward(Boolean allowForward) { this.allowForward = allowForward; }

    public Boolean getAllowScreenshot() { return allowScreenshot; }
    public void setAllowScreenshot(Boolean allowScreenshot) { this.allowScreenshot = allowScreenshot; }

    public Boolean getBlurPreview() { return blurPreview; }
    public void setBlurPreview(Boolean blurPreview) { this.blurPreview = blurPreview; }

    public String getNotificationMessage() { return notificationMessage; }
    public void setNotificationMessage(String notificationMessage) { this.notificationMessage = notificationMessage; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public Boolean getCanRead() { return canRead; }
    public void setCanRead(Boolean canRead) { this.canRead = canRead; }

    /**
     * 从实体转换为DTO（发送者视角）
     */
    public static SelfDestructMessageDTO fromEntityForSender(SelfDestructMessage message) {
        SelfDestructMessageDTO dto = new SelfDestructMessageDTO();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversationId());
        dto.setSenderId(message.getSenderId());
        dto.setReceiverId(message.getReceiverId());
        dto.setDurationSeconds(message.getDurationSeconds());
        dto.setIsRead(message.getIsRead());
        dto.setIsDestroyed(message.getIsDestroyed());
        dto.setScreenshotDetected(message.getScreenshotDetected());
        dto.setScreenshotCount(message.getScreenshotCount());
        dto.setAllowForward(message.getAllowForward());
        dto.setAllowScreenshot(message.getAllowScreenshot());
        dto.setBlurPreview(message.getBlurPreview());
        dto.setNotificationMessage(message.getNotificationMessage());
        dto.setContentType(message.getContentType() != null ? message.getContentType().name() : "TEXT");
        dto.setCanRead(message.canRead());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (message.getCreatedAt() != null) {
            dto.setCreatedAt(message.getCreatedAt().format(formatter));
        }
        if (message.getReadAt() != null) {
            dto.setReadAt(message.getReadAt().format(formatter));
        }
        if (message.getDestroyedAt() != null) {
            dto.setDestroyedAt(message.getDestroyedAt().format(formatter));
        }

        // 发送者可以看到原始内容
        dto.setMessageContent(message.getMessageContent());
        dto.setRemainingSeconds(message.getRemainingSeconds());

        return dto;
    }

    /**
     * 从实体转换为DTO（接收者视角）
     */
    public static SelfDestructMessageDTO fromEntityForReceiver(SelfDestructMessage message, boolean canViewContent) {
        SelfDestructMessageDTO dto = new SelfDestructMessageDTO();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversationId());
        dto.setSenderId(message.getSenderId());
        dto.setReceiverId(message.getReceiverId());
        dto.setDurationSeconds(message.getDurationSeconds());
        dto.setIsRead(message.getIsRead());
        dto.setIsDestroyed(message.getIsDestroyed());
        dto.setScreenshotDetected(message.getScreenshotDetected());
        dto.setScreenshotCount(message.getScreenshotCount());
        dto.setAllowForward(message.getAllowForward());
        dto.setAllowScreenshot(message.getAllowScreenshot());
        dto.setBlurPreview(message.getBlurPreview());
        dto.setNotificationMessage(message.getNotificationMessage());
        dto.setContentType(message.getContentType() != null ? message.getContentType().name() : "TEXT");
        dto.setCanRead(message.canRead());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (message.getCreatedAt() != null) {
            dto.setCreatedAt(message.getCreatedAt().format(formatter));
        }
        if (message.getReadAt() != null) {
            dto.setReadAt(message.getReadAt().format(formatter));
        }
        if (message.getDestroyedAt() != null) {
            dto.setDestroyedAt(message.getDestroyedAt().format(formatter));
        }

        // 接收者只有满足条件才能看到内容
        if (canViewContent && !message.getIsDestroyed()) {
            dto.setMessageContent(message.getMessageContent());
            dto.setRemainingSeconds(message.getRemainingSeconds());
        } else {
            dto.setMessageContent(null);
            dto.setRemainingSeconds(null);
        }

        return dto;
    }

    /**
     * 转换为列表项DTO（隐藏敏感信息）
     */
    public static SelfDestructMessageDTO toListItem(SelfDestructMessage message) {
        SelfDestructMessageDTO dto = new SelfDestructMessageDTO();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversationId());
        dto.setSenderId(message.getSenderId());
        dto.setReceiverId(message.getReceiverId());
        dto.setDurationSeconds(message.getDurationSeconds());
        dto.setIsRead(message.getIsRead());
        dto.setIsDestroyed(message.getIsDestroyed());
        dto.setScreenshotDetected(message.getScreenshotDetected());
        dto.setBlurPreview(message.getBlurPreview());
        dto.setNotificationMessage(message.getNotificationMessage());
        dto.setContentType(message.getContentType() != null ? message.getContentType().name() : "TEXT");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (message.getCreatedAt() != null) {
            dto.setCreatedAt(message.getCreatedAt().format(formatter));
        }

        // 列表中不显示内容
        dto.setMessageContent(null);
        dto.setRemainingSeconds(null);

        return dto;
    }

    /**
     * 创建请求DTO
     */
    public static class CreateRequest {
        private String conversationId;
        private String receiverId;
        private String messageContent;
        private String contentType = "TEXT";
        private Integer durationSeconds = 10;
        private Boolean allowForward = false;
        private Boolean allowScreenshot = false;
        private Boolean blurPreview = true;
        private String notificationMessage;

        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }

        public String getReceiverId() { return receiverId; }
        public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

        public String getMessageContent() { return messageContent; }
        public void setMessageContent(String messageContent) { this.messageContent = messageContent; }

        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }

        public Integer getDurationSeconds() { return durationSeconds; }
        public void setDurationSeconds(Integer durationSeconds) { 
            this.durationSeconds = durationSeconds != null ? durationSeconds : 10;
        }

        public Boolean getAllowForward() { return allowForward; }
        public void setAllowForward(Boolean allowForward) { this.allowForward = allowForward; }

        public Boolean getAllowScreenshot() { return allowScreenshot; }
        public void setAllowScreenshot(Boolean allowScreenshot) { this.allowScreenshot = allowScreenshot; }

        public Boolean getBlurPreview() { return blurPreview; }
        public void setBlurPreview(Boolean blurPreview) { this.blurPreview = blurPreview; }

        public String getNotificationMessage() { return notificationMessage; }
        public void setNotificationMessage(String notificationMessage) { this.notificationMessage = notificationMessage; }
    }

    /**
     * 阅读响应DTO
     */
    public static class ReadResponse {
        private String messageId;
        private String messageContent;
        private Integer remainingSeconds;
        private Integer durationSeconds;
        private Boolean allowScreenshot;

        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }

        public String getMessageContent() { return messageContent; }
        public void setMessageContent(String messageContent) { this.messageContent = messageContent; }

        public Integer getRemainingSeconds() { return remainingSeconds; }
        public void setRemainingSeconds(Integer remainingSeconds) { this.remainingSeconds = remainingSeconds; }

        public Integer getDurationSeconds() { return durationSeconds; }
        public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }

        public Boolean getAllowScreenshot() { return allowScreenshot; }
        public void setAllowScreenshot(Boolean allowScreenshot) { this.allowScreenshot = allowScreenshot; }
    }

    /**
     * 截图检测请求DTO
     */
    public static class ScreenshotDetectRequest {
        private String messageId;

        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
    }

    /**
     * 截图检测响应DTO
     */
    public static class ScreenshotDetectResponse {
        private Boolean detected;
        private Integer totalCount;
        private String warningMessage;

        public Boolean getDetected() { return detected; }
        public void setDetected(Boolean detected) { this.detected = detected; }

        public Integer getTotalCount() { return totalCount; }
        public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }

        public String getWarningMessage() { return warningMessage; }
        public void setWarningMessage(String warningMessage) { this.warningMessage = warningMessage; }
    }
}
