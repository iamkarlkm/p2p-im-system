package com.im.server.controller;

import com.im.server.dto.ApiResponse;
import com.im.server.entity.Group;
import com.im.server.entity.GroupMember;
import com.im.server.service.GroupService;
import com.im.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 群组控制器
 */
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    
    private final GroupService groupService;
    private final UserService userService;
    
    /**
     * 创建群组
     */
    @PostMapping
    public ApiResponse<Group> createGroup(@RequestHeader("Authorization") String token,
                                           @RequestBody Map<String, String> request) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        try {
            String groupName = request.get("groupName");
            String avatarUrl = request.get("avatarUrl");
            String notice = request.get("notice");
            
            Group group = groupService.createGroup(userId, groupName, avatarUrl, notice);
            return ApiResponse.success(group);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 加入群组
     */
    @PostMapping("/{groupId}/join")
    public ApiResponse<GroupMember> joinGroup(@RequestHeader("Authorization") String token,
                                               @PathVariable Long groupId,
                                               @RequestBody Map<String, String> request) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        try {
            String nickname = request.get("nickname");
            GroupMember member = groupService.joinGroup(groupId, userId, nickname);
            return ApiResponse.success(member);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 退出群组
     */
    @PostMapping("/{groupId}/leave")
    public ApiResponse<Void> leaveGroup(@RequestHeader("Authorization") String token,
                                         @PathVariable Long groupId) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        try {
            groupService.leaveGroup(groupId, userId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取群组信息
     */
    @GetMapping("/{groupId}")
    public ApiResponse<Group> getGroup(@PathVariable Long groupId) {
        Group group = groupService.getGroupById(groupId);
        if (group == null) {
            return ApiResponse.error(404, "群组不存在");
        }
        return ApiResponse.success(group);
    }
    
    /**
     * 获取群组成员列表
     */
    @GetMapping("/{groupId}/members")
    public ApiResponse<List<Map<String, Object>>> getGroupMembers(
            @RequestHeader("Authorization") String token,
            @PathVariable Long groupId) {
        
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        List<GroupMember> members = groupService.getGroupMembers(groupId);
        List<Map<String, Object>> result = members.stream().map(member -> {
            var user = userService.getUserById(member.getUserId());
            if (user != null) {
                user.setPasswordHash(null);
            }
            return Map.of(
                    "member", member,
                    "user", user
            );
        }).collect(Collectors.toList());
        
        return ApiResponse.success(result);
    }
    
    /**
     * 获取用户加入的群组列表
     */
    @GetMapping("/my")
    public ApiResponse<List<Map<String, Object>>> getMyGroups(@RequestHeader("Authorization") String token) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        List<GroupMember> members = groupService.getUserGroups(userId);
        List<Map<String, Object>> result = members.stream().map(member -> {
            var group = groupService.getGroupById(member.getGroupId());
            return Map.of(
                    "member", member,
                    "group", group
            );
        }).collect(Collectors.toList());
        
        return ApiResponse.success(result);
    }
    
    /**
     * 更新群组信息
     */
    @PutMapping("/{groupId}")
    public ApiResponse<Group> updateGroup(@RequestHeader("Authorization") String token,
                                           @PathVariable Long groupId,
                                           @RequestBody Map<String, String> request) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        try {
            String groupName = request.get("groupName");
            String avatarUrl = request.get("avatarUrl");
            String notice = request.get("notice");
            
            Group group = groupService.updateGroup(groupId, userId, groupName, avatarUrl, notice);
            return ApiResponse.success(group);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
