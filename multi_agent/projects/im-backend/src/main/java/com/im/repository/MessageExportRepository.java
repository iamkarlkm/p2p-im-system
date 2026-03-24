package com.im.repository;

import com.im.entity.MessageExportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息导出数据仓库接口
 */
@Repository
public interface MessageExportRepository extends JpaRepository<MessageExportEntity, Long> {
    
    // 根据用户ID查询导出记录
    List<MessageExportEntity> findByUserId(Long userId);
    
    // 根据用户ID分页查询导出记录
    Page<MessageExportEntity> findByUserId(Long userId, Pageable pageable);
    
    // 根据用户ID和状态查询导出记录
    List<MessageExportEntity> findByUserIdAndStatus(Long userId, MessageExportEntity.ExportStatus status);
    
    // 根据状态查询导出记录
    List<MessageExportEntity> findByStatus(MessageExportEntity.ExportStatus status);
    
    // 根据会话ID查询导出记录
    List<MessageExportEntity> findBySessionId(String sessionId);
    
    // 根据导出格式查询
    List<MessageExportEntity> findByExportFormat(MessageExportEntity.ExportFormat exportFormat);
    
    // 根据用户ID和导出格式查询
    List<MessageExportEntity> findByUserIdAndExportFormat(Long userId, MessageExportEntity.ExportFormat exportFormat);
    
    // 根据用户ID和时间范围查询
    List<MessageExportEntity> findByUserIdAndCreatedTimeBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    
    // 统计用户导出数量
    @Query("SELECT COUNT(e) FROM MessageExportEntity e WHERE e.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    // 统计用户成功导出数量
    @Query("SELECT COUNT(e) FROM MessageExportEntity e WHERE e.userId = :userId AND e.status = com.im.entity.MessageExportEntity.ExportStatus.COMPLETED")
    long countSuccessfulExportsByUserId(@Param("userId") Long userId);
    
    // 统计用户的导出消息总数
    @Query("SELECT SUM(e.messageCount) FROM MessageExportEntity e WHERE e.userId = :userId AND e.status = com.im.entity.MessageExportEntity.ExportStatus.COMPLETED")
    Long sumExportedMessagesByUserId(@Param("userId") Long userId);
    
    // 统计用户的导出文件总大小
    @Query("SELECT SUM(e.fileSize) FROM MessageExportEntity e WHERE e.userId = :userId AND e.status = com.im.entity.MessageExportEntity.ExportStatus.COMPLETED")
    Long sumExportedFileSizeByUserId(@Param("userId") Long userId);
    
    // 查找最近N个导出记录
    @Query("SELECT e FROM MessageExportEntity e WHERE e.userId = :userId ORDER BY e.createdTime DESC")
    List<MessageExportEntity> findRecentByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // 查找待处理的导出任务
    @Query("SELECT e FROM MessageExportEntity e WHERE e.status = com.im.entity.MessageExportEntity.ExportStatus.PENDING ORDER BY e.createdTime ASC")
    List<MessageExportEntity> findPendingExports(Pageable pageable);
    
    // 查找处理中的导出任务
    @Query("SELECT e FROM MessageExportEntity e WHERE e.status = com.im.entity.MessageExportEntity.ExportStatus.PROCESSING")
    List<MessageExportEntity> findProcessingExports();
    
    // 根据用户ID和状态分页查询
    Page<MessageExportEntity> findByUserIdAndStatus(Long userId, MessageExportEntity.ExportStatus status, Pageable pageable);
    
    // 根据用户ID和会话ID查询
    List<MessageExportEntity> findByUserIdAndSessionId(Long userId, String sessionId);
    
    // 根据用户ID、会话ID和状态查询
    List<MessageExportEntity> findByUserIdAndSessionIdAndStatus(Long userId, String sessionId, MessageExportEntity.ExportStatus status);
    
    // 根据导出名称模糊搜索
    @Query("SELECT e FROM MessageExportEntity e WHERE e.userId = :userId AND LOWER(e.exportName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<MessageExportEntity> searchByExportName(@Param("userId") Long userId, @Param("keyword") String keyword);
    
    // 根据描述模糊搜索
    @Query("SELECT e FROM MessageExportEntity e WHERE e.userId = :userId AND LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<MessageExportEntity> searchByDescription(@Param("userId") Long userId, @Param("keyword") String keyword);
    
    // 根据文件路径查询
    List<MessageExportEntity> findByFilePath(String filePath);
    
    // 删除过期的导出记录
    @Transactional
    @Modifying
    @Query("DELETE FROM MessageExportEntity e WHERE e.status = com.im.entity.MessageExportEntity.ExportStatus.COMPLETED AND e.completedTime < :cutoffTime")
    int deleteOldCompletedExports(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    // 删除失败的导出记录
    @Transactional
    @Modifying
    @Query("DELETE FROM MessageExportEntity e WHERE e.status = com.im.entity.MessageExportEntity.ExportStatus.FAILED AND e.createdTime < :cutoffTime")
    int deleteOldFailedExports(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    // 取消待处理的导出任务
    @Transactional
    @Modifying
    @Query("UPDATE MessageExportEntity e SET e.status = com.im.entity.MessageExportEntity.ExportStatus.CANCELLED, e.updatedTime = :now, e.errorMessage = '手动取消' WHERE e.id = :id AND e.status = com.im.entity.MessageExportEntity.ExportStatus.PENDING")
    int cancelPendingExport(@Param("id") Long id, @Param("now") LocalDateTime now);
    
    // 更新导出进度
    @Transactional
    @Modifying
    @Query("UPDATE MessageExportEntity e SET e.progress = :progress, e.progressMessage = :message, e.updatedTime = :now WHERE e.id = :id")
    int updateProgress(@Param("id") Long id, @Param("progress") int progress, @Param("message") String message, @Param("now") LocalDateTime now);
    
    // 标记为处理中
    @Transactional
    @Modifying
    @Query("UPDATE MessageExportEntity e SET e.status = com.im.entity.MessageExportEntity.ExportStatus.PROCESSING, e.updatedTime = :now, e.progress = 10, e.progressMessage = '开始处理导出任务' WHERE e.id = :id AND e.status = com.im.entity.MessageExportEntity.ExportStatus.PENDING")
    int markAsProcessing(@Param("id") Long id, @Param("now") LocalDateTime now);
    
    // 标记为完成
    @Transactional
    @Modifying
    @Query("UPDATE MessageExportEntity e SET e.status = com.im.entity.MessageExportEntity.ExportStatus.COMPLETED, e.updatedTime = :now, e.completedTime = :now, e.messageCount = :messageCount, e.fileSize = :fileSize, e.filePath = :filePath, e.progress = 100, e.progressMessage = '导出完成' WHERE e.id = :id")
    int markAsCompleted(@Param("id") Long id, @Param("now") LocalDateTime now, 
                       @Param("messageCount") int messageCount, @Param("fileSize") long fileSize, 
                       @Param("filePath") String filePath);
    
    // 标记为失败
    @Transactional
    @Modifying
    @Query("UPDATE MessageExportEntity e SET e.status = com.im.entity.MessageExportEntity.ExportStatus.FAILED, e.updatedTime = :now, e.errorMessage = :errorMessage, e.progress = 0, e.progressMessage = '导出失败' WHERE e.id = :id")
    int markAsFailed(@Param("id") Long id, @Param("now") LocalDateTime now, @Param("errorMessage") String errorMessage);
    
    // 统计导出状态分布
    @Query("SELECT e.status, COUNT(e) FROM MessageExportEntity e WHERE e.userId = :userId GROUP BY e.status")
    List<Object[]> countByStatusGroup(@Param("userId") Long userId);
    
    // 统计导出格式分布
    @Query("SELECT e.exportFormat, COUNT(e) FROM MessageExportEntity e WHERE e.userId = :userId GROUP BY e.exportFormat")
    List<Object[]> countByFormatGroup(@Param("userId") Long userId);
    
    // 统计导出时间分布（按小时）
    @Query("SELECT HOUR(e.createdTime), COUNT(e) FROM MessageExportEntity e WHERE e.userId = :userId GROUP BY HOUR(e.createdTime) ORDER BY HOUR(e.createdTime)")
    List<Object[]> countByHourGroup(@Param("userId") Long userId);
    
    // 获取用户的导出统计摘要
    @Query("SELECT COUNT(e), SUM(e.messageCount), SUM(e.fileSize), AVG(e.progress) FROM MessageExportEntity e WHERE e.userId = :userId")
    Object[] getUserExportSummary(@Param("userId") Long userId);
    
    // 查找大文件导出（超过阈值）
    @Query("SELECT e FROM MessageExportEntity e WHERE e.fileSize > :sizeThreshold AND e.status = com.im.entity.MessageExportEntity.ExportStatus.COMPLETED")
    List<MessageExportEntity> findLargeExports(@Param("sizeThreshold") long sizeThreshold);
    
    // 批量更新状态
    @Transactional
    @Modifying
    @Query("UPDATE MessageExportEntity e SET e.status = :newStatus, e.updatedTime = :now WHERE e.id IN :ids AND e.status = :oldStatus")
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("oldStatus") MessageExportEntity.ExportStatus oldStatus, 
                         @Param("newStatus") MessageExportEntity.ExportStatus newStatus, @Param("now") LocalDateTime now);
    
    // 根据ID列表查找
    List<MessageExportEntity> findByIdIn(List<Long> ids);
}