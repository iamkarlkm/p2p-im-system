package com.im.controller;

import com.im.dto.MessageExpirationDTO;
import com.im.dto.MessageExpirationRequest;
import com.im.service.MessageExpirationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 消息过期策略 REST API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/expiration")
@RequiredArgsConstructor
public class MessageExpirationController {

    private final MessageExpirationService expirationService;

    /** 设置会话过期策略 (群聊) */
    @PutMapping("/conversation/{conversationId}")
    public ResponseEntity<MessageExpirationDTO> setConversationExpiration(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String conversationId,
            @Valid @RequestBody MessageExpirationRequest request) {
        request.setConversationId(conversationId);
        return ResponseEntity.ok(expirationService.setExpiration(userId, request));
    }

    /** 获取会话过期策略 (群聊) */
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<MessageExpirationDTO> getConversationExpiration(
            @PathVariable String conversationId) {
        return ResponseEntity.ok(expirationService.getConversationExpiration(conversationId));
    }

    /** 删除会话过期策略 */
    @DeleteMapping("/conversation/{conversationId}")
    public ResponseEntity<Void> deleteConversationExpiration(
            @PathVariable String conversationId) {
        expirationService.deleteExpiration(conversationId);
        return ResponseEntity.noContent().build();
    }

    /** 设置私聊过期策略 */
    @PutMapping("/private/{receiverId}")
    public ResponseEntity<MessageExpirationDTO> setPrivateExpiration(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String receiverId,
            @Valid @RequestBody MessageExpirationRequest request) {
        request.setReceiverId(receiverId);
        return ResponseEntity.ok(expirationService.setExpiration(userId, request));
    }

    /** 获取私聊过期策略 */
    @GetMapping("/private/{receiverId}")
    public ResponseEntity<MessageExpirationDTO> getPrivateExpiration(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String receiverId) {
        MessageExpirationDTO dto = expirationService.getPrivateExpiration(userId, receiverId);
        return dto != null ? ResponseEntity.ok(dto)
                : ResponseEntity.ok(MessageExpirationDTO.builder()
                        .expirationType("OFF").enabled(false).build());
    }
}
