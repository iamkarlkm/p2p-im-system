package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignalRequest {

    @NotBlank(message = "Room ID is required")
    private String roomId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Signal type is required")
    private String signalType;

    // For offer: SDP
    private String sdp;
    private String sdpType; // offer, answer, pranswer, rollback

    // For ICE candidate
    private String candidate;
    private Integer sdpMLineIndex;
    private String sdpMid;

    // Call initiation
    private Long targetUserId;
    private String callType; // AUDIO, VIDEO

    // Call control
    private String action; // accept, reject, cancel, end

    public static final String TYPE_OFFER = "offer";
    public static final String TYPE_ANSWER = "answer";
    public static final String TYPE_ICE_CANDIDATE = "ice_candidate";
    public static final String TYPE_CALL_INVITE = "call_invite";
    public static final String TYPE_CALL_ACCEPTED = "call_accepted";
    public static final String TYPE_CALL_REJECTED = "call_rejected";
    public static final String TYPE_CALL_CANCELLED = "call_cancelled";
    public static final String TYPE_CALL_ENDED = "call_ended";
    public static final String TYPE_RINGING = "ringing";
    public static final String TYPE_BUSY = "busy";
    public static final String TYPE_NO_ANSWER = "no_answer";
}
