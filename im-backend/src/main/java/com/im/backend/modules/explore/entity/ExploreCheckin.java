package com.im.backend.modules.explore.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.im.backend.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户探店打卡记录实体类
 * 记录用户的探店打卡行为和足迹
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("im_explore_checkin")
public class ExploreCheckin extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 打卡ID */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** POI商户ID */
    private Long poiId;

    /** 商户名称（冗余存储） */
    private String poiName;

    /** 关联探店笔记ID（可选） */
    private Long noteId;

    /** 打卡类型：1-普通打卡 2-笔记关联打卡 3-路线打卡 */
    private Integer checkinType;

    /** 打卡方式：1-手动打卡 2-围栏自动打卡 3-扫码打卡 */
    private Integer checkinMethod;

    /** 打卡时经度 */
    private BigDecimal longitude;

    /** 打卡时纬度 */
    private BigDecimal latitude;

    /** 打卡地址 */
    private String address;

    /** 打卡照片（JSON数组） */
    private String photos;

    /** 打卡文案/心情 */
    private String content;

    /** 评分（1-5分） */
    private BigDecimal rating;

    /** 消费金额（元） */
    private BigDecimal spendAmount;

    /** 同行人数 */
    private Integer companionCount;

    /** 是否公开：0-私有 1-公开 */
    private Integer isPublic;

    /** 打卡时间 */
    private LocalDateTime checkinTime;

    /** 停留时长（分钟） */
    private Integer stayDuration;

    /** 是否获得徽章：0-否 1-是 */
    private Integer hasBadge;

    /** 获得徽章ID */
    private Long badgeId;

    /** 徽章名称 */
    private String badgeName;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;

    // ==================== 业务方法 ====================

    /**
     * 检查是否为公开打卡
     */
    public boolean isPublic() {
        return Integer.valueOf(1).equals(isPublic);
    }

    /**
     * 检查是否关联了笔记
     */
    public boolean hasNote() {
        return noteId != null && noteId > 0;
    }

    /**
     * 获取打卡类型文本
     */
    public String getCheckinTypeText() {
        switch (checkinType == null ? 0 : checkinType) {
            case 1: return "普通打卡";
            case 2: return "笔记关联打卡";
            case 3: return "路线打卡";
            default: return "未知";
        }
    }

    /**
     * 获取打卡方式文本
     */
    public String getCheckinMethodText() {
        switch (checkinMethod == null ? 0 : checkinMethod) {
            case 1: return "手动打卡";
            case 2: return "围栏自动打卡";
            case 3: return "扫码打卡";
            default: return "未知";
        }
    }

    /**
     * 计算打卡得分（用于用户成长体系）
     */
    public int calculateScore() {
        int score = 10; // 基础分
        
        if (hasNote()) {
            score += 20;
        }
        
        if (rating != null && rating.doubleValue() >= 4.0) {
            score += 10;
        }
        
        if (content != null && content.length() > 20) {
            score += 10;
        }
        
        if (photos != null && !photos.isEmpty()) {
            score += 15;
        }
        
        if (Integer.valueOf(1).equals(hasBadge)) {
            score += 30;
        }
        
        return score;
    }

    // ==================== 静态常量 ====================

    /** 打卡类型：普通打卡 */
    public static final int CHECKIN_TYPE_NORMAL = 1;
    
    /** 打卡类型：笔记关联打卡 */
    public static final int CHECKIN_TYPE_NOTE = 2;
    
    /** 打卡类型：路线打卡 */
    public static final int CHECKIN_TYPE_ROUTE = 3;

    /** 打卡方式：手动打卡 */
    public static final int CHECKIN_METHOD_MANUAL = 1;
    
    /** 打卡方式：围栏自动打卡 */
    public static final int CHECKIN_METHOD_AUTO = 2;
    
    /** 打卡方式：扫码打卡 */
    public static final int CHECKIN_METHOD_QR = 3;
}
