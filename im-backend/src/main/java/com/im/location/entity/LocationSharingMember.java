package com.im.location.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 位置共享成员实体
 * 记录参与位置共享的用户状态和位置信息
 */
@Data
@TableName("location_sharing_member")
public class LocationSharingMember {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 关联会话ID
     */
    private String sessionId;
    
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
     * 成员状态: 0-待加入 1-已加入 2-已离开 3-已移除
     */
    private Integer memberStatus;
    
    /**
     * 当前经度
     */
    private Double longitude;
    
    /**
     * 当前纬度
     */
    private Double latitude;
    
    /**
     * 位置精度(米)
     */
    private Double accuracy;
    
    /**
     * 海拔高度
     */
    private Double altitude;
    
    /**
     * 移动速度(m/s)
     */
    private Double speed;
    
    /**
     * 移动方向(0-360度)
     */
    private Double bearing;
    
    /**
     * 位置更新时间
     */
    private LocalDateTime locationUpdateTime;
    
    /**
     * 进入围栏状态: 0-未进入 1-已进入
     */
    private Integer inGeofence;
    
    /**
     * 进入围栏时间
     */
    private LocalDateTime enterGeofenceTime;
    
    /**
     * 到达状态: 0-未到达 1-已到达
     */
    private Integer arrivedStatus;
    
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
    
    /**
     * 离开时间
     */
    private LocalDateTime leaveTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
