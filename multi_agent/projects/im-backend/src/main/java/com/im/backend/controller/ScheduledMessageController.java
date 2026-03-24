package com.im.backend.controller;

import com.im.backend.dto.ScheduledMessageRequest;
import com.im.backend.dto.ScheduledMessageResponse;
import com.im.backend.service.ScheduledMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 定时消息控制器
 */
@RestController
@RequestMapping("/api/scheduled-messages")
public class ScheduledMessageController {

    private final ScheduledMessageService scheduledMessageService;

    public ScheduledMessageController(ScheduledMessageService scheduledMessageService) {
        this.scheduledMessageService = scheduledMessageService;
    }

    @PostMapping
    public ResponseEntity<ScheduledMessageResponse> scheduleMessage(
            @RequestHeader("X-User-Id") Long senderId,
            @RequestBody ScheduledMessageRequest request) {
        return ResponseEntity.ok(scheduledMessageService.scheduleMessage(senderId, request));
    }

    @GetMapping
    public ResponseEntity<List<ScheduledMessageResponse>> getUserScheduledMessages(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(scheduledMessageService.getUserScheduledMessages(userId));
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduledMessageResponse> getScheduledMessage(@PathVariable String scheduleId) {
        return ResponseEntity.ok(scheduledMessageService.getScheduledMessage(scheduleId));
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduledMessageResponse> updateScheduledMessage(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String scheduleId,
            @RequestBody ScheduledMessageRequest request) {
        return ResponseEntity.ok(scheduledMessageService.updateScheduledMessage(userId, scheduleId, request));
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> cancelScheduledMessage(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String scheduleId) {
        scheduledMessageService.cancelScheduledMessage(userId, scheduleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserScheduleStats(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(scheduledMessageService.getUserScheduleStats(userId));
    }
}
