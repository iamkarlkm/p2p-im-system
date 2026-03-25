package com.im.backend.repository;

import com.im.backend.entity.Device;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    List<Device> findByUserIdOrderByLastActiveAtDesc(String userId);

    List<Device> findByUserIdAndIsActiveTrueOrderByLastActiveAtDesc(String userId);

    Optional<Device> findByDeviceToken(String deviceToken);

    Optional<Device> findByUserIdAndDeviceToken(String userId, String deviceToken);

    Optional<Device> findByUserIdAndId(String userId, Long deviceId);

    @Query("SELECT d FROM Device d WHERE d.userId = :userId ORDER BY d.lastActiveAt DESC")
    List<Device> findRecentDevices(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT COUNT(d) FROM Device d WHERE d.userId = :userId AND d.isActive = true")
    Integer countActiveByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(d) FROM Device d WHERE d.userId = :userId AND d.isTrusted = true")
    Integer countTrustedByUserId(@Param("userId") String userId);

    @Query("SELECT d FROM Device d WHERE d.userId = :userId AND d.isCurrent = true")
    Optional<Device> findCurrentDevice(@Param("userId") String userId);

    List<Device> findByUserIdAndLastActiveAtBefore(String userId, Instant threshold);

    @Query("SELECT d.deviceType, COUNT(d) FROM Device d WHERE d.userId = :userId GROUP BY d.deviceType ORDER BY COUNT(d) DESC")
    List<Object[]> findMostUsedDeviceType(@Param("userId") String userId);

    void deleteByUserIdAndDeviceToken(String userId, String deviceToken);
}
