package com.im.entity.geofence;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 群组围栏实体类
 * 支持群组场景下的围栏管理
 */
@Data
public class GroupGeoFence {
    
    /** 群组围栏ID */
    private String groupFenceId;
    
    /** 群组ID */
    private String groupId;
    
    /** 群组名称 */
    private String groupName;
    
    /** 围栏类型: MEETING-会议, GATHERING-聚会, ACTIVITY-活动, CARE-看护 */
    private String groupFenceType;
    
    /** 关联围栏ID */
    private String fenceId;
    
    /** 围栏名称 */
    private String fenceName;
    
    /** 活动开始时间 */
    private LocalDateTime activityStartTime;
    
    /** 活动结束时间 */
    private LocalDateTime activityEndTime;
    
    /** 成员到达状态列表 */
    private List<MemberArrivalStatus> memberStatuses;
    
    /** 已到人数 */
    private Integer arrivedCount;
    
    /** 未到人数 */
    private Integer pendingCount;
    
    /** 是否启用到达提醒 */
    private Boolean enableArrivalNotification;
    
    /** 是否启用走散提醒 */
    private Boolean enableSeparationAlert;
    
    /** 走散提醒距离阈值(米) */
    private Integer separationDistanceThreshold;
    
    /** 群组围栏配置 */
    private Map<String, Object> config;
    
    /** 创建者 */
    private String creatorId;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 是否有效 */
    private Boolean active;
}
