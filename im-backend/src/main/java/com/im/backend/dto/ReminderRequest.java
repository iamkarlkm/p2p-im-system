package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderRequest {

    @NotNull(message = "Message ID is required")
    private Long messageId;

    @NotNull(message = "Conversation ID is required")
    private Long conversationId;

    @NotNull(message = "Reminder time is required")
    private LocalDateTime reminderTime;

    private String note;

    private String repeatType;

    private Integer remindBeforeMinutes;
}
