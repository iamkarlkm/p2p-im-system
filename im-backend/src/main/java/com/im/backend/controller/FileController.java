package com.im.backend.controller;

import com.im.backend.dto.FileUploadResponse;
import com.im.backend.entity.FileEntity;
import com.im.backend.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件控制器 - CDN/MinIO 文件服务 REST API
 * 提供文件上传、下载、预览、媒体库等接口
 */
@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileService fileService;

    // ==================== 文件上传 ====================

    /**
     * 上传文件
     * POST /api/v1/files/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(value = "conversationId", required = false) String conversationId) {

        try {
            FileUploadResponse response = fileService.uploadFile(file, userId, conversationId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取预签名上传URL (分片上传前获取地址)
     * GET /api/v1/files/presigned-upload
     */
    @GetMapping("/presigned-upload")
    public ResponseEntity<Map<String, String>> getPresignedUploadUrl(
            @RequestParam("fileName") String fileName,
            @RequestParam("contentType") String contentType) {
        try {
            String url = fileService.getPresignedUploadUrl(fileName, contentType);
            Map<String, String> result = new HashMap<>();
            result.put("uploadUrl", url);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("生成预签名URL失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==================== 文件访问 ====================

    /**
     * 获取文件下载URL
     * GET /api/v1/files/{fileId}/download
     */
    @GetMapping("/{fileId}/download")
    public ResponseEntity<Map<String, String>> getDownloadUrl(@PathVariable String fileId) {
        try {
            String downloadUrl = fileService.getDownloadUrl(fileId);
            Map<String, String> result = new HashMap<>();
            result.put("downloadUrl", downloadUrl);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取文件信息
     * GET /api/v1/files/{fileId}
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<FileEntity> getFileInfo(@PathVariable String fileId) {
        try {
            FileEntity file = fileService.getFileInfo(fileId);
            return ResponseEntity.ok(file);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== 文件列表 ====================

    /**
     * 获取用户文件列表
     * GET /api/v1/files/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<FileEntity>> getUserFiles(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<FileEntity> files = fileService.getUserFiles(userId, page, size);
        return ResponseEntity.ok(files);
    }

    /**
     * 获取会话媒体文件 (图片/视频/文件汇总)
     * GET /api/v1/files/conversation/{conversationId}/media
     */
    @GetMapping("/conversation/{conversationId}/media")
    public ResponseEntity<Page<FileEntity>> getConversationMedia(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<FileEntity> media = fileService.getConversationMedia(conversationId, page, size);
        return ResponseEntity.ok(media);
    }

    // ==================== 存储管理 ====================

    /**
     * 获取用户存储使用情况
     * GET /api/v1/files/storage/{userId}
     */
    @GetMapping("/storage/{userId}")
    public ResponseEntity<Map<String, Object>> getStorageInfo(@PathVariable String userId) {
        Map<String, Object> info = fileService.getStorageInfo(userId);
        return ResponseEntity.ok(info);
    }

    // ==================== 文件删除 ====================

    /**
     * 删除文件
     * DELETE /api/v1/files/{fileId}
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Map<String, String>> deleteFile(
            @PathVariable String fileId,
            @RequestHeader("X-User-Id") String userId) {
        try {
            fileService.deleteFile(fileId, userId);
            Map<String, String> result = new HashMap<>();
            result.put("message", "文件删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
