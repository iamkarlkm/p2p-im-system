package com.im.mobile.client;

import android.util.Log;

import com.im.mobile.callback.IMCallback;
import com.im.mobile.model.Message;
import com.im.mobile.model.MessageStatus;
import com.im.mobile.websocket.WebSocketClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 消息客户端 - 功能#9: 基础IM客户端SDK
 * 负责消息的发送、接收和管理
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public class MessageClient {
    
    private static final String TAG = "MessageClient";
    
    private final IMClient imClient;
    private final ExecutorService sendExecutor;
    private final ScheduledExecutorService retryExecutor;
    
    private static final int MAX_RETRY_COUNT = 3;
    private static final long RETRY_DELAY_MS = 3000;
    
    public MessageClient(IMClient imClient) {
        this.imClient = imClient;
        this.sendExecutor = Executors.newSingleThreadExecutor();
        this.retryExecutor = Executors.newScheduledThreadPool(1);
    }
    
    /**
     * 发送文本消息
     * 
     * @param conversationId 会话ID
     * @param content 消息内容
     * @param callback 发送回调
     */
    public void sendTextMessage(String conversationId, String content, IMCallback callback) {
        Message message = Message.createTextMessage(conversationId, content);
        sendMessage(message, callback);
    }
    
    /**
     * 发送图片消息
     * 
     * @param conversationId 会话ID
     * @param imagePath 图片路径
     * @param callback 发送回调
     */
    public void sendImageMessage(String conversationId, String imagePath, IMCallback callback) {
        Message message = Message.createImageMessage(conversationId, imagePath);
        sendMessage(message, callback);
    }
    
    /**
     * 发送语音消息
     * 
     * @param conversationId 会话ID
     * @param audioPath 音频路径
     * @param duration 时长(秒)
     * @param callback 发送回调
     */
    public void sendVoiceMessage(String conversationId, String audioPath, int duration, IMCallback callback) {
        Message message = Message.createVoiceMessage(conversationId, audioPath, duration);
        sendMessage(message, callback);
    }
    
    /**
     * 发送消息
     * 
     * @param message 消息对象
     * @param callback 发送回调
     */
    public void sendMessage(Message message, IMCallback callback) {
        sendExecutor.execute(() -> {
            try {
                // 更新消息状态为发送中
                message.setStatus(MessageStatus.SENDING);
                
                // 构建消息JSON
                JSONObject messageJson = buildMessageJson(message);
                
                // 发送消息
                WebSocketClient wsClient = imClient.getConnectionManager().getWebSocketClient();
                if (wsClient != null && wsClient.isConnected()) {
                    wsClient.send(messageJson.toString());
                    
                    // 更新状态为已发送
                    message.setStatus(MessageStatus.SENT);
                    
                    if (callback != null) {
                        callback.onSuccess();
                    }
                    
                    Log.d(TAG, "Message sent successfully: " + message.getMessageId());
                } else {
                    throw new IllegalStateException("WebSocket not connected");
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to send message", e);
                message.setStatus(MessageStatus.FAILED);
                
                if (callback != null) {
                    callback.onError(-1, e.getMessage());
                }
                
                // 重试机制
                retrySendMessage(message, callback, 1);
            }
        });
    }
    
    /**
     * 重试发送消息
     */
    private void retrySendMessage(Message message, IMCallback callback, int retryCount) {
        if (retryCount >= MAX_RETRY_COUNT) {
            Log.w(TAG, "Message send failed after " + MAX_RETRY_COUNT + " retries");
            return;
        }
        
        retryExecutor.schedule(() -> {
            Log.d(TAG, "Retrying send message, attempt " + (retryCount + 1));
            sendMessage(message, callback);
        }, RETRY_DELAY_MS * retryCount, TimeUnit.MILLISECONDS);
    }
    
    /**
     * 接收消息
     * 
     * @param messageJson 消息JSON字符串
     */
    public void receiveMessage(String messageJson) {
        try {
            JSONObject json = new JSONObject(messageJson);
            Message message = parseMessage(json);
            
            if (message != null) {
                // 分发消息
                imClient.dispatchMessage(message);
                
                // 发送已读回执
                sendReadReceipt(message.getMessageId());
            }
            
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse message", e);
        }
    }
    
    /**
     * 发送已读回执
     */
    private void sendReadReceipt(String messageId) {
        try {
            JSONObject receipt = new JSONObject();
            receipt.put("type", "read_receipt");
            receipt.put("messageId", messageId);
            receipt.put("timestamp", System.currentTimeMillis());
            
            WebSocketClient wsClient = imClient.getConnectionManager().getWebSocketClient();
            if (wsClient != null && wsClient.isConnected()) {
                wsClient.send(receipt.toString());
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to send read receipt", e);
        }
    }
    
    /**
     * 撤回消息
     * 
     * @param messageId 消息ID
     * @param callback 撤回回调
     */
    public void recallMessage(String messageId, IMCallback callback) {
        sendExecutor.execute(() -> {
            try {
                JSONObject recallJson = new JSONObject();
                recallJson.put("type", "recall");
                recallJson.put("messageId", messageId);
                recallJson.put("timestamp", System.currentTimeMillis());
                
                WebSocketClient wsClient = imClient.getConnectionManager().getWebSocketClient();
                if (wsClient != null && wsClient.isConnected()) {
                    wsClient.send(recallJson.toString());
                    
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } else {
                    throw new IllegalStateException("WebSocket not connected");
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to recall message", e);
                if (callback != null) {
                    callback.onError(-1, e.getMessage());
                }
            }
        });
    }
    
    /**
     * 构建消息JSON
     */
    private JSONObject buildMessageJson(Message message) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("messageId", message.getMessageId());
        json.put("conversationId", message.getConversationId());
        json.put("senderId", message.getSenderId());
        json.put("type", message.getType());
        json.put("content", message.getContent());
        json.put("timestamp", message.getTimestamp());
        json.put("status", message.getStatus().ordinal());
        
        if (message.getExtra() != null) {
            json.put("extra", new JSONObject(message.getExtra()));
        }
        
        return json;
    }
    
    /**
     * 解析消息
     */
    private Message parseMessage(JSONObject json) throws JSONException {
        Message message = new Message();
        message.setMessageId(json.optString("messageId"));
        message.setConversationId(json.optString("conversationId"));
        message.setSenderId(json.optString("senderId"));
        message.setType(json.optInt("type"));
        message.setContent(json.optString("content"));
        message.setTimestamp(json.optLong("timestamp"));
        message.setStatus(MessageStatus.fromOrdinal(json.optInt("status", 0)));
        
        return message;
    }
    
    /**
     * 释放资源
     */
    public void release() {
        sendExecutor.shutdown();
        retryExecutor.shutdown();
    }
}
