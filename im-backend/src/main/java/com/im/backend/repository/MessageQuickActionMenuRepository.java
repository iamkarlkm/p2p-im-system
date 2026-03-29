package com.im.backend.repository;

import com.im.backend.model.MessageQuickActionMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 消息快捷操作菜单数据访问层
 */
@Repository
public interface MessageQuickActionMenuRepository extends JpaRepository<MessageQuickActionMenu, Long> {
    
    /**
     * 根据用户ID查询所有菜单
     */
    List<MessageQuickActionMenu> findByUserIdOrderBySortOrderAsc(Long userId);
    
    /**
     * 根据用户ID和菜单类型查询
     */
    List<MessageQuickActionMenu> findByUserIdAndMenuTypeOrderBySortOrderAsc(Long userId, MessageQuickActionMenu.MenuType menuType);
    
    /**
     * 根据用户ID和会话ID查询
     */
    List<MessageQuickActionMenu> findByUserIdAndConversationIdOrderBySortOrderAsc(Long userId, Long conversationId);
    
    /**
     * 查询用户的默认菜单
     */
    @Query("SELECT m FROM MessageQuickActionMenu m WHERE m.userId = :userId AND m.isDefault = true AND m.isEnabled = true ORDER BY m.sortOrder")
    List<MessageQuickActionMenu> findDefaultMenusByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户的启用的菜单
     */
    @Query("SELECT m FROM MessageQuickActionMenu m WHERE m.userId = :userId AND m.isEnabled = true ORDER BY m.sortOrder")
    List<MessageQuickActionMenu> findEnabledMenusByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和菜单类型查询启用的菜单
     */
    @Query("SELECT m FROM MessageQuickActionMenu m WHERE m.userId = :userId AND m.menuType = :menuType AND m.isEnabled = true ORDER BY m.sortOrder")
    List<MessageQuickActionMenu> findEnabledMenusByUserIdAndType(@Param("userId") Long userId, @Param("menuType") MessageQuickActionMenu.MenuType menuType);
    
    /**
     * 查询特定类型的默认菜单
     */
    @Query("SELECT m FROM MessageQuickActionMenu m WHERE m.userId = :userId AND m.menuType = :menuType AND m.isDefault = true AND m.isEnabled = true")
    Optional<MessageQuickActionMenu> findDefaultMenuByUserIdAndType(@Param("userId") Long userId, @Param("menuType") MessageQuickActionMenu.MenuType menuType);
    
    /**
     * 检查是否存在同名菜单
     */
    boolean existsByUserIdAndName(Long userId, String name);
    
    /**
     * 统计用户的菜单数量
     */
    long countByUserId(Long userId);
    
    /**
     * 统计用户特定类型的菜单数量
     */
    long countByUserIdAndMenuType(Long userId, MessageQuickActionMenu.MenuType menuType);
    
    /**
     * 删除用户的所有菜单
     */
    void deleteByUserId(Long userId);
    
    /**
     * 根据用户ID和会话ID删除
     */
    void deleteByUserIdAndConversationId(Long userId, Long conversationId);
}
