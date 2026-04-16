/**
 * 告警通知组件 - AlertNotification
 * 功能#29 - 实时监控大屏
 * 模块: im-desktop
 */

import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { ScrollArea } from '@/components/ui/scroll-area';
import { 
  Bell, 
  AlertCircle, 
  AlertTriangle, 
  Info,
  CheckCircle,
  X,
  Clock,
  Filter
} from 'lucide-react';
import { cn } from '@/lib/utils';
import { formatDistanceToNow } from 'date-fns';
import { zhCN } from 'date-fns/locale';

export interface AlertItem {
  id: string;
  severity: 'info' | 'warning' | 'critical';
  title: string;
  message: string;
  service: string;
  timestamp: string;
  acknowledged: boolean;
}

export interface AlertNotificationProps {
  alerts: AlertItem[];
  onAcknowledge?: (alertId: string) => void;
  onDismiss?: (alertId: string) => void;
  maxHeight?: number;
  className?: string;
}

const severityConfig = {
  critical: {
    icon: AlertCircle,
    color: 'text-red-500',
    bgColor: 'bg-red-500/10',
    borderColor: 'border-red-500/30',
    label: '严重'
  },
  warning: {
    icon: AlertTriangle,
    color: 'text-yellow-500',
    bgColor: 'bg-yellow-500/10',
    borderColor: 'border-yellow-500/30',
    label: '警告'
  },
  info: {
    icon: Info,
    color: 'text-blue-500',
    bgColor: 'bg-blue-500/10',
    borderColor: 'border-blue-500/30',
    label: '信息'
  }
};

export const AlertNotification: React.FC<AlertNotificationProps> = ({
  alerts,
  onAcknowledge,
  onDismiss,
  maxHeight = 400,
  className
}) => {
  const [filter, setFilter] = useState<'all' | 'unacknowledged'>('all');

  const filteredAlerts = alerts.filter(alert => {
    if (filter === 'unacknowledged') {
      return !alert.acknowledged;
    }
    return true;
  });

  const criticalCount = alerts.filter(a => a.severity === 'critical' && !a.acknowledged).length;
  const warningCount = alerts.filter(a => a.severity === 'warning' && !a.acknowledged).length;

  return (
    <Card className={className}>
      <CardHeader className="pb-3">
        <div className="flex items-center justify-between">
          <CardTitle className="flex items-center gap-2 text-base">
            <Bell className="h-5 w-5" />
            告警通知
            {(criticalCount > 0 || warningCount > 0) && (
              <Badge variant="destructive" className="ml-2">
                {criticalCount + warningCount}
              </Badge>
            )}
          </CardTitle>
          <div className="flex items-center gap-2">
            <Button
              variant={filter === 'all' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setFilter('all')}
            >
              全部
            </Button>
            <Button
              variant={filter === 'unacknowledged' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setFilter('unacknowledged')}
            >
              未确认
            </Button>
          </div>
        </div>
        
        {/* 告警统计 */}
        <div className="flex items-center gap-4 mt-2">
          {criticalCount > 0 && (
            <div className="flex items-center gap-1 text-red-500 text-sm">
              <AlertCircle className="h-4 w-4" />
              <span>{criticalCount} 严重</span>
            </div>
          )}
          {warningCount > 0 && (
            <div className="flex items-center gap-1 text-yellow-500 text-sm">
              <AlertTriangle className="h-4 w-4" />
              <span>{warningCount} 警告</span>
            </div>
          )}
          {criticalCount === 0 && warningCount === 0 && (
            <div className="flex items-center gap-1 text-green-500 text-sm">
              <CheckCircle className="h-4 w-4" />
              <span>系统正常</span>
            </div>
          )}
        </div>
      </CardHeader>
      
      <CardContent>
        <ScrollArea className="pr-4" style={{ height: maxHeight }}>
          <div className="space-y-3">
            {filteredAlerts.length === 0 ? (
              <div className="text-center py-8 text-muted-foreground">
                <CheckCircle className="h-12 w-12 mx-auto mb-2 opacity-50" />
                <p>暂无告警</p>
              </div>
            ) : (
              filteredAlerts.map((alert) => {
                const config = severityConfig[alert.severity];
                const Icon = config.icon;
                
                return (
                  <div
                    key={alert.id}
                    className={cn(
                      "p-4 rounded-lg border transition-all",
                      config.bgColor,
                      config.borderColor,
                      alert.acknowledged && "opacity-60"
                    )}
                  >
                    <div className="flex items-start justify-between">
                      <div className="flex items-start gap-3">
                        <Icon className={cn("h-5 w-5 mt-0.5", config.color)} />
                        <div className="flex-1 min-w-0">
                          <div className="flex items-center gap-2">
                            <span className="font-medium text-sm">
                              {alert.title}
                            </span>
                            <Badge 
                              variant="outline" 
                              className={cn("text-xs", config.color)}
                            >
                              {config.label}
                            </Badge>
                          </div>
                          <p className="text-sm text-muted-foreground mt-1">
                            {alert.message}
                          </p>
                          <div className="flex items-center gap-3 mt-2 text-xs text-muted-foreground">
                            <span className="flex items-center gap-1">
                              <Clock className="h-3 w-3" />
                              {formatDistanceToNow(new Date(alert.timestamp), {
                                addSuffix: true,
                                locale: zhCN
                              })}
                            </span>
                            <span>服务: {alert.service}</span>
                          </div>
                        </div>
                      </div>
                      
                      <div className="flex items-center gap-1 ml-2">
                        {!alert.acknowledged && onAcknowledge && (
                          <Button
                            variant="ghost"
                            size="icon"
                            className="h-7 w-7"
                            onClick={() => onAcknowledge(alert.id)}
                            title="确认告警"
                          >
                            <CheckCircle className="h-4 w-4" />
                          </Button>
                        )}
                        {onDismiss && (
                          <Button
                            variant="ghost"
                            size="icon"
                            className="h-7 w-7"
                            onClick={() => onDismiss(alert.id)}
                            title="Dismiss"
                          >
                            <X className="h-4 w-4" />
                          </Button>
                        )}
                      </div>
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </ScrollArea>
      </CardContent>
    </Card>
  );
};

export default AlertNotification;
