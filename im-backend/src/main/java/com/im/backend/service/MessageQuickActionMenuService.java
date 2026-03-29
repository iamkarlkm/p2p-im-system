package com.im.backend.service;

import com.im.backend.dto.MessageQuickActionMenuDTO;
import com.im.backend.model.MessageQuickActionMenu;

import java.util.List;

/**
 * 消息快捷操作菜单服务接口
 */
public interface MessageQuickActionMenuService {
    
    /**
     * 创建菜单
     */
    MessageQuickActionMenuDTO createMenu(MessageQuickActionMenuDTO dto);
    
    /**
     * 更新菜单
     */
    MessageQuickActionMenuDTO updateMenu(Long menuId, MessageQuickActionMenuDTO dto);
    
    /**
     * 删除菜单
     */
    void deleteMenu(Long menuId);
    
    /**
     * 根据ID获取菜单
     */
    MessageQuickActionMenuDTO getMenuById(Long menuId);
    
    /**
     * 获取用户的所有菜单
     */
    List<MessageQuickActionMenuDTO> getMenusByUserId(Long userId);
    
    /**
     * 获取用户特定类型的菜单
     */
    List<MessageQuickActionMenuDTO> getMenusByUserIdAndType(Long userId, MessageQuickActionMenu.MenuType menuType);
    
    /**
     * 获取用户的默认菜单
     */
    List<MessageQuickActionMenuDTO> getDefaultMenusByUserId(Long userId);
    
    /**
     * 获取特定类型的默认菜单
     */
    MessageQuickActionMenuDTO getDefaultMenuByType(Long userId, MessageQuickActionMenu.MenuType menuType);
    
    /**
     * 初始化用户默认菜单
     */
    List<MessageQuickActionMenuDTO> initializeDefaultMenus(Long userId);
    
    /**
     * 设置默认菜单
     */
    MessageQuickActionMenuDTO setAsDefault(Long menuId);
    
    /**
     * 启用/禁用菜单
     */
    MessageQuickActionMenuDTO toggleMenuStatus(Long menuId, Boolean enabled);
    
    /**
     * 更新菜单排序
     */
    void updateMenuSortOrder(Long menuId, Integer sortOrder);
    
    /**
     * 复制菜单
     */
    MessageQuickActionMenuDTO duplicateMenu(Long menuId, String newName);
    
    /**
     * 获取会话特定的菜单
     */
    List<MessageQuickActionMenuDTO> getMenusByConversation(Long userId, Long conversationId);
    
    /**
     * 为会话设置自定义菜单
     */
    MessageQuickActionMenuDTO setConversationMenu(Long userId, Long conversationId, MessageQuickActionMenuDTO dto);
    
    /**
     * 重置会话菜单为默认
     */
    void resetConversationMenu(Long userId, Long conversationId);
}
