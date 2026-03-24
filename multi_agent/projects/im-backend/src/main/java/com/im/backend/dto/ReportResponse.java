package com.im.backend.dto;

import com.im.backend.entity.Report;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
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
    private String status;
    private String reviewerNote;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;

    public static ReportResponse fromEntity(Report report) {
        ReportResponse response = new ReportResponse();
        response.setReportId(report.getReportId());
        response.setReporterUserId(report.getReporterUserId());
        response.setReporterUsername(report.getReporterUsername());
        response.setReportedMessageId(report.getReportedMessageId());
        response.setReportedUserId(report.getReportedUserId());
        response.setConversationId(report.getConversationId());
        response.setConversationType(report.getConversationType());
        response.setReportReason(report.getReportReason());
        response.setReportCategory(report.getReportCategory());
        response.setDescription(report.getDescription());
        response.setStatus(report.getStatus().name());
        response.setReviewerNote(report.getReviewNote());
        response.setCreatedAt(report.getCreatedAt());
        response.setReviewedAt(report.getReviewedAt());
        return response;
    }
}
