package com.im.dto.customer_service;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 会话响应DTO
 * 功能 #319 - 智能客服与工单管理系统
 */
@Data
public class SessionResponse {
    
    /** 会话ID */
    private Long id;
    
    /** 会话编号 */
    private String sessionNo;
    
    /** 用户ID */
    private Long userId;
    
    /** 用户昵称 */
    private String userNickname;
    
    /** 用户头像 */
    private String userAvatar;
    
    /** 客服ID */
    private Long agentId;
    
    /** 客服昵称 */
    private String agentName;
    
    /** 客服头像 */
    private String agentAvatar;
    
    /** 会话类型 */
    private Integer sessionType;
    
    /** 会话类型名称 */
    private String sessionTypeName;
    
    /** 会话状态 */
    private Integer status;
    
    /** 会话状态名称 */
    private String statusName;
    
    /** 关联工单ID */
    private Long ticketId;
    
    /** 开始时间 */
    private LocalDateTime startTime;
    
    /** 最后活动时间 */
    private LocalDateTime lastActivityTime;
    
    /** 未读消息数 */
    private Integer unreadCount;
    
    /** 最后一条消息 */
    private String lastMessage;
}
