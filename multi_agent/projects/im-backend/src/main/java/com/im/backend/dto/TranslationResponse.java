package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 翻译响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TranslationResponse {

    private Long id;
    private Long messageId;
    private Long userId;
    private String sourceLang;
    private String targetLang;
    private String originalContent;
    private String translatedContent;
    private String provider;
    private String model;
    private LocalDateTime translatedAt;
    private Boolean autoTranslated;
    private Integer durationMs;
}
