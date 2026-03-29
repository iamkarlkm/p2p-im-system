package com.im.backend.controller;

import com.im.backend.dto.ScheduledMessageDTO;
import com.im.backend.model.ScheduledMessage;
import com.im.backend.service.ScheduledMessageService;
import com.im.backend.util.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 定时消息控制器
 */
@RestController
@RequestMapping("/api/scheduled-messages")
public class ScheduledMessageController {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledMessageController.class);

    @Autowired
    private ScheduledMessageService scheduledMessageService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 创建定时消息
     */
    @PostMapping
    public ResponseEntity<?> createScheduledMessage(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ScheduledMessageDTO dto) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long senderId = jwtUtil.extractUserId(token);

            ScheduledMessageDTO created = scheduledMessageService.createScheduledMessage(senderId, dto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", created);
            response.put("message", "定时消息创建成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("创建定时消息参数错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("创建定时消息失败", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "创建失败: " + e.getMessage()));
        }
    }

    /**
     * 获取用户的定时消息列表
     */
    @GetMapping
    public ResponseEntity<?> getScheduledMessages(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long senderId = jwtUtil.extractUserId(token);

            Pageable pageable = PageRequest.of(page, size, Sort.by("scheduledTime").descending());
            Page<ScheduledMessageDTO> messages;
            
            if (status != null && !status.isEmpty()) {
                ScheduledMessage.Status filterStatus = ScheduledMessage.Status.valueOf(status.toUpperCase());
                messages = scheduledMessageService.getUserScheduledMessagesByStatus(senderId, filterStatus, pageable);
            } else {
                messages = scheduledMessageService.getUserScheduledMessages(senderId, pageable);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", messages.getContent());
            response.put("totalElements", messages.getTotalElements());
            response.put("totalPages", messages.getTotalPages());
            response.put("currentPage", messages.getNumber());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取定时消息列表失败", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "获取失败: " + e.getMessage()));
        }
    }

    /**
     * 获取定时消息详情
     */
    @GetMapping("/{messageId}")
    public ResponseEntity<?> getScheduledMessage(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long messageId) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long senderId = jwtUtil.extractUserId(token);

            return scheduledMessageService.getScheduledMessage(messageId, senderId)
                    .map(dto -> ResponseEntity.ok(Map.of("success", true, "data", dto)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("获取定时消息详情失败", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "获取失败: " + e.getMessage()));
        }
    }

    /**
     * 更新定时消息
     */
    @PutMapping("/{messageId}")
    public ResponseEntity<?> updateScheduledMessage(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long messageId,
            @Valid @RequestBody ScheduledMessageDTO dto) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long senderId = jwtUtil.extractUserId(token);

            ScheduledMessageDTO updated = scheduledMessageService.updateScheduledMessage(messageId, senderId, dto);
            
            return ResponseEntity.ok(Map.of("success", true, "data", updated, "message", "更新成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("更新定时消息失败", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "更新失败: " + e.getMessage()));
        }
    }

    /**
     * 取消定时消息
     */
    @PostMapping("/{messageId}/cancel")
    public ResponseEntity<?> cancelScheduledMessage(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long messageId) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long senderId = jwtUtil.extractUserId(token);

            ScheduledMessageDTO cancelled = scheduledMessageService.cancelScheduledMessage(messageId, senderId);
            
            return ResponseEntity.ok(Map.of("success", true, "data", cancelled, "message", "取消成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("取消定时消息失败", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "取消失败: " + e.getMessage()));
        }
    }

    /**
     * 删除定时消息
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteScheduledMessage(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long messageId) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long senderId = jwtUtil.extractUserId(token);

            scheduledMessageService.deleteScheduledMessage(messageId, senderId);
            
            return ResponseEntity.ok(Map.of("success", true, "message", "删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("删除定时消息失败", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "删除失败: " + e.getMessage()));
        }
    }

    /**
     * 获取统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long senderId = jwtUtil.extractUserId(token);

            long pendingCount = scheduledMessageService.countPendingMessages(senderId);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("pendingCount", pendingCount);
            
            return ResponseEntity.ok(Map.of("success", true, "data", stats));
        } catch (Exception e) {
            logger.error("获取统计信息失败", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "获取统计失败: " + e.getMessage()));
        }
    }
}
