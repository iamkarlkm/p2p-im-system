package com.im.service.message.controller;

import com.im.dto.VoiceMessageRequest;
import com.im.dto.VoiceMessageResponse;
import com.im.dto.VoiceTranscriptionResponse;
import com.im.service.VoiceMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * 语音消息控制器
 * 提供语音录制、播放、转文字等功能
 */
@Slf4j
@RestController
@RequestMapping("/api/chat/voice")
@RequiredArgsConstructor
public class VoiceMessageController {

    private final VoiceMessageService voiceMessageService;

    /**
     * 发送语音消息
     */
    @PostMapping("/send")
    public ResponseEntity<VoiceMessageResponse> sendVoice(
            @RequestHeader("X-User-Id") Long senderId,
            @Valid @RequestBody VoiceMessageRequest request) {
        log.info("用户 {} 发送语音消息给 {}，时长 {} 秒", 
                senderId, request.getReceiverId(), request.getDuration());
        VoiceMessageResponse response = voiceMessageService.sendVoiceMessage(senderId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 上传语音文件
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadVoice(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("duration") Integer duration) {
        log.info("用户 {} 上传语音文件，时长 {} 秒", userId, duration);
        String voiceUrl = voiceMessageService.uploadVoice(userId, file, duration);
        return ResponseEntity.ok(voiceUrl);
    }

    /**
     * 获取语音播放
     */
    @GetMapping("/play/{messageId}")
    public ResponseEntity<byte[]> playVoice(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long messageId) {
        byte[] voiceData = voiceMessageService.getVoiceData(userId, messageId);
        return ResponseEntity.ok()
                .header("Content-Type", "audio/mp3")
                .body(voiceData);
    }

    /**
     * 语音转文字
     */
    @PostMapping("/transcribe/{messageId}")
    public ResponseEntity<VoiceTranscriptionResponse> transcribeVoice(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long messageId) {
        log.info("用户 {} 请求语音转文字: messageId={}", userId, messageId);
        VoiceTranscriptionResponse response = voiceMessageService.transcribeVoice(userId, messageId);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取语音转文字结果
     */
    @GetMapping("/transcription/{messageId}")
    public ResponseEntity<VoiceTranscriptionResponse> getTranscription(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long messageId) {
        VoiceTranscriptionResponse response = voiceMessageService.getTranscription(userId, messageId);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除语音消息
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteVoiceMessage(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long messageId) {
        log.info("用户 {} 删除语音消息 {}", userId, messageId);
        voiceMessageService.deleteVoiceMessage(userId, messageId);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取聊天中的语音列表
     */
    @GetMapping("/list/{targetUserId}")
    public ResponseEntity<List<VoiceMessageResponse>> getVoiceList(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long targetUserId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<VoiceMessageResponse> voices = voiceMessageService.getVoiceList(userId, targetUserId, page, size);
        return ResponseEntity.ok(voices);
    }

    /**
     * 将语音标记为已播放
     */
    @PostMapping("/mark-played/{messageId}")
    public ResponseEntity<Void> markVoiceAsPlayed(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long messageId) {
        voiceMessageService.markVoiceAsPlayed(userId, messageId);
        return ResponseEntity.ok().build();
    }
}
