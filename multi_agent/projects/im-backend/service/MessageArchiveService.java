package com.im.backend.service;

import com.im.backend.entity.MessageArchiveEntity;
import com.im.backend.entity.MessageStorageLayerEntity;
import com.im.backend.repository.MessageArchiveRepository;
import com.im.backend.repository.MessageRepository;
import com.im.backend.repository.MessageStorageLayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.GZIPOutputStream;

/**
 * 消息归档服务
 * 负责将冷消息归档到对象存储
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MessageArchiveService {
    
    private final MessageRepository messageRepository;
    private final MessageArchiveRepository messageArchiveRepository;
    private final MessageStorageLayerRepository messageStorageLayerRepository;
    private final MessageStorageLayerService messageStorageLayerService;
    private final ObjectStorageService objectStorageService;
    
    private final ExecutorService archiveExecutor = Executors.newFixedThreadPool(10);
    
    /**
     * 执行归档任务
     */
    @Transactional
    public ArchiveResult archiveMessages(Long strategyId) {
        log.info("开始执行归档任务，策略ID: {}", strategyId);
        
        MessageStorageLayerEntity strategy = messageStorageLayerRepository.findById(strategyId)
            .orElseThrow(() -> new IllegalArgumentException("策略不存在，ID: " + strategyId));
        
        // 检查策略状态
        if (!"ENABLED".equals(strategy.getStatus())) {
            throw new IllegalStateException("策略未启用，ID: " + strategyId);
        }
        
        if (!Boolean.TRUE.equals(strategy.getAutoArchiveEnabled())) {
            throw new IllegalStateException("策略未启用自动归档，ID: " + strategyId);
        }
        
        ArchiveResult result = new ArchiveResult();
        result.setStrategyId(strategyId);
        result.setStrategyName(strategy.getStrategyName());
        result.setStartTime(LocalDateTime.now());
        
        try {
            // 计算归档截止时间
            LocalDateTime archiveThreshold = LocalDateTime.now().minusDays(strategy.getWarmStorageDays());
            
            // 获取需要归档的消息
            List<Object[]> messages = messageRepository.findMessagesForArchive(
                archiveThreshold, 
                strategy.getLastArchiveMessageId(),
                strategy.getArchiveBatchSize()
            );
            
            if (messages.isEmpty()) {
                log.info("没有需要归档的消息，策略ID: {}", strategyId);
                result.setSuccess(true);
                result.setMessage("没有需要归档的消息");
                return result;
            }
            
            log.info("找到 {} 条需要归档的消息，策略ID: {}", messages.size(), strategyId);
            
            // 分批归档（根据并发数）
            int batchSize = Math.min(100, strategy.getArchiveBatchSize() / strategy.getArchiveConcurrency());
            List<List<Object[]>> batches = splitIntoBatches(messages, batchSize);
            
            List<CompletableFuture<ArchiveBatchResult>> futures = new ArrayList<>();
            
            for (int i = 0; i < batches.size(); i++) {
                List<Object[]> batch = batches.get(i);
                int batchIndex = i;
                
                CompletableFuture<ArchiveBatchResult> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return archiveBatch(strategy, batch, batchIndex);
                    } catch (Exception e) {
                        log.error("归档批次失败，批次索引: {}，策略ID: {}", batchIndex, strategyId, e);
                        return ArchiveBatchResult.failure(batchIndex, e.getMessage());
                    }
                }, archiveExecutor);
                
                futures.add(future);
            }
            
            // 等待所有批次完成
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
            );
            
            allFutures.get(30, TimeUnit.MINUTES); // 30分钟超时
            
            // 汇总结果
            long totalArchived = 0;
            long totalSize = 0;
            List<String> errors = new ArrayList<>();
            
            for (CompletableFuture<ArchiveBatchResult> future : futures) {
                ArchiveBatchResult batchResult = future.get();
                if (batchResult.isSuccess()) {
                    totalArchived += batchResult.getArchivedCount();
                    totalSize += batchResult.getArchivedSize();
                } else {
                    errors.add("批次 " + batchResult.getBatchIndex() + ": " + batchResult.getError());
                }
            }
            
            // 更新最后归档消息ID
            Long lastArchivedId = getLastMessageId(messages);
            
            // 更新策略统计
            messageStorageLayerService.updateArchiveStats(strategyId, lastArchivedId, totalArchived, totalSize);
            
            result.setArchivedCount(totalArchived);
            result.setArchivedSize(totalSize);
            result.setBatchCount(batches.size());
            
            if (errors.isEmpty()) {
                result.setSuccess(true);
                result.setMessage(String.format("成功归档 %d 条消息，大小: %d 字节", totalArchived, totalSize));
            } else {
                result.setSuccess(false);
                result.setMessage("部分批次归档失败: " + String.join("; ", errors));
            }
            
            log.info("归档任务完成，策略ID: {}，归档消息: {}，大小: {} 字节", 
                    strategyId, totalArchived, totalSize);
            
        } catch (Exception e) {
            log.error("归档任务失败，策略ID: {}", strategyId, e);
            result.setSuccess(false);
            result.setMessage("归档失败: " + e.getMessage());
            
            // 更新策略错误信息
            messageStorageLayerService.setStrategyError(strategyId, 
                "归档失败: " + e.getMessage());
        } finally {
            result.setEndTime(LocalDateTime.now());
            result.setDurationSeconds(
                java.time.Duration.between(result.getStartTime(), result.getEndTime()).getSeconds()
            );
        }
        
        return result;
    }
    
    /**
     * 归档单个批次
     */
    private ArchiveBatchResult archiveBatch(MessageStorageLayerEntity strategy, 
                                           List<Object[]> messages, 
                                           int batchIndex) throws IOException {
        log.debug("开始归档批次 {}，消息数量: {}，策略ID: {}", 
                batchIndex, messages.size(), strategy.getId());
        
        ArchiveBatchResult result = new ArchiveBatchResult();
        result.setBatchIndex(batchIndex);
        
        // 准备归档数据
        List<MessageArchiveEntity> archiveEntities = new ArrayList<>();
        Map<Long, Object[]> messageMap = new HashMap<>();
        
        for (Object[] row : messages) {
            Long messageId = (Long) row[0];
            String content = (String) row[1];
            LocalDateTime createdAt = ((java.sql.Timestamp) row[2]).toLocalDateTime();
            Long senderId = (Long) row[3];
            Long sessionId = (Long) row[4];
            String messageType = (String) row[5];
            
            MessageArchiveEntity archive = MessageArchiveEntity.builder()
                .messageId(messageId)
                .originalContent(content)
                .originalCreatedAt(createdAt)
                .senderId(senderId)
                .sessionId(sessionId)
                .messageType(messageType)
                .storageStrategyId(strategy.getId())
                .archiveTime(LocalDateTime.now())
                .compressionFormat(strategy.getCompressionFormat())
                .encryptionEnabled(strategy.getEncryptionEnabled())
                .encryptionAlgorithm(strategy.getEncryptionAlgorithm())
                .status("ARCHIVED")
                .build();
            
            archiveEntities.add(archive);
            messageMap.put(messageId, row);
        }
        
        // 保存归档记录
        messageArchiveRepository.saveAll(archiveEntities);
        
        // 准备存储到对象存储的数据
        byte[] archiveData = prepareArchiveData(archiveEntities, strategy);
        
        // 生成存储路径
        String storagePath = generateStoragePath(strategy, batchIndex);
        
        // 上传到对象存储
        boolean uploadSuccess = objectStorageService.uploadArchive(
            strategy.getColdStorageBucket(),
            storagePath,
            archiveData,
            strategy.getCompressionFormat(),
            strategy.getEncryptionEnabled(),
            strategy.getEncryptionAlgorithm()
        );
        
        if (!uploadSuccess) {
            throw new IOException("上传到对象存储失败，路径: " + storagePath);
        }
        
        // 更新归档记录的存储路径
        for (MessageArchiveEntity archive : archiveEntities) {
            archive.setStoragePath(storagePath);
            archive.setStorageBucket(strategy.getColdStorageBucket());
            archive.setArchivedSize(archiveData.length);
            archive.setStatus("UPLOADED");
        }
        
        messageArchiveRepository.saveAll(archiveEntities);
        
        // 标记消息为已归档（可选，取决于是否保留原消息）
        if (Boolean.TRUE.equals(strategy.getAutoCleanupEnabled())) {
            // 暂时不删除原消息，等待清理任务处理
            log.debug("批次 {} 归档完成，等待清理任务，策略ID: {}", batchIndex, strategy.getId());
        }
        
        result.setSuccess(true);
        result.setArchivedCount(archiveEntities.size());
        result.setArchivedSize(archiveData.length);
        
        log.debug("批次 {} 归档完成，归档消息: {}，大小: {} 字节，策略ID: {}", 
                batchIndex, archiveEntities.size(), archiveData.length, strategy.getId());
        
        return result;
    }
    
    /**
     * 准备归档数据
     */
    private byte[] prepareArchiveData(List<MessageArchiveEntity> archives, 
                                     MessageStorageLayerEntity strategy) throws IOException {
        // 将归档记录转换为JSON格式
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"version\":\"1.0\",\"archiveTime\":\"")
                  .append(LocalDateTime.now())
                  .append("\",\"strategyId\":")
                  .append(strategy.getId())
                  .append(",\"strategyName\":\"")
                  .append(strategy.getStrategyName())
                  .append("\",\"messages\":[");
        
        for (int i = 0; i < archives.size(); i++) {
            MessageArchiveEntity archive = archives.get(i);
            if (i > 0) {
                jsonBuilder.append(",");
            }
            
            jsonBuilder.append("{\"messageId\":")
                      .append(archive.getMessageId())
                      .append(",\"content\":")
                      .append(escapeJson(archive.getOriginalContent()))
                      .append(",\"createdAt\":\"")
                      .append(archive.getOriginalCreatedAt())
                      .append("\",\"senderId\":")
                      .append(archive.getSenderId())
                      .append(",\"sessionId\":")
                      .append(archive.getSessionId())
                      .append(",\"messageType\":\"")
                      .append(archive.getMessageType())
                      .append("\"}");
        }
        
        jsonBuilder.append("]}");
        
        String jsonData = jsonBuilder.toString();
        byte[] data = jsonData.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        
        // 应用压缩
        if ("GZIP".equals(strategy.getCompressionFormat())) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
                gzip.write(data);
                gzip.finish();
                data = baos.toByteArray();
            }
        } else if ("ZSTD".equals(strategy.getCompressionFormat())) {
            // TODO: 实现ZSTD压缩
            log.warn("ZSTD压缩暂未实现，使用原始数据");
        } else if ("LZ4".equals(strategy.getCompressionFormat())) {
            // TODO: 实现LZ4压缩
            log.warn("LZ4压缩暂未实现，使用原始数据");
        }
        
        // 应用加密
        if (Boolean.TRUE.equals(strategy.getEncryptionEnabled())) {
            // TODO: 实现加密
            log.warn("加密功能暂未实现，使用未加密数据");
        }
        
        return data;
    }
    
    /**
     * 生成存储路径
     */
    private String generateStoragePath(MessageStorageLayerEntity strategy, int batchIndex) {
        LocalDateTime now = LocalDateTime.now();
        String prefix = strategy.getColdStoragePrefix();
        if (!prefix.endsWith("/")) {
            prefix += "/";
        }
        
        return String.format("%s%d/%04d/%02d/%02d/batch_%d_%d.json%s",
            prefix,
            now.getYear(),
            now.getMonthValue(),
            now.getDayOfMonth(),
            now.getHour(),
            batchIndex,
            System.currentTimeMillis(),
            "GZIP".equals(strategy.getCompressionFormat()) ? ".gz" : ""
        );
    }
    
    /**
     * 拆分批次
     */
    private List<List<Object[]>> splitIntoBatches(List<Object[]> messages, int batchSize) {
        List<List<Object[]>> batches = new ArrayList<>();
        for (int i = 0; i < messages.size(); i += batchSize) {
            int end = Math.min(i + batchSize, messages.size());
            batches.add(messages.subList(i, end));
        }
        return batches;
    }
    
    /**
     * 获取最后一条消息的ID
     */
    private Long getLastMessageId(List<Object[]> messages) {
        if (messages.isEmpty()) {
            return null;
        }
        return (Long) messages.get(messages.size() - 1)[0];
    }
    
    /**
     * JSON转义
     */
    private String escapeJson(String input) {
        if (input == null) {
            return "null";
        }
        
        // 简单转义，实际应该使用JSON库
        return "\"" + input.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r") + "\"";
    }
    
    /**
     * 归档结果类
     */
    @lombok.Data
    public static class ArchiveResult {
        private Long strategyId;
        private String strategyName;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Long durationSeconds;
        private boolean success;
        private String message;
        private Long archivedCount;
        private Long archivedSize;
        private Integer batchCount;
    }
    
    /**
     * 批次归档结果类
     */
    @lombok.Data
    public static class ArchiveBatchResult {
        private int batchIndex;
        private boolean success;
        private String error;
        private Long archivedCount;
        private Long archivedSize;
        
        public static ArchiveBatchResult failure(int batchIndex, String error) {
            ArchiveBatchResult result = new ArchiveBatchResult();
            result.setBatchIndex(batchIndex);
            result.setSuccess(false);
            result.setError(error);
            return result;
        }
    }
    
    /**
     * 执行自动归档任务（由调度器调用）
     */
    @Transactional
    public AutoArchiveResult executeAutoArchive() {
        log.info("开始执行自动归档任务");
        
        AutoArchiveResult result = new AutoArchiveResult();
        result.setStartTime(LocalDateTime.now());
        
        // 获取需要归档的策略
        List<MessageStorageLayerEntity> strategies = messageStorageLayerService.getStrategiesNeedingArchive();
        
        if (strategies.isEmpty()) {
            log.info("没有需要归档的策略");
            result.setSuccess(true);
            result.setMessage("没有需要归档的策略");
            result.setEndTime(LocalDateTime.now());
            return result;
        }
        
        log.info("找到 {} 个需要归档的策略", strategies.size());
        
        List<ArchiveResult> strategyResults = new ArrayList<>();
        
        for (MessageStorageLayerEntity strategy : strategies) {
            try {
                ArchiveResult strategyResult = archiveMessages(strategy.getId());
                strategyResults.add(strategyResult);
                
                if (!strategyResult.isSuccess()) {
                    log.error("策略归档失败: {} (ID: {})", 
                            strategy.getStrategyName(), strategy.getId());
                }
            } catch (Exception e) {
                log.error("策略归档异常: {} (ID: {})", 
                        strategy.getStrategyName(), strategy.getId(), e);
                
                ArchiveResult errorResult = new ArchiveResult();
                errorResult.setStrategyId(strategy.getId());
                errorResult.setStrategyName(strategy.getStrategyName());
                errorResult.setSuccess(false);
                errorResult.setMessage("执行异常: " + e.getMessage());
                strategyResults.add(errorResult);
            }
        }
        
        // 汇总结果
        long totalArchived = strategyResults.stream()
            .filter(ArchiveResult::isSuccess)
            .mapToLong(r -> r.getArchivedCount() != null ? r.getArchivedCount() : 0)
            .sum();
        
        long totalSize = strategyResults.stream()
            .filter(ArchiveResult::isSuccess)
            .mapToLong(r -> r.getArchivedSize() != null ? r.getArchivedSize() : 0)
            .sum();
        
        long successCount = strategyResults.stream()
            .filter(ArchiveResult::isSuccess)
            .count();
        
        long failureCount = strategyResults.size() - successCount;
        
        result.setStrategyResults(strategyResults);
        result.setTotalArchived(totalArchived);
        result.setTotalSize(totalSize);
        result.setSuccessCount(successCount);
        result.setFailureCount(failureCount);
        result.setTotalStrategies(strategies.size());
        
        if (failureCount == 0) {
            result.setSuccess(true);
            result.setMessage(String.format("自动归档完成，成功处理 %d 个策略，归档 %d 条消息", 
                    successCount, totalArchived));
        } else if (successCount > 0) {
            result.setSuccess(true); // 部分成功也算成功
            result.setMessage(String.format("自动归档部分完成，成功 %d 个策略，失败 %d 个策略，归档 %d 条消息", 
                    successCount, failureCount, totalArchived));
        } else {
            result.setSuccess(false);
            result.setMessage("自动归档全部失败");
        }
        
        result.setEndTime(LocalDateTime.now());
        result.setDurationSeconds(
            java.time.Duration.between(result.getStartTime(), result.getEndTime()).getSeconds()
        );
        
        log.info("自动归档任务完成，成功策略: {}，失败策略: {}，归档消息: {}", 
                successCount, failureCount, totalArchived);
        
        return result;
    }
    
    /**
     * 自动归档结果类
     */
    @lombok.Data
    public static class AutoArchiveResult {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Long durationSeconds;
        private boolean success;
        private String message;
        private List<ArchiveResult> strategyResults;
        private Long totalArchived;
        private Long totalSize;
        private Long successCount;
        private Long failureCount;
        private Integer totalStrategies;
    }
}