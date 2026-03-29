package com.im.backend.modules.local_life.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 创建评价回复请求DTO
 */
@Data
public class CreateReplyRequestDTO {

    /** 评价ID */
    @NotNull(message = "评价ID不能为空")
    private Long reviewId;

    /** 父回复ID（楼中楼回复时使用） */
    private Long parentId;

    /** 被回复者ID */
    private Long replyTo;

    /** 被回复者名称 */
    private String replyToName;

    /** 回复内容 */
    @NotBlank(message = "回复内容不能为空")
    @Size(max = 500, message = "回复内容最多500字")
    private String content;

    /** 回复图片 */
    private List<String> images;
}
