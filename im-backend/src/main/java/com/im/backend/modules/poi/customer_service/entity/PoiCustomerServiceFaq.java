package com.im.backend.modules.poi.customer_service.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 智能客服FAQ知识库实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("poi_cs_faq")
public class PoiCustomerServiceFaq {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * POI商户ID(0表示平台通用FAQ)
     */
    private Long poiId;

    /**
     * FAQ分类: BUSINESS_HOURS-营业时间, PRICE-价格咨询, LOCATION-位置导航, 
     * SERVICE-服务内容, RESERVATION-预约相关, PARKING-停车信息, WIFI-WIFI密码, OTHER-其他
     */
    private String category;

    /**
     * 问题内容
     */
    private String question;

    /**
     * 标准答案
     */
    private String answer;

    /**
     * 相似问题(JSON数组)
     */
    private String similarQuestions;

    /**
     * 关键词(逗号分隔,用于匹配)
     */
    private String keywords;

    /**
     * 关联POI商品ID(可选)
     */
    private Long relatedProductId;

    /**
     * 回复类型: TEXT-纯文本, RICH-富文本, IMAGE-图文, LINK-链接跳转
     */
    private String replyType;

    /**
     * 富媒体内容(JSON)
     */
    private String richContent;

    /**
     * 命中次数
     */
    private Integer hitCount;

    /**
     * 有用次数
     */
    private Integer helpfulCount;

    /**
     * 无用次数
     */
    private Integer unhelpfulCount;

    /**
     * 排序权重
     */
    private Integer sortOrder;

    /**
     * 是否需要转人工: 0-不需要, 1-需要
     */
    private Boolean needTransfer;

    /**
     * 转人工提示语
     */
    private String transferHint;

    /**
     * 生效时间范围(JSON)
     */
    private String effectiveTime;

    /**
     * 是否启用
     */
    private Boolean enabled;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
