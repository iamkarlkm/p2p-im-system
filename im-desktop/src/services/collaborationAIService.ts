/**
 * 协作增强 AI 助手 API 服务
 * 提供与后端协作 AI 功能的完整交互接口
 */

import axios from 'axios';
import {
  CollaborationAIConfig,
  CreateCollaborationAIRequest,
  UpdateCollaborationAIRequest,
  AnalyzeMeetingRequest,
  TrackProgressRequest,
  IdentifyTasksRequest,
  AnalyzePatternsRequest,
  ProvideSuggestionsRequest,
  BuildKnowledgeRequest,
  IdentifyBottlenecksRequest,
  OptimizeRolesRequest,
  AssessMeetingRequest,
  AddInsightRequest,
  AddRecommendationRequest,
  UpdateMetricRequest,
  BatchOperationRequest,
  BatchOperationResponse,
  CollaborationTypeStatistics,
  AnalysisFrequencyStatistics,
  HealthStatus,
  CollaborationAIPageResponse
} from '../types/collaborationAI';

const API_BASE_URL = '/api/v1/collaboration-ai';

/**
 * 创建 HTTP 客户端实例
 */
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

/**
 * 协作增强 AI 助手服务类
 */
class CollaborationAIService {
  
  /**
   * 创建新的协作 AI 配置
   */
  async createCollaborationAI(request: CreateCollaborationAIRequest): Promise<CollaborationAIConfig> {
    const response = await apiClient.post<CollaborationAIConfig>('', request);
    return response.data;
  }

  /**
   * 根据 ID 获取协作 AI 配置
   */
  async getCollaborationAI(id: number): Promise<CollaborationAIConfig> {
    const response = await apiClient.get<CollaborationAIConfig>(`/${id}`);
    return response.data;
  }

  /**
   * 根据会话 ID 获取协作 AI 配置
   */
  async getCollaborationAIBySessionId(sessionId: string): Promise<CollaborationAIConfig> {
    const response = await apiClient.get<CollaborationAIConfig>(`/session/${sessionId}`);
    return response.data;
  }

  /**
   * 更新协作 AI 配置
   */
  async updateCollaborationAI(id: number, request: UpdateCollaborationAIRequest): Promise<CollaborationAIConfig> {
    const response = await apiClient.put<CollaborationAIConfig>(`/${id}`, request);
    return response.data;
  }

  /**
   * 删除协作 AI 配置
   */
  async deleteCollaborationAI(id: number): Promise<void> {
    await apiClient.delete(`/${id}`);
  }

  /**
   * 获取用户的所有协作 AI 配置
   */
  async getUserCollaborationAIs(userId: string): Promise<CollaborationAIConfig[]> {
    const response = await apiClient.get<CollaborationAIConfig[]>(`/user/${userId}`);
    return response.data;
  }

  /**
   * 获取群组的所有协作 AI 配置
   */
  async getGroupCollaborationAIs(groupId: string): Promise<CollaborationAIConfig[]> {
    const response = await apiClient.get<CollaborationAIConfig[]>(`/group/${groupId}`);
    return response.data;
  }

  /**
   * 获取所有启用的协作 AI 配置
   */
  async getAllEnabledCollaborationAIs(): Promise<CollaborationAIConfig[]> {
    const response = await apiClient.get<CollaborationAIConfig[]>('/enabled');
    return response.data;
  }

  /**
   * 分页获取协作 AI 配置
   */
  async getCollaborationAIs(
    page: number = 0,
    size: number = 20,
    sort?: string
  ): Promise<CollaborationAIPageResponse> {
    const params: any = { page, size };
    if (sort) params.sort = sort;
    
    const response = await apiClient.get<CollaborationAIPageResponse>('', { params });
    return response.data;
  }

  /**
   * 分析协作会议并生成纪要
   */
  async analyzeMeetingAndGenerateMinutes(
    id: number,
    request: AnalyzeMeetingRequest
  ): Promise<CollaborationAIConfig> {
    const response = await apiClient.post<CollaborationAIConfig>(`/${id}/analyze-meeting`, request);
    return response.data;
  }

  /**
   * 跟踪项目进度
   */
  async trackProjectProgress(
    id: number,
    request: TrackProgressRequest
  ): Promise<CollaborationAIConfig> {
    const response = await apiClient.post<CollaborationAIConfig>(`/${id}/track-progress`, request.projectData);
    return response.data;
  }

  /**
   * 识别任务分配
   */
  async identifyTaskAssignments(
    id: number,
    request: IdentifyTasksRequest
  ): Promise<CollaborationAIConfig> {
    const response = await apiClient.post<CollaborationAIConfig>(`/${id}/identify-tasks`, request);
    return response.data;
  }

  /**
   * 分析协作模式
   */
  async analyzeCollaborationPatterns(
    id: number,
    request: AnalyzePatternsRequest
  ): Promise<CollaborationAIConfig> {
    const response = await apiClient.post<CollaborationAIConfig>(`/${id}/analyze-patterns`, request.collaborationMetrics);
    return response.data;
  }

  /**
   * 提供实时协作建议
   */
  async provideRealtimeSuggestions(
    id: number,
    request: ProvideSuggestionsRequest
  ): Promise<CollaborationAIConfig> {
    const response = await apiClient.post<CollaborationAIConfig>(`/${id}/provide-suggestions`, request);
    return response.data;
  }

  /**
   * 生成个性化效率报告
   */
  async generateEfficiencyReport(id: number): Promise<CollaborationAIConfig> {
    const response = await apiClient.post<CollaborationAIConfig>(`/${id}/generate-report`);
    return response.data;
  }

  /**
   * 构建团队知识库
   */
  async buildTeamKnowledge(
    id: number,
    request: BuildKnowledgeRequest
  ): Promise<CollaborationAIConfig> {
    const response = await apiClient.post<CollaborationAIConfig>(`/${id}/build-knowledge`, request);
    return response.data;
  }

  /**
   * 识别协作瓶颈
   */
  async identifyBottlenecks(
    id: number,
    request: IdentifyBottlenecksRequest
  ): Promise<CollaborationAIConfig> {
    const response = await apiClient.post<CollaborationAIConfig>(`/${id}/identify-bottlenecks`, request.performanceData);
    return response.data;
  }

  /**
   * 优化角色分配
   */
  async optimizeRoleAllocation(
    id: number,
    request: OptimizeRolesRequest
  ): Promise<CollaborationAIConfig> {
    const response = await apiClient.post<CollaborationAIConfig>(`/${id}/optimize-roles`, request);
    return response.data;
  }

  /**
   * 评估会议质量
   */
  async assessMeetingQuality(
    id: number,
    request: AssessMeetingRequest
  ): Promise<CollaborationAIConfig> {
    const response = await apiClient.post<CollaborationAIConfig>(`/${id}/assess-meeting`, request.meetingMetrics);
    return response.data;
  }

  /**
   * 添加洞察
   */
  async addInsight(id: number, request: AddInsightRequest): Promise<CollaborationAIConfig> {
    const response = await apiClient.post<CollaborationAIConfig>(`/${id}/insights`, request);
    return response.data;
  }

  /**
   * 添加推荐
   */
  async addRecommendation(id: number, request: AddRecommendationRequest): Promise<CollaborationAIConfig> {
    const response = await apiClient.post<CollaborationAIConfig>(`/${id}/recommendations`, request);
    return response.data;
  }

  /**
   * 更新性能指标
   */
  async updatePerformanceMetric(
    id: number,
    request: UpdateMetricRequest
  ): Promise<CollaborationAIConfig> {
    const response = await apiClient.post<CollaborationAIConfig>(`/${id}/metrics`, request);
    return response.data;
  }

  /**
   * 获取需要进行分析的协作 AI 配置
   */
  async getCollaborationAIsForAnalysis(): Promise<CollaborationAIConfig[]> {
    const response = await apiClient.get<CollaborationAIConfig[]>('/for-analysis');
    return response.data;
  }

  /**
   * 批量启用协作 AI 配置
   */
  async batchEnableCollaborationAIs(request: BatchOperationRequest): Promise<BatchOperationResponse> {
    const response = await apiClient.post<BatchOperationResponse>('/batch-enable', request);
    return response.data;
  }

  /**
   * 批量禁用协作 AI 配置
   */
  async batchDisableCollaborationAIs(request: BatchOperationRequest): Promise<BatchOperationResponse> {
    const response = await apiClient.post<BatchOperationResponse>('/batch-disable', request);
    return response.data;
  }

  /**
   * 获取协作类型统计
   */
  async getCollaborationTypeStatistics(): Promise<CollaborationTypeStatistics> {
    const response = await apiClient.get<CollaborationTypeStatistics>('/statistics/types');
    return response.data;
  }

  /**
   * 获取分析频率统计
   */
  async getAnalysisFrequencyStatistics(): Promise<AnalysisFrequencyStatistics> {
    const response = await apiClient.get<AnalysisFrequencyStatistics>('/statistics/frequencies');
    return response.data;
  }

  /**
   * 搜索会议纪要
   */
  async searchMeetingMinutes(keyword: string): Promise<CollaborationAIConfig[]> {
    const response = await apiClient.get<CollaborationAIConfig[]>('/search/minutes', {
      params: { keyword }
    });
    return response.data;
  }

  /**
   * 搜索项目进度
   */
  async searchProjectProgress(keyword: string): Promise<CollaborationAIConfig[]> {
    const response = await apiClient.get<CollaborationAIConfig[]>('/search/progress', {
      params: { keyword }
    });
    return response.data;
  }

  /**
   * 搜索任务分配
   */
  async searchTaskAssignments(keyword: string): Promise<CollaborationAIConfig[]> {
    const response = await apiClient.get<CollaborationAIConfig[]>('/search/tasks', {
      params: { keyword }
    });
    return response.data;
  }

  /**
   * 获取最近更新的协作 AI 配置
   */
  async getRecentlyUpdatedCollaborationAIs(hours: number = 24): Promise<CollaborationAIConfig[]> {
    const response = await apiClient.get<CollaborationAIConfig[]>('/recently-updated', {
      params: { hours }
    });
    return response.data;
  }

  /**
   * 获取需要生成报告的配置
   */
  async getCollaborationAIsForReportGeneration(days: number = 7): Promise<CollaborationAIConfig[]> {
    const response = await apiClient.get<CollaborationAIConfig[]>('/for-report-generation', {
      params: { days }
    });
    return response.data;
  }

  /**
   * 获取有洞察但无推荐的配置
   */
  async getCollaborationAIsWithInsightsButNoRecommendations(): Promise<CollaborationAIConfig[]> {
    const response = await apiClient.get<CollaborationAIConfig[]>('/insights-without-recommendations');
    return response.data;
  }

  /**
   * 获取有瓶颈但无建议的配置
   */
  async getCollaborationAIsWithBottlenecksButNoSuggestions(): Promise<CollaborationAIConfig[]> {
    const response = await apiClient.get<CollaborationAIConfig[]>('/bottlenecks-without-suggestions');
    return response.data;
  }

  /**
   * 获取系统健康状态
   */
  async getHealthStatus(): Promise<HealthStatus> {
    const response = await apiClient.get<HealthStatus>('/health');
    return response.data;
  }

  /**
   * 导出协作 AI 配置
   */
  async exportCollaborationAIs(
    startDate?: string,
    endDate?: string
  ): Promise<{ message: string; timestamp: string }> {
    const params: any = {};
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;
    
    const response = await apiClient.get('/export', { params });
    return response.data;
  }

  /**
   * 快速创建并启用协作 AI 配置
   */
  async quickCreate(
    sessionId: string,
    userId: string,
    groupId?: string,
    collaborationType?: string
  ): Promise<CollaborationAIConfig> {
    const request: CreateCollaborationAIRequest = {
      sessionId,
      userId,
      groupId,
      collaborationType: collaborationType as any || 'TEAM_MEETING',
      analysisFrequency: 60,
      autoGenerateMinutes: true,
      trackProgress: true,
      identifyTasks: true,
      analyzePatterns: true,
      provideSuggestions: true,
      generateReport: true,
      buildKnowledge: true,
      identifyBottlenecks: true,
      optimizeRoles: true,
      assessMeetings: true
    };
    
    return await this.createCollaborationAI(request);
  }

  /**
   * 获取用户的协作 AI 配置摘要
   */
  async getUserCollaborationSummary(userId: string): Promise<{
    totalConfigs: number;
    enabledConfigs: number;
    activeAnalyses: number;
    pendingReports: number;
  }> {
    const [allConfigs, enabledConfigs, forAnalysis, forReport] = await Promise.all([
      this.getUserCollaborationAIs(userId),
      this.getAllEnabledCollaborationAIs(),
      this.getCollaborationAIsForAnalysis(),
      this.getCollaborationAIsForReportGeneration(7)
    ]);
    
    const userEnabledConfigs = enabledConfigs.filter(c => c.userId === userId);
    const userForAnalysis = forAnalysis.filter(c => c.userId === userId);
    const userForReport = forReport.filter(c => c.userId === userId);
    
    return {
      totalConfigs: allConfigs.length,
      enabledConfigs: userEnabledConfigs.length,
      activeAnalyses: userForAnalysis.length,
      pendingReports: userForReport.length
    };
  }

  /**
   * 一键分析所有待分析的协