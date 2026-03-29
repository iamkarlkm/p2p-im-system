package com.im.dto.customer_service;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 工单响应DTO
 * 功能 #319 - 智能客服与工单管理系统
 */
@Data
public class TicketResponse {
    
    /** 工单ID */
    private Long id;
    
    /** 工单编号 */
    private String ticketNo;
    
    /** 用户ID */
    private Long userId;
    
    /** 用户昵称 */
    private String userNickname;
    
    /** 商户ID */
    private Long merchantId;
    
    /** 商户名称 */
    private String merchantName;
    
    /** 订单ID */
    private Long orderId;
    
    /** 工单标题 */
    private String title;
    
    /** 工单内容 */
    private String content;
    
    /** 工单类型 */
    private Integer type;
    
    /** 工单类型名称 */
    private String typeName;
    
    /** 工单状态 */
    private Integer status;
    
    /** 工单状态名称 */
    private String statusName;
    
    /** 优先级 */
    private Integer priority;
    
    /** 优先级名称 */
    private String priorityName;
    
    /** 工单来源 */
    private Integer source;
    
    /** 分配客服ID */
    private Long assigneeId;
    
    /** 分配客服昵称 */
    private String assigneeName;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** SLA截止时间 */
    private LocalDateTime slaDeadline;
    
    /** 解决时间 */
    private LocalDateTime resolveTime;
}
