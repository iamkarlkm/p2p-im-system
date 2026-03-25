package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypingStatusDTO {
    private String conversationId;
    private String conversationType;
    private String userId;
    private String userName;
    private LocalDateTime updatedAt;
}
