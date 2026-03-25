/**
 * 消息草稿跨设备同步服务
 * 用于在桌面端管理消息草稿的本地存储和云端同步
 */

export interface MessageDraft {
  id?: number;
  userId: number;
  deviceId: string;
  conversationId: string;
  conversationType?: string;
  draftContent: string;
  draftType: string;
  replyToMessageId?: string;
  attachments?: string;
  mentions?: string;
  localVersion: number;
  serverVersion: number;
  lastUpdatedAt: string;
  syncStatus: 'PENDING' | 'SYNCING' | 'SYNCED' | 'CONFLICT' | 'ERROR';
  conflictInfo?: string;
  autoSave: boolean;
  createdAt: string;
  cleared: boolean;
  cursorPosition?: number;
  selectionRange?: string;
  language?: string;
  imeState?: string;
  active: boolean;
  contextInfo?: string;
  metadata?: string;
}

export interface DraftStatistics {
  totalDrafts: number;
  pendingSync: number;
  conflicts: number;
  lastUpdated: string;
}

export interface SyncResponse {
  success: boolean;
  draft?: MessageDraft;
  error?: string;
  conflict?: boolean;
}

export interface BatchSyncItem {
  deviceId: string;
  conversationId: string;
  draftContent: string;
  localVersion: number;
}

class MessageDraftService {
  private baseUrl = '/api/v1/drafts';
  private localStorageKey = 'im_drafts_cache';
  private pendingSyncQueue: MessageDraft[] = [];
  private syncInterval: number | null = null;
  private isSyncing = false;
  private retryCount = 0;
  private maxRetries = 3;

  /**
   * 保存或更新草稿（本地缓存+云端同步）
   */
  async saveDraft(
    userId: number,
    deviceId: string,
    conversationId: string,
    draftContent: string,
    options: {
      draftType?: string;
      autoSave?: boolean;
      replyToMessageId?: string;
      attachments?: string;
      mentions?: string;
      immediateSync?: boolean;
    } = {}
  ): Promise<SyncResponse> {
    try {
      // 创建本地草稿对象
      const draft: MessageDraft = {
        userId,
        deviceId,
        conversationId,
        draftContent,
        draftType: options.draftType || 'TEXT',
        replyToMessageId: options.replyToMessageId,
        attachments: options.attachments,
        mentions: options.mentions,
        localVersion: this.generateVersion(),
        serverVersion: 0,
        lastUpdatedAt: new Date().toISOString(),
        syncStatus: 'PENDING',
        autoSave: options.autoSave || false,
        createdAt: new Date().toISOString(),
        cleared: !draftContent || draftContent.trim().length === 0,
        active: true,
      };

      // 保存到本地存储
      await this.saveToLocalStorage(draft);

      // 立即同步或加入队列
      if (options.immediateSync) {
        return await this.syncDraftToServer(draft);
      } else {
        this.addToSyncQueue(draft);
        return {
          success: true,
          draft,
        };
      }
    } catch (error) {
      console.error('保存草稿失败:', error);
      return {
        success: false,
        error: '保存草稿失败',
      };
    }
  }

  /**
   * 获取指定会话的草稿
   */
  async getDraft(userId: number, conversationId: string): Promise<MessageDraft | null> {
    try {
      // 先检查本地缓存
      const localDraft = await this.getFromLocalStorage(userId, conversationId);
      if (localDraft) {
        return localDraft;
      }

      // 从服务器获取
      const response = await fetch(`${this.baseUrl}/${conversationId}?userId=${userId}`);
      if (response.ok) {
        const serverDraft: MessageDraft = await response.json();
        // 保存到本地缓存
        await this.saveToLocalStorage(serverDraft);
        return serverDraft;
      }
      
      return null;
    } catch (error) {
      console.error('获取草稿失败:', error);
      return null;
    }
  }

  /**
   * 获取用户所有草稿
   */
  async getUserDrafts(userId: number): Promise<MessageDraft[]> {
    try {
      // 先从本地获取
      const localDrafts = await this.getAllFromLocalStorage(userId);
      
      // 从服务器获取最新数据
      const response = await fetch(`${this.baseUrl}/user/${userId}`);
      if (response.ok) {
        const serverDrafts: MessageDraft[] = await response.json();
        
        // 合并本地和服务器数据（以服务器为准解决冲突）
        const mergedDrafts = this.mergeDrafts(localDrafts, serverDrafts);
        
        // 更新本地缓存
        await this.updateLocalStorage(userId, mergedDrafts);
        
        return mergedDrafts;
      }
      
      return localDrafts;
    } catch (error) {
      console.error('获取用户草稿失败:', error);
      return [];
    }
  }

  /**
   * 删除草稿
   */
  async deleteDraft(userId: number, conversationId: string): Promise<boolean> {
    try {
      // 从本地删除
      await this.removeFromLocalStorage(userId, conversationId);
      
      // 从服务器删除
      const response = await fetch(`${this.baseUrl}/${conversationId}?userId=${userId}`, {
        method: 'DELETE',
      });
      
      return response.ok;
    } catch (error) {
      console.error('删除草稿失败:', error);
      return false;
    }
  }

  /**
   * 同步草稿到服务器
   */
  async syncDraftToServer(draft: MessageDraft): Promise<SyncResponse> {
    try {
      const response = await fetch(`${this.baseUrl}/sync?userId=${draft.userId}&deviceId=${draft.deviceId}&conversationId=${draft.conversationId}&localVersion=${draft.localVersion}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `draftContent=${encodeURIComponent(draft.draftContent || '')}`,
      });

      if (response.status === 409) {
        // 冲突
        const conflictedDraft: MessageDraft = await response.json();
        return {
          success: false,
          conflict: true,
          draft: conflictedDraft,
        };
      }

      if (response.ok) {
        const syncedDraft: MessageDraft = await response.json();
        // 更新本地缓存
        await this.saveToLocalStorage(syncedDraft);
        
        // 从同步队列中移除
        this.removeFromSyncQueue(draft);
        
        return {
          success: true,
          draft: syncedDraft,
        };
      }
      
      return {
        success: false,
        error: '同步失败',
      };
    } catch (error) {
      console.error('同步草稿失败:', error);
      return {
        success: false,
        error: '网络错误',
      };
    }
  }

  /**
   * 批量同步
   */
  async batchSyncDrafts(userId: number, drafts: BatchSyncItem[]): Promise<SyncResponse[]> {
    try {
      const response = await fetch(`${this.baseUrl}/batch-sync?userId=${userId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(drafts),
      });

      if (response.ok) {
        const results: MessageDraft[] = await response.json();
        // 更新本地缓存
        for (const draft of results) {
          await this.saveToLocalStorage(draft);
        }
        
        // 从同步队列中移除已同步的
        this.removeFromSyncQueueByConversations(userId, drafts.map(d => d.conversationId));
        
        return results.map(draft => ({
          success: true,
          draft,
        }));
      }
      
      return drafts.map(() => ({
        success: false,
        error: '批量同步失败',
      }));
    } catch (error) {
      console.error('批量同步失败:', error);
      return drafts.map(() => ({
        success: false,
        error: '网络错误',
      }));
    }
  }

  /**
   * 解决冲突
   */
  async resolveConflict(draftId: number, resolvedContent: string, newVersion: number): Promise<SyncResponse> {
    try {
      const response = await fetch(`${this.baseUrl}/resolve-conflict/${draftId}?resolvedContent=${encodeURIComponent(resolvedContent)}&newVersion=${newVersion}`, {
        method: 'POST',
      });

      if (response.ok) {
        const resolvedDraft: MessageDraft = await response.json();
        // 更新本地缓存
        await this.saveToLocalStorage(resolvedDraft);
        
        return {
          success: true,
          draft: resolvedDraft,
        };
      }
      
      return {
        success: false,
        error: '解决冲突失败',
      };
    } catch (error) {
      console.error('解决冲突失败:', error);
      return {
        success: false,
        error: '网络错误',
      };
    }
  }

  /**
   * 获取草稿统计
   */
  async getDraftStatistics(userId: number): Promise<DraftStatistics | null> {
    try {
      const response = await fetch(`${this.baseUrl}/statistics?userId=${userId}`);
      if (response.ok) {
        return await response.json();
      }
      return null;
    } catch (error) {
      console.error('获取统计失败:', error);
      return null;
    }
  }

  /**
   * 开始自动同步
   */
  startAutoSync(intervalMs = 30000): void {
    if (this.syncInterval) {
      clearInterval(this.syncInterval);
    }
    
    this.syncInterval = window.setInterval(() => {
      this.processSyncQueue();
    }, intervalMs);
    
    console.log('草稿自动同步已启动，间隔:', intervalMs, 'ms');
  }

  /**
   * 停止自动同步
   */
  stopAutoSync(): void {
    if (this.syncInterval) {
      clearInterval(this.syncInterval);
      this.syncInterval = null;
    }
    
    console.log('草稿自动同步已停止');
  }

  /**
   * 处理同步队列
   */
  private async processSyncQueue(): Promise<void> {
    if (this.isSyncing || this.pendingSyncQueue.length === 0) {
      return;
    }
    
    this.isSyncing = true;
    
    try {
      // 按用户分组
      const groupedByUser = this.groupDraftsByUser(this.pendingSyncQueue);
      
      for (const [userId, drafts] of Object.entries(groupedByUser)) {
        // 转换为批量同步格式
        const batchItems: BatchSyncItem[] = drafts.map(draft => ({
          deviceId: draft.deviceId,
          conversationId: draft.conversationId,
          draftContent: draft.draftContent,
          localVersion: draft.localVersion,
        }));
        
        // 执行批量同步
        const results = await this.batchSyncDrafts(Number(userId), batchItems);
        
        // 处理失败的结果
        const failedItems = batchItems.filter((_, index) => !results[index].success);
        if (failedItems.length > 0 && this.retryCount < this.maxRetries) {
          this.retryCount++;
          console.warn(`批量同步部分失败，重试次数: ${this.retryCount}`, failedItems);
        } else {
          this.retryCount = 0;
        }
      }
      
      // 清空已处理的队列
      this.pendingSyncQueue = this.pendingSyncQueue.filter(draft => {
        const userDrafts = groupedByUser[draft.userId.toString()] || [];
        return !userDrafts.some(d => 
          d.deviceId === draft.deviceId && d.conversationId === draft.conversationId
        );
      });
      
    } catch (error) {
      console.error('处理同步队列失败:', error);
    } finally {
      this.isSyncing = false;
    }
  }

  /**
   * 添加到同步队列
   */
  private addToSyncQueue(draft: MessageDraft): void {
    // 检查是否已经在队列中
    const existingIndex = this.pendingSyncQueue.findIndex(d => 
      d.userId === draft.userId && 
      d.deviceId === draft.deviceId && 
      d.conversationId === draft.conversationId
    );
    
    if (existingIndex >= 0) {
      // 更新现有项
      this.pendingSyncQueue[existingIndex] = draft;
    } else {
      // 添加新项
      this.pendingSyncQueue.push(draft);
    }
    
    console.log('草稿已添加到同步队列，当前队列大小:', this.pendingSyncQueue.length);
  }

  /**
   * 从同步队列中移除
   */
  private removeFromSyncQueue(draft: MessageDraft): void {
    this.pendingSyncQueue = this.pendingSyncQueue.filter(d => 
      !(d.userId === draft.userId && 
        d.deviceId === draft.deviceId && 
        d.conversationId === draft.conversationId)
    );
  }

  /**
   * 从同步队列中移除多个
   */
  private removeFromSyncQueueByConversations(userId: number, conversationIds: string[]): void {
    this.pendingSyncQueue = this.pendingSyncQueue.filter(d => 
      !(d.userId === userId && conversationIds.includes(d.conversationId))
    );
  }

  /**
   * 按用户分组
   */
  private groupDraftsByUser(drafts: MessageDraft[]): Record<string, MessageDraft[]> {
    return drafts.reduce((groups, draft) => {
      const key = draft.userId.toString();
      if (!groups[key]) {
        groups[key] = [];
      }
      groups[key].push(draft);
      return groups;
    }, {} as Record<string, MessageDraft[]>);
  }

  /**
   * 合并草稿数据（解决冲突）
   */
  private mergeDrafts(localDrafts: MessageDraft[], serverDrafts: MessageDraft[]): MessageDraft[] {
    const merged: MessageDraft[] = [];
    const serverMap = new Map(serverDrafts.map(d => [`${d.userId}-${d.conversationId}`, d]));
    
    // 以服务器数据为主
    for (const serverDraft of serverDrafts) {
      merged.push(serverDraft);
    }
    
    // 添加本地独有的草稿
    for (const localDraft of localDrafts) {
      const key = `${localDraft.userId}-${localDraft.conversationId}`;
      if (!serverMap.has(key)) {
        merged.push(localDraft);
      }
    }
    
    return merged;
  }

  /**
   * 保存到本地存储
   */
  private async saveToLocalStorage(draft: MessageDraft): Promise<void> {
    try {
      const key = `draft_${draft.userId}_${draft.conversationId}`;
      localStorage.setItem(key, JSON.stringify(draft));
      
      // 更新索引
      await this.updateDraftIndex(draft.userId, draft.conversationId);
    } catch (error) {
      console.error('保存到本地存储失败:', error);
    }
  }

  /**
   * 从本地存储获取
   */
  private async getFromLocalStorage(userId: number, conversationId: string): Promise<MessageDraft | null> {
    try {
      const key = `draft_${userId}_${conversationId}`;
      const data = localStorage.getItem(key);
      return data ? JSON.parse(data) : null;
    } catch (error) {
      console.error('从本地存储获取失败:', error);
      return null;
    }
  }

  /**
   * 获取用户所有本地草稿
   */
  private async getAllFromLocalStorage(userId: number): Promise<MessageDraft[]> {
    try {
      const drafts: MessageDraft[] = [];
      const prefix = `draft_${userId}_`;
      
      for (let i = 0; i < localStorage.length; i++) {
        const key = localStorage.key(i);
        if (key && key.startsWith(prefix)) {
          const data = localStorage.getItem(key);
          if (data) {
            drafts.push(JSON.parse(data));
          }
        }
      }
      
      return drafts;
    } catch (error) {
      console.error('获取所有本地草稿失败:', error);
      return [];
    }
  }

  /**
   * 从本地存储删除
   */
  private async removeFromLocalStorage(userId: number, conversationId: string): Promise<void> {
    try {
      const key = `draft_${userId}_${conversationId}`;
      localStorage.removeItem(key);
      
      // 更新索引
      await this.removeFromDraftIndex(userId, conversationId);
    } catch (error) {
      console.error('从本地存储删除失败:', error);
    }
  }

  /**
   * 更新本地存储
   */
  private async updateLocalStorage(userId: number, drafts: MessageDraft[]): Promise<void> {
    try {
      // 清空用户所有旧数据
      const prefix = `draft_${userId}_`;
      for (let i = 0; i < localStorage.length; i++) {
        const key = localStorage.key(i);
        if (key && key.startsWith(prefix)) {
          localStorage.removeItem(key);
        }
      }
      
      // 保存新数据
      for (const draft of drafts) {
        await this.saveToLocalStorage(draft);
      }
    } catch (error) {
      console.error('更新本地存储失败:', error);
    }
  }

  /**
   * 更新草稿索引
   */
  private async updateDraftIndex(userId: number, conversationId: string): Promise<void> {
    try {
      const indexKey = `draft_index_${userId}`;
      let index = JSON.parse(localStorage.getItem(indexKey) || '[]');
      
      if (!index.includes(conversationId)) {
        index.push(conversationId);
        localStorage.setItem(indexKey, JSON.stringify(index));
      }
    } catch (error) {
      console.error('更新草稿索引失败:', error);
    }
  }

  /**
   * 从索引中移除
   */
  private async removeFromDraftIndex(userId: number, conversationId: string): Promise<void> {
    try {
      const indexKey = `draft_index_${userId}`;
      let index = JSON.parse(localStorage.getItem(indexKey) || '[]');
      
      index = index.filter((id: string) => id !== conversationId);
      localStorage.setItem(indexKey, JSON.stringify(index));
    } catch (error) {
      console.error('从索引中移除失败:', error);
    }
  }

  /**
   * 生成版本号
   */
  private generateVersion(): number {
    return Date.now();
  }
}

// 导出单例
export const messageDraftService = new MessageDraftService();

// 默认导出
export default messageDraftService;