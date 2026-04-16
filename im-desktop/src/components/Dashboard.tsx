import React, { useState, useEffect, useCallback } from 'react';
import { Line, Bar, Pie, Doughnut } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
  Filler
} from 'chart.js';

/**
 * Dashboard组件 - 业务洞察仪表盘
 * 功能#27: 业务洞察仪表盘 - 可观测性模块
 */

// 注册Chart.js组件
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
  Filler
);

// 指标数据类型
interface MetricData {
  label: string;
  value: number;
  change: number;
  trend: 'up' | 'down' | 'stable';
  unit?: string;
}

// 图表数据类型
interface ChartData {
  labels: string[];
  datasets: {
    label: string;
    data: number[];
    backgroundColor?: string | string[];
    borderColor?: string;
    fill?: boolean;
  }[];
}

export const Dashboard: React.FC = () => {
  const [timeRange, setTimeRange] = useState<'1h' | '24h' | '7d' | '30d'>('24h');
  const [metrics, setMetrics] = useState<MetricData[]>([]);
  const [loading, setLoading] = useState(true);

  // 模拟加载指标数据
  useEffect(() => {
    setLoading(true);
    // 模拟API调用
    setTimeout(() => {
      setMetrics([
        { label: '活跃用户', value: 12450, change: 12.5, trend: 'up', unit: '人' },
        { label: '消息发送量', value: 892300, change: 8.3, trend: 'up', unit: '条' },
        { label: '平均响应时间', value: 45, change: -15.2, trend: 'down', unit: 'ms' },
        { label: '系统可用性', value: 99.98, change: 0.02, trend: 'up', unit: '%' },
        { label: '错误率', value: 0.12, change: -0.05, trend: 'down', unit: '%' },
        { label: '并发连接', value: 5230, change: 23.1, trend: 'up', unit: '个' }
      ]);
      setLoading(false);
    }, 500);
  }, [timeRange]);

  // 实时用户趋势图数据
  const userTrendData: ChartData = {
    labels: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00', '23:59'],
    datasets: [
      {
        label: '在线用户',
        data: [1200, 800, 3500, 8200, 7800, 11200, 9800],
        borderColor: 'rgb(75, 192, 192)',
        backgroundColor: 'rgba(75, 192, 192, 0.2)',
        fill: true
      },
      {
        label: '活跃会话',
        data: [800, 500, 2200, 5600, 5200, 7800, 6500],
        borderColor: 'rgb(255, 99, 132)',
        backgroundColor: 'rgba(255, 99, 132, 0.2)',
        fill: true
      }
    ]
  };

  // 消息类型分布图数据
  const messageTypeData: ChartData = {
    labels: ['文本', '图片', '语音', '视频', '文件', '其他'],
    datasets: [{
      label: '消息分布',
      data: [65, 20, 8, 4, 2, 1],
      backgroundColor: [
        'rgba(54, 162, 235, 0.8)',
        'rgba(255, 99, 132, 0.8)',
        'rgba(255, 206, 86, 0.8)',
        'rgba(75, 192, 192, 0.8)',
        'rgba(153, 102, 255, 0.8)',
        'rgba(255, 159, 64, 0.8)'
      ]
    }]
  };

  // 服务器负载图数据
  const serverLoadData: ChartData = {
    labels: ['Web服务器1', 'Web服务器2', 'Web服务器3', '消息队列1', '消息队列2', '数据库主', '数据库从'],
    datasets: [{
      label: 'CPU使用率(%)',
      data: [45, 52, 38, 67, 55, 72, 48],
      backgroundColor: 'rgba(54, 162, 235, 0.8)'
    }, {
      label: '内存使用率(%)',
      data: [62, 58, 55, 73, 68, 81, 59],
      backgroundColor: 'rgba(255, 99, 132, 0.8)'
    }]
  };

  // 业务转化率漏斗数据
  const conversionData: ChartData = {
    labels: ['访问', '注册', '登录', '发消息', '创建群', '付费'],
    datasets: [{
      label: '转化人数',
      data: [10000, 6500, 5800, 4200, 1200, 380],
      backgroundColor: [
        'rgba(54, 162, 235, 0.9)',
        'rgba(54, 162, 235, 0.8)',
        'rgba(54, 162, 235, 0.7)',
        'rgba(54, 162, 235, 0.6)',
        'rgba(54, 162, 235, 0.5)',
        'rgba(54, 162, 235, 0.4)'
      ]
    }]
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top' as const,
      },
      title: {
        display: false
      }
    }
  };

  return (
    <div className="dashboard-container">
      {/* 头部 */}
      <div className="dashboard-header">
        <h1>业务洞察仪表盘</h1>
        <div className="time-range-selector">
          {(['1h', '24h', '7d', '30d'] as const).map(range => (
            <button
              key={range}
              className={timeRange === range ? 'active' : ''}
              onClick={() => setTimeRange(range)}
            >
              {range === '1h' ? '1小时' : range === '24h' ? '24小时' : range === '7d' ? '7天' : '30天'}
            </button>
          ))}
        </div>
      </div>

      {/* 指标卡片 */}
      <div className="metrics-grid">
        {loading ? (
          Array(6).fill(null).map((_, i) => (
            <div key={i} className="metric-card skeleton"></div>
          ))
        ) : (
          metrics.map((metric, index) => (
            <MetricCard key={index} data={metric} />
          ))
        )}
      </div>

      {/* 图表区域 */}
      <div className="charts-grid">
        {/* 用户趋势图 */}
        <div className="chart-panel large">
          <div className="chart-header">
            <h3>实时用户趋势</h3>
            <span className="live-indicator">● 实时</span>
          </div>
          <div className="chart-content">
            <Line data={userTrendData} options={chartOptions} />
          </div>
        </div>

        {/* 消息类型分布 */}
        <div className="chart-panel">
          <div className="chart-header">
            <h3>消息类型分布</h3>
          </div>
          <div className="chart-content">
            <Doughnut data={messageTypeData} options={chartOptions} />
          </div>
        </div>

        {/* 服务器负载 */}
        <div className="chart-panel">
          <div className="chart-header">
            <h3>服务器资源负载</h3>
          </div>
          <div className="chart-content">
            <Bar data={serverLoadData} options={chartOptions} />
          </div>
        </div>

        {/* 业务转化漏斗 */}
        <div className="chart-panel">
          <div className="chart-header">
            <h3>业务转化漏斗</h3>
          </div>
          <div className="chart-content">
            <Bar 
              data={conversionData} 
              options={{
                ...chartOptions,
                indexAxis: 'y' as const
              }} 
            />
          </div>
        </div>
      </div>

      {/* 底部信息 */}
      <div className="dashboard-footer">
        <span>数据更新时间: {new Date().toLocaleString('zh-CN')}</span>
        <span>系统状态: <span className="status-online">● 正常运行</span></span>
      </div>
    </div>
  );
};

// 指标卡片组件
const MetricCard: React.FC<{ data: MetricData }> = ({ data }) => {
  const trendIcon = data.trend === 'up' ? '↑' : data.trend === 'down' ? '↓' : '→';
  const trendClass = data.trend === 'up' ? 'trend-up' : data.trend === 'down' ? 'trend-down' : 'trend-stable';

  return (
    <div className="metric-card">
      <div className="metric-label">{data.label}</div>
      <div className="metric-value">
        {data.value.toLocaleString()}
        {data.unit && <span className="metric-unit">{data.unit}</span>}
      </div>
      <div className={`metric-change ${trendClass}`}>
        {trendIcon} {Math.abs(data.change)}%
        <span className="change-label">vs 上周期</span>
      </div>
    </div>
  );
};

// 导出仪表盘组件
export default Dashboard;
