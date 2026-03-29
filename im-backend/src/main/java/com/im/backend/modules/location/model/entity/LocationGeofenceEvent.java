package com.im.backend.modules.location.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 位置围栏事件实体
 * 记录基于位置触发的围栏事件
 */
@Data
@TableName("location_geofence_event")
public class LocationGeofenceEvent {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属会话ID
     */
    private String sessionId;

    /**
     * 触发用户ID
     */
    private Long userId;

    /**
     * 事件类型: ENTER_DESTINATION-到达目的地, LEAVE_DESTINATION-离开目的地,
     * ENTER_SAFE_ZONE-进入安全区, LEAVE_SAFE_ZONE-离开安全区,
     * ENTER_SHARED_AREA-进入共享区, LEAVE_SHARED_AREA-离开共享区
     */
    private String eventType;

    /**
     * 围栏ID(关联的围栏)
     */
    private String geofenceId;

    /**
     * 围栏名称
     */
    private String geofenceName;

    /**
     * 触发时纬度
     */
    private Double triggerLat;

    /**
     * 触发时经度
     */
    private Double triggerLng;

    /**
     * 触发时距离围栏中心(米)
     */
    private Double distanceToCenter;

    /**
     * 触发时间
     */
    private LocalDateTime triggerTime;

    /**
     * 是否已发送通知
     */
    private Boolean notificationSent;

    /**
     * 通知发送时间
     */
    private LocalDateTime notificationTime;

    /**
     * 通知接收者列表(JSON数组)
     */
    private String notificationRecipients;

    /**
     * IM消息ID(已发送的消息)
     */
    private String imMessageId;

    /**
     * 事件处理状态: PENDING-待处理, PROCESSED-已处理, IGNORED-已忽略
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
