package com.im.backend.controller;

import com.im.backend.dto.MessageQuickActionMenuDTO;
import com.im.backend.model.MessageQuickActionMenu;
import com.im.backend.service.MessageQuickActionMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 消息快捷操作菜单控制器
 */
@RestController
@RequestMapping("/api/quick-action-menu")
public class MessageQuickActionMenuController {
    
    @Autowired
    private MessageQuickActionMenuService menuService;
    
    /**
     * 创建菜单
     */
    @PostMapping
    public ResponseEntity<MessageQuickActionMenuDTO> createMenu(@Valid @RequestBody MessageQuickActionMenuDTO dto) {
        return ResponseEntity.ok(menuService.createMenu(dto));
    }
    
    /**
     * 更新菜单
     */
    @PutMapping("/{menuId}")
    public ResponseEntity<MessageQuickActionMenuDTO> updateMenu(
            @PathVariable Long menuId,
            @Valid @RequestBody MessageQuickActionMenuDTO dto) {
        return ResponseEntity.ok(menuService.updateMenu(menuId, dto));
    }
    
    /**
     * 删除菜单
     */
    @DeleteMapping("/{menuId}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 获取菜单详情
     */
    @GetMapping("/{menuId}")
    public ResponseEntity<MessageQuickActionMenuDTO> getMenu(@PathVariable Long menuId) {
        return ResponseEntity.ok(menuService.getMenuById(menuId));
    }
    
    /**
     * 获取用户的所有菜单
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MessageQuickActionMenuDTO>> getUserMenus(@PathVariable Long userId) {
        return ResponseEntity.ok(menuService.getMenusByUserId(userId));
    }
    
    /**
     * 获取用户特定类型的菜单
     */
    @GetMapping("/user/{userId}/type/{menuType}")
    public ResponseEntity<List<MessageQuickActionMenuDTO>> getUserMenusByType(
            @PathVariable Long userId,
            @PathVariable MessageQuickActionMenu.MenuType menuType) {
        return ResponseEntity.ok(menuService.getMenusByUserIdAndType(userId, menuType));
    }
    
    /**
     * 获取用户的默认菜单
     */
    @GetMapping("/user/{userId}/default")
    public ResponseEntity<List<MessageQuickActionMenuDTO>> getDefaultMenus(@PathVariable Long userId) {
        return ResponseEntity.ok(menuService.getDefaultMenusByUserId(userId));
    }
    
    /**
     * 获取特定类型的默认菜单
     */
    @GetMapping("/user/{userId}/default/type/{menuType}")
    public ResponseEntity<MessageQuickActionMenuDTO> getDefaultMenuByType(
            @PathVariable Long userId,
            @PathVariable MessageQuickActionMenu.MenuType menuType) {
        return ResponseEntity.ok(menuService.getDefaultMenuByType(userId, menuType));
    }
    
    /**
     * 初始化用户默认菜单
     */
    @PostMapping("/user/{userId}/initialize")
    public ResponseEntity<List<MessageQuickActionMenuDTO>> initializeDefaultMenus(@PathVariable Long userId) {
        return ResponseEntity.ok(menuService.initializeDefaultMenus(userId));
    }
    
    /**
     * 设置菜单为默认
     */
    @PostMapping("/{menuId}/set-default")
    public ResponseEntity<MessageQuickActionMenuDTO> setAsDefault(@PathVariable Long menuId) {
        return ResponseEntity.ok(menuService.setAsDefault(menuId));
    }
    
    /**
     * 启用菜单
     */
    @PostMapping("/{menuId}/enable")
    public ResponseEntity<MessageQuickActionMenuDTO> enableMenu(@PathVariable Long menuId) {
        return ResponseEntity.ok(menuService.toggleMenuStatus(menuId, true));
    }
    
    /**
     * 禁用菜单
     */
    @PostMapping("/{menuId}/disable")
    public ResponseEntity<MessageQuickActionMenuDTO> disableMenu(@PathVariable Long menuId) {
        return ResponseEntity.ok(menuService.toggleMenuStatus(menuId, false));
    }
    
    /**
     * 更新菜单排序
     */
    @PostMapping("/{menuId}/sort/{sortOrder}")
    public ResponseEntity<Void> updateSortOrder(
            @PathVariable Long menuId,
            @PathVariable Integer sortOrder) {
        menuService.updateMenuSortOrder(menuId, sortOrder);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 复制菜单
     */
    @PostMapping("/{menuId}/duplicate")
    public ResponseEntity<MessageQuickActionMenuDTO> duplicateMenu(
            @PathVariable Long menuId,
            @RequestParam String newName) {
        return ResponseEntity.ok(menuService.duplicateMenu(menuId, newName));
    }
    
    /**
     * 获取会话特定的菜单
     */
    @GetMapping("/user/{userId}/conversation/{conversationId}")
    public ResponseEntity<List<MessageQuickActionMenuDTO>> getConversationMenus(
            @PathVariable Long userId,
            @PathVariable Long conversationId) {
        return ResponseEntity.ok(menuService.getMenusByConversation(userId, conversationId));
    }
    
    /**
     * 为会话设置自定义菜单
     */
    @PostMapping("/user/{userId}/conversation/{conversationId}")
    public ResponseEntity<MessageQuickActionMenuDTO> setConversationMenu(
            @PathVariable Long userId,
            @PathVariable Long conversationId,
            @Valid @RequestBody MessageQuickActionMenuDTO dto) {
        return ResponseEntity.ok(menuService.setConversationMenu(userId, conversationId, dto));
    }
    
    /**
     * 重置会话菜单为默认
     */
    @PostMapping("/user/{userId}/conversation/{conversationId}/reset")
    public ResponseEntity<Void> resetConversationMenu(
            @PathVariable Long userId,
            @PathVariable Long conversationId) {
        menuService.resetConversationMenu(userId, conversationId);
        return ResponseEntity.ok().build();
    }
}
