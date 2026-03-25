package com.im.server.rtc;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通话记录服务
 */
@Service
public class CallHistoryService {

    private final Map<Long, List<CallRecord>> userCallHistory = new ConcurrentHashMap<>();
    private final Map<String, CallRecord> activeRecords = new ConcurrentHashMap<>();

    /**
     * 开始通话记录
     */
    public CallRecord startCall(Long callerId, Long calleeId, String callType) {
        CallRecord record = new CallRecord();
        record.id = UUID.randomUUID().toString();
        record.callerId = callerId;
        record.calleeId = calleeId;
        record.callType = callType;
        record.status = "calling";
        record.startTime = System.currentTimeMillis();
        
        activeRecords.put(record.id, record);
        return record;
    }

    /**
     * 通话接通
     */
    public void connectCall(String callId) {
        CallRecord record = activeRecords.get(callId);
        if (record != null) {
            record.status = "connected";
            record.connectTime = System.currentTimeMillis();
        }
    }

    /**
     * 通话结束
     */
    public void endCall(String callId) {
        CallRecord record = activeRecords.remove(callId);
        if (record != null) {
            record.status = "ended";
            record.endTime = System.currentTimeMillis();
            
            // 计算通话时长
            if (record.connectTime > 0) {
                record.duration = record.endTime - record.connectTime;
            }
            
            // 保存到用户历史记录
            userCallHistory.computeIfAbsent(record.callerId, k -> new ArrayList<>()).add(record);
            userCallHistory.computeIfAbsent(record.calleeId, k -> new ArrayList<>()).add(record);
        }
    }

    /**
     * 获取用户通话历史
     */
    public List<CallRecord> getCallHistory(Long userId, int page, int size) {
        List<CallRecord> history = userCallHistory.getOrDefault(userId, new ArrayList<>());
        Collections.sort(history, (a, b) -> Long.compare(b.startTime, a.startTime));
        
        int fromIndex = page * size;
        if (fromIndex >= history.size()) {
            return new ArrayList<>();
        }
        
        int toIndex = Math.min(fromIndex + size, history.size());
        return new ArrayList<>(history.subList(fromIndex, toIndex));
    }

    /**
     * 获取未接来电
     */
    public List<CallRecord> getMissedCalls(Long userId, int limit) {
        List<CallRecord> history = userCallHistory.getOrDefault(userId, new ArrayList<>());
        List<CallRecord> missed = new ArrayList<>();
        
        for (CallRecord record : history) {
            if (record.calleeId.equals(userId) && "missed".equals(record.status)) {
                missed.add(record);
                if (missed.size() >= limit) break;
            }
        }
        
        return missed;
    }

    /**
     * 标记为未接来电
     */
    public void markAsMissed(String callId) {
        CallRecord record = activeRecords.get(callId);
        if (record != null) {
            record.status = "missed";
            record.endTime = System.currentTimeMillis();
            
            userCallHistory.computeIfAbsent(record.calleeId, k -> new ArrayList<>()).add(record);
            activeRecords.remove(callId);
        }
    }

    /**
     * 通话记录内部类
     */
    public static class CallRecord {
        public String id;
        public Long callerId;
        public Long calleeId;
        public String callType;
        public String status;
        public long startTime;
        public long connectTime;
        public long endTime;
        public long duration;
    }
}
