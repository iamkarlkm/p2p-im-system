/**
 * 洞察数据类型定义
 * 功能#27: 业务洞察仪表盘
 */

export interface KPIMetric {
  id: string;
  name: string;
  value: number;
  target: number;
  change: number;
  changeType: 'increase' | 'decrease';
  completion: number;
  type: 'primary' | 'secondary' | 'distribution';
}

export interface TrendData {
  date: string;
  value: number;
  category: string;
}

export interface InsightData {
  kpis: KPIMetric[];
  trends: TrendData[];
  timestamp: number;
}

export interface DashboardWidget {
  id: string;
  type: 'statistic' | 'line' | 'pie' | 'bar' | 'table';
  title: string;
  dataSource: string;
  config: Record<string, any>;
}

export interface BusinessMetric {
  metricName: string;
  currentValue: number;
  previousValue: number;
  unit: string;
  trend: 'up' | 'down' | 'stable';
}
