package com.im.service.multimodal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 多模态图像处理服务
 * 支持图像生成、编辑、分析和处理
 */
@Slf4j
@Service
public class MultimodalImageService {

    private final Map<String, ImageGenerationTask> generationTasks = new HashMap<>();

    /**
     * 图像生成任务
     */
    public static class ImageGenerationTask {
        private String taskId;
        private String prompt;
        private String style;
        private int width;
        private int height;
        private String status; // PENDING, PROCESSING, COMPLETED, FAILED
        private String resultUrl;
        private long createTime;
        private long completeTime;

        public ImageGenerationTask(String taskId, String prompt, String style, int width, int height) {
            this.taskId = taskId;
            this.prompt = prompt;
            this.style = style;
            this.width = width;
            this.height = height;
            this.status = "PENDING";
            this.createTime = System.currentTimeMillis();
        }

        // Getters and setters
        public String getTaskId() { return taskId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getResultUrl() { return resultUrl; }
        public void setResultUrl(String resultUrl) { this.resultUrl = resultUrl; }
        public long getCreateTime() { return createTime; }
    }

    /**
     * 生成图像
     */
    public ImageGenerationTask generateImage(String prompt, String style, int width, int height) {
        String taskId = "IMG-" + System.currentTimeMillis();
        ImageGenerationTask task = new ImageGenerationTask(taskId, prompt, style, width, height);
        generationTasks.put(taskId, task);

        log.info("Image generation task created: {} - \"{}\" ({}x{})", taskId, prompt, width, height);
        
        // Simulate async processing
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate processing time
                task.setStatus("COMPLETED");
                task.setResultUrl("/api/v1/images/generated/" + taskId + ".png");
                task.completeTime = System.currentTimeMillis();
                log.info("Image generation completed: {}", taskId);
            } catch (InterruptedException e) {
                task.setStatus("FAILED");
                log.error("Image generation failed: {}", taskId, e);
            }
        }).start();

        return task;
    }

    /**
     * 编辑图像
     */
    public byte[] editImage(byte[] imageData, String editType, Map<String, Object> params) {
        log.info("Editing image with type: {}", editType);
        
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            if (image == null) {
                throw new RuntimeException("Failed to read image");
            }

            BufferedImage editedImage = switch (editType.toLowerCase()) {
                case "resize" -> resizeImage(image, params);
                case "crop" -> cropImage(image, params);
                case "filter" -> applyFilter(image, params);
                case "rotate" -> rotateImage(image, params);
                default -> image;
            };

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(editedImage, "PNG", output);
            return output.toByteArray();

        } catch (IOException e) {
            log.error("Image editing failed", e);
            throw new RuntimeException("Image editing failed", e);
        }
    }

    /**
     * 调整图像大小
     */
    private BufferedImage resizeImage(BufferedImage image, Map<String, Object> params) {
        int width = (int) params.getOrDefault("width", image.getWidth());
        int height = (int) params.getOrDefault("height", image.getHeight());
        
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        
        log.info("Image resized to {}x{}", width, height);
        return resized;
    }

    /**
     * 裁剪图像
     */
    private BufferedImage cropImage(BufferedImage image, Map<String, Object> params) {
        int x = (int) params.getOrDefault("x", 0);
        int y = (int) params.getOrDefault("y", 0);
        int width = (int) params.getOrDefault("width", image.getWidth());
        int height = (int) params.getOrDefault("height", image.getHeight());
        
        // Ensure bounds are valid
        x = Math.max(0, Math.min(x, image.getWidth()));
        y = Math.max(0, Math.min(y, image.getHeight()));
        width = Math.min(width, image.getWidth() - x);
        height = Math.min(height, image.getHeight() - y);
        
        return image.getSubimage(x, y, width, height);
    }

    /**
     * 应用滤镜
     */
    private BufferedImage applyFilter(BufferedImage image, Map<String, Object> params) {
        String filterType = (String) params.getOrDefault("filter", "grayscale");
        
        BufferedImage filtered = new BufferedImage(
            image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xFF;
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                int newRgb = switch (filterType.toLowerCase()) {
                    case "grayscale" -> {
                        int gray = (red + green + blue) / 3;
                        yield (alpha << 24) | (gray << 16) | (gray << 8) | gray;
                    }
                    case "sepia" -> {
                        int tr = (int)(0.393 * red + 0.769 * green + 0.189 * blue);
                        int tg = (int)(0.349 * red + 0.686 * green + 0.168 * blue);
                        int tb = (int)(0.272 * red + 0.534 * green + 0.131 * blue);
                        yield (alpha << 24) | 
                              (Math.min(255, tr) << 16) | 
                              (Math.min(255, tg) << 8) | 
                              Math.min(255, tb);
                    }
                    case "invert" -> {
                        yield (alpha << 24) | 
                              ((255 - red) << 16) | 
                              ((255 - green) << 8) | 
                              (255 - blue);
                    }
                    default -> rgb;
                };
                
                filtered.setRGB(x, y, newRgb);
            }
        }
        
        log.info("Applied filter: {}", filterType);
        return filtered;
    }

    /**
     * 旋转图像
     */
    private BufferedImage rotateImage(BufferedImage image, Map<String, Object> params) {
        double angle = ((Number) params.getOrDefault("angle", 0.0)).doubleValue();
        
        int w = image.getWidth();
        int h = image.getHeight();
        
        BufferedImage rotated = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = rotated.createGraphics();
        g.rotate(Math.toRadians(angle), w / 2.0, h / 2.0);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        
        log.info("Image rotated by {} degrees", angle);
        return rotated;
    }

    /**
     * 分析图像内容 (模拟OCR)
     */
    public Map<String, Object> analyzeImage(byte[] imageData) {
        Map<String, Object> analysis = new HashMap<>();
        
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            if (image != null) {
                analysis.put("width", image.getWidth());
                analysis.put("height", image.getHeight());
                analysis.put("format", "image/png");
                analysis.put("size", imageData.length);
                
                // Simulate color analysis
                long totalRed = 0, totalGreen = 0, totalBlue = 0;
                int pixelCount = image.getWidth() * image.getHeight();
                
                for (int x = 0; x < image.getWidth(); x += 10) {
                    for (int y = 0; y < image.getHeight(); y += 10) {
                        int rgb = image.getRGB(x, y);
                        totalRed += (rgb >> 16) & 0xFF;
                        totalGreen += (rgb >> 8) & 0xFF;
                        totalBlue += rgb & 0xFF;
                    }
                }
                
                Map<String, Integer> dominantColors = new HashMap<>();
                dominantColors.put("red", (int)(totalRed / (pixelCount / 100)));
                dominantColors.put("green", (int)(totalGreen / (pixelCount / 100)));
                dominantColors.put("blue", (int)(totalBlue / (pixelCount / 100)));
                analysis.put("dominantColors", dominantColors);
                
                // Simulate object detection
                analysis.put("detectedObjects", new String[]{"person", "object", "background"});
                analysis.put("confidence", 0.85);
            }
        } catch (IOException e) {
            log.error("Image analysis failed", e);
            analysis.put("error", "Failed to analyze image");
        }
        
        return analysis;
    }

    /**
     * 图像转Base64
     */
    public String imageToBase64(byte[] imageData) {
        return Base64.getEncoder().encodeToString(imageData);
    }

    /**
     * Base64转图像
     */
    public byte[] base64ToImage(String base64Data) {
        return Base64.getDecoder().decode(base64Data);
    }

    /**
     * 获取生成任务状态
     */
    public ImageGenerationTask getGenerationTask(String taskId) {
        return generationTasks.get(taskId);
    }
}
