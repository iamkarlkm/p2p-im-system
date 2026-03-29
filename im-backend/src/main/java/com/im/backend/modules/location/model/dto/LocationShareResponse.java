package com.im.backend.modules.location.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 位置共享会话响应DTO
 */
@Data
public class LocationShareResponse {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 会话类型
     */
    private String sessionType;

    /**
     * 创建者用户ID
     */
    private Long creatorId;

    /**
     * 创建者昵称
     */
    private String creatorNickname;

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
     * 目的地围栏半径
     */
    private Integer destinationRadius;

    /**
     * 位置精度
     */
    private String locationPrecision;

    /**
     * 会话状态
     */
    private String status;

    /**
     * 成员列表
     */
    private List<LocationShareMemberDTO> members;

    /**
     * 当前成员数
     */
    private Integer memberCount;

    /**
     * 预计结束时间
     */
    private LocalDateTime expectedEndTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
