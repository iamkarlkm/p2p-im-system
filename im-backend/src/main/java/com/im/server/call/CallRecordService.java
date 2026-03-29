package com.im.server.call;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallRecordService {

    private final CallRecordRepository repository;

    public CallRecord startCall(String callerId, String calleeId, String conversationId, String type) {
        CallRecord record = CallRecord.builder()
                .recordId(UUID.randomUUID().toString())
                .callerId(callerId)
                .calleeId(calleeId)
                .conversationId(conversationId)
                .type(CallRecord.CallType.valueOf(type))
                .status(CallRecord.CallStatus.MISSED)
                .startTime(Instant.now())
                .build();
        log.info("Call started: {} -> {}", callerId, calleeId);
        return repository.save(record);
    }

    public void answerCall(String recordId) {
        repository.findById(recordId).ifPresent(r -> {
            r.setStatus(CallRecord.CallStatus.ANSWERED);
            repository.save(r);
            log.info("Call answered: {}", recordId);
        });
    }

    public void rejectCall(String recordId) {
        repository.findById(recordId).ifPresent(r -> {
            r.setStatus(CallRecord.CallStatus.REJECTED);
            r.setEndTime(Instant.now());
            repository.save(r);
            log.info("Call rejected: {}", recordId);
        });
    }

    public void endCall(String recordId) {
        repository.findById(recordId).ifPresent(r -> {
            r.setEndTime(Instant.now());
            r.setDurationSeconds(r.getDurationSeconds());
            r.setStatus(CallRecord.CallStatus.ENDED);
            repository.save(r);
            log.info("Call ended: {} (duration: {}s)", recordId, r.getDurationSeconds());
        });
    }

    public List<CallRecord> getCallHistory(String userId, int page, int size) {
        return repository.findByUserId(userId, page * size, size);
    }

    public List<CallRecord> getMissedCalls(String userId) {
        return repository.findByCalleeIdAndStatus(userId, CallRecord.CallStatus.MISSED);
    }

    public CallStats getCallStats(String userId) {
        List<CallRecord> records = repository.findByUserIdAll(userId);
        long total = records.size();
        long missed = records.stream().filter(CallRecord::isMissed).count();
        long answered = records.stream().filter(r -> r.getStatus() == CallRecord.CallStatus.ANSWERED).count();
        long totalDuration = records.stream().mapToLong(CallRecord::getDurationSeconds).sum();
        return new CallStats(total, missed, answered, totalDuration);
    }

    public record CallStats(long total, long missed, long answered, long totalDurationSeconds) {}
}
