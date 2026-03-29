package com.im.miniprogram.security;

import java.util.*;

/**
 * 小程序安全策略配置
 * 定义小程序的安全限制和访问权限
 */
public class MiniProgramSecurityPolicy {
    
    private final String policyId;
    private final String policyName;
    private final int version;
    
    // 文件访问权限
    private boolean readAllowed = true;
    private boolean writeAllowed = true;
    private boolean deleteAllowed = false;
    private long maxStorageSize = 10 * 1024 * 1024; // 10MB
    
    // 网络访问权限
    private boolean networkAllowed = true;
    private boolean httpsOnly = true;
    private Set<String> allowedHosts = new HashSet<>();
    private Set<String> blockedHosts = new HashSet<>();
    private int maxConcurrentConnections = 10;
    private long maxRequestSize = 2 * 1024 * 1024; // 2MB
    private long maxResponseSize = 10 * 1024 * 1024; // 10MB
    
    // API访问权限
    private Set<String> allowedApis = new HashSet<>();
    private Set<String> blockedApis = new HashSet<>();
    private int maxApiCallsPerSecond = 100;
    private int maxApiCallsPerMinute = 1000;
    
    // 资源限制
    private long maxMemory = 256 * 1024 * 1024; // 256MB
    private long maxCpuTime = 60 * 1000; // 60秒
    private int maxThreads = 20;
    
    // 功能开关
    private boolean cameraAllowed = false;
    private boolean microphoneAllowed = false;
    private boolean locationAllowed = false;
    private boolean clipboardAllowed = false;
    private boolean bluetoothAllowed = false;
    private boolean nfcAllowed = false;
    
    // 运行时限制
    private long maxExecutionTime = 5 * 60 * 1000; // 5分钟
    private long idleTimeout = 30 * 60 * 1000; // 30分钟
    
    public MiniProgramSecurityPolicy() {
        this.policyId = UUID.randomUUID().toString();
        this.policyName = "default";
        this.version = 1;
        initializeDefaultPolicy();
    }
    
    public MiniProgramSecurityPolicy(String policyName) {
        this.policyId = UUID.randomUUID().toString();
        this.policyName = policyName;
        this.version = 1;
        initializeDefaultPolicy();
    }
    
    /**
     * 初始化默认安全策略
     */
    private void initializeDefaultPolicy() {
        // 默认允许的基础API
        allowedApis.add("console.log");
        allowedApis.add("console.error");
        allowedApis.add("console.warn");
        allowedApis.add("console.info");
        
        allowedApis.add("storage.get");
        allowedApis.add("storage.set");
        allowedApis.add("storage.remove");
        allowedApis.add("storage.clear");
        
        allowedApis.add("request.get");
        allowedApis.add("request.post");
        allowedApis.add("request.put");
        allowedApis.add("request.delete");
        
        allowedApis.add("ui.showToast");
        allowedApis.add("ui.showModal");
        allowedApis.add("ui.showLoading");
        allowedApis.add("ui.hideLoading");
        
        allowedApis.add("navigator.navigateTo");
        allowedApis.add("navigator.redirectTo");
        allowedApis.add("navigator.navigateBack");
        allowedApis.add("navigator.reLaunch");
        
        allowedApis.add("app.getInfo");
        allowedApis.add("app.getLaunchOptions");
        allowedApis.add("app.onError");
        allowedApis.add("app.onUnhandledRejection");
        
        // 默认允许的主机（需要通过用户授权）
        allowedHosts.add("*.im.example.com");
    }
    
    /**
     * 创建严格模式策略
     */
    public static MiniProgramSecurityPolicy createStrictPolicy() {
        MiniProgramSecurityPolicy policy = new MiniProgramSecurityPolicy("strict");
        
        policy.networkAllowed = false;
        policy.writeAllowed = false;
        policy.deleteAllowed = false;
        policy.maxStorageSize = 1 * 1024 * 1024; // 1MB
        policy.maxMemory = 64 * 1024 * 1024; // 64MB
        policy.maxApiCallsPerSecond = 20;
        
        // 只允许基础API
        policy.allowedApis.clear();
        policy.allowedApis.add("console.log");
        policy.allowedApis.add("storage.get");
        policy.allowedApis.add("ui.showToast");
        
        return policy;
    }
    
    /**
     * 创建宽松模式策略（用于开发调试）
     */
    public static MiniProgramSecurityPolicy createPermissivePolicy() {
        MiniProgramSecurityPolicy policy = new MiniProgramSecurityPolicy("permissive");
        
        policy.networkAllowed = true;
        policy.httpsOnly = false;
        policy.writeAllowed = true;
        policy.deleteAllowed = true;
        policy.maxStorageSize = 100 * 1024 * 1024; // 100MB
        policy.maxMemory = 512 * 1024 * 1024; // 512MB
        policy.maxApiCallsPerSecond = 500;
        
        // 允许所有API
        policy.allowedApis.clear();
        policy.blockedApis.clear();
        
        // 允许更多主机
        policy.allowedHosts.add("*");
        
        return policy;
    }
    
    /**
     * 检查API是否允许
     */
    public boolean isApiAllowed(String apiName) {
        if (blockedApis.contains(apiName)) {
            return false;
        }
        if (allowedApis.isEmpty() || allowedApis.contains("*")) {
            return true;
        }
        if (allowedApis.contains(apiName)) {
            return true;
        }
        // 支持通配符匹配
        for (String pattern : allowedApis) {
            if (pattern.endsWith(".*") && apiName.startsWith(pattern.substring(0, pattern.length() - 2))) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查主机是否允许
     */
    public boolean isHostAllowed(String host) {
        if (blockedHosts.contains(host)) {
            return false;
        }
        for (String allowedHost : allowedHosts) {
            if (matchesHostPattern(host, allowedHost)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean matchesHostPattern(String host, String pattern) {
        if (pattern.equals("*")) return true;
        if (pattern.equals(host)) return true;
        if (pattern.startsWith("*.")) {
            String suffix = pattern.substring(2);
            return host.equals(suffix) || host.endsWith("." + suffix);
        }
        return false;
    }
    
    // ============ Builder模式 ============
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private MiniProgramSecurityPolicy policy = new MiniProgramSecurityPolicy();
        
        public Builder readAllowed(boolean allowed) {
            policy.readAllowed = allowed;
            return this;
        }
        
        public Builder writeAllowed(boolean allowed) {
            policy.writeAllowed = allowed;
            return this;
        }
        
        public Builder deleteAllowed(boolean allowed) {
            policy.deleteAllowed = allowed;
            return this;
        }
        
        public Builder maxStorageSize(long size) {
            policy.maxStorageSize = size;
            return this;
        }
        
        public Builder networkAllowed(boolean allowed) {
            policy.networkAllowed = allowed;
            return this;
        }
        
        public Builder httpsOnly(boolean only) {
            policy.httpsOnly = only;
            return this;
        }
        
        public Builder addAllowedHost(String host) {
            policy.allowedHosts.add(host);
            return this;
        }
        
        public Builder addBlockedHost(String host) {
            policy.blockedHosts.add(host);
            return this;
        }
        
        public Builder addAllowedApi(String api) {
            policy.allowedApis.add(api);
            return this;
        }
        
        public Builder addBlockedApi(String api) {
            policy.blockedApis.add(api);
            return this;
        }
        
        public Builder maxMemory(long memory) {
            policy.maxMemory = memory;
            return this;
        }
        
        public Builder maxThreads(int threads) {
            policy.maxThreads = threads;
            return this;
        }
        
        public Builder cameraAllowed(boolean allowed) {
            policy.cameraAllowed = allowed;
            return this;
        }
        
        public Builder microphoneAllowed(boolean allowed) {
            policy.microphoneAllowed = allowed;
            return this;
        }
        
        public Builder locationAllowed(boolean allowed) {
            policy.locationAllowed = allowed;
            return this;
        }
        
        public MiniProgramSecurityPolicy build() {
            return policy;
        }
    }
    
    // ============ Getter/Setter ============
    
    public String getPolicyId() { return policyId; }
    public String getPolicyName() { return policyName; }
    public int getVersion() { return version; }
    
    public boolean isReadAllowed() { return readAllowed; }
    public void setReadAllowed(boolean allowed) { this.readAllowed = allowed; }
    
    public boolean isWriteAllowed() { return writeAllowed; }
    public void setWriteAllowed(boolean allowed) { this.writeAllowed = allowed; }
    
    public boolean isDeleteAllowed() { return deleteAllowed; }
    public void setDeleteAllowed(boolean allowed) { this.deleteAllowed = allowed; }
    
    public long getMaxStorageSize() { return maxStorageSize; }
    public void setMaxStorageSize(long size) { this.maxStorageSize = size; }
    
    public boolean isNetworkAllowed() { return networkAllowed; }
    public void setNetworkAllowed(boolean allowed) { this.networkAllowed = allowed; }
    
    public boolean isHttpsOnly() { return httpsOnly; }
    public void setHttpsOnly(boolean only) { this.httpsOnly = only; }
    
    public Set<String> getAllowedHosts() { return new HashSet<>(allowedHosts); }
    public void setAllowedHosts(Set<String> hosts) { this.allowedHosts = new HashSet<>(hosts); }
    
    public Set<String> getBlockedHosts() { return new HashSet<>(blockedHosts); }
    public void setBlockedHosts(Set<String> hosts) { this.blockedHosts = new HashSet<>(hosts); }
    
    public int getMaxConcurrentConnections() { return maxConcurrentConnections; }
    public void setMaxConcurrentConnections(int max) { this.maxConcurrentConnections = max; }
    
    public long getMaxRequestSize() { return maxRequestSize; }
    public void setMaxRequestSize(long size) { this.maxRequestSize = size; }
    
    public long getMaxResponseSize() { return maxResponseSize; }
    public void setMaxResponseSize(long size) { this.maxResponseSize = size; }
    
    public Set<String> getAllowedApis() { return new HashSet<>(allowedApis); }
    public void setAllowedApis(Set<String> apis) { this.allowedApis = new HashSet<>(apis); }
    
    public Set<String> getBlockedApis() { return new HashSet<>(blockedApis); }
    public void setBlockedApis(Set<String> apis) { this.blockedApis = new HashSet<>(apis); }
    
    public int getMaxApiCallsPerSecond() { return maxApiCallsPerSecond; }
    public void setMaxApiCallsPerSecond(int max) { this.maxApiCallsPerSecond = max; }
    
    public int getMaxApiCallsPerMinute() { return maxApiCallsPerMinute; }
    public void setMaxApiCallsPerMinute(int max) { this.maxApiCallsPerMinute = max; }
    
    public long getMaxMemory() { return maxMemory; }
    public void setMaxMemory(long memory) { this.maxMemory = memory; }
    
    public long getMaxCpuTime() { return maxCpuTime; }
    public void setMaxCpuTime(long time) { this.maxCpuTime = time; }
    
    public int getMaxThreads() { return maxThreads; }
    public void setMaxThreads(int threads) { this.maxThreads = threads; }
    
    public boolean isCameraAllowed() { return cameraAllowed; }
    public void setCameraAllowed(boolean allowed) { this.cameraAllowed = allowed; }
    
    public boolean isMicrophoneAllowed() { return microphoneAllowed; }
    public void setMicrophoneAllowed(boolean allowed) { this.microphoneAllowed = allowed; }
    
    public boolean isLocationAllowed() { return locationAllowed; }
    public void setLocationAllowed(boolean allowed) { this.locationAllowed = allowed; }
    
    public boolean isClipboardAllowed() { return clipboardAllowed; }
    public void setClipboardAllowed(boolean allowed) { this.clipboardAllowed = allowed; }
    
    public boolean isBluetoothAllowed() { return bluetoothAllowed; }
    public void setBluetoothAllowed(boolean allowed) { this.bluetoothAllowed = allowed; }
    
    public boolean isNfcAllowed() { return nfcAllowed; }
    public void setNfcAllowed(boolean allowed) { this.nfcAllowed = allowed; }
    
    public long getMaxExecutionTime() { return maxExecutionTime; }
    public void setMaxExecutionTime(long time) { this.maxExecutionTime = time; }
    
    public long getIdleTimeout() { return idleTimeout; }
    public void setIdleTimeout(long timeout) { this.idleTimeout = timeout; }
}
