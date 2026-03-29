package com.im.location.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 位置共享成员响应
 */
@Data
public class LocationSharingMemberResponse {
    
    /**
     * 成员ID
     */
    private Long memberId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 用户头像
     */
    private String avatar;
    
    /**
     * 成员状态
     */
    private Integer memberStatus;
    
    /**
     * 成员状态描述
     */
    private String memberStatusDesc;
    
    /**
     * 当前经度
     */
    private Double longitude;
    
    /**
     * 当前纬度
     */
    private Double latitude;
    
    /**
     * 位置精度
     */
    private Double accuracy;
    
    /**
     * 移动速度
     */
    private Double speed;
    
    /**
     * 移动方向
     */
    private Double bearing;
    
    /**
     * 位置更新时间
     */
    private LocalDateTime locationUpdateTime;
    
    /**
     * 是否进入围栏
     */
    private Boolean inGeofence;
    
    /**
     * 进入围栏时间
     */
    private LocalDateTime enterGeofenceTime;
    
    /**
     * 到达状态
     */
    private Integer arrivedStatus;
    
    /**
     * 到达状态描述
     */
    private String arrivedStatusDesc;
    
    /**
     * 预计到达时间(分钟)
     */
    private Integer etaMinutes;
    
    /**
     * 电量百分比
     */
    private Integer batteryLevel;
    
    /**
     * 加入时间
     */
    private LocalDateTime joinTime;
}
