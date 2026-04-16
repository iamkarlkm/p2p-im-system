package com.im.service.message.controller;

import com.im.dto.FileMessageRequest;
import com.im.dto.FileMessageResponse;
import com.im.dto.FileUploadRequest;
import com.im.service.FileTransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * 文件传输控制器
 * 提供文件发送、接收、断点续传等功能
 */
@Slf4j
@RestController
@RequestMapping("/api/chat/file")
@RequiredArgsConstructor
public class FileTransferController {

    private final FileTransferService fileTransferService;

    /**
     * 初始化文件上传（获取上传ID）
     */
    @PostMapping("/init-upload")
    public ResponseEntity<FileMessageResponse> initFileUpload(
            @RequestHeader("X-User-Id") Long senderId,
            @Valid @RequestBody FileUploadRequest request) {
        log.info("用户 {} 初始化文件上传: {}", senderId, request.getFileName());
        FileMessageResponse response = fileTransferService.initFileUpload(senderId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 分片上传文件
     */
    @PostMapping("/upload-chunk")
    public ResponseEntity<FileMessageResponse> uploadChunk(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam("uploadId") String uploadId,
            @RequestParam("chunkIndex") Integer chunkIndex,
            @RequestParam("file") MultipartFile chunk) {
        log.info("用户 {} 上传分片: uploadId={}, chunkIndex={}", userId, uploadId, chunkIndex);
        FileMessageResponse response = fileTransferService.uploadChunk(userId, uploadId, chunkIndex, chunk);
        return ResponseEntity.ok(response);
    }

    /**
     * 完成文件上传（合并分片）
     */
    @PostMapping("/complete-upload/{uploadId}")
    public ResponseEntity<FileMessageResponse> completeUpload(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String uploadId) {
        log.info("用户 {} 完成文件上传: uploadId={}", userId, uploadId);
        FileMessageResponse response = fileTransferService.completeUpload(userId, uploadId);
        return ResponseEntity.ok(response);
    }

    /**
     * 发送文件消息
     */
    @PostMapping("/send")
    public ResponseEntity<FileMessageResponse> sendFile(
            @RequestHeader("X-User-Id") Long senderId,
            @Valid @RequestBody FileMessageRequest request) {
        log.info("用户 {} 发送文件消息给 {}", senderId, request.getReceiverId());
        FileMessageResponse response = fileTransferService.sendFileMessage(senderId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 下载文件（支持断点续传）
     */
    @GetMapping("/download/{messageId}")
    public void downloadFile(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long messageId,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        log.info("用户 {} 下载文件: messageId={}", userId, messageId);
        fileTransferService.downloadFile(userId, messageId, request, response);
    }

    /**
     * 获取文件下载链接
     */
    @GetMapping("/download-url/{messageId}")
    public ResponseEntity<String> getDownloadUrl(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long messageId) {
        String downloadUrl = fileTransferService.getDownloadUrl(userId, messageId);
        return ResponseEntity.ok(downloadUrl);
    }

    /**
     * 获取文件信息
     */
    @GetMapping("/info/{messageId}")
    public ResponseEntity<FileMessageResponse> getFileInfo(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long messageId) {
        FileMessageResponse response = fileTransferService.getFileInfo(userId, messageId);
        return ResponseEntity.ok(response);
    }

    /**
     * 取消文件上传
     */
    @PostMapping("/cancel-upload/{uploadId}")
    public ResponseEntity<Void> cancelUpload(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String uploadId) {
        log.info("用户 {} 取消文件上传: uploadId={}", userId, uploadId);
        fileTransferService.cancelUpload(userId, uploadId);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除文件消息
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteFileMessage(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long messageId) {
        log.info("用户 {} 删除文件消息 {}", userId, messageId);
        fileTransferService.deleteFileMessage(userId, messageId);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取聊天中的文件列表
     */
    @GetMapping("/list/{targetUserId}")
    public ResponseEntity<List<FileMessageResponse>> getFileList(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long targetUserId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<FileMessageResponse> files = fileTransferService.getFileList(userId, targetUserId, page, size);
        return ResponseEntity.ok(files);
    }

    /**
     * 检查文件是否已上传（秒传）
     */
    @PostMapping("/check-exists")
    public ResponseEntity<FileMessageResponse> checkFileExists(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam("fileHash") String fileHash,
            @RequestParam("fileSize") Long fileSize) {
        FileMessageResponse response = fileTransferService.checkFileExists(userId, fileHash, fileSize);
        return ResponseEntity.ok(response);
    }
}
