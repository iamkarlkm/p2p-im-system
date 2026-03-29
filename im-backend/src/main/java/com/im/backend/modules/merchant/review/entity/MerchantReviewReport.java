package com.im.backend.modules.merchant.review.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 虚假评价举报记录实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_review_report")
public class MerchantReviewReport {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 举报ID */
    private String reportId;

    /** 评价ID */
    private String reviewId;

    /** 举报人ID */
    private Long reporterId;

    /** 举报类型 1-虚假评价 2-恶意差评 3-广告垃圾 4-违法违规 5-其他 */
    private Integer reportType;

    /** 举报原因描述 */
    private String reason;

    /** 举报证据图片 */
    private String evidenceImages;

    /** 处理状态 0-待处理 1-已处理-举报成立 2-已处理-举报不成立 */
    private Integer status;

    /** 处理结果说明 */
    private String result;

    /** 处理人ID */
    private Long handlerId;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 处理时间 */
    private LocalDateTime handledAt;
}
