package com.im.backend.repository;

import com.im.backend.model.FileMetadata;
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
 * 文件元数据数据访问层
 */
@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, String> {

    /**
     * 根据上传者查询文件列表（分页）
     */
    Page<FileMetadata> findByUploadedByOrderByUploadTimeDesc(Long uploadedBy, Pageable pageable);

    /**
     * 统计用户未过期的文件数量
     */
    long countByUploadedByAndExpiredFalse(Long uploadedBy);

    /**
     * 根据会话ID查询文件
     */
    List<FileMetadata> findByConversationIdOrderByUploadTimeDesc(Long conversationId);

    /**
     * 根据消息ID查询文件
     */
    Optional<FileMetadata> findByMessageId(Long messageId);

    /**
     * 查询过期文件（用于清理）
     */
    @Query("SELECT f FROM FileMetadata f WHERE f.uploadTime < :beforeTime AND f.expired = false")
    List<FileMetadata> findByUploadTimeBeforeAndExpiredFalse(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 根据文件哈希查询（用于去重）
     */
    Optional<FileMetadata> findByFileHash(String fileHash);

    /**
     * 搜索用户文件
     */
    @Query("SELECT f FROM FileMetadata f WHERE f.uploadedBy = :userId AND " +
           "(f.originalName LIKE %:keyword% OR f.description LIKE %:keyword%)")
    Page<FileMetadata> searchUserFiles(@Param("userId") Long userId,
                                       @Param("keyword") String keyword,
                                       Pageable pageable);

    /**
     * 统计用户总文件大小
     */
    @Query("SELECT SUM(f.fileSize) FROM FileMetadata f WHERE f.uploadedBy = :userId AND f.expired = false")
    Long sumFileSizeByUser(@Param("userId") Long userId);

    /**
     * 根据文件类型查询
     */
    @Query("SELECT f FROM FileMetadata f WHERE f.uploadedBy = :userId AND f.contentType LIKE %:type%")
    Page<FileMetadata> findByUploadedByAndContentTypeContaining(@Param("userId") Long userId,
                                                                 @Param("type") String type,
                                                                 Pageable pageable);

    /**
     * 获取热门文件（下载次数最多的）
     */
    @Query("SELECT f FROM FileMetadata f WHERE f.expired = false ORDER BY f.downloadCount DESC")
    List<FileMetadata> findPopularFiles(Pageable pageable);

    /**
     * 删除过期文件记录
     */
    @Query("DELETE FROM FileMetadata f WHERE f.expired = true AND f.uploadTime < :beforeTime")
    void deleteExpiredFilesBefore(@Param("beforeTime") LocalDateTime beforeTime);
}
