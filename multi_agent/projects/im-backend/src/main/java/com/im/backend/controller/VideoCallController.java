package com.im.backend.controller;

import com.im.backend.dto.VideoCallRequest;
import com.im.backend.dto.VideoCallResponse;
import com.im.backend.service.VideoCallService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 视频通话控制器
 */
@RestController
@RequestMapping("/api/video-call")
public class VideoCallController {

    private final VideoCallService videoCallService;

    public VideoCallController(VideoCallService videoCallService) {
        this.videoCallService = videoCallService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<VideoCallResponse> initiateCall(
            @RequestHeader("X-User-Id") Long callerId,
            @RequestBody VideoCallRequest request) {
        return ResponseEntity.ok(videoCallService.initiateCall(callerId, request));
    }

    @PostMapping("/{callId}/accept")
    public ResponseEntity<VideoCallResponse> acceptCall(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String callId) {
        return ResponseEntity.ok(videoCallService.acceptCall(userId, callId));
    }

    @PostMapping("/{callId}/reject")
    public ResponseEntity<VideoCallResponse> rejectCall(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String callId) {
        return ResponseEntity.ok(videoCallService.rejectCall(userId, callId));
    }

    @PostMapping("/{callId}/end")
    public ResponseEntity<VideoCallResponse> endCall(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String callId) {
        return ResponseEntity.ok(videoCallService.endCall(userId, callId));
    }

    @PostMapping("/{callId}/cancel")
    public ResponseEntity<VideoCallResponse> cancelCall(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String callId) {
        return ResponseEntity.ok(videoCallService.cancelCall(userId, callId));
    }

    @PostMapping("/{callId}/missed")
    public ResponseEntity<VideoCallResponse> markMissed(@PathVariable String callId) {
        return ResponseEntity.ok(videoCallService.markMissed(callId));
    }

    @GetMapping("/{callId}")
    public ResponseEntity<VideoCallResponse> getCall(@PathVariable String callId) {
        return ResponseEntity.ok(videoCallService.getCall(callId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<VideoCallResponse>> getUserCalls(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(videoCallService.getUserCalls(userId, limit));
    }

    @GetMapping("/incoming")
    public ResponseEntity<List<VideoCallResponse>> getIncomingCalls(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(videoCallService.getIncomingCalls(userId));
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveCall(@RequestHeader("X-User-Id") Long userId) {
        return videoCallService.getActiveCall(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCallStats(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(videoCallService.getCallStats(userId));
    }
}
