package com.im.backend.webrtc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WebRTC会话REST API控制器
 * 提供会话管理和控制接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/webrtc")
@RequiredArgsConstructor
public class WebRTCSessionController {

    private final WebRTCSessionService sessionService;
    private final WebRTCParticipantService participantService;
    private final SFUMediaForwardService sfuService;

    /**
     * 创建会话
     */
    @PostMapping("/sessions")
    public ResponseEntity<SessionResponse> createSession(
            @Valid @RequestBody CreateSessionRequest request,
            Principal principal) {
        
        log.info("Creating WebRTC session by user: {}", principal.getName());

        WebRTCSessionService.CreateSessionRequest serviceRequest = 
            WebRTCSessionService.CreateSessionRequest.builder()
                .roomName(request.getRoomName())
                .description(request.getDescription())
                .hostId(principal.getName())
                .sessionType(request.getSessionType())
                .mediaType(request.getMediaType())
                .maxParticipants(request.getMaxParticipants())
                .enableSimulcast(request.getEnableSimulcast())
                .enableTcc(request.getEnableTcc())
                .enableRemb(request.getEnableRemb())
                .videoBitrate(request.getVideoBitrate())
                .audioBitrate(request.getAudioBitrate())
                .isPublic(request.getIsPublic())
                .password(request.getPassword())
                .build();

        WebRTCSessionEntity session = sessionService.createSession(serviceRequest);
        
        return ResponseEntity.ok(SessionResponse.fromEntity(session));
    }

    /**
     * 获取会话信息
     */
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<SessionResponse> getSession(
            @PathVariable String sessionId) {
        
        WebRTCSessionEntity session = sessionService.getSession(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
        
        return ResponseEntity.ok(SessionResponse.fromEntity(session));
    }

    /**
     * 通过房间ID获取会话
     */
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<SessionResponse> getSessionByRoomId(
            @PathVariable String roomId) {
        
        WebRTCSessionEntity session = sessionService.getSessionByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));
        
        return ResponseEntity.ok(SessionResponse.fromEntity(session));
    }

    /**
     * 启动会话
     */
    @PostMapping("/sessions/{sessionId}/start")
    public ResponseEntity<SessionResponse> startSession(
            @PathVariable String sessionId,
            Principal principal) {
        
        WebRTCSessionEntity session = sessionService.getSession(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        
        // 验证权限
        if (!session.getHostId().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }

        WebRTCSessionEntity started = sessionService.startSession(sessionId);
        return ResponseEntity.ok(SessionResponse.fromEntity(started));
    }

    /**
     * 结束会话
     */
    @PostMapping("/sessions/{sessionId}/end")
    public ResponseEntity<Void> endSession(
            @PathVariable String sessionId,
            Principal principal) {
        
        WebRTCSessionEntity session = sessionService.getSession(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        
        // 验证权限
        if (!session.getHostId().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }

        sessionService.endSession(sessionId);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取会话参与者列表
     */
    @GetMapping("/sessions/{sessionId}/participants")
    public ResponseEntity<List<ParticipantResponse>> getParticipants(
            @PathVariable String sessionId) {
        
        List<WebRTCParticipantEntity> participants = 
            participantService.getParticipants(sessionId);
        
        List<ParticipantResponse> response = participants.stream()
            .map(ParticipantResponse::fromEntity)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 踢出参与者
     */
    @PostMapping("/sessions/{sessionId}/participants/{userId}/kick")
    public ResponseEntity<Void> kickParticipant(
            @PathVariable String sessionId,
            @PathVariable String userId,
            @RequestBody KickRequest request,
            Principal principal) {
        
        WebRTCSessionEntity session = sessionService.getSession(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        
        // 验证权限
        if (!session.getHostId().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }

        participantService.kickParticipant(sessionId, userId, request.getReason());
        return ResponseEntity.ok().build();
    }

    /**
     * 设置主持人
     */
    @PostMapping("/sessions/{sessionId}/participants/{userId}/host")
    public ResponseEntity<Void> setHost(
            @PathVariable String sessionId,
            @PathVariable String userId,
            Principal principal) {
        
        WebRTCSessionEntity session = sessionService.getSession(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        
        // 验证权限
        if (!session.getHostId().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }

        participantService.setAsHost(sessionId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 开始录制
     */
    @PostMapping("/sessions/{sessionId}/recording/start")
    public ResponseEntity<SessionResponse> startRecording(
            @PathVariable String sessionId,
            @RequestBody RecordingRequest request,
            Principal principal) {
        
        WebRTCSessionEntity session = sessionService.getSession(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        
        // 验证权限
        if (!session.getHostId().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }

        WebRTCSessionEntity recording = 
            sessionService.startRecording(sessionId, request.getRecordingUrl());
        return ResponseEntity.ok(SessionResponse.fromEntity(recording));
    }

    /**
     * 停止录制
     */
    @PostMapping("/sessions/{sessionId}/recording/stop")
    public ResponseEntity<SessionResponse> stopRecording(
            @PathVariable String sessionId,
            Principal principal) {
        
        WebRTCSessionEntity session = sessionService.getSession(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        
        // 验证权限
        if (!session.getHostId().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }

        WebRTCSessionEntity stopped = sessionService.stopRecording(sessionId);
        return ResponseEntity.ok(SessionResponse.fromEntity(stopped));
    }

    /**
     * 获取会话媒体统计
     */
    @GetMapping("/sessions/{sessionId}/media-stats")
    public ResponseEntity<SFUMediaForwardService.SessionMediaStats> getMediaStats(
            @PathVariable String sessionId) {
        
        SFUMediaForwardService.SessionMediaStats stats = 
            sfuService.getSessionStats(sessionId);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取活跃会话列表
     */
    @GetMapping("/sessions/active")
    public ResponseEntity<List<SessionSummaryResponse>> getActiveSessions() {
        
        List<WebRTCSessionEntity> sessions = sessionService.getActiveSessions();
        
        List<SessionSummaryResponse> response = sessions.stream()
            .map(SessionSummaryResponse::fromEntity)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户会话列表
     */
    @GetMapping("/sessions/my")
    public ResponseEntity<List<SessionSummaryResponse>> getMySessions(
            Principal principal) {
        
        List<WebRTCSessionEntity> sessions = 
            sessionService.getUserSessions(principal.getName());
        
        List<SessionSummaryResponse> response = sessions.stream()
            .map(SessionSummaryResponse::fromEntity)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取公开会话列表
     */
    @GetMapping("/sessions/public")
    public ResponseEntity<List<SessionSummaryResponse>> getPublicSessions() {
        
        List<WebRTCSessionEntity> sessions = sessionService.getPublicSessions();
        
        List<SessionSummaryResponse> response = sessions.stream()
            .map(SessionSummaryResponse::fromEntity)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 搜索会话
     */
    @GetMapping("/sessions/search")
    public ResponseEntity<List<SessionSummaryResponse>> searchSessions(
            @RequestParam String keyword) {
        
        List<WebRTCSessionEntity> sessions = sessionService.searchSessions(keyword);
        
        List<SessionSummaryResponse> response = sessions.stream()
            .map(SessionSummaryResponse::fromEntity)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> getStatistics() {
        
        WebRTCSessionService.SessionStatistics stats = sessionService.getStatistics();
        
        return ResponseEntity.ok(StatisticsResponse.builder()
            .totalSessions(stats.getTotalSessions())
            .activeSessions(stats.getActiveSessions())
            .pendingSessions(stats.getPendingSessions())
            .build());
    }

    // DTO类
    @lombok.Data
    @lombok.Builder
    public static class CreateSessionRequest {
        private String roomName;
        private String description;
        private WebRTCSessionEntity.SessionType sessionType;
        private WebRTCSessionEntity.MediaType mediaType;
        private Integer maxParticipants;
        private Boolean enableSimulcast;
        private Boolean enableTcc;
        private Boolean enableRemb;
        private Integer videoBitrate;
        private Integer audioBitrate;
        private Boolean isPublic;
        private String password;
    }

    @lombok.Data
    @lombok.Builder
    public static class SessionResponse {
        private String id;
        private String roomId;
        private String roomName;
        private String description;
        private String hostId;
        private String sessionType;
        private String status;
        private String mediaType;
        private Integer maxParticipants;
        private Integer currentParticipants;
        private Boolean isRecording;
        private Boolean isScreenSharing;
        private String recordingUrl;
        private Boolean isPublic;
        private Boolean requirePassword;
        private String createdAt;

        public static SessionResponse fromEntity(WebRTCSessionEntity entity) {
            return SessionResponse.builder()
                .id(entity.getId())
                .roomId(entity.getRoomId())
                .roomName(entity.getRoomName())
                .description(entity.getDescription())
                .hostId(entity.getHostId())
                .sessionType(entity.getSessionType().name())
                .status(entity.getStatus().name())
                .mediaType(entity.getMediaType().name())
                .maxParticipants(entity.getMaxParticipants())
                .currentParticipants(entity.getCurrentParticipants())
                .isRecording(entity.getIsRecording())
                .isScreenSharing(entity.getIsScreenSharing())
                .recordingUrl(entity.getRecordingUrl())
                .isPublic(entity.getIsPublic())
                .requirePassword(entity.getRequirePassword())
                .createdAt(entity.getCreatedAt().toString())
                .build();
        }
    }

    @lombok.Data
    @lombok.Builder
    public static class SessionSummaryResponse {
        private String id;
        private String roomId;
        private String roomName;
        private String sessionType;
        private String status;
        private Integer currentParticipants;
        private Integer maxParticipants;
        private Boolean isPublic;
        private Boolean requirePassword;

        public static SessionSummaryResponse fromEntity(WebRTCSessionEntity entity) {
            return SessionSummaryResponse.builder()
                .id(entity.getId())
                .roomId(entity.getRoomId())
                .roomName(entity.getRoomName())
                .sessionType(entity.getSessionType().name())
                .status(entity.getStatus().name())
                .currentParticipants(entity.getCurrentParticipants())
                .maxParticipants(entity.getMaxParticipants())
                .isPublic(entity.getIsPublic())
                .requirePassword(entity.getRequirePassword())
                .build();
        }
    }

    @lombok.Data
    @lombok.Builder
    public static class ParticipantResponse {
        private String id;
        private String userId;
        private String displayName;
        private String avatarUrl;
        private String role;
        private String status;
        private Boolean isAudioEnabled;
        private Boolean isVideoEnabled;
        private Boolean isScreenSharing;
        private Boolean isHandRaised;
        private Boolean isHost;
        private String joinedAt;

        public static ParticipantResponse fromEntity(WebRTCParticipantEntity entity) {
            return ParticipantResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .displayName(entity.getDisplayName())
                .avatarUrl(entity.getAvatarUrl())
                .role(entity.getRole().name())
                .status(entity.getStatus().name())
                .isAudioEnabled(entity.getIsAudioEnabled())
                .isVideoEnabled(entity.getIsVideoEnabled())
                .isScreenSharing(entity.getIsScreenSharing())
                .isHandRaised(entity.getIsHandRaised())
                .isHost(entity.getIsHost())
                .joinedAt(entity.getJoinedAt().toString())
                .build();
        }
    }

    @lombok.Data
    @lombok.Builder
    public static class KickRequest {
        private String reason;
    }

    @lombok.Data
    @lombok.Builder
    public static class RecordingRequest {
        private String recordingUrl;
    }

    @lombok.Data
    @lombok.Builder
    public static class StatisticsResponse {
        private long totalSessions;
        private long activeSessions;
        private long pendingSessions;
    }
}
