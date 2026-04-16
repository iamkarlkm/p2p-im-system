package com.im.mobile.client;

import android.content.Context;
import android.util.Log;

import com.im.mobile.callback.IMCallback;
import com.im.mobile.config.IMConfig;
import com.im.mobile.listener.MessageListener;
import com.im.mobile.model.Message;
import com.im.mobile.model.User;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * IM客户端核心类 - 功能#9: 基础IM客户端SDK
 * 提供IM系统的核心功能入口
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public class IMClient {
    
    private static final String TAG = "IMClient";
    private static IMClient instance;
    
    private Context context;
    private IMConfig config;
    private ConnectionManager connectionManager;
    private MessageClient messageClient;
    private User currentUser;
    
    private final AtomicBoolean isInitialized = new AtomicBoolean(false);
    private final ConcurrentHashMap<String, MessageListener> messageListeners = new ConcurrentHashMap<>();
    
    private IMClient() {
    }
    
    /**
     * 获取IMClient单例实例
     */
    public static synchronized IMClient getInstance() {
        if (instance == null) {
            instance = new IMClient();
        }
        return instance;
    }
    
    /**
     * 初始化IM客户端
     * 
     * @param context 应用上下文
     * @param config IM配置
     */
    public void initialize(Context context, IMConfig config) {
        if (isInitialized.get()) {
            Log.w(TAG, "IMClient already initialized");
            return;
        }
        
        this.context = context.getApplicationContext();
        this.config = config;
        
        // 初始化连接管理器
        this.connectionManager = new ConnectionManager(context, config);
        
        // 初始化消息客户端
        this.messageClient = new MessageClient(this);
        
        isInitialized.set(true);
        Log.i(TAG, "IMClient initialized successfully");
    }
    
    /**
     * 连接IM服务器
     * 
     * @param token 用户认证token
     * @param callback 连接回调
     */
    public void connect(String token, IMCallback callback) {
        checkInitialized();
        connectionManager.connect(token, callback);
    }
    
    /**
     * 断开连接
     */
    public void disconnect() {
        if (connectionManager != null) {
            connectionManager.disconnect();
        }
    }
    
    /**
     * 重新连接
     */
    public void reconnect() {
        checkInitialized();
        connectionManager.reconnect();
    }
    
    /**
     * 登录
     * 
     * @param userId 用户ID
     * @param token 认证token
     * @param callback 登录回调
     */
    public void login(String userId, String token, IMCallback callback) {
        checkInitialized();
        
        currentUser = new User(userId);
        connectionManager.connect(token, new IMCallback() {
            @Override
            public void onSuccess() {
                if (callback != null) {
                    callback.onSuccess();
                }
            }
            
            @Override
            public void onError(int code, String message) {
                if (callback != null) {
                    callback.onError(code, message);
                }
            }
            
            @Override
            public void onProgress(int progress) {
                if (callback != null) {
                    callback.onProgress(progress);
                }
            }
        });
    }
    
    /**
     * 登出
     */
    public void logout() {
        disconnect();
        currentUser = null;
        messageListeners.clear();
    }
    
    /**
     * 发送消息
     * 
     * @param message 消息对象
     * @param callback 发送回调
     */
    public void sendMessage(Message message, IMCallback callback) {
        checkInitialized();
        messageClient.sendMessage(message, callback);
    }
    
    /**
     * 添加消息监听器
     * 
     * @param listener 消息监听器
     */
    public void addMessageListener(MessageListener listener) {
        if (listener != null) {
            messageListeners.put(listener.getId(), listener);
        }
    }
    
    /**
     * 移除消息监听器
     * 
     * @param listener 消息监听器
     */
    public void removeMessageListener(MessageListener listener) {
        if (listener != null) {
            messageListeners.remove(listener.getId());
        }
    }
    
    /**
     * 分发接收到的消息
     * 
     * @param message 消息对象
     */
    public void dispatchMessage(Message message) {
        for (MessageListener listener : messageListeners.values()) {
            try {
                listener.onMessageReceived(message);
            } catch (Exception e) {
                Log.e(TAG, "Error dispatching message to listener", e);
            }
        }
    }
    
    /**
     * 检查是否已初始化
     */
    private void checkInitialized() {
        if (!isInitialized.get()) {
            throw new IllegalStateException("IMClient not initialized. Call initialize() first.");
        }
    }
    
    // Getters
    public Context getContext() {
        return context;
    }
    
    public IMConfig getConfig() {
        return config;
    }
    
    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }
    
    public MessageClient getMessageClient() {
        return messageClient;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isConnected() {
        return connectionManager != null && connectionManager.isConnected();
    }
    
    public boolean isInitialized() {
        return isInitialized.get();
    }
}
