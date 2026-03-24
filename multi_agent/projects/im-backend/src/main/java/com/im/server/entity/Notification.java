package com.im.server.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 通知实体
 */
@Data
public class Notification {
    
    /**
     * 通知ID
     */
    private Long id;
    
    /**
     * 接收者用户ID
     */
    private Long userId;
    
    /**
     * 通知类型: 1-好友请求 2-好友添加成功 3-群邀请 4-群成员加入 5-系统通知
     */
    private Integer type;
    
    /**
     * 通知标题
     */
    private String title;
    
    /**
     * 通知内容
     */
    private String content;
    
    /**
     * 相关数据ID (如好友请求ID、群ID等)
     */
    private Long relatedId;
    
    /**
     * 状态: 0-未读 1-已读
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    // 关联用户
    private User fromUser;
    private Group group;
    
    /**
     * 通知类型常量
     */
    public static final int TYPE_FRIEND_REQUEST = 1;
    public static final int TYPE_FRIEND_ADDED = 2;
    public static final int TYPE_GROUP_INVITE = 3;
    public static final int TYPE_GROUP_MEMBER_JOINED = 4;
    public static final int TYPE_SYSTEM_NOTIFICATION = 5;
    
    /**
     * 状态常量
     */
    public static final int STATUS_UNREAD = 0;
    public static final int STATUS_READ = 1;
}
