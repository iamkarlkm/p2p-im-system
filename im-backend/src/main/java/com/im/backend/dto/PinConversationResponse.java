package com.im.backend.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PinConversationResponse {
    private boolean success;
    private String message;
    private List<PinnedConversationDTO> pinnedConversations;

    public static PinConversationResponse success(List<PinnedConversationDTO> list) {
        return PinConversationResponse.builder()
                .success(true)
                .message("Operation successful")
                .pinnedConversations(list)
                .build();
    }

    public static PinConversationResponse error(String message) {
        return PinConversationResponse.builder()
                .success(false)
                .message(message)
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PinnedConversationDTO {
        private Long conversationId;
        private String conversationName;
        private Integer sortOrder;
        private LocalDateTime pinnedAt;
        private String pinNote;
    }
}
