package com.im.location.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 位置共享会话实体
 * 管理好友/群组间的实时位置共享
 */
@Data
@TableName("location_sharing_session")
public class LocationSharingSession {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 会话唯一标识
     */
    private String sessionId;
    
    /**
     * 会话类型: 1-好友共享 2-群组共享
     */
    private Integer sessionType;
    
    /**
     * 关联群组ID(群组共享时)
     */
    private Long groupId;
    
    /**
     * 发起人用户ID
     */
    private Long creatorId;
    
    /**
     * 会话标题
     */
    private String title;
    
    /**
     * 目的地名称
     */
    private String destinationName;
    
    /**
     * 目的地经度
     */
    private Double destLongitude;
    
    /**
     * 目的地纬度
     */
    private Double destLatitude;
    
    /**
     * 地理围栏半径(米)
     */
    private Integer geofenceRadius;
    
    /**
     * 位置精度级别: 1-精确 2-商圈级 3-城市级
     */
    private Integer precisionLevel;
    
    /**
     * 会话状态: 0-已创建 1-进行中 2-已暂停 3-已结束
     */
    private Integer status;
    
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    
    /**
     * 位置更新间隔(秒)
     */
    private Integer updateInterval;
    
    /**
     * 参与人数
     */
    private Integer participantCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
