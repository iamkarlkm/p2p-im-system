// 消息批量操作结果DTO
package com.im.backend.dto;

import com.im.backend.model.BatchOperationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "消息批量操作结果DTO")
public class BatchOperationResultDTO {

    @Schema(description = "操作批次ID")
    private String batchId;

    @Schema(description = "操作类型")
    private BatchOperationType operationType;

    @Schema(description = "总消息数")
    private Integer totalCount;

    @Schema(description = "成功数量")
    private Integer successCount;

    @Schema(description = "失败数量")
    private Integer failureCount;

    @Schema(description = "跳过数量（无权限或已处理）")
    private Integer skippedCount;

    @Schema(description = "成功处理的消息ID列表")
    private List<String> successMessageIds;

    @Schema(description = "失败的消息及原因")
    private List<FailedOperationDTO> failures;

    @Schema(description = "跳过的消息ID及原因")
    private List<SkippedOperationDTO> skipped;

    @Schema(description = "操作开始时间")
    private LocalDateTime startTime;

    @Schema(description = "操作完成时间")
    private LocalDateTime endTime;

    @Schema(description = "操作耗时（毫秒）")
    private Long durationMs;

    @Schema(description = "操作状态")
    private BatchOperationStatus status;

    @Schema(description = "是否异步执行")
    private Boolean asyncExecution;

    @Schema(description = "异步任务ID（异步执行时）")
    private String asyncTaskId;

    @Schema(description = "操作执行人ID")
    private String operatorId;

    @Schema(description = "操作执行人名称")
    private String operatorName;

    @Schema(description = "目标会话ID（转发/移动操作）")
    private String targetConversationId;

    @Schema(description = "额外结果数据")
    private Map<String, Object> extraData;

    @Schema(description = "生成的消息ID列表（转发/复制操作时）")
    private List<String> generatedMessageIds;

    public enum BatchOperationStatus {
        PENDING, RUNNING, COMPLETED, PARTIAL_SUCCESS, FAILED, CANCELLED
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "失败操作详情")
    public static class FailedOperationDTO {
        @Schema(description = "消息ID")
        private String messageId;

        @Schema(description = "错误码")
        private String errorCode;

        @Schema(description = "错误信息")
        private String errorMessage;

        @Schema(description = "失败原因类型")
        private FailureReason reason;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "跳过操作详情")
    public static class SkippedOperationDTO {
        @Schema(description = "消息ID")
        private String messageId;

        @Schema(description = "跳过原因")
        private SkipReason reason;

        @Schema(description = "原因描述")
        private String description;
    }

    public enum FailureReason {
        PERMISSION_DENIED, MESSAGE_NOT_FOUND, ALREADY_DELETED, 
        RECALL_TIMEOUT, NETWORK_ERROR, SERVER_ERROR, VALIDATION_ERROR
    }

    public enum SkipReason {
        ALREADY_PROCESSED, NO_PERMISSION, FILTERED_OUT, DUPLICATE_REQUEST
    }
}
