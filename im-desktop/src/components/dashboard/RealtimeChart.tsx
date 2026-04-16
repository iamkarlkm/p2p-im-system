/**
 * 实时数据图表组件 - RealtimeChart
 * 功能#27 - 业务洞察仪表盘
 * 模块: im-desktop
 */

import React, { useEffect, useState, useRef } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { 
  Play, 
  Pause, 
  RefreshCw,
  Settings,
  Download
} from 'lucide-react';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
  Area,
  AreaChart
} from 'recharts';

export interface ChartDataPoint {
  timestamp: string;
  value: number;
  label?: string;
}

export interface RealtimeChartProps {
  title: string;
  data: ChartDataPoint[];
  dataKey: string;
  color?: string;
  showGrid?: boolean;
  showLegend?: boolean;
  showTooltip?: boolean;
  isRealtime?: boolean;
  refreshInterval?: number;
  onRefresh?: () => void;
  onExport?: () => void;
  className?: string;
  height?: number;
}

export const RealtimeChart: React.FC<RealtimeChartProps> = ({
  title,
  data,
  dataKey,
  color = '#8884d8',
  showGrid = true,
  showLegend = true,
  showTooltip = true,
  isRealtime = false,
  refreshInterval = 5000,
  onRefresh,
  onExport,
  className,
  height = 300
}) => {
  const [isPlaying, setIsPlaying] = useState(isRealtime);
  const [lastUpdate, setLastUpdate] = useState<Date>(new Date());
  const intervalRef = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    if (isPlaying && onRefresh) {
      intervalRef.current = setInterval(() => {
        onRefresh();
        setLastUpdate(new Date());
      }, refreshInterval);
    }

    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, [isPlaying, refreshInterval, onRefresh]);

  const toggleRealtime = () => {
    setIsPlaying(!isPlaying);
  };

  const formatTime = (timestamp: string) => {
    const date = new Date(timestamp);
    return date.toLocaleTimeString('zh-CN', { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  };

  return (
    <Card className={className}>
      <CardHeader className="flex flex-row items-center justify-between pb-2">
        <div className="flex items-center gap-3">
          <CardTitle className="text-base font-medium">{title}</CardTitle>
          {isRealtime && (
            <Badge 
              variant={isPlaying ? "default" : "secondary"}
              className="text-xs"
            >
              {isPlaying ? '实时更新中' : '已暂停'}
            </Badge>
          )}
        </div>
        <div className="flex items-center gap-2">
          {isRealtime && (
            <Button
              variant="ghost"
              size="icon"
              className="h-8 w-8"
              onClick={toggleRealtime}
            >
              {isPlaying ? (
                <Pause className="h-4 w-4" />
              ) : (
                <Play className="h-4 w-4" />
              )}
            </Button>
          )}
          <Button
            variant="ghost"
            size="icon"
            className="h-8 w-8"
            onClick={onRefresh}
          >
            <RefreshCw className="h-4 w-4" />
          </Button>
          {onExport && (
            <Button
              variant="ghost"
              size="icon"
              className="h-8 w-8"
              onClick={onExport}
            >
              <Download className="h-4 w-4" />
            </Button>
          )}
          <Button
            variant="ghost"
            size="icon"
            className="h-8 w-8"
          >
            <Settings className="h-4 w-4" />
          </Button>
        </div>
      </CardHeader>
      <CardContent>
        <div style={{ height }}>
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={data} margin={{ top: 5, right: 5, left: 0, bottom: 5 }}>
              {showGrid && (
                <CartesianGrid 
                  strokeDasharray="3 3" 
                  stroke="#e0e0e0"
                  vertical={false}
                />
              )}
              <XAxis 
                dataKey="timestamp"
                tickFormatter={formatTime}
                tick={{ fontSize: 12 }}
                axisLine={false}
                tickLine={false}
              />
              <YAxis 
                tick={{ fontSize: 12 }}
                axisLine={false}
                tickLine={false}
                tickFormatter={(value) => value.toLocaleString()}
              />
              {showTooltip && (
                <Tooltip
                  contentStyle={{
                    backgroundColor: 'rgba(255, 255, 255, 0.95)',
                    border: '1px solid #e0e0e0',
                    borderRadius: '8px',
                    boxShadow: '0 4px 12px rgba(0,0,0,0.1)'
                  }}
                  formatter={(value: number) => [value.toLocaleString(), dataKey]}
                  labelFormatter={(label) => `时间: ${formatTime(label as string)}`}
                />
              )}
              {showLegend && <Legend />}
              <Area
                type="monotone"
                dataKey="value"
                stroke={color}
                fill={color}
                fillOpacity={0.2}
                strokeWidth={2}
                dot={false}
                activeDot={{ r: 6, strokeWidth: 0 }}
              />
            </AreaChart>
          </ResponsiveContainer>
        </div>
        <div className="flex justify-between items-center mt-4 text-xs text-muted-foreground">
          <span>数据点数: {data.length}</span>
          <span>最后更新: {lastUpdate.toLocaleTimeString('zh-CN')}</span>
        </div>
      </CardContent>
    </Card>
  );
};

export default RealtimeChart;
