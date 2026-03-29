package com.im.server.call;

import lombok.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallRecord {
    private String recordId;
    private String callerId;
    private String calleeId;
    private String conversationId;
    private CallType type;
    private CallStatus status;
    private Instant startTime;
    private Instant endTime;
    private long durationSeconds;
    private String rtcSessionId;

    public enum CallType {
        AUDIO, VIDEO
    }

    public enum CallStatus {
        MISSED, ANSWERED, REJECTED, BUSY, ENDED
    }

    public boolean isMissed() {
        return status == CallStatus.MISSED;
    }

    public long getDurationSeconds() {
        if (endTime == null || startTime == null) return 0;
        return endTime.getEpochSecond() - startTime.getEpochSecond();
    }
}
