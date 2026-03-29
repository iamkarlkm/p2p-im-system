package com.im.server.webhook;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Webhook签名验证器
 */
public class WebhookSignatureVerifier {

    /**
     * 生成签名
     */
    public static String sign(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return "sha256=" + bytesToHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("签名生成失败", e);
        }
    }

    /**
     * 验证签名
     */
    public static boolean verify(String payload, String secret, String signature) {
        String expected = sign(payload, secret);
        return constantTimeEquals(expected, signature);
        }

    /**
     * 生成带时间戳的签名
     */
    public static String signWithTimestamp(String payload, String secret, long timestamp) {
        String signedPayload = timestamp + "." + payload;
        return sign(signedPayload, secret);
    }

    /**
     * 验证带时间戳的签名（防止重放攻击）
     */
    public static boolean verifyWithTimestamp(String payload, String secret, String signature,
                                              long timestamp, int validitySeconds) {
        long now = System.currentTimeMillis() / 1000;
        if (Math.abs(now - timestamp) > validitySeconds) {
            return false;
        }
        String signedPayload = timestamp + "." + payload;
        String expected = sign(signedPayload, secret);
        return constantTimeEquals(expected, signature);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
