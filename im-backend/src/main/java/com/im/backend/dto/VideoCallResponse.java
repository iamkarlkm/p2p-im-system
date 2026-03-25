package com.im.backend.dto;

import java.time.LocalDateTime;

/**
 * 视频通话响应DTO
 */
public class VideoCallResponse {

    private Long id;
    private String callId;
    private Long callerId;
    private Long calleeId;
    private String type;
    private String status;
    private LocalDateTime initiatedAt;
    private LocalDateTime ringingAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime endedAt;
    private Long durationSeconds;
    private String endReason;
    private String roomId;
    private String sdpOffer;
    private String sdpAnswer;
    private String iceCandidates;
    private String stunServer;
    private String turnServer;
    private String turnUsername;
    private String turnCredential;
    private Boolean screenSharing;
    private Integer videoWidth;
    private Integer videoHeight;
    private String errorCode;
    private String errorMessage;

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

    public Boolean getScreenSharing() { return screenSharing; }
    public void setScreenSharing(Boolean screenSharing) { this.screenSharing = screenSharing; }

    public Integer getVideoWidth() { return videoWidth; }
    public void setVideoWidth(Integer videoWidth) { this.videoWidth = videoWidth; }

    public Integer getVideoHeight() { return videoHeight; }
    public void setVideoHeight(Integer videoHeight) { this.videoHeight = videoHeight; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
