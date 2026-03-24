package com.im.backend.service;

import com.im.backend.entity.MessageStorageLayerEntity;
import com.im.backend.repository.MessageStorageLayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 消息存储分层配置服务
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MessageStorageLayerService {
    
    private final MessageStorageLayerRepository messageStorageLayerRepository;
    
    /**
     * 创建新的存储分层策略
     */
    @Transactional
    public MessageStorageLayerEntity createStrategy(MessageStorageLayerEntity strategy) {
        // 检查策略名称是否已存在
        if (messageStorageLayerRepository.findByStrategyName(strategy.getStrategyName()).isPresent()) {
            throw new IllegalArgumentException("策略名称已存在: " + strategy.getStrategyName());
        }
        
        // 设置默认值
        if (strategy.getStrategyName() == null) {
            strategy.setStrategyName("default_strategy_" + System.currentTimeMillis());
        }
        
        if (strategy.getStatus() == null) {
            strategy.setStatus("ENABLED");
        }
        
        if (strategy.getCreatedAt() == null) {
            strategy.setCreatedAt(LocalDateTime.now());
        }
        
        if (strategy.getUpdatedAt() == null) {
            strategy.setUpdatedAt(LocalDateTime.now());
        }
        
        return messageStorageLayerRepository.save(strategy);
    }
    
    /**
     * 更新存储分层策略
     */
    @Transactional
    public MessageStorageLayerEntity updateStrategy(Long id, MessageStorageLayerEntity updatedStrategy) {
        MessageStorageLayerEntity existingStrategy = messageStorageLayerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("策略不存在，ID: " + id));
        
        // 检查策略名称是否冲突（如果不是更新自己）
        if (!existingStrategy.getStrategyName().equals(updatedStrategy.getStrategyName())) {
            Optional<MessageStorageLayerEntity> conflict = messageStorageLayerRepository
                .findByStrategyName(updatedStrategy.getStrategyName());
            if (conflict.isPresent() && !conflict.get().getId().equals(id)) {
                throw new IllegalArgumentException("策略名称已存在: " + updatedStrategy.getStrategyName());
            }
        }
        
        // 更新字段
        existingStrategy.setStrategyName(updatedStrategy.getStrategyName());
        existingStrategy.setDescription(updatedStrategy.getDescription());
        existingStrategy.setHotStorageDays(updatedStrategy.getHotStorageDays());
        existingStrategy.setWarmStorageDays(updatedStrategy.getWarmStorageDays());
        existingStrategy.setColdStorageType(updatedStrategy.getColdStorageType());
        existingStrategy.setColdStorageBucket(updatedStrategy.getColdStorageBucket());
        existingStrategy.setColdStoragePrefix(updatedStrategy.getColdStoragePrefix());
        existingStrategy.setCompressionFormat(updatedStrategy.getCompressionFormat());
        existingStrategy.setEncryptionEnabled(updatedStrategy.getEncryptionEnabled());
        existingStrategy.setEncryptionAlgorithm(updatedStrategy.getEncryptionAlgorithm());
        existingStrategy.setArchiveBatchSize(updatedStrategy.getArchiveBatchSize());
        existingStrategy.setArchiveIntervalMinutes(updatedStrategy.getArchiveIntervalMinutes());
        existingStrategy.setArchiveConcurrency(updatedStrategy.getArchiveConcurrency());
        existingStrategy.setAutoArchiveEnabled(updatedStrategy.getAutoArchiveEnabled());
        existingStrategy.setAutoCleanupEnabled(updatedStrategy.getAutoCleanupEnabled());
        existingStrategy.setCleanupRetentionDays(updatedStrategy.getCleanupRetentionDays());
        existingStrategy.setSmartLayeringEnabled(updatedStrategy.getSmartLayeringEnabled());
        existingStrategy.setSmartAccessThreshold(updatedStrategy.getSmartAccessThreshold());
        existingStrategy.setStatus(updatedStrategy.getStatus());
        
        existingStrategy.setUpdatedAt(LocalDateTime.now());
        
        return messageStorageLayerRepository.save(existingStrategy);
    }
    
    /**
     * 根据ID获取策略
     */
    public MessageStorageLayerEntity getStrategyById(Long id) {
        return messageStorageLayerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("策略不存在，ID: " + id));
    }
    
    /**
     * 根据策略名称获取策略
     */
    public MessageStorageLayerEntity getStrategyByName(String strategyName) {
        return messageStorageLayerRepository.findByStrategyName(strategyName)
            .orElseThrow(() -> new IllegalArgumentException("策略不存在，名称: " + strategyName));
    }
    
    /**
     * 获取所有策略（分页）
     */
    public Page<MessageStorageLayerEntity> getAllStrategies(Pageable pageable) {
        return messageStorageLayerRepository.findAll(pageable);
    }
    
    /**
     * 获取所有启用的策略
     */
    public List<MessageStorageLayerEntity> getAllEnabledStrategies() {
        return messageStorageLayerRepository.findAllEnabled();
    }
    
    /**
     * 根据状态获取策略
     */
    public List<MessageStorageLayerEntity> getStrategiesByStatus(String status) {
        return messageStorageLayerRepository.findByStatus(status);
    }
    
    /**
     * 根据存储类型获取策略
     */
    public List<MessageStorageLayerEntity> getStrategiesByStorageType(String storageType) {
        return messageStorageLayerRepository.findByColdStorageType(storageType);
    }
    
    /**
     * 删除策略
     */
    @Transactional
    public void deleteStrategy(Long id) {
        if (!messageStorageLayerRepository.existsById(id)) {
            throw new IllegalArgumentException("策略不存在，ID: " + id);
        }
        messageStorageLayerRepository.deleteById(id);
    }
    
    /**
     * 批量删除策略
     */
    @Transactional
    public void deleteStrategies(List<Long> ids) {
        List<MessageStorageLayerEntity> strategies = messageStorageLayerRepository.findByIdIn(ids);
        if (strategies.size() != ids.size()) {
            throw new IllegalArgumentException("部分策略不存在");
        }
        messageStorageLayerRepository.deleteAllById(ids);
    }
    
    /**
     * 启用策略
     */
    @Transactional
    public void enableStrategy(Long id) {
        int updated = messageStorageLayerRepository.updateStatus(id, "ENABLED");
        if (updated == 0) {
            throw new IllegalArgumentException("策略不存在或无法启用，ID: " + id);
        }
    }
    
    /**
     * 禁用策略
     */
    @Transactional
    public void disableStrategy(Long id) {
        int updated = messageStorageLayerRepository.updateStatus(id, "DISABLED");
        if (updated == 0) {
            throw new IllegalArgumentException("策略不存在或无法禁用，ID: " + id);
        }
    }
    
    /**
     * 清除策略错误信息
     */
    @Transactional
    public void clearStrategyError(Long id) {
        int updated = messageStorageLayerRepository.clearError(id);
        if (updated == 0) {
            throw new IllegalArgumentException("策略不存在或无法清除错误，ID: " + id);
        }
    }
    
    /**
     * 设置策略错误信息
     */
    @Transactional
    public void setStrategyError(Long id, String errorMessage) {
        int updated = messageStorageLayerRepository.setError(id, errorMessage);
        if (updated == 0) {
            throw new IllegalArgumentException("策略不存在或无法设置错误，ID: " + id);
        }
    }
    
    /**
     * 获取需要执行归档的策略
     */
    public List<MessageStorageLayerEntity> getStrategiesNeedingArchive() {
        LocalDateTime thresholdTime = LocalDateTime.now().minusMinutes(60); // 默认60分钟间隔
        return messageStorageLayerRepository.findStrategiesNeedingArchive(thresholdTime);
    }
    
    /**
     * 获取需要执行清理的策略
     */
    public List<MessageStorageLayerEntity> getStrategiesNeedingCleanup() {
        LocalDateTime thresholdTime = LocalDateTime.now().minusHours(24); // 默认24小时间隔
        return messageStorageLayerRepository.findStrategiesNeedingCleanup(thresholdTime);
    }
    
    /**
     * 更新归档统计信息
     */
    @Transactional
    public void updateArchiveStats(Long strategyId, Long lastArchiveMessageId, 
                                   Long incrementCount, Long incrementSize) {
        int updated = messageStorageLayerRepository.updateArchiveStats(
            strategyId, 
            LocalDateTime.now(), 
            lastArchiveMessageId, 
            incrementCount, 
            incrementSize
        );
        
        if (updated == 0) {
            log.warn("更新归档统计失败，策略ID: {}", strategyId);
        } else {
            log.info("已更新归档统计，策略ID: {}，新增消息: {}，大小: {} 字节", 
                    strategyId, incrementCount, incrementSize);
        }
    }
    
    /**
     * 更新清理统计信息
     */
    @Transactional
    public void updateCleanupStats(Long strategyId, Long incrementCount) {
        int updated = messageStorageLayerRepository.updateCleanupStats(
            strategyId,
            LocalDateTime.now(),
            incrementCount
        );
        
        if (updated == 0) {
            log.warn("更新清理统计失败，策略ID: {}", strategyId);
        } else {
            log.info("已更新清理统计，策略ID: {}，清理消息: {}", strategyId, incrementCount);
        }
    }
    
    /**
     * 统计信息
     */
    public StorageLayerStatistics getStatistics() {
        StorageLayerStatistics stats = new StorageLayerStatistics();
        stats.setEnabledCount(messageStorageLayerRepository.countEnabled());
        stats.setTotalArchivedMessages(messageStorageLayerRepository.sumArchivedMessagesCount());
        stats.setTotalArchivedSize(messageStorageLayerRepository.sumArchivedMessagesSize());
        stats.setTotalCleanedMessages(messageStorageLayerRepository.sumCleanedMessagesCount());
        
        // 获取归档最多的策略
        List<MessageStorageLayerEntity> topArchived = messageStorageLayerRepository
            .findAllOrderByArchivedMessagesCountDesc();
        if (!topArchived.isEmpty()) {
            stats.setTopArchivedStrategy(topArchived.get(0));
        }
        
        // 获取错误策略
        List<MessageStorageLayerEntity> errorStrategies = messageStorageLayerRepository
            .findByStatusAndErrorMessageIsNotNull("ERROR");
        stats.setErrorStrategyCount(errorStrategies.size());
        
        return stats;
    }
    
    /**
     * 统计信息类
     */
    @lombok.Data
    public static class StorageLayerStatistics {
        private long enabledCount;
        private long totalArchivedMessages;
        private long totalArchivedSize;
        private long totalCleanedMessages;
        private MessageStorageLayerEntity topArchivedStrategy;
        private int errorStrategyCount;
        
        public String getTotalArchivedSizeFormatted() {
            if (totalArchivedSize < 1024) {
                return totalArchivedSize + " B";
            } else if (totalArchivedSize < 1024 * 1024) {
                return String.format("%.2f KB", totalArchivedSize / 1024.0);
            } else if (totalArchivedSize < 1024 * 1024 * 1024) {
                return String.format("%.2f MB", totalArchivedSize / (1024.0 * 1024.0));
            } else {
                return String.format("%.2f GB", totalArchivedSize / (1024.0 * 1024.0 * 1024.0));
            }
        }
    }
    
    /**
     * 验证策略配置
     */
    public void validateStrategy(MessageStorageLayerEntity strategy) {
        if (strategy.getHotStorageDays() <= 0) {
            throw new IllegalArgumentException("热存储天数必须大于0");
        }
        
        if (strategy.getWarmStorageDays() <= strategy.getHotStorageDays()) {
            throw new IllegalArgumentException("温存储天数必须大于热存储天数");
        }
        
        if (strategy.getArchiveBatchSize() <= 0) {
            throw new IllegalArgumentException("归档批次大小必须大于0");
        }
        
        if (strategy.getArchiveConcurrency() <= 0) {
            throw new IllegalArgumentException("归档并发数必须大于0");
        }
        
        if (strategy.getAutoCleanupEnabled() && strategy.getCleanupRetentionDays() < 0) {
            throw new IllegalArgumentException("清理保留天数不能为负数");
        }
        
        // 验证压缩格式
        if (strategy.getCompressionFormat() != null) {
            List<String> validFormats = List.of("NONE", "GZIP", "ZSTD", "LZ4");
            if (!validFormats.contains(strategy.getCompressionFormat())) {
                throw new IllegalArgumentException("无效的压缩格式: " + strategy.getCompressionFormat());
            }
        }
        
        // 验证加密配置
        if (strategy.getEncryptionEnabled() && 
            (strategy.getEncryptionAlgorithm() == null || strategy.getEncryptionAlgorithm().isEmpty())) {
            throw new IllegalArgumentException("启用加密时必须指定加密算法");
        }
    }
}