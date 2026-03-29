package com.im.backend.controller;

import com.im.backend.entity.ChannelEntity;
import com.im.backend.entity.ChannelMemberEntity;
import com.im.backend.service.ChannelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 频道管理 REST API
 * 大型群组的频道划分系统
 */
@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    // ========== 频道 CRUD ==========

    @PostMapping
    public ResponseEntity<ChannelEntity> createChannel(@RequestBody Map<String, String> body) {
        String groupId = body.get("groupId");
        String name = body.get("name");
        String channelType = body.get("channelType");
        String userId = body.get("userId");
        ChannelEntity channel = channelService.createChannel(groupId, name, channelType, userId);
        return ResponseEntity.ok(channel);
    }

    @GetMapping("/{channelId}")
    public ResponseEntity<ChannelEntity> getChannel(@PathVariable String channelId) {
        return channelService.getChannel(channelId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{channelId}")
    public ResponseEntity<ChannelEntity> updateChannel(
            @PathVariable String channelId,
            @RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        String description = (String) body.get("description");
        String icon = (String) body.get("icon");
        String requiredRole = (String) body.get("requiredRole");
        Boolean isPublic = body.get("isPublic") != null ? (Boolean) body.get("isPublic") : null;
        ChannelEntity channel = channelService.updateChannel(channelId, name, description, icon, requiredRole, isPublic);
        return ResponseEntity.ok(channel);
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> deleteChannel(@PathVariable String channelId) {
        channelService.deleteChannel(channelId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{channelId}/archive")
    public ResponseEntity<ChannelEntity> archiveChannel(@PathVariable String channelId) {
        ChannelEntity channel = channelService.archiveChannel(channelId);
        return ResponseEntity.ok(channel);
    }

    // ========== 频道列表 ==========

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<ChannelEntity>> getChannelsByGroup(@PathVariable String groupId) {
        List<ChannelEntity> channels = channelService.getChannelsByGroup(groupId);
        return ResponseEntity.ok(channels);
    }

    @GetMapping("/group/{groupId}/root")
    public ResponseEntity<List<ChannelEntity>> getRootChannels(@PathVariable String groupId) {
        List<ChannelEntity> channels = channelService.getRootChannels(groupId);
        return ResponseEntity.ok(channels);
    }

    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<ChannelEntity>> getChildChannels(@PathVariable String parentId) {
        List<ChannelEntity> channels = channelService.getChildChannels(parentId);
        return ResponseEntity.ok(channels);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChannelEntity>> getUserChannels(@PathVariable String userId) {
        List<ChannelEntity> channels = channelService.getUserChannels(userId);
        return ResponseEntity.ok(channels);
    }

    // ========== 频道排序 ==========

    @PutMapping("/group/{groupId}/reorder")
    public ResponseEntity<Void> reorderChannels(
            @PathVariable String groupId,
            @RequestBody Map<String, List<String>> body) {
        List<String> channelIds = body.get("channelIds");
        channelService.reorderChannels(groupId, channelIds);
        return ResponseEntity.ok().build();
    }

    // ========== 成员管理 ==========

    @PostMapping("/{channelId}/members")
    public ResponseEntity<Map<String, Object>> addMember(
            @PathVariable String channelId,
            @RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String role = body.get("role");
        String invitedBy = body.get("invitedBy");
        channelService.addMember(channelId, userId, role, invitedBy);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("channelId", channelId);
        result.put("userId", userId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{channelId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable String channelId,
            @PathVariable String userId) {
        channelService.removeMember(channelId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{channelId}/members")
    public ResponseEntity<List<ChannelMemberEntity>> getMembers(@PathVariable String channelId) {
        List<ChannelMemberEntity> members = channelService.getMembers(channelId);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{channelId}/members/count")
    public ResponseEntity<Map<String, Object>> getMemberCount(@PathVariable String channelId) {
        long count = channelService.getMemberCount(channelId);
        Map<String, Object> result = new HashMap<>();
        result.put("channelId", channelId);
        result.put("memberCount", count);
        return ResponseEntity.ok(result);
    }

    // ========== 权限检查 ==========

    @GetMapping("/{channelId}/permission/{userId}")
    public ResponseEntity<Map<String, Object>> checkPermission(
            @PathVariable String channelId,
            @PathVariable String userId,
            @RequestParam(defaultValue = "MEMBER") String requiredRole) {
        boolean hasPermission = channelService.hasPermission(channelId, userId, requiredRole);
        Map<String, Object> result = new HashMap<>();
        result.put("channelId", channelId);
        result.put("userId", userId);
        result.put("hasPermission", hasPermission);
        result.put("requiredRole", requiredRole);
        return ResponseEntity.ok(result);
    }

    // ========== 消息统计 ==========

    @PostMapping("/{channelId}/message-count")
    public ResponseEntity<Void> incrementMessageCount(@PathVariable String channelId) {
        channelService.incrementMessageCount(channelId);
        return ResponseEntity.ok().build();
    }
}
