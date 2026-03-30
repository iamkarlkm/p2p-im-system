package com.im.service;

import com.im.dto.MessageDTO;
import com.im.dto.PrivateChatSessionDTO;
import com.im.entity.Message;
import com.im.entity.PrivateChatSession;
import com.im.entity.User;
import com.im.repository.MessageRepository;
import com.im.repository.PrivateChatSessionRepository;
import com.im.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 单聊服务
 * 功能ID: #6
 */
@Service
public class PrivateChatService {

    @Autowired
    private PrivateChatSessionRepository sessionRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 创建或获取会话
     */
    public PrivateChatSessionDTO createOrGetSession(String userId1, String userId2) {
        return sessionRepository.findByUserPair(userId1, userId2)
            .map(this::toSessionDTO)
            .orElseGet(() -> createNewSession(userId1, userId2));
    }

    /**
     * 获取用户会话列表
     */
    public List<PrivateChatSessionDTO> getUserSessions(String userId) {
        List<PrivateChatSession> sessions = sessionRepository.findByUserId(userId);
        return sessions.stream()
            .map(session -> toSessionDTO(session, userId))
            .collect(Collectors.toList());
    }

    /**
     * 获取或创建与某用户的会话
     */
    public PrivateChatSessionDTO getOrCreateSessionWithUser(String userId, String targetUserId) {
        return createOrGetSession(userId, targetUserId);
    }

    /**
     * 获取会话消息
     */
    public Page<MessageDTO> getSessionMessages(String sessionId, int page, int size) {
        PrivateChatSession session = sessionRepository.findById(sessionId).orElse(null);
        if (session == null) {
            return Page.empty();
        }
        
        Page<Message> messages = messageRepository.findConversation(
            session.getUser1Id(), session.getUser2Id(),
            PageRequest.of(page, size, Sort.by("createdAt").descending())
        );
        
        List<MessageDTO> dtoList = messages.getContent().stream()
            .map(this::toMessageDTO)
            .collect(Collectors.toList());
        
        return new PageImpl<>(dtoList, messages.getPageable(), messages.getTotalElements());
    }

    /**
     * 标记会话已读
     */
    public void markSessionAsRead(String sessionId, String userId) {
        PrivateChatSession session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            if (userId.equals(session.getUser1Id())) {
                session.setUnreadCountUser1(0);
            } else if (userId.equals(session.getUser2Id())) {
                session.setUnreadCountUser2(0);
            }
            sessionRepository.save(session);
        }
    }

    /**
     * 删除会话
     */
    public void deleteSession(String sessionId) {
        sessionRepository.deleteById(sessionId);
    }

    /**
     * 设置置顶
     */
    public void pinSession(String sessionId, boolean pinned) {
        PrivateChatSession session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            session.setPinned(pinned);
            sessionRepository.save(session);
        }
    }

    /**
     * 设置免打扰
     */
    public void muteSession(String sessionId, boolean muted) {
        PrivateChatSession session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            session.setMuted(muted);
            sessionRepository.save(session);
        }
    }

    // ============== 辅助方法 ==============

    private PrivateChatSessionDTO createNewSession(String userId1, String userId2) {
        PrivateChatSession session = new PrivateChatSession();
        session.setId(UUID.randomUUID().toString());
        session.setUser1Id(userId1);
        session.setUser2Id(userId2);
        session.setUnreadCountUser1(0);
        session.setUnreadCountUser2(0);
        session.setPinned(false);
        session.setMuted(false);
        session.setCreatedAt(LocalDateTime.now());
        
        sessionRepository.save(session);
        return toSessionDTO(session);
    }

    public PrivateChatSessionDTO toSessionDTO(PrivateChatSession session) {
        return toSessionDTO(session, null);
    }

    public PrivateChatSessionDTO toSessionDTO(PrivateChatSession session, String currentUserId) {
        PrivateChatSessionDTO dto = new PrivateChatSessionDTO();
        dto.setId(session.getId());
        dto.setUser1Id(session.getUser1Id());
        dto.setUser2Id(session.getUser2Id());
        dto.setLastMessageContent(session.getLastMessageContent());
        dto.setLastMessageTime(session.getLastMessageTime());
        dto.setPinned(session.getPinned() != null ? session.getPinned() : false);
        dto.setMuted(session.getMuted() != null ? session.getMuted() : false);
        dto.setCreatedAt(session.getCreatedAt());
        
        // 确定对方用户ID
        String otherUserId = currentUserId != null 
            ? (currentUserId.equals(session.getUser1Id()) ? session.getUser2Id() : session.getUser1Id())
            : session.getUser2Id();
        dto.setOtherUserId(otherUserId);
        
        // 获取对方用户信息
        User otherUser = userRepository.findById(otherUserId).orElse(null);
        if (otherUser != null) {
            dto.setOtherUsername(otherUser.getUsername());
            dto.setOtherNickname(otherUser.getNickname());
            dto.setOtherAvatar(otherUser.getAvatar());
        }
        
        // 设置未读数
        if (currentUserId != null) {
            if (currentUserId.equals(session.getUser1Id())) {
                dto.setUnreadCount(session.getUnreadCountUser1() != null ? session.getUnreadCountUser1() : 0);
            } else {
                dto.setUnreadCount(session.getUnreadCountUser2() != null ? session.getUnreadCountUser2() : 0);
            }
        }
        
        return dto;
    }

    private MessageDTO toMessageDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setFromUserId(message.getFromUserId());
        dto.setToUserId(message.getToUserId());
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setStatus(message.getStatus());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setRecalled(message.isRecalled());
        return dto;
    }
}
