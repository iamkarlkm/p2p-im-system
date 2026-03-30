package com.im.backend.repository;

import com.im.backend.entity.FileRecord;
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
 * 功能#17: 文件上传下载
 */
@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {
    
    /**
     * 根据文件hash查找
     */
    Optional<FileRecord> findByFileHash(String fileHash);
    
    /**
     * 根据存储名称查找
     */
    Optional<FileRecord> findByStoredName(String storedName);
    
    /**
     * 根据所有者ID分页查询
     */
    Page<FileRecord> findByOwnerIdOrderByCreatedAtDesc(Long ownerId, Pageable pageable);
    
    /**
     * 查询所有公开文件
     */
    Page<FileRecord> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 根据所有者ID和过期时间查询
     */
    List<FileRecord> findByOwnerIdAndExpiresAtBefore(Long ownerId, LocalDateTime time);
    
    /**
     * 增加下载次数
     */
    @Modifying
    @Query("UPDATE FileRecord f SET f.downloadCount = f.downloadCount + 1 WHERE f.id = :id")
    void incrementDownloadCount(@Param("id") Long id);
    
    /**
     * 根据文件名模糊搜索
     */
    @Query("SELECT f FROM FileRecord f WHERE f.originalName LIKE %:keyword% AND (f.ownerId = :ownerId OR f.isPublic = true)")
    Page<FileRecord> searchByName(@Param("keyword") String keyword, @Param("ownerId") Long ownerId, Pageable pageable);
    
    /**
     * 查询过期文件
     */
    @Query("SELECT f FROM FileRecord f WHERE f.expiresAt IS NOT NULL AND f.expiresAt < :now")
    List<FileRecord> findExpiredFiles(@Param("now") LocalDateTime now);
    
    /**
     * 统计用户存储空间使用
     */
    @Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM FileRecord f WHERE f.ownerId = :ownerId")
    Long sumFileSizeByOwnerId(@Param("ownerId") Long ownerId);
}
