package com.im.server.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息引用/回复实体
 * 
 * 用于存储消息的引用/回复关系，支持：
 * - 单聊消息引用回复
 * - 群聊消息引用回复
 * - 引用消息预览（包含被引用消息的发送者和内容摘要）
 * - 引用消息全文预览（长消息截断）
 * - 多层嵌套引用（最多支持3层嵌套）
 */
@Data
@Entity
@Table(name = "t_message_reply", indexes = {
    @Index(name = "idx_original_msg_id", columnList = "original_msg_id"),
    @Index(name = "idx_reply_msg_id", columnList = "reply_msg_id"),
    @Index(name = "idx_chat", columnList = "chat_type, chat_id"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class MessageReply {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 被引用的原消息ID
     */
    @Column(name = "original_msg_id", nullable = false, length = 64)
    private String originalMsgId;
    
    /**
     * 回复消息ID
     */
    @Column(name = "reply_msg_id", nullable = false, length = 64)
    private String replyMsgId;
    
    /**
     * 原消息发送者ID
     */
    @Column(name = "original_sender_id", nullable = false)
    private Long originalSenderId;
    
    /**
     * 原消息发送者昵称
     */
    @Column(name = "original_sender_nickname", length = 50)
    private String originalSenderNickname;
    
    /**
     * 原消息内容摘要（用于预览，长消息会被截断）
     * 最大长度：200字符
     */
    @Column(name = "original_content_preview", columnDefinition = "VARCHAR(200)")
    private String originalContentPreview;
    
    /**
     * 原消息类型
     * 1: 文本 2: 图片 3: 文件 4: 语音 5: 视频 6: 表情包 7: 位置
     */
    @Column(name = "original_msg_type")
    private Integer originalMsgType;
    
    /**
     * 原消息发送时间
     */
    @Column(name = "original_msg_time")
    private LocalDateTime originalMsgTime;
    
    /**
     * 引用层级深度
     * 0: 直接引用原消息
     * 1: 引用已有回复的消息（一层嵌套）
     * 2: 引用二层嵌套的消息
     * 最大支持3层嵌套（>=3时不再增加）
     */
    @Column(name = "reply_depth")
    private Integer replyDepth = 0;
    
    /**
     * 引用链ID（用于追踪引用关系链）
     * 格式：originalMsgId:replyMsgId:replyMsgId2:...
     */
    @Column(name = "reply_chain_id", length = 500)
    private String replyChainId;
    
    /**
     * 引用链深度（从原始消息算起的层数）
     */
    @Column(name = "chain_depth")
    private Integer chainDepth = 1;
    
    /**
     * 是否引用了被撤回的消息
     * true: 原消息已被撤回，但引用关系保留
     * false: 原消息正常存在
     */
    @Column(name = "original_recalled")
    private Boolean originalRecalled = false;
    
    /**
     * 引用标记内容（用户可以添加额外的引用说明）
     * 例如："这条消息很重要"、"请参考"
     */
    @Column(name = "reply_remark", length = 100)
    private String replyRemark;
    
    /**
     * 是否高亮显示引用
     * true: 在UI中以高亮样式展示
     * false: 普通样式展示
     */
    @Column(name = "highlight")
    private Boolean highlight = false;
    
    /**
     * 聊天类型
     * 1: 私聊 2: 群聊
     */
    @Column(name = "chat_type", nullable = false)
    private Integer chatType;
    
    /**
     * 聊天ID（私聊为对方用户ID，群聊为群ID）
     */
    @Column(name = "chat_id", nullable = false)
    private Long chatId;
    
    /**
     * 回复者ID
     */
    @Column(name = "reply_user_id", nullable = false)
    private Long replyUserId;
    
    /**
     * 回复者昵称
     */
    @Column(name = "reply_user_nickname", length = 50)
    private String replyUserNickname;
    
    /**
     * 回复内容（如果为空，则只引用不回复）
     */
    @Column(columnDefinition = "TEXT")
    private String replyContent;
    
    /**
     * 回复消息类型
     * 1: 文本 2: 图片 3: 文件 4: 语音 5: 视频 6: 表情包 7: 位置
     */
    @Column(name = "reply_msg_type")
    private Integer replyMsgType = 1;
    
    /**
     * 是否已删除
     * 0: 未删除 1: 已删除
     */
    @Column(name = "deleted")
    private Integer deleted = 0;
    
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
