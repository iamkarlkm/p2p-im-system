package com.im.backend.service;

import com.im.backend.dto.MessageReactionDTO;
import com.im.backend.dto.ReactionSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 消息表情回应服务接口
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-27
 */
public interface MessageReactionService {

    /**
     * 添加表情回应
     */
    MessageReactionDTO addReaction(MessageReactionDTO reactionDTO);

    /**
     * 移除表情回应
     */
    void removeReaction(Long messageId, Long userId, String emojiCode);

    /**
     * 切换表情回应（有则删除，无则添加）
     */
    MessageReactionDTO toggleReaction(MessageReactionDTO reactionDTO);

    /**
     * 获取消息的回应汇总
     */
    ReactionSummaryDTO getReactionSummary(Long messageId, Long currentUserId);

    /**
     * 获取消息的回应列表
     */
    List<MessageReactionDTO> getReactionsByMessage(Long messageId);

    /**
     * 分页获取消息的回应
     */
    Page<MessageReactionDTO> getReactionsByMessage(Long messageId, Pageable pageable);

    /**
     * 获取用户对消息的回应
     */
    List<MessageReactionDTO> getReactionsByMessageAndUser(Long messageId, Long userId);

    /**
     * 删除用户对消息的所有回应
     */
    void removeAllReactionsByUser(Long messageId, Long userId);

    /**
     * 获取会话中的热门表情
     */
    List<ReactionSummaryDTO.EmojiCountDTO> getPopularEmojis(Long conversationId, int limit);

    /**
     * 检查用户是否回应了消息
     */
    boolean hasUserReacted(Long messageId, Long userId);

    /**
     * 批量获取消息的回应汇总
     */
    List<ReactionSummaryDTO> getReactionSummaries(List<Long> messageIds, Long currentUserId);
}
