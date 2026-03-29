package com.im.backend.controller;

import com.im.backend.dto.ScheduledMessageRecallDTO;
import com.im.backend.service.ScheduledMessageRecallService;
import com.im.backend.util.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息定时撤回控制器
 */
@RestController
@RequestMapping("/api/scheduled-recall")
public class ScheduledMessageRecallController {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduledMessageRecallController.class);
    
    @Autowired
    private ScheduledMessageRecallService scheduledRecallService;
    
    /**
     * 创建定时撤回任务
     */
    @PostMapping("/create")
    public ResponseResult<ScheduledMessageRecallDTO> createScheduledRecall(
            @RequestBody Map<String, Object> request,
            @RequestAttribute("userId") Long userId) {
        try {
            Long messageId = Long.valueOf(request.get("messageId").toString());
            Long conversationId = Long.valueOf(request.get("conversationId").toString());
            String conversationType = (String) request.get("conversationType");
            String messageContent = (String) request.get("messageContent");
            Integer scheduledSeconds = Integer.valueOf(request.get("scheduledSeconds").toString());
            String recallReason = (String) request.get("recallReason");
            Boolean notifyReceivers = (Boolean) request.get("notifyReceivers");
            String customNotifyMessage = (String) request.get("customNotifyMessage");
            Boolean isCancelable = (Boolean) request.get("isCancelable");
            
            ScheduledMessageRecallDTO dto = scheduledRecallService.createScheduledRecall(
                userId, messageId, conversationId,
                com.im.backend.model.ScheduledMessageRecall.ConversationType.valueOf(conversationType),
                messageContent, scheduledSeconds, recallReason,
                notifyReceivers, customNotifyMessage, isCancelable);
            
            return ResponseResult.success(dto);
        } catch (Exception e) {
            logger.error("创建定时撤回失败", e);
            return ResponseResult.error(e.getMessage());
        }
    }
    
    /**
     * 取消定时撤回
     */
    @PostMapping("/{id}/cancel")
    public ResponseResult<ScheduledMessageRecallDTO> cancelScheduledRecall(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        try {
            ScheduledMessageRecallDTO dto = scheduledRecallService.cancelScheduledRecall(id, userId);
            return ResponseResult.success(dto);
        } catch (Exception e) {
            logger.error("取消定时撤回失败: id={}", id, e);
            return ResponseResult.error(e.getMessage());
        }
    }
    
    /**
     * 获取定时撤回详情
     */
    @GetMapping("/{id}")
    public ResponseResult<ScheduledMessageRecallDTO> getScheduledRecall(
            @PathVariable Long id) {
        return scheduledRecallService.getScheduledRecallById(id)
            .map(ResponseResult::success)
            .orElse(ResponseResult.error("定时撤回任务不存在"));
    }
    
    /**
     * 根据消息ID获取定时撤回
     */
    @GetMapping("/message/{messageId}")
    public ResponseResult<ScheduledMessageRecallDTO> getByMessageId(
            @PathVariable Long messageId) {
        return scheduledRecallService.getScheduledRecallByMessageId(messageId)
            .map(ResponseResult::success)
            .orElse(ResponseResult.error("未找到定时撤回任务"));
    }
    
    /**
     * 获取用户的所有定时撤回
     */
    @GetMapping("/list")
    public ResponseResult<List<ScheduledMessageRecallDTO>> getUserRecalls(
            @RequestAttribute("userId") Long userId) {
        List<ScheduledMessageRecallDTO> list = scheduledRecallService.getUserScheduledRecalls(userId);
        return ResponseResult.success(list);
    }
    
    /**
     * 分页获取用户的定时撤回
     */
    @GetMapping("/page")
    public ResponseResult<Page<ScheduledMessageRecallDTO>> getUserRecallsPage(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ScheduledMessageRecallDTO> result = scheduledRecallService.getUserScheduledRecalls(userId, pageable);
        return ResponseResult.success(result);
    }
    
    /**
     * 获取用户待执行的定时撤回
     */
    @GetMapping("/pending")
    public ResponseResult<List<ScheduledMessageRecallDTO>> getPendingRecalls(
            @RequestAttribute("userId") Long userId) {
        List<ScheduledMessageRecallDTO> list = scheduledRecallService.getUserPendingRecalls(userId);
        return ResponseResult.success(list);
    }
    
    /**
     * 获取用户定时撤回统计
     */
    @GetMapping("/stats")
    public ResponseResult<Map<String, Object>> getRecallStats(
            @RequestAttribute("userId") Long userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pendingCount", scheduledRecallService.countUserPendingRecalls(userId));
        stats.put("totalCount", scheduledRecallService.countUserRecalls(userId));
        return ResponseResult.success(stats);
    }
    
    /**
     * 检查消息是否已设置定时撤回
     */
    @GetMapping("/check/{messageId}")
    public ResponseResult<Map<String, Object>> checkMessageScheduled(
            @PathVariable Long messageId) {
        Map<String, Object> result = new HashMap<>();
        result.put("isScheduled", scheduledRecallService.isMessageScheduledForRecall(messageId));
        return ResponseResult.success(result);
    }
    
    /**
     * 删除定时撤回任务
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Void> deleteScheduledRecall(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        try {
            scheduledRecallService.deleteScheduledRecall(id, userId);
            return ResponseResult.success(null);
        } catch (Exception e) {
            logger.error("删除定时撤回失败: id={}", id, e);
            return ResponseResult.error(e.getMessage());
        }
    }
    
    /**
     * 更新定时撤回时间
     */
    @PostMapping("/{id}/update-time")
    public ResponseResult<ScheduledMessageRecallDTO> updateScheduledTime(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            @RequestAttribute("userId") Long userId) {
        try {
            Integer newSeconds = Integer.valueOf(request.get("newSeconds").toString());
            ScheduledMessageRecallDTO dto = scheduledRecallService.updateScheduledTime(id, userId, newSeconds);
            return ResponseResult.success(dto);
        } catch (Exception e) {
            logger.error("更新定时撤回时间失败: id={}", id, e);
            return ResponseResult.error(e.getMessage());
        }
    }
    
    /**
     * 获取推荐的时间选项
     */
    @GetMapping("/time-options")
    public ResponseResult<List<Integer>> getTimeOptions() {
        return ResponseResult.success(scheduledRecallService.getRecommendedTimeOptions());
    }
    
    /**
     * 手动执行定时撤回（测试接口）
     */
    @PostMapping("/{id}/execute")
    public ResponseResult<Map<String, Object>> executeRecall(
            @PathVariable Long id) {
        boolean success = scheduledRecallService.executeRecall(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseResult.success(result);
    }
    
    /**
     * 批量执行到期任务（定时任务调用）
     */
    @PostMapping("/batch-execute")
    public ResponseResult<Map<String, Object>> batchExecute() {
        int count = scheduledRecallService.batchExecuteDueRecalls();
        Map<String, Object> result = new HashMap<>();
        result.put("executedCount", count);
        return ResponseResult.success(result);
    }
    
    /**
     * 清理过期记录（管理接口）
     */
    @PostMapping("/cleanup")
    public ResponseResult<Map<String, Object>> cleanupOldRecords(
            @RequestParam(defaultValue = "30") int daysToKeep) {
        int count = scheduledRecallService.cleanupOldRecords(daysToKeep);
        Map<String, Object> result = new HashMap<>();
        result.put("deletedCount", count);
        return ResponseResult.success(result);
    }
}
