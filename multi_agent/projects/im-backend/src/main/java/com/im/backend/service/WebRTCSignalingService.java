package com.im.backend.service;

import com.im.backend.dto.SignalRequest;
import com.im.backend.dto.SignalResponse;
import com.im.backend.entity.SignalSession;
import com.im.backend.entity.SignalSession.SignalStatus;
import com.im.backend.repository.SignalSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebRTCSignalingService {

    private final SignalSessionRepository signalSessionRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${webrtc.stun-servers:stun:stun.l.google.com:19302}")
    private String stunServers;

    @Value("${webrtc.turn-servers:}")
    private String turnServers;

    @Value("${webrtc.turn-username:}")
    private String turnUsername;

    @Value("${webrtc.turn-credential:}")
    private String turnCredential;

    @Value("${webrtc.max-concurrent-calls:5}")
    private int maxConcurrentCalls;

    @Transactional
    public SignalResponse handleSignal(SignalRequest request) {
        log.info("WebRTC Signal: type={}, room={}, user={}",
                 request.getSignalType(), request.getRoomId(), request.getUserId());

        return switch (request.getSignalType()) {
            case SignalRequest.TYPE_CALL_INVITE -> handleCallInvite(request);
            case SignalRequest.TYPE_OFFER -> handleOffer(request);
            case SignalRequest.TYPE_ANSWER -> handleAnswer(request);
            case SignalRequest.TYPE_ICE_CANDIDATE -> handleIceCandidate(request);
            case SignalRequest.TYPE_RINGING -> handleRinging(request);
            case SignalRequest.TYPE_CALL_ACCEPTED -> handleCallAccepted(request);
            case SignalRequest.TYPE_CALL_REJECTED -> handleCallRejected(request);
            case SignalRequest.TYPE_CALL_CANCELLED -> handleCallCancelled(request);
            case SignalRequest.TYPE_CALL_ENDED -> handleCallEnded(request);
            case SignalRequest.TYPE_BUSY -> handleBusy(request);
            case SignalRequest.TYPE_NO_ANSWER -> handleNoAnswer(request);
            default -> buildErrorResponse("Unknown signal type");
        };
    }

    private SignalResponse handleCallInvite(SignalRequest request) {
        List<SignalStatus> activeStatuses = List.of(
            SignalStatus.PENDING, SignalStatus.RINGING, SignalStatus.ACCEPTED
        );
        long activeCalls = signalSessionRepository.countActiveSessions(request.getUserId(), activeStatuses);
        if (activeCalls >= maxConcurrentCalls) {
            return buildErrorResponse("Maximum concurrent calls reached");
        }

        String roomId = UUID.randomUUID().toString();
        SignalSession session = SignalSession.builder()
            .roomId(roomId)
            .callerId(request.getUserId())
            .calleeId(request.getTargetUserId())
            .callType(request.getCallType() != null ? request.getCallType() : "AUDIO")
            .status(SignalStatus.PENDING)
            .build();
        signalSessionRepository.save(session);

        SignalResponse response = buildBaseResponse(roomId, request.getUserId(), request.getTargetUserId());
        response.setSignalType(SignalRequest.TYPE_CALL_INVITE);
        response.setCallType(session.getCallType());
        response.setStunServers(stunServers);
        response.setTurnServers(turnServers);
        response.setTurnUsername(turnUsername);
        response.setTurnCredential(turnCredential);

        sendToUser(request.getTargetUserId(), response);
        return response;
    }

    private SignalResponse handleOffer(SignalRequest request) {
        SignalSession session = signalSessionRepository.findByRoomId(request.getRoomId())
            .orElse(null);
        if (session == null) {
            return buildErrorResponse("Session not found");
        }
        session.setCallerSdp(request.getSdp());
        signalSessionRepository.save(session);

        SignalResponse response = buildBaseResponse(request.getRoomId(), request.getUserId(),
            getOtherParty(session, request.getUserId()));
        response.setSignalType(SignalRequest.TYPE_OFFER);
        response.setSdp(request.getSdp());
        response.setSdpType(request.getSdpType());

        sendToUser(getOtherParty(session, request.getUserId()), response);
        return response;
    }

    private SignalResponse handleAnswer(SignalRequest request) {
        SignalSession session = signalSessionRepository.findByRoomId(request.getRoomId())
            .orElse(null);
        if (session == null) {
            return buildErrorResponse("Session not found");
        }
        session.setCalleeSdp(request.getSdp());
        session.setStatus(SignalStatus.ACCEPTED);
        session.setAcceptedAt(LocalDateTime.now());
        signalSessionRepository.save(session);

        SignalResponse response = buildBaseResponse(request.getRoomId(), request.getUserId(),
            getOtherParty(session, request.getUserId()));
        response.setSignalType(SignalRequest.TYPE_ANSWER);
        response.setSdp(request.getSdp());
        response.setSdpType(request.getSdpType());
        response.setStatus(SignalStatus.ACCEPTED.name());

        sendToUser(getOtherParty(session, request.getUserId()), response);
        return response;
    }

    private SignalResponse handleIceCandidate(SignalRequest request) {
        SignalSession session = signalSessionRepository.findByRoomId(request.getRoomId())
            .orElse(null);
        if (session == null) {
            return buildErrorResponse("Session not found");
        }

        SignalResponse response = buildBaseResponse(request.getRoomId(), request.getUserId(),
            getOtherParty(session, request.getUserId()));
        response.setSignalType(SignalRequest.TYPE_ICE_CANDIDATE);
        response.setCandidate(request.getCandidate());
        response.setSdpMLineIndex(request.getSdpMLineIndex());
        response.setSdpMid(request.getSdpMid());

        sendToUser(getOtherParty(session, request.getUserId()), response);
        return response;
    }

    private SignalResponse handleRinging(SignalRequest request) {
        SignalSession session = signalSessionRepository.findByRoomId(request.getRoomId())
            .orElse(null);
        if (session != null) {
            session.setStatus(SignalStatus.RINGING);
            session.setRingingAt(LocalDateTime.now());
            signalSessionRepository.save(session);
        }

        SignalResponse response = buildBaseResponse(request.getRoomId(), request.getUserId(),
            session != null ? getOtherParty(session, request.getUserId()) : null);
        response.setSignalType(SignalRequest.TYPE_RINGING);

        if (session != null) {
            sendToUser(getOtherParty(session, request.getUserId()), response);
        }
        return response;
    }

    private SignalResponse handleCallAccepted(SignalRequest request) {
        SignalSession session = signalSessionRepository.findByRoomId(request.getRoomId())
            .orElse(null);
        if (session != null) {
            session.setStatus(SignalStatus.ACCEPTED);
            session.setAcceptedAt(LocalDateTime.now());
            signalSessionRepository.save(session);
        }

        SignalResponse response = buildBaseResponse(request.getRoomId(), request.getUserId(),
            session != null ? getOtherParty(session, request.getUserId()) : null);
        response.setSignalType(SignalRequest.TYPE_CALL_ACCEPTED);
        response.setStatus(SignalStatus.ACCEPTED.name());

        if (session != null) {
            sendToUser(getOtherParty(session, request.getUserId()), response);
        }
        return response;
    }

    private SignalResponse handleCallRejected(SignalRequest request) {
        SignalSession session = signalSessionRepository.findByRoomId(request.getRoomId())
            .orElse(null);
        if (session != null) {
            session.setStatus(SignalStatus.REJECTED);
            session.setEndedAt(LocalDateTime.now());
            signalSessionRepository.save(session);
        }

        SignalResponse response = buildBaseResponse(request.getRoomId(), request.getUserId(),
            session != null ? getOtherParty(session, request.getUserId()) : null);
        response.setSignalType(SignalRequest.TYPE_CALL_REJECTED);
        response.setStatus(SignalStatus.REJECTED.name());

        if (session != null) {
            sendToUser(getOtherParty(session, request.getUserId()), response);
        }
        return response;
    }

    private SignalResponse handleCallCancelled(SignalRequest request) {
        SignalSession session = signalSessionRepository.findByRoomId(request.getRoomId())
            .orElse(null);
        if (session != null) {
            session.setStatus(SignalStatus.CANCELLED);
            session.setEndedAt(LocalDateTime.now());
            signalSessionRepository.save(session);
        }

        SignalResponse response = buildBaseResponse(request.getRoomId(), request.getUserId(),
            session != null ? getOtherParty(session, request.getUserId()) : null);
        response.setSignalType(SignalRequest.TYPE_CALL_CANCELLED);
        response.setStatus(SignalStatus.CANCELLED.name());

        if (session != null) {
            sendToUser(getOtherParty(session, request.getUserId()), response);
        }
        return response;
    }

    private SignalResponse handleCallEnded(SignalRequest request) {
        SignalSession session = signalSessionRepository.findByRoomId(request.getRoomId())
            .orElse(null);
        if (session != null) {
            session.setStatus(SignalStatus.ENDED);
            session.setEndedAt(LocalDateTime.now());
            signalSessionRepository.save(session);
        }

        SignalResponse response = buildBaseResponse(request.getRoomId(), request.getUserId(),
            session != null ? getOtherParty(session, request.getUserId()) : null);
        response.setSignalType(SignalRequest.TYPE_CALL_ENDED);
        response.setStatus(SignalStatus.ENDED.name());

        if (session != null) {
            sendToUser(getOtherParty(session, request.getUserId()), response);
        }
        return response;
    }

    private SignalResponse handleBusy(SignalRequest request) {
        SignalSession session = signalSessionRepository.findByRoomId(request.getRoomId())
            .orElse(null);
        if (session != null) {
            session.setStatus(SignalStatus.BUSY);
            session.setEndedAt(LocalDateTime.now());
            signalSessionRepository.save(session);
        }

        SignalResponse response = buildBaseResponse(request.getRoomId(), request.getUserId(),
            session != null ? getOtherParty(session, request.getUserId()) : null);
        response.setSignalType(SignalRequest.TYPE_BUSY);
        response.setStatus(SignalStatus.BUSY.name());

        if (session != null) {
            sendToUser(getOtherParty(session, request.getUserId()), response);
        }
        return response;
    }

    private SignalResponse handleNoAnswer(SignalRequest request) {
        SignalSession session = signalSessionRepository.findByRoomId(request.getRoomId())
            .orElse(null);
        if (session != null) {
            session.setStatus(SignalStatus.NO_ANSWER);
            session.setEndedAt(LocalDateTime.now());
            signalSessionRepository.save(session);
        }

        SignalResponse response = buildBaseResponse(request.getRoomId(), request.getUserId(),
            session != null ? getOtherParty(session, request.getUserId()) : null);
        response.setSignalType(SignalRequest.TYPE_NO_ANSWER);
        response.setStatus(SignalStatus.NO_ANSWER.name());

        if (session != null) {
            sendToUser(getOtherParty(session, request.getUserId()), response);
        }
        return response;
    }

    private SignalResponse buildBaseResponse(String roomId, Long fromUserId, Long toUserId) {
        return SignalResponse.builder()
            .roomId(roomId)
            .fromUserId(fromUserId)
            .toUserId(toUserId)
            .timestamp(LocalDateTime.now())
            .build();
    }

    private SignalResponse buildErrorResponse(String message) {
        return SignalResponse.builder()
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
    }

    private void sendToUser(Long userId, SignalResponse response) {
        if (userId != null) {
            messagingTemplate.convertAndSend("/queue/webrtc/" + userId, response);
        }
    }

    private Long getOtherParty(SignalSession session, Long currentUserId) {
        return session.getCallerId().equals(currentUserId)
            ? session.getCalleeId()
            : session.getCallerId();
    }

    public SignalSession getSessionByRoomId(String roomId) {
        return signalSessionRepository.findByRoomId(roomId).orElse(null);
    }

    @Transactional
    public void endSession(String roomId) {
        signalSessionRepository.findByRoomId(roomId).ifPresent(session -> {
            session.setStatus(SignalStatus.ENDED);
            session.setEndedAt(LocalDateTime.now());
            signalSessionRepository.save(session);
        });
    }
}
