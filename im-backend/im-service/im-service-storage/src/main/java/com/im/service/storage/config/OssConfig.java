package com.im.service.storage.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 存储服务配置类
 * 支持阿里云OSS和MinIO两种存储方式
 *
 * @author IM Team
 * @version 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "storage")
public class OssConfig {

    /**
     * 存储类型: local, oss, minio, s3
     */
    private String type = "local";

    /**
     * 本地存储配置
     */
    private LocalConfig local = new LocalConfig();

    /**
     * 阿里云OSS配置
     */
    private OssProperties oss = new OssProperties();

    /**
     * MinIO配置
     */
    private MinioConfig minio = new MinioConfig();

    /**
     * AWS S3配置
     */
    private S3Config s3 = new S3Config();

    /**
     * 通用配置
     */
    private Map<String, String> common = new HashMap<>();

    /**
     * 本地存储配置
     */
    @Data
    public static class LocalConfig {
        /**
         * 存储根目录
         */
        private String basePath = "/data/im-files";

        /**
         * 访问基础URL
         */
        private String baseUrl = "http://localhost:8080/files";
    }

    /**
     * 阿里云OSS配置
     */
    @Data
    public static class OssProperties {
        /**
         * 是否启用
         */
        private boolean enabled = false;

        /**
         * 地域节点
         */
        private String endpoint = "oss-cn-hangzhou.aliyuncs.com";

        /**
         * AccessKeyId
         */
        private String accessKeyId;

        /**
         * AccessKeySecret
         */
        private String accessKeySecret;

        /**
         * 存储桶名称
         */
        private String bucketName;

        /**
         * 访问域名
         */
        private String publicUrl;

        /**
         * 私有/公有访问
         */
        private boolean publicAccess = false;

        /**
         * 缩略图配置
         */
        private ThumbnailConfig thumbnail = new ThumbnailConfig();
    }

    /**
     * MinIO配置
     */
    @Data
    public static class MinioConfig {
        /**
         * 是否启用
         */
        private boolean enabled = false;

        /**
         * 服务地址
         */
        private String endpoint = "localhost:9000";

        /**
         * AccessKey
         */
        private String accessKey;

        /**
         * SecretKey
         */
        private String secretKey;

        /**
         * 存储桶名称
         */
        private String bucketName;

        /**
         * 访问域名
         */
        private String publicUrl;

        /**
         * 是否使用HTTPS
         */
        private boolean secure = false;

        /**
         * 缩略图配置
         */
        private ThumbnailConfig thumbnail = new ThumbnailConfig();
    }

    /**
     * AWS S3配置
     */
    @Data
    public static class S3Config {
        /**
         * 是否启用
         */
        private boolean enabled = false;

        /**
         * 区域
         */
        private String region = "us-east-1";

        /**
         * AccessKey
         */
        private String accessKey;

        /**
         * SecretKey
         */
        private String secretKey;

        /**
         * 存储桶名称
         */
        private String bucketName;

        /**
         * 访问域名
         */
        private String publicUrl;

        /**
         * 缩略图配置
         */
        private ThumbnailConfig thumbnail = new ThumbnailConfig();
    }

    /**
     * 缩略图配置
     */
    @Data
    public static class ThumbnailConfig {
        /**
         * 是否启用
         */
        private boolean enabled = true;

        /**
         * 最大宽度
         */
        private int maxWidth = 800;

        /**
         * 最大高度
         */
        private int maxHeight = 800;

        /**
         * 缩略图质量 (1-100)
         */
        private int quality = 80;

        /**
         * 缩略图后缀
         */
        private String suffix = "_thumb";
    }

    /**
     * 创建OSS客户端
     */
    @Bean
    public OSS ossClient() {
        if (!oss.isEnabled()) {
            return null;
        }
        return new OSSClientBuilder().build(oss.getEndpoint(), oss.getAccessKeyId(), oss.getAccessKeySecret());
    }

    /**
     * 创建MinIO客户端
     */
    @Bean
    public MinioClient minioClient() {
        if (!minio.isEnabled()) {
            return null;
        }
        return MinioClient.builder()
                .endpoint(minio.getEndpoint())
                .credentials(minio.getAccessKey(), minio.getSecretKey())
                .build();
    }

    /**
     * 获取当前使用的存储类型
     */
    public String getActiveStorageType() {
        if ("oss".equalsIgnoreCase(type) && oss.isEnabled()) {
            return "OSS";
        } else if ("minio".equalsIgnoreCase(type) && minio.isEnabled()) {
            return "MINIO";
        } else if ("s3".equalsIgnoreCase(type) && s3.isEnabled()) {
            return "S3";
        }
        return "LOCAL";
    }
}
