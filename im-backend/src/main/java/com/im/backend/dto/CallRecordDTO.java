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
public class CallRecordDTO {
    private Long id;
    private String callId;
    private String callerId;
    private String callerName;
    private String calleeId;
    private String calleeName;
    private String conversationId;
    private String callType;     // AUDIO / VIDEO
    private String status;       // INITIATED / RINGING / ANSWERED / ENDED / MISSED / REJECTED / FAILED
    private LocalDateTime startTime;
    private LocalDateTime answerTime;
    private LocalDateTime endTime;
    private Integer duration;    // 秒
    private Boolean endedByCaller;
}
