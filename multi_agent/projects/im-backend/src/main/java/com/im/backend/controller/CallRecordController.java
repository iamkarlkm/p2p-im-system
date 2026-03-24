package com.im.backend.controller;

import com.im.backend.dto.CallRecordDTO;
import com.im.backend.service.CallRecordService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/calls")
@RequiredArgsConstructor
public class CallRecordController {

    private final CallRecordService callRecordService;

    @Data
    public static class InitiateRequest {
        private String calleeId;
        private String conversationId;
        private String callType; // AUDIO / VIDEO
    }

    @Data
    public static class StatusRequest {
        private String status; // RINGING / ANSWERED / REJECTED / FAILED
    }

    @Data
    public static class EndRequest {
        private Boolean endedByCaller;
    }

    /** POST /api/v1/calls/initiate - 发起通话 */
    @PostMapping("/initiate")
    public ResponseEntity<CallRecordDTO> initiate(
            @RequestHeader("X-User-Id") String callerId,
            @RequestBody InitiateRequest req) {
        CallRecordDTO dto = callRecordService.initiateCall(
                callerId, req.getCalleeId(), req.getConversationId(), req.getCallType());
        return ResponseEntity.ok(dto);
    }

    /** PUT /api/v1/calls/{callId}/status - 更新通话状态 */
    @PutMapping("/{callId}/status")
    public ResponseEntity<CallRecordDTO> updateStatus(
            @PathVariable String callId,
            @RequestBody StatusRequest req) {
        CallRecordDTO dto = callRecordService.updateStatus(callId, req.getStatus());
        return ResponseEntity.ok(dto);
    }

    /** POST /api/v1/calls/{callId}/end - 结束通话 */
    @PostMapping("/{callId}/end")
    public ResponseEntity<CallRecordDTO> endCall(
            @PathVariable String callId,
            @RequestBody EndRequest req) {
        CallRecordDTO dto = callRecordService.endCall(callId, req.getEndedByCaller() != null && req.getEndedByCaller());
        return ResponseEntity.ok(dto);
    }

    /** POST /api/v1/calls/{callId}/missed - 标记未接 */
    @PostMapping("/{callId}/missed")
    public ResponseEntity<Void> markMissed(@PathVariable String callId) {
        callRecordService.markMissed(callId);
        return ResponseEntity.ok().build();
    }

    /** GET /api/v1/calls/history - 获取通话历史 */
    @GetMapping("/history")
    public ResponseEntity<Page<CallRecordDTO>> getHistory(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(callRecordService.getCallHistory(userId, page, size));
    }

    /** GET /api/v1/calls/missed - 未接来电 */
    @GetMapping("/missed")
    public ResponseEntity<List<CallRecordDTO>> getMissed(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(callRecordService.getMissedCalls(userId));
    }

    /** DELETE /api/v1/calls/{id} - 删除记录 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        callRecordService.deleteCall(id);
        return ResponseEntity.noContent().build();
    }
}
