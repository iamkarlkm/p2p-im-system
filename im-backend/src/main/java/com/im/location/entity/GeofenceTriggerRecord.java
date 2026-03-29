package com.im.location.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 地理围栏触发记录实体
 * 记录围栏进入/离开/停留事件
 */
@Data
@TableName("geofence_trigger_record")
public class GeofenceTriggerRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 关联围栏ID
     */
    private String geofenceId;
    
    /**
     * 关联会话ID
     */
    private String sessionId;
    
    /**
     * 触发用户ID
     */
    private Long userId;
    
    /**
     * 触发类型: 1-进入 2-离开 3-停留
     */
    private Integer triggerType;
    
    /**
     * 触发时经度
     */
    private Double longitude;
    
    /**
     * 触发时纬度
     */
    private Double latitude;
    
    /**
     * 触发时精度
     */
    private Double accuracy;
    
    /**
     * 触发时间
     */
    private LocalDateTime triggerTime;
    
    /**
     * 停留时长(分钟，停留触发时)
     */
    private Integer dwellDuration;
    
    /**
     * 处理状态: 0-未处理 1-已处理 2-已忽略
     */
    private Integer processStatus;
    
    /**
     * 关联消息ID(触发的IM消息)
     */
    private Long messageId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
