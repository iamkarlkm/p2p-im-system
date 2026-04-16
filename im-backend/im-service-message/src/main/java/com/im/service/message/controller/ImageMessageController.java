package com.im.service.message.controller;

import com.im.dto.ImageMessageRequest;
import com.im.dto.ImageMessageResponse;
import com.im.service.ImageMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * 图片消息控制器
 * 提供图片发送、接收、预览等功能
 */
@Slf4j
@RestController
@RequestMapping("/api/chat/image")
@RequiredArgsConstructor
public class ImageMessageController {

    private final ImageMessageService imageMessageService;

    /**
     * 发送图片消息（单张）
     */
    @PostMapping("/send")
    public ResponseEntity<ImageMessageResponse> sendImage(
            @RequestHeader("X-User-Id") Long senderId,
            @Valid @RequestBody ImageMessageRequest request) {
        log.info("用户 {} 发送图片消息给 {}", senderId, request.getReceiverId());
        ImageMessageResponse response = imageMessageService.sendImageMessage(senderId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 批量发送图片
     */
    @PostMapping("/send-batch")
    public ResponseEntity<List<ImageMessageResponse>> sendImages(
            @RequestHeader("X-User-Id") Long senderId,
            @RequestParam Long receiverId,
            @RequestParam List<String> imageUrls) {
        log.info("用户 {} 批量发送 {} 张图片给 {}", senderId, imageUrls.size(), receiverId);
        List<ImageMessageResponse> responses = imageMessageService.sendBatchImages(senderId, receiverId, imageUrls);
        return ResponseEntity.ok(responses);
    }

    /**
     * 上传图片文件
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam("file") MultipartFile file) {
        log.info("用户 {} 上传图片: {}", userId, file.getOriginalFilename());
        String imageUrl = imageMessageService.uploadImage(userId, file);
        return ResponseEntity.ok(imageUrl);
    }

    /**
     * 获取图片预览
     */
    @GetMapping("/preview/{messageId}")
    public ResponseEntity<ImageMessageResponse> getImagePreview(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long messageId) {
        ImageMessageResponse response = imageMessageService.getImagePreview(userId, messageId);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取原图下载链接
     */
    @GetMapping("/download/{messageId}")
    public ResponseEntity<String> getImageDownloadUrl(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long messageId) {
        String downloadUrl = imageMessageService.getImageDownloadUrl(userId, messageId);
        return ResponseEntity.ok(downloadUrl);
    }

    /**
     * 获取缩略图
     */
    @GetMapping("/thumbnail/{messageId}")
    public ResponseEntity<byte[]> getThumbnail(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long messageId,
            @RequestParam(defaultValue = "200") Integer width) {
        byte[] thumbnail = imageMessageService.getThumbnail(userId, messageId, width);
        return ResponseEntity.ok(thumbnail);
    }

    /**
     * 删除图片消息
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteImageMessage(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long messageId) {
        log.info("用户 {} 删除图片消息 {}", userId, messageId);
        imageMessageService.deleteImageMessage(userId, messageId);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取聊天中的图片列表
     */
    @GetMapping("/list/{targetUserId}")
    public ResponseEntity<List<ImageMessageResponse>> getImageList(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long targetUserId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<ImageMessageResponse> images = imageMessageService.getImageList(userId, targetUserId, page, size);
        return ResponseEntity.ok(images);
    }
}
