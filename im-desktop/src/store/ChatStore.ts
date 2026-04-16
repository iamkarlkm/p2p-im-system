import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';
import { Message, ChatSession } from '../types/chat';

interface ChatState {
  // 会话状态
  sessions: Map<string, ChatSession>;
  currentSessionId: string | null;
  messages: Map<string, Message[]>;
  
  // UI状态
  isLoading: boolean;
  error: string | null;
  unreadCount: number;
  
  // 操作
  setCurrentSession: (sessionId: string) => void;
  addMessage: (sessionId: string, message: Message) => void;
  updateMessage: (sessionId: string, messageId: string, updates: Partial<Message>) => void;
  deleteMessage: (sessionId: string, messageId: string) => void;
  clearMessages: (sessionId: string) => void;
  addSession: (session: ChatSession) => void;
  removeSession: (sessionId: string) => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  incrementUnread: () => void;
  clearUnread: () => void;
}

export const useChatStore = create<ChatState>()(
  immer((set) => ({
    sessions: new Map(),
    currentSessionId: null,
    messages: new Map(),
    isLoading: false,
    error: null,
    unreadCount: 0,
    
    setCurrentSession: (sessionId) =>
      set((state) => {
        state.currentSessionId = sessionId;
      }),
    
    addMessage: (sessionId, message) =>
      set((state) => {
        if (!state.messages.has(sessionId)) {
          state.messages.set(sessionId, []);
        }
        state.messages.get(sessionId)!.push(message);
      }),
    
    updateMessage: (sessionId, messageId, updates) =>
      set((state) => {
        const msgs = state.messages.get(sessionId);
        if (msgs) {
          const idx = msgs.findIndex((m) => m.id === messageId);
          if (idx !== -1) {
            Object.assign(msgs[idx], updates);
          }
        }
      }),
    
    deleteMessage: (sessionId, messageId) =>
      set((state) => {
        const msgs = state.messages.get(sessionId);
        if (msgs) {
          const idx = msgs.findIndex((m) => m.id === messageId);
          if (idx !== -1) {
            msgs.splice(idx, 1);
          }
        }
      }),
    
    clearMessages: (sessionId) =>
      set((state) => {
        state.messages.set(sessionId, []);
      }),
    
    addSession: (session) =>
      set((state) => {
        state.sessions.set(session.id, session);
      }),
    
    removeSession: (sessionId) =>
      set((state) => {
        state.sessions.delete(sessionId);
        state.messages.delete(sessionId);
        if (state.currentSessionId === sessionId) {
          state.currentSessionId = null;
        }
      }),
    
    setLoading: (loading) =>
      set((state) => {
        state.isLoading = loading;
      }),
    
    setError: (error) =>
      set((state) => {
        state.error = error;
      }),
    
    incrementUnread: () =>
      set((state) => {
        state.unreadCount += 1;
      }),
    
    clearUnread: () =>
      set((state) => {
        state.unreadCount = 0;
      }),
  }))
);
