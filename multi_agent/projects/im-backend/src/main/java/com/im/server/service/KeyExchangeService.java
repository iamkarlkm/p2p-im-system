package com.im.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 密钥交换服务
 * 处理端到端加密的密钥交换逻辑
 */
@Service
public class KeyExchangeService {

    private static final Logger logger = LoggerFactory.getLogger(KeyExchangeService.class);

    private static final String KEY_EXCHANGE_PREFIX = "key_exchange:";
    private static final String SESSION_KEY_PREFIX = "session_key:";
    private static final long KEY_EXPIRE_TIME = 24 * 60 * 60; // 24小时

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 发起密钥交换请求
     */
    public KeyExchangeRequest initiateKeyExchange(String senderId, String recipientId, String chatId) {
        // 获取发送者的公钥
        String senderPublicKey = encryptionService.getPublicKeyString(senderId);

        // 创建密钥交换请求
        KeyExchangeRequest request = new KeyExchangeRequest();
        request.setSenderId(senderId);
        request.setRecipientId(recipientId);
        request.setChatId(chatId);
        request.setSenderPublicKey(senderPublicKey);
        request.setTimestamp(System.currentTimeMillis());

        // 存储到Redis（24小时过期）
        String key = KEY_EXCHANGE_PREFIX + recipientId + ":" + chatId + ":" + senderId;
        redisTemplate.opsForValue().set(key, request, KEY_EXPIRE_TIME, TimeUnit.SECONDS);

        logger.info("Key exchange initiated from {} to {} for chat {}", senderId, recipientId, chatId);

        return request;
    }

    /**
     * 响应密钥交换请求
     */
    public KeyExchangeResponse respondToKeyExchange(String recipientId, String senderId, String chatId) {
        // 获取发送者的公钥
        String key = KEY_EXCHANGE_PREFIX + recipientId + ":" + chatId + ":" + senderId;
        KeyExchangeRequest request = (KeyExchangeRequest) redisTemplate.opsForValue().get(key);

        if (request == null) {
            logger.warn("Key exchange request not found for {} to {}", senderId, recipientId);
            return null;
        }

        // 生成会话密钥
        var sessionKey = encryptionService.generateSessionKey();
        String sessionKeyString = java.util.Base64.getEncoder().encodeToString(sessionKey.getEncoded());

        // 使用发送者的公钥加密会话密钥
        var senderPublicKey = encryptionService.decodePublicKey(request.getSenderPublicKey());
        byte[] encryptedSessionKey = encryptionService.encryptSessionKeyWithRSA(senderPublicKey, sessionKey);
        String encryptedSessionKeyString = java.util.Base64.getEncoder().encodeToString(encryptedSessionKey);

        // 存储会话密钥到Redis
        String sessionKeyRedisKey = SESSION_KEY_PREFIX + chatId + ":" + senderId + ":" + recipientId;
        redisTemplate.opsForValue().set(sessionKeyRedisKey, sessionKeyString, KEY_EXPIRE_TIME, TimeUnit.SECONDS);

        // 创建响应
        KeyExchangeResponse response = new KeyExchangeResponse();
        response.setSenderId(recipientId);
        response.setRecipientId(senderId);
        response.setChatId(chatId);
        response.setEncryptedSessionKey(encryptedSessionKeyString);
        response.setRecipientPublicKey(encryptionService.getPublicKeyString(recipientId));
        response.setTimestamp(System.currentTimeMillis());

        logger.info("Key exchange response from {} to {} for chat {}", recipientId, senderId, chatId);

        // 清理请求
        redisTemplate.delete(key);

        return response;
    }

    /**
     * 获取会话密钥
     */
    public String getSessionKey(String chatId, String userId1, String userId2) {
        // 尝试两个方向
        String key1 = SESSION_KEY_PREFIX + chatId + ":" + userId1 + ":" + userId2;
        String key2 = SESSION_KEY_PREFIX + chatId + ":" + userId2 + ":" + userId1;

        Object sessionKeyObj = redisTemplate.opsForValue().get(key1);
        if (sessionKeyObj == null) {
            sessionKeyObj = redisTemplate.opsForValue().get(key2);
        }

        if (sessionKeyObj == null) {
            logger.warn("Session key not found for chat {}", chatId);
            return null;
        }

        return sessionKeyObj.toString();
    }

    /**
     * 存储会话密钥
     */
    public void storeSessionKey(String chatId, String userId1, String userId2, String sessionKey) {
        String key = SESSION_KEY_PREFIX + chatId + ":" + userId1 + ":" + userId2;
        redisTemplate.opsForValue().set(key, sessionKey, KEY_EXPIRE_TIME, TimeUnit.SECONDS);
        logger.info("Stored session key for chat {}", chatId);
    }

    /**
     * 检查密钥交换是否已完成
     */
    public boolean isKeyExchangeComplete(String chatId, String userId1, String userId2) {
        return getSessionKey(chatId, userId1, userId2) != null;
    }

    /**
     * 清除会话密钥
     */
    public void clearSessionKey(String chatId, String userId1, String userId2) {
        String key1 = SESSION_KEY_PREFIX + chatId + ":" + userId1 + ":" + userId2;
        String key2 = SESSION_KEY_PREFIX + chatId + ":" + userId2 + ":" + userId1;

        redisTemplate.delete(key1);
        redisTemplate.delete(key2);

        logger.info("Cleared session keys for chat {}", chatId);
    }

    /**
     * 密钥交换请求类
     */
    public static class KeyExchangeRequest {
        private String senderId;
        private String recipientId;
        private String chatId;
        private String senderPublicKey;
        private long timestamp;

        // Getters and Setters
        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }

        public String getRecipientId() {
            return recipientId;
        }

        public void setRecipientId(String recipientId) {
            this.recipientId = recipientId;
        }

        public String getChatId() {
            return chatId;
        }

        public void setChatId(String chatId) {
            this.chatId = chatId;
        }

        public String getSenderPublicKey() {
            return senderPublicKey;
        }

        public void setSenderPublicKey(String senderPublicKey) {
            this.senderPublicKey = senderPublicKey;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    /**
     * 密钥交换响应类
     */
    public static class KeyExchangeResponse {
        private String senderId;
        private String recipientId;
        private String chatId;
        private String encryptedSessionKey;
        private String recipientPublicKey;
        private long timestamp;

        // Getters and Setters
        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }

        public String getRecipientId() {
            return recipientId;
        }

        public void setRecipientId(String recipientId) {
            this.recipientId = recipientId;
        }

        public String getChatId() {
            return chatId;
        }

        public void setChatId(String chatId) {
            this.chatId = chatId;
        }

        public String getEncryptedSessionKey() {
            return encryptedSessionKey;
        }

        public void setEncryptedSessionKey(String encryptedSessionKey) {
            this.encryptedSessionKey = encryptedSessionKey;
        }

        public String getRecipientPublicKey() {
            return recipientPublicKey;
        }

        public void setRecipientPublicKey(String recipientPublicKey) {
            this.recipientPublicKey = recipientPublicKey;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
