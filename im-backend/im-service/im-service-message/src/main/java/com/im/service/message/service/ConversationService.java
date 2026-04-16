package com.im.service.message.service;

import com.im.service.message.dto.ConversationResponse;
import com.im.service.message.entity.Conversation;
import com.im.service.message.repository.ConversationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;

    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    @Transactional
    public ConversationResponse createConversation(String type, String name, String creatorId) {
        Conversation conversation = new Conversation();
        conversation.setType(type);
        conversation.setName(name);
        conversation.setCreatorId(creatorId);
        conversation.setMemberCount(1);
        
        conversation = conversationRepository.save(conversation);
        return toResponse(conversation);
    }

    public Optional<ConversationResponse> getConversation(String conversationId) {
        return conversationRepository.findByIdAndDeletedFalse(conversationId)
            .map(this::toResponse);
    }

    public List<ConversationResponse> getUserConversations(String userId, int page, int size) {
        // 简化实现：返回用户创建的会话
        return conversationRepository.findByCreatorIdOrderByUpdatedAtDesc(userId, PageRequest.of(page, size))
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public boolean updateLastMessage(String conversationId, String messageId) {
        conversationRepository.updateLastMessage(conversationId, messageId, LocalDateTime.now());
        return true;
    }

    @Transactional
    public boolean updateMemberCount(String conversationId, int delta) {
        conversationRepository.updateMemberCount(conversationId, delta, LocalDateTime.now());
        return true;
    }

    @Transactional
    public boolean deleteConversation(String conversationId, String userId) {
        Optional<Conversation> opt = conversationRepository.findById(conversationId);
        if (opt.isEmpty()) return false;
        
        Conversation conversation = opt.get();
        // 只有创建者可以删除
        if (!conversation.getCreatorId().equals(userId)) return false;
        
        conversationRepository.softDelete(conversationId, LocalDateTime.now());
        return true;
    }

    private ConversationResponse toResponse(Conversation conversation) {
        ConversationResponse response = new ConversationResponse();
        response.setId(conversation.getId());
        response.setType(conversation.getType());
        response.setName(conversation.getName());
        response.setAvatar(conversation.getAvatar());
        response.setCreatorId(conversation.getCreatorId());
        response.setLastMessageId(conversation.getLastMessageId());
        response.setLastMessageAt(conversation.getLastMessageAt());
        response.setMemberCount(conversation.getMemberCount());
        response.setCreatedAt(conversation.getCreatedAt());
        response.setUpdatedAt(conversation.getUpdatedAt());
        return response;
    }
}
