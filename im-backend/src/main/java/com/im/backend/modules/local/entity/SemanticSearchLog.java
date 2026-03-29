package com.im.backend.modules.local.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 语义搜索日志实体
 * 记录用户的语义搜索请求和结果，用于优化搜索算法
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_semantic_search_log")
public class SemanticSearchLog {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 搜索ID
     */
    private String searchId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 原始查询
     */
    private String originalQuery;

    /**
     * 标准化查询
     */
    private String normalizedQuery;

    /**
     * 识别到的意图
     */
    private String intent;

    /**
     * 意图置信度
     */
    private BigDecimal intentConfidence;

    /**
     * 提取的实体JSON
     */
    private String entitiesJson;

    /**
     * 用户位置纬度
     */
    private BigDecimal latitude;

    /**
     * 用户位置经度
     */
    private BigDecimal longitude;

    /**
     * 搜索半径
     */
    private Integer radius;

    /**
     * 搜索结果数
     */
    private Integer resultCount;

    /**
     * 是否零结果
     */
    private Boolean zeroResult;

    /**
     * 用户点击的POI ID
     */
    private String clickedPoiId;

    /**
     * 点击位置
     */
    private Integer clickPosition;

    /**
     * 搜索耗时（毫秒）
     */
    private Integer searchTimeMs;

    /**
     * 用户满意度：1-5
     */
    private Integer userSatisfaction;

    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * 搜索场景
     */
    private String scene;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 是否语音输入
     */
    private Boolean voiceInput;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
