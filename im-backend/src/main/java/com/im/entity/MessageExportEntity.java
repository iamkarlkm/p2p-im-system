package com.im.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息批量导出实体
 */
@Entity
@Table(name = "message_exports", indexes = {
    @Index(name = "idx_user_created", columnList = "userId, createdTime DESC"),
    @Index(name = "idx_status_format", columnList = "status, exportFormat"),
    @Index(name = "idx_session_export", columnList = "sessionId, sessionType")
})
public class MessageExportEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 用户ID
    @Column(nullable = false)
    private Long userId;
    
    // 导出名称
    @Column(length = 255)
    private String exportName;
    
    // 导出描述
    @Column(length = 1000)
    private String description;
    
    // 导出格式
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExportFormat exportFormat = ExportFormat.JSON;
    
    // 导出状态
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExportStatus status = ExportStatus.PENDING;
    
    // 会话ID (可为空，表示全局导出)
    @Column
    private String sessionId;
    
    // 会话类型
    @Enumerated(EnumType.STRING)
    @Column
    private SessionType sessionType;
    
    // 开始时间
    @Column
    private LocalDateTime startTime;
    
    // 结束时间
    @Column
    private LocalDateTime endTime;
    
    // 消息数量
    @Column
    private Integer messageCount = 0;
    
    // 文件大小 (字节)
    @Column
    private Long fileSize;
    
    // 文件路径/URL
    @Column(length = 1000)
    private String filePath;
    
    // 导出选项 JSON
    @Column(columnDefinition = "TEXT")
    private String exportOptions;
    
    // 进度 (0-100)
    @Column
    private Integer progress = 0;
    
    // 进度消息
    @Column(length = 500)
    private String progressMessage;
    
    // 创建时间
    @Column(nullable = false)
    private LocalDateTime createdTime = LocalDateTime.now();
    
    // 更新时间
    @Column
    private LocalDateTime updatedTime;
    
    // 完成时间
    @Column
    private LocalDateTime completedTime;
    
    // 错误消息
    @Column(length = 2000)
    private String errorMessage;
    
    // 导出统计 JSON
    @Column(columnDefinition = "TEXT")
    private String exportStats;
    
    public enum ExportFormat {
        JSON, CSV, TXT, PDF
    }
    
    public enum ExportStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED
    }
    
    public enum SessionType {
        PRIVATE_CHAT, GROUP_CHAT, CHANNEL, TOPIC
    }
    
    // 构造方法
    public MessageExportEntity() {}
    
    public MessageExportEntity(Long userId, String exportName, ExportFormat exportFormat) {
        this.userId = userId;
        this.exportName = exportName;
        this.exportFormat = exportFormat;
    }
    
    // Getters 和 Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getExportName() {
        return exportName;
    }
    
    public void setExportName(String exportName) {
        this.exportName = exportName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public ExportFormat getExportFormat() {
        return exportFormat;
    }
    
    public void setExportFormat(ExportFormat exportFormat) {
        this.exportFormat = exportFormat;
    }
    
    public ExportStatus getStatus() {
        return status;
    }
    
    public void setStatus(ExportStatus status) {
        this.status = status;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public SessionType getSessionType() {
        return sessionType;
    }
    
    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public Integer getMessageCount() {
        return messageCount;
    }
    
    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public String getExportOptions() {
        return exportOptions;
    }
    
    public void setExportOptions(String exportOptions) {
        this.exportOptions = exportOptions;
    }
    
    public Integer getProgress() {
        return progress;
    }
    
    public void setProgress(Integer progress) {
        this.progress = progress;
    }
    
    public String getProgressMessage() {
        return progressMessage;
    }
    
    public void setProgressMessage(String progressMessage) {
        this.progressMessage = progressMessage;
    }
    
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
    
    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }
    
    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
    
    public LocalDateTime getCompletedTime() {
        return completedTime;
    }
    
    public void setCompletedTime(LocalDateTime completedTime) {
        this.completedTime = completedTime;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getExportStats() {
        return exportStats;
    }
    
    public void setExportStats(String exportStats) {
        this.exportStats = exportStats;
    }
    
    // 实用方法
    public void markProcessing() {
        this.status = ExportStatus.PROCESSING;
        this.updatedTime = LocalDateTime.now();
        this.progress = 10;
        this.progressMessage = "开始处理导出任务";
    }
    
    public void markCompleted(int messageCount, long fileSize, String filePath) {
        this.status = ExportStatus.COMPLETED;
        this.updatedTime = LocalDateTime.now();
        this.completedTime = LocalDateTime.now();
        this.messageCount = messageCount;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.progress = 100;
        this.progressMessage = "导出完成";
    }
    
    public void markFailed(String errorMessage) {
        this.status = ExportStatus.FAILED;
        this.updatedTime = LocalDateTime.now();
        this.errorMessage = errorMessage;
        this.progress = 0;
        this.progressMessage = "导出失败: " + errorMessage;
    }
    
    public void updateProgress(int progress, String message) {
        this.progress = progress;
        this.progressMessage = message;
        this.updatedTime = LocalDateTime.now();
    }
    
    public boolean isPending() {
        return status == ExportStatus.PENDING;
    }
    
    public boolean isProcessing() {
        return status == ExportStatus.PROCESSING;
    }
    
    public boolean isCompleted() {
        return status == ExportStatus.COMPLETED;
    }
    
    public boolean isFailed() {
        return status == ExportStatus.FAILED;
    }
    
    @Override
    public String toString() {
        return "MessageExportEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", exportName='" + exportName + '\'' +
                ", exportFormat=" + exportFormat +
                ", status=" + status +
                ", sessionId='" + sessionId + '\'' +
                ", messageCount=" + messageCount +
                ", progress=" + progress +
                ", createdTime=" + createdTime +
                '}';
    }
}