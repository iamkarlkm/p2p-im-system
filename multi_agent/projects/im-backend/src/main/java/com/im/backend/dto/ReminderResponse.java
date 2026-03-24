package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderResponse {

    private Long id;

    private Long messageId;

    private Long conversationId;

    private LocalDateTime reminderTime;

    private String note;

    private Boolean isTriggered;

    private Boolean isDismissed;

    private LocalDateTime createdAt;

    private String repeatType;

    private Integer remindBeforeMinutes;

    private String messagePreview;

    private String conversationName;
}
