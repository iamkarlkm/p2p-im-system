import React, { useEffect, useState } from 'react';
import { Card, Row, Col, Statistic, Table, DatePicker } from 'antd';
import { Line, Pie, Column } from '@ant-design/plots';
import type { InsightData, KPIMetric, TrendData } from '../types/insight';

/**
 * 业务洞察仪表盘组件
 * 功能#27: 业务洞察仪表盘
 */
const InsightDashboard: React.FC = () => {
  const [loading, setLoading] = useState(true);
  const [kpiData, setKpiData] = useState<KPIMetric[]>([]);
  const [trendData, setTrendData] = useState<TrendData[]>([]);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/v1/insight/dashboard');
      const data = await response.json();
      setKpiData(data.kpis || []);
      setTrendData(data.trends || []);
    } catch (error) {
      console.error('Failed to fetch dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const lineConfig = {
    data: trendData,
    xField: 'date',
    yField: 'value',
    seriesField: 'category',
    smooth: true,
    animation: {
      appear: {
        animation: 'path-in',
        duration: 1000,
      },
    },
  };

  const pieConfig = {
    data: kpiData.filter(k => k.type === 'distribution'),
    angleField: 'value',
    colorField: 'name',
    radius: 0.8,
  };

  return (
    <div className="insight-dashboard">
      <Row gutter={[16, 16]}>
        <Col span={24}>
          <Card title="业务洞察仪表盘" extra={<DatePicker.RangePicker />}>
            <Row gutter={16}>
              <Col span={6}>
                <Statistic title="日活跃用户" value={12580} precision={0} suffix="人" />
              </Col>
              <Col span={6}>
                <Statistic title="消息发送量" value={892341} precision={0} suffix="条" />
              </Col>
              <Col span={6}>
                <Statistic title="平均会话时长" value={12.5} precision={1} suffix="分钟" />
              </Col>
              <Col span={6}>
                <Statistic title="用户满意度" value={94.2} precision={1} suffix="%" />
              </Col>
            </Row>
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col span={16}>
          <Card title="业务趋势分析" loading={loading}>
            <Line {...lineConfig} />
          </Card>
        </Col>
        <Col span={8}>
          <Card title="指标分布" loading={loading}>
            <Pie {...pieConfig} />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col span={24}>
          <Card title="关键指标详情">
            <Table
              dataSource={kpiData}
              columns={[
                { title: '指标名称', dataIndex: 'name', key: 'name' },
                { title: '当前值', dataIndex: 'value', key: 'value' },
                { title: '环比', dataIndex: 'change', key: 'change' },
                { title: '目标', dataIndex: 'target', key: 'target' },
                { title: '完成率', dataIndex: 'completion', key: 'completion' },
              ]}
              rowKey="id"
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default InsightDashboard;
