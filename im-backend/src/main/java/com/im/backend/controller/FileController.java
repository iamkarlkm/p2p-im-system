package com.im.backend.controller;

import com.im.backend.dto.FileInfoDTO;
import com.im.backend.dto.FileUploadRequest;
import com.im.backend.dto.FileUploadResponse;
import com.im.backend.dto.response.ApiResponse;
import com.im.backend.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 文件管理控制器
 * 功能#17: 文件上传下载
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPublic", defaultValue = "false") Boolean isPublic,
            HttpServletRequest request) {
        try {
            Long ownerId = getCurrentUserId(request);
            
            FileUploadRequest uploadRequest = new FileUploadRequest();
            uploadRequest.setFileName(file.getOriginalFilename());
            uploadRequest.setFileSize(file.getSize());
            uploadRequest.setMimeType(file.getContentType());
            uploadRequest.setIsPublic(isPublic);

            FileUploadResponse response = fileStorageService.uploadFile(file, uploadRequest, ownerId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 批量上传文件
     */
    @PostMapping("/upload/batch")
    public ResponseEntity<ApiResponse<List<FileUploadResponse>>> uploadFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "isPublic", defaultValue = "false") Boolean isPublic,
            HttpServletRequest request) {
        try {
            Long ownerId = getCurrentUserId(request);
            List<FileUploadResponse> responses = files.stream()
                .map(file -> {
                    try {
                        FileUploadRequest uploadRequest = new FileUploadRequest();
                        uploadRequest.setFileName(file.getOriginalFilename());
                        uploadRequest.setFileSize(file.getSize());
                        uploadRequest.setMimeType(file.getContentType());
                        uploadRequest.setIsPublic(isPublic);
                        return fileStorageService.uploadFile(file, uploadRequest, ownerId);
                    } catch (IOException e) {
                        throw new RuntimeException("Upload failed: " + file.getOriginalFilename());
                    }
                })
                .toList();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 下载文件
     */
    @GetMapping("/download/{storedName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String storedName) {
        try {
            Resource resource = fileStorageService.downloadFile(storedName);
            String contentType = "application/octet-stream";
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + storedName + "\"")
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 预览文件（内联显示）
     */
    @GetMapping("/preview/{storedName}")
    public ResponseEntity<Resource> previewFile(@PathVariable String storedName) {
        try {
            Resource resource = fileStorageService.downloadFile(storedName);
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取文件信息
     */
    @GetMapping("/info/{fileId}")
    public ResponseEntity<ApiResponse<FileInfoDTO>> getFileInfo(@PathVariable Long fileId) {
        try {
            FileInfoDTO fileInfo = fileStorageService.getFileInfo(fileId);
            return ResponseEntity.ok(ApiResponse.success(fileInfo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(404, e.getMessage()));
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @PathVariable Long fileId,
            HttpServletRequest request) {
        try {
            Long ownerId = getCurrentUserId(request);
            fileStorageService.deleteFile(fileId, ownerId);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 获取当前用户文件列表
     */
    @GetMapping("/my-files")
    public ResponseEntity<ApiResponse<Page<FileInfoDTO>>> getMyFiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        try {
            Long ownerId = getCurrentUserId(request);
            Pageable pageable = PageRequest.of(page, size);
            Page<FileInfoDTO> files = fileStorageService.getUserFiles(ownerId, pageable);
            return ResponseEntity.ok(ApiResponse.success(files));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 搜索文件
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<FileInfoDTO>>> searchFiles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        try {
            Long ownerId = getCurrentUserId(request);
            Pageable pageable = PageRequest.of(page, size);
            Page<FileInfoDTO> files = fileStorageService.searchFiles(keyword, ownerId, pageable);
            return ResponseEntity.ok(ApiResponse.success(files));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 获取公开文件列表
     */
    @GetMapping("/public")
    public ResponseEntity<ApiResponse<Page<FileInfoDTO>>> getPublicFiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<FileInfoDTO> files = fileStorageService.getPublicFiles(pageable);
            return ResponseEntity.ok(ApiResponse.success(files));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 获取用户存储空间使用情况
     */
    @GetMapping("/storage/usage")
    public ResponseEntity<ApiResponse<Long>> getStorageUsage(HttpServletRequest request) {
        try {
            Long ownerId = getCurrentUserId(request);
            Long usage = fileStorageService.getUserStorageUsage(ownerId);
            return ResponseEntity.ok(ApiResponse.success(usage));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        // Get from JWT token or session
        Object userId = request.getAttribute("userId");
        if (userId != null) {
            return Long.valueOf(userId.toString());
        }
        // Default for testing
        return 1L;
    }
}
