package com.im.backend.modules.geofencing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户围栏状态实体类
 * 记录用户与各围栏的实时状态关系
 * 状态机: OUTSIDE → ENTERING → INSIDE → DWELLING → EXITING → OUTSIDE
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_geofence_state")
public class UserGeofenceState {
    
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 围栏ID */
    private Long geofenceId;
    
    /** 当前状态: OUTSIDE-外部, ENTERING-进入中, INSIDE-内部, DWELLING-停留中, EXITING-离开中 */
    private String currentState;
    
    /** 上一次状态 */
    private String previousState;
    
    /** 状态最后更新时间 */
    private LocalDateTime stateUpdateTime;
    
    /** 首次进入时间 */
    private LocalDateTime firstEnterTime;
    
    /** 最后进入时间 */
    private LocalDateTime lastEnterTime;
    
    /** 最后离开时间 */
    private LocalDateTime lastExitTime;
    
    /** 累计停留时间（分钟） */
    private Integer totalDwellMinutes;
    
    /** 本次进入后的停留时间（分钟） */
    private Integer currentDwellMinutes;
    
    /** 进入次数统计 */
    private Integer enterCount;
    
    /** 离开次数统计 */
    private Integer exitCount;
    
    /** 触发次数统计 */
    private Integer triggerCount;
    
    /** 最后一次触发时间 */
    private LocalDateTime lastTriggerTime;
    
    /** 当前位置经度 */
    private BigDecimal currentLongitude;
    
    /** 当前位置纬度 */
    private BigDecimal currentLatitude;
    
    /** 位置精度（米） */
    private BigDecimal locationAccuracy;
    
    /** 定位来源: GPS, NETWORK, WIFI, PASSIVE */
    private String locationSource;
    
    /** 位置上报时间 */
    private LocalDateTime locationReportTime;
    
    /** 置信度评分: 0-100 */
    private Integer confidenceScore;
    
    /** 是否怀疑位置作弊 */
    private Boolean suspectedSpoofing;
    
    /** 距离围栏边界距离（米），正数在外部，负数在内部 */
    private BigDecimal distanceToBoundary;
    
    /** 最近边界点经度 */
    private BigDecimal nearestBoundaryLongitude;
    
    /** 最近边界点纬度 */
    private BigDecimal nearestBoundaryLatitude;
    
    /** 是否订阅该围栏 */
    private Boolean subscribed;
    
    /** 订阅时间 */
    private LocalDateTime subscribeTime;
    
    /** 个性化欢迎消息已发送 */
    private Boolean welcomeMessageSent;
    
    /** 感谢消息已发送 */
    private Boolean thankYouMessageSent;
    
    /** 本次会话ID，用于区分不同到店会话 */
    private String sessionId;
    
    /** 会话开始时间 */
    private LocalDateTime sessionStartTime;
    
    /** 会话结束时间 */
    private LocalDateTime sessionEndTime;
    
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
