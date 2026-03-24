package com.im.backend.controller;

import com.im.backend.dto.ScreenshotEventRequest;
import com.im.backend.dto.ScreenshotEventResponse;
import com.im.backend.entity.ScreenshotSettings;
import com.im.backend.service.ScreenshotNotificationService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/screenshot")
public class ScreenshotNotificationController {

    private final ScreenshotNotificationService screenshotService;

    public ScreenshotNotificationController(ScreenshotNotificationService screenshotService) {
        this.screenshotService = screenshotService;
    }

    @PostMapping("/report")
    public ScreenshotEventResponse reportScreenshot(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Username") String username,
            @RequestBody ScreenshotEventRequest request) {
        return screenshotService.reportScreenshot(userId, username, request);
    }

    @GetMapping("/settings")
    public ScreenshotSettings getSettings(@RequestHeader("X-User-Id") Long userId) {
        return screenshotService.getSettings(userId);
    }

    @PutMapping("/settings")
    public ScreenshotSettings updateSettings(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody ScreenshotSettings settings) {
        return screenshotService.updateSettings(userId, settings);
    }

    @GetMapping("/history")
    public List<ScreenshotEventResponse> getHistory(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "50") int limit) {
        return screenshotService.getScreenshotHistory(userId, limit);
    }

    @DeleteMapping("/event/{eventId}")
    public Map<String, Object> deleteEvent(@PathVariable String eventId) {
        boolean deleted = screenshotService.deleteEvent(eventId);
        return Map.of("success", deleted, "eventId", eventId);
    }

    @DeleteMapping("/history")
    public Map<String, Object> clearHistory(@RequestHeader("X-User-Id") Long userId) {
        boolean cleared = screenshotService.clearHistory(userId);
        return Map.of("success", cleared, "message", "Screenshot history cleared");
    }
}
