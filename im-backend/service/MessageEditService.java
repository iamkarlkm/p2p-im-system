package com.im.backend.service;

import com.im.backend.dto.MessageEditDTO;
import com.im.backend.model.Message;
import com.im.backend.model.MessageEditHistory;
import com.im.backend.repository.MessageEditHistoryRepository;
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
 * 消息编辑服务核心类
 * 负责处理消息编辑逻辑、编辑历史管理、编辑时间限制等
 */
@Service
public class MessageEditService {

    private static final Logger logger = LoggerFactory.getLogger(MessageEditService.class);

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageEditHistoryRepository editHistoryRepository;

    @Autowired
    private WebSocketSessionManager sessionManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${message.edit.time-limit-seconds:300}")
    private int editTimeLimitSeconds;

    @Value("${message.edit.max-edit-count:10}")
    private int maxEditCount;

    @Value("${message.edit.save-history:true}")
    private boolean saveEditHistory;

    @Value("${message.edit.show-edit-mark:true}")
    private boolean showEditMark;

    /**
     * 编辑消息（核心方法）
     */
    @Transactional
    public MessageEditDTO editMessage(Long messageId, Long userId, String newContent, String editReason) {
        logger.info("User {} attempting to edit message {}", userId, messageId);

        try {
            // 1. 查找消息
            Optional<Message> messageOpt = messageRepository.findById(messageId);
            if (!messageOpt.isPresent()) {
                return createErrorDTO("MESSAGE_NOT_FOUND", "消息不存在或已被删除");
            }

            Message message = messageOpt.get();

            // 2. 检查消息是否已被撤回
            if (message.isRecalled()) {
                return createErrorDTO("MESSAGE_RECALLED", "该消息已被撤回，无法编辑");
            }

            // 3. 验证编辑权限（只有发送者可以编辑）
            if (!message.getSenderId().equals(userId)) {
                return createErrorDTO("NO_PERMISSION", "只有消息发送者可以编辑此消息");
            }

            // 4. 检查编辑时间限制
            if (!isWithinEditTimeLimit(message)) {
                return createErrorDTO("TIME_LIMIT_EXCEEDED",
                    "消息发送超过" + editTimeLimitSeconds + "秒，无法编辑");
            }

            // 5. 检查编辑次数限制
            if (!checkEditCountLimit(messageId)) {
                return createErrorDTO("EDIT_COUNT_EXCEEDED",
                    "该消息已达到最大编辑次数限制（" + maxEditCount + "次）");
            }

            // 6. 执行编辑
            return executeEdit(message, userId, newContent, editReason);

        } catch (Exception e) {
            logger.error("Failed to edit message {}: {}", messageId, e.getMessage(), e);
            return createErrorDTO("INTERNAL_ERROR", "编辑失败，请稍后重试");
        }
    }

    /**
     * 执行编辑操作
     */
    private MessageEditDTO executeEdit(Message message, Long userId, String newContent, String editReason) {
        LocalDateTime editTime = LocalDateTime.now();
        String originalContent = message.getContent();
        int editVersion = getNextEditVersion(message.getId());

        // 1. 保存编辑历史
        if (saveEditHistory) {
            saveEditHistory(message, originalContent, newContent, userId, editReason, editVersion);
        }

        // 2. 更新消息内容
        message.setContent(newContent);
        message.setEdited(true);
        message.setEditTime(editTime);
        message.setEditCount(message.getEditCount() + 1);
        message.setEditVersion(editVersion);
        message.setUpdatedAt(editTime);

        messageRepository.save(message);

        // 3. 发送编辑通知
        sendEditNotification(message, userId, originalContent);

        // 4. 构建返回DTO
        MessageEditDTO result = new MessageEditDTO();
        result.setSuccess(true);
        result.setMessageId(message.getId());
        result.setConversationId(message.getConversationId());
        result.setNewContent(newContent);
        result.setOriginalContent(originalContent);
        result.setEditedBy(userId);
        result.setEditTime(editTime);
        result.setEditVersion(editVersion);
        result.setEditCount(message.getEditCount());

        logger.info("Message {} edited successfully by user {}, version {}",
            message.getId(), userId, editVersion);

        return result;
    }

    /**
     * 保存编辑历史
     */
    private void saveEditHistory(Message message, String oldContent, String newContent,
                                 Long userId, String reason, int version) {
        MessageEditHistory history = new MessageEditHistory();
        history.setMessageId(message.getId());
        history.setConversationId(message.getConversationId());
        history.setSenderId(message.getSenderId());
        history.setEditedBy(userId);
        history.setEditTime(LocalDateTime.now());
        history.setOldContent(oldContent);
        history.setNewContent(newContent);
        history.setEditReason(reason);
        history.setEditVersion(version);
        history.setMessageType(message.getMessageType());

        editHistoryRepository.save(history);
    }

    /**
     * 发送编辑通知
     */
    private void sendEditNotification(Message message, Long userId, String originalContent) {
        try {
            ObjectNode notification = objectMapper.createObjectNode();
            notification.put("type", "MESSAGE_EDITED");
            notification.put("messageId", message.getId());
            notification.put("conversationId", message.getConversationId());
            notification.put("editedBy", userId);
            notification.put("editTime", LocalDateTime.now().toString());
            notification.put("newContent", message.getContent());
            notification.put("editVersion", message.getEditVersion());
            notification.put("editCount", message.getEditCount());

            String notificationJson = objectMapper.writeValueAsString(notification);

            // 发送给所有接收者
            message.getReceiverIds().forEach(receiverId -> {
                sessionManager.sendMessage(receiverId, notificationJson);
            });

            logger.debug("Edit notification sent for message {}", message.getId());

        } catch (Exception e) {
            logger.error("Failed to send edit notification: {}", e.getMessage());
        }
    }

    /**
     * 检查是否在编辑时间限制内
     */
    private boolean isWithinEditTimeLimit(Message message) {
        LocalDateTime sentTime = message.getSentTime();
        LocalDateTime now = LocalDateTime.now();
        long secondsElapsed = ChronoUnit.SECONDS.between(sentTime, now);

        return secondsElapsed <= editTimeLimitSeconds;
    }

    /**
     * 检查编辑次数限制
     */
    private boolean checkEditCountLimit(Long messageId) {
        int currentEditCount = editHistoryRepository.countByMessageId(messageId);
        return currentEditCount < maxEditCount;
    }

    /**
     * 获取下一个编辑版本号
     */
    private int getNextEditVersion(Long messageId) {
        return editHistoryRepository.countByMessageId(messageId) + 1;
    }

    /**
     * 获取消息的编辑历史
     */
    public List<MessageEditHistory> getEditHistory(Long messageId) {
        return editHistoryRepository.findByMessageIdOrderByEditVersionDesc(messageId);
    }

    /**
     * 获取消息的编辑历史（分页）
     */
    public Page<MessageEditHistory> getEditHistory(Long messageId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return editHistoryRepository.findByMessageIdOrderByEditTimeDesc(messageId, pageable);
    }

    /**
     * 获取用户编辑历史
     */
    public Page<MessageEditHistory> getUserEditHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return editHistoryRepository.findByEditedByOrderByEditTimeDesc(userId, pageable);
    }

    /**
     * 恢复消息到指定版本
     */
    @Transactional
    public MessageEditDTO revertToVersion(Long messageId, Long userId, int targetVersion) {
        logger.info("User {} reverting message {} to version {}", userId, messageId, targetVersion);

        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (!messageOpt.isPresent()) {
            return createErrorDTO("MESSAGE_NOT_FOUND", "消息不存在");
        }

        Message message = messageOpt.get();

        if (!message.getSenderId().equals(userId)) {
            return createErrorDTO("NO_PERMISSION", "只有发送者可以恢复消息版本");
        }

        // 查找目标版本的历史记录
        Optional<MessageEditHistory> historyOpt =
            editHistoryRepository.findByMessageIdAndEditVersion(messageId, targetVersion);

        if (!historyOpt.isPresent()) {
            return createErrorDTO("VERSION_NOT_FOUND", "指定的版本不存在");
        }

        MessageEditHistory history = historyOpt.get();
        return editMessage(messageId, userId, history.getOldContent(), "恢复到版本 " + targetVersion);
    }

    /**
     * 比较两个版本的差异
     */
    public String compareVersions(Long messageId, int version1, int version2) {
        Optional<MessageEditHistory> history1 =
            editHistoryRepository.findByMessageIdAndEditVersion(messageId, version1);
        Optional<MessageEditHistory> history2 =
            editHistoryRepository.findByMessageIdAndEditVersion(messageId, version2);

        if (!history1.isPresent() || !history2.isPresent()) {
            return "版本不存在";
        }

        String content1 = history1.get().getNewContent();
        String content2 = history2.get().getNewContent();

        return generateDiff(content1, content2);
    }

    /**
     * 生成文本差异（简化版）
     */
    private String generateDiff(String oldText, String newText) {
        if (oldText.equals(newText)) {
            return "内容相同";
        }

        return "旧版本: " + oldText + "\n新版本: " + newText;
    }

    /**
     * 获取编辑配置
     */
    public MessageEditDTO.EditConfig getEditConfig() {
        MessageEditDTO.EditConfig config = new MessageEditDTO.EditConfig();
        config.setTimeLimitSeconds(editTimeLimitSeconds);
        config.setMaxEditCount(maxEditCount);
        config.setSaveHistory(saveEditHistory);
        config.setShowEditMark(showEditMark);
        return config;
    }

    /**
     * 更新编辑配置（仅管理员）
     */
    public void updateEditConfig(int timeLimit, int maxCount) {
        if (timeLimit < 30 || timeLimit > 86400) {
            throw new IllegalArgumentException("编辑时间限制必须在30秒到24小时之间");
        }
        if (maxCount < 1 || maxCount > 100) {
            throw new IllegalArgumentException("编辑次数限制必须在1到100之间");
        }

        this.editTimeLimitSeconds = timeLimit;
        this.maxEditCount = maxCount;

        logger.info("Edit config updated: timeLimit={}, maxCount={}", timeLimit, maxCount);
    }

    /**
     * 异步编辑消息
     */
    @Transactional
    public CompletableFuture<MessageEditDTO> editMessageAsync(Long messageId, Long userId,
                                                               String newContent, String reason) {
        return CompletableFuture.supplyAsync(() -> editMessage(messageId, userId, newContent, reason));
    }

    /**
     * 创建错误DTO
     */
    private MessageEditDTO createErrorDTO(String errorCode, String errorMessage) {
        MessageEditDTO dto = new MessageEditDTO();
        dto.setSuccess(false);
        dto.setErrorCode(errorCode);
        dto.setErrorMessage(errorMessage);
        return dto;
    }
}
