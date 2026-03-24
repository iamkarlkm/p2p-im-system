package com.im.backend.controller;

import com.im.backend.entity.MessageQuoteSidebarEntity;
import com.im.backend.service.MessageQuoteSidebarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 引用消息侧边栏控制器
 * 提供引用消息侧边栏的REST API接口
 */
@RestController
@RequestMapping("/api/v1/message-quote-sidebar")
public class MessageQuoteSidebarController {

    @Autowired
    private MessageQuoteSidebarService sidebarService;

    // 基本CRUD操作

    /**
     * 添加引用消息到侧边栏
     */
    @PostMapping("/add")
    public ResponseEntity<?> addToSidebar(
            @RequestParam Long userId,
            @RequestParam Long sessionId,
            @RequestParam Long quoteId) {
        try {
            MessageQuoteSidebarEntity entity = sidebarService.addToSidebar(userId, sessionId, quoteId);
            return ResponseEntity.ok(buildSuccessResponse("添加到侧边栏成功", entity));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("添加到侧边栏失败: " + e.getMessage()));
        }
    }

    /**
     * 批量添加引用消息到侧边栏
     */
    @PostMapping("/batch-add")
    public ResponseEntity<?> batchAddToSidebar(
            @RequestParam Long userId,
            @RequestParam Long sessionId,
            @RequestBody List<Long> quoteIds) {
        try {
            List<MessageQuoteSidebarEntity> entities = sidebarService.batchAddToSidebar(userId, sessionId, quoteIds);
            return ResponseEntity.ok(buildSuccessResponse("批量添加到侧边栏成功", entities));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("批量添加到侧边栏失败: " + e.getMessage()));
        }
    }

    /**
     * 从侧边栏移除引用消息
     */
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromSidebar(
            @RequestParam Long userId,
            @RequestParam Long quoteId) {
        try {
            sidebarService.removeFromSidebar(userId, quoteId);
            return ResponseEntity.ok(buildSuccessResponse("从侧边栏移除成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("从侧边栏移除失败: " + e.getMessage()));
        }
    }

    /**
     * 批量从侧边栏移除引用消息
     */
    @DeleteMapping("/batch-remove")
    public ResponseEntity<?> batchRemoveFromSidebar(
            @RequestParam Long userId,
            @RequestBody List<Long> quoteIds) {
        try {
            sidebarService.batchRemoveFromSidebar(userId, quoteIds);
            return ResponseEntity.ok(buildSuccessResponse("批量从侧边栏移除成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("批量从侧边栏移除失败: " + e.getMessage()));
        }
    }

    /**
     * 清除会话的所有侧边栏记录
     */
    @DeleteMapping("/clear-session")
    public ResponseEntity<?> clearSessionSidebar(
            @RequestParam Long userId,
            @RequestParam Long sessionId) {
        try {
            sidebarService.clearSessionSidebar(userId, sessionId);
            return ResponseEntity.ok(buildSuccessResponse("清除会话侧边栏成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("清除会话侧边栏失败: " + e.getMessage()));
        }
    }

    // 查询操作

    /**
     * 获取用户的侧边栏记录列表
     */
    @GetMapping("/user-session-items")
    public ResponseEntity<?> getUserSidebarItems(
            @RequestParam Long userId,
            @RequestParam Long sessionId) {
        try {
            List<MessageQuoteSidebarEntity> items = sidebarService.getUserSidebarItems(userId, sessionId);
            return ResponseEntity.ok(buildSuccessResponse("获取侧边栏记录成功", items));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("获取侧边栏记录失败: " + e.getMessage()));
        }
    }

    /**
     * 获取用户的侧边栏记录列表（分页）
     */
    @GetMapping("/user-session-items-page")
    public ResponseEntity<?> getUserSidebarItemsPage(
            @RequestParam Long userId,
            @RequestParam Long sessionId,
            @PageableDefault(size = 20, sort = "lastViewedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<MessageQuoteSidebarEntity> page = sidebarService.getUserSidebarItemsPage(userId, sessionId, pageable);
            return ResponseEntity.ok(buildSuccessResponse("获取侧边栏记录分页成功", page));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("获取侧边栏记录分页失败: " + e.getMessage()));
        }
    }

    /**
     * 获取用户最近查看的侧边栏记录
     */
    @GetMapping("/recent-items")
    public ResponseEntity<?> getRecentSidebarItems(
            @RequestParam Long userId,
            @RequestParam Long sessionId) {
        try {
            List<MessageQuoteSidebarEntity> items = sidebarService.getRecentSidebarItems(userId, sessionId);
            return ResponseEntity.ok(buildSuccessResponse("获取最近侧边栏记录成功", items));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("获取最近侧边栏记录失败: " + e.getMessage()));
        }
    }

    /**
     * 获取用户所有固定的侧边栏记录
     */
    @GetMapping("/pinned-items")
    public ResponseEntity<?> getPinnedSidebarItems(@RequestParam Long userId) {
        try {
            List<MessageQuoteSidebarEntity> items = sidebarService.getPinnedSidebarItems(userId);
            return ResponseEntity.ok(buildSuccessResponse("获取固定侧边栏记录成功", items));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("获取固定侧边栏记录失败: " + e.getMessage()));
        }
    }

    /**
     * 获取会话中固定的侧边栏记录
     */
    @GetMapping("/pinned-items-in-session")
    public ResponseEntity<?> getPinnedSidebarItemsInSession(
            @RequestParam Long userId,
            @RequestParam Long sessionId) {
        try {
            List<MessageQuoteSidebarEntity> items = sidebarService.getPinnedSidebarItemsInSession(userId, sessionId);
            return ResponseEntity.ok(buildSuccessResponse("获取会话中固定侧边栏记录成功", items));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("获取会话中固定侧边栏记录失败: " + e.getMessage()));
        }
    }

    /**
     * 检查引用消息是否已在侧边栏
     */
    @GetMapping("/check-in-sidebar")
    public ResponseEntity<?> checkInSidebar(
            @RequestParam Long userId,
            @RequestParam Long quoteId) {
        try {
            boolean isInSidebar = sidebarService.isInSidebar(userId, quoteId);
            Map<String, Object> result = new HashMap<>();
            result.put("isInSidebar", isInSidebar);
            return ResponseEntity.ok(buildSuccessResponse("检查侧边栏状态成功", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("检查侧边栏状态失败: " + e.getMessage()));
        }
    }

    /**
     * 获取侧边栏记录详情
     */
    @GetMapping("/item-detail")
    public ResponseEntity<?> getSidebarItem(
            @RequestParam Long userId,
            @RequestParam Long quoteId) {
        try {
            Optional<MessageQuoteSidebarEntity> item = sidebarService.getSidebarItem(userId, quoteId);
            if (item.isPresent()) {
                return ResponseEntity.ok(buildSuccessResponse("获取侧边栏记录详情成功", item.get()));
            } else {
                return ResponseEntity.ok(buildSuccessResponse("侧边栏记录不存在", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("获取侧边栏记录详情失败: " + e.getMessage()));
        }
    }

    // 更新操作

    /**
     * 切换固定状态
     */
    @PutMapping("/toggle-pin")
    public ResponseEntity<?> togglePinStatus(
            @RequestParam Long userId,
            @RequestParam Long quoteId,
            @RequestParam boolean isPinned) {
        try {
            MessageQuoteSidebarEntity entity = sidebarService.togglePinStatus(userId, quoteId, isPinned);
            return ResponseEntity.ok(buildSuccessResponse("切换固定状态成功", entity));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("切换固定状态失败: " + e.getMessage()));
        }
    }

    /**
     * 批量更新固定状态
     */
    @PutMapping("/batch-toggle-pin")
    public ResponseEntity<?> batchTogglePinStatus(
            @RequestParam Long userId,
            @RequestBody List<Long> quoteIds,
            @RequestParam boolean isPinned) {
        try {
            sidebarService.batchUpdatePinStatus(userId, quoteIds, isPinned);
            return ResponseEntity.ok(buildSuccessResponse("批量切换固定状态成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("批量切换固定状态失败: " + e.getMessage()));
        }
    }

    /**
     * 更新侧边栏位置索引
     */
    @PutMapping("/update-index")
    public ResponseEntity<?> updateSidebarIndex(
            @RequestParam Long userId,
            @RequestParam Long quoteId,
            @RequestParam Integer newIndex) {
        try {
            MessageQuoteSidebarEntity entity = sidebarService.updateSidebarIndex(userId, quoteId, newIndex);
            return ResponseEntity.ok(buildSuccessResponse("更新位置索引成功", entity));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("更新位置索引失败: " + e.getMessage()));
        }
    }

    /**
     * 批量更新侧边栏位置索引
     */
    @PutMapping("/batch-update-indices")
    public ResponseEntity<?> batchUpdateSidebarIndices(
            @RequestParam Long userId,
            @RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> quoteIds = (List<Long>) request.get("quoteIds");
            @SuppressWarnings("unchecked")
            List<Integer> indices = (List<Integer>) request.get("indices");
            
            sidebarService.batchUpdateSidebarIndices(userId, quoteIds, indices);
            return ResponseEntity.ok(buildSuccessResponse("批量更新位置索引成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("批量更新位置索引失败: " + e.getMessage()));
        }
    }

    /**
     * 更新最后查看时间
     */
    @PutMapping("/update-last-viewed")
    public ResponseEntity<?> updateLastViewedAt(
            @RequestParam Long userId,
            @RequestParam Long quoteId) {
        try {
            sidebarService.updateLastViewedAt(userId, quoteId);
            return ResponseEntity.ok(buildSuccessResponse("更新最后查看时间成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("更新最后查看时间失败: " + e.getMessage()));
        }
    }

    /**
     * 批量更新最后查看时间
     */
    @PutMapping("/batch-update-last-viewed")
    public ResponseEntity<?> batchUpdateLastViewedAt(
            @RequestParam Long userId,
            @RequestBody List<Long> quoteIds) {
        try {
            sidebarService.batchUpdateLastViewedAt(userId, quoteIds);
            return ResponseEntity.ok(buildSuccessResponse("批量更新最后查看时间成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("批量更新最后查看时间失败: " + e.getMessage()));
        }
    }

    // 搜索功能

    /**
     * 搜索侧边栏记录
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchSidebarItems(
            @RequestParam Long userId,
            @RequestParam String keyword) {
        try {
            List<MessageQuoteSidebarEntity> items = sidebarService.searchSidebarItems(userId, keyword);
            return ResponseEntity.ok(buildSuccessResponse("搜索侧边栏记录成功", items));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("搜索侧边栏记录失败: " + e.getMessage()));
        }
    }

    /**
     * 按消息类型搜索侧边栏记录
     */
    @GetMapping("/search-by-type")
    public ResponseEntity<?> searchByMessageType(
            @RequestParam Long userId,
            @RequestParam String messageType) {
        try {
            List<MessageQuoteSidebarEntity> items = sidebarService.searchByMessageType(userId, messageType);
            return ResponseEntity.ok(buildSuccessResponse("按类型搜索侧边栏记录成功", items));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("按类型搜索侧边栏记录失败: " + e.getMessage()));
        }
    }

    /**
     * 按发送者搜索侧边栏记录
     */
    @GetMapping("/search-by-sender")
    public ResponseEntity<?> searchBySender(
            @RequestParam Long userId,
            @RequestParam Long senderId) {
        try {
            List<MessageQuoteSidebarEntity> items = sidebarService.searchBySender(userId, senderId);
            return ResponseEntity.ok(buildSuccessResponse("按发送者搜索侧边栏记录成功", items));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("按发送者搜索侧边栏记录失败: " + e.getMessage()));
        }
    }

    // 统计功能

    /**
     * 统计用户侧边栏记录数量
     */
    @GetMapping("/count-user-items")
    public ResponseEntity<?> countUserSidebarItems(@RequestParam Long userId) {
        try {
            long count = sidebarService.countUserSidebarItems(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("count", count);
            result.put("userId", userId);
            return ResponseEntity.ok(buildSuccessResponse("统计用户侧边栏记录数量成功", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("统计用户侧边栏记录数量失败: " + e.getMessage()));
        }
    }

    /**
     * 统计会话侧边栏记录数量
     */
    @GetMapping("/count-session-items")
    public ResponseEntity<?> countSessionSidebarItems(@RequestParam Long sessionId) {
        try {
            long count = sidebarService.countSessionSidebarItems(sessionId);
            Map<String, Object> result = new HashMap<>();
            result.put("count", count);
            result.put("sessionId", sessionId);
            return ResponseEntity.ok(buildSuccessResponse("统计会话侧边栏记录数量成功", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("统计会话侧边栏记录数量失败: " + e.getMessage()));
        }
    }

    /**
     * 统计用户固定侧边栏记录数量
     */
    @GetMapping("/count-pinned-items")
    public ResponseEntity<?> countPinnedSidebarItems(@RequestParam Long userId) {
        try {
            long count = sidebarService.countPinnedSidebarItems(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("count", count);
            result.put("userId", userId);
            return ResponseEntity.ok(buildSuccessResponse("统计固定侧边栏记录数量成功", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("统计固定侧边栏记录数量失败: " + e.getMessage()));
        }
    }

    /**
     * 按消息类型统计侧边栏记录
     */
    @GetMapping("/stats-by-type")
    public ResponseEntity<?> getSidebarStatsByMessageType(@RequestParam Long userId) {
        try {
            List<Object[]> stats = sidebarService.getSidebarStatsByMessageType(userId);
            return ResponseEntity.ok(buildSuccessResponse("按类型统计侧边栏记录成功", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("按类型统计侧边栏记录失败: " + e.getMessage()));
        }
    }

    // 清理和维护操作

    /**
     * 清理超过指定天数未查看的非固定侧边栏记录
     */
    @DeleteMapping("/cleanup-stale")
    public ResponseEntity<?> cleanupStaleSidebarItems(@RequestParam(defaultValue = "30") int daysThreshold) {
        try {
            int cleanedCount = sidebarService.cleanupStaleSidebarItems(daysThreshold);
            Map<String, Object> result = new HashMap<>();
            result.put("cleanedCount", cleanedCount);
            result.put("daysThreshold", daysThreshold);
            return ResponseEntity.ok(buildSuccessResponse("清理过期侧边栏记录成功", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("清理过期侧边栏记录失败: " + e.getMessage()));
        }
    }

    /**
     * 自动清理侧边栏记录
     */
    @DeleteMapping("/auto-cleanup")
    public ResponseEntity<?> autoCleanupSidebarItems() {
        try {
            int cleanedCount = sidebarService.autoCleanupSidebarItems();
            Map<String, Object> result = new HashMap<>();
            result.put("cleanedCount", cleanedCount);
            result.put("thresholdDays", 30);
            return ResponseEntity.ok(buildSuccessResponse("自动清理侧边栏记录成功", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("自动清理侧边栏记录失败: " + e.getMessage()));
        }
    }

    // 管理功能

    /**
     * 获取所有侧边栏记录（管理用）
     */
    @GetMapping("/admin/all-items")
    public ResponseEntity<?> getAllSidebarItems() {
        try {
            List<MessageQuoteSidebarEntity> items = sidebarService.getAllSidebarItems();
            return ResponseEntity.ok(buildSuccessResponse("获取所有侧边栏记录成功", items));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("获取所有侧边栏记录失败: " + e.getMessage()));
        }
    }

    /**
     * 获取所有侧边栏记录（分页，管理用）
     */
    @GetMapping("/admin/all-items-page")
    public ResponseEntity<?> getAllSidebarItemsPage(
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<MessageQuoteSidebarEntity> page = sidebarService.getAllSidebarItemsPage(pageable);
            return ResponseEntity.ok(buildSuccessResponse("获取所有侧边栏记录分页成功", page));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("获取所有侧边栏记录分页失败: " + e.getMessage()));
        }
    }

    // 辅助方法

    private Map<String, Object> buildSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("timestamp", Instant.now().toString());
        if (data != null) {
            response.put("data", data);
        }
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