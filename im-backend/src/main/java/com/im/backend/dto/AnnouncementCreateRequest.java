package com.im.backend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.time.LocalDateTime;

@Data
public class AnnouncementCreateRequest {
    private Long groupId;
    private Long authorId;

    @Size(max = 200, message = "标题最多200字")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private Boolean pinned = false;
    private Boolean requiredRead = false;
    private Boolean urgent = false;

    /** 附件文件ID列表 */
    private List<Long> attachments;

    /** 公告类型: normal/rule/notice/event */
    private String type = "normal";

    /** 过期时间（null=永不过期） */
    private LocalDateTime expireTime;
}
