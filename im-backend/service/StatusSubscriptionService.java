package com.im.backend.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 状态订阅服务
 * 管理用户状态订阅和通知
 */
@Service
public class StatusSubscriptionService {

    // 用户订阅关系: userId -> 订阅者集合
    private final Map<Long, Set<Long>> statusSubscriptions = new ConcurrentHashMap<>();

    // 反向索引: subscriberId -> 被订阅用户集合
    private final Map<Long, Set<Long>> subscriberIndex = new ConcurrentHashMap<>();

    /**
     * 订阅用户状态
     */
    public void subscribeStatus(Long subscriberId, Long targetUserId) {
        // 添加订阅关系
        statusSubscriptions.computeIfAbsent(targetUserId, k -> new CopyOnWriteArraySet<>())
            .add(subscriberId);

        // 更新反向索引
        subscriberIndex.computeIfAbsent(subscriberId, k -> new CopyOnWriteArraySet<>())
            .add(targetUserId);
    }

    /**
     * 取消订阅用户状态
     */
    public void unsubscribeStatus(Long subscriberId, Long targetUserId) {
        Set<Long> subscribers = statusSubscriptions.get(targetUserId);
        if (subscribers != null) {
            subscribers.remove(subscriberId);
            if (subscribers.isEmpty()) {
                statusSubscriptions.remove(targetUserId);
            }
        }

        Set<Long> subscribedUsers = subscriberIndex.get(subscriberId);
        if (subscribedUsers != null) {
            subscribedUsers.remove(targetUserId);
            if (subscribedUsers.isEmpty()) {
                subscriberIndex.remove(subscriberId);
            }
        }
    }

    /**
     * 取消所有订阅
     */
    public void unsubscribeAll(Long subscriberId) {
        Set<Long> subscribedUsers = subscriberIndex.get(subscriberId);
        if (subscribedUsers != null) {
            for (Long targetUserId : subscribedUsers) {
                Set<Long> subscribers = statusSubscriptions.get(targetUserId);
                if (subscribers != null) {
                    subscribers.remove(subscriberId);
                }
            }
            subscriberIndex.remove(subscriberId);
        }
    }

    /**
     * 通知状态变更
     */
    public void notifyStatusChange(Long userId, String status) {
        Set<Long> subscribers = statusSubscriptions.get(userId);
        if (subscribers != null && !subscribers.isEmpty()) {
            // 实际实现中应该通过WebSocket推送
            // 这里仅记录日志
            System.out.println("用户 " + userId + " 状态变更为 " + status + 
                ", 通知 " + subscribers.size() + " 个订阅者");
        }
    }

    /**
     * 获取用户的订阅者列表
     */
    public Set<Long> getSubscribers(Long userId) {
        return statusSubscriptions.getOrDefault(userId, new CopyOnWriteArraySet<>());
    }

    /**
     * 获取用户订阅的用户列表
     */
    public Set<Long> getSubscriptions(Long subscriberId) {
        return subscriberIndex.getOrDefault(subscriberId, new CopyOnWriteArraySet<>());
    }

    /**
     * 检查是否已订阅
     */
    public boolean isSubscribed(Long subscriberId, Long targetUserId) {
        Set<Long> subscriptions = subscriberIndex.get(subscriberId);
        return subscriptions != null && subscriptions.contains(targetUserId);
    }

    /**
     * 获取订阅统计信息
     */
    public Map<String, Object> getSubscriptionStats() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("totalSubscriptions", statusSubscriptions.size());
        stats.put("totalSubscribers", subscriberIndex.size());

        long totalSubscriberCount = statusSubscriptions.values().stream()
            .mapToLong(Set::size)
            .sum();
        stats.put("totalSubscriberCount", totalSubscriberCount);

        return stats;
    }

    /**
     * 清理用户的所有订阅关系（用户注销时调用）
     */
    public void cleanupUserSubscriptions(Long userId) {
        // 取消该用户作为订阅者的所有订阅
        unsubscribeAll(userId);

        // 从其他用户的订阅者列表中移除该用户
        for (Set<Long> subscribers : statusSubscriptions.values()) {
            subscribers.remove(userId);
        }
    }
}
