import { makeAutoObservable, runInAction } from 'mobx';
import {
  MessageQuoteReply,
  QuotedMessageInfo,
  CreateQuoteReplyRequest,
  QuoteType,
  QuoteStatus,
  QuoteReplyStats,
  QuoteTree,
  isActiveQuote,
  isNestedQuote,
  calculateQuoteStats,
  buildQuoteTree,
} from '../types/quote-reply';
import { quoteReplyApi } from '../api/quote-reply-api';

/**
 * 消息引用回复状态管理
 */
export class QuoteReplyStore {
  quoteReplies: MessageQuoteReply[] = [];
  conversationQuotes: MessageQuoteReply[] = [];
  currentQuoteReply: MessageQuoteReply | null = null;
  selectedQuoteForReply: MessageQuoteReply | null = null;
  quoteTree: QuoteTree | null = null;

  isLoading = false;
  error: string | null = null;

  currentQuoteLevel = 1;
  quoteChainStack: QuotedMessageInfo[] = [];

  isBatchQuoteMode = false;
  selectedMessageIds: number[] = [];

  currentUserId: number;

  constructor(currentUserId: number) {
    this.currentUserId = currentUserId;
    makeAutoObservable(this);
  }

  get activeQuotes(): MessageQuoteReply[] {
    return this.quoteReplies.filter(isActiveQuote);
  }

  get nestedQuotes(): MessageQuoteReply[] {
    return this.quoteReplies.filter(isNestedQuote);
  }

  get topLevelQuotes(): MessageQuoteReply[] {
    return this.quoteReplies.filter(q => q.quoteLevel === 1);
  }

  get totalQuotes(): number {
    return this.quoteReplies.length;
  }

  get hasSelectedQuote(): boolean {
    return this.selectedQuoteForReply !== null;
  }

  get hasQuoteChain(): boolean {
    return this.quoteChainStack.length > 0;
  }

  get canSubmitBatchQuote(): boolean {
    return this.isBatchQuoteMode && this.selectedMessageIds.length >= 2;
  }

  get stats(): QuoteReplyStats {
    return calculateQuoteStats(this.quoteReplies, this.currentUserId);
  }

  /**
   * 创建单条引用回复
   */
  async createQuoteReply(
    quotedMessageId: number,
    conversationId: number,
    replyContent: string,
    options?: {
      includeOriginal?: boolean;
      highlightKeywords?: string;
    }
  ): Promise<boolean> {
    this.isLoading = true;
    this.error = null;

    try {
      const request: CreateQuoteReplyRequest = {
        quotedMessageId,
        conversationId,
        replyContent,
        parentQuoteId: this.selectedQuoteForReply?.id,
        includeOriginal: options?.includeOriginal ?? true,
        highlightKeywords: options?.highlightKeywords,
      };

      const dto = await quoteReplyApi.createQuoteReply(request);
      if (dto) {
        runInAction(() => {
          this.quoteReplies.unshift(dto);
          this.clearSelectedQuote();
        });
        return true;
      }
      return false;
    } catch (e) {
      runInAction(() => {
        this.error = `创建引用回复失败: ${e}`;
      });
      return false;
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  /**
   * 创建批量引用回复
   */
  async createBatchQuoteReply(
    conversationId: number,
    replyContent: string,
    options?: { includeOriginal?: boolean }
  ): Promise<boolean> {
    if (this.selectedMessageIds.length < 2) {
      this.error = '批量引用至少需要选择2条消息';
      return false;
    }

    this.isLoading = true;
    this.error = null;

    try {
      const request: CreateQuoteReplyRequest = {
        quotedMessageId: this.selectedMessageIds[0],
        conversationId,
        replyContent,
        batchQuotedMessageIds: this.selectedMessageIds,
        includeOriginal: options?.includeOriginal ?? true,
      };

      const dto = await quoteReplyApi.createQuoteReply(request);
      if (dto) {
        runInAction(() => {
          this.quoteReplies.unshift(dto);
          this.exitBatchQuoteMode();
        });
        return true;
      }
      return false;
    } catch (e) {
      runInAction(() => {
        this.error = `创建批量引用回复失败: ${e}`;
      });
      return false;
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  /**
   * 加载会话的引用回复列表
   */
  async loadConversationQuotes(conversationId: number): Promise<void> {
    this.isLoading = true;
    this.error = null;

    try {
      const list = await quoteReplyApi.getQuoteRepliesByConversation(conversationId);
      runInAction(() => {
        this.conversationQuotes = list;
      });
    } catch (e) {
      runInAction(() => {
        this.error = `加载引用回复列表失败: ${e}`;
      });
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  /**
   * 加载引用树
   */
  async loadQuoteTree(rootQuoteId: number): Promise<void> {
    this.isLoading = true;
    this.error = null;

    try {
      const list = await quoteReplyApi.getQuoteTree(rootQuoteId);
      runInAction(() => {
        this.quoteReplies = list;
        this.quoteTree = buildQuoteTree(list);
      });
    } catch (e) {
      runInAction(() => {
        this.error = `加载引用树失败: ${e}`;
      });
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  /**
   * 加载消息的引用回复
   */
  async loadQuotesByMessage(messageId: number): Promise<void> {
    this.isLoading = true;
    this.error = null;

    try {
      const list = await quoteReplyApi.getQuotesByMessage(messageId);
      runInAction(() => {
        this.quoteReplies = list;
      });
    } catch (e) {
      runInAction(() => {
        this.error = `加载消息引用回复失败: ${e}`;
      });
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  /**
   * 更新引用回复
   */
  async updateQuoteReply(id: number, newContent: string): Promise<boolean> {
    this.isLoading = true;
    this.error = null;

    try {
      const dto = await quoteReplyApi.updateQuoteReply(id, newContent);
      if (dto) {
        runInAction(() => {
          const index = this.quoteReplies.findIndex(q => q.id === id);
          if (index >= 0) {
            this.quoteReplies[index] = dto;
          }
          if (this.currentQuoteReply?.id === id) {
            this.currentQuoteReply = dto;
          }
        });
        return true;
      }
      return false;
    } catch (e) {
      runInAction(() => {
        this.error = `更新引用回复失败: ${e}`;
      });
      return false;
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  /**
   * 删除引用回复
   */
  async deleteQuoteReply(id: number): Promise<boolean> {
    this.isLoading = true;
    this.error = null;

    try {
      const success = await quoteReplyApi.deleteQuoteReply(id);
      if (success) {
        runInAction(() => {
          this.quoteReplies = this.quoteReplies.filter(q => q.id !== id);
          this.conversationQuotes = this.conversationQuotes.filter(q => q.id !== id);
          if (this.currentQuoteReply?.id === id) {
            this.currentQuoteReply = null;
          }
        });
        return true;
      }
      return false;
    } catch (e) {
      runInAction(() => {
        this.error = `删除引用回复失败: ${e}`;
      });
      return false;
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  /**
   * 撤回引用回复
   */
  async recallQuoteReply(id: number): Promise<boolean> {
    this.isLoading = true;
    this.error = null;

    try {
      const dto = await quoteReplyApi.recallQuoteReply(id);
      if (dto) {
        runInAction(() => {
          const index = this.quoteReplies.findIndex(q => q.id === id);
          if (index >= 0) {
            this.quoteReplies[index] = dto;
          }
        });
        return true;
      }
      return false;
    } catch (e) {
      runInAction(() => {
        this.error = `撤回引用回复失败: ${e}`;
      });
      return false;
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  /**
   * 选择要引用的消息
   */
  selectQuoteForReply(quote: MessageQuoteReply): void {
    this.selectedQuoteForReply = quote;
    if (quote.quotedMessageInfo) {
      this.quoteChainStack.push(quote.quotedMessageInfo);
    }
    this.currentQuoteLevel = quote.quoteLevel + 1;
  }

  /**
   * 清除选中的引用
   */
  clearSelectedQuote(): void {
    this.selectedQuoteForReply = null;
    this.quoteChainStack = [];
    this.currentQuoteLevel = 1;
  }

  /**
   * 进入批量引用模式
   */
  enterBatchQuoteMode(): void {
    this.isBatchQuoteMode = true;
    this.selectedMessageIds = [];
  }

  /**
   * 退出批量引用模式
   */
  exitBatchQuoteMode(): void {
    this.isBatchQuoteMode = false;
    this.selectedMessageIds = [];
  }

  /**
   * 切换消息选中状态
   */
  toggleMessageSelection(messageId: number): void {
    const index = this.selectedMessageIds.indexOf(messageId);
    if (index >= 0) {
      this.selectedMessageIds.splice(index, 1);
    } else {
      this.selectedMessageIds.push(messageId);
    }
  }

  /**
   * 检查消息是否已选中
   */
  isMessageSelected(messageId: number): boolean {
    return this.selectedMessageIds.includes(messageId);
  }

  /**
   * 获取引用统计
   */
  async getQuoteCount(messageId: number): Promise<number> {
    try {
      return await quoteReplyApi.countQuotesByMessage(messageId);
    } catch (e) {
      return 0;
    }
  }

  /**
   * 检查是否可以引用消息
   */
  async canQuoteMessage(messageId: number): Promise<boolean> {
    try {
      return await quoteReplyApi.canQuoteMessage(messageId);
    } catch (e) {
      return false;
    }
  }

  /**
   * 清除错误
   */
  clearError(): void {
    this.error = null;
  }

  /**
   * 重置状态
   */
  reset(): void {
    this.quoteReplies = [];
    this.conversationQuotes = [];
    this.currentQuoteReply = null;
    this.selectedQuoteForReply = null;
    this.quoteTree = null;
    this.quoteChainStack = [];
    this.selectedMessageIds = [];
    this.isBatchQuoteMode = false;
    this.currentQuoteLevel = 1;
    this.error = null;
  }
}

export const createQuoteReplyStore = (currentUserId: number) => new QuoteReplyStore(currentUserId);
