package com.im.mobile.client;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.im.mobile.callback.IMCallback;
import com.im.mobile.config.IMConfig;
import com.im.mobile.websocket.WebSocketClient;
import com.im.mobile.websocket.WebSocketListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 连接管理器 - 功能#9: 基础IM客户端SDK
 * 负责WebSocket连接的生命周期管理
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public class ConnectionManager {
    
    private static final String TAG = "ConnectionManager";
    
    private static final int CONNECT_TIMEOUT_MS = 10000;
    private static final int HEARTBEAT_INTERVAL_MS = 30000;
    private static final int RECONNECT_DELAY_MS = 5000;
    private static final int MAX_RECONNECT_ATTEMPTS = 10;
    
    private final Context context;
    private final IMConfig config;
    private final Handler mainHandler;
    private final ExecutorService executor;
    private final ScheduledExecutorService heartbeatExecutor;
    
    private WebSocketClient webSocketClient;
    private IMConnectionListener connectionListener;
    
    private final AtomicBoolean isConnected = new AtomicBoolean(false);
    private final AtomicBoolean isConnecting = new AtomicBoolean(false);
    private final AtomicBoolean shouldReconnect = new AtomicBoolean(true);
    private final AtomicInteger reconnectAttempts = new AtomicInteger(0);
    
    private String currentToken;
    
    public ConnectionManager(Context context, IMConfig config) {
        this.context = context.getApplicationContext();
        this.config = config;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.executor = Executors.newSingleThreadExecutor();
        this.heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
        
        // 注册网络状态监听
        registerNetworkCallback();
    }
    
    /**
     * 连接IM服务器
     * 
     * @param token 用户认证token
     * @param callback 连接回调
     */
    public void connect(String token, IMCallback callback) {
        if (isConnected.get()) {
            Log.w(TAG, "Already connected");
            if (callback != null) {
                callback.onSuccess();
            }
            return;
        }
        
        if (isConnecting.get()) {
            Log.w(TAG, "Connection in progress");
            return;
        }
        
        this.currentToken = token;
        this.shouldReconnect.set(true);
        
        executor.execute(() -> performConnect(token, callback));
    }
    
    /**
     * 执行连接
     */
    private void performConnect(String token, IMCallback callback) {
        isConnecting.set(true);
        
        try {
            String wsUrl = buildWebSocketUrl(token);
            
            webSocketClient = new WebSocketClient(wsUrl, new WebSocketListener() {
                @Override
                public void onConnected() {
                    isConnected.set(true);
                    isConnecting.set(false);
                    reconnectAttempts.set(0);
                    
                    // 启动心跳
                    startHeartbeat();
                    
                    // 回调
                    if (callback != null) {
                        mainHandler.post(callback::onSuccess);
                    }
                    
                    if (connectionListener != null) {
                        mainHandler.post(connectionListener::onConnected);
                    }
                    
                    Log.i(TAG, "WebSocket connected");
                }
                
                @Override
                public void onMessage(String message) {
                    handleMessage(message);
                }
                
                @Override
                public void onDisconnected(int code, String reason) {
                    handleDisconnect(code, reason);
                }
                
                @Override
                public void onError(Exception e) {
                    isConnecting.set(false);
                    
                    if (callback != null) {
                        mainHandler.post(() -> callback.onError(-1, e.getMessage()));
                    }
                    
                    if (connectionListener != null) {
                        mainHandler.post(() -> connectionListener.onConnectionError(e.getMessage()));
                    }
                    
                    Log.e(TAG, "WebSocket error", e);
                    
                    // 尝试重连
                    scheduleReconnect();
                }
            });
            
            webSocketClient.connect();
            
        } catch (Exception e) {
            isConnecting.set(false);
            Log.e(TAG, "Failed to connect", e);
            
            if (callback != null) {
                mainHandler.post(() -> callback.onError(-1, e.getMessage()));
            }
            
            scheduleReconnect();
        }
    }
    
    /**
     * 断开连接
     */
    public void disconnect() {
        shouldReconnect.set(false);
        stopHeartbeat();
        
        if (webSocketClient != null) {
            webSocketClient.close();
            webSocketClient = null;
        }
        
        isConnected.set(false);
        isConnecting.set(false);
        
        Log.i(TAG, "Disconnected");
    }
    
    /**
     * 重新连接
     */
    public void reconnect() {
        disconnect();
        
        if (currentToken != null) {
            connect(currentToken, null);
        }
    }
    
    /**
     * 处理断开连接
     */
    private void handleDisconnect(int code, String reason) {
        boolean wasConnected = isConnected.getAndSet(false);
        isConnecting.set(false);
        stopHeartbeat();
        
        if (connectionListener != null && wasConnected) {
            mainHandler.post(() -> connectionListener.onDisconnected(code, reason));
        }
        
        Log.w(TAG, "WebSocket disconnected: " + code + " - " + reason);
        
        // 尝试重连
        if (shouldReconnect.get()) {
            scheduleReconnect();
        }
    }
    
    /**
     * 处理收到的消息
     */
    private void handleMessage(String message) {
        Log.d(TAG, "Received message: " + message);
        
        // 交给IMClient处理
        IMClient imClient = IMClient.getInstance();
        if (imClient.getMessageClient() != null) {
            imClient.getMessageClient().receiveMessage(message);
        }
        
        if (connectionListener != null) {
            mainHandler.post(() -> connectionListener.onMessageReceived(message));
        }
    }
    
    /**
     * 调度重连
     */
    private void scheduleReconnect() {
        if (!shouldReconnect.get()) {
            return;
        }
        
        int attempts = reconnectAttempts.incrementAndGet();
        if (attempts > MAX_RECONNECT_ATTEMPTS) {
            Log.e(TAG, "Max reconnect attempts reached");
            if (connectionListener != null) {
                mainHandler.post(() -> connectionListener.onConnectionFailed("Max reconnect attempts reached"));
            }
            return;
        }
        
        long delay = Math.min(RECONNECT_DELAY_MS * attempts, 60000); // 最大60秒
        
        Log.d(TAG, "Scheduling reconnect in " + delay + "ms (attempt " + attempts + ")");
        
        mainHandler.postDelayed(() -> {
            if (shouldReconnect.get() && !isConnected.get() && !isConnecting.get()) {
                reconnect();
            }
        }, delay);
    }
    
    /**
     * 启动心跳
     */
    private void startHeartbeat() {
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            if (isConnected.get() && webSocketClient != null) {
                try {
                    webSocketClient.sendPing();
                } catch (Exception e) {
                    Log.e(TAG, "Heartbeat failed", e);
                }
            }
        }, HEARTBEAT_INTERVAL_MS, HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }
    
    /**
     * 停止心跳
     */
    private void stopHeartbeat() {
        heartbeatExecutor.shutdownNow();
    }
    
    /**
     * 构建WebSocket URL
     */
    private String buildWebSocketUrl(String token) {
        return config.getWebSocketUrl() + "?token=" + token + "&device=android";
    }
    
    /**
     * 注册网络状态监听
     */
    private void registerNetworkCallback() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            
            cm.registerNetworkCallback(request, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    Log.d(TAG, "Network available");
                    if (shouldReconnect.get() && !isConnected.get() && !isConnecting.get()) {
                        reconnect();
                    }
                }
                
                @Override
                public void onLost(@NonNull Network network) {
                    Log.d(TAG, "Network lost");
                }
            });
        }
    }
    
    /**
     * 设置连接监听器
     */
    public void setConnectionListener(IMConnectionListener listener) {
        this.connectionListener = listener;
    }
    
    // Getters
    public boolean isConnected() {
        return isConnected.get();
    }
    
    public boolean isConnecting() {
        return isConnecting.get();
    }
    
    public WebSocketClient getWebSocketClient() {
        return webSocketClient;
    }
    
    /**
     * 连接监听器接口
     */
    public interface IMConnectionListener {
        void onConnected();
        void onDisconnected(int code, String reason);
        void onConnectionError(String error);
        void onConnectionFailed(String reason);
        void onMessageReceived(String message);
    }
}
