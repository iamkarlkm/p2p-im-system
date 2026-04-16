package com.im.service.group.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 群组响应 DTO
 */
@Data
public class GroupResponse {
    private String id;
    private String name;
    private String description;
    private String avatar;
    private String type;
    private String ownerId;
    private Integer memberCount;
    private Integer maxMembers;
    private LocalDateTime createdAt;
}
