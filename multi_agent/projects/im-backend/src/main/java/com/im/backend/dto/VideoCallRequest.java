package com.im.backend.dto;

/**
 * 视频通话请求DTO
 */
public class VideoCallRequest {

    private Long calleeId;
    private String type; // AUDIO, VIDEO
    private String sdpOffer;
    private String sdpAnswer;
    private String iceCandidate;
    private String iceCandidates; // JSON array
    private String callId;
    private String action; // INITIATE, ACCEPT, REJECT, END, CANCEL, OFFER, ANSWER, ICE

    private String roomId;
    private Boolean screenSharing;
    private Integer videoWidth;
    private Integer videoHeight;

    // Getters and Setters
    public Long getCalleeId() { return calleeId; }
    public void setCalleeId(Long calleeId) { this.calleeId = calleeId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSdpOffer() { return sdpOffer; }
    public void setSdpOffer(String sdpOffer) { this.sdpOffer = sdpOffer; }

    public String getSdpAnswer() { return sdpAnswer; }
    public void setSdpAnswer(String sdpAnswer) { this.sdpAnswer = sdpAnswer; }

    public String getIceCandidate() { return iceCandidate; }
    public void setIceCandidate(String iceCandidate) { this.iceCandidate = iceCandidate; }

    public String getIceCandidates() { return iceCandidates; }
    public void setIceCandidates(String iceCandidates) { this.iceCandidates = iceCandidates; }

    public String getCallId() { return callId; }
    public void setCallId(String callId) { this.callId = callId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public Boolean getScreenSharing() { return screenSharing; }
    public void setScreenSharing(Boolean screenSharing) { this.screenSharing = screenSharing; }

    public Integer getVideoWidth() { return videoWidth; }
    public void setVideoWidth(Integer videoWidth) { this.videoWidth = videoWidth; }

    public Integer getVideoHeight() { return videoHeight; }
    public void setVideoHeight(Integer videoHeight) { this.videoHeight = videoHeight; }
}
