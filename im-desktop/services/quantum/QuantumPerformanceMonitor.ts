import { EventEmitter } from 'events';
import { QuantumKeyExchange } from './QuantumKeyExchange';
import { QuantumSecureChannel } from './QuantumSecureChannel';

/**
 * 量子通信性能监控器
 * 实时监控量子通信的各项性能指标
 */
export interface QuantumPerformanceMetrics {
  // 密钥生成性能
  keyGenerationRate: number;        // 密钥生成速率 (bits/s)
  keyGenerationLatency: number;     // 密钥生成延迟 (ms)
  keyGenerationSuccessRate: number; // 密钥生成成功率 (%)
  
  // 信道性能
  channelThroughput: number;        // 信道吞吐量 (Mbps)
  channelLatency: number;           // 信道延迟 (ms)
  channelErrorRate: number;         // 信道误码率 (%)
  channelStability: number;         // 信道稳定性评分 (0-100)
  
  // QKD性能
  qkdKeyRate: number;               // QKD密钥速率 (kbps)
  qkdQuantumBitErrorRate: number;   // QKD量子误码率 (%)
  qkdSecureKeyRate: number;         // QKD安全密钥速率 (kbps)
  
  // 系统资源
  cpuUsage: number;                 // CPU使用率 (%)
  memoryUsage: number;              // 内存使用 (MB)
  networkUsage: number;             // 网络使用率 (%)
  
  // 时间戳
  timestamp: number;
}

/**
 * 性能阈值配置
 */
export interface PerformanceThresholds {
  minKeyGenerationRate: number;
  maxKeyGenerationLatency: number;
  maxChannelErrorRate: number;
  minChannelThroughput: number;
  maxCpuUsage: number;
  maxMemoryUsage: number;
}

/**
 * 性能告警
 */
export interface PerformanceAlert {
  id: string;
  type: 'warning' | 'critical' | 'info';
  metric: keyof QuantumPerformanceMetrics;
  value: number;
  threshold: number;
  message: string;
  timestamp: number;
  acknowledged: boolean;
}

/**
 * 性能历史记录
 */
export interface PerformanceHistory {
  metrics: QuantumPerformanceMetrics[];
  startTime: number;
  endTime: number;
  interval: number;
}

export class QuantumPerformanceMonitor extends EventEmitter {
  private metrics: QuantumPerformanceMetrics;
  private history: QuantumPerformanceMetrics[] = [];
  private alerts: PerformanceAlert[] = [];
  private thresholds: PerformanceThresholds;
  private monitoringInterval: NodeJS.Timeout | null = null;
  private readonly maxHistorySize = 10000;
  private readonly defaultInterval = 1000; // 1秒
  
  private keyExchange: QuantumKeyExchange;
  private secureChannel: QuantumSecureChannel;

  constructor(
    keyExchange: QuantumKeyExchange,
    secureChannel: QuantumSecureChannel,
    thresholds?: Partial<PerformanceThresholds>
  ) {
    super();
    this.keyExchange = keyExchange;
    this.secureChannel = secureChannel;
    
    this.thresholds = {
      minKeyGenerationRate: thresholds?.minKeyGenerationRate ?? 1000,
      maxKeyGenerationLatency: thresholds?.maxKeyGenerationLatency ?? 100,
      maxChannelErrorRate: thresholds?.maxChannelErrorRate ?? 0.1,
      minChannelThroughput: thresholds?.minChannelThroughput ?? 10,
      maxCpuUsage: thresholds?.maxCpuUsage ?? 80,
      maxMemoryUsage: thresholds?.maxMemoryUsage ?? 512,
    };
    
    this.metrics = this.initializeMetrics();
    this.setupEventListeners();
  }

  /**
   * 初始化性能指标
   */
  private initializeMetrics(): QuantumPerformanceMetrics {
    return {
      keyGenerationRate: 0,
      keyGenerationLatency: 0,
      keyGenerationSuccessRate: 100,
      channelThroughput: 0,
      channelLatency: 0,
      channelErrorRate: 0,
      channelStability: 100,
      qkdKeyRate: 0,
      qkdQuantumBitErrorRate: 0,
      qkdSecureKeyRate: 0,
      cpuUsage: 0,
      memoryUsage: 0,
      networkUsage: 0,
      timestamp: Date.now(),
    };
  }

  /**
   * 设置事件监听器
   */
  private setupEventListeners(): void {
    // 监听密钥交换事件
    this.keyExchange.on('keyGenerated', (data: { latency: number; success: boolean }) => {
      this.updateKeyGenerationMetrics(data);
    });
    
    // 监听安全信道事件
    this.secureChannel.on('dataTransmitted', (data: { 
      bytes: number; 
      latency: number; 
      errors: number;
    }) => {
      this.updateChannelMetrics(data);
    });
    
    this.secureChannel.on('error', () => {
      this.incrementErrorCount();
    });
  }

  /**
   * 开始性能监控
   */
  public startMonitoring(interval: number = this.defaultInterval): void {
    if (this.monitoringInterval) {
      return;
    }
    
    this.monitoringInterval = setInterval(() => {
      this.collectMetrics();
    }, interval);
    
    this.emit('monitoringStarted', { interval });
  }

  /**
   * 停止性能监控
   */
  public stopMonitoring(): void {
    if (this.monitoringInterval) {
      clearInterval(this.monitoringInterval);
      this.monitoringInterval = null;
      this.emit('monitoringStopped');
    }
  }

  /**
   * 收集性能指标
   */
  private collectMetrics(): void {
    const newMetrics: QuantumPerformanceMetrics = {
      ...this.metrics,
      timestamp: Date.now(),
    };
    
    // 收集系统资源指标
    this.collectSystemMetrics(newMetrics);
    
    // 收集QKD指标
    this.collectQKDMetrics(newMetrics);
    
    // 更新指标
    this.metrics = newMetrics;
    
    // 添加到历史记录
    this.addToHistory(newMetrics);
    
    // 检查阈值
    this.checkThresholds(newMetrics);
    
    // 发出指标更新事件
    this.emit('metricsUpdated', newMetrics);
  }

  /**
   * 收集系统指标
   */
  private collectSystemMetrics(metrics: QuantumPerformanceMetrics): void {
    // 使用Electron的process API获取资源使用
    if (process.memoryUsage) {
      const memUsage = process.memoryUsage();
      metrics.memoryUsage = Math.round(memUsage.heapUsed / 1024 / 1024);
    }
    
    // CPU使用率估算（基于事件循环延迟）
    const start = process.hrtime();
    setImmediate(() => {
      const diff = process.hrtime(start);
      const delay = diff[0] * 1000 + diff[1] / 1e6;
      metrics.cpuUsage = Math.min(100, Math.round(delay / 10));
    });
  }

  /**
   * 收集QKD指标
   */
  private collectQKDMetrics(metrics: QuantumPerformanceMetrics): void {
    // 从密钥交换模块获取QKD统计
    const qkdStats = this.keyExchange.getQKDStats?.();
    if (qkdStats) {
      metrics.qkdKeyRate = qkdStats.keyRate;
      metrics.qkdQuantumBitErrorRate = qkdStats.quantumBitErrorRate;
      metrics.qkdSecureKeyRate = qkdStats.secureKeyRate;
    }
  }

  /**
   * 更新密钥生成指标
   */
  private updateKeyGenerationMetrics(data: { latency: number; success: boolean }): void {
    const now = Date.now();
    const timeDiff = now - this.metrics.timestamp;
    
    if (timeDiff > 0) {
      // 估算密钥生成速率（假设每次生成256位密钥）
      const bitsGenerated = data.success ? 256 : 0;
      this.metrics.keyGenerationRate = Math.round((bitsGenerated / timeDiff) * 1000);
    }
    
    this.metrics.keyGenerationLatency = data.latency;
    
    // 更新成功率（使用滑动窗口平均）
    const currentSuccess = data.success ? 1 : 0;
    this.metrics.keyGenerationSuccessRate = 
      (this.metrics.keyGenerationSuccessRate * 0.9) + (currentSuccess * 10);
  }

  /**
   * 更新信道指标
   */
  private updateChannelMetrics(data: { 
    bytes: number; 
    latency: number; 
    errors: number;
  }): void {
    const now = Date.now();
    const timeDiff = now - this.metrics.timestamp;
    
    if (timeDiff > 0) {
      // 计算吞吐量（Mbps）
      const bitsTransferred = data.bytes * 8;
      this.metrics.channelThroughput = (bitsTransferred / timeDiff) * 1000 / 1024 / 1024;
    }
    
    this.metrics.channelLatency = data.latency;
    
    // 计算误码率
    const totalBits = data.bytes * 8;
    if (totalBits > 0) {
      this.metrics.channelErrorRate = (data.errors / totalBits) * 100;
    }
    
    // 更新信道稳定性
    const latencyFactor = Math.max(0, 1 - (data.latency / 100));
    const errorFactor = Math.max(0, 1 - (this.metrics.channelErrorRate / 1));
    this.metrics.channelStability = Math.round((latencyFactor * 0.5 + errorFactor * 0.5) * 100);
  }

  /**
   * 增加错误计数
   */
  private incrementErrorCount(): void {
    // 信道稳定性下降
    this.metrics.channelStability = Math.max(0, this.metrics.channelStability - 5);
  }

  /**
   * 添加到历史记录
   */
  private addToHistory(metrics: QuantumPerformanceMetrics): void {
    this.history.push({ ...metrics });
    
    // 限制历史记录大小
    if (this.history.length > this.maxHistorySize) {
      this.history.shift();
    }
  }

  /**
   * 检查阈值
   */
  private checkThresholds(metrics: QuantumPerformanceMetrics): void {
    // 检查密钥生成速率
    if (metrics.keyGenerationRate < this.thresholds.minKeyGenerationRate) {
      this.createAlert('warning', 'keyGenerationRate', metrics.keyGenerationRate, 
        this.thresholds.minKeyGenerationRate, 
        `密钥生成速率过低: ${metrics.keyGenerationRate} bits/s`);
    }
    
    // 检查密钥生成延迟
    if (metrics.keyGenerationLatency > this.thresholds.maxKeyGenerationLatency) {
      this.createAlert('critical', 'keyGenerationLatency', metrics.keyGenerationLatency,
        this.thresholds.maxKeyGenerationLatency,
        `密钥生成延迟过高: ${metrics.keyGenerationLatency} ms`);
    }
    
    // 检查信道误码率
    if (metrics.channelErrorRate > this.thresholds.maxChannelErrorRate) {
      this.createAlert('critical', 'channelErrorRate', metrics.channelErrorRate,
        this.thresholds.maxChannelErrorRate,
        `信道误码率过高: ${metrics.channelErrorRate.toFixed(2)}%`);
    }
    
    // 检查信道吞吐量
    if (metrics.channelThroughput < this.thresholds.minChannelThroughput) {
      this.createAlert('warning', 'channelThroughput', metrics.channelThroughput,
        this.thresholds.minChannelThroughput,
        `信道吞吐量过低: ${metrics.channelThroughput.toFixed(2)} Mbps`);
    }
    
    // 检查CPU使用率
    if (metrics.cpuUsage > this.thresholds.maxCpuUsage) {
      this.createAlert('warning', 'cpuUsage', metrics.cpuUsage,
        this.thresholds.maxCpuUsage,
        `CPU使用率过高: ${metrics.cpuUsage}%`);
    }
    
    // 检查内存使用
    if (metrics.memoryUsage > this.thresholds.maxMemoryUsage) {
      this.createAlert('critical', 'memoryUsage', metrics.memoryUsage,
        this.thresholds.maxMemoryUsage,
        `内存使用过高: ${metrics.memoryUsage} MB`);
    }
  }

  /**
   * 创建告警
   */
  private createAlert(
    type: PerformanceAlert['type'],
    metric: keyof QuantumPerformanceMetrics,
    value: number,
    threshold: number,
    message: string
  ): void {
    // 检查是否已存在相同告警
    const existingAlert = this.alerts.find(a => 
      a.metric === metric && !a.acknowledged && a.type === type
    );
    
    if (existingAlert) {
      // 更新现有告警
      existingAlert.value = value;
      existingAlert.timestamp = Date.now();
      return;
    }
    
    const alert: PerformanceAlert = {
      id: `alert-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
      type,
      metric,
      value,
      threshold,
      message,
      timestamp: Date.now(),
      acknowledged: false,
    };
    
    this.alerts.push(alert);
    this.emit('alert', alert);
    
    // 限制告警数量
    if (this.alerts.length > 100) {
      this.alerts.shift();
    }
  }

  /**
   * 获取当前指标
   */
  public getCurrentMetrics(): QuantumPerformanceMetrics {
    return { ...this.metrics };
  }

  /**
   * 获取历史记录
   */
  public getHistory(duration?: number): PerformanceHistory {
    let metrics = this.history;
    
    if (duration) {
      const cutoff = Date.now() - duration;
      metrics = this.history.filter(m => m.timestamp >= cutoff);
    }
    
    return {
      metrics,
      startTime: metrics[0]?.timestamp ?? Date.now(),
      endTime: metrics[metrics.length - 1]?.timestamp ?? Date.now(),
      interval: this.defaultInterval,
    };
  }

  /**
   * 获取告警列表
   */
  public getAlerts(acknowledged?: boolean): PerformanceAlert[] {
    if (acknowledged === undefined) {
      return [...this.alerts];
    }
    return this.alerts.filter(a => a.acknowledged === acknowledged);
  }

  /**
   * 确认告警
   */
  public acknowledgeAlert(alertId: string): boolean {
    const alert = this.alerts.find(a => a.id === alertId);
    if (alert) {
      alert.acknowledged = true;
      this.emit('alertAcknowledged', alert);
      return true;
    }
    return false;
  }

  /**
   * 清除告警
   */
  public clearAlerts(): void {
    this.alerts = [];
    this.emit('alertsCleared');
  }

  /**
   * 更新阈值
   */
  public updateThresholds(thresholds: Partial<PerformanceThresholds>): void {
    this.thresholds = { ...this.thresholds, ...thresholds };
    this.emit('thresholdsUpdated', this.thresholds);
  }

  /**
   * 获取阈值
   */
  public getThresholds(): PerformanceThresholds {
    return { ...this.thresholds };
  }

  /**
   * 获取性能报告
   */
  public getPerformanceReport(duration: number = 3600000): {
    summary: {
      avgKeyGenerationRate: number;
      avgChannelThroughput: number;
      avgChannelLatency: number;
      avgChannelStability: number;
      totalAlerts: number;
      criticalAlerts: number;
    };
    trends: {
      keyGenerationTrend: 'up' | 'down' | 'stable';
      throughputTrend: 'up' | 'down' | 'stable';
      stabilityTrend: 'up' | 'down' | 'stable';
    };
  } {
    const history = this.getHistory(duration);
    const metrics = history.metrics;
    
    if (metrics.length === 0) {
      return {
        summary: {
          avgKeyGenerationRate: 0,
          avgChannelThroughput: 0,
          avgChannelLatency: 0,
          avgChannelStability: 0,
          totalAlerts: 0,
          criticalAlerts: 0,
        },
        trends: {
          keyGenerationTrend: 'stable',
          throughputTrend: 'stable',
          stabilityTrend: 'stable',
        },
      };
    }
    
    // 计算平均值
    const avgKeyGenerationRate = metrics.reduce((sum, m) => sum + m.keyGenerationRate, 0) / metrics.length;
    const avgChannelThroughput = metrics.reduce((sum, m) => sum + m.channelThroughput, 0) / metrics.length;
    const avgChannelLatency = metrics.reduce((sum, m) => sum + m.channelLatency, 0) / metrics.length;
    const avgChannelStability = metrics.reduce((sum, m) => sum + m.channelStability, 0) / metrics.length;
    
    // 计算告警统计
    const relevantAlerts = this.alerts.filter(a => a.timestamp >= Date.now() - duration);
    const criticalAlerts = relevantAlerts.filter(a => a.type === 'critical').length;
    
    // 计算趋势（比较前半段和后半段）
    const midPoint = Math.floor(metrics.length / 2);
    const firstHalf = metrics.slice(0, midPoint);
    const secondHalf = metrics.slice(midPoint);
    
    const calculateTrend = (first: number, second: number): 'up' | 'down' | 'stable' => {
      const diff = second - first;
      const threshold = first * 0.05; // 5%变化视为显著
      if (diff > threshold) return 'up';
      if (diff < -threshold) return 'down';
      return 'stable';
    };
    
    const firstKeyRate = firstHalf.reduce((sum, m) => sum + m.keyGenerationRate, 0) / firstHalf.length || 0;
    const secondKeyRate = secondHalf.reduce((sum, m) => sum + m.keyGenerationRate, 0) / secondHalf.length || 0;
    
    const firstThroughput = firstHalf.reduce((sum, m) => sum + m.channelThroughput, 0) / firstHalf.length || 0;
    const secondThroughput = secondHalf.reduce((sum, m) => sum + m.channelThroughput, 0) / secondHalf.length || 0;
    
    const firstStability = firstHalf.reduce((sum, m) => sum + m.channelStability, 0) / firstHalf.length || 0;
    const secondStability = secondHalf.reduce((sum, m) => sum + m.channelStability, 0) / secondHalf.length || 0;
    
    return {
      summary: {
        avgKeyGenerationRate: Math.round(avgKeyGenerationRate),
        avgChannelThroughput: Math.round(avgChannelThroughput * 100) / 100,
        avgChannelLatency: Math.round(avgChannelLatency),
        avgChannelStability: Math.round(avgChannelStability),
        totalAlerts: relevantAlerts.length,
        criticalAlerts,
      },
      trends: {
        keyGenerationTrend: calculateTrend(firstKeyRate, secondKeyRate),
        throughputTrend: calculateTrend(firstThroughput, secondThroughput),
        stabilityTrend: calculateTrend(firstStability, secondStability),
      },
    };
  }

  /**
   * 销毁监控器
   */
  public destroy(): void {
    this.stopMonitoring();
    this.removeAllListeners();
    this.history = [];
    this.alerts = [];
  }
}

export default QuantumPerformanceMonitor;
