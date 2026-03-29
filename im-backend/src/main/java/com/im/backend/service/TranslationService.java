package com.im.backend.service;

import com.im.backend.model.TranslationRequest;
import com.im.backend.model.TranslationResult;
import com.im.backend.model.LanguagePair;
import com.im.backend.model.TranslationCache;
import com.im.backend.repository.TranslationCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

/**
 * 实时翻译服务
 * 支持多语言实时翻译、缓存管理、批量翻译
 */
@Service
public class TranslationService {

    @Autowired
    private TranslationCacheRepository cacheRepository;

    @Autowired
    private LanguageDetector languageDetector;

    private final RestTemplate restTemplate = new RestTemplate();
    
    // 翻译API配置
    private static final String TRANSLATION_API_URL = "https://api.translation.service/v2/translate";
    private static final String API_KEY = System.getenv("TRANSLATION_API_KEY");
    
    // 支持的翻译引擎
    private final Map<String, TranslationEngine> engines = new ConcurrentHashMap<>();
    
    // 翻译统计
    private final TranslationStats stats = new TranslationStats();

    public TranslationService() {
        initializeEngines();
    }

    /**
     * 初始化翻译引擎
     */
    private void initializeEngines() {
        engines.put("google", new GoogleTranslationEngine());
        engines.put("deepl", new DeepLTranslationEngine());
        engines.put("azure", new AzureTranslationEngine());
        engines.put("local", new LocalTranslationEngine());
    }

    /**
     * 单条消息翻译（同步）
     */
    public TranslationResult translate(TranslationRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. 检查缓存
            Optional<TranslationCache> cached = findInCache(
                request.getText(), 
                request.getSourceLanguage(), 
                request.getTargetLanguage()
            );
            
            if (cached.isPresent()) {
                stats.incrementCacheHit();
                return buildResultFromCache(cached.get(), request);
            }

            // 2. 自动检测源语言
            String sourceLang = request.getSourceLanguage();
            if (sourceLang == null || sourceLang.equals("auto")) {
                sourceLang = languageDetector.detect(request.getText());
            }

            // 3. 如果源语言和目标语言相同，直接返回
            if (sourceLang.equals(request.getTargetLanguage())) {
                return buildNoTranslationResult(request.getText(), sourceLang);
            }

            // 4. 执行翻译
            TranslationResult result = executeTranslation(request, sourceLang);
            
            // 5. 保存到缓存
            saveToCache(request.getText(), result.getTranslatedText(), 
                       sourceLang, request.getTargetLanguage());
            
            stats.incrementSuccess();
            result.setProcessingTime(System.currentTimeMillis() - startTime);
            
            return result;
            
        } catch (Exception e) {
            stats.incrementError();
            return buildErrorResult(request, e.getMessage());
        }
    }

    /**
     * 异步翻译
     */
    public CompletableFuture<TranslationResult> translateAsync(TranslationRequest request) {
        return CompletableFuture.supplyAsync(() -> translate(request));
    }

    /**
     * 批量翻译
     */
    public List<TranslationResult> translateBatch(List<TranslationRequest> requests) {
        List<TranslationResult> results = new ArrayList<>();
        
        for (TranslationRequest request : requests) {
            results.add(translate(request));
        }
        
        return results;
    }

    /**
     * 流式翻译（用于长文本）
     */
    public void translateStream(TranslationRequest request, 
                               java.util.function.Consumer<TranslationResult> callback) {
        String text = request.getText();
        
        // 将长文本分割成句子
        List<String> sentences = splitIntoSentences(text);
        
        for (String sentence : sentences) {
            TranslationRequest sentenceRequest = new TranslationRequest();
            sentenceRequest.setText(sentence);
            sentenceRequest.setSourceLanguage(request.getSourceLanguage());
            sentenceRequest.setTargetLanguage(request.getTargetLanguage());
            sentenceRequest.setEngine(request.getEngine());
            
            TranslationResult result = translate(sentenceRequest);
            callback.accept(result);
        }
    }

    /**
     * 执行实际翻译
     */
    private TranslationResult executeTranslation(TranslationRequest request, String sourceLang) {
        String engineName = request.getEngine() != null ? request.getEngine() : "google";
        TranslationEngine engine = engines.getOrDefault(engineName, engines.get("google"));
        
        String translatedText = engine.translate(
            request.getText(), 
            sourceLang, 
            request.getTargetLanguage()
        );
        
        TranslationResult result = new TranslationResult();
        result.setId(UUID.randomUUID().toString());
        result.setOriginalText(request.getText());
        result.setTranslatedText(translatedText);
        result.setSourceLanguage(sourceLang);
        result.setTargetLanguage(request.getTargetLanguage());
        result.setEngineUsed(engineName);
        result.setSuccess(true);
        result.setTimestamp(LocalDateTime.now());
        
        // 计算置信度
        double confidence = calculateConfidence(request.getText(), translatedText);
        result.setConfidence(confidence);
        
        return result;
    }

    /**
     * 从缓存构建结果
     */
    private TranslationResult buildResultFromCache(TranslationCache cache, TranslationRequest request) {
        TranslationResult result = new TranslationResult();
        result.setId(UUID.randomUUID().toString());
        result.setOriginalText(request.getText());
        result.setTranslatedText(cache.getTranslatedText());
        result.setSourceLanguage(cache.getSourceLanguage());
        result.setTargetLanguage(cache.getTargetLanguage());
        result.setEngineUsed("cache");
        result.setSuccess(true);
        result.setFromCache(true);
        result.setTimestamp(LocalDateTime.now());
        result.setConfidence(1.0);
        result.setProcessingTime(0);
        return result;
    }

    /**
     * 构建无需翻译的结果
     */
    private TranslationResult buildNoTranslationResult(String text, String language) {
        TranslationResult result = new TranslationResult();
        result.setId(UUID.randomUUID().toString());
        result.setOriginalText(text);
        result.setTranslatedText(text);
        result.setSourceLanguage(language);
        result.setTargetLanguage(language);
        result.setEngineUsed("none");
        result.setSuccess(true);
        result.setTimestamp(LocalDateTime.now());
        result.setConfidence(1.0);
        return result;
    }

    /**
     * 构建错误结果
     */
    private TranslationResult buildErrorResult(TranslationRequest request, String errorMessage) {
        TranslationResult result = new TranslationResult();
        result.setId(UUID.randomUUID().toString());
        result.setOriginalText(request.getText());
        result.setTranslatedText("");
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        result.setTimestamp(LocalDateTime.now());
        return result;
    }

    /**
     * 在缓存中查找
     */
    private Optional<TranslationCache> findInCache(String text, String sourceLang, String targetLang) {
        // 生成缓存键
        String cacheKey = generateCacheKey(text, sourceLang, targetLang);
        return cacheRepository.findByCacheKey(cacheKey);
    }

    /**
     * 保存到缓存
     */
    private void saveToCache(String originalText, String translatedText, 
                            String sourceLang, String targetLang) {
        try {
            TranslationCache cache = new TranslationCache();
            cache.setId(UUID.randomUUID().toString());
            cache.setCacheKey(generateCacheKey(originalText, sourceLang, targetLang));
            cache.setOriginalText(originalText);
            cache.setTranslatedText(translatedText);
            cache.setSourceLanguage(sourceLang);
            cache.setTargetLanguage(targetLang);
            cache.setCreatedAt(LocalDateTime.now());
            cache.setExpiresAt(LocalDateTime.now().plusDays(30));
            
            cacheRepository.save(cache);
        } catch (Exception e) {
            // 缓存失败不影响主流程
            System.err.println("Failed to save translation cache: " + e.getMessage());
        }
    }

    /**
     * 生成缓存键
     */
    private String generateCacheKey(String text, String sourceLang, String targetLang) {
        String content = sourceLang + ":" + targetLang + ":" + text;
        return java.security.MessageDigest.getInstance("SHA-256")
            .digest(content.getBytes())
            .toString();
    }

    /**
     * 分割长文本为句子
     */
    private List<String> splitIntoSentences(String text) {
        List<String> sentences = new ArrayList<>();
        // 按句子分隔符分割
        String[] parts = text.split("(?<=[.!?。！？])\\s+");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                sentences.add(trimmed);
            }
        }
        return sentences;
    }

    /**
     * 计算翻译置信度
     */
    private double calculateConfidence(String original, String translated) {
        // 基于长度比例和字符类型计算简单置信度
        if (original == null || translated == null || original.isEmpty()) {
            return 0.0;
        }
        
        double lengthRatio = (double) translated.length() / original.length();
        
        // 理想情况下，翻译后的长度应该在原长度的0.5-2倍之间
        if (lengthRatio >= 0.5 && lengthRatio <= 2.0) {
            return 0.8 + (1.0 - Math.abs(1.0 - lengthRatio)) * 0.2;
        } else {
            return 0.5;
        }
    }

    /**
     * 获取支持的翻译引擎列表
     */
    public List<String> getSupportedEngines() {
        return new ArrayList<>(engines.keySet());
    }

    /**
     * 获取翻译统计信息
     */
    public TranslationStats getStats() {
        return stats;
    }

    /**
     * 清除过期缓存
     */
    public void clearExpiredCache() {
        cacheRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    /**
     * 翻译引擎接口
     */
    public interface TranslationEngine {
        String translate(String text, String sourceLang, String targetLang);
    }

    /**
     * Google翻译引擎
     */
    public class GoogleTranslationEngine implements TranslationEngine {
        @Override
        public String translate(String text, String sourceLang, String targetLang) {
            // 实际实现会调用Google Translation API
            // 这里使用模拟实现
            return "[Google] " + text;
        }
    }

    /**
     * DeepL翻译引擎
     */
    public class DeepLTranslationEngine implements TranslationEngine {
        @Override
        public String translate(String text, String sourceLang, String targetLang) {
            return "[DeepL] " + text;
        }
    }

    /**
     * Azure翻译引擎
     */
    public class AzureTranslationEngine implements TranslationEngine {
        @Override
        public String translate(String text, String sourceLang, String targetLang) {
            return "[Azure] " + text;
        }
    }

    /**
     * 本地翻译引擎（离线）
     */
    public class LocalTranslationEngine implements TranslationEngine {
        @Override
        public String translate(String text, String sourceLang, String targetLang) {
            return "[Local] " + text;
        }
    }

    /**
     * 翻译统计类
     */
    public static class TranslationStats {
        private long totalRequests = 0;
        private long cacheHits = 0;
        private long successCount = 0;
        private long errorCount = 0;

        public synchronized void incrementTotal() { totalRequests++; }
        public synchronized void incrementCacheHit() { cacheHits++; }
        public synchronized void incrementSuccess() { successCount++; }
        public synchronized void incrementError() { errorCount++; }

        public long getTotalRequests() { return totalRequests; }
        public long getCacheHits() { return cacheHits; }
        public long getSuccessCount() { return successCount; }
        public long getErrorCount() { return errorCount; }
        public double getCacheHitRate() { 
            return totalRequests > 0 ? (double) cacheHits / totalRequests : 0; 
        }
    }
}
