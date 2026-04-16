package com.im.service.push.repository;

import com.im.service.push.entity.PushNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PushNotificationRepository extends JpaRepository<PushNotification, Long> {

    Optional<PushNotification> findByNotificationId(String notificationId);

    List<PushNotification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    Page<PushNotification> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, String status, Pageable pageable);

    List<PushNotification> findByUserIdAndNotificationType(String userId, String notificationType);

    @Query("SELECT p FROM PushNotification p WHERE p.status = 'PENDING' AND (p.scheduledAt IS NULL OR p.scheduledAt <= :now) ORDER BY CASE p.priority WHEN 'HIGH' THEN 0 WHEN 'NORMAL' THEN 1 ELSE 2 END, p.createdAt ASC")
    List<PushNotification> findPendingNotifications(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT p FROM PushNotification p WHERE p.status = 'FAILED' AND p.retryCount < p.maxRetries AND (p.expiresAt IS NULL OR p.expiresAt > :now) ORDER BY p.createdAt ASC")
    List<PushNotification> findRetryableNotifications(@Param("now") LocalDateTime now, Pageable pageable);

    long countByUserIdAndStatus(String userId, String status);

    long countByStatus(String status);

    long countByNotificationType(String notificationType);

    @Query("SELECT p.status, COUNT(p) FROM PushNotification p WHERE p.userId = :userId GROUP BY p.status")
    List<Object[]> countByStatusGroup(@Param("userId") String userId);

    @Query("SELECT DATE(p.createdAt), COUNT(p) FROM PushNotification p WHERE p.createdAt >= :since GROUP BY DATE(p.createdAt) ORDER BY DATE(p.createdAt)")
    List<Object[]> countByDay(@Param("since") LocalDateTime since);

    @Modifying
    @Query("UPDATE PushNotification p SET p.status = :status, p.updatedAt = :now WHERE p.notificationId = :id")
    int updateStatus(@Param("id") String notificationId, @Param("status") String status, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE PushNotification p SET p.status = 'SENT', p.sentAt = :sentAt, p.apnsId = :apnsId, p.fcmMessageId = :fcmId, p.updatedAt = :sentAt WHERE p.notificationId = :id")
    int markSent(@Param("id") String notificationId, @Param("sentAt") LocalDateTime sentAt, @Param("apnsId") String apnsId, @Param("fcmId") String fcmId);

    @Modifying
    @Query("UPDATE PushNotification p SET p.status = 'DELIVERED', p.deliveredAt = :deliveredAt, p.updatedAt = :deliveredAt WHERE p.notificationId = :id")
    int markDelivered(@Param("id") String notificationId, @Param("deliveredAt") LocalDateTime deliveredAt);

    @Modifying
    @Query("UPDATE PushNotification p SET p.status = 'FAILED', p.failureReason = :reason, p.retryCount = p.retryCount + 1, p.updatedAt = :now WHERE p.notificationId = :id")
    int markFailed(@Param("id") String notificationId, @Param("reason") String reason, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE PushNotification p SET p.status = 'SILENCED', p.updatedAt = :now WHERE p.userId = :userId AND p.silentType != 'NONE'")
    int silencePending(@Param("userId") String userId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM PushNotification p WHERE p.expiresAt < :now AND p.status IN ('PENDING', 'FAILED')")
    int deleteExpiredNotifications(@Param("now") LocalDateTime now);

    List<PushNotification> findByDeviceIdAndStatus(String deviceId, String status);

    @Query("SELECT p FROM PushNotification p WHERE p.userId = :userId AND p.messageId = :messageId")
    List<PushNotification> findByUserAndMessage(@Param("userId") String userId, @Param("messageId") String messageId);

    @Query("SELECT p FROM PushNotification p WHERE p.batchId = :batchId ORDER BY p.createdAt")
    List<PushNotification> findByBatchId(@Param("batchId") String batchId);

    boolean existsByMessageIdAndUserId(String messageId, String userId);
}
