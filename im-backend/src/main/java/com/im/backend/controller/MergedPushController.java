package com.im.backend.controller;

import com.im.backend.service.MergedPushService;
import com.im.backend.dto.MergedPushRequest;
import com.im.backend.dto.MergedPushResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/push/merged")
@RequiredArgsConstructor
public class MergedPushController {
    private final MergedPushService mergedPushService;

    @PostMapping("/buffer")
    public ResponseEntity<MergedPushResponse> bufferMessage(@RequestBody MergedPushRequest request) {
        return ResponseEntity.ok(mergedPushService.bufferPushMessage(request));
    }

    @PostMapping("/{id}/flush")
    public ResponseEntity<MergedPushResponse> flushBuffer(@PathVariable Long id) {
        return ResponseEntity.ok(mergedPushService.getPendingPushes("").stream()
            .filter(p -> p.getId() != null && p.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Merged push not found")));
    }

    @PostMapping("/{id}/sent")
    public ResponseEntity<Void> markSent(@PathVariable Long id) {
        mergedPushService.markAsSent(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/failed")
    public ResponseEntity<Void> markFailed(@PathVariable Long id) {
        mergedPushService.markAsFailed(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pending")
    public ResponseEntity<List<MergedPushResponse>> getPending(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(mergedPushService.getPendingPushes(userId));
    }

    @GetMapping("/stats")
    public ResponseEntity<MergedPushResponse.PushStatsResponse> getStats() {
        return ResponseEntity.ok(mergedPushService.getPushStats());
    }

    @GetMapping("/buffer-status")
    public ResponseEntity<MergedPushResponse.BufferStatusResponse> getBufferStatus() {
        return ResponseEntity.ok(mergedPushService.getBufferStatus());
    }
}
