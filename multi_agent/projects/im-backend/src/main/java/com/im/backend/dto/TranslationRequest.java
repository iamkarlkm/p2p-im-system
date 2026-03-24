package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 翻译请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TranslationRequest {

    /** 消息ID */
    @NotNull(message = "消息ID不能为空")
    private Long messageId;

    /** 原始语言代码（如 en, zh, ja） */
    private String sourceLang;

    /** 目标语言代码 */
    @NotBlank(message = "目标语言不能为空")
    private String targetLang;

    /** 原始文本内容 */
    @NotBlank(message = "原文内容不能为空")
    private String text;

    /** 用户ID */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /** 是否自动翻译 */
    private Boolean autoTranslate = false;
}
