package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Entity
@Table(name = "contact_pinned", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_contact", columnNames = {"user_id", "contact_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactPinned {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "contact_id", nullable = false)
    private Long contactId;

    @Column(name = "pin_order", nullable = false)
    private Integer pinOrder;

    @Column(name = "pinned_at", nullable = false)
    private LocalDateTime pinnedAt;

    @Column(name = "note")
    private String note;

    @PrePersist
    protected void onCreate() {
        if (pinnedAt == null) {
            pinnedAt = LocalDateTime.now();
        }
        if (pinOrder == null) {
            pinOrder = 0;
        }
    }
}
