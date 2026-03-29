/**
 * 差分隐私与安全 AI 框架类型定义
 */

/**
 * 差分隐私配置接口
 */
export interface DifferentialPrivacyConfig {
  id?: number;
  configKey: string;
  configValue?: string;
  description?: string;
  dataType: string;
  isSensitive?: boolean;
  privacyBudgetLimit?: number;
  epsilon?: number;
  delta?: number;
  noiseMechanism?: string;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  updatedBy?: string;
  isActive?: boolean;
  version?: number;
  requiresApproval?: boolean;
  approvalStatus?: string;
}

/**
 * 隐私预算接口
 */
export interface PrivacyBudget {
  id?: number;
  userId: string;
  sessionId?: string;
  budgetType: string;
  totalBudget: number;
  consumedBudget?: number;
  remainingBudget?: number;
  budgetPeriod: string;
  periodStart: string;
  periodEnd: string;
  lastConsumedAt?: string;
  consumptionCount?: number;
  avgEpsilonPerConsumption?: number;
  maxEpsilonPerConsumption?: number;
  violationCount?: number;
  warningThreshold?: number;
  blockThreshold?: number;
  isBlocked?: boolean;
  blockReason?: string;
  createdAt?: string;
  updatedAt?: string;
  metadataJson?: string;
}

/**
 * 隐私影响评估接口
 */
export interface PrivacyImpact {
  id?: number;
  operationId: string;
  operationType: string;
  userId?: string;
  sessionId?: string;
  aiModelId?: string;
  datasetId?: string;
  epsilonConsumed: number;
  deltaConsumed?: number;
  privacyBudgetBefore?: number;
  privacyBudgetAfter?: number;
  dataSensitivityScore?: number;
  impactSeverity?: string;
  riskLevel?: string;
  mitigationMeasuresApplied?: string;
  complianceCheckPassed?: boolean;
  dataMinimizationScore?: number;
  purposeLimitationScore?: number;
  storageLimitationScore?: number;
  integrityConfidentialityScore?: number;
  accountabilityScore?: number;
  overallImpactScore?: number;
  recommendationsJson?: string;
  auditTrailJson?: string;
  createdAt?: string;
  completedAt?: string;
  processingTimeMs?: number;
  processingSuccess?: boolean;
  errorMessage?: string;
  userConsentObtained?: boolean;
  consentTimestamp?: string;
  dataRetentionPeriodDays?: number;
  reidentificationRiskScore?: number;
  inferenceAttackRiskScore?: number;
  membershipInferenceRiskScore?: number;
  attributeInferenceRiskScore?: number;
  overallRiskScore?: number;
}

/**
 * 隐私预算统计接口
 */
export interface PrivacyBudgetStats {
  totalConsumed?: number;
  remainingPercentage?: number;
  blockedCount?: number;
  warningCount?: number;
}

/**
 * 隐私影响统计接口
 */
export interface PrivacyImpactStats {
  byOperationType: Record<string, number>;
  bySeverity: Record<string, number>;
  byRiskLevel: Record<string, number>;
  averageImpactScore?: number;
  averageRiskScore?: number;
}

/**
 * 差分隐私配置创建请求
 */
export interface CreateDifferentialPrivacyConfigRequest {
  configKey: string;
  configValue?: string;
  description?: string;
  dataType: string;
  isSensitive?: boolean;
  privacyBudgetLimit?: number;
  epsilon?: number;
  delta?: number;
  noiseMechanism?: string;
  requiresApproval?: boolean;
}

/**
 * 隐私预算创建请求
 */
export interface CreatePrivacyBudgetRequest {
  userId: string;
  sessionId?: string;
  budgetType: string;
  totalBudget: number;
  budgetPeriod: string;
  periodStart: string;
  periodEnd: string;
  warningThreshold?: number;
  blockThreshold?: number;
}

/**
 * 隐私影响评估创建请求
 */
export interface CreatePrivacyImpactRequest {
  operationId: string;
  operationType: string;
  userId?: string;
  sessionId?: string;
  aiModelId?: string;
  datasetId?: string;
  epsilonConsumed: number;
  deltaConsumed?: number;
  dataSensitivityScore?: number;
}

/**
 * 隐私预算消耗请求
 */
export interface ConsumePrivacyBudgetRequest {
  userId: string;
  budgetType: string;
  epsilon: number;
}

/**
 * 隐私预算检查响应
 */
export interface BudgetCheckResponse {
  sufficient: boolean;
  remainingBudget?: number;
  requiredBudget?: number;
}

/**
 * 隐私配置验证响应
 */
export interface ConfigValidationResponse {
  valid: boolean;
  message?: string;
}
