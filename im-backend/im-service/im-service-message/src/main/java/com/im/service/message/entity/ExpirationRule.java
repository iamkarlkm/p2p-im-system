package com.im.service.message.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 消息阅后即焚规则实体
 * 对应数据库表: im_message_expiration_rule
 * 
 * @author IM Team
 * @version 1.0
 */
@Data
@Entity
@Table(name = "im_message_expiration_rule", indexes = {
    @Index(name = "idx_expiration_conversation", columnList = "conversationId"),
    @Index(name = "idx_expiration_user", columnList = "userId"),
    @Index(name = "idx_expiration_type", columnList = "ruleType")
})
public class ExpirationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    /** 
     * 用户ID - 规则所属用户
     */
    @Column(nullable = false, length = 36)
    private String userId;

    /** 
     * 会话ID - 可为空，为空表示全局规则
     */
    @Column(length = 36)
    private String conversationId;

    /** 
     * 规则类型: GLOBAL(全局), CONVERSATION(会话), MESSAGE(单条)
     */
    @Column(nullable = false, length = 20)
    private String ruleType = "GLOBAL";

    /** 
     * 过期时间(秒) - 消息发出后多少秒自动销毁
     * 0 表示不自动销毁
     */
    @Column(nullable = false)
    private Integer expirationSeconds = 0;

    /** 
     * 是否启用
     */
    @Column(nullable = false)
    private Boolean enabled = true;

    /** 
     * 创建时间
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 是否是全局规则
     */
    public boolean isGlobal() {
        return "GLOBAL".equals(ruleType);
    }

    /**
     * 是否会话规则
     */
    public boolean isConversation() {
        return "CONVERSATION".equals(ruleType);
    }

    /**
     * 启用规则
     */
    public void enable() {
        this.enabled = true;
    }

    /**
     * 禁用规则
     */
    public void disable() {
        this.enabled = false;
    }
}
