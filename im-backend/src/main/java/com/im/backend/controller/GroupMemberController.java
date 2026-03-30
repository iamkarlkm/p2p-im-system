package com.im.backend.controller;

import com.im.backend.dto.AddGroupMemberRequest;
import com.im.backend.dto.GroupMemberDTO;
import com.im.backend.entity.GroupMemberRole;
import com.im.backend.service.GroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 群成员管理控制器
 * 功能#29: 群成员管理
 */
@RestController
@RequestMapping("/api/group-member")
public class GroupMemberController {
    
    @Autowired
    private GroupMemberService groupMemberService;
    
    @PostMapping("/add")
    public ResponseEntity<GroupMemberDTO> addMember(
            @RequestAttribute("userId") Long operatorId,
            @RequestBody AddGroupMemberRequest request) {
        GroupMemberDTO response = groupMemberService.addMember(operatorId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{groupId}/remove/{userId}")
    public ResponseEntity<Void> removeMember(
            @RequestAttribute("userId") Long operatorId,
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        groupMemberService.removeMember(operatorId, groupId, userId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{groupId}/role/{userId}")
    public ResponseEntity<Void> updateRole(
            @RequestAttribute("userId") Long operatorId,
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestParam GroupMemberRole role) {
        groupMemberService.updateMemberRole(operatorId, groupId, userId, role);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{groupId}/nickname")
    public ResponseEntity<Void> updateNickname(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long groupId,
            @RequestParam String nickname) {
        groupMemberService.updateGroupNickname(groupId, userId, nickname);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{groupId}/mute/{userId}")
    public ResponseEntity<Void> muteMember(
            @RequestAttribute("userId") Long operatorId,
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestParam int minutes) {
        groupMemberService.muteMember(operatorId, groupId, userId, minutes);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{groupId}/unmute/{userId}")
    public ResponseEntity<Void> unmuteMember(
            @RequestAttribute("userId") Long operatorId,
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        groupMemberService.unmuteMember(operatorId, groupId, userId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{groupId}/member/{userId}")
    public ResponseEntity<GroupMemberDTO> getMember(@PathVariable Long groupId, @PathVariable Long userId) {
        GroupMemberDTO response = groupMemberService.getMember(groupId, userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberDTO>> getGroupMembers(@PathVariable Long groupId) {
        List<GroupMemberDTO> response = groupMemberService.getGroupMembers(groupId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{groupId}/members/page")
    public ResponseEntity<Page<GroupMemberDTO>> getGroupMembersPage(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GroupMemberDTO> response = groupMemberService.getGroupMembers(groupId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my-groups")
    public ResponseEntity<List<GroupMemberDTO>> getUserGroups(@RequestAttribute("userId") Long userId) {
        List<GroupMemberDTO> response = groupMemberService.getUserGroups(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{groupId}/count")
    public ResponseEntity<Map<String, Long>> getMemberCount(@PathVariable Long groupId) {
        Long count = groupMemberService.getGroupMemberCount(groupId);
        Map<String, Long> result = new HashMap<>();
        result.put("count", count);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{groupId}/check/{userId}")
    public ResponseEntity<Map<String, Boolean>> checkMembership(
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        boolean isMember = groupMemberService.isGroupMember(groupId, userId);
        boolean isAdminOrOwner = groupMemberService.isAdminOrOwner(groupId, userId);
        boolean isOwner = groupMemberService.isOwner(groupId, userId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("isMember", isMember);
        result.put("isAdminOrOwner", isAdminOrOwner);
        result.put("isOwner", isOwner);
        return ResponseEntity.ok(result);
    }
}
