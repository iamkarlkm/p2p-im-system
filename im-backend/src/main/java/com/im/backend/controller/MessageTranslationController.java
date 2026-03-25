package com.im.backend.controller;

import com.im.backend.dto.TranslationRequest;
import com.im.backend.dto.TranslationResponse;
import com.im.backend.entity.TranslationSettings;
import com.im.backend.service.MessageTranslationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息翻译控制器
 */
@RestController
@RequestMapping("/api/translation")
@RequiredArgsConstructor
public class MessageTranslationController {

    private final MessageTranslationService translationService;

    /**
     * 翻译文本
     */
    @PostMapping("/translate")
    public ResponseEntity<Map<String, Object>> translate(
            @Valid @RequestBody TranslationRequest request) {
        TranslationResponse response = translationService.translate(request);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);
        return ResponseEntity.ok(result);
    }

    /**
     * 批量翻译
     */
    @PostMapping("/translate/batch")
    public ResponseEntity<Map<String, Object>> batchTranslate(
            @Valid @RequestBody java.util.List<TranslationRequest> requests) {
        java.util.List<TranslationResponse> responses = requests.stream()
                .map(translationService::translate)
                .toList();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", responses);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取翻译设置
     */
    @GetMapping("/settings")
    public ResponseEntity<Map<String, Object>> getSettings(@RequestParam Long userId) {
        TranslationSettings settings = translationService.getSettings(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", settings);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新翻译设置
     */
    @PutMapping("/settings")
    public ResponseEntity<Map<String, Object>> updateSettings(
            @RequestParam Long userId,
            @RequestBody TranslationSettings settings) {
        TranslationSettings updated = translationService.updateSettings(userId, settings);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", updated);
        return ResponseEntity.ok(result);
    }
}
