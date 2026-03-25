package com.im.server.push;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 推送通知 REST API
 */
@RestController
@RequestMapping("/api/push")
public class PushNotificationController {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationController.class);

    @Autowired
    private PushService pushService;

    @Autowired
    private DeviceTokenRepository deviceTokenRepository;

    // ==================== 设备Token管理 ====================

    /**
     * 注册设备Token
     */
    @PostMapping("/device/register")
    public Result<DeviceToken> registerDevice(@RequestBody RegisterDeviceRequest request) {
        if (request.userId == null || request.token == null) {
            return Result.fail("userId and token are required");
        }

        DeviceToken device = new DeviceToken();
        device.setUserId(request.userId);
        device.setDeviceToken(request.token);
        device.setPlatform(DeviceToken.Platform.fromString(request.platform));
        device.setDeviceType(DeviceToken.DeviceType.fromString(request.deviceType));
        device.setDeviceName(request.deviceName);
        device.setDeviceModel(request.deviceModel);
        device.setOsVersion(request.osVersion);
        device.setAppVersion(request.appVersion);
        device.setBundleId(request.bundleId);
        device.setPackageName(request.packageName);
        device.setChannel(request.channel);
        device.setSandbox(request.sandbox != null && request.sandbox);
        device.setVoipToken(request.voipToken);
        device.setVoipEnabled(request.voipEnabled != null && request.voipEnabled);
        device.setSound(request.sound);
        device.setEnabled(true);
        device.setCreatedAt(LocalDateTime.now());
        device.setUpdatedAt(LocalDateTime.now());

        DeviceToken saved = deviceTokenRepository.save(device);
        log.info("Device registered: userId={}, platform={}, channel={}",
                request.userId, request.platform, request.channel);

        return Result.success(saved);
    }

    /**
     * 更新Token
     */
    @PutMapping("/device/update")
    public Result<String> updateDevice(@RequestBody UpdateTokenRequest request) {
        if (request.oldToken == null || request.newToken == null) {
            return Result.fail("oldToken and newToken are required");
        }

        Optional<DeviceToken> opt = deviceTokenRepository.findByToken(request.oldToken);
        if (opt.isEmpty()) {
            return Result.fail("Device not found");
        }

        DeviceToken device = opt.get();
        deviceTokenRepository.deleteByToken(request.oldToken);

        device.setDeviceToken(request.newToken);
        device.setTokenUpdatedTime(LocalDateTime.now());
        device.setLastActiveTime(LocalDateTime.now());
        deviceTokenRepository.save(device);

        return Result.success("Token updated");
    }

    /**
     * 删除设备Token
     */
    @DeleteMapping("/device/{token}")
    public Result<String> deleteDevice(@PathVariable String token) {
        deviceTokenRepository.deleteByToken(token);
        return Result.success("Device deleted");
    }

    /**
     * 获取用户设备列表
     */
    @GetMapping("/devices/{userId}")
    public Result<List<DeviceToken>> getUserDevices(@PathVariable Long userId) {
        List<DeviceToken> devices = deviceTokenRepository.findByUserId(userId);
        return Result.success(devices);
    }

    /**
     * 禁用设备
     */
    @PutMapping("/device/{token}/disable")
    public Result<String> disableDevice(@PathVariable String token) {
        deviceTokenRepository.disableByToken(token);
        return Result.success("Device disabled");
    }

    // ==================== 推送发送 ====================

    /**
     * 发送推送（用户ID）
     */
    @PostMapping("/send")
    public Result<PushService.PushResult> sendPush(@RequestBody SendPushRequest request) {
        if (request.userId == null) {
            return Result.fail("userId is required");
        }

        PushMessage message = buildMessage(request);
        PushService.PushResult result = pushService.sendPush(request.userId, message);
        return result.isSuccess() ? Result.success(result) : Result.fail(result.getMessage());
    }

    /**
     * 批量发送推送
     */
    @PostMapping("/send/batch")
    public Result<Map<Long, PushService.PushResult>> sendBatchPush(@RequestBody BatchPushRequest request) {
        if (request.userIds == null || request.userIds.isEmpty()) {
            return Result.fail("userIds is required");
        }

        PushMessage message = buildMessage(request);
        Map<Long, PushService.PushResult> results = pushService.sendBatchPush(request.userIds, message);
        return Result.success(results);
    }

    /**
     * 广播推送
     */
    @PostMapping("/broadcast")
    public Result<PushService.PushResult> broadcast(@RequestBody BroadcastPushRequest request) {
        PushMessage message = PushMessage.builder()
                .title(request.title)
                .body(request.body)
                .priority(request.priority == 1 ? PushMessage.Priority.HIGH : PushMessage.Priority.NORMAL)
                .data(request.data)
                .build();

        PushService.PushResult result = pushService.sendBroadcast(message);
        return result.isSuccess() ? Result.success(result) : Result.fail(result.getMessage());
    }

    /**
     * 发送测试推送
     */
    @PostMapping("/test/{userId}")
    public Result<PushService.PushResult> testPush(@PathVariable Long userId) {
        PushMessage message = PushMessageBuilder.buildTestPush();
        PushService.PushResult result = pushService.sendPush(userId, message);
        return result.isSuccess() ? Result.success(result) : Result.fail(result.getMessage());
    }

    /**
     * 发送静默推送
     */
    @PostMapping("/silent")
    public Result<PushService.PushResult> sendSilentPush(@RequestBody SilentPushRequest request) {
        if (request.userId == null || request.data == null) {
            return Result.fail("userId and data are required");
        }
        PushService.PushResult result = pushService.sendSilentPush(request.userId, request.data);
        return result.isSuccess() ? Result.success(result) : Result.fail(result.getMessage());
    }

    // ==================== 统计 ====================

    /**
     * 获取推送统计
     */
    @GetMapping("/stats")
    public Result<PushService.PushStats> getStats() {
        return Result.success(pushService.getStats());
    }

    // ==================== 辅助方法 ====================

    private PushMessage buildMessage(SendPushRequest request) {
        PushMessage.Priority priority = request.priority == 1
                ? PushMessage.Priority.HIGH
                : (request.priority == 0 ? PushMessage.Priority.LOW : PushMessage.Priority.NORMAL);

        return PushMessage.builder()
                .title(request.title)
                .body(request.body)
                .subtitle(request.subtitle)
                .category(request.category)
                .priority(priority)
                .data(request.data)
                .senderId(request.senderId)
                .senderName(request.senderName)
                .senderAvatar(request.senderAvatar)
                .conversationId(request.conversationId)
                .conversationType(request.conversationType)
                .messageId(request.messageId)
                .messageType(request.messageType)
                .mergeKey(request.mergeKey)
                .build();
    }

    // ==================== 请求 DTO ====================

    public static class RegisterDeviceRequest {
        public Long userId;
        public String token;
        public String platform;
        public String deviceType;
        public String deviceName;
        public String deviceModel;
        public String osVersion;
        public String appVersion;
        public String bundleId;
        public String packageName;
        public String channel;
        public Boolean sandbox;
        public String voipToken;
        public Boolean voipEnabled;
        public String sound;
    }

    public static class UpdateTokenRequest {
        public String oldToken;
        public String newToken;
    }

    public static class SendPushRequest {
        public Long userId;
        public String title;
        public String body;
        public String subtitle;
        public String category;
        public Integer priority;
        public Map<String, String> data;
        public Long senderId;
        public String senderName;
        public String senderAvatar;
        public String conversationId;
        public String conversationType;
        public Long messageId;
        public String messageType;
        public String mergeKey;
    }

    public static class BatchPushRequest extends SendPushRequest {
        public List<Long> userIds;
    }

    public static class BroadcastPushRequest {
        public String title;
        public String body;
        public Integer priority;
        public Map<String, String> data;
    }

    public static class SilentPushRequest {
        public Long userId;
        public Map<String, String> data;
    }

    // ==================== 响应 ====================

    public static class Result<T> {
        public boolean success;
        public String message;
        public T data;

        public static <T> Result<T> success(T data) {
            Result<T> r = new Result<>();
            r.success = true;
            r.data = data;
            return r;
        }

        public static <T> Result<T> success(T data, String message) {
            Result<T> r = new Result<>();
            r.success = true;
            r.data = data;
            r.message = message;
            return r;
        }

        public static <T> Result<T> fail(String message) {
            Result<T> r = new Result<>();
            r.success = false;
            r.message = message;
            return r;
        }
    }
}
