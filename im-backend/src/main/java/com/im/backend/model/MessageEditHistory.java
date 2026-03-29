package com.im.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 消息编辑历史实体
 * 记录消息每次编辑的内容和元数据
 * 
 * @author IM Development Team
 * @since 2026-03-27
 */
@Entity
@Table(name = "message_edit_history", indexes = {
    @Index(name = "idx_edit_msg_id", columnList = "message_id"),
    @Index(name = "idx_edit_user_id", columnList = "user_id"),
    @Index(name = "idx_edit_time", columnList = "created_at")
})
public class MessageEditHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 原消息ID
     */
    @NotNull(message = "消息ID不能为空")
    @Column(name = "message_id", nullable = false)
    private Long messageId;

    /**
     * 编辑用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 编辑前的内容
     */
    @NotBlank(message = "原内容不能为空")
    @Size(max = 10000, message = "原内容不能超过10000字符")
    @Column(name = "original_content", nullable = false, length = 10000)
    private String originalContent;

    /**
     * 编辑后的内容
     */
    @NotBlank(message = "新内容不能为空")
    @Size(max = 10000, message = "新内容不能超过10000字符")
    @Column(name = "edited_content", nullable = false, length = 10000)
    private String editedContent;

    /**
     * 编辑原因/备注
     */
    @Size(max = 500, message = "编辑原因不能超过500字符")
    @Column(name = "edit_reason", length = 500)
    private String editReason;

    /**
     * 编辑序号（第几次编辑）
     */
    @NotNull(message = "编辑序号不能为空")
    @Column(name = "edit_sequence", nullable = false)
    private Integer editSequence;

    /**
     * 客户端信息
     */
    @Size(max = 200, message = "客户端信息不能超过200字符")
    @Column(name = "client_info", length = 200)
    private String clientInfo;

    /**
     * IP地址
     */
    @Size(max = 45, message = "IP地址格式错误")
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 编辑类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "edit_type", length = 30)
    private EditType editType = EditType.NORMAL;

    /**
     * 编辑类型枚举
     */
    public enum EditType {
        NORMAL,           // 普通编辑
        CORRECTION,       // 纠错编辑
        FORMATTING,       // 格式调整
        CONTENT_UPDATE,   // 内容更新
        REVERT,           // 回滚编辑
        SYSTEM            // 系统编辑
    }

    // ==================== 构造方法 ====================

    public MessageEditHistory() {
    }

    public MessageEditHistory(Long messageId, Long userId, String originalContent, 
                              String editedContent, Integer editSequence) {
        this.messageId = messageId;
        this.userId = userId;
        this.originalContent = originalContent;
        this.editedContent = editedContent;
        this.editSequence = editSequence;
    }

    // ==================== Getter & Setter ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }

    public String getEditedContent() {
        return editedContent;
    }

    public void setEditedContent(String editedContent) {
        this.editedContent = editedContent;
    }

    public String getEditReason() {
        return editReason;
    }

    public void setEditReason(String editReason) {
        this.editReason = editReason;
    }

    public Integer getEditSequence() {
        return editSequence;
    }

    public void setEditSequence(Integer editSequence) {
        this.editSequence = editSequence;
    }

    public String getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public EditType getEditType() {
        return editType;
    }

    public void setEditType(EditType editType) {
        this.editType = editType;
    }

    // ==================== 业务方法 ====================

    /**
     * 计算内容变化量
     */
    public int getContentChangeLength() {
        if (originalContent == null || editedContent == null) {
            return 0;
        }
        return editedContent.length() - originalContent.length();
    }

    /**
     * 判断是否为内容增加
     */
    public boolean isContentIncreased() {
        return getContentChangeLength() > 0;
    }

    /**
     * 判断是否为内容减少
     */
    public boolean isContentDecreased() {
        return getContentChangeLength() < 0;
    }

    /**
     * 计算编辑耗时（从消息发送到编辑的时间差，外部计算后设置）
     */
    @Transient
    private Long editTimeMillis;

    public Long getEditTimeMillis() {
        return editTimeMillis;
    }

    public void setEditTimeMillis(Long editTimeMillis) {
        this.editTimeMillis = editTimeMillis;
    }

    // ==================== equals & hashCode ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageEditHistory that = (MessageEditHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // ==================== toString ====================

    @Override
    public String toString() {
        return "MessageEditHistory{" +
                "id=" + id +
                ", messageId=" + messageId +
                ", userId=" + userId +
                ", editSequence=" + editSequence +
                ", editType=" + editType +
                ", createdAt=" + createdAt +
                '}';
    }
}
