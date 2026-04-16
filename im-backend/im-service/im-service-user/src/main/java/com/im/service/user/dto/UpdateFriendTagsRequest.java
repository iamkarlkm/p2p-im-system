package com.im.service.user.dto;

import lombok.Data;

import java.util.List;

/**
 * 更新好友标签请求DTO
 */
@Data
public class UpdateFriendTagsRequest {

    private List<String> tags;
}
