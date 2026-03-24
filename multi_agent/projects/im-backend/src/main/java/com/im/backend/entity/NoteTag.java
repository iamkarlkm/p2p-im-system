package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.Instant;

@Data
@Entity
@Table(name = "note_tag", indexes = {
    @Index(name = "idx_tag_user", columnList = "userId")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 10)
    private String color;

    private Integer usageCount;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (usageCount == null) usageCount = 0;
    }
}
