package com.im.service.message.service;

import com.im.dto.PrivateChatRequest;
import com.im.dto.PrivateChatResponse;
import com.im.dto.MessageDTO;
import com.im.entity.Message;
import com.im.entity.PrivateSession;
import com.im.entity.User;
import com.im.repository.MessageRepository;
import com.im.repository.PrivateSessionRepository;
import com.im.repository.UserRepository;
import com.im.websocket.WebSocketMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 单聊功能服务
 * 处理一对一私信的核心业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrivateChatService {

    private final MessageRepository messageRepository;
    private final PrivateSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final WebSocketMessageHandler webSocketHandler;

    /**
     * 发送单聊消息
     */
    @Transactional
    public PrivateChatResponse sendMessage(Long senderId, PrivateChatRequest request) {
        // 验证接收者是否存在
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("接收者不存在"));

        // 验证是否是好友关系（可选）
        // checkFriendship(senderId, request.getReceiverId());

        // 创建消息
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(request.getReceiverId());
        message.setContent(request.getContent());
        message.setMessageType(request.getMessageType());
        message.setSendTime(LocalDateTime.now());
        message.setStatus(0); // 0-未读
        message.setSessionId(getOrCreateSessionId(senderId, request.getReceiverId()));

        Message savedMessage = messageRepository.save(message);

        // 更新会话最后消息
        updateSessionLastMessage(senderId, request.getReceiverId(), savedMessage);

        // WebSocket推送消息给接收者
        webSocketHandler.sendPrivateMessage(request.getReceiverId(), convertToDTO(savedMessage));

        log.info("单聊消息发送成功: {} -> {}", senderId, request.getReceiverId());
        return convertToResponse(savedMessage);
    }

    /**
     * 获取会话列表
     */
    public List<PrivateChatResponse> getSessionList(Long userId) {
        List<PrivateSession> sessions = sessionRepository.findByUserIdOrderByLastMessageTimeDesc(userId);
        return sessions.stream()
                .map(this::convertSessionToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取聊天记录
     */
    public List<MessageDTO> getChatHistory(Long userId, Long targetUserId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sendTime"));
        
        List<Message> messages = messageRepository.findPrivateChatHistory(
                userId, targetUserId, pageRequest);

        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 标记消息已读
     */
    @Transactional
    public void markMessageAsRead(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));

        // 验证消息接收者是当前用户
        if (!message.getReceiverId().equals(userId)) {
            throw new RuntimeException("无权操作此消息");
        }

        message.setStatus(1); // 1-已读
        message.setReadTime(LocalDateTime.now());
        messageRepository.save(message);

        log.info("消息 {} 已标记为已读", messageId);
    }

    /**
     * 撤回消息（2分钟内可撤回）
     */
    @Transactional
    public void recallMessage(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));

        // 验证消息发送者是当前用户
        if (!message.getSenderId().equals(userId)) {
            throw new RuntimeException("只能撤回自己发送的消息");
        }

        // 检查是否在2分钟内
        if (message.getSendTime().plusMinutes(2).isBefore(LocalDateTime.now())) {
            throw new RuntimeException("消息已超过2分钟，无法撤回");
        }

        message.setRecalled(true);
        message.setRecallTime(LocalDateTime.now());
        messageRepository.save(message);

        // 推送撤回通知给接收者
        webSocketHandler.sendMessageRecallNotification(message.getReceiverId(), messageId);

        log.info("消息 {} 已撤回", messageId);
    }

    /**
     * 删除会话
     */
    @Transactional
    public void deleteSession(Long userId, Long targetUserId) {
        sessionRepository.deleteByUserIdAndTargetUserId(userId, targetUserId);
        log.info("用户 {} 删除了与 {} 的会话", userId, targetUserId);
    }

    /**
     * 获取未读消息数
     */
    public Integer getUnreadCount(Long userId) {
        return messageRepository.countUnreadMessages(userId);
    }

    // ============ 私有方法 ============

    private Long getOrCreateSessionId(Long userId1, Long userId2) {
        // 生成会话ID（较小的userId在前）
        Long smallerId = Math.min(userId1, userId2);
        Long largerId = Math.max(userId1, userId2);
        
        return sessionRepository.findByUserIds(smallerId, largerId)
                .map(PrivateSession::getId)
                .orElseGet(() -> createNewSession(smallerId, largerId));
    }

    private Long createNewSession(Long userId1, Long userId2) {
        PrivateSession session = new PrivateSession();
        session.setUserId1(userId1);
        session.setUserId2(userId2);
        session.setCreateTime(LocalDateTime.now());
        PrivateSession saved = sessionRepository.save(session);
        return saved.getId();
    }

    private void updateSessionLastMessage(Long senderId, Long receiverId, Message message) {
        Long smallerId = Math.min(senderId, receiverId);
        Long largerId = Math.max(senderId, receiverId);

        PrivateSession session = sessionRepository.findByUserIds(smallerId, largerId)
                .orElseThrow(() -> new RuntimeException("会话不存在"));

        session.setLastMessageId(message.getId());
        session.setLastMessageContent(message.getContent());
        session.setLastMessageTime(message.getSendTime());
        session.setLastMessageSenderId(senderId);
        sessionRepository.save(session);
    }

    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSenderId());
        dto.setReceiverId(message.getReceiverId());
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setSendTime(message.getSendTime());
        dto.setStatus(message.getStatus());
        dto.setRecalled(message.isRecalled());
        return dto;
    }

    private PrivateChatResponse convertToResponse(Message message) {
        PrivateChatResponse response = new PrivateChatResponse();
        response.setMessageId(message.getId());
        response.setSenderId(message.getSenderId());
        response.setReceiverId(message.getReceiverId());
        response.setContent(message.getContent());
        response.setMessageType(message.getMessageType());
        response.setSendTime(message.getSendTime());
        response.setStatus(message.getStatus());
        return response;
    }

    private PrivateChatResponse convertSessionToResponse(PrivateSession session) {
        PrivateChatResponse response = new PrivateChatResponse();
        response.setSessionId(session.getId());
        response.setTargetUserId(session.getUserId1());
        response.setLastMessageContent(session.getLastMessageContent());
        response.setLastMessageTime(session.getLastMessageTime());
        return response;
    }
}
