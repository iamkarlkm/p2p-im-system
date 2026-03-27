package com.im.miniprogram.container;

import com.im.miniprogram.sandbox.MiniProgramSandbox;
import com.im.miniprogram.model.MiniProgramManifest;
import com.im.miniprogram.security.MiniProgramSecurityPolicy;

import java.util.*;
import java.util.concurrent.*;

/**
 * 小程序容器管理器
 * 管理多个小程序沙箱实例的生命周期
 */
public class MiniProgramContainer {
    
    private static MiniProgramContainer instance;
    
    // 沙箱实例映射
    private final Map<String, MiniProgramSandbox> sandboxes = new ConcurrentHashMap<>();
    private final Map<String, String> appIdToSandboxId = new ConcurrentHashMap<>();
    
    // 沙箱配置
    private MiniProgramSecurityPolicy defaultPolicy;
    private int maxConcurrentSandboxes = 10;
    private long maxSandboxLifetime = 30 * 60 * 1000; // 30分钟
    
    // 清理调度器
    private final ScheduledExecutorService cleanupScheduler = Executors.newScheduledThreadPool(1);
    
    // 事件监听器
    private final List<ContainerListener> listeners = new CopyOnWriteArrayList<>();
    
    public interface ContainerListener {
        void onSandboxCreated(String sandboxId, String appId);
        void onSandboxDestroyed(String sandboxId, String appId);
        void onSandboxError(String sandboxId, String appId, Throwable error);
    }
    
    private MiniProgramContainer() {
        this.defaultPolicy = MiniProgramSecurityPolicy.builder()
            .networkAllowed(true)
            .readAllowed(true)
            .writeAllowed(true)
            .maxMemory(256 * 1024 * 1024)
            .build();
        
        // 启动定期清理任务
        startCleanupTask();
    }
    
    public static synchronized MiniProgramContainer getInstance() {
        if (instance == null) {
            instance = new MiniProgramContainer();
        }
        return instance;
    }
    
    /**
     * 创建小程序沙箱
     */
    public MiniProgramSandbox createSandbox(String appId, MiniProgramManifest manifest) {
        return createSandbox(appId, manifest, defaultPolicy);
    }
    
    /**
     * 创建小程序沙箱（指定安全策略）
     */
    public MiniProgramSandbox createSandbox(String appId, MiniProgramManifest manifest, 
                                            MiniProgramSecurityPolicy policy) {
        // 检查是否已存在
        if (appIdToSandboxId.containsKey(appId)) {
            String existingSandboxId = appIdToSandboxId.get(appId);
            MiniProgramSandbox existing = sandboxes.get(existingSandboxId);
            if (existing != null && existing.getState() != MiniProgramSandbox.SandboxState.TERMINATED) {
                return existing;
            }
        }
        
        // 检查并发限制
        if (sandboxes.size() >= maxConcurrentSandboxes) {
            // 清理最久未使用的沙箱
            cleanupOldestSandbox();
        }
        
        // 创建新沙箱
        MiniProgramSandbox sandbox = new MiniProgramSandbox(appId, manifest, policy);
        
        try {
            sandbox.initialize();
            sandboxes.put(sandbox.getSandboxId(), sandbox);
            appIdToSandboxId.put(appId, sandbox.getSandboxId());
            
            notifySandboxCreated(sandbox.getSandboxId(), appId);
            
            return sandbox;
        } catch (Exception e) {
            notifySandboxError(sandbox.getSandboxId(), appId, e);
            throw new RuntimeException("Failed to create sandbox", e);
        }
    }
    
    /**
     * 获取沙箱实例
     */
    public MiniProgramSandbox getSandbox(String sandboxId) {
        return sandboxes.get(sandboxId);
    }
    
    /**
     * 通过AppId获取沙箱
     */
    public MiniProgramSandbox getSandboxByAppId(String appId) {
        String sandboxId = appIdToSandboxId.get(appId);
        if (sandboxId != null) {
            return sandboxes.get(sandboxId);
        }
        return null;
    }
    
    /**
     * 销毁沙箱
     */
    public void destroySandbox(String sandboxId) {
        MiniProgramSandbox sandbox = sandboxes.remove(sandboxId);
        if (sandbox != null) {
            appIdToSandboxId.remove(sandbox.getAppId());
            sandbox.terminate();
            notifySandboxDestroyed(sandboxId, sandbox.getAppId());
        }
    }
    
    /**
     * 销毁所有沙箱
     */
    public void destroyAllSandboxes() {
        List<String> sandboxIds = new ArrayList<>(sandboxes.keySet());
        for (String sandboxId : sandboxIds) {
            destroySandbox(sandboxId);
        }
    }
    
    /**
     * 启动小程序
     */
    public void launchMiniProgram(String sandboxId, Map<String, Object> params) {
        MiniProgramSandbox sandbox = sandboxes.get(sandboxId);
        if (sandbox == null) {
            throw new IllegalArgumentException("Sandbox not found: " + sandboxId);
        }
        sandbox.launch(params);
    }
    
    /**
     * 暂停小程序
     */
    public void pauseMiniProgram(String sandboxId) {
        MiniProgramSandbox sandbox = sandboxes.get(sandboxId);
        if (sandbox != null) {
            sandbox.pause();
        }
    }
    
    /**
     * 恢复小程序
     */
    public void resumeMiniProgram(String sandboxId) {
        MiniProgramSandbox sandbox = sandboxes.get(sandboxId);
        if (sandbox != null) {
            sandbox.resume();
        }
    }
    
    /**
     * 获取所有沙箱列表
     */
    public List<MiniProgramSandbox> getAllSandboxes() {
        return new ArrayList<>(sandboxes.values());
    }
    
    /**
     * 获取运行中的沙箱数量
     */
    public int getRunningSandboxCount() {
        int count = 0;
        for (MiniProgramSandbox sandbox : sandboxes.values()) {
            if (sandbox.getState() == MiniProgramSandbox.SandboxState.RUNNING) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * 添加容器监听器
     */
    public void addListener(ContainerListener listener) {
        listeners.add(listener);
    }
    
    /**
     * 移除容器监听器
     */
    public void removeListener(ContainerListener listener) {
        listeners.remove(listener);
    }
    
    // ============ 配置方法 ============
    
    public void setDefaultPolicy(MiniProgramSecurityPolicy policy) {
        this.defaultPolicy = policy;
    }
    
    public void setMaxConcurrentSandboxes(int max) {
        this.maxConcurrentSandboxes = max;
    }
    
    public void setMaxSandboxLifetime(long lifetime) {
        this.maxSandboxLifetime = lifetime;
    }
    
    // ============ 私有方法 ============
    
    private void startCleanupTask() {
        cleanupScheduler.scheduleAtFixedRate(() -> {
            try {
                cleanupInactiveSandboxes();
            } catch (Exception e) {
                // 清理异常不应影响系统
            }
        }, 5, 5, TimeUnit.MINUTES); // 每5分钟清理一次
    }
    
    private void cleanupInactiveSandboxes() {
        long now = System.currentTimeMillis();
        List<String> toRemove = new ArrayList<>();
        
        for (Map.Entry<String, MiniProgramSandbox> entry : sandboxes.entrySet()) {
            MiniProgramSandbox sandbox = entry.getValue();
            
            // 检查是否超时
            if (sandbox.getUptime() > maxSandboxLifetime) {
                toRemove.add(entry.getKey());
                continue;
            }
            
            // 检查是否已终止
            if (sandbox.getState() == MiniProgramSandbox.SandboxState.TERMINATED ||
                sandbox.getState() == MiniProgramSandbox.SandboxState.ERROR) {
                toRemove.add(entry.getKey());
            }
        }
        
        for (String sandboxId : toRemove) {
            destroySandbox(sandboxId);
        }
    }
    
    private void cleanupOldestSandbox() {
        MiniProgramSandbox oldest = null;
        String oldestId = null;
        
        for (Map.Entry<String, MiniProgramSandbox> entry : sandboxes.entrySet()) {
            MiniProgramSandbox sandbox = entry.getValue();
            if (oldest == null || sandbox.getStartTime() < oldest.getStartTime()) {
                oldest = sandbox;
                oldestId = entry.getKey();
            }
        }
        
        if (oldestId != null) {
            destroySandbox(oldestId);
        }
    }
    
    private void notifySandboxCreated(String sandboxId, String appId) {
        for (ContainerListener listener : listeners) {
            try {
                listener.onSandboxCreated(sandboxId, appId);
            } catch (Exception e) {
                // 监听器异常不应影响系统
            }
        }
    }
    
    private void notifySandboxDestroyed(String sandboxId, String appId) {
        for (ContainerListener listener : listeners) {
            try {
                listener.onSandboxDestroyed(sandboxId, appId);
            } catch (Exception e) {
                // 监听器异常不应影响系统
            }
        }
    }
    
    private void notifySandboxError(String sandboxId, String appId, Throwable error) {
        for (ContainerListener listener : listeners) {
            try {
                listener.onSandboxError(sandboxId, appId, error);
            } catch (Exception e) {
                // 监听器异常不应影响系统
            }
        }
    }
    
    /**
     * 关闭容器
     */
    public void shutdown() {
        cleanupScheduler.shutdown();
        destroyAllSandboxes();
    }
}
