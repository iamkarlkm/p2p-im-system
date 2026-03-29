package com.im.entity.customer_service;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 工单评论/备注实体
 * 功能 #319 - 智能客服与工单管理系统
 */
@Data
public class CustomerServiceTicketComment {
    
    /** 评论ID */
    private Long id;
    
    /** 工单ID */
    private Long ticketId;
    
    /** 评论者类型：1-用户 2-客服 3-系统 */
    private Integer commenterType;
    
    /** 评论者ID */
    private Long commenterId;
    
    /** 评论内容 */
    private String content;
    
    /** 评论类型：1-普通备注 2-处理记录 3-内部沟通 */
    private Integer commentType;
    
    /** 附件URL（JSON数组） */
    private String attachments;
    
    /** 是否内部可见 */
    private Integer internalOnly;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 是否删除 */
    private Integer deleted;
}
