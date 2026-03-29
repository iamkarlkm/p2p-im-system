package com.im.location.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 位置共享会话详情响应
 */
@Data
public class LocationSharingSessionResponse {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 会话类型
     */
    private Integer sessionType;
    
    /**
     * 会话类型描述
     */
    private String sessionTypeDesc;
    
    /**
     * 发起人信息
     */
    private UserBriefInfo creator;
    
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
     * 地理围栏半径
     */
    private Integer geofenceRadius;
    
    /**
     * 位置精度级别
     */
    private Integer precisionLevel;
    
    /**
     * 位置精度描述
     */
    private String precisionLevelDesc;
    
    /**
     * 会话状态
     */
    private Integer status;
    
    /**
     * 会话状态描述
     */
    private String statusDesc;
    
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    
    /**
     * 参与人数
     */
    private Integer participantCount;
    
    /**
     * 位置更新间隔
     */
    private Integer updateInterval;
    
    /**
     * 成员列表
     */
    private List<LocationSharingMemberResponse> members;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

/**
 * 用户简要信息
 */
@Data
class UserBriefInfo {
    private Long userId;
    private String nickname;
    private String avatar;
}
