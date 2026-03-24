import axios, { AxiosResponse } from 'axios';
import { GroupManagementLog, GroupManagementLogDTO, GroupManagementLogFilter, GroupManagementLogStatistics } from '../types/groupManagementLog';
import { apiClient, handleApiError } from './apiClient';

/**
 * 群管理日志服务
 */
export class GroupManagementLogService {
  private baseUrl = '/api/v1/group-management-logs';

  /**
   * 记录群管理操作日志
   */
  async logOperation(logDTO: GroupManagementLogDTO): Promise<GroupManagementLog> {
    try {
      const response: AxiosResponse<GroupManagementLog> = await apiClient.post(
        this.baseUrl,
        logDTO
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error, '记录群管理操作日志失败');
    }
  }

  /**
   * 批量记录操作日志
   */
  async batchLogOperations(logDTOs: GroupManagementLogDTO[]): Promise<GroupManagementLog[]> {
    try {
      const response: AxiosResponse<GroupManagementLog[]> = await apiClient.post(
        `${this.baseUrl}/batch`,
        logDTOs
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error, '批量记录群管理操作日志失败');
    }
  }

  /**
   * 根据ID查询日志
   */
  async getLogById(id: string): Promise<GroupManagementLog> {
    try {
      const response: AxiosResponse<GroupManagementLog> = await apiClient.get(
        `${this.baseUrl}/${id}`
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error, '查询日志失败');
    }
  }

  /**
   * 根据群组ID查询日志
   */
  async getLogsByGroupId(groupId: string): Promise<GroupManagementLog[]> {
    try {
      const response: AxiosResponse<GroupManagementLog[]> = await apiClient.get(
        `${this.baseUrl}/group/${groupId}`
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error, '查询群组日志失败');
    }
  }

  /**
   * 根据群组ID分页查询日志
   */
  async getLogsByGroupIdPage(
    groupId: string,
    page: number = 0,
    size: number = 20,
    sort: string = 'createdAt,desc'
  ): Promise<{ content: GroupManagementLog[]; totalElements: number; totalPages: number }> {
    try {
      const params = {
        page,
        size,
        sort,
      };
      const response: AxiosResponse<{
        content: GroupManagementLog[];
        totalElements: number;
        totalPages: number;
      }> = await apiClient.get(`${this.baseUrl}/group/${groupId}/page`, { params });
      return response.data;
    } catch (error) {
      throw handleApiError(error, '分页查询群组日志失败');
    }
  }

  /**
   * 根据操作者ID查询日志
   */
  async getLogsByOperatorId(operatorId: string): Promise<GroupManagementLog[]> {
    try {
      const response: AxiosResponse<GroupManagementLog[]> = await apiClient.get(
        `${this.baseUrl}/operator/${operatorId}`
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error, '查询操作者日志失败');
    }
  }

  /**
   * 根据目标用户ID查询日志
   */
  async getLogsByTargetUserId(targetUserId: string): Promise<GroupManagementLog[]> {
    try {
      const response: AxiosResponse<GroupManagementLog[]> = await apiClient.get(
        `${this.baseUrl}/target/${targetUserId}`
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error, '查询目标用户日志失败');
    }
  }

  /**
   * 根据操作类型查询日志
   */
  async getLogsByActionType(actionType: string): Promise<GroupManagementLog[]> {
    try {
      const response: AxiosResponse<GroupManagementLog[]> = await apiClient.get(
        `${this.baseUrl}/action/${actionType}`
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error, '查询操作类型日志失败');
    }
  }

  /**
   * 根据时间范围查询日志
   */
  async getLogsByTimeRange(startDate: Date, endDate: Date): Promise<GroupManagementLog[]> {
    try {
      const params = {
        start: startDate.toISOString(),
        end: endDate.toISOString(),
      };
      const response: AxiosResponse<GroupManagementLog[]> = await apiClient.get(
        `${this.baseUrl}/time-range`,
        { params }
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error, '查询时间范围日志失败');
    }
  }

  /**
   * 高级搜索日志
   */
  async searchLogs(
    filter: GroupManagementLogFilter,
    page: number = 0,
    size: number = 20,
    sort: string = 'createdAt,desc'
  ): Promise<{ content: GroupManagementLog[]; totalElements: number; totalPages: number }> {
    try {
      const params = {
        ...filter,
        page,
        size,
        sort,
      };
      const response: AxiosResponse<{
        content: GroupManagementLog[];
        totalElements: number;
        totalPages: number;
      }> = await apiClient.get(`${this.baseUrl}/search`, { params });
      return response.data;
    } catch (error) {
      throw handleApiError(error, '搜索日志失败');
    }
  }

  /**
   * 获取群组最近的操作日志
   */
  async getRecentLogsByGroupId(groupId: string, limit: number = 10): Promise<GroupManagementLog[]> {
    try {
      const params = { limit };
      const response: AxiosResponse<GroupManagementLog[]> = await apiClient.get(
        `${this.baseUrl}/group/${groupId}/recent`,
        { params }
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error, '获取最近日志失败');
    }
  }

  /**
   * 获取需要通知的日志
   */
  async getPendingNotificationLogs(): Promise<GroupManagementLog[]> {
    try {
      const response: AxiosResponse<GroupManagementLog[]> = await apiClient.get(
        `${this.baseUrl}/pending-notifications`
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error, '获取待通知日志失败');
    }
  }

  /**
   * 获取重要操作日志
   */
  async getImportantLogs(): Promise<GroupManagementLog[]> {
    try {
      const response: AxiosResponse<GroupManagementLog[]> = await apiClient.get(
        `${this.baseUrl}/important`
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error, '获取重要日志失败');
    }
  }

  /**
   * 标记日志为已通知
   */
  async markLogsAsNotified(logIds: string[]): Promise<number> {
    try {
      const response: AxiosResponse<number> = await apiClient.put(
        `${this.baseUrl}/mark-notified`,
        logIds
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error, '标记日志为已通知失败');
    }
  }

  /**
   * 获取操作统计信息
   */
  async getStatistics(): Promise<GroupManagementLogStatistics> {
    try {
      const response: AxiosResponse<GroupManagementLogStatistics> = await apiClient.get(
        `${this.baseUrl}/statistics`
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error, '获取统计信息失败');
    }
  }

  /**
   * 获取群组操作统计信息
   */
  async getStatisticsByGroupId(groupId: string): Promise<GroupManagementLogStatistics> {
    try {
      const response: AxiosResponse<GroupManagementLogStatistics> = await apiClient.get(
        `${this.baseUrl}/statistics/group/${groupId}`
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error, '获取群组统计信息失败');
    }
  }

  /**
   * 获取热门操作类型
   */
  async getTopActionTypes(): Promise<Array<{ actionType: string; count: number }>> {
    try {
      const response: AxiosResponse<Array<{ actionType: string; count: number }>> = await apiClient.get(
        `${this.baseUrl}/statistics/top-actions`
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error, '获取热门操作类型失败');
    }
  }

  /**
   * 获取活跃操作者
   */
  async getTopOperators(): Promise<Array<{ operatorId: string; count: number }>> {
    try {
      const response: AxiosResponse<Array<{ operatorId: string; count: number }>> = await apiClient.get(
        `${this.baseUrl}/statistics/top-operators`
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error, '获取活跃操作者失败');
    }
  }

  /**
   * 导出日志为CSV格式
   */
  async exportToCsv(
    groupId?: string,
    startDate?: Date,
    endDate?: Date
  ): Promise<Blob> {
    try {
      const params: any = {};
      if (groupId) params.groupId = groupId;
      if (startDate) params.startDate = startDate.toISOString();
      if (endDate) params.endDate = endDate.toISOString();

      const response: AxiosResponse<ArrayBuffer> = await apiClient.get(
        `${this.baseUrl}/export/csv`,
        {
          params,
          responseType: 'arraybuffer',
        }
      );

      return new Blob([response.data], { type: 'text/csv' });
    } catch (error) {
      throw handleApiError(error, '导出CSV失败');
    }
  }

  /**
   * 导出日志为JSON格式
   */
  async exportToJson(
    groupId?: string,
    startDate?: Date,
    endDate?: Date
  ): Promise<Blob> {
    try {
      const params: any = {};
      if (groupId) params.groupId = groupId;
      if (startDate) params.startDate = startDate.toISOString();
      if (endDate) params.endDate = endDate.toISOString();

      const response: AxiosResponse<ArrayBuffer> = await apiClient.get(
        `${this.baseUrl}/export/json`,
        {
          params,
          responseType: 'arraybuffer',
        }
      );

      return new Blob([response.data], { type: 'application/json' });
    } catch (error) {
      throw handleApiError(error, '导出JSON失败');
    }
  }

  /**
   * 批量归档日志
   */
  async archiveLogs(logIds: string[]): Promise<number> {
    try {
      const response: AxiosResponse<number> = await apiClient.put(
        `${this.baseUrl}/archive`,
        logIds
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error, '归档日志失败');
    }
  }

  /**
   * 清理已归档的旧日志
   */
  async cleanupArchivedLogs(cutoffDate: Date): Promise<number> {
    try {
      const params = { cutoffDate: cutoffDate.toISOString() };
      const response: AxiosResponse<number> = await apiClient.delete(
        `${this.baseUrl}/cleanup`,
        { params }
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error, '清理归档日志失败');
    }
  }

  /**
   * 检查重复操作
   */
  async checkDuplicateOperation(
    groupId: string,
    operatorId: string,
    actionType: string,
    targetUserId?: string,
    withinMinutes: number = 5
  ): Promise<boolean> {
    try {
      const params: any = {
        groupId,
        operatorId,
        actionType,
        withinMinutes,
      };
      if (targetUserId) params.targetUserId = targetUserId;

      const response: AxiosResponse<boolean> = await apiClient.get(
        `${this.baseUrl}/check-duplicate`,
        { params }
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error, '检查重复操作失败');
    }
  }

  /**
   * 健康检查
   */
  async healthCheck(): Promise<{ status: string; service: string; timestamp: string }> {
    try {
      const response: AxiosResponse<{ status: string; service: string; timestamp: string }> = await apiClient.get(
        `${this.baseUrl}/health`
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error, '健康检查失败');
    }
  }

  /**
   * 下载文件
   */
  private downloadFile(blob: Blob, filename: string) {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  }

  /**
   * 导出并下载CSV文件
   */
  async exportAndDownloadCsv(
    groupId?: string,
    startDate?: Date,
    endDate?: Date,
    filename: string = 'group-management-logs.csv'
  ): Promise<void> {
    const blob = await this.exportToCsv(groupId, startDate, endDate);
    this.downloadFile(blob, filename);
  }

  /**
   * 导出并下载JSON文件
   */
  async exportAndDownloadJson(
    groupId?: string,
    startDate?: Date,
    endDate?: Date,
    filename: string = 'group-management-logs.json'
  ): Promise<void> {
    const blob = await this.exportToJson(groupId, startDate, endDate);
    this.downloadFile(blob, filename);
  }

  /**
   * 创建添加成员日志
   */
  createMemberAddLog(
    groupId: string,
    operatorId: string,
    targetUserId: string,
    operatorType: string = 'ADMIN',
    details: Record<string, any> = {}
  ): GroupManagementLogDTO {
    return {
      groupId,
      operatorId,
      operatorType,
      targetUserId,
      actionType: 'MEMBER_ADD',
      description: '添加新成员',
      details,
      result: 'SUCCESS',
      important: false,
      needNotification: true,
    };
  }

  /**
   * 创建移除成员日志
   */
  createMemberRemoveLog(
    groupId: string,
    operatorId: string,
    targetUserId: string,
    operatorType: string = 'ADMIN',
    reason?: string,
    details: Record<string, any> = {}
  ): GroupManagementLogDTO {
    return {
      groupId,
      operatorId,
      operatorType,
      targetUserId,
      actionType: 'MEMBER_REMOVE',
      description: `移除成员${reason ? ` (${reason})` : ''}`,
      details,
      result: 'SUCCESS',
      important: true,
      needNotification: true,
    };
  }

  /**
   * 创建角色变更日志
   */
  createRoleChangeLog(
    groupId: string,
    operatorId: string,
    targetUserId: string,
    operatorType: string = 'ADMIN',
    fromRole: string,
    toRole: string,
    details: Record<string, any> = {}
  ): GroupManagementLogDTO {
    return {
      groupId,
      operatorId,
      operatorType,
      targetUserId,
      actionType: 'ROLE_CHANGE',
      description: `角色变更: ${fromRole} → ${toRole}`,
      details,
      result: 'SUCCESS',
      important: true,
      needNotification: true,
    };
  }
}

// 导出单例实例
export const groupManagementLogService = new GroupManagementLogService();