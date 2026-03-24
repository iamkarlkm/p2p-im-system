package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MergedPushRequest {
    private String userId;
    private String deviceToken;
    private String conversationId;
    private String conversationType;
    private String senderName;
    private String messageContent;
    private String messageType;
    private Integer mergeWindowSeconds;
    private Integer maxMessages;
}

package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MergedPushResponse {
    private Long id;
    private String userId;
    private String conversationId;
    private String conversationType;
    private Integer messageCount;
    private String title;
    private String mergedContent;
    private String senderNames;
    private Instant createdAt;
    private Instant scheduledAt;
    private Instant sentAt;
    private String status;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PushStatsResponse {
        private Long totalMergedMessages;
        private Long totalPushSent;
        private Long totalPushFailed;
        private Double averageMergeRatio;
        private Long savedPushCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BufferStatusResponse {
        private Integer activeBuffers;
        private Integer pendingMerges;
        private Long totalBufferedMessages;
    }
}
