package com.im.backend.controller;

import com.im.backend.model.UserStatus;
import com.im.backend.model.UserStatus.StatusType;
import com.im.backend.service.StatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StatusController {

    private final StatusService statusService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserStatus> getStatus(@PathVariable String userId) {
        UserStatus status = statusService.getStatus(userId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<UserStatus> setStatus(
            @PathVariable String userId,
            @RequestBody StatusUpdateRequest request) {
        StatusType type = StatusType.fromValue(request.getStatus());
        UserStatus status = statusService.setStatus(userId, type, request.getCustomMessage());
        return ResponseEntity.ok(status);
    }

    @PostMapping("/{userId}/online")
    public ResponseEntity<UserStatus> setOnline(@PathVariable String userId) {
        return ResponseEntity.ok(statusService.setStatus(userId, StatusType.ONLINE, null));
    }

    @PostMapping("/{userId}/away")
    public ResponseEntity<UserStatus> setAway(@PathVariable String userId) {
        return ResponseEntity.ok(statusService.setStatus(userId, StatusType.AWAY, null));
    }

    @PostMapping("/{userId}/busy")
    public ResponseEntity<UserStatus> setBusy(@PathVariable String userId) {
        return ResponseEntity.ok(statusService.setStatus(userId, StatusType.BUSY, null));
    }

    @PostMapping("/{userId}/dnd")
    public ResponseEntity<UserStatus> setDnd(@PathVariable String userId) {
        return ResponseEntity.ok(statusService.setStatus(userId, StatusType.DND, null));
    }

    @PostMapping("/{userId}/invisible")
    public ResponseEntity<UserStatus> setInvisible(@PathVariable String userId) {
        return ResponseEntity.ok(statusService.setStatus(userId, StatusType.INVISIBLE, null));
    }

    @PostMapping("/batch")
    public ResponseEntity<Map<String, UserStatus>> getBatchStatuses(@RequestBody BatchStatusRequest request) {
        Map<String, UserStatus> statuses = statusService.getStatuses(request.getUserIds());
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/online")
    public ResponseEntity<List<UserStatus>> getOnlineUsers() {
        return ResponseEntity.ok(statusService.getOnlineUsers());
    }

    @GetMapping("/types")
    public ResponseEntity<List<Map<String, Object>>> getStatusTypes() {
        List<Map<String, Object>> types = new ArrayList<>();
        for (StatusType type : StatusType.values()) {
            Map<String, Object> info = new HashMap<>();
            info.put("value", type.getValue());
            info.put("label", type.getLabel());
            info.put("autoRevert", type.getAutoRevertMs() > 0);
            info.put("autoRevertMs", type.getAutoRevertMs());
            types.add(info);
        }
        return ResponseEntity.ok(types);
    }

    public static class StatusUpdateRequest {
        private String status;
        private String customMessage;
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getCustomMessage() { return customMessage; }
        public void setCustomMessage(String customMessage) { this.customMessage = customMessage; }
    }

    public static class BatchStatusRequest {
        private List<String> userIds;
        
        public List<String> getUserIds() { return userIds; }
        public void setUserIds(List<String> userIds) { this.userIds = userIds; }
    }
}
