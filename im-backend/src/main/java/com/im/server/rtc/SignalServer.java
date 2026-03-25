package com.im.server.rtc;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WebRTC信令服务器
 * 
 * 功能：
 * 1. 处理WebRTC连接建立
 * 2. SDP offer/answer交换
 * 3. ICE candidate交换
 * 4. 房间管理
 * 5. 通话状态管理
 * 6. 通话记录
 */
@Component
public class SignalServer extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 房间列表: roomId -> Set<WebSocketSession>
    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    
    // 用户会话映射: userId -> WebSocketSession
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    
    // 通话状态: callId -> CallState
    private final Map<String, CallState> activeCalls = new ConcurrentHashMap<>();
    
    // 用户当前通话: userId -> callId
    private final Map<String, String> userCalls = new ConcurrentHashMap<>();

    // ICE服务器配置
    private static final List<Map<String, Object>> ICE_SERVERS = Arrays.asList(
        Map.of(
            "urls", "stun:stun.l.google.com:19302"
        ),
        Map.of(
            "urls", "stun:stun1.l.google.com:19302"
        )
    );

    @PostConstruct
    public void init() {
        System.out.println("WebRTC Signaling Server initialized");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.put(userId, session);
            System.out.println("WebRTC: User connected: " + userId);
            
            // 发送ICE服务器配置
            sendMessage(session, createMessage("ice_config", Map.of(
                "iceServers", ICE_SERVERS
            )));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode json = objectMapper.readTree(message.getPayload());
        String type = json.get("type").asText();
        
        switch (type) {
            case "join_room":
                handleJoinRoom(session, json);
                break;
            case "leave_room":
                handleLeaveRoom(session);
                break;
            case "offer":
                handleOffer(session, json);
                break;
            case "answer":
                handleAnswer(session, json);
                break;
            case "ice_candidate":
                handleIceCandidate(session, json);
                break;
            case "call":
                handleCall(session, json);
                break;
            case "accept_call":
                handleAcceptCall(session, json);
                break;
            case "reject_call":
                handleRejectCall(session, json);
                break;
            case "hangup":
                handleHangup(session, json);
                break;
            case "toggle_audio":
                handleToggleAudio(session, json);
                break;
            case "toggle_video":
                handleToggleVideo(session, json);
                break;
            case "ping":
                sendMessage(session, createMessage("pong", Map.of()));
                break;
            default:
                System.out.println("Unknown message type: " + type);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            // 清理用户会话
            userSessions.remove(userId);
            
            // 如果用户在通话中，挂断
            String callId = userCalls.get(userId);
            if (callId != null) {
                handleHangup(session, null);
            }
            
            // 离开所有房间
            for (Set<WebSocketSession> room : rooms.values()) {
                room.remove(session);
            }
            
            System.out.println("WebRTC: User disconnected: " + userId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebRTC transport error: " + exception.getMessage());
        session.close();
    }

    // ========== 房间管理 ==========

    private void handleJoinRoom(WebSocketSession session, JsonNode json) throws IOException {
        String roomId = json.get("roomId").asText();
        String userId = getUserIdFromSession(session);
        
        rooms.computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>()).add(session);
        
        // 获取房间中的其他用户
        List<String> participants = new ArrayList<>();
        for (WebSocketSession s : rooms.get(roomId)) {
            if (!s.equals(session)) {
                participants.add(getUserIdFromSession(s));
            }
        }
        
        sendMessage(session, createMessage("room_joined", Map.of(
            "roomId", roomId,
            "participants", participants
        )));
        
        // 通知房间中的其他用户
        broadcastToRoom(roomId, createMessage("user_joined", Map.of(
            "userId", userId
        )), session);
        
        System.out.println("User " + userId + " joined room " + roomId);
    }

    private void handleLeaveRoom(WebSocketSession session) throws IOException {
        String userId = getUserIdFromSession(session);
        
        for (Map.Entry<String, Set<WebSocketSession>> entry : rooms.entrySet()) {
            if (entry.getValue().remove(session)) {
                String roomId = entry.getKey();
                
                broadcastToRoom(roomId, createMessage("user_left", Map.of(
                    "userId", userId
                )), null);
                
                // 如果房间空了，清理
                if (entry.getValue().isEmpty()) {
                    rooms.remove(roomId);
                }
                
                System.out.println("User " + userId + " left room " + roomId);
                break;
            }
        }
    }

    // ========== WebRTC信令 ==========

    private void handleOffer(WebSocketSession session, JsonNode json) throws IOException {
        String targetUserId = json.get("targetUserId").asText();
        String sdp = json.get("sdp").asText();
        
        WebSocketSession targetSession = userSessions.get(targetUserId);
        if (targetSession != null && targetSession.isOpen()) {
            sendMessage(targetSession, createMessage("offer", Map.of(
                "fromUserId", getUserIdFromSession(session),
                "sdp", sdp
            )));
        }
    }

    private void handleAnswer(WebSocketSession session, JsonNode json) throws IOException {
        String targetUserId = json.get("targetUserId").asText();
        String sdp = json.get("sdp").asText();
        
        WebSocketSession targetSession = userSessions.get(targetUserId);
        if (targetSession != null && targetSession.isOpen()) {
            sendMessage(targetSession, createMessage("answer", Map.of(
                "fromUserId", getUserIdFromSession(session),
                "sdp", sdp
            )));
        }
    }

    private void handleIceCandidate(WebSocketSession session, JsonNode json) throws IOException {
        String targetUserId = json.get("targetUserId").asText();
        String candidate = json.get("candidate").asText();
        String sdpMid = json.has("sdpMid") ? json.get("sdpMid").asText() : null;
        int sdpMLineIndex = json.has("sdpMLineIndex") ? json.get("sdpMLineIndex").asInt() : 0;
        
        WebSocketSession targetSession = userSessions.get(targetUserId);
        if (targetSession != null && targetSession.isOpen()) {
            sendMessage(targetSession, createMessage("ice_candidate", Map.of(
                "fromUserId", getUserIdFromSession(session),
                "candidate", candidate,
                "sdpMid", sdpMid != null ? sdpMid : "",
                "sdpMLineIndex", sdpMLineIndex
            )));
        }
    }

    // ========== 通话控制 ==========

    private void handleCall(WebSocketSession session, JsonNode json) throws IOException {
        String callerId = getUserIdFromSession(session);
        String calleeId = json.get("calleeId").asText();
        String callType = json.has("callType") ? json.get("callType").asText() : "video"; // video or audio
        String callId = UUID.randomUUID().toString();
        
        // 保存通话状态
        CallState callState = new CallState();
        callState.callId = callId;
        callState.callerId = callerId;
        callState.calleeId = calleeId;
        callState.callType = callType;
        callState.status = "calling";
        callState.startTime = System.currentTimeMillis();
        
        activeCalls.put(callId, callState);
        userCalls.put(callerId, callId);
        userCalls.put(calleeId, callId);
        
        // 发送呼叫给对方
        WebSocketSession calleeSession = userSessions.get(calleeId);
        if (calleeSession != null && calleeSession.isOpen()) {
            sendMessage(calleeSession, createMessage("incoming_call", Map.of(
                "callId", callId,
                "callerId", callerId,
                "callType", callType
            )));
        } else {
            // 用户不在线
            sendMessage(session, createMessage("call_error", Map.of(
                "error", "User is not online"
            )));
            activeCalls.remove(callId);
            userCalls.remove(callerId);
            userCalls.remove(calleeId);
        }
    }

    private void handleAcceptCall(WebSocketSession session, JsonNode json) throws IOException {
        String callId = json.get("callId").asText();
        String acceptorId = getUserIdFromSession(session);
        
        CallState callState = activeCalls.get(callId);
        if (callState != null) {
            callState.status = "connected";
            callState.connectedTime = System.currentTimeMillis();
            
            // 通知呼叫者
            WebSocketSession callerSession = userSessions.get(callState.callerId);
            if (callerSession != null && callerSession.isOpen()) {
                sendMessage(callerSession, createMessage("call_accepted", Map.of(
                    "callId", callId,
                    "acceptorId", acceptorId
                )));
            }
            
            // 通知接听者
            sendMessage(session, createMessage("call_started", Map.of(
                "callId", callId
            )));
        }
    }

    private void handleRejectCall(WebSocketSession session, JsonNode json) throws IOException {
        String callId = json.get("callId").asText();
        String rejecterId = getUserIdFromSession(session);
        
        CallState callState = activeCalls.get(callId);
        if (callState != null) {
            callState.status = "rejected";
            
            // 通知呼叫者
            WebSocketSession callerSession = userSessions.get(callState.callerId);
            if (callerSession != null && callerSession.isOpen()) {
                sendMessage(callerSession, createMessage("call_rejected", Map.of(
                    "callId", callId,
                    "rejecterId", rejecterId
                )));
            }
            
            cleanupCall(callId);
        }
    }

    private void handleHangup(WebSocketSession session, JsonNode json) throws IOException {
        String userId = getUserIdFromSession(session);
        String callId = userCalls.get(userId);
        
        if (callId != null) {
            CallState callState = activeCalls.get(callId);
            if (callState != null) {
                callState.status = "ended";
                callState.endTime = System.currentTimeMillis();
                
                // 通知对方
                String otherUserId = callState.callerId.equals(userId) 
                    ? callState.calleeId 
                    : callState.callerId;
                
                WebSocketSession otherSession = userSessions.get(otherUserId);
                if (otherSession != null && otherSession.isOpen()) {
                    sendMessage(otherSession, createMessage("call_ended", Map.of(
                        "callId", callId,
                        "endedBy", userId
                    )));
                }
            }
            
            cleanupCall(callId);
        }
    }

    private void cleanupCall(String callId) {
        CallState callState = activeCalls.remove(callId);
        if (callState != null) {
            userCalls.remove(callState.callerId);
            userCalls.remove(callState.calleeId);
        }
    }

    // ========== 媒体控制 ==========

    private void handleToggleAudio(WebSocketSession session, JsonNode json) throws IOException {
        String userId = getUserIdFromSession(session);
        String callId = userCalls.get(userId);
        boolean enabled = json.get("enabled").asBoolean();
        
        if (callId != null) {
            CallState callState = activeCalls.get(callId);
            if (callState != null) {
                String targetUserId = callState.callerId.equals(userId) 
                    ? callState.calleeId 
                    : callState.callerId;
                
                WebSocketSession targetSession = userSessions.get(targetUserId);
                if (targetSession != null && targetSession.isOpen()) {
                    sendMessage(targetSession, createMessage("audio_toggled", Map.of(
                        "userId", userId,
                        "enabled", enabled
                    )));
                }
            }
        }
    }

    private void handleToggleVideo(WebSocketSession session, JsonNode json) throws IOException {
        String userId = getUserIdFromSession(session);
        String callId = userCalls.get(userId);
        boolean enabled = json.get("enabled").asBoolean();
        
        if (callId != null) {
            CallState callState = activeCalls.get(callId);
            if (callState != null) {
                String targetUserId = callState.callerId.equals(userId) 
                    ? callState.calleeId 
                    : callState.callerId;
                
                WebSocketSession targetSession = userSessions.get(targetUserId);
                if (targetSession != null && targetSession.isOpen()) {
                    sendMessage(targetSession, createMessage("video_toggled", Map.of(
                        "userId", userId,
                        "enabled", enabled
                    )));
                }
            }
        }
    }

    // ========== 辅助方法 ==========

    private void sendMessage(WebSocketSession session, String message) throws IOException {
        if (session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }

    private void broadcastToRoom(String roomId, String message, WebSocketSession exclude) throws IOException {
        Set<WebSocketSession> room = rooms.get(roomId);
        if (room != null) {
            for (WebSocketSession session : room) {
                if (session != exclude && session.isOpen()) {
                    sendMessage(session, message);
                }
            }
        }
    }

    private String createMessage(String type, Map<String, Object> data) throws IOException {
        Map<String, Object> message = new HashMap<>();
        message.put("type", type);
        message.put("timestamp", System.currentTimeMillis());
        message.putAll(data);
        return objectMapper.writeValueAsString(message);
    }

    private String getUserIdFromSession(WebSocketSession session) {
        // 从session获取userId（通常在握手阶段设置）
        return session.getUri().getQuery().split("=")[1];
    }

    // 通话状态内部类
    private static class CallState {
        String callId;
        String callerId;
        String calleeId;
        String callType;
        String status;
        long startTime;
        long connectedTime;
        long endTime;
    }
}
