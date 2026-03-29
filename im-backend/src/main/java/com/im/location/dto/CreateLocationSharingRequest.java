package com.im.location.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建位置共享会话请求
 */
@Data
public class CreateLocationSharingRequest {
    
    /**
     * 会话类型: 1-好友共享 2-群组共享
     */
    @NotNull(message = "会话类型不能为空")
    private Integer sessionType;
    
    /**
     * 关联群组ID(群组共享时)
     */
    private Long groupId;
    
    /**
     * 会话标题
     */
    @NotBlank(message = "会话标题不能为空")
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
    @NotNull(message = "位置精度级别不能为空")
    private Integer precisionLevel;
    
    /**
     * 有效期(小时)
     */
    private Integer validityHours;
    
    /**
     * 位置更新间隔(秒)
     */
    private Integer updateInterval;
    
    /**
     * 邀请成员列表
     */
    private List<Long> inviteeIds;
}
