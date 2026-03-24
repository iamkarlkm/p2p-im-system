package com.im.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.im.entity.MessageExportEntity;
import com.im.repository.MessageExportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 消息批量导出服务
 */
@Service
public class MessageExportService {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageExportService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String EXPORT_DIR = "exports";
    private static final int MAX_CONCURRENT_EXPORTS = 3;
    
    @Autowired
    private MessageExportRepository messageExportRepository;
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // 导出任务执行器
    private final ExecutorService exportExecutor = Executors.newFixedThreadPool(MAX_CONCURRENT_EXPORTS);
    private final Map<Long, CompletableFuture<Void>> activeExports = new ConcurrentHashMap<>();
    
    /**
     * 创建导出任务
     */
    @Transactional
    public MessageExportEntity createExportTask(Long userId, String exportName, String description,
                                               MessageExportEntity.ExportFormat exportFormat,
                                               String sessionId, MessageExportEntity.SessionType sessionType,
                                               LocalDateTime startTime, LocalDateTime endTime,
                                               Map<String, Object> exportOptions) {
        try {
            MessageExportEntity export = new MessageExportEntity(userId, exportName, exportFormat);
            export.setDescription(description);
            export.setSessionId(sessionId);
            export.setSessionType(sessionType);
            export.setStartTime(startTime);
            export.setEndTime(endTime);
            
            // 序列化导出选项
            if (exportOptions != null && !exportOptions.isEmpty()) {
                export.setExportOptions(objectMapper.writeValueAsString(exportOptions));
            }
            
            // 计算预估消息数量
            if (sessionId != null) {
                int estimatedCount = messageService.estimateMessageCount(sessionId, startTime, endTime);
                export.setMessageCount(estimatedCount);
            }
            
            MessageExportEntity saved = messageExportRepository.save(export);
            logger.info("创建导出任务: id={}, userId={}, format={}", saved.getId(), userId, exportFormat);
            
            // 异步触发导出处理
            triggerExportAsync(saved.getId());
            
            return saved;
        } catch (Exception e) {
            logger.error("创建导出任务失败: userId={}, exportName={}", userId, exportName, e);
            throw new RuntimeException("创建导出任务失败", e);
        }
    }
    
    /**
     * 异步触发导出处理
     */
    private void triggerExportAsync(Long exportId) {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000); // 等待1秒，确保事务提交
                processExport(exportId);
            } catch (Exception e) {
                logger.error("异步触发导出处理失败: exportId={}", exportId, e);
            }
        }, exportExecutor);
    }
    
    /**
     * 处理导出任务
     */
    @Async
    public void processExport(Long exportId) {
        try {
            Optional<MessageExportEntity> optionalExport = messageExportRepository.findById(exportId);
            if (!optionalExport.isPresent()) {
                logger.error("导出任务不存在: exportId={}", exportId);
                return;
            }
            
            MessageExportEntity export = optionalExport.get();
            
            // 标记为处理中
            if (!markAsProcessing(exportId)) {
                logger.warn("无法标记导出任务为处理中: exportId={}", exportId);
                return;
            }
            
            // 执行导出
            activeExports.put(exportId, new CompletableFuture<>());
            try {
                String filePath = performExport(export);
                activeExports.remove(exportId);
                
                // 更新完成状态
                markAsCompleted(exportId, filePath);
                
            } catch (Exception e) {
                activeExports.remove(exportId);
                logger.error("导出处理失败: exportId={}", exportId, e);
                markAsFailed(exportId, e.getMessage());
            }
            
        } catch (Exception e) {
            logger.error("处理导出任务异常: exportId={}", exportId, e);
        }
    }
    
    /**
     * 执行实际导出
     */
    private String performExport(MessageExportEntity export) throws IOException {
        // 更新进度
        updateProgress(export.getId(), 20, "准备导出数据");
        
        // 准备导出目录
        Path exportDir = Paths.get(EXPORT_DIR, String.valueOf(export.getUserId()));
        Files.createDirectories(exportDir);
        
        // 生成文件名
        String timestamp = DATE_FORMATTER.format(LocalDateTime.now());
        String fileName = String.format("%s_%s.%s", 
            export.getExportName().replaceAll("[^a-zA-Z0-9_-]", "_"),
            timestamp,
            export.getExportFormat().name().toLowerCase()
        );
        
        Path filePath = exportDir.resolve(fileName);
        
        // 根据格式执行导出
        switch (export.getExportFormat()) {
            case JSON:
                exportToJson(export, filePath);
                break;
            case CSV:
                exportToCsv(export, filePath);
                break;
            case TXT:
                exportToTxt(export, filePath);
                break;
            case PDF:
                exportToPdf(export, filePath);
                break;
            default:
                throw new IllegalArgumentException("不支持的导出格式: " + export.getExportFormat());
        }
        
        return filePath.toString();
    }
    
    /**
     * 导出为 JSON 格式
     */
    private void exportToJson(MessageExportEntity export, Path filePath) throws IOException {
        updateProgress(export.getId(), 30, "查询消息数据");
        
        // 查询消息数据
        List<Map<String, Object>> messages = messageService.getMessagesForExport(
            export.getUserId(),
            export.getSessionId(),
            export.getStartTime(),
            export.getEndTime()
        );
        
        updateProgress(export.getId(), 60, "生成 JSON 格式");
        
        // 构建导出结构
        ObjectNode exportData = objectMapper.createObjectNode();
        exportData.put("exportId", export.getId());
        exportData.put("userId", export.getUserId());
        exportData.put("exportName", export.getExportName());
        exportData.put("exportFormat", export.getExportFormat().name());
        exportData.put("sessionId", export.getSessionId());
        exportData.put("sessionType", export.getSessionType() != null ? export.getSessionType().name() : null);
        exportData.put("startTime", export.getStartTime() != null ? export.getStartTime().toString() : null);
        exportData.put("endTime", export.getEndTime() != null ? export.getEndTime().toString() : null);
        exportData.put("messageCount", messages.size());
        exportData.put("exportTime", LocalDateTime.now().toString());
        
        // 添加消息数组
        exportData.set("messages", objectMapper.valueToTree(messages));
        
        // 添加统计信息
        ObjectNode stats = objectMapper.createObjectNode();
        stats.put("totalMessages", messages.size());
        
        // 统计消息类型
        Map<String, Long> typeCount = messages.stream()
            .map(msg -> (String) msg.get("messageType"))
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(type -> type, Collectors.counting()));
        
        stats.set("messageTypes", objectMapper.valueToTree(typeCount));
        
        // 统计发送者
        Map<String, Long> senderCount = messages.stream()
            .map(msg -> (String) msg.get("senderId"))
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(sender -> sender, Collectors.counting()));
        
        stats.set("senders", objectMapper.valueToTree(senderCount));
        
        exportData.set("statistics", stats);
        
        updateProgress(export.getId(), 80, "写入文件");
        
        // 写入文件
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), exportData);
        
        // 计算文件大小
        long fileSize = Files.size(filePath);
        
        // 更新统计信息
        export.setExportStats(stats.toString());
        export.setMessageCount(messages.size());
        export.setFileSize(fileSize);
    }
    
    /**
     * 导出为 CSV 格式
     */
    private void exportToCsv(MessageExportEntity export, Path filePath) throws IOException {
        updateProgress(export.getId(), 30, "查询消息数据");
        
        List<Map<String, Object>> messages = messageService.getMessagesForExport(
            export.getUserId(),
            export.getSessionId(),
            export.getStartTime(),
            export.getEndTime()
        );
        
        updateProgress(export.getId(), 60, "生成 CSV 格式");
        
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            // 写入CSV头部
            writer.write("messageId,timestamp,senderId,senderName,messageType,content,attachments,reactions\n");
            
            // 写入数据行
            for (Map<String, Object> message : messages) {
                String line = String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    escapeCsv(message.get("messageId")),
                    escapeCsv(message.get("timestamp")),
                    escapeCsv(message.get("senderId")),
                    escapeCsv(message.get("senderName")),
                    escapeCsv(message.get("messageType")),
                    escapeCsv(message.get("content")),
                    escapeCsv(message.get("attachments")),
                    escapeCsv(message.get("reactions"))
                );
                writer.write(line);
            }
        }
        
        // 计算文件大小
        long fileSize = Files.size(filePath);
        export.setMessageCount(messages.size());
        export.setFileSize(fileSize);
    }
    
    /**
     * 导出为 TXT 格式
     */
    private void exportToTxt(MessageExportEntity export, Path filePath) throws IOException {
        updateProgress(export.getId(), 30, "查询消息数据");
        
        List<Map<String, Object>> messages = messageService.getMessagesForExport(
            export.getUserId(),
            export.getSessionId(),
            export.getStartTime(),
            export.getEndTime()
        );
        
        updateProgress(export.getId(), 60, "生成 TXT 格式");
        
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            // 写入头部信息
            writer.write("=".repeat(80) + "\n");
            writer.write("消息导出记录\n");
            writer.write("=".repeat(80) + "\n");
            writer.write(String.format("导出ID: %s\n", export.getId()));
            writer.write(String.format("导出名称: %s\n", export.getExportName()));
            writer.write(String.format("用户ID: %s\n", export.getUserId()));
            writer.write(String.format("会话ID: %s\n", export.getSessionId()));
            writer.write(String.format("时间范围: %s 至 %s\n", 
                export.getStartTime() != null ? export.getStartTime() : "无限制",
                export.getEndTime() != null ? export.getEndTime() : "无限制"
            ));
            writer.write(String.format("消息数量: %s\n", messages.size()));
            writer.write(String.format("导出时间: %s\n", LocalDateTime.now()));
            writer.write("=".repeat(80) + "\n\n");
            
            // 写入消息内容
            int index = 1;
            for (Map<String, Object> message : messages) {
                writer.write(String.format("消息 #%d\n", index++));
                writer.write(String.format("时间: %s\n", message.get("timestamp")));
                writer.write(String.format("发送者: %s (%s)\n", message.get("senderName"), message.get("senderId")));
                writer.write(String.format("类型: %s\n", message.get("messageType")));
                writer.write(String.format("内容: %s\n", message.get("content")));
                
                if (message.get("attachments") != null) {
                    writer.write(String.format("附件: %s\n", message.get("attachments")));
                }
                
                if (message.get("reactions") != null) {
                    writer.write(String.format("回应: %s\n", message.get("reactions")));
                }
                
                writer.write("-".repeat(40) + "\n\n");
            }
            
            // 写入统计信息
            writer.write("=".repeat(80) + "\n");
            writer.write("统计信息\n");
            writer.write("=".repeat(80) + "\n");
            
            Map<String, Long> typeCount = messages.stream()
                .map(msg -> (String) msg.get("messageType"))
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(type -> type, Collectors.counting()));
            
            for (Map.Entry<String, Long> entry : typeCount.entrySet()) {
                writer.write(String.format("%s: %d 条\n", entry.getKey(), entry.getValue()));
            }
            
            writer.write(String.format("总计: %d 条消息\n", messages.size()));
        }
        
        // 计算文件大小
        long fileSize = Files.size(filePath);
        export.setMessageCount(messages.size());
        export.setFileSize(fileSize);
    }
    
    /**
     * 导出为 PDF 格式
     */
    private void exportToPdf(MessageExportEntity export, Path filePath) throws IOException {
        updateProgress(export.getId(), 30, "查询消息数据");
        
        List<Map<String, Object>> messages = messageService.getMessagesForExport(
            export.getUserId(),
            export.getSessionId(),
            export.getStartTime(),
            export.getEndTime()
        );
        
        updateProgress(export.getId(), 60, "生成 PDF 格式");
        
        // 这里简化处理，实际应用中需要使用PDF库如iText或Apache PDFBox
        // 此处先创建一个包含基本信息的文本文件作为占位符
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write("PDF Export - This is a placeholder for actual PDF export\n");
            writer.write("Export ID: " + export.getId() + "\n");
            writer.write("Export Name: " + export.getExportName() + "\n");
            writer.write("User ID: " + export.getUserId() + "\n");
            writer.write("Message Count: " + messages.size() + "\n");
            writer.write("Generated at: " + LocalDateTime.now() + "\n");
            writer.write("\nNote: Actual PDF generation requires PDF library integration.\n");
        }
        
        // 计算文件大小
        long fileSize = Files.size(filePath);
        export.setMessageCount(messages.size());
        export.setFileSize(fileSize);
    }
    
    /**
     * 转义CSV字段
     */
    private String escapeCsv(Object value) {
        if (value == null) {
            return "";
        }
        String str = value.toString();
        // 转义双引号和换行符
        str = str.replace("\"", "\"\"");
        str = str.replace("\n", "\\n").replace("\r", "\\r");
        return str;
    }
    
    /**
     * 获取导出任务详情
     */
    public MessageExportEntity getExport(Long exportId) {
        return messageExportRepository.findById(exportId)
            .orElseThrow(() -> new RuntimeException("导出任务不存在: " + exportId));
    }
    
    /**
     * 获取用户的导出任务列表
     */
    public Page<MessageExportEntity> getUserExports(Long userId, Pageable pageable) {
        return messageExportRepository.findByUserId(userId, pageable);
    }
    
    /**
     * 获取用户的导出任务列表（按状态过滤）
     */
    public Page<MessageExportEntity> getUserExportsByStatus(Long userId, MessageExportEntity.ExportStatus status, Pageable pageable) {
        return messageExportRepository.findByUserIdAndStatus(userId, status, pageable);
    }
    
    /**
     * 取消导出任务
     */
    @Transactional
    public boolean cancelExport(Long exportId) {
        try {
            int updated = messageExportRepository.cancelPendingExport(exportId, LocalDateTime.now());
            if (updated > 0) {
                // 如果任务正在执行，尝试取消
                CompletableFuture<Void> future = activeExports.get(exportId);
                if (future != null) {
                    future.cancel(true);
                    activeExports.remove(exportId);
                }
                logger.info("取消导出任务: exportId={}", exportId);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("取消导出任务失败: exportId={}", exportId, e);
            throw new RuntimeException("取消导出任务失败", e);
        }
    }
    
    /**
     * 删除导出任务
     */
    @Transactional
    public boolean deleteExport(Long exportId) {
        try {
            Optional<MessageExportEntity> export = messageExportRepository.findById(exportId);
            if (!export.isPresent()) {
                return false;
            }
            
            // 删除文件（如果存在）
            MessageExportEntity exportEntity = export.get();
            if (exportEntity.getFilePath() != null) {
                try {
                    Path filePath = Paths.get(exportEntity.getFilePath());
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    logger.warn("删除导出文件失败: path={}", exportEntity.getFilePath(), e);
                }
            }
            
            // 取消正在执行的任务
            cancelExport(exportId);
            
            // 删除数据库记录
            messageExportRepository.deleteById(exportId);
            logger.info("删除导出任务: exportId={}", exportId);
            return true;
            
        } catch (Exception e) {
            logger.error("删除导出任务失败: exportId={}", exportId, e);
            throw new RuntimeException("删除导出任务失败", e);
        }
    }
    
    /**
     * 获取导出文件
     */
    public File getExportFile(Long exportId) {
        MessageExportEntity export = getExport(exportId);
        if (export.getStatus() != MessageExportEntity.ExportStatus.COMPLETED) {
            throw new RuntimeException("导出任务未完成或已失败");
        }
        
        if (export.getFilePath() == null) {
            throw new RuntimeException("导出文件路径不存在");
        }
        
        File file = new File(export.getFilePath());
        if (!file.exists()) {
            throw new RuntimeException("导出文件不存在");
        }
        
        return file;
    }
    
    /**
     * 清理过期导出记录
     */
    @Transactional
    public int cleanupOldExports(int daysToKeep) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(daysToKeep);
        
        int deletedCompleted = messageExportRepository.deleteOldCompletedExports(cutoffTime);
        int deletedFailed = messageExportRepository.deleteOldFailedExports(cutoffTime);
        
        logger.info("清理过期导出记录: 完成={}, 失败={}", deletedCompleted, deletedFailed);
        return deletedCompleted + deletedFailed;
    }
    
    /**
     * 获取导出统计
     */
    public Map<String, Object> getExportStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取统计摘要
        Object[] summary = messageExportRepository.getUserExportSummary(userId);
        if (summary != null && summary.length >= 4) {
            stats.put("totalExports", summary[0]);
            stats.put("totalMessages", summary[1] != null ? summary[1] : 0);
            stats.put("totalFileSize", summary[2] != null ? summary[2] : 0);
            stats.put("avgProgress", summary[3] != null ? summary[3] : 0);
        }
        
        // 状态分布
        List<Object[]> statusGroups = messageExportRepository.countByStatusGroup(userId);
        Map<String, Long> statusDistribution = new HashMap<>();
        for (Object[] group : statusGroups) {
            statusDistribution.put(group[0].toString(), (Long) group[1]);
        }
        stats.put("statusDistribution", statusDistribution);
        
        // 格式分布
        List<Object[]> formatGroups = messageExportRepository.countByFormatGroup(userId);
        Map<String, Long> formatDistribution = new HashMap<>();
        for (Object[] group : formatGroups) {
            formatDistribution.put(group[0].toString(), (Long) group[1]);
        }
        stats.put("formatDistribution", formatDistribution);
        
        return stats;
    }
    
    /**
     * 更新导出进度
     */
    private void updateProgress(Long exportId, int progress, String message) {
        try {
            int updated = messageExportRepository.updateProgress(
                exportId, progress, message, LocalDateTime.now()
            );
            if (updated > 0) {
                logger.debug("更新导出进度: exportId={}, progress={}, message={}", exportId, progress, message);
            }
        } catch (Exception e) {
            logger.error("更新导出进度失败: exportId={}", exportId, e);
        }
    }
    
    /**
     * 标记为处理中
     */
    private boolean markAsProcessing(Long exportId) {
        try {
            int updated = messageExportRepository.markAsProcessing(exportId, LocalDateTime.now());
            return updated > 0;
        } catch (Exception e) {
            logger.error("标记处理中失败: exportId={}", exportId, e);
            return false;
        }
    }
    
    /**
     * 标记为完成
     */
    private void markAsCompleted(Long exportId, String filePath) {
        try {
            Optional<MessageExportEntity> optionalExport = messageExportRepository.findById(exportId);
            if (optionalExport.isPresent()) {
                MessageExportEntity export = optionalExport.get();
                messageExportRepository.markAsCompleted(
                    exportId, LocalDateTime.now(),
                    export.getMessageCount(), export.getFileSize(),
                    filePath
                );
                logger.info("标记导出完成: exportId={}, filePath={}", exportId, filePath);
            }
        } catch (Exception e) {
            logger.error("标记完成失败: exportId={}", exportId, e);
        }
    }
    
    /**
     * 标记为失败
     */
    private void markAsFailed(Long exportId, String errorMessage) {
        try {
            messageExportRepository.markAsFailed(exportId, LocalDateTime.now(), errorMessage);
            logger.error("标记导出失败: exportId={}, error={}", exportId, errorMessage);
        } catch (Exception e) {
            logger.error("标记失败失败: exportId={}", exportId, e);
        }
    }
    
    /**
     * 获取正在执行的任务
     */
    public List<MessageExportEntity> getActiveExports() {
        return messageExportRepository.findProcessingExports();
    }
    
    /**
     * 批量导出多个会话
     */
    @Transactional
    public List<MessageExportEntity> batchExportSessions(Long userId, List<String> sessionIds, 
                                                        MessageExportEntity.ExportFormat format,
                                                        Map<String, Object> exportOptions) {
        List<MessageExportEntity> exports = new ArrayList<>();
        String timestamp = DATE_FORMATTER.format(LocalDateTime.now());
        
        for (int i = 0; i < sessionIds.size(); i++) {
            String sessionId = sessionIds.get(i);
            String exportName = String.format("会话导出_%s_%d", timestamp, i + 1);
            
            MessageExportEntity export = createExportTask(
                userId, exportName, "批量导出会话 " + (i + 1) + "/" + sessionIds.size(),
                format, sessionId, null, null, null, exportOptions
            );
            
            exports.add(export);
        }
        
        logger.info("批量导出会话: userId={}, count={}", userId, sessionIds.size());
        return exports;
    }
    
    /**
     * 验证导出选项
     */
    public Map<String, Object> validateExportOptions(Map<String, Object> options) {
        Map<String, Object> validated = new HashMap<>(options);
        
        // 设置默认值
        validated.putIfAbsent("includeAttachments", true);
        validated.putIfAbsent("includeReactions", true);
        validated.putIfAbsent("includeMetadata", true);
        validated.putIfAbsent("formatOptions", new HashMap<>());
        validated.putIfAbsent("compression", "none");
        
        // 验证参数
        if (!"none".equals(validated.get("compression")) && 
            !"gzip".equals(validated.get("compression"))) {
            validated.put("compression", "none");
        }
        
        return validated;
    }
}