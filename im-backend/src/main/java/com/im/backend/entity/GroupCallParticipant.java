package com.im.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupCallParticipant {
    
    private Long id;
    
    private Long callId;
    
    private Long userId;
    
    private String status;
    
    private LocalDateTime joinedAt;
    
    private LocalDateTime leftAt;
    
    private Boolean isAudioEnabled;
    
    private Boolean isVideoEnabled;
    
    private Boolean isScreenSharing;
    
    private Boolean isMuted;
    
    public static final String STATUS_JOINED = "joined";
    public static final String STATUS_LEFT = "left";
    public static final String STATUS_PENDING = "pending";
}
