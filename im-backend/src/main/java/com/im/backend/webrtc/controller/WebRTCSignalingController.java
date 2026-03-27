package com.im.backend.webrtc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * WebRTC WebSocket信令控制器
 * 处理实时信令消息：SDP协商、ICE候选、媒体控制
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class WebRTCSignalingController {

    private final WebRTCSignalingService signalingService;
    private final WebRTCSessionService sessionService;
    private final WebRTCParticipantService participantService;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String TOPIC_PREFIX = "/topic/webrtc/";
    private static final String QUEUE_PREFIX = "/queue/webrtc/";
    private static final String USER_PREFIX = "/user/queue/webrtc/";

    /**
     * 发送SDP Offer
     */
    @MessageMapping("/webrtc/{sessionId}/offer")
    public void sendOffer(@DestinationVariable String sessionId,
                          @Payload OfferMessage message,
                          Principal principal) {
        String fromUserId = principal.getName();
        log.debug("Received offer from {} in session {}", fromUserId, sessionId);

        WebRTCSignalingEntity signal = signalingService.sendOffer(
            sessionId, fromUserId, message.getToUserId(), message.getSdp());

        // 发送给目标用户
        if (message.getToUserId() != null) {
            messagingTemplate.convertAndSendToUser(
                message.getToUserId(),
                QUEUE_PREFIX + sessionId + "/signal",
                SignalMessage.fromEntity(signal)
            );
        }
    }

    /**
     * 发送SDP Answer
     */
    @MessageMapping("/webrtc/{sessionId}/answer")
    public void sendAnswer(@DestinationVariable String sessionId,
                           @Payload AnswerMessage message,
                           Principal principal) {
        String fromUserId = principal.getName();
        log.debug("Received answer from {} in session {}", fromUserId, sessionId);

        WebRTCSignalingEntity signal = signalingService.sendAnswer(
            sessionId, fromUserId, message.getToUserId(), message.getSdp());

        if (message.getToUserId() != null) {
            messagingTemplate.convertAndSendToUser(
                message.getToUserId(),
                QUEUE_PREFIX + sessionId + "/signal",
                SignalMessage.fromEntity(signal)
            );
        }
    }

    /**
     * 发送ICE Candidate
     */
    @MessageMapping("/webrtc/{sessionId}/ice")
    public void sendIceCandidate(@DestinationVariable String sessionId,
                                  @Payload IceCandidateMessage message,
                                  Principal principal) {
        String fromUserId = principal.getName();

        WebRTCSignalingEntity signal = signalingService.sendIceCandidate(
            sessionId, fromUserId, message.getToUserId(),
            message.getCandidate(), message.getSdpMid(), message.getSdpMLineIndex());

        if (message.getToUserId() != null) {
            messagingTemplate.convertAndSendToUser(
                message.getToUserId(),
                QUEUE_PREFIX + sessionId + "/ice",
                IceSignalMessage.fromEntity(signal)
            );
        } else {
            // 广播给会话中所有其他用户
            broadcastToSession(sessionId, fromUserId, 
                TOPIC_PREFIX + sessionId + "/ice",
                IceSignalMessage.fromEntity(signal));
        }
    }

    /**
     * 加入会话
     */
    @MessageMapping("/webrtc/{sessionId}/join")
    public void joinSession(@DestinationVariable String sessionId,
                            @Payload JoinMessage message,
                            Principal principal) {
        String userId = principal.getName();
        log.info("User {} joining session {}", userId, sessionId);

        // 验证会话
        WebRTCSessionService.JoinValidationResult validation = 
            sessionService.validateJoin(sessionId, message.getPassword());

        if (!validation.isValid()) {
            messagingTemplate.convertAndSendToUser(
                userId,
                USER_PREFIX + "error",
                ErrorMessage.builder()
                    .code("JOIN_FAILED")
                    .message(validation.getErrorMessage())
                    .build()
            );
            return;
        }

        // 添加参与者
        WebRTCParticipantService.AddParticipantRequest request = 
            WebRTCParticipantService.AddParticipantRequest.builder()
                .sessionId(sessionId)
                .userId(userId)
                .connectionId(message.getConnectionId())
                .displayName(message.getDisplayName())
                .avatarUrl(message.getAvatarUrl())
                .isAudioEnabled(message.getIsAudioEnabled())
                .isVideoEnabled(message.getIsVideoEnabled())
                .deviceType(message.getDeviceType())
                .build();

        WebRTCParticipantEntity participant = participantService.addParticipant(request);

        // 通知用户加入成功
        messagingTemplate.convertAndSendToUser(
            userId,
            QUEUE_PREFIX + sessionId + "/joined",
            JoinedMessage.builder()
                .sessionId(sessionId)
                .participantId(participant.getId())
                .roomId(validation.getSession().getRoomId())
                .build()
        );

        // 广播新用户加入
        broadcastToSession(sessionId, userId,
            TOPIC_PREFIX + sessionId + "/participants/joined",
            ParticipantMessage.fromEntity(participant));

        // 发送现有参与者列表
        List<WebRTCParticipantEntity> existing = 
            participantService.getOnlineParticipants(sessionId);
        
        messagingTemplate.convertAndSendToUser(
            userId,
            QUEUE_PREFIX + sessionId + "/participants/list",
            existing.stream()
                .filter(p -> !p.getUserId().equals(userId))
                .map(ParticipantMessage::fromEntity)
                .collect(Collectors.toList())
        );
    }

    /**
     * 离开会话
     */
    @MessageMapping("/webrtc/{sessionId}/leave")
    public void leaveSession(@DestinationVariable String sessionId,
                             Principal principal) {
        String userId = principal.getName();
        log.info("User {} leaving session {}", userId, sessionId);

        participantService.removeParticipant(sessionId, userId);

        // 广播用户离开
        broadcastToSession(sessionId, null,
            TOPIC_PREFIX + sessionId + "/participants/left",
            LeftMessage.builder().userId(userId).build());
    }

    /**
     * 切换音频
     */
    @MessageMapping("/webrtc/{sessionId}/mute")
    public void toggleMute(@DestinationVariable String sessionId,
                           @Payload MuteMessage message,
                           Principal principal) {
        String userId = principal.getName();
        
        boolean newState = participantService.toggleMute(sessionId, userId);

        // 广播状态变更
        broadcastToSession(sessionId, null,
            TOPIC_PREFIX + sessionId + "/participants/mute",
            MuteStatusMessage.builder()
                .userId(userId)
                .muted(!newState)
                .build());
    }

    /**
     * 切换视频
     */
    @MessageMapping("/webrtc/{sessionId}/video")
    public void toggleVideo(@DestinationVariable String sessionId,
                            @Payload VideoMessage message,
                            Principal principal) {
        String userId = principal.getName();
        
        boolean newState = participantService.toggleVideo(sessionId, userId);

        broadcastToSession(sessionId, null,
            TOPIC_PREFIX + sessionId + "/participants/video",
            VideoStatusMessage.builder()
                .userId(userId)
                .videoEnabled(newState)
                .build());
    }

    /**
     * 举手
     */
    @MessageMapping("/webrtc/{sessionId}/raise-hand")
    public void raiseHand(@DestinationVariable String sessionId,
                          Principal principal) {
        String userId = principal.getName();
        participantService.raiseHand(sessionId, userId);

        broadcastToSession(sessionId, null,
            TOPIC_PREFIX + sessionId + "/participants/hand",
            HandMessage.builder()
                .userId(userId)
                .raised(true)
                .build());
    }

    /**
     * 放下手
     */
    @MessageMapping("/webrtc/{sessionId}/lower-hand")
    public void lowerHand(@DestinationVariable String sessionId,
                          Principal principal) {
        String userId = principal.getName();
        participantService.lowerHand(sessionId, userId);

        broadcastToSession(sessionId, null,
            TOPIC_PREFIX + sessionId + "/participants/hand",
            HandMessage.builder()
                .userId(userId)
                .raised(false)
                .build());
    }

    /**
     * 开始屏幕共享
     */
    @MessageMapping("/webrtc/{sessionId}/screen-share/start")
    public void startScreenShare(@DestinationVariable String sessionId,
                                  Principal principal) {
        String userId = principal.getName();
        participantService.startScreenSharing(sessionId, userId);

        broadcastToSession(sessionId, null,
            TOPIC_PREFIX + sessionId + "/screen-share/started",
            ScreenShareMessage.builder()
                .userId(userId)
                .active(true)
                .build());
    }

    /**
     * 停止屏幕共享
     */
    @MessageMapping("/webrtc/{sessionId}/screen-share/stop")
    public void stopScreenShare(@DestinationVariable String sessionId,
                                 Principal principal) {
        String userId = principal.getName();
        participantService.stopScreenSharing(sessionId, userId);

        broadcastToSession(sessionId, null,
            TOPIC_PREFIX + sessionId + "/screen-share/stopped",
            ScreenShareMessage.builder()
                .userId(userId)
                .active(false)
                .build());
    }

    /**
     * 获取信令消息
     */
    @SubscribeMapping("/webrtc/{sessionId}/signals")
    public List<SignalMessage> getPendingSignals(@DestinationVariable String sessionId,
                                                  Principal principal) {
        String userId = principal.getName();
        
        List<WebRTCSignalingEntity> signals = 
            signalingService.getPendingSignals(sessionId, userId);

        // 标记为已送达
        signals.forEach(s -> signalingService.markDelivered(s.getId()));

        return signals.stream()
            .map(SignalMessage::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * 心跳
     */
    @MessageMapping("/webrtc/{sessionId}/ping")
    public void ping(@DestinationVariable String sessionId,
                     Principal principal) {
        String userId = principal.getName();
        
        messagingTemplate.convertAndSendToUser(
            userId,
            QUEUE_PREFIX + sessionId + "/pong",
            PongMessage.builder()
                .timestamp(System.currentTimeMillis())
                .build()
        );
    }

    // 辅助方法
    private void broadcastToSession(String sessionId, String excludeUserId,
                                     String destination, Object message) {
        List<WebRTCParticipantEntity> participants = 
            participantService.getOnlineParticipants(sessionId);

        for (WebRTCParticipantEntity p : participants) {
            if (excludeUserId != null && p.getUserId().equals(excludeUserId)) {
                continue;
            }
            messagingTemplate.convertAndSendToUser(
                p.getUserId(), destination, message);
        }
    }

    // DTO类
    @lombok.Data
    @lombok.Builder
    public static class OfferMessage {
        private String toUserId;
        private String sdp;
    }

    @lombok.Data
    @lombok.Builder
    public static class AnswerMessage {
        private String toUserId;
        private String sdp;
    }

    @lombok.Data
    @lombok.Builder
    public static class IceCandidateMessage {
        private String toUserId;
        private String candidate;
        private String sdpMid;
        private Integer sdpMLineIndex;
    }

    @lombok.Data
    @lombok.Builder
    public static class JoinMessage {
        private String connectionId;
        private String displayName;
        private String avatarUrl;
        private Boolean isAudioEnabled;
        private Boolean isVideoEnabled;
        private String deviceType;
        private String password;
    }

    @lombok.Data
    @lombok.Builder
    public static class JoinedMessage {
        private String sessionId;
        private String participantId;
        private String roomId;
    }

    @lombok.Data
    @lombok.Builder
    public static class LeftMessage {
        private String userId;
    }

    @lombok.Data
    @lombok.Builder
    public static class MuteMessage {
        private Boolean muted;
    }

    @lombok.Data
    @lombok.Builder
    public static class MuteStatusMessage {
        private String userId;
        private Boolean muted;
    }

    @lombok.Data
    @lombok.Builder
    public static class VideoMessage {
        private Boolean enabled;
    }

    @lombok.Data
    @lombok.Builder
    public static class VideoStatusMessage {
        private String userId;
        private Boolean videoEnabled;
    }

    @lombok.Data
    @lombok.Builder
    public static class HandMessage {
        private String userId;
        private Boolean raised;
    }

    @lombok.Data
    @lombok.Builder
    public static class ScreenShareMessage {
        private String userId;
        private Boolean active;
    }

    @lombok.Data
    @lombok.Builder
    public static class SignalMessage {
        private String signalId;
        private String type;
        private String fromUserId;
        private String payload;

        public static SignalMessage fromEntity(WebRTCSignalingEntity entity) {
            return SignalMessage.builder()
                .signalId(entity.getId())
                .type(entity.getType().name())
                .fromUserId(entity.getFromUserId())
                .payload(entity.getPayload())
                .build();
        }
    }

    @lombok.Data
    @lombok.Builder
    public static class IceSignalMessage {
        private String signalId;
        private String candidate;
        private String sdpMid;
        private Integer sdpMLineIndex;

        public static IceSignalMessage fromEntity(WebRTCSignalingEntity entity) {
            return IceSignalMessage.builder()
                .signalId(entity.getId())
                .candidate(entity.getIceCandidate())
                .sdpMid(entity.getIceSdpMid())
                .sdpMLineIndex(entity.getIceSdpMLineIndex())
                .build();
        }
    }

    @lombok.Data
    @lombok.Builder
    public static class ParticipantMessage {
        private String userId;
        private String displayName;
        private String avatarUrl;
        private Boolean isHost;
        private Boolean isAudioEnabled;
        private Boolean isVideoEnabled;

        public static ParticipantMessage fromEntity(WebRTCParticipantEntity entity) {
            return ParticipantMessage.builder()
                .userId(entity.getUserId())
                .displayName(entity.getDisplayName())
                .avatarUrl(entity.getAvatarUrl())
                .isHost(entity.getIsHost())
                .isAudioEnabled(entity.getIsAudioEnabled())
                .isVideoEnabled(entity.getIsVideoEnabled())
                .build();
        }
    }

    @lombok.Data
    @lombok.Builder
    public static class PongMessage {
        private Long timestamp;
    }

    @lombok.Data
    @lombok.Builder
    public static class ErrorMessage {
        private String code;
        private String message;
    }
}
