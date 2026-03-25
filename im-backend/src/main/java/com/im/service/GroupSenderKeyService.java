package com.im.service;

import com.im.entity.GroupSenderKeyEntity;
import com.im.repository.GroupSenderKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;

/**
 * 群组Sender Key服务层
 * Signal Protocol群组加密核心服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupSenderKeyService {
    
    private final GroupSenderKeyRepository senderKeyRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    
    // ==================== 密钥生成 ====================
    
    /**
     * 生成新的Sender Key
     * @param groupId 群组ID
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @return 生成的Sender Key实体
     */
    @Transactional
    public GroupSenderKeyEntity generateSenderKey(String groupId, String senderId, String receiverId) {
        return generateSenderKey(groupId, senderId, receiverId, 1);
    }
    
    /**
     * 生成新版本的Sender Key
     */
    @Transactional
    public GroupSenderKeyEntity generateSenderKey(String groupId, String senderId, String receiverId, Integer keyVersion) {
        // 生成32字节的链密钥
        byte[] chainKeyBytes = new byte[32];
        secureRandom.nextBytes(chainKeyBytes);
        String chainKey = Base64.getEncoder().encodeToString(chainKeyBytes);
        
        // 生成签名密钥对（EC key pair）
        String[] signingKeys = generateSigningKeyPair();
        
        // 查找最新版本
        Optional<GroupSenderKeyEntity> latest = senderKeyRepository.findLatestBySenderAndReceiver(
            groupId, senderId, receiverId);
        
        Integer effectiveVersion = keyVersion;
        if (latest.isPresent() && keyVersion <= latest.get().getKeyVersion()) {
            effectiveVersion = latest.get().getKeyVersion() + 1;
        }
        
        String senderKeyId = GroupSenderKeyEntity.generateSenderKeyId(groupId, senderId, receiverId, effectiveVersion);
        
        GroupSenderKeyEntity senderKey = GroupSenderKeyEntity.builder()
            .senderKeyId(senderKeyId)
            .groupId(groupId)
            .senderId(senderId)
            .receiverId(receiverId)
            .keyVersion(effectiveVersion)
            .chainKey(chainKey)
            .chainKeyIndex(0L)
            .signingPublicKey(signingKeys[0])
            .signingPrivateKey(signingKeys[1])
            .senderKeyStatus("INITIAL")
            .distributionStatus("PENDING")
            .forwardSecrecyEnabled(true)
            .futureSecrecyEnabled(true)
            .rotationPeriodDays(30)
            .acknowledged(false)
            .build();
        
        return senderKeyRepository.save(senderKey);
    }
    
    /**
     * 批量生成分发给所有群成员的Sender Key
     */
    @Transactional
    public List<GroupSenderKeyEntity> generateAndDistributeToMembers(String groupId, String senderId, List<String> memberIds) {
        return memberIds.stream()
            .filter(memberId -> !memberId.equals(senderId)) // 不给自己分发
            .map(receiverId -> generateSenderKey(groupId, senderId, receiverId))
            .toList();
    }
    
    // ==================== 密钥分发与确认 ====================
    
    /**
     * 激活Sender Key（确认分发成功）
     */
    @Transactional
    public GroupSenderKeyEntity activateSenderKey(String senderKeyId) {
        GroupSenderKeyEntity senderKey = senderKeyRepository.findBySenderKeyId(senderKeyId)
            .orElseThrow(() -> new RuntimeException("Sender Key not found: " + senderKeyId));
        
        senderKey.activate();
        return senderKeyRepository.save(senderKey);
    }
    
    /**
     * 确认收到Sender Key
     */
    @Transactional
    public GroupSenderKeyEntity acknowledgeSenderKey(String senderKeyId) {
        GroupSenderKeyEntity senderKey = senderKeyRepository.findBySenderKeyId(senderKeyId)
            .orElseThrow(() -> new RuntimeException("Sender Key not found: " + senderKeyId));
        
        senderKey.acknowledge();
        return senderKeyRepository.save(senderKey);
    }
    
    /**
     * 批量确认Sender Key
     */
    @Transactional
    public int batchAcknowledge(List<String> senderKeyIds) {
        return senderKeyRepository.acknowledgeSenderKeys(senderKeyIds, LocalDateTime.now());
    }
    
    // ==================== 消息密钥派生 ====================
    
    /**
     * 从Sender Key派生消息密钥
     * HKDF-like密钥派生：hash(chainKey || index)
     */
    public String deriveMessageKey(String chainKey, long index) {
        try {
            byte[] chainKeyBytes = Base64.getDecoder().decode(chainKey);
            byte[] indexBytes = String.valueOf(index).getBytes();
            
            // 简单的密钥派生：拼接后哈希
            byte[] combined = new byte[chainKeyBytes.length + indexBytes.length];
            System.arraycopy(chainKeyBytes, 0, combined, 0, chainKeyBytes.length);
            System.arraycopy(indexBytes, 0, combined, chainKeyBytes.length, indexBytes.length);
            
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combined);
            
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to derive message key", e);
        }
    }
    
    /**
     * 获取下一条消息的密钥（派生后推进链）
     */
    @Transactional
    public String getNextMessageKey(String senderKeyId) {
        GroupSenderKeyEntity senderKey = senderKeyRepository.findBySenderKeyId(senderKeyId)
            .orElseThrow(() -> new RuntimeException("Sender Key not found: " + senderKeyId));
        
        String messageKey = deriveMessageKey(senderKey.getChainKey(), senderKey.getChainKeyIndex());
        
        // 推进链密钥索引
        senderKey.incrementChainKeyIndex();
        
        // 如果启用了前向保密，更新链密钥
        if (senderKey.getForwardSecrecyEnabled()) {
            String newChainKey = deriveNextChainKey(senderKey.getChainKey());
            senderKey.setChainKey(newChainKey);
        }
        
        senderKeyRepository.save(senderKey);
        return messageKey;
    }
    
    /**
     * 派生下一个链密钥
     */
    private String deriveNextChainKey(String currentChainKey) {
        try {
            byte[] chainKeyBytes = Base64.getDecoder().decode(currentChainKey);
            byte[] oneByte = new byte[]{0x01};
            
            byte[] combined = new byte[chainKeyBytes.length + 1];
            System.arraycopy(chainKeyBytes, 0, combined, 0, chainKeyBytes.length);
            System.arraycopy(oneByte, 0, combined, chainKeyBytes.length, 1);
            
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(combined));
        } catch (Exception e) {
            throw new RuntimeException("Failed to derive next chain key", e);
        }
    }
    
    // ==================== 密钥轮换 ====================
    
    /**
     * 开始密钥轮换
     */
    @Transactional
    public GroupSenderKeyEntity startRotation(String groupId, String senderId) {
        List<GroupSenderKeyEntity> activeKeys = senderKeyRepository
            .findByGroupIdAndSenderIdAndSenderKeyStatus(groupId, senderId, "ACTIVE");
        
        activeKeys.forEach(GroupSenderKeyEntity::startRotation);
        senderKeyRepository.saveAll(activeKeys);
        
        if (!activeKeys.isEmpty()) {
            return activeKeys.get(0);
        }
        return null;
    }
    
    /**
     * 完成密钥轮换（生成新密钥版本）
     */
    @Transactional
    public List<GroupSenderKeyEntity> completeRotation(String groupId, String senderId, List<String> receiverIds) {
        // 标记旧版本为已替换
        senderKeyRepository.findByGroupIdAndSenderId(groupId, senderId)
            .forEach(key -> {
                if (!"REPLACED".equals(key.getSenderKeyStatus())) {
                    key.replace();
                }
            });
        
        // 生成新版本
        return receiverIds.stream()
            .filter(receiverId -> !receiverId.equals(senderId))
            .map(receiverId -> {
                GroupSenderKeyEntity newKey = generateSenderKey(groupId, senderId, receiverId);
                newKey.activate();
                return senderKeyRepository.save(newKey);
            })
            .toList();
    }
    
    /**
     * 检查并执行需要轮换的Sender Key
     */
    @Transactional
    public void checkAndRotateExpiredKeys() {
        LocalDateTime now = LocalDateTime.now();
        
        // 处理需要轮换的密钥
        List<GroupSenderKeyEntity> keysNeedingRotation = senderKeyRepository.findKeysNeedingRotation(now);
        keysNeedingRotation.forEach(key -> {
            startRotation(key.getGroupId(), key.getSenderId());
        });
        
        // 处理已过期的密钥
        List<GroupSenderKeyEntity> expiredKeys = senderKeyRepository.findExpiredKeys(now);
        expiredKeys.forEach(key -> {
            key.markExpired();
            senderKeyRepository.save(key);
        });
    }
    
    // ==================== 密钥撤销 ====================
    
    /**
     * 撤销Sender Key
     */
    @Transactional
    public int revokeSenderKey(String senderKeyId) {
        GroupSenderKeyEntity senderKey = senderKeyRepository.findBySenderKeyId(senderKeyId)
            .orElseThrow(() -> new RuntimeException("Sender Key not found: " + senderKeyId));
        
        senderKey.revoke();
        senderKeyRepository.save(senderKey);
        return 1;
    }
    
    /**
     * 撤销发送者的所有Sender Key（成员被移除时）
     */
    @Transactional
    public int revokeAllBySender(String groupId, String senderId) {
        List<GroupSenderKeyEntity> keys = senderKeyRepository.findByGroupIdAndSenderId(groupId, senderId);
        keys.forEach(GroupSenderKeyEntity::revoke);
        senderKeyRepository.saveAll(keys);
        return keys.size();
    }
    
    /**
     * 撤销接收者的所有Sender Key（成员离开群组时）
     */
    @Transactional
    public int revokeAllByReceiver(String groupId, String receiverId) {
        List<GroupSenderKeyEntity> keys = senderKeyRepository.findByGroupIdAndReceiverId(groupId, receiverId);
        keys.forEach(GroupSenderKeyEntity::revoke);
        senderKeyRepository.saveAll(keys);
        return keys.size();
    }
    
    // ==================== 查询方法 ====================
    
    /**
     * 获取群组中所有激活的Sender Key
     */
    public List<GroupSenderKeyEntity> getActiveKeysInGroup(String groupId) {
        return senderKeyRepository.findActiveByGroupId(groupId);
    }
    
    /**
     * 获取发送者发给接收者的最新Sender Key
     */
    public Optional<GroupSenderKeyEntity> getLatestKey(String groupId, String senderId, String receiverId) {
        return senderKeyRepository.findLatestBySenderAndReceiver(groupId, senderId, receiverId);
    }
    
    /**
     * 获取用户在群组中持有的所有Sender Key
     */
    public List<GroupSenderKeyEntity> getKeysForReceiver(String groupId, String receiverId) {
        return senderKeyRepository.findByGroupIdAndReceiverId(groupId, receiverId);
    }
    
    /**
     * 获取用户在群组中分发的所有Sender Key
     */
    public List<GroupSenderKeyEntity> getKeysForSender(String groupId, String senderId) {
        return senderKeyRepository.findByGroupIdAndSenderId(groupId, senderId);
    }
    
    /**
     * 获取Sender Key详情
     */
    public Optional<GroupSenderKeyEntity> getSenderKey(String senderKeyId) {
        return senderKeyRepository.findBySenderKeyId(senderKeyId);
    }
    
    // ==================== 统计方法 ====================
    
    /**
     * 统计群组中的Sender Key数量
     */
    public long countKeysInGroup(String groupId) {
        return senderKeyRepository.countByGroupId(groupId);
    }
    
    /**
     * 统计群组成员数量
     */
    public long countMembersInGroup(String groupId) {
        return senderKeyRepository.countDistinctSendersInGroup(groupId);
    }
    
    /**
     * 获取Sender Key统计信息
     */
    public SenderKeyStats getStats(String groupId) {
        List<GroupSenderKeyEntity> allKeys = senderKeyRepository.findByGroupId(groupId);
        
        return new SenderKeyStats(
            allKeys.size(),
            allKeys.stream().filter(k -> "ACTIVE".equals(k.getSenderKeyStatus())).count(),
            allKeys.stream().filter(k -> k.getAcknowledged()).count(),
            allKeys.stream().filter(k -> !k.getAcknowledged()).count(),
            allKeys.stream().filter(k -> "REPLACED".equals(k.getSenderKeyStatus())).count()
        );
    }
    
    // ==================== 清理方法 ====================
    
    /**
     * 删除群组的所有Sender Key
     */
    @Transactional
    public int deleteGroupKeys(String groupId) {
        return senderKeyRepository.deleteByGroupId(groupId);
    }
    
    /**
     * 清理过期的Sender Key记录
     */
    @Transactional
    public int cleanupStaleKeys(int daysOld) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysOld);
        return senderKeyRepository.deleteStaleKeys(cutoff);
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 生成签名密钥对（简化版，实际应使用ECDSA）
     */
    private String[] generateSigningKeyPair() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256, secureRandom);
            SecretKey secretKey = keyGen.generateKey();
            String publicKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            // 注意：实际生产应使用ECDSA，这里简化处理
            String privateKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            return new String[]{publicKey, privateKey};
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signing key pair", e);
        }
    }
    
    /**
     * Sender Key统计信息
     */
    public record SenderKeyStats(
        long totalKeys,
        long activeKeys,
        long acknowledgedKeys,
        long pendingKeys,
        long replacedKeys
    ) {}
}
