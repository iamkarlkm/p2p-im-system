package com.im.entity.geofence;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 群组成员到达状态
 */
@Data
public class MemberArrivalStatus {
    
    /** 用户ID */
    private String userId;
    
    /** 用户昵称 */
    private String nickname;
    
    /** 头像URL */
    private String avatarUrl;
    
    /** 到达状态: PENDING-未到, ARRIVED-已到, LATE-迟到, LEFT-已离开 */
    private String arrivalStatus;
    
    /** 进入时间 */
    private LocalDateTime enterTime;
    
    /** 离开时间 */
    private LocalDateTime leaveTime;
    
    /** 当前经度 */
    private Double longitude;
    
    /** 当前纬度 */
    private Double latitude;
    
    /** 距离中心点距离(米) */
    private Double distanceToCenter;
}
