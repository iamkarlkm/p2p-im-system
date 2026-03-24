package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    private Long reportedMessageId;
    private Long reportedUserId;
    private Long conversationId;
    private String conversationType;
    private String reportReason;
    private String reportCategory;
    private String description;
    private String evidence;
}
