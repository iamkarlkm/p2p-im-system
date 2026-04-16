/**
 * 聊天API服务
 * 功能#10: 桌面端聊天界面
 */

const API_BASE = '/api/chat';

export const chatApi = {
  async getMessages(conversationId: string): Promise<any[]> {
    const response = await fetch(`${API_BASE}/messages?conversationId=${conversationId}`);
    return response.json();
  },

  async sendMessage(conversationId: string, content: string): Promise<void> {
    await fetch(`${API_BASE}/send`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ conversationId, content }),
    });
  },

  async deleteMessage(messageId: string): Promise<void> {
    await fetch(`${API_BASE}/delete`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ messageId }),
    });
  },
};
