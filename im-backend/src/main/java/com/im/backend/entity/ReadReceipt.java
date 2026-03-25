package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "im_read_receipt",
       indexes = {
           @Index(name = "idx_receipt_user", columnList = "userId"),
           @Index(name = "idx_receipt_message", columnList = "messageId"),
           @Index(name = "idx_receipt_conversation", columnList = "conversationId, userId")
       })
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String messageId;

    @Column(nullable = false)
    private String conversationId;

    @Column
    private Long readAt;

    @Column
    private Boolean isBatch;

    @PrePersist
    public void prePersist() {
        if (this.readAt == null) {
            this.readAt = System.currentTimeMillis();
        }
        if (this.isBatch == null) {
            this.isBatch = false;
        }
    }
}
