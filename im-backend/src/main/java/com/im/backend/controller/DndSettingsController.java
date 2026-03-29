package com.im.backend.controller;

import com.im.backend.service.DndSettingsService;
import com.im.backend.dto.DndSettingsRequest;
import com.im.backend.dto.DndSettingsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/dnd")
@RequiredArgsConstructor
@Slf4j
public class DndSettingsController {

    private final DndSettingsService dndSettingsService;

    @GetMapping("/settings")
    public ResponseEntity<?> getSettings(@RequestHeader("X-User-Id") Long userId) {
        Optional<DndSettingsResponse> settings = dndSettingsService.getByUserId(userId);
        if (settings.isPresent()) {
            return ResponseEntity.ok(settings.get());
        }
        return ResponseEntity.ok(Map.of(
                "enabled", false,
                "startTime", "22:00",
                "endTime", "08:00",
                "timezone", "Asia/Shanghai",
                "repeatDays", "1,2,3,4,5,6,7",
                "allowMentions", true,
                "allowStarred", true
        ));
    }

    @PostMapping("/settings")
    public ResponseEntity<?> saveSettings(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody DndSettingsRequest request) {
        DndSettingsResponse response = dndSettingsService.saveOrUpdate(userId, request);
        log.info("User {} saved DND settings", userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<?> getDndStatus(@RequestHeader("X-User-Id") Long userId) {
        boolean inDnd = dndSettingsService.isInDndPeriod(userId);
        boolean allowMention = dndSettingsService.shouldAllowMention(userId);
        boolean allowStarred = dndSettingsService.shouldAllowStarred(userId);
        return ResponseEntity.ok(Map.of(
                "inDndPeriod", inDnd,
                "allowMention", allowMention,
                "allowStarred", allowStarred
        ));
    }

    @DeleteMapping("/settings")
    public ResponseEntity<?> deleteSettings(@RequestHeader("X-User-Id") Long userId) {
        dndSettingsService.deleteByUserId(userId);
        return ResponseEntity.ok(Map.of("success", true, "message", "DND settings deleted"));
    }
}
