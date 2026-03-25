import axios from 'axios';
import {
  DifferentialPrivacyConfig,
  PrivacyBudget,
  PrivacyImpact,
  PrivacyBudgetStats,
  PrivacyImpactStats,
  CreateDifferentialPrivacyConfigRequest,
  CreatePrivacyBudgetRequest,
  CreatePrivacyImpactRequest,
  ConsumePrivacyBudgetRequest,
  BudgetCheckResponse,
  ConfigValidationResponse,
} from '../types/differentialPrivacy';

const API_BASE = '/api/v1/differential-privacy';

/**
 * 差分隐私配置服务
 */
export const differentialPrivacyConfigService = {
  /**
   * 获取配置
   */
  async getConfig(configKey: string): Promise<DifferentialPrivacyConfig> {
    const response = await axios.get(`${API_BASE}/config/${configKey}`);
    return response.data;
  },

  /**
   * 获取所有活跃配置
   */
  async getAllActiveConfigs(): Promise<DifferentialPrivacyConfig[]> {
    const response = await axios.get(`${API_BASE}/config`);
    return response.data;
  },

  /**
   * 分页获取配置
   */
  async getConfigsPage(page: number, size: number) {
    const response = await axios.get(`${API_BASE}/config/page`, {
      params: { page, size },
    });
    return response.data;
  },

  /**
   * 创建配置
   */
  async createConfig(config: CreateDifferentialPrivacyConfigRequest): Promise<DifferentialPrivacyConfig> {
    const response = await axios.post(`${API_BASE}/config`, config);
    return response.data;
  },

  /**
   * 更新配置
   */
  async updateConfig(
    configKey: string,
    updates: Partial<DifferentialPrivacyConfig>
  ): Promise<DifferentialPrivacyConfig> {
    const response = await axios.put(`${API_BASE}/config/${configKey}`, updates);
    return response.data;
  },

  /**
   * 删除配置
   */
  async deleteConfig(configKey: string): Promise<void> {
    await axios.delete(`${API_BASE}/config/${configKey}`);
  },

  /**
   * 更新审批状态
   */
  async updateApprovalStatus(configKey: string, status: string): Promise<DifferentialPrivacyConfig> {
    const response = await axios.post(`${API_BASE}/config/${configKey}/approval`, null, {
      params: { status },
    });
    return response.data;
  },

  /**
   * 获取待审批配置
   */
  async getPendingApprovals(): Promise<DifferentialPrivacyConfig[]> {
    const response = await axios.get(`${API_BASE}/config/approval/pending`);
    return response.data;
  },

  /**
   * 获取敏感配置
   */
  async getSensitiveConfigs(): Promise<DifferentialPrivacyConfig[]> {
    const response = await axios.get(`${API_BASE}/config/sensitive`);
    return response.data;
  },

  /**
   * 搜索配置
   */
  async searchConfigs(keyword: string): Promise<DifferentialPrivacyConfig[]> {
    const response = await axios.get(`${API_BASE}/config/search`, {
      params: { keyword },
    });
    return response.data;
  },

  /**
   * 获取统计信息
   */
  async getStats(): Promise<{ activeCount: number; sensitiveCount: number }> {
    const response = await axios.get(`${API_BASE}/config/stats`);
    return response.data;
  },

  /**
   * 验证 Epsilon
   */
  async validateEpsilon(epsilon: number, configKey: string): Promise<ConfigValidationResponse> {
    const response = await axios.post(`${API_BASE}/config/validate/epsilon`, null, {
      params: { epsilon, configKey },
    });
    return response.data;
  },
};

/**
 * 隐私预算服务
 */
export const privacyBudgetService = {
  /**
   * 获取用户所有预算
   */
  async getUserBudgets(userId: string): Promise<PrivacyBudget[]> {
    const response = await axios.get(`${API_BASE}/budget/user/${userId}`);
    return response.data;
  },

  /**
   * 获取用户指定类型的预算
   */
  async getUserBudget(userId: string, budgetType: string): Promise<PrivacyBudget> {
    const response = await axios.get(`${API_BASE}/budget/user/${userId}/type/${budgetType}`);
    return response.data;
  },

  /**
   * 创建预算
   */
  async createBudget(budget: CreatePrivacyBudgetRequest): Promise<PrivacyBudget> {
    const response = await axios.post(`${API_BASE}/budget`, budget);
    return response.data;
  },

  /**
   * 消耗预算
   */
  async consumeBudget(request: ConsumePrivacyBudgetRequest): Promise<PrivacyBudget> {
    const response = await axios.post(`${API_BASE}/budget/consume`, null, {
      params: {
        userId: request.userId,
        budgetType: request.budgetType,
        epsilon: request.epsilon,
      },
    });
    return response.data;
  },

  /**
   * 重置预算
   */
  async resetBudget(userId: string, period: string): Promise<void> {
    await axios.post(`${API_BASE}/budget/reset/${userId}`, null, {
      params: { period },
    });
  },

  /**
   * 解除预算封锁
   */
  async unblockBudget(budgetId: number): Promise<void> {
    await axios.post(`${API_BASE}/budget/unblock/${budgetId}`);
  },

  /**
   * 获取低于阈值的预算
   */
  async getBudgetsBelowThreshold(threshold: number): Promise<PrivacyBudget[]> {
    const response = await axios.get(`${API_BASE}/budget/warning/threshold`, {
      params: { threshold },
    });
    return response.data;
  },

  /**
   * 获取已封锁的预算
   */
  async getBlockedBudgets(): Promise<PrivacyBudget[]> {
    const response = await axios.get(`${API_BASE}/budget/blocked`);
    return response.data;
  },

  /**
   * 获取已封锁预算数量
   */
  async getBlockedBudgetCount(): Promise<{ count: number }> {
    const response = await axios.get(`${API_BASE}/budget/blocked/count`);
    return response.data;
  },

  /**
   * 分页获取用户预算
   */
  async getUserBudgetsPage(userId: string, page: number, size: number) {
    const response = await axios.get(`${API_BASE}/budget/user/${userId}/page`, {
      params: { page, size },
    });
    return response.data;
  },

  /**
   * 获取有违规的预算
   */
  async getBudgetsWithViolations(page: number, size: number) {
    const response = await axios.get(`${API_BASE}/budget/violations`, {
      params: { page, size },
    });
    return response.data;
  },

  /**
   * 获取用户统计
   */
  async getUserStats(userId: string): Promise<PrivacyBudgetStats> {
    const response = await axios.get(`${API_BASE}/budget/stats/user/${userId}`);
    return response.data;
  },

  /**
   * 检查预算是否充足
   */
  async checkBudget(userId: string, budgetType: string, epsilon: number): Promise<BudgetCheckResponse> {
    const response = await axios.post(`${API_BASE}/budget/check`, null, {
      params: { userId, budgetType, epsilon },
    });
    return response.data;
  },
};

/**
 * 隐私影响评估服务
 */
export const privacyImpactService = {
  /**
   * 创建评估
   */
  async createAssessment(assessment: CreatePrivacyImpactRequest): Promise<PrivacyImpact> {
    const response = await axios.post(`${API_BASE}/impact`, assessment);
    return response.data;
  },

  /**
   * 完成评估
   */
  async completeAssessment(id: number): Promise<PrivacyImpact> {
    const response = await axios.post(`${API_BASE}/impact/${id}/complete`);
    return response.data;
  },

  /**
   * 按操作 ID 获取评估
   */
  async getByOperationId(operationId: string): Promise<PrivacyImpact[]> {
    const response = await axios.get(`${API_BASE}/impact/operation/${operationId}`);
    return response.data;
  },

  /**
   * 按用户 ID 获取评估
   */
  async getByUserId(userId: string): Promise<PrivacyImpact[]> {
    const response = await axios.get(`${API_BASE}/impact/user/${userId}`);
    return response.data;
  },

  /**
   * 分页获取用户评估
   */
  async getByUserIdPage(userId: string, page: number, size: number) {
    const response = await axios.get(`${API_BASE}/impact/user/${userId}/page`, {
      params: { page, size },
    });
    return response.data;
  },

  /**
   * 按操作类型获取评估
   */
  async getByOperationType(operationType: string, page: number, size: number) {
    const response = await axios.get(`${API_BASE}/impact/operation-type/${operationType}`, {
      params: { page, size },
    });
    return response.data;
  },

  /**
   * 按时间范围获取评估
   */
  async getByTimeRange(startTime: string, endTime: string): Promise<PrivacyImpact[]> {
    const response = await axios.get(`${API_BASE}/impact/time-range`, {
      params: { startTime, endTime },
    });
    return response.data;
  },

  /**
   * 按影响严重性获取评估
   */
  async getByImpactSeverity(severity: string, page: number, size: number) {
    const response = await axios.get(`${API_BASE}/impact/severity/${severity}`, {
      params: { page, size },
    });
    return response.data;
  },

  /**
   * 按风险等级获取评估
   */
  async getByRiskLevel(riskLevel: string, page: number, size: number) {
    const response = await axios.get(`${API_BASE}/impact/risk/${riskLevel}`, {
      params: { page, size },
    });
    return response.data;
  },

  /**
   * 获取失败的合规检查
   */
  async getFailedComplianceChecks(page: number, size: number) {
    const response = await axios.get(`${API_BASE}/impact/compliance/failed`, {
      params: { page, size },
    });
    return response.data;
  },

  /**
   * 获取高风险评估
   */
  async getHighRiskAssessments(threshold: number, page: number, size: number) {
    const response = await axios.get(`${API_BASE}/impact/risk/high`, {
      params: { threshold, page, size },
    });
    return response.data;
  },

  /**
   * 获取按操作类型统计
   */
  async getCountByOperationType(): Promise<Record<string, number>> {
    const response = await axios.get(`${API_BASE}/impact/stats/operation-type`);
    return response.data;
  },

  /**
   * 获取按严重性统计
   */
  async getCountByImpactSeverity(): Promise<Record<string, number>> {
    const response = await axios.get(`${API_BASE}/impact/stats/severity`);
    return response.data;
  },

  /**
   * 获取按风险等级统计
   */
  async getCountByRiskLevel(): Promise<Record<string, number>> {
    const response = await axios.get(`${API_BASE}/impact/stats/risk-level`);
    return response.data;
  },

  /**
   * 获取操作类型的平均 Epsilon
   */
  async getAverageEpsilonByOperationType(operationType: string): Promise<{ averageEpsilon: number }> {
    const response = await axios.get(`${API_BASE}/impact/stats/epsilon/operation-type/${operationType}`);
    return response.data;
  },

  /**
   * 获取用户总 Epsilon
   */
  async getTotalEpsilonByUser(userId: string): Promise<{ totalEpsilon: number }> {
    const response = await axios.get(`${API_BASE}/impact/stats/epsilon/user/${userId}`);
    return response.data;
  },

  /**
   * 获取平均影响分数
   */
  async getAverageImpactScore(startTime: string, endTime: string): Promise<{ averageImpactScore: number }> {
    const response = await axios.get(`${API_BASE}/impact/stats/impact-score`, {
      params: { startTime, endTime },
    });
    return response.data;
  },

  /**
   * 获取平均风险分数
   */
  async getAverageRiskScore(startTime: string, endTime: string): Promise<{ averageRiskScore: number }> {
    const response = await axios.get(`${API_BASE}/impact/stats/risk-score`, {
      params: { startTime, endTime },
    });
    return response.data;
  },

  /**
   * 获取总览统计
   */
  async getOverviewStats(): Promise<PrivacyImpactStats> {
    const response = await axios.get(`${API_BASE}/impact/stats/overview`);
    return response.data;
  },
};