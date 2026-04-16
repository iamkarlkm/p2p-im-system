package com.im.service.storage.repository;

import com.im.service.storage.entity.FileRecord;
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
 * 文件记录数据访问层
 */
@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {

    /**
     * 根据文件ID查询
     */
    Optional<FileRecord> findByFileId(String fileId);

    /**
     * 根据用户ID查询文件列表
     */
    List<FileRecord> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 根据用户ID分页查询
     */
    Page<FileRecord> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据文件哈希查询（用于去重）
     */
    Optional<FileRecord> findByFileHash(String fileHash);

    /**
     * 检查文件哈希是否存在
     */
    boolean existsByFileHash(String fileHash);

    /**
     * 根据状态查询文件
     */
    List<FileRecord> findByStatusOrderByCreatedAtDesc(String status);

    /**
     * 分页查询指定状态的文件
     */
    Page<FileRecord> findByStatus(String status, Pageable pageable);

    /**
     * 根据用户ID和状态查询
     */
    List<FileRecord> findByUserIdAndStatus(Long userId, String status);

    /**
     * 根据文件类型查询
     */
    List<FileRecord> findByFileTypeOrderByCreatedAtDesc(String fileType);

    /**
     * 根据用户ID和文件类型查询
     */
    List<FileRecord> findByUserIdAndFileType(Long userId, String fileType);

    /**
     * 搜索文件名
     */
    @Query("SELECT f FROM FileRecord f WHERE f.originalName LIKE %:keyword% AND f.status = 'ACTIVE'")
    List<FileRecord> searchByName(@Param("keyword") String keyword);

    /**
     * 分页搜索文件名
     */
    @Query("SELECT f FROM FileRecord f WHERE f.originalName LIKE %:keyword% AND f.status = 'ACTIVE'")
    Page<FileRecord> searchByNamePageable(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查询过期文件
     */
    @Query("SELECT f FROM FileRecord f WHERE f.expireAt < :now AND f.status = 'ACTIVE'")
    List<FileRecord> findExpiredFiles(@Param("now") LocalDateTime now);

    /**
     * 查询指定时间范围内的文件
     */
    @Query("SELECT f FROM FileRecord f WHERE f.createdAt BETWEEN :startTime AND :endTime")
    List<FileRecord> findByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 查询指定时间范围内的文件（带状态过滤）
     */
    @Query("SELECT f FROM FileRecord f WHERE f.createdAt BETWEEN :startTime AND :endTime AND f.status = :status")
    List<FileRecord> findByTimeRangeAndStatus(@Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime,
                                               @Param("status") String status);

    /**
     * 统计用户文件数量
     */
    long countByUserId(Long userId);

    /**
     * 统计用户指定类型的文件数量
     */
    long countByUserIdAndFileType(Long userId, String fileType);

    /**
     * 统计用户文件总大小
     */
    @Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM FileRecord f WHERE f.userId = :userId AND f.status = 'ACTIVE'")
    Long sumFileSizeByUserId(@Param("userId") Long userId);

    /**
     * 统计指定类型的文件总大小
     */
    @Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM FileRecord f WHERE f.userId = :userId AND f.fileType = :fileType AND f.status = 'ACTIVE'")
    Long sumFileSizeByUserIdAndType(@Param("userId") Long userId, @Param("fileType") String fileType);

    /**
     * 更新文件状态
     */
    @Modifying
    @Query("UPDATE FileRecord f SET f.status = :status WHERE f.fileId = :fileId")
    int updateStatusByFileId(@Param("fileId") String fileId, @Param("status") String status);

    /**
     * 批量更新过期文件状态
     */
    @Modifying
    @Query("UPDATE FileRecord f SET f.status = 'EXPIRED' WHERE f.expireAt < :now AND f.status = 'ACTIVE'")
    int batchMarkExpired(@Param("now") LocalDateTime now);

    /**
     * 增加下载次数
     */
    @Modifying
    @Query("UPDATE FileRecord f SET f.downloadCount = f.downloadCount + 1 WHERE f.fileId = :fileId")
    int incrementDownloadCount(@Param("fileId") String fileId);

    /**
     * 更新文件URL
     */
    @Modifying
    @Query("UPDATE FileRecord f SET f.fileUrl = :url WHERE f.fileId = :fileId")
    int updateFileUrl(@Param("fileId") String fileId, @Param("url") String url);

    /**
     * 根据文件ID删除
     */
    @Modifying
    @Query("DELETE FROM FileRecord f WHERE f.fileId = :fileId")
    int deleteByFileId(@Param("fileId") String fileId);

    /**
     * 批量删除用户的所有文件记录
     */
    @Modifying
    @Query("DELETE FROM FileRecord f WHERE f.userId = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);

    /**
     * 查询热门文件（按下载次数排序）
     */
    @Query("SELECT f FROM FileRecord f WHERE f.status = 'ACTIVE' ORDER BY f.downloadCount DESC")
    List<FileRecord> findHotFiles(Pageable pageable);

    /**
     * 查询最近上传的文件
     */
    @Query("SELECT f FROM FileRecord f WHERE f.status = 'ACTIVE' ORDER BY f.createdAt DESC")
    List<FileRecord> findRecentFiles(Pageable pageable);

    /**
     * 根据ID列表查询
     */
    @Query("SELECT f FROM FileRecord f WHERE f.id IN :ids")
    List<FileRecord> findByIds(@Param("ids") List<Long> ids);

    /**
     * 根据文件ID列表查询
     */
    @Query("SELECT f FROM FileRecord f WHERE f.fileId IN :fileIds")
    List<FileRecord> findByFileIds(@Param("fileIds") List<String> fileIds);
}
