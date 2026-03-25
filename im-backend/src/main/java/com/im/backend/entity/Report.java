package com.im.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    private String reportId;
    private Long reporterUserId;
    private String reporterUsername;
    private Long reportedMessageId;
    private Long reportedUserId;
    private Long conversationId;
    private String conversationType;
    private String reportReason;
    private String reportCategory;
    private String description;
    private String evidence;
    private ReportStatus status;
    private String reviewerId;
    private String reviewNote;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;

    public enum ReportStatus {
        PENDING, REVIEWING, RESOLVED, DISMISSED, ESCALATED
    }

    public Report(String reportId, Long reporterUserId, String reporterUsername, Long reportedMessageId, String reportReason) {
        this.reportId = reportId;
        this.reporterUserId = reporterUserId;
        this.reporterUsername = reporterUsername;
        this.reportedMessageId = reportedMessageId;
        this.reportReason = reportReason;
        this.status = ReportStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
}
