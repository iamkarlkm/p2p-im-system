package com.im.server.webhook;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Webhook配置
 */
@Configuration
@ConfigurationProperties(prefix = "im.webhook")
public class WebhookConfig {

    // 是否启用Webhook
    private boolean enabled = true;

    // 处理器线程数
    private int processorThreads = 4;

    // 最大重试次数
    private int maxRetries = 5;

    // 重试间隔（毫秒）
    private long retryIntervalMs = 1000;

    // 最大队列大小
    private int maxQueueSize = 10000;

    // 请求超时（毫秒）
    private int requestTimeoutMs = 10000;

    // 连接超时（毫秒）
    private int connectTimeoutMs = 5000;

    // 签名算法
    private String signatureAlgorithm = "HmacSHA256";

    // 时间戳有效期（秒）
    private int timestampValiditySeconds = 300;

    // 投递统计保留天数
    private int statsRetentionDays = 30;

    // ==================== Getters and Setters ====================

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public int getProcessorThreads() { return processorThreads; }
    public void setProcessorThreads(int processorThreads) { this.processorThreads = processorThreads; }

    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }

    public long getRetryIntervalMs() { return retryIntervalMs; }
    public void setRetryIntervalMs(long retryIntervalMs) { this.retryIntervalMs = retryIntervalMs; }

    public int getMaxQueueSize() { return maxQueueSize; }
    public void setMaxQueueSize(int maxQueueSize) { this.maxQueueSize = maxQueueSize; }

    public int getRequestTimeoutMs() { return requestTimeoutMs; }
    public void setRequestTimeoutMs(int requestTimeoutMs) { this.requestTimeoutMs = requestTimeoutMs; }

    public int getConnectTimeoutMs() { return connectTimeoutMs; }
    public void setConnectTimeoutMs(int connectTimeoutMs) { this.connectTimeoutMs = connectTimeoutMs; }

    public String getSignatureAlgorithm() { return signatureAlgorithm; }
    public void setSignatureAlgorithm(String signatureAlgorithm) { this.signatureAlgorithm = signatureAlgorithm; }

    public int getTimestampValiditySeconds() { return timestampValiditySeconds; }
    public void setTimestampValiditySeconds(int timestampValiditySeconds) { this.timestampValiditySeconds = timestampValiditySeconds; }

    public int getStatsRetentionDays() { return statsRetentionDays; }
    public void setStatsRetentionDays(int statsRetentionDays) { this.statsRetentionDays = statsRetentionDays; }
}
