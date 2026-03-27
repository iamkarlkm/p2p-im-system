package com.im.backend.webrtc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * WebRTC事件发布器
 * 发布各类WebRTC事件到消息总线
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebRTCEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 发布会话创建事件
     */
    public void publishSessionCreated(WebRTCSessionEntity session) {
        eventPublisher.publishEvent(new WebRTCEvent(
            WebRTCEvent.EventType.SESSION_CREATED,
            session.getId(),
            session.getHostId(),
            null,
            buildSessionPayload(session)
        ));
    }

    /**
     * 发布会话启动事件
     */
    public void publishSessionStarted(WebRTCSessionEntity session) {
        eventPublisher.publishEvent(new WebRTCEvent(
            WebRTCEvent.EventType.SESSION_STARTED,
            session.getId(),
            session.getHostId(),
            null,
            buildSessionPayload(session)
        ));
    }

    /**
     * 发布会话结束事件
     */
    public void publishSessionEnded(WebRTCSessionEntity session) {
        eventPublisher.publishEvent(new WebRTCEvent(
            WebRTCEvent.EventType.SESSION_ENDED,
            session.getId(),
            session.getHostId(),
            null,
            buildSessionPayload(session)
        ));
    }

    /**
     * 发布参与者加入事件
     */
    public void publishParticipantJoined(String sessionId, String userId, String displayName) {
        eventPublisher.publishEvent(new WebRTCEvent(
            WebRTCEvent.EventType.PARTICIPANT_JOINED,
            sessionId,
            userId,
            null,
            "{\"userId\":\"" + userId + "\",\"displayName\":\"" + displayName + "\"}"
        ));
    }

    /**
     * 发布参与者离开事件
     */
    public void publishParticipantLeft(String sessionId, String userId) {
        eventPublisher.publishEvent(new WebRTCEvent(
            WebRTCEvent.EventType.PARTICIPANT_LEFT,
            sessionId,
            userId,
            null,
            "{\"userId\":\"" + userId + "\"}"
        ));
    }

    /**
     * 发布参与者重连事件
     */
    public void publishParticipantReconnected(String sessionId, String userId) {
        eventPublisher.publishEvent(new WebRTCEvent(
            WebRTCEvent.EventType.PARTICIPANT_RECONNECTED,
            sessionId,
            userId,
            null,
            "{\"userId\":\"" + userId + "\"}"
        ));
    }

    /**
     * 发布参与者被踢出事件
     */
    public void publishParticipantKicked(String sessionId, String userId, String reason) {
        eventPublisher.publishEvent(new WebRTCEvent(
            WebRTCEvent.EventType.PARTICIPANT_KICKED,
            sessionId,
            userId,
            null,
            "{\"userId\":\"" + userId + "\",\"reason\":\"" + reason + "\"}"
        ));
    }

    /**
     * 发布媒体状态变更事件
     */
    public void publishMediaStateChanged(String sessionId, String userId, 
                                         Boolean audio, Boolean video) {
        eventPublisher.publishEvent(new WebRTCEvent(
            WebRTCEvent.EventType.MEDIA_STATE_CHANGED,
            sessionId,
            userId,
            null,
            String.format("{\"userId\":\"%s\",\"audio\":%s,\"video\":%s}",
                userId, audio, video)
        ));
    }

    /**
     * 发布音频切换事件
     */
    public void publishAudioToggled(String sessionId, String userId, boolean enabled) {
        eventPublisher.publishEvent(new WebRTCEvent(
            WebRTCEvent.EventType.AUDIO_TOGGLED,
            sessionId,
            userId,
            null,
            "{\"userId\":\"" + userId + "\",\"enabled\":" + enabled + "}"
        ));
    }

    /**
     * 发布视频切换事件
     */
    public void publishVideoToggled(String sessionId, String userId, boolean enabled) {
        eventPublisher.publishEvent(new WebRTCEvent(
            WebRTCEvent.EventType.VIDEO_TOGGLED,
            sessionId,
            userId,
            null,
            "{\"userId\":\"" + userId + "\",\"enabled\":" + enabled + "}"
        ));
    }

    /**
     * 发布举手事件
     */
    public void publishHandRaised(String sessionId, String userId) {
        eventPublisher.publishEvent(new WebRTCEvent(
            WebRTCEvent.EventType.HAND_RAISED,
            sessionId,
            userId,
            null,
            "{\"userId\":\"" + userId + "\"}"
        ));
    }

    /**
     * 发布放下手事件
     */
    public void publishHandLowered(String sessionId, String userId) {
        eventPublisher.publishEvent(new WebRTCEvent(
            WebRTCEvent.EventType.HAND_LOWERED,
            sessionId,
            userId,
            null,
            "{\"userId\":\"" + userId + "\"}"
        ));
    }

    /**
     * 发布屏幕共享开始事件
     */
    public void publishScreenShareStarted(String sessionId, String userId) {
        eventPublisher.publishEvent(new WebRTCEvent(
            WebRTCEvent.EventType.SCREEN_SHARE_STARTED,
            sessionId,
            userId,
            null,
            "{\"userId\":\"" + userId + "\"}"
        ));
    }

    /**
     * 发布屏幕共享停止事件
     */
    public void publishScreenShareStopped(String sessionId, String userId) {
        eventPublisher.publishEvent(new WebRTCEvent(
            WebRTCEvent.EventType.SCREEN_SHARE_STOPPED,
            sessionId,
            userId,
            null,
            "{\"userId\":\"" + userId + "\"}"
        ));
    }

    /**
     * 发布主持人变更事件
     */
    public void publishHostChanged(String sessionId, String newHostId) {
        eventPublisher.publishEvent(new WebRTCEvent(
            WebRTCEvent.EventType.HOST_CHANGED,
            sessionId,
            newHostId,
            null,
            "{\"newHostId\":\"" + newHostId + "\"}"
        ));
    }

    /**
     * 发布录制开始事件
     */
    public void publishRecordingStarted(String sessionId, String recordingUrl) {
        eventPublisher.publishEvent(new WebRTCEvent(
            WebRTCEvent.EventType.RECORDING_STARTED,
            sessionId,
            null,
            null,
            "{\"recordingUrl\":\"" + recordingUrl + "\"}"
        ));
    }

    /**
     * 发布录制停止事件
     */
    public void publishRecordingStopped(String sessionId) {
        eventPublisher.publishEvent(new WebRTCEvent(
            WebRTCEvent.EventType.RECORDING_STOPPED,
            sessionId,
            null,
            null,
            "{}"
        ));
    }

    /**
     * 发布信令事件
     */
    public void publishSignal(String sessionId, WebRTCSignalingEntity signal) {
        eventPublisher.publishEvent(new WebRTCEvent(
            WebRTCEvent.EventType.SIGNAL,
            sessionId,
            signal.getFromUserId(),
            signal.getToUserId(),
            buildSignalPayload(signal)
        ));
    }

    /**
     * 发布广播信令事件
     */
    public void publishBroadcast(String sessionId, WebRTCSignalingEntity signal) {
        eventPublisher.publishEvent(new WebRTCEvent(
            WebRTCEvent.EventType.BROADCAST,
            sessionId,
            signal.getFromUserId(),
            null,
            buildSignalPayload(signal)
        ));
    }

    /**
     * 构建会话数据负载
     */
    private String buildSessionPayload(WebRTCSessionEntity session) {
        return String.format(
            "{\"sessionId\":\"%s\",\"roomId\":\"%s\",\"roomName\":\"%s\",\"hostId\":\"%s\",\"status\":\"%s\"}",
            session.getId(),
            session.getRoomId(),
            session.getRoomName(),
            session.getHostId(),
            session.getStatus()
        );
    }

    /**
     * 构建信令数据负载
     */
    private String buildSignalPayload(WebRTCSignalingEntity signal) {
        return String.format(
            "{\"signalId\":\"%s\",\"type\":\"%s\",\"from\":\"%s\",\"to\":\"%s\",\"payload\":\"%s\"}",
            signal.getId(),
            signal.getType(),
            signal.getFromUserId(),
            signal.getToUserId(),
            signal.getPayload().replace("\"", "\\\"")
        );
    }

    /**
     * WebRTC事件类
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class WebRTCEvent {
        private EventType type;
        private String sessionId;
        private String fromUserId;
        private String toUserId;
        private String payload;

        public enum EventType {
            SESSION_CREATED,
            SESSION_STARTED,
            SESSION_ENDED,
            PARTICIPANT_JOINED,
            PARTICIPANT_LEFT,
            PARTICIPANT_RECONNECTED,
            PARTICIPANT_KICKED,
            MEDIA_STATE_CHANGED,
            AUDIO_TOGGLED,
            VIDEO_TOGGLED,
            HAND_RAISED,
            HAND_LOWERED,
            SCREEN_SHARE_STARTED,
            SCREEN_SHARE_STOPPED,
            HOST_CHANGED,
            RECORDING_STARTED,
            RECORDING_STOPPED,
            SIGNAL,
            BROADCAST
        }
    }
}
