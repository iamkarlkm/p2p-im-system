import { useState, useEffect, useCallback, useRef } from 'react';
import { QuantumPerformanceMonitor, QuantumPerformanceMetrics, PerformanceAlert, PerformanceHistory } from '../services/quantum/QuantumPerformanceMonitor';
import { QuantumPerformanceOptimizer, OptimizationResult, OptimizationStrategy } from '../services/quantum/QuantumPerformanceOptimizer';
import { QuantumKeyExchange } from '../services/quantum/QuantumKeyExchange';
import { QuantumSecureChannel } from '../services/quantum/QuantumSecureChannel';

/**
 * 量子性能Hook的返回类型
 */
interface UseQuantumPerformanceReturn {
  // 当前状态
  metrics: QuantumPerformanceMetrics | null;
  history: PerformanceHistory | null;
  alerts: PerformanceAlert[];
  isMonitoring: boolean;
  bottleneck: BottleneckAnalysis | null;
  optimizationResults: OptimizationResult[];
  
  // 操作方法
  startMonitoring: () => void;
  stopMonitoring: () => void;
  acknowledgeAlert: (alertId: string) => void;
  runOptimization: (strategy: OptimizationStrategy) => Promise<void>;
  getPerformanceReport: (duration?: number) => any;
  
  // 加载状态
  isLoading: boolean;
  error: Error | null;
}

/**
 * 瓶颈分析类型
 */
interface BottleneckAnalysis {
  bottleneck: 'key_generation' | 'channel_throughput' | 'error_rate' | 'resource_usage' | 'none';
  severity: 'low' | 'medium' | 'high' | 'critical';
  description: string;
  recommendations: string[];
}

/**
 * 量子性能监控Hook
 * 
 * 使用示例:
 * ```tsx
 * const { metrics, alerts, startMonitoring } = useQuantumPerformance();
 * ```
 */
export const useQuantumPerformance = (): UseQuantumPerformanceReturn => {
  // 状态
  const [metrics, setMetrics] = useState<QuantumPerformanceMetrics | null>(null);
  const [history, setHistory] = useState<PerformanceHistory | null>(null);
  const [alerts, setAlerts] = useState<PerformanceAlert[]>([]);
  const [isMonitoring, setIsMonitoring] = useState(false);
  const [bottleneck, setBottleneck] = useState<BottleneckAnalysis | null>(null);
  const [optimizationResults, setOptimizationResults] = useState<OptimizationResult[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  // Refs用于存储实例
  const monitorRef = useRef<QuantumPerformanceMonitor | null>(null);
  const optimizerRef = useRef<QuantumPerformanceOptimizer | null>(null);
  const keyExchangeRef = useRef<QuantumKeyExchange | null>(null);
  const secureChannelRef = useRef<QuantumSecureChannel | null>(null);

  /**
   * 初始化服务和实例
   */
  useEffect(() => {
    try {
      // 创建量子密钥交换实例（如果不存在）
      if (!keyExchangeRef.current) {
        keyExchangeRef.current = new QuantumKeyExchange();
      }
      
      // 创建量子安全信道实例（如果不存在）
      if (!secureChannelRef.current) {
        secureChannelRef.current = new QuantumSecureChannel();
      }
      
      // 创建性能监控器
      if (!monitorRef.current && keyExchangeRef.current && secureChannelRef.current) {
        monitorRef.current = new QuantumPerformanceMonitor(
          keyExchangeRef.current,
          secureChannelRef.current
        );
        
        // 设置事件监听器
        setupEventListeners();
      }
      
      // 创建性能优化器
      if (!optimizerRef.current && monitorRef.current && keyExchangeRef.current && secureChannelRef.current) {
        optimizerRef.current = new QuantumPerformanceOptimizer(
          monitorRef.current,
          keyExchangeRef.current,
          secureChannelRef.current
        );
      }
    } catch (err) {
      setError(err instanceof Error ? err : new Error('初始化失败'));
    }
    
    // 清理函数
    return () => {
      if (monitorRef.current) {
        monitorRef.current.destroy();
        monitorRef.current = null;
      }
      if (optimizerRef.current) {
        optimizerRef.current.destroy();
        optimizerRef.current = null;
      }
    };
  }, []);

  /**
   * 设置事件监听器
   */
  const setupEventListeners = () => {
    const monitor = monitorRef.current;
    if (!monitor) return;
    
    // 监听指标更新
    monitor.on('metricsUpdated', (newMetrics: QuantumPerformanceMetrics) => {
      setMetrics(newMetrics);
    });
    
    // 监听告警
    monitor.on('alert', (alert: PerformanceAlert) => {
      setAlerts(prev => {
        // 检查是否已存在相同告警
        const exists = prev.some(a => 
          a.metric === alert.metric && 
          a.type === alert.type && 
          !a.acknowledged
        );
        if (exists) {
          return prev;
        }
        return [...prev, alert];
      });
    });
    
    // 监听监控状态
    monitor.on('monitoringStarted', () => {
      setIsMonitoring(true);
    });
    
    monitor.on('monitoringStopped', () => {
      setIsMonitoring(false);
    });
  };

  /**
   * 开始监控
   */
  const startMonitoring = useCallback(() => {
    try {
      setIsLoading(true);
      setError(null);
      
      if (monitorRef.current) {
        monitorRef.current.startMonitoring();
        
        // 开始自动优化
        if (optimizerRef.current) {
          optimizerRef.current.startAutoOptimize();
        }
        
        // 立即获取当前指标
        setMetrics(monitorRef.current.getCurrentMetrics());
      }
    } catch (err) {
      setError(err instanceof Error ? err : new Error('启动监控失败'));
    } finally {
      setIsLoading(false);
    }
  }, []);

  /**
   * 停止监控
   */
  const stopMonitoring = useCallback(() => {
    try {
      if (monitorRef.current) {
        monitorRef.current.stopMonitoring();
      }
      if (optimizerRef.current) {
        optimizerRef.current.stopAutoOptimize();
      }
    } catch (err) {
      setError(err instanceof Error ? err : new Error('停止监控失败'));
    }
  }, []);

  /**
   * 确认告警
   */
  const acknowledgeAlert = useCallback((alertId: string) => {
    try {
      if (monitorRef.current) {
        monitorRef.current.acknowledgeAlert(alertId);
        setAlerts(prev => 
          prev.map(alert => 
            alert.id === alertId 
              ? { ...alert, acknowledged: true }
              : alert
          )
        );
      }
    } catch (err) {
      setError(err instanceof Error ? err : new Error('确认告警失败'));
    }
  }, []);

  /**
   * 运行优化
   */
  const runOptimization = useCallback(async (strategy: OptimizationStrategy) => {
    try {
      setIsLoading(true);
      setError(null);
      
      if (optimizerRef.current) {
        const result = await optimizerRef.current.optimize(strategy);
        setOptimizationResults(prev => [...prev, result]);
        
        // 更新瓶颈分析
        const analysis = optimizerRef.current.analyzeBottleneck();
        setBottleneck(analysis);
      }
    } catch (err) {
      setError(err instanceof Error ? err : new Error('优化失败'));
    } finally {
      setIsLoading(false);
    }
  }, []);

  /**
   * 获取性能报告
   */
  const getPerformanceReport = useCallback((duration?: number) => {
    try {
      if (monitorRef.current) {
        return monitorRef.current.getPerformanceReport(duration);
      }
      return null;
    } catch (err) {
      setError(err instanceof Error ? err : new Error('获取报告失败'));
      return null;
    }
  }, []);

  /**
   * 定期更新历史记录和瓶颈分析
   */
  useEffect(() => {
    if (!isMonitoring) return;
    
    const intervalId = setInterval(() => {
      // 更新历史记录
      if (monitorRef.current) {
        setHistory(monitorRef.current.getHistory(3600000)); // 最近1小时
      }
      
      // 更新瓶颈分析
      if (optimizerRef.current) {
        const analysis = optimizerRef.current.analyzeBottleneck();
        setBottleneck(analysis);
      }
    }, 5000); // 每5秒更新一次
    
    return () => {
      clearInterval(intervalId);
    };
  }, [isMonitoring]);

  return {
    metrics,
    history,
    alerts,
    isMonitoring,
    bottleneck,
    optimizationResults,
    startMonitoring,
    stopMonitoring,
    acknowledgeAlert,
    runOptimization,
    getPerformanceReport,
    isLoading,
    error,
  };
};

export default useQuantumPerformance;
