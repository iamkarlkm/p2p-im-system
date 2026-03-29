package com.im.server.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 好友请求实体
 */
@Data
public class FriendRequest {
    
    /**
     * 请求ID
     */
    private Long id;
    
    /**
     * 发送者用户ID
     */
    private Long fromUserId;
    
    /**
     * 接收者用户ID
     */
    private Long toUserId;
    
    /**
     * 请求消息
     */
    private String message;
    
    /**
     * 请求状态: 0-待处理 1-已同意 2-已拒绝 3-已过期
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 处理时间
     */
    private LocalDateTime handleTime;
    
    // 关联的用户信息（非数据库字段）
    private User fromUser;
    private User toUser;
    
    /**
     * 状态常量
     */
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_ACCEPTED = 1;
    public static final int STATUS_REJECTED = 2;
    public static final int STATUS_EXPIRED = 3;
}
