package com.im.server.typing;

import lombok.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypingIndicator {
    private String sessionId;
    private String userId;
    private String conversationId;
    private TypingState state;
    private Instant timestamp;

    public enum TypingState {
        STARTED,
        STOPPED
    }
}
