package com.im.backend.entity.search;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话会话实体类
 * 存储用户的多轮对话会话信息
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_conversation_session")
public class ConversationSession {

    @TableId(type = IdType.ASSIGN_UUID)
    private String sessionId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 会话类型
     * SEARCH: 搜索对话
     * QA: 问答对话
     * NAVIGATION: 导航对话
     * RECOMMENDATION: 推荐对话
     * GENERAL: 通用对话
     */
    private String sessionType;
    
    /**
     * 会话状态
     * ACTIVE: 活跃
     * PAUSED: 暂停
     * ENDED: 已结束
     * TIMEOUT: 超时
     */
    private String status;
    
    /**
     * 会话上下文（JSON格式，存储对话历史和上下文信息）
     */
    private String context;
    
    /**
     * 当前意图
     */
    private String currentIntent;
    
    /**
     * 已收集的槽位信息（JSON格式）
     */
    private String collectedSlots;
    
    /**
     * 待澄清的槽位
     */
    private String pendingSlots;
    
    /**
     * 对话轮次
     */
    private Integer turnCount;
    
    /**
     * 最大轮次限制
     */
    private Integer maxTurns;
    
    /**
     * 会话开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 最后交互时间
     */
    private LocalDateTime lastInteractionTime;
    
    /**
     * 会话结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 超时时间（分钟）
     */
    private Integer timeoutMinutes;
    
    /**
     * 用户位置经度
     */
    private Double userLongitude;
    
    /**
     * 用户位置纬度
     */
    private Double userLatitude;
    
    /**
     * 用户当前城市
     */
    private String userCity;
    
    /**
     * 用户当前区县
     */
    private String userDistrict;
    
    /**
     * 是否已推荐结果
     */
    private Boolean hasRecommended;
    
    /**
     * 推荐结果数量
     */
    private Integer recommendationCount;
    
    /**
     * 用户点击数量
     */
    private Integer userClickCount;
    
    /**
     * 会话满意度评分（1-5）
     */
    private Integer satisfactionScore;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    // ========== 状态常量 ==========
    
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_PAUSED = "PAUSED";
    public static final String STATUS_ENDED = "ENDED";
    public static final String STATUS_TIMEOUT = "TIMEOUT";
    
    public static final String TYPE_SEARCH = "SEARCH";
    public static final String TYPE_QA = "QA";
    public static final String TYPE_NAVIGATION = "NAVIGATION";
    public static final String TYPE_RECOMMENDATION = "RECOMMENDATION";
    public static final String TYPE_GENERAL = "GENERAL";
    
    // ========== 业务方法 ==========
    
    /**
     * 判断会话是否活跃
     */
    public boolean isActive() {
        if (!STATUS_ACTIVE.equals(status)) return false;
        if (lastInteractionTime == null) return true;
        LocalDateTime timeout = lastInteractionTime.plusMinutes(timeoutMinutes != null ? timeoutMinutes : 10);
        return LocalDateTime.now().isBefore(timeout);
    }
    
    /**
     * 判断会话是否超时
     */
    public boolean isTimeout() {
        if (lastInteractionTime == null) return false;
        LocalDateTime timeout = lastInteractionTime.plusMinutes(timeoutMinutes != null ? timeoutMinutes : 10);
        return LocalDateTime.now().isAfter(timeout);
    }
    
    /**
     * 判断是否可以继续对话
     */
    public boolean canContinue() {
        if (!isActive()) return false;
        if (maxTurns != null && turnCount != null && turnCount >= maxTurns) return false;
        return true;
    }
    
    /**
     * 增加轮次
     */
    public void incrementTurn() {
        if (turnCount == null) turnCount = 0;
        turnCount++;
        lastInteractionTime = LocalDateTime.now();
    }
    
    /**
     * 结束会话
     */
    public void endSession() {
        this.status = STATUS_ENDED;
        this.endTime = LocalDateTime.now();
    }
    
    /**
     * 获取会话持续时间（分钟）
     */
    public long getDurationMinutes() {
        if (startTime == null) return 0;
        LocalDateTime end = endTime != null ? endTime : LocalDateTime.now();
        return java.time.Duration.between(startTime, end).toMinutes();
    }
    
    /**
     * 获取转化率
     */
    public double getConversionRate() {
        if (recommendationCount == null || recommendationCount == 0) return 0.0;
        if (userClickCount == null) return 0.0;
        return (double) userClickCount / recommendationCount;
    }
    
    /**
     * 更新用户位置
     */
    public void updateLocation(Double longitude, Double latitude, String city, String district) {
        this.userLongitude = longitude;
        this.userLatitude = latitude;
        this.userCity = city;
        this.userDistrict = district;
        this.lastInteractionTime = LocalDateTime.now();
    }
    
    /**
     * 创建新会话
     */
    public static ConversationSession create(Long userId, String sessionType) {
        return ConversationSession.builder()
                .sessionId(java.util.UUID.randomUUID().toString())
                .userId(userId)
                .sessionType(sessionType != null ? sessionType : TYPE_GENERAL)
                .status(STATUS_ACTIVE)
                .turnCount(0)
                .maxTurns(10)
                .timeoutMinutes(10)
                .startTime(LocalDateTime.now())
                .lastInteractionTime(LocalDateTime.now())
                .hasRecommended(false)
                .recommendationCount(0)
                .userClickCount(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }
}
