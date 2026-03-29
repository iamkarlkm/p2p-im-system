package com.im.backend.webrtc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * WebRTC会话管理服务
 * 处理会话的创建、管理和生命周期
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebRTCSessionService {

    private final WebRTCSessionRepository sessionRepository;
    private final WebRTCParticipantRepository participantRepository;
    private final WebRTCEventPublisher eventPublisher;

    /**
     * 创建新会话
     */
    @Transactional
    public WebRTCSessionEntity createSession(CreateSessionRequest request) {
        log.info("Creating new WebRTC session: {}", request.getRoomName());

        WebRTCSessionEntity session = WebRTCSessionEntity.builder()
            .roomName(request.getRoomName())
            .description(request.getDescription())
            .hostId(request.getHostId())
            .sessionType(request.getSessionType())
            .status(WebRTCSessionEntity.SessionStatus.PENDING)
            .mediaType(request.getMediaType())
            .maxParticipants(request.getMaxParticipants() != null ? 
                request.getMaxParticipants() : 50)
            .currentParticipants(0)
            .isRecording(false)
            .isScreenSharing(false)
            .sfuNodeId(selectSfuNode())
            .stunServers(request.getStunServers())
            .turnServers(request.getTurnServers())
            .enableSimulcast(request.getEnableSimulcast() != null ? 
                request.getEnableSimulcast() : true)
            .enableTcc(request.getEnableTcc() != null ? 
                request.getEnableTcc() : true)
            .enableRemb(request.getEnableRemb() != null ? 
                request.getEnableRemb() : true)
            .videoBitrate(request.getVideoBitrate() != null ? 
                request.getVideoBitrate() : 2500000)
            .audioBitrate(request.getAudioBitrate() != null ? 
                request.getAudioBitrate() : 128000)
            .videoCodec(request.getVideoCodec())
            .audioCodec(request.getAudioCodec())
            .isPublic(request.getIsPublic() != null ? 
                request.getIsPublic() : true)
            .requirePassword(false)
            .scheduledMeetingId(request.getScheduledMeetingId())
            .metadata(request.getMetadata())
            .build();

        // 设置密码
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            session.setPassword(request.getPassword());
        }

        WebRTCSessionEntity saved = sessionRepository.save(session);
        
        // 发布会话创建事件
        eventPublisher.publishSessionCreated(saved);
        
        log.info("WebRTC session created: {} with roomId: {}", 
            saved.getId(), saved.getRoomId());
        
        return saved;
    }

    /**
     * 获取会话信息
     */
    @Transactional(readOnly = true)
    public Optional<WebRTCSessionEntity> getSession(String sessionId) {
        return sessionRepository.findById(sessionId);
    }

    /**
     * 根据房间ID获取会话
     */
    @Transactional(readOnly = true)
    public Optional<WebRTCSessionEntity> getSessionByRoomId(String roomId) {
        return sessionRepository.findByRoomId(roomId);
    }

    /**
     * 启动会话
     */
    @Transactional
    public WebRTCSessionEntity startSession(String sessionId) {
        WebRTCSessionEntity session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        if (session.getStatus() != WebRTCSessionEntity.SessionStatus.PENDING &&
            session.getStatus() != WebRTCSessionEntity.SessionStatus.CONNECTING) {
            throw new RuntimeException("Session cannot be started: " + session.getStatus());
        }

        session.start();
        WebRTCSessionEntity saved = sessionRepository.save(session);
        
        eventPublisher.publishSessionStarted(saved);
        
        log.info("WebRTC session started: {}", sessionId);
        return saved;
    }

    /**
     * 结束会话
     */
    @Transactional
    public WebRTCSessionEntity endSession(String sessionId) {
        WebRTCSessionEntity session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        // 结束所有参与者
        List<WebRTCParticipantEntity> participants = 
            participantRepository.findBySessionIdOrderByJoinedAtAsc(sessionId);
        
        for (WebRTCParticipantEntity p : participants) {
            if (p.isOnline()) {
                p.leave();
                participantRepository.save(p);
                eventPublisher.publishParticipantLeft(sessionId, p.getUserId());
            }
        }

        session.end();
        WebRTCSessionEntity saved = sessionRepository.save(session);
        
        eventPublisher.publishSessionEnded(saved);
        
        log.info("WebRTC session ended: {}", sessionId);
        return saved;
    }

    /**
     * 更新参与者数量
     */
    @Transactional
    public void updateParticipantCount(String sessionId, int delta) {
        WebRTCSessionEntity session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        session.updateParticipantCount(delta);
        sessionRepository.save(session);
    }

    /**
     * 加入会话验证
     */
    @Transactional(readOnly = true)
    public JoinValidationResult validateJoin(String sessionId, String password) {
        WebRTCSessionEntity session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        if (!session.canJoin()) {
            return JoinValidationResult.error("Session is not active");
        }

        if (session.isFull()) {
            return JoinValidationResult.error("Session is full");
        }

        if (!session.verifyPassword(password)) {
            return JoinValidationResult.error("Invalid password");
        }

        return JoinValidationResult.success(session);
    }

    /**
     * 开始录制
     */
    @Transactional
    public WebRTCSessionEntity startRecording(String sessionId, String recordingUrl) {
        WebRTCSessionEntity session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        session.startRecording(recordingUrl);
        WebRTCSessionEntity saved = sessionRepository.save(session);
        
        eventPublisher.publishRecordingStarted(sessionId, recordingUrl);
        
        log.info("Recording started for session: {}", sessionId);
        return saved;
    }

    /**
     * 停止录制
     */
    @Transactional
    public WebRTCSessionEntity stopRecording(String sessionId) {
        WebRTCSessionEntity session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        session.stopRecording();
        WebRTCSessionEntity saved = sessionRepository.save(session);
        
        eventPublisher.publishRecordingStopped(sessionId);
        
        log.info("Recording stopped for session: {}", sessionId);
        return saved;
    }

    /**
     * 更新屏幕共享状态
     */
    @Transactional
    public void updateScreenSharing(String sessionId, boolean isSharing) {
        WebRTCSessionEntity session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        if (isSharing) {
            session.startScreenSharing();
        } else {
            session.stopScreenSharing();
        }
        sessionRepository.save(session);
    }

    /**
     * 获取活跃会话列表
     */
    @Transactional(readOnly = true)
    public List<WebRTCSessionEntity> getActiveSessions() {
        return sessionRepository.findActiveSessions();
    }

    /**
     * 获取用户的会话
     */
    @Transactional(readOnly = true)
    public List<WebRTCSessionEntity> getUserSessions(String userId) {
        return sessionRepository.findByHostIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 获取公开会话
     */
    @Transactional(readOnly = true)
    public List<WebRTCSessionEntity> getPublicSessions() {
        return sessionRepository.findByIsPublicTrueAndStatus(
            WebRTCSessionEntity.SessionStatus.ACTIVE);
    }

    /**
     * 搜索会话
     */
    @Transactional(readOnly = true)
    public List<WebRTCSessionEntity> searchSessions(String keyword) {
        return sessionRepository.searchByKeyword(keyword);
    }

    /**
     * 获取会话统计
     */
    @Transactional(readOnly = true)
    public SessionStatistics getStatistics() {
        long total = sessionRepository.count();
        long active = sessionRepository.countByStatus(
            WebRTCSessionEntity.SessionStatus.ACTIVE);
        long pending = sessionRepository.countByStatus(
            WebRTCSessionEntity.SessionStatus.PENDING);
        
        return SessionStatistics.builder()
            .totalSessions(total)
            .activeSessions(active)
            .pendingSessions(pending)
            .build();
    }

    /**
     * 选择SFU节点
     */
    private String selectSfuNode() {
        // 实际实现中应该根据负载选择最优节点
        return "sfu-node-1";
    }

    // DTO类
    @Data
    @Builder
    public static class CreateSessionRequest {
        private String roomName;
        private String description;
        private String hostId;
        private WebRTCSessionEntity.SessionType sessionType;
        private WebRTCSessionEntity.MediaType mediaType;
        private Integer maxParticipants;
        private String stunServers;
        private String turnServers;
        private Boolean enableSimulcast;
        private Boolean enableTcc;
        private Boolean enableRemb;
        private Integer videoBitrate;
        private Integer audioBitrate;
        private String videoCodec;
        private String audioCodec;
        private Boolean isPublic;
        private String password;
        private String scheduledMeetingId;
        private String metadata;
    }

    @Data
    @Builder
    public static class JoinValidationResult {
        private boolean valid;
        private String errorMessage;
        private WebRTCSessionEntity session;

        public static JoinValidationResult success(WebRTCSessionEntity session) {
            return JoinValidationResult.builder()
                .valid(true)
                .session(session)
                .build();
        }

        public static JoinValidationResult error(String message) {
            return JoinValidationResult.builder()
                .valid(false)
                .errorMessage(message)
                .build();
        }
    }

    @Data
    @Builder
    public static class SessionStatistics {
        private long totalSessions;
        private long activeSessions;
        private long pendingSessions;
    }
}
