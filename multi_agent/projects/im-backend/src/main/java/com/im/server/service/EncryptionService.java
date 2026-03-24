package com.im.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 端到端加密服务
 * 提供消息加密、解密、密钥生成和交换功能
 */
@Service
public class EncryptionService {

    private static final Logger logger = LoggerFactory.getLogger(EncryptionService.class);

    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final String RSA_ALGORITHM = "RSA";
    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    // 存储用户的会话密钥
    private final ConcurrentHashMap<String, SecretKey> sessionKeys = new ConcurrentHashMap<>();
    // 存储用户的密钥对
    private final ConcurrentHashMap<String, KeyPair> userKeyPairs = new ConcurrentHashMap<>();
    // 存储其他用户的公钥
    private final ConcurrentHashMap<String, PublicKey> userPublicKeys = new ConcurrentHashMap<>();

    /**
     * 生成用户的RSA密钥对
     */
    public KeyPair generateKeyPair(String userId) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            keyGen.initialize(2048, new SecureRandom());
            KeyPair keyPair = keyGen.generateKeyPair();
            userKeyPairs.put(userId, keyPair);
            logger.info("Generated RSA key pair for user: {}", userId);
            return keyPair;
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to generate key pair for user: {}", userId, e);
            throw new RuntimeException("Failed to generate key pair", e);
        }
    }

    /**
     * 获取用户的公钥
     */
    public PublicKey getPublicKey(String userId) {
        KeyPair keyPair = userKeyPairs.get(userId);
        if (keyPair == null) {
            keyPair = generateKeyPair(userId);
        }
        return keyPair.getPublic();
    }

    /**
     * 获取用户的私钥
     */
    public PrivateKey getPrivateKey(String userId) {
        KeyPair keyPair = userKeyPairs.get(userId);
        if (keyPair == null) {
            keyPair = generateKeyPair(userId);
        }
        return keyPair.getPrivate();
    }

    /**
     * 获取公钥的Base64编码
     */
    public String getPublicKeyString(String userId) {
        PublicKey publicKey = getPublicKey(userId);
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * 从Base64编码的公钥字符串还原公钥
     */
    public PublicKey decodePublicKey(String publicKeyString) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            logger.error("Failed to decode public key", e);
            throw new RuntimeException("Failed to decode public key", e);
        }
    }

    /**
     * 存储其他用户的公钥
     */
    public void storePublicKey(String userId, PublicKey publicKey) {
        userPublicKeys.put(userId, publicKey);
        logger.info("Stored public key for user: {}", userId);
    }

    /**
     * 获取其他用户的公钥
     */
    public PublicKey getUserPublicKey(String userId) {
        return userPublicKeys.get(userId);
    }

    /**
     * 生成会话密钥
     */
    public SecretKey generateSessionKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(AES_KEY_SIZE, new SecureRandom());
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to generate session key", e);
            throw new RuntimeException("Failed to generate session key", e);
        }
    }

    /**
     * 存储会话密钥
     */
    public void storeSessionKey(String userId, String chatId, SecretKey key) {
        String sessionKeyId = userId + ":" + chatId;
        sessionKeys.put(sessionKeyId, key);
        logger.info("Stored session key for user: {}, chat: {}", userId, chatId);
    }

    /**
     * 获取会话密钥
     */
    public SecretKey getSessionKey(String userId, String chatId) {
        String sessionKeyId = userId + ":" + chatId;
        SecretKey key = sessionKeys.get(sessionKeyId);
        if (key == null) {
            key = generateSessionKey();
            sessionKeys.put(sessionKeyId, key);
        }
        return key;
    }

    /**
     * 使用RSA公钥加密AES会话密钥
     */
    public byte[] encryptSessionKeyWithRSA(PublicKey publicKey, SecretKey sessionKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(sessionKey.getEncoded());
        } catch (Exception e) {
            logger.error("Failed to encrypt session key with RSA", e);
            throw new RuntimeException("Failed to encrypt session key", e);
        }
    }

    /**
     * 使用RSA私钥解密AES会话密钥
     */
    public SecretKey decryptSessionKeyWithRSA(PrivateKey privateKey, byte[] encryptedKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] keyBytes = cipher.doFinal(encryptedKey);
            return new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            logger.error("Failed to decrypt session key with RSA", e);
            throw new RuntimeException("Failed to decrypt session key", e);
        }
    }

    /**
     * 使用AES-GCM加密消息
     */
    public EncryptedMessage encryptMessage(String plainText, SecretKey secretKey) {
        try {
            // 生成随机IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            // 创建GCM参数规范
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            // 初始化加密器
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

            // 加密消息
            byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF-8"));

            // 返回加密结果
            return new EncryptedMessage(
                    Base64.getEncoder().encodeToString(cipherText),
                    Base64.getEncoder().encodeToString(iv)
            );
        } catch (Exception e) {
            logger.error("Failed to encrypt message", e);
            throw new RuntimeException("Failed to encrypt message", e);
        }
    }

    /**
     * 使用AES-GCM解密消息
     */
    public String decryptMessage(EncryptedMessage encryptedMessage, SecretKey secretKey) {
        try {
            // 解码IV和密文
            byte[] iv = Base64.getDecoder().decode(encryptedMessage.getIv());
            byte[] cipherText = Base64.getDecoder().decode(encryptedMessage.getCipherText());

            // 创建GCM参数规范
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            // 初始化解密器
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

            // 解密消息
            byte[] plainText = cipher.doFinal(cipherText);

            return new String(plainText, "UTF-8");
        } catch (Exception e) {
            logger.error("Failed to decrypt message", e);
            throw new RuntimeException("Failed to decrypt message", e);
        }
    }

    /**
     * 使用接收者的公钥加密消息
     */
    public EncryptedMessage encryptMessageForRecipient(String plainText, String recipientId, String chatId) {
        // 获取或生成会话密钥
        SecretKey sessionKey = getSessionKey(recipientId, chatId);

        // 使用会话密钥加密消息
        return encryptMessage(plainText, sessionKey);
    }

    /**
     * 使用接收者的公钥加密会话密钥
     */
    public byte[] encryptSessionKeyForRecipient(String senderId, String recipientId, String chatId) {
        // 获取接收者的公钥
        PublicKey recipientPublicKey = getUserPublicKey(recipientId);
        if (recipientPublicKey == null) {
            throw new RuntimeException("Recipient public key not found: " + recipientId);
        }

        // 获取或生成会话密钥
        SecretKey sessionKey = getSessionKey(senderId, chatId);

        // 使用接收者的公钥加密会话密钥
        return encryptSessionKeyWithRSA(recipientPublicKey, sessionKey);
    }

    /**
     * 解密来自发送者的消息
     */
    public String decryptMessageFromSender(String senderId, String recipientId, String chatId,
                                            EncryptedMessage encryptedMessage) {
        // 获取接收者的私钥
        PrivateKey privateKey = getPrivateKey(recipientId);

        // 从数据库或缓存获取加密的会话密钥
        // 这里简化处理，实际应该从数据库获取
        SecretKey sessionKey = getSessionKey(recipientId, chatId);

        // 使用会话密钥解密消息
        return decryptMessage(encryptedMessage, sessionKey);
    }

    /**
     * 加密消息（兼容旧版本，使用Base64编码的密钥）
     */
    public EncryptedMessage encryptMessageWithKey(String plainText, String base64Key) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        return encryptMessage(plainText, keySpec);
    }

    /**
     * 解密消息（兼容旧版本）
     */
    public String decryptMessageWithKey(EncryptedMessage encryptedMessage, String base64Key) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        return decryptMessage(encryptedMessage, keySpec);
    }

    /**
     * 生成随机密钥（用于临时加密）
     */
    public String generateRandomKey() {
        SecretKey key = generateSessionKey();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * 删除用户的密钥（用于登出或安全清除）
     */
    public void clearUserKeys(String userId) {
        userKeyPairs.remove(userId);
        // 清理以该用户ID开头的所有会话密钥
        sessionKeys.keySet().stream()
                .filter(key -> key.startsWith(userId + ":"))
                .forEach(sessionKeys::remove);
        logger.info("Cleared keys for user: {}", userId);
    }

    /**
     * 加密消息类
     */
    public static class EncryptedMessage {
        private String cipherText;
        private String iv;

        public EncryptedMessage(String cipherText, String iv) {
            this.cipherText = cipherText;
            this.iv = iv;
        }

        public String getCipherText() {
            return cipherText;
        }

        public void setCipherText(String cipherText) {
            this.cipherText = cipherText;
        }

        public String getIv() {
            return iv;
        }

        public void setIv(String iv) {
            this.iv = iv;
        }
    }
}
