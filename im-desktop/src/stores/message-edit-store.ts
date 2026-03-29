/**
 * 消息编辑状态管理 Store
 * 
 * @module stores/message-edit-store
 * @since 2026-03-27
 */

import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';
import type {
  MessageEdit,
  MessageEditHistory,
  EditMessageRequest,
  CanEditResult,
  EditState,
  EditType,
  EditCountsMap
} from '../types/message-edit';
import * as editApi from '../api/message-edit-api';

/**
 * 编辑 Store 状态
 */
interface MessageEditStoreState {
  // 状态
  editState: EditState;
  editHistories: Map<number, MessageEditHistory>;
  editCounts: EditCountsMap;
  canEditResults: Map<number, CanEditResult>;
  isLoadingHistory: boolean;
  isCheckingPermission: boolean;
  
  // Actions
  startEditing: (messageId: number, originalContent: string) => void;
  updateEditedContent: (content: string) => void;
  updateEditReason: (reason: string) => void;
  updateEditType: (editType: EditType) => void;
  cancelEditing: () => void;
  submitEdit: () => Promise<boolean>;
  
  // API Actions
  fetchEditHistory: (messageId: number) => Promise<MessageEditHistory | null>;
  checkCanEdit: (messageId: number) => Promise<CanEditResult | null>;
  fetchEditCounts: (messageIds: number[]) => Promise<void>;
  revertToVersion: (messageId: number, sequence: number) => Promise<boolean>;
  
  // Getters
  getEditHistory: (messageId: number) => MessageEditHistory | undefined;
  getEditCount: (messageId: number) => number;
  getCanEditResult: (messageId: number) => CanEditResult | undefined;
  
  // Reset
  reset: () => void;
}

/**
 * 默认编辑状态
 */
const defaultEditState: EditState = {
  editingMessageId: null,
  originalContent: '',
  editedContent: '',
  editReason: '',
  editType: 'NORMAL' as EditType,
  isSubmitting: false,
  error: null
};

/**
 * 使用 Zustand + Immer 创建 Store
 */
export const useMessageEditStore = create<MessageEditStoreState>()(
  immer((set, get) => ({
    // 初始状态
    editState: { ...defaultEditState },
    editHistories: new Map(),
    editCounts: {},
    canEditResults: new Map(),
    isLoadingHistory: false,
    isCheckingPermission: false,

    /**
     * 开始编辑
     */
    startEditing: (messageId: number, originalContent: string) => {
      set((state) => {
        state.editState.editingMessageId = messageId;
        state.editState.originalContent = originalContent;
        state.editState.editedContent = originalContent;
        state.editState.editReason = '';
        state.editState.editType = 'NORMAL' as EditType;
        state.editState.error = null;
      });
    },

    /**
     * 更新编辑内容
     */
    updateEditedContent: (content: string) => {
      set((state) => {
        state.editState.editedContent = content;
        state.editState.error = null;
      });
    },

    /**
     * 更新编辑原因
     */
    updateEditReason: (reason: string) => {
      set((state) => {
        state.editState.editReason = reason;
      });
    },

    /**
     * 更新编辑类型
     */
    updateEditType: (editType: EditType) => {
      set((state) => {
        state.editState.editType = editType;
      });
    },

    /**
     * 取消编辑
     */
    cancelEditing: () => {
      set((state) => {
        state.editState = { ...defaultEditState };
      });
    },

    /**
     * 提交编辑
     */
    submitEdit: async (): Promise<boolean> => {
      const { editState } = get();
      
      if (!editState.editingMessageId) {
        return false;
      }

      set((state) => {
        state.editState.isSubmitting = true;
        state.editState.error = null;
      });

      try {
        const request: EditMessageRequest = {
          messageId: editState.editingMessageId,
          originalContent: editState.originalContent,
          editedContent: editState.editedContent,
          editReason: editState.editReason || undefined,
          editType: editState.editType
        };

        const result = await editApi.editMessage(request);

        set((state) => {
          state.editState.isSubmitting = false;
          state.editState.editingMessageId = null;
          
          // 更新编辑次数
          if (state.editCounts[result.messageId] !== undefined) {
            state.editCounts[result.messageId] = result.editSequence;
          }
        });

        return true;
      } catch (error: any) {
        set((state) => {
          state.editState.isSubmitting = false;
          state.editState.error = error.response?.data?.message || error.message || '编辑失败';
        });
        return false;
      }
    },

    /**
     * 获取编辑历史
     */
    fetchEditHistory: async (messageId: number): Promise<MessageEditHistory | null> => {
      set((state) => {
        state.isLoadingHistory = true;
      });

      try {
        const history = await editApi.getEditHistory(messageId);
        
        set((state) => {
          state.editHistories.set(messageId, history);
          state.isLoadingHistory = false;
        });

        return history;
      } catch (error) {
        set((state) => {
          state.isLoadingHistory = false;
        });
        return null;
      }
    },

    /**
     * 检查是否可以编辑
     */
    checkCanEdit: async (messageId: number): Promise<CanEditResult | null> => {
      set((state) => {
        state.isCheckingPermission = true;
      });

      try {
        const result = await editApi.canEditMessage(messageId);
        
        set((state) => {
          state.canEditResults.set(messageId, result);
          state.isCheckingPermission = false;
        });

        return result;
      } catch (error) {
        set((state) => {
          state.isCheckingPermission = false;
        });
        return null;
      }
    },

    /**
     * 批量获取编辑次数
     */
    fetchEditCounts: async (messageIds: number[]): Promise<void> => {
      if (messageIds.length === 0) return;

      try {
        const counts = await editApi.preloadEditCounts(messageIds);
        
        set((state) => {
          state.editCounts = { ...state.editCounts, ...counts };
        });
      } catch (error) {
        console.error('Failed to fetch edit counts:', error);
      }
    },

    /**
     * 回滚到指定版本
     */
    revertToVersion: async (messageId: number, sequence: number): Promise<boolean> => {
      try {
        const result = await editApi.revertToVersion(messageId, sequence);
        
        set((state) => {
          // 更新编辑次数
          state.editCounts[messageId] = result.editSequence;
          // 清除历史缓存（需要重新获取）
          state.editHistories.delete(messageId);
        });

        return true;
      } catch (error: any) {
        console.error('Failed to revert:', error);
        return false;
      }
    },

    /**
     * 获取编辑历史
     */
    getEditHistory: (messageId: number): MessageEditHistory | undefined => {
      return get().editHistories.get(messageId);
    },

    /**
     * 获取编辑次数
     */
    getEditCount: (messageId: number): number => {
      return get().editCounts[messageId] || 0;
    },

    /**
     * 获取可编辑结果
     */
    getCanEditResult: (messageId: number): CanEditResult | undefined => {
      return get().canEditResults.get(messageId);
    },

    /**
     * 重置 Store
     */
    reset: () => {
      set((state) => {
        state.editState = { ...defaultEditState };
        state.editHistories.clear();
        state.editCounts = {};
        state.canEditResults.clear();
        state.isLoadingHistory = false;
        state.isCheckingPermission = false;
      });
    }
  }))
);

/**
 * 编辑 Store Hook（简化版）
 */
export function useMessageEdit() {
  const store = useMessageEditStore();
  
  return {
    // 当前编辑状态
    isEditing: store.editState.editingMessageId !== null,
    editingMessageId: store.editState.editingMessageId,
    editedContent: store.editState.editedContent,
    editReason: store.editState.editReason,
    editType: store.editState.editType,
    isSubmitting: store.editState.isSubmitting,
    error: store.editState.error,
    
    // Actions
    startEditing: store.startEditing,
    updateContent: store.updateEditedContent,
    updateReason: store.updateEditReason,
    updateType: store.updateEditType,
    submit: store.submitEdit,
    cancel: store.cancelEditing,
    
    // API
    fetchHistory: store.fetchEditHistory,
    checkPermission: store.checkCanEdit,
    revert: store.revertToVersion,
    
    // Getters
    getHistory: store.getEditHistory,
    getEditCount: store.getEditCount
  };
}
