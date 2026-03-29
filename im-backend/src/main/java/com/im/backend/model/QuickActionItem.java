package com.im.backend.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 快捷操作项实体
 * 菜单中的具体操作按钮配置
 */
@Entity
@Table(name = "quick_action_item", indexes = {
    @Index(name = "idx_menu_id", columnList = "menu_id"),
    @Index(name = "idx_action_type", columnList = "actionType"),
    @Index(name = "idx_sort_order", columnList = "sortOrder")
})
public class QuickActionItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private MessageQuickActionMenu menu;
    
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private MessageQuickActionMenu.ActionType actionType;
    
    @Column(nullable = false, length = 100)
    private String label;
    
    @Column(length = 50)
    private String icon;
    
    @Column(length = 50)
    private String iconColor;
    
    @Column(length = 20)
    private String shortcutKey;
    
    @Column(nullable = false)
    private Integer sortOrder;
    
    @Column(nullable = false)
    private Boolean isVisible;
    
    @Column(nullable = false)
    private Boolean isEnabled;
    
    @Column
    private Boolean requiresConfirmation;
    
    @Column(length = 500)
    private String confirmationMessage;
    
    @Column(length = 1000)
    private String customActionData;
    
    @Column(length = 50)
    private String visibilityCondition;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    // 构造方法
    public QuickActionItem() {
        this.createdAt = LocalDateTime.now();
        this.isVisible = true;
        this.isEnabled = true;
        this.requiresConfirmation = false;
        this.sortOrder = 0;
    }
    
    // 快捷构造方法
    public QuickActionItem(MessageQuickActionMenu.ActionType actionType, String label, String icon) {
        this();
        this.actionType = actionType;
        this.label = label;
        this.icon = icon;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public MessageQuickActionMenu getMenu() { return menu; }
    public void setMenu(MessageQuickActionMenu menu) { this.menu = menu; }
    
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
    
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // 创建标准操作项的静态方法
    public static QuickActionItem createReplyItem() {
        return new QuickActionItem(MessageQuickActionMenu.ActionType.REPLY, "回复", "reply");
    }
    
    public static QuickActionItem createForwardItem() {
        return new QuickActionItem(MessageQuickActionMenu.ActionType.FORWARD, "转发", "forward");
    }
    
    public static QuickActionItem createCopyItem() {
        return new QuickActionItem(MessageQuickActionMenu.ActionType.COPY, "复制", "copy");
    }
    
    public static QuickActionItem createDeleteItem() {
        QuickActionItem item = new QuickActionItem(MessageQuickActionMenu.ActionType.DELETE, "删除", "delete");
        item.setRequiresConfirmation(true);
        item.setConfirmationMessage("确定要删除这条消息吗？");
        item.setIconColor("#FF4D4F");
        return item;
    }
    
    public static QuickActionItem createRecallItem() {
        QuickActionItem item = new QuickActionItem(MessageQuickActionMenu.ActionType.RECALL, "撤回", "recall");
        item.setRequiresConfirmation(true);
        item.setConfirmationMessage("确定要撤回这条消息吗？");
        return item;
    }
    
    public static QuickActionItem createQuoteItem() {
        return new QuickActionItem(MessageQuickActionMenu.ActionType.QUOTE, "引用", "quote");
    }
    
    public static QuickActionItem createMultiSelectItem() {
        return new QuickActionItem(MessageQuickActionMenu.ActionType.MULTI_SELECT, "多选", "check-square");
    }
    
    public static QuickActionItem createPinItem() {
        return new QuickActionItem(MessageQuickActionMenu.ActionType.PIN, "置顶", "pushpin");
    }
    
    public static QuickActionItem createFavoriteItem() {
        return new QuickActionItem(MessageQuickActionMenu.ActionType.FAVORITE, "收藏", "star");
    }
    
    public static QuickActionItem createTranslateItem() {
        return new QuickActionItem(MessageQuickActionMenu.ActionType.TRANSLATE, "翻译", "translation");
    }
    
    public static QuickActionItem createSpeakItem() {
        return new QuickActionItem(MessageQuickActionMenu.ActionType.SPEAK, "朗读", "sound");
    }
    
    public static QuickActionItem createEditItem() {
        return new QuickActionItem(MessageQuickActionMenu.ActionType.EDIT, "编辑", "edit");
    }
    
    public static QuickActionItem createRemindItem() {
        return new QuickActionItem(MessageQuickActionMenu.ActionType.REMIND, "提醒", "bell");
    }
    
    public static QuickActionItem createScheduleItem() {
        return new QuickActionItem(MessageQuickActionMenu.ActionType.SCHEDULE, "定时发送", "clock-circle");
    }
    
    public static QuickActionItem createReactionItem() {
        return new QuickActionItem(MessageQuickActionMenu.ActionType.REACTION, "表情", "smile");
    }
    
    public static QuickActionItem createThreadItem() {
        return new QuickActionItem(MessageQuickActionMenu.ActionType.THREAD, "话题", "message");
    }
    
    public static QuickActionItem createReportItem() {
        QuickActionItem item = new QuickActionItem(MessageQuickActionMenu.ActionType.REPORT, "举报", "flag");
        item.setIconColor("#FF4D4F");
        return item;
    }
    
    @Override
    public String toString() {
        return "QuickActionItem{" +
                "id=" + id +
                ", actionType=" + actionType +
                ", label='" + label + '\'' +
                ", sortOrder=" + sortOrder +
                '}';
    }
}
