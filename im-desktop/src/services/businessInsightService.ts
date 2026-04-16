/**
 * 业务洞察服务 - BusinessInsightService
 * 功能#27 - 业务洞察仪表盘
 * 模块: im-desktop
 */

import { useState, useEffect, useCallback } from 'react';
import { api } from '@/lib/api';

// 业务指标接口
export interface BusinessMetrics {
  totalRevenue: number;
  totalOrders: number;
  activeUsers: number;
  conversionRate: number;
  revenueGrowth: number;
  orderGrowth: number;
  userGrowth: number;
  conversionGrowth: number;
}

// 趋势数据接口
export interface TrendData {
  date: string;
  revenue: number;
  orders: number;
  users: number;
  conversion: number;
}

// 品类数据接口
export interface CategoryData {
  name: string;
  value: number;
  percentage: number;
}

// 漏斗数据接口
export interface FunnelData {
  stage: string;
  count: number;
  conversion: number;
  dropOff: number;
}

// 洞察报告接口
export interface InsightReport {
  id: string;
  title: string;
  summary: string;
  recommendations: string[];
  riskFactors: string[];
  opportunities: string[];
  createdAt: string;
}

// 仪表盘配置接口
export interface DashboardConfig {
  id: string;
  name: string;
  layout: 'grid' | 'list' | 'custom';
  widgets: WidgetConfig[];
  refreshInterval: number;
  timeRange: '1d' | '7d' | '30d' | '90d' | 'custom';
}

export interface WidgetConfig {
  id: string;
  type: 'metric' | 'chart' | 'table' | 'alert';
  title: string;
  position: { x: number; y: number; w: number; h: number };
  config: Record<string, any>;
}

// API响应类型
interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
}

/**
 * 业务洞察服务类
 */
export class BusinessInsightService {
  private static instance: BusinessInsightService;
  private baseUrl: string = '/api/business-insights';

  private constructor() {}

  static getInstance(): BusinessInsightService {
    if (!BusinessInsightService.instance) {
      BusinessInsightService.instance = new BusinessInsightService();
    }
    return BusinessInsightService.instance;
  }

  /**
   * 获取核心指标
   */
  async getCoreMetrics(timeRange: string = '30d'): Promise<BusinessMetrics> {
    try {
      const response = await api.get<ApiResponse<BusinessMetrics>>(
        `${this.baseUrl}/metrics?range=${timeRange}`
      );
      return response.data.data;
    } catch (error) {
      console.error('获取核心指标失败:', error);
      throw error;
    }
  }

  /**
   * 获取趋势数据
   */
  async getTrendData(days: number = 30): Promise<TrendData[]> {
    try {
      const response = await api.get<ApiResponse<TrendData[]>>(
        `${this.baseUrl}/trends?days=${days}`
      );
      return response.data.data;
    } catch (error) {
      console.error('获取趋势数据失败:', error);
      throw error;
    }
  }

  /**
   * 获取品类分布
   */
  async getCategoryDistribution(): Promise<CategoryData[]> {
    try {
      const response = await api.get<ApiResponse<CategoryData[]>>(
        `${this.baseUrl}/categories`
      );
      return response.data.data;
    } catch (error) {
      console.error('获取品类分布失败:', error);
      throw error;
    }
  }

  /**
   * 获取转化漏斗
   */
  async getConversionFunnel(): Promise<FunnelData[]> {
    try {
      const response = await api.get<ApiResponse<FunnelData[]>>(
        `${this.baseUrl}/funnel`
      );
      return response.data.data;
    } catch (error) {
      console.error('获取转化漏斗失败:', error);
      throw error;
    }
  }

  /**
   * 获取洞察报告
   */
  async getInsightReports(limit: number = 10): Promise<InsightReport[]> {
    try {
      const response = await api.get<ApiResponse<InsightReport[]>>(
        `${this.baseUrl}/reports?limit=${limit}`
      );
      return response.data.data;
    } catch (error) {
      console.error('获取洞察报告失败:', error);
      throw error;
    }
  }

  /**
   * 生成洞察报告
   */
  async generateInsightReport(
    title: string,
    metrics: string[]
  ): Promise<InsightReport> {
    try {
      const response = await api.post<ApiResponse<InsightReport>>(
        `${this.baseUrl}/reports`,
        { title, metrics }
      );
      return response.data.data;
    } catch (error) {
      console.error('生成洞察报告失败:', error);
      throw error;
    }
  }

  /**
   * 获取仪表盘配置
   */
  async getDashboardConfigs(): Promise<DashboardConfig[]> {
    try {
      const response = await api.get<ApiResponse<DashboardConfig[]>>(
        `${this.baseUrl}/dashboards`
      );
      return response.data.data;
    } catch (error) {
      console.error('获取仪表盘配置失败:', error);
      throw error;
    }
  }

  /**
   * 保存仪表盘配置
   */
  async saveDashboardConfig(config: DashboardConfig): Promise<DashboardConfig> {
    try {
      const response = await api.post<ApiResponse<DashboardConfig>>(
        `${this.baseUrl}/dashboards`,
        config
      );
      return response.data.data;
    } catch (error) {
      console.error('保存仪表盘配置失败:', error);
      throw error;
    }
  }

  /**
   * 导出报表
   */
  async exportReport(
    format: 'pdf' | 'excel' | 'csv',
    timeRange: string
  ): Promise<Blob> {
    try {
      const response = await api.get<Blob>(
        `${this.baseUrl}/export?format=${format}&range=${timeRange}`,
        { responseType: 'blob' }
      );
      return response.data;
    } catch (error) {
      console.error('导出报表失败:', error);
      throw error;
    }
  }
}

// React Hook
export function useBusinessInsights(timeRange: string = '30d') {
  const [metrics, setMetrics] = useState<BusinessMetrics | null>(null);
  const [trendData, setTrendData] = useState<TrendData[]>([]);
  const [categoryData, setCategoryData] = useState<CategoryData[]>([]);
  const [funnelData, setFunnelData] = useState<FunnelData[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  const service = BusinessInsightService.getInstance();

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [metricsRes, trendRes, categoryRes, funnelRes] = await Promise.all([
        service.getCoreMetrics(timeRange),
        service.getTrendData(timeRange === '7d' ? 7 : timeRange === '30d' ? 30 : 90),
        service.getCategoryDistribution(),
        service.getConversionFunnel()
      ]);
      setMetrics(metricsRes);
      setTrendData(trendRes);
      setCategoryData(categoryRes);
      setFunnelData(funnelRes);
    } catch (err) {
      setError(err as Error);
    } finally {
      setLoading(false);
    }
  }, [timeRange, service]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return {
    metrics,
    trendData,
    categoryData,
    funnelData,
    loading,
    error,
    refetch: fetchData
  };
}

export default BusinessInsightService;
