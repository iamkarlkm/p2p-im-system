package com.im.backend.repository;

import com.im.backend.entity.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, Long> {

    Optional<DeviceEntity> findByDeviceId(String deviceId);

    Optional<DeviceEntity> findByDeviceToken(String deviceToken);

    List<DeviceEntity> findByUserIdAndIsHiddenFalseOrderByLastActiveAtDesc(String userId);

    List<DeviceEntity> findByUserIdOrderByLastActiveAtDesc(String userId);

    List<DeviceEntity> findByUserIdAndStatusOrderByLastActiveAtDesc(String userId, String status);

    @Query("SELECT d FROM DeviceEntity d WHERE d.userId = :userId AND d.status = 'ONLINE' ORDER BY d.lastActiveAt DESC")
    List<DeviceEntity> findOnlineDevices(@Param("userId") String userId);

    long countByUserId(String userId);

    long countByUserIdAndStatus(String userId, String status);

    boolean existsByUserIdAndDeviceToken(String userId, String deviceToken);

    boolean existsByDeviceToken(String deviceToken);

    @Query("SELECT d.status, COUNT(d) FROM DeviceEntity d WHERE d.userId = :userId GROUP BY d.status")
    List<Object[]> countByStatusGroup(@Param("userId") String userId);

    @Modifying
    @Query("UPDATE DeviceEntity d SET d.status = 'OFFLINE', d.sessionId = NULL WHERE d.userId = :userId AND d.deviceId != :currentDeviceId")
    int logoutOtherDevices(@Param("userId") String userId, @Param("currentDeviceId") String currentDeviceId);

    @Modifying
    @Query("UPDATE DeviceEntity d SET d.status = 'OFFLINE', d.sessionId = NULL WHERE d.deviceId = :deviceId")
    int logoutDevice(@Param("deviceId") String deviceId);

    @Modifying
    @Query("UPDATE DeviceEntity d SET d.status = 'ONLINE', d.lastActiveAt = :now, d.lastOnlineAt = :now, d.sessionId = :sessionId WHERE d.deviceId = :deviceId")
    int markOnline(@Param("deviceId") String deviceId, @Param("now") LocalDateTime now, @Param("sessionId") String sessionId);

    @Modifying
    @Query("UPDATE DeviceEntity d SET d.status = 'OFFLINE', d.lastOnlineAt = :now, d.sessionId = NULL WHERE d.deviceId = :deviceId")
    int markOffline(@Param("deviceId") String deviceId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE DeviceEntity d SET d.lastActiveAt = :now, d.status = 'ONLINE' WHERE d.deviceId = :deviceId")
    int updateLastActive(@Param("deviceId") String deviceId, @Param("now") LocalDateTime now);

    @Query("SELECT d FROM DeviceEntity d WHERE d.pushEnabled = true AND d.pushToken IS NOT NULL AND d.userId = :userId")
    List<DeviceEntity> findDevicesWithPush(@Param("userId") String userId);

    @Modifying
    @Query("UPDATE DeviceEntity d SET d.pushToken = :pushToken, d.pushType = :pushType WHERE d.deviceId = :deviceId")
    int updatePushToken(@Param("deviceId") String deviceId, @Param("pushToken") String pushToken, @Param("pushType") String pushType);

    List<DeviceEntity> findByUserIdAndDeviceTypeOrderByLastActiveAtDesc(String userId, String deviceType);

    @Query("SELECT d FROM DeviceEntity d WHERE d.userId = :userId AND d.createdAt < :before ORDER BY d.lastActiveAt ASC")
    List<DeviceEntity> findInactiveDevices(@Param("userId") String userId, @Param("before") LocalDateTime before);

    @Modifying
    @Query("DELETE FROM DeviceEntity d WHERE d.deviceId = :deviceId")
    int deleteByDeviceId(@Param("deviceId") String deviceId);
}
