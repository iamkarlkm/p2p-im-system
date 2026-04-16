package com.im.service.websocket.service;

import com.im.dto.WebRTCSignalRequest;
import com.im.dto.WebRTCSignalResponse;
import com.im.dto.CallRequest;
import com.im.dto.CallResponse;
import com.im.entity.CallRecord;
import com.im.repository.CallRecordRepository;
import com.im.websocket.WebSocketMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * WebRTC音视频通话服务
 * 处理1v1音视频通话的信令和状态管理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebRTCService {

    private final CallRecordRepository callRecordRepository;
    private final WebSocketMessageHandler webSocketHandler;

    // 内存中存储活跃通话
    private final Map<String, CallContext> activeCalls = new ConcurrentHashMap<>();
    
    // 定时任务执行器（用于超时处理）
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * 发起通话
     */
    @Transactional
    public CallResponse initiateCall(Long callerId, CallRequest request) {
        // 生成通话ID
        String callId = UUID.randomUUID().toString();

        // 创建通话记录
        CallRecord callRecord = new CallRecord();
        callRecord.setCallId(callId);
        callRecord.setCallerId(callerId);
        callRecord.setCalleeId(request.getCalleeId());
        callRecord.setCallType(request.getCallType()); // AUDIO or VIDEO
        callRecord.setStatus("RINGING");
        callRecord.setStartTime(LocalDateTime.now());
        callRecordRepository.save(callRecord);

        // 创建通话上下文
        CallContext context = new CallContext();
        context.setCallId(callId);
        context.setCallerId(callerId);
        context.setCalleeId(request.getCalleeId());
        context.setCallType(request.getCallType());
        context.setStatus("RINGING");
        context.setStartTime(LocalDateTime.now());
        activeCalls.put(callId, context);

        // 通过WebSocket推送呼叫给被叫方
        CallResponse callNotification = new CallResponse();
        callNotification.setCallId(callId);
        callNotification.setCallerId(callerId);
        callNotification.setCallType(request.getCallType());
        callNotification.setStatus("RINGING");
        callNotification.setTimestamp(LocalDateTime.now());
        
        webSocketHandler.sendCallNotification(request.getCalleeId(), callNotification);

        // 设置60秒超时
        scheduler.schedule(() -> handleCallTimeout(callId), 60, TimeUnit.SECONDS);

        log.info("通话已发起: callId={}, caller={}, callee={}, type={}", 
                callId, callerId, request.getCalleeId(), request.getCallType());

        // 返回给主叫方
        CallResponse response = new CallResponse();
        response.setCallId(callId);
        response.setStatus("RINGING");
        response.setCalleeId(request.getCalleeId());
        return response;
    }

    /**
     * 接受通话
     */
    @Transactional
    public CallResponse acceptCall(Long calleeId, String callId) {
        CallContext context = activeCalls.get(callId);
        if (context == null) {
            throw new RuntimeException("通话不存在或已结束");
        }

        // 验证被叫方
        if (!context.getCalleeId().equals(calleeId)) {
            throw new RuntimeException("无权操作此通话");
        }

        // 更新通话状态
        context.setStatus("CONNECTED");
        context.setConnectTime(LocalDateTime.now());

        // 更新数据库
        CallRecord callRecord = callRecordRepository.findByCallId(callId)
                .orElseThrow(() -> new RuntimeException("通话记录不存在"));
        callRecord.setStatus("CONNECTED");
        callRecord.setConnectTime(LocalDateTime.now());
        callRecordRepository.save(callRecord);

        // 通知主叫方通话已接受
        CallResponse response = new CallResponse();
        response.setCallId(callId);
        response.setStatus("CONNECTED");
        response.setCalleeId(calleeId);
        response.setTimestamp(LocalDateTime.now());
        
        webSocketHandler.sendCallStatusUpdate(context.getCallerId(), response);

        log.info("通话已接受: callId={}, callee={}", callId, calleeId);
        return response;
    }

    /**
     * 拒绝通话
     */
    @Transactional
    public void rejectCall(Long calleeId, String callId, String reason) {
        CallContext context = activeCalls.get(callId);
        if (context == null) {
            return; // 通话已不存在，忽略
        }

        // 验证被叫方
        if (!context.getCalleeId().equals(calleeId)) {
            throw new RuntimeException("无权操作此通话");
        }

        // 更新通话状态
        context.setStatus("REJECTED");
        context.setEndTime(LocalDateTime.now());
        context.setEndReason(reason);

        // 更新数据库
        CallRecord callRecord = callRecordRepository.findByCallId(callId)
                .orElseThrow(() -> new RuntimeException("通话记录不存在"));
        callRecord.setStatus("REJECTED");
        callRecord.setEndTime(LocalDateTime.now());
        callRecord.setEndReason(reason);
        callRecordRepository.save(callRecord);

        // 通知主叫方通话被拒绝
        CallResponse response = new CallResponse();
        response.setCallId(callId);
        response.setStatus("REJECTED");
        response.setEndReason(reason);
        response.setTimestamp(LocalDateTime.now());
        
        webSocketHandler.sendCallStatusUpdate(context.getCallerId(), response);

        // 从活跃通话中移除
        activeCalls.remove(callId);

        log.info("通话已拒绝: callId={}, callee={}, reason={}", callId, calleeId, reason);
    }

    /**
     * 结束通话
     */
    @Transactional
    public void hangupCall(Long userId, String callId) {
        CallContext context = activeCalls.get(callId);
        if (context == null) {
            return; // 通话已不存在，忽略
        }

        // 验证参与者
        if (!context.getCallerId().equals(userId) && !context.getCalleeId().equals(userId)) {
            throw new RuntimeException("无权操作此通话");
        }

        // 计算通话时长
        LocalDateTime endTime = LocalDateTime.now();
        long duration = 0;
        if (context.getConnectTime() != null) {
            duration = java.time.Duration.between(context.getConnectTime(), endTime).getSeconds();
        }

        // 更新通话状态
        context.setStatus("ENDED");
        context.setEndTime(endTime);
        context.setDuration(duration);

        // 更新数据库
        CallRecord callRecord = callRecordRepository.findByCallId(callId)
                .orElseThrow(() -> new RuntimeException("通话记录不存在"));
        callRecord.setStatus("ENDED");
        callRecord.setEndTime(endTime);
        callRecord.setDuration(duration);
        callRecord.setEndReason("USER_HANGUP");
        callRecordRepository.save(callRecord);

        // 通知对方通话已结束
        Long otherPartyId = context.getCallerId().equals(userId) ? 
                context.getCalleeId() : context.getCallerId();
        
        CallResponse response = new CallResponse();
        response.setCallId(callId);
        response.setStatus("ENDED");
        response.setDuration(duration);
        response.setTimestamp(endTime);
        
        webSocketHandler.sendCallStatusUpdate(otherPartyId, response);

        // 从活跃通话中移除
        activeCalls.remove(callId);

        log.info("通话已结束: callId={}, duration={}s", callId, duration);
    }

    /**
     * 处理WebRTC信令
     */
    public WebRTCSignalResponse processSignal(Long userId, WebRTCSignalRequest request) {
        CallContext context = activeCalls.get(request.getCallId());
        if (context == null) {
            throw new RuntimeException("通话不存在或已结束");
        }

        // 验证参与者
        if (!context.getCallerId().equals(userId) && !context.getCalleeId().equals(userId)) {
            throw new RuntimeException("无权操作此通话");
        }

        // 确定接收方
        Long targetUserId = context.getCallerId().equals(userId) ? 
                context.getCalleeId() : context.getCallerId();

        // 转发信令
        WebRTCSignalResponse signal = new WebRTCSignalResponse();
        signal.setCallId(request.getCallId());
        signal.setSignalType(request.getSignalType());
        signal.setSenderId(userId);
        signal.setSdp(request.getSdp());
        signal.setCandidate(request.getCandidate());
        signal.setTimestamp(LocalDateTime.now());

        webSocketHandler.sendWebRTCSignal(targetUserId, signal);

        log.debug("信令已转发: callId={}, type={}, from={}, to={}", 
                request.getCallId(), request.getSignalType(), userId, targetUserId);

        WebRTCSignalResponse response = new WebRTCSignalResponse();
        response.setCallId(request.getCallId());
        response.setStatus("DELIVERED");
        return response;
    }

    /**
     * 切换摄像头
     */
    public void switchCamera(Long userId, String callId, String facingMode) {
        CallContext context = activeCalls.get(callId);
        if (context == null || !"CONNECTED".equals(context.getStatus())) {
            return;
        }

        Long targetUserId = context.getCallerId().equals(userId) ? 
                context.getCalleeId() : context.getCallerId();

        // 通知对方摄像头已切换
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "CAMERA_SWITCHED");
        notification.put("callId", callId);
        notification.put("facingMode", facingMode);
        
        webSocketHandler.sendMessage(targetUserId, notification);
    }

    /**
     * 切换静音状态
     */
    public void toggleMute(Long userId, String callId, Boolean muted) {
        CallContext context = activeCalls.get(callId);
        if (context == null || !"CONNECTED".equals(context.getStatus())) {
            return;
        }

        context.setMuted(userId, muted);

        Long targetUserId = context.getCallerId().equals(userId) ? 
                context.getCalleeId() : context.getCallerId();

        // 通知对方静音状态变化
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "MUTE_STATE_CHANGED");
        notification.put("callId", callId);
        notification.put("muted", muted);
        
        webSocketHandler.sendMessage(targetUserId, notification);
    }

    /**
     * 切换视频状态
     */
    public void toggleVideo(Long userId, String callId, Boolean videoEnabled) {
        CallContext context = activeCalls.get(callId);
        if (context == null || !"CONNECTED".equals(context.getStatus())) {
            return;
        }

        context.setVideoEnabled(userId, videoEnabled);

        Long targetUserId = context.getCallerId().equals(userId) ? 
                context.getCalleeId() : context.getCallerId();

        // 通知对方视频状态变化
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "VIDEO_STATE_CHANGED");
        notification.put("callId", callId);
        notification.put("videoEnabled", videoEnabled);
        
        webSocketHandler.sendMessage(targetUserId, notification);
    }

    /**
     * 获取通话状态
     */
    public CallResponse getCallStatus(Long userId, String callId) {
        CallContext context = activeCalls.get(callId);
        if (context == null) {
            // 从数据库查询历史记录
            CallRecord callRecord = callRecordRepository.findByCallId(callId)
                    .orElseThrow(() -> new RuntimeException("通话记录不存在"));
            
            CallResponse response = new CallResponse();
            response.setCallId(callId);
            response.setStatus(callRecord.getStatus());
            response.setDuration(callRecord.getDuration());
            return response;
        }

        CallResponse response = new CallResponse();
        response.setCallId(callId);
        response.setStatus(context.getStatus());
        response.setCallerId(context.getCallerId());
        response.setCalleeId(context.getCalleeId());
        response.setCallType(context.getCallType());
        
        if (context.getConnectTime() != null) {
            long duration = java.time.Duration.between(
                    context.getConnectTime(), LocalDateTime.now()).getSeconds();
            response.setDuration(duration);
        }
        
        return response;
    }

    /**
     * 获取ICE服务器配置
     */
    public Object getIceServers() {
        // 返回STUN/TURN服务器配置
        List<Map<String, Object>> iceServers = new ArrayList<>();

        // Google STUN服务器
        Map<String, Object> googleStun = new HashMap<>();
        googleStun.put("urls", Arrays.asList(
                "stun:stun.l.google.com:19302",
                "stun:stun1.l.google.com:19302"
        ));
        iceServers.add(googleStun);

        // 如果有TURN服务器，也添加进去
        // Map<String, Object> turnServer = new HashMap<>();
        // turnServer.put("urls", "turn:your-turn-server.com:3478");
        // turnServer.put("username", "username");
        // turnServer.put("credential", "password");
        // iceServers.add(turnServer);

        return iceServers;
    }

    /**
     * 处理通话超时
     */
    private void handleCallTimeout(String callId) {
        CallContext context = activeCalls.get(callId);
        if (context == null || !"RINGING".equals(context.getStatus())) {
            return; // 通话已不在响铃状态，忽略
        }

        // 更新状态为超时
        context.setStatus("TIMEOUT");
        context.setEndTime(LocalDateTime.now());
        context.setEndReason("NO_ANSWER");

        // 更新数据库
        CallRecord callRecord = callRecordRepository.findByCallId(callId)
                .orElse(null);
        if (callRecord != null) {
            callRecord.setStatus("TIMEOUT");
            callRecord.setEndTime(LocalDateTime.now());
            callRecord.setEndReason("NO_ANSWER");
            callRecordRepository.save(callRecord);
        }

        // 通知双方通话超时
        CallResponse response = new CallResponse();
        response.setCallId(callId);
        response.setStatus("TIMEOUT");
        response.setEndReason("NO_ANSWER");
        response.setTimestamp(LocalDateTime.now());
        
        webSocketHandler.sendCallStatusUpdate(context.getCallerId(), response);
        webSocketHandler.sendCallStatusUpdate(context.getCalleeId(), response);

        // 从活跃通话中移除
        activeCalls.remove(callId);

        log.info("通话超时: callId={}", callId);
    }

    /**
     * 通话上下文
     */
    private static class CallContext {
        private String callId;
        private Long callerId;
        private Long calleeId;
        private String callType; // AUDIO or VIDEO
        private String status; // RINGING, CONNECTED, ENDED, REJECTED, TIMEOUT
        private LocalDateTime startTime;
        private LocalDateTime connectTime;
        private LocalDateTime endTime;
        private Long duration;
        private String endReason;
        private Map<Long, Boolean> muteStates = new HashMap<>();
        private Map<Long, Boolean> videoStates = new HashMap<>();

        // Getters and Setters
        public String getCallId() { return callId; }
        public void setCallId(String callId) { this.callId = callId; }
        public Long getCallerId() { return callerId; }
        public void setCallerId(Long callerId) { this.callerId = callerId; }
        public Long getCalleeId() { return calleeId; }
        public void setCalleeId(Long calleeId) { this.calleeId = calleeId; }
        public String getCallType() { return callType; }
        public void setCallType(String callType) { this.callType = callType; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public LocalDateTime getConnectTime() { return connectTime; }
        public void setConnectTime(LocalDateTime connectTime) { this.connectTime = connectTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        public Long getDuration() { return duration; }
        public void setDuration(Long duration) { this.duration = duration; }
        public String getEndReason() { return endReason; }
        public void setEndReason(String endReason) { this.endReason = endReason; }
        public void setMuted(Long userId, Boolean muted) { muteStates.put(userId, muted); }
        public void setVideoEnabled(Long userId, Boolean enabled) { videoStates.put(userId, enabled); }
    }
}
