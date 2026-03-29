package com.im.backend.service;

import com.im.backend.dto.MessageDTO;
import com.im.backend.dto.PagedResponse;
import com.im.backend.dto.PrivateChatSessionDTO;
import com.im.backend.model.Message;
import com.im.backend.model.User;
import com.im.backend.repository.MessageRepository;
import com.im.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 单聊服务
 * 处理单聊会话的创建、查询、管理等业务逻辑
 */
@Service
public class PrivateChatService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    // 内存中存储会话信息（实际生产环境应使用Redis或数据库表）
    private final Map<Long, PrivateChatSession> sessionStore = new HashMap<>();
    private final Map<String, Long> sessionKeyMap = new HashMap<>();
    private long sessionIdCounter = 1;

    /**
     * 创建或获取单聊会话
     * 如果两个用户之间已存在会话，则返回已有会话
     */
    @Transactional
    public PrivateChatSessionDTO createOrGetSession(Long userId1, Long userId2) {
        // 验证用户存在
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在: " + userId1));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在: " + userId2));

        // 生成会话键（用户ID小的在前，保证唯一性）
        String sessionKey = generateSessionKey(userId1, userId2);

        // 检查是否已存在会话
        if (sessionKeyMap.containsKey(sessionKey)) {
            Long existingSessionId = sessionKeyMap.get(sessionKey);
            PrivateChatSession session = sessionStore.get(existingSessionId);
            return convertToDTO(session, userId1);
        }

        // 创建新会话
        Long sessionId = sessionIdCounter++;
        PrivateChatSession session = new PrivateChatSession();
        session.setId(sessionId);
        session.setUserId1(Math.min(userId1, userId2));
        session.setUserId2(Math.max(userId1, userId2));
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        session.setLastMessageTime(LocalDateTime.now());

        // 存储会话
        sessionStore.put(sessionId, session);
        sessionKeyMap.put(sessionKey, sessionId);

        return convertToDTO(session, userId1);
    }

    /**
     * 获取用户的所有会话列表
     */
    public List<PrivateChatSessionDTO> getUserSessions(Long userId) {
        return sessionStore.values().stream()
                .filter(session -> session.getUserId1().equals(userId) || session.getUserId2().equals(userId))
                .sorted(Comparator.comparing(PrivateChatSession::getLastMessageTime).reversed())
                .map(session -> convertToDTO(session, userId))
                .collect(Collectors.toList());
    }

    /**
     * 获取单个会话详情
     */
    public PrivateChatSessionDTO getSessionDetail(Long sessionId, Long currentUserId) {
        PrivateChatSession session = sessionStore.get(sessionId);
        if (session == null) {
            throw new EntityNotFoundException("会话不存在: " + sessionId);
        }

        // 验证用户是否属于该会话
        if (!session.getUserId1().equals(currentUserId) && !session.getUserId2().equals(currentUserId)) {
            throw new SecurityException("无权访问该会话");
        }

        return convertToDTO(session, currentUserId);
    }

    /**
     * 获取会话的消息历史
     */
    public PagedResponse<MessageDTO> getSessionMessages(Long sessionId, Long currentUserId, int page, int size) {
        PrivateChatSession session = sessionStore.get(sessionId);
        if (session == null) {
            throw new EntityNotFoundException("会话不存在: " + sessionId);
        }

        // 验证用户是否属于该会话
        if (!session.getUserId1().equals(currentUserId) && !session.getUserId2().equals(currentUserId)) {
            throw new SecurityException("无权访问该会话");
        }

        // 获取两个用户之间的消息
        Long otherUserId = session.getUserId1().equals(currentUserId) ? session.getUserId2() : session.getUserId1();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Message> messagePage = messageRepository.findMessagesBetweenUsers(currentUserId, otherUserId, pageable);

        List<MessageDTO> messageDTOs = messagePage.getContent().stream()
                .map(this::convertToMessageDTO)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                messageDTOs,
                messagePage.getNumber(),
                messagePage.getSize(),
                messagePage.getTotalElements(),
                messagePage.getTotalPages()
        );
    }

    /**
     * 标记会话消息为已读
     */
    @Transactional
    public void markSessionAsRead(Long sessionId, Long currentUserId) {
        PrivateChatSession session = sessionStore.get(sessionId);
        if (session == null) {
            throw new EntityNotFoundException("会话不存在: " + sessionId);
        }

        Long otherUserId = session.getUserId1().equals(currentUserId) ? session.getUserId2() : session.getUserId1();
        
        // 将对方发送给当前用户的未读消息标记为已读
        List<Message> unreadMessages = messageRepository.findUnreadMessagesBetweenUsers(otherUserId, currentUserId);
        
        for (Message message : unreadMessages) {
            message.setRead(true);
            messageRepository.save(message);
        }

        // 更新会话未读数
        session.getUnreadCount().put(currentUserId, 0);
    }

    /**
     * 删除会话（软删除）
     */
    public void deleteSession(Long sessionId, Long currentUserId) {
        PrivateChatSession session = sessionStore.get(sessionId);
        if (session == null) {
            throw new EntityNotFoundException("会话不存在: " + sessionId);
        }

        // 验证用户是否属于该会话
        if (!session.getUserId1().equals(currentUserId) && !session.getUserId2().equals(currentUserId)) {
            throw new SecurityException("无权访问该会话");
        }

        // 标记为对用户隐藏
        session.getHiddenForUsers().add(currentUserId);
    }

    /**
     * 设置会话置顶状态
     */
    public void setSessionPinned(Long sessionId, Long currentUserId, boolean pinned) {
        PrivateChatSession session = sessionStore.get(sessionId);
        if (session == null) {
            throw new EntityNotFoundException("会话不存在: " + sessionId);
        }

        // 验证用户是否属于该会话
        if (!session.getUserId1().equals(currentUserId) && !session.getUserId2().equals(currentUserId)) {
            throw new SecurityException("无权访问该会话");
        }

        session.getPinnedUsers().put(currentUserId, pinned);
        session.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 设置会话免打扰状态
     */
    public void setSessionMuted(Long sessionId, Long currentUserId, boolean muted) {
        PrivateChatSession session = sessionStore.get(sessionId);
        if (session == null) {
            throw new EntityNotFoundException("会话不存在: " + sessionId);
        }

        // 验证用户是否属于该会话
        if (!session.getUserId1().equals(currentUserId) && !session.getUserId2().equals(currentUserId)) {
            throw new SecurityException("无权访问该会话");
        }

        session.getMutedUsers().put(currentUserId, muted);
        session.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 更新会话最后消息信息
     */
    public void updateSessionLastMessage(Long sessionId, String lastMessage, LocalDateTime lastMessageTime) {
        PrivateChatSession session = sessionStore.get(sessionId);
        if (session != null) {
            session.setLastMessage(lastMessage);
            session.setLastMessageTime(lastMessageTime);
            session.setUpdatedAt(LocalDateTime.now());
            
            // 增加接收方未读数
            Long senderId = session.getUserId1();
            Long receiverId = session.getUserId2();
            
            session.getUnreadCount().merge(receiverId, 1, Integer::sum);
        }
    }

    // ============ 私有方法 ============

    private String generateSessionKey(Long userId1, Long userId2) {
        Long smaller = Math.min(userId1, userId2);
        Long larger = Math.max(userId1, userId2);
        return smaller + "_" + larger;
    }

    private PrivateChatSessionDTO convertToDTO(PrivateChatSession session, Long currentUserId) {
        PrivateChatSessionDTO dto = new PrivateChatSessionDTO();
        dto.setId(session.getId());
        dto.setCreatedAt(session.getCreatedAt());
        dto.setUpdatedAt(session.getUpdatedAt());
        dto.setLastMessage(session.getLastMessage());
        dto.setLastMessageTime(session.getLastMessageTime());

        // 获取对方用户信息
        Long otherUserId = session.getUserId1().equals(currentUserId) ? session.getUserId2() : session.getUserId1();
        User otherUser = userRepository.findById(otherUserId).orElse(null);
        
        if (otherUser != null) {
            dto.setTargetUserId(otherUser.getId());
            dto.setTargetUsername(otherUser.getUsername());
            dto.setTargetNickname(otherUser.getNickname());
            dto.setTargetAvatar(otherUser.getAvatar());
        }

        // 获取当前用户的会话设置
        dto.setPinned(session.getPinnedUsers().getOrDefault(currentUserId, false));
        dto.setMuted(session.getMutedUsers().getOrDefault(currentUserId, false));
        dto.setUnreadCount(session.getUnreadCount().getOrDefault(currentUserId, 0));

        return dto;
    }

    private MessageDTO convertToMessageDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSenderId());
        dto.setReceiverId(message.getReceiverId());
        dto.setContent(message.getContent());
        dto.setType(message.getType());
        dto.setRead(message.isRead());
        dto.setCreatedAt(message.getCreatedAt());
        
        // 获取发送者信息
        User sender = userRepository.findById(message.getSenderId()).orElse(null);
        if (sender != null) {
            dto.setSenderUsername(sender.getUsername());
            dto.setSenderNickname(sender.getNickname());
            dto.setSenderAvatar(sender.getAvatar());
        }
        
        return dto;
    }

    // ============ 内部会话类 ============

    /**
     * 内部会话存储类
     */
    private static class PrivateChatSession {
        private Long id;
        private Long userId1;
        private Long userId2;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String lastMessage;
        private LocalDateTime lastMessageTime;
        private Map<Long, Boolean> pinnedUsers = new HashMap<>();
        private Map<Long, Boolean> mutedUsers = new HashMap<>();
        private Map<Long, Integer> unreadCount = new HashMap<>();
        private Set<Long> hiddenForUsers = new HashSet<>();

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public Long getUserId1() { return userId1; }
        public void setUserId1(Long userId1) { this.userId1 = userId1; }
        
        public Long getUserId2() { return userId2; }
        public void setUserId2(Long userId2) { this.userId2 = userId2; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        
        public String getLastMessage() { return lastMessage; }
        public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
        
        public LocalDateTime getLastMessageTime() { return lastMessageTime; }
        public void setLastMessageTime(LocalDateTime lastMessageTime) { this.lastMessageTime = lastMessageTime; }
        
        public Map<Long, Boolean> getPinnedUsers() { return pinnedUsers; }
        public void setPinnedUsers(Map<Long, Boolean> pinnedUsers) { this.pinnedUsers = pinnedUsers; }
        
        public Map<Long, Boolean> getMutedUsers() { return mutedUsers; }
        public void setMutedUsers(Map<Long, Boolean> mutedUsers) { this.mutedUsers = mutedUsers; }
        
        public Map<Long, Integer> getUnreadCount() { return unreadCount; }
        public void setUnreadCount(Map<Long, Integer> unreadCount) { this.unreadCount = unreadCount; }
        
        public Set<Long> getHiddenForUsers() { return hiddenForUsers; }
        public void setHiddenForUsers(Set<Long> hiddenForUsers) { this.hiddenForUsers = hiddenForUsers; }
    }
}
