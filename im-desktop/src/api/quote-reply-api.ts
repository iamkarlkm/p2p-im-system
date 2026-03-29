import {
  MessageQuoteReply,
  CreateQuoteReplyRequest,
  UpdateQuoteReplyRequest,
  QuoteReplyFilter,
} from '../types/quote-reply';
import { apiClient } from './api-client';

/**
 * 消息引用回复API
 */
class QuoteReplyApi {
  private client = apiClient;

  /**
   * 创建引用回复
   */
  async createQuoteReply(request: CreateQuoteReplyRequest): Promise<MessageQuoteReply | null> {
    try {
      const response = await this.client.post('/api/v1/quote-reply/create', request);
      if (response.data?.success) {
        return response.data.data as MessageQuoteReply;
      }
      return null;
    } catch (error) {
      console.error('Create quote reply error:', error);
      throw error;
    }
  }

  /**
   * 获取引用回复详情
   */
  async getQuoteReplyById(id: number): Promise<MessageQuoteReply | null> {
    try {
      const response = await this.client.get(`/api/v1/quote-reply/${id}`);
      if (response.data?.success) {
        return response.data.data as MessageQuoteReply;
      }
      return null;
    } catch (error) {
      console.error('Get quote reply error:', error);
      throw error;
    }
  }

  /**
   * 通过消息ID获取引用回复
   */
  async getQuoteReplyByMessageId(messageId: number): Promise<MessageQuoteReply | null> {
    try {
      const response = await this.client.get(`/api/v1/quote-reply/by-message/${messageId}`);
      if (response.data?.success) {
        return response.data.data as MessageQuoteReply;
      }
      return null;
    } catch (error) {
      console.error('Get quote reply by message error:', error);
      throw error;
    }
  }

  /**
   * 获取会话的引用回复列表
   */
  async getQuoteRepliesByConversation(conversationId: number): Promise<MessageQuoteReply[]> {
    try {
      const response = await this.client.get(`/api/v1/quote-reply/conversation/${conversationId}`);
      if (response.data?.success) {
        return response.data.data as MessageQuoteReply[];
      }
      return [];
    } catch (error) {
      console.error('Get conversation quotes error:', error);
      throw error;
    }
  }

  /**
   * 获取我的引用回复（分页）
   */
  async getMyQuoteReplies(
    conversationId: number,
    page: number = 0,
    size: number = 20
  ): Promise<{ content: MessageQuoteReply[]; total: number }> {
    try {
      const response = await this.client.get(`/api/v1/quote-reply/my/${conversationId}`, {
        params: { page, size }
      });
      if (response.data?.success) {
        const pageData = response.data.data;
        return {
          content: pageData.content as MessageQuoteReply[],
          total: pageData.totalElements as number
        };
      }
      return { content: [], total: 0 };
    } catch (error) {
      console.error('Get my quotes error:', error);
      throw error;
    }
  }

  /**
   * 更新引用回复
   */
  async updateQuoteReply(id: number, newContent: string): Promise<MessageQuoteReply | null> {
    try {
      const request: UpdateQuoteReplyRequest = { content: newContent };
      const response = await this.client.put(`/api/v1/quote-reply/${id}`, request);
      if (response.data?.success) {
        return response.data.data as MessageQuoteReply;
      }
      return null;
    } catch (error) {
      console.error('Update quote reply error:', error);
      throw error;
    }
  }

  /**
   * 删除引用回复
   */
  async deleteQuoteReply(id: number): Promise<boolean> {
    try {
      const response = await this.client.delete(`/api/v1/quote-reply/${id}`);
      return response.data?.success === true;
    } catch (error) {
      console.error('Delete quote reply error:', error);
      throw error;
    }
  }

  /**
   * 获取引用树
   */
  async getQuoteTree(rootQuoteId: number): Promise<MessageQuoteReply[]> {
    try {
      const response = await this.client.get(`/api/v1/quote-reply/tree/${rootQuoteId}`);
      if (response.data?.success) {
        return response.data.data as MessageQuoteReply[];
      }
      return [];
    } catch (error) {
      console.error('Get quote tree error:', error);
      throw error;
    }
  }

  /**
   * 获取嵌套引用回复
   */
  async getNestedQuotes(parentQuoteId: number): Promise<MessageQuoteReply[]> {
    try {
      const response = await this.client.get(`/api/v1/quote-reply/nested/${parentQuoteId}`);
      if (response.data?.success) {
        return response.data.data as MessageQuoteReply[];
      }
      return [];
    } catch (error) {
      console.error('Get nested quotes error:', error);
      throw error;
    }
  }

  /**
   * 统计消息的引用数量
   */
  async countQuotesByMessage(messageId: number): Promise<number> {
    try {
      const response = await this.client.get(`/api/v1/quote-reply/count/${messageId}`);
      if (response.data?.success) {
        return response.data.data.count as number;
      }
      return 0;
    } catch (error) {
      console.error('Count quotes error:', error);
      return 0;
    }
  }

  /**
   * 获取消息的引用回复列表
   */
  async getQuotesByMessage(messageId: number): Promise<MessageQuoteReply[]> {
    try {
      const response = await this.client.get(`/api/v1/quote-reply/by-quoted-message/${messageId}`);
      if (response.data?.success) {
        return response.data.data as MessageQuoteReply[];
      }
      return [];
    } catch (error) {
      console.error('Get quotes by message error:', error);
      throw error;
    }
  }

  /**
   * 获取引用链中包含某消息的回复
   */
  async getQuotesContainingInChain(
    conversationId: number,
    messageId: number
  ): Promise<MessageQuoteReply[]> {
    try {
      const response = await this.client.get(`/api/v1/quote-reply/chain/${conversationId}/${messageId}`);
      if (response.data?.success) {
        return response.data.data as MessageQuoteReply[];
      }
      return [];
    } catch (error) {
      console.error('Get quotes in chain error:', error);
      throw error;
    }
  }

  /**
   * 撤回引用回复
   */
  async recallQuoteReply(id: number): Promise<MessageQuoteReply | null> {
    try {
      const response = await this.client.post(`/api/v1/quote-reply/${id}/recall`);
      if (response.data?.success) {
        return response.data.data as MessageQuoteReply;
      }
      return null;
    } catch (error) {
      console.error('Recall quote reply error:', error);
      throw error;
    }
  }

  /**
   * 检查是否可以引用消息
   */
  async canQuoteMessage(messageId: number): Promise<boolean> {
    try {
      const response = await this.client.get(`/api/v1/quote-reply/can-quote/${messageId}`);
      if (response.data?.success) {
        return response.data.data.canQuote as boolean;
      }
      return false;
    } catch (error) {
      console.error('Check can quote error:', error);
      return false;
    }
  }
}

export const quoteReplyApi = new QuoteReplyApi();
