package com.im.backend.dto;

import com.im.backend.entity.SharedMedia;
import com.im.backend.entity.MediaLink;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedMediaResponse {
    private Long id;
    private String conversationId;
    private String messageId;
    private String senderId;
    private String senderName;
    private String senderAvatar;
    private SharedMedia.MediaType mediaType;
    private String fileName;
    private String fileUrl;
    private String thumbnailUrl;
    private Long fileSize;
    private String mimeType;
    private Integer width;
    private Integer height;
    private Long duration;
    private String description;
    private Instant createdAt;
    private Boolean canDelete;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MediaPage {
        private List<SharedMediaResponse> items;
        private Integer page;
        private Integer size;
        private Long total;
        private Integer totalPages;
        private SharedMedia.MediaType mediaType;
        private Long totalSize;
        private MediaStatistics statistics;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MediaStatistics {
        private Long imageCount;
        private Long videoCount;
        private Long audioCount;
        private Long fileCount;
        private Long linkCount;
        private Long totalSize;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LinkPreview {
        private Long id;
        private String conversationId;
        private String messageId;
        private String url;
        private String title;
        private String description;
        private String image;
        private String domain;
        private MediaLink.MediaLinkType linkType;
        private Instant createdAt;
    }
}
