package com.im.server.controller;

import com.im.server.dto.ApiResponse;
import com.im.server.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * 上传图片
     */
    @PostMapping("/image")
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("文件不能为空"));
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("只能上传图片文件"));
        }
        
        // 验证文件大小 (最大 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("图片大小不能超过 10MB"));
        }
        
        try {
            String url = fileService.saveImage(file);
            return ResponseEntity.ok(ApiResponse.success(url));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("上传失败: " + e.getMessage()));
        }
    }

    /**
     * 上传文件
     */
    @PostMapping("/file")
    public ResponseEntity<ApiResponse<String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "file") String type,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("文件不能为空"));
        }
        
        // 验证文件大小 (最大 100MB)
        if (file.getSize() > 100 * 1024 * 1024) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("文件大小不能超过 100MB"));
        }
        
        try {
            String url = fileService.saveFile(file, type);
            return ResponseEntity.ok(ApiResponse.success(url));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("上传失败: " + e.getMessage()));
        }
    }

    /**
     * 上传语音
     */
    @PostMapping("/voice")
    public ResponseEntity<ApiResponse<String>> uploadVoice(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("文件不能为空"));
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("audio/mpeg") && 
                !contentType.equals("audio/wav") && 
                !contentType.equals("audio/ogg"))) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("只能上传音频文件"));
        }
        
        // 验证文件大小 (最大 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("音频大小不能超过 10MB"));
        }
        
        try {
            String url = fileService.saveVoice(file);
            return ResponseEntity.ok(ApiResponse.success(url));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("上传失败: " + e.getMessage()));
        }
    }

    /**
     * 上传群头像
     */
    @PostMapping("/group/avatar")
    public ResponseEntity<ApiResponse<String>> uploadGroupAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("groupId") Long groupId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("文件不能为空"));
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("只能上传图片文件"));
        }
        
        // 验证文件大小 (最大 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("图片大小不能超过 5MB"));
        }
        
        try {
            String url = fileService.saveGroupAvatar(file, groupId);
            return ResponseEntity.ok(ApiResponse.success(url));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("上传失败: " + e.getMessage()));
        }
    }

    /**
     * 上传用户头像
     */
    @PostMapping("/user/avatar")
    public ResponseEntity<ApiResponse<String>> uploadUserAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("文件不能为空"));
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("只能上传图片文件"));
        }
        
        // 验证文件大小 (最大 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("图片大小不能超过 5MB"));
        }
        
        try {
            String url = fileService.saveUserAvatar(file);
            return ResponseEntity.ok(ApiResponse.success(url));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("上传失败: " + e.getMessage()));
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        String fileUrl = request.get("url");
        if (fileUrl == null || fileUrl.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("文件URL不能为空"));
        }
        
        try {
            fileService.deleteFile(fileUrl);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("删除失败: " + e.getMessage()));
        }
    }
}
