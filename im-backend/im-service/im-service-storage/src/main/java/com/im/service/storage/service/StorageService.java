package com.im.service.storage.service;

import com.im.service.storage.dto.FileResponse;
import com.im.service.storage.dto.UploadRequest;
import com.im.service.storage.entity.FileRecord;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 文件存储服务接口
 */
public interface StorageService {

    /**
     * 上传文件
     * @param request 上传请求
     * @param userId 用户ID
     * @return 文件响应
     */
    FileResponse uploadFile(UploadRequest request, Long userId);

    /**
     * 根据文件ID获取文件信息
     * @param fileId 文件ID
     * @return 文件记录
     */
    Optional<FileRecord> getFileById(String fileId);

    /**
     * 根据文件ID获取文件响应
     * @param fileId 文件ID
     * @return 文件响应
     */
    Optional<FileResponse> getFileResponse(String fileId);

    /**
     * 下载文件
     * @param fileId 文件ID
     * @return 文件内容（Base64编码）
     */
    Optional<String> downloadFile(String fileId);

    /**
     * 删除文件
     * @param fileId 文件ID
     * @param userId 用户ID（用于权限检查）
     * @return 是否成功
     */
    boolean deleteFile(String fileId, Long userId);

    /**
     * 批量删除文件
     * @param fileIds 文件ID列表
     * @param userId 用户ID
     * @return 删除数量
     */
    int batchDeleteFiles(List<String> fileIds, Long userId);

    /**
     * 获取用户的文件列表
     * @param userId 用户ID
     * @return 文件列表
     */
    List<FileResponse> getUserFiles(Long userId);

    /**
     * 分页获取用户文件
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 文件分页
     */
    Page<FileResponse> getUserFilesPage(Long userId, Pageable pageable);

    /**
     * 搜索文件
     * @param keyword 关键词
     * @param userId 用户ID（可选，为null时搜索所有）
     * @return 文件列表
     */
    List<FileResponse> searchFiles(String keyword, Long userId);

    /**
     * 分页搜索文件
     * @param keyword 关键词
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 文件分页
     */
    Page<FileResponse> searchFilesPage(String keyword, Long userId, Pageable pageable);

    /**
     * 根据类型获取文件
     * @param fileType 文件类型
     * @param userId 用户ID
     * @return 文件列表
     */
    List<FileResponse> getFilesByType(String fileType, Long userId);

    /**
     * 更新文件信息
     * @param fileId 文件ID
     * @param description 新描述
     * @param isPublic 新的公开状态
     * @param userId 用户ID
     * @return 更新后的文件响应
     */
    Optional<FileResponse> updateFileInfo(String fileId, String description, Boolean isPublic, Long userId);

    /**
     * 增加下载次数
     * @param fileId 文件ID
     */
    void incrementDownloadCount(String fileId);

    /**
     * 检查文件是否存在
     * @param fileId 文件ID
     * @return 是否存在
     */
    boolean existsByFileId(String fileId);

    /**
     * 检查文件是否属于用户
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 是否属于
     */
    boolean isFileOwner(String fileId, Long userId);

    /**
     * 检查文件是否可访问
     * @param fileId 文件ID
     * @param userId 访问者ID（可为null）
     * @return 是否可访问
     */
    boolean isFileAccessible(String fileId, Long userId);

    /**
     * 获取用户已用存储空间
     * @param userId 用户ID
     * @return 已用空间（字节）
     */
    long getUserUsedStorage(Long userId);

    /**
     * 获取用户指定类型的已用空间
     * @param userId 用户ID
     * @param fileType 文件类型
     * @return 已用空间（字节）
     */
    long getUserUsedStorageByType(Long userId, String fileType);

    /**
     * 获取用户文件统计
     * @param userId 用户ID
     * @return 统计信息
     */
    FileStatistics getUserStatistics(Long userId);

    /**
     * 清理过期文件
     * @return 清理的文件数量
     */
    int cleanupExpiredFiles();

    /**
     * 获取文件内容哈希
     * @param base64Content Base64编码的文件内容
     * @return MD5哈希值
     */
    String calculateFileHash(String base64Content);

    /**
     * 根据哈希查找文件（用于去重）
     * @param fileHash 文件哈希
     * @return 文件响应
     */
    Optional<FileResponse> findByHash(String fileHash);

    /**
     * 获取热门文件
     * @param limit 数量限制
     * @return 文件列表
     */
    List<FileResponse> getHotFiles(int limit);

    /**
     * 获取最近上传的文件
     * @param limit 数量限制
     * @return 文件列表
     */
    List<FileResponse> getRecentFiles(int limit);

    /**
     * 文件统计信息
     */
    class FileStatistics {
        private long totalFiles;
        private long totalSize;
        private long imageCount;
        private long videoCount;
        private long audioCount;
        private long documentCount;
        private long otherCount;

        // Getters and Setters
        public long getTotalFiles() { return totalFiles; }
        public void setTotalFiles(long totalFiles) { this.totalFiles = totalFiles; }
        public long getTotalSize() { return totalSize; }
        public void setTotalSize(long totalSize) { this.totalSize = totalSize; }
        public long getImageCount() { return imageCount; }
        public void setImageCount(long imageCount) { this.imageCount = imageCount; }
        public long getVideoCount() { return videoCount; }
        public void setVideoCount(long videoCount) { this.videoCount = videoCount; }
        public long getAudioCount() { return audioCount; }
        public void setAudioCount(long audioCount) { this.audioCount = audioCount; }
        public long getDocumentCount() { return documentCount; }
        public void setDocumentCount(long documentCount) { this.documentCount = documentCount; }
        public long getOtherCount() { return otherCount; }
        public void setOtherCount(long otherCount) { this.otherCount = otherCount; }
    }
}
