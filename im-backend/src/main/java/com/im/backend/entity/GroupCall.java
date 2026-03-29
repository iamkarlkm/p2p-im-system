package com.im.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupCall {
    
    private Long id;
    
    private Long conversationId;
    
    private Long initiatorId;
    
    private String callType;
    
    private String status;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime endedAt;
    
    private Integer maxParticipants;
    
    private Integer currentParticipants;
    
    private String recordingUrl;
    
    private Boolean isRecording;
    
    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_AUDIO = "audio";
    
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_ENDED = "ended";
}
