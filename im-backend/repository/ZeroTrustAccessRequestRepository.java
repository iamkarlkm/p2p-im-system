package com.im.backend.repository;

import com.im.backend.entity.ZeroTrustAccessRequestEntity;
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
 * 零信任访问请求数据访问层
 */
@Repository
public interface ZeroTrustAccessRequestRepository extends JpaRepository<ZeroTrustAccessRequestEntity, String> {

    /**
     * 根据用户ID查询
     */
    List<ZeroTrustAccessRequestEntity> findByUserIdOrderByRequestTimeDesc(Long userId);

    /**
     * 分页查询用户的访问请求
     */
    Page<ZeroTrustAccessRequestEntity> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据设备ID查询
     */
    List<ZeroTrustAccessRequestEntity> findByDeviceIdOrderByRequestTimeDesc(String deviceId);

    /**
     * 根据状态查询
     */
    List<ZeroTrustAccessRequestEntity> findByStatus(ZeroTrustAccessRequestEntity.AccessStatus status);

    /**
     * 根据资源ID查询
     */
    List<ZeroTrustAccessRequestEntity> findByResourceId(String resourceId);

    /**
     * 查询会话的所有请求
     */
    List<ZeroTrustAccessRequestEntity> findBySessionIdOrderByRequestTimeDesc(String sessionId);

    /**
     * 统计指定时间段内的请求数量
     */
    @Query("SELECT COUNT(r) FROM ZeroTrustAccessRequestEntity r WHERE r.requestTime BETWEEN :start AND :end")
    long countByTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 统计指定时间段内各状态的请求数量
     */
    @Query("SELECT r.status, COUNT(r) FROM ZeroTrustAccessRequestEntity r WHERE r.requestTime BETWEEN :start AND :end GROUP BY r.status")
    List<Object[]> countByStatusAndTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 查询高风险请求
     */
    @Query("SELECT r FROM ZeroTrustAccessRequestEntity r WHERE r.riskScore >= :minScore ORDER BY r.requestTime DESC")
    List<ZeroTrustAccessRequestEntity> findHighRiskRequests(@Param("minScore") int minScore);

    /**
     * 查询被拒绝的请求
     */
    @Query("SELECT r FROM ZeroTrustAccessRequestEntity r WHERE r.status = 'DENIED' AND r.requestTime > :since ORDER BY r.requestTime DESC")
    List<ZeroTrustAccessRequestEntity> findRecentDeniedRequests(@Param("since") LocalDateTime since);

    /**
     * 统计用户的访问模式
     */
    @Query("SELECT r.resourceType, COUNT(r) FROM ZeroTrustAccessRequestEntity r WHERE r.userId = :userId AND r.requestTime > :since GROUP BY r.resourceType")
    List<Object[]> countAccessByResourceType(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    /**
     * 查询最近的访问请求（用于实时监控）
     */
    @Query("SELECT r FROM ZeroTrustAccessRequestEntity r WHERE r.requestTime > :since ORDER BY r.requestTime DESC")
    List<ZeroTrustAccessRequestEntity> findRecentRequests(@Param("since") LocalDateTime since);

    /**
     * 按风险等级统计
     */
    @Query("SELECT r.riskLevel, COUNT(r) FROM ZeroTrustAccessRequestEntity r WHERE r.requestTime BETWEEN :start AND :end GROUP BY r.riskLevel")
    List<Object[]> countByRiskLevel(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
