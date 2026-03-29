package com.im.dto.customer_service;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 开始会话请求DTO
 * 功能 #319 - 智能客服与工单管理系统
 */
@Data
public class StartSessionRequest {
    
    /** 用户ID */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /** 会话类型：1-机器人 2-人工 */
    @NotNull(message = "会话类型不能为空")
    private Integer sessionType;
    
    /** 关联工单ID */
    private Long ticketId;
    
    /** 初始消息内容 */
    private String initialMessage;
}
