package com.im.server.reaction;

import lombok.*;
import java.time.Instant;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageReaction {
    private String reactionId;
    private String messageId;
    private String userId;
    private String emoji;
    private ReactionType type;
    private Instant createdAt;

    public enum ReactionType {
        EMOJI,
        GIF,
        CUSTOM_IMAGE
    }
}
