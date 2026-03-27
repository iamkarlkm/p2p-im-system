package com.im.entity.multimodal;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息附件实体
 * 支持图像、音频、视频、文档等多种附件类型
 */
@Data
public class MessageAttachment {

    /** 附件ID */
    private String attachmentId;

    /** 消息ID */
    private String messageId;

    /** 附件名称 */
    private String name;

    /** 附件类型: IMAGE-图像, AUDIO-音频, VIDEO-视频, DOCUMENT-文档, FILE-文件 */
    private AttachmentType type;

    /** MIME类型 */
    private String mimeType;

    /** 文件URL */
    private String fileUrl;

    /** 缩略图URL (图像/视频) */
    private String thumbnailUrl;

    /** 文件大小(字节) */
    private Long fileSize;

    /** 文件宽度 (图像/视频) */
    private Integer width;

    /** 文件高度 (图像/视频) */
    private Integer height;

    /** 时长ms (音频/视频) */
    private Long duration;

    /** 文件格式 */
    private String format;

    /** 描述/替代文本 */
    private String description;

    /** OCR文本内容 (图像) */
    private String ocrText;

    /** 转录文本 (音频/视频) */
    private String transcription;

    /** 上传时间 */
    private LocalDateTime uploadTime;

    /** 处理状态 */
    private ProcessingStatus processingStatus;

    /** 处理结果 */
    private String processingResult;

    /** 文件哈希 */
    private String fileHash;

    /**
     * 附件类型枚举
     */
    public enum AttachmentType {
        IMAGE,      // 图像 (jpg, png, gif, webp, svg)
        AUDIO,      // 音频 (mp3, wav, ogg, m4a, flac)
        VIDEO,      // 视频 (mp4, webm, mov, avi)
        DOCUMENT,   // 文档 (pdf, doc, docx, txt, md)
        CODE,       // 代码文件
        SPREADSHEET, // 电子表格
        ARCHIVE,    // 压缩包
        FILE        // 其他文件
    }

    /**
     * 处理状态枚举
     */
    public enum ProcessingStatus {
        PENDING,        // 待处理
        UPLOADING,      // 上传中
        PROCESSING,     // 处理中
        COMPLETED,      // 完成
        FAILED,         // 失败
        QUARANTINED     // 隔离
    }

    /**
     * 检查是否为图像
     */
    public boolean isImage() {
        return type == AttachmentType.IMAGE;
    }

    /**
     * 检查是否为音频
     */
    public boolean isAudio() {
        return type == AttachmentType.AUDIO;
    }

    /**
     * 检查是否为视频
     */
    public boolean isVideo() {
        return type == AttachmentType.VIDEO;
    }

    /**
     * 检查是否为文档
     */
    public boolean isDocument() {
        return type == AttachmentType.DOCUMENT;
    }

    /**
     * 获取文件大小描述
     */
    public String getFileSizeDescription() {
        if (fileSize == null) return "Unknown";
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.2f KB", fileSize / 1024.0);
        if (fileSize < 1024 * 1024 * 1024) return String.format("%.2f MB", fileSize / (1024.0 * 1024));
        return String.format("%.2f GB", fileSize / (1024.0 * 1024 * 1024));
    }

    /**
     * 获取时长描述
     */
    public String getDurationDescription() {
        if (duration == null) return null;
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes % 60, seconds % 60);
        }
        return String.format("%d:%02d", minutes, seconds % 60);
    }

    @Override
    public String toString() {
        return String.format("MessageAttachment[id=%s, type=%s, name=%s, size=%s]",
            attachmentId, type, name, getFileSizeDescription());
    }
}
