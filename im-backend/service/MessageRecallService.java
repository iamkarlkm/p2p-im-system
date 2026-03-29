package com.im.backend.service;

import com.im.backend.dto.MessageRecallDTO;
import com.im.backend.model.Message;
import com.im.backend.model.MessageRecallLog;
import com.im.backend.repository.MessageRecallRepository;
import com.im.backend.repository.MessageRepository;
import com.im.backend.websocket.WebSocketSessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * 消息撤回服务核心类
 * 负责处理消息撤回逻辑、时间限制验证、撤回通知等
 */
@Service
public class MessageRecallService {

    private static final Logger logger = LoggerFactory.getLogger(MessageRecallService.class);

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageRecallRepository recallRepository;

    @Autowired
    private WebSocketSessionManager sessionManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${message.recall.time-limit-seconds:120}")
    private int recallTimeLimitSeconds;

    @Value("${message.recall.allow-admin:true}")
    private boolean allowAdminRecall;

    @Value("${message.recall.save-log:true}")
    private boolean saveRecallLog;

    /**
     * 撤回消息（核心方法）
     *
     * @param messageId 消息ID
     * @param userId 撤回用户ID
     * @param recallType 撤回类型（USER-用户撤回，ADMIN-管理员撤回）
     * @return 撤回结果DTO
     */
    @Transactional
    public MessageRecallDTO recallMessage(Long messageId, Long userId, String recallType) {
        logger.info("Attempting to recall message {} by user {}, type: {}", messageId, userId, recallType);

        try {
            // 1. 查找消息
            Optional<Message> messageOpt = messageRepository.findById(messageId);
            if (!messageOpt.isPresent()) {
                return createErrorDTO("MESSAGE_NOT_FOUND", "消息不存在或已被删除");
            }

            Message message = messageOpt.get();

            // 2. 检查消息是否已被撤回
            if (message.isRecalled()) {
                return createErrorDTO("ALREADY_RECALLED", "该消息已被撤回");
            }

            // 3. 验证撤回权限
            if (!hasRecallPermission(message, userId, recallType)) {
                return createErrorDTO("NO_PERMISSION", "您没有权限撤回此消息");
            }

            // 4. 检查撤回时间限制
            if (!isWithinRecallTimeLimit(message)) {
                return createErrorDTO("TIME_LIMIT_EXCEEDED",
                    "消息发送超过" + recallTimeLimitSeconds + "秒，无法撤回");
            }

            // 5. 执行撤回
            return executeRecall(message, userId, recallType);

        } catch (Exception e) {
            logger.error("Failed to recall message {}: {}", messageId, e.getMessage(), e);
            return createErrorDTO("INTERNAL_ERROR", "撤回失败，请稍后重试");
        }
    }

    /**
     * 批量撤回消息
     */
    @Transactional
    public List<MessageRecallDTO> batchRecallMessages(List<Long> messageIds, Long userId, String recallType) {
        logger.info("Batch recalling {} messages by user {}", messageIds.size(), userId);

        return messageIds.stream()
            .map(msgId -> recallMessage(msgId, userId, recallType))
            .toList();
    }

    /**
     * 验证撤回权限
     */
    private boolean hasRecallPermission(Message message, Long userId, String recallType) {
        // 管理员可以撤回任何消息（如果配置允许）
        if ("ADMIN".equals(recallType) && allowAdminRecall) {
            return true;
        }

        // 消息发送者可以撤回自己的消息
        return message.getSenderId().equals(userId);
    }

    /**
     * 检查是否在撤回时间限制内
     */
    private boolean isWithinRecallTimeLimit(Message message) {
        LocalDateTime sentTime = message.getSentTime();
        LocalDateTime now = LocalDateTime.now();
        long secondsElapsed = ChronoUnit.SECONDS.between(sentTime, now);

        return secondsElapsed <= recallTimeLimitSeconds;
    }

    /**
     * 执行撤回操作
     */
    private MessageRecallDTO executeRecall(Message message, Long userId, String recallType) {
        LocalDateTime recallTime = LocalDateTime.now();

        // 1. 更新消息状态为已撤回
        message.setRecalled(true);
        message.setRecallTime(recallTime);
        message.setRecalledBy(userId);
        message.setRecallType(recallType);
        message.setContent("[此消息已被撤回]");
        message.setUpdatedAt(recallTime);

        messageRepository.save(message);

        // 2. 保存撤回日志
        MessageRecallLog recallLog = saveRecallLog(message, userId, recallType);

        // 3. 发送撤回通知
        sendRecallNotification(message, userId, recallType);

        // 4. 构建返回DTO
        MessageRecallDTO result = new MessageRecallDTO();
        result.setSuccess(true);
        result.setMessageId(message.getId());
        result.setConversationId(message.getConversationId());
        result.setRecalledBy(userId);
        result.setRecallTime(recallTime);
        result.setRecallType(recallType);
        result.setOriginalContent(recallLog.getOriginalContent());

        logger.info("Message {} recalled successfully by user {}", message.getId(), userId);

        return result;
    }

    /**
     * 保存撤回日志
     */
    private MessageRecallLog saveRecallLog(Message message, Long userId, String recallType) {
        if (!saveRecallLog) {
            return null;
        }

        MessageRecallLog log = new MessageRecallLog();
        log.setMessageId(message.getId());
        log.setConversationId(message.getConversationId());
        log.setSenderId(message.getSenderId());
        log.setRecalledBy(userId);
        log.setRecallTime(LocalDateTime.now());
        log.setRecallType(recallType);
        log.setOriginalContent(message.getContent());
        log.setMessageType(message.getMessageType());

        return recallRepository.save(log);
    }

    /**
     * 发送撤回通知
     */
    private void sendRecallNotification(Message message, Long userId, String recallType) {
        try {
            ObjectNode notification = objectMapper.createObjectNode();
            notification.put("type", "MESSAGE_RECALLED");
            notification.put("messageId", message.getId());
            notification.put("conversationId", message.getConversationId());
            notification.put("recalledBy", userId);
            notification.put("recallType", recallType);
            notification.put("recallTime", LocalDateTime.now().toString());

            String notificationJson = objectMapper.writeValueAsString(notification);

            // 发送给消息接收者
            message.getReceiverIds().forEach(receiverId -> {
                sessionManager.sendMessage(receiverId, notificationJson);
            });

            // 发送给消息发送者（除撤回者外）
            if (!message.getSenderId().equals(userId)) {
                sessionManager.sendMessage(message.getSenderId(), notificationJson);
            }

            logger.debug("Recall notification sent for message {}", message.getId());

        } catch (Exception e) {
            logger.error("Failed to send recall notification: {}", e.getMessage());
        }
    }

    /**
     * 获取消息的撤回历史
     */
    public List<MessageRecallLog> getRecallHistory(Long messageId) {
        return recallRepository.findByMessageIdOrderByRecallTimeDesc(messageId);
    }

    /**
     * 获取用户的撤回记录
     */
    public Page<MessageRecallLog> getUserRecallHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return recallRepository.findByRecalledByOrderByRecallTimeDesc(userId, pageable);
    }

    /**
     * 获取会话中的撤回记录
     */
    public Page<MessageRecallLog> getConversationRecallHistory(Long conversationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return recallRepository.findByConversationIdOrderByRecallTimeDesc(conversationId, pageable);
    }

    /**
     * 检查消息是否可撤回
     */
    public boolean canRecall(Long messageId, Long userId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (!messageOpt.isPresent()) {
            return false;
        }

        Message message = messageOpt.get();
        return !message.isRecalled()
            && hasRecallPermission(message, userId, "USER")
            && isWithinRecallTimeLimit(message);
    }

    /**
     * 获取撤回时间限制
     */
    public int getRecallTimeLimit() {
        return recallTimeLimitSeconds;
    }

    /**
     * 更新撤回时间限制（仅管理员）
     */
    public void updateRecallTimeLimit(int seconds) {
        if (seconds < 10 || seconds > 86400) {
            throw new IllegalArgumentException("撤回时间限制必须在10秒到24小时之间");
        }
        this.recallTimeLimitSeconds = seconds;
        logger.info("Recall time limit updated to {} seconds", seconds);
    }

    /**
     * 异步撤回消息
     */
    @Transactional
    public CompletableFuture<MessageRecallDTO> recallMessageAsync(Long messageId, Long userId, String recallType) {
        return CompletableFuture.supplyAsync(() -> recallMessage(messageId, userId, recallType));
    }

    /**
     * 获取会话中可撤回的消息列表
     */
    public List<Message> getRecallableMessages(Long conversationId, Long userId) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusSeconds(recallTimeLimitSeconds);
        return messageRepository.findRecallableMessages(conversationId, userId, cutoffTime);
    }

    /**
     * 创建错误DTO
     */
    private MessageRecallDTO createErrorDTO(String errorCode, String errorMessage) {
        MessageRecallDTO dto = new MessageRecallDTO();
        dto.setSuccess(false);
        dto.setErrorCode(errorCode);
        dto.setErrorMessage(errorMessage);
        return dto;
    }

    /**
     * 撤销撤回（恢复消息）- 仅管理员可用
     */
    @Transactional
    public MessageRecallDTO undoRecall(Long messageId, Long adminId) {
        logger.info("Attempting to undo recall for message {} by admin {}", messageId, adminId);

        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (!messageOpt.isPresent()) {
            return createErrorDTO("MESSAGE_NOT_FOUND", "消息不存在");
        }

        Message message = messageOpt.get();
        if (!message.isRecalled()) {
            return createErrorDTO("NOT_RECALLED", "该消息未被撤回");
        }

        // 查找撤回日志恢复原始内容
        List<MessageRecallLog> logs = recallRepository.findByMessageIdOrderByRecallTimeDesc(messageId);
        String originalContent = logs.isEmpty() ? "" : logs.get(0).getOriginalContent();

        // 恢复消息
        message.setRecalled(false);
        message.setRecallTime(null);
        message.setRecalledBy(null);
        message.setRecallType(null);
        message.setContent(originalContent);
        message.setUpdatedAt(LocalDateTime.now());

        messageRepository.save(message);

        MessageRecallDTO result = new MessageRecallDTO();
        result.setSuccess(true);
        result.setMessageId(messageId);
        result.setRecalledBy(adminId);
        result.setRecallTime(LocalDateTime.now());
        result.setRecallType("UNDO_RECALL");

        logger.info("Recall undone for message {} by admin {}", messageId, adminId);

        return result;
    }
}
