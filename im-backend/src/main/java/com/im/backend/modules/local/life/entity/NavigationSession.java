package com.im.backend.modules.local.life.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 实时导航会话实体类
 * Real-time Navigation Session Entity
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_navigation_session")
public class NavigationSession implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话唯一标识
     */
    @TableField("session_id")
    private String sessionId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 关联的路线ID
     */
    @TableField("route_id")
    private Long routeId;

    /**
     * 当前经度
     */
    @TableField("current_lng")
    private BigDecimal currentLng;

    /**
     * 当前纬度
     */
    @TableField("current_lat")
    private BigDecimal currentLat;

    /**
     * 当前所在道路名称
     */
    @TableField("current_road")
    private String currentRoad;

    /**
     * 当前速度（km/h）
     */
    @TableField("current_speed")
    private Integer currentSpeed;

    /**
     * 当前航向角度（0-360度）
     */
    @TableField("heading")
    private Integer heading;

    /**
     * 剩余距离（米）
     */
    @TableField("remaining_distance")
    private Integer remainingDistance;

    /**
     * 剩余时间（秒）
     */
    @TableField("remaining_duration")
    private Integer remainingDuration;

    /**
     * 当前步骤索引
     */
    @TableField("current_step_index")
    private Integer currentStepIndex;

    /**
     * 是否偏航
     */
    @TableField("is_off_route")
    private Boolean isOffRoute;

    /**
     * 偏航次数
     */
    @TableField("off_route_count")
    private Integer offRouteCount;

    /**
     * 是否模拟导航
     */
    @TableField("is_simulated")
    private Boolean isSimulated;

    /**
     * 导航开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 导航结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 会话状态：NAVIGATING-导航中, PAUSED-暂停, COMPLETED-已完成, CANCELLED-已取消
     */
    @TableField("status")
    private String status;

    /**
     * 总里程（米）
     */
    @TableField("total_distance")
    private Integer totalDistance;

    /**
     * 实际耗时（秒）
     */
    @TableField("actual_duration")
    private Integer actualDuration;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 最后位置更新时间
     */
    @TableField("last_location_update")
    private LocalDateTime lastLocationUpdate;
}
