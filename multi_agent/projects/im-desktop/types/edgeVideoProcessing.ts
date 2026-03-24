/**
 * 边缘视频处理类型定义
 * 用于 TypeScript 桌面端应用
 */

// 媒体类型枚举
export enum MediaType {
  VIDEO_ONLY = 'VIDEO_ONLY',
  AUDIO_ONLY = 'AUDIO_ONLY',
  VIDEO_WITH_AUDIO = 'VIDEO_WITH_AUDIO',
  SCREEN_SHARE = 'SCREEN_SHARE',
  VIDEO_CONFERENCE = 'VIDEO_CONFERENCE',
  LIVE_STREAMING = 'LIVE_STREAMING',
  VOD_PROCESSING = 'VOD_PROCESSING'
}

// 处理状态枚举
export enum ProcessingStatus {
  PENDING = 'PENDING',
  QUEUED = 'QUEUED',
  PROCESSING = 'PROCESSING',
  PAUSED = 'PAUSED',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
  CANCELLED = 'CANCELLED',
  TIMEOUT = 'TIMEOUT'
}

// 边缘节点类型枚举
export enum NodeType {
  CLOUD_EDGE = 'CLOUD_EDGE',
  REGIONAL_EDGE = 'REGIONAL_EDGE',
  LOCAL_EDGE = 'LOCAL_EDGE',
  MOBILE_EDGE = 'MOBILE_EDGE',
  IOT_EDGE = 'IOT_EDGE',
  HYBRID_EDGE = 'HYBRID_EDGE',
  FOG_COMPUTING = 'FOG_COMPUTING'
}

// 健康状态枚举
export enum HealthStatus {
  HEALTHY = 'HEALTHY',
  WARNING = 'WARNING',
  CRITICAL = 'CRITICAL',
  DEGRADED = 'DEGRADED',
  UNKNOWN = 'UNKNOWN'
}

// 连接状态枚举
export enum ConnectionStatus {
  ONLINE = 'ONLINE',
  OFFLINE = 'OFFLINE',
  CONNECTING = 'CONNECTING',
  DISCONNECTED = 'DISCONNECTED',
  UNREACHABLE = 'UNREACHABLE'
}

// 安全级别枚举
export enum SecurityLevel {
  MINIMAL = 'MINIMAL',
  STANDARD = 'STANDARD',
  ENHANCED = 'ENHANCED',
  HIGH = 'HIGH',
  MISSION_CRITICAL = 'MISSION_CRITICAL'
}

// 视频处理任务接口
export interface EdgeVideoProcessingTask {
  id?: string;
  taskId: string;
  sessionId: string;
  userId: string;
  edgeNodeId: string;
  mediaType: MediaType;
  inputSource: string;
  outputDestination?: string;
  processingStatus: ProcessingStatus;
  videoCodec?: string;
  audioCodec?: string;
  resolutionWidth?: number;
  resolutionHeight?: number;
  frameRate?: number;
  bitrateKbps?: number;
  aiEnhancementsEnabled?: boolean;
  enhancementType?: string;
  bandwidthOptimizationEnabled?: boolean;
  compressionLevel?: number;
  latencyMs?: number;
  processingStartTime?: Date;
  processingEndTime?: Date;
  processingDurationMs?: number;
  cpuUsagePercent?: number;
  memoryUsageMb?: number;
  networkBandwidthMbps?: number;
  qualityScore?: number;
  errorMessage?: string;
  retryCount?: number;
  maxRetries?: number;
  priorityLevel?: number;
  createdAt: Date;
  updatedAt: Date;
  expiresAt?: Date;
  metadataJson?: string;
}

// 边缘节点接口
export interface EdgeNode {
  id?: string;
  nodeId: string;
  nodeName: string;
  nodeType: NodeType;
  geographicLocation?: string;
  latitude?: number;
  longitude?: number;
  ipAddress: string;
  port: number;
  apiEndpoint?: string;
  healthStatus: HealthStatus;
  connectionStatus: ConnectionStatus;
  lastHeartbeat?: Date;
  cpuCores?: number;
  cpuUsagePercent?: number;
  totalMemoryMb?: number;
  usedMemoryMb?: number;
  totalDiskGb?: number;
  usedDiskGb?: number;
  networkBandwidthMbps?: number;
  networkLatencyMs?: number;
  gpuAvailable?: boolean;
  gpuType?: string;
  gpuMemoryGb?: number;
  supportedVideoCodecs?: string;
  supportedAudioCodecs?: string;
  maxConcurrentSessions: number;
  currentSessions: number;
  videoProcessingCapacity?: number;
  audioProcessingCapacity?: number;
  aiAccelerationSupported?: boolean;
  aiModelTypes?: string;
  bandwidthOptimizationSupported?: boolean;
  realTimeTranscodingSupported?: boolean;
  securityLevel: SecurityLevel;
  sslEnabled?: boolean;
  certificateExpiry?: Date;
  maintenanceMode?: boolean;
  scheduledMaintenanceStart?: Date;
  scheduledMaintenanceEnd?: Date;
  softwareVersion?: string;
  lastSoftwareUpdate?: Date;
  tags?: string;
  metadataJson?: string;
  createdAt: Date;
  updatedAt: Date;
  lastPerformanceReport?: Date;
}

// 创建任务请求接口
export interface CreateTaskRequest {
  sessionId: string;
  userId: string;
  mediaType: MediaType;
  inputSource: string;
  processingOptions?: ProcessingOptions;
}

// 处理选项接口
export interface ProcessingOptions {
  videoCodec?: string;
  audioCodec?: string;
  resolutionWidth?: number;
  resolutionHeight?: number;
  frameRate?: number;
  bitrateKbps?: number;
  aiEnhancementsEnabled?: boolean;
  enhancementType?: string;
  bandwidthOptimizationEnabled?: boolean;
  compressionLevel?: number;
  priorityLevel?: number;
  maxRetries?: number;
  [key: string]: any;
}

// API 响应接口
export interface ApiResponse<T = any> {
  success: boolean;
  data?: T;
  error?: string;
  message?: string;
  timestamp?: number;
}

// 任务创建响应接口
export interface TaskCreationResponse {
  taskId: string;
  status: ProcessingStatus;
  edgeNodeId: string;
  createdAt: Date;
}

// 系统统计信息接口
export interface SystemStatistics {
  totalTasks: number;
  activeTasks: number;
  completedTasks: number;
  failedTasks: number;
  availableNodes: number;
  totalNodeCapacity: number;
  usedNodeCapacity: number;
  averageProcessingTimeMs: number;
  averageQualityScore: number;
}

// 支持的编解码器接口
export interface SupportedCodecs {
  videoCodecs: string[];
  audioCodecs: string[];
  containerFormats: string[];
}

// AI 增强选项接口
export interface AiEnhancementOptions {
  availableEnhancements: string[];
  supportedModels: string[];
}

// 带宽优化选项接口
export interface BandwidthOptimizationOptions {
  compressionLevels: number[];
  adaptiveBitrate: boolean;
  contentAwareCompression: boolean;
  webOptimized: boolean;
  mobileOptimized: boolean;
  recommendedSettings: {
    lowBandwidth: BandwidthSetting;
    mediumBandwidth: BandwidthSetting;
    highBandwidth: BandwidthSetting;
  };
}

// 带宽设置接口
export interface BandwidthSetting {
  bitrateKbps: number;
  resolution: string;
}

// 系统配置接口
export interface SystemConfiguration {
  maxConcurrentTasksPerNode: number;
  defaultRetryCount: number;
  defaultPriorityLevel: number;
  taskTimeoutMinutes: number;
  heartbeatIntervalSeconds: number;
  cleanupIntervalHours: number;
  maxTaskHistoryDays: number;
  enableAiEnhancements: boolean;
  enableBandwidthOptimization: boolean;
  enableRealTimeMonitoring: boolean;
  defaultVideoCodec: string;
  defaultAudioCodec: string;
  defaultResolution: string;
  defaultFrameRate: number;
  defaultBitrateKbps: number;
}

// 健康检查响应接口
export interface HealthCheckResponse {
  status: string;
  service: string;
  timestamp: number;
  activeTasks: number;
  availableNodes: number;
}

// 节点连接测试响应接口
export interface NodeConnectionTestResponse {
  nodeId: string;
  status: string;
  responseTimeMs: number;
  timestamp: number;
}

// 任务列表响应接口
export interface TaskListResponse {
  userId?: string;
  sessionId?: string;
  totalTasks: number;
  tasks: EdgeVideoProcessingTask[];
}

// 任务操作响应接口
export interface TaskOperationResponse {
  success: boolean;
  message?: string;
  error?: string;
}

// 工具函数：将字符串转换为枚举
export function stringToMediaType(value: string): MediaType {
  return MediaType[value as keyof typeof MediaType] || MediaType.VIDEO_WITH_AUDIO;
}

export function stringToProcessingStatus(value: string): ProcessingStatus {
  return ProcessingStatus[value as keyof typeof ProcessingStatus] || ProcessingStatus.PENDING;
}

export function stringToNodeType(value: string): NodeType {
  return NodeType[value as keyof typeof NodeType] || NodeType.LOCAL_EDGE;
}

// 工具函数：验证处理选项
export function validateProcessingOptions(options: ProcessingOptions): string[] {
  const errors: string[] = [];

  if (options.resolutionWidth && options.resolutionWidth < 64) {
    errors.push('Resolution width must be at least 64 pixels');
  }

  if (options.resolutionHeight && options.resolutionHeight < 64) {
    errors.push('Resolution height must be at least 64 pixels');
  }

  if (options.frameRate && (options.frameRate < 1 || options.frameRate > 120)) {
    errors.push('Frame rate must be between 1 and 120 fps');
  }

  if (options.bitrateKbps && options.bitrateKbps < 64) {
    errors.push('Bitrate must be at least 64 kbps');
  }

  if (options.compressionLevel && (options.compressionLevel < 1 || options.compressionLevel > 10)) {
    errors.push('Compression level must be between 1 and 10');
  }

  if (options.priorityLevel && (options.priorityLevel < 1 || options.priorityLevel > 10)) {
    errors.push('Priority level must be between 1 and 10');
  }

  return errors;
}

// 工具函数：获取媒体类型显示名称
export function getMediaTypeDisplayName(mediaType: MediaType): string {
  const displayNames: Record<MediaType, string> = {
    [MediaType.VIDEO_ONLY]: 'Video Only',
    [MediaType.AUDIO_ONLY]: 'Audio Only',
    [MediaType.VIDEO_WITH_AUDIO]: 'Video with Audio',
    [MediaType.SCREEN_SHARE]: 'Screen Share',
    [MediaType.VIDEO_CONFERENCE]: 'Video Conference',
    [MediaType.LIVE_STREAMING]: 'Live Streaming',
    [MediaType.VOD_PROCESSING]: 'Video on Demand'
  };
  return displayNames[mediaType] || mediaType;
}

// 工具函数：获取状态显示名称
export function getStatusDisplayName(status: ProcessingStatus): string {
  const displayNames: Record<ProcessingStatus, string> = {
    [ProcessingStatus.PENDING]: 'Pending',
    [ProcessingStatus.QUEUED]: 'Queued',
    [ProcessingStatus.PROCESSING]: 'Processing',
    [ProcessingStatus.PAUSED]: 'Paused',
    [ProcessingStatus.COMPLETED]: 'Completed',
    [ProcessingStatus.FAILED]: 'Failed',
    [ProcessingStatus.CANCELLED]: 'Cancelled',
    [ProcessingStatus.TIMEOUT]: 'Timeout'
  };
  return displayNames[status] || status;
}

// 工具函数：获取状态颜色
export function getStatusColor(status: ProcessingStatus): string {
  const colors: Record<ProcessingStatus, string> = {
    [ProcessingStatus.PENDING]: 'warning',
    [ProcessingStatus.QUEUED]: 'info',
    [ProcessingStatus.PROCESSING]: 'primary',
    [ProcessingStatus.PAUSED]: 'secondary',
    [ProcessingStatus.COMPLETED]: 'success',
    [ProcessingStatus.FAILED]: 'error',
    [ProcessingStatus.CANCELLED]: 'default',
    [ProcessingStatus.TIMEOUT]: 'warning'
  };
  return colors[status] || 'default';
}

// 工具函数：计算估计处理时间
export function estimateProcessingTime(task: EdgeVideoProcessingTask): number {
  let estimatedTime = 1000; // 基础 1 秒

  // 基于分辨率
  if (task.resolutionWidth && task.resolutionHeight) {
    const pixels = task.resolutionWidth * task.resolutionHeight;
    estimatedTime += pixels / 100000; // 每 10 万像素增加 1 毫秒
  }

  // 基于帧率
  if (task.frameRate) {
    estimatedTime += task.frameRate * 10;
  }

  // AI 增强会增加时间
  if (task.aiEnhancementsEnabled) {
    estimatedTime *= 2;
  }

  return Math.min(estimatedTime, 10000); // 最大 10 秒
}

// 工具函数：格式化文件大小
export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 Bytes';
  
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

// 工具函数：格式化时间
export function formatDuration(ms: number): string {
  if (!ms) return 'N/A';
  
  const seconds = Math.floor(ms / 1000);
  const minutes = Math.floor(seconds / 60);
  const hours = Math.floor(minutes / 60);
  
  if (hours > 0) {
    return `${hours}h ${minutes % 60}m ${seconds % 60}s`;
  } else if (minutes > 0) {
    return `${minutes}m ${seconds % 60}s`;
  } else {
    return `${seconds}s`;
  }
}

// 默认处理选项
export const DEFAULT_PROCESSING_OPTIONS: ProcessingOptions = {
  videoCodec: 'H.264/AVC',
  audioCodec: 'AAC',
  resolutionWidth: 1280,
  resolutionHeight: 720,
  frameRate: 30,
  bitrateKbps: 2500,
  aiEnhancementsEnabled: false,
  bandwidthOptimizationEnabled: true,
  compressionLevel: 5,
  priorityLevel: 5,
  maxRetries: 3
};

// 预定义的分辨率选项
export const RESOLUTION_PRESETS = {
  '240p': { width: 426, height: 240 },
  '360p': { width: 640, height: 360 },
  '480p': { width: 854, height: 480 },
  '720p': { width: 1280, height: 720 },
  '1080p': { width: 1920, height: 1080 },
  '1440p': { width: 2560, height: 1440 },
  '4K': { width: 3840, height: 2160 },
  '8K': { width: 7680, height: 4320 }
};

// 预定义的比特率选项
export const BITRATE_PRESETS = {
  'Low (240p)': 500,
  'Medium (360p)': 1000,
  'Standard (480p)': 1500,
  'HD (720p)': 2500,
  'Full HD (1080p)': 5000,
  '2K (1440p)': 8000,
  '4K': 15000,
  '8K': 30000
};

// 预定义的帧率选项
export const FRAMERATE_PRESETS = [15, 24, 25, 30, 48, 50, 60, 90, 120];

// 视频编解码器选项
export const VIDEO_CODEC_OPTIONS = [
  'H.264/AVC',
  'H.265/HEVC',
  'VP9',
  'AV1',
  'MPEG-4',
  'H.263'
];

// 音频编解码器选项
export const AUDIO_CODEC_OPTIONS = [
  'AAC',
  'MP3',
  'Opus',
  'Vorbis',
  'FLAC',
  'PCM'
];