package com.im.backend.controller;

import com.im.backend.entity.Report;
import com.im.backend.entity.ModerationSettings;
import com.im.backend.dto.ReportRequest;
import com.im.backend.dto.ReportResponse;
import com.im.backend.service.ReportModerationService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/report")
public class ReportModerationController {

    private final ReportModerationService reportService;

    public ReportModerationController(ReportModerationService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/submit")
    public ReportResponse submitReport(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Username") String username,
            @RequestBody ReportRequest request) {
        return reportService.submitReport(userId, username, request);
    }

    @GetMapping("/my-reports")
    public List<ReportResponse> getMyReports(@RequestHeader("X-User-Id") Long userId) {
        return reportService.getMyReports(userId);
    }

    @GetMapping("/list")
    public List<ReportResponse> getReports(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Report.ReportStatus reportStatus = status != null ? Report.ReportStatus.valueOf(status.toUpperCase()) : null;
        return reportService.getReports(reportStatus, page, size);
    }

    @GetMapping("/{reportId}")
    public ReportResponse getReport(@PathVariable String reportId) {
        return reportService.getReport(reportId);
    }

    @PutMapping("/{reportId}/review")
    public ReportResponse reviewReport(
            @PathVariable String reportId,
            @RequestHeader("X-User-Id") String reviewerId,
            @RequestBody Map<String, String> body) {
        Report.ReportStatus newStatus = Report.ReportStatus.valueOf(body.getOrDefault("status", "RESOLVED").toUpperCase());
        String reviewNote = body.getOrDefault("reviewNote", "");
        return reportService.reviewReport(reportId, reviewerId, newStatus, reviewNote);
    }

    @GetMapping("/settings")
    public ModerationSettings getSettings(@RequestHeader("X-User-Id") Long userId) {
        return reportService.getSettings(userId);
    }

    @PutMapping("/settings")
    public ModerationSettings updateSettings(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody ModerationSettings settings) {
        return reportService.updateSettings(userId, settings);
    }

    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        return reportService.getStatistics();
    }
}
