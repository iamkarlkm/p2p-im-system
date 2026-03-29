package com.im.backend.modules.local_life.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话会话实体
 * 管理用户与智能对话助手的会话状态
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("conversation_session")
public class ConversationSession extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID（UUID）
     */
    @TableId(type = IdType.INPUT)
    private String sessionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 会话类型：SEARCH-搜索对话, QA-问答对话, NAVIGATE-导航对话
     */
    private String sessionType;

    /**
     * 会话状态：ACTIVE-活跃, PAUSED-暂停, ENDED-已结束, TIMEOUT-超时
     */
    private String status;

    /**
     * 当前轮次
     */
    private Integer currentTurn;

    /**
     * 最大轮次限制
     */
    private Integer maxTurns;

    /**
     * 会话上下文（JSON存储）
     * 如：{"last_poi_category": "火锅", "price_filter": "100-200", "location_bias": "xxx"}
     */
    private String contextData;

    /**
     * 最后一次用户位置（经纬度）
     */
    private Double lastLatitude;

    /**
     * 最后一次用户位置（经纬度）
     */
    private Double lastLongitude;

    /**
     * 位置精度（米）
     */
    private Double locationAccuracy;

    /**
     * 位置更新时间
     */
    private LocalDateTime locationUpdatedAt;

    /**
     * 最后一条消息ID
     */
    private Long lastMessageId;

    /**
     * 会话开始时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime startedAt;

    /**
     * 会话结束时间
     */
    private LocalDateTime endedAt;

    /**
     * 最后活跃时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime lastActiveAt;

    /**
     * 超时时间（分钟）
     */
    private Integer timeoutMinutes;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * 语音搜索次数
     */
    private Integer voiceSearchCount;

    /**
     * 文本搜索次数
     */
    private Integer textSearchCount;

    /**
     * 是否启用语音输入
     */
    private Boolean voiceEnabled;

    /**
     * 用户偏好语言
     */
    private String preferredLanguage;

    /**
     * 会话满意度评分
     */
    private Integer satisfactionScore;

    /**
     * 会话来源：APP-移动端, MINI_PROGRAM-小程序, WEB-网页
     */
    private String source;

    /**
     * 检查会话是否超时
     */
    public boolean isTimeout() {
        if (lastActiveAt == null || timeoutMinutes == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(lastActiveAt.plusMinutes(timeoutMinutes));
    }

    /**
     * 增加轮次
     */
    public void incrementTurn() {
        if (currentTurn == null) {
            currentTurn = 0;
        }
        currentTurn++;
    }

    /**
     * 记录文本搜索
     */
    public void recordTextSearch() {
        if (textSearchCount == null) {
            textSearchCount = 0;
        }
        textSearchCount++;
    }

    /**
     * 记录语音搜索
     */
    public void recordVoiceSearch() {
        if (voiceSearchCount == null) {
            voiceSearchCount = 0;
        }
        voiceSearchCount++;
    }

    /**
     * 会话类型枚举
     */
    public enum SessionType {
        SEARCH("搜索对话"),
        QA("问答对话"),
        NAVIGATE("导航对话"),
        RECOMMENDATION("推荐对话"),
        GENERAL("通用对话");

        private final String label;

        SessionType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    /**
     * 会话状态枚举
     */
    public enum SessionStatus {
        ACTIVE("活跃"),
        PAUSED("暂停"),
        ENDED("已结束"),
        TIMEOUT("超时");

        private final String label;

        SessionStatus(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}
