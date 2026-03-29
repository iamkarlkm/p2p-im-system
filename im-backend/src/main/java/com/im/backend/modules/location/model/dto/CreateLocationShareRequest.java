package com.im.backend.modules.location.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 创建位置共享请求DTO
 */
@Data
public class CreateLocationShareRequest {

    /**
     * 会话类型: USER-用户间, GROUP-群组内
     */
    @NotBlank(message = "会话类型不能为空")
    private String sessionType;

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
    private Integer destinationRadius = 100;

    /**
     * 位置精度: HIGH-精确, AREA-商圈级, CITY-城市级
     */
    @NotBlank(message = "位置精度不能为空")
    private String locationPrecision;

    /**
     * 预计结束时间
     */
    private LocalDateTime expectedEndTime;

    /**
     * 是否开启到达通知
     */
    private Boolean arrivalNotificationEnabled = true;

    /**
     * 是否开启离开通知
     */
    private Boolean departureNotificationEnabled = false;
}
