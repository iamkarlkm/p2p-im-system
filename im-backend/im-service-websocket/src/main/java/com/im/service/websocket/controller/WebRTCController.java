package com.im.service.websocket.controller;

import com.im.dto.WebRTCSignalRequest;
import com.im.dto.WebRTCSignalResponse;
import com.im.dto.CallRequest;
import com.im.dto.CallResponse;
import com.im.service.WebRTCService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * WebRTC音视频通话控制器
 * 提供1v1音视频通话的信令服务
 */
@Slf4j
@RestController
@RequestMapping("/api/webrtc")
@RequiredArgsConstructor
public class WebRTCController {

    private final WebRTCService webRTCService;

    /**
     * 发起通话
     */
    @PostMapping("/call")
    public ResponseEntity<CallResponse> initiateCall(
            @RequestHeader("X-User-Id") Long callerId,
            @Valid @RequestBody CallRequest request) {
        log.info("用户 {} 发起{}通话给 {}", callerId, request.getCallType(), request.getCalleeId());
        CallResponse response = webRTCService.initiateCall(callerId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 接受通话
     */
    @PostMapping("/accept/{callId}")
    public ResponseEntity<CallResponse> acceptCall(
            @RequestHeader("X-User-Id") Long calleeId,
            @PathVariable String callId) {
        log.info("用户 {} 接受通话 {}", calleeId, callId);
        CallResponse response = webRTCService.acceptCall(calleeId, callId);
        return ResponseEntity.ok(response);
    }

    /**
     * 拒绝通话
     */
    @PostMapping("/reject/{callId}")
    public ResponseEntity<Void> rejectCall(
            @RequestHeader("X-User-Id") Long calleeId,
            @PathVariable String callId,
            @RequestParam(defaultValue = "USER_REJECTED") String reason) {
        log.info("用户 {} 拒绝通话 {}，原因: {}", calleeId, callId, reason);
        webRTCService.rejectCall(calleeId, callId, reason);
        return ResponseEntity.ok().build();
    }

    /**
     * 结束通话
     */
    @PostMapping("/hangup/{callId}")
    public ResponseEntity<Void> hangupCall(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String callId) {
        log.info("用户 {} 结束通话 {}", userId, callId);
        webRTCService.hangupCall(userId, callId);
        return ResponseEntity.ok().build();
    }

    /**
     * 发送WebRTC信令（Offer/Answer/ICE Candidate）
     */
    @PostMapping("/signal")
    public ResponseEntity<WebRTCSignalResponse> sendSignal(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody WebRTCSignalRequest request) {
        log.debug("用户 {} 发送 {} 信令", userId, request.getSignalType());
        WebRTCSignalResponse response = webRTCService.processSignal(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 切换摄像头
     */
    @PostMapping("/switch-camera/{callId}")
    public ResponseEntity<Void> switchCamera(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String callId,
            @RequestParam String facingMode) {
        log.info("用户 {} 切换摄像头为 {}", userId, facingMode);
        webRTCService.switchCamera(userId, callId, facingMode);
        return ResponseEntity.ok().build();
    }

    /**
     * 切换静音状态
     */
    @PostMapping("/mute/{callId}")
    public ResponseEntity<Void> toggleMute(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String callId,
            @RequestParam Boolean muted) {
        log.info("用户 {} 设置静音状态: {}", userId, muted);
        webRTCService.toggleMute(userId, callId, muted);
        return ResponseEntity.ok().build();
    }

    /**
     * 切换视频状态
     */
    @PostMapping("/video/{callId}")
    public ResponseEntity<Void> toggleVideo(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String callId,
            @RequestParam Boolean videoEnabled) {
        log.info("用户 {} 设置视频状态: {}", userId, videoEnabled);
        webRTCService.toggleVideo(userId, callId, videoEnabled);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取通话状态
     */
    @GetMapping("/status/{callId}")
    public ResponseEntity<CallResponse> getCallStatus(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String callId) {
        CallResponse response = webRTCService.getCallStatus(userId, callId);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取ICE服务器配置
     */
    @GetMapping("/ice-servers")
    public ResponseEntity<Object> getIceServers() {
        Object iceServers = webRTCService.getIceServers();
        return ResponseEntity.ok(iceServers);
    }
}
