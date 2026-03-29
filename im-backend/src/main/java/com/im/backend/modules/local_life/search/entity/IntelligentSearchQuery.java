package com.im.backend.modules.local_life.search.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 智能搜索查询实体
 * 记录用户搜索查询历史与分析结果
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_intelligent_search_query")
public class IntelligentSearchQuery {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 原始搜索查询文本
     */
    private String rawQuery;

    /**
     * 清洗后的查询文本
     */
    private String cleanedQuery;

    /**
     * 搜索意图类型
     * NAVIGATION-导航, GROUP_BUY-团购, RESERVATION-预约, PRICE_COMPARE-比价, INFO-了解详情
     */
    private String intentType;

    /**
     * 意图置信度 (0.0-1.0)
     */
    private Double intentConfidence;

    /**
     * 提取的实体（JSON格式）
     * { "location": "附近", "category": "火锅", "price_range": "100-200" }
     */
    private String extractedEntities;

    /**
     * 当前对话轮次
     */
    private Integer dialogRound;

    /**
     * 会话ID（多轮对话标识）
     */
    private String sessionId;

    /**
     * 上一查询ID（上下文关联）
     */
    private Long prevQueryId;

    /**
     * 用户当前位置经度
     */
    private Double longitude;

    /**
     * 用户当前位置纬度
     */
    private Double latitude;

    /**
     * 搜索结果数量
     */
    private Integer resultCount;

    /**
     * 用户点击的结果索引
     */
    private Integer clickedIndex;

    /**
     * 是否零结果
     */
    private Boolean isZeroResult;

    /**
     * 搜索耗时(ms)
     */
    private Long searchTimeMs;

    /**
     * 是否语音搜索
     */
    private Boolean isVoiceSearch;

    /**
     * 语音识别的方言类型
     */
    private String dialectType;

    /**
     * 搜索来源（小程序/APP/H5）
     */
    private String source;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    // ============ 非数据库字段 ============

    @TableField(exist = false)
    private List<Map<String, Object>> entityList;

    @TableField(exist = false)
    private IntelligentSearchSession searchSession;
}
