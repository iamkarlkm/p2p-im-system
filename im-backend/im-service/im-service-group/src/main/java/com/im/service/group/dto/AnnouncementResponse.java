package com.im.service.group.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 群公告响应 DTO
 * 
 * @author IM Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementResponse {

    /** 公告ID */
    private String id;

    /** 群组ID */
    private String groupId;

    /** 创建者ID */
    private String creatorId;

    /** 创建者昵称 */
    private String creatorNickname;

    /** 公告标题 */
    private String title;

    /** 公告内容 */
    private String content;

    /** 是否置顶 */
    private Boolean isPinned;

    /** 置顶时间 */
    private LocalDateTime pinnedAt;

    /** 阅读次数 */
    private Integer readCount;

    /** 是否已读（当前用户） */
    private Boolean isRead;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 是否当前用户创建 */
    private Boolean isOwner;
}
