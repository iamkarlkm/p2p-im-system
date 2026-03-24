package com.im.backend.service;

import com.im.backend.entity.PushNotificationEntity;
import com.im.backend.repository.PushNotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 推送通知服务 - 离线消息推送
 * 支持 APNs/FCM/HMS、消息同步、推送静默、重试队列
 */
@Service
public class PushNotificationService {

    private final PushNotificationRepository pushRepository;

    public PushNotificationService(PushNotificationRepository pushRepository) {
        this.pushRepository = pushRepository;
    }

    @Transactional
    public PushNotificationEntity sendPush(String userId, String title, String body,
                                          String notificationType, String senderId,
                                          String senderName, String conversationId,
                                          String messageId, String priority) {
        PushNotificationEntity notification = new PushNotificationEntity();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setNotificationType(notificationType != null ? notificationType : "MESSAGE");
        notification.setSenderId(senderId);
        notification.setSenderName(senderName);
        notification.setConversationId(conversationId);
        notification.setMessageId(messageId);
        notification.setStatus("PENDING");
        notification.setPriority(priority != null ? priority : "NORMAL");
        notification.setIsSilent(false);
        notification.setRetryCount(0);
        notification.setMaxRetries(5);
        notification.setSource("SERVER");
        notification.setCreatedAt(LocalDateTime.now());

        return pushRepository.save(notification);
    }

    @Transactional
    public PushNotificationEntity sendSilentPush(String userId, String deviceId, String deviceToken,
                                                String pushType, String silentType,
                                                Map<String, String> data, String priority) {
        PushNotificationEntity notification = new PushNotificationEntity();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setUserId(userId);
        notification.setDeviceId(deviceId);
        notification.setDeviceToken(deviceToken);
        notification.setPushType(pushType);
        notification.setTitle("");
        notification.setBody("");
        notification.setNotificationType("SYSTEM");
        notification.setStatus("PENDING");
        notification.setPriority(priority != null ? priority : "HIGH");
        notification.setIsSilent(true);
        notification.setSilentType(silentType);
        notification.setCustomData(toJson(data));
        notification.setRetryCount(0);
        notification.setMaxRetries(3);
        notification.setSource("SERVER");
        notification.setCreatedAt(LocalDateTime.now());

        return pushRepository.save(notification);
    }

    @Transactional
    public void sendBatchPush(List<String> userIds, String title, String body,
                               String notificationType, String senderId, String senderName,
                               String conversationId, String messageId) {
        String batchId = UUID.randomUUID().toString();
        for (String userId : userIds) {
            PushNotificationEntity notification = new PushNotificationEntity();
            notification.setNotificationId(UUID.randomUUID().toString());
            notification.setUserId(userId);
            notification.setBatchId(batchId);
            notification.setTitle(title);
            notification.setBody(body);
            notification.setNotificationType(notificationType);
            notification.setSenderId(senderId);
            notification.setSenderName(senderName);
            notification.setConversationId(conversationId);
            notification.setMessageId(messageId);
            notification.setStatus("PENDING");
            notification.setPriority("NORMAL");
            notification.setIsSilent(false);
            notification.setRetryCount(0);
            notification.setMaxRetries(5);
            notification.setSource("SERVER");
            notification.setCreatedAt(LocalDateTime.now());
            pushRepository.save(notification);
        }
    }

    public List<PushNotificationEntity> getPendingNotifications(int limit) {
        return pushRepository.findPendingNotifications(LocalDateTime.now(), org.springframework.data.domain.PageRequest.of(0, limit));
    }

    public List<PushNotificationEntity> getRetryableNotifications(int limit) {
        return pushRepository.findRetryableNotifications(LocalDateTime.now(), org.springframework.data.domain.PageRequest.of(0, limit));
    }

    @Transactional
    public void markSent(String notificationId, String apnsId, String fcmMessageId) {
        pushRepository.markSent(notificationId, LocalDateTime.now(), apnsId, fcmMessageId);
    }

    @Transactional
    public void markDelivered(String notificationId) {
        pushRepository.markDelivered(notificationId, LocalDateTime.now());
    }

    @Transactional
    public void markFailed(String notificationId, String reason) {
        pushRepository.markFailed(notificationId, reason, LocalDateTime.now());
    }

    @Transactional
    public void silenceUser(String userId) {
        pushRepository.silencePending(userId, LocalDateTime.now());
    }

    @Transactional
    public void cleanupExpired() {
        pushRepository.deleteExpiredNotifications(LocalDateTime.now());
    }

    public Optional<PushNotificationEntity> getNotification(String notificationId) {
        return pushRepository.findByNotificationId(notificationId);
    }

    public List<PushNotificationEntity> getUserNotifications(String userId, int page, int size) {
        return pushRepository.findByUserIdOrderByCreatedAtDesc(userId, org.springframework.data.domain.PageRequest.of(page, size));
    }

    public Map<String, Long> getUserNotificationStats(String userId) {
        List<Object[]> stats = pushRepository.countByStatusGroup(userId);
        Map<String, Long> result = new HashMap<>();
        for (Object[] row : stats) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    public List<Object[]> getDailyStats(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return pushRepository.countByDay(since);
    }

    public boolean hasRecentlyNotified(String messageId, String userId) {
        return pushRepository.existsByMessageIdAndUserId(messageId, userId);
    }

    @Transactional
    public void retryFailed(String notificationId) {
        pushRepository.findByNotificationId(notificationId).ifPresent(notification -> {
            if (notification.getRetryCount() < notification.getMaxRetries()) {
                notification.setStatus("PENDING");
                notification.setRetryCount(notification.getRetryCount() + 1);
                notification.setUpdatedAt(LocalDateTime.now());
                pushRepository.save(notification);
            }
        });
    }

    @Transactional
    public void cancelNotification(String notificationId) {
        pushRepository.findByNotificationId(notificationId).ifPresent(notification -> {
            notification.setStatus("FAILED");
            notification.setFailureReason("CANCELLED");
            notification.setUpdatedAt(LocalDateTime.now());
            pushRepository.save(notification);
        });
    }

    public String toJson(Map<String, String> data) {
        if (data == null) return "{}";
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
