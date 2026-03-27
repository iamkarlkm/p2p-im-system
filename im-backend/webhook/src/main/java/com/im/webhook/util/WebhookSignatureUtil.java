package com.im.webhook.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

/**
 * Webhook签名工具类
 * 提供请求签名生成和验证功能
 */
@Slf4j
public class WebhookSignatureUtil {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    
    /**
     * 生成请求签名
     * 
     * @param payload 请求体数据
     * @param secret 密钥
     * @return Base64编码的签名
     */
    public static String sign(Map<String, Object> payload, String secret) {
        try {
            // 将payload按键排序后转为JSON字符串
            TreeMap<String, Object> sortedPayload = new TreeMap<>(payload);
            String payloadString = objectMapper.writeValueAsString(sortedPayload);
            
            return sign(payloadString, secret);
        } catch (Exception e) {
            log.error("生成Webhook签名失败", e);
            throw new RuntimeException("Failed to generate signature", e);
        }
    }
    
    /**
     * 生成字符串签名
     * 
     * @param data 待签名数据
     * @param secret 密钥
     * @return Base64编码的签名
     */
    public static String sign(String data, String secret) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8), 
                    HMAC_ALGORITHM
            );
            mac.init(secretKeySpec);
            
            byte[] signatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            log.error("生成Webhook签名失败", e);
            throw new RuntimeException("Failed to generate signature", e);
        }
    }
    
    /**
     * 生成带时间戳的签名
     * 格式: Base64(HMAC_SHA256(timestamp + "." + json_payload))
     * 
     * @param payload 请求体数据
     * @param secret 密钥
     * @param timestamp 时间戳（秒）
     * @return 签名
     */
    public static String signWithTimestamp(Map<String, Object> payload, String secret, long timestamp) {
        try {
            TreeMap<String, Object> sortedPayload = new TreeMap<>(payload);
            String payloadString = objectMapper.writeValueAsString(sortedPayload);
            
            String data = timestamp + "." + payloadString;
            return sign(data, secret);
        } catch (Exception e) {
            log.error("生成Webhook签名失败", e);
            throw new RuntimeException("Failed to generate signature", e);
        }
    }
    
    /**
     * 验证签名
     * 
     * @param payload 请求体数据
     * @param secret 密钥
     * @param signature 待验证的签名
     * @return 验证结果
     */
    public static boolean verify(Map<String, Object> payload, String secret, String signature) {
        try {
            String expectedSignature = sign(payload, secret);
            return constantTimeEquals(expectedSignature, signature);
        } catch (Exception e) {
            log.error("验证Webhook签名失败", e);
            return false;
        }
    }
    
    /**
     * 验证带时间戳的签名
     * 
     * @param payload 请求体数据
     * @param secret 密钥
     * @param signature 签名
     * @param timestamp 时间戳
     * @return 验证结果
     */
    public static boolean verifyWithTimestamp(Map<String, Object> payload, String secret, 
                                               String signature, long timestamp) {
        try {
            String expectedSignature = signWithTimestamp(payload, secret, timestamp);
            return constantTimeEquals(expectedSignature, signature);
        } catch (Exception e) {
            log.error("验证Webhook签名失败", e);
            return false;
        }
    }
    
    /**
     * 验证签名（带时间窗口）
     * 防止重放攻击
     * 
     * @param payload 请求体数据
     * @param secret 密钥
     * @param signature 签名
     * @param timestamp 时间戳
     * @param toleranceSeconds 允许的时间差（秒）
     * @return 验证结果
     */
    public static boolean verifyWithTolerance(Map<String, Object> payload, String secret,
                                               String signature, long timestamp, int toleranceSeconds) {
        // 检查时间戳
        long now = System.currentTimeMillis() / 1000;
        if (Math.abs(now - timestamp) > toleranceSeconds) {
            log.warn("Webhook时间戳超出允许范围: timestamp={}, now={}, diff={}", 
                    timestamp, now, Math.abs(now - timestamp));
            return false;
        }
        
        return verifyWithTimestamp(payload, secret, signature, timestamp);
    }
    
    /**
     * 常量时间字符串比较
     * 防止时序攻击
     */
    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        
        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
        
        if (aBytes.length != bBytes.length) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < aBytes.length; i++) {
            result |= aBytes[i] ^ bBytes[i];
        }
        
        return result == 0;
    }
    
    /**
     * 生成签名头值
     * 格式: t=<timestamp>,v1=<signature>
     * 
     * @param payload 请求体
     * @param secret 密钥
     * @return 签名头值
     */
    public static String generateSignatureHeader(Map<String, Object> payload, String secret) {
        long timestamp = System.currentTimeMillis() / 1000;
        String signature = signWithTimestamp(payload, secret, timestamp);
        return String.format("t=%d,v1=%s", timestamp, signature);
    }
    
    /**
     * 解析签名头
     * 
     * @param signatureHeader 签名头值
     * @return [timestamp, signature]
     */
    public static long[] parseSignatureHeader(String signatureHeader) {
        if (signatureHeader == null || signatureHeader.isEmpty()) {
            return null;
        }
        
        try {
            long timestamp = 0;
            String signature = null;
            
            String[] parts = signatureHeader.split(",");
            for (String part : parts) {
                String[] kv = part.trim().split("=");
                if (kv.length == 2) {
                    if (kv[0].equals("t")) {
                        timestamp = Long.parseLong(kv[1]);
                    } else if (kv[0].equals("v1")) {
                        signature = kv[1];
                    }
                }
            }
            
            if (signature != null) {
                return new long[]{timestamp, Long.parseLong(signature)};
            }
        } catch (Exception e) {
            log.error("解析签名头失败: {}", signatureHeader, e);
        }
        
        return null;
    }
}
