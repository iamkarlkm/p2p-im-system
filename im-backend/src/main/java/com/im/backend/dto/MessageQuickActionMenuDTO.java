package com.im.backend.dto;

import com.im.backend.model.MessageQuickActionMenu;
import com.im.backend.model.QuickActionItem;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 消息快捷操作菜单数据传输对象
 */
public class MessageQuickActionMenuDTO {
    
    private Long id;
    private Long userId;
    private Long conversationId;
    
    @NotNull(message = "菜单类型不能为空")
    private MessageQuickActionMenu.MenuType menuType;
    
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 100, message = "菜单名称不能超过100个字符")
    private String name;
    
    @Size(max = 500, message = "描述不能超过500个字符")
    private String description;
    
    private Integer sortOrder;
    private Boolean isEnabled;
    private Boolean isDefault;
    private String icon;
    private String color;
    
    @Valid
    private Set<QuickActionItemDTO> items = new HashSet<>();
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造方法
    public MessageQuickActionMenuDTO() {}
    
    public MessageQuickActionMenuDTO(MessageQuickActionMenu menu) {
        this.id = menu.getId();
        this.userId = menu.getUserId();
        this.conversationId = menu.getConversationId();
        this.menuType = menu.getMenuType();
        this.name = menu.getName();
        this.description = menu.getDescription();
        this.sortOrder = menu.getSortOrder();
        this.isEnabled = menu.getIsEnabled();
        this.isDefault = menu.getIsDefault();
        this.icon = menu.getIcon();
        this.color = menu.getColor();
        this.createdAt = menu.getCreatedAt();
        this.updatedAt = menu.getUpdatedAt();
        
        if (menu.getItems() != null) {
            this.items = menu.getItems().stream()
                    .map(QuickActionItemDTO::new)
                    .collect(Collectors.toSet());
        }
    }
    
    // 转换为实体
    public MessageQuickActionMenu toEntity() {
        MessageQuickActionMenu menu = new MessageQuickActionMenu();
        menu.setId(this.id);
        menu.setUserId(this.userId);
        menu.setConversationId(this.conversationId);
        menu.setMenuType(this.menuType);
        menu.setName(this.name);
        menu.setDescription(this.description);
        menu.setSortOrder(this.sortOrder != null ? this.sortOrder : 0);
        menu.setIsEnabled(this.isEnabled != null ? this.isEnabled : true);
        menu.setIsDefault(this.isDefault != null ? this.isDefault : false);
        menu.setIcon(this.icon);
        menu.setColor(this.color);
        
        if (this.items != null) {
            this.items.forEach(itemDTO -> {
                QuickActionItem item = itemDTO.toEntity();
                menu.addItem(item);
            });
        }
        
        return menu;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
    
    public MessageQuickActionMenu.MenuType getMenuType() { return menuType; }
    public void setMenuType(MessageQuickActionMenu.MenuType menuType) { this.menuType = menuType; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    
    public Boolean getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Boolean isEnabled) { this.isEnabled = isEnabled; }
    
    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public Set<QuickActionItemDTO> getItems() { return items; }
    public void setItems(Set<QuickActionItemDTO> items) { this.items = items; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // 快捷方法：创建默认消息长按菜单
    public static MessageQuickActionMenuDTO createDefaultMessageLongPressMenu(Long userId) {
        MessageQuickActionMenuDTO dto = new MessageQuickActionMenuDTO();
        dto.setUserId(userId);
        dto.setMenuType(MessageQuickActionMenu.MenuType.MESSAGE_LONG_PRESS);
        dto.setName("消息长按菜单");
        dto.setDescription("消息长按显示的快捷操作菜单");
        dto.setSortOrder(1);
        dto.setIsEnabled(true);
        dto.setIsDefault(true);
        dto.setIcon("menu");
        dto.setColor("#1890FF");
        
        Set<QuickActionItemDTO> items = new HashSet<>();
        items.add(QuickActionItemDTO.fromEntity(QuickActionItem.createReplyItem(), 1));
        items.add(QuickActionItemDTO.fromEntity(QuickActionItem.createForwardItem(), 2));
        items.add(QuickActionItemDTO.fromEntity(QuickActionItem.createCopyItem(), 3));
        items.add(QuickActionItemDTO.fromEntity(QuickActionItem.createQuoteItem(), 4));
        items.add(QuickActionItemDTO.fromEntity(QuickActionItem.createMultiSelectItem(), 5));
        items.add(QuickActionItemDTO.fromEntity(QuickActionItem.createFavoriteItem(), 6));
        items.add(QuickActionItemDTO.fromEntity(QuickActionItem.createRecallItem(), 7));
        items.add(QuickActionItemDTO.fromEntity(QuickActionItem.createDeleteItem(), 8));
        
        dto.setItems(items);
        return dto;
    }
    
    // 快捷方法：创建默认右键菜单
    public static MessageQuickActionMenuDTO createDefaultRightClickMenu(Long userId) {
        MessageQuickActionMenuDTO dto = new MessageQuickActionMenuDTO();
        dto.setUserId(userId);
        dto.setMenuType(MessageQuickActionMenu.MenuType.MESSAGE_RIGHT_CLICK);
        dto.setName("消息右键菜单");
        dto.setDescription("消息右键显示的上下文菜单");
        dto.setSortOrder(2);
        dto.setIsEnabled(true);
        dto.setIsDefault(true);
        dto.setIcon("menu");
        dto.setColor("#1890FF");
        
        Set<QuickActionItemDTO> items = new HashSet<>();
        items.add(QuickActionItemDTO.fromEntity(QuickActionItem.createReplyItem(), 1));
        items.add(QuickActionItemDTO.fromEntity(QuickActionItem.createForwardItem(), 2));
        items.add(QuickActionItemDTO.fromEntity(QuickActionItem.createCopyItem(), 3));
        items.add(QuickActionItemDTO.fromEntity(QuickActionItem.createQuoteItem(), 4));
        items.add(QuickActionItemDTO.fromEntity(QuickActionItem.createPinItem(), 5));
        items.add(QuickActionItemDTO.fromEntity(QuickActionItem.createFavoriteItem(), 6));
        items.add(QuickActionItemDTO.fromEntity(QuickActionItem.createTranslateItem(), 7));
        items.add(QuickActionItemDTO.fromEntity(QuickActionItem.createSpeakItem(), 8));
        items.add(QuickActionItemDTO.fromEntity(QuickActionItem.createMultiSelectItem(), 9));
        items.add(QuickActionItemDTO.fromEntity(QuickActionItem.createThreadItem(), 10));
        items.add(QuickActionItemDTO.fromEntity(QuickActionItem.createRecallItem(), 11));
        items.add(QuickActionItemDTO.fromEntity(QuickActionItem.createDeleteItem(), 12));
        
        dto.setItems(items);
        return dto;
    }
    
    @Override
    public String toString() {
        return "MessageQuickActionMenuDTO{" +
                "id=" + id +
                ", menuType=" + menuType +
                ", name='" + name + '\'' +
                ", itemsCount=" + (items != null ? items.size() : 0) +
                '}';
    }
}
