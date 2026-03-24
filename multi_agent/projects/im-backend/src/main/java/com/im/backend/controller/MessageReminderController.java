package com.im.backend.controller;

import com.im.backend.dto.ReminderRequest;
import com.im.backend.dto.ReminderResponse;
import com.im.backend.service.MessageReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reminders")
@RequiredArgsConstructor
public class MessageReminderController {

    private final MessageReminderService reminderService;

    @PostMapping
    public ResponseEntity<ReminderResponse> createReminder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody ReminderRequest request) {
        return ResponseEntity.ok(reminderService.createReminder(userId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReminderResponse> updateReminder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @RequestBody ReminderRequest request) {
        return ResponseEntity.ok(reminderService.updateReminder(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteReminder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        reminderService.deleteReminder(userId, id);
        return ResponseEntity.ok(Map.of("message", "Reminder deleted"));
    }

    @PostMapping("/{id}/dismiss")
    public ResponseEntity<ReminderResponse> dismissReminder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(reminderService.dismissReminder(userId, id));
    }

    @GetMapping
    public ResponseEntity<List<ReminderResponse>> getUserReminders(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(reminderService.getUserReminders(userId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ReminderResponse>> getPendingReminders(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(reminderService.getPendingReminders(userId));
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getPendingCount(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(Map.of("count", reminderService.getPendingCount(userId)));
    }

    @PostMapping("/trigger")
    public ResponseEntity<List<ReminderResponse>> triggerDueReminders() {
        return ResponseEntity.ok(reminderService.triggerDueReminders());
    }
}
