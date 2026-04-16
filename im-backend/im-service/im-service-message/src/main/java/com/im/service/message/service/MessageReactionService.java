package com.im.service.message.service;

import com.im.service.message.dto.ReactionRequest;
import com.im.service.message.dto.ReactionResponse;
import com.im.service.message.entity.MessageReaction;
import com.im.service.message.repository.MessageReactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 消息反应服务
 * 处理消息表情反应的核心业务逻辑
 * 
 * @author IM Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageReactionService {

    private final MessageReactionRepository reactionRepository;

    /**
     * 添加消息反应
     * 如果用户已经对该消息有相同类型的反应，则返回已有的反应（幂等）
     * 如果用户已经有不同类型的反应，先删除旧的再添加新的
     */
    @Transactional
    public ReactionResponse addReaction(String userId, ReactionRequest request) {
        // 检查是否已有相同类型的反应
        Optional<MessageReaction> existingReaction = reactionRepository
                .findByMessageIdAndUserIdAndReactionType(
                        request.getMessageId(), 
                        userId, 
                        request.getReactionType()
                );
        
        if (existingReaction.isPresent()) {
            log.info("用户 {} 对消息 {} 已有相同反应 {}", 
                    userId, request.getMessageId(), request.getReactionType());
            return convertToResponse(existingReaction.get(), true);
        }

        // 删除该用户对该消息的其他反应（每个消息只允许一个反应）
        reactionRepository.findByMessageIdAndUserId(request.getMessageId(), userId)
                .ifPresent(oldReaction -> {
                    log.info("用户 {} 切换消息 {} 的反应从 {} 到 {}", 
                            userId, request.getMessageId(), 
                            oldReaction.getReactionType(), request.getReactionType());
                    reactionRepository.delete(oldReaction);
                });

        // 创建新反应
        MessageReaction reaction = new MessageReaction();
        reaction.setMessageId(request.getMessageId());
        reaction.setUserId(userId);
        reaction.setReactionType(request.getReactionType());
        reaction.setCreatedAt(LocalDateTime.now());

        MessageReaction savedReaction = reactionRepository.save(reaction);
        log.info("用户 {} 对消息 {} 添加反应 {}", 
                userId, request.getMessageId(), request.getReactionType());
        
        return convertToResponse(savedReaction, true);
    }

    /**
     * 移除消息反应
     * 用户可以移除自己的反应
     */
    @Transactional
    public void removeReaction(String userId, String messageId, String reactionType) {
        Optional<MessageReaction> reaction = reactionRepository
                .findByMessageIdAndUserIdAndReactionType(messageId, userId, reactionType);
        
        if (reaction.isPresent()) {
            reactionRepository.delete(reaction.get());
            log.info("用户 {} 移除对消息 {} 的反应 {}", userId, messageId, reactionType);
        } else {
            log.warn("用户 {} 尝试移除不存在的反应: 消息 {} 类型 {}", 
                    userId, messageId, reactionType);
        }
    }

    /**
     * 获取消息的所有反应列表
     */
    public List<ReactionResponse> getReactionsByMessageId(String messageId, String currentUserId) {
        List<MessageReaction> reactions = reactionRepository.findByMessageId(messageId);
        return reactions.stream()
                .map(r -> convertToResponse(r, Objects.equals(r.getUserId(), currentUserId)))
                .collect(Collectors.toList());
    }

    /**
     * 获取消息的反应统计
     * 返回每种反应类型的数量和用户列表
     */
    public Map<String, Object> getReactionStats(String messageId) {
        List<Object[]> stats = reactionRepository.countByReactionTypeGroup(messageId);
        
        List<Map<String, Object>> reactionStats = stats.stream()
                .map(stat -> {
                    String type = (String) stat[0];
                    Long count = (Long) stat[1];
                    return Map.of(
                            "reactionType", type,
                            "count", count
                    );
                })
                .collect(Collectors.toList());
        
        Long total = reactionRepository.countByMessageId(messageId);
        
        return Map.of(
                "messageId", messageId,
                "totalReactions", total,
                "reactionsByType", reactionStats
        );
    }

    /**
     * 转换实体为响应DTO
     */
    private ReactionResponse convertToResponse(MessageReaction reaction, boolean isCurrentUser) {
        return ReactionResponse.builder()
                .id(reaction.getId())
                .messageId(reaction.getMessageId())
                .userId(reaction.getUserId())
                .reactionType(reaction.getReactionType())
                .createdAt(reaction.getCreatedAt())
                .isCurrentUser(isCurrentUser)
                .build();
    }
}
