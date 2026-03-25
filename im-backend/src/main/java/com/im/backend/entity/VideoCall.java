package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 视频通话记录实体
 */
@Entity
@Table(name = "video_calls")
public class VideoCall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String callId; // UUID

    @Column(nullable = false)
    private Long callerId;

    @Column(nullable = false)
    private Long calleeId;

    @Column(nullable = false)
    private String type; // AUDIO, VIDEO

    @Column(nullable = false)
    private String status; // INITIATED, RINGING, ACCEPTED, REJECTED, ENDED, MISSED, CANCELLED

    private LocalDateTime initiatedAt;

    private LocalDateTime ringingAt;

    private LocalDateTime acceptedAt;

    private LocalDateTime endedAt;

    private Long durationSeconds;

    private String endReason; // NORMAL, REJECTED, MISSED, ERROR, TIMEOUT

    private String roomId; // WebRTC room ID

    private String sdpOffer;

    private String sdpAnswer;

    private String iceCandidates; // JSON array

    private String stunServer;

    private String turnServer;

    private String turnUsername;

    private String turnCredential;

    private Integer videoWidth;

    private Integer videoHeight;

    private Integer videoBitrate;

    private Integer audioBitrate;

    private Boolean screenSharing;

    private String errorCode;

    private String errorMessage;

    @PrePersist
    protected void onCreate() {
        if (status == null) status = "INITIATED";
        if (initiatedAt == null) initiatedAt = LocalDateTime.now();
        if (type == null) type = "VIDEO";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCallId() { return callId; }
    public void setCallId(String callId) { this.callId = callId; }

    public Long getCallerId() { return callerId; }
    public void setCallerId(Long callerId) { this.callerId = callerId; }

    public Long getCalleeId() { return calleeId; }
    public void setCalleeId(Long calleeId) { this.calleeId = calleeId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getInitiatedAt() { return initiatedAt; }
    public void setInitiatedAt(LocalDateTime initiatedAt) { this.initiatedAt = initiatedAt; }

    public LocalDateTime getRingingAt() { return ringingAt; }
    public void setRingingAt(LocalDateTime ringingAt) { this.ringingAt = ringingAt; }

    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }

    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }

    public Long getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Long durationSeconds) { this.durationSeconds = durationSeconds; }

    public String getEndReason() { return endReason; }
    public void setEndReason(String endReason) { this.endReason = endReason; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getSdpOffer() { return sdpOffer; }
    public void setSdpOffer(String sdpOffer) { this.sdpOffer = sdpOffer; }

    public String getSdpAnswer() { return sdpAnswer; }
    public void setSdpAnswer(String sdpAnswer) { this.sdpAnswer = sdpAnswer; }

    public String getIceCandidates() { return iceCandidates; }
    public void setIceCandidates(String iceCandidates) { this.iceCandidates = iceCandidates; }

    public String getStunServer() { return stunServer; }
    public void setStunServer(String stunServer) { this.stunServer = stunServer; }

    public String getTurnServer() { return turnServer; }
    public void setTurnServer(String turnServer) { this.turnServer = turnServer; }

    public String getTurnUsername() { return turnUsername; }
    public void setTurnUsername(String turnUsername) { this.turnUsername = turnUsername; }

    public String getTurnCredential() { return turnCredential; }
    public void setTurnCredential(String turnCredential) { this.turnCredential = turnCredential; }

    public Integer getVideoWidth() { return videoWidth; }
    public void setVideoWidth(Integer videoWidth) { this.videoWidth = videoWidth; }

    public Integer getVideoHeight() { return videoHeight; }
    public void setVideoHeight(Integer videoHeight) { this.videoHeight = videoHeight; }

    public Integer getVideoBitrate() { return videoBitrate; }
    public void setVideoBitrate(Integer videoBitrate) { this.videoBitrate = videoBitrate; }

    public Integer getAudioBitrate() { return audioBitrate; }
    public void setAudioBitrate(Integer audioBitrate) { this.audioBitrate = audioBitrate; }

    public Boolean getScreenSharing() { return screenSharing; }
    public void setScreenSharing(Boolean screenSharing) { this.screenSharing = screenSharing; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
