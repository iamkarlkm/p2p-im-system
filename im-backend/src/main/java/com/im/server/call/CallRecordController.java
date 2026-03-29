package com.im.server.call;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/calls")
@RequiredArgsConstructor
public class CallRecordController {

    private final CallRecordService callService;

    @PostMapping("/start")
    public ResponseEntity<CallRecord> startCall(@RequestParam String callerId,
                                                 @RequestParam String calleeId,
                                                 @RequestParam String conversationId,
                                                 @RequestParam(defaultValue = "AUDIO") String type) {
        return ResponseEntity.ok(callService.startCall(callerId, calleeId, conversationId, type));
    }

    @PostMapping("/answer/{recordId}")
    public ResponseEntity<Void> answerCall(@PathVariable String recordId) {
        callService.answerCall(recordId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject/{recordId}")
    public ResponseEntity<Void> rejectCall(@PathVariable String recordId) {
        callService.rejectCall(recordId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/end/{recordId}")
    public ResponseEntity<Void> endCall(@PathVariable String recordId) {
        callService.endCall(recordId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    public ResponseEntity<List<CallRecord>> getHistory(@RequestParam String userId,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(callService.getCallHistory(userId, page, size));
    }

    @GetMapping("/missed")
    public ResponseEntity<List<CallRecord>> getMissed(@RequestParam String userId) {
        return ResponseEntity.ok(callService.getMissedCalls(userId));
    }

    @GetMapping("/stats")
    public ResponseEntity<CallStats> getStats(@RequestParam String userId) {
        return ResponseEntity.ok(callService.getCallStats(userId));
    }

    public record CallStats(long total, long missed, long answered, long totalDurationSeconds) {}
}
