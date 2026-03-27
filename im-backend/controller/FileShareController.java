package com.im.backend.controller;

import com.im.backend.dto.FileShareDTO;
import com.im.backend.model.FileMetadata;
import com.im.backend.service.FileShareService;
import com.im.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;

/**
 * 文件分享API控制器
 */
@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "文件分享", description = "文件上传下载分享相关接口")
@SecurityRequirement(name = "bearerAuth")
public class FileShareController {

    private static final Logger logger = LoggerFactory.getLogger(FileShareController.class);

    @Autowired
    private FileShareService fileShareService;

    @Autowired
    private AuthService authService;

    /**
     * 上传文件
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传文件", description = "上传文件到服务器")
    public ResponseEntity<FileShareDTO> uploadFile(
            @Parameter(description = "文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "文件描述") @RequestParam(required = false) String description,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        logger.info("File upload request from user {}", userId);

        FileShareDTO result = fileShareService.uploadFile(file, userId, description);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 下载文件
     */
    @GetMapping("/download/{fileId}")
    @Operation(summary = "下载文件", description = "下载指定ID的文件")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "文件ID") @PathVariable String fileId,
            @RequestHeader("Authorization") String token,
            HttpServletRequest request) throws IOException {

        Long userId = authService.getUserIdFromToken(token);
        FileShareDTO result = fileShareService.downloadFile(fileId, userId);

        if (!result.isSuccess()) {
            return ResponseEntity.notFound().build();
        }

        // Load file as Resource
        Resource resource = loadFileAsResource(result.getStoragePath());

        // Determine content type
        String contentType = result.getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + result.getOriginalName() + "\"")
            .body(resource);
    }

    /**
     * 获取文件信息
     */
    @GetMapping("/info/{fileId}")
    @Operation(summary = "获取文件信息", description = "获取文件的元数据信息")
    public ResponseEntity<FileShareDTO> getFileInfo(
            @Parameter(description = "文件ID") @PathVariable String fileId,
            @RequestHeader("Authorization") String token) {

        authService.validateToken(token);
        FileShareDTO result = fileShareService.getFileInfo(fileId);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取用户文件列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取文件列表", description = "获取当前用户的文件列表")
    public ResponseEntity<Page<FileMetadata>> getUserFiles(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        Page<FileMetadata> files = fileShareService.getUserFiles(userId, page, size);

        return ResponseEntity.ok(files);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{fileId}")
    @Operation(summary = "删除文件", description = "删除指定的文件")
    public ResponseEntity<FileShareDTO> deleteFile(
            @Parameter(description = "文件ID") @PathVariable String fileId,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        FileShareDTO result = fileShareService.deleteFile(fileId, userId);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 更新文件描述
     */
    @PutMapping("/{fileId}/description")
    @Operation(summary = "更新文件描述", description = "更新文件的描述信息")
    public ResponseEntity<FileShareDTO> updateFileDescription(
            @Parameter(description = "文件ID") @PathVariable String fileId,
            @RequestBody @Valid UpdateDescriptionRequest request,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        FileShareDTO result = fileShareService.updateFileDescription(
            fileId, userId, request.getDescription());

        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 清理过期文件（仅管理员）
     */
    @PostMapping("/admin/cleanup")
    @Operation(summary = "清理过期文件", description = "清理已过期的文件（仅管理员可用）")
    public ResponseEntity<String> cleanupExpiredFiles(
            @RequestHeader("Authorization") String token) {

        if (!authService.isAdmin(token)) {
            return ResponseEntity.status(403).body("需要管理员权限");
        }

        int deletedCount = fileShareService.cleanupExpiredFiles();
        return ResponseEntity.ok("已清理 " + deletedCount + " 个过期文件");
    }

    // ========== 辅助方法 ==========

    private Resource loadFileAsResource(String storagePath) throws IOException {
        // Implementation depends on FileStorageService
        return null;
    }

    // ========== 内部请求类 ==========

    public static class UpdateDescriptionRequest {
        @NotBlank(message = "描述不能为空")
        private String description;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
