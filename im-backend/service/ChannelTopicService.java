package com.im.backend.service;

import com.im.backend.entity.ChannelTopicEntity;
import com.im.backend.repository.ChannelTopicRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ChannelTopicService {

    private final ChannelTopicRepository topicRepository;
    private final ObjectMapper objectMapper;

    public ChannelTopicService(ChannelTopicRepository topicRepository, ObjectMapper objectMapper) {
        this.topicRepository = topicRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ChannelTopicEntity createTopic(String channelId, String title, String content, String authorId, String tags) {
        Integer maxOrder = topicRepository.findMaxRootSortOrder(channelId);

        ChannelTopicEntity topic = new ChannelTopicEntity();
        topic.setTopicId(UUID.randomUUID().toString());
        topic.setChannelId(channelId);
        topic.setTitle(title);
        topic.setContent(content);
        topic.setParentTopicId(null);
        topic.setRootTopicId(null);
        topic.setDepth(0);
        topic.setSortOrder(maxOrder + 1);
        topic.setReplyCount(0L);
        topic.setAuthorId(authorId);
        topic.setCreatedAt(LocalDateTime.now());
        topic.setIsPinned(false);
        topic.setIsLocked(false);
        topic.setStatus("OPEN");
        topic.setTags(tags);
        topic.setViewCount(0L);
        topic.setReactions("{}");

        return topicRepository.save(topic);
    }

    @Transactional
    public ChannelTopicEntity replyToTopic(String channelId, String parentTopicId, String content, String authorId) {
        ChannelTopicEntity parent = topicRepository.findByTopicId(parentTopicId)
            .orElseThrow(() -> new IllegalArgumentException("父话题不存在"));

        if (parent.getIsLocked()) {
            throw new IllegalStateException("话题已锁定，无法回复");
        }

        Integer maxOrder = topicRepository.findMaxChildSortOrder(parentTopicId);

        ChannelTopicEntity reply = new ChannelTopicEntity();
        reply.setTopicId(UUID.randomUUID().toString());
        reply.setChannelId(channelId);
        reply.setTitle(null);
        reply.setContent(content);
        reply.setParentTopicId(parentTopicId);
        reply.setRootTopicId(parent.getRootTopicId() != null ? parent.getRootTopicId() : parentTopicId);
        reply.setDepth(parent.getDepth() + 1);
        reply.setSortOrder(maxOrder + 1);
        reply.setReplyCount(0L);
        reply.setAuthorId(authorId);
        reply.setCreatedAt(LocalDateTime.now());
        reply.setIsPinned(false);
        reply.setIsLocked(false);
        reply.setStatus("OPEN");
        reply.setViewCount(0L);
        reply.setReactions("{}");

        parent.setReplyCount(parent.getReplyCount() + 1);
        parent.setLastReplyAt(LocalDateTime.now());
        topicRepository.save(parent);

        return topicRepository.save(reply);
    }

    public Optional<ChannelTopicEntity> getTopic(String topicId) {
        return topicRepository.findByTopicId(topicId);
    }

    public List<ChannelTopicEntity> getTopics(String channelId, int page, int size) {
        return topicRepository.findPinnedThenSorted(channelId);
    }

    public List<ChannelTopicEntity> getRecentTopics(String channelId, int limit) {
        return topicRepository.findRecentlyActive(channelId, PageRequest.of(0, limit));
    }

    public List<ChannelTopicEntity> getTopicThread(String rootTopicId) {
        return topicRepository.findByRootTopicIdAndStatusOrderBySortOrderAsc(rootTopicId, "OPEN");
    }

    public List<ChannelTopicEntity> getReplies(String parentTopicId) {
        return topicRepository.findByParentTopicIdOrderBySortOrderAsc(parentTopicId);
    }

    @Transactional
    public ChannelTopicEntity updateTopic(String topicId, String title, String content, String tags) {
        ChannelTopicEntity topic = topicRepository.findByTopicId(topicId)
            .orElseThrow(() -> new IllegalArgumentException("话题不存在"));
        if (title != null) topic.setTitle(title);
        if (content != null) topic.setContent(content);
        if (tags != null) topic.setTags(tags);
        topic.setUpdatedAt(LocalDateTime.now());
        return topicRepository.save(topic);
    }

    @Transactional
    public void pinTopic(String topicId, boolean pinned) {
        topicRepository.findByTopicId(topicId).ifPresent(topic -> {
            topic.setIsPinned(pinned);
            topic.setUpdatedAt(LocalDateTime.now());
            topicRepository.save(topic);
        });
    }

    @Transactional
    public void lockTopic(String topicId, boolean locked) {
        topicRepository.findByTopicId(topicId).ifPresent(topic -> {
            topic.setIsLocked(locked);
            topic.setUpdatedAt(LocalDateTime.now());
            topicRepository.save(topic);
        });
    }

    @Transactional
    public void deleteTopic(String topicId) {
        topicRepository.findByTopicId(topicId).ifPresent(topic -> {
            topic.setStatus("DELETED");
            topic.setUpdatedAt(LocalDateTime.now());
            topicRepository.save(topic);
        });
    }

    @Transactional
    public void addReaction(String topicId, String emoji, String userId) {
        topicRepository.findByTopicId(topicId).ifPresent(topic -> {
            Map<String, Integer> reactions = parseReactions(topic.getReactions());
            String key = emoji + ":" + userId;
            int current = reactions.getOrDefault(key, 0);
            reactions.put(key, current + 1);
            topic.setReactions(toJson(reactions));
            topicRepository.save(topic);
        });
    }

    @Transactional
    public void removeReaction(String topicId, String emoji, String userId) {
        topicRepository.findByTopicId(topicId).ifPresent(topic -> {
            Map<String, Integer> reactions = parseReactions(topic.getReactions());
            String key = emoji + ":" + userId;
            reactions.remove(key);
            topic.setReactions(toJson(reactions));
            topicRepository.save(topic);
        });
    }

    @Transactional
    public void incrementViewCount(String topicId) {
        topicRepository.findByTopicId(topicId).ifPresent(topic -> {
            topic.setViewCount(topic.getViewCount() + 1);
            topicRepository.save(topic);
        });
    }

    public long getTopicCount(String channelId) {
        return topicRepository.countByChannelId(channelId);
    }

    public Map<String, Integer> parseReactions(String json) {
        if (json == null || json.isEmpty()) return new HashMap<>();
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Integer>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public String toJson(Map<String, Integer> reactions) {
        try {
            return objectMapper.writeValueAsString(reactions);
        } catch (Exception e) {
            return "{}";
        }
    }
}
