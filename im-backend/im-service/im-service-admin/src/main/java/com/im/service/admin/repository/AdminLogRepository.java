package com.im.service.admin.repository;

import com.im.service.admin.entity.AdminLog;
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

/**
 * 管理员操作日志数据访问层
 */
@Repository
public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {

    /**
     * 根据管理员ID查询日志
     */
    List<AdminLog> findByAdminIdOrderByCreatedAtDesc(Long adminId);

    /**
     * 分页查询管理员日志
     */
    Page<AdminLog> findByAdminId(Long adminId, Pageable pageable);

    /**
     * 根据操作类型查询
     */
    List<AdminLog> findByOperationTypeOrderByCreatedAtDesc(String operationType);

    /**
     * 根据模块查询
     */
    List<AdminLog> findByModuleOrderByCreatedAtDesc(String module);

    /**
     * 根据目标ID查询
     */
    List<AdminLog> findByTargetIdOrderByCreatedAtDesc(String targetId);

    /**
     * 根据时间范围查询
     */
    @Query("SELECT a FROM AdminLog a WHERE a.createdAt BETWEEN :startTime AND :endTime ORDER BY a.createdAt DESC")
    List<AdminLog> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 分页查询时间范围日志
     */
    @Query("SELECT a FROM AdminLog a WHERE a.createdAt BETWEEN :startTime AND :endTime ORDER BY a.createdAt DESC")
    Page<AdminLog> findByTimeRangePageable(@Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime,
                                            Pageable pageable);

    /**
     * 根据IP查询
     */
    List<AdminLog> findByIpAddressOrderByCreatedAtDesc(String ipAddress);

    /**
     * 根据结果查询
     */
    List<AdminLog> findByResultOrderByCreatedAtDesc(String result);

    /**
     * 查询管理员的操作次数
     */
    long countByAdminId(Long adminId);

    /**
     * 查询管理员指定操作类型的次数
     */
    long countByAdminIdAndOperationType(Long adminId, String operationType);

    /**
     * 查询失败的操作
     */
    @Query("SELECT a FROM AdminLog a WHERE a.result = 'FAILURE' ORDER BY a.createdAt DESC")
    List<AdminLog> findFailedOperations(Pageable pageable);

    /**
     * 查询最近的操作
     */
    @Query("SELECT a FROM AdminLog a ORDER BY a.createdAt DESC")
    List<AdminLog> findRecentOperations(Pageable pageable);

    /**
     * 查询最近登录
     */
    @Query("SELECT a FROM AdminLog a WHERE a.operationType = 'LOGIN' ORDER BY a.createdAt DESC")
    List<AdminLog> findRecentLogins(Pageable pageable);

    /**
     * 统计操作次数（按模块）
     */
    @Query("SELECT a.module, COUNT(a) FROM AdminLog a WHERE a.createdAt >= :since GROUP BY a.module")
    List<Object[]> countByModule(@Param("since") LocalDateTime since);

    /**
     * 统计操作次数（按操作类型）
     */
    @Query("SELECT a.operationType, COUNT(a) FROM AdminLog a WHERE a.createdAt >= :since GROUP BY a.operationType")
    List<Object[]> countByOperationType(@Param("since") LocalDateTime since);

    /**
     * 统计操作结果
     */
    @Query("SELECT a.result, COUNT(a) FROM AdminLog a WHERE a.createdAt >= :since GROUP BY a.result")
    List<Object[]> countByResult(@Param("since") LocalDateTime since);

    long countByResultEquals(String result);

    /**
     * 统计平均操作耗时
     */
    @Query("SELECT AVG(a.duration) FROM AdminLog a WHERE a.createdAt >= :since AND a.duration IS NOT NULL")
    Double getAverageDuration(@Param("since") LocalDateTime since);

    /**
     * 批量删除指定时间之前的日志
     */
    @Modifying
    @Query("DELETE FROM AdminLog a WHERE a.createdAt < :beforeTime")
    int deleteOldLogs(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 根据管理员ID和目标ID查询
     */
    @Query("SELECT a FROM AdminLog a WHERE a.adminId = :adminId AND a.targetId = :targetId ORDER BY a.createdAt DESC")
    List<AdminLog> findByAdminIdAndTargetId(@Param("adminId") Long adminId, @Param("targetId") String targetId);

    /**
     * 查询特定模块的操作日志
     */
    @Query("SELECT a FROM AdminLog a WHERE a.module = :module AND a.operationType = :operationType ORDER BY a.createdAt DESC")
    List<AdminLog> findByModuleAndOperationType(@Param("module") String module, 
                                                  @Param("operationType") String operationType,
                                                  Pageable pageable);

    /**
     * 查询用户的敏感操作
     */
    @Query("SELECT a FROM AdminLog a WHERE a.targetType = :targetType AND a.targetId = :targetId " +
           "AND a.operationType IN ('DELETE', 'DISABLE', 'RESET') ORDER BY a.createdAt DESC")
    List<AdminLog> findSensitiveOperations(@Param("targetType") String targetType,
                                            @Param("targetId") String targetId,
                                            Pageable pageable);

    /**
     * 获取管理员的最后一次登录时间
     */
    @Query("SELECT a FROM AdminLog a WHERE a.adminId = :adminId AND a.operationType = 'LOGIN' " +
           "AND a.result = 'SUCCESS' ORDER BY a.createdAt DESC")
    Optional<AdminLog> findLastLogin(@Param("adminId") Long adminId);

    /**
     * 复杂条件查询
     */
    @Query("SELECT a FROM AdminLog a WHERE " +
           "(:adminId IS NULL OR a.adminId = :adminId) AND " +
           "(:module IS NULL OR a.module = :module) AND " +
           "(:operationType IS NULL OR a.operationType = :operationType) AND " +
           "(:result IS NULL OR a.result = :result) AND " +
           "(:startTime IS NULL OR a.createdAt >= :startTime) AND " +
           "(:endTime IS NULL OR a.createdAt <= :endTime) " +
           "ORDER BY a.createdAt DESC")
    Page<AdminLog> findByConditions(@Param("adminId") Long adminId,
                                     @Param("module") String module,
                                     @Param("operationType") String operationType,
                                     @Param("result") String result,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime,
                                     Pageable pageable);
}
