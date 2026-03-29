package com.im.backend.modules.delivery.dto;

import lombok.Data;

/**
 * 骑手负载均衡VO
 */
@Data
public class RiderLoadBalanceVO {

    private Long riderId;
    private String riderName;
    private Integer currentOrders;
    private Integer maxOrders;
    private Double loadFactor;
    private String suggestion; // INCREASE-建议增加, DECREASE-建议减少, MAINTAIN-维持
    private Integer suggestedOrders;
}
