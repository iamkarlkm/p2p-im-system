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
 * WebRTC参与者管理服务
 * 处理参与者的加入、离开和媒体状态
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebRTCParticipantService {

    private final WebRTCParticipantRepository participantRepository;
    private final WebRTCSessionRepository sessionRepository;
    private final WebRTCEventPublisher eventPublisher;

    /**
     * 添加参与者
     */
    @Transactional
    public WebRTCParticipantEntity addParticipant(AddParticipantRequest request) {
        log.info("Adding participant {} to session {}", 
            request.getUserId(), request.getSessionId());

        // 检查会话是否存在
        WebRTCSessionEntity session = sessionRepository.findById(request.getSessionId())
            .orElseThrow(() -> new RuntimeException("Session not found: " + request.getSessionId()));

        // 检查是否已在会话中
        Optional<WebRTCParticipantEntity> existing = 
            participantRepository.findBySessionIdAndUserId(
                request.getSessionId(), request.getUserId());

        if (existing.isPresent()) {
            WebRTCParticipantEntity p = existing.get();
            if (p.isOnline()) {
                log.warn("Participant {} already in session {}", 
                    request.getUserId(), request.getSessionId());
                return p;
            } else {
                // 重新连接
                p.updateStatus(WebRTCParticipantEntity.ParticipantStatus.CONNECTED);
                p.setConnectionId(request.getConnectionId());
                WebRTCParticipantEntity saved = participantRepository.save(p);
                eventPublisher.publishParticipantReconnected(
                    request.getSessionId(), request.getUserId());
                return saved;
            }
        }

        // 创建新参与者
        WebRTCParticipantEntity participant = WebRTCParticipantEntity.builder()
            .sessionId(request.getSessionId())
            .userId(request.getUserId())
            .connectionId(request.getConnectionId())
            .displayName(request.getDisplayName())
            .avatarUrl(request.getAvatarUrl())
            .role(request.getRole() != null ? request.getRole() : 
                WebRTCParticipantEntity.ParticipantRole.PARTICIPANT)
            .status(WebRTCParticipantEntity.ParticipantStatus.CONNECTED)
            .isAudioEnabled(request.getIsAudioEnabled() != null ? 
                request.getIsAudioEnabled() : true)
            .isVideoEnabled(request.getIsVideoEnabled() != null ? 
                request.getIsVideoEnabled() : true)
            .isScreenSharing(false)
            .isHandRaised(false)
            .ipAddress(request.getIpAddress())
            .userAgent(request.getUserAgent())
            .deviceType(request.getDeviceType())
            .isHost(request.getIsHost() != null ? request.getIsHost() : false)
            .invitedBy(request.getInvitedBy())
            .invitedAt(request.getInvitedBy() != null ? LocalDateTime.now() : null)
            .build();

        WebRTCParticipantEntity saved = participantRepository.save(participant);

        // 更新会话参与者数量
        session.updateParticipantCount(1);
        sessionRepository.save(session);

        eventPublisher.publishParticipantJoined(
            request.getSessionId(), request.getUserId(), request.getDisplayName());

        log.info("Participant {} added to session {}", 
            request.getUserId(), request.getSessionId());

        return saved;
    }

    /**
     * 参与者离开
     */
    @Transactional
    public void removeParticipant(String sessionId, String userId) {
        log.info("Removing participant {} from session {}", userId, sessionId);

        WebRTCParticipantEntity participant = 
            participantRepository.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        participant.leave();
        participantRepository.save(participant);

        // 更新会话参与者数量
        WebRTCSessionEntity session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        session.updateParticipantCount(-1);
        sessionRepository.save(session);

        eventPublisher.publishParticipantLeft(sessionId, userId);

        log.info("Participant {} left session {}", userId, sessionId);
    }

    /**
     * 踢出参与者
     */
    @Transactional
    public void kickParticipant(String sessionId, String userId, String reason) {
        log.info("Kicking participant {} from session {}", userId, sessionId);

        WebRTCParticipantEntity participant = 
            participantRepository.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        participant.kick();
        participantRepository.save(participant);

        // 更新会话参与者数量
        WebRTCSessionEntity session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        session.updateParticipantCount(-1);
        sessionRepository.save(session);

        eventPublisher.publishParticipantKicked(sessionId, userId, reason);

        log.info("Participant {} kicked from session {}", userId, sessionId);
    }

    /**
     * 更新媒体状态
     */
    @Transactional
    public WebRTCParticipantEntity updateMediaState(String sessionId, String userId,
                                                     Boolean audioEnabled, Boolean videoEnabled) {
        WebRTCParticipantEntity participant = 
            participantRepository.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        participant.updateMediaState(audioEnabled, videoEnabled);
        WebRTCParticipantEntity saved = participantRepository.save(participant);

        eventPublisher.publishMediaStateChanged(sessionId, userId, audioEnabled, videoEnabled);

        return saved;
    }

    /**
     * 切换静音状态
     */
    @Transactional
    public boolean toggleMute(String sessionId, String userId) {
        WebRTCParticipantEntity participant = 
            participantRepository.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        boolean newState = !participant.getIsAudioEnabled();
        participant.updateMediaState(newState, null);
        participantRepository.save(participant);

        eventPublisher.publishAudioToggled(sessionId, userId, newState);

        return newState;
    }

    /**
     * 切换视频状态
     */
    @Transactional
    public boolean toggleVideo(String sessionId, String userId) {
        WebRTCParticipantEntity participant = 
            participantRepository.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        boolean newState = !participant.getIsVideoEnabled();
        participant.updateMediaState(null, newState);
        participantRepository.save(participant);

        eventPublisher.publishVideoToggled(sessionId, userId, newState);

        return newState;
    }

    /**
     * 举手
     */
    @Transactional
    public void raiseHand(String sessionId, String userId) {
        WebRTCParticipantEntity participant = 
            participantRepository.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        participant.raiseHand();
        participantRepository.save(participant);

        eventPublisher.publishHandRaised(sessionId, userId);
    }

    /**
     * 放下手
     */
    @Transactional
    public void lowerHand(String sessionId, String userId) {
        WebRTCParticipantEntity participant = 
            participantRepository.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        participant.lowerHand();
        participantRepository.save(participant);

        eventPublisher.publishHandLowered(sessionId, userId);
    }

    /**
     * 开始屏幕共享
     */
    @Transactional
    public void startScreenSharing(String sessionId, String userId) {
        WebRTCParticipantEntity participant = 
            participantRepository.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        participant.startScreenSharing();
        participantRepository.save(participant);

        // 更新会话屏幕共享状态
        WebRTCSessionEntity session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        session.startScreenSharing();
        sessionRepository.save(session);

        eventPublisher.publishScreenShareStarted(sessionId, userId);
    }

    /**
     * 停止屏幕共享
     */
    @Transactional
    public void stopScreenSharing(String sessionId, String userId) {
        WebRTCParticipantEntity participant = 
            participantRepository.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        participant.stopScreenSharing();
        participantRepository.save(participant);

        // 检查是否还有其他人在屏幕共享
        boolean anySharing = participantRepository
            .findBySessionIdAndIsScreenSharingTrue(sessionId)
            .stream()
            .anyMatch(p -> !p.getUserId().equals(userId));

        if (!anySharing) {
            WebRTCSessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
            session.stopScreenSharing();
            sessionRepository.save(session);
        }

        eventPublisher.publishScreenShareStopped(sessionId, userId);
    }

    /**
     * 设置主持人
     */
    @Transactional
    public void setAsHost(String sessionId, String userId) {
        // 取消原主持人
        participantRepository.findBySessionIdAndIsHostTrue(sessionId)
            .ifPresent(oldHost -> {
                oldHost.demoteToParticipant();
                participantRepository.save(oldHost);
            });

        // 设置新主持人
        WebRTCParticipantEntity participant = 
            participantRepository.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        participant.setAsHost();
        participantRepository.save(participant);

        eventPublisher.publishHostChanged(sessionId, userId);
    }

    /**
     * 获取会话参与者列表
     */
    @Transactional(readOnly = true)
    public List<WebRTCParticipantEntity> getParticipants(String sessionId) {
        return participantRepository.findBySessionIdOrderByJoinedAtAsc(sessionId);
    }

    /**
     * 获取在线参与者
     */
    @Transactional(readOnly = true)
    public List<WebRTCParticipantEntity> getOnlineParticipants(String sessionId) {
        return participantRepository.findOnlineParticipants(sessionId);
    }

    /**
     * 获取参与者信息
     */
    @Transactional(readOnly = true)
    public Optional<WebRTCParticipantEntity> getParticipant(String sessionId, String userId) {
        return participantRepository.findBySessionIdAndUserId(sessionId, userId);
    }

    /**
     * 获取举手的参与者
     */
    @Transactional(readOnly = true)
    public List<WebRTCParticipantEntity> getRaisedHands(String sessionId) {
        return participantRepository.findBySessionIdAndIsHandRaisedTrue(sessionId);
    }

    /**
     * 更新网络统计
     */
    @Transactional
    public void updateStats(String sessionId, String userId, 
                           ParticipantStats stats) {
        WebRTCParticipantEntity participant = 
            participantRepository.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        participant.updateStats(stats.getPacketLoss(), stats.getJitter(),
            stats.getRoundTripTime(), stats.getAudioLevel());
        participant.updateVideoInfo(stats.getVideoResolution(), 
            stats.getFrameRate(), stats.getVideoBitrate());
        participantRepository.save(participant);
    }

    // DTO类
    @Data
    @Builder
    public static class AddParticipantRequest {
        private String sessionId;
        private String userId;
        private String connectionId;
        private String displayName;
        private String avatarUrl;
        private WebRTCParticipantEntity.ParticipantRole role;
        private Boolean isAudioEnabled;
        private Boolean isVideoEnabled;
        private String ipAddress;
        private String userAgent;
        private String deviceType;
        private Boolean isHost;
        private String invitedBy;
    }

    @Data
    @Builder
    public static class ParticipantStats {
        private Integer packetLoss;
        private Integer jitter;
        private Integer roundTripTime;
        private Integer audioLevel;
        private String videoResolution;
        private Integer frameRate;
        private Integer videoBitrate;
    }
}
