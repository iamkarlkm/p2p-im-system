package com.im.backend.service;

import com.im.backend.entity.GroupCall;
import com.im.backend.entity.GroupCallParticipant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupCallService {
    
    private final ConcurrentHashMap<Long, GroupCall> activeCalls = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, List<GroupCallParticipant>> callParticipants = new ConcurrentHashMap<>();
    
    public GroupCall createGroupCall(Long conversationId, Long initiatorId, String callType) {
        GroupCall call = GroupCall.builder()
                .conversationId(conversationId)
                .initiatorId(initiatorId)
                .callType(callType)
                .status(GroupCall.STATUS_PENDING)
                .startedAt(LocalDateTime.now())
                .maxParticipants(50)
                .currentParticipants(1)
                .isRecording(false)
                .build();
        
        activeCalls.put(call.getId(), call);
        
        List<GroupCallParticipant> participants = new ArrayList<>();
        GroupCallParticipant initiator = GroupCallParticipant.builder()
                .callId(call.getId())
                .userId(initiatorId)
                .status(GroupCallParticipant.STATUS_JOINED)
                .joinedAt(LocalDateTime.now())
                .isAudioEnabled(true)
                .isVideoEnabled(callType.equals(GroupCall.TYPE_VIDEO))
                .isScreenSharing(false)
                .isMuted(false)
                .build();
        participants.add(initiator);
        callParticipants.put(call.getId(), participants);
        
        log.info("Created group call {} for conversation {}", call.getId(), conversationId);
        return call;
    }
    
    public GroupCall joinCall(Long callId, Long userId) {
        GroupCall call = activeCalls.get(callId);
        if (call == null || call.getStatus().equals(GroupCall.STATUS_ENDED)) {
            throw new RuntimeException("Call not found or ended");
        }
        
        List<GroupCallParticipant> participants = callParticipants.computeIfAbsent(callId, k -> new ArrayList<>());
        
        Optional<GroupCallParticipant> existing = participants.stream()
                .filter(p -> p.getUserId().equals(userId))
                .findFirst();
        
        if (existing.isPresent()) {
            existing.get().setStatus(GroupCallParticipant.STATUS_JOINED);
            existing.get().setJoinedAt(LocalDateTime.now());
        } else {
            GroupCallParticipant participant = GroupCallParticipant.builder()
                    .callId(callId)
                    .userId(userId)
                    .status(GroupCallParticipant.STATUS_JOINED)
                    .joinedAt(LocalDateTime.now())
                    .isAudioEnabled(true)
                    .isVideoEnabled(call.getCallType().equals(GroupCall.TYPE_VIDEO))
                    .isScreenSharing(false)
                    .isMuted(false)
                    .build();
            participants.add(participant);
        }
        
        call.setCurrentParticipants(participants.stream()
                .filter(p -> p.getStatus().equals(GroupCallParticipant.STATUS_JOINED))
                .mapToInt(p -> 1)
                .sum());
        
        log.info("User {} joined call {}", userId, callId);
        return call;
    }
    
    public GroupCall leaveCall(Long callId, Long userId) {
        GroupCall call = activeCalls.get(callId);
        if (call == null) {
            throw new RuntimeException("Call not found");
        }
        
        List<GroupCallParticipant> participants = callParticipants.get(callId);
        if (participants != null) {
            Optional<GroupCallParticipant> participant = participants.stream()
                    .filter(p -> p.getUserId().equals(userId))
                    .findFirst();
            
            participant.ifPresent(p -> {
                p.setStatus(GroupCallParticipant.STATUS_LEFT);
                p.setLeftAt(LocalDateTime.now());
            });
        }
        
        int activeCount = participants != null ? 
                (int) participants.stream()
                        .filter(p -> p.getStatus().equals(GroupCallParticipant.STATUS_JOINED))
                        .count() : 0;
        
        call.setCurrentParticipants(activeCount);
        
        if (activeCount == 0) {
            call.setStatus(GroupCall.STATUS_ENDED);
            call.setEndedAt(LocalDateTime.now());
            activeCalls.remove(callId);
        }
        
        log.info("User {} left call {}", userId, callId);
        return call;
    }
    
    public void toggleMute(Long callId, Long userId) {
        List<GroupCallParticipant> participants = callParticipants.get(callId);
        if (participants != null) {
            participants.stream()
                    .filter(p -> p.getUserId().equals(userId))
                    .findFirst()
                    .ifPresent(p -> p.setIsMuted(!p.getIsMuted()));
        }
    }
    
    public void toggleVideo(Long callId, Long userId) {
        List<GroupCallParticipant> participants = callParticipants.get(callId);
        if (participants != null) {
            participants.stream()
                    .filter(p -> p.getUserId().equals(userId))
                    .findFirst()
                    .ifPresent(p -> p.setIsVideoEnabled(!p.getIsVideoEnabled()));
        }
    }
    
    public void toggleScreenShare(Long callId, Long userId, boolean enable) {
        List<GroupCallParticipant> participants = callParticipants.get(callId);
        if (participants != null) {
            participants.stream()
                    .filter(p -> p.getUserId().equals(userId))
                    .findFirst()
                    .ifPresent(p -> {
                        p.setIsScreenSharing(enable);
                        if (enable) {
                            participants.stream()
                                    .filter(p2 -> !p2.getUserId().equals(userId))
                                    .forEach(p2 -> p2.setIsScreenSharing(false));
                        }
                    });
        }
    }
    
    public GroupCall getCall(Long callId) {
        return activeCalls.get(callId);
    }
    
    public List<GroupCallParticipant> getParticipants(Long callId) {
        return callParticipants.getOrDefault(callId, new ArrayList<>());
    }
    
    public void endCall(Long callId) {
        GroupCall call = activeCalls.get(callId);
        if (call != null) {
            call.setStatus(GroupCall.STATUS_ENDED);
            call.setEndedAt(LocalDateTime.now());
            activeCalls.remove(callId);
            log.info("Call {} ended", callId);
        }
    }
    
    public List<GroupCall> getActiveCalls() {
        return new ArrayList<>(activeCalls.values());
    }
}
