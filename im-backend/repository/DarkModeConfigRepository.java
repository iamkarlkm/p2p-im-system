package com.im.backend.repository;

import com.im.backend.entity.DarkModeConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 暗黑模式配置仓储接口
 */
@Repository
public interface DarkModeConfigRepository extends JpaRepository<DarkModeConfigEntity, Long> {

    // 基础查询方法
    
    /**
     * 根据用户ID查询所有配置
     */
    List<DarkModeConfigEntity> findByUserId(String userId);
    
    /**
     * 根据用户ID和平台查询配置
     */
    List<DarkModeConfigEntity> findByUserIdAndPlatform(String userId, DarkModeConfigEntity.Platform platform);
    
    /**
     * 根据用户ID和设备ID查询配置
     */
    Optional<DarkModeConfigEntity> findByUserIdAndDeviceId(String userId, String deviceId);
    
    /**
     * 根据用户ID、平台和设备ID查询配置
     */
    Optional<DarkModeConfigEntity> findByUserIdAndPlatformAndDeviceId(String userId, DarkModeConfigEntity.Platform platform, String deviceId);
    
    /**
     * 查询用户的活动配置
     */
    Optional<DarkModeConfigEntity> findByUserIdAndIsActiveTrue(String userId);
    
    /**
     * 查询用户的系统默认配置
     */
    @Query("SELECT d FROM DarkModeConfigEntity d WHERE d.userId = :userId AND d.configName = 'default'")
    Optional<DarkModeConfigEntity> findDefaultByUserId(@Param("userId") String userId);
    
    /**
     * 根据主题模式查询配置
     */
    List<DarkModeConfigEntity> findByThemeMode(DarkModeConfigEntity.ThemeMode themeMode);
    
    /**
     * 查询所有系统级默认配置
     */
    List<DarkModeConfigEntity> findByUserIdIsNull();
    
    /**
     * 查询系统级的活动配置
     */
    Optional<DarkModeConfigEntity> findByUserIdIsNullAndIsActiveTrue();
    
    // 批量查询方法
    
    /**
     * 批量查询用户配置
     */
    @Query("SELECT d FROM DarkModeConfigEntity d WHERE d.userId IN :userIds")
    List<DarkModeConfigEntity> findByUserIds(@Param("userIds") List<String> userIds);
    
    /**
     * 查询多个用户的活跃配置
     */
    @Query("SELECT d FROM DarkModeConfigEntity d WHERE d.userId IN :userIds AND d.isActive = true")
    List<DarkModeConfigEntity> findActiveByUserIds(@Param("userIds") List<String> userIds);
    
    /**
     * 查询指定平台的所有配置
     */
    @Query("SELECT d FROM DarkModeConfigEntity d WHERE d.platform = :platform AND d.isActive = true")
    List<DarkModeConfigEntity> findActiveByPlatform(@Param("platform") DarkModeConfigEntity.Platform platform);
    
    /**
     * 查询需要同步的配置 (上次同步时间早于指定时间)
     */
    @Query("SELECT d FROM DarkModeConfigEntity d WHERE d.lastSyncedAt IS NULL OR d.lastSyncedAt < :syncBefore")
    List<DarkModeConfigEntity> findNeedsSync(@Param("syncBefore") LocalDateTime syncBefore);
    
    /**
     * 查询过期的临时配置 (创建时间早于指定时间)
     */
    @Query("SELECT d FROM DarkModeConfigEntity d WHERE d.configName LIKE 'temp%' AND d.createdAt < :expireBefore")
    List<DarkModeConfigEntity> findExpiredTemporary(@Param("expireBefore") LocalDateTime expireBefore);
    
    /**
     * 查询高对比度模式的配置
     */
    @Query("SELECT d FROM DarkModeConfigEntity d WHERE d.highContrast = true")
    List<DarkModeConfigEntity> findHighContrastConfigs();
    
    /**
     * 查询启用夜间保护的配置
     */
    @Query("SELECT d FROM DarkModeConfigEntity d WHERE d.nightProtection = true")
    List<DarkModeConfigEntity> findNightProtectionConfigs();
    
    /**
     * 查询启用减少动画效果的配置
     */
    @Query("SELECT d FROM DarkModeConfigEntity d WHERE d.reduceMotion = true")
    List<DarkModeConfigEntity> findReduceMotionConfigs();
    
    /**
     * 查询启用自动切换的配置
     */
    @Query("SELECT d FROM DarkModeConfigEntity d WHERE d.autoSwitchEnabled = true")
    List<DarkModeConfigEntity> findAutoSwitchConfigs();
    
    // 统计查询方法
    
    /**
     * 统计用户的配置数量
     */
    @Query("SELECT COUNT(d) FROM DarkModeConfigEntity d WHERE d.userId = :userId")
    Long countByUserId(@Param("userId") String userId);
    
    /**
     * 统计主题模式使用情况
     */
    @Query("SELECT d.themeMode, COUNT(d) FROM DarkModeConfigEntity d GROUP BY d.themeMode")
    List<Object[]> countByThemeMode();
    
    /**
     * 统计平台使用情况
     */
    @Query("SELECT d.platform, COUNT(d) FROM DarkModeConfigEntity d WHERE d.platform IS NOT NULL GROUP BY d.platform")
    List<Object[]> countByPlatform();
    
    /**
     * 统计启用高对比度的用户比例
     */
    @Query("SELECT COUNT(d) FROM DarkModeConfigEntity d WHERE d.highContrast = true AND d.isActive = true")
    Long countHighContrastActive();
    
    /**
     * 统计启用减少动画的用户比例
     */
    @Query("SELECT COUNT(d) FROM DarkModeConfigEntity d WHERE d.reduceMotion = true AND d.isActive = true")
    Long countReduceMotionActive();
    
    /**
     * 统计启用夜间保护的用户比例
     */
    @Query("SELECT COUNT(d) FROM DarkModeConfigEntity d WHERE d.nightProtection = true AND d.isActive = true")
    Long countNightProtectionActive();
    
    /**
     * 统计启用自动切换的用户比例
     */
    @Query("SELECT COUNT(d) FROM DarkModeConfigEntity d WHERE d.autoSwitchEnabled = true AND d.isActive = true")
    Long countAutoSwitchActive();
    
    // 更新操作方法
    
    /**
     * 更新配置同步时间
     */
    @Modifying
    @Transactional
    @Query("UPDATE DarkModeConfigEntity d SET d.lastSyncedAt = :syncTime WHERE d.id = :id")
    int updateLastSyncedTime(@Param("id") Long id, @Param("syncTime") LocalDateTime syncTime);
    
    /**
     * 批量更新配置同步时间
     */
    @Modifying
    @Transactional
    @Query("UPDATE DarkModeConfigEntity d SET d.lastSyncedAt = :syncTime WHERE d.id IN :ids")
    int batchUpdateLastSyncedTime(@Param("ids") List<Long> ids, @Param("syncTime") LocalDateTime syncTime);
    
    /**
     * 停用用户的所有其他配置 (激活指定配置时使用)
     */
    @Modifying
    @Transactional
    @Query("UPDATE DarkModeConfigEntity d SET d.isActive = false WHERE d.userId = :userId AND d.id != :activeId")
    int deactivateOtherConfigs(@Param("userId") String userId, @Param("activeId") Long activeId);
    
    /**
     * 激活指定配置
     */
    @Modifying
    @Transactional
    @Query("UPDATE DarkModeConfigEntity d SET d.isActive = true, d.updatedAt = :now WHERE d.id = :id")
    int activateConfig(@Param("id") Long id, @Param("now") LocalDateTime now);
    
    /**
     * 更新配置版本
     */
    @Modifying
    @Transactional
    @Query("UPDATE DarkModeConfigEntity d SET d.configVersion = d.configVersion + 1, d.updatedAt = :now WHERE d.id = :id")
    int incrementVersion(@Param("id") Long id, @Param("now") LocalDateTime now);
    
    /**
     * 启用自动切换
     */
    @Modifying
    @Transactional
    @Query("UPDATE DarkModeConfigEntity d SET d.autoSwitchEnabled = true, d.updatedAt = :now WHERE d.id = :id")
    int enableAutoSwitch(@Param("id") Long id, @Param("now") LocalDateTime now);
    
    /**
     * 禁用自动切换
     */
    @Modifying
    @Transactional
    @Query("UPDATE DarkModeConfigEntity d SET d.autoSwitchEnabled = false, d.updatedAt = :now WHERE d.id = :id")
    int disableAutoSwitch(@Param("id") Long id, @Param("now") LocalDateTime now);
    
    /**
     * 启用高对比度模式
     */
    @Modifying
    @Transactional
    @Query("UPDATE DarkModeConfigEntity d SET d.highContrast = true, d.updatedAt = :now WHERE d.id = :id")
    int enableHighContrast(@Param("id") Long id, @Param("now") LocalDateTime now);
    
    /**
     * 禁用高对比度模式
     */
    @Modifying
    @Transactional
    @Query("UPDATE DarkModeConfigEntity d SET d.highContrast = false, d.updatedAt = :now WHERE d.id = :id")
    int disableHighContrast(@Param("id") Long id, @Param("now") LocalDateTime now);
    
    /**
     * 启用减少动画
     */
    @Modifying
    @Transactional
    @Query("UPDATE DarkModeConfigEntity d SET d.reduceMotion = true, d.updatedAt = :now WHERE d.id = :id")
    int enableReduceMotion(@Param("id") Long id, @Param("now") LocalDateTime now);
    
    /**
     * 禁用减少动画
     */
    @Modifying
    @Transactional
    @Query("UPDATE DarkModeConfigEntity d SET d.reduceMotion = false, d.updatedAt = :now WHERE d.id = :id")
    int disableReduceMotion(@Param("id") Long id, @Param("now") LocalDateTime now);
    
    /**
     * 启用夜间保护
     */
    @Modifying
    @Transactional
    @Query("UPDATE DarkModeConfigEntity d SET d.nightProtection = true, d.updatedAt = :now WHERE d.id = :id")
    int enableNightProtection(@Param("id") Long id, @Param("now") LocalDateTime now);
    
    /**
     * 禁用夜间保护
     */
    @Modifying
    @Transactional
    @Query("UPDATE DarkModeConfigEntity d SET d.nightProtection = false, d.updatedAt = :now WHERE d.id = :id")
    int disableNightProtection(@Param("id") Long id, @Param("now") LocalDateTime now);
    
    // 清理操作方法
    
    /**
     * 删除用户的指定配置
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM DarkModeConfigEntity d WHERE d.userId = :userId AND d.id = :id")
    int deleteByUserIdAndId(@Param("userId") String userId, @Param("id") Long id);
    
    /**
     * 删除用户的所有配置
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM DarkModeConfigEntity d WHERE d.userId = :userId")
    int deleteAllByUserId(@Param("userId") String userId);
    
    /**
     * 删除过期的临时配置
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM DarkModeConfigEntity d WHERE d.configName LIKE 'temp%' AND d.createdAt < :expireBefore")
    int deleteExpiredTemporary(@Param("expireBefore") LocalDateTime expireBefore);
    
    /**
     * 删除指定设备上的配置
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM DarkModeConfigEntity d WHERE d.deviceId = :deviceId")
    int deleteByDeviceId(@Param("deviceId") String deviceId);
    
    /**
     * 删除指定平台的配置
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM DarkModeConfigEntity d WHERE d.platform = :platform")
    int deleteByPlatform(@Param("platform") DarkModeConfigEntity.Platform platform);
    
    // 存在性检查方法
    
    /**
     * 检查用户是否存在配置
     */
    boolean existsByUserId(String userId);
    
    /**
     * 检查用户是否存在活动配置
     */
    boolean existsByUserIdAndIsActiveTrue(String userId);
    
    /**
     * 检查用户和设备是否存在配置
     */
    boolean existsByUserIdAndDeviceId(String userId, String deviceId);
    
    /**
     * 检查指定名称的配置是否存在
     */
    boolean existsByUserIdAndConfigName(String userId, String configName);
    
    /**
     * 检查系统级默认配置是否存在
     */
    boolean existsByUserIdIsNullAndConfigName(String configName);
    
    // 排序查询方法
    
    /**
     * 按创建时间倒序查询用户的配置
     */
    @Query("SELECT d FROM DarkModeConfigEntity d WHERE d.userId = :userId ORDER BY d.createdAt DESC")
    List<DarkModeConfigEntity> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId);
    
    /**
     * 按更新时间倒序查询用户的配置
     */
    @Query("SELECT d FROM DarkModeConfigEntity d WHERE d.userId = :userId ORDER BY d.updatedAt DESC")
    List<DarkModeConfigEntity> findByUserIdOrderByUpdatedAtDesc(@Param("userId") String userId);
    
    /**
     * 按配置版本倒序查询
     */
    @Query("SELECT d FROM DarkModeConfigEntity d WHERE d.userId = :userId ORDER BY d.configVersion DESC")
    List<DarkModeConfigEntity> findByUserIdOrderByConfigVersionDesc(@Param("userId") String userId);
    
    // 自定义组合查询
    
    /**
     * 查询用户的自定义主题配置
     */
    @Query("SELECT d FROM DarkModeConfigEntity d WHERE d.userId = :userId AND d.themeMode = 'CUSTOM' AND d.isActive = true")
    Optional<DarkModeConfigEntity> findCustomThemeByUserId(@Param("userId") String userId);
    
    /**
     * 查询系统跟随模式的用户配置
     */
    @Query("SELECT d FROM DarkModeConfigEntity d WHERE d.userId = :userId AND d.themeMode = 'SYSTEM' AND d.isActive = true")
    Optional<DarkModeConfigEntity> findSystemFollowThemeByUserId(@Param("userId") String userId);
    
    /**
     * 查询启用系统颜色的配置
     */
    @Query("SELECT d FROM DarkModeConfigEntity d WHERE d.useSystemColors = true")
    List<DarkModeConfigEntity> findByUseSystemColorsTrue();
}