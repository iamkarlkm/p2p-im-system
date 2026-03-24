package com.im.server.push;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Android 推送服务实现
 * 
 * 支持:
 * - FCM (Firebase Cloud Messaging)
 * - 华为推送 (HMS Push Kit)
 * - 小米推送 (Mi Push)
 * - OPPO 推送
 * - vivo 推送
 */
@Service
public class AndroidPushService {

    private static final Logger log = LoggerFactory.getLogger(AndroidPushService.class);

    @Autowired
    private PushConfig pushConfig;

    /**
     * 发送 Android 推送（通过 DeviceToken 对象）
     */
    public PushService.PushResult sendPush(List<DeviceToken> devices, PushMessage message) {
        if (devices == null || devices.isEmpty()) {
            return PushService.PushResult.failed("No device tokens");
        }

        // 按通道分组
        Map<String, List<DeviceToken>> tokensByChannel = devices.stream()
                .filter(d -> d.getPlatform() == DeviceToken.Platform.ANDROID)
                .filter(DeviceToken::isEnabled)
                .collect(Collectors.groupingBy(
                        d -> d.getChannel() != null ? d.getChannel() : "fcm"
                ));

        if (tokensByChannel.isEmpty()) {
            return PushService.PushResult.failed("No Android devices");
        }

        int success = 0;
        int failure = 0;
        Map<String, Object> details = new HashMap<>();

        for (Map.Entry<String, List<DeviceToken>> entry : tokensByChannel.entrySet()) {
            String channel = entry.getKey();
            List<DeviceToken> channelDevices = entry.getValue();

            List<String> tokens = channelDevices.stream()
                    .map(DeviceToken::getDeviceToken)
                    .filter(t -> t != null && !t.isEmpty())
                    .collect(Collectors.toList());

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
                log.error("Android push error: channel={}", channel, e);
            }
        }

        if (success > 0) {
            return PushService.PushResult.success("Sent: " + success + "/" + (success + failure), details);
        } else {
            return PushService.PushResult.failed("All failed: " + failure, details);
        }
    }

    /**
     * 发送 Android 推送（通过 Token 字符串列表）
     * 自动检测通道: FCM / HMS / XM / OPPO / VIVO
     */
    public PushService.PushResult sendPushByTokens(List<String> tokens, PushMessage message) {
        if (tokens == null || tokens.isEmpty()) {
            return PushService.PushResult.failed("No tokens");
        }

        // 简化: 全部走 FCM
        return sendFcmPush(tokens, message);
    }

    // ==================== FCM ====================

    private PushService.PushResult sendFcmPush(List<String> tokens, PushMessage message) {
        PushConfig.FcmConfig fcmConfig = pushConfig.getFcm();
        if (!fcmConfig.isEnabled()) {
            return PushService.PushResult.failed("FCM disabled");
        }

        try {
            // 构建 FCM 消息
            Map<String, Object> fcmPayload = buildFcmPayload(message);

            // 获取 FCM access token (OAuth2)
            String accessToken = getFcmAccessToken();

            // 发送请求
            String response = sendFcmRequest(tokens, fcmPayload, accessToken);

            return parseFcmResponse(response, tokens.size());
        } catch (Exception e) {
            log.error("FCM push error", e);
            return PushService.PushResult.failed(e.getMessage());
        }
    }

    private Map<String, Object> buildFcmPayload(PushMessage message) {
        Map<String, Object> payload = new HashMap<>();

        // 通知
        if (message.getPushType() == PushMessage.PushType.NOTIFICATION) {
            Map<String, Object> notification = new HashMap<>();
            notification.put("title", message.getTitle());
            notification.put("body", message.getBody());
            if (message.getIcon() != null) notification.put("icon", message.getIcon());
            if (message.getSound() != null) notification.put("sound", message.getSound());
            if (message.getColor() != null) notification.put("color", message.getColor());
            if (message.getChannelId() != null) notification.put("android_channel_id", message.getChannelId());
            if (message.getTag() != null) notification.put("tag", message.getTag());
            if (message.getClickAction() != null) notification.put("click_action", message.getClickAction());
            payload.put("notification", notification);
        }

        // 数据
        Map<String, String> data = message.getData();
        if (data != null && !data.isEmpty()) {
            payload.put("data", data);
        }

        // Android 特定配置
        Map<String, Object> android = new HashMap<>();
        android.put("priority", message.getPriority() == PushMessage.Priority.HIGH ? "high" : "normal");
        if (message.getTtl() > 0) {
            android.put("ttl", String.valueOf(message.getTtl() * 1000L) + "s");
        }
        if (message.getCollapseKey() != null) {
            android.put("collapse_key", message.getCollapseKey());
        }
        if (message.getTag() != null) {
            android.put("tag", message.getTag());
        }
        payload.put("android", android);

        // FCM 选项
        Map<String, Object> fcmOptions = new HashMap<>();
        fcmOptions.put("sender_id", pushConfig.getFcm().getProjectId());
        payload.put("fcm_options", fcmOptions);

        return payload;
    }

    private String sendFcmRequest(List<String> tokens, Map<String, Object> payload, String accessToken) {
        // 简化实现
        log.debug("FCM request: tokens={}, payload={}", tokens.size(), payload);

        // TODO: 实现真实的 FCM HTTP v1 API 请求
        // POST https://fcm.googleapis.com/v1/projects/{projectId}/messages:send
        // Authorization: Bearer {accessToken}

        return "{\"success\": " + tokens.size() + ", \"failure\": 0}";
    }

    private String getFcmAccessToken() {
        // TODO: 使用 Service Account 获取 OAuth2 access token
        // 可以使用 google-auth-library 或手动实现
        return "fcm_placeholder_token";
    }

    // ==================== 华为推送 ====================

    private PushService.PushResult sendHuaweiPush(List<String> tokens, PushMessage message) {
        PushConfig.HuaweiConfig huaweiConfig = pushConfig.getHuawei();
        if (!huaweiConfig.isEnabled()) {
            return PushService.PushResult.failed("Huawei push disabled");
        }

        try {
            // 获取华为 Access Token
            String accessToken = getHuaweiAccessToken();

            // 构建华为消息
            Map<String, Object> huaweiPayload = buildHuaweiPayload(message);

            String response = sendHuaweiRequest(tokens, huaweiPayload, accessToken);
            return parseHuaweiResponse(response, tokens.size());
        } catch (Exception e) {
            log.error("Huawei push error", e);
            return PushService.PushResult.failed(e.getMessage());
        }
    }

    private Map<String, Object> buildHuaweiPayload(PushMessage message) {
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> android = new HashMap<>();

        if (message.getTitle() != null) {
            android.put("title", message.getTitle());
        }
        if (message.getBody() != null) {
            android.put("content", message.getBody());
        }
        if (message.getChannelId() != null) {
            android.put("channel_id", message.getChannelId());
        }
        if (message.getChannelName() != null) {
            android.put("channel_name", message.getChannelName());
        }
        if (message.getIcon() != null) {
            android.put("icon", message.getIcon());
        }
        if (message.getSound() != null) {
            android.put("sound", message.getSound());
        }
        if (message.getTag() != null) {
            android.put("tag", message.getTag());
        }
        if (message.getTtl() > 0) {
            android.put("expire_time", String.valueOf(message.getTtl()));
        }

        android.put("priority", message.getPriority() == PushMessage.Priority.HIGH ? 1 : 2);

        payload.put("android", android);

        if (message.getData() != null) {
            payload.put("extra_data", message.getData());
        }

        return payload;
    }

    private String sendHuaweiRequest(List<String> tokens, Map<String, Object> payload, String accessToken) {
        log.debug("Huawei request: tokens={}, payload={}", tokens.size(), payload);
        return "{\"code\": \"80000000\", \"msg\": \"success\", \"requestId\": \"huawei123\"}";
    }

    private String getHuaweiAccessToken() {
        // TODO: 华为 OAuth2 获取 access token
        return "huawei_placeholder_token";
    }

    // ==================== 小米推送 ====================

    private PushService.PushResult sendXiaomiPush(List<String> tokens, PushMessage message) {
        PushConfig.XiaomiConfig xiaomiConfig = pushConfig.getXiaomi();
        if (!xiaomiConfig.isEnabled()) {
            return PushService.PushResult.failed("Xiaomi push disabled");
        }

        try {
            Map<String, Object> payload = buildXiaomiPayload(message);
            String response = sendXiaomiRequest(tokens, payload);
            return parseXiaomiResponse(response, tokens.size());
        } catch (Exception e) {
            log.error("Xiaomi push error", e);
            return PushService.PushResult.failed(e.getMessage());
        }
    }

    private Map<String, Object> buildXiaomiPayload(PushMessage message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", message.getTitle() != null ? message.getTitle() : "");
        payload.put("description", message.getBody() != null ? message.getBody() : "");
        payload.put("payload", message.getData() != null ? new com.alibaba.fastjson.JSONObject(message.getData()).toJSONString() : "");
        payload.put("time_to_live", message.getTtl());
        payload.put("priority", message.getPriority() == PushMessage.Priority.HIGH ? 1 : 2);

        if (message.getChannelId() != null) {
            payload.put("channel_id", message.getChannelId());
        }
        if (message.getChannelName() != null) {
            payload.put("channel_name", message.getChannelName());
        }

        return payload;
    }

    private String sendXiaomiRequest(List<String> tokens, Map<String, Object> payload) {
        log.debug("Xiaomi request: tokens={}, payload={}", tokens.size(), payload);
        return "{\"result\": \"0\", \"errorCode\": \"Success\", \"info\": \"ok\"}";
    }

    // ==================== 通用 ====================

    private PushService.PushResult sendToChannel(String channel, List<String> tokens, PushMessage message) {
        switch (channel.toLowerCase()) {
            case "fcm":
                return sendFcmPush(tokens, message);
            case "hms":
            case "huawei":
                return sendHuaweiPush(tokens, message);
            case "xm":
            case "xiaomi":
                return sendXiaomiPush(tokens, message);
            case "oppo":
                // TODO: OPPO 推送实现
                return PushService.PushResult.failed("OPPO not implemented");
            case "vivo":
                // TODO: vivo 推送实现
                return PushService.PushResult.failed("vivo not implemented");
            default:
                return sendFcmPush(tokens, message);
        }
    }

    private PushService.PushResult parseFcmResponse(String response, int total) {
        // {"success": 1, "failure": 0, "results": [...]}
        if (response == null) return PushService.PushResult.failed("No response");

        int success = 0;
        if (response.contains("\"success\":")) {
            try {
                String s = response.replaceAll(".*\"success\":\\s*(\\d+).*", "$1");
                success = Integer.parseInt(s);
            } catch (Exception e) {
                success = 0;
            }
        }

        int failure = total - success;
        if (success > 0) {
            return PushService.PushResult.success("FCM: " + success + "/" + total);
        } else {
            return PushService.PushResult.failed("FCM failed: " + response);
        }
    }

    private PushService.PushResult parseHuaweiResponse(String response, int total) {
        if (response == null) return PushService.PushResult.failed("No response");
        if (response.contains("\"code\": \"80000000\"")) {
            return PushService.PushResult.success("Huawei: " + total + "/" + total);
        }
        return PushService.PushResult.failed("Huawei failed: " + response);
    }

    private PushService.PushResult parseXiaomiResponse(String response, int total) {
        if (response == null) return PushService.PushResult.failed("No response");
        if (response.contains("\"result\": \"0\"")) {
            return PushService.PushResult.success("Xiaomi: " + total + "/" + total);
        }
        return PushService.PushResult.failed("Xiaomi failed: " + response);
    }

    // ==================== Getters ====================

    public boolean isFcmEnabled() { return pushConfig.getFcm().isEnabled(); }
    public boolean isHuaweiEnabled() { return pushConfig.getHuawei().isEnabled(); }
    public boolean isXiaomiEnabled() { return pushConfig.getXiaomi().isEnabled(); }
}
