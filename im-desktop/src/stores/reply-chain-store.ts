// Reply Chain Store for im-desktop

import { makeAutoObservable, runInAction } from 'mobx';
import { ReplyChain, BranchTree, ReplyChainRequest } from '../types/reply-chain';
import { replyChainService } from '../services/reply-chain-service';

class ReplyChainStore {
  activeChain: ReplyChain | null = null;
  conversationChains: Map<number, ReplyChain[]> = new Map();
  branchView: BranchTree | null = null;
  expandedNodes: Set<number> = new Set();
  loading = false;
  error: string | null = null;
  replyDrafts: Map<number, number> = new Map();

  constructor() {
    makeAutoObservable(this);
  }

  async createChain(request: ReplyChainRequest): Promise<ReplyChain | null> {
    this.loading = true;
    this.error = null;
    try {
      const chain = await replyChainService.createReplyChain(request);
      runInAction(() => {
        this.activeChain = chain;
        this.loading = false;
      });
      return chain;
    } catch (e: any) {
      runInAction(() => {
        this.error = e.message;
        this.loading = false;
      });
      return null;
    }
  }

  async loadConversationChains(conversationId: number): Promise<void> {
    this.loading = true;
    try {
      const chains = await replyChainService.getConversationReplyChains(conversationId);
      runInAction(() => {
        this.conversationChains.set(conversationId, chains);
        this.loading = false;
      });
    } catch (e: any) {
      runInAction(() => {
        this.error = e.message;
        this.loading = false;
      });
    }
  }

  async loadBranchTree(rootMessageId: number): Promise<void> {
    this.loading = true;
    try {
      const tree = await replyChainService.getBranchTree(rootMessageId);
      runInAction(() => {
        this.branchView = { root: tree, branches: [], totalNodes: 1, maxDepth: tree.depth };
        this.loading = false;
      });
    } catch (e: any) {
      runInAction(() => {
        this.error = e.message;
        this.loading = false;
      });
    }
  }

  toggleNode(nodeId: number): void {
    if (this.expandedNodes.has(nodeId)) {
      this.expandedNodes.delete(nodeId);
    } else {
      this.expandedNodes.add(nodeId);
    }
  }

  setReplyDraft(messageId: number, parentMessageId: number): void {
    this.replyDrafts.set(messageId, parentMessageId);
  }

  clearReplyDraft(messageId: number): void {
    this.replyDrafts.delete(messageId);
  }

  clearError(): void {
    this.error = null;
  }
}

export const replyChainStore = new ReplyChainStore();
