package com.im.backend.service;

import com.im.backend.dto.ContactCardRequest;
import com.im.backend.dto.ContactCardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 名片分享服务接口
 * 功能#27: 名片分享
 */
public interface ContactCardService {
    
    ContactCardResponse sendContactCard(Long senderId, ContactCardRequest request);
    
    ContactCardResponse getContactCard(String messageId);
    
    Page<ContactCardResponse> getContactCardHistory(Long userId, Pageable pageable);
    
    Page<ContactCardResponse> getConversationCards(Long userId1, Long userId2, Pageable pageable);
    
    Page<ContactCardResponse> getGroupCards(Long groupId, Pageable pageable);
    
    void markAsRead(String messageId);
    
    Long getUnreadCount(Long userId);
}
