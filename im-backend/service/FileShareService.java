package com.im.backend.service;

import com.im.backend.dto.FileShareDTO;
import com.im.backend.model.FileMetadata;
import com.im.backend.repository.FileMetadataRepository;
import com.im.backend.storage.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 文件分享服务核心类
 * 负责文件上传、下载、分享链接生成等
 */
@Service
public class FileShareService {

    private static final Logger logger = LoggerFactory.getLogger(FileShareService.class);

    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    @Autowired
    private FileStorageService storageService;

    @Value("${file.share.max-file-size:104857600}")
    private long maxFileSize;

    @Value("${file.share.allowed-types:*}")
    private String allowedFileTypes;

    @Value("${file.share.link-expiry-hours:168}")
    private int linkExpiryHours;

    @Value("${file.share.max-files-per-user:1000}")
    private int maxFilesPerUser;

    /**
     * 上传文件
     */
    @Transactional
    public FileShareDTO uploadFile(MultipartFile file, Long userId, String description) {
        logger.info("User {} uploading file: {}", userId, file.getOriginalFilename());

        try {
            // 1. 验证文件
            FileShareDTO validation = validateFile(file, userId);
            if (!validation.isSuccess()) {
                return validation;
            }

            // 2. 生成文件ID和存储路径
            String fileId = generateFileId();
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);

            // 3. 存储文件
            String storagePath = storageService.store(file, fileId);

            // 4. 保存元数据
            FileMetadata metadata = new FileMetadata();
            metadata.setFileId(fileId);
            metadata.setOriginalName(originalFilename);
            metadata.setStoragePath(storagePath);
            metadata.setFileSize(file.getSize());
            metadata.setContentType(file.getContentType());
            metadata.setExtension(fileExtension);
            metadata.setUploadedBy(userId);
            metadata.setUploadTime(LocalDateTime.now());
            metadata.setDescription(description);
            metadata.setExpired(false);

            fileMetadataRepository.save(metadata);

            // 5. 生成分享链接
            String shareLink = generateShareLink(fileId);

            // 6. 构建返回DTO
            FileShareDTO result = new FileShareDTO();
            result.setSuccess(true);
            result.setFileId(fileId);
            result.setOriginalName(originalFilename);
            result.setFileSize(file.getSize());
            result.setContentType(file.getContentType());
            result.setShareLink(shareLink);
            result.setUploadTime(LocalDateTime.now());
            result.setExpiryTime(LocalDateTime.now().plusHours(linkExpiryHours));

            logger.info("File uploaded successfully: {}", fileId);
            return result;

        } catch (IOException e) {
            logger.error("Failed to upload file: {}", e.getMessage(), e);
            return createErrorDTO("UPLOAD_FAILED", "文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 验证文件
     */
    private FileShareDTO validateFile(MultipartFile file, Long userId) {
        // 检查文件大小
        if (file.getSize() > maxFileSize) {
            return createErrorDTO("FILE_TOO_LARGE",
                "文件大小超过限制（最大 " + (maxFileSize / 1024 / 1024) + "MB）");
        }

        // 检查文件类型
        if (!allowedFileTypes.equals("*")) {
            String contentType = file.getContentType();
            if (contentType == null || !allowedFileTypes.contains(contentType)) {
                return createErrorDTO("FILE_TYPE_NOT_ALLOWED", "不支持的文件类型");
            }
        }

        // 检查用户文件数量限制
        long userFileCount = fileMetadataRepository.countByUploadedByAndExpiredFalse(userId);
        if (userFileCount >= maxFilesPerUser) {
            return createErrorDTO("FILE_LIMIT_EXCEEDED",
                "您上传的文件数量已达到上限（" + maxFilesPerUser + "个）");
        }

        return new FileShareDTO(true);
    }

    /**
     * 下载文件
     */
    public FileShareDTO downloadFile(String fileId, Long userId) {
        logger.info("User {} downloading file: {}", userId, fileId);

        Optional<FileMetadata> metadataOpt = fileMetadataRepository.findById(fileId);
        if (!metadataOpt.isPresent()) {
            return createErrorDTO("FILE_NOT_FOUND", "文件不存在");
        }

        FileMetadata metadata = metadataOpt.get();

        // 检查文件是否过期
        if (metadata.isExpired()) {
            return createErrorDTO("FILE_EXPIRED", "文件链接已过期");
        }

        // 检查下载权限
        if (!hasDownloadPermission(metadata, userId)) {
            return createErrorDTO("NO_PERMISSION", "您没有权限下载此文件");
        }

        // 更新下载统计
        metadata.setDownloadCount(metadata.getDownloadCount() + 1);
        metadata.setLastDownloadTime(LocalDateTime.now());
        fileMetadataRepository.save(metadata);

        FileShareDTO result = new FileShareDTO(true);
        result.setFileId(fileId);
        result.setOriginalName(metadata.getOriginalName());
        result.setFileSize(metadata.getFileSize());
        result.setContentType(metadata.getContentType());
        result.setStoragePath(metadata.getStoragePath());

        return result;
    }

    /**
     * 获取文件信息
     */
    public FileShareDTO getFileInfo(String fileId) {
        Optional<FileMetadata> metadataOpt = fileMetadataRepository.findById(fileId);
        if (!metadataOpt.isPresent()) {
            return createErrorDTO("FILE_NOT_FOUND", "文件不存在");
        }

        FileMetadata metadata = metadataOpt.get();

        FileShareDTO result = new FileShareDTO(true);
        result.setFileId(fileId);
        result.setOriginalName(metadata.getOriginalName());
        result.setFileSize(metadata.getFileSize());
        result.setContentType(metadata.getContentType());
        result.setUploadTime(metadata.getUploadTime());
        result.setDownloadCount(metadata.getDownloadCount());
        result.setDescription(metadata.getDescription());
        result.setExpired(metadata.isExpired());

        return result;
    }

    /**
     * 获取用户文件列表
     */
    public Page<FileMetadata> getUserFiles(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return fileMetadataRepository.findByUploadedByOrderByUploadTimeDesc(userId, pageable);
    }

    /**
     * 删除文件
     */
    @Transactional
    public FileShareDTO deleteFile(String fileId, Long userId) {
        logger.info("User {} deleting file: {}", userId, fileId);

        Optional<FileMetadata> metadataOpt = fileMetadataRepository.findById(fileId);
        if (!metadataOpt.isPresent()) {
            return createErrorDTO("FILE_NOT_FOUND", "文件不存在");
        }

        FileMetadata metadata = metadataOpt.get();

        // 验证删除权限
        if (!metadata.getUploadedBy().equals(userId)) {
            return createErrorDTO("NO_PERMISSION", "您没有权限删除此文件");
        }

        // 删除存储的文件
        try {
            storageService.delete(metadata.getStoragePath());
        } catch (IOException e) {
            logger.error("Failed to delete file from storage: {}", e.getMessage());
        }

        // 删除元数据
        fileMetadataRepository.delete(metadata);

        FileShareDTO result = new FileShareDTO(true);
        result.setFileId(fileId);
        result.setMessage("文件已删除");

        return result;
    }

    /**
     * 生成分享链接
     */
    private String generateShareLink(String fileId) {
        return "/api/v1/files/share/" + fileId;
    }

    /**
     * 生成文件ID
     */
    private String generateFileId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 检查下载权限
     */
    private boolean hasDownloadPermission(FileMetadata metadata, Long userId) {
        // 上传者可以下载
        if (metadata.getUploadedBy().equals(userId)) {
            return true;
        }

        // TODO: 实现更复杂的权限检查（如分享范围、白名单等）
        return true;
    }

    /**
     * 批量删除过期文件
     */
    @Transactional
    public int cleanupExpiredFiles() {
        LocalDateTime expiryTime = LocalDateTime.now().minusHours(linkExpiryHours);
        List<FileMetadata> expiredFiles = fileMetadataRepository.findByUploadTimeBeforeAndExpiredFalse(expiryTime);

        int deletedCount = 0;
        for (FileMetadata file : expiredFiles) {
            file.setExpired(true);
            fileMetadataRepository.save(file);
            deletedCount++;
        }

        logger.info("Cleaned up {} expired files", deletedCount);
        return deletedCount;
    }

    /**
     * 更新文件描述
     */
    @Transactional
    public FileShareDTO updateFileDescription(String fileId, Long userId, String newDescription) {
        Optional<FileMetadata> metadataOpt = fileMetadataRepository.findById(fileId);
        if (!metadataOpt.isPresent()) {
            return createErrorDTO("FILE_NOT_FOUND", "文件不存在");
        }

        FileMetadata metadata = metadataOpt.get();
        if (!metadata.getUploadedBy().equals(userId)) {
            return createErrorDTO("NO_PERMISSION", "您没有权限修改此文件");
        }

        metadata.setDescription(newDescription);
        metadata.setUpdatedAt(LocalDateTime.now());
        fileMetadataRepository.save(metadata);

        FileShareDTO result = new FileShareDTO(true);
        result.setFileId(fileId);
        result.setMessage("文件描述已更新");

        return result;
    }

    /**
     * 创建错误DTO
     */
    private FileShareDTO createErrorDTO(String errorCode, String errorMessage) {
        FileShareDTO dto = new FileShareDTO();
        dto.setSuccess(false);
        dto.setErrorCode(errorCode);
        dto.setErrorMessage(errorMessage);
        return dto;
    }
}
