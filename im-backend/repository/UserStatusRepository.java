package com.im.backend.repository;

import com.im.backend.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户状态数据层
 */
@Repository
public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {

    /**
     * 根据用户ID查找状态
     */
    Optional<UserStatus> findByUserId(Long userId);

    /**
     * 查找在线用户
     */
    @Query("SELECT us FROM UserStatus us WHERE us.onlineStatus = 'ONLINE'")
    List<UserStatus> findOnlineUsers();

    /**
     * 查找指定用户列表的状态
     */
    @Query("SELECT us FROM UserStatus us WHERE us.userId IN ?1")
    List<UserStatus> findByUserIdIn(List<Long> userIds);

    /**
     * 更新用户在线状态
     */
    @Modifying
    @Query("UPDATE UserStatus us SET us.onlineStatus = ?2, us.lastActivityAt = ?3 WHERE us.userId = ?1")
    void updateOnlineStatus(Long userId, String status, LocalDateTime activityAt);

    /**
     * 更新用户最后活动时间
     */
    @Modifying
    @Query("UPDATE UserStatus us SET us.lastActivityAt = ?2 WHERE us.userId = ?1")
    void updateLastActivity(Long userId, LocalDateTime activityAt);

    /**
     * 查找长时间不活动的用户（用于离线检测）
     */
    @Query("SELECT us FROM UserStatus us WHERE us.lastActivityAt < ?1 AND us.onlineStatus = 'ONLINE'")
    List<UserStatus> findInactiveUsers(LocalDateTime threshold);

    /**
     * 统计在线用户数量
     */
    @Query("SELECT COUNT(us) FROM UserStatus us WHERE us.onlineStatus = 'ONLINE'")
    Long countOnlineUsers();

    /**
     * 删除用户状态
     */
    void deleteByUserId(Long userId);
}
