package com.im.security.repository;

import com.im.security.entity.IdentityFingerprintEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 身份指纹验证数据访问层
 */
@Repository
public interface IdentityFingerprintRepository extends JpaRepository<IdentityFingerprintEntity, Long> {

    // 按用户ID和状态查询
    List<IdentityFingerprintEntity> findByUserIdAndStatus(Long userId, IdentityFingerprintEntity.FingerprintStatus status);
    
    // 按用户ID和类型查询
    List<IdentityFingerprintEntity> findByUserIdAndFingerprintType(Long userId, String fingerprintType);
    
    // 按用户ID、类型和状态查询
    List<IdentityFingerprintEntity> findByUserIdAndFingerprintTypeAndStatus(
        Long userId, String fingerprintType, IdentityFingerprintEntity.FingerprintStatus status);
    
    // 查找待验证的安全码
    @Query("SELECT f FROM IdentityFingerprintEntity f WHERE f.userId = :userId AND f.fingerprintType = 'SAFETY_CODE' " +
           "AND f.status = 'PENDING' AND (f.expiresAt IS NULL OR f.expiresAt > :now) " +
           "ORDER BY f.createdAt DESC")
    List<IdentityFingerprintEntity> findPendingSafetyCodes(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    // 查找过期的指纹
    @Query("SELECT f FROM IdentityFingerprintEntity f WHERE f.status = 'PENDING' AND f.expiresAt IS NOT NULL AND f.expiresAt <= :now")
    List<IdentityFingerprintEntity> findExpiredFingerprints(@Param("now") LocalDateTime now);
    
    // 按验证码查询
    Optional<IdentityFingerprintEntity> findByVerificationCodeAndUserId(String verificationCode, Long userId);
    
    // 按二维码数据查询
    Optional<IdentityFingerprintEntity> findByFingerprintValueAndUserId(String fingerprintValue, Long userId);
    
    // 统计用户的验证尝试
    @Query("SELECT COUNT(f) FROM IdentityFingerprintEntity f WHERE f.userId = :userId AND f.status = 'FAILED' " +
           "AND f.createdAt >= :startTime")
    Long countFailedAttemptsSince(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime);
    
    // 批量标记为过期
    @Modifying
    @Query("UPDATE IdentityFingerprintEntity f SET f.status = 'EXPIRED', f.updatedAt = :now " +
           "WHERE f.id IN :ids")
    int markAsExpired(@Param("ids") List<Long> ids, @Param("now") LocalDateTime now);
    
    // 查找需要发送通知的指纹
    @Query("SELECT f FROM IdentityFingerprintEntity f WHERE f.notificationSent = false " +
           "AND f.status = 'PENDING' AND f.fingerprintType IN ('KEY_CHANGE', 'DEVICE_FINGERPRINT')")
    List<IdentityFingerprintEntity> findPendingNotifications();
    
    // 按设备ID查询
    List<IdentityFingerprintEntity> findByDeviceId(String deviceId);
    
    // 按设备ID和用户ID查询
    List<IdentityFingerprintEntity> findByDeviceIdAndUserId(String deviceId, Long userId);
    
    // 查找最近的有效指纹
    @Query("SELECT f FROM IdentityFingerprintEntity f WHERE f.userId = :userId " +
           "AND f.status = 'VERIFIED' AND f.fingerprintType = :type " +
           "ORDER BY f.verifiedAt DESC LIMIT 1")
    Optional<IdentityFingerprintEntity> findLatestVerified(@Param("userId") Long userId, @Param("type") String type);
    
    // 查找所有锁定的指纹
    @Query("SELECT f FROM IdentityFingerprintEntity f WHERE f.status = 'LOCKED' " +
           "AND f.updatedAt <= :lockExpiryTime")
    List<IdentityFingerprintEntity> findLockedFingerprintsOlderThan(@Param("lockExpiryTime") LocalDateTime lockExpiryTime);
    
    // 统计各种类型的指纹数量
    @Query("SELECT f.fingerprintType, COUNT(f) FROM IdentityFingerprintEntity f " +
           "WHERE f.userId = :userId GROUP BY f.fingerprintType")
    List<Object[]> countByTypeForUser(@Param("userId") Long userId);
    
    // 查找用户的最后验证时间
    @Query("SELECT MAX(f.verifiedAt) FROM IdentityFingerprintEntity f WHERE f.userId = :userId AND f.status = 'VERIFIED'")
    Optional<LocalDateTime> findLastVerificationTime(@Param("userId") Long userId);
    
    // 按IP地址查询最近的验证记录
    @Query("SELECT f FROM IdentityFingerprintEntity f WHERE f.ipAddress = :ipAddress " +
           "ORDER BY f.createdAt DESC LIMIT :limit")
    List<IdentityFingerprintEntity> findRecentByIpAddress(@Param("ipAddress") String ipAddress, @Param("limit") int limit);
    
    // 按用户代理查询
    @Query("SELECT DISTINCT f.userAgent FROM IdentityFingerprintEntity f WHERE f.userId = :userId AND f.userAgent IS NOT NULL")
    List<String> findDistinctUserAgentsForUser(@Param("userId") Long userId);
    
    // 批量更新通知发送状态
    @Modifying
    @Query("UPDATE IdentityFingerprintEntity f SET f.notificationSent = true, f.updatedAt = :now " +
           "WHERE f.id IN :ids AND f.notificationSent = false")
    int markNotificationsAsSent(@Param("ids") List<Long> ids, @Param("now") LocalDateTime now);
    
    // 查找需要清理的旧记录（超过90天）
    @Query("SELECT f FROM IdentityFingerprintEntity f WHERE f.createdAt <= :cutoffTime " +
           "AND f.status IN ('EXPIRED', 'FAILED', 'REVOKED')")
    List<IdentityFingerprintEntity> findOldRecordsForCleanup(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    // 查找安全相关的验证（密钥变更、设备指纹）
    @Query("SELECT f FROM IdentityFingerprintEntity f WHERE f.userId = :userId " +
           "AND f.fingerprintType IN ('KEY_CHANGE', 'DEVICE_FINGERPRINT') " +
           "ORDER BY f.createdAt DESC")
    List<IdentityFingerprintEntity> findSecurityFingerprints(@Param("userId") Long userId);
}