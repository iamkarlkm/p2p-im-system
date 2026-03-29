/**
 * 消息编辑 API 接口
 * 
 * @module api/message-edit-api
 * @since 2026-03-27
 */

import axios from 'axios';
import type {
  MessageEdit,
  MessageEditHistory,
  EditMessageRequest,
  CanEditResult,
  EditCountsMap,
  EditHistoryItem
} from '../types/message-edit';
import type { Page, Pageable } from '../types/common';

const API_BASE_URL = '/api/v1/messages/edit';

/**
 * 编辑消息
 * 
 * @param request 编辑请求
 * @returns 编辑后的消息
 */
export async function editMessage(request: EditMessageRequest): Promise<MessageEdit> {
  const response = await axios.post(`${API_BASE_URL}`, request);
  return response.data.data;
}

/**
 * 获取消息的编辑历史
 * 
 * @param messageId 消息ID
 * @returns 编辑历史
 */
export async function getEditHistory(messageId: number): Promise<MessageEditHistory> {
  const response = await axios.get(`${API_BASE_URL}/history/${messageId}`);
  return response.data.data;
}

/**
 * 分页获取编辑历史
 * 
 * @param messageId 消息ID
 * @param pageable 分页参数
 * @returns 分页编辑历史
 */
export async function getEditHistoryPage(
  messageId: number,
  pageable: Pageable
): Promise<Page<EditHistoryItem>> {
  const { page = 0, size = 10 } = pageable;
  const response = await axios.get(`${API_BASE_URL}/history/${messageId}/page`, {
    params: { page, size }
  });
  return response.data.data;
}

/**
 * 检查是否可以编辑消息
 * 
 * @param messageId 消息ID
 * @returns 检查结果
 */
export async function canEditMessage(messageId: number): Promise<CanEditResult> {
  const response = await axios.get(`${API_BASE_URL}/can-edit/${messageId}`);
  return response.data.data;
}

/**
 * 获取消息的编辑次数
 * 
 * @param messageId 消息ID
 * @returns 编辑次数
 */
export async function getEditCount(messageId: number): Promise<number> {
  const response = await axios.get(`${API_BASE_URL}/count/${messageId}`);
  return response.data.data;
}

/**
 * 批量获取编辑次数
 * 
 * @param messageIds 消息ID列表
 * @returns 编辑次数映射
 */
export async function getEditCounts(messageIds: number[]): Promise<EditCountsMap> {
  const response = await axios.post(`${API_BASE_URL}/counts`, messageIds);
  return response.data.data;
}

/**
 * 回滚到指定版本
 * 
 * @param messageId 消息ID
 * @param sequence 编辑序号
 * @returns 回滚后的编辑记录
 */
export async function revertToVersion(
  messageId: number,
  sequence: number
): Promise<MessageEdit> {
  const response = await axios.post(`${API_BASE_URL}/revert/${messageId}`, null, {
    params: { sequence }
  });
  return response.data.data;
}

/**
 * 获取当前用户的编辑历史
 * 
 * @param pageable 分页参数
 * @returns 编辑记录分页
 */
export async function getMyEditHistory(
  pageable: Pageable
): Promise<Page<MessageEdit>> {
  const { page = 0, size = 20 } = pageable;
  const response = await axios.get(`${API_BASE_URL}/my-edits`, {
    params: { page, size }
  });
  return response.data.data;
}

/**
 * 快速编辑（简化版，自动获取原内容）
 * 
 * @param messageId 消息ID
 * @param newContent 新内容
 * @returns 编辑后的消息
 */
export async function quickEdit(
  messageId: number,
  newContent: string
): Promise<MessageEdit> {
  // 先获取当前历史
  const history = await getEditHistory(messageId);
  const currentContent = history.currentContent;

  const request: EditMessageRequest = {
    messageId,
    originalContent: currentContent,
    editedContent: newContent,
    editType: 'NORMAL'
  };

  return editMessage(request);
}

/**
 * 检查并编辑（先检查权限再编辑）
 * 
 * @param request 编辑请求
 * @returns 编辑结果或失败原因
 */
export async function checkAndEdit(
  request: EditMessageRequest
): Promise<{ success: boolean; data?: MessageEdit; error?: string }> {
  try {
    // 先检查是否可以编辑
    const canEdit = await canEditMessage(request.messageId);
    
    if (!canEdit.canEdit) {
      return {
        success: false,
        error: canEdit.reason
      };
    }

    // 执行编辑
    const result = await editMessage(request);
    return {
      success: true,
      data: result
    };
  } catch (error: any) {
    return {
      success: false,
      error: error.response?.data?.message || error.message || '编辑失败'
    };
  }
}

/**
 * 批量预加载编辑次数
 * 用于消息列表显示编辑标记
 * 
 * @param messageIds 消息ID列表
 * @returns 编辑次数映射
 */
export async function preloadEditCounts(
  messageIds: number[]
): Promise<EditCountsMap> {
  if (messageIds.length === 0) {
    return {};
  }
  
  // 去重
  const uniqueIds = [...new Set(messageIds)];
  
  // 最多批量查询100个
  const batchSize = 100;
  const results: EditCountsMap = {};
  
  for (let i = 0; i < uniqueIds.length; i += batchSize) {
    const batch = uniqueIds.slice(i, i + batchSize);
    const batchResults = await getEditCounts(batch);
    Object.assign(results, batchResults);
  }
  
  return results;
}
