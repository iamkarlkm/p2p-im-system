/**
 * 业务洞察仪表盘 - BusinessInsightDashboard
 * 功能#27 - 业务洞察仪表盘
 * 模块: im-desktop
 */

import React, { useState, useEffect, useCallback } from 'react';
import { Line, Bar, Pie, Area } from 'recharts';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { 
  TrendingUp, 
  TrendingDown, 
  Users, 
  ShoppingCart, 
  DollarSign,
  Activity,
  BarChart3,
  PieChart,
  ArrowUpRight,
  ArrowDownRight,
  Calendar,
  Filter,
  Download,
  RefreshCw
} from 'lucide-react';
import { format, subDays, startOfDay, endOfDay } from 'date-fns';
import { zhCN } from 'date-fns/locale';

// 数据类型定义
interface BusinessMetric {
  timestamp: string;
  value: number;
  label: string;
}

interface InsightCardData {
  title: string;
  value: string | number;
  change: number;
  trend: 'up' | 'down' | 'stable';
  icon: React.ReactNode;
  description: string;
}

interface ChartData {
  name: string;
  value: number;
  [key: string]: any;
}

// 模拟数据生成器
const generateMockData = (days: number = 30): BusinessMetric[] => {
  const data: BusinessMetric[] = [];
  for (let i = days; i >= 0; i--) {
    data.push({
      timestamp: format(subDays(new Date(), i), 'yyyy-MM-dd'),
      value: Math.floor(Math.random() * 10000) + 5000,
      label: format(subDays(new Date(), i), 'MM/dd')
    });
  }
  return data;
};

const generateMultiSeriesData = (days: number = 30): ChartData[] => {
  const data: ChartData[] = [];
  for (let i = days; i >= 0; i--) {
    data.push({
      name: format(subDays(new Date(), i), 'MM/dd'),
      revenue: Math.floor(Math.random() * 50000) + 30000,
      orders: Math.floor(Math.random() * 500) + 200,
      users: Math.floor(Math.random() * 1000) + 500,
      conversion: parseFloat((Math.random() * 5 + 2).toFixed(2))
    });
  }
  return data;
};

// 主要组件
export const BusinessInsightDashboard: React.FC = () => {
  const [activeTab, setActiveTab] = useState('overview');
  const [timeRange, setTimeRange] = useState<'7d' | '30d' | '90d'>('30d');
  const [isLoading, setIsLoading] = useState(false);
  const [lastUpdated, setLastUpdated] = useState<Date>(new Date());

  // 核心指标数据
  const [metrics, setMetrics] = useState<InsightCardData[]>([
    {
      title: '总营收',
      value: '¥2,847,392',
      change: 12.5,
      trend: 'up',
      icon: <DollarSign className="h-5 w-5" />,
      description: '较上月同期'
    },
    {
      title: '订单量',
      value: '15,847',
      change: 8.3,
      trend: 'up',
      icon: <ShoppingCart className="h-5 w-5" />,
      description: '较上月同期'
    },
    {
      title: '活跃用户',
      value: '45,231',
      change: -2.1,
      trend: 'down',
      icon: <Users className="h-5 w-5" />,
      description: '较上月同期'
    },
    {
      title: '转化率',
      value: '3.24%',
      change: 0.8,
      trend: 'up',
      icon: <Activity className="h-5 w-5" />,
      description: '较上月同期'
    }
  ]);

  // 图表数据
  const [chartData, setChartData] = useState<ChartData[]>(generateMultiSeriesData());
  const [pieData, setPieData] = useState<ChartData[]>([
    { name: '餐饮', value: 35, color: '#0088FE' },
    { name: '零售', value: 25, color: '#00C49F' },
    { name: '服务', value: 20, color: '#FFBB28' },
    { name: '娱乐', value: 15, color: '#FF8042' },
    { name: '其他', value: 5, color: '#8884D8' }
  ]);

  // 刷新数据
  const handleRefresh = useCallback(() => {
    setIsLoading(true);
    setTimeout(() => {
      setChartData(generateMultiSeriesData(timeRange === '7d' ? 7 : timeRange === '30d' ? 30 : 90));
      setLastUpdated(new Date());
      setIsLoading(false);
    }, 800);
  }, [timeRange]);

  useEffect(() => {
    handleRefresh();
  }, [timeRange, handleRefresh]);

  // 渲染指标卡片
  const renderMetricCard = (metric: InsightCardData, index: number) => (
    <Card key={index} className="hover:shadow-lg transition-shadow">
      <CardContent className="p-6">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <div className="p-2 bg-primary/10 rounded-lg">
              {metric.icon}
            </div>
            <span className="text-sm text-muted-foreground">{metric.title}</span>
          </div>
          <Badge 
            variant={metric.trend === 'up' ? 'default' : metric.trend === 'down' ? 'destructive' : 'secondary'}
            className="flex items-center gap-1"
          >
            {metric.trend === 'up' ? <ArrowUpRight className="h-3 w-3" /> : 
             metric.trend === 'down' ? <ArrowDownRight className="h-3 w-3" /> : null}
            {metric.change > 0 ? '+' : ''}{metric.change}%
          </Badge>
        </div>
        <div className="mt-4">
          <div className="text-2xl font-bold">{metric.value}</div>
          <p className="text-xs text-muted-foreground mt-1">{metric.description}</p>
        </div>
      </CardContent>
    </Card>
  );

  return (
    <div className="p-6 space-y-6 bg-background min-h-screen">
      {/* 页面头部 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">业务洞察仪表盘</h1>
          <p className="text-muted-foreground mt-1">
            实时监控业务核心指标与趋势分析
          </p>
        </div>
        <div className="flex items-center gap-4">
          <div className="flex items-center gap-2">
            <Calendar className="h-4 w-4 text-muted-foreground" />
            <span className="text-sm text-muted-foreground">
              更新时间: {format(lastUpdated, 'yyyy-MM-dd HH:mm:ss')}
            </span>
          </div>
          <div className="flex gap-2">
            <Button 
              variant={timeRange === '7d' ? 'default' : 'outline'} 
              size="sm"
              onClick={() => setTimeRange('7d')}
            >
              7天
            </Button>
            <Button 
              variant={timeRange === '30d' ? 'default' : 'outline'} 
              size="sm"
              onClick={() => setTimeRange('30d')}
            >
              30天
            </Button>
            <Button 
              variant={timeRange === '90d' ? 'default' : 'outline'} 
              size="sm"
              onClick={() => setTimeRange('90d')}
            >
              90天
            </Button>
          </div>
          <Button variant="outline" size="icon" onClick={handleRefresh} disabled={isLoading}>
            <RefreshCw className={`h-4 w-4 ${isLoading ? 'animate-spin' : ''}`} />
          </Button>
          <Button variant="outline" size="icon">
            <Download className="h-4 w-4" />
          </Button>
        </div>
      </div>

      {/* 核心指标卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {metrics.map((metric, index) => renderMetricCard(metric, index))}
      </div>

      {/* 图表区域 */}
      <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-4">
        <TabsList className="grid w-full grid-cols-4 lg:w-[400px]">
          <TabsTrigger value="overview">总览</TabsTrigger>
          <TabsTrigger value="revenue">营收分析</TabsTrigger>
          <TabsTrigger value="users">用户分析</TabsTrigger>
          <TabsTrigger value="conversion">转化漏斗</TabsTrigger>
        </TabsList>

        <TabsContent value="overview" className="space-y-4">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
            <Card className="lg:col-span-2">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <BarChart3 className="h-5 w-5" />
                  营收趋势
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="h-[300px]">
                  <Line
                    data={chartData}
                    dataKey="revenue"
                    stroke="#8884d8"
                    strokeWidth={2}
                    dot={false}
                  />
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <PieChart className="h-5 w-5" />
                  品类占比
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="h-[300px]">
                  <Pie
                    data={pieData}
                    dataKey="value"
                    nameKey="name"
                    cx="50%"
                    cy="50%"
                    outerRadius={80}
                    label
                  />
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>

        <TabsContent value="revenue" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>营收详细分析</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="h-[400px]">
                <Area
                  data={chartData}
                  dataKey="revenue"
                  stroke="#8884d8"
                  fill="#8884d8"
                  fillOpacity={0.3}
                />
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="users" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>用户增长趋势</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="h-[400px]">
                <Bar
                  data={chartData}
                  dataKey="users"
                  fill="#82ca9d"
                />
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="conversion" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>转化漏斗分析</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="h-[400px]">
                <Line
                  data={chartData}
                  dataKey="conversion"
                  stroke="#ffc658"
                  strokeWidth={2}
                />
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
};

export default BusinessInsightDashboard;
