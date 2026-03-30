package com.im.backend.service.impl;

import com.im.backend.dto.VideoMessageRequest;
import com.im.backend.dto.VideoMessageResponse;
import com.im.backend.entity.ConversationType;
import com.im.backend.entity.VideoMessage;
import com.im.backend.repository.VideoMessageRepository;
import com.im.backend.service.VideoMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 视频消息服务实现
 * 功能#25: 视频消息
 */
@Service
public class VideoMessageServiceImpl implements VideoMessageService {
    
    @Autowired
    private VideoMessageRepository videoMessageRepository;
    
    @Override
    public VideoMessageResponse sendVideoMessage(Long senderId, VideoMessageRequest request) {
        VideoMessage message = new VideoMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setSenderId(senderId);
        message.setReceiverId(request.getReceiverId());
        message.setGroupId(request.getGroupId());
        message.setConversationType(ConversationType.valueOf(request.getConversationType()));
        message.setVideoUrl(request.getVideoUrl());
        message.setThumbnailUrl(request.getThumbnailUrl());
        message.setDurationSeconds(request.getDurationSeconds());
        message.setWidth(request.getWidth());
        message.setHeight(request.getHeight());
        message.setFileSize(request.getFileSize());
        message.setFormat(request.getFormat());
        message.setIsRead(false);
        
        VideoMessage saved = videoMessageRepository.save(message);
        return convertToResponse(saved);
    }
    
    @Override
    public VideoMessageResponse getVideoMessage(String messageId) {
        VideoMessage message = videoMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("Video message not found"));
        return convertToResponse(message);
    }
    
    @Override
    public Page<VideoMessageResponse> getVideoHistory(Long userId, Pageable pageable) {
        return videoMessageRepository.findByUserId(userId, pageable)
                .map(this::convertToResponse);
    }
    
    @Override
    public Page<VideoMessageResponse> getConversationVideos(Long userId1, Long userId2, Pageable pageable) {
        return videoMessageRepository.findByConversation(userId1, userId2, pageable)
                .map(this::convertToResponse);
    }
    
    @Override
    public Page<VideoMessageResponse> getGroupVideos(Long groupId, Pageable pageable) {
        return videoMessageRepository.findByGroupId(groupId, pageable)
                .map(this::convertToResponse);
    }
    
    @Override
    public void markAsRead(String messageId) {
        VideoMessage message = videoMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("Video message not found"));
        videoMessageRepository.markAsRead(message.getId(), LocalDateTime.now());
    }
    
    @Override
    public Long getUnreadCount(Long userId) {
        return videoMessageRepository.countUnreadByUserId(userId);
    }
    
    @Override
    public List<VideoMessageResponse> getRecentVideos(Long userId, int days) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        return videoMessageRepository.findRecentBySender(userId, startTime)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private VideoMessageResponse convertToResponse(VideoMessage message) {
        VideoMessageResponse response = new VideoMessageResponse();
        response.setMessageId(message.getMessageId());
        response.setSenderId(message.getSenderId());
        response.setReceiverId(message.getReceiverId());
        response.setGroupId(message.getGroupId());
        response.setConversationType(message.getConversationType().name());
        response.setVideoUrl(message.getVideoUrl());
        response.setThumbnailUrl(message.getThumbnailUrl());
        response.setDurationSeconds(message.getDurationSeconds());
        response.setWidth(message.getWidth());
        response.setHeight(message.getHeight());
        response.setFileSize(message.getFileSize());
        response.setFormat(message.getFormat());
        response.setIsRead(message.getIsRead());
        response.setReadTime(message.getReadTime());
        response.setCreatedAt(message.getCreatedAt());
        return response;
    }
}
