// 消息批量操作请求DTO
package com.im.backend.dto;

import com.im.backend.model.BatchOperationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "消息批量操作请求DTO")
public class BatchMessageOperationRequest {

    @NotEmpty(message = "消息ID列表不能为空")
    @Schema(description = "要批量操作的消息ID列表", required = true)
    private List<String> messageIds;

    @NotNull(message = "操作类型不能为空")
    @Schema(description = "批量操作类型", required = true)
    private BatchOperationType operationType;

    @Schema(description = "目标会话ID（转发操作时需要）")
    private String targetConversationId;

    @Schema(description = "目标用户ID列表（单独转发时需要）")
    private List<String> targetUserIds;

    @Schema(description = "是否保留原消息（复制操作时）")
    private Boolean keepOriginal;

    @Schema(description = "附加参数（各操作类型特定）")
    private Map<String, Object> additionalParams;

    @Schema(description = "操作原因说明")
    private String reason;

    @Schema(description = "是否在操作后删除原消息（移动操作时）")
    private Boolean deleteAfterMove;

    @Schema(description = "是否异步执行（大量消息时推荐）")
    private Boolean asyncExecution;

    @Schema(description = "期望完成时间（定时操作时使用）")
    private LocalDateTime scheduledTime;
}
