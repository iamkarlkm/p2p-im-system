/**
 * 自适应网络协议与传输优化服务
 * 支持 QUIC/HTTP3、WebRTC、WebSocket 等多协议自适应切换
 */

export enum NetworkProtocol {
  QUIC = 'quic',
  HTTP3 = 'http3',
  WebRTC = 'webrtc',
  WebSocket = 'websocket',
  HTTP2 = 'http2',
  HTTP1_1 = 'http1.1',
  TCP = 'tcp',
  UDP = 'udp'
}

export enum NetworkQuality {
  EXCELLENT = 'excellent',    // < 50ms, > 10Mbps
  GOOD = 'good',             // 50-200ms, 2-10Mbps
  FAIR = 'fair',             // 200-500ms, 500kbps-2Mbps
  POOR = 'poor',             // > 500ms, < 500kbps
  OFFLINE = 'offline'
}

export interface NetworkMetrics {
  latencyMs: number;          // 延迟（毫秒）
  bandwidthMbps: number;      // 带宽（Mbps）
  packetLossRate: number;     // 丢包率（0-1）
  jitterMs: number;           // 抖动（毫秒）
  connectionStability: number; // 连接稳定性（0-1）
  rttMs: number;             // 往返时间
}

export interface ProtocolCapabilities {
  protocol: NetworkProtocol;
  supportsEncryption: boolean;
  supportsMultiplexing: boolean;
  supportsStreaming: boolean;
  minLatencyMs: number;
  maxBandwidthMbps: number;
  connectionOverheadMs: number;
  recommendedFor: NetworkQuality[];
}

export interface NetworkProfile {
  name: string;
  quality: NetworkQuality;
  recommendedProtocols: NetworkProtocol[];
  fallbackProtocols: NetworkProtocol[];
  optimizationStrategies: string[];
}

export class AdaptiveNetworkService {
  private currentProtocol: NetworkProtocol = NetworkProtocol.WebSocket;
  private currentMetrics: NetworkMetrics;
  private protocolCapabilities: Map<NetworkProtocol, ProtocolCapabilities>;
  private networkProfiles: Map<NetworkQuality, NetworkProfile>;
  
  // 连接池和会话管理
  private activeConnections: Map<string, NetworkConnection> = new Map();
  private protocolSessions: Map<NetworkProtocol, ProtocolSession> = new Map();
  
  // 统计信息
  private stats: NetworkStats = {
    totalConnections: 0,
    successfulConnections: 0,
    failedConnections: 0,
    totalBytesTransferred: 0,
    protocolSwitches: 0,
    averageLatencyMs: 0
  };

  constructor() {
    this.initializeCapabilities();
    this.initializeNetworkProfiles();
    this.currentMetrics = this.getDefaultMetrics();
  }

  /**
   * 初始化协议能力配置
   */
  private initializeCapabilities(): void {
    this.protocolCapabilities = new Map([
      [NetworkProtocol.QUIC, {
        protocol: NetworkProtocol.QUIC,
        supportsEncryption: true,
        supportsMultiplexing: true,
        supportsStreaming: true,
        minLatencyMs: 10,
        maxBandwidthMbps: 100,
        connectionOverheadMs: 50,
        recommendedFor: [NetworkQuality.EXCELLENT, NetworkQuality.GOOD]
      }],
      [NetworkProtocol.HTTP3, {
        protocol: NetworkProtocol.HTTP3,
        supportsEncryption: true,
        supportsMultiplexing: true,
        supportsStreaming: true,
        minLatencyMs: 20,
        maxBandwidthMbps: 50,
        connectionOverheadMs: 100,
        recommendedFor: [NetworkQuality.EXCELLENT, NetworkQuality.GOOD, NetworkQuality.FAIR]
      }],
      [NetworkProtocol.WebRTC, {
        protocol: NetworkProtocol.WebRTC,
        supportsEncryption: true,
        supportsMultiplexing: true,
        supportsStreaming: true,
        minLatencyMs: 30,
        maxBandwidthMbps: 30,
        connectionOverheadMs: 200,
        recommendedFor: [NetworkQuality.GOOD, NetworkQuality.FAIR, NetworkQuality.POOR]
      }],
      [NetworkProtocol.WebSocket, {
        protocol: NetworkProtocol.WebSocket,
        supportsEncryption: true,
        supportsMultiplexing: false,
        supportsStreaming: true,
        minLatencyMs: 40,
        maxBandwidthMbps: 20,
        connectionOverheadMs: 150,
        recommendedFor: [NetworkQuality.GOOD, NetworkQuality.FAIR]
      }],
      [NetworkProtocol.HTTP2, {
        protocol: NetworkProtocol.HTTP2,
        supportsEncryption: true,
        supportsMultiplexing: true,
        supportsStreaming: false,
        minLatencyMs: 50,
        maxBandwidthMbps: 15,
        connectionOverheadMs: 200,
        recommendedFor: [NetworkQuality.FAIR, NetworkQuality.POOR]
      }],
      [NetworkProtocol.HTTP1_1, {
        protocol: NetworkProtocol.HTTP1_1,
        supportsEncryption: true,
        supportsMultiplexing: false,
        supportsStreaming: false,
        minLatencyMs: 100,
        maxBandwidthMbps: 5,
        connectionOverheadMs: 300,
        recommendedFor: [NetworkQuality.POOR]
      }]
    ]);
  }

  /**
   * 初始化网络质量配置
   */
  private initializeNetworkProfiles(): void {
    this.networkProfiles = new Map([
      [NetworkQuality.EXCELLENT, {
        name: '优质网络',
        quality: NetworkQuality.EXCELLENT,
        recommendedProtocols: [NetworkProtocol.QUIC, NetworkProtocol.HTTP3, NetworkProtocol.WebRTC],
        fallbackProtocols: [NetworkProtocol.WebSocket, NetworkProtocol.HTTP2],
        optimizationStrategies: ['最大带宽', '最低延迟', '多路复用']
      }],
      [NetworkQuality.GOOD, {
        name: '良好网络',
        quality: NetworkQuality.GOOD,
        recommendedProtocols: [NetworkProtocol.HTTP3, NetworkProtocol.WebRTC, NetworkProtocol.WebSocket],
        fallbackProtocols: [NetworkProtocol.HTTP2, NetworkProtocol.HTTP1_1],
        optimizationStrategies: ['平衡带宽和延迟', '连接复用', '压缩优化']
      }],
      [NetworkQuality.FAIR, {
        name: '一般网络',
        quality: NetworkQuality.FAIR,
        recommendedProtocols: [NetworkProtocol.WebRTC, NetworkProtocol.HTTP2, NetworkProtocol.WebSocket],
        fallbackProtocols: [NetworkProtocol.HTTP1_1],
        optimizationStrategies: ['降低延迟', '减少数据量', '连接保持']
      }],
      [NetworkQuality.POOR, {
        name: '较差网络',
        quality: NetworkQuality.POOR,
        recommendedProtocols: [NetworkProtocol.HTTP1_1, NetworkProtocol.HTTP2],
        fallbackProtocols: [],
        optimizationStrategies: ['最小数据量', '连接复用', '重试机制']
      }],
      [NetworkQuality.OFFLINE, {
        name: '离线模式',
        quality: NetworkQuality.OFFLINE,
        recommendedProtocols: [],
        fallbackProtocols: [],
        optimizationStrategies: ['本地缓存', '离线队列', '延迟同步']
      }]
    ]);
  }

  /**
   * 获取默认网络指标
   */
  private getDefaultMetrics(): NetworkMetrics {
    return {
      latencyMs: 100,
      bandwidthMbps: 5,
      packetLossRate: 0.01,
      jitterMs: 20,
      connectionStability: 0.9,
      rttMs: 200
    };
  }

  /**
   * 评估当前网络质量
   */
  public assessNetworkQuality(): NetworkQuality {
    const metrics = this.currentMetrics;
    
    if (metrics.bandwidthMbps === 0) {
      return NetworkQuality.OFFLINE;
    }
    
    if (metrics.latencyMs < 50 && metrics.bandwidthMbps > 10) {
      return NetworkQuality.EXCELLENT;
    } else if (metrics.latencyMs < 200 && metrics.bandwidthMbps > 2) {
      return NetworkQuality.GOOD;
    } else if (metrics.latencyMs < 500 && metrics.bandwidthMbps > 0.5) {
      return NetworkQuality.FAIR;
    } else {
      return NetworkQuality.POOR;
    }
  }

  /**
   * 选择最优协议
   */
  public selectOptimalProtocol(): NetworkProtocol {
    const quality = this.assessNetworkQuality();
    const profile = this.networkProfiles.get(quality);
    
    if (!profile || profile.recommendedProtocols.length === 0) {
      return NetworkProtocol.HTTP1_1; // 默认回退
    }
    
    // 基于当前指标和协议能力评分
    let bestProtocol = profile.recommendedProtocols[0];
    let bestScore = -1;
    
    for (const protocol of profile.recommendedProtocols) {
      const capabilities = this.protocolCapabilities.get(protocol);
      if (!capabilities) continue;
      
      // 评分算法：考虑延迟、带宽、稳定性
      const latencyScore = Math.max(0, 1 - (this.currentMetrics.latencyMs / 1000));
      const bandwidthScore = Math.min(1, this.currentMetrics.bandwidthMbps / capabilities.maxBandwidthMbps);
      const stabilityScore = this.currentMetrics.connectionStability;
      
      const score = (latencyScore * 0.4) + (bandwidthScore * 0.3) + (stabilityScore * 0.3);
      
      if (score > bestScore) {
        bestScore = score;
        bestProtocol = protocol;
      }
    }
    
    return bestProtocol;
  }

  /**
   * 切换到最优协议
   */
  public async switchToOptimalProtocol(): Promise<boolean> {
    const optimalProtocol = this.selectOptimalProtocol();
    
    if (optimalProtocol === this.currentProtocol) {
      return true; // 无需切换
    }
    
    try {
      // 关闭当前协议连接
      await this.closeProtocolConnections(this.currentProtocol);
      
      // 建立新协议连接
      await this.initializeProtocol(optimalProtocol);
      
      this.currentProtocol = optimalProtocol;
      this.stats.protocolSwitches++;
      
      console.log(`协议切换成功: ${this.currentProtocol} -> ${optimalProtocol}`);
      return true;
    } catch (error) {
      console.error(`协议切换失败: ${error.message}`);
      return false;
    }
  }

  /**
   * 发送消息（自适应协议）
   */
  public async sendMessage(message: NetworkMessage): Promise<NetworkResponse> {
    const startTime = Date.now();
    
    try {
      // 检查是否需要协议切换
      if (this.shouldSwitchProtocol()) {
        await this.switchToOptimalProtocol();
      }
      
      // 使用当前协议发送
      const connection = await this.getOrCreateConnection(message.target);
      const response = await connection.send(message);
      
      // 更新统计
      const endTime = Date.now();
      const latency = endTime - startTime;
      
      this.updateStats({
        successful: true,
        bytesTransferred: message.data?.length || 0,
        latencyMs: latency
      });
      
      // 更新网络指标（基于本次传输）
      this.updateNetworkMetrics(latency, message.data?.length || 0);
      
      return response;
    } catch (error) {
      this.updateStats({
        successful: false,
        bytesTransferred: 0,
        latencyMs: Date.now() - startTime
      });
      
      // 尝试回退协议
      if (await this.tryFallbackProtocol()) {
        return this.sendMessage(message); // 重试
      }
      
      throw error;
    }
  }

  /**
   * 更新网络指标
   */
  private updateNetworkMetrics(latencyMs: number, bytesTransferred: number): void {
    // 指数加权移动平均更新指标
    const alpha = 0.2; // 平滑因子
    
    this.currentMetrics.latencyMs = 
      this.currentMetrics.latencyMs * (1 - alpha) + latencyMs * alpha;
    
    // 根据传输字节数和时间估算带宽（简化）
    if (latencyMs > 0) {
      const currentBandwidthMbps = (bytesTransferred * 8 / latencyMs) / 1_000_000;
      this.currentMetrics.bandwidthMbps = 
        this.currentMetrics.bandwidthMbps * (1 - alpha) + currentBandwidthMbps * alpha;
    }
    
    // 更新其他指标（简化）
    this.currentMetrics.rttMs = this.currentMetrics.latencyMs * 2;
    this.currentMetrics.jitterMs = Math.abs(latencyMs - this.currentMetrics.latencyMs) * alpha;
  }

  /**
   * 检查是否需要切换协议
   */
  private shouldSwitchProtocol(): boolean {
    // 基于网络质量变化或性能下降
    const quality = this.assessNetworkQuality();
    const currentCapabilities = this.protocolCapabilities.get(this.currentProtocol);
    
    if (!currentCapabilities) return true;
    
    // 检查当前协议是否适合当前网络质量
    const isProtocolSuitable = currentCapabilities.recommendedFor.includes(quality);
    
    // 检查性能是否低于阈值
    const isPerformancePoor = 
      this.currentMetrics.latencyMs > currentCapabilities.minLatencyMs * 3 ||
      this.currentMetrics.bandwidthMbps < currentCapabilities.maxBandwidthMbps * 0.3;
    
    return !isProtocolSuitable || isPerformancePoor;
  }

  /**
   * 尝试回退协议
   */
  private async tryFallbackProtocol(): Promise<boolean> {
    const quality = this.assessNetworkQuality();
    const profile = this.networkProfiles.get(quality);
    
    if (!profile || profile.fallbackProtocols.length === 0) {
      return false;
    }
    
    for (const protocol of profile.fallbackProtocols) {
      try {
        await this.switchToProtocol(protocol);
        return true;
      } catch (error) {
        continue; // 尝试下一个回退协议
      }
    }
    
    return false;
  }

  /**
   * 切换到指定协议
   */
  private async switchToProtocol(protocol: NetworkProtocol): Promise<void> {
    await this.closeProtocolConnections(this.currentProtocol);
    await this.initializeProtocol(protocol);
    this.currentProtocol = protocol;
    this.stats.protocolSwitches++;
  }

  /**
   * 获取或创建连接
   */
  private async getOrCreateConnection(target: string): Promise<NetworkConnection> {
    if (this.activeConnections.has(target)) {
      return this.activeConnections.get(target)!;
    }
    
    const connection = new NetworkConnection(target, this.currentProtocol);
    await connection.connect();
    
    this.activeConnections.set(target, connection);
    this.stats.totalConnections++;
    
    return connection;
  }

  /**
   * 初始化协议
   */
  private async initializeProtocol(protocol: NetworkProtocol): Promise<void> {
    if (this.protocolSessions.has(protocol)) {
      return; // 已经初始化
    }
    
    const session = new ProtocolSession(protocol);
    await session.initialize();
    
    this.protocolSessions.set(protocol, session);
  }

  /**
   * 关闭协议连接
   */
  private async closeProtocolConnections(protocol: NetworkProtocol): Promise<void> {
    // 关闭使用该协议的所有连接
    for (const [target, connection] of this.activeConnections) {
      if (connection.protocol === protocol) {
        await connection.disconnect();
        this.activeConnections.delete(target);
      }
    }
    
    // 关闭协议会话
    const session = this.protocolSessions.get(protocol);
    if (session) {
      await session.terminate();
      this.protocolSessions.delete(protocol);
    }
  }

  /**
   * 更新统计信息
   */
  private updateStats(transmission: { successful: boolean; bytesTransferred: number; latencyMs: number }): void {
    if (transmission.successful) {
      this.stats.successfulConnections++;
    } else {
      this.stats.failedConnections++;
    }
    
    this.stats.totalBytesTransferred += transmission.bytesTransferred;
    
    // 更新平均延迟（指数加权移动平均）
    const alpha = 0.1;
    this.stats.averageLatencyMs = 
      this.stats.averageLatencyMs * (1 - alpha) + transmission.latencyMs * alpha;
  }

  /**
   * 获取服务统计信息
   */
  public getStatistics(): NetworkStats {
    return { ...this.stats };
  }

  /**
   * 获取当前协议信息
   */
  public getCurrentProtocolInfo(): ProtocolInfo {
    const capabilities = this.protocolCapabilities.get(this.currentProtocol);
    const quality = this.assessNetworkQuality();
    
    return {
      protocol: this.currentProtocol,
      quality: quality,
      metrics: { ...this.currentMetrics },
      capabilities: capabilities ? { ...capabilities } : undefined,
      stats: this.getStatistics()
    };
  }

  /**
   * 获取网络优化建议
   */
  public getOptimizationSuggestions(): OptimizationSuggestion[] {
    const quality = this.assessNetworkQuality();
    const profile = this.networkProfiles.get(quality);
    const suggestions: OptimizationSuggestion[] = [];
    
    if (profile) {
      suggestions.push({
        priority: 'high',
        title: `网络质量: ${profile.name}`,
        description: `当前网络质量建议使用以下优化策略`,
        actions: profile.optimizationStrategies
      });
    }
    
    // 基于指标的特定建议
    if (this.currentMetrics.latencyMs > 300) {
      suggestions.push({
        priority: 'medium',
        title: '高延迟检测',
        description: `当前延迟 ${this.currentMetrics.latencyMs}ms 较高，建议启用数据压缩和连接复用`,
        actions: ['启用数据压缩', '开启连接复用', '减少请求频率']
      });
    }
    
    if (this.currentMetrics.bandwidthMbps < 1) {
      suggestions.push({
        priority: 'high',
        title: '低带宽警告',
        description: `当前带宽 ${this.currentMetrics.bandwidthMbps.toFixed(2)}Mbps 较低`,
        actions: ['启用数据压缩', '减少媒体质量', '启用离线模式']
      });
    }
    
    if (this.currentMetrics.packetLossRate > 0.05) {
      suggestions.push({
        priority: 'medium',
        title: '丢包率较高',
        description: `当前丢包率 ${(this.currentMetrics.packetLossRate * 100).toFixed(1)}%`,
        actions: ['启用重传机制', '降低数据包大小', '切换到更稳定的协议']
      });
    }
    
    return suggestions;
  }
}

// 辅助类和接口定义

export interface NetworkMessage {
  id: string;
  target: string;
  data?: any;
  type: string;
  priority: number;
  timestamp: number;
  metadata?: Record<string, any>;
}

export interface NetworkResponse {
  success: boolean;
  data?: any;
  latencyMs: number;
  protocol: NetworkProtocol;
  timestamp: number;
  error?: string;
}

export interface NetworkStats {
  totalConnections: number;
  successfulConnections: number;
  failedConnections: number;
  totalBytesTransferred: number;
  protocolSwitches: number;
  averageLatencyMs: number;
}

export interface ProtocolInfo {
  protocol: NetworkProtocol;
  quality: NetworkQuality;
  metrics: NetworkMetrics;
  capabilities?: ProtocolCapabilities;
  stats: NetworkStats;
}

export interface OptimizationSuggestion {
  priority: 'high' | 'medium' | 'low';
  title: string;
  description: string;
  actions: string[];
}

class NetworkConnection {
  constructor(
    public readonly target: string,
    public readonly protocol: NetworkProtocol
  ) {}
  
  async connect(): Promise<void> {
    // 模拟连接建立
    await new Promise(resolve => setTimeout(resolve, 100));
  }
  
  async disconnect(): Promise<void> {
    // 模拟连接关闭
    await new Promise(resolve => setTimeout(resolve, 50));
  }
  
  async send(message: NetworkMessage): Promise<NetworkResponse> {
    // 模拟消息发送
    const latency = Math.random() * 100 + 50; // 50-150ms
    
    await new Promise(resolve => setTimeout(resolve, latency));
    
    return {
      success: true,
      data: { received: message.data },
      latencyMs: latency,
      protocol: this.protocol,
      timestamp: Date.now()
    };
  }
}

class ProtocolSession {
  constructor(public readonly protocol: NetworkProtocol) {}
  
  async initialize(): Promise<void> {
    // 模拟协议初始化
    await new Promise(resolve => setTimeout(resolve, 200));
  }
  
  async terminate(): Promise<void> {
    // 模拟协议终止
    await new Promise(resolve => setTimeout(resolve, 100));
  }
}

// 导出单例实例
export const adaptiveNetworkService = new AdaptiveNetworkService();