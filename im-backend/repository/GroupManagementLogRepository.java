package com.im.backend.repository;

import com.im.backend.entity.GroupManagementLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 群管理日志仓储接口
 */
@Repository
public interface GroupManagementLogRepository extends JpaRepository<GroupManagementLogEntity, UUID>, JpaSpecificationExecutor<GroupManagementLogEntity> {

    /**
     * 根据群组ID查询日志
     */
    List<GroupManagementLogEntity> findByGroupIdOrderByCreatedAtDesc(UUID groupId);

    /**
     * 根据群组ID分页查询日志
     */
    Page<GroupManagementLogEntity> findByGroupId(UUID groupId, Pageable pageable);

    /**
     * 根据操作者ID查询日志
     */
    List<GroupManagementLogEntity> findByOperatorIdOrderByCreatedAtDesc(UUID operatorId);

    /**
     * 根据目标用户ID查询日志
     */
    List<GroupManagementLogEntity> findByTargetUserIdOrderByCreatedAtDesc(UUID targetUserId);

    /**
     * 根据操作类型查询日志
     */
    List<GroupManagementLogEntity> findByActionTypeOrderByCreatedAtDesc(String actionType);

    /**
     * 根据操作结果查询日志
     */
    List<GroupManagementLogEntity> findByResultOrderByCreatedAtDesc(String result);

    /**
     * 查询群组中特定操作类型的日志
     */
    List<GroupManagementLogEntity> findByGroupIdAndActionTypeOrderByCreatedAtDesc(UUID groupId, String actionType);

    /**
     * 查询操作者对目标用户的操作日志
     */
    List<GroupManagementLogEntity> findByOperatorIdAndTargetUserIdOrderByCreatedAtDesc(UUID operatorId, UUID targetUserId);

    /**
     * 查询重要操作日志
     */
    List<GroupManagementLogEntity> findByImportantTrueOrderByCreatedAtDesc();

    /**
     * 查询需要通知但未通知的日志
     */
    List<GroupManagementLogEntity> findByNeedNotificationTrueAndNotifiedFalse();

    /**
     * 根据时间范围查询日志
     */
    List<GroupManagementLogEntity> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);

    /**
     * 查询群组在时间范围内的日志
     */
    List<GroupManagementLogEntity> findByGroupIdAndCreatedAtBetweenOrderByCreatedAtDesc(UUID groupId, LocalDateTime start, LocalDateTime end);

    /**
     * 统计群组操作次数
     */
    @Query("SELECT COUNT(l) FROM GroupManagementLogEntity l WHERE l.groupId = :groupId")
    Long countByGroupId(@Param("groupId") UUID groupId);

    /**
     * 统计操作者操作次数
     */
    @Query("SELECT COUNT(l) FROM GroupManagementLogEntity l WHERE l.operatorId = :operatorId")
    Long countByOperatorId(@Param("operatorId") UUID operatorId);

    /**
     * 统计目标用户被操作次数
     */
    @Query("SELECT COUNT(l) FROM GroupManagementLogEntity l WHERE l.targetUserId = :targetUserId")
    Long countByTargetUserId(@Param("targetUserId") UUID targetUserId);

    /**
     * 统计操作类型次数
     */
    @Query("SELECT l.actionType, COUNT(l) FROM GroupManagementLogEntity l GROUP BY l.actionType")
    List<Object[]> countByActionType();

    /**
     * 统计群组操作类型分布
     */
    @Query("SELECT l.actionType, COUNT(l) FROM GroupManagementLogEntity l WHERE l.groupId = :groupId GROUP BY l.actionType")
    List<Object[]> countByGroupIdAndActionType(@Param("groupId") UUID groupId);

    /**
     * 获取群组最近的操作日志
     */
    @Query("SELECT l FROM GroupManagementLogEntity l WHERE l.groupId = :groupId ORDER BY l.createdAt DESC LIMIT :limit")
    List<GroupManagementLogEntity> findRecentByGroupId(@Param("groupId") UUID groupId, @Param("limit") int limit);

    /**
     * 获取操作者最近的操作日志
     */
    @Query("SELECT l FROM GroupManagementLogEntity l WHERE l.operatorId = :operatorId ORDER BY l.createdAt DESC LIMIT :limit")
    List<GroupManagementLogEntity> findRecentByOperatorId(@Param("operatorId") UUID operatorId, @Param("limit") int limit);

    /**
     * 批量标记为已通知
     */
    @Modifying
    @Query("UPDATE GroupManagementLogEntity l SET l.notified = true, l.updatedAt = CURRENT_TIMESTAMP WHERE l.id IN :ids")
    int markAsNotified(@Param("ids") List<UUID> ids);

    /**
     * 批量归档日志
     */
    @Modifying
    @Query("UPDATE GroupManagementLogEntity l SET l.archived = true, l.archivedAt = CURRENT_TIMESTAMP, l.updatedAt = CURRENT_TIMESTAMP WHERE l.id IN :ids")
    int archiveLogs(@Param("ids") List<UUID> ids);

    /**
     * 删除已归档的旧日志
     */
    @Modifying
    @Query("DELETE FROM GroupManagementLogEntity l WHERE l.archived = true AND l.archivedAt < :cutoffDate")
    int deleteArchivedBefore(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * 根据多个条件搜索日志
     */
    @Query("SELECT l FROM GroupManagementLogEntity l WHERE " +
           "(:groupId IS NULL OR l.groupId = :groupId) AND " +
           "(:operatorId IS NULL OR l.operatorId = :operatorId) AND " +
           "(:targetUserId IS NULL OR l.targetUserId = :targetUserId) AND " +
           "(:actionType IS NULL OR l.actionType = :actionType) AND " +
           "(:result IS NULL OR l.result = :result) AND " +
           "(:important IS NULL OR l.important = :important) AND " +
           "(:startDate IS NULL OR l.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR l.createdAt <= :endDate) " +
           "ORDER BY l.createdAt DESC")
    List<GroupManagementLogEntity> searchLogs(
        @Param("groupId") UUID groupId,
        @Param("operatorId") UUID operatorId,
        @Param("targetUserId") UUID targetUserId,
        @Param("actionType") String actionType,
        @Param("result") String result,
        @Param("important") Boolean important,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * 获取操作统计信息
     */
    @Query("SELECT " +
           "COUNT(l) as total, " +
           "SUM(CASE WHEN l.result = 'SUCCESS' THEN 1 ELSE 0 END) as success, " +
           "SUM(CASE WHEN l.result = 'FAILED' THEN 1 ELSE 0 END) as failed, " +
           "SUM(CASE WHEN l.result = 'PARTIAL' THEN 1 ELSE 0 END) as partial, " +
           "SUM(CASE WHEN l.important = true THEN 1 ELSE 0 END) as important, " +
           "SUM(CASE WHEN l.needNotification = true AND l.notified = false THEN 1 ELSE 0 END) as pendingNotification " +
           "FROM GroupManagementLogEntity l")
    Object[] getStatistics();

    /**
     * 获取群组操作统计信息
     */
    @Query("SELECT " +
           "COUNT(l) as total, " +
           "SUM(CASE WHEN l.result = 'SUCCESS' THEN 1 ELSE 0 END) as success, " +
           "SUM(CASE WHEN l.result = 'FAILED' THEN 1 ELSE 0 END) as failed, " +
           "SUM(CASE WHEN l.important = true THEN 1 ELSE 0 END) as important " +
           "FROM GroupManagementLogEntity l WHERE l.groupId = :groupId")
    Object[] getStatisticsByGroupId(@Param("groupId") UUID groupId);

    /**
     * 获取热门操作类型
     */
    @Query("SELECT l.actionType, COUNT(l) as count FROM GroupManagementLogEntity l GROUP BY l.actionType ORDER BY count DESC LIMIT 10")
    List<Object[]> getTopActionTypes();

    /**
     * 获取活跃操作者
     */
    @Query("SELECT l.operatorId, COUNT(l) as count FROM GroupManagementLogEntity l GROUP BY l.operatorId ORDER BY count DESC LIMIT 10")
    List<Object[]> getTopOperators();

    /**
     * 获取频繁被操作的目标用户
     */
    @Query("SELECT l.targetUserId, COUNT(l) as count FROM GroupManagementLogEntity l WHERE l.targetUserId IS NOT NULL GROUP BY l.targetUserId ORDER BY count DESC LIMIT 10")
    List<Object[]> getTopTargetUsers();

    /**
     * 检查是否存在特定操作日志
     */
    boolean existsByGroupIdAndOperatorIdAndActionTypeAndTargetUserIdAndCreatedAtAfter(
        UUID groupId, UUID operatorId, String actionType, UUID targetUserId, LocalDateTime after
    );

    /**
     * 批量删除日志
     */
    @Modifying
    @Query("DELETE FROM GroupManagementLogEntity l WHERE l.id IN :ids")
    int deleteByIds(@Param("ids") List<UUID> ids);
}