/**
 * 消息表情回应状态管理 Store
 * @module stores/message-reaction-store
 */

import { makeAutoObservable, runInAction } from 'mobx';
import {
  MessageReaction,
  ReactionSummary,
  ReactionType,
  AddReactionRequest,
  ToggleReactionRequest,
  ReactionEvent,
  MessageReactionState,
  COMMON_EMOJIS,
} from '../types/message-reaction';
import * as reactionApi from '../api/message-reaction-api';

/**
 * 消息表情回应 Store
 */
class MessageReactionStore {
  // 状态
  reactionStates = new Map<number, MessageReactionState>();
  currentUserId: number = 0;
  frequentlyUsedEmojis: string[] = [];
  loading = false;
  error: string | null = null;

  // 事件监听器
  private eventListeners: Set<(event: ReactionEvent) => void> = new Set();

  constructor() {
    makeAutoObservable(this);
    this.loadFrequentlyUsedEmojis();
  }

  /**
   * 设置当前用户ID
   */
  setCurrentUserId(userId: number): void {
    this.currentUserId = userId;
  }

  /**
   * 获取消息的表情状态
   */
  getReactionState(messageId: number): MessageReactionState | undefined {
    return this.reactionStates.get(messageId);
  }

  /**
   * 获取消息的表情汇总
   */
  getReactionSummary(messageId: number): ReactionSummary | undefined {
    return this.reactionStates.get(messageId)?.summary;
  }

  /**
   * 获取消息的用户回应
   */
  getUserReactions(messageId: number): MessageReaction[] {
    return this.reactionStates.get(messageId)?.userReactions || [];
  }

  /**
   * 获取消息是否加载中
   */
  isLoading(messageId: number): boolean {
    return this.reactionStates.get(messageId)?.loading || false;
  }

  /**
   * 加载消息的表情汇总
   */
  async loadReactionSummary(messageId: number, conversationId: number): Promise<void> {
    if (!this.currentUserId) {
      this.error = 'User not authenticated';
      return;
    }

    this.setLoading(messageId, true);

    try {
      const summary = await reactionApi.getReactionSummary(messageId, this.currentUserId);
      runInAction(() => {
        this.setReactionState(messageId, {
          messageId,
          summary,
          userReactions: [],
          loading: false,
        });
      });
    } catch (err) {
      runInAction(() => {
        this.setError(messageId, err instanceof Error ? err.message : 'Failed to load reactions');
      });
    }
  }

  /**
   * 批量加载消息的表情汇总
   */
  async loadReactionSummaries(messageIds: number[]): Promise<void> {
    if (!this.currentUserId || messageIds.length === 0) return;

    try {
      const summaries = await reactionApi.getReactionSummaries(messageIds, this.currentUserId);
      runInAction(() => {
        summaries.forEach(summary => {
          this.setReactionState(summary.messageId, {
            messageId: summary.messageId,
            summary,
            userReactions: [],
            loading: false,
          });
        });
      });
    } catch (err) {
      console.error('Failed to load reaction summaries:', err);
    }
  }

  /**
   * 添加表情回应
   */
  async addReaction(
    messageId: number,
    conversationId: number,
    emojiCode: string
  ): Promise<void> {
    if (!this.currentUserId) return;

    const request: AddReactionRequest = {
      messageId,
      userId: this.currentUserId,
      conversationId,
      emojiCode,
      reactionType: ReactionType.EMOJI,
    };

    try {
      const reaction = await reactionApi.addReaction(request);
      runInAction(() => {
        this.recordEmojiUsage(emojiCode);
        this.notifyEvent({
          type: 'ADDED',
          reaction,
          messageId,
          userId: this.currentUserId,
          emojiCode,
          timestamp: new Date().toISOString(),
        });
      });
      // 刷新汇总
      await this.loadReactionSummary(messageId, conversationId);
    } catch (err) {
      this.error = err instanceof Error ? err.message : 'Failed to add reaction';
      throw err;
    }
  }

  /**
   * 切换表情回应
   */
  async toggleReaction(
    messageId: number,
    conversationId: number,
    emojiCode: string
  ): Promise<void> {
    if (!this.currentUserId) return;

    const request: ToggleReactionRequest = {
      messageId,
      userId: this.currentUserId,
      conversationId,
      emojiCode,
      reactionType: ReactionType.EMOJI,
    };

    try {
      const reaction = await reactionApi.toggleReaction(request);
      runInAction(() => {
        if (reaction) {
          this.recordEmojiUsage(emojiCode);
          this.notifyEvent({
            type: 'ADDED',
            reaction,
            messageId,
            userId: this.currentUserId,
            emojiCode,
            timestamp: new Date().toISOString(),
          });
        } else {
          this.notifyEvent({
            type: 'REMOVED',
            messageId,
            userId: this.currentUserId,
            emojiCode,
            timestamp: new Date().toISOString(),
          });
        }
      });
      // 刷新汇总
      await this.loadReactionSummary(messageId, conversationId);
    } catch (err) {
      this.error = err instanceof Error ? err.message : 'Failed to toggle reaction';
      throw err;
    }
  }

  /**
   * 移除表情回应
   */
  async removeReaction(messageId: number, emojiCode: string): Promise<void> {
    if (!this.currentUserId) return;

    try {
      await reactionApi.removeReaction({
        messageId,
        userId: this.currentUserId,
        emojiCode,
      });
      runInAction(() => {
        this.notifyEvent({
          type: 'REMOVED',
          messageId,
          userId: this.currentUserId,
          emojiCode,
          timestamp: new Date().toISOString(),
        });
      });
    } catch (err) {
      this.error = err instanceof Error ? err.message : 'Failed to remove reaction';
      throw err;
    }
  }

  /**
   * 移除所有回应
   */
  async removeAllReactions(messageId: number): Promise<void> {
    if (!this.currentUserId) return;

    try {
      await reactionApi.removeAllReactionsByUser(messageId, this.currentUserId);
      runInAction(() => {
        this.notifyEvent({
          type: 'REMOVED',
          messageId,
          userId: this.currentUserId,
          timestamp: new Date().toISOString(),
        });
      });
    } catch (err) {
      this.error = err instanceof Error ? err.message : 'Failed to remove reactions';
      throw err;
    }
  }

  /**
   * 检查用户是否回应了消息
   */
  hasUserReacted(messageId: number): boolean {
    const state = this.reactionStates.get(messageId);
    return state?.summary?.hasCurrentUserReacted || false;
  }

  /**
   * 获取用户的表情回应
   */
  getCurrentUserReaction(messageId: number): string | undefined {
    const state = this.reactionStates.get(messageId);
    return state?.summary?.currentUserEmoji;
  }

  /**
   * 获取常用表情
   */
  getFrequentlyUsedEmojis(): string[] {
    if (this.frequentlyUsedEmojis.length > 0) {
      return this.frequentlyUsedEmojis;
    }
    return COMMON_EMOJIS.slice(0, 8);
  }

  /**
   * 记录表情使用
   */
  private recordEmojiUsage(emojiCode: string): void {
    // 添加到常用列表开头，去重
    this.frequentlyUsedEmojis = [
      emojiCode,
      ...this.frequentlyUsedEmojis.filter(e => e !== emojiCode),
    ].slice(0, 16);

    // 保存到本地存储
    this.saveFrequentlyUsedEmojis();
  }

  /**
   * 加载常用表情
   */
  private loadFrequentlyUsedEmojis(): void {
    try {
      const stored = localStorage.getItem('im_frequent_emojis');
      if (stored) {
        this.frequentlyUsedEmojis = JSON.parse(stored);
      }
    } catch {
      this.frequentlyUsedEmojis = [];
    }
  }

  /**
   * 保存常用表情
   */
  private saveFrequentlyUsedEmojis(): void {
    try {
      localStorage.setItem('im_frequent_emojis', JSON.stringify(this.frequentlyUsedEmojis));
    } catch {
      // Ignore storage errors
    }
  }

  /**
   * 设置加载状态
   */
  private setLoading(messageId: number, loading: boolean): void {
    const state = this.reactionStates.get(messageId);
    if (state) {
      state.loading = loading;
    } else {
      this.reactionStates.set(messageId, {
        messageId,
        userReactions: [],
        loading,
      });
    }
  }

  /**
   * 设置错误状态
   */
  private setError(messageId: number, error: string): void {
    const state = this.reactionStates.get(messageId);
    if (state) {
      state.error = error;
      state.loading = false;
    }
  }

  /**
   * 设置回应状态
   */
  private setReactionState(messageId: number, state: MessageReactionState): void {
    this.reactionStates.set(messageId, state);
  }

  /**
   * 添加事件监听器
   */
  addEventListener(listener: (event: ReactionEvent) => void): () => void {
    this.eventListeners.add(listener);
    return () => {
      this.eventListeners.delete(listener);
    };
  }

  /**
   * 通知事件
   */
  private notifyEvent(event: ReactionEvent): void {
    this.eventListeners.forEach(listener => {
      try {
        listener(event);
      } catch (err) {
        console.error('Reaction event listener error:', err);
      }
    });
  }

  /**
   * 清空消息状态
   */
  clearMessageState(messageId: number): void {
    this.reactionStates.delete(messageId);
  }

  /**
   * 清空所有状态
   */
  clearAllStates(): void {
    this.reactionStates.clear();
    this.error = null;
  }
}

export const messageReactionStore = new MessageReactionStore();
