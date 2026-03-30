package com.im.backend.controller;

import com.im.backend.dto.*;
import com.im.backend.entity.Group;
import com.im.backend.entity.GroupMember;
import com.im.backend.entity.GroupMessage;
import com.im.backend.service.GroupChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 群聊控制器
 * 对应功能 #15 - 群聊功能
 * 
 * API列表:
 * POST   /api/group                    - 创建群组
 * GET    /api/group/{id}               - 获取群组信息
 * PUT    /api/group/{id}               - 更新群组信息
 * DELETE /api/group/{id}               - 解散群组
 * POST   /api/group/{id}/members       - 邀请成员
 * DELETE /api/group/{id}/members/{userId} - 移除成员
 * GET    /api/group/{id}/members       - 获取成员列表
 * POST   /api/group/{id}/messages      - 发送群消息
 * GET    /api/group/{id}/messages      - 获取群消息历史
 * POST   /api/group/{id}/recall/{messageId} - 撤回消息
 * POST   /api/group/{id}/quit          - 退出群组
 * GET    /api/group/list               - 获取我的群组列表
 */
@RestController
@RequestMapping("/api/group")
public class GroupChatController {

    @Autowired
    private GroupChatService groupChatService;
    
    /**
     * 创建群组
     */
    @PostMapping
    public ResponseEntity<?> createGroup(@RequestAttribute("userId") Long userId,
                                          @RequestBody CreateGroupRequest request) {
        try {
            Group group = groupChatService.createGroup(userId, request);
            return ResponseEntity.ok(createSuccessResponse(group));
        } catch (Exception e) {
            return ResponseEntity.badRequest().create(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取群组信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getGroup(@RequestAttribute("userId") Long userId,
                                       @PathVariable Long id) {
        try {
            Group group = groupChatService.getGroup(id)
                .orElseThrow(() -> new RuntimeException("群组不存在"));
            return ResponseEntity.ok(createSuccessResponse(group));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 更新群组信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroup(@RequestAttribute("userId") Long userId,
                                          @PathVariable Long id,
                                          @RequestBody CreateGroupRequest request) {
        try {
            Group group = groupChatService.updateGroup(id, userId, request);
            return ResponseEntity.ok(createSuccessResponse(group));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 解散群组
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> dissolveGroup(@RequestAttribute("userId") Long userId,
                                            @PathVariable Long id) {
        try {
            groupChatService.dissolveGroup(id, userId);
            return ResponseEntity.ok(createSuccessResponse("群组已解散"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 邀请成员
     */
    @PostMapping("/{id}/members")
    public ResponseEntity<?> inviteMember(@RequestAttribute("userId") Long userId,
                                           @PathVariable Long id,
                                           @RequestBody Map<String, Long> request) {
        try {
            Long memberId = request.get("userId");
            if (memberId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("userId不能为空"));
            }
            GroupMember member = groupChatService.inviteMember(id, userId, memberId);
            return ResponseEntity.ok(createSuccessResponse(member));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 移除成员
     */
    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<?> removeMember(@RequestAttribute("userId") Long operatorId,
                                           @PathVariable Long id,
                                           @PathVariable Long userId) {
        try {
            groupChatService.removeMember(id, operatorId, userId);
            return ResponseEntity.ok(createSuccessResponse("成员已移除"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取成员列表
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<?> getGroupMembers(@RequestAttribute("userId") Long userId,
                                              @PathVariable Long id) {
        try {
            List<GroupMember> members = groupChatService.getGroupMembers(id);
            return ResponseEntity.ok(createSuccessResponse(members));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 发送群消息
     */
    @PostMapping("/{id}/messages")
    public ResponseEntity<?> sendMessage(@RequestAttribute("userId") Long userId,
                                          @PathVariable Long id,
                                          @RequestBody GroupMessageRequest request) {
        try {
            request.setGroupId(id);
            GroupMessage message = groupChatService.sendMessage(userId, request);
            return ResponseEntity.ok(createSuccessResponse(message));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取群消息历史
     */
    @GetMapping("/{id}/messages")
    public ResponseEntity<?> getGroupMessages(@RequestAttribute("userId") Long userId,
                                               @PathVariable Long id,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<GroupMessage> messages = groupChatService.getGroupMessages(id, pageable);
            return ResponseEntity.ok(createSuccessResponse(messages));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 撤回消息
     */
    @PostMapping("/{id}/recall/{messageId}")
    public ResponseEntity<?> recallMessage(@RequestAttribute("userId") Long userId,
                                            @PathVariable Long id,
                                            @PathVariable Long messageId) {
        try {
            groupChatService.recallMessage(messageId, userId);
            return ResponseEntity.ok(createSuccessResponse("消息已撤回"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 退出群组
     */
    @PostMapping("/{id}/quit")
    public ResponseEntity<?> quitGroup(@RequestAttribute("userId") Long userId,
                                        @PathVariable Long id) {
        try {
            groupChatService.quitGroup(id, userId);
            return ResponseEntity.ok(createSuccessResponse("已退出群组"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取我的群组列表
     */
    @GetMapping("/list")
    public ResponseEntity<?> getMyGroups(@RequestAttribute("userId") Long userId) {
        try {
            List<Group> groups = groupChatService.getUserGroups(userId);
            return ResponseEntity.ok(createSuccessResponse(groups));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 创建成功响应
     */
    private Map<String, Object> createSuccessResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", data);
        return response;
    }
    
    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", message);
        response.put("data", null);
        return response;
    }
}
