package com.im.service.push.service;

import com.im.service.push.entity.PushNotification;
import com.im.service.push.repository.PushNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * PushNotificationService 单元测试
 *
 * @author IM Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("推送通知服务单元测试")
class PushNotificationServiceTest {

    @Mock
    private PushNotificationRepository pushRepository;

    @InjectMocks
    private PushNotificationService pushService;

    private static final String TEST_USER_ID = "user_001";
    private static final String TEST_NOTIFICATION_ID = "notif_001";

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("发送普通推送通知 - 成功")
    void sendPush_Success() {
        // Given
        String title = "Test Title";
        String body = "Test Body";
        String notificationType = "MESSAGE";
        String senderId = "user_002";
        String senderName = "Sender";
        String conversationId = "conv_001";
        String messageId = "msg_001";
        String priority = "HIGH";

        PushNotification savedNotification = new PushNotification();
        savedNotification.setNotificationId(TEST_NOTIFICATION_ID);
        savedNotification.setUserId(TEST_USER_ID);
        savedNotification.setTitle(title);
        savedNotification.setBody(body);
        savedNotification.setStatus("PENDING");

        when(pushRepository.save(any(PushNotification.class))).thenReturn(savedNotification);

        // When
        PushNotification result = pushService.sendPush(TEST_USER_ID, title, body, 
            notificationType, senderId, senderName, conversationId, messageId, priority);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNotificationId()).isEqualTo(TEST_NOTIFICATION_ID);
        assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(result.getTitle()).isEqualTo(title);
        assertThat(result.getBody()).isEqualTo(body);
        assertThat(result.getStatus()).isEqualTo("PENDING");
        verify(pushRepository, times(1)).save(any(PushNotification.class));
    }

    @Test
    @DisplayName("发送静默推送通知 - 成功")
    void sendSilentPush_Success() {
        // Given
        String deviceId = "device_001";
        String deviceToken = "token_001";
        String pushType = "APNS";
        String silentType = "SYNC";
        Map<String, String> data = new HashMap<>();
        data.put("syncType", "MESSAGE");
        String priority = "HIGH";

        PushNotification savedNotification = new PushNotification();
        savedNotification.setNotificationId(TEST_NOTIFICATION_ID);
        savedNotification.setUserId(TEST_USER_ID);
        savedNotification.setIsSilent(true);
        savedNotification.setSilentType(silentType);
        savedNotification.setStatus("PENDING");

        when(pushRepository.save(any(PushNotification.class))).thenReturn(savedNotification);

        // When
        PushNotification result = pushService.sendSilentPush(TEST_USER_ID, deviceId, deviceToken, 
            pushType, silentType, data, priority);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(result.getIsSilent()).isTrue();
        assertThat(result.getSilentType()).isEqualTo(silentType);
        assertThat(result.getStatus()).isEqualTo("PENDING");
        verify(pushRepository, times(1)).save(any(PushNotification.class));
    }

    @Test
    @DisplayName("批量发送推送通知 - 成功")
    void sendBatchPush_Success() {
        // Given
        List<String> userIds = Arrays.asList("user_001", "user_002", "user_003");
        String title = "Batch Title";
        String body = "Batch Body";
        String notificationType = "ANNOUNCEMENT";
        String senderId = "admin";
        String senderName = "Admin";
        String conversationId = "conv_001";
        String messageId = "msg_001";

        when(pushRepository.save(any(PushNotification.class))).thenAnswer(invocation -> {
            PushNotification notif = invocation.getArgument(0);
            notif.setNotificationId(UUID.randomUUID().toString());
            return notif;
        });

        // When
        pushService.sendBatchPush(userIds, title, body, notificationType, 
            senderId, senderName, conversationId, messageId);

        // Then
        verify(pushRepository, times(3)).save(any(PushNotification.class));
    }

    @Test
    @DisplayName("标记推送为已发送 - 成功")
    void markSent_Success() {
        // Given
        String apnsId = "apns_001";
        String fcmMessageId = "fcm_001";

        when(pushRepository.markSent(eq(TEST_NOTIFICATION_ID), any(LocalDateTime.class), eq(apnsId), eq(fcmMessageId)))
            .thenReturn(1);

        // When
        pushService.markSent(TEST_NOTIFICATION_ID, apnsId, fcmMessageId);

        // Then
        verify(pushRepository, times(1)).markSent(eq(TEST_NOTIFICATION_ID), any(LocalDateTime.class), eq(apnsId), eq(fcmMessageId));
    }

    @Test
    @DisplayName("标记推送为已送达 - 成功")
    void markDelivered_Success() {
        // Given
        when(pushRepository.markDelivered(eq(TEST_NOTIFICATION_ID), any(LocalDateTime.class)))
            .thenReturn(1);

        // When
        pushService.markDelivered(TEST_NOTIFICATION_ID);

        // Then
        verify(pushRepository, times(1)).markDelivered(eq(TEST_NOTIFICATION_ID), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("标记推送为失败 - 成功")
    void markFailed_Success() {
        // Given
        String reason = "Device token invalid";

        when(pushRepository.markFailed(eq(TEST_NOTIFICATION_ID), eq(reason), any(LocalDateTime.class)))
            .thenReturn(1);

        // When
        pushService.markFailed(TEST_NOTIFICATION_ID, reason);

        // Then
        verify(pushRepository, times(1)).markFailed(eq(TEST_NOTIFICATION_ID), eq(reason), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("获取待发送推送列表 - 成功")
    void getPendingNotifications_Success() {
        // Given
        int limit = 10;
        PushNotification pendingNotif = new PushNotification();
        pendingNotif.setNotificationId(TEST_NOTIFICATION_ID);
        pendingNotif.setStatus("PENDING");

        when(pushRepository.findPendingNotifications(any(LocalDateTime.class), any(Pageable.class)))
            .thenReturn(Arrays.asList(pendingNotif));

        // When
        List<PushNotification> result = pushService.getPendingNotifications(limit);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("PENDING");
        verify(pushRepository, times(1)).findPendingNotifications(any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    @DisplayName("静默用户的所有待发送推送 - 成功")
    void silenceUser_Success() {
        // Given
        when(pushRepository.silencePending(eq(TEST_USER_ID), any(LocalDateTime.class)))
            .thenReturn(5);

        // When
        pushService.silenceUser(TEST_USER_ID);

        // Then
        verify(pushRepository, times(1)).silencePending(eq(TEST_USER_ID), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("检查消息是否已推送过 - 返回true")
    void hasRecentlyNotified_True() {
        // Given
        String messageId = "msg_001";
        when(pushRepository.existsByMessageIdAndUserId(messageId, TEST_USER_ID)).thenReturn(true);

        // When
        boolean result = pushService.hasRecentlyNotified(messageId, TEST_USER_ID);

        // Then
        assertThat(result).isTrue();
        verify(pushRepository, times(1)).existsByMessageIdAndUserId(messageId, TEST_USER_ID);
    }

    @Test
    @DisplayName("检查消息是否已推送过 - 返回false")
    void hasRecentlyNotified_False() {
        // Given
        String messageId = "msg_002";
        when(pushRepository.existsByMessageIdAndUserId(messageId, TEST_USER_ID)).thenReturn(false);

        // When
        boolean result = pushService.hasRecentlyNotified(messageId, TEST_USER_ID);

        // Then
        assertThat(result).isFalse();
        verify(pushRepository, times(1)).existsByMessageIdAndUserId(messageId, TEST_USER_ID);
    }

    @Test
    @DisplayName("获取用户推送统计 - 成功")
    void getUserNotificationStats_Success() {
        // Given
        List<Object[]> stats = new ArrayList<>();
        stats.add(new Object[]{"PENDING", 5L});
        stats.add(new Object[]{"SENT", 10L});
        stats.add(new Object[]{"DELIVERED", 8L});
        stats.add(new Object[]{"FAILED", 2L});

        when(pushRepository.countByStatusGroup(TEST_USER_ID)).thenReturn(stats);

        // When
        Map<String, Long> result = pushService.getUserNotificationStats(TEST_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(4);
        assertThat(result.get("PENDING")).isEqualTo(5L);
        assertThat(result.get("SENT")).isEqualTo(10L);
        assertThat(result.get("DELIVERED")).isEqualTo(8L);
        assertThat(result.get("FAILED")).isEqualTo(2L);
    }

    @Test
    @DisplayName("取消推送通知 - 成功")
    void cancelNotification_Success() {
        // Given
        PushNotification notification = new PushNotification();
        notification.setNotificationId(TEST_NOTIFICATION_ID);
        notification.setStatus("PENDING");

        when(pushRepository.findByNotificationId(TEST_NOTIFICATION_ID)).thenReturn(Optional.of(notification));
        when(pushRepository.save(any(PushNotification.class))).thenReturn(notification);

        // When
        pushService.cancelNotification(TEST_NOTIFICATION_ID);

        // Then
        assertThat(notification.getStatus()).isEqualTo("FAILED");
        assertThat(notification.getFailureReason()).isEqualTo("CANCELLED");
        verify(pushRepository, times(1)).save(notification);
    }

    @Test
    @DisplayName("取消推送通知 - 不存在")
    void cancelNotification_NotFound() {
        // Given
        when(pushRepository.findByNotificationId(TEST_NOTIFICATION_ID)).thenReturn(Optional.empty());

        // When
        pushService.cancelNotification(TEST_NOTIFICATION_ID);

        // Then
        verify(pushRepository, never()).save(any(PushNotification.class));
    }

    @Test
    @DisplayName("重试失败的推送 - 成功")
    void retryFailed_Success() {
        // Given
        PushNotification notification = new PushNotification();
        notification.setNotificationId(TEST_NOTIFICATION_ID);
        notification.setStatus("FAILED");
        notification.setRetryCount(2);
        notification.setMaxRetries(5);

        when(pushRepository.findByNotificationId(TEST_NOTIFICATION_ID)).thenReturn(Optional.of(notification));
        when(pushRepository.save(any(PushNotification.class))).thenReturn(notification);

        // When
        pushService.retryFailed(TEST_NOTIFICATION_ID);

        // Then
        assertThat(notification.getStatus()).isEqualTo("PENDING");
        assertThat(notification.getRetryCount()).isEqualTo(3);
        verify(pushRepository, times(1)).save(notification);
    }

    @Test
    @DisplayName("重试失败的推送 - 超过最大重试次数")
    void retryFailed_MaxRetriesReached() {
        // Given
        PushNotification notification = new PushNotification();
        notification.setNotificationId(TEST_NOTIFICATION_ID);
        notification.setStatus("FAILED");
        notification.setRetryCount(5);
        notification.setMaxRetries(5);

        when(pushRepository.findByNotificationId(TEST_NOTIFICATION_ID)).thenReturn(Optional.of(notification));
        // Note: save() is not called when retryCount >= maxRetries

        // When
        pushService.retryFailed(TEST_NOTIFICATION_ID);

        // Then - should set to FAILED permanently
        assertThat(notification.getStatus()).isEqualTo("FAILED");
        verify(pushRepository, never()).save(any(PushNotification.class));
    }
}
