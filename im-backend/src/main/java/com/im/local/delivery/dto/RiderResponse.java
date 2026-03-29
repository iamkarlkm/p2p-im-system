package com.im.local.delivery.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 骑手信息响应
 */
@Data
public class RiderResponse {
    
    /** 骑手ID */
    private Long id;
    
    /** 骑手姓名 */
    private String realName;
    
    /** 手机号 */
    private String phone;
    
    /** 工号 */
    private String employeeNo;
    
    /** 当前纬度 */
    private BigDecimal currentLat;
    
    /** 当前经度 */
    private BigDecimal currentLng;
    
    /** 位置更新时间 */
    private LocalDateTime locationUpdatedAt;
    
    /** 骑手状态 */
    private Integer status;
    
    /** 状态名称 */
    private String statusName;
    
    /** 今日接单数 */
    private Integer todayOrderCount;
    
    /** 评分 */
    private BigDecimal rating;
    
    /** 总配送单数 */
    private Integer totalDeliveries;
    
    /** 头像URL */
    private String avatarUrl;
    
    /** 当前配送中的订单 */
    private List<DeliveryOrderResponse> currentOrders;
}
