package com.im.message.dto;

import com.im.message.entity.Message;
import com.im.message.entity.MessageAttachment;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 消息搜索响应DTO - 包含搜索结果和高亮信息
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageSearchResponse {
    
    /**
     * 消息ID
     */
    private Long messageId;
    
    /**
     * 消息UUID
     */
    private String messageUuid;
    
    /**
     * 发送者ID
     */
    private Long senderId;
    
    /**
     * 发送者名称
     */
    private String senderName;
    
    /**
     * 发送者头像
     */
    private String senderAvatar;
    
    /**
     * 接收者ID
     */
    private Long receiverId;
    
    /**
     * 会话类型
     */
    private Integer conversationType;
    
    /**
     * 消息类型
     */
    private Integer messageType;
    
    /**
     * 消息类型描述
     */
    private String messageTypeDesc;
    
    /**
     * 消息内容(可能包含高亮标签)
     */
    private String content;
    
    /**
     * 原始内容(无高亮)
     */
    private String originalContent;
    
    /**
     * 内容摘要
     */
    private String contentDigest;
    
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
    
    /**
     * 发送时间格式化
     */
    private String sendTimeFormatted;
    
    /**
     * 是否已撤回
     */
    private Boolean recalled;
    
    /**
     * 撤回信息
     */
    private String recallInfo;
    
    /**
     * 附件列表
     */
    private List<AttachmentInfo> attachments;
    
    /**
     * 是否包含附件
     */
    private Boolean hasAttachment;
    
    /**
     * 附件数量
     */
    private Integer attachmentCount;
    
    /**
     * 是否@我
     */
    private Boolean mentionMe;
    
    /**
     * @用户列表
     */
    private List<Long> mentionUsers;
    
    /**
     * 是否@所有人
     */
    private Boolean mentionAll;
    
    /**
     * 引用消息信息
     */
    private ReferenceMessageInfo referenceMessage;
    
    /**
     * 消息状态: 0-发送中, 1-已发送, 2-已送达, 3-已读
     */
    private Integer status;
    
    /**
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 是否需要回执
     */
    private Boolean needReceipt;
    
    /**
     * 已读回执数量(群聊)
     */
    private Integer readCount;
    
    /**
     * 未读数量(群聊)
     */
    private Integer unreadCount;
    
    /**
     * 搜索匹配信息
     */
    private SearchMatchInfo searchMatch;
    
    /**
     * 扩展字段
     */
    private Map<String, Object> extra;
    
    // ============ 嵌套类 ============
    
    /**
     * 附件信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttachmentInfo {
        private Long attachmentId;
        private Integer type;
        private String typeDesc;
        private String name;
        private String url;
        private String thumbnailUrl;
        private Long size;
        private String sizeFormatted;
        private Integer width;
        private Integer height;
        private Integer duration;
        private String mimeType;
    }
    
    /**
     * 引用消息信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReferenceMessageInfo {
        private Long messageId;
        private Long senderId;
        private String senderName;
        private String content;
        private Integer messageType;
    }
    
    /**
     * 搜索匹配信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchMatchInfo {
        /**
         * 匹配到的关键词
         */
        private List<String> matchedKeywords;
        
        /**
         * 匹配次数
         */
        private Integer matchCount;
        
        /**
         * 相关度评分
         */
        private Double relevanceScore;
        
        /**
         * 匹配位置列表
         */
        private List<MatchPosition> matchPositions;
    }
    
    /**
     * 匹配位置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchPosition {
        private Integer start;
        private Integer end;
    }
    
    // ============ 构造方法 ============
    
    /**
     * 从Message实体构建响应
     */
    public static MessageSearchResponse fromMessage(Message message) {
        return MessageSearchResponse.builder()
                .messageId(message.getId())
                .messageUuid(message.getMessageId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .conversationType(message.getConversationType())
                .messageType(message.getMessageType())
                .originalContent(message.getContent())
                .contentDigest(message.getContentDigest())
                .sendTime(message.getSendTime())
                .recalled(message.getRecalled())
                .mentionAll(message.getMentionAll())
                .status(message.getStatus())
                .needReceipt(message.getNeedReceipt())
                .build();
    }
}
