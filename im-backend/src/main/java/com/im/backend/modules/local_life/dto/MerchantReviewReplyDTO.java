package com.im.backend.modules.local_life.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价回复DTO
 */
@Data
public class MerchantReviewReplyDTO {

    /** 回复ID */
    private Long id;

    /** 评价ID */
    private Long reviewId;

    /** 父回复ID */
    private Long parentId;

    /** 回复者类型：1-用户 2-商家 3-平台 */
    private Integer replyType;

    /** 回复者ID */
    private Long replyBy;

    /** 回复者名称 */
    private String replyName;

    /** 回复者头像 */
    private String replyAvatar;

    /** 被回复者ID */
    private Long replyTo;

    /** 被回复者名称 */
    private String replyToName;

    /** 回复内容 */
    private String content;

    /** 回复图片 */
    private List<String> images;

    /** 点赞数 */
    private Integer likeCount;

    /** 是否官方认证 */
    private Boolean official;

    /** 是否商家置顶 */
    private Boolean merchantPinned;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 子回复列表（楼中楼） */
    private List<MerchantReviewReplyDTO> children;
}
