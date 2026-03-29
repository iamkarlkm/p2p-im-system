package com.im.backend.repository;

import com.im.backend.model.FileChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileChunkRepository extends JpaRepository<FileChunk, String> {

    /**
     * 根据上传ID查找所有分片
     */
    List<FileChunk> findByUploadId(String uploadId);

    /**
     * 根据上传ID排序查找所有分片
     */
    List<FileChunk> findByUploadIdOrderByChunkIndexAsc(String uploadId);

    /**
     * 根据上传ID和分片索引查找
     */
    Optional<FileChunk> findByUploadIdAndChunkIndex(String uploadId, Integer chunkIndex);

    /**
     * 检查分片是否存在
     */
    boolean existsByUploadIdAndChunkIndex(String uploadId, Integer chunkIndex);

    /**
     * 统计上传任务的分片数量
     */
    long countByUploadId(String uploadId);

    /**
     * 删除上传任务的所有分片
     */
    @Modifying
    @Query("DELETE FROM FileChunk c WHERE c.uploadId = ?1")
    void deleteByUploadId(String uploadId);

    /**
     * 查找过期的分片
     */
    List<FileChunk> findByExpiresAtBefore(LocalDateTime expiryTime);

    /**
     * 批量删除过期分片
     */
    @Modifying
    @Query("DELETE FROM FileChunk c WHERE c.expiresAt < ?1")
    void deleteExpiredChunks(LocalDateTime expiryTime);

    /**
     * 查找需要重试的分片（重试次数少于3次且上次重试超过5分钟）
     */
    @Query("SELECT c FROM FileChunk c WHERE c.retryCount < 3 AND (c.lastRetryAt IS NULL OR c.lastRetryAt < ?1)")
    List<FileChunk> findRetryableChunks(LocalDateTime retryThreshold);

    /**
     * 获取上传任务的最大分片索引
     */
    @Query("SELECT MAX(c.chunkIndex) FROM FileChunk c WHERE c.uploadId = ?1")
    Integer findMaxChunkIndexByUploadId(String uploadId);
}
