package com.im.backend.modules.local.life.dto;

import lombok.Data;

/**
 * 活动媒体DTO
 */
@Data
public class ActivityMediaDTO {

    private Long id;
    private String mediaCode;
    private String mediaType;
    private String mediaUrl;
    private String thumbnailUrl;
    private Integer duration;
    private Long fileSize;
    private Integer width;
    private Integer height;
    private String description;
    private Boolean isCover;
}
