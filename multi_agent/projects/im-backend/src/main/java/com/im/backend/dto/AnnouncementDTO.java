package com.im.backend.dto;

import com.im.backend.entity.AnnouncementEntity;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Data
@Builder
public class AnnouncementDTO {
    private Long id;
    private Long groupId;
    private Long authorId;
    private String authorName;
    private String title;
    private String content;
    private Boolean pinned;
    private Boolean requiredRead;
    private Boolean urgent;
    private List<Long> attachmentIds;
    private String type;
    private LocalDateTime publishTime;
    private LocalDateTime expireTime;
    private Integer readCount;
    private Integer totalMemberCount;
    private Boolean isRead;       // 当前用户是否已读
    private Boolean isConfirmed;  // 当前用户是否已确认
    private LocalDateTime createdAt;

    private static final ObjectMapper mapper = new ObjectMapper();

    public static AnnouncementDTO fromEntity(AnnouncementEntity entity) {
        return fromEntity(entity, null, null, null, null);
    }

    public static AnnouncementDTO fromEntity(AnnouncementEntity entity, Boolean isRead, Boolean isConfirmed, Integer readCount, Integer totalMemberCount) {
        List<Long> attIds = null;
        if (entity.getAttachments() != null && !entity.getAttachments().isBlank()) {
            try {
                attIds = mapper.readValue(entity.getAttachments(), new TypeReference<List<Long>>() {});
            } catch (Exception ignored) {}
        }
        return AnnouncementDTO.builder()
            .id(entity.getId())
            .groupId(entity.getGroupId())
            .authorId(entity.getAuthorId())
            .title(entity.getTitle())
            .content(entity.getContent())
            .pinned(entity.getPinned())
            .requiredRead(entity.getRequiredRead())
            .urgent(entity.getUrgent())
            .attachmentIds(attIds)
            .type(entity.getType())
            .publishTime(entity.getPublishTime())
            .expireTime(entity.getExpireTime())
            .isRead(isRead)
            .isConfirmed(isConfirmed)
            .readCount(readCount)
            .totalMemberCount(totalMemberCount)
            .createdAt(entity.getCreatedAt())
            .build();
    }
}
