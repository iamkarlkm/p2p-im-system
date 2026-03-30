package com.im.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息附件实体 - 管理消息中的文件附件
 * 
 * 功能: 附件存储、类型管理、缩略图生成、过期清理
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_message_attachment")
public class MessageAttachment {
    
    /**
     * 附件ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 关联的消息ID
     */
    private Long messageId;
    
    /**
     * 消息全局唯一标识
     */
    private String messageUuid;
    
    /**
     * 附件类型: 1-图片, 2-语音, 3-视频, 4-文件, 5-位置截图
     */
    private Integer attachmentType;
    
    /**
     * 原始文件名
     */
    private String originalName;
    
    /**
     * 文件存储路径
     */
    private String filePath;
    
    /**
     * 文件URL(可访问地址)
     */
    private String fileUrl;
    
    /**
     * 文件大小(字节)
     */
    private Long fileSize;
    
    /**
     * 文件MIME类型
     */
    private String mimeType;
    
    /**
     * 文件扩展名
     */
    private String fileExt;
    
    /**
     * 文件MD5哈希(用于去重和完整性校验)
     */
    private String fileMd5;
    
    /**
     * 图片宽度(px)
     */
    private Integer imageWidth;
    
    /**
     * 图片高度(px)
     */
    private Integer imageHeight;
    
    /**
     * 缩略图URL
     */
    private String thumbnailUrl;
    
    /**
     * 视频时长(秒)
     */
    private Integer videoDuration;
    
    /**
     * 语音时长(秒)
     */
    private Integer audioDuration;
    
    /**
     * 是否转码完成(视频需要转码)
     */
    private Boolean transcoded;
    
    /**
     * 转码后文件路径
     */
    private String transcodedPath;
    
    /**
     * 存储位置: 1-本地存储, 2-OSS, 3-MinIO
     */
    private Integer storageType;
    
    /**
     * 存储桶名称
     */
    private String bucketName;
    
    /**
     * 文件对象Key
     */
    private String objectKey;
    
    /**
     * 下载次数
     */
    private Integer downloadCount;
    
    /**
     * 最后下载时间
     */
    private LocalDateTime lastDownloadTime;
    
    /**
     * 文件过期时间
     */
    private LocalDateTime expireTime;
    
    /**
     * 上传者ID
     */
    private Long uploaderId;
    
    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;
    
    // ============ 附件类型常量 ============
    
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_AUDIO = 2;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_FILE = 4;
    public static final int TYPE_LOCATION = 5;
    
    // ============ 存储类型常量 ============
    
    public static final int STORAGE_LOCAL = 1;
    public static final int STORAGE_OSS = 2;
    public static final int STORAGE_MINIO = 3;
    
    // ============ 业务方法 ============
    
    /**
     * 检查是否过期
     */
    public boolean isExpired() {
        if (expireTime == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expireTime);
    }
    
    /**
     * 格式化文件大小显示
     */
    public String getFormattedSize() {
        if (fileSize == null || fileSize < 0) {
            return "0 B";
        }
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = fileSize;
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        return String.format("%.2f %s", size, units[unitIndex]);
    }
    
    /**
     * 是否为图片类型
     */
    public boolean isImage() {
        return attachmentType != null && attachmentType == TYPE_IMAGE;
    }
    
    /**
     * 是否为视频类型
     */
    public boolean isVideo() {
        return attachmentType != null && attachmentType == TYPE_VIDEO;
    }
    
    /**
     * 是否为语音类型
     */
    public boolean isAudio() {
        return attachmentType != null && attachmentType == TYPE_AUDIO;
    }
}
