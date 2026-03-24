package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyChainRequest {
    
    @NotNull(message = "Conversation ID is required")
    private Long conversationId;
    
    @NotNull(message = "Root message ID is required")
    private Long rootMessageId;
    
    @NotNull(message = "Parent message ID is required")
    private Long parentMessageId;
    
    @Min(value = 0, message = "Depth must be non-negative")
    @Max(value = 10, message = "Depth cannot exceed 10")
    private Integer depth;
    
    private String branchPath;
}
