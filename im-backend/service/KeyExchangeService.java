package com.im.backend.service;

import com.im.backend.model.EncryptionKey;
import com.im.backend.repository.EncryptionKeyRepository;
import com.im.backend.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyPair;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 密钥交换服务
 * 处理端到端加密的密钥生成和交换
 */
@Service
public class KeyExchangeService {

    @Autowired
    private EncryptionKeyRepository encryptionKeyRepository;

    /**
     * 为用户生成新的加密密钥对
     */
    @Transactional
    public EncryptionKey generateKeyPair(Long userId, String keyType) throws Exception {
        // 停用用户现有的同类型密钥
        List<EncryptionKey> existingKeys = encryptionKeyRepository.findActiveKeysByUserId(userId);
        for (EncryptionKey key : existingKeys) {
            if (key.getKeyType().equals(keyType)) {
                key.setIsActive(false);
                encryptionKeyRepository.save(key);
            }
        }

        // 生成新的密钥对
        KeyPair keyPair;
        if ("EC".equals(keyType)) {
            keyPair = EncryptionUtil.generateECKeyPair();
        } else if ("RSA".equals(keyType)) {
            keyPair = EncryptionUtil.generateRSAKeyPair();
        } else {
            throw new IllegalArgumentException("不支持的密钥类型: " + keyType);
        }

        // 获取最新的版本号
        List<EncryptionKey> userKeys = encryptionKeyRepository.findByUserIdOrderByCreatedAtDesc(userId);
        Integer newVersion = userKeys.isEmpty() ? 1 : userKeys.get(0).getKeyVersion() + 1;

        // 创建密钥实体
        EncryptionKey encryptionKey = new EncryptionKey();
        encryptionKey.setUserId(userId);
        encryptionKey.setKeyType(keyType);
        encryptionKey.setPublicKey(EncryptionUtil.base64Encode(keyPair.getPublic().getEncoded()));
        // 私钥需要加密存储（这里简化为Base64，实际应该使用更强的加密）
        encryptionKey.setEncryptedPrivateKey(EncryptionUtil.base64Encode(keyPair.getPrivate().getEncoded()));
        encryptionKey.setKeyVersion(newVersion);
        encryptionKey.setIsActive(true);
        encryptionKey.setExpiresAt(LocalDateTime.now().plusDays(90)); // 密钥有效期90天

        return encryptionKeyRepository.save(encryptionKey);
    }

    /**
     * 获取用户的公钥
     */
    public Optional<String> getUserPublicKey(Long userId, String keyType) {
        Optional<EncryptionKey> key = encryptionKeyRepository.findActiveKeyByUserIdAndType(userId, keyType);
        return key.map(EncryptionKey::getPublicKey);
    }

    /**
     * 获取用户的加密密钥
     */
    public Optional<EncryptionKey> getUserEncryptionKey(Long userId, String keyType) {
        return encryptionKeyRepository.findActiveKeyByUserIdAndType(userId, keyType);
    }

    /**
     * 获取用户的私钥（解密后）
     */
    public byte[] getDecryptedPrivateKey(Long userId, String keyType) throws Exception {
        Optional<EncryptionKey> keyOpt = encryptionKeyRepository.findActiveKeyByUserIdAndType(userId, keyType);
        if (keyOpt.isPresent()) {
            EncryptionKey key = keyOpt.get();
            // 解密私钥（这里简化为Base64解码，实际应该解密）
            return EncryptionUtil.base64Decode(key.getEncryptedPrivateKey());
        }
        throw new Exception("未找到用户的" + keyType + "密钥");
    }

    /**
     * 轮换用户的密钥
     */
    @Transactional
    public EncryptionKey rotateKeys(Long userId, String keyType) throws Exception {
        // 生成新密钥
        EncryptionKey newKey = generateKeyPair(userId, keyType);

        // 通知相关用户密钥已更新（实际实现中需要发送通知）
        // notificationService.notifyKeyUpdate(userId, newKey.getKeyVersion());

        return newKey;
    }

    /**
     * 检查密钥是否需要轮换
     */
    public boolean needsRotation(Long userId, String keyType) {
        Optional<EncryptionKey> keyOpt = encryptionKeyRepository.findActiveKeyByUserIdAndType(userId, keyType);
        if (keyOpt.isPresent()) {
            EncryptionKey key = keyOpt.get();
            // 密钥即将过期（30天内）或已过期
            return key.getExpiresAt() != null &&
                   key.getExpiresAt().isBefore(LocalDateTime.now().plusDays(30));
        }
        return true; // 没有密钥需要生成
    }

    /**
     * 获取所有即将过期的密钥
     */
    public List<EncryptionKey> getExpiringKeys() {
        return encryptionKeyRepository.findExpiringKeys();
    }
}
