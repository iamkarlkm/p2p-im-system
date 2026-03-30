package com.im.service;

import com.im.dto.ImageMessageRequest;
import com.im.dto.ImageMessageResponse;
import java.util.List;
import java.util.Map;

/**
 * 图片消息服务接口
 * 功能#24: 图片消息
 */
public interface ImageMessageService {

    /**
     * 发送图片消息
     */
    ImageMessageResponse sendImageMessage(Long senderId, ImageMessageRequest request);

    /**
     * 获取图片消息详情
     */
    ImageMessageResponse getImageMessage(Long messageId);

    /**
     * 获取会话中的图片消息历史
     */
    List<ImageMessageResponse> getImageMessageHistory(Long conversationId, String conversationType, Integer limit);

    /**
     * 标记图片消息已读
     */
    Boolean markAsRead(Long messageId, Long userId);

    /**
     * 获取原图URL
     */
    String getOriginalImageUrl(Long messageId);

    /**
     * 获取用户的最近图片
     */
    List<ImageMessageResponse> getRecentImages(Long userId, Integer limit);

    /**
     * 获取图片使用统计
     */
    Map<String, Object> getImageStatistics(Long userId);
}
