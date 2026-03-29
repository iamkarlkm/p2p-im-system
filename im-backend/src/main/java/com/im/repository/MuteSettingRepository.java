package com.im.repository;

import com.im.entity.MuteSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 免打扰设置仓储
 */
@Repository
public interface MuteSettingRepository extends JpaRepository<MuteSettingEntity, Long> {
    
    /**
     * 查询用户的全局免打扰设置
     */
    Optional<MuteSettingEntity> findByUserIdAndConversationIdIsNull(Long userId);
    
    /**
     * 查询用户对指定会话的静音设置
     */
    Optional<MuteSettingEntity> findByUserIdAndConversationId(Long userId, Long conversationId);
    
    /**
     * 查询用户所有会话的静音设置
     */
    List<MuteSettingEntity> findByUserIdAndConversationIdIsNotNull(Long userId);
    
    /**
     * 查询所有已静音的会话
     */
    @Query("SELECT m FROM MuteSettingEntity m WHERE m.userId = :userId AND m.conversationId IS NOT NULL AND m.isMuted = true")
    List<MuteSettingEntity> findMutedConversations(@Param("userId") Long userId);
    
    /**
     * 批量查询会话是否被静音
     */
    @Query("SELECT m.conversationId FROM MuteSettingEntity m WHERE m.userId = :userId AND m.conversationId IN :conversationIds AND m.isMuted = true")
    List<Long> findMutedConversationIds(@Param("userId") Long userId, @Param("conversationIds") List<Long> conversationIds);
    
    /**
     * 检查会话是否被静音
     */
    @Query("SELECT COUNT(m) > 0 FROM MuteSettingEntity m WHERE m.userId = :userId AND m.conversationId = :conversationId AND m.isMuted = true")
    boolean isConversationMuted(@Param("userId") Long userId, @Param("conversationId") Long conversationId);
    
    /**
     * 启用全局免打扰的用户
     */
    @Query("SELECT m FROM MuteSettingEntity m WHERE m.conversationId IS NULL AND m.dndEnabled = true")
    List<MuteSettingEntity> findAllDndEnabledUsers();
    
    /**
     * 删除用户的全局设置
     */
    @Modifying
    @Query("DELETE FROM MuteSettingEntity m WHERE m.userId = :userId AND m.conversationId IS NULL")
    void deleteGlobalSetting(@Param("userId") Long userId);
    
    /**
     * 删除用户对指定会话的设置
     */
    @Modifying
    @Query("DELETE FROM MuteSettingEntity m WHERE m.userId = :userId AND m.conversationId = :conversationId")
    void deleteConversationSetting(@Param("userId") Long userId, @Param("conversationId") Long conversationId);
}
