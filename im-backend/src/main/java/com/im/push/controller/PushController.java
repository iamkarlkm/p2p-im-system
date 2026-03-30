package com.im.push.controller;

import com.im.common.dto.ApiResponse;
import com.im.push.dto.PushRequest;
import com.im.push.service.PushService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 推送控制器
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/push")
@RequiredArgsConstructor
@Validated
@Tag(name = "消息推送", description = "离线消息推送通知")
public class PushController {
    
    private final PushService pushService;
    
    @PostMapping("/send")
    @Operation(summary = "发送推送", description = "发送单条推送消息")
    public ApiResponse<Boolean> sendPush(@Valid @RequestBody PushRequest request) {
        log.info("发送推送: userId={}, title={}", request.getUserId(), request.getTitle());
        boolean success = pushService.sendPush(request);
        return ApiResponse.success(success);
    }
    
    @PostMapping("/offline/{userId}")
    @Operation(summary = "处理离线推送", description = "处理用户的离线消息推送")
    public ApiResponse<Integer> processOfflinePush(@PathVariable Long userId) {
        log.info("处理离线推送: userId={}", userId);
        int count = pushService.processOfflinePush(userId);
        return ApiResponse.success(count, "推送了 " + count + " 条离线消息");
    }
}
