package com.im.server.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息实体
 */
@Data
@Entity
@Table(name = "t_message")
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "msg_id", unique = true, nullable = false, length = 64)
    private String msgId; // 消息唯一ID (UUID)
    
    @Column(name = "from_user_id", nullable = false)
    private Long fromUserId;
    
    @Column(name = "to_user_id", nullable = false)
    private Long toUserId;
    
    @Column(name = "chat_type", nullable = false)
    private Integer chatType; // 1:私聊 2:群聊
    
    @Column(name = "chat_id", nullable = false)
    private Long chatId;
    
    @Column(name = "msg_type", nullable = false)
    private Integer msgType; // 1:文本 2:图片 3:文件 4:语音
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(nullable = false)
    private Integer status = 1; // 1:发送中 2:已发送 3:已送达 4:已读 5:发送失败 6:已撤回
    
    // 撤回相关（非数据库字段）
    private Boolean isRecalled = false;
    private LocalDateTime recallTime;
    private String recallReason;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
