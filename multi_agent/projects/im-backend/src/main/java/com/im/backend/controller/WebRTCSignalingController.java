package com.im.backend.controller;

import com.im.backend.dto.SignalRequest;
import com.im.backend.dto.SignalResponse;
import com.im.backend.service.WebRTCSignalingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/webrtc")
@RequiredArgsConstructor
public class WebRTCSignalingController {

    private final WebRTCSignalingService signalingService;

    @PostMapping("/signal")
    public ResponseEntity<SignalResponse> handleSignal(@Valid @RequestBody SignalRequest request) {
        SignalResponse response = signalingService.handleSignal(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/call/invite")
    public ResponseEntity<SignalResponse> inviteCall(@Valid @RequestBody SignalRequest request) {
        request.setSignalType(SignalRequest.TYPE_CALL_INVITE);
        SignalResponse response = signalingService.handleSignal(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/call/end")
    public ResponseEntity<Map<String, Object>> endCall(@RequestBody Map<String, String> body) {
        String roomId = body.get("roomId");
        Long userId = Long.parseLong(body.get("userId"));
        SignalRequest request = SignalRequest.builder()
            .roomId(roomId)
            .userId(userId)
            .signalType(SignalRequest.TYPE_CALL_ENDED)
            .build();
        signalingService.handleSignal(request);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/session/{roomId}")
    public ResponseEntity<?> getSession(@PathVariable String roomId) {
        var session = signalingService.getSessionByRoomId(roomId);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of(
            "roomId", session.getRoomId(),
            "callerId", session.getCallerId(),
            "calleeId", session.getCalleeId(),
            "callType", session.getCallType(),
            "status", session.getStatus().name(),
            "createdAt", session.getCreatedAt()
        ));
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getWebRTCConfig() {
        return ResponseEntity.ok(Map.of(
            "iceServers", java.util.List.of(
                Map.of("urls", "stun:stun.l.google.com:19302"),
                Map.of("urls", "stun:stun1.l.google.com:19302")
            ),
            "iceTransportPolicy", "all",
            "bundlePolicy", "max-bundle",
            "rtcpMuxPolicy", "require"
        ));
    }
}
