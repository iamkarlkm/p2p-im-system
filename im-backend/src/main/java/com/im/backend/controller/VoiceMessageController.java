package com.im.backend.controller;

import com.im.backend.dto.VoiceMessageDTO;
import com.im.backend.dto.VoiceSendRequest;
import com.im.backend.dto.VoiceUploadRequest;
import com.im.backend.model.entity.User;
import com.im.backend.security.CurrentUser;
import com.im.backend.service.VoiceMessageService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 语音消息控制器
 * 提供语音上传、下载、发送等API接口
 */
@RestController
@RequestMapping("/api/voice")
public class VoiceMessageController {
    
    private static final Logger logger = LoggerFactory.getLogger(VoiceMessageController.class);
    
    @Autowired
    private VoiceMessageService voiceMessageService;
    
    /**
     * 上传语音文件
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadVoice(
            @CurrentUser User user,
            @RequestParam("file") MultipartFile file,
            @Valid VoiceUploadRequest request) {
        
        logger.info("上传语音文件: userId={}, duration={}", user.getId(), request.getDuration());
        String fileId = voiceMessageService.uploadVoiceFile(user.getId(), file, request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("fileId", fileId);
        response.put("message", "语音文件上传成功");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 下载语音文件
     */
    @GetMapping("/download/{voiceId}")
    public ResponseEntity<Resource> downloadVoice(@PathVariable String voiceId) {
        logger.info("下载语音文件: voiceId={}", voiceId);
        Resource resource = voiceMessageService.downloadVoiceFile(voiceId);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + voiceId + ".mp3\"")
                .body(resource);
    }
    
    /**
     * 发送语音消息
     */
    @PostMapping("/message/send")
    public ResponseEntity<?> sendVoiceMessage(
            @CurrentUser User user,
            @Valid @RequestBody VoiceSendRequest request) {
        
        logger.info("发送语音消息: senderId={}, receiverId={}", user.getId(), request.getReceiverId());
        VoiceMessageDTO message = voiceMessageService.sendVoiceMessage(user.getId(), request);
        
        return ResponseEntity.ok(message);
    }
    
    /**
     * 获取语音消息详情
     */
    @GetMapping("/message/{messageId}")
    public ResponseEntity<?> getVoiceMessage(@PathVariable String messageId) {
        logger.info("获取语音消息详情: messageId={}", messageId);
        VoiceMessageDTO message = voiceMessageService.getVoiceMessage(messageId);
        return ResponseEntity.ok(message);
    }
    
    /**
     * 标记语音消息为已读
     */
    @PostMapping("/message/{messageId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String messageId) {
        logger.info("标记语音消息已读: messageId={}", messageId);
        voiceMessageService.markAsRead(messageId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "已标记为已读");
        return ResponseEntity.ok(response);
    }
    
    /**
     * 记录语音播放
     */
    @PostMapping("/message/{messageId}/play")
    public ResponseEntity<?> recordPlay(@PathVariable String messageId) {
        logger.info("记录语音播放: messageId={}", messageId);
        voiceMessageService.recordPlay(messageId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "播放记录已更新");
        return ResponseEntity.ok(response);
    }
    
    /**
     * 语音转文字
     */
    @PostMapping("/convert/text/{voiceId}")
    public ResponseEntity<?> convertToText(@PathVariable String voiceId) {
        logger.info("语音转文字: voiceId={}", voiceId);
        String text = voiceMessageService.convertToText(voiceId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("text", text);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取语音时长
     */
    @GetMapping("/duration/{voiceId}")
    public ResponseEntity<?> getVoiceDuration(@PathVariable String voiceId) {
        Integer duration = voiceMessageService.getVoiceDuration(voiceId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("duration", duration);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取我的语音消息列表
     */
    @GetMapping("/my/list")
    public ResponseEntity<?> getMyVoiceMessages(@CurrentUser User user, Pageable pageable) {
        Page<VoiceMessageDTO> messages = voiceMessageService.getMyVoiceMessages(user.getId(), pageable);
        return ResponseEntity.ok(messages);
    }
    
    /**
     * 获取未读语音消息数量
     */
    @GetMapping("/unread/count")
    public ResponseEntity<?> getUnreadCount(@CurrentUser User user) {
        long count = voiceMessageService.getUnreadCount(user.getId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 删除语音文件
     */
    @DeleteMapping("/{voiceId}")
    public ResponseEntity<?> deleteVoiceFile(@CurrentUser User user, @PathVariable String voiceId) {
        logger.info("删除语音文件: userId={}, voiceId={}", user.getId(), voiceId);
        voiceMessageService.deleteVoiceFile(voiceId, user.getId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "语音文件删除成功");
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取会话的语音消息历史
     */
    @GetMapping("/conversation/{userId}")
    public ResponseEntity<?> getConversationVoiceMessages(
            @CurrentUser User user,
            @PathVariable Long userId) {
        
        List<VoiceMessageDTO> messages = voiceMessageService
            .getConversationVoiceMessages(user.getId(), userId);
        return ResponseEntity.ok(messages);
    }
}
