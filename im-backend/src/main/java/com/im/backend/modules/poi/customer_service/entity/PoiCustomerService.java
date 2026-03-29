package com.im.backend.modules.poi.customer_service.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * POI商家客服实体
 * 管理POI商户接入的客服坐席信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("poi_customer_service")
public class PoiCustomerService {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * POI商户ID
     */
    private Long poiId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 客服用户ID（关联用户表）
     */
    private Long agentUserId;

    /**
     * 客服昵称
     */
    private String agentNickname;

    /**
     * 客服工号
     */
    private String agentNo;

    /**
     * 客服头像
     */
    private String agentAvatar;

    /**
     * 客服状态: ONLINE-在线, BUSY-忙碌, OFFLINE-离线
     */
    private String status;

    /**
     * 客服类型: HUMAN-人工, ROBOT-机器人, HYBRID-混合
     */
    private String agentType;

    /**
     * 最大并发会话数
     */
    private Integer maxConcurrentSessions;

    /**
     * 当前会话数
     */
    private Integer currentSessions;

    /**
     * 服务评分(1-5)
     */
    private Double rating;

    /**
     * 服务次数
     */
    private Integer serviceCount;

    /**
     * 平均响应时间(秒)
     */
    private Integer avgResponseTime;

    /**
     * 技能标签: 逗号分隔
     */
    private String skillTags;

    /**
     * 工作时间配置(JSON)
     */
    private String workTimeConfig;

    /**
     * 自动回复配置(JSON)
     */
    private String autoReplyConfig;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 最后在线时间
     */
    private LocalDateTime lastOnlineTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
