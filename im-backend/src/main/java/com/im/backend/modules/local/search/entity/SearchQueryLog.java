package com.im.backend.modules.local.search.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.im.backend.common.BaseEntity;

import java.time.LocalDateTime;

/**
 * 搜索查询记录实体
 * 记录用户的搜索行为和意图分析结果
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("search_query_log")
public class SearchQueryLog extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 原始查询文本
     */
    private String originalQuery;

    /**
     * 标准化后的查询
     */
    private String normalizedQuery;

    /**
     * 搜索意图类型
     */
    private String intentType;

    /**
     * 意图置信度 0-100
     */
    private Integer intentConfidence;

    /**
     * 识别的实体类型
     */
    private String recognizedEntities;

    /**
     * 搜索上下文会话ID
     */
    private String sessionId;

    /**
     * 是否多轮对话
     */
    private Boolean isMultiTurn;

    /**
     * 上一查询ID
     */
    private Long previousQueryId;

    /**
     * 用户位置纬度
     */
    private Double userLat;

    /**
     * 用户位置经度
     */
    private Double userLng;

    /**
     * 搜索结果数量
     */
    private Integer resultCount;

    /**
     * 用户点击结果数
     */
    private Integer clickCount;

    /**
     * 搜索响应时间(ms)
     */
    private Integer responseTime;

    /**
     * 搜索来源：TEXT-文本 VOICE-语音
     */
    private String searchSource;

    /**
     * 搜索时间
     */
    private LocalDateTime searchTime;
}
