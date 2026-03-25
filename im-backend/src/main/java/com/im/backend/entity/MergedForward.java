package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "merged_forwards",
    indexes = {
        @Index(name = "idx_merged_id", columnList = "mergedForwardId"),
        @Index(name = "idx_merged_conversation", columnList = "targetConversationId")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MergedForward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String mergedForwardId;

    @Column(nullable = false)
    private Long targetConversationId;

    @Column(nullable = false)
    private Long forwardedBy;

    @Column(nullable = false)
    private LocalDateTime forwardedAt;

    @Column(length = 500)
    private String title;

    @OneToMany(mappedBy = "mergedForward", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MergedForwardMessage> messages;

    @Transient
    private List<MergedForwardMessageDTO> messageList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MergedForwardMessageDTO {
        private Long originalMessageId;
        private String senderName;
        private String content;
        private LocalDateTime sentAt;
        private String contentType;
    }
}
