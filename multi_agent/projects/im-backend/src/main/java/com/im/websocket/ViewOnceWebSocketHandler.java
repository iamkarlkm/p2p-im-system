package com.im.websocket;

import com.im.service.ViewOnceMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.HashMap;
import java.util.Map;

/**
 * 一次性媒体消息 WebSocket 处理器
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ViewOnceWebSocketHandler {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final ViewOnceMessageService viewOnceService;
    
    /**
     * 用户请求查看一次性媒体
     */
    @MessageMapping("/view-once/{messageId}/view")
    @SendTo("/topic/view-once/{messageId}/status")
    public Map<String, Object> requestView(
            @DestinationVariable String messageId,
            @Payload Map<String, Object> payload,
            SimpMessageHeaderAccessor headerAccessor) {
        
        String receiverId = (String) payload.get("receiverId");
        String deviceId = (String) payload.get("deviceId");
        String ip = (String) payload.getOrDefault("ip", "unknown");
        
        log.info("ViewOnce view request: messageId={}, receiverId={}, deviceId={}", 
                messageId, receiverId, deviceId);
        
        boolean success = viewOnceService.markAsViewed(messageId, receiverId, ip, deviceId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "VIEW_STATUS");
        response.put("messageId", messageId);
        response.put("viewed", success);
        response.put("timestamp", System.currentTimeMillis());
        
        // 如果查看成功，通知发送者
        if (success) {
            notifySenderAboutView(messageId, receiverId);
        }
        
        return response;
    }
    
    /**
     * 用户报告截图
     */
    @MessageMapping("/view-once/{messageId}/screenshot")
    @SendTo("/topic/view-once/{messageId}/screenshot")
    public Map<String, Object> reportScreenshot(
            @DestinationVariable String messageId,
            @Payload Map<String, Object> payload) {
        
        String reporterId = (String) payload.get("userId");
        String timestamp = (String) payload.get("timestamp");
        String details = (String) payload.get("details");
        
        log.info("Screenshot reported: messageId={}, reporterId={}", messageId, reporterId);
        
        boolean recorded = viewOnceService.recordScreenshot(messageId, timestamp, details);
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "SCREENSHOT_RECORDED");
        response.put("messageId", messageId);
        response.put("recorded", recorded);
        response.put("timestamp", System.currentTimeMillis());
        
        // 通知发送者截图被检测到
        if (recorded) {
            notifySenderAboutScreenshot(messageId, reporterId);
        }
        
        return response;
    }
    
    /**
     * 用户销毁一次性媒体
     */
    @MessageMapping("/view-once/{messageId}/destroy")
    @SendTo("/topic/view-once/{messageId}/status")
    public Map<String, Object> destroyMessage(
            @DestinationVariable String messageId,
            @Payload Map<String, Object> payload) {
        
        String reason = (String) payload.getOrDefault("reason", "MANUAL");
        
        log.info("ViewOnce destroy request: messageId={}, reason={}", messageId, reason);
        
        boolean success = viewOnceService.destroyMessage(messageId, reason);
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "DESTROY_STATUS");
        response.put("messageId", messageId);
        response.put("destroyed", success);
        response.put("timestamp", System.currentTimeMillis());
        
        return response;
    }
    
    /**
     * 获取一次性媒体状态
     */
    @MessageMapping("/view-once/{messageId}/status")
    @SendTo("/topic/view-once/{messageId}/status")
    public Map<String, Object> getStatus(
            @DestinationVariable String messageId) {
        
        boolean viewed = viewOnceService.isMessageViewed(messageId);
        boolean active = viewOnceService.isMessageActive(messageId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "STATUS_UPDATE");
        response.put("messageId", messageId);
        response.put("viewed", viewed);
        response.put("active", active);
        response.put("timestamp", System.currentTimeMillis());
        
        return response;
    }
    
    /**
     * 通知发送者消息已被查看
     */
    private void notifySenderAboutView(String messageId, String receiverId) {
        viewOnceService.getViewOnceMessage(messageId, null).ifPresent(entity -> {
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "VIEW_ONCE_VIEWED");
            notification.put("messageId", messageId);
            notification.put("viewedBy", receiverId);
            notification.put("viewedAt", System.currentTimeMillis());
            
            // 发送到发送者的个人通知频道
            messagingTemplate.convertAndSend(
                    "/user/" + entity.getSenderId() + "/queue/notifications",
                    notification);
        });
    }
    
    /**
     * 通知发送者截图被检测到
     */
    private void notifySenderAboutScreenshot(String messageId, String reporterId) {
        viewOnceService.getViewOnceMessage(messageId, null).ifPresent(entity -> {
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "VIEW_ONCE_SCREENSHOT");
            notification.put("messageId", messageId);
            notification.put("screenshotBy", reporterId);
            notification.put("timestamp", System.currentTimeMillis());
            
            // 发送到发送者的个人通知频道
            messagingTemplate.convertAndSend(
                    "/user/" + entity.getSenderId() + "/queue/notifications",
                    notification);
        });
    }
}
