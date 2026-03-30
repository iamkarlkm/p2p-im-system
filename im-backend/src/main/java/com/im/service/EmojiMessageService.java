package com.im.service;

import com.im.dto.EmojiMessageRequest;
import com.im.dto.EmojiMessageResponse;
import java.util.List;
import java.util.Map;

/**
 * 表情消息服务接口
 * 功能#23: 表情消息
 */
public interface EmojiMessageService {

    /**
     * 发送表情消息
     */
    EmojiMessageResponse sendEmojiMessage(Long senderId, EmojiMessageRequest request);

    /**
     * 获取会话中的表情消息历史
     */
    List<EmojiMessageResponse> getEmojiMessageHistory(Long conversationId, String conversationType, Integer limit);

    /**
     * 标记表情消息已读
     */
    Boolean markAsRead(Long messageId, Long userId);

    /**
     * 获取用户最常用的表情
     */
    List<Map<String, Object>> getTopEmojis(Long userId, Integer topN);

    /**
     * 获取用户的表情使用统计
     */
    Map<String, Object> getEmojiStatistics(Long userId);

    /**
     * 获取表情分类列表
     */
    List<String> getEmojiCategories();

    /**
     * 按分类获取表情
     */
    List<EmojiMessageResponse> getEmojisByCategory(String category, Integer limit);
}
