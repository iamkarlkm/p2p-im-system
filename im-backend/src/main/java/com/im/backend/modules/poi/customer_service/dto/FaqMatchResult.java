package com.im.backend.modules.poi.customer_service.dto;

import lombok.Data;

/**
 * 智能客服匹配结果
 */
@Data
public class FaqMatchResult {

    /**
     * 是否匹配成功
     */
    private Boolean matched;

    /**
     * 匹配的FAQ ID
     */
    private Long faqId;

    /**
     * 匹配的问题
     */
    private String question;

    /**
     * 匹配答案
     */
    private String answer;

    /**
     * 匹配置信度(0-100)
     */
    private Integer confidence;

    /**
     * 是否需要转人工
     */
    private Boolean needTransfer;

    /**
     * 转人工提示语
     */
    private String transferHint;
}
