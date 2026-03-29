package com.im.backend.service;

import com.im.backend.dto.FileUploadResponseDTO;
import com.im.backend.model.FileUpload;
import com.im.backend.repository.FileChunkRepository;
import com.im.backend.repository.FileUploadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChunkedUploadService {

    private static final Logger logger = LoggerFactory.getLogger(ChunkedUploadService.class);

    @Value("${file.upload.chunk-size:5242880}")
    private int defaultChunkSize;

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private FileChunkRepository fileChunkRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileUploadService fileUploadService;

    // 内存缓存，记录已上传的分片索引
    private final ConcurrentHashMap<String, List<Integer>> chunkCache = new ConcurrentHashMap<>();

    /**
     * 检查分片是否存在
     */
    public boolean chunkExists(String uploadId, int chunkIndex) {
        // 检查内存缓存
        List<Integer> cached = chunkCache.get(uploadId);
        if (cached != null && cached.contains(chunkIndex)) {
            return true;
        }

        // 检查数据库
        return fileChunkRepository.existsByUploadIdAndChunkIndex(uploadId, chunkIndex);
    }

    /**
     * 批量上传分片
     */
    public List<FileUploadResponseDTO> uploadChunks(String uploadId, List<MultipartFile> chunks, 
                                                     List<Integer> chunkIndices, Long userId) {
        List<FileUploadResponseDTO> responses = new ArrayList<>();

        if (chunks.size() != chunkIndices.size()) {
            throw new IllegalArgumentException("分片数量和索引数量不匹配");
        }

        FileUpload upload = fileUploadRepository.findById(uploadId)
                .orElseThrow(() -> new IllegalArgumentException("上传任务不存在: " + uploadId));

        if (!upload.getUserId().equals(userId)) {
            throw new SecurityException("无权访问此上传任务");
        }

        // 并发上传分片
        for (int i = 0; i < chunks.size(); i++) {
            try {
                FileUploadResponseDTO response = fileUploadService.uploadChunk(
                        uploadId, chunkIndices.get(i), chunks.get(i), userId);
                responses.add(response);

                // 更新缓存
                chunkCache.computeIfAbsent(uploadId, k -> new ArrayList<>()).add(chunkIndices.get(i));

            } catch (Exception e) {
                logger.error("分片上传失败: uploadId={}, chunkIndex={}", uploadId, chunkIndices.get(i), e);
                throw new RuntimeException("分片上传失败", e);
            }
        }

        // 如果上传完成，清理缓存
        FileUpload updatedUpload = fileUploadRepository.findById(uploadId).orElse(null);
        if (updatedUpload != null && "COMPLETED".equals(updatedUpload.getStatus())) {
            chunkCache.remove(uploadId);
        }

        return responses;
    }

    /**
     * 获取缺失的分片索引
     */
    public List<Integer> getMissingChunks(String uploadId, Long userId) {
        FileUpload upload = fileUploadRepository.findById(uploadId)
                .orElseThrow(() -> new IllegalArgumentException("上传任务不存在: " + uploadId));

        if (!upload.getUserId().equals(userId)) {
            throw new SecurityException("无权访问此上传任务");
        }

        List<Integer> uploadedChunks = fileChunkRepository.findByUploadId(uploadId)
                .stream()
                .map(com.im.backend.model.FileChunk::getChunkIndex)
                .toList();

        List<Integer> missingChunks = new ArrayList<>();
        for (int i = 0; i < upload.getTotalChunks(); i++) {
            if (!uploadedChunks.contains(i)) {
                missingChunks.add(i);
            }
        }

        return missingChunks;
    }

    /**
     * 预分配分片上传任务
     */
    public FileUpload preallocateUpload(String fileName, String fileHash, long fileSize, 
                                         String mimeType, Long userId) {
        int totalChunks = (int) Math.ceil((double) fileSize / defaultChunkSize);

        FileUpload upload = new FileUpload();
        upload.setId(java.util.UUID.randomUUID().toString());
        upload.setFileName(fileName);
        upload.setFileHash(fileHash);
        upload.setFileSize(fileSize);
        upload.setMimeType(mimeType);
        upload.setChunkSize(defaultChunkSize);
        upload.setTotalChunks(totalChunks);
        upload.setUploadedChunks(0);
        upload.setUserId(userId);
        upload.setStatus("UPLOADING");
        upload.setCreatedAt(LocalDateTime.now());
        upload.setUpdatedAt(LocalDateTime.now());
        upload.setStoragePath(generateStoragePath(fileName));

        return fileUploadRepository.save(upload);
    }

    /**
     * 重试失败的分片
     */
    public FileUploadResponseDTO retryChunk(String uploadId, int chunkIndex, MultipartFile chunk, Long userId) {
        // 删除旧分片
        Optional<com.im.backend.model.FileChunk> existingChunk = 
                fileChunkRepository.findByUploadIdAndChunkIndex(uploadId, chunkIndex);

        existingChunk.ifPresent(oldChunk -> {
            try {
                fileStorageService.deleteChunk(oldChunk.getStoragePath());
                fileChunkRepository.delete(oldChunk);

                // 更新上传记录
                FileUpload upload = fileUploadRepository.findById(uploadId).orElse(null);
                if (upload != null) {
                    upload.setUploadedChunks(upload.getUploadedChunks() - 1);
                    upload.setUpdatedAt(LocalDateTime.now());
                    fileUploadRepository.save(upload);
                }
            } catch (IOException e) {
                logger.warn("删除旧分片失败: chunkId={}", oldChunk.getId(), e);
            }
        });

        // 重新上传
        return fileUploadService.uploadChunk(uploadId, chunkIndex, chunk, userId);
    }

    /**
     * 暂停上传
     */
    public void pauseUpload(String uploadId, Long userId) {
        FileUpload upload = fileUploadRepository.findById(uploadId)
                .orElseThrow(() -> new IllegalArgumentException("上传任务不存在: " + uploadId));

        if (!upload.getUserId().equals(userId)) {
            throw new SecurityException("无权访问此上传任务");
        }

        upload.setStatus("PAUSED");
        upload.setUpdatedAt(LocalDateTime.now());
        fileUploadRepository.save(upload);

        logger.info("上传已暂停: uploadId={}", uploadId);
    }

    /**
     * 恢复上传
     */
    public FileUploadResponseDTO resumeUpload(String uploadId, Long userId) {
        FileUpload upload = fileUploadRepository.findById(uploadId)
                .orElseThrow(() -> new IllegalArgumentException("上传任务不存在: " + uploadId));

        if (!upload.getUserId().equals(userId)) {
            throw new SecurityException("无权访问此上传任务");
        }

        if (!"PAUSED".equals(upload.getStatus())) {
            throw new IllegalStateException("上传任务状态无效: " + upload.getStatus());
        }

        upload.setStatus("UPLOADING");
        upload.setUpdatedAt(LocalDateTime.now());
        fileUploadRepository.save(upload);

        // 获取已上传的分片列表
        List<Integer> uploadedChunks = fileChunkRepository.findByUploadId(uploadId)
                .stream()
                .map(com.im.backend.model.FileChunk::getChunkIndex)
                .toList();

        FileUploadResponseDTO response = new FileUploadResponseDTO();
        response.setUploadId(upload.getId());
        response.setFileName(upload.getFileName());
        response.setFileSize(upload.getFileSize());
        response.setStatus(upload.getStatus());
        response.setUploadedChunks(uploadedChunks);
        response.setResumeUpload(true);
        response.setTotalChunks(upload.getTotalChunks());

        logger.info("上传已恢复: uploadId={}", uploadId);
        return response;
    }

    /**
     * 优化上传策略
     */
    public int getOptimalChunkSize(long fileSize) {
        // 根据文件大小动态调整分片大小
        if (fileSize < 10 * 1024 * 1024) { // < 10MB
            return 1 * 1024 * 1024; // 1MB
        } else if (fileSize < 100 * 1024 * 1024) { // < 100MB
            return 5 * 1024 * 1024; // 5MB
        } else if (fileSize < 1024 * 1024 * 1024) { // < 1GB
            return 10 * 1024 * 1024; // 10MB
        } else {
            return 50 * 1024 * 1024; // 50MB
        }
    }

    private String generateStoragePath(String fileName) {
        String datePath = LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String ext = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : "";
        return datePath + "/" + java.util.UUID.randomUUID().toString() + ext;
    }
}
