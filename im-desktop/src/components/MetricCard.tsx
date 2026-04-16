import React from 'react';

/**
 * MetricCard组件 - 指标卡片
 * 功能#27: 业务洞察仪表盘 - 子组件
 */

export interface MetricCardProps {
  title: string;
  value: number | string;
  unit?: string;
  change?: number;
  changeType?: 'increase' | 'decrease' | 'neutral';
  icon?: string;
  color?: 'blue' | 'green' | 'orange' | 'red' | 'purple';
  loading?: boolean;
}

export const MetricCard: React.FC<MetricCardProps> = ({
  title,
  value,
  unit = '',
  change,
  changeType = 'neutral',
  icon,
  color = 'blue',
  loading = false
}) => {
  const colorClasses = {
    blue: { bg: 'bg-blue-50', text: 'text-blue-600', icon: 'text-blue-500' },
    green: { bg: 'bg-green-50', text: 'text-green-600', icon: 'text-green-500' },
    orange: { bg: 'bg-orange-50', text: 'text-orange-600', icon: 'text-orange-500' },
    red: { bg: 'bg-red-50', text: 'text-red-600', icon: 'text-red-500' },
    purple: { bg: 'bg-purple-50', text: 'text-purple-600', icon: 'text-purple-500' }
  };

  const theme = colorClasses[color];

  const formatValue = (val: number | string): string => {
    if (typeof val === 'number') {
      if (val >= 1000000) {
        return (val / 1000000).toFixed(1) + 'M';
      }
      if (val >= 1000) {
        return (val / 1000).toFixed(1) + 'K';
      }
      return val.toString();
    }
    return val;
  };

  const getChangeIcon = () => {
    if (changeType === 'increase') return '↑';
    if (changeType === 'decrease') return '↓';
    return '→';
  };

  const getChangeClass = () => {
    if (changeType === 'increase') return 'change-up';
    if (changeType === 'decrease') return 'change-down';
    return 'change-neutral';
  };

  if (loading) {
    return (
      <div className="metric-card-skeleton">
        <div className="skeleton-header"></div>
        <div className="skeleton-value"></div>
        <div className="skeleton-change"></div>
      </div>
    );
  }

  return (
    <div className="metric-card">
      <div className="metric-card-header">
        <span className="metric-title">{title}</span>
        {icon && (
          <div className={`metric-icon ${theme.bg}`}>
            <span className={theme.icon}>{icon}</span>
          </div>
        )}
      </div>
      <div className="metric-card-body">
        <div className={`metric-value ${theme.text}`}>
          {formatValue(value)}
          {unit && <span className="metric-unit">{unit}</span>}
        </div>
        {change !== undefined && (
          <div className={`metric-change ${getChangeClass()}`}>
            <span className="change-icon">{getChangeIcon()}</span>
            <span className="change-value">{Math.abs(change)}%</span>
            <span className="change-label">vs 上周期</span>
          </div>
        )}
      </div>
      <div className="metric-card-footer">
        <div className="metric-sparkline">
          <Sparkline type={changeType} color={theme.text} />
        </div>
      </div>
    </div>
  );
};

// 迷你趋势图组件
const Sparkline: React.FC<{ type: string; color: string }> = ({ type, color }) => {
  // 根据趋势生成不同的SVG路径
  const getPath = () => {
    if (type === 'increase') {
      return 'M0,30 Q10,25 20,20 T40,15 T60,10 T80,5 T100,0';
    }
    if (type === 'decrease') {
      return 'M0,0 Q10,5 20,10 T40,15 T60,20 T80,25 T100,30';
    }
    return 'M0,15 Q25,10 50,15 T100,15';
  };

  return (
    <svg viewBox="0 0 100 35" className="sparkline-svg">
      <path
        d={getPath()}
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        className={color}
      />
    </svg>
  );
};

export default MetricCard;
