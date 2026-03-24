package com.im.controller;

import com.im.websocket.BrotliCompressionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket 消息压缩管理 API
 * 
 * GET  /api/ws-compression/stats      - 压缩统计信息
 * POST /api/ws-compression/compress  - 测试压缩接口
 * POST /api/ws-compression/decompress - 测试解压缩
 * GET  /api/ws-compression/capabilities - 获取服务端支持的压缩能力
 */
@RestController
@RequestMapping("/api/ws-compression")
@RequiredArgsConstructor
public class WebSocketCompressionController {

    private final BrotliCompressionService compressionService;

    private long totalCompressed = 0;
    private long totalBytesOriginal = 0;
    private long totalBytesCompressed = 0;
    private long totalDecompressed = 0;

    /**
     * POST /api/ws-compression/compress
     * 测试压缩接口
     */
    @PostMapping("/compress")
    public ResponseEntity<Map<String, Object>> compress(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        if (text == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "text is required"));
        }

        byte[] original = text.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] compressed = compressionService.compressStream(original);

        BrotliCompressionService.CompressionStats stats =
            compressionService.getStats(original, compressed);

        // 更新统计
        if (compressed.length < original.length) {
            totalCompressed++;
            totalBytesOriginal += original.length;
            totalBytesCompressed += compressed.length;
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("originalSize", stats.originalSize);
        resp.put("compressedSize", stats.compressedSize);
        resp.put("ratio", String.format("%.2f%%", stats.ratio));
        resp.put("compressed", compressed != original);
        resp.put("compressedData", compressed != original
            ? java.util.Base64.getEncoder().encodeToString(compressed)
            : null);

        return ResponseEntity.ok(resp);
    }

    /**
     * POST /api/ws-compression/decompress
     * 测试解压缩接口
     */
    @PostMapping("/decompress")
    public ResponseEntity<Map<String, Object>> decompress(@RequestBody Map<String, String> body) {
        String encoded = body.get("data");
        if (encoded == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "data is required"));
        }

        try {
            byte[] compressed = java.util.Base64.getDecoder().decode(encoded);
            byte[] decompressed = compressionService.decompressStream(compressed);
            String text = new String(decompressed, java.nio.charset.StandardCharsets.UTF_8);

            totalDecompressed++;

            Map<String, Object> resp = new HashMap<>();
            resp.put("originalSize", compressed.length);
            resp.put("decompressedSize", decompressed.length);
            resp.put("text", text);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Decompression failed: " + e.getMessage()));
        }
    }

    /**
     * GET /api/ws-compression/stats
     * 全局压缩统计
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        double overallRatio = totalBytesOriginal > 0
            ? (1 - (double) totalBytesCompressed / totalBytesOriginal) * 100
            : 0;

        Map<String, Object> resp = new HashMap<>();
        resp.put("totalCompressed", totalCompressed);
        resp.put("totalDecompressed", totalDecompressed);
        resp.put("totalBytesOriginal", totalBytesOriginal);
        resp.put("totalBytesCompressed", totalBytesCompressed);
        resp.put("overallRatio", String.format("%.2f%%", overallRatio));
        resp.put("savedBytes", totalBytesOriginal - totalBytesCompressed);
        return ResponseEntity.ok(resp);
    }

    /**
     * GET /api/ws-compression/capabilities
     * 服务端支持的压缩能力
     */
    @GetMapping("/capabilities")
    public ResponseEntity<Map<String, Object>> getCapabilities() {
        Map<String, Object> caps = new HashMap<>();
        caps.put("gzip", true);
        caps.put("brotli", false); // 需要额外依赖
        caps.put("deflate", true);
        caps.put("algorithms", new String[]{"gzip", "deflate"});
        caps.put("minThreshold", 512);
        caps.put("autoThreshold", 1024);
        caps.put("forceThreshold", 32 * 1024);
        caps.put("maxPayload", 10 * 1024 * 1024); // 10MB
        return ResponseEntity.ok(caps);
    }
}
