package com.im.backend.dto;

import com.im.backend.model.MessageQuickActionMenu;
import com.im.backend.model.QuickActionItem;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 快捷操作项数据传输对象
 */
public class QuickActionItemDTO {
    
    private Long id;
    
    @NotNull(message = "操作类型不能为空")
    private MessageQuickActionMenu.ActionType actionType;
    
    @NotBlank(message = "标签不能为空")
    @Size(max = 100, message = "标签不能超过100个字符")
    private String label;
    
    @Size(max = 50, message = "图标不能超过50个字符")
    private String icon;
    
    @Size(max = 50, message = "图标颜色不能超过50个字符")
    private String iconColor;
    
    @Size(max = 20, message = "快捷键不能超过20个字符")
    private String shortcutKey;
    
    private Integer sortOrder;
    private Boolean isVisible;
    private Boolean isEnabled;
    private Boolean requiresConfirmation;
    
    @Size(max = 500, message = "确认消息不能超过500个字符")
    private String confirmationMessage;
    
    @Size(max = 1000, message = "自定义数据不能超过1000个字符")
    private String customActionData;
    
    @Size(max = 50, message = "可见性条件不能超过50个字符")
    private String visibilityCondition;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造方法
    public QuickActionItemDTO() {}
    
    public QuickActionItemDTO(QuickActionItem item) {
        this.id = item.getId();
        this.actionType = item.getActionType();
        this.label = item.getLabel();
        this.icon = item.getIcon();
        this.iconColor = item.getIconColor();
        this.shortcutKey = item.getShortcutKey();
        this.sortOrder = item.getSortOrder();
        this.isVisible = item.getIsVisible();
        this.isEnabled = item.getIsEnabled();
        this.requiresConfirmation = item.getRequiresConfirmation();
        this.confirmationMessage = item.getConfirmationMessage();
        this.customActionData = item.getCustomActionData();
        this.visibilityCondition = item.getVisibilityCondition();
        this.createdAt = item.getCreatedAt();
        this.updatedAt = item.getUpdatedAt();
    }
    
    // 转换为实体
    public QuickActionItem toEntity() {
        QuickActionItem item = new QuickActionItem();
        item.setId(this.id);
        item.setActionType(this.actionType);
        item.setLabel(this.label);
        item.setIcon(this.icon);
        item.setIconColor(this.iconColor);
        item.setShortcutKey(this.shortcutKey);
        item.setSortOrder(this.sortOrder != null ? this.sortOrder : 0);
        item.setIsVisible(this.isVisible != null ? this.isVisible : true);
        item.setIsEnabled(this.isEnabled != null ? this.isEnabled : true);
        item.setRequiresConfirmation(this.requiresConfirmation != null ? this.requiresConfirmation : false);
        item.setConfirmationMessage(this.confirmationMessage);
        item.setCustomActionData(this.customActionData);
        item.setVisibilityCondition(this.visibilityCondition);
        return item;
    }
    
    // 从实体创建DTO并设置排序
    public static QuickActionItemDTO fromEntity(QuickActionItem item, int sortOrder) {
        QuickActionItemDTO dto = new QuickActionItemDTO(item);
        dto.setSortOrder(sortOrder);
        return dto;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public MessageQuickActionMenu.ActionType getActionType() { return actionType; }
    public void setActionType(MessageQuickActionMenu.ActionType actionType) { this.actionType = actionType; }
    
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public String getIconColor() { return iconColor; }
    public void setIconColor(String iconColor) { this.iconColor = iconColor; }
    
    public String getShortcutKey() { return shortcutKey; }
    public void setShortcutKey(String shortcutKey) { this.shortcutKey = shortcutKey; }
    
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    
    public Boolean getIsVisible() { return isVisible; }
    public void setIsVisible(Boolean isVisible) { this.isVisible = isVisible; }
    
    public Boolean getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Boolean isEnabled) { this.isEnabled = isEnabled; }
    
    public Boolean getRequiresConfirmation() { return requiresConfirmation; }
    public void setRequiresConfirmation(Boolean requiresConfirmation) { this.requiresConfirmation = requiresConfirmation; }
    
    public String getConfirmationMessage() { return confirmationMessage; }
    public void setConfirmationMessage(String confirmationMessage) { this.confirmationMessage = confirmationMessage; }
    
    public String getCustomActionData() { return customActionData; }
    public void setCustomActionData(String customActionData) { this.customActionData = customActionData; }
    
    public String getVisibilityCondition() { return visibilityCondition; }
    public void setVisibilityCondition(String visibilityCondition) { this.visibilityCondition = visibilityCondition; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return "QuickActionItemDTO{" +
                "id=" + id +
                ", actionType=" + actionType +
                ", label='" + label + '\'' +
                ", sortOrder=" + sortOrder +
                '}';
    }
}
