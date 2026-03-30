package com.im.backend.service;

import com.im.backend.dto.MentionRequest;
import com.im.backend.dto.MentionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @提醒服务接口
 * 功能#28: 消息@提醒
 */
public interface MentionService {
    
    MentionResponse createMention(Long senderId, MentionRequest request);
    
    MentionResponse getMention(String messageId);
    
    Page<MentionResponse> getUserMentions(Long userId, Pageable pageable);
    
    List<MentionResponse> getUnreadMentions(Long userId);
    
    Page<MentionResponse> getGroupMentions(Long groupId, Long userId, Pageable pageable);
    
    void markAsRead(String messageId);
    
    void markAllAsRead(Long userId);
    
    Long getUnreadCount(Long userId);
    
    List<MentionResponse> getMentionsByOriginalMessage(String originalMessageId);
}
