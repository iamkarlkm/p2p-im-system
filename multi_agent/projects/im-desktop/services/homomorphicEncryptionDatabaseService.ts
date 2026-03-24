/**
 * 同态加密数据库 TypeScript API 服务
 * 提供加密数据库管理、隐私保护查询的 API 调用封装
 */

import {
  HomomorphicEncryptionDatabase,
  PrivacyPreservingQuery,
  CreateDatabaseRequest,
  CreateQueryRequest,
  DatabaseStatistics,
  QueryStatistics,
  ApiResponse,
  DatabaseListResponse,
  QueryListResponse
} from '../types/homomorphicEncryptionDatabase';

const API_BASE_URL = '/api/v1/homomorphic-encryption';

/**
 * 同态加密数据库服务
 */
export const homomorphicDatabaseService = {
  /**
   * 创建同态加密数据库
   */
  async createDatabase(request: CreateDatabaseRequest): Promise<ApiResponse<HomomorphicEncryptionDatabase>> {
    try {
      const response = await fetch(`${API_BASE_URL}/database`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(request)
      });
      return await response.json();
    } catch (error) {
      return { success: false, error: `创建数据库失败：${error}` };
    }
  },

  /**
   * 获取数据库信息
   */
  async getDatabase(databaseId: number): Promise<ApiResponse<HomomorphicEncryptionDatabase>> {
    try {
      const response = await fetch(`${API_BASE_URL}/database/${databaseId}`);
      return await response.json();
    } catch (error) {
      return { success: false, error: `获取数据库失败：${error}` };
    }
  },

  /**
   * 获取用户的所有数据库
   */
  async getUserDatabases(userId: number): Promise<DatabaseListResponse> {
    try {
      const response = await fetch(`${API_BASE_URL}/user/${userId}/databases`);
      return await response.json();
    } catch (error) {
      return { success: false, count: 0, data: [] };
    }
  },

  /**
   * 更新数据库状态
   */
  async updateDatabaseStatus(
    databaseId: number,
    status: string
  ): Promise<ApiResponse<HomomorphicEncryptionDatabase>> {
    try {
      const response = await fetch(`${API_BASE_URL}/database/${databaseId}/status`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status })
      });
      return await response.json();
    } catch (error) {
      return { success: false, error: `更新数据库状态失败：${error}` };
    }
  },

  /**
   * 删除数据库（软删除）
   */
  async deleteDatabase(databaseId: number): Promise<ApiResponse<void>> {
    try {
      const response = await fetch(`${API_BASE_URL}/database/${databaseId}/status`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status: 'DELETED' })
      });
      return await response.json();
    } catch (error) {
      return { success: false, error: `删除数据库失败：${error}` };
    }
  },

  /**
   * 优化数据库性能
   */
  async optimizeDatabase(databaseId: number): Promise<ApiResponse<void>> {
    try {
      const response = await fetch(`${API_BASE_URL}/database/${databaseId}/optimize`, {
        method: 'POST'
      });
      return await response.json();
    } catch (error) {
      return { success: false, error: `优化数据库失败：${error}` };
    }
  },

  /**
   * 重新生成密钥
   */
  async rekeyDatabase(databaseId: number): Promise<ApiResponse<void>> {
    try {
      const response = await fetch(`${API_BASE_URL}/database/${databaseId}/rekey`, {
        method: 'POST'
      });
      return await response.json();
    } catch (error) {
      return { success: false, error: `重新生成密钥失败：${error}` };
    }
  },

  /**
   * 备份数据库
   */
  async backupDatabase(databaseId: number): Promise<ApiResponse<void>> {
    try {
      const response = await fetch(`${API_BASE_URL}/database/${databaseId}/backup`, {
        method: 'POST'
      });
      return await response.json();
    } catch (error) {
      return { success: false, error: `备份数据库失败：${error}` };
    }
  },

  /**
   * 获取数据库统计信息
   */
  async getDatabaseStatistics(databaseId: number): Promise<ApiResponse<DatabaseStatistics>> {
    try {
      const response = await fetch(`${API_BASE_URL}/database/${databaseId}/statistics`);
      return await response.json();
    } catch (error) {
      return { success: false, error: `获取统计信息失败：${error}` };
    }
  }
};

/**
 * 隐私保护查询服务
 */
export const privacyQueryService = {
  /**
   * 创建隐私保护查询
   */
  async createQuery(request: CreateQueryRequest): Promise<ApiResponse<PrivacyPreservingQuery>> {
    try {
      const response = await fetch(`${API_BASE_URL}/query`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(request)
      });
      return await response.json();
    } catch (error) {
      return { success: false, error: `创建查询失败：${error}` };
    }
  },

  /**
   * 执行查询
   */
  async executeQuery(queryUuid: string): Promise<ApiResponse<PrivacyPreservingQuery>> {
    try {
      const response = await fetch(`${API_BASE_URL}/query/${queryUuid}/execute`, {
        method: 'POST'
      });
      return await response.json();
    } catch (error) {
      return { success: false, error: `执行查询失败：${error}` };
    }
  },

  /**
   * 获取查询信息
   */
  async getQuery(queryUuid: string): Promise<ApiResponse<PrivacyPreservingQuery>> {
    try {
      const response = await fetch(`${API_BASE_URL}/query/${queryUuid}`);
      return await response.json();
    } catch (error) {
      return { success: false, error: `获取查询失败：${error}` };
    }
  },

  /**
   * 获取用户的所有查询
   */
  async getUserQueries(userId: number): Promise<QueryListResponse> {
    try {
      const response = await fetch(`${API_BASE_URL}/user/${userId}/queries`);
      return await response.json();
    } catch (error) {
      return { success: false, count: 0, data: [] };
    }
  },

  /**
   * 取消查询
   */
  async cancelQuery(queryUuid: string): Promise<ApiResponse<PrivacyPreservingQuery>> {
    try {
      const response = await fetch(`${API_BASE_URL}/query/${queryUuid}/cancel`, {
        method: 'POST'
      });
      return await response.json();
    } catch (error) {
      return { success: false, error: `取消查询失败：${error}` };
    }
  },

  /**
   * 批量执行查询
   */
  async batchExecuteQueries(queryUuids: string[]): Promise<ApiResponse<PrivacyPreservingQuery[]>> {
    try {
      const response = await fetch(`${API_BASE_URL}/query/batch-execute`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(queryUuids)
      });
      return await response.json();
    } catch (error) {
      return { success: false, error: `批量执行查询失败：${error}` };
    }
  },

  /**
   * 获取查询统计信息
   */
  async getQueryStatistics(databaseId: number): Promise<ApiResponse<QueryStatistics>> {
    try {
      const response = await fetch(`${API_BASE_URL}/database/${databaseId}/query-statistics`);
      return await response.json();
    } catch (error) {
      return { success: false, error: `获取查询统计失败：${error}` };
    }
  },

  /**
   * 监控查询执行状态（轮询）
   */
  async monitorQueryExecution(
    queryUuid: string,
    onProgress: (query: PrivacyPreservingQuery) => void,
    intervalMs: number = 1000,
    timeoutMs: number = 300000
  ): Promise<PrivacyPreservingQuery | null> {
    const startTime = Date.now();
    
    while (Date.now() - startTime < timeoutMs) {
      const result = await this.getQuery(queryUuid);
      if (!result.success || !result.data) {
        return null;
      }
      
      const query = result.data as PrivacyPreservingQuery;
      onProgress(query);
      
      // 检查是否完成
      if (['SUCCESS', 'FAILED', 'TIMEOUT', 'CANCELLED'].includes(query.queryStatus)) {
        return query;
      }
      
      // 等待下次轮询
      await new Promise(resolve => setTimeout(resolve, intervalMs));
    }
    
    return null;
  },

  /**
   * 等待查询完成并返回结果
   */
  async waitForQueryCompletion(
    queryUuid: string,
    timeoutMs: number = 300000
  ): Promise<PrivacyPreservingQuery | null> {
    return new Promise(async (resolve) => {
      const result = await this.monitorQueryExecution(
        queryUuid,
        () => {}, // 空进度回调
        1000,
        timeoutMs
      );
      resolve(result);
    });
  },

  /**
   * 重试失败的查询
   */
  async retryQuery(queryUuid: string): Promise<ApiResponse<PrivacyPreservingQuery>> {
    // 先获取查询信息
    const getResult = await this.getQuery(queryUuid);
    if (!getResult.success || !getResult.data) {
      return { success: false, error: '无法获取查询信息' };
    }
    
    const query = getResult.data as PrivacyPreservingQuery;
    
    // 检查是否可以重试
    if (query.retryCount >= query.maxRetries) {
      return { success: false, error: '已达到最大重试次数' };
    }
    
    // 重新执行查询
    return await this.executeQuery(queryUuid);
  },

  /**
   * 验证查询结果
   */
  async verifyQueryResult(queryUuid: string): Promise<ApiResponse<PrivacyPreservingQuery>> {
    try {
      // 获取查询信息（验证结果应该已经包含在查询对象中）
      const response = await fetch(`${API_BASE_URL}/query/${queryUuid}`);
      const result = await response.json();
      
      if (result.success && result.data) {
        const query = result.data as PrivacyPreservingQuery;
        if (query.resultVerificationEnabled && query.verificationResult) {
          return { success: true, message: '查询结果验证通过', data: query };
        } else if (query.resultVerificationEnabled && !query.verificationResult) {
          return { success: false, error: '查询结果验证失败', data: query };
        } else {
          return { success: true, message: '查询结果未启用验证', data: query };
        }
      }
      
      return result;
    } catch (error) {
      return { success: false, error: `验证查询结果失败：${error}` };
    }
  }
};

/**
 * 工具函数：格式化数据大小
 */
export function formatDataSize(bytes: number): string {
  const units = ['B', 'KB', 'MB', 'GB', 'TB'];
  let size = bytes;
  let unitIndex = 0;
  
  while (size >= 1024 && unitIndex < units.length - 1) {
    size /= 1024;
    unitIndex++;
  }
  
  return `${size.toFixed(2)} ${units[unitIndex]}`;
}

/**
 * 工具函数：格式化时间
 */
export function formatDuration(ms: number): string {
  if (ms < 1000) {
    return `${ms.toFixed(0)}ms`;
  } else if (ms < 60000) {
    return `${(ms / 1000).toFixed(2)}s`;
  } else {
    const minutes = Math.floor(ms / 60000);
    const seconds = ((ms % 60000) / 1000).toFixed(1);
    return `${minutes}m ${seconds}s`;
  }
}

/**
 * 工具函数：获取状态颜色
 */
export function getStatusColor(status: string): string {
  const colors: Record<string, string> = {
    'SUCCESS': '#22c55e',
    'FAILED': '#ef4444',
    'PENDING': '#f59e0b',
    'EXECUTING': '#3b82f6',
    'CANCELLED': '#6b7280',
    'TIMEOUT': '#ef4444',
    'ACTIVE': '#22c55e',
    'INACTIVE': '#6b7280',
    'MAINTENANCE': '#f59e0b',
    'ARCHIVED': '#6b7280',
    'DELETED': '#ef4444'
  };
  return colors[status] || '#6b7280';
}

/**
 * 工具函数：获取隐私级别颜色
 */
export function getPrivacyLevelColor(level: string): string {
  const colors: Record<string, string> = {
    'PUBLIC': '#22c55e',
    'LOW': '#84cc16',
    'MEDIUM': '#f59e0b',
    'HIGH': '#f97316',
    'VERY_HIGH': '#ef4444',
    'CONFIDENTIAL': '#dc2626',
    'SECRET': '#991b1b'
  };
  return colors[level] || '#6b7280';
}

export default {
  homomorphicDatabase: homomorphicDatabaseService,
  privacyQuery: privacyQueryService,
  formatDataSize,
  formatDuration,
  getStatusColor,
  getPrivacyLevelColor
};