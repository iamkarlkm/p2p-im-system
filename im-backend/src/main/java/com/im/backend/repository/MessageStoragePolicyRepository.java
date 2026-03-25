package com.im.backend.repository;

import com.im.backend.entity.MessageStoragePolicy;
import com.im.backend.entity.MessageStoragePolicy.PolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 消息存储策略仓储接口
 * 
 * @author im-backend
 * @version 1.0.0
 * @since 2026-03-22
 */
@Repository
public interface MessageStoragePolicyRepository extends JpaRepository<MessageStoragePolicy, Long> {
    
    /**
     * 根据策略名称查找
     */
    Optional<MessageStoragePolicy> findByPolicyName(String policyName);
    
    /**
     * 查找所有启用的策略
     */
    List<MessageStoragePolicy> findByStatusOrderByPriorityAsc(PolicyStatus status);
    
    /**
     * 根据会话类型查找启用的策略
     */
    List<MessageStoragePolicy> findBySessionTypeAndStatusOrderByPriorityAsc(String sessionType, PolicyStatus status);
    
    /**
     * 检查策略名称是否存在
     */
    boolean existsByPolicyName(String policyName);
    
    /**
     * 根据冷存储类型查找策略
     */
    List<MessageStoragePolicy> findByColdStorageTypeAndStatus(MessageStoragePolicy.ColdStorageType type, PolicyStatus status);
    
    /**
     * 查找需要自动归档的策略
     */
    @Query("SELECT p FROM MessageStoragePolicy p WHERE p.enableAutoArchive = true AND p.status = 'ACTIVE'")
    List<MessageStoragePolicy> findActiveAutoArchivePolicies();
    
    /**
     * 查找过期的归档数据 (超过保留天数)
     */
    @Query("SELECT p FROM MessageStoragePolicy p WHERE p.archiveRetentionDays > 0 AND p.createdTime < :cutoffDate")
    List<MessageStoragePolicy> findExpiredPolicies(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * 统计不同状态的策略数量
     */
    @Query("SELECT p.status, COUNT(p) FROM MessageStoragePolicy p GROUP BY p.status")
    List<Object[]> countPoliciesByStatus();
    
    /**
     * 统计不同冷存储类型的策略数量
     */
    @Query("SELECT p.coldStorageType, COUNT(p) FROM MessageStoragePolicy p WHERE p.status = 'ACTIVE' GROUP BY p.coldStorageType")
    List<Object[]> countActivePoliciesByStorageType();
}