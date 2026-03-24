package com.im.backend.service;

import com.im.backend.entity.MessageStoragePolicy;
import com.im.backend.entity.MessageStoragePolicy.PolicyStatus;
import com.im.backend.repository.MessageStoragePolicyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 消息存储策略服务
 * 
 * @author im-backend
 * @version 1.0.0
 * @since 2026-03-22
 */
@Slf4j
@Service
public class MessageStoragePolicyService {
    
    @Autowired
    private MessageStoragePolicyRepository repository;
    
    /**
     * 创建存储策略
     */
    @Transactional
    public MessageStoragePolicy createPolicy(MessageStoragePolicy policy) {
        policy.setCreatedTime(LocalDateTime.now());
        policy.setStatus(PolicyStatus.ACTIVE);
        policy.setVersion(0);
        
        if (repository.existsByPolicyName(policy.getPolicyName())) {
            throw new IllegalArgumentException("策略名称已存在：" + policy.getPolicyName());
        }
        
        MessageStoragePolicy saved = repository.save(policy);
        log.info("创建消息存储策略：{}", saved.getPolicyName());
        return saved;
    }
    
    /**
     * 更新存储策略
     */
    @Transactional
    public MessageStoragePolicy updatePolicy(Long id, MessageStoragePolicy updates) {
        Optional<MessageStoragePolicy> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("策略不存在，ID: " + id);
        }
        
        MessageStoragePolicy existing = existingOpt.get();
        
        // 更新字段
        if (updates.getPolicyName() != null) {
            existing.setPolicyName(updates.getPolicyName());
        }
        if (updates.getDescription() != null) {
            existing.setDescription(updates.getDescription());
        }
        if (updates.getHotStorageDays() != null) {
            existing.setHotStorageDays(updates.getHotStorageDays());
        }
        if (updates.getColdStorageType() != null) {
            existing.setColdStorageType(updates.getColdStorageType());
        }
        if (updates.getColdBucketName() != null) {
            existing.setColdBucketName(updates.getColdBucketName());
        }
        if (updates.getColdStorageEndpoint() != null) {
            existing.setColdStorageEndpoint(updates.getColdStorageEndpoint());
        }
        if (updates.getColdStorageRegion() != null) {
            existing.setColdStorageRegion(updates.getColdStorageRegion());
        }
        if (updates.getEnableCompression() != null) {
            existing.setEnableCompression(updates.getEnableCompression());
        }
        if (updates.getCompressionAlgorithm() != null) {
            existing.setCompressionAlgorithm(updates.getCompressionAlgorithm());
        }
        if (updates.getEnableEncryption() != null) {
            existing.setEnableEncryption(updates.getEnableEncryption());
        }
        if (updates.getEncryptionKeyId() != null) {
            existing.setEncryptionKeyId(updates.getEncryptionKeyId());
        }
        if (updates.getArchiveFormat() != null) {
            existing.setArchiveFormat(updates.getArchiveFormat());
        }
        if (updates.getArchiveFileSizeMb() != null) {
            existing.setArchiveFileSizeMb(updates.getArchiveFileSizeMb());
        }
        if (updates.getArchiveNamingPattern() != null) {
            existing.setArchiveNamingPattern(updates.getArchiveNamingPattern());
        }
        if (updates.getEnableAutoArchive() != null) {
            existing.setEnableAutoArchive(updates.getEnableAutoArchive());
        }
        if (updates.getAutoArchiveCron() != null) {
            existing.setAutoArchiveCron(updates.getAutoArchiveCron());
        }
        if (updates.getDeleteAfterArchive() != null) {
            existing.setDeleteAfterArchive(updates.getDeleteAfterArchive());
        }
        if (updates.getArchiveRetentionDays() != null) {
            existing.setArchiveRetentionDays(updates.getArchiveRetentionDays());
        }
        if (updates.getEnableArchiveStatistics() != null) {
            existing.setEnableArchiveStatistics(updates.getEnableArchiveStatistics());
        }
        if (updates.getPriority() != null) {
            existing.setPriority(updates.getPriority());
        }
        if (updates.getStatus() != null) {
            existing.setStatus(updates.getStatus());
        }
        if (updates.getSessionType() != null) {
            existing.setSessionType(updates.getSessionType());
        }
        if (updates.getApplicableSessionIds() != null) {
            existing.setApplicableSessionIds(updates.getApplicableSessionIds());
        }
        
        existing.setUpdatedTime(LocalDateTime.now());
        existing.setVersion(existing.getVersion() + 1);
        
        MessageStoragePolicy saved = repository.save(existing);
        log.info("更新消息存储策略：{}", saved.getPolicyName());
        return saved;
    }
    
    /**
     * 删除存储策略
     */
    @Transactional
    public void deletePolicy(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("策略不存在，ID: " + id);
        }
        repository.deleteById(id);
        log.info("删除消息存储策略，ID: {}", id);
    }
    
    /**
     * 根据ID获取策略
     */
    public MessageStoragePolicy getPolicyById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("策略不存在，ID: " + id));
    }
    
    /**
     * 根据名称获取策略
     */
    public MessageStoragePolicy getPolicyByName(String name) {
        return repository.findByPolicyName(name)
            .orElseThrow(() -> new IllegalArgumentException("策略不存在，名称：" + name));
    }
    
    /**
     * 获取所有启用的策略
     */
    public List<MessageStoragePolicy> getAllActivePolicies() {
        return repository.findByStatusOrderByPriorityAsc(PolicyStatus.ACTIVE);
    }
    
    /**
     * 根据会话类型获取策略
     */
    public List<MessageStoragePolicy> getPoliciesBySessionType(String sessionType) {
        return repository.findBySessionTypeAndStatusOrderByPriorityAsc(sessionType, PolicyStatus.ACTIVE);
    }
    
    /**
     * 获取所有策略 (不分状态)
     */
    public List<MessageStoragePolicy> getAllPolicies() {
        return repository.findAll();
    }
    
    /**
     * 启用/禁用策略
     */
    @Transactional
    public MessageStoragePolicy togglePolicyStatus(Long id, boolean enable) {
        MessageStoragePolicy policy = getPolicyById(id);
        policy.setStatus(enable ? PolicyStatus.ACTIVE : PolicyStatus.INACTIVE);
        policy.setUpdatedTime(LocalDateTime.now());
        return repository.save(policy);
    }
    
    /**
     * 获取需要自动归档的策略
     */
    public List<MessageStoragePolicy> getAutoArchivePolicies() {
        return repository.findActiveAutoArchivePolicies();
    }
    
    /**
     * 统计策略信息
     */
    public PolicyStatistics getPolicyStatistics() {
        List<Object[]> statusCounts = repository.countPoliciesByStatus();
        List<Object[]> typeCounts = repository.countActivePoliciesByStorageType();
        
        PolicyStatistics stats = new PolicyStatistics();
        for (Object[] row : statusCounts) {
            PolicyStatus status = PolicyStatus.valueOf((String) row[0]);
            Long count = (Long) row[1];
            stats.addStatusCount(status, count);
        }
        
        for (Object[] row : typeCounts) {
            MessageStoragePolicy.ColdStorageType type = 
                MessageStoragePolicy.ColdStorageType.valueOf((String) row[0]);
            Long count = (Long) row[1];
            stats.addTypeCount(type, count);
        }
        
        return stats;
    }
    
    /**
     * 策略统计内部类
     */
    public static class PolicyStatistics {
        private int totalPolicies = 0;
        private int activePolicies = 0;
        private int inactivePolicies = 0;
        private int testingPolicies = 0;
        private int archivedPolicies = 0;
        private int s3Policies = 0;
        private int ossPolicies = 0;
        private int cosPolicies = 0;
        private int minioPolicies = 0;
        private int azurePolicies = 0;
        private int gcsPolicies = 0;
        private int nonePolicies = 0;
        
        public void addStatusCount(PolicyStatus status, Long count) {
            totalPolicies += count;
            switch (status) {
                case ACTIVE: activePolicies = count.intValue(); break;
                case INACTIVE: inactivePolicies = count.intValue(); break;
                case TESTING: testingPolicies = count.intValue(); break;
                case ARCHIVED: archivedPolicies = count.intValue(); break;
            }
        }
        
        public void addTypeCount(MessageStoragePolicy.ColdStorageType type, Long count) {
            switch (type) {
                case S3: s3Policies = count.intValue(); break;
                case OSS: ossPolicies = count.intValue(); break;
                case COS: cosPolicies = count.intValue(); break;
                case MINIO: minioPolicies = count.intValue(); break;
                case AZURE: azurePolicies = count.intValue(); break;
                case GCS: gcsPolicies = count.intValue(); break;
                case NONE: nonePolicies = count.intValue(); break;
            }
        }
        
        // Getters
        public int getTotalPolicies() { return totalPolicies; }
        public int getActivePolicies() { return activePolicies; }
        public int getInactivePolicies() { return inactivePolicies; }
        public int getTestingPolicies() { return testingPolicies; }
        public int getArchivedPolicies() { return archivedPolicies; }
        public int getS3Policies() { return s3Policies; }
        public int getOssPolicies() { return ossPolicies; }
        public int getCosPolicies() { return cosPolicies; }
        public int getMinioPolicies() { return minioPolicies; }
        public int getAzurePolicies() { return azurePolicies; }
        public int getGcsPolicies() { return gcsPolicies; }
        public int getNonePolicies() { return nonePolicies; }
    }
}