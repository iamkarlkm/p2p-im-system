package com.im.backend.controller;

import com.im.backend.entity.GroupCall;
import com.im.backend.entity.GroupCallParticipant;
import com.im.backend.service.GroupCallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calls/group")
@RequiredArgsConstructor
public class GroupCallController {
    
    private final GroupCallService groupCallService;
    
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createCall(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, Object> request) {
        
        Long conversationId = Long.valueOf(request.get("conversationId").toString());
        String callType = (String) request.getOrDefault("callType", "video");
        
        GroupCall call = groupCallService.createGroupCall(conversationId, userId, callType);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("callId", call.getId());
        response.put("callType", call.getCallType());
        response.put("conversationId", call.getConversationId());
        response.put("initiatorId", call.getInitiatorId());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{callId}/join")
    public ResponseEntity<Map<String, Object>> joinCall(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long callId) {
        
        GroupCall call = groupCallService.joinCall(callId, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("callId", callId);
        response.put("currentParticipants", call.getCurrentParticipants());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{callId}/leave")
    public ResponseEntity<Map<String, Object>> leaveCall(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long callId) {
        
        GroupCall call = groupCallService.leaveCall(callId, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("callId", callId);
        response.put("callStatus", call.getStatus());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{callId}/mute")
    public ResponseEntity<Map<String, Object>> toggleMute(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long callId) {
        
        groupCallService.toggleMute(callId, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("action", "mute_toggled");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{callId}/video")
    public ResponseEntity<Map<String, Object>> toggleVideo(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long callId) {
        
        groupCallService.toggleVideo(callId, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("action", "video_toggled");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{callId}/screen-share")
    public ResponseEntity<Map<String, Object>> toggleScreenShare(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long callId,
            @RequestBody Map<String, Boolean> request) {
        
        boolean enable = request.getOrDefault("enable", false);
        groupCallService.toggleScreenShare(callId, userId, enable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("action", "screen_share_" + (enable ? "enabled" : "disabled"));
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{callId}")
    public ResponseEntity<Map<String, Object>> getCallInfo(@PathVariable Long callId) {
        GroupCall call = groupCallService.getCall(callId);
        
        if (call == null) {
            return ResponseEntity.status(404).body(Map.of("status", "not_found"));
        }
        
        List<GroupCallParticipant> participants = groupCallService.getParticipants(callId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("call", Map.of(
                "callId", call.getId(),
                "callType", call.getCallType(),
                "status", call.getStatus(),
                "currentParticipants", call.getCurrentParticipants(),
                "maxParticipants", call.getMaxParticipants(),
                "startedAt", call.getStartedAt()
        ));
        response.put("participants", participants.stream().map(p -> Map.of(
                "userId", p.getUserId(),
                "status", p.getStatus(),
                "isMuted", p.getIsMuted(),
                "isVideoEnabled", p.getIsVideoEnabled(),
                "isScreenSharing", p.getIsScreenSharing(),
                "joinedAt", p.getJoinedAt()
        )).toList());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveCalls() {
        List<GroupCall> activeCalls = groupCallService.getActiveCalls();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("activeCallsCount", activeCalls.size());
        response.put("calls", activeCalls.stream().map(call -> Map.of(
                "callId", call.getId(),
                "conversationId", call.getConversationId(),
                "callType", call.getCallType(),
                "currentParticipants", call.getCurrentParticipants()
        )).toList());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{callId}/end")
    public ResponseEntity<Map<String, Object>> endCall(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long callId) {
        
        groupCallService.endCall(callId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("callId", callId);
        response.put("message", "Call ended");
        
        return ResponseEntity.ok(response);
    }
}
