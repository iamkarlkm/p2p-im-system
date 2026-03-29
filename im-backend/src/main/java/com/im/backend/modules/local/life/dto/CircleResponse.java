package com.im.backend.modules.local.life.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交圈子响应DTO
 */
@Data
public class CircleResponse {

    private Long id;
    private String circleCode;
    private String name;
    private String description;
    private String avatar;
    private String coverImage;

    private String category;
    private String categoryName;
    private String circleType;
    private String circleTypeName;
    private String status;

    private Long creatorId;
    private String creatorNickname;
    private Long ownerId;
    private String ownerNickname;

    private List<String> poiTypeTags;
    private Double longitude;
    private Double latitude;
    private String cityName;

    private Integer memberCount;
    private Integer postCount;
    private Integer todayActiveCount;

    private Integer level;
    private Integer points;

    private String announcement;
    private Boolean requireApproval;
    private String postPermission;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private Boolean isMember;
    private String memberRole;
    private Integer userPoints;
}
