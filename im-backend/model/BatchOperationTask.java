// 批量操作任务实体
package com.im.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "batch_operation_tasks", indexes = {
    @Index(name = "idx_batch_operator", columnList = "operatorId"),
    @Index(name = "idx_batch_status", columnList = "status"),
    @Index(name = "idx_batch_created", columnList = "createdAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchOperationTask {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 36)
    private String operatorId;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private BatchOperationType operationType;

    @Column(nullable = false)
    private Integer totalCount;

    private Integer successCount;
    private Integer failureCount;
    private Integer skippedCount;

    @ElementCollection
    @CollectionTable(name = "batch_task_message_ids", joinColumns = @JoinColumn(name = "taskId"))
    @Column(name = "messageId", length = 36)
    private List<String> messageIds;

    @ElementCollection
    @CollectionTable(name = "batch_task_success_ids", joinColumns = @JoinColumn(name = "taskId"))
    @Column(name = "messageId", length = 36)
    private List<String> successMessageIds;

    @Column(length = 36)
    private String targetConversationId;

    @Column(length = 500)
    private String reason;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(length = 1000)
    private String errorMessage;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationMs;

    private Boolean asyncExecution;

    @Column(length = 500)
    private String additionalParams;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum TaskStatus {
        PENDING, RUNNING, COMPLETED, PARTIAL_SUCCESS, FAILED, CANCELLED
    }

    public void incrementSuccess() {
        if (this.successCount == null) this.successCount = 0;
        this.successCount++;
    }

    public void incrementFailure() {
        if (this.failureCount == null) this.failureCount = 0;
        this.failureCount++;
    }

    public void incrementSkipped() {
        if (this.skippedCount == null) this.skippedCount = 0;
        this.skippedCount++;
    }
}
