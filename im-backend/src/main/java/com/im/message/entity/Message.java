package com.im.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 消息实体 - 消息存储与检索引擎核心实体
 * 
 * 功能: 消息持久化存储、历史查询、搜索、撤回
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_message")
public class Message {
    
    /**
     * 消息ID - 全局唯一标识
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 消息全局唯一标识 (UUID)
     */
    private String messageId;
    
    /**
     * 发送者ID
     */
    private Long senderId;
    
    /**
     * 发送者名称
     */
    @TableField(exist = false)
    private String senderName;
    
    /**
     * 接收者ID (单聊为对方ID，群聊为群组ID)
     */
    private Long receiverId;
    
    /**
     * 会话类型: 1-单聊, 2-群聊, 3-系统消息
     */
    private Integer conversationType;
    
    /**
     * 消息类型: 1-文本, 2-图片, 3-语音, 4-视频, 5-文件, 6-位置, 7-链接, 8-卡片
     */
    private Integer messageType;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息内容摘要(用于搜索和预览)
     */
    private String contentDigest;
    
    /**
     * 消息状态: 0-发送中, 1-已发送, 2-已送达, 3-已读, 4-已撤回
     */
    private Integer status;
    
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
    
    /**
     * 送达时间
     */
    private LocalDateTime deliverTime;
    
    /**
     * 已读时间
     */
    private LocalDateTime readTime;
    
    /**
     * 是否撤回
     */
    private Boolean recalled;
    
    /**
     * 撤回时间
     */
    private LocalDateTime recallTime;
    
    /**
     * 撤回操作者ID
     */
    private Long recallBy;
    
    /**
     * 引用消息ID
     */
    private Long referenceMessageId;
    
    /**
     * 引用消息内容摘要
     */
    private String referenceContent;
    
    /**
     * @提及用户ID列表(JSON格式)
     */
    private String mentionUserIds;
    
    /**
     * 是否@所有人
     */
    private Boolean mentionAll;
    
    /**
     * 消息附件列表(JSON格式存储文件URL列表)
     */
    private String attachments;
    
    /**
     * 扩展字段(JSON格式，存储额外属性)
     */
    private String extra;
    
    /**
     * 发送设备信息
     */
    private String deviceInfo;
    
    /**
     * 发送IP地址
     */
    private String sendIp;
    
    /**
     * 消息本地ID(客户端生成)
     */
    private String clientMessageId;
    
    /**
     * 是否需要回执
     */
    private Boolean needReceipt;
    
    /**
     * 消息优先级: 1-高, 2-普通, 3-低
     */
    private Integer priority;
    
    /**
     * 消息过期时间(阅后即焚等场景)
     */
    private LocalDateTime expireTime;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;
    
    /**
     * 租户ID(多租户支持)
     */
    private Long tenantId;
    
    // ============ 业务方法 ============
    
    /**
     * 检查消息是否可以撤回
     * 规则: 2分钟内可撤回
     */
    public boolean canRecall() {
        if (this.recalled != null && this.recalled) {
            return false;
        }
        if (this.sendTime == null) {
            return false;
        }
        return LocalDateTime.now().minusMinutes(2).isBefore(this.sendTime);
    }
    
    /**
     * 检查消息是否已过期
     */
    public boolean isExpired() {
        if (this.expireTime == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(this.expireTime);
    }
    
    /**
     * 获取会话唯一标识
     */
    public String getConversationId() {
        if (conversationType == 1) {
            // 单聊: userId1_userId2 (小ID在前)
            long minId = Math.min(senderId, receiverId);
            long maxId = Math.max(senderId, receiverId);
            return minId + "_" + maxId;
        } else {
            // 群聊: group_群组ID
            return "group_" + receiverId;
        }
    }
    
    /**
     * 生成内容摘要
     */
    public void generateDigest() {
        if (content == null) {
            this.contentDigest = "";
            return;
        }
        // 去除HTML标签
        String plainText = content.replaceAll("<[^>]*>", "");
        // 限制长度
        if (plainText.length() > 100) {
            this.contentDigest = plainText.substring(0, 100) + "...";
        } else {
            this.contentDigest = plainText;
        }
    }
}
