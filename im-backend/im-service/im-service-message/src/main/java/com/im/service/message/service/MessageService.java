package com.im.service.message.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.service.message.dto.MessageResponse;
import com.im.service.message.dto.SendMessageRequest;
import com.im.service.message.entity.Message;
import com.im.service.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息服务 - 核心业务逻辑实现
 * 
 * @author IM Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper;

    @Value("${message.recall.timeout-minutes:2}")
    private int recallTimeoutMinutes;

    // ========== 消息发送 ==========

    /**
     * 发送消息
     */
    @Transactional
    public MessageResponse sendMessage(SendMessageRequest request) {
        // 检查客户端消息ID是否已存在(去重)
        if (StringUtils.hasText(request.getClientMessageId())) {
            Optional<Message> existing = messageRepository.findByClientMessageId(request.getClientMessageId());
            if (existing.isPresent()) {
                log.info("Message with clientMessageId {} already exists, returning existing message", 
                        request.getClientMessageId());
                return toResponse(existing.get());
            }
        }

        Message message = new Message();
        message.setClientMessageId(request.getClientMessageId());
        message.setConversationId(request.getConversationId());
        message.setSenderId(request.getSenderId());
        message.setReceiverId(request.getReceiverId());
        message.setConversationType(request.getConversationType());
        message.setType(request.getType());
        message.setContent(request.getContent());
        message.setContentSummary(generateContentSummary(request));
        message.setStatus("SENT");
        message.setSequence(request.getSequence());
        message.setDeviceType(request.getDeviceType());

        // 引用消息
        if (StringUtils.hasText(request.getReplyToId())) {
            message.setReplyToId(request.getReplyToId());
            // 查询引用消息信息
            final Message finalMessage = message;
            messageRepository.findById(request.getReplyToId()).ifPresent(replyMsg -> {
                finalMessage.setReplyToSenderId(replyMsg.getSenderId());
                finalMessage.setReplyToContentSummary(replyMsg.getContentSummary());
                finalMessage.setRootMessageId(StringUtils.hasText(replyMsg.getRootMessageId()) 
                        ? replyMsg.getRootMessageId() : replyMsg.getId());
            });
        }

        // 附件处理
        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            message.setAttachmentCount(request.getAttachments().size());
            try {
                message.setAttachments(objectMapper.writeValueAsString(request.getAttachments()));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize attachments", e);
            }
            
            // 设置第一个附件的信息到主字段
            SendMessageRequest.AttachmentDTO firstAttachment = request.getAttachments().get(0);
            message.setFileSize(firstAttachment.getSize());
            message.setMimeType(firstAttachment.getMimeType());
            message.setFileUrl(firstAttachment.getUrl());
            message.setThumbnailUrl(firstAttachment.getThumbnailUrl());
        } else if (request.getAttachment() != null) {
            // 兼容旧版单附件
            message.setAttachmentCount(1);
            try {
                message.setAttachments(objectMapper.writeValueAsString(Collections.singletonList(request.getAttachment())));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize attachment", e);
            }
            message.setFileSize(request.getFileSize());
            message.setMimeType(request.getMimeType());
            message.setFileUrl(request.getFileUrl());
            message.setThumbnailUrl(request.getThumbnailUrl());
        }

        // 位置信息
        if (request.getLocation() != null) {
            try {
                message.setLocation(objectMapper.writeValueAsString(request.getLocation()));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize location", e);
            }
        }

        // @提及处理
        if (request.getMentions() != null && !request.getMentions().isEmpty()) {
            try {
                message.setMentions(objectMapper.writeValueAsString(request.getMentions()));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize mentions", e);
            }
        }
        message.setMentionAll(request.getMentionAll() != null ? request.getMentionAll() : false);

        // 加密和阅后即焚
        message.setEncrypted(request.getEncrypted() != null ? request.getEncrypted() : false);
        message.setEncryptionType(request.getEncryptionType());
        message.setSelfDestruct(request.getSelfDestruct() != null ? request.getSelfDestruct() : false);
        message.setSelfDestructTime(request.getSelfDestructTime());

        // 扩展数据
        if (request.getExtraData() != null) {
            try {
                message.setExtraData(objectMapper.writeValueAsString(request.getExtraData()));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize extraData", e);
            }
        }

        message = messageRepository.save(message);
        log.info("Message sent successfully: id={}, conversationId={}, senderId={}", 
                message.getId(), message.getConversationId(), message.getSenderId());
        
        return toResponse(message);
    }

    /**
     * 生成内容摘要
     */
    private String generateContentSummary(SendMessageRequest request) {
        if (StringUtils.hasText(request.getContentSummary())) {
            return request.getContentSummary();
        }
        
        String content = request.getContent();
        if (!StringUtils.hasText(content)) {
            return getDefaultSummaryByType(request.getType());
        }
        
        // 截取前100个字符作为摘要
        if (content.length() > 100) {
            return content.substring(0, 100) + "...";
        }
        return content;
    }

    /**
     * 根据消息类型获取默认摘要
     */
    private String getDefaultSummaryByType(String type) {
        return switch (type != null ? type.toUpperCase() : "TEXT") {
            case "IMAGE" -> "[图片]";
            case "FILE" -> "[文件]";
            case "VOICE" -> "[语音]";
            case "VIDEO" -> "[视频]";
            case "LOCATION" -> "[位置]";
            case "SYSTEM" -> "[系统消息]";
            case "CARD" -> "[名片]";
            case "MERGE" -> "[聊天记录]";
            default -> "[消息]";
        };
    }

    // ========== 消息查询 ==========

    /**
     * 获取单条消息
     */
    public Optional<MessageResponse> getMessage(String messageId, String userId) {
        return messageRepository.findByIdAndUserId(messageId, userId)
                .filter(msg -> msg.canView(userId))
                .map(this::toResponse);
    }

    /**
     * 获取会话消息列表(分页)
     */
    public List<MessageResponse> getConversationMessages(String conversationId, String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return messageRepository.findByConversationIdAndUserId(conversationId, userId, pageable)
                .getContent().stream()
                .filter(msg -> msg.canView(userId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取会话消息列表(分页，返回Page对象)
     */
    public Page<MessageResponse> getConversationMessagesPage(String conversationId, String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return messageRepository.findByConversationIdAndUserId(conversationId, userId, pageable)
                .map(this::toResponse);
    }

    /**
     * 获取会话中指定时间之后的消息
     */
    public List<MessageResponse> getMessagesSince(String conversationId, LocalDateTime since, String userId) {
        return messageRepository.findByConversationIdSince(conversationId, since, userId)
                .stream()
                .filter(msg -> msg.canView(userId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 搜索会话中的消息
     */
    public List<MessageResponse> searchMessages(String conversationId, String keyword, String userId) {
        return messageRepository.searchMessages(conversationId, keyword, userId)
                .stream()
                .filter(msg -> msg.canView(userId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 全局搜索用户的消息
     */
    public Page<MessageResponse> searchUserMessages(String userId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return messageRepository.searchUserMessages(userId, keyword, pageable)
                .map(this::toResponse);
    }

    /**
     * 获取会话最新一条消息
     */
    public Optional<MessageResponse> getLatestMessage(String conversationId, String userId) {
        return messageRepository.findLatestMessage(conversationId, userId, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .filter(msg -> msg.canView(userId))
                .map(this::toResponse);
    }

    /**
     * 获取置顶消息
     */
    public List<MessageResponse> getPinnedMessages(String conversationId, String userId) {
        return messageRepository.findPinnedMessages(conversationId, userId)
                .stream()
                .filter(msg -> msg.canView(userId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ========== 消息状态更新 ==========

    /**
     * 标记消息为已发送
     */
    @Transactional
    public boolean markAsSent(String messageId) {
        int updated = messageRepository.markAsSent(messageId, LocalDateTime.now());
        return updated > 0;
    }

    /**
     * 标记消息为已送达
     */
    @Transactional
    public boolean markAsDelivered(String messageId) {
        int updated = messageRepository.markAsDelivered(messageId, LocalDateTime.now());
        return updated > 0;
    }

    /**
     * 标记消息为已读
     */
    @Transactional
    public boolean markAsRead(String messageId) {
        int updated = messageRepository.markAsRead(messageId, LocalDateTime.now());
        return updated > 0;
    }

    /**
     * 标记会话中所有消息为已读
     */
    @Transactional
    public int markConversationAsRead(String conversationId, String userId) {
        return messageRepository.markConversationAsRead(conversationId, userId, LocalDateTime.now());
    }

    // ========== 消息撤回 ==========

    /**
     * 撤回消息
     */
    @Transactional
    public boolean recallMessage(String messageId, String userId) {
        Optional<Message> opt = messageRepository.findById(messageId);
        if (opt.isEmpty()) {
            log.warn("Message not found: {}", messageId);
            return false;
        }

        Message message = opt.get();
        
        // 检查是否可以撤回
        if (!message.canRecall(userId, recallTimeoutMinutes)) {
            log.warn("Cannot recall message {}: senderId={}, userId={}, createdAt={}", 
                    messageId, message.getSenderId(), userId, message.getCreatedAt());
            return false;
        }

        int updated = messageRepository.recallMessage(messageId, userId, LocalDateTime.now());
        if (updated > 0) {
            log.info("Message recalled successfully: id={}, recalledBy={}", messageId, userId);
        }
        return updated > 0;
    }

    /**
     * 检查消息是否可以撤回
     */
    public boolean canRecall(String messageId, String userId) {
        return messageRepository.findById(messageId)
                .map(msg -> msg.canRecall(userId, recallTimeoutMinutes))
                .orElse(false);
    }

    // ========== 消息删除 ==========

    /**
     * 删除消息
     */
    @Transactional
    public boolean deleteMessage(String messageId, String userId) {
        Optional<Message> opt = messageRepository.findById(messageId);
        if (opt.isEmpty()) {
            return false;
        }

        Message message = opt.get();
        LocalDateTime now = LocalDateTime.now();
        
        // 发送者删除
        if (message.getSenderId().equals(userId)) {
            int updated = messageRepository.deleteBySender(messageId, now);
            log.info("Message deleted by sender: id={}", messageId);
            return updated > 0;
        }
        
        // 接收者删除
        if (message.getReceiverId().equals(userId)) {
            int updated = messageRepository.deleteByReceiver(messageId, now);
            log.info("Message deleted by receiver: id={}", messageId);
            return updated > 0;
        }

        return false;
    }

    /**
     * 批量删除消息
     */
    @Transactional
    public int batchDeleteMessages(List<String> messageIds, String userId) {
        if (messageIds == null || messageIds.isEmpty()) {
            return 0;
        }
        return messageRepository.batchDeleteBySender(messageIds, LocalDateTime.now());
    }

    // ========== 消息置顶 ==========

    /**
     * 置顶消息
     */
    @Transactional
    public boolean pinMessage(String messageId, String userId) {
        int updated = messageRepository.pinMessage(messageId, userId, LocalDateTime.now());
        if (updated > 0) {
            log.info("Message pinned: id={}, pinnedBy={}", messageId, userId);
        }
        return updated > 0;
    }

    /**
     * 取消置顶消息
     */
    @Transactional
    public boolean unpinMessage(String messageId) {
        int updated = messageRepository.unpinMessage(messageId, LocalDateTime.now());
        if (updated > 0) {
            log.info("Message unpinned: id={}", messageId);
        }
        return updated > 0;
    }

    // ========== 消息收藏 ==========

    /**
     * 收藏消息
     */
    @Transactional
    public boolean favoriteMessage(String messageId, String userId) {
        int updated = messageRepository.favoriteMessage(messageId, userId, LocalDateTime.now());
        return updated > 0;
    }

    /**
     * 取消收藏消息
     */
    @Transactional
    public boolean unfavoriteMessage(String messageId, String userId) {
        int updated = messageRepository.unfavoriteMessage(messageId, userId, LocalDateTime.now());
        return updated > 0;
    }

    /**
     * 获取用户收藏的消息
     */
    public Page<MessageResponse> getFavoriteMessages(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "favoritedAt"));
        return messageRepository.findBySenderIdAndFavoritedTrueAndSenderDeletedFalseOrderByFavoritedAtDesc(userId, pageable)
                .map(this::toResponse);
    }

    // ========== 消息编辑 ==========

    /**
     * 编辑消息
     */
    @Transactional
    public boolean editMessage(String messageId, String userId, String newContent) {
        Optional<Message> opt = messageRepository.findById(messageId);
        if (opt.isEmpty()) {
            return false;
        }

        Message message = opt.get();
        // 只能编辑自己的消息
        if (!message.getSenderId().equals(userId)) {
            return false;
        }
        
        // 不能编辑已被撤回的消息
        if (message.getRecalled()) {
            return false;
        }

        int updated = messageRepository.editMessage(messageId, newContent, LocalDateTime.now());
        if (updated > 0) {
            log.info("Message edited: id={}", messageId);
        }
        return updated > 0;
    }

    // ========== 统计方法 ==========

    /**
     * 获取未读消息数
     */
    public long getUnreadCount(String userId) {
        return messageRepository.countUnreadByReceiver(userId);
    }

    /**
     * 获取会话未读消息数
     */
    public long getUnreadCountByConversation(String conversationId, String userId) {
        return messageRepository.countUnreadByConversation(conversationId, userId);
    }

    /**
     * 获取用户发送的消息数
     */
    public long getSentMessageCount(String userId) {
        return messageRepository.countBySenderIdAndSenderDeletedFalse(userId);
    }

    // ========== 转换方法 ==========

    /**
     * 实体转换为响应DTO
     */
    private MessageResponse toResponse(Message message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setClientMessageId(message.getClientMessageId());
        response.setConversationId(message.getConversationId());
        response.setConversationType(message.getConversationType());
        response.setSenderId(message.getSenderId());
        response.setReceiverId(message.getReceiverId());
        response.setType(message.getType());
        response.setContent(message.getContent());
        response.setContentSummary(message.getContentSummary());
        response.setStatus(message.getStatus());
        response.setSequence(message.getSequence());

        // 删除状态
        response.setSenderDeleted(message.getSenderDeleted());
        response.setReceiverDeleted(message.getReceiverDeleted());

        // 撤回状态
        response.setRecalled(message.getRecalled());
        response.setRecalledAt(message.getRecalledAt());
        response.setRecalledBy(message.getRecalledBy());

        // 收藏状态
        response.setFavorited(message.getFavorited());
        response.setFavoritedAt(message.getFavoritedAt());

        // 置顶状态
        response.setPinned(message.getPinned());
        response.setPinnedAt(message.getPinnedAt());
        response.setPinnedBy(message.getPinnedBy());

        // 引用消息
        response.setReplyToId(message.getReplyToId());
        response.setReplyToSenderId(message.getReplyToSenderId());
        response.setReplyToContentSummary(message.getReplyToContentSummary());
        response.setRootMessageId(message.getRootMessageId());

        // 附件
        response.setAttachmentCount(message.getAttachmentCount());
        response.setFileSize(message.getFileSize());
        response.setMimeType(message.getMimeType());
        response.setFileUrl(message.getFileUrl());
        response.setThumbnailUrl(message.getThumbnailUrl());
        
        // 解析附件JSON
        if (StringUtils.hasText(message.getAttachments())) {
            try {
                List<Map<String, Object>> attachments = objectMapper.readValue(
                        message.getAttachments(), new TypeReference<List<Map<String, Object>>>() {});
                
                List<MessageResponse.AttachmentResponse> attachmentResponses = attachments.stream()
                        .map(this::mapToAttachmentResponse)
                        .collect(Collectors.toList());
                response.setAttachments(attachmentResponses);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse attachments", e);
            }
        }

        // 位置信息
        if (StringUtils.hasText(message.getLocation())) {
            try {
                Map<String, Object> locationMap = objectMapper.readValue(
                        message.getLocation(), new TypeReference<Map<String, Object>>() {});
                MessageResponse.LocationResponse locationResponse = new MessageResponse.LocationResponse();
                locationResponse.setLatitude(getDoubleValue(locationMap.get("latitude")));
                locationResponse.setLongitude(getDoubleValue(locationMap.get("longitude")));
                locationResponse.setAddress((String) locationMap.get("address"));
                locationResponse.setName((String) locationMap.get("name"));
                locationResponse.setZoom(getIntValue(locationMap.get("zoom")));
                response.setLocation(locationResponse);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse location", e);
            }
        }

        // 已读统计
        response.setReadCount(message.getReadCount());
        response.setUnreadCount(message.getUnreadCount());

        // 安全/加密
        response.setEncrypted(message.getEncrypted());
        response.setEncryptionType(message.getEncryptionType());
        response.setSelfDestruct(message.getSelfDestruct());
        response.setSelfDestructTime(message.getSelfDestructTime());
        response.setDestroyed(message.getDestroyed());
        response.setDestroyedAt(message.getDestroyedAt());

        // 编辑状态
        response.setEdited(message.getEdited());
        response.setEditedAt(message.getEditedAt());

        // 提及
        response.setMentionAll(message.getMentionAll());
        if (StringUtils.hasText(message.getMentions())) {
            try {
                List<String> mentions = objectMapper.readValue(
                        message.getMentions(), new TypeReference<List<String>>() {});
                response.setMentions(mentions);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse mentions", e);
            }
        }

        // 时间戳
        response.setCreatedAt(message.getCreatedAt());
        response.setUpdatedAt(message.getUpdatedAt());
        response.setSentAt(message.getSentAt());
        response.setDeliveredAt(message.getDeliveredAt());
        response.setReadAt(message.getReadAt());

        // 扩展数据
        if (StringUtils.hasText(message.getExtraData())) {
            try {
                Map<String, Object> extraData = objectMapper.readValue(
                        message.getExtraData(), new TypeReference<Map<String, Object>>() {});
                response.setExtraData(extraData);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse extraData", e);
            }
        }

        // 表情回应
        if (StringUtils.hasText(message.getReactions())) {
            try {
                List<Map<String, Object>> reactions = objectMapper.readValue(
                        message.getReactions(), new TypeReference<List<Map<String, Object>>>() {});
                // 这里可以进一步转换为 ReactionResponse
            } catch (JsonProcessingException e) {
                log.error("Failed to parse reactions", e);
            }
        }

        return response;
    }

    private MessageResponse.AttachmentResponse mapToAttachmentResponse(Map<String, Object> map) {
        MessageResponse.AttachmentResponse response = new MessageResponse.AttachmentResponse();
        response.setId((String) map.get("id"));
        response.setType((String) map.get("type"));
        response.setName((String) map.get("name"));
        response.setUrl((String) map.get("url"));
        response.setThumbnailUrl((String) map.get("thumbnailUrl"));
        response.setSize(getLongValue(map.get("size")));
        response.setMimeType((String) map.get("mimeType"));
        response.setWidth(getIntValue(map.get("width")));
        response.setHeight(getIntValue(map.get("height")));
        response.setDuration(getIntValue(map.get("duration")));
        response.setExtra((Map<String, Object>) map.get("extra"));
        return response;
    }

    private Double getDoubleValue(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Double) return (Double) obj;
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        try {
            return Double.parseDouble(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer getIntValue(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof Number) return ((Number) obj).intValue();
        try {
            return Integer.parseInt(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long getLongValue(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Long) return (Long) obj;
        if (obj instanceof Number) return ((Number) obj).longValue();
        try {
            return Long.parseLong(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
