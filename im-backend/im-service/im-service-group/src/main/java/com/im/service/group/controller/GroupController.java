package com.im.service.group.controller;

import com.im.service.group.dto.CreateGroupRequest;
import com.im.service.group.dto.GroupMemberResponse;
import com.im.service.group.dto.GroupResponse;
import com.im.service.group.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(
            @RequestBody CreateGroupRequest request,
            @RequestHeader("X-User-Id") String userId) {
        GroupResponse response = groupService.createGroup(request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable String groupId) {
        return groupService.getGroup(groupId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GroupResponse>> getUserGroups(@PathVariable String userId) {
        List<GroupResponse> groups = groupService.getUserGroups(userId);
        return ResponseEntity.ok(groups);
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<Map<String, Object>> updateGroup(
            @PathVariable String groupId,
            @RequestBody CreateGroupRequest request,
            @RequestHeader("X-User-Id") String userId) {
        boolean success = groupService.updateGroup(groupId, request, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("groupId", groupId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Map<String, Object>> dissolveGroup(
            @PathVariable String groupId,
            @RequestHeader("X-User-Id") String userId) {
        boolean success = groupService.dissolveGroup(groupId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("groupId", groupId);
        return ResponseEntity.ok(result);
    }

    // ========== 成员管理 ==========

    @PostMapping("/{groupId}/members")
    public ResponseEntity<Map<String, Object>> addMember(
            @PathVariable String groupId,
            @RequestBody Map<String, String> request,
            @RequestHeader("X-User-Id") String operatorId) {
        String userId = request.get("userId");
        String role = request.get("role");
        boolean success = groupService.addMember(groupId, userId, role);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("groupId", groupId);
        result.put("userId", userId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Map<String, Object>> removeMember(
            @PathVariable String groupId,
            @PathVariable String userId,
            @RequestHeader("X-User-Id") String operatorId) {
        boolean success = groupService.removeMember(groupId, userId, operatorId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("groupId", groupId);
        result.put("userId", userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberResponse>> getGroupMembers(
            @PathVariable String groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        List<GroupMemberResponse> members = groupService.getGroupMembers(groupId, page, size);
        return ResponseEntity.ok(members);
    }

    @PutMapping("/{groupId}/members/{userId}/role")
    public ResponseEntity<Map<String, Object>> updateMemberRole(
            @PathVariable String groupId,
            @PathVariable String userId,
            @RequestBody Map<String, String> request,
            @RequestHeader("X-User-Id") String operatorId) {
        String role = request.get("role");
        boolean success = groupService.updateMemberRole(groupId, userId, role, operatorId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("groupId", groupId);
        result.put("userId", userId);
        result.put("role", role);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{groupId}/members/{userId}/mute")
    public ResponseEntity<Map<String, Object>> muteMember(
            @PathVariable String groupId,
            @PathVariable String userId,
            @RequestBody Map<String, Object> request,
            @RequestHeader("X-User-Id") String operatorId) {
        boolean muted = (Boolean) request.getOrDefault("muted", true);
        int minutes = (Integer) request.getOrDefault("minutes", 60);
        boolean success = groupService.muteMember(groupId, userId, muted, minutes, operatorId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("groupId", groupId);
        result.put("userId", userId);
        result.put("muted", muted);
        result.put("minutes", minutes);
        return ResponseEntity.ok(result);
    }
}
