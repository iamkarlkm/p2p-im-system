package com.im.server.repository;

import com.im.server.entity.Notification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 通知Repository
 */
@Repository
public class NotificationRepository {
    
    private final ConcurrentHashMap<Long, Notification> notifications = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);
    
    /**
     * 保存通知
     */
    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            notification.setId(idCounter.getAndIncrement());
        }
        notifications.put(notification.getId(), notification);
        return notification;
    }
    
    /**
     * 根据ID查询
     */
    public Notification findById(Long id) {
        return notifications.get(id);
    }
    
    /**
     * 查询用户的所有通知
     */
    public List<Notification> findByUserId(Long userId) {
        return notifications.values().stream()
                .filter(n -> n.getUserId().equals(userId))
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .collect(Collectors.toList());
    }
    
    /**
     * 查询用户的未读通知
     */
    public List<Notification> findUnreadByUserId(Long userId) {
        return notifications.values().stream()
                .filter(n -> n.getUserId().equals(userId))
                .filter(n -> n.getStatus() == Notification.STATUS_UNREAD)
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .collect(Collectors.toList());
    }
    
    /**
     * 统计未读通知数量
     */
    public int countUnreadByUserId(Long userId) {
        return (int) notifications.values().stream()
                .filter(n -> n.getUserId().equals(userId))
                .filter(n -> n.getStatus() == Notification.STATUS_UNREAD)
                .count();
    }
    
    /**
     * 标记通知为已读
     */
    public void markAsRead(Long notificationId) {
        Notification notification = notifications.get(notificationId);
        if (notification != null) {
            notification.setStatus(Notification.STATUS_READ);
        }
    }
    
    /**
     * 标记用户所有通知为已读
     */
    public void markAllAsRead(Long userId) {
        notifications.values().stream()
                .filter(n -> n.getUserId().equals(userId))
                .filter(n -> n.getStatus() == Notification.STATUS_UNREAD)
                .forEach(n -> n.setStatus(Notification.STATUS_READ));
    }
    
    /**
     * 删除通知
     */
    public void delete(Long notificationId) {
        notifications.remove(notificationId);
    }
}
