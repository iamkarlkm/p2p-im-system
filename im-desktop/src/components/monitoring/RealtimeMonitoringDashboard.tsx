/**
 * 实时监控大屏 - RealtimeMonitoringDashboard
 * 功能#29 - 实时监控大屏
 * 模块: im-desktop
 */

import React, { useState, useEffect, useCallback } from 'react';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { 
  Activity, 
  Server, 
  Database, 
  Network,
  Cpu,
  HardDrive,
  Zap,
  AlertCircle,
  CheckCircle,
  Clock,
  Users,
  MessageSquare,
  Wifi
} from 'lucide-react';
import { cn } from '@/lib/utils';
import { format } from 'date-fns';

// 系统指标接口
interface SystemMetric {
  name: string;
  value: number;
  unit: string;
  threshold: number;
  status: 'normal' | 'warning' | 'critical';
  icon: React.ReactNode;
}

// 服务状态接口
interface ServiceStatus {
  name: string;
  status: 'online' | 'degraded' | 'offline';
  latency: number;
  uptime: string;
  lastCheck: string;
}

// 实时数据点
interface DataPoint {
  timestamp: string;
  value: number;
}

export const RealtimeMonitoringDashboard: React.FC = () => {
  const [currentTime, setCurrentTime] = useState<Date>(new Date());
  const [metrics, setMetrics] = useState<SystemMetric[]>([
    { name: 'CPU使用率', value: 45.2, unit: '%', threshold: 80, status: 'normal', icon: <Cpu className="h-5 w-5" /> },
    { name: '内存使用率', value: 62.8, unit: '%', threshold: 85, status: 'normal', icon: <HardDrive className="h-5 w-5" /> },
    { name: '磁盘IO', value: 28.5, unit: 'MB/s', threshold: 100, status: 'normal', icon: <Database className="h-5 w-5" /> },
    { name: '网络流量', value: 156.3, unit: 'Mbps', threshold: 500, status: 'normal', icon: <Network className="h-5 w-5" /> }
  ]);

  const [services, setServices] = useState<ServiceStatus[]>([
    { name: '消息服务', status: 'online', latency: 12, uptime: '99.99%', lastCheck: new Date().toISOString() },
    { name: 'WebSocket网关', status: 'online', latency: 8, uptime: '99.98%', lastCheck: new Date().toISOString() },
    { name: '用户服务', status: 'online', latency: 15, uptime: '99.95%', lastCheck: new Date().toISOString() },
    { name: '推送服务', status: 'online', latency: 22, uptime: '99.92%', lastCheck: new Date().toISOString() },
    { name: '存储服务', status: 'online', latency: 18, uptime: '99.97%', lastCheck: new Date().toISOString() },
    { name: '搜索服务', status: 'degraded', latency: 156, uptime: '99.85%', lastCheck: new Date().toISOString() }
  ]);

  const [realtimeStats, setRealtimeStats] = useState({
    onlineUsers: 45231,
    messagesPerSecond: 12580,
    activeConnections: 89421,
    errorRate: 0.02
  });

  // 更新时间
  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  // 模拟实时数据更新
  useEffect(() => {
    const timer = setInterval(() => {
      // 更新系统指标
      setMetrics(prev => prev.map(m => ({
        ...m,
        value: Math.max(0, Math.min(100, m.value + (Math.random() - 0.5) * 5)),
        status: m.value > m.threshold ? 'critical' : m.value > m.threshold * 0.8 ? 'warning' : 'normal'
      })));

      // 更新实时统计
      setRealtimeStats(prev => ({
        onlineUsers: prev.onlineUsers + Math.floor((Math.random() - 0.5) * 100),
        messagesPerSecond: Math.max(1000, prev.messagesPerSecond + Math.floor((Math.random() - 0.5) * 500)),
        activeConnections: prev.activeConnections + Math.floor((Math.random() - 0.5) * 50),
        errorRate: Math.max(0, prev.errorRate + (Math.random() - 0.5) * 0.01)
      }));
    }, 2000);

    return () => clearInterval(timer);
  }, []);

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'online':
      case 'normal':
        return 'bg-green-500';
      case 'degraded':
      case 'warning':
        return 'bg-yellow-500';
      case 'offline':
      case 'critical':
        return 'bg-red-500';
      default:
        return 'bg-gray-500';
    }
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'online':
      case 'normal':
        return <Badge className="bg-green-500">正常</Badge>;
      case 'degraded':
      case 'warning':
        return <Badge className="bg-yellow-500">警告</Badge>;
      case 'offline':
      case 'critical':
        return <Badge className="bg-red-500">异常</Badge>;
      default:
        return <Badge>未知</Badge>;
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 text-white p-6">
      {/* 头部 */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight flex items-center gap-3">
            <Activity className="h-8 w-8 text-green-400" />
            实时监控大屏
          </h1>
          <p className="text-slate-400 mt-1">
            实时监控系统运行状态与业务指标
          </p>
        </div>
        <div className="flex items-center gap-6">
          <div className="flex items-center gap-2 text-slate-400">
            <Clock className="h-4 w-4" />
            <span className="font-mono text-lg">
              {format(currentTime, 'yyyy-MM-dd HH:mm:ss')}
            </span>
          </div>
          <Badge className="bg-green-500 flex items-center gap-1">
            <div className="w-2 h-2 bg-white rounded-full animate-pulse" />
            实时监控中
          </Badge>
        </div>
      </div>

      {/* 实时统计卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <Card className="bg-slate-900 border-slate-800">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="p-3 bg-blue-500/20 rounded-lg">
                  <Users className="h-6 w-6 text-blue-400" />
                </div>
                <div>
                  <p className="text-slate-400 text-sm">在线用户</p>
                  <p className="text-2xl font-bold">
                    {realtimeStats.onlineUsers.toLocaleString()}
                  </p>
                </div>
              </div>
              <Badge variant="outline" className="text-green-400 border-green-400">
                +2.4%
              </Badge>
            </div>
          </CardContent>
        </Card>

        <Card className="bg-slate-900 border-slate-800">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="p-3 bg-purple-500/20 rounded-lg">
                  <MessageSquare className="h-6 w-6 text-purple-400" />
                </div>
                <div>
                  <p className="text-slate-400 text-sm">消息/秒</p>
                  <p className="text-2xl font-bold">
                    {realtimeStats.messagesPerSecond.toLocaleString()}
                  </p>
                </div>
              </div>
              <Badge variant="outline" className="text-green-400 border-green-400">
                +5.2%
              </Badge>
            </div>
          </CardContent>
        </Card>

        <Card className="bg-slate-900 border-slate-800">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="p-3 bg-orange-500/20 rounded-lg">
                  <Wifi className="h-6 w-6 text-orange-400" />
                </div>
                <div>
                  <p className="text-slate-400 text-sm">活跃连接</p>
                  <p className="text-2xl font-bold">
                    {realtimeStats.activeConnections.toLocaleString()}
                  </p>
                </div>
              </div>
              <Badge variant="outline" className="text-yellow-400 border-yellow-400">
                +0.8%
              </Badge>
            </div>
          </CardContent>
        </Card>

        <Card className="bg-slate-900 border-slate-800">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="p-3 bg-red-500/20 rounded-lg">
                  <AlertCircle className="h-6 w-6 text-red-400" />
                </div>
                <div>
                  <p className="text-slate-400 text-sm">错误率</p>
                  <p className="text-2xl font-bold">
                    {realtimeStats.errorRate.toFixed(3)}%
                  </p>
                </div>
              </div>
              <Badge variant="outline" className="text-green-400 border-green-400">
                -0.1%
              </Badge>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* 系统资源监控 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
        <Card className="bg-slate-900 border-slate-800">
          <CardContent className="p-6">
            <h3 className="text-lg font-semibold mb-4 flex items-center gap-2">
              <Server className="h-5 w-5 text-blue-400" />
              系统资源监控
            </h3>
            <div className="space-y-4">
              {metrics.map((metric, index) => (
                <div key={index} className="space-y-2">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      {metric.icon}
                      <span className="text-sm">{metric.name}</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className="font-mono font-bold">
                        {metric.value.toFixed(1)}{metric.unit}
                      </span>
                      {getStatusBadge(metric.status)}
                    </div>
                  </div>
                  <div className="h-2 bg-slate-800 rounded-full overflow-hidden">
                    <div 
                      className={cn(
                        "h-full transition-all duration-500",
                        getStatusColor(metric.status)
                      )}
                      style={{ width: `${Math.min(100, (metric.value / metric.threshold) * 100)}%` }}
                    />
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        <Card className="bg-slate-900 border-slate-800">
          <CardContent className="p-6">
            <h3 className="text-lg font-semibold mb-4 flex items-center gap-2">
              <Zap className="h-5 w-5 text-yellow-400" />
              服务健康状态
            </h3>
            <div className="space-y-3">
              {services.map((service, index) => (
                <div 
                  key={index} 
                  className="flex items-center justify-between p-3 bg-slate-800/50 rounded-lg"
                >
                  <div className="flex items-center gap-3">
                    <div className={cn("w-3 h-3 rounded-full", getStatusColor(service.status))} />
                    <span className="font-medium">{service.name}</span>
                  </div>
                  <div className="flex items-center gap-4 text-sm">
                    <span className="text-slate-400">
                      延迟: {service.latency}ms
                    </span>
                    <span className="text-slate-400">
                      可用性: {service.uptime}
                    </span>
                    {getStatusBadge(service.status)}
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default RealtimeMonitoringDashboard;
