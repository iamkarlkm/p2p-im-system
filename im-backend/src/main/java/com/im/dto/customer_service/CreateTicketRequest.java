package com.im.dto.customer_service;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建工单请求DTO
 * 功能 #319 - 智能客服与工单管理系统
 */
@Data
public class CreateTicketRequest {
    
    /** 用户ID */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /** 商户ID */
    private Long merchantId;
    
    /** 订单ID */
    private Long orderId;
    
    /** 工单标题 */
    @NotBlank(message = "工单标题不能为空")
    private String title;
    
    /** 工单内容 */
    @NotBlank(message = "工单内容不能为空")
    private String content;
    
    /** 工单类型：1-咨询 2-投诉 3-退款 4-售后 */
    @NotNull(message = "工单类型不能为空")
    private Integer type;
    
    /** 优先级：1-低 2-中 3-高 4-紧急 */
    private Integer priority;
    
    /** 工单来源：1-APP 2-小程序 3-电话 4-邮件 */
    @NotNull(message = "工单来源不能为空")
    private Integer source;
    
    /** 扩展数据 */
    private String extraData;
}
