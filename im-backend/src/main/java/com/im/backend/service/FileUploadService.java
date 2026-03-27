package com.im.backend.service;

import com.im.backend.dto.FileUploadRequestDTO;
import com.im.backend.dto.FileUploadResponseDTO;
import com.im.backend.model.FileChunk;
import com.im.backend.model.FileMetadata;
import com.im.backend.model.FileUpload;
import com.im.backend.repository.FileChunkRepository;
import com.im.backend.repository.FileUploadRepository;
import com.im.backend.util.FileHashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    @Value("${file.upload.max-size:1073741824}")
    private long maxFileSize;

    @Value("${file.upload.chunk-size:5242880}")
    private int chunkSize;

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private FileChunkRepository fileChunkRepository;

    @Autowired
    private FileHashUtil fileHashUtil;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 初始化文件上传
     */
    @Transactional
    public FileUploadResponseDTO initUpload(FileUploadRequestDTO request, Long userId) {
        String fileHash = request.getFileHash();
        long fileSize = request.getFileSize();
        String fileName = request.getFileName();

        // 检查文件大小限制
        if (fileSize > maxFileSize) {
            throw new IllegalArgumentException("文件大小超过限制: " + maxFileSize + " bytes");
        }

        // 检查是否已存在相同文件（秒传）
        Optional<FileUpload> existingFile = fileUploadRepository.findByFileHashAndStatus(fileHash, "COMPLETED");
        if (existingFile.isPresent()) {
            logger.info("文件秒传: fileHash={}, fileName={}", fileHash, fileName);
            return createInstantUploadResponse(existingFile.get(), request);
        }

        // 检查是否存在未完成的上传（断点续传）
        Optional<FileUpload> ongoingUpload = fileUploadRepository.findByFileHashAndUserIdAndStatus(
                fileHash, userId, "UPLOADING");

        if (ongoingUpload.isPresent()) {
            return createResumeResponse(ongoingUpload.get(), request);
        }

        // 创建新的上传任务
        FileUpload upload = new FileUpload();
        upload.setId(UUID.randomUUID().toString());
        upload.setFileName(fileName);
        upload.setFileHash(fileHash);
        upload.setFileSize(fileSize);
        upload.setMimeType(request.getMimeType());
        upload.setChunkSize(chunkSize);
        upload.setTotalChunks(calculateTotalChunks(fileSize));
        upload.setUploadedChunks(0);
        upload.setUserId(userId);
        upload.setStatus("UPLOADING");
        upload.setCreatedAt(LocalDateTime.now());
        upload.setUpdatedAt(LocalDateTime.now());
        upload.setStoragePath(generateStoragePath(fileName));

        fileUploadRepository.save(upload);

        return createUploadResponse(upload, false);
    }

    /**
     * 上传文件分片
     */
    @Transactional
    public FileUploadResponseDTO uploadChunk(String uploadId, int chunkIndex, MultipartFile chunk, Long userId) {
        FileUpload upload = fileUploadRepository.findById(uploadId)
                .orElseThrow(() -> new IllegalArgumentException("上传任务不存在: " + uploadId));

        if (!upload.getUserId().equals(userId)) {
            throw new SecurityException("无权访问此上传任务");
        }

        if (!"UPLOADING".equals(upload.getStatus())) {
            throw new IllegalStateException("上传任务状态无效: " + upload.getStatus());
        }

        // 保存分片
        try {
            byte[] chunkData = chunk.getBytes();
            String chunkHash = fileHashUtil.calculateHash(chunkData);

            FileChunk fileChunk = new FileChunk();
            fileChunk.setId(UUID.randomUUID().toString());
            fileChunk.setUploadId(uploadId);
            fileChunk.setChunkIndex(chunkIndex);
            fileChunk.setChunkSize(chunkData.length);
            fileChunk.setChunkHash(chunkHash);
            fileChunk.setCreatedAt(LocalDateTime.now());

            // 存储分片
            Path chunkPath = fileStorageService.storeChunk(uploadId, chunkIndex, chunkData);
            fileChunk.setStoragePath(chunkPath.toString());

            fileChunkRepository.save(fileChunk);

            // 更新上传进度
            upload.setUploadedChunks(upload.getUploadedChunks() + 1);
            upload.setUpdatedAt(LocalDateTime.now());

            // 检查是否所有分片上传完成
            if (upload.getUploadedChunks() >= upload.getTotalChunks()) {
                return completeUpload(upload);
            }

            fileUploadRepository.save(upload);

            return createUploadResponse(upload, false);

        } catch (IOException e) {
            logger.error("分片上传失败: uploadId={}, chunkIndex={}", uploadId, chunkIndex, e);
            throw new RuntimeException("分片上传失败", e);
        }
    }

    /**
     * 完成文件上传，合并分片
     */
    @Transactional
    public FileUploadResponseDTO completeUpload(FileUpload upload) {
        try {
            logger.info("合并文件分片: uploadId={}", upload.getId());

            // 获取所有分片
            List<FileChunk> chunks = fileChunkRepository.findByUploadIdOrderByChunkIndexAsc(upload.getId());

            // 合并分片
            Path mergedFile = fileStorageService.mergeChunks(upload.getStoragePath(), chunks);

            // 验证文件哈希
            String finalHash = fileHashUtil.calculateFileHash(mergedFile.toFile());
            if (!finalHash.equals(upload.getFileHash())) {
                logger.error("文件哈希验证失败: expected={}, actual={}", upload.getFileHash(), finalHash);
                throw new RuntimeException("文件校验失败");
            }

            // 更新上传状态
            upload.setStatus("COMPLETED");
            upload.setCompletedAt(LocalDateTime.now());
            upload.setUpdatedAt(LocalDateTime.now());
            fileUploadRepository.save(upload);

            // 清理分片
            cleanupChunks(upload.getId(), chunks);

            logger.info("文件上传完成: uploadId={}, fileSize={}", upload.getId(), upload.getFileSize());

            return createUploadResponse(upload, true);

        } catch (IOException e) {
            logger.error("文件合并失败: uploadId={}", upload.getId(), e);
            upload.setStatus("FAILED");
            upload.setUpdatedAt(LocalDateTime.now());
            fileUploadRepository.save(upload);
            throw new RuntimeException("文件合并失败", e);
        }
    }

    /**
     * 取消上传
     */
    @Transactional
    public void cancelUpload(String uploadId, Long userId) {
        FileUpload upload = fileUploadRepository.findById(uploadId)
                .orElseThrow(() -> new IllegalArgumentException("上传任务不存在: " + uploadId));

        if (!upload.getUserId().equals(userId)) {
            throw new SecurityException("无权访问此上传任务");
        }

        // 删除分片
        List<FileChunk> chunks = fileChunkRepository.findByUploadId(uploadId);
        cleanupChunks(uploadId, chunks);

        // 删除上传记录
        fileUploadRepository.delete(upload);

        logger.info("上传已取消: uploadId={}", uploadId);
    }

    /**
     * 查询上传进度
     */
    public FileUploadResponseDTO getUploadProgress(String uploadId, Long userId) {
        FileUpload upload = fileUploadRepository.findById(uploadId)
                .orElseThrow(() -> new IllegalArgumentException("上传任务不存在: " + uploadId));

        if (!upload.getUserId().equals(userId)) {
            throw new SecurityException("无权访问此上传任务");
        }

        return createUploadResponse(upload, "COMPLETED".equals(upload.getStatus()));
    }

    /**
     * 获取用户的上传历史
     */
    public List<FileUpload> getUserUploads(Long userId, int page, int size) {
        return fileUploadRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 清理过期上传
     */
    @Transactional
    public void cleanupExpiredUploads() {
        LocalDateTime expiryTime = LocalDateTime.now().minusDays(7);
        List<FileUpload> expiredUploads = fileUploadRepository.findByStatusAndUpdatedAtBefore("UPLOADING", expiryTime);

        for (FileUpload upload : expiredUploads) {
            try {
                List<FileChunk> chunks = fileChunkRepository.findByUploadId(upload.getId());
                cleanupChunks(upload.getId(), chunks);
                fileUploadRepository.delete(upload);
                logger.info("清理过期上传: uploadId={}", upload.getId());
            } catch (Exception e) {
                logger.error("清理过期上传失败: uploadId={}", upload.getId(), e);
            }
        }
    }

    // ===== 私有方法 =====

    private int calculateTotalChunks(long fileSize) {
        return (int) Math.ceil((double) fileSize / chunkSize);
    }

    private String generateStoragePath(String fileName) {
        String datePath = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String ext = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : "";
        return datePath + "/" + UUID.randomUUID().toString() + ext;
    }

    private FileUploadResponseDTO createInstantUploadResponse(FileUpload existingFile, FileUploadRequestDTO request) {
        FileUploadResponseDTO response = new FileUploadResponseDTO();
        response.setUploadId(existingFile.getId());
        response.setFileName(request.getFileName());
        response.setFileSize(existingFile.getFileSize());
        response.setStatus("COMPLETED");
        response.setInstantUpload(true);
        response.setCompleted(true);
        response.setFileUrl(fileStorageService.getFileUrl(existingFile.getStoragePath()));
        response.setProgress(100);
        return response;
    }

    private FileUploadResponseDTO createResumeResponse(FileUpload upload, FileUploadRequestDTO request) {
        List<Integer> uploadedChunkIndices = fileChunkRepository.findByUploadId(upload.getId())
                .stream()
                .map(FileChunk::getChunkIndex)
                .toList();

        FileUploadResponseDTO response = createUploadResponse(upload, false);
        response.setUploadedChunks(uploadedChunkIndices);
        response.setResumeUpload(true);
        return response;
    }

    private FileUploadResponseDTO createUploadResponse(FileUpload upload, boolean completed) {
        FileUploadResponseDTO response = new FileUploadResponseDTO();
        response.setUploadId(upload.getId());
        response.setFileName(upload.getFileName());
        response.setFileSize(upload.getFileSize());
        response.setStatus(upload.getStatus());
        response.setCompleted("COMPLETED".equals(upload.getStatus()) || completed);
        response.setProgress(calculateProgress(upload));
        response.setTotalChunks(upload.getTotalChunks());
        response.setChunkSize(upload.getChunkSize());

        if (completed) {
            response.setFileUrl(fileStorageService.getFileUrl(upload.getStoragePath()));
            response.setCompletedAt(upload.getCompletedAt());
        }

        return response;
    }

    private int calculateProgress(FileUpload upload) {
        if (upload.getTotalChunks() == 0) return 0;
        return (int) ((upload.getUploadedChunks() * 100.0) / upload.getTotalChunks());
    }

    private void cleanupChunks(String uploadId, List<FileChunk> chunks) {
        for (FileChunk chunk : chunks) {
            try {
                fileStorageService.deleteChunk(chunk.getStoragePath());
                fileChunkRepository.delete(chunk);
            } catch (Exception e) {
                logger.warn("清理分片失败: chunkId={}", chunk.getId(), e);
            }
        }
    }
}
