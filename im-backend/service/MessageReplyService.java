package com.im.backend.service;

import com.im.backend.dto.ReplyMessageDTO;
import com.im.backend.dto.ReplyMessageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 消息引用回复服务接口
 */
public interface MessageReplyService {

    /**
     * 发送引用回复消息
     */
    ReplyMessageDTO sendReply(Long senderId, String senderName, ReplyMessageRequest request);

    /**
     * 获取消息的引用回复列表
     */
    List<ReplyMessageDTO> getRepliesByOriginalMessage(Long originalMessageId);

    /**
     * 获取会话中的引用回复
     */
    Page<ReplyMessageDTO> getRepliesByConversation(String conversationType, Long conversationId, Pageable pageable);

    /**
     * 获取嵌套回复（支持多级引用）
     */
    ReplyMessageDTO getReplyWithNested(Long replyId);

    /**
     * 获取回复详情
     */
    ReplyMessageDTO getReplyById(Long replyId);

    /**
     * 获取消息的所有相关回复
     */
    List<ReplyMessageDTO> getAllRelatedReplies(Long messageId);

    /**
     * 删除引用回复
     */
    void deleteReply(Long replyId, Long operatorId);

    /**
     * 获取原消息ID（用于跳转）
     */
    Long getOriginalMessageId(Long replyId);

    /**
     * 检查是否是引用消息
     */
    boolean isReplyMessage(Long messageId);

    /**
     * 获取回复统计
     */
    long getReplyCount(Long originalMessageId);
}
