package com.im.server.sequence;

import javax.persistence.*;
import java.util.Date;

/**
 * 消息序列号元数据实体
 */
@Entity
@Table(name = "im_message_sequence", indexes = {
        @Index(name = "idx_sequence_id", columnList = "sequenceId", unique = true),
        @Index(name = "idx_sender_id", columnList = "senderId"),
        @Index(name = "idx_conversation_id", columnList = "conversationId"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_created_at", columnList = "createdAt")
})
public class SequenceMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 序列号ID（唯一标识）
    @Column(nullable = false, unique = true, length = 64)
    private String sequenceId;

    // 发送者ID
    @Column(nullable = false)
    private Long senderId;

    // 会话ID
    @Column(nullable = false, length = 64)
    private String conversationId;

    // 会话类型：SINGLE（单聊）, GROUP（群聊）
    @Column(length = 16)
    private String conversationType;

    // 序列号版本
    @Column
    private Integer version;

    // 序列号类型：SNOWFLAKE, UUID, HYBRID, TIMESTAMP_RANDOM
    @Column(length = 32)
    private String sequenceType;

    // 生成节点（数据中心ID）
    @Column
    private Integer datacenterId;

    // 生成机器（机器ID）
    @Column
    private Integer machineId;

    // 序列号数值
    @Column
    private Long sequenceValue;

    // 父序列号（用于回复链）
    @Column(length = 64)
    private String parentSequenceId;

    // 序列号状态：ACTIVE, USED, EXPIRED, CANCELLED
    @Column(nullable = false, length = 32)
    private String status = "ACTIVE";

    // 创建时间
    @Column(nullable = false)
    private Long createdAt;

    // 更新时间
    @Column
    private Long updatedAt;

    // 过期时间
    @Column
    private Long expireAt;

    // 备注
    @Column(length = 512)
    private String remark;

    // ==================== Getters and Setters ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationType() {
        return conversationType;
    }

    public void setConversationType(String conversationType) {
        this.conversationType = conversationType;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getSequenceType() {
        return sequenceType;
    }

    public void setSequenceType(String sequenceType) {
        this.sequenceType = sequenceType;
    }

    public Integer getDatacenterId() {
        return datacenterId;
    }

    public void setDatacenterId(Integer datacenterId) {
        this.datacenterId = datacenterId;
    }

    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }

    public Long getSequenceValue() {
        return sequenceValue;
    }

    public void setSequenceValue(Long sequenceValue) {
        this.sequenceValue = sequenceValue;
    }

    public String getParentSequenceId() {
        return parentSequenceId;
    }

    public void setParentSequenceId(String parentSequenceId) {
        this.parentSequenceId = parentSequenceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Long expireAt) {
        this.expireAt = expireAt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    // ==================== 辅助方法 ====================

    @Override
    public String toString() {
        return "SequenceMetadata{" +
                "id=" + id +
                ", sequenceId='" + sequenceId + '\'' +
                ", senderId=" + senderId +
                ", conversationId='" + conversationId + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
