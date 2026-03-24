package com.im.server.controller;

import com.im.server.dto.ApiResponse;
import com.im.server.entity.Friend;
import com.im.server.entity.User;
import com.im.server.service.FriendService;
import com.im.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 好友控制器
 */
@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {
    
    private final FriendService friendService;
    private final UserService userService;
    
    /**
     * 添加好友
     */
    @PostMapping
    public ApiResponse<Friend> addFriend(@RequestHeader("Authorization") String token,
                                         @RequestBody Map<String, Object> request) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        try {
            Long friendId = Long.parseLong(request.get("friendId").toString());
            String remark = request.get("remark") != null ? request.get("remark").toString() : null;
            
            Friend friend = friendService.addFriend(userId, friendId, remark);
            return ApiResponse.success(friend);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 删除好友
     */
    @DeleteMapping("/{friendId}")
    public ApiResponse<Void> deleteFriend(@RequestHeader("Authorization") String token,
                                           @PathVariable Long friendId) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        friendService.deleteFriend(userId, friendId);
        return ApiResponse.success(null);
    }
    
    /**
     * 拉黑好友
     */
    @PostMapping("/block/{friendId}")
    public ApiResponse<Void> blockFriend(@RequestHeader("Authorization") String token,
                                          @PathVariable Long friendId) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        friendService.blockFriend(userId, friendId);
        return ApiResponse.success(null);
    }
    
    /**
     * 获取好友列表
     */
    @GetMapping
    public ApiResponse<List<Map<String, Object>>> getFriends(@RequestHeader("Authorization") String token) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        List<Friend> friends = friendService.getFriends(userId);
        List<Map<String, Object>> result = friends.stream().map(friend -> {
            User user = userService.getUserById(friend.getFriendId());
            if (user != null) {
                user.setPasswordHash(null);
            }
            return Map.of(
                    "friend", friend,
                    "user", user
            );
        }).collect(Collectors.toList());
        
        return ApiResponse.success(result);
    }
    
    /**
     * 检查是否为好友
     */
    @GetMapping("/check/{friendId}")
    public ApiResponse<Boolean> isFriend(@RequestHeader("Authorization") String token,
                                          @PathVariable Long friendId) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        boolean isFriend = friendService.isFriend(userId, friendId);
        return ApiResponse.success(isFriend);
    }
}
