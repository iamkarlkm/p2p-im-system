package com.im.message.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息查询请求DTO - 支持多维度消息检索
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageQueryRequest {
    
    /**
     * 会话ID(必填)
     */
    @NotNull(message = "会话ID不能为空")
    private Long conversationId;
    
    /**
     * 会话类型: 1-单聊, 2-群聊
     */
    @NotNull(message = "会话类型不能为空")
    @Min(value = 1, message = "会话类型不合法")
    @Max(value = 2, message = "会话类型不合法")
    private Integer conversationType;
    
    /**
     * 查询起始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 查询结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 消息类型过滤(多选)
     */
    private List<Integer> messageTypes;
    
    /**
     * 发送者ID过滤
     */
    private Long senderId;
    
    /**
     * 关键词搜索
     */
    private String keyword;
    
    /**
     * 是否只查询包含附件的消息
     */
    private Boolean hasAttachment;
    
    /**
     * 是否只查询@我的消息
     */
    private Boolean mentionMe;
    
    /**
     * 当前用户ID(用于@我的查询)
     */
    private Long currentUserId;
    
    /**
     * 消息ID游标(用于分页)
     */
    private Long cursor;
    
    /**
     * 每页数量
     */
    @Builder.Default
    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 100, message = "每页数量不能超过100")
    private Integer pageSize = 20;
    
    /**
     * 查询方向: true-向后查询(更新的消息), false-向前查询(更早的消息)
     */
    @Builder.Default
    private Boolean forward = false;
    
    /**
     * 是否包含撤回的消息
     */
    @Builder.Default
    private Boolean includeRecalled = false;
    
    /**
     * 是否只查询未读消息
     */
    private Boolean unreadOnly;
    
    /**
     * 最后已读消息ID
     */
    private Long lastReadMessageId;
    
    // ============ 便捷构造方法 ============
    
    /**
     * 创建基础会话查询
     */
    public static MessageQueryRequest forConversation(Long conversationId, Integer conversationType) {
        return MessageQueryRequest.builder()
                .conversationId(conversationId)
                .conversationType(conversationType)
                .build();
    }
    
    /**
     * 创建关键词搜索查询
     */
    public static MessageQueryRequest forSearch(Long conversationId, Integer conversationType, String keyword) {
        return MessageQueryRequest.builder()
                .conversationId(conversationId)
                .conversationType(conversationType)
                .keyword(keyword)
                .build();
    }
    
    /**
     * 创建@我的查询
     */
    public static MessageQueryRequest forMentions(Long conversationId, Integer conversationType, Long currentUserId) {
        return MessageQueryRequest.builder()
                .conversationId(conversationId)
                .conversationType(conversationType)
                .currentUserId(currentUserId)
                .mentionMe(true)
                .build();
    }
    
    /**
     * 创建带附件查询
     */
    public static MessageQueryRequest forAttachments(Long conversationId, Integer conversationType) {
        return MessageQueryRequest.builder()
                .conversationId(conversationId)
                .conversationType(conversationType)
                .hasAttachment(true)
                .build();
    }
    
    /**
     * 获取查询类型描述
     */
    public String getQueryTypeDesc() {
        if (keyword != null && !keyword.isEmpty()) {
            return "关键词搜索";
        }
        if (Boolean.TRUE.equals(mentionMe)) {
            return "@我的消息";
        }
        if (Boolean.TRUE.equals(hasAttachment)) {
            return "附件消息";
        }
        if (senderId != null) {
            return "指定发送者";
        }
        return "普通查询";
    }
}
