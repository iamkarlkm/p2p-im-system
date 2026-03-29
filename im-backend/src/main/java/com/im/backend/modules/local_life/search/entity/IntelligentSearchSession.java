package com.im.backend.modules.local_life.search.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 智能搜索会话实体
 * 管理多轮搜索对话的上下文
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_search_session")
public class IntelligentSearchSession {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 会话开始时间
     */
    private LocalDateTime startTime;

    /**
     * 会话最后活跃时间
     */
    private LocalDateTime lastActiveTime;

    /**
     * 对话轮次计数
     */
    private Integer roundCount;

    /**
     * 会话状态
     * ACTIVE-活跃, EXPIRED-已过期, COMPLETED-已完成
     */
    private String status;

    /**
     * 会话上下文（JSON格式）
     * 存储位置、偏好、历史约束等信息
     */
    private String contextJson;

    /**
     * 当前累积的意图
     */
    private String accumulatedIntent;

    /**
     * 当前位置经度
     */
    private Double currentLongitude;

    /**
     * 当前位置纬度
     */
    private Double currentLatitude;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 会话来源
     */
    private String source;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

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
    private List<IntelligentSearchQuery> queryHistory;

    @TableField(exist = false)
    private SearchContext context;
}
