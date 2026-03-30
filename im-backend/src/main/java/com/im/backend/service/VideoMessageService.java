package com.im.backend.service;

import com.im.backend.dto.VideoMessageRequest;
import com.im.backend.dto.VideoMessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 视频消息服务接口
 * 功能#25: 视频消息
 */
public interface VideoMessageService {
    
    VideoMessageResponse sendVideoMessage(Long senderId, VideoMessageRequest request);
    
    VideoMessageResponse getVideoMessage(String messageId);
    
    Page<VideoMessageResponse> getVideoHistory(Long userId, Pageable pageable);
    
    Page<VideoMessageResponse> getConversationVideos(Long userId1, Long userId2, Pageable pageable);
    
    Page<VideoMessageResponse> getGroupVideos(Long groupId, Pageable pageable);
    
    void markAsRead(String messageId);
    
    Long getUnreadCount(Long userId);
    
    List<VideoMessageResponse> getRecentVideos(Long userId, int days);
}
