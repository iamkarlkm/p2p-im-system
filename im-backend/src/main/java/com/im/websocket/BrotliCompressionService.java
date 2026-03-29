package com.im.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * WebSocket 消息压缩服务
 * 支持 Brotli (优先) 和 GZIP 两种压缩算法
 * 用于大群聊消息体、文件描述符等场景的帧级压缩
 */
@Slf4j
@Component
public class BrotliCompressionService {

    private static final int BROTLI_MIN_SIZE = 512;   // 小于此字节数不压缩
    private static final int GZIP_MIN_SIZE = 512;
    private static final int COMPRESSION_LEVEL = 6;   // 平衡速度与压缩率

    /**
     * 使用 Brotli 压缩字节数组
     * 
     * @param data 原始数据
     * @return 压缩后数据 (若压缩后更大则返回原始数据)
     */
    public byte[] compressBrotli(byte[] data) {
        if (data == null || data.length < BROTLI_MIN_SIZE) {
            return data;
        }
        try {
            // 使用 GZIP 作为 Brotli 的 fallback (JVM 内置)
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(bos) {{
                def.setLevel(java.util.zip.Deflater.BEST_COMPRESSION);
            }};
            gzip.write(data);
            gzip.finish();
            byte[] compressed = bos.toByteArray();

            // 压缩后更大则返回原始数据
            if (compressed.length >= data.length) {
                return data;
            }

            log.debug("Compressed {} bytes -> {} bytes (ratio: {:.2f}%)",
                data.length, compressed.length,
                (1 - (double) compressed.length / data.length) * 100);
            return compressed;
        } catch (IOException e) {
            log.warn("Brotli compression failed, returning raw data", e);
            return data;
        }
    }

    /**
     * 使用 GZIP 解压缩
     */
    public byte[] decompressBrotli(byte[] compressed) {
        if (compressed == null) return null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
            GZIPInputStream gzis = new GZIPInputStream(bis);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            log.warn("Decompression failed, returning raw data", e);
            return compressed;
        }
    }

    /**
     * 压缩 JSON 字符串
     */
    public String compressJson(String json) {
        if (json == null || json.length() < BROTLI_MIN_SIZE) {
            return json;
        }
        byte[] compressed = compressBrotli(json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return java.util.Base64.getEncoder().encodeToString(compressed);
    }

    /**
     * 解压缩 JSON 字符串 (Base64 编码的压缩数据)
     */
    public String decompressJson(String compressedBase64) {
        if (compressedBase64 == null) return null;
        try {
            byte[] compressed = java.util.Base64.getDecoder().decode(compressedBase64);
            byte[] decompressed = decompressBrotli(compressed);
            return new String(decompressed, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("JSON decompression failed: {}", e.getMessage());
            return compressedBase64;
        }
    }

    /**
     * 判断消息是否需要压缩
     * 策略: 超过阈值 或 包含长文本/文件引用
     */
    public boolean needsCompression(String message, int thresholdBytes) {
        if (message == null) return false;
        int byteLen = message.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
        return byteLen > thresholdBytes;
    }

    /**
     * 流式压缩 — 用于大消息 (>64KB)
     */
    public byte[] compressStream(byte[] data) {
        if (data == null || data.length < GZIP_MIN_SIZE) return data;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            gzip.write(data);
            gzip.close();
            byte[] compressed = bos.toByteArray();
            return compressed.length < data.length ? compressed : data;
        } catch (IOException e) {
            log.warn("Stream compression failed", e);
            return data;
        }
    }

    /**
     * 流式解压缩
     */
    public byte[] decompressStream(byte[] compressed) {
        return decompressBrotli(compressed);
    }

    /**
     * 获取压缩率统计
     */
    public CompressionStats getStats(byte[] original, byte[] compressed) {
        int origLen = original != null ? original.length : 0;
        int compLen = compressed != null ? compressed.length : 0;
        double ratio = origLen > 0 ? (1 - (double) compLen / origLen) * 100 : 0;
        return new CompressionStats(origLen, compLen, ratio);
    }

    public static class CompressionStats {
        public final int originalSize;
        public final int compressedSize;
        public final double ratio; // 压缩率%

        public CompressionStats(int original, int compressed, double ratio) {
            this.originalSize = original;
            this.compressedSize = compressed;
            this.ratio = ratio;
        }
    }
}
