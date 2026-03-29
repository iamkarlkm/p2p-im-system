package com.im.server.push;

import java.util.Map;
import java.util.Set;

/**
 * 推送消息模型
 * 
 * 支持普通推送、静默推送、VoIP推送等多种类型
 */
public class PushMessage {

    public enum Priority {
        HIGH(10, "高优先级"),
        NORMAL(5, "普通优先级"),
        LOW(1, "低优先级");

        private final int value;
        private final String description;

        Priority(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() { return value; }
        public String getDescription() { return description; }
    }

    public enum PushType {
        NOTIFICATION("通知"),
        DATA("数据消息"),
        SILENT("静默推送"),
        VOIP("VoIP通话"),
        BACKGROUND("后台刷新");

        private final String value;

        PushType(String value) {
            this.value = value;
        }

        public String getValue() { return value; }
    }

    public enum TargetType {
        SINGLE("单设备"),
        MULTI("多设备"),
        BROADCAST("广播"),
        TAG("标签推送"),
        ALIAS("别名推送"),
        TOPIC("主题推送");

        private final String value;

        TargetType(String value) {
            this.value = value;
        }

        public String getValue() { return value; }
    }

    // ==================== 基础字段 ====================
    private String messageId;          // 消息ID (唯一标识)
    private String title;              // 通知标题
    private String body;               // 通知内容
    private String subtitle;           // iOS副标题
    private String category;           // 通知类别 (iOS)
    private String threadId;           // 线程ID (iOS)
    private String icon;               // 图标
    private String sound;              // 声音
    private String badge;              // 角标数字
    private String color;              // 通知颜色 (Android)
    private String channelId;          // 渠道ID (Android)
    private String channelName;        // 渠道名称 (Android)
    private String tag;                // 消息标签 (用于覆盖)
    private String collapseKey;        // 折叠键 (Android)

    // ==================== 高级字段 ====================
    private Priority priority = Priority.NORMAL;
    private PushType pushType = PushType.NOTIFICATION;
    private TargetType targetType = TargetType.SINGLE;
    private int ttl = 86400;           // 存活时间 (秒), 默认7天
    private boolean mutableContent;   // iOS可变内容
    private String interruptionLevel; // iOS中断级别: passive, active, timeSensitive, critical

    // ==================== 目标 ====================
    private Long targetUserId;         // 目标用户ID
    private Set<String> targetTokens;  // 目标Token集合
    private String targetTag;          // 标签
    private String targetAlias;        // 别名
    private String targetTopic;        // 主题

    // ==================== 数据 ====================
    private Map<String, String> data;       // 自定义数据
    private Map<String, Object> aps;         // iOS APNs payload
    private Map<String, Object> androidConfig; // Android配置
    private Map<String, Object> fcmOptions;   // FCM选项
    private Map<String, Object> huaweiOptions; // 华为选项
    private Map<String, Object> xiaomiOptions; // 小米选项

    // ==================== 来源 ====================
    private Long senderId;             // 发送者用户ID
    private String senderName;          // 发送者昵称
    private String senderAvatar;        // 发送者头像
    private String conversationId;      // 会话ID
    private String conversationType;   // 会话类型: private/group
    private Long messageId;            // 关联消息ID
    private String messageType;        // 消息类型: text/image/file/etc

    // ==================== 扩展 ====================
    private String mergeKey;           // 消息合并Key (相同Key的消息合并)
    private boolean retryOnFailure;   // 失败时重试
    private int maxRetryAttempts;      // 最大重试次数
    private Set<String> excludeTokens; // 排除的Token
    private Map<String, String> localization; // 本地化: { "zh_CN": "中文标题", "en": "English Title" }
    private String locale;             // 当前语言

    // ==================== 统计 ====================
    private boolean isTest = false;    // 是否为测试推送
    private String scheduledTime;       // 定时发送时间 (ISO-8601)
    private String expireTime;         // 过期时间 (ISO-8601)

    // ==================== Getters and Setters ====================

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getThreadId() { return threadId; }
    public void setThreadId(String threadId) { this.threadId = threadId; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getSound() { return sound; }
    public void setSound(String sound) { this.sound = sound; }

    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getChannelId() { return channelId; }
    public void setChannelId(String channelId) { this.channelId = channelId; }

    public String getChannelName() { return channelName; }
    public void setChannelName(String channelName) { this.channelName = channelName; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public String getCollapseKey() { return collapseKey; }
    public void setCollapseKey(String collapseKey) { this.collapseKey = collapseKey; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public PushType getPushType() { return pushType; }
    public void setPushType(PushType pushType) { this.pushType = pushType; }

    public TargetType getTargetType() { return targetType; }
    public void setTargetType(TargetType targetType) { this.targetType = targetType; }

    public int getTtl() { return ttl; }
    public void setTtl(int ttl) { this.ttl = ttl; }

    public boolean isMutableContent() { return mutableContent; }
    public void setMutableContent(boolean mutableContent) { this.mutableContent = mutableContent; }

    public String getInterruptionLevel() { return interruptionLevel; }
    public void setInterruptionLevel(String interruptionLevel) { this.interruptionLevel = interruptionLevel; }

    public Long getTargetUserId() { return targetUserId; }
    public void setTargetUserId(Long targetUserId) { this.targetUserId = targetUserId; }

    public Set<String> getTargetTokens() { return targetTokens; }
    public void setTargetTokens(Set<String> targetTokens) { this.targetTokens = targetTokens; }

    public String getTargetTag() { return targetTag; }
    public void setTargetTag(String targetTag) { this.targetTag = targetTag; }

    public String getTargetAlias() { return targetAlias; }
    public void setTargetAlias(String targetAlias) { this.targetAlias = targetAlias; }

    public String getTargetTopic() { return targetTopic; }
    public void setTargetTopic(String targetTopic) { this.targetTopic = targetTopic; }

    public Map<String, String> getData() { return data; }
    public void setData(Map<String, String> data) { this.data = data; }

    public Map<String, Object> getAps() { return aps; }
    public void setAps(Map<String, Object> aps) { this.aps = aps; }

    public Map<String, Object> getAndroidConfig() { return androidConfig; }
    public void setAndroidConfig(Map<String, Object> androidConfig) { this.androidConfig = androidConfig; }

    public Map<String, Object> getFcmOptions() { return fcmOptions; }
    public void setFcmOptions(Map<String, Object> fcmOptions) { this.fcmOptions = fcmOptions; }

    public Map<String, Object> getHuaweiOptions() { return huaweiOptions; }
    public void setHuaweiOptions(Map<String, Object> huaweiOptions) { this.huaweiOptions = huaweiOptions; }

    public Map<String, Object> getXiaomiOptions() { return xiaomiOptions; }
    public void setXiaomiOptions(Map<String, Object> xiaomiOptions) { this.xiaomiOptions = xiaomiOptions; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderAvatar() { return senderAvatar; }
    public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getConversationType() { return conversationType; }
    public void setConversationType(String conversationType) { this.conversationType = conversationType; }

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public String getMergeKey() { return mergeKey; }
    public void setMergeKey(String mergeKey) { this.mergeKey = mergeKey; }

    public boolean isRetryOnFailure() { return retryOnFailure; }
    public void setRetryOnFailure(boolean retryOnFailure) { this.retryOnFailure = retryOnFailure; }

    public int getMaxRetryAttempts() { return maxRetryAttempts; }
    public void setMaxRetryAttempts(int maxRetryAttempts) { this.maxRetryAttempts = maxRetryAttempts; }

    public Set<String> getExcludeTokens() { return excludeTokens; }
    public void setExcludeTokens(Set<String> excludeTokens) { this.excludeTokens = excludeTokens; }

    public Map<String, String> getLocalization() { return localization; }
    public void setLocalization(Map<String, String> localization) { this.localization = localization; }

    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }

    public boolean isTest() { return isTest; }
    public void setTest(boolean test) { isTest = test; }

    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

    public String getExpireTime() { return expireTime; }
    public void setExpireTime(String expireTime) { this.expireTime = expireTime; }

    @Override
    public String toString() {
        return "PushMessage{" +
                "messageId='" + messageId + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", pushType=" + pushType +
                ", priority=" + priority +
                ", targetUserId=" + targetUserId +
                '}';
    }

    /**
     * 消息构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final PushMessage msg = new PushMessage();

        public Builder messageId(String messageId) { msg.messageId = messageId; return this; }
        public Builder title(String title) { msg.title = title; return this; }
        public Builder body(String body) { msg.body = body; return this; }
        public Builder subtitle(String subtitle) { msg.subtitle = subtitle; return this; }
        public Builder category(String category) { msg.category = category; return this; }
        public Builder threadId(String threadId) { msg.threadId = threadId; return this; }
        public Builder icon(String icon) { msg.icon = icon; return this; }
        public Builder sound(String sound) { msg.sound = sound; return this; }
        public Builder badge(String badge) { msg.badge = badge; return this; }
        public Builder color(String color) { msg.color = color; return this; }
        public Builder channelId(String channelId) { msg.channelId = channelId; return this; }
        public Builder channelName(String channelName) { msg.channelName = channelName; return this; }
        public Builder tag(String tag) { msg.tag = tag; return this; }
        public Builder collapseKey(String collapseKey) { msg.collapseKey = collapseKey; return this; }
        public Builder priority(Priority priority) { msg.priority = priority; return this; }
        public Builder pushType(PushType pushType) { msg.pushType = pushType; return this; }
        public Builder targetType(TargetType targetType) { msg.targetType = targetType; return this; }
        public Builder ttl(int ttl) { msg.ttl = ttl; return this; }
        public Builder mutableContent(boolean mutableContent) { msg.mutableContent = mutableContent; return this; }
        public Builder interruptionLevel(String interruptionLevel) { msg.interruptionLevel = interruptionLevel; return this; }
        public Builder targetUserId(Long targetUserId) { msg.targetUserId = targetUserId; return this; }
        public Builder targetTokens(Set<String> targetTokens) { msg.targetTokens = targetTokens; return this; }
        public Builder targetTag(String targetTag) { msg.targetTag = targetTag; return this; }
        public Builder targetAlias(String targetAlias) { msg.targetAlias = targetAlias; return this; }
        public Builder targetTopic(String targetTopic) { msg.targetTopic = targetTopic; return this; }
        public Builder data(Map<String, String> data) { msg.data = data; return this; }
        public Builder senderId(Long senderId) { msg.senderId = senderId; return this; }
        public Builder senderName(String senderName) { msg.senderName = senderName; return this; }
        public Builder senderAvatar(String senderAvatar) { msg.senderAvatar = senderAvatar; return this; }
        public Builder conversationId(String conversationId) { msg.conversationId = conversationId; return this; }
        public Builder conversationType(String conversationType) { msg.conversationType = conversationType; return this; }
        public Builder messageId(Long messageId) { msg.messageId = messageId; return this; }
        public Builder messageType(String messageType) { msg.messageType = messageType; return this; }
        public Builder mergeKey(String mergeKey) { msg.mergeKey = mergeKey; return this; }
        public Builder retryOnFailure(boolean retryOnFailure) { msg.retryOnFailure = retryOnFailure; return this; }
        public Builder maxRetryAttempts(int maxRetryAttempts) { msg.maxRetryAttempts = maxRetryAttempts; return this; }
        public Builder isTest(boolean isTest) { msg.isTest = isTest; return this; }
        public Builder scheduledTime(String scheduledTime) { msg.scheduledTime = scheduledTime; return this; }
        public Builder expireTime(String expireTime) { msg.expireTime = expireTime; return this; }
        public Builder locale(String locale) { msg.locale = locale; return this; }

        public PushMessage build() {
            return msg;
        }
    }
}
