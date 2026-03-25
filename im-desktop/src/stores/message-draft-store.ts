// Message Draft Store - Pinia store for draft state management
import { defineStore } from 'pinia';
import { MessageDraftService } from '../services/message-draft-service';
import { MessageDraft } from '../types/message-draft';

export const useMessageDraftStore = defineStore('messageDraft', {
  state: () => ({
    drafts: {} as Record<string, MessageDraft>,
    service: null as MessageDraftService | null,
    userId: 0,
    lastSyncTime: 0,
  }),

  getters: {
    getDraft: (state) => (conversationId: string): MessageDraft | undefined => {
      return state.drafts[conversationId];
    },
    allDrafts: (state): MessageDraft[] => {
      return Object.values(state.drafts);
    },
  },

  actions: {
    init(userId: number) {
      this.userId = userId;
      this.service = new MessageDraftService(userId);
    },

    async saveDraft(conversationId: string, content: string, options?: {
      mentionIds?: string;
      replyMessageId?: string;
      messageType?: string;
    }) {
      if (!this.service) throw new Error('Draft service not initialized');
      const draft = await this.service.saveDraft(conversationId, content, options);
      this.drafts[conversationId] = draft;
    },

    async deleteDraft(conversationId: string) {
      if (!this.service) throw new Error('Draft service not initialized');
      await this.service.deleteDraft(conversationId);
      delete this.drafts[conversationId];
    },

    async loadDraft(conversationId: string) {
      if (!this.service) throw new Error('Draft service not initialized');
      const draft = await this.service.getDraft(conversationId);
      if (draft) {
        this.drafts[conversationId] = draft;
      }
      return draft;
    },

    async loadAllDrafts() {
      if (!this.service) throw new Error('Draft service not initialized');
      const drafts = await this.service.getAllDrafts();
      this.drafts = {};
      for (const draft of drafts) {
        this.drafts[draft.conversationId] = draft;
      }
    },
  },
});
