package com.im.backend.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 语音上传请求DTO
 */
public class VoiceUploadRequest {
    
    @NotNull(message = "语音时长不能为空")
    private Integer duration;
    
    private String originalName;
    
    private String format = "mp3";
    
    private Integer bitrate;
    
    private Integer sampleRate;
    
    // Getters and Setters
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    
    public Integer getBitrate() { return bitrate; }
    public void setBitrate(Integer bitrate) { this.bitrate = bitrate; }
    
    public Integer getSampleRate() { return sampleRate; }
    public void setSampleRate(Integer sampleRate) { this.sampleRate = sampleRate; }
}
