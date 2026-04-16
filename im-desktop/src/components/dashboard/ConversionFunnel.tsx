/**
 * 转化漏斗组件 - ConversionFunnel
 * 功能#27 - 业务洞察仪表盘
 * 模块: im-desktop
 */

import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';
import { Badge } from '@/components/ui/badge';
import { 
  Funnel,
  Users,
  MousePointer,
  ShoppingCart,
  CheckCircle,
  ArrowRight,
  TrendingDown
} from 'lucide-react';

export interface FunnelStage {
  name: string;
  count: number;
  conversion: number;
  dropOff: number;
  icon?: React.ReactNode;
  color?: string;
}

export interface ConversionFunnelProps {
  stages: FunnelStage[];
  className?: string;
  showDetails?: boolean;
}

const defaultStages: FunnelStage[] = [
  {
    name: '访问用户',
    count: 100000,
    conversion: 100,
    dropOff: 0,
    icon: <Users className="h-5 w-5" />,
    color: '#0088FE'
  },
  {
    name: '浏览商品',
    count: 65000,
    conversion: 65,
    dropOff: 35,
    icon: <MousePointer className="h-5 w-5" />,
    color: '#00C49F'
  },
  {
    name: '加入购物车',
    count: 25000,
    conversion: 25,
    dropOff: 40,
    icon: <ShoppingCart className="h-5 w-5" />,
    color: '#FFBB28'
  },
  {
    name: '完成订单',
    count: 8500,
    conversion: 8.5,
    dropOff: 16.5,
    icon: <CheckCircle className="h-5 w-5" />,
    color: '#FF8042'
  }
];

export const ConversionFunnel: React.FC<ConversionFunnelProps> = ({
  stages = defaultStages,
  className,
  showDetails = true
}) => {
  const maxCount = Math.max(...stages.map(s => s.count));

  return (
    <Card className={className}>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Funnel className="h-5 w-5" />
          转化漏斗分析
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-6">
        {stages.map((stage, index) => (
          <div key={index} className="space-y-2">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div 
                  className="p-2 rounded-lg"
                  style={{ backgroundColor: `${stage.color}20`, color: stage.color }}
                >
                  {stage.icon}
                </div>
                <div>
                  <div className="font-medium">{stage.name}</div>
                  <div className="text-sm text-muted-foreground">
                    {stage.count.toLocaleString()} 人
                  </div>
                </div>
              </div>
              <div className="text-right">
                <div className="font-bold">{stage.conversion}%</div>
                <div className="text-xs text-muted-foreground">
                  转化率
                </div>
              </div>
            </div>
            
            <div className="relative">
              <div 
                className="h-3 rounded-full transition-all duration-500"
                style={{ 
                  width: `${(stage.count / maxCount) * 100}%`,
                  backgroundColor: stage.color
                }}
              />
              {showDetails && stage.dropOff > 0 && (
                <div className="flex items-center gap-1 mt-1 text-xs text-red-500">
                  <TrendingDown className="h-3 w-3" />
                  <span>流失 {stage.dropOff}%</span>
                </div>
              )}
            </div>

            {index < stages.length - 1 && (
              <div className="flex justify-center py-2">
                <ArrowRight className="h-4 w-4 text-muted-foreground rotate-90" />
              </div>
            )}
          </div>
        ))}

        <div className="pt-4 border-t">
          <div className="flex items-center justify-between text-sm">
            <span className="text-muted-foreground">整体转化率</span>
            <Badge variant="default" className="text-lg">
              {stages[stages.length - 1]?.conversion || 0}%
            </Badge>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default ConversionFunnel;
