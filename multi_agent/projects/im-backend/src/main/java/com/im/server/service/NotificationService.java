package com.im.server.service;

import com.im.server.entity.Notification;
import com.im.server.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知服务
 */
@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    /**
     * 创建通知
     */
    public Notification createNotification(Long userId, Integer type, String title, 
                                          String content, Long relatedId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedId(relatedId);
        notification.setStatus(Notification.STATUS_UNREAD);
        notification.setCreateTime(LocalDateTime.now());
        
        return notificationRepository.save(notification);
    }
    
    /**
     * 创建好友请求通知
     */
    public Notification createFriendRequestNotification(Long fromUserId, Long toUserId, Long requestId) {
        return createNotification(
                toUserId,
                Notification.TYPE_FRIEND_REQUEST,
                "新的好友请求",
                "用户 " + fromUserId + " 请求添加您为好友",
                requestId
        );
    }
    
    /**
     * 创建好友添加成功通知
     */
    public Notification createFriendAddedNotification(Long userId, Long friendId) {
        return createNotification(
                userId,
                Notification.TYPE_FRIEND_ADDED,
                "好友添加成功",
                "您已与用户 " + friendId + " 成为好友",
                friendId
        );
    }
    
    /**
     * 创建群邀请通知
     */
    public Notification createGroupInviteNotification(Long userId, Long groupId, Long inviteId) {
        return createNotification(
                userId,
                Notification.TYPE_GROUP_INVITE,
                "群邀请",
                "您收到了一个群邀请",
                groupId
        );
    }
    
    /**
     * 创建群成员加入通知
     */
    public Notification createGroupMemberJoinedNotification(Long userId, Long groupId, Long memberId) {
        return createNotification(
                userId,
                Notification.TYPE_GROUP_MEMBER_JOINED,
                "新成员加入",
                "有新成员加入了群聊",
                groupId
        );
    }
    
    /**
     * 创建系统通知
     */
    public Notification createSystemNotification(Long userId, String title, String content) {
        return createNotification(
                userId,
                Notification.TYPE_SYSTEM_NOTIFICATION,
                title,
                content,
                null
        );
    }
    
    /**
     * 获取用户的所有通知
     */
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
    
    /**
     * 获取用户的未读通知
     */
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findUnreadByUserId(userId);
    }
    
    /**
     * 获取未读通知数量
     */
    public int getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }
    
    /**
     * 标记通知为已读
     */
    public void markAsRead(Long notificationId) {
        notificationRepository.markAsRead(notificationId);
    }
    
    /**
     * 标记所有通知为已读
     */
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }
    
    /**
     * 删除通知
     */
    public void deleteNotification(Long notificationId) {
        notificationRepository.delete(notificationId);
    }
}
