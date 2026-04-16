package com.im.service.storage.service.impl;

import com.im.service.storage.dto.FileResponse;
import com.im.service.storage.dto.UploadRequest;
import com.im.service.storage.entity.FileRecord;
import com.im.service.storage.repository.FileRecordRepository;
import com.im.service.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件存储服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final FileRecordRepository fileRecordRepository;

    @Value("${storage.file.base-path:./uploads}")
    private String basePath;

    @Value("${storage.file.base-url:http://localhost:8080/files}")
    private String baseUrl;

    @Value("${storage.file.max-size:10485760}")
    private long maxFileSize;

    @Override
    @Transactional
    public FileResponse uploadFile(UploadRequest request, Long userId) {
        log.info("Uploading file for user: {}, originalName: {}", userId, request.getOriginalName());

        // 解析Base64文件内容
        byte[] fileContent;
        try {
            String base64Data = request.getFileContent();
            if (base64Data.contains(",")) {
                base64Data = base64Data.substring(base64Data.indexOf(",") + 1);
            }
            fileContent = Base64.getDecoder().decode(base64Data);
        } catch (IllegalArgumentException e) {
            log.error("Failed to decode file content", e);
            throw new IllegalArgumentException("无效的文件内容格式");
        }

        // 检查文件大小
        if (fileContent.length > maxFileSize) {
            throw new IllegalArgumentException("文件大小超过限制: " + maxFileSize);
        }

        // 计算文件哈希（用于去重）
        String fileHash = calculateFileHash(request.getFileContent());

        // 检查是否需要去重
        FileRecord existingFile = null;
        if (request.isUseDeduplication()) {
            existingFile = fileRecordRepository.findByFileHash(fileHash).orElse(null);
            if (existingFile != null) {
                log.info("Found duplicate file, returning existing file: {}", existingFile.getFileId());
                return toFileResponse(existingFile);
            }
        }

        // 生成文件ID和存储名
        String fileId = UUID.randomUUID().toString().replace("-", "");
        String extension = getExtension(request.getOriginalName());
        String storedName = fileId + (extension.isEmpty() ? "" : "." + extension);

        // 确定文件类型
        String fileType = request.getEffectiveFileType();

        // 创建文件记录
        FileRecord fileRecord = FileRecord.builder()
                .fileId(fileId)
                .userId(userId)
                .originalName(request.getOriginalName())
                .storedName(storedName)
                .filePath(basePath + "/" + storedName)
                .fileUrl(baseUrl + "/" + fileId)
                .fileSize((long) fileContent.length)
                .mimeType(getMimeType(extension))
                .fileType(fileType)
                .fileHash(fileHash)
                .extension(extension)
                .description(request.getDescription())
                .isPublic(request.getIsPublic())
                .downloadCount(0)
                .status(FileRecord.FileStatus.ACTIVE.name())
                .expireAt(request.getExpireMinutes() != null ? 
                    LocalDateTime.now().plusMinutes(request.getExpireMinutes()) : null)
                .build();

        // 保存记录
        fileRecord = fileRecordRepository.save(fileRecord);
        log.info("File uploaded successfully: {}", fileId);

        return toFileResponse(fileRecord);
    }

    @Override
    public Optional<FileRecord> getFileById(String fileId) {
        return fileRecordRepository.findByFileId(fileId);
    }

    @Override
    public Optional<FileResponse> getFileResponse(String fileId) {
        return fileRecordRepository.findByFileId(fileId)
                .map(this::toFileResponse);
    }

    @Override
    public Optional<String> downloadFile(String fileId) {
        return fileRecordRepository.findByFileId(fileId)
                .filter(f -> f.isAccessible())
                .map(f -> {
                    // 增加下载次数
                    fileRecordRepository.incrementDownloadCount(fileId);
                    // 实际应该返回文件内容，这里简化处理
                    return f.getFileId() + ":" + f.getOriginalName();
                });
    }

    @Override
    @Transactional
    public boolean deleteFile(String fileId, Long userId) {
        Optional<FileRecord> fileOpt = fileRecordRepository.findByFileId(fileId);
        if (fileOpt.isEmpty()) {
            return false;
        }

        FileRecord file = fileOpt.get();
        
        // 检查权限
        if (!file.getUserId().equals(userId)) {
            log.warn("User {} attempted to delete file {} owned by user {}", userId, fileId, file.getUserId());
            throw new SecurityException("无权限删除此文件");
        }

        // 逻辑删除
        file.markAsDeleted();
        fileRecordRepository.save(file);
        
        log.info("File deleted: {}", fileId);
        return true;
    }

    @Override
    @Transactional
    public int batchDeleteFiles(List<String> fileIds, Long userId) {
        int count = 0;
        for (String fileId : fileIds) {
            if (deleteFile(fileId, userId)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public List<FileResponse> getUserFiles(Long userId) {
        return fileRecordRepository.findByUserIdAndStatus(userId, FileRecord.FileStatus.ACTIVE.name())
                .stream()
                .map(this::toFileResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<FileResponse> getUserFilesPage(Long userId, Pageable pageable) {
        Page<FileRecord> page = fileRecordRepository.findByUserId(userId, pageable);
        List<FileResponse> content = page.getContent().stream()
                .map(this::toFileResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public List<FileResponse> searchFiles(String keyword, Long userId) {
        List<FileRecord> files;
        if (userId != null) {
            files = fileRecordRepository.findByUserIdAndStatus(userId, FileRecord.FileStatus.ACTIVE.name())
                    .stream()
                    .filter(f -> f.getOriginalName().contains(keyword))
                    .collect(Collectors.toList());
        } else {
            files = fileRecordRepository.searchByName(keyword);
        }
        return files.stream().map(this::toFileResponse).collect(Collectors.toList());
    }

    @Override
    public Page<FileResponse> searchFilesPage(String keyword, Long userId, Pageable pageable) {
        Page<FileRecord> page = fileRecordRepository.searchByNamePageable(keyword, pageable);
        List<FileResponse> content = page.getContent().stream()
                .filter(f -> userId == null || f.getUserId().equals(userId))
                .map(this::toFileResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public List<FileResponse> getFilesByType(String fileType, Long userId) {
        return fileRecordRepository.findByUserIdAndFileType(userId, fileType)
                .stream()
                .filter(f -> FileRecord.FileStatus.ACTIVE.name().equals(f.getStatus()))
                .map(this::toFileResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<FileResponse> updateFileInfo(String fileId, String description, Boolean isPublic, Long userId) {
        return fileRecordRepository.findByFileId(fileId)
                .filter(f -> f.getUserId().equals(userId))
                .map(f -> {
                    if (description != null) {
                        f.setDescription(description);
                    }
                    if (isPublic != null) {
                        f.setIsPublic(isPublic);
                    }
                    return fileRecordRepository.save(f);
                })
                .map(this::toFileResponse);
    }

    @Override
    @Transactional
    public void incrementDownloadCount(String fileId) {
        fileRecordRepository.incrementDownloadCount(fileId);
    }

    @Override
    public boolean existsByFileId(String fileId) {
        return fileRecordRepository.findByFileId(fileId).isPresent();
    }

    @Override
    public boolean isFileOwner(String fileId, Long userId) {
        return fileRecordRepository.findByFileId(fileId)
                .map(f -> f.getUserId().equals(userId))
                .orElse(false);
    }

    @Override
    public boolean isFileAccessible(String fileId, Long userId) {
        return fileRecordRepository.findByFileId(fileId)
                .map(f -> {
                    // 可访问条件：ACTIVE状态、未过期
                    if (!f.isAccessible()) {
                        return false;
                    }
                    // 公开文件或所有者
                    return f.getIsPublic() || (userId != null && f.getUserId().equals(userId));
                })
                .orElse(false);
    }

    @Override
    public long getUserUsedStorage(Long userId) {
        return fileRecordRepository.sumFileSizeByUserId(userId);
    }

    @Override
    public long getUserUsedStorageByType(Long userId, String fileType) {
        return fileRecordRepository.sumFileSizeByUserIdAndType(userId, fileType);
    }

    @Override
    public FileStatistics getUserStatistics(Long userId) {
        FileStatistics stats = new FileStatistics();
        stats.setTotalFiles(fileRecordRepository.countByUserId(userId));
        stats.setTotalSize(fileRecordRepository.sumFileSizeByUserId(userId));
        stats.setImageCount(fileRecordRepository.countByUserIdAndFileType(userId, "IMAGE"));
        stats.setVideoCount(fileRecordRepository.countByUserIdAndFileType(userId, "VIDEO"));
        stats.setAudioCount(fileRecordRepository.countByUserIdAndFileType(userId, "AUDIO"));
        stats.setDocumentCount(fileRecordRepository.countByUserIdAndFileType(userId, "DOCUMENT"));
        stats.setOtherCount(fileRecordRepository.countByUserIdAndFileType(userId, "OTHER"));
        return stats;
    }

    @Override
    @Transactional
    public int cleanupExpiredFiles() {
        List<FileRecord> expiredFiles = fileRecordRepository.findExpiredFiles(LocalDateTime.now());
        for (FileRecord file : expiredFiles) {
            file.markAsExpired();
            fileRecordRepository.save(file);
        }
        log.info("Cleaned up {} expired files", expiredFiles.size());
        return expiredFiles.size();
    }

    @Override
    public String calculateFileHash(String base64Content) {
        try {
            String base64Data = base64Content;
            if (base64Data.contains(",")) {
                base64Data = base64Data.substring(base64Data.indexOf(",") + 1);
            }
            byte[] data = Base64.getDecoder().decode(base64Data);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
            log.error("Failed to calculate file hash", e);
            return UUID.randomUUID().toString();
        }
    }

    @Override
    public Optional<FileResponse> findByHash(String fileHash) {
        return fileRecordRepository.findByFileHash(fileHash)
                .filter(f -> f.isAccessible())
                .map(this::toFileResponse);
    }

    @Override
    public List<FileResponse> getHotFiles(int limit) {
        return fileRecordRepository.findHotFiles(Pageable.ofSize(limit))
                .stream()
                .map(this::toFileResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FileResponse> getRecentFiles(int limit) {
        return fileRecordRepository.findRecentFiles(Pageable.ofSize(limit))
                .stream()
                .map(this::toFileResponse)
                .collect(Collectors.toList());
    }

    // 辅助方法：转换为响应DTO
    private FileResponse toFileResponse(FileRecord record) {
        FileResponse response = FileResponse.builder()
                .fileId(record.getFileId())
                .originalName(record.getOriginalName())
                .fileUrl(record.getFileUrl())
                .fileSize(record.getFileSize())
                .formattedSize(record.getFormattedSize())
                .mimeType(record.getMimeType())
                .fileType(record.getFileType())
                .extension(record.getExtension())
                .description(record.getDescription())
                .isPublic(record.getIsPublic())
                .downloadCount(record.getDownloadCount())
                .status(record.getStatus())
                .userId(record.getUserId())
                .expireAt(record.getExpireAt())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
        
        response.setExpiredFlag();
        response.setIsAccessible(record.isAccessible());
        
        return response;
    }

    // 获取文件扩展名
    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    // 根据扩展名获取MIME类型
    private String getMimeType(String extension) {
        return switch (extension.toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";
            case "mp4" -> "video/mp4";
            case "avi" -> "video/x-msvideo";
            case "mov" -> "video/quicktime";
            case "wmv" -> "video/x-ms-wmv";
            case "mp3" -> "audio/mpeg";
            case "wav" -> "audio/wav";
            case "aac" -> "audio/aac";
            case "flac" -> "audio/flac";
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt" -> "application/vnd.ms-powerpoint";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt" -> "text/plain";
            case "json" -> "application/json";
            case "xml" -> "application/xml";
            case "html", "htm" -> "text/html";
            case "css" -> "text/css";
            case "js" -> "application/javascript";
            default -> "application/octet-stream";
        };
    }
}
