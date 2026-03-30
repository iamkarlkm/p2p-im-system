package com.im.backend.service.impl;

import com.im.backend.dto.ContactCardRequest;
import com.im.backend.dto.ContactCardResponse;
import com.im.backend.entity.ContactCardMessage;
import com.im.backend.entity.ConversationType;
import com.im.backend.repository.ContactCardRepository;
import com.im.backend.service.ContactCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 名片分享服务实现
 * 功能#27: 名片分享
 */
@Service
public class ContactCardServiceImpl implements ContactCardService {
    
    @Autowired
    private ContactCardRepository contactCardRepository;
    
    @Override
    public ContactCardResponse sendContactCard(Long senderId, ContactCardRequest request) {
        ContactCardMessage message = new ContactCardMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setSenderId(senderId);
        message.setReceiverId(request.getReceiverId());
        message.setGroupId(request.getGroupId());
        message.setConversationType(ConversationType.valueOf(request.getConversationType()));
        message.setContactUserId(request.getContactUserId());
        message.setContactNickname(request.getContactNickname());
        message.setContactAvatar(request.getContactAvatar());
        message.setContactRemark(request.getContactRemark());
        message.setIsRead(false);
        
        ContactCardMessage saved = contactCardRepository.save(message);
        return convertToResponse(saved);
    }
    
    @Override
    public ContactCardResponse getContactCard(String messageId) {
        ContactCardMessage message = contactCardRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("Contact card not found"));
        return convertToResponse(message);
    }
    
    @Override
    public Page<ContactCardResponse> getContactCardHistory(Long userId, Pageable pageable) {
        return contactCardRepository.findByUserId(userId, pageable)
                .map(this::convertToResponse);
    }
    
    @Override
    public Page<ContactCardResponse> getConversationCards(Long userId1, Long userId2, Pageable pageable) {
        return contactCardRepository.findByConversation(userId1, userId2, pageable)
                .map(this::convertToResponse);
    }
    
    @Override
    public Page<ContactCardResponse> getGroupCards(Long groupId, Pageable pageable) {
        return contactCardRepository.findByGroupId(groupId, pageable)
                .map(this::convertToResponse);
    }
    
    @Override
    public void markAsRead(String messageId) {
        ContactCardMessage message = contactCardRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("Contact card not found"));
        contactCardRepository.markAsRead(message.getId(), LocalDateTime.now());
    }
    
    @Override
    public Long getUnreadCount(Long userId) {
        return contactCardRepository.countUnreadByUserId(userId);
    }
    
    private ContactCardResponse convertToResponse(ContactCardMessage message) {
        ContactCardResponse response = new ContactCardResponse();
        response.setMessageId(message.getMessageId());
        response.setSenderId(message.getSenderId());
        response.setReceiverId(message.getReceiverId());
        response.setGroupId(message.getGroupId());
        response.setConversationType(message.getConversationType().name());
        response.setContactUserId(message.getContactUserId());
        response.setContactNickname(message.getContactNickname());
        response.setContactAvatar(message.getContactAvatar());
        response.setContactRemark(message.getContactRemark());
        response.setIsRead(message.getIsRead());
        response.setReadTime(message.getReadTime());
        response.setCreatedAt(message.getCreatedAt());
        return response;
    }
}
