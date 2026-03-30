package com.im.backend.service.impl;

import com.im.backend.dto.FileInfoDTO;
import com.im.backend.dto.FileUploadRequest;
import com.im.backend.dto.FileUploadResponse;
import com.im.backend.entity.FileRecord;
import com.im.backend.repository.FileRecordRepository;
import com.im.backend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 文件存储服务实现
 * 功能#17: 文件上传下载
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Autowired
    private FileRecordRepository fileRecordRepository;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8080/api/file/download/}")
    private String baseDownloadUrl;

    @Value("${file.max-storage-per-user:1073741824}") // 1GB default
    private Long maxStoragePerUser;

    @Override
    @Transactional
    public FileUploadResponse uploadFile(MultipartFile file, FileUploadRequest request, Long ownerId) throws IOException {
        // Check storage limit
        Long currentUsage = getUserStorageUsage(ownerId);
        if (currentUsage + file.getSize() > maxStoragePerUser) {
            throw new RuntimeException("Storage limit exceeded");
        }

        // Generate unique stored name
        String storedName = UUID.randomUUID().toString() + "_" + System.currentTimeMillis();
        String extension = "";
        int dotIndex = request.getFileName().lastIndexOf('.');
        if (dotIndex > 0) {
            extension = request.getFileName().substring(dotIndex);
            storedName += extension;
        }

        // Create upload directory if not exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Create subdirectory based on date
        String dateDir = LocalDateTime.now().toLocalDate().toString();
        Path datePath = uploadPath.resolve(dateDir);
        if (!Files.exists(datePath)) {
            Files.createDirectories(datePath);
        }

        // Save file
        Path filePath = datePath.resolve(storedName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Save record to database
        FileRecord record = new FileRecord();
        record.setOriginalName(request.getFileName());
        record.setStoredName(storedName);
        record.setFilePath(filePath.toString());
        record.setFileSize(file.getSize());
        record.setMimeType(request.getMimeType() != null ? request.getMimeType() : file.getContentType());
        record.setFileHash(request.getFileHash());
        record.setOwnerId(ownerId);
        record.setIsPublic(request.getIsPublic());
        record.setExpiresAt(request.getExpiresAt());

        record = fileRecordRepository.save(record);

        return new FileUploadResponse(
            record.getId(),
            record.getOriginalName(),
            record.getStoredName(),
            record.getFileSize(),
            record.getMimeType(),
            baseDownloadUrl + record.getStoredName(),
            record.getIsPublic(),
            record.getCreatedAt(),
            record.getExpiresAt()
        );
    }

    @Override
    public Resource downloadFile(String storedName) throws IOException {
        FileRecord record = fileRecordRepository.findByStoredName(storedName)
            .orElseThrow(() -> new RuntimeException("File not found"));

        // Check if expired
        if (record.getExpiresAt() != null && record.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("File has expired");
        }

        Path filePath = Paths.get(record.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("File not readable");
        }

        // Increment download count
        fileRecordRepository.incrementDownloadCount(record.getId());

        return resource;
    }

    @Override
    public FileInfoDTO getFileInfo(Long fileId) {
        FileRecord record = fileRecordRepository.findById(fileId)
            .orElseThrow(() -> new RuntimeException("File not found"));
        return convertToDTO(record);
    }

    @Override
    @Transactional
    public void deleteFile(Long fileId, Long operatorId) {
        FileRecord record = fileRecordRepository.findById(fileId)
            .orElseThrow(() -> new RuntimeException("File not found"));

        // Check permission
        if (!record.getOwnerId().equals(operatorId)) {
            throw new RuntimeException("No permission to delete this file");
        }

        // Delete physical file
        try {
            Path filePath = Paths.get(record.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log error but continue to delete record
        }

        // Delete database record
        fileRecordRepository.delete(record);
    }

    @Override
    public Page<FileInfoDTO> getUserFiles(Long ownerId, Pageable pageable) {
        return fileRecordRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId, pageable)
            .map(this::convertToDTO);
    }

    @Override
    public Page<FileInfoDTO> searchFiles(String keyword, Long ownerId, Pageable pageable) {
        return fileRecordRepository.searchByName(keyword, ownerId, pageable)
            .map(this::convertToDTO);
    }

    @Override
    public Page<FileInfoDTO> getPublicFiles(Pageable pageable) {
        return fileRecordRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable)
            .map(this::convertToDTO);
    }

    @Override
    public void incrementDownloadCount(Long fileId) {
        fileRecordRepository.incrementDownloadCount(fileId);
    }

    @Override
    public Long getUserStorageUsage(Long ownerId) {
        return fileRecordRepository.sumFileSizeByOwnerId(ownerId);
    }

    private FileInfoDTO convertToDTO(FileRecord record) {
        FileInfoDTO dto = new FileInfoDTO();
        dto.setId(record.getId());
        dto.setOriginalName(record.getOriginalName());
        dto.setFileSize(record.getFileSize());
        dto.setMimeType(record.getMimeType());
        dto.setOwnerId(record.getOwnerId());
        dto.setIsPublic(record.getIsPublic());
        dto.setDownloadCount(record.getDownloadCount());
        dto.setCreatedAt(record.getCreatedAt());
        dto.setExpiresAt(record.getExpiresAt());
        return dto;
    }
}
