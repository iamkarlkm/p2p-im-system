package com.im.api.user;

import com.im.common.base.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "im-service-user")
public interface UserClient {

    /**
     * 获取用户信息
     */
    @GetMapping("/api/users/{userId}")
    Result<Map<String, Object>> getUserById(@PathVariable Long userId);
    
    /**
     * 搜索用户
     */
    @GetMapping("/api/users/search")
    Result<List<Map<String, Object>>> searchUsers(@RequestParam String keyword);
}
