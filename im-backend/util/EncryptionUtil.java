package com.im.backend.util;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 加密工具类
 * 提供端到端加密、密钥交换、消息签名等功能
 */
public class EncryptionUtil {

    private static final String ALGORITHM_AES = "AES";
    private static final String TRANSFORMATION_AES_GCM = "AES/GCM/NoPadding";
    private static final String ALGORITHM_ECDH = "EC";
    private static final String CURVE_NAME = "secp256r1";
    private static final String ALGORITHM_HMAC = "HmacSHA256";
    private static final String ALGORITHM_RSA = "RSA";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int AES_KEY_SIZE = 256;

    /**
     * 生成EC密钥对（用于ECDH密钥交换）
     */
    public static KeyPair generateECKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_ECDH);
        keyPairGenerator.initialize(new ECGenParameterSpec(CURVE_NAME));
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 生成RSA密钥对（用于消息签名）
     */
    public static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_RSA);
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 执行ECDH密钥交换，生成共享密钥
     */
    public static byte[] performECDHKeyExchange(PrivateKey privateKey, PublicKey publicKey) throws Exception {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(publicKey, true);
        return keyAgreement.generateSecret();
    }

    /**
     * 使用AES-GCM加密消息
     */
    public static EncryptedData encryptAESGCM(byte[] plaintext, byte[] key) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION_AES_GCM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM_AES);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec);

        byte[] ciphertext = cipher.doFinal(plaintext);
        return new EncryptedData(ciphertext, iv);
    }

    /**
     * 使用AES-GCM解密消息
     */
    public static byte[] decryptAESGCM(byte[] ciphertext, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION_AES_GCM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM_AES);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, parameterSpec);
        return cipher.doFinal(ciphertext);
    }

    /**
     * 使用HMAC-SHA256计算消息认证码
     */
    public static byte[] calculateHMAC(byte[] data, byte[] key) throws Exception {
        Mac mac = Mac.getInstance(ALGORITHM_HMAC);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM_HMAC);
        mac.init(secretKeySpec);
        return mac.doFinal(data);
    }

    /**
     * 使用RSA私钥签名消息
     */
    public static byte[] signMessage(byte[] message, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message);
        return signature.sign();
    }

    /**
     * 使用RSA公钥验证签名
     */
    public static boolean verifySignature(byte[] message, byte[] signatureBytes, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(message);
        return signature.verify(signatureBytes);
    }

    /**
     * 从字节数组加载EC公钥
     */
    public static PublicKey loadECPublicKey(byte[] keyBytes) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_ECDH);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return keyFactory.generatePublic(spec);
    }

    /**
     * 从字节数组加载EC私钥
     */
    public static PrivateKey loadECPrivateKey(byte[] keyBytes) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_ECDH);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return keyFactory.generatePrivate(spec);
    }

    /**
     * 从字节数组加载RSA公钥
     */
    public static PublicKey loadRSAPublicKey(byte[] keyBytes) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return keyFactory.generatePublic(spec);
    }

    /**
     * 从字节数组加载RSA私钥
     */
    public static PrivateKey loadRSAPrivateKey(byte[] keyBytes) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return keyFactory.generatePrivate(spec);
    }

    /**
     * 生成安全的随机密钥
     */
    public static byte[] generateRandomKey(int length) {
        byte[] key = new byte[length];
        SecureRandom random = new SecureRandom();
        random.nextBytes(key);
        return key;
    }

    /**
     * 使用SHA-256计算哈希
     */
    public static byte[] sha256(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(data);
    }

    /**
     * Base64编码
     */
    public static String base64Encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Base64解码
     */
    public static byte[] base64Decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    /**
     * 加密数据封装类
     */
    public static class EncryptedData {
        private final byte[] ciphertext;
        private final byte[] iv;

        public EncryptedData(byte[] ciphertext, byte[] iv) {
            this.ciphertext = ciphertext;
            this.iv = iv;
        }

        public byte[] getCiphertext() {
            return ciphertext;
        }

        public byte[] getIv() {
            return iv;
        }
    }
}
