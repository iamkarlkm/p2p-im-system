package com.im.backend.controller;

import com.im.backend.dto.TypingRequest;
import com.im.backend.dto.TypingStatusDTO;
import com.im.backend.service.TypingStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/typing")
@RequiredArgsConstructor
public class TypingStatusController {

    private final TypingStatusService typingStatusService;

    /**
     * POST /api/v1/typing/start - 用户开始输入
     */
    @PostMapping("/start")
    public ResponseEntity<Void> startTyping(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody TypingRequest request) {
        typingStatusService.handleTyping(userId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * POST /api/v1/typing/stop - 用户停止输入
     */
    @PostMapping("/stop")
    public ResponseEntity<Void> stopTyping(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody TypingRequest request) {
        typingStatusService.handleStopTyping(userId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/v1/typing/{conversationId} - 获取会话当前Typing用户列表
     */
    @GetMapping("/{conversationId}")
    public ResponseEntity<List<TypingStatusDTO>> getTypingStatus(
            @PathVariable String conversationId) {
        List<TypingStatusDTO> typingUsers = typingStatusService.getActiveTypingUsers(conversationId);
        return ResponseEntity.ok(typingUsers);
    }
}
