package com.im.backend.controller;

import com.im.backend.entity.ChannelTopicEntity;
import com.im.backend.service.ChannelTopicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 频道话题/讨论串 REST API
 * 频道内的二级话题树状对话系统
 */
@RestController
@RequestMapping("/api/channel-topics")
public class ChannelTopicController {

    private final ChannelTopicService topicService;

    public ChannelTopicController(ChannelTopicService topicService) {
        this.topicService = topicService;
    }

    // ========== 话题 CRUD ==========

    @PostMapping
    public ResponseEntity<ChannelTopicEntity> createTopic(@RequestBody Map<String, String> body) {
        String channelId = body.get("channelId");
        String title = body.get("title");
        String content = body.get("content");
        String authorId = body.get("authorId");
        String tags = body.get("tags");
        ChannelTopicEntity topic = topicService.createTopic(channelId, title, content, authorId, tags);
        return ResponseEntity.ok(topic);
    }

    @GetMapping("/{topicId}")
    public ResponseEntity<ChannelTopicEntity> getTopic(@PathVariable String topicId) {
        topicService.incrementViewCount(topicId);
        return topicService.getTopic(topicId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{topicId}")
    public ResponseEntity<ChannelTopicEntity> updateTopic(
            @PathVariable String topicId,
            @RequestBody Map<String, String> body) {
        String title = body.get("title");
        String content = body.get("content");
        String tags = body.get("tags");
        ChannelTopicEntity topic = topicService.updateTopic(topicId, title, content, tags);
        return ResponseEntity.ok(topic);
    }

    @DeleteMapping("/{topicId}")
    public ResponseEntity<Void> deleteTopic(@PathVariable String topicId) {
        topicService.deleteTopic(topicId);
        return ResponseEntity.ok().build();
    }

    // ========== 回复 ==========

    @PostMapping("/{topicId}/replies")
    public ResponseEntity<ChannelTopicEntity> replyToTopic(
            @PathVariable String topicId,
            @RequestBody Map<String, String> body) {
        String channelId = body.get("channelId");
        String content = body.get("content");
        String authorId = body.get("authorId");
        ChannelTopicEntity reply = topicService.replyToTopic(channelId, topicId, content, authorId);
        return ResponseEntity.ok(reply);
    }

    @GetMapping("/{topicId}/replies")
    public ResponseEntity<List<ChannelTopicEntity>> getReplies(@PathVariable String topicId) {
        List<ChannelTopicEntity> replies = topicService.getReplies(topicId);
        return ResponseEntity.ok(replies);
    }

    @GetMapping("/{topicId}/thread")
    public ResponseEntity<List<ChannelTopicEntity>> getThread(@PathVariable String topicId) {
        List<ChannelTopicEntity> thread = topicService.getTopicThread(topicId);
        return ResponseEntity.ok(thread);
    }

    // ========== 话题列表 ==========

    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<ChannelTopicEntity>> getTopics(
            @PathVariable String channelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<ChannelTopicEntity> topics = topicService.getTopics(channelId, page, size);
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/channel/{channelId}/recent")
    public ResponseEntity<List<ChannelTopicEntity>> getRecentTopics(
            @PathVariable String channelId,
            @RequestParam(defaultValue = "10") int limit) {
        List<ChannelTopicEntity> topics = topicService.getRecentTopics(channelId, limit);
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/channel/{channelId}/count")
    public ResponseEntity<Map<String, Object>> getTopicCount(@PathVariable String channelId) {
        long count = topicService.getTopicCount(channelId);
        Map<String, Object> result = new HashMap<>();
        result.put("channelId", channelId);
        result.put("topicCount", count);
        return ResponseEntity.ok(result);
    }

    // ========== 置顶/锁定 ==========

    @PostMapping("/{topicId}/pin")
    public ResponseEntity<Map<String, Object>> pinTopic(
            @PathVariable String topicId,
            @RequestBody Map<String, Boolean> body) {
        boolean pinned = body.getOrDefault("pinned", true);
        topicService.pinTopic(topicId, pinned);
        Map<String, Object> result = new HashMap<>();
        result.put("topicId", topicId);
        result.put("isPinned", pinned);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{topicId}/lock")
    public ResponseEntity<Map<String, Object>> lockTopic(
            @PathVariable String topicId,
            @RequestBody Map<String, Boolean> body) {
        boolean locked = body.getOrDefault("locked", true);
        topicService.lockTopic(topicId, locked);
        Map<String, Object> result = new HashMap<>();
        result.put("topicId", topicId);
        result.put("isLocked", locked);
        return ResponseEntity.ok(result);
    }

    // ========== 表情反应 ==========

    @PostMapping("/{topicId}/reactions")
    public ResponseEntity<Map<String, Object>> addReaction(
            @PathVariable String topicId,
            @RequestBody Map<String, String> body) {
        String emoji = body.get("emoji");
        String userId = body.get("userId");
        topicService.addReaction(topicId, emoji, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("topicId", topicId);
        result.put("emoji", emoji);
        result.put("action", "added");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{topicId}/reactions")
    public ResponseEntity<Map<String, Object>> removeReaction(
            @PathVariable String topicId,
            @RequestBody Map<String, String> body) {
        String emoji = body.get("emoji");
        String userId = body.get("userId");
        topicService.removeReaction(topicId, emoji, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("topicId", topicId);
        result.put("emoji", emoji);
        result.put("action", "removed");
        return ResponseEntity.ok(result);
    }

    // ========== 用户话题 ==========

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<ChannelTopicEntity>> getAuthorTopics(
            @PathVariable String authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        // NOTE: Would need to add this method to service
        Map<String, Object> result = new HashMap<>();
        result.put("authorId", authorId);
        result.put("page", page);
        result.put("size", size);
        return ResponseEntity.ok(List.of());
    }
}
