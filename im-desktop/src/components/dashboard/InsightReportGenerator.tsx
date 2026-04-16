/**
 * 洞察报告生成器 - InsightReportGenerator
 * 功能#27 - 业务洞察仪表盘
 * 模块: im-desktop
 */

import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Badge } from '@/components/ui/badge';
import { Checkbox } from '@/components/ui/checkbox';
import { 
  FileText, 
  Sparkles, 
  Download,
  Loader2,
  CheckCircle,
  AlertCircle,
  TrendingUp,
  TrendingDown,
  Lightbulb,
  AlertTriangle
} from 'lucide-react';
import { 
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger
} from '@/components/ui/dialog';

export interface InsightReportData {
  id: string;
  title: string;
  summary: string;
  recommendations: string[];
  riskFactors: string[];
  opportunities: string[];
  createdAt: string;
  metrics: string[];
}

export interface InsightReportGeneratorProps {
  onGenerate?: (report: InsightReportData) => void;
  className?: string;
}

const availableMetrics = [
  { id: 'revenue', label: '营收分析', checked: true },
  { id: 'users', label: '用户增长', checked: true },
  { id: 'conversion', label: '转化漏斗', checked: true },
  { id: 'retention', label: '留存分析', checked: false },
  { id: 'engagement', label: '用户活跃度', checked: false },
  { id: 'churn', label: '流失预警', checked: false }
];

export const InsightReportGenerator: React.FC<InsightReportGeneratorProps> = ({
  onGenerate,
  className
}) => {
  const [isGenerating, setIsGenerating] = useState(false);
  const [isComplete, setIsComplete] = useState(false);
  const [reportTitle, setReportTitle] = useState('');
  const [selectedMetrics, setSelectedMetrics] = useState<string[]>(
    availableMetrics.filter(m => m.checked).map(m => m.id)
  );
  const [generatedReport, setGeneratedReport] = useState<InsightReportData | null>(null);

  const handleGenerate = async () => {
    if (!reportTitle) return;
    
    setIsGenerating(true);
    setIsComplete(false);

    // 模拟AI生成过程
    await new Promise(resolve => setTimeout(resolve, 3000));

    const mockReport: InsightReportData = {
      id: `report-${Date.now()}`,
      title: reportTitle,
      summary: `基于${selectedMetrics.length}个核心指标的分析，本周期业务整体呈现稳定增长态势。营收环比增长12.5%，用户活跃度提升8.3%，但转化率有轻微下滑，建议重点关注用户转化路径优化。`,
      recommendations: [
        '优化首单转化流程，减少用户流失节点',
        '加强高价值用户的精细化运营策略',
        '提升推荐算法准确度，增加个性化内容',
        '完善新用户引导流程，提高7日留存率'
      ],
      riskFactors: [
        '用户增长率环比放缓，需警惕市场饱和',
        '部分品类转化率持续下降',
        '竞品促销活动对订单量产生冲击'
      ],
      opportunities: [
        '下沉市场渗透率仍有较大提升空间',
        '会员体系升级有望提升客单价',
        '直播电商渠道增长潜力巨大'
      ],
      createdAt: new Date().toISOString(),
      metrics: selectedMetrics
    };

    setGeneratedReport(mockReport);
    setIsGenerating(false);
    setIsComplete(true);
    onGenerate?.(mockReport);
  };

  const toggleMetric = (metricId: string) => {
    setSelectedMetrics(prev => 
      prev.includes(metricId)
        ? prev.filter(id => id !== metricId)
        : [...prev, metricId]
    );
  };

  const handleExport = () => {
    if (!generatedReport) return;
    
    const blob = new Blob([JSON.stringify(generatedReport, null, 2)], {
      type: 'application/json'
    });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${generatedReport.title}.json`;
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <Card className={className}>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Sparkles className="h-5 w-5" />
          AI 洞察报告生成器
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="space-y-2">
          <label className="text-sm font-medium">报告标题</label>
          <Input
            placeholder="输入报告标题..."
            value={reportTitle}
            onChange={(e) => setReportTitle(e.target.value)}
          />
        </div>

        <div className="space-y-2">
          <label className="text-sm font-medium">选择分析指标</label>
          <div className="grid grid-cols-2 gap-3">
            {availableMetrics.map((metric) => (
              <div key={metric.id} className="flex items-center space-x-2">
                <Checkbox
                  id={metric.id}
                  checked={selectedMetrics.includes(metric.id)}
                  onCheckedChange={() => toggleMetric(metric.id)}
                />
                <label
                  htmlFor={metric.id}
                  className="text-sm cursor-pointer"
                >
                  {metric.label}
                </label>
              </div>
            ))}
          </div>
        </div>

        <Button 
          className="w-full"
          onClick={handleGenerate}
          disabled={isGenerating || !reportTitle || selectedMetrics.length === 0}
        >
          {isGenerating ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              生成中...
            </>
          ) : (
            <>
              <Sparkles className="mr-2 h-4 w-4" />
              生成洞察报告
            </>
          )}
        </Button>

        {isComplete && generatedReport && (
          <Dialog>
            <DialogTrigger asChild>
              <Button variant="outline" className="w-full">
                <FileText className="mr-2 h-4 w-4" />
                查看报告
              </Button>
            </DialogTrigger>
            <DialogContent className="max-w-2xl max-h-[80vh] overflow-y-auto">
              <DialogHeader>
                <DialogTitle>{generatedReport.title}</DialogTitle>
                <DialogDescription>
                  生成时间: {new Date(generatedReport.createdAt).toLocaleString('zh-CN')}
                </DialogDescription>
              </DialogHeader>

              <div className="space-y-6 py-4">
                {/* 报告摘要 */}
                <div className="bg-muted p-4 rounded-lg">
                  <h4 className="font-medium mb-2">报告摘要</h4>
                  <p className="text-sm text-muted-foreground">
                    {generatedReport.summary}
                  </p>
                </div>

                {/* 核心建议 */}
                <div>
                  <h4 className="font-medium mb-3 flex items-center gap-2">
                    <Lightbulb className="h-4 w-4 text-yellow-500" />
                    核心建议
                  </h4>
                  <ul className="space-y-2">
                    {generatedReport.recommendations.map((rec, index) => (
                      <li key={index} className="flex items-start gap-2 text-sm">
                        <CheckCircle className="h-4 w-4 text-green-500 mt-0.5 shrink-0" />
                        {rec}
                      </li>
                    ))}
                  </ul>
                </div>

                {/* 风险因素 */}
                <div>
                  <h4 className="font-medium mb-3 flex items-center gap-2">
                    <AlertTriangle className="h-4 w-4 text-red-500" />
                    风险因素
                  </h4>
                  <ul className="space-y-2">
                    {generatedReport.riskFactors.map((risk, index) => (
                      <li key={index} className="flex items-start gap-2 text-sm">
                        <TrendingDown className="h-4 w-4 text-red-500 mt-0.5 shrink-0" />
                        {risk}
                      </li>
                    ))}
                  </ul>
                </div>

                {/* 增长机会 */}
                <div>
                  <h4 className="font-medium mb-3 flex items-center gap-2">
                    <TrendingUp className="h-4 w-4 text-green-500" />
                    增长机会
                  </h4>
                  <ul className="space-y-2">
                    {generatedReport.opportunities.map((opp, index) => (
                      <li key={index} className="flex items-start gap-2 text-sm">
                        <AlertCircle className="h-4 w-4 text-blue-500 mt-0.5 shrink-0" />
                        {opp}
                      </li>
                    ))}
                  </ul>
                </div>
              </div>

              <DialogFooter>
                <Button onClick={handleExport}>
                  <Download className="mr-2 h-4 w-4" />
                  导出报告
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        )}
      </CardContent>
    </Card>
  );
};

export default InsightReportGenerator;
