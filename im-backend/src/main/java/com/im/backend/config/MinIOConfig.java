package com.im.backend.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 文件存储配置
 * CDN/MinIO 文件服务 - 核心配置
 */
@Configuration
public class MinIOConfig {

    @Value("${minio.endpoint:http://localhost:9000}")
    private String endpoint;

    @Value("${minio.access-key:minioadmin}")
    private String accessKey;

    @Value("${minio.secret-key:minioadmin}")
    private String secretKey;

    @Value("${minio.bucket-name:im-files}")
    private String bucketName;

    @Value("${minio.secure:false}")
    private boolean secure;

    @Value("${minio.presigned-url-expiry:3600}")
    private int presignedUrlExpiry;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getBucketName() {
        return bucketName;
    }

    public boolean isSecure() {
        return secure;
    }

    public int getPresignedUrlExpiry() {
        return presignedUrlExpiry;
    }
}
