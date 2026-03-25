package com.im.backend.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForwardRequest {
    @NotEmpty(message = "Messages cannot be empty")
    private List<Long> messageIds;

    @NotNull(message = "Target conversation is required")
    private Long targetConversationId;

    private String comment;

    private boolean merged = false;

    private String mergedTitle;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class BatchForwardRequest {
    @NotEmpty(message = "At least one target is required")
    private List<Long> targetConversationIds;

    @NotEmpty(message = "Messages cannot be empty")
    private List<Long> messageIds;

    private boolean merged = false;

    private String mergedTitle;
}
