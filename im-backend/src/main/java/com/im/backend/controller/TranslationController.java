package com.im.backend.controller;

import com.im.backend.model.TranslationRequest;
import com.im.backend.model.TranslationResult;
import com.im.backend.model.LanguagePair;
import com.im.backend.service.TranslationService;
import com.im.backend.service.LanguageDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 翻译服务控制器
 * 提供REST API接口
 */
@RestController
@RequestMapping("/api/v1/translation")
@CrossOrigin(origins = "*")
public class TranslationController {

    @Autowired
    private TranslationService translationService;

    @Autowired
    private LanguageDetector languageDetector;

    /**
     * 单条文本翻译
     */
    @PostMapping("/translate")
    public ResponseEntity<TranslationResult> translate(@RequestBody TranslationRequest request) {
        TranslationResult result = translationService.translate(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 异步翻译
     */
    @PostMapping("/translate/async")
    public CompletableFuture<ResponseEntity<TranslationResult>> translateAsync(
            @RequestBody TranslationRequest request) {
        return translationService.translateAsync(request)
                .thenApply(ResponseEntity::ok);
    }

    /**
     * 批量翻译
     */
    @PostMapping("/translate/batch")
    public ResponseEntity<List<TranslationResult>> translateBatch(
            @RequestBody List<TranslationRequest> requests) {
        List<TranslationResult> results = translationService.translateBatch(requests);
        return ResponseEntity.ok(results);
    }

    /**
     * 检测语言
     */
    @PostMapping("/detect")
    public ResponseEntity<Map<String, Object>> detectLanguage(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        String detectedLang = languageDetector.detect(text);
        LanguageDetector.LanguageDetectionResult confidence = 
            languageDetector.detectWithConfidence(text);

        Map<String, Object> response = new HashMap<>();
        response.put("language", detectedLang);
        response.put("confidence", confidence.getConfidence());
        response.put("text", text);

        return ResponseEntity.ok(response);
    }

    /**
     * 检测语言并返回概率分布
     */
    @PostMapping("/detect/probabilities")
    public ResponseEntity<Map<String, Object>> detectLanguageProbabilities(
            @RequestBody Map<String, String> request) {
        String text = request.get("text");
        List<LanguageDetector.LanguageProbability> probabilities = 
            languageDetector.detectProbabilities(text);

        Map<String, Object> response = new HashMap<>();
        response.put("text", text);
        response.put("probabilities", probabilities);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取支持的翻译引擎列表
     */
    @GetMapping("/engines")
    public ResponseEntity<Map<String, Object>> getSupportedEngines() {
        List<String> engines = translationService.getSupportedEngines();
        
        Map<String, Object> response = new HashMap<>();
        response.put("engines", engines);
        response.put("count", engines.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取翻译统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getTranslationStats() {
        TranslationService.TranslationStats stats = translationService.getStats();
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalRequests", stats.getTotalRequests());
        response.put("cacheHits", stats.getCacheHits());
        response.put("successCount", stats.getSuccessCount());
        response.put("errorCount", stats.getErrorCount());
        response.put("cacheHitRate", stats.getCacheHitRate());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 清除过期缓存
     */
    @PostMapping("/cache/clear")
    public ResponseEntity<Map<String, Object>> clearExpiredCache() {
        translationService.clearExpiredCache();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Expired cache cleared successfully");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "translation");
        response.put("timestamp", new Date());
        
        return ResponseEntity.ok(response);
    }
}
