package com.im.entity.geofence;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户围栏状态实体类
 * 记录用户与围栏的交互状态
 */
@Data
public class UserFenceStatus {
    
    /** 状态ID */
    private String statusId;
    
    /** 用户ID */
    private String userId;
    
    /** 围栏ID */
    private String fenceId;
    
    /** 围栏状态: OUTSIDE-外部, ENTERING-进入中, INSIDE-内部, DWELLING-停留中, EXITING-离开中 */
    private String status;
    
    /** 首次进入时间 */
    private LocalDateTime firstEnterTime;
    
    /** 最后进入时间 */
    private LocalDateTime lastEnterTime;
    
    /** 最后离开时间 */
    private LocalDateTime lastExitTime;
    
    /** 累计停留次数 */
    private Integer totalVisits;
    
    /** 本次停留开始时间 */
    private LocalDateTime currentDwellStartTime;
    
    /** 本次已停留时长(分钟) */
    private Integer currentDwellMinutes;
    
    /** 累计停留时长(分钟) */
    private Integer totalDwellMinutes;
    
    /** 是否已触发停留超时消息 */
    private Boolean dwellMessageTriggered;
    
    /** 用户当前经度 */
    private Double currentLongitude;
    
    /** 用户当前纬度 */
    private Double currentLatitude;
    
    /** 位置更新时间 */
    private LocalDateTime locationUpdateTime;
    
    /** 距离围栏中心距离(米) */
    private Double distanceToCenter;
    
    /** 扩展数据 */
    private Map<String, Object> extraData;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
}
