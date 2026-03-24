package com.im.backend.controller;

import com.im.backend.entity.ContactGroupEntity;
import com.im.backend.service.ContactGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 联系人好友分组控制器
 */
@RestController
@RequestMapping("/api/v1/contact-group")
public class ContactGroupController {

    @Autowired
    private ContactGroupService groupService;

    @PostMapping("/create")
    public ResponseEntity<?> createGroup(
            @RequestParam Long userId,
            @RequestParam String groupName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String icon,
            @RequestParam(required = false) String color) {
        try {
            ContactGroupEntity group = groupService.createGroup(userId, groupName, description, icon, color);
            return ResponseEntity.ok(buildSuccessResponse("创建分组成功", group));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("创建分组失败: " + e.getMessage()));
        }
    }

    @PutMapping("/update/{groupId}")
    public ResponseEntity<?> updateGroup(
            @PathVariable Long groupId,
            @RequestParam Long userId,
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String icon,
            @RequestParam(required = false) String color) {
        try {
            ContactGroupEntity group = groupService.updateGroup(userId, groupId, groupName, description, icon, color);
            return ResponseEntity.ok(buildSuccessResponse("更新分组成功", group));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("更新分组失败: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long groupId, @RequestParam Long userId) {
        try {
            groupService.deleteGroup(userId, groupId);
            return ResponseEntity.ok(buildSuccessResponse("删除分组成功", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("删除分组失败: " + e.getMessage()));
        }
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroup(@PathVariable Long groupId, @RequestParam Long userId) {
        try {
            ContactGroupEntity group = groupService.getGroupById(userId, groupId);
            return ResponseEntity.ok(buildSuccessResponse("获取分组成功", group));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("获取分组失败: " + e.getMessage()));
        }
    }

    @GetMapping("/user-groups")
    public ResponseEntity<?> getUserGroups(@RequestParam Long userId) {
        try {
            List<ContactGroupEntity> groups = groupService.getUserGroups(userId);
            return ResponseEntity.ok(buildSuccessResponse("获取分组列表成功", groups));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("获取分组列表失败: " + e.getMessage()));
        }
    }

    @GetMapping("/custom-groups")
    public ResponseEntity<?> getCustomGroups(@RequestParam Long userId) {
        try {
            List<ContactGroupEntity> groups = groupService.getCustomGroups(userId);
            return ResponseEntity.ok(buildSuccessResponse("获取自定义分组成功", groups));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("获取自定义分组失败: " + e.getMessage()));
        }
    }

    @GetMapping("/default-groups")
    public ResponseEntity<?> getDefaultGroups(@RequestParam Long userId) {
        try {
            List<ContactGroupEntity> groups = groupService.getDefaultGroups(userId);
            return ResponseEntity.ok(buildSuccessResponse("获取默认分组成功", groups));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("获取默认分组失败: " + e.getMessage()));
        }
    }

    @PutMapping("/{groupId}/sort-index")
    public ResponseEntity<?> updateSortIndex(
            @PathVariable Long groupId,
            @RequestParam Long userId,
            @RequestParam Integer newIndex) {
        try {
            ContactGroupEntity group = groupService.updateGroupSortIndex(userId, groupId, newIndex);
            return ResponseEntity.ok(buildSuccessResponse("更新排序成功", group));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("更新排序失败: " + e.getMessage()));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getCount(@RequestParam Long userId) {
        try {
            long count = groupService.getGroupCount(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("count", count);
            result.put("userId", userId);
            return ResponseEntity.ok(buildSuccessResponse("获取分组数量成功", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("获取分组数量失败: " + e.getMessage()));
        }
    }

    @DeleteMapping("/cleanup-empty")
    public ResponseEntity<?> cleanupEmptyGroups(@RequestParam Long userId) {
        try {
            groupService.cleanupEmptyGroups(userId);
            return ResponseEntity.ok(buildSuccessResponse("清理空分组成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("清理空分组失败: " + e.getMessage()));
        }
    }

    private Map<String, Object> buildSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("timestamp", Instant.now().toString());
        if (data != null) response.put("data", data);
        return response;
    }

    private Map<String, Object> buildErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", Instant.now().toString());
        return response;
    }
}