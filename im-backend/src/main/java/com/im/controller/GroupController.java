package com.im.controller;

import com.im.service.IGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 群组控制器
 * 功能 #5: 群组管理基础模块 - REST API
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {
    
    @Autowired
    private IGroupService groupService;
    
    @PostMapping("/create")
    public ResponseEntity<?> createGroup(@RequestParam String ownerId,
                                          @RequestParam String groupName,
                                          @RequestParam(required = false) String description) {
        var group = groupService.createGroup(ownerId, groupName, description);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "groupId", group.getGroupId(),
            "data", group
        ));
    }
    
    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> dissolveGroup(@PathVariable String groupId,
                                           @RequestParam String operatorId) {
        boolean dissolved = groupService.dissolveGroup(groupId, operatorId);
        return ResponseEntity.ok(Map.of("success", dissolved));
    }
    
    @PostMapping("/{groupId}/invite")
    public ResponseEntity<?> inviteMember(@PathVariable String groupId,
                                          @RequestParam String inviterId,
                                          @RequestParam String userId) {
        boolean invited = groupService.inviteMember(groupId, inviterId, userId);
        return ResponseEntity.ok(Map.of("success", invited));
    }
    
    @PostMapping("/{groupId}/kick")
    public ResponseEntity<?> kickMember(@PathVariable String groupId,
                                        @RequestParam String operatorId,
                                        @RequestParam String userId) {
        boolean kicked = groupService.kickMember(groupId, operatorId, userId);
        return ResponseEntity.ok(Map.of("success", kicked));
    }
    
    @PostMapping("/{groupId}/leave")
    public ResponseEntity<?> leaveGroup(@PathVariable String groupId,
                                        @RequestParam String userId) {
        boolean left = groupService.leaveGroup(groupId, userId);
        return ResponseEntity.ok(Map.of("success", left));
    }
    
    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroupInfo(@PathVariable String groupId) {
        var group = groupService.getGroupInfo(groupId);
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("success", true, "data", group));
    }
    
    @GetMapping("/{groupId}/members")
    public ResponseEntity<?> getGroupMembers(@PathVariable String groupId) {
        var members = groupService.getGroupMembers(groupId);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", members,
            "count", members.size()
        ));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserGroups(@PathVariable String userId) {
        var groups = groupService.getUserGroups(userId);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", groups,
            "count", groups.size()
        ));
    }
    
    @PutMapping("/{groupId}")
    public ResponseEntity<?> updateGroupInfo(@PathVariable String groupId,
                                             @RequestParam String operatorId,
                                             @RequestParam(required = false) String groupName,
                                             @RequestParam(required = false) String description) {
        boolean updated = groupService.updateGroupInfo(groupId, operatorId, groupName, description);
        return ResponseEntity.ok(Map.of("success", updated));
    }
    
    @PostMapping("/{groupId}/mute")
    public ResponseEntity<?> muteAll(@PathVariable String groupId,
                                     @RequestParam String operatorId,
                                     @RequestParam boolean mute) {
        boolean muted = groupService.muteAll(groupId, operatorId, mute);
        return ResponseEntity.ok(Map.of("success", muted));
    }
}
