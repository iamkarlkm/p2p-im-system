package com.im.server.netty.dto;

import java.io.Serializable;

/**
 * WebSocket消息DTO
 */
public class WsMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 消息类型: auth, auth_ack, message, presence, ack, error
     */
    private String type;
    
    /**
     * 发送者用户ID
     */
    private String from;
    
    /**
     * 接收者用户ID或群ID
     */
    private String to;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 时间戳
     */
    private Long timestamp;
    
    /**
     * 消息唯一ID
     */
    private String msgId;
    
    /**
     * 扩展字段
     */
    private String ext;
    
    // Getters and Setters
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getFrom() {
        return from;
    }
    
    public void setFrom(String from) {
        this.from = from;
    }
    
    public String getTo() {
        return to;
    }
    
    public void setTo(String to) {
        this.to = to;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getMsgId() {
        return msgId;
    }
    
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
    
    public String getExt() {
        return ext;
    }
    
    public void setExt(String ext) {
        this.ext = ext;
    }
    
    @Override
    public String toString() {
        return "WsMessage{" +
                "type='" + type + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", msgId='" + msgId + '\'' +
                '}';
    }
}
