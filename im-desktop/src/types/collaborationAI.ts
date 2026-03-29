/**
 * 协作增强 AI 助手类型定义
 * 用于团队协作 AI 功能的类型系统和接口
 */

/**
 * 协作类型枚举
 */
export enum CollaborationType {
  TEAM_MEETING = 'TEAM_MEETING',
  PROJECT_DISCUSSION = 'PROJECT_DISCUSSION',
  BRAINSTORM_SESSION = 'BRAINSTORM_SESSION',
  DAILY_STANDUP = 'DAILY_STANDUP',
  RETROSPECTIVE = 'RETROSPECTIVE',
  PLANNING_SESSION = 'PLANNING_SESSION',
  CODE_REVIEW = 'CODE_REVIEW',
  DESIGN_REVIEW = 'DESIGN_REVIEW',
  TRAINING_SESSION = 'TRAINING_SESSION',
  CLIENT_MEETING = 'CLIENT_MEETING',
  ONE_ON_ONE = 'ONE_ON_ONE',
  WORKSHOP = 'WORKSHOP',
  CONFERENCE = 'CONFERENCE',
  OTHER = 'OTHER'
}

/**
 * 协作 AI 配置接口
 */
export interface CollaborationAIConfig {
  id?: number;
  sessionId: string;
  userId: string;
  groupId?: string | null;
  collaborationType: CollaborationType;
  meetingMinutes?: string | null;
  projectProgress?: string | null;
  taskAssignments?: string | null;
  collaborationPatterns?: string | null;
  realtimeSuggestions?: string | null;
  efficiencyReport?: string | null;
  teamKnowledge?: string | null;
  bottleneckAnalysis?: string | null;
  roleAllocation?: string | null;
  meetingQuality?: string | null;
  enabled: boolean;
  aiConfidence: number;
  analysisFrequency: number;
  autoGenerateMinutes: boolean;
  trackProgress: boolean;
  identifyTasks: boolean;
  analyzePatterns: boolean;
  provideSuggestions: boolean;
  generateReport: boolean;
  buildKnowledge: boolean;
  identifyBottlenecks: boolean;
  optimizeRoles: boolean;
  assessMeetings: boolean;
  insights?: string[];
  recommendations?: string[];
  performanceMetrics?: Record<string, number>;
  customSettings?: string | null;
  createdAt?: string;
  updatedAt?: string;
  lastAnalysisAt?: string | null;
  nextAnalysisAt?: string | null;
  version?: number;
}

/**
 * 创建协作 AI 配置请求
 */
export interface CreateCollaborationAIRequest {
  sessionId: string;
  userId: string;
  groupId?: string;
  collaborationType: CollaborationType;
  analysisFrequency?: number;
  autoGenerateMinutes?: boolean;
  trackProgress?: boolean;
  identifyTasks?: boolean;
  analyzePatterns?: boolean;
  provideSuggestions?: boolean;
  generateReport?: boolean;
  buildKnowledge?: boolean;
  identifyBottlenecks?: boolean;
  optimizeRoles?: boolean;
  assessMeetings?: boolean;
  customSettings?: Record<string, any>;
}

/**
 * 更新协作 AI 配置请求
 */
export interface UpdateCollaborationAIRequest {
  sessionId?: string;
  userId?: string;
  groupId?: string;
  collaborationType?: CollaborationType;
  meetingMinutes?: string;
  projectProgress?: string;
  taskAssignments?: string;
  collaborationPatterns?: string;
  realtimeSuggestions?: string;
  efficiencyReport?: string;
  teamKnowledge?: string;
  bottleneckAnalysis?: string;
  roleAllocation?: string;
  meetingQuality?: string;
  enabled?: boolean;
  aiConfidence?: number;
  analysisFrequency?: number;
  autoGenerateMinutes?: boolean;
  trackProgress?: boolean;
  identifyTasks?: boolean;
  analyzePatterns?: boolean;
  provideSuggestions?: boolean;
  generateReport?: boolean;
  buildKnowledge?: boolean;
  identifyBottlenecks?: boolean;
  optimizeRoles?: boolean;
  assessMeetings?: boolean;
  insights?: string[];
  recommendations?: string[];
  performanceMetrics?: Record<string, number>;
  customSettings?: Record<string, any>;
}

/**
 * 分析会议请求
 */
export interface AnalyzeMeetingRequest {
  conversationContent: string;
}

/**
 * 跟踪项目进度请求
 */
export interface TrackProgressRequest {
  projectData: Record<string, any>;
}

/**
 * 识别任务请求
 */
export interface IdentifyTasksRequest {
  participantMessages: string[];
}

/**
 * 分析协作模式请求
 */
export interface AnalyzePatternsRequest {
  collaborationMetrics: Record<string, any>;
}

/**
 * 提供实时建议请求
 */
export interface ProvideSuggestionsRequest {
  currentContext: string;
}

/**
 * 构建知识请求
 */
export interface BuildKnowledgeRequest {
  knowledgeSources: string[];
}

/**
 * 识别瓶颈请求
 */
export interface IdentifyBottlenecksRequest {
  performanceData: Record<string, any>;
}

/**
 * 优化角色请求
 */
export interface OptimizeRolesRequest {
  participantSkills: Array<Record<string, any>>;
}

/**
 * 评估会议请求
 */
export interface AssessMeetingRequest {
  meetingMetrics: Record<string, any>;
}

/**
 * 添加洞察请求
 */
export interface AddInsightRequest {
  insight: string;
}

/**
 * 添加推荐请求
 */
export interface AddRecommendationRequest {
  recommendation: string;
}

/**
 * 更新性能指标请求
 */
export interface UpdateMetricRequest {
  metricName: string;
  metricValue: number;
}

/**
 * 批量操作请求
 */
export interface BatchOperationRequest {
  ids: number[];
}

/**
 * 批量操作响应
 */
export interface BatchOperationResponse {
  updatedCount: number;
  message: string;
}

/**
 * 协作类型统计
 */
export interface CollaborationTypeStatistics {
  [key: string]: number;
}

/**
 * 分析频率统计
 */
export interface AnalysisFrequencyStatistics {
  [key: number]: number;
}

/**
 * 系统健康状态
 */
export interface HealthStatus {
  status: 'healthy' | 'unhealthy';
  timestamp: string;
  totalConfigurations?: number;
  enabledConfigurations?: number;
  configurationsForAnalysis?: number;
  configurationsForReport?: number;
  uptime?: string;
  error?: string;
}

/**
 * 协作 AI 配置分页响应
 */
export interface CollaborationAIPageResponse {
  content: CollaborationAIConfig[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      sorted: boolean;
      unsorted: boolean;
      empty: boolean;
    };
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}

/**
 * 协作类型标签映射
 */
export const COLLABORATION_TYPE_LABELS: Record<CollaborationType, string> = {
  [CollaborationType.TEAM_MEETING]: '团队会议',
  [CollaborationType.PROJECT_DISCUSSION]: '项目讨论',
  [CollaborationType.BRAINSTORM_SESSION]: '头脑风暴',
  [CollaborationType.DAILY_STANDUP]: '每日站会',
  [CollaborationType.RETROSPECTIVE]: '回顾会议',
  [CollaborationType.PLANNING_SESSION]: '规划会议',
  [CollaborationType.CODE_REVIEW]: '代码评审',
  [CollaborationType.DESIGN_REVIEW]: '设计评审',
  [CollaborationType.TRAINING_SESSION]: '培训会议',
  [CollaborationType.CLIENT_MEETING]: '客户会议',
  [CollaborationType.ONE_ON_ONE]: '一对一会议',
  [CollaborationType.WORKSHOP]: '研讨会',
  [CollaborationType.CONFERENCE]: '会议',
  [CollaborationType.OTHER]: '其他'
};

/**
 * 获取协作类型标签
 */
export function getCollaborationTypeLabel(type: CollaborationType): string {
  return COLLABORATION_TYPE_LABELS[type] || type;
}

/**
 * 验证协作 AI 配置
 */
export function validateCollaborationAIConfig(config: CreateCollaborationAIRequest): string[] {
  const errors: string[] = [];
  
  if (!config.sessionId || config.sessionId.trim().length === 0) {
    errors.push('会话ID不能为空');
  }
  
  if (!config.userId || config.userId.trim().length === 0) {
    errors.push('用户ID不能为空');
  }
  
  if (!config.collaborationType) {
    errors.push('协作类型不能为空');
  }
  
  if (config.analysisFrequency !== undefined && config.analysisFrequency <= 0) {
    errors.push('分析频率必须大于0');
  }
  
  if (config.aiConfidence !== undefined && (config.aiConfidence < 0 || config.aiConfidence > 100)) {
    errors.push('AI置信度必须在0-100之间');
  }
  
  return errors;
}

/**
 * 创建默认的协作 AI 配置
 */
export function createDefaultCollaborationConfig(
  sessionId: string,
  userId: string,
  groupId?: string,
  collaborationType: CollaborationType = CollaborationType.TEAM_MEETING
): CreateCollaborationAIRequest {
  return {
    sessionId,
    userId,
    groupId,
    collaborationType,
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
}

/**
 * 格式化 AI 置信度显示
 */
export function formatConfidence(confidence: number): string {
  if (confidence >= 80) return `高 (${confidence}%)`;
  if (confidence >= 50) return `中 (${confidence}%)`;
  return `低 (${confidence}%)`;
}

/**
 * 格式化分析频率显示
 */
export function formatAnalysisFrequency(minutes: number): string {
  if (minutes < 60) return `${minutes}分钟`;
  if (minutes < 1440) return `${Math.floor(minutes / 60)}小时`;
  return `${Math.floor(minutes / 1440)}天`;
}

/**
 * 检查配置是否启用
 */
export function isCollaborationAIEnabled(config: CollaborationAIConfig): boolean {
  return config.enabled === true;
}

/**
 * 检查是否需要分析
 */
export function needsAnalysis(config: CollaborationAIConfig): boolean {
  if (!config.nextAnalysisAt) return false;
  const nextAnalysis = new Date(config.nextAnalysisAt);
  return new Date() >= nextAnalysis;
}

/**
 * 获取启用的功能列表
 */
export function getEnabledFeatures(config: CollaborationAIConfig): string[] {
  const features: string[] = [];
  
  if (config.autoGenerateMinutes) features.push('自动生成纪要');
  if (config.trackProgress) features.push('跟踪进度');
  if (config.identifyTasks) features.push('识别任务');
  if (config.analyzePatterns) features.push('分析模式');
  if (config.provideSuggestions) features.push('实时建议');
  if (config.generateReport) features.push('效率报告');
  if (config.buildKnowledge) features.push('构建知识库');
  if (config.identifyBottlenecks) features.push('识别瓶颈');
  if (config.optimizeRoles) features.push('优化角色');
  if (config.assessMeetings) features.push('评估会议');
  
  return features;
}

/**
 * 计算配置完整度
 */
export function calculateConfigCompleteness(config: CollaborationAIConfig): number {
  const totalFields = 20;
  let filledFields = 0;
  
  if (config.sessionId) filledFields++;
  if (config.userId) filledFields++;
  if (config.groupId) filledFields++;
  if (config.collaborationType) filledFields++;
  if (config.meetingMinutes) filledFields++;
  if (config.projectProgress) filledFields++;
  if (config.taskAssignments) filledFields++;
  if (config.collaborationPatterns) filledFields++;
  if (config.realtimeSuggestions) filledFields++;
  if (config.efficiencyReport) filledFields++;
  if (config.teamKnowledge) filledFields++;
  if (config.bottleneckAnalysis) filledFields++;
  if (config.roleAllocation) filledFields++;
  if (config.meetingQuality) filledFields++;
  if (config.insights && config.insights.length > 0) filledFields++;
  if (config.recommendations && config.recommendations.length > 0) filledFields++;
  if (config.performanceMetrics && Object.keys(config.performanceMetrics).length > 0) filledFields++;
  if (config.customSettings) filledFields++;
  if (config.lastAnalysisAt) filledFields++;
  if (config.nextAnalysisAt) filledFields++;
  
  return Math.round((filledFields / totalFields) * 100);
}

/**
 * 获取 AI 置信度颜色
 */
export function getConfidenceColor(confidence: number): string {
  if (confidence >= 80) return '#10B981';
  if (confidence >= 50) return '#F59E0B';
  return '#EF4444';
}

/**
 * 协作 AI 配置状态
 */
export enum CollaborationAIStatus {
  ACTIVE = 'active',
  INACTIVE = 'inactive',
  ANALYZING = 'analyzing',
  ERROR = 'error'
}

/**
 * 获取配置状态
 */
export function getCollaborationAIStatus(config: CollaborationAIConfig): CollaborationAIStatus {
  if (!config.enabled) return CollaborationAIStatus.INACTIVE;
  if (needsAnalysis(config)) return CollaborationAIStatus.ANALYZING;
  return CollaborationAIStatus.ACTIVE;
}
