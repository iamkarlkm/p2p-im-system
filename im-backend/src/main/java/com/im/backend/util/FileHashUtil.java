package com.im.backend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

@Component
public class FileHashUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileHashUtil.class);

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int BUFFER_SIZE = 8192;

    /**
     * 计算字节数组的哈希值
     */
    public String calculateHash(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(data);
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            logger.error("哈希算法不可用: {}", HASH_ALGORITHM, e);
            throw new RuntimeException("哈希计算失败", e);
        }
    }

    /**
     * 计算文件的哈希值
     */
    public String calculateFileHash(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }

            return bytesToHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            logger.error("哈希算法不可用: {}", HASH_ALGORITHM, e);
            throw new RuntimeException("文件哈希计算失败", e);
        }
    }

    /**
     * 计算文件的MD5哈希
     */
    public String calculateMD5(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }

            return bytesToHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            logger.error("MD5算法不可用", e);
            throw new RuntimeException("MD5计算失败", e);
        }
    }

    /**
     * 计算字节数组的MD5
     */
    public String calculateMD5(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            return bytesToHex(digest.digest(data));
        } catch (NoSuchAlgorithmException e) {
            logger.error("MD5算法不可用", e);
            throw new RuntimeException("MD5计算失败", e);
        }
    }

    /**
     * 计算分片哈希（用于验证分片完整性）
     */
    public String calculateChunkHash(byte[] chunkData, int chunkIndex, String uploadId) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(chunkData);
            digest.update(String.valueOf(chunkIndex).getBytes());
            digest.update(uploadId.getBytes());
            return bytesToHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            logger.error("哈希算法不可用", e);
            throw new RuntimeException("分片哈希计算失败", e);
        }
    }

    /**
     * 验证文件哈希
     */
    public boolean verifyFileHash(File file, String expectedHash) throws IOException {
        String actualHash = calculateFileHash(file);
        boolean matches = actualHash.equalsIgnoreCase(expectedHash);
        if (!matches) {
            logger.warn("文件哈希不匹配: expected={}, actual={}", expectedHash, actualHash);
        }
        return matches;
    }

    /**
     * 验证字节数组哈希
     */
    public boolean verifyHash(byte[] data, String expectedHash) {
        String actualHash = calculateHash(data);
        boolean matches = actualHash.equalsIgnoreCase(expectedHash);
        if (!matches) {
            logger.warn("哈希不匹配: expected={}, actual={}", expectedHash, actualHash);
        }
        return matches;
    }

    /**
     * 字节数组转十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    /**
     * 生成文件的唯一标识符
     */
    public String generateFileId(String fileName, long fileSize, String fileHash) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(fileName.getBytes());
            digest.update(String.valueOf(fileSize).getBytes());
            digest.update(fileHash.getBytes());
            return bytesToHex(digest.digest()).substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            logger.error("哈希算法不可用", e);
            throw new RuntimeException("文件ID生成失败", e);
        }
    }

    /**
     * 快速估算大文件的哈希（采样）
     */
    public String estimateFileHash(File file) throws IOException {
        long fileSize = file.length();
        if (fileSize <= 10 * 1024 * 1024) { // 小文件直接计算
            return calculateFileHash(file);
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] buffer = new byte[BUFFER_SIZE];

            // 采样策略：文件头、中间、尾部各取一部分
            long[] positions = {
                0,
                fileSize / 2,
                fileSize - BUFFER_SIZE
            };

            for (long pos : positions) {
                fis.getChannel().position(Math.max(0, pos));
                int bytesRead = fis.read(buffer);
                if (bytesRead > 0) {
                    digest.update(buffer, 0, bytesRead);
                }
            }

            // 添加文件大小信息
            digest.update(String.valueOf(fileSize).getBytes());

            return bytesToHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            logger.error("哈希算法不可用", e);
            throw new RuntimeException("文件哈希估算失败", e);
        }
    }
}
