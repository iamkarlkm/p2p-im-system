package com.im.backend.service;

import com.im.backend.model.SecureSession;
import com.im.backend.util.EncryptionUtil;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加密服务
 * 管理安全会话和加密操作
 */
@Service
public class EncryptionService {

    // 内存中存储活跃会话（生产环境应使用Redis或数据库）
    private final Map<String, SecureSession> activeSessions = new ConcurrentHashMap<>();

    @Autowired
    private KeyExchangeService keyExchangeService;

    /**
     * 创建新的安全会话
     */
    public SecureSession createSession(Long userId, Long peerId, String sessionType) throws Exception {
        // 获取双方的EC密钥
        byte[] userPrivateKeyBytes = keyExchangeService.getDecryptedPrivateKey(userId, "EC");
        byte[] peerPublicKeyBytes = EncryptionUtil.base64Decode(
            keyExchangeService.getUserPublicKey(peerId, "EC")
                .orElseThrow(() -> new Exception("对方的公钥不存在"))
        );

        PrivateKey userPrivateKey = EncryptionUtil.loadECPrivateKey(userPrivateKeyBytes);
        PublicKey peerPublicKey = EncryptionUtil.loadECPublicKey(peerPublicKeyBytes);

        // 执行ECDH密钥交换
        byte[] sharedSecret = EncryptionUtil.performECDHKeyExchange(userPrivateKey, peerPublicKey);

        // 派生会话密钥
        byte[] sessionKey = EncryptionUtil.sha256(sharedSecret);

        // 创建会话
        SecureSession session = new SecureSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUserId(userId);
        session.setPeerId(peerId);
        session.setSessionType(sessionType);
        session.setSharedSecret(EncryptionUtil.base64Encode(sharedSecret));
        session.setSessionKey(EncryptionUtil.base64Encode(sessionKey));
        session.setStatus("ACTIVE");
        session.setEstablishedAt(LocalDateTime.now());
        session.setLastUsedAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusDays(7)); // 会话有效期7天

        // 存储会话
        activeSessions.put(session.getSessionId(), session);

        return session;
    }

    /**
     * 获取现有会话或创建新会话
     */
    public SecureSession getOrCreateSession(Long userId, Long peerId, String sessionType) throws Exception {
        // 查找现有会话
        SecureSession existingSession = findExistingSession(userId, peerId);
        if (existingSession != null && !isSessionExpired(existingSession)) {
            return existingSession;
        }

        // 创建新会话
        return createSession(userId, peerId, sessionType);
    }

    /**
     * 查找现有会话
     */
    public SecureSession findExistingSession(Long userId, Long peerId) {
        for (SecureSession session : activeSessions.values()) {
            boolean isParticipant = (session.getUserId().equals(userId) && session.getPeerId().equals(peerId)) ||
                                   (session.getUserId().equals(peerId) && session.getPeerId().equals(userId));
            if (isParticipant && "ACTIVE".equals(session.getStatus())) {
                return session;
            }
        }
        return null;
    }

    /**
     * 检查会话是否过期
     */
    public boolean isSessionExpired(SecureSession session) {
        return session.getExpiresAt() != null && session.getExpiresAt().isBefore(LocalDateTime.now());
    }

    /**
     * 根据ID获取会话
     */
    public SecureSession getSession(String sessionId) {
        return activeSessions.get(sessionId);
    }

    /**
     * 更新会话
     */
    public void updateSession(SecureSession session) {
        activeSessions.put(session.getSessionId(), session);
    }

    /**
     * 关闭会话
     */
    public void closeSession(String sessionId) {
        SecureSession session = activeSessions.get(sessionId);
        if (session != null) {
            session.setStatus("CLOSED");
            activeSessions.remove(sessionId);
        }
    }

    /**
     * 终止用户的所有会话
     */
    public void terminateUserSessions(Long userId) {
        activeSessions.values().removeIf(session ->
            session.getUserId().equals(userId) || session.getPeerId().equals(userId)
        );
    }

    /**
     * 清理过期会话
     */
    public void cleanupExpiredSessions() {
        activeSessions.values().removeIf(this::isSessionExpired);
    }

    /**
     * 获取会话统计信息
     */
    public Map<String, Object> getSessionStats() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("activeSessions", activeSessions.size());
        stats.put("timestamp", LocalDateTime.now());
        return stats;
    }
}
