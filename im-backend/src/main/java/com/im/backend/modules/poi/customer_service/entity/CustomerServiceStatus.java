package com.im.backend.modules.poi.customer_service.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客服在线状态记录(缓存实体,Redis存储)
 */
@Data
public class CustomerServiceStatus {

    /**
     * 客服ID
     */
    private Long agentId;

    /**
     * POI商户ID
     */
    private Long poiId;

    /**
     * 状态: ONLINE-在线, BUSY-忙碌, AWAY-离开, OFFLINE-离线
     */
    private String status;

    /**
     * 当前会话数
     */
    private Integer currentSessions;

    /**
     * 最大会话数
     */
    private Integer maxSessions;

    /**
     * 最后心跳时间
     */
    private LocalDateTime lastHeartbeat;

    /**
     * 最后活动时间
     */
    private LocalDateTime lastActiveTime;

    /**
     * 接入时间
     */
    private LocalDateTime loginTime;

    /**
     * 服务的会话ID列表
     */
    private String activeSessionIds;

    /**
     * 技能标签
     */
    private String skillTags;
}
