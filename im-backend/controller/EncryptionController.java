package com.im.backend.controller;

import com.im.backend.dto.EncryptedMessageDTO;
import com.im.backend.model.EncryptionKey;
import com.im.backend.model.SecureSession;
import com.im.backend.service.EncryptionService;
import com.im.backend.service.KeyExchangeService;
import com.im.backend.service.SecureMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 加密API控制器
 * 提供端到端加密的RESTful接口
 */
@RestController
@RequestMapping("/api/encryption")
public class EncryptionController {

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private KeyExchangeService keyExchangeService;

    @Autowired
    private SecureMessageService secureMessageService;

    /**
     * 生成用户的加密密钥对
     */
    @PostMapping("/keys/generate")
    public ResponseEntity<?> generateKeys(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "EC") String keyType) {
        try {
            EncryptionKey key = keyExchangeService.generateKeyPair(userId, keyType);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("keyId", key.getId());
            response.put("keyVersion", key.getKeyVersion());
            response.put("publicKey", key.getPublicKey());
            response.put("createdAt", key.getCreatedAt());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 获取用户的公钥
     */
    @GetMapping("/keys/public/{userId}")
    public ResponseEntity<?> getPublicKey(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "EC") String keyType) {
        try {
            java.util.Optional<String> publicKey = keyExchangeService.getUserPublicKey(userId, keyType);
            if (publicKey.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("userId", userId);
                response.put("keyType", keyType);
                response.put("publicKey", publicKey.get());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 创建安全会话
     */
    @PostMapping("/sessions/create")
    public ResponseEntity<?> createSession(
            @RequestParam Long userId,
            @RequestParam Long peerId,
            @RequestParam(defaultValue = "ONE_TO_ONE") String sessionType) {
        try {
            SecureSession session = encryptionService.createSession(userId, peerId, sessionType);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sessionId", session.getSessionId());
            response.put("status", session.getStatus());
            response.put("establishedAt", session.getEstablishedAt());
            response.put("expiresAt", session.getExpiresAt());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 获取会话信息
     */
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<?> getSession(@PathVariable String sessionId) {
        SecureSession session = encryptionService.getSession(sessionId);
        if (session != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sessionId", session.getSessionId());
            response.put("userId", session.getUserId());
            response.put("peerId", session.getPeerId());
            response.put("status", session.getStatus());
            response.put("lastUsedAt", session.getLastUsedAt());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 关闭会话
     */
    @PostMapping("/sessions/{sessionId}/close")
    public ResponseEntity<?> closeSession(@PathVariable String sessionId) {
        encryptionService.closeSession(sessionId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "会话已关闭");
        return ResponseEntity.ok(response);
    }

    /**
     * 加密消息
     */
    @PostMapping("/messages/encrypt")
    public ResponseEntity<?> encryptMessage(@RequestBody EncryptedMessageDTO request) {
        try {
            EncryptedMessageDTO encrypted = secureMessageService.encryptMessage(
                request.getSenderId(),
                request.getReceiverId(),
                request.getPlaintext()
            );
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("messageId", encrypted.getMessageId());
            response.put("ciphertext", encrypted.getCiphertext());
            response.put("iv", encrypted.getIv());
            response.put("signature", encrypted.getSignature());
            response.put("sessionId", encrypted.getSessionId());
            response.put("keyVersion", encrypted.getKeyVersion());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 解密消息
     */
    @PostMapping("/messages/decrypt")
    public ResponseEntity<?> decryptMessage(@RequestBody EncryptedMessageDTO request) {
        try {
            String plaintext = secureMessageService.decryptMessage(request.getReceiverId(), request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("plaintext", plaintext);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 验证消息签名
     */
    @PostMapping("/messages/verify")
    public ResponseEntity<?> verifyMessage(@RequestBody EncryptedMessageDTO request) {
        try {
            boolean isValid = secureMessageService.verifyMessageIntegrity(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("valid", isValid);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 轮换用户密钥
     */
    @PostMapping("/keys/rotate")
    public ResponseEntity<?> rotateKeys(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "EC") String keyType) {
        try {
            EncryptionKey newKey = keyExchangeService.rotateKeys(userId, keyType);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("keyId", newKey.getId());
            response.put("keyVersion", newKey.getKeyVersion());
            response.put("publicKey", newKey.getPublicKey());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 获取加密服务状态
     */
    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("encryptionEnabled", true);
        response.put("supportedAlgorithms", new String[]{"AES-256-GCM", "ECDH", "RSA-SHA256"});
        response.put("sessionStats", encryptionService.getSessionStats());
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        return response;
    }
}
