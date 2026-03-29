package com.im.backend.modules.geofencing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 围栏触发事件实体类
 * 记录所有围栏触发事件的详细日志
 * 用于数据分析、消息推送、个性化服务
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("geofence_trigger_event")
public class GeofenceTriggerEvent {
    
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    
    /** 事件唯一标识 */
    private String eventId;
    
    /** 用户ID */
    private Long userId;
    
    /** 围栏ID */
    private Long geofenceId;
    
    /** 父围栏ID（用于层级围栏） */
    private Long parentGeofenceId;
    
    /** 关联商户ID */
    private Long merchantId;
    
    /** 关联POI ID */
    private Long poiId;
    
    /** 事件类型: ENTER-进入, EXIT-离开, DWELL-停留超时 */
    private String eventType;
    
    /** 触发时间 */
    private LocalDateTime triggerTime;
    
    /** 触发位置经度 */
    private BigDecimal triggerLongitude;
    
    /** 触发位置纬度 */
    private BigDecimal triggerLatitude;
    
    /** 位置精度（米） */
    private BigDecimal locationAccuracy;
    
    /** 定位来源 */
    private String locationSource;
    
    /** 进入时间（EXIT事件时记录） */
    private LocalDateTime enterTime;
    
    /** 停留时长（分钟） */
    private Integer dwellDuration;
    
    /** 本次会话ID */
    private String sessionId;
    
    /** 触发状态: SUCCESS-成功, FAILED-失败, PENDING-处理中, RETRY-重试 */
    private String triggerStatus;
    
    /** 消息推送状态: PENDING-待推送, SENT-已发送, FAILED-发送失败, READ-已读 */
    private String pushStatus;
    
    /** 推送消息ID */
    private String pushMessageId;
    
    /** 推送时间 */
    private LocalDateTime pushTime;
    
    /** 消息模板ID */
    private Long messageTemplateId;
    
    /** 个性化消息内容 */
    private String personalizedMessage;
    
    /** 优惠券ID（如有关联） */
    private Long couponId;
    
    /** 活动ID（如有关联） */
    private Long activityId;
    
    /** 设备信息: iOS/Android */
    private String deviceType;
    
    /** 应用版本 */
    private String appVersion;
    
    /** 网络类型: WIFI/4G/5G */
    private String networkType;
    
    /** 触发置信度: 0-100 */
    private Integer confidenceScore;
    
    /** 处理耗时（毫秒） */
    private Integer processTimeMs;
    
    /** 是否重复触发（冷却期内） */
    private Boolean duplicateTrigger;
    
    /** 重复原因 */
    private String duplicateReason;
    
    /** 失败原因 */
    private String failReason;
    
    /** 重试次数 */
    private Integer retryCount;
    
    /** 扩展数据JSON */
    private String extraData;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /** 删除标记 */
    @TableLogic
    private Boolean deleted;
}
