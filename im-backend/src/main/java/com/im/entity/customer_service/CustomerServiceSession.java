package com.im.entity.customer_service;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客服会话实体
 * 功能 #319 - 智能客服与工单管理系统
 */
@Data
public class CustomerServiceSession {
    
    /** 会话ID */
    private Long id;
    
    /** 会话编号 */
    private String sessionNo;
    
    /** 用户ID */
    private Long userId;
    
    /** 客服ID（机器人时为null） */
    private Long agentId;
    
    /** 会话类型：1-机器人 2-人工 */
    private Integer sessionType;
    
    /** 会话状态：0-排队中 1-进行中 2-等待回复 3-已结束 */
    private Integer status;
    
    /** 关联工单ID */
    private Long ticketId;
    
    /** 开始时间 */
    private LocalDateTime startTime;
    
    /** 结束时间 */
    private LocalDateTime endTime;
    
    /** 最后活动时间 */
    private LocalDateTime lastActivityTime;
    
    /** 用户满意度评分 */
    private Integer satisfactionScore;
    
    /** 满意度评价内容 */
    private String satisfactionComment;
    
    /** 会话标签（逗号分隔） */
    private String tags;
    
    /** 意图类型 */
    private String intentType;
    
    /** 是否转人工 */
    private Integer transferredToHuman;
    
    /** 转人工原因 */
    private String transferReason;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 是否删除 */
    private Integer deleted;
}
