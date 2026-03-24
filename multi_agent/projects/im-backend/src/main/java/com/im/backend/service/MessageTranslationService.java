package com.im.backend.service;

import com.im.backend.dto.TranslationRequest;
import com.im.backend.dto.TranslationResponse;
import com.im.backend.entity.MessageTranslation;
import com.im.backend.entity.TranslationSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息翻译服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageTranslationService {

    private final Map<String, TranslationProvider> providers = new ConcurrentHashMap<>();

    /**
     * 翻译文本
     */
    @Transactional
    public TranslationResponse translate(TranslationRequest request) {
        long start = System.currentTimeMillis();
        String sourceLang = request.getSourceLang();
        String targetLang = request.getTargetLang();

        // 检测语言
        if (sourceLang == null || sourceLang.isEmpty()) {
            sourceLang = detectLanguage(request.getText());
        }

        // 如果源语言和目标语言相同，直接返回原文
        if (sourceLang.equals(targetLang)) {
            return TranslationResponse.builder()
                    .messageId(request.getMessageId())
                    .userId(request.getUserId())
                    .sourceLang(sourceLang)
                    .targetLang(targetLang)
                    .originalContent(request.getText())
                    .translatedContent(request.getText())
                    .translatedAt(LocalDateTime.now())
                    .autoTranslated(false)
                    .durationMs((int)(System.currentTimeMillis() - start))
                    .build();
        }

        // 调用翻译服务
        String translatedText = callTranslationProvider(request.getText(), sourceLang, targetLang);
        int durationMs = (int)(System.currentTimeMillis() - start);

        // 保存翻译记录
        MessageTranslation translation = MessageTranslation.builder()
                .originalMessageId(request.getMessageId())
                .userId(request.getUserId())
                .sourceLang(sourceLang)
                .targetLang(targetLang)
                .originalContent(request.getText())
                .translatedContent(translatedText)
                .provider("SIMULATED")
                .autoTranslated(request.getAutoTranslate())
                .translatedAt(LocalDateTime.now())
                .durationMs(durationMs)
                .build();

        // TODO: 保存到数据库
        // messageTranslationRepository.save(translation);

        return toResponse(translation);
    }

    /**
     * 语言检测（简化版，实际应调用Google/百度API）
     */
    private String detectLanguage(String text) {
        if (text == null || text.isEmpty()) return "unknown";
        // 简单规则检测
        if (text.matches(".*[\\u4e00-\\u9fa5].*")) return "zh";
        if (text.matches(".*[\\u3040-\\u309f\\u30a0-\\u30ff].*")) return "ja";
        if (text.matches(".*[\\uac00-\\ud7af].*")) return "ko";
        if (text.matches(".*[\\u0400-\\u04ff].*")) return "ru";
        if (text.matches(".*[\\u0600-\\u06ff].*")) return "ar";
        return "en";
    }

    /**
     * 调用翻译服务（实际应集成OpenAI/Claude/Gemini/百度翻译）
     */
    private String callTranslationProvider(String text, String sourceLang, String targetLang) {
        // TODO: 实际实现应调用真实的翻译API
        // 这里使用模拟翻译返回
        log.info("翻译请求: {} ({} -> {})", text.substring(0, Math.min(20, text.length())), sourceLang, targetLang);
        return "[翻译] " + text;
    }

    /**
     * 获取翻译设置
     */
    public TranslationSettings getSettings(Long userId) {
        // TODO: 从数据库获取
        return TranslationSettings.builder().userId(userId).autoTranslate(false)
                .preferredTargetLang("zh-CN").provider("OPENAI").showOriginal(true).build();
    }

    /**
     * 更新翻译设置
     */
    @Transactional
    public TranslationSettings updateSettings(Long userId, TranslationSettings settings) {
        // TODO: 保存到数据库
        settings.setUserId(userId);
        return settings;
    }

    private TranslationResponse toResponse(MessageTranslation t) {
        return TranslationResponse.builder()
                .id(t.getId())
                .messageId(t.getOriginalMessageId())
                .userId(t.getUserId())
                .sourceLang(t.getSourceLang())
                .targetLang(t.getTargetLang())
                .originalContent(t.getOriginalContent())
                .translatedContent(t.getTranslatedContent())
                .provider(t.getProvider())
                .model(t.getModel())
                .translatedAt(t.getTranslatedAt())
                .autoTranslated(t.getAutoTranslated())
                .durationMs(t.getDurationMs())
                .build();
    }

    /**
     * 翻译提供者接口
     */
    public interface TranslationProvider {
        String translate(String text, String sourceLang, String targetLang) throws Exception;
    }
}
