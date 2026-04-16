package com.im.service.group.dto;

import lombok.Data;

/**
 * 创建群组请求 DTO
 */
@Data
public class CreateGroupRequest {
    private String name;
    private String description;
    private String type;
    private String avatar;
    private Integer maxMembers;
}
