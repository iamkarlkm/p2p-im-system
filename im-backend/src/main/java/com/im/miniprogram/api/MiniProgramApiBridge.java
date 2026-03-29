package com.im.miniprogram.api;

import com.im.miniprogram.sandbox.MiniProgramSandbox;
import com.im.miniprogram.exception.MiniProgramException;

import java.util.*;
import java.util.concurrent.*;

/**
 * 小程序API桥接器
 * 提供小程序与宿主环境之间的API调用接口
 */
public class MiniProgramApiBridge {
    
    private final MiniProgramSandbox sandbox;
    private final Map<String, ApiHandler> apiHandlers = new ConcurrentHashMap<>();
    private final Map<String, PendingApiCall> pendingCalls = new ConcurrentHashMap<>();
    private final ExecutorService apiExecutor = Executors.newFixedThreadPool(4);
    
    private final AtomicInteger callIdGenerator = new AtomicInteger(0);
    private volatile boolean initialized = false;
    
    // API调用统计
    private final Map<String, ApiStats> apiStats = new ConcurrentHashMap<>();
    private long totalCalls = 0;
    private long failedCalls = 0;
    
    public MiniProgramApiBridge(MiniProgramSandbox sandbox) {
        this.sandbox = sandbox;
        initializeHandlers();
    }
    
    /**
     * 初始化API处理器
     */
    private void initializeHandlers() {
        // 注册存储相关API
        registerHandler("storage.get", new StorageGetHandler());
        registerHandler("storage.set", new StorageSetHandler());
        registerHandler("storage.remove", new StorageRemoveHandler());
        registerHandler("storage.clear", new StorageClearHandler());
        
        // 注册网络相关API
        registerHandler("request.get", new RequestHandler("GET"));
        registerHandler("request.post", new RequestHandler("POST"));
        registerHandler("request.put", new RequestHandler("PUT"));
        registerHandler("request.delete", new RequestHandler("DELETE"));
        registerHandler("download", new DownloadHandler());
        registerHandler("upload", new UploadHandler());
        
        // 注册UI相关API
        registerHandler("ui.showToast", new ShowToastHandler());
        registerHandler("ui.showModal", new ShowModalHandler());
        registerHandler("ui.showLoading", new ShowLoadingHandler());
        registerHandler("ui.hideLoading", new HideLoadingHandler());
        registerHandler("ui.showActionSheet", new ShowActionSheetHandler());
        
        // 注册导航相关API
        registerHandler("navigator.navigateTo", new NavigateToHandler());
        registerHandler("navigator.redirectTo", new RedirectToHandler());
        registerHandler("navigator.navigateBack", new NavigateBackHandler());
        registerHandler("navigator.reLaunch", new ReLaunchHandler());
        registerHandler("navigator.switchTab", new SwitchTabHandler());
        
        // 注册应用相关API
        registerHandler("app.getInfo", new GetAppInfoHandler());
        registerHandler("app.getLaunchOptions", new GetLaunchOptionsHandler());
        registerHandler("app.exit", new ExitAppHandler());
        
        // 注册设备相关API
        registerHandler("device.getSystemInfo", new GetSystemInfoHandler());
        registerHandler("device.getNetworkType", new GetNetworkTypeHandler());
        registerHandler("device.makePhoneCall", new MakePhoneCallHandler());
        registerHandler("device.scanCode", new ScanCodeHandler());
        registerHandler("device.setClipboard", new SetClipboardHandler());
        registerHandler("device.getClipboard", new GetClipboardHandler());
        
        // 注册媒体相关API
        registerHandler("media.chooseImage", new ChooseImageHandler());
        registerHandler("media.previewImage", new PreviewImageHandler());
        registerHandler("media.saveImageToPhotosAlbum", new SaveImageHandler());
        registerHandler("media.chooseVideo", new ChooseVideoHandler());
        
        // 注册位置相关API
        registerHandler("location.get", new GetLocationHandler());
        registerHandler("location.choose", new ChooseLocationHandler());
        registerHandler("location.open", new OpenLocationHandler());
        
        // 注册Socket相关API
        registerHandler("socket.connect", new SocketConnectHandler());
        registerHandler("socket.send", new SocketSendHandler());
        registerHandler("socket.close", new SocketCloseHandler());
        
        initialized = true;
    }
    
    /**
     * 注册API处理器
     */
    public void registerHandler(String apiName, ApiHandler handler) {
        apiHandlers.put(apiName, handler);
    }
    
    /**
     * 调用API
     */
    public Object invokeApi(String apiName, Map<String, Object> params) throws MiniProgramException {
        if (!initialized) {
            throw new MiniProgramException("API bridge not initialized", sandbox.getAppId());
        }
        
        ApiHandler handler = apiHandlers.get(apiName);
        if (handler == null) {
            throw new MiniProgramException("API not found: " + apiName, sandbox.getAppId());
        }
        
        // 更新统计
        totalCalls++;
        apiStats.computeIfAbsent(apiName, k -> new ApiStats()).increment();
        
        try {
            Object result = handler.handle(sandbox, params);
            return result;
        } catch (Exception e) {
            failedCalls++;
            apiStats.get(apiName).incrementFailed();
            throw new MiniProgramException("API call failed: " + apiName + " - " + e.getMessage(), 
                sandbox.getAppId(), e);
        }
    }
    
    /**
     * 异步调用API
     */
    public CompletableFuture<Object> invokeApiAsync(String apiName, Map<String, Object> params) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return invokeApi(apiName, params);
            } catch (MiniProgramException e) {
                throw new CompletionException(e);
            }
        }, apiExecutor);
    }
    
    /**
     * 调用小程序方法
     */
    public void callMethod(String target, String method, Object data) {
        String callId = String.valueOf(callIdGenerator.incrementAndGet());
        
        Map<String, Object> message = new HashMap<>();
        message.put("type", "method_call");
        message.put("callId", callId);
        message.put("target", target);
        message.put("method", method);
        message.put("data", data);
        
        // 序列化并发送到小程序
        String json = toJson(message);
        sandbox.sendMessageToMiniProgram(json);
    }
    
    /**
     * 处理API调用结果
     */
    public void onApiResult(String callId, Object result, String error) {
        PendingApiCall pending = pendingCalls.remove(callId);
        if (pending != null) {
            if (error != null) {
                pending.completeExceptionally(new MiniProgramException(error, sandbox.getAppId()));
            } else {
                pending.complete(result);
            }
        }
    }
    
    /**
     * 获取API统计信息
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCalls", totalCalls);
        stats.put("failedCalls", failedCalls);
        stats.put("successRate", totalCalls > 0 ? (totalCalls - failedCalls) * 100.0 / totalCalls : 0);
        
        Map<String, Object> apiDetails = new HashMap<>();
        apiStats.forEach((api, s) -> apiDetails.put(api, s.toMap()));
        stats.put("apiDetails", apiDetails);
        
        return stats;
    }
    
    // ============ API处理器接口 ============
    
    public interface ApiHandler {
        Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) throws Exception;
    }
    
    // ============ 具体API处理器实现 ============
    
    // 存储API
    class StorageGetHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            String key = (String) params.get("key");
            Object defaultValue = params.get("default");
            return sandbox.getStorage().getOrDefault(key, defaultValue);
        }
    }
    
    class StorageSetHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            String key = (String) params.get("key");
            Object value = params.get("data");
            sandbox.getStorage().put(key, value);
            return Collections.singletonMap("success", true);
        }
    }
    
    class StorageRemoveHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            String key = (String) params.get("key");
            sandbox.getStorage().remove(key);
            return Collections.singletonMap("success", true);
        }
    }
    
    class StorageClearHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            sandbox.getStorage().clear();
            return Collections.singletonMap("success", true);
        }
    }
    
    // 网络API
    class RequestHandler implements ApiHandler {
        private final String method;
        
        RequestHandler(String method) {
            this.method = method;
        }
        
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) throws Exception {
            String url = (String) params.get("url");
            Map<String, String> headers = (Map<String, String>) params.getOrDefault("header", new HashMap<>());
            Object data = params.get("data");
            int timeout = (int) params.getOrDefault("timeout", 30000);
            
            // 执行HTTP请求
            return executeHttpRequest(url, method, headers, data, timeout);
        }
        
        private Object executeHttpRequest(String url, String method, Map<String, String> headers, 
                                         Object data, int timeout) {
            // 简化实现
            Map<String, Object> result = new HashMap<>();
            result.put("statusCode", 200);
            result.put("data", "{}");
            result.put("header", new HashMap<String, String>());
            return result;
        }
    }
    
    class DownloadHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) throws Exception {
            String url = (String) params.get("url");
            String filePath = (String) params.get("filePath");
            
            Map<String, Object> result = new HashMap<>();
            result.put("statusCode", 200);
            result.put("tempFilePath", "/temp/" + UUID.randomUUID().toString());
            return result;
        }
    }
    
    class UploadHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) throws Exception {
            String url = (String) params.get("url");
            String filePath = (String) params.get("filePath");
            String name = (String) params.get("name");
            
            Map<String, Object> result = new HashMap<>();
            result.put("statusCode", 200);
            result.put("data", "{}");
            return result;
        }
    }
    
    // UI API
    class ShowToastHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            String title = (String) params.get("title");
            String icon = (String) params.getOrDefault("icon", "success");
            int duration = (int) params.getOrDefault("duration", 1500);
            
            // 发送UI事件到前端
            Map<String, Object> event = new HashMap<>();
            event.put("type", "showToast");
            event.put("title", title);
            event.put("icon", icon);
            event.put("duration", duration);
            
            callMethod("ui", "showToast", event);
            return Collections.singletonMap("success", true);
        }
    }
    
    class ShowModalHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            String title = (String) params.get("title");
            String content = (String) params.get("content");
            
            Map<String, Object> event = new HashMap<>();
            event.put("type", "showModal");
            event.put("title", title);
            event.put("content", content);
            
            callMethod("ui", "showModal", event);
            return Collections.singletonMap("success", true);
        }
    }
    
    class ShowLoadingHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            String title = (String) params.get("title");
            
            Map<String, Object> event = new HashMap<>();
            event.put("type", "showLoading");
            event.put("title", title);
            
            callMethod("ui", "showLoading", event);
            return Collections.singletonMap("success", true);
        }
    }
    
    class HideLoadingHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            Map<String, Object> event = new HashMap<>();
            event.put("type", "hideLoading");
            
            callMethod("ui", "hideLoading", event);
            return Collections.singletonMap("success", true);
        }
    }
    
    class ShowActionSheetHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            List<String> itemList = (List<String>) params.get("itemList");
            
            Map<String, Object> event = new HashMap<>();
            event.put("type", "showActionSheet");
            event.put("itemList", itemList);
            
            callMethod("ui", "showActionSheet", event);
            return Collections.singletonMap("success", true);
        }
    }
    
    // 导航API
    class NavigateToHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            String url = (String) params.get("url");
            
            Map<String, Object> event = new HashMap<>();
            event.put("type", "navigateTo");
            event.put("url", url);
            
            callMethod("navigator", "navigateTo", event);
            return Collections.singletonMap("success", true);
        }
    }
    
    class RedirectToHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            String url = (String) params.get("url");
            
            Map<String, Object> event = new HashMap<>();
            event.put("type", "redirectTo");
            event.put("url", url);
            
            callMethod("navigator", "redirectTo", event);
            return Collections.singletonMap("success", true);
        }
    }
    
    class NavigateBackHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            int delta = (int) params.getOrDefault("delta", 1);
            
            Map<String, Object> event = new HashMap<>();
            event.put("type", "navigateBack");
            event.put("delta", delta);
            
            callMethod("navigator", "navigateBack", event);
            return Collections.singletonMap("success", true);
        }
    }
    
    class ReLaunchHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            String url = (String) params.get("url");
            
            Map<String, Object> event = new HashMap<>();
            event.put("type", "reLaunch");
            event.put("url", url);
            
            callMethod("navigator", "reLaunch", event);
            return Collections.singletonMap("success", true);
        }
    }
    
    class SwitchTabHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            String url = (String) params.get("url");
            
            Map<String, Object> event = new HashMap<>();
            event.put("type", "switchTab");
            event.put("url", url);
            
            callMethod("navigator", "switchTab", event);
            return Collections.singletonMap("success", true);
        }
    }
    
    // 应用API
    class GetAppInfoHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            Map<String, Object> info = new HashMap<>();
            info.put("appId", sandbox.getAppId());
            info.put("version", "1.0.0");
            info.put("name", "Mini Program");
            return info;
        }
    }
    
    class GetLaunchOptionsHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            Map<String, Object> options = new HashMap<>();
            options.put("path", "/index");
            options.put("query", new HashMap<>());
            options.put("scene", 1001);
            return options;
        }
    }
    
    class ExitAppHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            sandbox.terminate();
            return Collections.singletonMap("success", true);
        }
    }
    
    // 设备API（简化实现）
    class GetSystemInfoHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            Map<String, Object> info = new HashMap<>();
            info.put("model", "Desktop");
            info.put("system", "Windows");
            info.put("version", "10");
            info.put("platform", "desktop");
            return info;
        }
    }
    
    class GetNetworkTypeHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            return Collections.singletonMap("networkType", "wifi");
        }
    }
    
    class MakePhoneCallHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            String phoneNumber = (String) params.get("phoneNumber");
            // 实际实现需要调用系统API
            return Collections.singletonMap("success", true);
        }
    }
    
    class ScanCodeHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            // 简化实现
            Map<String, Object> result = new HashMap<>();
            result.put("result", "");
            result.put("scanType", "QR_CODE");
            return result;
        }
    }
    
    class SetClipboardHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            String data = (String) params.get("data");
            // 设置剪贴板
            return Collections.singletonMap("success", true);
        }
    }
    
    class GetClipboardHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            // 获取剪贴板
            return Collections.singletonMap("data", "");
        }
    }
    
    // 媒体API（简化实现）
    class ChooseImageHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            int count = (int) params.getOrDefault("count", 9);
            Map<String, Object> result = new HashMap<>();
            result.put("tempFilePaths", new ArrayList<>());
            return result;
        }
    }
    
    class PreviewImageHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            List<String> urls = (List<String>) params.get("urls");
            int current = (int) params.getOrDefault("current", 0);
            // 预览图片
            return Collections.singletonMap("success", true);
        }
    }
    
    class SaveImageHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            String filePath = (String) params.get("filePath");
            // 保存图片
            return Collections.singletonMap("success", true);
        }
    }
    
    class ChooseVideoHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            Map<String, Object> result = new HashMap<>();
            result.put("tempFilePath", "");
            result.put("duration", 0);
            result.put("size", 0);
            result.put("height", 0);
            result.put("width", 0);
            return result;
        }
    }
    
    // 位置API（简化实现）
    class GetLocationHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            Map<String, Object> location = new HashMap<>();
            location.put("latitude", 39.9042);
            location.put("longitude", 116.4074);
            location.put("speed", 0);
            location.put("accuracy", 0);
            location.put("altitude", 0);
            location.put("verticalAccuracy", 0);
            location.put("horizontalAccuracy", 0);
            return location;
        }
    }
    
    class ChooseLocationHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            Map<String, Object> location = new HashMap<>();
            location.put("name", "");
            location.put("address", "");
            location.put("latitude", 0);
            location.put("longitude", 0);
            return location;
        }
    }
    
    class OpenLocationHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            double latitude = ((Number) params.get("latitude")).doubleValue();
            double longitude = ((Number) params.get("longitude")).doubleValue();
            String name = (String) params.get("name");
            String address = (String) params.get("address");
            // 打开地图
            return Collections.singletonMap("success", true);
        }
    }
    
    // Socket API（简化实现）
    class SocketConnectHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            String url = (String) params.get("url");
            Map<String, String> protocols = (Map<String, String>) params.get("protocols");
            // 建立WebSocket连接
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("socketTaskId", UUID.randomUUID().toString());
            return result;
        }
    }
    
    class SocketSendHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            String socketTaskId = (String) params.get("socketTaskId");
            Object data = params.get("data");
            // 发送WebSocket消息
            return Collections.singletonMap("success", true);
        }
    }
    
    class SocketCloseHandler implements ApiHandler {
        public Object handle(MiniProgramSandbox sandbox, Map<String, Object> params) {
            String socketTaskId = (String) params.get("socketTaskId");
            int code = (int) params.getOrDefault("code", 1000);
            String reason = (String) params.get("reason");
            // 关闭WebSocket连接
            return Collections.singletonMap("success", true);
        }
    }
    
    // ============ 辅助类 ============
    
    private class PendingApiCall extends CompletableFuture<Object> {
        private final String callId;
        private final long createTime;
        
        PendingApiCall(String callId) {
            this.callId = callId;
            this.createTime = System.currentTimeMillis();
        }
        
        public String getCallId() { return callId; }
        public long getCreateTime() { return createTime; }
    }
    
    private static class ApiStats {
        private long count = 0;
        private long failedCount = 0;
        private long totalTime = 0;
        
        synchronized void increment() {
            count++;
        }
        
        synchronized void incrementFailed() {
            failedCount++;
        }
        
        synchronized void addTime(long time) {
            totalTime += time;
        }
        
        Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("count", count);
            map.put("failedCount", failedCount);
            map.put("avgTime", count > 0 ? totalTime / count : 0);
            return map;
        }
    }
    
    private String toJson(Map<String, Object> map) {
        // 简化JSON序列化
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else {
                sb.append(value);
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
