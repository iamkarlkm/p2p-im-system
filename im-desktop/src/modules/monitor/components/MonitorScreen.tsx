import React, { useEffect, useState } from 'react';
import { Card, Row, Col, Statistic, Table, Tag, Progress } from 'antd';
import { Line, Gauge } from '@ant-design/charts';
import { useWebSocket } from '../../hooks/useWebSocket';
import './MonitorScreen.css';

interface SystemMetrics {
  cpuUsage: number;
  memoryUsage: number;
  diskUsage: number;
  networkIO: { time: string; value: number }[];
  activeConnections: number;
  messageThroughput: number;
}

interface AlertItem {
  id: string;
  level: 'warning' | 'error' | 'critical';
  message: string;
  timestamp: string;
  source: string;
}

const MonitorScreen: React.FC = () => {
  const [metrics, setMetrics] = useState<SystemMetrics>({
    cpuUsage: 0,
    memoryUsage: 0,
    diskUsage: 0,
    networkIO: [],
    activeConnections: 0,
    messageThroughput: 0,
  });
  
  const [alerts, setAlerts] = useState<AlertItem[]>([]);
  const { lastMessage } = useWebSocket('ws://localhost:8080/monitor');
  
  useEffect(() => {
    if (lastMessage) {
      const data = JSON.parse(lastMessage.data);
      if (data.type === 'metrics') {
        setMetrics(prev => ({
          ...data.metrics,
          networkIO: [...prev.networkIO, data.metrics.networkIO].slice(-20),
        }));
      } else if (data.type === 'alert') {
        setAlerts(prev => [data.alert, ...prev].slice(0, 50));
      }
    }
  }, [lastMessage]);
  
  const alertColumns = [
    { title: '时间', dataIndex: 'timestamp', key: 'timestamp' },
    { title: '级别', dataIndex: 'level', key: 'level', 
      render: (level: string) => {
        const colors = { warning: 'orange', error: 'red', critical: 'purple' };
        return <Tag color={colors[level as keyof typeof colors]}>{level.toUpperCase()}</Tag>;
      }
    },
    { title: '来源', dataIndex: 'source', key: 'source' },
    { title: '消息', dataIndex: 'message', key: 'message' },
  ];
  
  const gaugeConfig = (value: number, title: string) => ({
    percent: value / 100,
    range: { ticks: [0, 0.25, 0.5, 0.75, 1] },
    indicator: { pointer: { style: { stroke: '#D0D0D0' } } },
    statistic: {
      content: { formatter: () => `${value.toFixed(1)}%` },
      title: { content: title },
    },
  });
  
  return (
    <div className="monitor-screen">
      <h1>实时监控大屏</h1>
      
      <Row gutter={16}>
        <Col span={6}>
          <Card><Gauge {...gaugeConfig(metrics.cpuUsage, 'CPU使用率')} /></Card>
        </Col>
        <Col span={6}>
          <Card><Gauge {...gaugeConfig(metrics.memoryUsage, '内存使用率')} /></Card>
        </Col>
        <Col span={6}>
          <Card><Gauge {...gaugeConfig(metrics.diskUsage, '磁盘使用率')} /></Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic title="活跃连接数" value={metrics.activeConnections} />
            <Statistic title="消息吞吐量" value={metrics.messageThroughput} suffix="/s" />
          </Card>
        </Col>
      </Row>
      
      <Row gutter={16} style={{ marginTop: 16 }}>
        <Col span={12}>
          <Card title="网络IO趋势">
            <Line
              data={metrics.networkIO}
              xField="time"
              yField="value"
              smooth
              animation={false}
            />
          </Card>
        </Col>
        <Col span={12}>
          <Card title="实时告警">
            <Table
              dataSource={alerts}
              columns={alertColumns}
              rowKey="id"
              pagination={{ pageSize: 5 }}
              size="small"
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default MonitorScreen;
