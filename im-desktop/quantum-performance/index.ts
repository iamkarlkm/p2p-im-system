/**
 * 量子性能优化模块导出索引
 * 
 * 功能#199: 量子通信性能优化模块 - 桌面端实现
 * 
 * 本模块提供量子通信系统的性能监控和自动优化功能：
 * - 实时监控量子通信各项性能指标
 * - 自动检测性能瓶颈
 * - 提供多种优化策略
 * - 可视化性能仪表板
 */

// 性能监控
export {
  QuantumPerformanceMonitor,
  type QuantumPerformanceMetrics,
  type PerformanceThresholds,
  type PerformanceAlert,
  type PerformanceHistory,
} from '../services/quantum/QuantumPerformanceMonitor';

// 性能优化
export {
  QuantumPerformanceOptimizer,
  type OptimizationStrategy,
  type OptimizationConfig,
  type OptimizationResult,
  type AdaptiveConfig,
  type BottleneckAnalysis,
} from '../services/quantum/QuantumPerformanceOptimizer';

// React Hook
export { useQuantumPerformance, type UseQuantumPerformanceReturn } from '../hooks/useQuantumPerformance';

// UI组件
export { QuantumPerformanceDashboard } from '../components/quantum/QuantumPerformanceDashboard';
