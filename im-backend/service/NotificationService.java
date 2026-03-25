package com.im.service;

import com.im.dto.NotificationDTO;
import com.im.entity.NotificationEntity;
import com.im.repository.NotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    // ========== 查询 ==========

    /**
     * 分页获取用户通知列表
     */
    public Page<NotificationDTO> getNotifications(Long userId, String type, Boolean isRead, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationEntity> pageEntity;

        if (type != null && !type.isEmpty()) {
            pageEntity = notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type, pageable);
        } else if (Boolean.TRUE.equals(isRead)) {
            pageEntity = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId, pageable);
        } else {
            pageEntity = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }

        return pageEntity.map(this::toDTO);
    }

    /**
     * 获取未读总数
     */
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * 获取各类型未读统计
     */
    public Map<String, Long> getUnreadCountByType(Long userId) {
        List<Object[]> results = notificationRepository.countUnreadByType(userId);
        Map<String, Long> counts = new LinkedHashMap<>();
        for (Object[] row : results) {
            counts.put((String) row[0], (Long) row[1]);
        }
        return counts;
    }

    // ========== 发送通知 ==========

    /**
     * 发送通知 (通用方法)
     */
    @Transactional
    public NotificationDTO sendNotification(Long userId, String type, String title, String content,
                                           String refType, Long refId, Long conversationId,
                                           Long senderId, String senderNickname, String senderAvatar,
                                           Map<String, Object> extraData, LocalDateTime expiresAt) {
        NotificationEntity entity = new NotificationEntity();
        entity.setUserId(userId);
        entity.setType(type);
        entity.setTitle(title);
        entity.setContent(content);
        entity.setRefType(refType);
        entity.setRefId(refId);
        entity.setConversationId(conversationId);
        entity.setSenderId(senderId);
        entity.setSenderNickname(senderNickname);
        entity.setSenderAvatar(senderAvatar);
        entity.setExpiresAt(expiresAt);

        if (extraData != null && !extraData.isEmpty()) {
            try {
                entity.setExtraData(objectMapper.writeValueAsString(extraData));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize extra data", e);
            }
        }

        entity = notificationRepository.save(entity);
        log.info("Sent notification id={} to user={} type={}", entity.getId(), userId, type);
        return toDTO(entity);
    }

    /**
     * 发送系统通知
     */
    public NotificationDTO sendSystemNotification(Long userId, String title, String content, Map<String, Object> extra) {
        return sendNotification(userId, "SYSTEM", title, content, null, null, null, null, "System", null, extra, null);
    }

    /**
     * 发送好友请求通知
     */
    public NotificationDTO sendFriendRequestNotification(Long targetUserId, Long requesterId, String requesterNickname, String requesterAvatar, Long requestId) {
        Map<String, Object> extra = new HashMap<>();
        extra.put("requestId", requestId);
        return sendNotification(targetUserId, "FRIEND_REQUEST",
            "新的好友请求", requesterNickname + " 请求添加你为好友",
            "FRIEND_REQUEST", requestId, null,
            requesterId, requesterNickname, requesterAvatar,
            extra, null);
    }

    /**
     * 发送群邀请通知
     */
    public NotificationDTO sendGroupInviteNotification(Long targetUserId, Long inviterId, String inviterNickname, Long groupId, String groupName) {
        Map<String, Object> extra = new HashMap<>();
        extra.put("groupId", groupId);
        extra.put("groupName", groupName);
        return sendNotification(targetUserId, "GROUP_INVITE",
            "收到群聊邀请", inviterNickname + " 邀请你加入群聊 " + groupName,
            "GROUP", groupId, null,
            inviterId, inviterNickname, null,
            extra, null);
    }

    /**
     * 发送投票提醒通知
     */
    public NotificationDTO sendVoteNotification(Long userId, Long voteId, String voteTitle, Long groupId, String groupName) {
        Map<String, Object> extra = new HashMap<>();
        extra.put("voteId", voteId);
        extra.put("groupName", groupName);
        return sendNotification(userId, "VOTE",
            "群投票提醒", "群 " + groupName + " 发起新投票: " + voteTitle,
            "VOTE", voteId, groupId,
            null, "System", null,
            extra, null);
    }

    // ========== 操作 ==========

    /**
     * 标记单条已读
     */
    @Transactional
    public boolean markAsRead(Long notificationId, Long userId) {
        return notificationRepository.markAsRead(notificationId, userId, LocalDateTime.now()) > 0;
    }

    /**
     * 批量标记已读
     */
    @Transactional
    public int batchMarkAsRead(List<Long> ids, Long userId) {
        return notificationRepository.batchMarkAsRead(ids, userId, LocalDateTime.now());
    }

    /**
     * 全部标记已读
     */
    @Transactional
    public int markAllAsRead(Long userId) {
        return notificationRepository.markAllAsRead(userId, LocalDateTime.now());
    }

    /**
     * 处理通知 (如: 接受/拒绝好友请求)
     */
    @Transactional
    public boolean handleNotification(Long notificationId, Long userId, String result) {
        Optional<NotificationEntity> opt = notificationRepository.findById(notificationId);
        if (opt.isEmpty() || !opt.get().getUserId().equals(userId)) {
            return false;
        }
        NotificationEntity entity = opt.get();
        entity.setIsHandled(true);
        entity.setHandleResult(result);
        notificationRepository.save(entity);
        return true;
    }

    /**
     * 删除过期通知 (每天凌晨执行)
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupExpiredNotifications() {
        int deleted = notificationRepository.deleteExpired(LocalDateTime.now());
        log.info("Cleaned up {} expired notifications", deleted);
    }

    // ========== 工具方法 ==========

    private NotificationDTO toDTO(NotificationEntity entity) {
        Map<String, Object> extra = null;
        if (entity.getExtraData() != null && !entity.getExtraData().isEmpty()) {
            try {
                extra = objectMapper.readValue(entity.getExtraData(), Map.class);
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse extraData for notification {}", entity.getId());
            }
        }
        return NotificationDTO.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .type(entity.getType())
            .title(entity.getTitle())
            .content(entity.getContent())
            .refType(entity.getRefType())
            .refId(entity.getRefId())
            .conversationId(entity.getConversationId())
            .senderId(entity.getSenderId())
            .senderNickname(entity.getSenderNickname())
            .senderAvatar(entity.getSenderAvatar())
            .isRead(entity.getIsRead())
            .readAt(entity.getReadAt())
            .isHandled(entity.getIsHandled())
            .handleResult(entity.getHandleResult())
            .dndLevel(entity.getDndLevel())
            .expiresAt(entity.getExpiresAt())
            .extraData(entity.getExtraData())
            .createdAt(entity.getCreatedAt())
            .build();
    }
}
