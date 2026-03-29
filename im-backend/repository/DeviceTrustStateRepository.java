package com.im.backend.repository;

import com.im.backend.entity.DeviceTrustStateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 设备信任状态数据访问层
 */
@Repository
public interface DeviceTrustStateRepository extends JpaRepository<DeviceTrustStateEntity, String> {

    /**
     * 根据设备ID查询
     */
    Optional<DeviceTrustStateEntity> findByDeviceId(String deviceId);

    /**
     * 根据用户ID查询所有设备
     */
    List<DeviceTrustStateEntity> findByUserId(Long userId);

    /**
     * 分页查询用户的设备
     */
    Page<DeviceTrustStateEntity> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据信任状态查询
     */
    List<DeviceTrustStateEntity> findByTrustStatus(DeviceTrustStateEntity.TrustStatus status);

    /**
     * 查询被隔离的设备
     */
    List<DeviceTrustStateEntity> findByTrustStatusAndQuarantineStartIsNotNull(
        DeviceTrustStateEntity.TrustStatus status);

    /**
     * 查询信任分数低于阈值的设备
     */
    List<DeviceTrustStateEntity> findByTrustScoreLessThan(Integer score);

    /**
     * 查询不合规的设备
     */
    List<DeviceTrustStateEntity> findByIsCompliantFalse();

    /**
     * 查询长时间未活跃的设备
     */
    @Query("SELECT d FROM DeviceTrustStateEntity d WHERE d.lastSeenAt < :threshold")
    List<DeviceTrustStateEntity> findInactiveDevices(@Param("threshold") LocalDateTime threshold);

    /**
     * 统计各信任状态的设备数量
     */
    @Query("SELECT d.trustStatus, COUNT(d) FROM DeviceTrustStateEntity d GROUP BY d.trustStatus")
    List<Object[]> countByTrustStatus();

    /**
     * 统计用户的设备数量
     */
    long countByUserId(Long userId);

    /**
     * 查询已越狱/Root的设备
     */
    List<DeviceTrustStateEntity> findByIsJailbrokenTrue();

    /**
     * 查询存在漏洞的设备
     */
    @Query("SELECT d FROM DeviceTrustStateEntity d WHERE SIZE(d.vulnerabilities) > 0")
    List<DeviceTrustStateEntity> findDevicesWithVulnerabilities();

    /**
     * 查询高信任分数的设备
     */
    List<DeviceTrustStateEntity> findByTrustScoreGreaterThanEqual(Integer score);

    /**
     * 根据设备类型查询
     */
    List<DeviceTrustStateEntity> findByDeviceType(DeviceTrustStateEntity.DeviceType type);
}
