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
public class SignalResponse {

    private String roomId;
    private Long fromUserId;
    private Long toUserId;
    private String signalType;
    private String sdp;
    private String sdpType;
    private String candidate;
    private Integer sdpMLineIndex;
    private String sdpMid;
    private String callType;
    private String status;
    private String message;
    private LocalDateTime timestamp;
    private String stunServers;
    private String turnServers;
    private String turnUsername;
    private String turnCredential;
}
