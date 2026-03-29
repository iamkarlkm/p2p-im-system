import { QuantumPerformanceMonitor, QuantumPerformanceMetrics, PerformanceThresholds } from './QuantumPerformanceMonitor';
import { QuantumKeyExchange } from './QuantumKeyExchange';
import { QuantumSecureChannel } from './QuantumSecureChannel';

/**
 * 优化策略类型
 */
export type OptimizationStrategy = 
  | 'adaptive_key_rate'
  | 'dynamic_buffer_sizing'
  | 'error_correction_optimization'
  | 'compression_optimization'
  | 'batch_processing'
  | 'predictive_preloading';

/**
 * 优化配置
 */
export interface OptimizationConfig {
  enabled: boolean;
  strategy: OptimizationStrategy;
  priority: number;
  parameters: Record<string, number | boolean | string>;
}

/**
 * 优化结果
 */
export interface OptimizationResult {
  success: boolean;
  strategy: OptimizationStrategy;
  improvements: {
    metric: keyof QuantumPerformanceMetrics;
    before: number;
    after: number;
    improvement: number;
  }[];
  timestamp: number;
  duration: number;
}

/**
 * 自适应配置
 */
export interface AdaptiveConfig {
  autoOptimize: boolean;
  optimizationInterval: number;
  minImprovementThreshold: number;
  maxConcurrentOptimizations: number;
  learningEnabled: boolean;
}

/**
 * 性能瓶颈分析
 */
export interface BottleneckAnalysis {
  bottleneck: 'key_generation' | 'channel_throughput' | 'error_rate' | 'resource_usage' | 'none';
  severity: 'low' | 'medium' | 'high' | 'critical';
  description: string;
  recommendations: string[];
  affectedMetrics: (keyof QuantumPerformanceMetrics)[];
}

/**
 * 量子通信性能优化器
 * 自动优化量子通信系统的性能
 */
export class QuantumPerformanceOptimizer {
  private monitor: QuantumPerformanceMonitor;
  private keyExchange: QuantumKeyExchange;
  private secureChannel: QuantumSecureChannel;
  
  private configs: Map<OptimizationStrategy, OptimizationConfig> = new Map();
  private optimizationHistory: OptimizationResult[] = [];
  private isOptimizing: boolean = false;
  private autoOptimizeInterval: NodeJS.Timeout | null = null;
  
  private adaptiveConfig: AdaptiveConfig;
  private learningData: Map<string, number[]> = new Map();
  
  // 默认配置
  private readonly defaultConfigs: Record<OptimizationStrategy, OptimizationConfig> = {
    adaptive_key_rate: {
      enabled: true,
      strategy: 'adaptive_key_rate',
      priority: 1,
      parameters: {
        minRate: 500,
        maxRate: 10000,
        adjustmentStep: 100,
        targetLatency: 50,
      },
    },
    dynamic_buffer_sizing: {
      enabled: true,
      strategy: 'dynamic_buffer_sizing',
      priority: 2,
      parameters: {
        minBufferSize: 1024,
        maxBufferSize: 65536,
        initialBufferSize: 4096,
        scaleFactor: 2,
      },
    },
    error_correction_optimization: {
      enabled: true,
      strategy: 'error_correction_optimization',
      priority: 3,
      parameters: {
        maxIterations: 10,
        targetErrorRate: 0.01,
        adaptiveStrength: true,
      },
    },
    compression_optimization: {
      enabled: false,
      strategy: 'compression_optimization',
      priority: 4,
      parameters: {
        algorithm: 'lz4',
        level: 3,
        minCompressionRatio: 0.8,
      },
    },
    batch_processing: {
      enabled: true,
      strategy: 'batch_processing',
      priority: 5,
      parameters: {
        maxBatchSize: 100,
        maxBatchDelay: 10,
        minBatchSize: 5,
      },
    },
    predictive_preloading: {
      enabled: false,
      strategy: 'predictive_preloading',
      priority: 6,
      parameters: {
        preloadThreshold: 0.7,
        preloadAmount: 10,
        historyWindow: 100,
      },
    },
  };

  constructor(
    monitor: QuantumPerformanceMonitor,
    keyExchange: QuantumKeyExchange,
    secureChannel: QuantumSecureChannel,
    adaptiveConfig?: Partial<AdaptiveConfig>
  ) {
    this.monitor = monitor;
    this.keyExchange = keyExchange;
    this.secureChannel = secureChannel;
    
    this.adaptiveConfig = {
      autoOptimize: adaptiveConfig?.autoOptimize ?? true,
      optimizationInterval: adaptiveConfig?.optimizationInterval ?? 30000,
      minImprovementThreshold: adaptiveConfig?.minImprovementThreshold ?? 0.05,
      maxConcurrentOptimizations: adaptiveConfig?.maxConcurrentOptimizations ?? 2,
      learningEnabled: adaptiveConfig?.learningEnabled ?? true,
    };
    
    // 初始化配置
    this.initializeConfigs();
    this.setupEventListeners();
  }

  /**
   * 初始化配置
   */
  private initializeConfigs(): void {
    for (const [strategy, config] of Object.entries(this.defaultConfigs)) {
      this.configs.set(strategy as OptimizationStrategy, { ...config });
    }
  }

  /**
   * 设置事件监听器
   */
  private setupEventListeners(): void {
    // 监听性能告警
    this.monitor.on('alert', (alert) => {
      if (this.adaptiveConfig.autoOptimize && alert.type === 'critical') {
        this.triggerEmergencyOptimization(alert);
      }
    });
  }

  /**
   * 开始自动优化
   */
  public startAutoOptimize(): void {
    if (this.autoOptimizeInterval) {
      return;
    }
    
    this.autoOptimizeInterval = setInterval(() => {
      this.runAdaptiveOptimization();
    }, this.adaptiveConfig.optimizationInterval);
  }

  /**
   * 停止自动优化
   */
  public stopAutoOptimize(): void {
    if (this.autoOptimizeInterval) {
      clearInterval(this.autoOptimizeInterval);
      this.autoOptimizeInterval = null;
    }
  }

  /**
   * 运行自适应优化
   */
  private async runAdaptiveOptimization(): Promise<void> {
    if (this.isOptimizing) {
      return;
    }
    
    this.isOptimizing = true;
    
    try {
      // 分析瓶颈
      const bottleneck = this.analyzeBottleneck();
      
      if (bottleneck.bottleneck !== 'none' && bottleneck.severity !== 'low') {
        // 根据瓶颈选择优化策略
        const strategies = this.selectStrategiesForBottleneck(bottleneck);
        
        // 执行优化
        for (const strategy of strategies.slice(0, this.adaptiveConfig.maxConcurrentOptimizations)) {
          await this.optimize(strategy);
        }
      }
    } finally {
      this.isOptimizing = false;
    }
  }

  /**
   * 触发紧急优化
   */
  private async triggerEmergencyOptimization(alert: any): Promise<void> {
    const emergencyStrategies: OptimizationStrategy[] = [
      'adaptive_key_rate',
      'dynamic_buffer_sizing',
      'error_correction_optimization',
    ];
    
    for (const strategy of emergencyStrategies) {
      await this.optimize(strategy);
    }
  }

  /**
   * 执行优化
   */
  public async optimize(strategy: OptimizationStrategy): Promise<OptimizationResult> {
    const config = this.configs.get(strategy);
    if (!config || !config.enabled) {
      return {
        success: false,
        strategy,
        improvements: [],
        timestamp: Date.now(),
        duration: 0,
      };
    }
    
    const startTime = Date.now();
    const beforeMetrics = this.monitor.getCurrentMetrics();
    
    try {
      // 执行具体的优化策略
      switch (strategy) {
        case 'adaptive_key_rate':
          await this.optimizeAdaptiveKeyRate(config);
          break;
        case 'dynamic_buffer_sizing':
          await this.optimizeDynamicBufferSizing(config);
          break;
        case 'error_correction_optimization':
          await this.optimizeErrorCorrection(config);
          break;
        case 'compression_optimization':
          await this.optimizeCompression(config);
          break;
        case 'batch_processing':
          await this.optimizeBatchProcessing(config);
          break;
        case 'predictive_preloading':
          await this.optimizePredictivePreloading(config);
          break;
      }
      
      // 等待优化生效
      await this.delay(1000);
      
      const afterMetrics = this.monitor.getCurrentMetrics();
      const duration = Date.now() - startTime;
      
      // 计算改进
      const improvements = this.calculateImprovements(beforeMetrics, afterMetrics);
      
      const result: OptimizationResult = {
        success: improvements.length > 0,
        strategy,
        improvements,
        timestamp: Date.now(),
        duration,
      };
      
      // 记录优化历史
      this.optimizationHistory.push(result);
      
      // 学习优化效果
      if (this.adaptiveConfig.learningEnabled) {
        this.learnFromOptimization(result);
      }
      
      // 限制历史记录大小
      if (this.optimizationHistory.length > 1000) {
        this.optimizationHistory.shift();
      }
      
      return result;
    } catch (error) {
      return {
        success: false,
        strategy,
        improvements: [],
        timestamp: Date.now(),
        duration: Date.now() - startTime,
      };
    }
  }

  /**
   * 自适应密钥速率优化
   */
  private async optimizeAdaptiveKeyRate(config: OptimizationConfig): Promise<void> {
    const metrics = this.monitor.getCurrentMetrics();
    const { targetLatency, adjustmentStep, minRate, maxRate } = config.parameters;
    
    // 根据延迟调整密钥生成速率
    if (metrics.keyGenerationLatency > (targetLatency as number)) {
      // 延迟过高，降低速率
      const newRate = Math.max(minRate as number, metrics.keyGenerationRate - (adjustmentStep as number));
      this.keyExchange.setKeyGenerationRate?.(newRate);
    } else if (metrics.keyGenerationLatency < (targetLatency as number) * 0.5) {
      // 延迟过低，可以提升速率
      const newRate = Math.min(maxRate as number, metrics.keyGenerationRate + (adjustmentStep as number));
      this.keyExchange.setKeyGenerationRate?.(newRate);
    }
  }

  /**
   * 动态缓冲区大小优化
   */
  private async optimizeDynamicBufferSizing(config: OptimizationConfig): Promise<void> {
    const metrics = this.monitor.getCurrentMetrics();
    const { minBufferSize, maxBufferSize, scaleFactor } = config.parameters;
    
    // 根据吞吐量调整缓冲区大小
    if (metrics.channelThroughput < 5) {
      // 吞吐量低，减小缓冲区
      const newSize = Math.max(minBufferSize as number, 
        (config.parameters.currentBufferSize as number || maxBufferSize as number) / (scaleFactor as number));
      this.secureChannel.setBufferSize?.(Math.floor(newSize));
      config.parameters.currentBufferSize = newSize;
    } else if (metrics.channelThroughput > 50) {
      // 吞吐量高，增大缓冲区
      const newSize = Math.min(maxBufferSize as number,
        (config.parameters.currentBufferSize as number || minBufferSize as number) * (scaleFactor as number));
      this.secureChannel.setBufferSize?.(Math.floor(newSize));
      config.parameters.currentBufferSize = newSize;
    }
  }

  /**
   * 错误纠正优化
   */
  private async optimizeErrorCorrection(config: OptimizationConfig): Promise<void> {
    const metrics = this.monitor.getCurrentMetrics();
    const { targetErrorRate, maxIterations, adaptiveStrength } = config.parameters;
    
    if (adaptiveStrength) {
      if (metrics.channelErrorRate > (targetErrorRate as number)) {
        // 错误率高，增强纠错
        const newIterations = Math.min(maxIterations as number, 
          (config.parameters.currentIterations as number || 5) + 1);
        this.secureChannel.setErrorCorrectionStrength?.(newIterations);
        config.parameters.currentIterations = newIterations;
      } else if (metrics.channelErrorRate < (targetErrorRate as number) * 0.5) {
        // 错误率低，减弱纠错以提升性能
        const newIterations = Math.max(1,
          (config.parameters.currentIterations as number || 5) - 1);
        this.secureChannel.setErrorCorrectionStrength?.(newIterations);
        config.parameters.currentIterations = newIterations;
      }
    }
  }

  /**
   * 压缩优化
   */
  private async optimizeCompression(config: OptimizationConfig): Promise<void> {
    const { algorithm, level } = config.parameters;
    this.secureChannel.setCompressionOptions?.({
      algorithm: algorithm as string,
      level: level as number,
    });
  }

  /**
   * 批处理优化
   */
  private async optimizeBatchProcessing(config: OptimizationConfig): Promise<void> {
    const metrics = this.monitor.getCurrentMetrics();
    const { maxBatchSize, maxBatchDelay, minBatchSize } = config.parameters;
    
    // 根据信道延迟调整批处理参数
    if (metrics.channelLatency > 50) {
      // 延迟高，减小批次大小，增加批次频率
      this.secureChannel.setBatchOptions?.({
        maxSize: Math.max(minBatchSize as number, (maxBatchSize as number) / 2),
        maxDelay: Math.max(1, (maxBatchDelay as number) / 2),
      });
    } else {
      // 延迟低，可以增大批次
      this.secureChannel.setBatchOptions?.({
        maxSize: maxBatchSize as number,
        maxDelay: maxBatchDelay as number,
      });
    }
  }

  /**
   * 预测性预加载优化
   */
  private async optimizePredictivePreloading(config: OptimizationConfig): Promise<void> {
    const { preloadThreshold, preloadAmount, historyWindow } = config.parameters;
    
    // 基于历史数据预测并预加载
    const history = this.monitor.getHistory((historyWindow as number) * 1000);
    if (history.metrics.length > 10) {
      const recentMetrics = history.metrics.slice(-10);
      const avgKeyRate = recentMetrics.reduce((sum, m) => sum + m.keyGenerationRate, 0) / recentMetrics.length;
      const thresholdRate = (preloadThreshold as number) * 10000; // 假设最大10000
      
      if (avgKeyRate > thresholdRate) {
        // 预加载密钥
        this.keyExchange.preloadKeys?.(preloadAmount as number);
      }
    }
  }

  /**
   * 计算改进
   */
  private calculateImprovements(
    before: QuantumPerformanceMetrics,
    after: QuantumPerformanceMetrics
  ): OptimizationResult['improvements'] {
    const improvements: OptimizationResult['improvements'] = [];
    
    const metricsToCompare: (keyof QuantumPerformanceMetrics)[] = [
      'keyGenerationRate',
      'keyGenerationLatency',
      'channelThroughput',
      'channelLatency',
      'channelErrorRate',
      'channelStability',
    ];
    
    for (const metric of metricsToCompare) {
      const beforeValue = before[metric];
      const afterValue = after[metric];
      
      if (typeof beforeValue === 'number' && typeof afterValue === 'number') {
        let improvement: number;
        
        // 对于延迟和错误率，越低越好
        if (metric === 'keyGenerationLatency' || metric === 'channelLatency' || metric === 'channelErrorRate') {
          improvement = (beforeValue - afterValue) / (beforeValue || 1);
        } else {
          // 对于速率、稳定性等，越高越好
          improvement = (afterValue - beforeValue) / (beforeValue || 1);
        }
        
        // 只记录显著的改进
        if (Math.abs(improvement) >= this.adaptiveConfig.minImprovementThreshold) {
          improvements.push({
            metric,
            before: Math.round(beforeValue * 100) / 100,
            after: Math.round(afterValue * 100) / 100,
            improvement: Math.round(improvement * 100) / 100,
          });
        }
      }
    }
    
    return improvements;
  }

  /**
   * 从优化中学习
   */
  private learnFromOptimization(result: OptimizationResult): void {
    if (!result.success) {
      return;
    }
    
    const key = `${result.strategy}`;
    const history = this.learningData.get(key) || [];
    history.push(result.improvements.length);
    this.learningData.set(key, history);
    
    // 如果某个策略连续失败，禁用它
    if (history.length >= 10) {
      const recentResults = history.slice(-10);
      const successCount = recentResults.filter(r => r > 0).length;
      
      if (successCount < 3) {
        // 成功率低于30%，禁用该策略
        const config = this.configs.get(result.strategy);
        if (config) {
          config.enabled = false;
        }
      }
    }
  }

  /**
   * 分析瓶颈
   */
  public analyzeBottleneck(): BottleneckAnalysis {
    const metrics = this.monitor.getCurrentMetrics();
    const thresholds = this.monitor.getThresholds();
    
    // 检查各个方面的性能
    const issues: { type: BottleneckAnalysis['bottleneck']; severity: BottleneckAnalysis['severity']; score: number }[] = [];
    
    // 密钥生成问题
    if (metrics.keyGenerationRate < thresholds.minKeyGenerationRate * 0.5) {
      issues.push({ type: 'key_generation', severity: 'critical', score: 100 });
    } else if (metrics.keyGenerationRate < thresholds.minKeyGenerationRate) {
      issues.push({ type: 'key_generation', severity: 'high', score: 70 });
    } else if (metrics.keyGenerationLatency > thresholds.maxKeyGenerationLatency * 2) {
      issues.push({ type: 'key_generation', severity: 'high', score: 80 });
    }
    
    // 信道吞吐量问题
    if (metrics.channelThroughput < thresholds.minChannelThroughput * 0.3) {
      issues.push({ type: 'channel_throughput', severity: 'critical', score: 100 });
    } else if (metrics.channelThroughput < thresholds.minChannelThroughput * 0.7) {
      issues.push({ type: 'channel_throughput', severity: 'high', score: 75 });
    }
    
    // 错误率问题
    if (metrics.channelErrorRate > thresholds.maxChannelErrorRate * 5) {
      issues.push({ type: 'error_rate', severity: 'critical', score: 95 });
    } else if (metrics.channelErrorRate > thresholds.maxChannelErrorRate * 2) {
      issues.push({ type: 'error_rate', severity: 'high', score: 70 });
    }
    
    // 资源使用问题
    if (metrics.cpuUsage > 90 || metrics.memoryUsage > 1024) {
      issues.push({ type: 'resource_usage', severity: 'high', score: 80 });
    } else if (metrics.cpuUsage > 80 || metrics.memoryUsage > 700) {
      issues.push({ type: 'resource_usage', severity: 'medium', score: 50 });
    }
    
    if (issues.length === 0) {
      return {
        bottleneck: 'none',
        severity: 'low',
        description: '系统运行正常，未发现性能瓶颈',
        recommendations: ['继续保持当前配置'],
        affectedMetrics: [],
      };
    }
    
    // 找出最严重的问题
    const worstIssue = issues.sort((a, b) => b.score - a.score)[0];
    
    // 生成推荐
    const recommendations = this.generateRecommendations(worstIssue.type);
    
    // 获取受影响的指标
    const affectedMetrics = this.getAffectedMetrics(worstIssue.type);
    
    return {
      bottleneck: worstIssue.type,
      severity: worstIssue.severity,
      description: this.getBottleneckDescription(worstIssue.type),
      recommendations,
      affectedMetrics,
    };
  }

  /**
   * 为瓶颈生成推荐
   */
  private generateRecommendations(bottleneck: BottleneckAnalysis['bottleneck']): string[] {
    const recommendations: Record<BottleneckAnalysis['bottleneck'], string[]> = {
      key_generation: [
        '启用自适应密钥速率优化',
        '检查量子密钥分发设备状态',
        '考虑降低密钥生成频率以提升稳定性',
        '检查网络连接质量',
      ],
      channel_throughput: [
        '启用动态缓冲区大小优化',
        '增加批处理大小',
        '检查网络带宽限制',
        '考虑启用数据压缩',
      ],
      error_rate: [
        '启用错误纠正优化',
        '增强错误纠正强度',
        '检查信道干扰源',
        '考虑切换到更稳定的信道',
      ],
      resource_usage: [
        '降低批处理频率',
        '减少并发优化任务数',
        '考虑关闭非关键功能',
        '检查内存泄漏',
      ],
      none: [],
    };
    
    return recommendations[bottleneck] || [];
  }

  /**
   * 获取瓶颈描述
   */
  private getBottleneckDescription(bottleneck: BottleneckAnalysis['bottleneck']): string {
    const descriptions: Record<BottleneckAnalysis['bottleneck'], string> = {
      key_generation: '量子密钥生成速率过低或延迟过高',
      channel_throughput: '量子安全信道吞吐量不足',
      error_rate: '信道误码率超出可接受范围',
      resource_usage: '系统资源使用率过高',
      none: '',
    };
    
    return descriptions[bottleneck];
  }

  /**
   * 获取受影响的指标
   */
  private getAffectedMetrics(bottleneck: BottleneckAnalysis['bottleneck']): (keyof QuantumPerformanceMetrics)[] {
    const mapping: Record<BottleneckAnalysis['bottleneck'], (keyof QuantumPerformanceMetrics)[]> = {
      key_generation: ['keyGenerationRate', 'keyGenerationLatency', 'keyGenerationSuccessRate'],
      channel_throughput: ['channelThroughput', 'channelLatency'],
      error_rate: ['channelErrorRate', 'channelStability'],
      resource_usage: ['cpuUsage', 'memoryUsage'],
      none: [],
    };
    
    return mapping[bottleneck] || [];
  }

  /**
   * 为瓶颈选择优化策略
   */
  private selectStrategiesForBottleneck(bottleneck: BottleneckAnalysis): OptimizationStrategy[] {
    const mapping: Record<BottleneckAnalysis['bottleneck'], OptimizationStrategy[]> = {
      key_generation: ['adaptive_key_rate', 'predictive_preloading'],
      channel_throughput: ['dynamic_buffer_sizing', 'batch_processing', 'compression_optimization'],
      error_rate: ['error_correction_optimization'],
      resource_usage: ['batch_processing'],
      none: [],
    };
    
    return mapping[bottleneck.bottleneck] || [];
  }

  /**
   * 获取优化配置
   */
  public getConfig(strategy: OptimizationStrategy): OptimizationConfig | undefined {
    return this.configs.get(strategy);
  }

  /**
   * 更新优化配置
   */
  public updateConfig(strategy: OptimizationStrategy, config: Partial<OptimizationConfig>): void {
    const existing = this.configs.get(strategy);
    if (existing) {
      this.configs.set(strategy, { ...existing, ...config });
    }
  }

  /**
   * 获取所有配置
   */
  public getAllConfigs(): OptimizationConfig[] {
    return Array.from(this.configs.values());
  }

  /**
   * 获取优化历史
   */
  public getOptimizationHistory(limit?: number): OptimizationResult[] {
    const history = [...this.optimizationHistory];
    if (limit) {
      return history.slice(-limit);
    }
    return history;
  }

  /**
   * 延迟
   */
  private delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  /**
   * 销毁优化器
   */
  public destroy(): void {
    this.stopAutoOptimize();
    this.configs.clear();
    this.optimizationHistory = [];
    this.learningData.clear();
  }
}

export default QuantumPerformanceOptimizer;
