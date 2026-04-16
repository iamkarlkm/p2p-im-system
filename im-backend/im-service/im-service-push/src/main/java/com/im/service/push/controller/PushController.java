package com.im.service.push.controller;

import com.im.common.base.Result;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 推送控制器
 */
@RestController
@RequestMapping("/api/push")
public class PushController {

    /**
     * 发送推送
     */
    @PostMapping("/send")
    public Result<Map<String, Object>> sendPush(@RequestBody Map<String, Object> pushInfo) {
        // TODO: 实现推送发送
        Map<String, Object> result = new HashMap<>();
        result.put("pushId", UUID.randomUUID().toString());
        result.put("status", "delivered");
        return Result.success(result);
    }

    /**
     * 注册设备
     */
    @PostMapping("/register")
    public Result<Void> registerDevice(@RequestBody Map<String, String> deviceInfo) {
        // TODO: 实现设备注册
        return Result.success(null);
    }
}
