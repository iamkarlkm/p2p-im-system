/**
 * 消息快捷操作菜单类型定义
 * 完整类型系统支持
 */

// 菜单类型枚举
export enum MenuType {
  MESSAGE_LONG_PRESS = 'MESSAGE_LONG_PRESS',
  MESSAGE_RIGHT_CLICK = 'MESSAGE_RIGHT_CLICK',
  INPUT_TOOLBAR = 'INPUT_TOOLBAR',
  QUICK_REPLY = 'QUICK_REPLY',
  QUICK_EMOJI = 'QUICK_EMOJI',
  MESSAGE_ACTION_BAR = 'MESSAGE_ACTION_BAR',
  CONTEXTUAL_ACTIONS = 'CONTEXTUAL_ACTIONS'
}

// 操作类型枚举
export enum ActionType {
  REPLY = 'REPLY',
  FORWARD = 'FORWARD',
  COPY = 'COPY',
  DELETE = 'DELETE',
  RECALL = 'RECALL',
  QUOTE = 'QUOTE',
  MULTI_SELECT = 'MULTI_SELECT',
  PIN = 'PIN',
  FAVORITE = 'FAVORITE',
  TRANSLATE = 'TRANSLATE',
  SPEAK = 'SPEAK',
  EDIT = 'EDIT',
  REMIND = 'REMIND',
  SCHEDULE = 'SCHEDULE',
  REACTION = 'REACTION',
  THREAD = 'THREAD',
  REPORT = 'REPORT',
  CUSTOM = 'CUSTOM'
}

// 快捷操作项接口
export interface QuickActionItem {
  id: number;
  actionType: ActionType;
  label: string;
  icon?: string;
  iconColor?: string;
  shortcutKey?: string;
  sortOrder: number;
  isVisible: boolean;
  isEnabled: boolean;
  requiresConfirmation: boolean;
  confirmationMessage?: string;
  customActionData?: string;
  visibilityCondition?: string;
  createdAt: string;
  updatedAt?: string;
}

// 消息快捷操作菜单接口
export interface MessageQuickActionMenu {
  id: number;
  userId: number;
  conversationId?: number;
  menuType: MenuType;
  name: string;
  description?: string;
  sortOrder: number;
  isEnabled: boolean;
  isDefault: boolean;
  icon?: string;
  color?: string;
  items: QuickActionItem[];
  createdAt: string;
  updatedAt?: string;
}

// 创建菜单请求
export interface CreateMenuRequest {
  menuType: MenuType;
  name: string;
  description?: string;
  sortOrder?: number;
  icon?: string;
  color?: string;
  items: CreateItemRequest[];
}

// 创建操作项请求
export interface CreateItemRequest {
  actionType: ActionType;
  label: string;
  icon?: string;
  iconColor?: string;
  shortcutKey?: string;
  sortOrder?: number;
  requiresConfirmation?: boolean;
  confirmationMessage?: string;
  customActionData?: string;
  visibilityCondition?: string;
}

// 更新菜单请求
export interface UpdateMenuRequest {
  name?: string;
  description?: string;
  menuType?: MenuType;
  sortOrder?: number;
  isEnabled?: boolean;
  icon?: string;
  color?: string;
  items?: CreateItemRequest[];
}

// 菜单过滤器
export interface MenuFilter {
  menuType?: MenuType;
  isEnabled?: boolean;
  isDefault?: boolean;
}

// 菜单上下文 - 用于动态生成菜单
export interface MenuContext {
  messageId?: number;
  conversationId?: number;
  messageType?: string;
  isOwnMessage: boolean;
  canRecall: boolean;
  canEdit: boolean;
  canDelete: boolean;
  hasSelectedText: boolean;
  isMultiSelectMode: boolean;
  userPermissions: string[];
}

// 菜单操作结果
export interface MenuActionResult {
  success: boolean;
  actionType: ActionType;
  messageId?: number;
  data?: unknown;
  error?: string;
}

// 菜单显示位置
export interface MenuPosition {
  x: number;
  y: number;
  placement?: 'top' | 'bottom' | 'left' | 'right';
}

// 菜单显示选项
export interface MenuDisplayOptions {
  position: MenuPosition;
  context: MenuContext;
  menuType: MenuType;
  animation?: boolean;
  autoClose?: boolean;
  closeOnClickOutside?: boolean;
}

// 快捷操作配置
export interface QuickActionConfig {
  showIcons: boolean;
  showShortcutKeys: boolean;
  showDividers: boolean;
  maxVisibleItems: number;
  enableAnimations: boolean;
  theme: 'light' | 'dark' | 'auto';
}

// 默认配置
export const DEFAULT_QUICK_ACTION_CONFIG: QuickActionConfig = {
  showIcons: true,
  showShortcutKeys: true,
  showDividers: true,
  maxVisibleItems: 10,
  enableAnimations: true,
  theme: 'auto'
};

// 菜单项分组
export interface MenuItemGroup {
  id: string;
  label?: string;
  items: QuickActionItem[];
  showDividerAfter?: boolean;
}

// 可见菜单项 - 经过上下文过滤后的结果
export interface VisibleMenuItem extends QuickActionItem {
  isVisible: true;
  groupId?: string;
}

// 操作处理器映射
export type ActionHandler = (context: MenuContext, item: QuickActionItem) => Promise<MenuActionResult> | MenuActionResult;

export interface ActionHandlerRegistry {
  [key: string]: ActionHandler;
}

// 菜单状态
export interface MenuState {
  isOpen: boolean;
  currentMenu: MessageQuickActionMenu | null;
  visibleItems: VisibleMenuItem[];
  selectedItems: number[];
  position: MenuPosition | null;
  context: MenuContext | null;
  isLoading: boolean;
  error: string | null;
}

// 菜单事件类型
export enum MenuEventType {
  MENU_OPENED = 'MENU_OPENED',
  MENU_CLOSED = 'MENU_CLOSED',
  ITEM_CLICKED = 'ITEM_CLICKED',
  ITEM_HOVERED = 'ITEM_HOVERED',
  ACTION_EXECUTED = 'ACTION_EXECUTED',
  ACTION_FAILED = 'ACTION_FAILED'
}

// 菜单事件
export interface MenuEvent {
  type: MenuEventType;
  menuId?: number;
  itemId?: number;
  actionType?: ActionType;
  timestamp: number;
  data?: unknown;
}

// API 响应类型
export interface MenuListResponse {
  data: MessageQuickActionMenu[];
  total: number;
}

export interface MenuResponse {
  data: MessageQuickActionMenu;
}

// 快捷操作创建器工具类型
export interface QuickActionBuilder {
  setActionType(type: ActionType): QuickActionBuilder;
  setLabel(label: string): QuickActionBuilder;
  setIcon(icon: string): QuickActionBuilder;
  setIconColor(color: string): QuickActionBuilder;
  setShortcut(shortcut: string): QuickActionBuilder;
  setOrder(order: number): QuickActionBuilder;
  requireConfirmation(message: string): QuickActionBuilder;
  setVisibility(condition: string): QuickActionBuilder;
  setCustomData(data: string): QuickActionBuilder;
  build(): CreateItemRequest;
}
