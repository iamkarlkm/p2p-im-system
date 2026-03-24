package com.im.backend.service;

import com.im.backend.dto.VideoCallRequest;
import com.im.backend.dto.VideoCallResponse;
import com.im.backend.entity.VideoCall;
import com.im.backend.repository.VideoCallRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class VideoCallService {

    private final VideoCallRepository videoCallRepository;

    private static final String DEFAULT_STUN = "stun:stun.l.google.com:19302";
    private static final String DEFAULT_TURN = "turn:turn.example.com:3478";
    private static final String DEFAULT_TURN_USER = "webrtc_user";
    private static final String DEFAULT_TURN_PASS = "webrtc_password";

    public VideoCallService(VideoCallRepository videoCallRepository) {
        this.videoCallRepository = videoCallRepository;
    }

    @Transactional
    public VideoCallResponse initiateCall(Long callerId, VideoCallRequest request) {
        Optional<VideoCall> activeCall = videoCallRepository.findActiveCall(callerId);
        if (activeCall.isPresent()) {
            throw new RuntimeException("You already have an active call");
        }

        String callId = UUID.randomUUID().toString();
        String roomId = "room_" + UUID.randomUUID().toString().substring(0, 8);

        VideoCall call = new VideoCall();
        call.setCallId(callId);
        call.setCallerId(callerId);
        call.setCalleeId(request.getCalleeId());
        call.setType(request.getType() != null ? request.getType() : "VIDEO");
        call.setStatus("INITIATED");
        call.setInitiatedAt(LocalDateTime.now());
        call.setRoomId(roomId);
        call.setSdpOffer(request.getSdpOffer());
        call.setScreenSharing(request.getScreenSharing() != null ? request.getScreenSharing() : false);
        call.setVideoWidth(request.getVideoWidth() != null ? request.getVideoWidth() : 1280);
        call.setVideoHeight(request.getVideoHeight() != null ? request.getVideoHeight() : 720);
        call.setStunServer(DEFAULT_STUN);
        call.setTurnServer(DEFAULT_TURN);
        call.setTurnUsername(DEFAULT_TURN_USER);
        call.setTurnCredential(DEFAULT_TURN_PASS);
        call = videoCallRepository.save(call);

        return toResponse(call);
    }

    @Transactional
    public VideoCallResponse acceptCall(Long userId, String callId) {
        VideoCall call = videoCallRepository.findByCallId(callId)
                .orElseThrow(() -> new RuntimeException("Call not found: " + callId));

        if (!call.getCalleeId().equals(userId)) {
            throw new RuntimeException("Not authorized to accept this call");
        }
        if (!"INITIATED".equals(call.getStatus()) && !"RINGING".equals(call.getStatus())) {
            throw new RuntimeException("Call is not in a state that can be accepted");
        }

        call.setStatus("ACCEPTED");
        call.setRingingAt(LocalDateTime.now());
        call.setAcceptedAt(LocalDateTime.now());
        call = videoCallRepository.save(call);

        return toResponse(call);
    }

    @Transactional
    public VideoCallResponse rejectCall(Long userId, String callId) {
        VideoCall call = videoCallRepository.findByCallId(callId)
                .orElseThrow(() -> new RuntimeException("Call not found: " + callId));

        if (!call.getCalleeId().equals(userId)) {
            throw new RuntimeException("Not authorized to reject this call");
        }

        call.setStatus("REJECTED");
        call.setEndedAt(LocalDateTime.now());
        call.setEndReason("REJECTED");
        if (call.getAcceptedAt() == null) call.setRingingAt(LocalDateTime.now());
        call = videoCallRepository.save(call);

        return toResponse(call);
    }

    @Transactional
    public VideoCallResponse endCall(Long userId, String callId) {
        VideoCall call = videoCallRepository.findByCallId(callId)
                .orElseThrow(() -> new RuntimeException("Call not found: " + callId));

        if (!call.getCallerId().equals(userId) && !call.getCalleeId().equals(userId)) {
            throw new RuntimeException("Not authorized to end this call");
        }

        call.setStatus("ENDED");
        call.setEndedAt(LocalDateTime.now());
        call.setEndReason("NORMAL");

        if (call.getAcceptedAt() != null) {
            long duration = Duration.between(call.getAcceptedAt(), call.getEndedAt()).getSeconds();
            call.setDurationSeconds(duration);
        }

        call = videoCallRepository.save(call);
        return toResponse(call);
    }

    @Transactional
    public VideoCallResponse cancelCall(Long userId, String callId) {
        VideoCall call = videoCallRepository.findByCallId(callId)
                .orElseThrow(() -> new RuntimeException("Call not found: " + callId));

        if (!call.getCallerId().equals(userId)) {
            throw new RuntimeException("Not authorized to cancel this call");
        }

        call.setStatus("CANCELLED");
        call.setEndedAt(LocalDateTime.now());
        call.setEndReason("NORMAL");
        call = videoCallRepository.save(call);

        return toResponse(call);
    }

    @Transactional
    public VideoCallResponse markMissed(String callId) {
        VideoCall call = videoCallRepository.findByCallId(callId)
                .orElseThrow(() -> new RuntimeException("Call not found: " + callId));

        if ("INITIATED".equals(call.getStatus()) || "RINGING".equals(call.getStatus())) {
            call.setStatus("MISSED");
            call.setEndedAt(LocalDateTime.now());
            call.setEndReason("MISSED");
            call = videoCallRepository.save(call);
        }
        return toResponse(call);
    }

    public VideoCallResponse getCall(String callId) {
        VideoCall call = videoCallRepository.findByCallId(callId)
                .orElseThrow(() -> new RuntimeException("Call not found: " + callId));
        return toResponse(call);
    }

    public List<VideoCallResponse> getUserCalls(Long userId, int limit) {
        List<VideoCall> calls = videoCallRepository.findUserCalls(userId);
        List<VideoCallResponse> responses = new ArrayList<>();
        int count = 0;
        for (VideoCall call : calls) {
            if (count++ >= limit) break;
            responses.add(toResponse(call));
        }
        return responses;
    }

    public List<VideoCallResponse> getIncomingCalls(Long userId) {
        List<VideoCall> calls = videoCallRepository.findByCalleeIdOrderByInitiatedAtDesc(userId);
        List<VideoCallResponse> responses = new ArrayList<>();
        for (VideoCall call : calls) {
            responses.add(toResponse(call));
        }
        return responses;
    }

    public Optional<VideoCallResponse> getActiveCall(Long userId) {
        return videoCallRepository.findActiveCall(userId).map(this::toResponse);
    }

    public Map<String, Object> getCallStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCalls", videoCallRepository.findUserCalls(userId).size());
        stats.put("outgoingCalls", videoCallRepository.countByCallerIdAndStatus(userId, "ENDED"));
        stats.put("incomingCalls", videoCallRepository.countByCalleeIdAndStatus(userId, "ENDED"));
        stats.put("missedCalls", videoCallRepository.countByCalleeIdAndStatus(userId, "MISSED"));
        stats.put("totalDurationSeconds", videoCallRepository.getTotalCallDuration(userId));
        return stats;
    }

    private VideoCallResponse toResponse(VideoCall call) {
        VideoCallResponse r = new VideoCallResponse();
        r.setId(call.getId());
        r.setCallId(call.getCallId());
        r.setCallerId(call.getCallerId());
        r.setCalleeId(call.getCalleeId());
        r.setType(call.getType());
        r.setStatus(call.getStatus());
        r.setInitiatedAt(call.getInitiatedAt());
        r.setRingingAt(call.getRingingAt());
        r.setAcceptedAt(call.getAcceptedAt());
        r.setEndedAt(call.getEndedAt());
        r.setDurationSeconds(call.getDurationSeconds());
        r.setEndReason(call.getEndReason());
        r.setRoomId(call.getRoomId());
        r.setSdpOffer(call.getSdpOffer());
        r.setSdpAnswer(call.getSdpAnswer());
        r.setIceCandidates(call.getIceCandidates());
        r.setStunServer(call.getStunServer());
        r.setTurnServer(call.getTurnServer());
        r.setTurnUsername(call.getTurnUsername());
        r.setTurnCredential(call.getTurnCredential());
        r.setScreenSharing(call.getScreenSharing());
        r.setVideoWidth(call.getVideoWidth());
        r.setVideoHeight(call.getVideoHeight());
        r.setErrorCode(call.getErrorCode());
        r.setErrorMessage(call.getErrorMessage());
        return r;
    }
}
