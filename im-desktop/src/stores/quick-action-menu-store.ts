/**
 * 快捷操作菜单状态管理 (MobX Store)
 */

import { makeAutoObservable, runInAction } from 'mobx';
import {
  MessageQuickActionMenu,
  QuickActionItem,
  MenuType,
  ActionType,
  MenuContext,
  MenuPosition,
  MenuState,
  MenuDisplayOptions,
  MenuEvent,
  MenuEventType,
  VisibleMenuItem,
  ActionHandler,
  ActionHandlerRegistry,
  QuickActionConfig,
  DEFAULT_QUICK_ACTION_CONFIG,
  CreateMenuRequest
} from '../types/quick-action-menu';
import { quickActionMenuApi } from '../api/quick-action-menu-api';

class QuickActionMenuStore {
  // ============ 状态 ============
  menus: MessageQuickActionMenu[] = [];
  currentMenu: MessageQuickActionMenu | null = null;
  visibleItems: VisibleMenuItem[] = [];
  isLoading = false;
  error: string | null = null;
  
  // UI 状态
  isMenuOpen = false;
  menuPosition: MenuPosition | null = null;
  menuContext: MenuContext | null = null;
  currentMenuType: MenuType | null = null;
  
  // 配置
  config: QuickActionConfig = DEFAULT_QUICK_ACTION_CONFIG;
  
  // 事件监听
  private eventListeners: ((event: MenuEvent) => void)[] = [];
  
  // 操作处理器
  private actionHandlers: ActionHandlerRegistry = {};
  
  constructor() {
    makeAutoObservable(this);
    this.registerDefaultHandlers();
  }
  
  // ============ 计算属性 ============
  
  get enabledMenus(): MessageQuickActionMenu[] {
    return this.menus.filter(m => m.isEnabled);
  }
  
  get defaultMenus(): MessageQuickActionMenu[] {
    return this.menus.filter(m => m.isDefault && m.isEnabled);
  }
  
  get menuByType() {
    return (type: MenuType) => this.menus.find(m => m.menuType === type && m.isDefault);
  }
  
  // ============ 数据加载 ============
  
  async loadUserMenus(userId: number) {
    this.isLoading = true;
    try {
      const menus = await quickActionMenuApi.getUserMenus(userId);
      runInAction(() => {
        this.menus = menus;
        this.error = null;
      });
    } catch (err) {
      runInAction(() => {
        this.error = err instanceof Error ? err.message : '加载失败';
      });
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }
  
  async initializeDefaultMenus(userId: number) {
    this.isLoading = true;
    try {
      const menus = await quickActionMenuApi.initializeDefaultMenus(userId);
      runInAction(() => {
        this.menus = menus;
        this.error = null;
      });
    } catch (err) {
      runInAction(() => {
        this.error = err instanceof Error ? err.message : '初始化失败';
      });
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }
  
  // ============ 菜单显示 ============
  
  showMenu(options: MenuDisplayOptions) {
    const { position, context, menuType } = options;
    
    // 查找对应类型的默认菜单
    const menu = this.menuByType(menuType);
    if (!menu) {
      console.warn(`未找到类型为 ${menuType} 的菜单`);
      return;
    }
    
    // 过滤可见项
    const visibleItems = this.filterVisibleItems(menu.items, context);
    
    runInAction(() => {
      this.currentMenu = menu;
      this.visibleItems = visibleItems;
      this.menuPosition = position;
      this.menuContext = context;
      this.currentMenuType = menuType;
      this.isMenuOpen = true;
    });
    
    this.emitEvent({
      type: MenuEventType.MENU_OPENED,
      menuId: menu.id,
      timestamp: Date.now()
    });
  }
  
  closeMenu() {
    runInAction(() => {
      this.isMenuOpen = false;
      this.currentMenu = null;
      this.visibleItems = [];
      this.menuPosition = null;
      this.menuContext = null;
      this.currentMenuType = null;
    });
    
    this.emitEvent({
      type: MenuEventType.MENU_CLOSED,
      timestamp: Date.now()
    });
  }
  
  // ============ 过滤可见项 ============
  
  private filterVisibleItems(items: QuickActionItem[], context: MenuContext): VisibleMenuItem[] {
    return items
      .filter(item => {
        // 基础可见性检查
        if (!item.isVisible || !item.isEnabled) return false;
        
        // 根据上下文条件过滤
        switch (item.actionType) {
          case ActionType.RECALL:
            return context.isOwnMessage && context.canRecall;
          case ActionType.EDIT:
            return context.isOwnMessage && context.canEdit;
          case ActionType.DELETE:
            return context.canDelete;
          case ActionType.COPY:
            return context.hasSelectedText;
          default:
            return true;
        }
      })
      .sort((a, b) => a.sortOrder - b.sortOrder)
      .map(item => ({ ...item, isVisible: true }));
  }
  
  // ============ 操作处理 ============
  
  registerActionHandler(actionType: ActionType, handler: ActionHandler) {
    this.actionHandlers[actionType] = handler;
  }
  
  private registerDefaultHandlers() {
    // 注册默认处理器
    this.registerActionHandler(ActionType.COPY, (ctx, item) => {
      // 默认复制处理
      return { success: true, actionType: ActionType.COPY };
    });
    
    this.registerActionHandler(ActionType.DELETE, (ctx, item) => {
      // 默认删除处理
      return { success: true, actionType: ActionType.DELETE };
    });
  }
  
  async executeAction(item: VisibleMenuItem): Promise<boolean> {
    if (!this.menuContext) return false;
    
    // 发送点击事件
    this.emitEvent({
      type: MenuEventType.ITEM_CLICKED,
      menuId: this.currentMenu?.id,
      itemId: item.id,
      actionType: item.actionType,
      timestamp: Date.now()
    });
    
    // 获取处理器
    const handler = this.actionHandlers[item.actionType];
    if (!handler) {
      console.warn(`未找到 ${item.actionType} 的处理器`);
      return false;
    }
    
    try {
      const result = await handler(this.menuContext, item);
      
      if (result.success) {
        this.emitEvent({
          type: MenuEventType.ACTION_EXECUTED,
          actionType: item.actionType,
          timestamp: Date.now(),
          data: result.data
        });
        this.closeMenu();
      } else {
        this.emitEvent({
          type: MenuEventType.ACTION_FAILED,
          actionType: item.actionType,
          timestamp: Date.now(),
          data: result.error
        });
      }
      
      return result.success;
    } catch (err) {
      this.emitEvent({
        type: MenuEventType.ACTION_FAILED,
        actionType: item.actionType,
        timestamp: Date.now(),
        data: err instanceof Error ? err.message : '执行失败'
      });
      return false;
    }
  }
  
  // ============ 菜单管理 ============
  
  async createMenu(data: CreateMenuRequest) {
    this.isLoading = true;
    try {
      const menu = await quickActionMenuApi.createMenu(data);
      runInAction(() => {
        this.menus.push(menu);
        this.error = null;
      });
      return menu;
    } catch (err) {
      runInAction(() => {
        this.error = err instanceof Error ? err.message : '创建失败';
      });
      throw err;
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }
  
  async deleteMenu(menuId: number) {
    this.isLoading = true;
    try {
      await quickActionMenuApi.deleteMenu(menuId);
      runInAction(() => {
        this.menus = this.menus.filter(m => m.id !== menuId);
        this.error = null;
      });
    } catch (err) {
      runInAction(() => {
        this.error = err instanceof Error ? err.message : '删除失败';
      });
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }
  
  async setAsDefault(menuId: number) {
    this.isLoading = true;
    try {
      const menu = await quickActionMenuApi.setAsDefault(menuId);
      runInAction(() => {
        // 更新同类型菜单的默认状态
        this.menus = this.menus.map(m => {
          if (m.menuType === menu.menuType) {
            return { ...m, isDefault: m.id === menuId };
          }
          return m;
        });
        this.error = null;
      });
    } catch (err) {
      runInAction(() => {
        this.error = err instanceof Error ? err.message : '设置失败';
      });
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }
  
  // ============ 事件系统 ============
  
  onEvent(listener: (event: MenuEvent) => void) {
    this.eventListeners.push(listener);
    return () => {
      this.eventListeners = this.eventListeners.filter(l => l !== listener);
    };
  }
  
  private emitEvent(event: MenuEvent) {
    this.eventListeners.forEach(listener => listener(event));
  }
  
  // ============ 配置 ============
  
  updateConfig(config: Partial<QuickActionConfig>) {
    this.config = { ...this.config, ...config };
  }
  
  resetConfig() {
    this.config = DEFAULT_QUICK_ACTION_CONFIG;
  }
}

export const quickActionMenuStore = new QuickActionMenuStore();
export default quickActionMenuStore;
