package com.im.backend.service.impl;

import com.im.backend.dto.MentionRequest;
import com.im.backend.dto.MentionResponse;
import com.im.backend.entity.MentionMessage;
import com.im.backend.repository.MentionMessageRepository;
import com.im.backend.service.MentionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @提醒服务实现
 * 功能#28: 消息@提醒
 */
@Service
public class MentionServiceImpl implements MentionService {
    
    @Autowired
    private MentionMessageRepository mentionMessageRepository;
    
    @Override
    public MentionResponse createMention(Long senderId, MentionRequest request) {
        MentionMessage mention = new MentionMessage();
        mention.setMessageId(UUID.randomUUID().toString());
        mention.setSenderId(senderId);
        mention.setGroupId(request.getGroupId());
        mention.setMentionedUserId(request.getMentionedUserId());
        mention.setOriginalMessageId(request.getOriginalMessageId());
        mention.setOriginalContent(request.getOriginalContent());
        mention.setMentionType(request.getMentionType());
        mention.setIsRead(false);
        
        MentionMessage saved = mentionMessageRepository.save(mention);
        return convertToResponse(saved);
    }
    
    @Override
    public MentionResponse getMention(String messageId) {
        MentionMessage mention = mentionMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("Mention not found"));
        return convertToResponse(mention);
    }
    
    @Override
    public Page<MentionResponse> getUserMentions(Long userId, Pageable pageable) {
        return mentionMessageRepository.findByMentionedUserId(userId, pageable)
                .map(this::convertToResponse);
    }
    
    @Override
    public List<MentionResponse> getUnreadMentions(Long userId) {
        return mentionMessageRepository.findUnreadByUserId(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<MentionResponse> getGroupMentions(Long groupId, Long userId, Pageable pageable) {
        return mentionMessageRepository.findByGroupAndUser(groupId, userId, pageable)
                .map(this::convertToResponse);
    }
    
    @Override
    public void markAsRead(String messageId) {
        MentionMessage mention = mentionMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("Mention not found"));
        mentionMessageRepository.markAsRead(mention.getId(), LocalDateTime.now());
    }
    
    @Override
    public void markAllAsRead(Long userId) {
        List<MentionMessage> unreadMentions = mentionMessageRepository.findUnreadByUserId(userId);
        LocalDateTime now = LocalDateTime.now();
        for (MentionMessage mention : unreadMentions) {
            mentionMessageRepository.markAsRead(mention.getId(), now);
        }
    }
    
    @Override
    public Long getUnreadCount(Long userId) {
        return mentionMessageRepository.countUnreadByUserId(userId);
    }
    
    @Override
    public List<MentionResponse> getMentionsByOriginalMessage(String originalMessageId) {
        return mentionMessageRepository.findByOriginalMessageId(originalMessageId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private MentionResponse convertToResponse(MentionMessage mention) {
        MentionResponse response = new MentionResponse();
        response.setMessageId(mention.getMessageId());
        response.setSenderId(mention.getSenderId());
        response.setGroupId(mention.getGroupId());
        response.setMentionedUserId(mention.getMentionedUserId());
        response.setOriginalMessageId(mention.getOriginalMessageId());
        response.setOriginalContent(mention.getOriginalContent());
        response.setMentionType(mention.getMentionType());
        response.setIsRead(mention.getIsRead());
        response.setReadTime(mention.getReadTime());
        response.setCreatedAt(mention.getCreatedAt());
        return response;
    }
}
