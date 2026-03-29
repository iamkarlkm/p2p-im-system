package com.im.backend.modules.location.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 位置共享成员实体
 * 记录参与位置共享的用户信息
 */
@Data
@TableName("location_sharing_member")
public class LocationSharingMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属会话ID
     */
    private String sessionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称(快照)
     */
    private String userNickname;

    /**
     * 用户头像(快照)
     */
    private String userAvatar;

    /**
     * 成员角色: CREATOR-创建者, MEMBER-普通成员
     */
    private String role;

    /**
     * 加入时间
     */
    private LocalDateTime joinedAt;

    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActiveAt;

    /**
     * 成员状态: ACTIVE-活跃, PAUSED-暂停, LEFT-已离开
     */
    private String status;

    /**
     * 最后位置纬度
     */
    private Double lastLat;

    /**
     * 最后位置经度
     */
    private Double lastLng;

    /**
     * 最后位置更新时间
     */
    private LocalDateTime lastLocationTime;

    /**
     * 最后位置精度(米)
     */
    private Double lastAccuracy;

    /**
     * 是否已到达目的地
     */
    private Boolean hasArrived;

    /**
     * 到达时间
     */
    private LocalDateTime arrivedAt;

    /**
     * 距离目的地(米)
     */
    private Integer distanceToDestination;

    /**
     * 预计到达时间(ETA)
     */
    private LocalDateTime estimatedArrivalTime;

    /**
     * 电池电量(百分比)
     */
    private Integer batteryLevel;

    /**
     * 移动速度(m/s)
     */
    private Double speed;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
