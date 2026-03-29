package com.im.backend.modules.merchant.order.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 骑手位置卡片数据(嵌入IM消息)
 */
@Data
public class RiderLocationCardDTO {

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
     * 骑手头像
     */
    private String riderAvatar;

    /**
     * 当前经度
     */
    private BigDecimal longitude;

    /**
     * 当前纬度
     */
    private BigDecimal latitude;

    /**
     * 配送状态
     */
    private Integer deliveryStatus;

    /**
     * 配送状态描述
     */
    private String deliveryStatusDesc;

    /**
     * 距离目的地(米)
     */
    private Double distanceToDestination;

    /**
     * 预计到达时间(分钟)
     */
    private Integer estimatedMinutes;
}
