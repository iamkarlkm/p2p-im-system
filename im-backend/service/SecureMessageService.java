package com.im.backend.service;

import com.im.backend.dto.EncryptedMessageDTO;
import com.im.backend.model.EncryptionKey;
import com.im.backend.model.SecureSession;
import com.im.backend.repository.EncryptionKeyRepository;
import com.im.backend.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * 安全消息服务
 * 处理加密消息的发送、接收和解密
 */
@Service
public class SecureMessageService {

    @Autowired
    private KeyExchangeService keyExchangeService;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private EncryptionKeyRepository encryptionKeyRepository;

    /**
     * 加密消息
     */
    public EncryptedMessageDTO encryptMessage(Long senderId, Long receiverId, String plaintext) throws Exception {
        // 获取或创建安全会话
        SecureSession session = encryptionService.getOrCreateSession(senderId, receiverId, "ONE_TO_ONE");

        if (!"ACTIVE".equals(session.getStatus())) {
            throw new Exception("安全会话未建立");
        }

        // 解码共享密钥
        byte[] sharedSecret = EncryptionUtil.base64Decode(session.getSharedSecret());

        // 加密消息
        EncryptionUtil.EncryptedData encryptedData = EncryptionUtil.encryptAESGCM(
            plaintext.getBytes("UTF-8"),
            sharedSecret
        );

        // 获取发送者的签名密钥
        Optional<EncryptionKey> signingKeyOpt = keyExchangeService.getUserEncryptionKey(senderId, "RSA");
        if (!signingKeyOpt.isPresent()) {
            throw new Exception("发送者没有签名密钥");
        }

        EncryptionKey signingKey = signingKeyOpt.get();
        PrivateKey privateKey = EncryptionUtil.loadRSAPrivateKey(
            EncryptionUtil.base64Decode(signingKey.getEncryptedPrivateKey())
        );

        // 签名消息
        byte[] signature = EncryptionUtil.signMessage(encryptedData.getCiphertext(), privateKey);

        // 构建DTO
        EncryptedMessageDTO dto = new EncryptedMessageDTO();
        dto.setMessageId(UUID.randomUUID().toString());
        dto.setSenderId(senderId);
        dto.setReceiverId(receiverId);
        dto.setCiphertext(EncryptionUtil.base64Encode(encryptedData.getCiphertext()));
        dto.setIv(EncryptionUtil.base64Encode(encryptedData.getIv()));
        dto.setSignature(EncryptionUtil.base64Encode(signature));
        dto.setSessionId(session.getSessionId());
        dto.setEncryptionAlgorithm("AES-256-GCM");
        dto.setKeyVersion(signingKey.getKeyVersion());
        dto.setCreatedAt(LocalDateTime.now());

        return dto;
    }

    /**
     * 解密消息
     */
    public String decryptMessage(Long receiverId, EncryptedMessageDTO encryptedMessage) throws Exception {
        // 获取安全会话
        SecureSession session = encryptionService.getSession(encryptedMessage.getSessionId());
        if (session == null || !"ACTIVE".equals(session.getStatus())) {
            throw new Exception("安全会话不存在或未激活");
        }

        // 验证接收者身份
        if (!session.getUserId().equals(receiverId) && !session.getPeerId().equals(receiverId)) {
            throw new Exception("接收者不在会话中");
        }

        // 验证签名
        Optional<EncryptionKey> senderKeyOpt = encryptionKeyRepository.findByUserIdAndKeyVersion(
            encryptedMessage.getSenderId(),
            encryptedMessage.getKeyVersion()
        );
        if (!senderKeyOpt.isPresent()) {
            throw new Exception("无法找到发送者的公钥");
        }

        EncryptionKey senderKey = senderKeyOpt.get();
        PublicKey publicKey = EncryptionUtil.loadRSAPublicKey(
            EncryptionUtil.base64Decode(senderKey.getPublicKey())
        );

        boolean isValid = EncryptionUtil.verifySignature(
            EncryptionUtil.base64Decode(encryptedMessage.getCiphertext()),
            EncryptionUtil.base64Decode(encryptedMessage.getSignature()),
            publicKey
        );

        if (!isValid) {
            throw new Exception("消息签名验证失败");
        }

        // 解码共享密钥
        byte[] sharedSecret = EncryptionUtil.base64Decode(session.getSharedSecret());

        // 解密消息
        byte[] plaintext = EncryptionUtil.decryptAESGCM(
            EncryptionUtil.base64Decode(encryptedMessage.getCiphertext()),
            sharedSecret,
            EncryptionUtil.base64Decode(encryptedMessage.getIv())
        );

        // 更新最后使用时间
        session.setLastUsedAt(LocalDateTime.now());
        encryptionService.updateSession(session);

        return new String(plaintext, "UTF-8");
    }

    /**
     * 验证消息完整性
     */
    public boolean verifyMessageIntegrity(EncryptedMessageDTO encryptedMessage) throws Exception {
        Optional<EncryptionKey> senderKeyOpt = encryptionKeyRepository.findByUserIdAndKeyVersion(
            encryptedMessage.getSenderId(),
            encryptedMessage.getKeyVersion()
        );

        if (!senderKeyOpt.isPresent()) {
            return false;
        }

        EncryptionKey senderKey = senderKeyOpt.get();
        PublicKey publicKey = EncryptionUtil.loadRSAPublicKey(
            EncryptionUtil.base64Decode(senderKey.getPublicKey())
        );

        return EncryptionUtil.verifySignature(
            EncryptionUtil.base64Decode(encryptedMessage.getCiphertext()),
            EncryptionUtil.base64Decode(encryptedMessage.getSignature()),
            publicKey
        );
    }
}
