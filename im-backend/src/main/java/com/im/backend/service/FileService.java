package com.im.backend.service;

import com.im.backend.config.MinIOConfig;
import com.im.backend.dto.FileUploadResponse;
import com.im.backend.entity.FileEntity;
import com.im.backend.repository.FileRepository;
import io.minio.*;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 文件服务 - CDN/MinIO 文件服务核心实现
 * 支持云端文件存储 + CDN 加速分发、文件预览
 */
@Service
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinIOConfig minIOConfig;

    @Autowired
    private FileRepository fileRepository;

    @Value("${cdn.enabled:false}")
    private boolean cdnEnabled;

    @Value("${cdn.base-url:}")
    private String cdnBaseUrl;

    private static final Set<String> IMAGE_TYPES = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp");
    private static final Set<String> VIDEO_TYPES = Set.of("mp4", "avi", "mov", "mkv", "webm");
    private static final Set<String> AUDIO_TYPES = Set.of("mp3", "wav", "ogg", "m4a", "aac");
    private static final Set<String> DOCUMENT_TYPES = Set.of("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt");

    /**
     * 上传文件到 MinIO
     */
    public FileUploadResponse uploadFile(MultipartFile file, String userId, String conversationId) throws Exception {
        ensureBucketExists();

        String fileId = UUID.randomUUID().toString().replace("-", "");
        String originalName = file.getOriginalFilename();
        String extension = getExtension(originalName);
        String fileType = determineFileType(extension);
        String storageName = generateStorageName(userId, fileType, extension);

        // 构建 MinIO 对象名
        String objectName = buildObjectName(userId, storageName);

        // 上传到 MinIO
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(minIOConfig.getBucketName())
                .object(objectName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build()
        );

        // 保存到数据库
        FileEntity entity = new FileEntity();
        entity.setFileId(fileId);
        entity.setUserId(userId);
        entity.setConversationId(conversationId);
        entity.setOriginalName(originalName);
        entity.setStorageName(storageName);
        entity.setFileType(fileType);
        entity.setMimeType(file.getContentType());
        entity.setFileSize(file.getSize());
        entity.setExtension(extension);
        entity.setObjectName(objectName);
        entity.setStatus("completed");
        entity.setUploadTime(LocalDateTime.now());
        fileRepository.save(entity);

        // 生成访问URL
        String downloadUrl = generateDownloadUrl(objectName);

        log.info("文件上传成功: fileId={}, userId={}, size={}", fileId, userId, file.getSize());

        return FileUploadResponse.success(
            fileId, originalName, fileType, file.getSize(),
            file.getContentType(), downloadUrl
        );
    }

    /**
     * 获取文件下载URL
     */
    public String getDownloadUrl(String fileId) {
        FileEntity entity = fileRepository.findByFileIdAndDeletedFalse(fileId)
            .orElseThrow(() -> new RuntimeException("文件不存在: " + fileId));
        return generateDownloadUrl(entity.getObjectName());
    }

    /**
     * 获取文件信息
     */
    public FileEntity getFileInfo(String fileId) {
        return fileRepository.findByFileIdAndDeletedFalse(fileId)
            .orElseThrow(() -> new RuntimeException("文件不存在: " + fileId));
    }

    /**
     * 获取用户的文件列表
     */
    public Page<FileEntity> getUserFiles(String userId, int page, int size) {
        return fileRepository.findByUserIdAndDeletedFalseOrderByUploadTimeDesc(
            userId, PageRequest.of(page, size)
        );
    }

    /**
     * 获取会话的媒体文件
     */
    public Page<FileEntity> getConversationMedia(String conversationId, int page, int size) {
        return fileRepository.findMediaByConversation(conversationId, PageRequest.of(page, size));
    }

    /**
     * 获取用户存储使用情况
     */
    public Map<String, Object> getStorageInfo(String userId) {
        Long totalSize = fileRepository.getTotalStorageUsedByUser(userId);
        Long fileCount = fileRepository.countFilesByUser(userId);
        Map<String, Object> info = new HashMap<>();
        info.put("totalSize", totalSize != null ? totalSize : 0);
        info.put("fileCount", fileCount != null ? fileCount : 0);
        info.put("formattedSize", formatFileSize(totalSize != null ? totalSize : 0));
        return info;
    }

    /**
     * 删除文件
     */
    public void deleteFile(String fileId, String userId) {
        FileEntity entity = fileRepository.findByFileIdAndDeletedFalse(fileId)
            .orElseThrow(() -> new RuntimeException("文件不存在: " + fileId));

        if (!entity.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此文件");
        }

        // 从 MinIO 删除
        try {
            minioClient.removeObject(minIOConfig.getBucketName(), entity.getObjectName());
        } catch (Exception e) {
            log.warn("MinIO删除失败: {}", e.getMessage());
        }

        // 标记数据库删除
        entity.setDeleted(true);
        entity.setDeleteTime(LocalDateTime.now());
        fileRepository.save(entity);

        log.info("文件删除成功: fileId={}, userId={}", fileId, userId);
    }

    /**
     * 获取预签名上传URL (分片上传)
     */
    public String getPresignedUploadUrl(String fileName, String contentType) throws Exception {
        ensureBucketExists();
        String objectName = "uploads/" + UUID.randomUUID().toString() + "/" + fileName;

        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.PUT)
                .bucket(minIOConfig.getBucketName())
                .object(objectName)
                .expiry(60, TimeUnit.MINUTES)
                .build()
        );
    }

    // ==================== 私有方法 ====================

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(
            BucketExistsArgs.builder().bucket(minIOConfig.getBucketName()).build()
        );
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(minIOConfig.getBucketName()).build());
            log.info("创建MinIO存储桶: {}", minIOConfig.getBucketName());
        }
    }

    private String generateStorageName(String userId, String fileType, String extension) {
        long timestamp = System.currentTimeMillis();
        return String.format("%s_%s_%d.%s", userId, fileType, timestamp, extension);
    }

    private String buildObjectName(String userId, String storageName) {
        return String.format("files/%s/%s", userId, storageName);
    }

    private String determineFileType(String extension) {
        String ext = extension.toLowerCase();
        if (IMAGE_TYPES.contains(ext)) return "image";
        if (VIDEO_TYPES.contains(ext)) return "video";
        if (AUDIO_TYPES.contains(ext)) return "audio";
        if (DOCUMENT_TYPES.contains(ext)) return "document";
        return "other";
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private String generateDownloadUrl(String objectName) {
        if (cdnEnabled && cdnBaseUrl != null && !cdnBaseUrl.isEmpty()) {
            return cdnBaseUrl + "/" + objectName;
        }
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(minIOConfig.getBucketName())
                    .object(objectName)
                    .expiry(minIOConfig.getPresignedUrlExpiry(), TimeUnit.SECONDS)
                    .build()
            );
        } catch (Exception e) {
            log.error("生成下载URL失败: {}", e.getMessage());
            return null;
        }
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.2f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.2f MB", size / (1024.0 * 1024));
        return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
    }
}
