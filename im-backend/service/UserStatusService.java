package com.im.backend.service;

import com.im.backend.dto.UserStatusDTO;
import com.im.backend.model.UserStatus;
import com.im.backend.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户状态服务
 * 管理用户在线状态、最后seen时间等
 */
@Service
public class UserStatusService {

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private StatusSubscriptionService statusSubscriptionService;

    /**
     * 用户上线
     */
    @Transactional
    public UserStatus userOnline(Long userId, String deviceType, String ipAddress) {
        Optional<UserStatus> existing = userStatusRepository.findByUserId(userId);

        UserStatus status;
        if (existing.isPresent()) {
            status = existing.get();
        } else {
            status = new UserStatus();
            status.setUserId(userId);
        }

        status.setOnlineStatus("ONLINE");
        status.setDeviceType(deviceType);
        status.setIpAddress(ipAddress);
        status.setLastActivityAt(LocalDateTime.now());
        status.setUpdatedAt(LocalDateTime.now());

        UserStatus saved = userStatusRepository.save(status);

        // 通知订阅者
        statusSubscriptionService.notifyStatusChange(userId, "ONLINE");

        return saved;
    }

    /**
     * 用户离线
     */
    @Transactional
    public UserStatus userOffline(Long userId) {
        Optional<UserStatus> existing = userStatusRepository.findByUserId(userId);

        if (existing.isPresent()) {
            UserStatus status = existing.get();
            status.setOnlineStatus("OFFLINE");
            status.setLastSeenAt(LocalDateTime.now());
            status.setUpdatedAt(LocalDateTime.now());

            UserStatus saved = userStatusRepository.save(status);

            // 通知订阅者
            statusSubscriptionService.notifyStatusChange(userId, "OFFLINE");

            return saved;
        }

        return null;
    }

    /**
     * 更新用户活动状态
     */
    @Transactional
    public void updateActivity(Long userId) {
        userStatusRepository.updateLastActivity(userId, LocalDateTime.now());
    }

    /**
     * 设置自定义状态
     */
    @Transactional
    public UserStatus setCustomStatus(Long userId, String customStatus, String statusMessage) {
        Optional<UserStatus> existing = userStatusRepository.findByUserId(userId);

        UserStatus status;
        if (existing.isPresent()) {
            status = existing.get();
        } else {
            status = new UserStatus();
            status.setUserId(userId);
            status.setOnlineStatus("OFFLINE");
        }

        status.setCustomStatus(customStatus);
        status.setStatusMessage(statusMessage);
        status.setUpdatedAt(LocalDateTime.now());

        UserStatus saved = userStatusRepository.save(status);

        // 通知订阅者
        statusSubscriptionService.notifyStatusChange(userId, "CUSTOM_STATUS_UPDATED");

        return saved;
    }

    /**
     * 获取用户状态
     */
    public UserStatusDTO getUserStatus(Long userId, Long requesterId) {
        Optional<UserStatus> statusOpt = userStatusRepository.findByUserId(userId);

        if (!statusOpt.isPresent()) {
            return createDefaultStatus(userId);
        }

        UserStatus status = statusOpt.get();

        // 检查隐私设置
        if (!canViewStatus(status, requesterId)) {
            return createHiddenStatus(userId);
        }

        return convertToDTO(status);
    }

    /**
     * 批量获取用户状态
     */
    public List<UserStatusDTO> getUserStatuses(List<Long> userIds, Long requesterId) {
        List<UserStatus> statuses = userStatusRepository.findByUserIdIn(userIds);

        return statuses.stream()
            .filter(status -> canViewStatus(status, requesterId))
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(Long userId) {
        Optional<UserStatus> status = userStatusRepository.findByUserId(userId);
        return status.isPresent() && "ONLINE".equals(status.get().getOnlineStatus());
    }

    /**
     * 获取在线用户列表
     */
    public List<UserStatusDTO> getOnlineUsers() {
        List<UserStatus> onlineUsers = userStatusRepository.findOnlineUsers();
        return onlineUsers.stream()
            .filter(status -> status.getIsVisible())
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 设置隐私选项
     */
    @Transactional
    public UserStatus setPrivacyOptions(Long userId, Boolean isVisible, Boolean showLastSeen) {
        Optional<UserStatus> existing = userStatusRepository.findByUserId(userId);

        if (existing.isPresent()) {
            UserStatus status = existing.get();
            status.setIsVisible(isVisible);
            status.setShowLastSeen(showLastSeen);
            status.setUpdatedAt(LocalDateTime.now());
            return userStatusRepository.save(status);
        }

        return null;
    }

    /**
     * 检测并更新离线用户（定时任务调用）
     */
    @Transactional
    public void detectOfflineUsers() {
        // 超过5分钟没有活动的用户标记为离线
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);
        List<UserStatus> inactiveUsers = userStatusRepository.findInactiveUsers(threshold);

        for (UserStatus user : inactiveUsers) {
            user.setOnlineStatus("OFFLINE");
            user.setLastSeenAt(user.getLastActivityAt());
            user.setUpdatedAt(LocalDateTime.now());
            userStatusRepository.save(user);

            // 通知订阅者
            statusSubscriptionService.notifyStatusChange(user.getUserId(), "OFFLINE");
        }
    }

    /**
     * 获取在线用户数量
     */
    public Long getOnlineUserCount() {
        return userStatusRepository.countOnlineUsers();
    }

    /**
     * 转换为DTO
     */
    private UserStatusDTO convertToDTO(UserStatus status) {
        UserStatusDTO dto = new UserStatusDTO();
        dto.setUserId(status.getUserId());
        dto.setOnlineStatus(status.getOnlineStatus());
        dto.setCustomStatus(status.getCustomStatus());
        dto.setStatusMessage(status.getStatusMessage());
        dto.setIsOnline("ONLINE".equals(status.getOnlineStatus()));
        dto.setIsVisible(status.getIsVisible());
        dto.setDeviceType(status.getDeviceType());

        if (status.getShowLastSeen()) {
            dto.setLastSeenAt(status.getLastSeenAt());
            dto.setLastSeenText(formatLastSeen(status.getLastSeenAt()));
        }

        return dto;
    }

    /**
     * 格式化最后seen时间
     */
    private String formatLastSeen(LocalDateTime lastSeen) {
        if (lastSeen == null) {
            return "未知";
        }

        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(lastSeen, now);

        if (minutes < 1) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (minutes < 1440) {
            return (minutes / 60) + "小时前";
        } else {
            return (minutes / 1440) + "天前";
        }
    }

    /**
     * 检查是否可以查看状态
     */
    private boolean canViewStatus(UserStatus status, Long requesterId) {
        if (status.getUserId().equals(requesterId)) {
            return true; // 自己可以查看自己的状态
        }
        return status.getIsVisible();
    }

    /**
     * 创建默认状态
     */
    private UserStatusDTO createDefaultStatus(Long userId) {
        UserStatusDTO dto = new UserStatusDTO();
        dto.setUserId(userId);
        dto.setOnlineStatus("OFFLINE");
        dto.setIsOnline(false);
        dto.setLastSeenText("从未上线");
        return dto;
    }

    /**
     * 创建隐藏状态
     */
    private UserStatusDTO createHiddenStatus(Long userId) {
        UserStatusDTO dto = new UserStatusDTO();
        dto.setUserId(userId);
        dto.setOnlineStatus("HIDDEN");
        dto.setIsVisible(false);
        dto.setLastSeenText("隐藏");
        return dto;
    }
}
