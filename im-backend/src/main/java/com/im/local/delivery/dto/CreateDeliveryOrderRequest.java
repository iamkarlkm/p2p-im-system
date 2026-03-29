package com.im.local.delivery.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 创建配送订单请求
 */
@Data
public class CreateDeliveryOrderRequest {
    
    /** 商户订单ID */
    private Long merchantOrderId;
    
    /** 商户ID */
    private Long merchantId;
    
    /** 用户ID */
    private Long userId;
    
    /** 取货地址 */
    private String pickupAddress;
    
    /** 取货纬度 */
    private BigDecimal pickupLat;
    
    /** 取货经度 */
    private BigDecimal pickupLng;
    
    /** 取货联系人 */
    private String pickupContact;
    
    /** 取货电话 */
    private String pickupPhone;
    
    /** 送货地址 */
    private String deliveryAddress;
    
    /** 送货纬度 */
    private BigDecimal deliveryLat;
    
    /** 送货经度 */
    private BigDecimal deliveryLng;
    
    /** 收货人姓名 */
    private String receiverName;
    
    /** 收货人电话 */
    private String receiverPhone;
    
    /** 配送费 */
    private BigDecimal deliveryFee;
    
    /** 物品类型 */
    private String itemType;
    
    /** 物品重量(kg) */
    private BigDecimal itemWeight;
    
    /** 备注 */
    private String remark;
    
    /** 期望送达时间 */
    private String expectDeliverTime;
    
    /** 配送类型：1-即时送, 2-预约送 */
    private Integer deliveryType;
}
