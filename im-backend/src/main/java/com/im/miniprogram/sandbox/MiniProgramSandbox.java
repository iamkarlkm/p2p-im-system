package com.im.miniprogram.sandbox;

import com.im.miniprogram.security.MiniProgramSecurityPolicy;
import com.im.miniprogram.lifecycle.MiniProgramLifecycle;
import com.im.miniprogram.api.MiniProgramApiBridge;
import com.im.miniprogram.exception.MiniProgramException;
import com.im.miniprogram.model.MiniProgramManifest;
import com.im.miniprogram.model.MiniProgramContext;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.security.*;
import java.net.*;

/**
 * 小程序沙箱环境 - 核心容器类
 * 提供隔离的运行环境、资源限制和安全管控
 */
public class MiniProgramSandbox {
    
    private final String appId;
    private final String sandboxId;
    private final MiniProgramManifest manifest;
    private final MiniProgramSecurityPolicy securityPolicy;
    private final MiniProgramLifecycle lifecycle;
    private final MiniProgramApiBridge apiBridge;
    
    private volatile SandboxState state = SandboxState.CREATED;
    private final Path sandboxRoot;
    private final Path codeBasePath;
    private final Path dataPath;
    private final Path tempPath;
    
    private Process jsRuntimeProcess;
    private SandboxResourceMonitor resourceMonitor;
    private SecurityManager securityManager;
    
    private final Map<String, Object> storage = new ConcurrentHashMap<>();
    private final BlockingQueue<SandboxEvent> eventQueue = new LinkedBlockingQueue<>(1000);
    private final ExecutorService eventExecutor = Executors.newSingleThreadExecutor();
    
    private long startTime;
    private long lastActiveTime;
    private int apiCallCount = 0;
    private long totalMemoryUsed = 0;
    
    public enum SandboxState {
        CREATED, INITIALIZING, READY, RUNNING, PAUSED, RESUMED, 
        TERMINATING, TERMINATED, CRASHED, ERROR
    }
    
    public MiniProgramSandbox(String appId, MiniProgramManifest manifest, 
                              MiniProgramSecurityPolicy policy) {
        this.appId = appId;
        this.sandboxId = generateSandboxId();
        this.manifest = manifest;
        this.securityPolicy = policy;
        this.sandboxRoot = Paths.get(System.getProperty("user.home"), 
            ".im_miniprogram", "sandboxes", sandboxId);
        this.codeBasePath = sandboxRoot.resolve("code");
        this.dataPath = sandboxRoot.resolve("data");
        this.tempPath = sandboxRoot.resolve("temp");
        this.lifecycle = new MiniProgramLifecycle(this);
        this.apiBridge = new MiniProgramApiBridge(this);
    }
    
    /**
     * 初始化沙箱环境
     */
    public synchronized void initialize() throws MiniProgramException {
        if (state != SandboxState.CREATED) {
            throw new MiniProgramException("Sandbox already initialized", appId);
        }
        
        state = SandboxState.INITIALIZING;
        
        try {
            // 创建沙箱目录结构
            createSandboxDirectoryStructure();
            
            // 解压小程序代码包
            extractCodePackage();
            
            // 初始化安全策略
            initializeSecurity();
            
            // 启动资源监控
            startResourceMonitor();
            
            // 启动事件处理器
            startEventProcessor();
            
            state = SandboxState.READY;
            
        } catch (Exception e) {
            state = SandboxState.ERROR;
            throw new MiniProgramException("Failed to initialize sandbox: " + e.getMessage(), appId, e);
        }
    }
    
    /**
     * 启动小程序
     */
    public synchronized void launch(Map<String, Object> launchParams) throws MiniProgramException {
        if (state != SandboxState.READY && state != SandboxState.PAUSED) {
            throw new MiniProgramException("Sandbox not ready for launch", appId);
        }
        
        try {
            state = SandboxState.RUNNING;
            startTime = System.currentTimeMillis();
            lastActiveTime = startTime;
            
            // 构建启动上下文
            MiniProgramContext context = buildLaunchContext(launchParams);
            
            // 启动JS运行时
            startJsRuntime(context);
            
            // 触发生命周期事件
            lifecycle.onLaunch(context);
            
            // 显示首页
            apiBridge.callMethod("app", "onLaunch", context.toJson());
            
        } catch (Exception e) {
            state = SandboxState.CRASHED;
            throw new MiniProgramException("Failed to launch mini program: " + e.getMessage(), appId, e);
        }
    }
    
    /**
     * 暂停小程序（进入后台）
     */
    public synchronized void pause() {
        if (state == SandboxState.RUNNING) {
            state = SandboxState.PAUSED;
            lifecycle.onHide();
            apiBridge.callMethod("app", "onHide", null);
        }
    }
    
    /**
     * 恢复小程序（进入前台）
     */
    public synchronized void resume() {
        if (state == SandboxState.PAUSED) {
            state = SandboxState.RUNNING;
            lastActiveTime = System.currentTimeMillis();
            lifecycle.onShow();
            apiBridge.callMethod("app", "onShow", null);
        }
    }
    
    /**
     * 终止小程序
     */
    public synchronized void terminate() {
        if (state == SandboxState.TERMINATING || state == SandboxState.TERMINATED) {
            return;
        }
        
        state = SandboxState.TERMINATING;
        
        try {
            // 触发终止事件
            lifecycle.onTerminate();
            apiBridge.callMethod("app", "onTerminate", null);
            
            // 停止JS运行时
            stopJsRuntime();
            
            // 停止资源监控
            stopResourceMonitor();
            
            // 停止事件处理器
            stopEventProcessor();
            
            // 清理临时文件
            cleanupTempFiles();
            
            state = SandboxState.TERMINATED;
            
        } catch (Exception e) {
            state = SandboxState.ERROR;
        }
    }
    
    /**
     * 处理API调用
     */
    public Object handleApiCall(String apiName, Map<String, Object> params) throws MiniProgramException {
        // 检查API权限
        if (!securityPolicy.isApiAllowed(apiName)) {
            throw new MiniProgramException("API not allowed: " + apiName, appId);
        }
        
        // 检查调用频率限制
        if (!checkRateLimit()) {
            throw new MiniProgramException("API rate limit exceeded", appId);
        }
        
        apiCallCount++;
        lastActiveTime = System.currentTimeMillis();
        
        // 转发到API桥接器
        return apiBridge.invokeApi(apiName, params);
    }
    
    /**
     * 消息通道 - 从小程序接收消息
     */
    public void onMessageFromMiniProgram(String message) {
        try {
            // 解析消息
            Map<String, Object> msg = parseMessage(message);
            String type = (String) msg.get("type");
            
            switch (type) {
                case "api_call":
                    handleApiCallMessage(msg);
                    break;
                case "event":
                    handleEventMessage(msg);
                    break;
                case "log":
                    handleLogMessage(msg);
                    break;
                case "error":
                    handleErrorMessage(msg);
                    break;
                default:
                    // 未知消息类型
            }
        } catch (Exception e) {
            // 消息处理错误
        }
    }
    
    /**
     * 发送消息到小程序
     */
    public void sendMessageToMiniProgram(String message) {
        if (jsRuntimeProcess != null && jsRuntimeProcess.isAlive()) {
            try {
                OutputStream os = jsRuntimeProcess.getOutputStream();
                os.write((message + "\n").getBytes());
                os.flush();
            } catch (IOException e) {
                // 发送失败
            }
        }
    }
    
    // ============ 私有方法 ============
    
    private String generateSandboxId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    private void createSandboxDirectoryStructure() throws IOException {
        Files.createDirectories(codeBasePath);
        Files.createDirectories(dataPath);
        Files.createDirectories(tempPath);
        
        // 创建存储目录
        Path storagePath = dataPath.resolve("storage");
        Files.createDirectories(storagePath);
        
        // 创建缓存目录
        Path cachePath = tempPath.resolve("cache");
        Files.createDirectories(cachePath);
    }
    
    private void extractCodePackage() throws IOException {
        // 从小程序包中解压代码到codeBasePath
        // 实际实现需要解压zip/wxa文件
        Path packagePath = Paths.get(manifest.getPackagePath());
        
        if (Files.exists(packagePath)) {
            // 解压逻辑
            extractZip(packagePath, codeBasePath);
        }
    }
    
    private void extractZip(Path zipPath, Path targetPath) throws IOException {
        // 使用Java的ZipFile或外部工具解压
        // 简化实现
    }
    
    private void initializeSecurity() {
        // 安装安全管理器
        this.securityManager = new MiniProgramSecurityManager(securityPolicy, sandboxRoot);
        System.setSecurityManager(securityManager);
    }
    
    private void startResourceMonitor() {
        this.resourceMonitor = new SandboxResourceMonitor(this);
        resourceMonitor.start();
    }
    
    private void stopResourceMonitor() {
        if (resourceMonitor != null) {
            resourceMonitor.stop();
        }
    }
    
    private void startEventProcessor() {
        eventExecutor.submit(this::processEvents);
    }
    
    private void stopEventProcessor() {
        eventExecutor.shutdown();
    }
    
    private void processEvents() {
        while (state != SandboxState.TERMINATED && !Thread.currentThread().isInterrupted()) {
            try {
                SandboxEvent event = eventQueue.poll(100, TimeUnit.MILLISECONDS);
                if (event != null) {
                    handleEvent(event);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private void handleEvent(SandboxEvent event) {
        // 处理各种沙箱事件
    }
    
    private MiniProgramContext buildLaunchContext(Map<String, Object> params) {
        MiniProgramContext context = new MiniProgramContext();
        context.setAppId(appId);
        context.setSandboxId(sandboxId);
        context.setLaunchPath(manifest.getEntryPath());
        context.setLaunchParams(params);
        context.setStoragePath(dataPath.toString());
        context.setTempPath(tempPath.toString());
        return context;
    }
    
    private void startJsRuntime(MiniProgramContext context) throws IOException {
        // 启动JavaScript运行时进程（如Node.js或自定义引擎）
        ProcessBuilder pb = new ProcessBuilder(
            "node", "miniprogram-runtime.js",
            "--app-id", appId,
            "--sandbox-id", sandboxId,
            "--code-path", codeBasePath.toString(),
            "--context", context.toJson()
        );
        
        pb.directory(sandboxRoot.toFile());
        pb.redirectErrorStream(true);
        
        this.jsRuntimeProcess = pb.start();
        
        // 启动输出读取线程
        startOutputReader();
    }
    
    private void startOutputReader() {
        Thread readerThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(jsRuntimeProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    onMessageFromMiniProgram(line);
                }
            } catch (IOException e) {
                // 读取结束
            }
        });
        readerThread.setDaemon(true);
        readerThread.start();
    }
    
    private void stopJsRuntime() {
        if (jsRuntimeProcess != null && jsRuntimeProcess.isAlive()) {
            jsRuntimeProcess.destroy();
            try {
                if (!jsRuntimeProcess.waitFor(5, TimeUnit.SECONDS)) {
                    jsRuntimeProcess.destroyForcibly();
                }
            } catch (InterruptedException e) {
                jsRuntimeProcess.destroyForcibly();
            }
        }
    }
    
    private void cleanupTempFiles() {
        try {
            deleteDirectory(tempPath);
        } catch (IOException e) {
            // 清理失败
        }
    }
    
    private void deleteDirectory(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.list(path).forEach(child -> {
                try {
                    deleteDirectory(child);
                } catch (IOException e) {
                    // 忽略
                }
            });
        }
        Files.deleteIfExists(path);
    }
    
    private boolean checkRateLimit() {
        // 检查API调用频率是否超过限制
        // 简化实现：每秒最多100次调用
        return apiCallCount < 10000; // 简化逻辑
    }
    
    private Map<String, Object> parseMessage(String message) {
        // JSON解析
        return new HashMap<>(); // 简化实现
    }
    
    private void handleApiCallMessage(Map<String, Object> msg) {
        // 处理API调用消息
    }
    
    private void handleEventMessage(Map<String, Object> msg) {
        // 处理事件消息
    }
    
    private void handleLogMessage(Map<String, Object> msg) {
        // 处理日志消息
    }
    
    private void handleErrorMessage(Map<String, Object> msg) {
        // 处理错误消息
    }
    
    // ============ Getter方法 ============
    
    public String getAppId() { return appId; }
    public String getSandboxId() { return sandboxId; }
    public SandboxState getState() { return state; }
    public MiniProgramManifest getManifest() { return manifest; }
    public Path getSandboxRoot() { return sandboxRoot; }
    public Path getDataPath() { return dataPath; }
    public Map<String, Object> getStorage() { return storage; }
    public long getStartTime() { return startTime; }
    public long getUptime() { return System.currentTimeMillis() - startTime; }
    public int getApiCallCount() { return apiCallCount; }
}

/**
 * 沙箱事件
 */
class SandboxEvent {
    private final String type;
    private final Map<String, Object> data;
    private final long timestamp;
    
    public SandboxEvent(String type, Map<String, Object> data) {
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getType() { return type; }
    public Map<String, Object> getData() { return data; }
    public long getTimestamp() { return timestamp; }
}

/**
 * 沙箱资源监控器
 */
class SandboxResourceMonitor {
    private final MiniProgramSandbox sandbox;
    private volatile boolean running = false;
    private Thread monitorThread;
    
    private static final long MEMORY_LIMIT = 256 * 1024 * 1024; // 256MB
    private static final long CPU_TIME_LIMIT = 60 * 1000; // 60秒CPU时间
    
    public SandboxResourceMonitor(MiniProgramSandbox sandbox) {
        this.sandbox = sandbox;
    }
    
    public void start() {
        running = true;
        monitorThread = new Thread(this::monitor);
        monitorThread.setDaemon(true);
        monitorThread.start();
    }
    
    public void stop() {
        running = false;
        if (monitorThread != null) {
            monitorThread.interrupt();
        }
    }
    
    private void monitor() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                // 检查内存使用
                checkMemoryUsage();
                
                // 检查CPU使用
                checkCpuUsage();
                
                // 检查运行时间
                checkUptime();
                
                Thread.sleep(5000); // 每5秒检查一次
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private void checkMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        
        if (usedMemory > MEMORY_LIMIT) {
            // 内存超限，触发GC或终止
            System.gc();
        }
    }
    
    private void checkCpuUsage() {
        // CPU使用检查逻辑
    }
    
    private void checkUptime() {
        // 检查小程序运行时间，长时间不活跃可以暂停
        long idleTime = System.currentTimeMillis() - sandbox.getStartTime();
        if (idleTime > 30 * 60 * 1000) { // 30分钟无活动
            // 可以考虑暂停沙箱
        }
    }
}
