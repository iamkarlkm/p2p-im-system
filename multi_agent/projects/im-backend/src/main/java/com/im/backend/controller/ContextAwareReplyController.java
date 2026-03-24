package com.im.backend.controller;

import com.im.backend.entity.ContextAwareReplyEntity;
import com.im.backend.service.ContextAwareReplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 上下文感知智能回复生成器REST API控制器
 */
@RestController
@RequestMapping("/api/v1/context-aware-reply")
@Api(tags = "上下文感知智能回复生成器")
@Slf4j
public class ContextAwareReplyController {
    
    @Autowired
    private ContextAwareReplyService replyService;
    
    // 基础CRUD端点
    
    @PostMapping
    @ApiOperation("创建智能回复记录")
    public ResponseEntity<ContextAwareReplyEntity> createReply(
            @ApiParam(value = "回复记录数据", required = true)
            @RequestBody ContextAwareReplyEntity reply) {
        try {
            ContextAwareReplyEntity created = replyService.createReply(reply);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            log.error("创建回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/batch")
    @ApiOperation("批量创建智能回复记录")
    public ResponseEntity<List<ContextAwareReplyEntity>> createReplies(
            @ApiParam(value = "回复记录列表", required = true)
            @RequestBody List<ContextAwareReplyEntity> replies) {
        try {
            List<ContextAwareReplyEntity> created = replyService.createReplies(replies);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            log.error("批量创建回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    @ApiOperation("根据ID获取智能回复记录")
    public ResponseEntity<ContextAwareReplyEntity> getReplyById(
            @ApiParam(value = "回复记录ID", required = true)
            @PathVariable Long id) {
        try {
            Optional<ContextAwareReplyEntity> reply = replyService.getReplyById(id);
            return reply.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("获取回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    @ApiOperation("更新智能回复记录")
    public ResponseEntity<ContextAwareReplyEntity> updateReply(
            @ApiParam(value = "回复记录ID", required = true)
            @PathVariable Long id,
            @ApiParam(value = "更新数据", required = true)
            @RequestBody ContextAwareReplyEntity updateData) {
        try {
            ContextAwareReplyEntity updated = replyService.updateReply(id, updateData);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("不存在")) {
                return ResponseEntity.notFound().build();
            }
            log.error("更新回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("更新回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation("删除智能回复记录")
    public ResponseEntity<Void> deleteReply(
            @ApiParam(value = "回复记录ID", required = true)
            @PathVariable Long id) {
        try {
            replyService.deleteReply(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("不存在")) {
                return ResponseEntity.notFound().build();
            }
            log.error("删除回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("删除回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/batch")
    @ApiOperation("批量删除智能回复记录")
    public ResponseEntity<Void> deleteReplies(
            @ApiParam(value = "回复记录ID列表", required = true)
            @RequestBody List<Long> ids) {
        try {
            replyService.deleteReplies(ids);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("批量删除回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 查询端点
    
    @GetMapping("/user/{userId}")
    @ApiOperation("查询用户的所有回复记录")
    public ResponseEntity<List<ContextAwareReplyEntity>> getRepliesByUser(
            @ApiParam(value = "用户ID", required = true)
            @PathVariable String userId) {
        try {
            List<ContextAwareReplyEntity> replies = replyService.getRepliesByUser(userId);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            log.error("查询用户回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user/{userId}/paged")
    @ApiOperation("分页查询用户的回复记录")
    public ResponseEntity<Page<ContextAwareReplyEntity>> getRepliesByUserPaged(
            @ApiParam(value = "用户ID", required = true)
            @PathVariable String userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        try {
            Page<ContextAwareReplyEntity> page = replyService.getRepliesByUser(userId, pageable);
            return ResponseEntity.ok(page);
        } catch (Exception e) {
            log.error("分页查询用户回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/session/{sessionId}")
    @ApiOperation("查询会话的所有回复记录")
    public ResponseEntity<List<ContextAwareReplyEntity>> getRepliesBySession(
            @ApiParam(value = "会话ID", required = true)
            @PathVariable String sessionId) {
        try {
            List<ContextAwareReplyEntity> replies = replyService.getRepliesBySession(sessionId);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            log.error("查询会话回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user/{userId}/session/{sessionId}")
    @ApiOperation("查询用户和会话的回复记录")
    public ResponseEntity<List<ContextAwareReplyEntity>> getRepliesByUserAndSession(
            @ApiParam(value = "用户ID", required = true)
            @PathVariable String userId,
            @ApiParam(value = "会话ID", required = true)
            @PathVariable String sessionId) {
        try {
            List<ContextAwareReplyEntity> replies = replyService.getRepliesByUserAndSession(userId, sessionId);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            log.error("查询用户和会话回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/trigger-message/{messageId}")
    @ApiOperation("查询触发消息的回复记录")
    public ResponseEntity<ContextAwareReplyEntity> getReplyByTriggerMessage(
            @ApiParam(value = "触发消息ID", required = true)
            @PathVariable String messageId) {
        try {
            Optional<ContextAwareReplyEntity> reply = replyService.getReplyByTriggerMessage(messageId);
            return reply.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("查询触发消息回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/status/{status}")
    @ApiOperation("查询指定状态的回复记录")
    public ResponseEntity<List<ContextAwareReplyEntity>> getRepliesByStatus(
            @ApiParam(value = "状态", required = true)
            @PathVariable String status) {
        try {
            List<ContextAwareReplyEntity> replies = replyService.getRepliesByStatus(status);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            log.error("查询状态回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/used")
    @ApiOperation("查询已使用的回复记录")
    public ResponseEntity<List<ContextAwareReplyEntity>> getUsedReplies() {
        try {
            List<ContextAwareReplyEntity> replies = replyService.getUsedReplies();
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            log.error("查询已使用回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/high-quality")
    @ApiOperation("查询高质量的回复记录")
    public ResponseEntity<List<ContextAwareReplyEntity>> getHighQualityReplies() {
        try {
            List<ContextAwareReplyEntity> replies = replyService.getHighQualityReplies();
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            log.error("查询高质量回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/high-confidence")
    @ApiOperation("查询高置信度的回复记录")
    public ResponseEntity<List<ContextAwareReplyEntity>> getHighConfidenceReplies() {
        try {
            List<ContextAwareReplyEntity> replies = replyService.getHighConfidenceReplies();
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            log.error("查询高置信度回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 意图相关端点
    
    @GetMapping("/intent/{intent}")
    @ApiOperation("查询指定意图的回复记录")
    public ResponseEntity<List<ContextAwareReplyEntity>> getRepliesByIntent(
            @ApiParam(value = "意图", required = true)
            @PathVariable String intent) {
        try {
            List<ContextAwareReplyEntity> replies = replyService.getRepliesByIntent(intent);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            log.error("查询意图回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user/{userId}/intent/{intent}")
    @ApiOperation("查询用户指定意图的回复记录")
    public ResponseEntity<List<ContextAwareReplyEntity>> getRepliesByUserAndIntent(
            @ApiParam(value = "用户ID", required = true)
            @PathVariable String userId,
            @ApiParam(value = "意图", required = true)
            @PathVariable String intent) {
        try {
            List<ContextAwareReplyEntity> replies = replyService.getRepliesByUserAndIntent(userId, intent);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            log.error("查询用户意图回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user/{userId}/top-intents")
    @ApiOperation("获取用户最常用的意图")
    public ResponseEntity<Map<String, Long>> getUserTopIntents(
            @ApiParam(value = "用户ID", required = true)
            @PathVariable String userId) {
        try {
            Map<String, Long> intentCounts = replyService.getUserTopIntents(userId);
            return ResponseEntity.ok(intentCounts);
        } catch (Exception e) {
            log.error("获取用户常用意图失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 语言风格相关端点
    
    @GetMapping("/language-style/{style}")
    @ApiOperation("查询指定语言风格的回复记录")
    public ResponseEntity<List<ContextAwareReplyEntity>> getRepliesByLanguageStyle(
            @ApiParam(value = "语言风格", required = true)
            @PathVariable String style) {
        try {
            List<ContextAwareReplyEntity> replies = replyService.getRepliesByLanguageStyle(style);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            log.error("查询语言风格回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user/{userId}/language-style/{style}")
    @ApiOperation("查询用户指定语言风格的回复记录")
    public ResponseEntity<List<ContextAwareReplyEntity>> getRepliesByUserAndLanguageStyle(
            @ApiParam(value = "用户ID", required = true)
            @PathVariable String userId,
            @ApiParam(value = "语言风格", required = true)
            @PathVariable String style) {
        try {
            List<ContextAwareReplyEntity> replies = replyService.getRepliesByUserAndLanguageStyle(userId, style);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            log.error("查询用户语言风格回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user/{userId}/top-language-styles")
    @ApiOperation("获取用户最常用的语言风格")
    public ResponseEntity<Map<String, Long>> getUserTopLanguageStyles(
            @ApiParam(value = "用户ID", required = true)
            @PathVariable String userId) {
        try {
            Map<String, Long> styleCounts = replyService.getUserTopLanguageStyles(userId);
            return ResponseEntity.ok(styleCounts);
        } catch (Exception e) {
            log.error("获取用户常用语言风格失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 时间范围查询端点
    
    @GetMapping("/date-range")
    @ApiOperation("查询时间范围内的回复记录")
    public ResponseEntity<List<ContextAwareReplyEntity>> getRepliesByDateRange(
            @ApiParam(value = "开始时间", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @ApiParam(value = "结束时间", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        try {
            List<ContextAwareReplyEntity> replies = replyService.getRepliesByDateRange(start, end);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            log.error("查询时间范围回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user/{userId}/date-range")
    @ApiOperation("查询用户时间范围内的回复记录")
    public ResponseEntity<List<ContextAwareReplyEntity>> getRepliesByUserAndDateRange(
            @ApiParam(value = "用户ID", required = true)
            @PathVariable String userId,
            @ApiParam(value = "开始时间", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @ApiParam(value = "结束时间", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        try {
            List<ContextAwareReplyEntity> replies = replyService.getRepliesByUserAndDateRange(userId, start, end);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            log.error("查询用户时间范围回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 统计端点
    
    @GetMapping("/count/user/{userId}")
    @ApiOperation("统计用户回复记录数量")
    public ResponseEntity<Long> countRepliesByUser(
            @ApiParam(value = "用户ID", required = true)
            @PathVariable String userId) {
        try {
            long count = replyService.countRepliesByUser(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("统计用户回复记录数量失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/count/session/{sessionId}")
    @ApiOperation("统计会话回复记录数量")
    public ResponseEntity<Long> countRepliesBySession(
            @ApiParam(value = "会话ID", required = true)
            @PathVariable String sessionId) {
        try {
            long count = replyService.countRepliesBySession(sessionId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("统计会话回复记录数量失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/count/status/{status}")
    @ApiOperation("统计指定状态回复记录数量")
    public ResponseEntity<Long> countRepliesByStatus(
            @ApiParam(value = "状态", required = true)
            @PathVariable String status) {
        try {
            long count = replyService.countRepliesByStatus(status);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("统计状态回复记录数量失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/count/used")
    @ApiOperation("统计已使用回复记录数量")
    public ResponseEntity<Long> countUsedReplies() {
        try {
            long count = replyService.countUsedReplies();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("统计已使用回复记录数量失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/count/high-quality")
    @ApiOperation("统计高质量回复记录数量")
    public ResponseEntity<Long> countHighQualityReplies() {
        try {
            long count = replyService.countHighQualityReplies();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("统计高质量回复记录数量失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/stats/intent-distribution")
    @ApiOperation("获取意图分布统计")
    public ResponseEntity<Map<String, Long>> getIntentDistribution() {
        try {
            Map<String, Long> distribution = replyService.getIntentDistribution();
            return ResponseEntity.ok(distribution);
        } catch (Exception e) {
            log.error("获取意图分布统计失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/stats/language-style-distribution")
    @ApiOperation("获取语言风格分布统计")
    public ResponseEntity<Map<String, Long>> getLanguageStyleDistribution() {
        try {
            Map<String, Long> distribution = replyService.getLanguageStyleDistribution();
            return ResponseEntity.ok(distribution);
        } catch (Exception e) {
            log.error("获取语言风格分布统计失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/stats/average-feedback-score")
    @ApiOperation("获取平均反馈评分")
    public ResponseEntity<Double> getAverageFeedbackScore() {
        try {
            Double score = replyService.getAverageFeedbackScore();
            return ResponseEntity.ok(score);
        } catch (Exception e) {
            log.error("获取平均反馈评分失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/stats/average-generation-time")
    @ApiOperation("获取平均生成时间")
    public ResponseEntity<Double> getAverageGenerationTime() {
        try {
            Double time = replyService.getAverageGenerationTime();
            return ResponseEntity.ok(time);
        } catch (Exception e) {
            log.error("获取平均生成时间失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 高级操作端点
    
    @GetMapping("/search")
    @ApiOperation("搜索回复记录")
    public ResponseEntity<List<ContextAwareReplyEntity>> searchReplies(
            @ApiParam(value = "搜索关键词", required = true)
            @RequestParam String keyword) {
        try {
            List<ContextAwareReplyEntity> results = replyService.searchReplies(keyword);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("搜索回复记录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/{id}/mark-used")
    @ApiOperation("标记回复为已使用")
    public ResponseEntity<Void> markAsUsed(
            @ApiParam(value = "回复记录ID", required = true)
            @PathVariable Long id) {
        try {
            replyService.markAsUsed(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("标记回复为已使用失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/batch/mark-used")
    @ApiOperation("批量标记为已使用")
    public ResponseEntity<Void> markMultipleAsUsed(
            @ApiParam(value = "回复记录ID列表", required = true)
            @RequestBody List<Long> ids) {
        try {
            replyService.markMultipleAsUsed(ids);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("批量标记为已使用失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/{id}/feedback")
    @ApiOperation("提交用户反馈")
    public ResponseEntity<Void> submitFeedback(
            @ApiParam(value = "回复记录ID", required = true)
            @PathVariable Long id,
            @ApiParam(value = "评分 (1-5)", required = true)
            @RequestParam Integer score,
            @ApiParam(value = "评论")
            @RequestParam(required = false) String comment) {
        try {
            if (score < 1 || score > 5) {
                return ResponseEntity.badRequest().build();
            }
            replyService.submitFeedback(id, score, comment);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("提交用户反馈失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user/{userId}/recent")
    @ApiOperation("获取最近N条用户回复")
    public ResponseEntity<List<ContextAwareReplyEntity>> getRecentRepliesByUser(
            @ApiParam(value = "用户ID", required = true)
            @PathVariable String userId,
            @ApiParam(value = "返回数量", defaultValue = "10")
            @RequestParam(defaultValue = "10") int limit) {
        try {
            if (limit <= 0 || limit > 100) {
                return ResponseEntity.badRequest().build();
            }
            List<ContextAwareReplyEntity> replies = replyService.getRecentRepliesByUser(userId, limit);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            log.error("获取最近用户回复失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/cleanup/expired")
    @ApiOperation("清理过期回复")
    public ResponseEntity<Integer> cleanupExpiredReplies() {
        try {
            int deleted = replyService.cleanupExpiredReplies();
            return ResponseEntity.ok(deleted);
        } catch (Exception e) {
            log.error("清理过期回复失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/cleanup/low-quality")
    @ApiOperation("清理低质量回复")
    public ResponseEntity<Integer> cleanupLowQualityReplies() {
        try {
            int deleted = replyService.cleanupLowQualityReplies();
            return ResponseEntity.ok(deleted);
        } catch (Exception e) {
            log.error("清理低质量回复失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 内容获取端点
    
    @GetMapping("/{id}/candidates")
    @ApiOperation("获取回复候选列表")
    public ResponseEntity<List<String>> getReplyCandidates(
            @ApiParam(value = "回复记录ID", required = true)
            @PathVariable Long id) {
        try {
            List<String> candidates = replyService.getReplyCandidates(id);
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            log.error("获取回复候选列表失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/emojis")
    @ApiOperation("获取推荐的表情符号列表")
    public ResponseEntity<List<String>> getRecommendedEmojis(
            @ApiParam(value = "回复记录ID", required = true)
            @PathVariable Long id) {
        try {
            List<String> emojis = replyService.getRecommendedEmojis(id);
            return ResponseEntity.ok(emojis);
        } catch (Exception e) {
            log.error("获取推荐表情符号失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/personalization")
    @ApiOperation("获取个性化特征")
    public ResponseEntity<Map<String, Object>> getPersonalizationFeatures(
            @ApiParam(value = "回复记录ID", required = true)
            @PathVariable Long id) {
        try {
            Map<String, Object> features = replyService.getPersonalizationFeatures(id);
            return ResponseEntity.ok(features);
        } catch (Exception e) {
            log.error("获取个性化特征失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/options")
    @ApiOperation("获取生成选项")
    public ResponseEntity<Map<String, Object>> getGenerationOptions(
            @ApiParam(value = "回复记录ID", required = true)
            @PathVariable Long id) {
        try {
            Map<String, Object> options = replyService.getGenerationOptions(id);
            return ResponseEntity.ok(options);
        } catch (Exception e) {
            log.error("获取生成选项失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 智能生成端点
    
    @PostMapping("/generate")
    @ApiOperation("生成智能回复")
    public ResponseEntity<ContextAwareReplyEntity> generateReply(
            @ApiParam(value = "用户ID", required = true)
            @RequestParam String userId,
            @ApiParam(value = "会话ID")
            @RequestParam(required = false) String sessionId,
            @ApiParam(value = "触发消息内容", required = true)
            @RequestParam String triggerMessageContent) {
        try {
            // 这里简化了上下文，实际应该传递完整的上下文信息
            ContextAwareReplyEntity reply = replyService.generateReply(
                    userId, sessionId, triggerMessageContent, null);
            return ResponseEntity.ok(reply);
        } catch (Exception e) {
            log.error("生成智能回复失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 健康检查端点
    
    @GetMapping("/health")
    @ApiOperation("健康检查")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            Map<String, Object> health = new HashMap<>();
            health.put("status", "UP");
            health.put("timestamp", LocalDateTime.now());
            
            // 基本统计
            health.put("totalReplies", replyService.countRepliesByStatus(ContextAwareReplyEntity.Status.GENERATED));
            health.put("usedReplies", replyService.countUsedReplies());
            health.put("highQualityReplies", replyService.countHighQualityReplies());
            
            // 平均性能
            Double avgScore = replyService.getAverageFeedbackScore();
            Double avgTime = replyService.getAverageGenerationTime();
            health.put("averageFeedbackScore", avgScore != null ? avgScore : 0);
            health.put("averageGenerationTimeMs", avgTime != null ? avgTime : 0);
            
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            log.error("健康检查失败: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("status", "DOWN");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}