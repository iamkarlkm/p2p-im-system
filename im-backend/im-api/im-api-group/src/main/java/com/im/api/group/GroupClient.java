package com.im.api.group;

import com.im.common.base.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 群组服务Feign客户端
 */
@FeignClient(name = "im-service-group")
public interface GroupClient {

    /**
     * 创建群组
     */
    @PostMapping("/api/groups/create")
    Result<Map<String, Object>> createGroup(@RequestBody Map<String, Object> groupInfo);
    
    /**
     * 获取群组成员
     */
    @GetMapping("/api/groups/{groupId}/members")
    Result<List<Map<String, Object>>> getGroupMembers(@PathVariable String groupId);
}
