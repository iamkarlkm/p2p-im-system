import { apiClient } from '../utils/api-client';
import {
  ScheduledMessageRecall,
  CreateScheduledRecallRequest,
  UpdateScheduledTimeRequest,
  ScheduledRecallStats,
  ScheduledRecallCheckResult,
  ScheduledRecallExecuteResult,
  ScheduledRecallBatchResult,
  ScheduledRecallCleanupResult,
} from '../types/scheduled-message-recall';

const BASE_PATH = '/scheduled-recall';

export interface ApiResult<T> {
  success: boolean;
  message?: string;
  data?: T;
}

/**
 * 创建定时撤回任务
 */
export async function createScheduledRecall(
  request: CreateScheduledRecallRequest
): Promise<ApiResult<ScheduledMessageRecall>> {
  try {
    const response = await apiClient.post(`${BASE_PATH}/create`, request);
    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    }
    return { success: false, message: response.data.message };
  } catch (error: any) {
    return { success: false, message: error.message || '网络错误' };
  }
}

/**
 * 取消定时撤回任务
 */
export async function cancelScheduledRecall(
  id: number
): Promise<ApiResult<ScheduledMessageRecall>> {
  try {
    const response = await apiClient.post(`${BASE_PATH}/${id}/cancel`);
    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    }
    return { success: false, message: response.data.message };
  } catch (error: any) {
    return { success: false, message: error.message || '网络错误' };
  }
}

/**
 * 获取定时撤回详情
 */
export async function getRecallDetail(
  id: number
): Promise<ApiResult<ScheduledMessageRecall>> {
  try {
    const response = await apiClient.get(`${BASE_PATH}/${id}`);
    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    }
    return { success: false, message: response.data.message };
  } catch (error: any) {
    return { success: false, message: error.message || '网络错误' };
  }
}

/**
 * 根据消息ID获取定时撤回
 */
export async function getByMessageId(
  messageId: number
): Promise<ApiResult<ScheduledMessageRecall>> {
  try {
    const response = await apiClient.get(`${BASE_PATH}/message/${messageId}`);
    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    }
    return { success: false, message: response.data.message };
  } catch (error: any) {
    return { success: false, message: error.message || '网络错误' };
  }
}

/**
 * 获取用户的所有定时撤回
 */
export async function getUserRecalls(): Promise<ApiResult<ScheduledMessageRecall[]>> {
  try {
    const response = await apiClient.get(`${BASE_PATH}/list`);
    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    }
    return { success: false, message: response.data.message };
  } catch (error: any) {
    return { success: false, message: error.message || '网络错误' };
  }
}

/**
 * 获取用户待执行的定时撤回
 */
export async function getPendingRecalls(): Promise<ApiResult<ScheduledMessageRecall[]>> {
  try {
    const response = await apiClient.get(`${BASE_PATH}/pending`);
    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    }
    return { success: false, message: response.data.message };
  } catch (error: any) {
    return { success: false, message: error.message || '网络错误' };
  }
}

/**
 * 获取用户定时撤回统计
 */
export async function getRecallStats(): Promise<ApiResult<ScheduledRecallStats>> {
  try {
    const response = await apiClient.get(`${BASE_PATH}/stats`);
    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    }
    return { success: false, message: response.data.message };
  } catch (error: any) {
    return { success: false, message: error.message || '网络错误' };
  }
}

/**
 * 检查消息是否已设置定时撤回
 */
export async function checkMessageScheduled(
  messageId: number
): Promise<ApiResult<ScheduledRecallCheckResult>> {
  try {
    const response = await apiClient.get(`${BASE_PATH}/check/${messageId}`);
    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    }
    return { success: false, message: response.data.message };
  } catch (error: any) {
    return { success: false, message: error.message || '网络错误' };
  }
}

/**
 * 删除定时撤回任务
 */
export async function deleteScheduledRecall(id: number): Promise<ApiResult<void>> {
  try {
    const response = await apiClient.delete(`${BASE_PATH}/${id}`);
    if (response.data.code === 200) {
      return { success: true };
    }
    return { success: false, message: response.data.message };
  } catch (error: any) {
    return { success: false, message: error.message || '网络错误' };
  }
}

/**
 * 更新定时撤回时间
 */
export async function updateScheduledTime(
  id: number,
  newSeconds: number
): Promise<ApiResult<ScheduledMessageRecall>> {
  try {
    const response = await apiClient.post(`${BASE_PATH}/${id}/update-time`, {
      newSeconds,
    });
    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    }
    return { success: false, message: response.data.message };
  } catch (error: any) {
    return { success: false, message: error.message || '网络错误' };
  }
}

/**
 * 获取推荐的时间选项
 */
export async function getTimeOptions(): Promise<ApiResult<number[]>> {
  try {
    const response = await apiClient.get(`${BASE_PATH}/time-options`);
    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    }
    return { success: false, message: response.data.message };
  } catch (error: any) {
    return { success: false, message: error.message || '网络错误' };
  }
}

/**
 * 手动执行定时撤回（测试用）
 */
export async function executeRecall(
  id: number
): Promise<ApiResult<ScheduledRecallExecuteResult>> {
  try {
    const response = await apiClient.post(`${BASE_PATH}/${id}/execute`);
    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    }
    return { success: false, message: response.data.message };
  } catch (error: any) {
    return { success: false, message: error.message || '网络错误' };
  }
}

/**
 * 批量执行到期任务（定时任务用）
 */
export async function batchExecute(): Promise<ApiResult<ScheduledRecallBatchResult>> {
  try {
    const response = await apiClient.post(`${BASE_PATH}/batch-execute`);
    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    }
    return { success: false, message: response.data.message };
  } catch (error: any) {
    return { success: false, message: error.message || '网络错误' };
  }
}

/**
 * 清理过期记录（管理接口）
 */
export async function cleanupOldRecords(
  daysToKeep: number = 30
): Promise<ApiResult<ScheduledRecallCleanupResult>> {
  try {
    const response = await apiClient.post(`${BASE_PATH}/cleanup?daysToKeep=${daysToKeep}`);
    if (response.data.code === 200) {
      return { success: true, data: response.data.data };
    }
    return { success: false, message: response.data.message };
  } catch (error: any) {
    return { success: false, message: error.message || '网络错误' };
  }
}
