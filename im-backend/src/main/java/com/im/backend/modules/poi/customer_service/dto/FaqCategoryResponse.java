package com.im.backend.modules.poi.customer_service.dto;

import lombok.Data;
import java.util.List;

/**
 * FAQ分类响应
 */
@Data
public class FaqCategoryResponse {

    private String category;
    private String categoryName;
    private List<FaqItem> faqs;

    @Data
    public static class FaqItem {
        private Long faqId;
        private String question;
        private String answer;
        private Boolean needTransfer;
    }
}
