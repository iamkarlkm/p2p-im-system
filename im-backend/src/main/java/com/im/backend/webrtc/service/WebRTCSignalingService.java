package com.im.backend.webrtc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * WebRTC信令服务
 * 处理SDP offer/answer和ICE candidate交换
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebRTCSignalingService {

    private final WebRTCSignalingRepository signalingRepository;
    private final WebRTCEventPublisher eventPublisher;

    /**
     * 发送SDP offer
     */
    @Transactional
    public WebRTCSignalingEntity sendOffer(String sessionId, String fromUserId,
                                            String toUserId, String sdp) {
        log.debug("Sending offer from {} to {} in session {}", 
            fromUserId, toUserId, sessionId);

        WebRTCSignalingEntity signal = WebRTCSignalingEntity.createOffer(
            sessionId, fromUserId, toUserId, sdp);

        WebRTCSignalingEntity saved = signalingRepository.save(signal);
        
        eventPublisher.publishSignal(sessionId, saved);
        
        return saved;
    }

    /**
     * 发送SDP answer
     */
    @Transactional
    public WebRTCSignalingEntity sendAnswer(String sessionId, String fromUserId,
                                             String toUserId, String sdp) {
        log.debug("Sending answer from {} to {} in session {}", 
            fromUserId, toUserId, sessionId);

        WebRTCSignalingEntity signal = WebRTCSignalingEntity.createAnswer(
            sessionId, fromUserId, toUserId, sdp);

        WebRTCSignalingEntity saved = signalingRepository.save(signal);
        
        eventPublisher.publishSignal(sessionId, saved);
        
        return saved;
    }

    /**
     * 发送ICE candidate
     */
    @Transactional
    public WebRTCSignalingEntity sendIceCandidate(String sessionId, String fromUserId,
                                                   String toUserId, String candidate,
                                                   String sdpMid, Integer sdpMLineIndex) {
        log.debug("Sending ICE candidate from {} to {} in session {}", 
            fromUserId, toUserId, sessionId);

        WebRTCSignalingEntity signal = WebRTCSignalingEntity.createIceCandidate(
            sessionId, fromUserId, toUserId, candidate, sdpMid, sdpMLineIndex);

        WebRTCSignalingEntity saved = signalingRepository.save(signal);
        
        eventPublisher.publishSignal(sessionId, saved);
        
        return saved;
    }

    /**
     * 获取待处理的信令
     */
    @Transactional(readOnly = true)
    public List<WebRTCSignalingEntity> getPendingSignals(String sessionId, String userId) {
        return signalingRepository.findPendingSignalsForUser(sessionId, userId);
    }

    /**
     * 标记信令为已送达
     */
    @Transactional
    public void markDelivered(String signalId) {
        signalingRepository.findById(signalId).ifPresent(signal -> {
            signal.markDelivered();
            signalingRepository.save(signal);
        });
    }

    /**
     * 标记信令为已处理
     */
    @Transactional
    public void markProcessed(String signalId) {
        signalingRepository.findById(signalId).ifPresent(signal -> {
            signal.markProcessed();
            signalingRepository.save(signal);
        });
    }

    /**
     * 获取会话的所有信令
     */
    @Transactional(readOnly = true)
    public List<WebRTCSignalingEntity> getSessionSignals(String sessionId) {
        return signalingRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    /**
     * 获取SDP信令历史
     */
    @Transactional(readOnly = true)
    public List<WebRTCSignalingEntity> getSdpHistory(String sessionId) {
        return signalingRepository.findSdpSignals(sessionId);
    }

    /**
     * 获取ICE候选
     */
    @Transactional(readOnly = true)
    public List<WebRTCSignalingEntity> getIceCandidates(String sessionId, String fromUserId) {
        return signalingRepository.findPendingIceCandidates(sessionId, fromUserId);
    }

    /**
     * 清理过期信令
     */
    @Transactional
    public int cleanupExpiredSignals(int timeoutSeconds) {
        LocalDateTime expiryTime = LocalDateTime.now().minusSeconds(timeoutSeconds);
        return signalingRepository.markExpiredSignals(expiryTime);
    }

    /**
     * 删除会话信令
     */
    @Transactional
    public void deleteSessionSignals(String sessionId) {
        signalingRepository.deleteBySessionId(sessionId);
    }

    /**
     * 重试失败的信令
     */
    @Transactional
    public void retryFailedSignals() {
        LocalDateTime since = LocalDateTime.now().minusMinutes(5);
        List<WebRTCSignalingEntity> retryable = 
            signalingRepository.findRetryableSignals(since);

        for (WebRTCSignalingEntity signal : retryable) {
            signal.setStatus(WebRTCSignalingEntity.SignalStatus.PENDING);
            signal.setRetryCount(signal.getRetryCount() + 1);
            signalingRepository.save(signal);
            
            eventPublisher.publishSignal(signal.getSessionId(), signal);
            
            log.info("Retrying signal {} for session {}", 
                signal.getId(), signal.getSessionId());
        }
    }

    /**
     * 广播参与者加入
     */
    @Transactional
    public void broadcastParticipantJoined(String sessionId, String participantId,
                                            String metadata) {
        WebRTCSignalingEntity signal = WebRTCSignalingEntity.createParticipantJoined(
            sessionId, participantId, metadata);
        
        signalingRepository.save(signal);
        eventPublisher.publishBroadcast(sessionId, signal);
    }

    /**
     * 广播参与者离开
     */
    @Transactional
    public void broadcastParticipantLeft(String sessionId, String participantId,
                                          String reason) {
        WebRTCSignalingEntity signal = WebRTCSignalingEntity.createParticipantLeft(
            sessionId, participantId, reason);
        
        signalingRepository.save(signal);
        eventPublisher.publishBroadcast(sessionId, signal);
    }

    /**
     * 创建并发送媒体控制信令
     */
    @Transactional
    public WebRTCSignalingEntity sendMediaControl(String sessionId, String fromUserId,
                                                   String toUserId, 
                                                   WebRTCSignalingEntity.SignalType type) {
        WebRTCSignalingEntity signal = WebRTCSignalingEntity.builder()
            .sessionId(sessionId)
            .fromUserId(fromUserId)
            .toUserId(toUserId)
            .type(type)
            .payload(type.name())
            .build();

        WebRTCSignalingEntity saved = signalingRepository.save(signal);
        eventPublisher.publishSignal(sessionId, saved);
        
        return saved;
    }

    /**
     * 创建质量报告信令
     */
    @Transactional
    public WebRTCSignalingEntity sendQualityStats(String sessionId, String fromUserId,
                                                   String statsJson) {
        WebRTCSignalingEntity signal = WebRTCSignalingEntity.builder()
            .sessionId(sessionId)
            .fromUserId(fromUserId)
            .type(WebRTCSignalingEntity.SignalType.QUALITY_STATS)
            .payload(statsJson)
            .build();

        return signalingRepository.save(signal);
    }

    /**
     * 获取会话统计
     */
    @Transactional(readOnly = true)
    public SignalingStats getSessionStats(String sessionId) {
        long total = signalingRepository.countBySessionId(sessionId);
        long sdpCount = signalingRepository.countBySessionIdAndType(
            sessionId, WebRTCSignalingEntity.SignalType.OFFER) +
            signalingRepository.countBySessionIdAndType(
                sessionId, WebRTCSignalingEntity.SignalType.ANSWER);
        long iceCount = signalingRepository.countBySessionIdAndType(
            sessionId, WebRTCSignalingEntity.SignalType.ICE_CANDIDATE);

        return SignalingStats.builder()
            .totalSignals(total)
            .sdpExchanges(sdpCount)
            .iceCandidates(iceCount)
            .build();
    }

    @lombok.Data
    @lombok.Builder
    public static class SignalingStats {
        private long totalSignals;
        private long sdpExchanges;
        private long iceCandidates;
    }
}
