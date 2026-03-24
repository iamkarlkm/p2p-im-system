package com.im.backend.repository;

import com.im.backend.entity.FileEntity;
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
 * 文件仓储 - CDN/MinIO 文件服务
 */
@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    Optional<FileEntity> findByFileIdAndDeletedFalse(String fileId);

    Optional<FileEntity> findByObjectNameAndDeletedFalse(String objectName);

    Page<FileEntity> findByUserIdAndDeletedFalseOrderByUploadTimeDesc(String userId, Pageable pageable);

    Page<FileEntity> findByConversationIdAndDeletedFalseOrderByUploadTimeDesc(String conversationId, Pageable pageable);

    @Query("SELECT f FROM FileEntity f WHERE f.userId = :userId AND f.fileType = :fileType AND f.deleted = false ORDER BY f.uploadTime DESC")
    Page<FileEntity> findByUserIdAndFileType(@Param("userId") String userId, @Param("fileType") String fileType, Pageable pageable);

    @Query("SELECT SUM(f.fileSize) FROM FileEntity f WHERE f.userId = :userId AND f.deleted = false")
    Long getTotalStorageUsedByUser(@Param("userId") String userId);

    @Query("SELECT COUNT(f) FROM FileEntity f WHERE f.userId = :userId AND f.deleted = false")
    Long countFilesByUser(@Param("userId") String userId);

    @Modifying
    @Query("UPDATE FileEntity f SET f.deleted = true, f.deleteTime = :now WHERE f.expireTime < :now AND f.deleted = false")
    int deleteExpiredFiles(@Param("now") LocalDateTime now);

    List<FileEntity> findByConversationIdAndFileTypeAndDeletedFalseOrderByUploadTimeDesc(
            String conversationId, String fileType);

    @Query("SELECT f FROM FileEntity f WHERE f.conversationId = :conversationId AND f.deleted = false ORDER BY f.uploadTime DESC")
    Page<FileEntity> findMediaByConversation(@Param("conversationId") String conversationId, Pageable pageable);
}
