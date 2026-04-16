package com.im.service.storage.controller;

import com.im.service.storage.dto.FileResponse;
import com.im.service.storage.dto.UploadRequest;
import com.im.service.storage.service.StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件存储控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @Valid @RequestBody UploadRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        
        log.info("Upload request from user: {}, fileName: {}", userId, request.getOriginalName());
        
        FileResponse response = storageService.uploadFile(request, userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "文件上传成功");
        result.put("data", response);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * 获取文件信息
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<Map<String, Object>> getFileInfo(
            @PathVariable String fileId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        return storageService.getFileResponse(fileId)
                .map(response -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("data", response);
                    return ResponseEntity.ok(result);
                })
                .orElseGet(() -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", false);
                    result.put("message", "文件不存在");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
                });
    }

    /**
     * 下载文件
     */
    @GetMapping("/{fileId}/download")
    public ResponseEntity<Map<String, Object>> downloadFile(
            @PathVariable String fileId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        return storageService.downloadFile(fileId)
                .map(content -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("data", content);
                    return ResponseEntity.ok(result);
                })
                .orElseGet(() -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", false);
                    result.put("message", "文件不存在或无权限下载");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
                });
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Map<String, Object>> deleteFile(
            @PathVariable String fileId,
            @RequestHeader("X-User-Id") Long userId) {
        
        try {
            boolean deleted = storageService.deleteFile(fileId, userId);
            Map<String, Object> result = new HashMap<>();
            if (deleted) {
                result.put("success", true);
                result.put("message", "文件删除成功");
            } else {
                result.put("success", false);
                result.put("message", "文件不存在");
            }
            return ResponseEntity.ok(result);
        } catch (SecurityException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }
    }

    /**
     * 批量删除文件
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchDeleteFiles(
            @RequestBody List<String> fileIds,
            @RequestHeader("X-User-Id") Long userId) {
        
        int count = storageService.batchDeleteFiles(fileIds, userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "成功删除 " + count + " 个文件");
        result.put("deletedCount", count);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取用户文件列表
     */
    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyFiles(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<FileResponse> filePage = storageService.getUserFilesPage(userId, pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", filePage.getContent());
        result.put("totalElements", filePage.getTotalElements());
        result.put("totalPages", filePage.getTotalPages());
        result.put("currentPage", filePage.getNumber());
        result.put("pageSize", filePage.getSize());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 搜索文件
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchFiles(
            @RequestParam String keyword,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<FileResponse> filePage = storageService.searchFilesPage(keyword, userId, pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", filePage.getContent());
        result.put("totalElements", filePage.getTotalElements());
        result.put("totalPages", filePage.getTotalPages());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 根据类型获取文件
     */
    @GetMapping("/type/{fileType}")
    public ResponseEntity<Map<String, Object>> getFilesByType(
            @PathVariable String fileType,
            @RequestHeader("X-User-Id") Long userId) {
        
        List<FileResponse> files = storageService.getFilesByType(fileType, userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", files);
        result.put("count", files.size());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 更新文件信息
     */
    @PutMapping("/{fileId}")
    public ResponseEntity<Map<String, Object>> updateFileInfo(
            @PathVariable String fileId,
            @RequestBody Map<String, Object> updateRequest,
            @RequestHeader("X-User-Id") Long userId) {
        
        String description = (String) updateRequest.get("description");
        Boolean isPublic = updateRequest.get("isPublic") != null ? 
                (Boolean) updateRequest.get("isPublic") : null;
        
        return storageService.updateFileInfo(fileId, description, isPublic, userId)
                .map(response -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("message", "文件信息更新成功");
                    result.put("data", response);
                    return ResponseEntity.ok(result);
                })
                .orElseGet(() -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", false);
                    result.put("message", "文件不存在或无权限更新");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
                });
    }

    /**
     * 获取用户存储使用统计
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics(
            @RequestHeader("X-User-Id") Long userId) {
        
        StorageService.FileStatistics stats = storageService.getUserStatistics(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", stats);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取热门文件
     */
    @GetMapping("/hot")
    public ResponseEntity<Map<String, Object>> getHotFiles(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<FileResponse> files = storageService.getHotFiles(limit);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", files);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取最近上传的文件
     */
    @GetMapping("/recent")
    public ResponseEntity<Map<String, Object>> getRecentFiles(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<FileResponse> files = storageService.getRecentFiles(limit);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", files);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 检查文件是否存在
     */
    @GetMapping("/{fileId}/exists")
    public ResponseEntity<Map<String, Object>> checkFileExists(
            @PathVariable String fileId) {
        
        boolean exists = storageService.existsByFileId(fileId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("exists", exists);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 清理过期文件（管理员接口）
     */
    @PostMapping("/admin/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupExpiredFiles() {
        
        int count = storageService.cleanupExpiredFiles();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "清理了 " + count + " 个过期文件");
        result.put("cleanedCount", count);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "storage");
        return ResponseEntity.ok(result);
    }
}
