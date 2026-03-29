package com.im.entity.customer_service;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客服工单实体
 * 功能 #319 - 智能客服与工单管理系统
 */
@Data
public class CustomerServiceTicket {
    
    /** 工单ID */
    private Long id;
    
    /** 工单编号 */
    private String ticketNo;
    
    /** 用户ID */
    private Long userId;
    
    /** 商户ID */
    private Long merchantId;
    
    /** 订单ID（关联订单） */
    private Long orderId;
    
    /** 工单标题 */
    private String title;
    
    /** 工单内容 */
    private String content;
    
    /** 工单类型：1-咨询 2-投诉 3-退款 4-售后 */
    private Integer type;
    
    /** 工单状态：0-待处理 1-处理中 2-待确认 3-已解决 4-已关闭 */
    private Integer status;
    
    /** 优先级：1-低 2-中 3-高 4-紧急 */
    private Integer priority;
    
    /** 工单来源：1-APP 2-小程序 3-电话 4-邮件 */
    private Integer source;
    
    /** 分配客服ID */
    private Long assigneeId;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** SLA截止时间 */
    private LocalDateTime slaDeadline;
    
    /** 解决时间 */
    private LocalDateTime resolveTime;
    
    /** 关闭时间 */
    private LocalDateTime closeTime;
    
    /** 是否删除 */
    private Integer deleted;
    
    /** 扩展字段 */
    private String extraData;
}
