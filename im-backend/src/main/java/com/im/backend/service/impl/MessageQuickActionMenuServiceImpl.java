package com.im.backend.service.impl;

import com.im.backend.dto.MessageQuickActionMenuDTO;
import com.im.backend.dto.QuickActionItemDTO;
import com.im.backend.model.MessageQuickActionMenu;
import com.im.backend.model.QuickActionItem;
import com.im.backend.repository.MessageQuickActionMenuRepository;
import com.im.backend.repository.QuickActionItemRepository;
import com.im.backend.service.MessageQuickActionMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息快捷操作菜单服务实现
 */
@Service
public class MessageQuickActionMenuServiceImpl implements MessageQuickActionMenuService {
    
    @Autowired
    private MessageQuickActionMenuRepository menuRepository;
    
    @Autowired
    private QuickActionItemRepository itemRepository;
    
    @Override
    @Transactional
    public MessageQuickActionMenuDTO createMenu(MessageQuickActionMenuDTO dto) {
        MessageQuickActionMenu menu = dto.toEntity();
        menu = menuRepository.save(menu);
        return new MessageQuickActionMenuDTO(menu);
    }
    
    @Override
    @Transactional
    public MessageQuickActionMenuDTO updateMenu(Long menuId, MessageQuickActionMenuDTO dto) {
        MessageQuickActionMenu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("菜单不存在: " + menuId));
        
        // 更新基本属性
        menu.setName(dto.getName());
        menu.setDescription(dto.getDescription());
        menu.setMenuType(dto.getMenuType());
        menu.setSortOrder(dto.getSortOrder());
        menu.setIsEnabled(dto.getIsEnabled());
        menu.setIcon(dto.getIcon());
        menu.setColor(dto.getColor());
        
        // 更新菜单项
        if (dto.getItems() != null) {
            // 删除旧的项目
            menu.getItems().clear();
            
            // 添加新的项目
            for (QuickActionItemDTO itemDTO : dto.getItems()) {
                QuickActionItem item = itemDTO.toEntity();
                menu.addItem(item);
            }
        }
        
        menu = menuRepository.save(menu);
        return new MessageQuickActionMenuDTO(menu);
    }
    
    @Override
    @Transactional
    public void deleteMenu(Long menuId) {
        menuRepository.deleteById(menuId);
    }
    
    @Override
    public MessageQuickActionMenuDTO getMenuById(Long menuId) {
        MessageQuickActionMenu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("菜单不存在: " + menuId));
        return new MessageQuickActionMenuDTO(menu);
    }
    
    @Override
    public List<MessageQuickActionMenuDTO> getMenusByUserId(Long userId) {
        return menuRepository.findByUserIdOrderBySortOrderAsc(userId).stream()
                .map(MessageQuickActionMenuDTO::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MessageQuickActionMenuDTO> getMenusByUserIdAndType(Long userId, MessageQuickActionMenu.MenuType menuType) {
        return menuRepository.findByUserIdAndMenuTypeOrderBySortOrderAsc(userId, menuType).stream()
                .map(MessageQuickActionMenuDTO::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MessageQuickActionMenuDTO> getDefaultMenusByUserId(Long userId) {
        return menuRepository.findDefaultMenusByUserId(userId).stream()
                .map(MessageQuickActionMenuDTO::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public MessageQuickActionMenuDTO getDefaultMenuByType(Long userId, MessageQuickActionMenu.MenuType menuType) {
        return menuRepository.findDefaultMenuByUserIdAndType(userId, menuType)
                .map(MessageQuickActionMenuDTO::new)
                .orElse(null);
    }
    
    @Override
    @Transactional
    public List<MessageQuickActionMenuDTO> initializeDefaultMenus(Long userId) {
        List<MessageQuickActionMenuDTO> result = new ArrayList<>();
        
        // 检查是否已有默认菜单
        List<MessageQuickActionMenu> existingMenus = menuRepository.findDefaultMenusByUserId(userId);
        if (!existingMenus.isEmpty()) {
            return existingMenus.stream()
                    .map(MessageQuickActionMenuDTO::new)
                    .collect(Collectors.toList());
        }
        
        // 创建默认消息长按菜单
        MessageQuickActionMenuDTO longPressMenu = MessageQuickActionMenuDTO.createDefaultMessageLongPressMenu(userId);
        result.add(createMenu(longPressMenu));
        
        // 创建默认右键菜单
        MessageQuickActionMenuDTO rightClickMenu = MessageQuickActionMenuDTO.createDefaultRightClickMenu(userId);
        result.add(createMenu(rightClickMenu));
        
        return result;
    }
    
    @Override
    @Transactional
    public MessageQuickActionMenuDTO setAsDefault(Long menuId) {
        MessageQuickActionMenu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("菜单不存在: " + menuId));
        
        // 取消同类型的其他默认菜单
        List<MessageQuickActionMenu> sameTypeMenus = menuRepository
                .findByUserIdAndMenuTypeOrderBySortOrderAsc(menu.getUserId(), menu.getMenuType());
        for (MessageQuickActionMenu m : sameTypeMenus) {
            if (!m.getId().equals(menuId)) {
                m.setIsDefault(false);
                menuRepository.save(m);
            }
        }
        
        menu.setIsDefault(true);
        menu = menuRepository.save(menu);
        return new MessageQuickActionMenuDTO(menu);
    }
    
    @Override
    @Transactional
    public MessageQuickActionMenuDTO toggleMenuStatus(Long menuId, Boolean enabled) {
        MessageQuickActionMenu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("菜单不存在: " + menuId));
        menu.setIsEnabled(enabled);
        menu = menuRepository.save(menu);
        return new MessageQuickActionMenuDTO(menu);
    }
    
    @Override
    @Transactional
    public void updateMenuSortOrder(Long menuId, Integer sortOrder) {
        MessageQuickActionMenu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("菜单不存在: " + menuId));
        menu.setSortOrder(sortOrder);
        menuRepository.save(menu);
    }
    
    @Override
    @Transactional
    public MessageQuickActionMenuDTO duplicateMenu(Long menuId, String newName) {
        MessageQuickActionMenu sourceMenu = menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("菜单不存在: " + menuId));
        
        MessageQuickActionMenu newMenu = new MessageQuickActionMenu();
        newMenu.setUserId(sourceMenu.getUserId());
        newMenu.setMenuType(sourceMenu.getMenuType());
        newMenu.setName(newName);
        newMenu.setDescription(sourceMenu.getDescription());
        newMenu.setSortOrder(sourceMenu.getSortOrder() + 1);
        newMenu.setIsEnabled(sourceMenu.getIsEnabled());
        newMenu.setIsDefault(false);
        newMenu.setIcon(sourceMenu.getIcon());
        newMenu.setColor(sourceMenu.getColor());
        
        // 复制菜单项
        for (QuickActionItem item : sourceMenu.getItems()) {
            QuickActionItem newItem = new QuickActionItem();
            newItem.setActionType(item.getActionType());
            newItem.setLabel(item.getLabel());
            newItem.setIcon(item.getIcon());
            newItem.setIconColor(item.getIconColor());
            newItem.setShortcutKey(item.getShortcutKey());
            newItem.setSortOrder(item.getSortOrder());
            newItem.setIsVisible(item.getIsVisible());
            newItem.setIsEnabled(item.getIsEnabled());
            newItem.setRequiresConfirmation(item.getRequiresConfirmation());
            newItem.setConfirmationMessage(item.getConfirmationMessage());
            newItem.setCustomActionData(item.getCustomActionData());
            newItem.setVisibilityCondition(item.getVisibilityCondition());
            newMenu.addItem(newItem);
        }
        
        newMenu = menuRepository.save(newMenu);
        return new MessageQuickActionMenuDTO(newMenu);
    }
    
    @Override
    public List<MessageQuickActionMenuDTO> getMenusByConversation(Long userId, Long conversationId) {
        List<MessageQuickActionMenu> menus = menuRepository.findByUserIdAndConversationIdOrderBySortOrderAsc(userId, conversationId);
        if (menus.isEmpty()) {
            // 如果没有会话特定菜单，返回默认菜单
            return getDefaultMenusByUserId(userId);
        }
        return menus.stream()
                .map(MessageQuickActionMenuDTO::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public MessageQuickActionMenuDTO setConversationMenu(Long userId, Long conversationId, MessageQuickActionMenuDTO dto) {
        dto.setUserId(userId);
        dto.setConversationId(conversationId);
        return createMenu(dto);
    }
    
    @Override
    @Transactional
    public void resetConversationMenu(Long userId, Long conversationId) {
        menuRepository.deleteByUserIdAndConversationId(userId, conversationId);
    }
}
