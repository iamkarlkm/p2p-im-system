package com.im.backend.modules.merchant.order.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手配送位置响应
 */
@Data
public class RiderLocationResponse {

    /**
     * 骑手ID
     */
    private Long riderId;

    /**
     * 骑手姓名
     */
    private String riderName;

    /**
     * 骑手电话
     */
    private String riderPhone;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 位置精度
     */
    private Double accuracy;

    /**
     * 移动速度(m/s)
     */
    private Double speed;

    /**
     * 移动方向
     */
    private Double heading;

    /**
     * 手机电量(%)
     */
    private Integer batteryLevel;

    /**
     * 距离商家(米)
     */
    private Double distanceToMerchant;

    /**
     * 距离用户(米)
     */
    private Double distanceToUser;

    /**
     * 配送状态
     */
    private Integer deliveryStatus;

    /**
     * 配送状态描述
     */
    private String deliveryStatusDesc;

    /**
     * 预计送达时间
     */
    private LocalDateTime estimatedArrivalTime;

    /**
     * 位置更新时间
     */
    private LocalDateTime locationTime;
}
