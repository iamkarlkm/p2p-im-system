/**
 * 洞察指标卡片组件 - InsightMetricCard
 * 功能#27 - 业务洞察仪表盘
 * 模块: im-desktop
 */

import React from 'react';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { 
  ArrowUpRight, 
  ArrowDownRight, 
  Minus,
  TrendingUp,
  TrendingDown,
  Activity
} from 'lucide-react';
import { cn } from '@/lib/utils';

export interface InsightMetricCardProps {
  title: string;
  value: string | number;
  change?: number;
  trend?: 'up' | 'down' | 'stable';
  icon?: React.ReactNode;
  description?: string;
  className?: string;
  size?: 'sm' | 'md' | 'lg';
  onClick?: () => void;
}

export const InsightMetricCard: React.FC<InsightMetricCardProps> = ({
  title,
  value,
  change = 0,
  trend = 'stable',
  icon,
  description,
  className,
  size = 'md',
  onClick
}) => {
  const sizeClasses = {
    sm: 'p-4',
    md: 'p-6',
    lg: 'p-8'
  };

  const valueSizeClasses = {
    sm: 'text-xl',
    md: 'text-2xl',
    lg: 'text-3xl'
  };

  const getTrendIcon = () => {
    switch (trend) {
      case 'up':
        return <ArrowUpRight className="h-3 w-3" />;
      case 'down':
        return <ArrowDownRight className="h-3 w-3" />;
      default:
        return <Minus className="h-3 w-3" />;
    }
  };

  const getTrendColor = () => {
    switch (trend) {
      case 'up':
        return 'bg-green-100 text-green-700 border-green-200';
      case 'down':
        return 'bg-red-100 text-red-700 border-red-200';
      default:
        return 'bg-gray-100 text-gray-700 border-gray-200';
    }
  };

  return (
    <Card 
      className={cn(
        "hover:shadow-lg transition-all duration-200 cursor-pointer",
        className
      )}
      onClick={onClick}
    >
      <CardContent className={cn(sizeClasses[size], "space-y-3")}>
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            {icon && (
              <div className="p-2 bg-primary/10 rounded-lg text-primary">
                {icon}
              </div>
            )}
            <span className="text-sm font-medium text-muted-foreground">
              {title}
            </span>
          </div>
          {change !== 0 && (
            <Badge 
              variant="outline"
              className={cn("flex items-center gap-1 text-xs", getTrendColor())}
            >
              {getTrendIcon()}
              {change > 0 ? '+' : ''}{change.toFixed(1)}%
            </Badge>
          )}
        </div>
        
        <div className={cn("font-bold tracking-tight", valueSizeClasses[size])}>
          {value}
        </div>
        
        {description && (
          <p className="text-xs text-muted-foreground">
            {description}
          </p>
        )}
      </CardContent>
    </Card>
  );
};

export default InsightMetricCard;
