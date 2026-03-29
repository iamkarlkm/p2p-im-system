package com.im.backend.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 消息快捷操作菜单配置实体
 * 支持自定义快捷操作按钮、快捷回复、快捷表情等
 */
@Entity
@Table(name = "message_quick_action_menu", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_conversation_id", columnList = "conversationId"),
    @Index(name = "idx_menu_type", columnList = "menuType")
})
public class MessageQuickActionMenu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column
    private Long conversationId;
    
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private MenuType menuType;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false)
    private Integer sortOrder;
    
    @Column(nullable = false)
    private Boolean isEnabled;
    
    @Column(nullable = false)
    private Boolean isDefault;
    
    @Column(length = 50)
    private String icon;
    
    @Column(length = 50)
    private String color;
    
    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("sortOrder ASC")
    private Set<QuickActionItem> items = new HashSet<>();
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    // 菜单类型枚举
    public enum MenuType {
        MESSAGE_LONG_PRESS,      // 消息长按菜单
        MESSAGE_RIGHT_CLICK,     // 消息右键菜单
        INPUT_TOOLBAR,           // 输入框工具栏
        QUICK_REPLY,             // 快捷回复菜单
        QUICK_EMOJI,             // 快捷表情菜单
        MESSAGE_ACTION_BAR,      // 消息操作栏
        CONTEXTUAL_ACTIONS       // 上下文相关操作
    }
    
    // 构造方法
    public MessageQuickActionMenu() {
        this.createdAt = LocalDateTime.now();
        this.isEnabled = true;
        this.isDefault = false;
        this.sortOrder = 0;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
    
    public MenuType getMenuType() { return menuType; }
    public void setMenuType(MenuType menuType) { this.menuType = menuType; }
    
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
    
    public Set<QuickActionItem> getItems() { return items; }
    public void setItems(Set<QuickActionItem> items) { this.items = items; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // 业务方法
    public void addItem(QuickActionItem item) {
        items.add(item);
        item.setMenu(this);
    }
    
    public void removeItem(QuickActionItem item) {
        items.remove(item);
        item.setMenu(null);
    }
    
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // 快捷操作项类型枚举
    public enum ActionType {
        REPLY,              // 回复
        FORWARD,            // 转发
        COPY,               // 复制
        DELETE,             // 删除
        RECALL,             // 撤回
        QUOTE,              // 引用
        MULTI_SELECT,       // 多选
        PIN,                // 置顶
        FAVORITE,           // 收藏
        TRANSLATE,          // 翻译
        SPEAK,              // 朗读
        EDIT,               // 编辑
        REMIND,             // 提醒
        SCHEDULE,           // 定时
        REACTION,           // 表情反应
        THREAD,             // 话题
        REPORT,             // 举报
        CUSTOM              // 自定义
    }
    
    @Override
    public String toString() {
        return "MessageQuickActionMenu{" +
                "id=" + id +
                ", userId=" + userId +
                ", menuType=" + menuType +
                ", name='" + name + '\'' +
                ", isEnabled=" + isEnabled +
                '}';
    }
}
