package com.im.backend.service;

import com.im.backend.dto.RecallMessageDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息撤回服务接口
 */
public interface MessageRecallService {

    /**
     * 撤回消息
     * @param messageId 消息ID
     * @param senderId 发送者ID
     * @param reason 撤回原因
     * @return 撤回记录
     */
    RecallMessageDTO recallMessage(Long messageId, Long senderId, String reason);

    /**
     * 检查消息是否可以撤回
     */
    boolean canRecall(Long messageId, Long senderId);

    /**
     * 检查消息是否已被撤回
     */
    boolean isRecalled(Long messageId);

    /**
     * 获取撤回记录
     */
    RecallMessageDTO getRecallRecord(Long messageId);

    /**
     * 获取用户的撤回记录
     */
    List<RecallMessageDTO> getUserRecallRecords(Long senderId, int limit);

    /**
     * 获取会话中的撤回记录
     */
    List<RecallMessageDTO> getConversationRecallRecords(String conversationType, Long conversationId);

    /**
     * 获取用户今日撤回次数
     */
    long getTodayRecallCount(Long senderId);
}
