package com.im.backend.service;

import com.im.backend.dto.CallRecordDTO;
import com.im.backend.entity.CallRecordEntity;
import com.im.backend.repository.CallRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallRecordService {

    private final CallRecordRepository callRecordRepository;
    private final UserService userService;

    /** 发起通话 → 创建记录 */
    @Transactional
    public CallRecordDTO initiateCall(String callerId, String calleeId, String conversationId, String callType) {
        CallRecordEntity entity = CallRecordEntity.builder()
                .callId(UUID.randomUUID().toString())
                .callerId(callerId)
                .calleeId(calleeId)
                .conversationId(conversationId)
                .callType(callType)
                .status("INITIATED")
                .startTime(LocalDateTime.now())
                .build();
        entity = callRecordRepository.save(entity);
        log.info("Call initiated: {} -> {} callId={}", callerId, calleeId, entity.getCallId());
        return toDTO(entity);
    }

    /** 接听/响铃中 */
    @Transactional
    public CallRecordDTO updateStatus(String callId, String status) {
        CallRecordEntity entity = callRecordRepository.findByCallId(callId)
                .orElseThrow(() -> new RuntimeException("Call not found: " + callId));
        entity.setStatus(status);
        if ("ANSWERED".equals(status) && entity.getAnswerTime() == null) {
            entity.setAnswerTime(LocalDateTime.now());
        }
        entity = callRecordRepository.save(entity);
        return toDTO(entity);
    }

    /** 结束通话 */
    @Transactional
    public CallRecordDTO endCall(String callId, boolean endedByCaller) {
        CallRecordEntity entity = callRecordRepository.findByCallId(callId)
                .orElseThrow(() -> new RuntimeException("Call not found: " + callId));
        entity.setStatus("ENDED");
        entity.setEndTime(LocalDateTime.now());
        entity.setEndedByCaller(endedByCaller);
        if (entity.getAnswerTime() != null) {
            entity.setDuration((int) java.time.Duration.between(entity.getAnswerTime(), entity.getEndTime()).getSeconds());
        }
        entity = callRecordRepository.save(entity);
        log.info("Call ended: callId={} duration={}s", callId, entity.getDuration());
        return toDTO(entity);
    }

    /** 标记为未接 */
    @Transactional
    public void markMissed(String callId) {
        callRecordRepository.findByCallId(callId).ifPresent(entity -> {
            entity.setStatus("MISSED");
            entity.setEndTime(LocalDateTime.now());
            callRecordRepository.save(entity);
        });
    }

    /** 分页查询通话记录 */
    public Page<CallRecordDTO> getCallHistory(String userId, int page, int size) {
        Page<CallRecordEntity> pageResult = callRecordRepository
                .findByCallerIdOrCalleeIdOrderByStartTimeDesc(userId, userId, PageRequest.of(page, size));
        return pageResult.map(this::toDTO);
    }

    /** 未接来电列表 */
    public List<CallRecordDTO> getMissedCalls(String userId) {
        return callRecordRepository.findMissedByUserId(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /** 删除通话记录 */
    @Transactional
    public void deleteCall(Long id) {
        callRecordRepository.deleteById(id);
    }

    private CallRecordDTO toDTO(CallRecordEntity e) {
        return CallRecordDTO.builder()
                .id(e.getId())
                .callId(e.getCallId())
                .callerId(e.getCallerId())
                .callerName(userService.getUserDisplayName(e.getCallerId()))
                .calleeId(e.getCalleeId())
                .calleeName(userService.getUserDisplayName(e.getCalleeId()))
                .conversationId(e.getConversationId())
                .callType(e.getCallType())
                .status(e.getStatus())
                .startTime(e.getStartTime())
                .answerTime(e.getAnswerTime())
                .endTime(e.getEndTime())
                .duration(e.getDuration())
                .endedByCaller(e.getEndedByCaller())
                .build();
    }
}
