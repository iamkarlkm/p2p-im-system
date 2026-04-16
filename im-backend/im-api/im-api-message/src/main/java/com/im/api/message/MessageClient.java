package com.im.api.message;

import com.im.common.base.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 消息服务Feign客户端
 */
@FeignClient(name = "im-service-message")
public interface MessageClient {

    /**
     * 发送消息
     */
    @PostMapping("/api/messages/send")
    Result<Map<String, Object>> sendMessage(@RequestBody Map<String, Object> message);
    
    /**
     * 获取历史消息
     */
    @GetMapping("/api/messages/history/{userId}")
    Result<List<Map<String, Object>>> getHistory(@PathVariable Long userId);
}
