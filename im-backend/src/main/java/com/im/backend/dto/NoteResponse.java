package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteResponse {
    private Long id;
    private String userId;
    private String conversationId;
    private String content;
    private String quotedMessageId;
    private String quotedMessageContent;
    private List<TagInfo> tags;
    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TagInfo {
        private Long id;
        private String name;
        private String color;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NotePage {
        private List<NoteResponse> items;
        private Integer page;
        private Integer size;
        private Long total;
        private Integer totalPages;
    }
}
