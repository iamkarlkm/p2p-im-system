package com.im.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * 翻译服务配置属性
 */
@Configuration
@ConfigurationProperties(prefix = "translation")
public class TranslationConfig {

    // 默认翻译引擎
    private String defaultEngine = "google";
    
    // 缓存配置
    private CacheConfig cache = new CacheConfig();
    
    // API密钥配置
    private Map<String, String> apiKeys;
    
    // 支持的语言对
    private List<LanguagePairConfig> supportedLanguagePairs;
    
    // 速率限制
    private RateLimitConfig rateLimit = new RateLimitConfig();
    
    // 离线翻译配置
    private OfflineConfig offline = new OfflineConfig();

    // Getters and Setters
    public String getDefaultEngine() { return defaultEngine; }
    public void setDefaultEngine(String engine) { this.defaultEngine = engine; }

    public CacheConfig getCache() { return cache; }
    public void setCache(CacheConfig cache) { this.cache = cache; }

    public Map<String, String> getApiKeys() { return apiKeys; }
    public void setApiKeys(Map<String, String> apiKeys) { this.apiKeys = apiKeys; }

    public List<LanguagePairConfig> getSupportedLanguagePairs() { return supportedLanguagePairs; }
    public void setSupportedLanguagePairs(List<LanguagePairConfig> pairs) { this.supportedLanguagePairs = pairs; }

    public RateLimitConfig getRateLimit() { return rateLimit; }
    public void setRateLimit(RateLimitConfig rateLimit) { this.rateLimit = rateLimit; }

    public OfflineConfig getOffline() { return offline; }
    public void setOffline(OfflineConfig offline) { this.offline = offline; }

    /**
     * 缓存配置
     */
    public static class CacheConfig {
        private boolean enabled = true;
        private int ttlDays = 30;
        private int maxSize = 10000;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public int getTtlDays() { return ttlDays; }
        public void setTtlDays(int days) { this.ttlDays = days; }
        public int getMaxSize() { return maxSize; }
        public void setMaxSize(int size) { this.maxSize = size; }
    }

    /**
     * 速率限制配置
     */
    public static class RateLimitConfig {
        private int requestsPerMinute = 100;
        private int requestsPerHour = 1000;
        private int maxTextLength = 5000;

        public int getRequestsPerMinute() { return requestsPerMinute; }
        public void setRequestsPerMinute(int limit) { this.requestsPerMinute = limit; }
        public int getRequestsPerHour() { return requestsPerHour; }
        public void setRequestsPerHour(int limit) { this.requestsPerHour = limit; }
        public int getMaxTextLength() { return maxTextLength; }
        public void setMaxTextLength(int length) { this.maxTextLength = length; }
    }

    /**
     * 离线翻译配置
     */
    public static class OfflineConfig {
        private boolean enabled = false;
        private String modelPath = "/models/translation";
        private List<String> supportedLanguages;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getModelPath() { return modelPath; }
        public void setModelPath(String path) { this.modelPath = path; }
        public List<String> getSupportedLanguages() { return supportedLanguages; }
        public void setSupportedLanguages(List<String> languages) { this.supportedLanguages = languages; }
    }

    /**
     * 语言对配置
     */
    public static class LanguagePairConfig {
        private String source;
        private String target;
        private double accuracy;

        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getTarget() { return target; }
        public void setTarget(String target) { this.target = target; }
        public double getAccuracy() { return accuracy; }
        public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
    }
}
