package com.im.backend.controller;

import com.im.backend.dto.FileUploadRequestDTO;
import com.im.backend.dto.FileUploadResponseDTO;
import com.im.backend.model.FileUpload;
import com.im.backend.service.ChunkedUploadService;
import com.im.backend.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private ChunkedUploadService chunkedUploadService;

    /**
     * 初始化上传 - 检查秒传和断点续传
     */
    @PostMapping("/upload/init")
    public ResponseEntity<FileUploadResponseDTO> initUpload(
            @RequestBody FileUploadRequestDTO request,
            @RequestHeader("X-User-Id") Long userId) {
        logger.info("初始化文件上传: fileName={}, fileSize={}, userId={}", 
                request.getFileName(), request.getFileSize(), userId);
        
        FileUploadResponseDTO response = fileUploadService.initUpload(request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 上传单个分片
     */
    @PostMapping("/upload/chunk")
    public ResponseEntity<FileUploadResponseDTO> uploadChunk(
            @RequestParam("uploadId") String uploadId,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("chunk") MultipartFile chunk,
            @RequestHeader("X-User-Id") Long userId) {
        logger.debug("上传分片: uploadId={}, chunkIndex={}, size={}", 
                uploadId, chunkIndex, chunk.getSize());
        
        FileUploadResponseDTO response = fileUploadService.uploadChunk(uploadId, chunkIndex, chunk, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 批量上传分片
     */
    @PostMapping("/upload/chunks")
    public ResponseEntity<List<FileUploadResponseDTO>> uploadChunks(
            @RequestParam("uploadId") String uploadId,
            @RequestParam("chunkIndices") List<Integer> chunkIndices,
            @RequestParam("chunks") List<MultipartFile> chunks,
            @RequestHeader("X-User-Id") Long userId) {
        logger.info("批量上传分片: uploadId={}, count={}", uploadId, chunks.size());
        
        List<FileUploadResponseDTO> responses = chunkedUploadService.uploadChunks(
                uploadId, chunks, chunkIndices, userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 获取上传进度
     */
    @GetMapping("/upload/{uploadId}/progress")
    public ResponseEntity<FileUploadResponseDTO> getUploadProgress(
            @PathVariable String uploadId,
            @RequestHeader("X-User-Id") Long userId) {
        FileUploadResponseDTO response = fileUploadService.getUploadProgress(uploadId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取缺失的分片索引
     */
    @GetMapping("/upload/{uploadId}/missing-chunks")
    public ResponseEntity<List<Integer>> getMissingChunks(
            @PathVariable String uploadId,
            @RequestHeader("X-User-Id") Long userId) {
        List<Integer> missingChunks = chunkedUploadService.getMissingChunks(uploadId, userId);
        return ResponseEntity.ok(missingChunks);
    }

    /**
     * 取消上传
     */
    @DeleteMapping("/upload/{uploadId}")
    public ResponseEntity<Map<String, String>> cancelUpload(
            @PathVariable String uploadId,
            @RequestHeader("X-User-Id") Long userId) {
        fileUploadService.cancelUpload(uploadId, userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "上传已取消");
        response.put("uploadId", uploadId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 暂停上传
     */
    @PostMapping("/upload/{uploadId}/pause")
    public ResponseEntity<Map<String, String>> pauseUpload(
            @PathVariable String uploadId,
            @RequestHeader("X-User-Id") Long userId) {
        chunkedUploadService.pauseUpload(uploadId, userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "上传已暂停");
        response.put("uploadId", uploadId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 恢复上传
     */
    @PostMapping("/upload/{uploadId}/resume")
    public ResponseEntity<FileUploadResponseDTO> resumeUpload(
            @PathVariable String uploadId,
            @RequestHeader("X-User-Id") Long userId) {
        FileUploadResponseDTO response = chunkedUploadService.resumeUpload(uploadId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 重试分片
     */
    @PostMapping("/upload/{uploadId}/retry/{chunkIndex}")
    public ResponseEntity<FileUploadResponseDTO> retryChunk(
            @PathVariable String uploadId,
            @PathVariable int chunkIndex,
            @RequestParam("chunk") MultipartFile chunk,
            @RequestHeader("X-User-Id") Long userId) {
        FileUploadResponseDTO response = chunkedUploadService.retryChunk(uploadId, chunkIndex, chunk, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户上传历史
     */
    @GetMapping("/uploads")
    public ResponseEntity<List<FileUpload>> getUserUploads(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<FileUpload> uploads = fileUploadService.getUserUploads(userId, page, size);
        return ResponseEntity.ok(uploads);
    }

    /**
     * 检查秒传
     */
    @PostMapping("/upload/check-instant")
    public ResponseEntity<Map<String, Object>> checkInstantUpload(
            @RequestBody FileUploadRequestDTO request,
            @RequestHeader("X-User-Id") Long userId) {
        FileUploadResponseDTO uploadResult = fileUploadService.initUpload(request, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("instantUpload", uploadResult.isInstantUpload());
        response.put("uploadId", uploadResult.getUploadId());
        response.put("completed", uploadResult.isCompleted());
        
        if (uploadResult.isCompleted()) {
            response.put("fileUrl", uploadResult.getFileUrl());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "file-upload");
        return ResponseEntity.ok(response);
    }
}
