// Reply Chain Service for im-desktop

import { ReplyChain, ReplyChainRequest, BranchTree, MessageContext } from '../types/reply-chain';

const API_BASE = '/api/reply-chain';

class ReplyChainService {
  
  private getHeaders(): Record<string, string> {
    const userId = localStorage.getItem('userId') || '1';
    const nickname = localStorage.getItem('nickname') || 'Unknown';
    return {
      'Content-Type': 'application/json',
      'X-User-Id': userId,
      'X-User-Nickname': nickname,
    };
  }

  async createReplyChain(request: ReplyChainRequest): Promise<ReplyChain> {
    const response = await fetch(`${API_BASE}/create`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify(request),
    });
    if (!response.ok) throw new Error('Failed to create reply chain');
    return response.json();
  }

  async getReplyChain(chainId: number): Promise<ReplyChain> {
    const response = await fetch(`${API_BASE}/${chainId}`, {
      method: 'GET',
      headers: this.getHeaders(),
    });
    if (!response.ok) throw new Error('Failed to get reply chain');
    return response.json();
  }

  async getConversationReplyChains(conversationId: number): Promise<ReplyChain[]> {
    const response = await fetch(`${API_BASE}/conversation/${conversationId}`, {
      method: 'GET',
      headers: this.getHeaders(),
    });
    if (!response.ok) throw new Error('Failed to get conversation reply chains');
    return response.json();
  }

  async getBranchTree(rootMessageId: number): Promise<ReplyChain> {
    const response = await fetch(`${API_BASE}/branch/${rootMessageId}`, {
      method: 'GET',
      headers: this.getHeaders(),
    });
    if (!response.ok) throw new Error('Failed to get branch tree');
    return response.json();
  }

  async getMessageContext(messageId: number): Promise<MessageContext> {
    const response = await fetch(`${API_BASE}/context/${messageId}`, {
      method: 'GET',
      headers: this.getHeaders(),
    });
    if (!response.ok) throw new Error('Failed to get message context');
    return response.json();
  }

  async markMessageDeleted(messageId: number): Promise<void> {
    const response = await fetch(`${API_BASE}/message/${messageId}/deleted`, {
      method: 'PUT',
      headers: this.getHeaders(),
    });
    if (!response.ok) throw new Error('Failed to mark message deleted');
  }

  async deleteChain(chainId: number): Promise<void> {
    const response = await fetch(`${API_BASE}/${chainId}`, {
      method: 'DELETE',
      headers: this.getHeaders(),
    });
    if (!response.ok) throw new Error('Failed to delete chain');
  }

  calculateMaxDepth(chain: ReplyChain): number {
    if (!chain.branchNodes || chain.branchNodes.length === 0) return chain.depth;
    return Math.max(chain.depth, ...chain.branchNodes.map(n => this.calculateMaxDepthFromNode(n)));
  }

  private calculateMaxDepthFromNode(node: ReplyChain): number {
    return node.depth || 0;
  }

  buildBranchPath(parentPath: string, messageId: number): string {
    return `${parentPath}/${messageId}`;
  }

  parseBranchPath(path: string): number[] {
    return path.split('/').filter(p => p && !isNaN(Number(p))).map(Number);
  }
}

export const replyChainService = new ReplyChainService();
