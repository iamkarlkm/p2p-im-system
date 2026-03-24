package com.im.backend.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PinConversationRequest {
    @NotNull(message = "Conversation ID is required")
    private Long conversationId;

    private Integer sortOrder;

    private String pinNote;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ReorderPinnedRequest {
    @NotEmpty(message = "Conversation IDs are required")
    private Long[] conversationIds;
}
