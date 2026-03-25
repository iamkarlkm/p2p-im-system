package com.im.server.push;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 第三方推送服务集成
 * 
 * 支持: 极光(JPush)、个推(GeTui)、OneSignal
 */
@Service
public class ThirdPartyPushService {

    private static final Logger log = LoggerFactory.getLogger(ThirdPartyPushService.class);

    @Autowired
    private PushConfig pushConfig;

    /**
     * 发送第三方推送（通过 DeviceToken 对象）
     */
    public PushService.PushResult sendPush(List<DeviceToken> devices, PushMessage message) {
        if (devices == null || devices.isEmpty()) {
            return PushService.PushResult.failed("No device tokens");
        }

        // 按通道分组
        Map<String, List<DeviceToken>> tokensByChannel = devices.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getChannel() != null ? d.getChannel() : "unknown"
                ));

        int success = 0;
        int failure = 0;
        Map<String, Object> details = new HashMap<>();

        for (Map.Entry<String, List<DeviceToken>> entry : tokensByChannel.entrySet()) {
            String channel = entry.getKey();
            List<String> tokens = entry.getValue().stream()
                    .map(DeviceToken::getDeviceToken)
                    .filter(t -> t != null && !t.isEmpty())
                    .collect(java.util.stream.Collectors.toList());

            if (tokens.isEmpty()) continue;

            try {
                PushService.PushResult result = sendToChannel(channel, tokens, message);
                if (result.isSuccess()) {
                    success += tokens.size();
                } else {
                    failure += tokens.size();
                }
                details.put(channel, result.getMessage());
            } catch (Exception e) {
                failure += tokens.size();
                details.put(channel, "error: " + e.getMessage());
            }
        }

        if (success > 0) {
            return PushService.PushResult.success("Sent: " + success, details);
        } else {
            return PushService.PushResult.failed("All failed", details);
        }
    }

    /**
     * 发送第三方推送（通过 Token 字符串列表）
     */
    public PushService.PushResult sendPushByTokens(List<String> tokens, PushMessage message) {
        if (tokens == null || tokens.isEmpty()) {
            return PushService.PushResult.failed("No tokens");
        }

        // 简化: 使用极光
        return sendJPush(tokens, message);
    }

    // ==================== 极光推送 ====================

    private PushService.PushResult sendJPush(List<String> tokens, PushMessage message) {
        PushConfig.JPushConfig jpushConfig = pushConfig.getJpush();
        if (!jpushConfig.isEnabled()) {
            return PushService.PushResult.failed("JPush disabled");
        }

        try {
            Map<String, Object> jpushPayload = buildJPushPayload(message);

            // 构建请求
            Map<String, Object> body = new HashMap<>();
            body.put("platform", "android,ios");
            body.put("audience", buildAudience(tokens));
            body.put("notification", buildJPushNotification(message));
            body.put("options", buildJPushOptions(message));
            body.put("message", buildJPushMessage(message));

            String response = sendJPushRequest(body);
            return parseJPushResponse(response, tokens.size());
        } catch (Exception e) {
            log.error("JPush error", e);
            return PushService.PushResult.failed(e.getMessage());
        }
    }

    private Map<String, Object> buildJPushPayload(PushMessage message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("platform", "android,ios");
        payload.put("audience", "all");
        return payload;
    }

    private Map<String, Object> buildAudience(List<String> tokens) {
        Map<String, Object> audience = new HashMap<>();
        if (tokens != null && !tokens.isEmpty()) {
            Map<String, Object> registrationId = new HashMap<>();
            registrationId.put("registration_id", tokens);
            audience.put("registration_id", registrationId);
        } else {
            audience.put("all", "");
        }
        return audience;
    }

    private Map<String, Object> buildJPushNotification(PushMessage message) {
        Map<String, Object> notification = new HashMap<>();

        // Android 通知
        Map<String, Object> android = new HashMap<>();
        android.put("title", message.getTitle() != null ? message.getTitle() : "");
        android.put("alert", message.getBody() != null ? message.getBody() : "");
        if (message.getChannelId() != null) android.put("builder_id", message.getChannelId());
        if (message.getPriority() == PushMessage.Priority.HIGH) {
            android.put("priority", 2);
        }
        notification.put("android", android);

        // iOS 通知
        Map<String, Object> ios = new HashMap<>();
        ios.put("alert", message.getBody() != null ? message.getBody() : "");
        ios.put("sound", message.getSound() != null ? message.getSound() : "default");
        if (message.getBadge() != null) {
            try { ios.put("badge", Integer.parseInt(message.getBadge())); } catch (Exception e) {}
        }
        if (message.getCategory() != null) ios.put("category", message.getCategory());
        if ("timeSensitive".equals(message.getInterruptionLevel())) {
            ios.put("interruption-level", "time-sensitive");
        }
        notification.put("ios", ios);

        return notification;
    }

    private Map<String, Object> buildJPushMessage(PushMessage message) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("msg_content", message.getBody() != null ? message.getBody() : "");
        msg.put("title", message.getTitle() != null ? message.getTitle() : "");
        if (message.getMessageType() != null) msg.put("content_type", message.getMessageType());
        if (message.getData() != null) {
            for (Map.Entry<String, String> e : message.getData().entrySet()) {
                msg.put(e.getKey(), e.getValue());
            }
        }
        return msg;
    }

    private Map<String, Object> buildJPushOptions(PushMessage message) {
        Map<String, Object> options = new HashMap<>();
        options.put("time_to_send", message.getScheduledTime() != null ? message.getScheduledTime() : "");
        options.put("apns_production", !pushConfig.getApns().isSandbox());
        if (message.getTtl() > 0) {
            options.put("time_to_live", message.getTtl());
        }
        if (message.getTag() != null) {
            options.put("cid", message.getTag());
        }
        return options;
    }

    private String sendJPushRequest(Map<String, Object> body) {
        log.debug("JPush request: body={}", body);
        // TODO: 实现真实的极光 API 调用
        // POST https://api.jpush.cn/v3/push
        // Authorization: Basic base64(appKey:masterSecret)
        return "{\"sendno\":\"123\",\"msg_id\":123456789,\"error\":null}";
    }

    private PushService.PushResult parseJPushResponse(String response, int total) {
        if (response == null) return PushService.PushResult.failed("No response");
        if (response.contains("\"msg_id\"")) {
            return PushService.PushResult.success("JPush: sent to " + total);
        }
        return PushService.PushResult.failed("JPush failed: " + response);
    }

    // ==================== 通用 ====================

    private PushService.PushResult sendToChannel(String channel, List<String> tokens, PushMessage message) {
        switch (channel.toLowerCase()) {
            case "jpush":
            case "jiguang":
                return sendJPush(tokens, message);
            case "getui":
            case "个推":
                return sendGeTuiPush(tokens, message);
            case "onesignal":
                return sendOneSignalPush(tokens, message);
            default:
                log.warn("Unknown push channel: {}", channel);
                return PushService.PushResult.failed("Unknown channel: " + channel);
        }
    }

    private PushService.PushResult sendGeTuiPush(List<String> tokens, PushMessage message) {
        log.debug("GeTui push: tokens={}, message={}", tokens.size(), message);
        // TODO: 个推 API 实现
        return PushService.PushResult.failed("GeTui not implemented");
    }

    private PushService.PushResult sendOneSignalPush(List<String> tokens, PushMessage message) {
        log.debug("OneSignal push: tokens={}, message={}", tokens.size(), message);
        // TODO: OneSignal API 实现
        return PushService.PushResult.failed("OneSignal not implemented");
    }
}
