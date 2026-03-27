package com.im.backend.repository;

import com.im.backend.model.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, String> {

    /**
     * 根据文件哈希和状态查找（用于秒传）
     */
    Optional<FileUpload> findByFileHashAndStatus(String fileHash, String status);

    /**
     * 根据文件哈希、用户ID和状态查找（用于断点续传）
     */
    Optional<FileUpload> findByFileHashAndUserIdAndStatus(String fileHash, Long userId, String status);

    /**
     * 查找用户的所有上传记录
     */
    List<FileUpload> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 查找用户指定状态的上传记录
     */
    List<FileUpload> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);

    /**
     * 查找特定状态且在指定时间之前更新的记录（用于清理）
     */
    List<FileUpload> findByStatusAndUpdatedAtBefore(String status, LocalDateTime updatedAt);

    /**
     * 统计用户的上传文件数量
     */
    long countByUserIdAndStatus(Long userId, String status);

    /**
     * 统计用户的总上传大小
     */
    @Query("SELECT SUM(f.fileSize) FROM FileUpload f WHERE f.userId = ?1 AND f.status = 'COMPLETED'")
    Long sumFileSizeByUserIdAndStatus(Long userId);

    /**
     * 查找过期的上传记录
     */
    @Query("SELECT f FROM FileUpload f WHERE f.status = 'UPLOADING' AND f.updatedAt < ?1")
    List<FileUpload> findExpiredUploads(LocalDateTime expiryTime);

    /**
     * 查找会话中的文件上传
     */
    List<FileUpload> findByConversationIdOrderByCreatedAtDesc(Long conversationId);

    /**
     * 查找消息关联的文件
     */
    Optional<FileUpload> findByMessageId(Long messageId);

    /**
     * 统计系统总文件数
     */
    long countByStatus(String status);

    /**
     * 统计系统总存储大小
     */
    @Query("SELECT SUM(f.fileSize) FROM FileUpload f WHERE f.status = 'COMPLETED'")
    Long sumTotalStorageSize();
}
