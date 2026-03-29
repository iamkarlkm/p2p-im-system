/**
 * 引用消息侧边栏数据类型定义
 */

/**
 * 引用消息侧边栏项目
 */
export interface MessageQuoteSidebar {
  /**
   * 记录 ID
   */
  id: number;

  /**
   * 引用消息 ID
   */
  quoteId: number;

  /**
   * 用户 ID
   */
  userId: number;

  /**
   * 会话 ID
   */
  sessionId: number;

  /**
   * 侧边栏位置索引（用于排序）
   */
  sidebarIndex?: number;

  /**
   * 是否固定在侧边栏
   */
  isPinned: boolean;

  /**
   * 引用消息的预览内容（截取前 100 个字符）
   */
  previewContent?: string;

  /**
   * 引用消息的发送者 ID
   */
  senderId?: number;

  /**
   * 引用消息的发送者昵称
   */
  senderNickname?: string;

  /**
   * 引用消息的类型：TEXT, IMAGE, VOICE, VIDEO, FILE, SYSTEM
   */
  messageType?: string;

  /**
   * 引用消息的创建时间（原消息时间）
   */
  originalCreatedAt: string;

  /**
   * 最后查看时间
   */
  lastViewedAt: string;

  /**
   * 侧边栏记录创建时间
   */
  createdAt: string;

  /**
   * 更新时间
   */
  updatedAt?: string;
}

/**
 * 侧边栏视图模式
 */
export type SidebarViewMode = 'all' | 'pinned' | 'recent';

/**
 * 侧边栏排序字段
 */
export type SidebarSortField = 'lastViewed' | 'createdAt' | 'originalCreatedAt';

/**
 * 侧边栏排序方向
 */
export type SidebarSortDirection = 'asc' | 'desc';

/**
 * 消息类型枚举
 */
export enum MessageType {
  TEXT = 'TEXT',
  IMAGE = 'IMAGE',
  VOICE = 'VOICE',
  VIDEO = 'VIDEO',
  FILE = 'FILE',
  SYSTEM = 'SYSTEM',
  LINK = 'LINK',
  STICKER = 'STICKER',
  LOCATION = 'LOCATION',
  CONTACT = 'CONTACT',
}

/**
 * 侧边栏筛选条件
 */
export interface SidebarFilter {
  /**
   * 视图模式
   */
  viewMode?: SidebarViewMode;

  /**
   * 搜索关键词
   */
  keyword?: string;

  /**
   * 消息类型过滤
   */
  messageType?: MessageType;

  /**
   * 发送者 ID 过滤
   */
  senderId?: number;

  /**
   * 是否只显示已固定
   */
  pinnedOnly?: boolean;

  /**
   * 时间范围开始
   */
  startDate?: string;

  /**
   * 时间范围结束
   */
  endDate?: string;
}

/**
 * 侧边栏排序配置
 */
export interface SidebarSortConfig {
  /**
   * 排序字段
   */
  sortBy: SidebarSortField;

  /**
   * 排序方向
   */
  sortDirection: SidebarSortDirection;
}

/**
 * 侧边栏统计信息
 */
export interface SidebarStats {
  /**
   * 总数量
   */
  totalCount: number;

  /**
   * 已固定数量
   */
  pinnedCount: number;

  /**
   * 最近查看数量（3 天内）
   */
  recentCount: number;

  /**
   * 按消息类型统计
   */
  byMessageType: Record<string, number>;

  /**
   * 按发送者统计
   */
  bySender?: Record<number, number>;
}

/**
 * 侧边栏操作结果
 */
export interface SidebarActionResult {
  /**
   * 是否成功
   */
  success: boolean;

  /**
   * 消息
   */
  message: string;

  /**
   * 受影响的项目 ID 列表
   */
  affectedIds?: number[];

  /**
   * 错误信息
   */
  error?: string;
}

/**
 * 侧边栏批量操作请求
 */
export interface BatchOperationRequest {
  /**
   * 项目 ID 列表
   */
  ids: number[];

  /**
   * 操作类型
   */
  operation: 'pin' | 'unpin' | 'delete' | 'updateIndex';

  /**
   * 新的索引值（仅当 operation 为 updateIndex 时使用）
   */
  newIndex?: number;
}

/**
 * 侧边栏配置选项
 */
export interface SidebarConfig {
  /**
   * 是否启用
   */
  enabled: boolean;

  /**
   * 默认视图模式
   */
  defaultViewMode: SidebarViewMode;

  /**
   * 默认排序字段
   */
  defaultSortBy: SidebarSortField;

  /**
   * 默认排序方向
   */
  defaultSortDirection: SidebarSortDirection;

  /**
   * 是否显示搜索框
   */
  showSearch: boolean;

  /**
   * 是否显示筛选器
   */
  showFilter: boolean;

  /**
   * 是否显示统计信息
   */
  showStats: boolean;

  /**
   * 是否启用批量操作
   */
  enableBatchOperations: boolean;

  /**
   * 是否启用右键菜单
   */
  enableContextMenu: boolean;

  /**
   * 是否启用双击跳转
   */
  enableDoubleClickJump: boolean;

  /**
   * 自动清理天数阈值
   */
  autoCleanupDays: number;

  /**
   * 是否启用自动清理
   */
  enableAutoCleanup: boolean;
}

/**
 * 默认侧边栏配置
 */
export const DEFAULT_SIDEBAR_CONFIG: SidebarConfig = {
  enabled: true,
  defaultViewMode: 'all',
  defaultSortBy: 'lastViewed',
  defaultSortDirection: 'desc',
  showSearch: true,
  showFilter: true,
  showStats: true,
  enableBatchOperations: true,
  enableContextMenu: true,
  enableDoubleClickJump: true,
  autoCleanupDays: 30,
  enableAutoCleanup: true,
};

/**
 * 获取消息类型的显示名称
 */
export function getMessageTypeDisplayName(type: MessageType | string): string {
  const typeMap: Record<string, string> = {
    [MessageType.TEXT]: '文本',
    [MessageType.IMAGE]: '图片',
    [MessageType.VOICE]: '语音',
    [MessageType.VIDEO]: '视频',
    [MessageType.FILE]: '文件',
    [MessageType.SYSTEM]: '系统',
    [MessageType.LINK]: '链接',
    [MessageType.STICKER]: '表情',
    [MessageType.LOCATION]: '位置',
    [MessageType.CONTACT]: '联系人',
  };
  return typeMap[type.toUpperCase()] || '未知';
}

/**
 * 获取消息类型的颜色
 */
export function getMessageTypeColor(type: MessageType | string): string {
  const colorMap: Record<string, string> = {
    [MessageType.TEXT]: '#2196f3',
    [MessageType.IMAGE]: '#4caf50',
    [MessageType.VOICE]: '#ff9800',
    [MessageType.VIDEO]: '#f44336',
    [MessageType.FILE]: '#9c27b0',
    [MessageType.SYSTEM]: '#607d8b',
    [MessageType.LINK]: '#00bcd4',
    [MessageType.STICKER]: '#e91e63',
    [MessageType.LOCATION]: '#795548',
    [MessageType.CONTACT]: '#3f51b5',
  };
  return colorMap[type.toUpperCase()] || '#9e9e9e';
}

/**
 * 格式化相对时间
 */
export function formatRelativeTime(dateString: string, locale: string = 'zh-CN'): string {
  const date = new Date(dateString);
  const now = new Date();
  const diffMs = now.getTime() - date.getTime();
  const diffMins = Math.floor(diffMs / 60000);
  const diffHours = Math.floor(diffMs / 3600000);
  const diffDays = Math.floor(diffMs / 86400000);

  if (diffMins < 1) {
    return '刚刚';
  } else if (diffMins < 60) {
    return `${diffMins}分钟前`;
  } else if (diffHours < 24) {
    return `${diffHours}小时前`;
  } else if (diffDays < 30) {
    return `${diffDays}天前`;
  } else {
    return date.toLocaleDateString(locale);
  }
}

/**
 * 验证侧边栏项目数据
 */
export function validateSidebarItem(item: Partial<MessageQuoteSidebar>): boolean {
  if (!item.quoteId || !item.userId || !item.sessionId) {
    return false;
  }
  if (!item.originalCreatedAt || !item.lastViewedAt || !item.createdAt) {
    return false;
  }
  return true;
}

/**
 * 创建侧边栏项目（带默认值）
 */
export function createSidebarItem(
  quoteId: number,
  userId: number,
  sessionId: number,
  previewContent: string = '',
  senderId?: number,
  senderNickname?: string,
  messageType: string = 'TEXT'
): MessageQuoteSidebar {
  const now = new Date().toISOString();
  return {
    id: 0, // 由服务器生成
    quoteId,
    userId,
    sessionId,
    sidebarIndex: 0,
    isPinned: false,
    previewContent,
    senderId,
    senderNickname,
    messageType,
    originalCreatedAt: now,
    lastViewedAt: now,
    createdAt: now,
  };
}

/**
 * 比较两个侧边栏项目
 */
export function compareSidebarItems(
  a: MessageQuoteSidebar,
  b: MessageQuoteSidebar,
  sortBy: SidebarSortField,
  direction: SidebarSortDirection
): number {
  let aValue: Date, bValue: Date;

  switch (sortBy) {
    case 'lastViewed':
      aValue = new Date(a.lastViewedAt);
      bValue = new Date(b.lastViewedAt);
      break;
    case 'createdAt':
      aValue = new Date(a.createdAt);
      bValue = new Date(b.createdAt);
      break;
    case 'originalCreatedAt':
      aValue = new Date(a.originalCreatedAt);
      bValue = new Date(b.originalCreatedAt);
      break;
    default:
      aValue = new Date(a.lastViewedAt);
      bValue = new Date(b.lastViewedAt);
  }

  const comparison = aValue.getTime() - bValue.getTime();
  return direction === 'desc' ? -comparison : comparison;
}

/**
 * 过滤侧边栏项目列表
 */
export function filterSidebarItems(
  items: MessageQuoteSidebar[],
  filter: SidebarFilter
): MessageQuoteSidebar[] {
  return items.filter(item => {
    // 视图模式过滤
    if (filter.viewMode === 'pinned' && !item.isPinned) {
      return false;
    }
    if (filter.viewMode === 'recent') {
      const threeDaysAgo = new Date();
      threeDaysAgo.setDate(threeDaysAgo.getDate() - 3);
      if (new Date(item.lastViewedAt) <= threeDaysAgo) {
        return false;
      }
    }

    // 关键词搜索
    if (filter.keyword) {
      const keyword = filter.keyword.toLowerCase();
      const matchContent = item.previewContent?.toLowerCase().includes(keyword);
      const matchSender = item.senderNickname?.toLowerCase().includes(keyword);
      const matchType = item.messageType?.toLowerCase().includes(keyword);
      if (!matchContent && !matchSender && !matchType) {
        return false;
      }
    }

    // 消息类型过滤
    if (filter.messageType && item.messageType !== filter.messageType) {
      return false;
    }

    // 发送者过滤
    if (filter.senderId && item.senderId !== filter.senderId) {
      return false;
    }

    // 时间范围过滤
    if (filter.startDate && new Date(item.createdAt) < new Date(filter.startDate)) {
      return false;
    }
    if (filter.endDate && new Date(item.createdAt) > new Date(filter.endDate)) {
      return false;
    }

    return true;
  });
}

/**
 * 对侧边栏项目列表进行排序
 */
export function sortSidebarItems(
  items: MessageQuoteSidebar[],
  sortBy: SidebarSortField,
  direction: SidebarSortDirection
): MessageQuoteSidebar[] {
  return [...items].sort((a, b) => 
    compareSidebarItems(a, b, sortBy, direction)
  );
}

/**
 * 计算侧边栏统计信息
 */
export function calculateSidebarStats(items: MessageQuoteSidebar[]): SidebarStats {
  const stats: SidebarStats = {
    totalCount: items.length,
    pinnedCount: items.filter(item => item.isPinned).length,
    recentCount: 0,
    byMessageType: {},
  };

  const threeDaysAgo = new Date();
  threeDaysAgo.setDate(threeDaysAgo.getDate() - 3);

  items.forEach(item => {
    // 统计最近查看
    if (new Date(item.lastViewedAt) > threeDaysAgo) {
      stats.recentCount++;
    }

    // 按消息类型统计
    const type = item.messageType || 'UNKNOWN';
    stats.byMessageType[type] = (stats.byMessageType[type] || 0) + 1;
  });

  return stats;
}
