package com.im.backend.repository;

import com.im.backend.entity.ZeroTrustPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 零信任策略数据访问层
 */
@Repository
public interface ZeroTrustPolicyRepository extends JpaRepository<ZeroTrustPolicyEntity, String> {

    /**
     * 查询所有启用的策略，按优先级排序
     */
    List<ZeroTrustPolicyEntity> findByEnabledTrueOrderByPriorityAsc();

    /**
     * 根据资源类型查询策略
     */
    List<ZeroTrustPolicyEntity> findByResourceTypeAndEnabledTrue(
        ZeroTrustPolicyEntity.ResourceType resourceType);

    /**
     * 根据名称查询策略
     */
    Optional<ZeroTrustPolicyEntity> findByName(String name);

    /**
     * 查询特定资源的策略
     */
    @Query("SELECT p FROM ZeroTrustPolicyEntity p WHERE p.resourceType = :type " +
           "AND (p.resourceId IS NULL OR p.resourceId = :resourceId) " +
           "AND p.enabled = true ORDER BY p.priority ASC")
    List<ZeroTrustPolicyEntity> findApplicablePolicies(
        @Param("type") ZeroTrustPolicyEntity.ResourceType type,
        @Param("resourceId") String resourceId);

    /**
     * 统计启用的策略数量
     */
    long countByEnabledTrue();

    /**
     * 查询需要MFA的策略
     */
    List<ZeroTrustPolicyEntity> findByRequireMFATrueAndEnabledTrue();

    /**
     * 查询需要审批的策略
     */
    List<ZeroTrustPolicyEntity> findByRequireApprovalTrueAndEnabledTrue();
}
