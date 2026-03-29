package com.im.backend.modules.location.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 位置共享会话实体
 * 管理用户或群组间的实时位置共享会话
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
     * 会话类型: USER-用户间, GROUP-群组内
     */
    private String sessionType;

    /**
     * 创建者用户ID
     */
    private Long creatorId;

    /**
     * 目标用户ID(用户间共享时)
     */
    private Long targetUserId;

    /**
     * 群组ID(群组共享时)
     */
    private Long groupId;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 目的地名称
     */
    private String destinationName;

    /**
     * 目的地纬度
     */
    private Double destinationLat;

    /**
     * 目的地经度
     */
    private Double destinationLng;

    /**
     * 目的地围栏半径(米)
     */
    private Integer destinationRadius;

    /**
     * 位置精度: HIGH-精确, AREA-商圈级, CITY-城市级
     */
    private String locationPrecision;

    /**
     * 预计结束时间
     */
    private LocalDateTime expectedEndTime;

    /**
     * 实际结束时间
     */
    private LocalDateTime actualEndTime;

    /**
     * 会话状态: ACTIVE-进行中, PAUSED-暂停, ENDED-已结束
     */
    private String status;

    /**
     * 是否开启到达通知
     */
    private Boolean arrivalNotificationEnabled;

    /**
     * 是否开启离开通知
     */
    private Boolean departureNotificationEnabled;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
