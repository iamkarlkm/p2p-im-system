package com.im.backend.service;

import com.im.backend.entity.Report;
import com.im.backend.entity.ModerationSettings;
import com.im.backend.dto.ReportRequest;
import com.im.backend.dto.ReportResponse;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ReportModerationService {

    private final Map<String, Report> reportStore = new ConcurrentHashMap<>();
    private final Map<Long, ModerationSettings> settingsStore = new ConcurrentHashMap<>();

    public ReportResponse submitReport(Long reporterUserId, String reporterUsername, ReportRequest request) {
        String reportId = UUID.randomUUID().toString();
        Report report = new Report(reportId, reporterUserId, reporterUsername, request.getReportedMessageId(), request.getReportReason());
        report.setReportedUserId(request.getReportedUserId());
        report.setConversationId(request.getConversationId());
        report.setConversationType(request.getConversationType());
        report.setReportCategory(request.getReportCategory());
        report.setDescription(request.getDescription());
        report.setEvidence(request.getEvidence());
        report.setStatus(Report.ReportStatus.PENDING);
        report.setCreatedAt(LocalDateTime.now());

        reportStore.put(reportId, report);
        return ReportResponse.fromEntity(report);
    }

    public ReportResponse reviewReport(String reportId, String reviewerId, Report.ReportStatus newStatus, String reviewNote) {
        Report report = reportStore.get(reportId);
        if (report == null) return null;

        report.setStatus(newStatus);
        report.setReviewerId(reviewerId);
        report.setReviewNote(reviewNote);
        report.setReviewedAt(LocalDateTime.now());

        return ReportResponse.fromEntity(report);
    }

    public List<ReportResponse> getReports(Report.ReportStatus status, int page, int size) {
        return reportStore.values().stream()
                .filter(r -> status == null || r.getStatus() == status)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .skip((long) page * size)
                .limit(size)
                .map(ReportResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public ReportResponse getReport(String reportId) {
        Report report = reportStore.get(reportId);
        return report != null ? ReportResponse.fromEntity(report) : null;
    }

    public List<ReportResponse> getMyReports(Long userId) {
        return reportStore.values().stream()
                .filter(r -> r.getReporterUserId().equals(userId))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(ReportResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public ModerationSettings getSettings(Long userId) {
        return settingsStore.computeIfAbsent(userId, ModerationSettings::defaultSettings);
    }

    public ModerationSettings updateSettings(Long userId, ModerationSettings settings) {
        settings.setUserId(userId);
        settingsStore.put(userId, settings);
        return settings;
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", reportStore.size());
        stats.put("pending", reportStore.values().stream().filter(r -> r.getStatus() == Report.ReportStatus.PENDING).count());
        stats.put("resolved", reportStore.values().stream().filter(r -> r.getStatus() == Report.ReportStatus.RESOLVED).count());
        stats.put("dismissed", reportStore.values().stream().filter(r -> r.getStatus() == Report.ReportStatus.DISMISSED).count());
        return stats;
    }
}
