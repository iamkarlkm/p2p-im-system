package com.im.backend.modules.explore.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.im.backend.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 探店达人实体类
 * 记录探店达人的认证信息和权益
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("im_explore_influencer")
public class ExploreInfluencer extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 达人ID */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 达人昵称 */
    private String nickname;

    /** 头像URL */
    private String avatar;

    /** 个人简介 */
    private String bio;

    /** 擅长领域标签（JSON数组：美食/旅行/摄影等） */
    private String expertiseTags;

    /** 达人等级：1-普通达人 2-银牌达人 3-金牌达人 4-钻石达人 */
    private Integer level;

    /** 认证状态：0-未认证 1-认证中 2-已认证 3-认证失败 */
    private Integer authStatus;

    /** 认证时间 */
    private LocalDateTime authTime;

    /** 粉丝数量 */
    private Integer followerCount;

    /** 探店笔记数量 */
    private Integer noteCount;

    /** 总获赞数 */
    private Integer totalLikes;

    /** 总浏览量 */
    private Long totalViews;

    /** 探店评分平均分 */
    private BigDecimal avgRating;

    /** 探店足迹数（打卡POI数量） */
    private Integer footprintCount;

    /** 影响力分数 */
    private BigDecimal influenceScore;

    /** 本月收益（元） */
    private BigDecimal monthlyIncome;

    /** 累计收益（元） */
    private BigDecimal totalIncome;

    /** 专属权益（JSON格式） */
    private String benefits;

    /** 审核备注 */
    private String auditRemark;

    /** 状态：0-禁用 1-正常 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;

    // ==================== 业务方法 ====================

    /**
     * 计算影响力分数
     */
    public void calculateInfluenceScore() {
        double score = 0.0;
        score += (followerCount == null ? 0 : followerCount) * 0.5;
        score += (noteCount == null ? 0 : noteCount) * 10.0;
        score += (totalLikes == null ? 0 : totalLikes) * 0.1;
        score += (totalViews == null ? 0 : totalViews) * 0.01;
        score += (footprintCount == null ? 0 : footprintCount) * 5.0;
        
        // 等级加权
        int levelWeight = level == null ? 1 : level;
        score *= (1 + levelWeight * 0.2);
        
        this.influenceScore = BigDecimal.valueOf(score);
    }

    /**
     * 检查是否已认证
     */
    public boolean isAuthenticated() {
        return Integer.valueOf(2).equals(authStatus);
    }

    /**
     * 获取等级文本
     */
    public String getLevelText() {
        switch (level == null ? 0 : level) {
            case 1: return "普通达人";
            case 2: return "银牌达人";
            case 3: return "金牌达人";
            case 4: return "钻石达人";
            default: return "未认证";
        }
    }

    /**
     * 获取认证状态文本
     */
    public String getAuthStatusText() {
        switch (authStatus == null ? 0 : authStatus) {
            case 0: return "未认证";
            case 1: return "认证中";
            case 2: return "已认证";
            case 3: return "认证失败";
            default: return "未知";
        }
    }

    /**
     * 增加粉丝数
     */
    public void incrementFollower() {
        this.followerCount = (this.followerCount == null ? 0 : this.followerCount) + 1;
    }

    /**
     * 减少粉丝数
     */
    public void decrementFollower() {
        this.followerCount = Math.max(0, (this.followerCount == null ? 0 : this.followerCount) - 1);
    }

    // ==================== 静态常量 ====================

    /** 达人等级：普通达人 */
    public static final int LEVEL_NORMAL = 1;
    
    /** 达人等级：银牌达人 */
    public static final int LEVEL_SILVER = 2;
    
    /** 达人等级：金牌达人 */
    public static final int LEVEL_GOLD = 3;
    
    /** 达人等级：钻石达人 */
    public static final int LEVEL_DIAMOND = 4;

    /** 认证状态：未认证 */
    public static final int AUTH_STATUS_NONE = 0;
    
    /** 认证状态：认证中 */
    public static final int AUTH_STATUS_PENDING = 1;
    
    /** 认证状态：已认证 */
    public static final int AUTH_STATUS_PASSED = 2;
    
    /** 认证状态：认证失败 */
    public static final int AUTH_STATUS_REJECTED = 3;
}
