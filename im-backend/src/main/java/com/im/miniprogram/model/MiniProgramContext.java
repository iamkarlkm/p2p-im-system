package com.im.miniprogram.model;

import java.util.*;

/**
 * 小程序上下文
 */
public class MiniProgramContext {
    
    private String appId;
    private String sandboxId;
    private String launchPath;
    private Map<String, Object> launchParams;
    private String storagePath;
    private String tempPath;
    private String userId;
    private String sessionId;
    private long launchTime;
    
    // 系统信息
    private String system;
    private String platform;
    private String version;
    private String language;
    
    // 场景值
    private int scene;
    private String sceneDesc;
    
    // 来源信息
    private String referrerAppId;
    private Map<String, Object> referrerInfo;
    
    public MiniProgramContext() {
        this.launchTime = System.currentTimeMillis();
    }
    
    // ============ Getter/Setter ============
    
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    
    public String getSandboxId() { return sandboxId; }
    public void setSandboxId(String sandboxId) { this.sandboxId = sandboxId; }
    
    public String getLaunchPath() { return launchPath; }
    public void setLaunchPath(String launchPath) { this.launchPath = launchPath; }
    
    public Map<String, Object> getLaunchParams() { return launchParams; }
    public void setLaunchParams(Map<String, Object> launchParams) { this.launchParams = launchParams; }
    
    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }
    
    public String getTempPath() { return tempPath; }
    public void setTempPath(String tempPath) { this.tempPath = tempPath; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public long getLaunchTime() { return launchTime; }
    public void setLaunchTime(long launchTime) { this.launchTime = launchTime; }
    
    public String getSystem() { return system; }
    public void setSystem(String system) { this.system = system; }
    
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public int getScene() { return scene; }
    public void setScene(int scene) { this.scene = scene; }
    
    public String getSceneDesc() { return sceneDesc; }
    public void setSceneDesc(String sceneDesc) { this.sceneDesc = sceneDesc; }
    
    public String getReferrerAppId() { return referrerAppId; }
    public void setReferrerAppId(String referrerAppId) { this.referrerAppId = referrerAppId; }
    
    public Map<String, Object> getReferrerInfo() { return referrerInfo; }
    public void setReferrerInfo(Map<String, Object> referrerInfo) { this.referrerInfo = referrerInfo; }
    
    /**
     * 转换为JSON字符串
     */
    public String toJson() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"appId\":\"").append(appId).append("\",");
        sb.append("\"sandboxId\":\"").append(sandboxId).append("\",");
        sb.append("\"launchPath\":\"").append(launchPath).append("\",");
        sb.append("\"storagePath\":\"").append(storagePath).append("\",");
        sb.append("\"tempPath\":\"").append(tempPath).append("\",");
        sb.append("\"launchTime\":").append(launchTime);
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * 从JSON解析
     */
    public static MiniProgramContext fromJson(String json) {
        // 简化实现
        return new MiniProgramContext();
    }
}
