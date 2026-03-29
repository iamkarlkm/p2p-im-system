package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Entity
@Table(name = "im_message_reminder")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "reminder_time", nullable = false)
    private LocalDateTime reminderTime;

    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "is_triggered", nullable = false)
    private Boolean isTriggered;

    @Column(name = "is_dismissed", nullable = false)
    private Boolean isDismissed;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "repeat_type")
    private String repeatType;

    @Column(name = "remind_before_minutes")
    private Integer remindBeforeMinutes;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isTriggered == null) isTriggered = false;
        if (isDismissed == null) isDismissed = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
