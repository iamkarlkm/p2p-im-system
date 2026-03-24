/**
 * Message Search Service (TypeScript)
 * 
 * 消息搜索服务 - 桌面端实现
 * 提供全文搜索、搜索建议、搜索历史等功能
 * 
 * 功能特性：
 * - 全文搜索（关键字高亮）
 * - 会话内搜索
 * - 搜索建议/补全
 * - 搜索历史记录
 * - 热门搜索展示
 * - 本地搜索缓存
 * - 防抖/节流优化
 */

// ==================== 类型定义 ====================

export interface SearchHit {
  messageId: number;
  conversationId: number;
  conversationType: 1 | 2; // 1-私聊 2-群聊
  senderId: number;
  senderNickname: string;
  messageType: number;
  content: string;        // 摘要/片段
  fullContent: string;    // 完整高亮内容
  fileName?: string;
  messageTime: string;    // ISO 8601
}

export interface SearchResult {
  hits: SearchHit[];
  total: number;
  page: number;
  size: number;
  totalPages: number;
  hasNext: boolean;
  hasPrevious: boolean;
  keyword: string;
}

export interface SearchRequest {
  keyword: string;
  userId?: number;
  page?: number;
  size?: number;
}

export interface ConversationSearchRequest {
  keyword: string;
  conversationId?: number;
  conversationType?: 1 | 2;
  page?: number;
  size?: number;
}

export interface AdvancedSearchRequest {
  keyword?: string;
  conversationId?: number;
  conversationType?: 1 | 2;
  senderId?: number;
  messageType?: number;
  startTime?: string;
  endTime?: string;
  page?: number;
  size?: number;
}

// ==================== API 配置 ====================

const API_BASE = 'http://localhost:8080/api';

// ==================== 防抖/节流工具 ====================

class Debouncer {
  private timer: ReturnType<typeof setTimeout> | null = null;

  debounce(fn: Function, delay: number): (...args: any[]) => void {
    return (...args: any[]) => {
      if (this.timer) {
        clearTimeout(this.timer);
      }
      this.timer = setTimeout(() => {
        fn(...args);
        this.timer = null;
      }, delay);
    };
  }

  cancel(): void {
    if (this.timer) {
      clearTimeout(this.timer);
      this.timer = null;
    }
  }
}

// ==================== 搜索服务类 ====================

class MessageSearchService {
  private static instance: MessageSearchService;
  private debouncer: Debouncer;
  private cache: Map<string, { data: SearchResult; timestamp: number }>;
  private cacheTTL: number = 5 * 60 * 1000; // 5分钟缓存
  private currentUserId: number | null = null;

  private constructor() {
    this.debouncer = new Debouncer();
    this.cache = new Map();
  }

  static getInstance(): MessageSearchService {
    if (!MessageSearchService.instance) {
      MessageSearchService.instance = new MessageSearchService();
    }
    return MessageSearchService.instance;
  }

  /**
   * 设置当前用户ID
   */
  setCurrentUser(userId: number): void {
    this.currentUserId = userId;
  }

  /**
   * 全局搜索
   */
  async search(keyword: string, page: number = 0, size: number = 20): Promise<SearchResult> {
    // 参数校验
    if (!keyword || keyword.trim().length === 0) {
      return this.emptyResult(keyword);
    }
    keyword = keyword.trim();

    // 检查缓存
    const cacheKey = `search:${keyword.toLowerCase()}:${page}:${size}`;
    const cached = this.getCached(cacheKey);
    if (cached) {
      console.debug('[Search] Cache hit:', keyword);
      return cached;
    }

    try {
      const request: SearchRequest = {
        keyword,
        userId: this.currentUserId || undefined,
        page,
        size
      };

      const response = await fetch(`${API_BASE}/search`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(request)
      });

      if (!response.ok) {
        throw new Error(`Search failed: ${response.status}`);
      }

      const result = await response.json();
      if (result.code === 200) {
        this.setCached(cacheKey, result.data);
        return result.data;
      } else {
        throw new Error(result.message || 'Search failed');
      }
    } catch (error) {
      console.error('[Search] Search error:', error);
      throw error;
    }
  }

  /**
   * 会话内搜索
   */
  async searchInConversation(
    conversationId: number,
    keyword: string,
    conversationType: 1 | 2 = 1,
    page: number = 0,
    size: number = 20
  ): Promise<SearchResult> {
    if (!keyword || keyword.trim().length === 0) {
      return this.emptyResult(keyword);
    }
    keyword = keyword.trim();

    try {
      const request: ConversationSearchRequest = {
        keyword,
        conversationId,
        conversationType,
        page,
        size
      };

      const response = await fetch(`${API_BASE}/search/conversation`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(request)
      });

      if (!response.ok) {
        throw new Error(`Conversation search failed: ${response.status}`);
      }

      const result = await response.json();
      if (result.code === 200) {
        return result.data;
      } else {
        throw new Error(result.message || 'Search failed');
      }
    } catch (error) {
      console.error('[Search] Conversation search error:', error);
      throw error;
    }
  }

  /**
   * 高级搜索（多条件）
   */
  async advancedSearch(request: AdvancedSearchRequest): Promise<SearchResult> {
    try {
      const response = await fetch(`${API_BASE}/search/advanced`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(request)
      });

      if (!response.ok) {
        throw new Error(`Advanced search failed: ${response.status}`);
      }

      const result = await response.json();
      if (result.code === 200) {
        return result.data;
      } else {
        throw new Error(result.message || 'Advanced search failed');
      }
    } catch (error) {
      console.error('[Search] Advanced search error:', error);
      throw error;
    }
  }

  /**
   * 获取搜索建议
   */
  async getSuggestions(prefix: string, limit: number = 10): Promise<string[]> {
    if (!prefix || prefix.trim().length === 0) {
      return [];
    }

    try {
      const url = `${API_BASE}/search/suggestions?prefix=${encodeURIComponent(prefix)}&limit=${limit}`;
      const response = await fetch(url);

      if (!response.ok) {
        throw new Error(`Get suggestions failed: ${response.status}`);
      }

      const result = await response.json();
      if (result.code === 200) {
        return result.data || [];
      } else {
        throw new Error(result.message || 'Get suggestions failed');
      }
    } catch (error) {
      console.error('[Search] Get suggestions error:', error);
      return [];
    }
  }

  /**
   * 获取热门搜索
   */
  async getHotSearch(limit: number = 10): Promise<string[]> {
    try {
      const url = `${API_BASE}/search/hot?limit=${limit}`;
      const response = await fetch(url);

      if (!response.ok) {
        throw new Error(`Get hot search failed: ${response.status}`);
      }

      const result = await response.json();
      if (result.code === 200) {
        return result.data || [];
      } else {
        throw new Error(result.message || 'Get hot search failed');
      }
    } catch (error) {
      console.error('[Search] Get hot search error:', error);
      return [];
    }
  }

  /**
   * 获取搜索历史
   */
  async getSearchHistory(userId: number, limit: number = 20): Promise<string[]> {
    try {
      const url = `${API_BASE}/search/history?userId=${userId}&limit=${limit}`;
      const response = await fetch(url);

      if (!response.ok) {
        throw new Error(`Get history failed: ${response.status}`);
      }

      const result = await response.json();
      if (result.code === 200) {
        return result.data || [];
      } else {
        throw new Error(result.message || 'Get history failed');
      }
    } catch (error) {
      console.error('[Search] Get history error:', error);
      return [];
    }
  }

  /**
   * 清空搜索历史
   */
  async clearSearchHistory(userId: number): Promise<void> {
    try {
      const url = `${API_BASE}/search/history?userId=${userId}`;
      const response = await fetch(url, {
        method: 'DELETE'
      });

      if (!response.ok) {
        throw new Error(`Clear history failed: ${response.status}`);
      }
    } catch (error) {
      console.error('[Search] Clear history error:', error);
      throw error;
    }
  }

  /**
   * 防抖搜索（适合实时搜索场景）
   */
  debouncedSearch(
    keyword: string,
    delay: number = 300,
    callback: (result: SearchResult) => void,
    errorCallback?: (error: Error) => void
  ): void {
    this.debouncer.debounce(async () => {
      try {
        const result = await this.search(keyword);
        callback(result);
      } catch (error) {
        if (errorCallback) {
          errorCallback(error as Error);
        }
      }
    }, delay)();
  }

  /**
   * 取消防抖
   */
  cancelDebounce(): void {
    this.debouncer.cancel();
  }

  // ==================== 缓存管理 ====================

  private getCached(key: string): SearchResult | null {
    const item = this.cache.get(key);
    if (item && Date.now() - item.timestamp < this.cacheTTL) {
      return item.data;
    }
    this.cache.delete(key);
    return null;
  }

  private setCached(key: string, data: SearchResult): void {
    // 限制缓存大小
    if (this.cache.size >= 100) {
      const oldestKey = this.cache.keys().next().value;
      this.cache.delete(oldestKey);
    }
    this.cache.set(key, { data, timestamp: Date.now() });
  }

  clearCache(): void {
    this.cache.clear();
  }

  // ==================== 工具方法 ====================

  private emptyResult(keyword: string): SearchResult {
    return {
      hits: [],
      total: 0,
      page: 0,
      size: 0,
      totalPages: 0,
      hasNext: false,
      hasPrevious: false,
      keyword: keyword || ''
    };
  }

  /**
   * 提取高亮内容中的纯文本
   */
  static stripHighlight(html: string): string {
    return html.replace(/<em>/g, '').replace(/<\/em>/g, '');
  }

  /**
   * 检查关键词是否匹配
   */
  static matchesKeyword(text: string, keyword: string): boolean {
    if (!text || !keyword) return false;
    return text.toLowerCase().includes(keyword.toLowerCase());
  }

  /**
   * 格式化消息时间
   */
  static formatMessageTime(isoTime: string): string {
    const date = new Date(isoTime);
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));

    if (days === 0) {
      return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } else if (days === 1) {
      return '昨天 ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } else if (days < 7) {
      return date.toLocaleDateString([], { weekday: 'short', hour: '2-digit', minute: '2-digit' });
    } else {
      return date.toLocaleDateString([], { month: 'short', day: 'numeric' });
    }
  }

  /**
   * 获取消息类型名称
   */
  static getMessageTypeName(type: number): string {
    const types: Record<number, string> = {
      1: '文本',
      2: '图片',
      3: '语音',
      4: '视频',
      5: '文件',
      6: '位置',
      7: '名片',
      8: '撤回',
      9: '引用',
      10: '阅后即焚'
    };
    return types[type] || '未知';
  }
}

// ==================== 导出单例 ====================

export const messageSearchService = MessageSearchService.getInstance();

// ==================== 默认导出 ====================

export default messageSearchService;
