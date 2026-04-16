package com.im.api.push;

import com.im.common.base.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 推送服务Feign客户端
 */
@FeignClient(name = "im-service-push")
public interface PushClient {

    /**
     * 发送推送
     */
    @PostMapping("/api/push/send")
    Result<Map<String, Object>> sendPush(@RequestBody Map<String, Object> pushInfo);
    
    /**
     * 注册设备
     */
    @PostMapping("/api/push/register")
    Result<Void> registerDevice(@RequestBody Map<String, String> deviceInfo);
}
