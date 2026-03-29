package com.im.local.delivery.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送站点实体
 * 管理骑手配送站点信息
 */
@Data
public class DeliveryStation {
    
    /** 站点ID */
    private Long id;
    
    /** 站点名称 */
    private String name;
    
    /** 站点编码 */
    private String code;
    
    /** 所在城市 */
    private String city;
    
    /** 所在区域 */
    private String district;
    
    /** 详细地址 */
    private String address;
    
    /** 纬度 */
    private BigDecimal lat;
    
    /** 经度 */
    private BigDecimal lng;
    
    /** 服务半径(米) */
    private Integer serviceRadius;
    
    /** 负责人姓名 */
    private String managerName;
    
    /** 负责人电话 */
    private String managerPhone;
    
    /** 骑手数量 */
    private Integer riderCount;
    
    /** 在线骑手数 */
    private Integer onlineRiderCount;
    
    /** 站点状态：0-关闭, 1-运营中 */
    private Integer status;
    
    /** 运营时间 */
    private String businessHours;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 站点类型：1-直营, 2-加盟 */
    private Integer stationType;
    
    /** 日配送能力 */
    private Integer dailyCapacity;
}
