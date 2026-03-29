package com.im.local.scheduler.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

/**
 * 骑手注册/更新请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterStaffRequest {
    
    /** 骑手姓名 */
    private String staffName;
    
    /** 手机号 */
    private String phone;
    
    /** 骑手类型: 1-专职 2-兼职 3-众包 */
    private Integer staffType;
    
    /** 最大接单量 */
    private Integer maxOrderCapacity;
    
    /** 所属配送区域ID */
    private Long deliveryAreaId;
}
