package com.im.service.group.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 创建群公告请求 DTO
 * 
 * @author IM Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnnouncementRequest {

    /**
     * 群组ID
     */
    @NotBlank(message = "群组ID不能为空")
    private String groupId;

    /**
     * 公告标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200")
    private String title;

    /**
     * 公告内容
     */
    @NotBlank(message = "内容不能为空")
    private String content;
}
