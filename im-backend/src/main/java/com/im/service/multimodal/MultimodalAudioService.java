package com.im.service.multimodal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 多模态音频处理服务
 * 支持语音转文字、文字转语音、音频处理
 */
@Slf4j
@Service
public class MultimodalAudioService {

    private final Map<String, AudioProcessingTask> processingTasks = new HashMap<>();

    /**
     * 音频处理任务
     */
    public static class AudioProcessingTask {
        private String taskId;
        private String type; // STT, TTS, ENHANCE
        private String status;
        private String result;
        private long createTime;
        private long completeTime;

        public AudioProcessingTask(String taskId, String type) {
            this.taskId = taskId;
            this.type = type;
            this.status = "PENDING";
            this.createTime = System.currentTimeMillis();
        }

        public String getTaskId() { return taskId; }
        public String getType() { return type; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }
    }

    /**
     * 语音转文字 (Speech to Text)
     */
    public AudioProcessingTask speechToText(byte[] audioData, String language) {
        String taskId = "STT-" + System.currentTimeMillis();
        AudioProcessingTask task = new AudioProcessingTask(taskId, "STT");
        processingTasks.put(taskId, task);

        log.info("STT task created: {} (language: {})", taskId, language);

        // Simulate async processing
        new Thread(() -> {
            try {
                Thread.sleep(1500);
                
                // Simulate transcription based on audio length
                String mockTranscription = generateMockTranscription(audioData.length, language);
                task.setResult(mockTranscription);
                task.setStatus("COMPLETED");
                task.completeTime = System.currentTimeMillis();
                
                log.info("STT completed: {} - \"{}\"", taskId, 
                    mockTranscription.substring(0, Math.min(50, mockTranscription.length())));
            } catch (InterruptedException e) {
                task.setStatus("FAILED");
                log.error("STT failed: {}", taskId, e);
            }
        }).start();

        return task;
    }

    /**
     * 文字转语音 (Text to Speech)
     */
    public AudioProcessingTask textToSpeech(String text, String voice, String language) {
        String taskId = "TTS-" + System.currentTimeMillis();
        AudioProcessingTask task = new AudioProcessingTask(taskId, "TTS");
        processingTasks.put(taskId, task);

        log.info("TTS task created: {} (voice: {}, language: {})", taskId, voice, language);

        // Simulate async processing
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                
                // Generate mock audio data
                byte[] mockAudio = generateMockAudioData(text.length());
                String base64Audio = Base64.getEncoder().encodeToString(mockAudio);
                
                task.setResult(base64Audio);
                task.setStatus("COMPLETED");
                task.completeTime = System.currentTimeMillis();
                
                log.info("TTS completed: {} ({} chars -> {} bytes)", taskId, text.length(), mockAudio.length);
            } catch (Exception e) {
                task.setStatus("FAILED");
                log.error("TTS failed: {}", taskId, e);
            }
        }).start();

        return task;
    }

    /**
     * 生成模拟转录文本
     */
    private String generateMockTranscription(int audioLength, String language) {
        String[] phrases = {
            "Hello, this is a voice message",
            "I'm using the multimodal AI assistant",
            "The speech to text conversion is working",
            "Please process this audio file",
            "Thank you for your help"
        };
        
        int phraseCount = Math.max(1, audioLength / 5000);
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < phraseCount && i < phrases.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(phrases[i]);
        }
        
        return sb.toString();
    }

    /**
     * 生成模拟音频数据
     */
    private byte[] generateMockAudioData(int textLength) {
        // Generate a simple sine wave
        int sampleRate = 16000;
        double duration = Math.min(10, textLength / 10.0); // Max 10 seconds
        int numSamples = (int)(duration * sampleRate);
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        for (int i = 0; i < numSamples; i++) {
            double time = i / (double)sampleRate;
            double frequency = 440; // A4 note
            double amplitude = Math.sin(2 * Math.PI * frequency * time) * 0.5;
            short sample = (short)(amplitude * 32767);
            output.write(sample & 0xFF);
            output.write((sample >> 8) & 0xFF);
        }
        
        return output.toByteArray();
    }

    /**
     * 音频增强
     */
    public byte[] enhanceAudio(byte[] audioData, String enhancementType) {
        log.info("Enhancing audio with type: {}", enhancementType);
        
        try {
            // Parse audio data
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                new ByteArrayInputStream(audioData));
            
            AudioFormat format = audioStream.getFormat();
            byte[] enhancedData;
            
            switch (enhancementType.toLowerCase()) {
                case "noise_reduction":
                    enhancedData = applyNoiseReduction(audioData);
                    break;
                case "volume_boost":
                    enhancedData = applyVolumeBoost(audioData, 1.5f);
                    break;
                case "normalize":
                    enhancedData = applyNormalization(audioData);
                    break;
                default:
                    enhancedData = audioData;
            }
            
            log.info("Audio enhancement completed: {} -> {} bytes", 
                audioData.length, enhancedData.length);
            return enhancedData;
            
        } catch (UnsupportedAudioFileException | IOException e) {
            log.error("Audio enhancement failed", e);
            return audioData;
        }
    }

    /**
     * 应用降噪 (模拟)
     */
    private byte[] applyNoiseReduction(byte[] audioData) {
        // Simple noise gate simulation
        byte[] result = new byte[audioData.length];
        byte threshold = 10;
        
        for (int i = 0; i < audioData.length; i++) {
            if (Math.abs(audioData[i]) < threshold) {
                result[i] = 0;
            } else {
                result[i] = audioData[i];
            }
        }
        
        return result;
    }

    /**
     * 应用音量提升 (模拟)
     */
    private byte[] applyVolumeBoost(byte[] audioData, float factor) {
        byte[] result = new byte[audioData.length];
        
        for (int i = 0; i < audioData.length; i++) {
            int value = (int)(audioData[i] * factor);
            result[i] = (byte)Math.max(-128, Math.min(127, value));
        }
        
        return result;
    }

    /**
     * 应用归一化 (模拟)
     */
    private byte[] applyNormalization(byte[] audioData) {
        byte max = 0;
        for (byte b : audioData) {
            if (Math.abs(b) > Math.abs(max)) {
                max = b;
            }
        }
        
        if (max == 0) return audioData;
        
        float factor = 127.0f / Math.abs(max);
        return applyVolumeBoost(audioData, factor);
    }

    /**
     * 分析音频
     */
    public Map<String, Object> analyzeAudio(byte[] audioData) {
        Map<String, Object> analysis = new HashMap<>();
        
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(
                new ByteArrayInputStream(audioData));
            AudioFormat format = stream.getFormat();
            
            analysis.put("format", format.getEncoding().toString());
            analysis.put("sampleRate", format.getSampleRate());
            analysis.put("channels", format.getChannels());
            analysis.put("sampleSize", format.getSampleSizeInBits());
            analysis.put("duration", audioData.length / format.getFrameRate());
            analysis.put("size", audioData.length);
            
            // Simulate audio metrics
            analysis.put("volume", calculateVolume(audioData));
            analysis.put("silenceRatio", 0.15);
            analysis.put("quality", "good");
            
        } catch (Exception e) {
            log.error("Audio analysis failed", e);
            analysis.put("error", "Failed to analyze audio");
        }
        
        return analysis;
    }

    /**
     * 计算音量
     */
    private double calculateVolume(byte[] audioData) {
        if (audioData.length == 0) return 0;
        
        double sum = 0;
        for (byte b : audioData) {
            sum += Math.abs(b);
        }
        
        return sum / audioData.length / 128.0; // Normalize to 0-1
    }

    /**
     * 获取任务状态
     */
    public AudioProcessingTask getTask(String taskId) {
        return processingTasks.get(taskId);
    }

    /**
     * 音频格式转换
     */
    public byte[] convertFormat(byte[] audioData, String targetFormat) {
        log.info("Converting audio to format: {}", targetFormat);
        
        try {
            AudioInputStream sourceStream = AudioSystem.getAudioInputStream(
                new ByteArrayInputStream(audioData));
            
            AudioFormat sourceFormat = sourceStream.getFormat();
            
            AudioFormat.Encoding targetEncoding = switch (targetFormat.toLowerCase()) {
                case "pcm" -> AudioFormat.Encoding.PCM_SIGNED;
                case "ulaw" -> AudioFormat.Encoding.ULAW;
                case "alaw" -> AudioFormat.Encoding.ALAW;
                default -> sourceFormat.getEncoding();
            };
            
            AudioFormat targetAudioFormat = new AudioFormat(
                targetEncoding,
                sourceFormat.getSampleRate(),
                sourceFormat.getSampleSizeInBits(),
                sourceFormat.getChannels(),
                sourceFormat.getFrameSize(),
                sourceFormat.getFrameRate(),
                sourceFormat.isBigEndian()
            );
            
            AudioInputStream convertedStream = AudioSystem.getAudioInputStream(
                targetAudioFormat, sourceStream);
            
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = convertedStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            
            return output.toByteArray();
            
        } catch (Exception e) {
            log.error("Format conversion failed", e);
            return audioData;
        }
    }
}
