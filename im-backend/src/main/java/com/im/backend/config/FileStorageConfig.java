package com.im.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "file.storage")
public class FileStorageConfig {

    private String path = "./uploads";
    private String tempPath = "./temp";
    private String baseUrl = "http://localhost:8080/files";
    private long maxSize = 1073741824L; // 1GB
    private int chunkSize = 5242880; // 5MB
    private int maxConcurrentUploads = 10;
    private int cleanupIntervalHours = 24;
    private boolean autoCleanup = true;

    // 存储提供商: local, s3, oss
    private String provider = "local";

    // S3配置
    private String s3AccessKey;
    private String s3SecretKey;
    private String s3Bucket;
    private String s3Region;
    private String s3Endpoint;

    // OSS配置
    private String ossAccessKey;
    private String ossSecretKey;
    private String ossBucket;
    private String ossEndpoint;

    // Getters and Setters
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getTempPath() { return tempPath; }
    public void setTempPath(String tempPath) { this.tempPath = tempPath; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public long getMaxSize() { return maxSize; }
    public void setMaxSize(long maxSize) { this.maxSize = maxSize; }

    public int getChunkSize() { return chunkSize; }
    public void setChunkSize(int chunkSize) { this.chunkSize = chunkSize; }

    public int getMaxConcurrentUploads() { return maxConcurrentUploads; }
    public void setMaxConcurrentUploads(int maxConcurrentUploads) { this.maxConcurrentUploads = maxConcurrentUploads; }

    public int getCleanupIntervalHours() { return cleanupIntervalHours; }
    public void setCleanupIntervalHours(int cleanupIntervalHours) { this.cleanupIntervalHours = cleanupIntervalHours; }

    public boolean isAutoCleanup() { return autoCleanup; }
    public void setAutoCleanup(boolean autoCleanup) { this.autoCleanup = autoCleanup; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getS3AccessKey() { return s3AccessKey; }
    public void setS3AccessKey(String s3AccessKey) { this.s3AccessKey = s3AccessKey; }

    public String getS3SecretKey() { return s3SecretKey; }
    public void setS3SecretKey(String s3SecretKey) { this.s3SecretKey = s3SecretKey; }

    public String getS3Bucket() { return s3Bucket; }
    public void setS3Bucket(String s3Bucket) { this.s3Bucket = s3Bucket; }

    public String getS3Region() { return s3Region; }
    public void setS3Region(String s3Region) { this.s3Region = s3Region; }

    public String getS3Endpoint() { return s3Endpoint; }
    public void setS3Endpoint(String s3Endpoint) { this.s3Endpoint = s3Endpoint; }

    public String getOssAccessKey() { return ossAccessKey; }
    public void setOssAccessKey(String ossAccessKey) { this.ossAccessKey = ossAccessKey; }

    public String getOssSecretKey() { return ossSecretKey; }
    public void setOssSecretKey(String ossSecretKey) { this.ossSecretKey = ossSecretKey; }

    public String getOssBucket() { return ossBucket; }
    public void setOssBucket(String ossBucket) { this.ossBucket = ossBucket; }

    public String getOssEndpoint() { return ossEndpoint; }
    public void setOssEndpoint(String ossEndpoint) { this.ossEndpoint = ossEndpoint; }

    @Override
    public String toString() {
        return "FileStorageConfig{path='" + path + "', provider='" + provider + "', maxSize=" + maxSize + "}'";
    }
}
