package com.im.service.user.controller;

import com.im.common.base.Result;
import com.im.service.user.dto.FriendResponse;
import com.im.service.user.service.FriendService;
import com.im.service.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 好友关系控制器 - 专注好友关系管理API
 * 
 * @author IM Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;
    private final UserService userService;

    /**
     * 获取好友统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Long>> getFriendStats(@RequestHeader("Authorization") String token) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        Map<String, Long> stats = friendService.getFriendStats(userId);
        return Result.success(stats);
    }

    /**
     * 检查是否为好友
     */
    @GetMapping("/check/{targetUserId}")
    public Result<Boolean> checkFriendship(@RequestHeader("Authorization") String token,
                                          @PathVariable Long targetUserId) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        boolean areFriends = friendService.areFriends(userId, targetUserId);
        return Result.success(areFriends);
    }

    /**
     * 检查是否被屏蔽
     */
    @GetMapping("/blocked-by/{targetUserId}")
    public Result<Boolean> checkBlocked(@RequestHeader("Authorization") String token,
                                       @PathVariable Long targetUserId) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        boolean isBlocked = friendService.isBlocked(userId, targetUserId);
        return Result.success(isBlocked);
    }
}
