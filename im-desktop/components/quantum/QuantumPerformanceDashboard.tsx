import React, { useState, useEffect, useCallback, useMemo } from 'react';
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Grid,
  Typography,
  LinearProgress,
  Chip,
  IconButton,
  Button,
  Alert,
  AlertTitle,
  Divider,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Tooltip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Tabs,
  Tab,
  useTheme,
} from '@mui/material';
import {
  Speed as SpeedIcon,
  Memory as MemoryIcon,
  NetworkCheck as NetworkIcon,
  Security as SecurityIcon,
  Warning as WarningIcon,
  Error as ErrorIcon,
  CheckCircle as CheckCircleIcon,
  TrendingUp as TrendingUpIcon,
  TrendingDown as TrendingDownIcon,
  TrendingFlat as TrendingFlatIcon,
  Refresh as RefreshIcon,
  Settings as SettingsIcon,
  PlayArrow as PlayIcon,
  Stop as StopIcon,
  Timeline as TimelineIcon,
} from '@mui/icons-material';
import { Line } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip as ChartTooltip,
  Legend,
  Filler,
} from 'chart.js';
import { useQuantumPerformance } from '../../hooks/useQuantumPerformance';
import { QuantumPerformanceMetrics, PerformanceAlert, BottleneckAnalysis } from '../../services/quantum/QuantumPerformanceMonitor';
import { OptimizationResult, OptimizationStrategy } from '../../services/quantum/QuantumPerformanceOptimizer';

// 注册Chart.js组件
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  ChartTooltip,
  Legend,
  Filler
);

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;
  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`quantum-performance-tabpanel-${index}`}
      aria-labelledby={`quantum-performance-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );
}

/**
 * 量子性能监控仪表板
 */
export const QuantumPerformanceDashboard: React.FC = () => {
  const theme = useTheme();
  const [activeTab, setActiveTab] = useState(0);
  const [selectedAlert, setSelectedAlert] = useState<PerformanceAlert | null>(null);
  const [settingsOpen, setSettingsOpen] = useState(false);
  
  const {
    metrics,
    history,
    alerts,
    isMonitoring,
    bottleneck,
    optimizationResults,
    startMonitoring,
    stopMonitoring,
    acknowledgeAlert,
    runOptimization,
    getPerformanceReport,
  } = useQuantumPerformance();

  // 处理标签页切换
  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setActiveTab(newValue);
  };

  // 处理告警确认
  const handleAcknowledgeAlert = (alertId: string) => {
    acknowledgeAlert(alertId);
    setSelectedAlert(null);
  };

  // 处理优化
  const handleOptimize = async (strategy: OptimizationStrategy) => {
    await runOptimization(strategy);
  };

  // 渲染指标卡片
  const renderMetricCard = (
    title: string,
    value: string | number,
    unit: string,
    icon: React.ReactNode,
    trend?: 'up' | 'down' | 'stable',
    color?: string
  ) => (
    <Card sx={{ height: '100%' }}>
      <CardContent>
        <Box display="flex" alignItems="center" mb={2}>
          <Box
            sx={{
              backgroundColor: color || theme.palette.primary.main,
              borderRadius: '50%',
              p: 1,
              mr: 2,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            {icon}
          </Box>
          <Typography variant="h6" component="div">
            {title}
          </Typography>
        </Box>
        <Typography variant="h3" component="div" gutterBottom>
          {value}
          <Typography variant="body2" component="span" color="text.secondary" sx={{ ml: 1 }}>
            {unit}
          </Typography>
        </Typography>
        {trend && (
          <Box display="flex" alignItems="center">
            {trend === 'up' && <TrendingUpIcon color="success" fontSize="small" />}
            {trend === 'down' && <TrendingDownIcon color="error" fontSize="small" />}
            {trend === 'stable' && <TrendingFlatIcon color="info" fontSize="small" />}
            <Typography variant="body2" color="text.secondary" sx={{ ml: 0.5 }}>
              {trend === 'up' ? '上升' : trend === 'down' ? '下降' : '稳定'}
            </Typography>
          </Box>
        )}
      </CardContent>
    </Card>
  );

  // 渲染状态芯片
  const renderStatusChip = (status: 'good' | 'warning' | 'critical') => {
    const configs = {
      good: { color: 'success' as const, icon: <CheckCircleIcon />, label: '良好' },
      warning: { color: 'warning' as const, icon: <WarningIcon />, label: '警告' },
      critical: { color: 'error' as const, icon: <ErrorIcon />, label: '严重' },
    };
    const config = configs[status];
    return (
      <Chip
        icon={config.icon}
        label={config.label}
        color={config.color}
        size="small"
      />
    );
  };

  // 计算状态
  const getOverallStatus = useMemo((): 'good' | 'warning' | 'critical' => {
    if (!metrics) return 'good';
    if (metrics.channelErrorRate > 0.5 || metrics.keyGenerationSuccessRate < 50) {
      return 'critical';
    }
    if (metrics.channelErrorRate > 0.1 || metrics.keyGenerationSuccessRate < 80) {
      return 'warning';
    }
    return 'good';
  }, [metrics]);

  // 性能图表数据
  const chartData = useMemo(() => {
    if (!history || history.metrics.length === 0) {
      return null;
    }

    const labels = history.metrics.map(m => 
      new Date(m.timestamp).toLocaleTimeString()
    );

    return {
      labels,
      datasets: [
        {
          label: '密钥生成速率 (bits/s)',
          data: history.metrics.map(m => m.keyGenerationRate),
          borderColor: theme.palette.primary.main,
          backgroundColor: theme.palette.primary.main + '20',
          fill: true,
          tension: 0.4,
        },
        {
          label: '信道吞吐量 (Mbps)',
          data: history.metrics.map(m => m.channelThroughput),
          borderColor: theme.palette.secondary.main,
          backgroundColor: theme.palette.secondary.main + '20',
          fill: true,
          tension: 0.4,
        },
        {
          label: '信道稳定性 (%)',
          data: history.metrics.map(m => m.channelStability),
          borderColor: theme.palette.success.main,
          backgroundColor: theme.palette.success.main + '20',
          fill: true,
          tension: 0.4,
        },
      ],
    };
  }, [history, theme]);

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    interaction: {
      mode: 'index' as const,
      intersect: false,
    },
    plugins: {
      legend: {
        position: 'top' as const,
      },
      title: {
        display: true,
        text: '性能趋势',
      },
    },
    scales: {
      y: {
        beginAtZero: true,
      },
    },
  };

  return (
    <Box sx={{ p: 3 }}>
      {/* 头部 */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1">
          量子通信性能监控
        </Typography>
        <Box display="flex" gap={2}>
          <Button
            variant={isMonitoring ? "contained" : "outlined"}
            color={isMonitoring ? "error" : "primary"}
            startIcon={isMonitoring ? <StopIcon /> : <PlayIcon />}
            onClick={isMonitoring ? stopMonitoring : startMonitoring}
          >
            {isMonitoring ? '停止监控' : '开始监控'}
          </Button>
          <IconButton onClick={() => setSettingsOpen(true)}>
            <SettingsIcon />
          </IconButton>
        </Box>
      </Box>

      {/* 整体状态 */}
      <Alert 
        severity={getOverallStatus === 'good' ? 'success' : getOverallStatus === 'warning' ? 'warning' : 'error'}
        sx={{ mb: 3 }}
      >
        <AlertTitle>系统状态</AlertTitle>
        当前系统整体状态：{getOverallStatus === 'good' ? '良好' : getOverallStatus === 'warning' ? '需要关注' : '需要立即处理'}
        {bottleneck && bottleneck.bottleneck !== 'none' && (
          <Typography variant="body2" sx={{ mt: 1 }}>
            检测到瓶颈: {bottleneck.description}
          </Typography>
        )}
      </Alert>

      {/* 指标概览 */}
      <Grid container spacing={3} mb={3}>
        <Grid item xs={12} sm={6} md={3}>
          {renderMetricCard(
            '密钥生成速率',
            metrics?.keyGenerationRate?.toFixed(0) || '0',
            'bits/s',
            <SecurityIcon sx={{ color: 'white' }} />,
            'stable',
            theme.palette.primary.main
          )}
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          {renderMetricCard(
            '信道吞吐量',
            metrics?.channelThroughput?.toFixed(2) || '0',
            'Mbps',
            <NetworkIcon sx={{ color: 'white' }} />,
            'up',
            theme.palette.secondary.main
          )}
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          {renderMetricCard(
            '信道稳定性',
            metrics?.channelStability?.toFixed(1) || '0',
            '%',
            <SpeedIcon sx={{ color: 'white' }} />,
            'stable',
            theme.palette.success.main
          )}
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          {renderMetricCard(
            '内存使用',
            metrics?.memoryUsage?.toFixed(0) || '0',
            'MB',
            <MemoryIcon sx={{ color: 'white' }} />,
            'down',
            theme.palette.info.main
          )}
        </Grid>
      </Grid>

      {/* 标签页 */}
      <Card>
        <Tabs
          value={activeTab}
          onChange={handleTabChange}
          indicatorColor="primary"
          textColor="primary"
          variant="scrollable"
          scrollButtons="auto"
        >
          <Tab label="实时图表" icon={<TimelineIcon />} iconPosition="start" />
          <Tab label="性能指标" icon={<SpeedIcon />} iconPosition="start" />
          <Tab 
            label={`告警 (${alerts.filter(a => !a.acknowledged).length})`} 
            icon={<WarningIcon />} 
            iconPosition="start" 
          />
          <Tab label="优化" icon={<SettingsIcon />} iconPosition="start" />
        </Tabs>

        {/* 实时图表 */}
        <TabPanel value={activeTab} index={0}>
          <Box sx={{ height: 400 }}>
            {chartData ? (
              <Line data={chartData} options={chartOptions} />
            ) : (
              <Box 
                display="flex" 
                alignItems="center" 
                justifyContent="center" 
                height="100%"
              >
                <Typography color="text.secondary">
                  暂无历史数据，请开始监控
                </Typography>
              </Box>
            )}
          </Box>
        </TabPanel>

        {/* 性能指标 */}
        <TabPanel value={activeTab} index={1}>
          <TableContainer component={Paper} variant="outlined">
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>指标</TableCell>
                  <TableCell align="right">当前值</TableCell>
                  <TableCell align="right">阈值</TableCell>
                  <TableCell align="center">状态</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                <TableRow>
                  <TableCell>密钥生成速率</TableCell>
                  <TableCell align="right">{metrics?.keyGenerationRate?.toFixed(0)} bits/s</TableCell>
                  <TableCell align="right">≥ 1000 bits/s</TableCell>
                  <TableCell align="center">
                    {(metrics?.keyGenerationRate || 0) >= 1000 ? 
                      renderStatusChip('good') : 
                      (metrics?.keyGenerationRate || 0) >= 500 ? 
                        renderStatusChip('warning') : renderStatusChip('critical')}
                  </TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>密钥生成延迟</TableCell>
                  <TableCell align="right">{metrics?.keyGenerationLatency?.toFixed(1)} ms</TableCell>
                  <TableCell align="right">≤ 100 ms</TableCell>
                  <TableCell align="center">
                    {(metrics?.keyGenerationLatency || 0) <= 100 ? 
                      renderStatusChip('good') : 
                      (metrics?.keyGenerationLatency || 0) <= 200 ? 
                        renderStatusChip('warning') : renderStatusChip('critical')}
                  </TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>信道吞吐量</TableCell>
                  <TableCell align="right">{metrics?.channelThroughput?.toFixed(2)} Mbps</TableCell>
                  <TableCell align="right">≥ 10 Mbps</TableCell>
                  <TableCell align="center">
                    {(metrics?.channelThroughput || 0) >= 10 ? 
                      renderStatusChip('good') : 
                      (metrics?.channelThroughput || 0) >= 5 ? 
                        renderStatusChip('warning') : renderStatusChip('critical')}
                  </TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>信道误码率</TableCell>
                  <TableCell align="right">{metrics?.channelErrorRate?.toFixed(3)}%</TableCell>
                  <TableCell align="right">≤ 0.1%</TableCell>
                  <TableCell align="center">
                    {(metrics?.channelErrorRate || 0) <= 0.1 ? 
                      renderStatusChip('good') : 
                      (metrics?.channelErrorRate || 0) <= 0.5 ? 
                        renderStatusChip('warning') : renderStatusChip('critical')}
                  </TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>信道稳定性</TableCell>
                  <TableCell align="right">{metrics?.channelStability?.toFixed(1)}%</TableCell>
                  <TableCell align="right">≥ 80%</TableCell>
                  <TableCell align="center">
                    {(metrics?.channelStability || 0) >= 80 ? 
                      renderStatusChip('good') : 
                      (metrics?.channelStability || 0) >= 50 ? 
                        renderStatusChip('warning') : renderStatusChip('critical')}
                  </TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>QKD密钥速率</TableCell>
                  <TableCell align="right">{metrics?.qkdKeyRate?.toFixed(1)} kbps</TableCell>
                  <TableCell align="right">≥ 1 kbps</TableCell>
                  <TableCell align="center">
                    {(metrics?.qkdKeyRate || 0) >= 1 ? 
                      renderStatusChip('good') : renderStatusChip('warning')}
                  </TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>CPU使用率</TableCell>
                  <TableCell align="right">{metrics?.cpuUsage?.toFixed(1)}%</TableCell>
                  <TableCell align="right">≤ 80%</TableCell>
                  <TableCell align="center">
                    {(metrics?.cpuUsage || 0) <= 80 ? 
                      renderStatusChip('good') : 
                      (metrics?.cpuUsage || 0) <= 90 ? 
                        renderStatusChip('warning') : renderStatusChip('critical')}
                  </TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </TableContainer>
        </TabPanel>

        {/* 告警 */}
        <TabPanel value={activeTab} index={2}>
          {alerts.length === 0 ? (
            <Box textAlign="center" py={4}>
              <CheckCircleIcon color="success" sx={{ fontSize: 64, mb: 2 }} />
              <Typography variant="h6" color="text.secondary">
                暂无告警
              </Typography>
            </Box>
          ) : (
            <TableContainer component={Paper} variant="outlined">
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>级别</TableCell>
                    <TableCell>时间</TableCell>
                    <TableCell>指标</TableCell>
                    <TableCell>消息</TableCell>
                    <TableCell>状态</TableCell>
                    <TableCell>操作</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {alerts.map((alert) => (
                    <TableRow key={alert.id}>
                      <TableCell>
                        <Chip
                          size="small"
                          color={alert.type === 'critical' ? 'error' : alert.type === 'warning' ? 'warning' : 'info'}
                          label={alert.type === 'critical' ? '严重' : alert.type === 'warning' ? '警告' : '信息'}
                        />
                      </TableCell>
                      <TableCell>
                        {new Date(alert.timestamp).toLocaleString()}
                      </TableCell>
                      <TableCell>{alert.metric}</TableCell>
                      <TableCell>{alert.message}</TableCell>
                      <TableCell>
                        {alert.acknowledged ? 
                          <Chip size="small" color="success" label="已确认" /> : 
                          <Chip size="small" color="default" label="未确认" />}
                      </TableCell>
                      <TableCell>
                        {!alert.acknowledged && (
                          <Button
                            size="small"
                            variant="outlined"
                            onClick={() => handleAcknowledgeAlert(alert.id)}
                          >
                            确认
                          </Button>
                        )}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </TabPanel>

        {/* 优化 */}
        <TabPanel value={activeTab} index={3}>
          <Grid container spacing={3}>
            <Grid item xs={12} md={6}>
              <Card variant="outlined">
                <CardHeader title="自动优化" />
                <CardContent>
                  <Typography variant="body2" color="text.secondary" paragraph>
                    选择以下优化策略来提升量子通信性能
                  </Typography>
                  <Box display="flex" flexWrap="wrap" gap={1}>
                    {(['adaptive_key_rate', 'dynamic_buffer_sizing', 'error_correction_optimization', 'batch_processing'] as OptimizationStrategy[]).map((strategy) => (
                      <Button
                        key={strategy}
                        variant="outlined"
                        size="small"
                        onClick={() => handleOptimize(strategy)}
                      >
                        {strategy === 'adaptive_key_rate' && '自适应密钥速率'}
                        {strategy === 'dynamic_buffer_sizing' && '动态缓冲区'}
                        {strategy === 'error_correction_optimization' && '错误纠正优化'}
                        {strategy === 'batch_processing' && '批处理优化'}
                      </Button>
                    ))}
                  </Box>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} md={6}>
              <Card variant="outlined">
                <CardHeader title="瓶颈分析" />
                <CardContent>
                  {bottleneck && bottleneck.bottleneck !== 'none' ? (
                    <>
                      <Alert severity={bottleneck.severity === 'critical' ? 'error' : bottleneck.severity === 'high' ? 'warning' : 'info'}>
                        <AlertTitle>检测到性能瓶颈</AlertTitle>
                        {bottleneck.description}
                      </Alert>
                      <Typography variant="subtitle2" sx={{ mt: 2, mb: 1 }}>
                        推荐操作:
                      </Typography>
                      <List dense>
                        {bottleneck.recommendations.map((rec, index) => (
                          <ListItem key={index}>
                            <ListItemIcon sx={{ minWidth: 32 }}>
                              <CheckCircleIcon color="primary" fontSize="small" />
                            </ListItemIcon>
                            <ListItemText primary={rec} />
                          </ListItem>
                        ))}
                      </List>
                    </>
                  ) : (
                    <Box textAlign="center" py={2}>
                      <CheckCircleIcon color="success" sx={{ fontSize: 48, mb: 1 }} />
                      <Typography color="text.secondary">
                        未检测到性能瓶颈
                      </Typography>
                    </Box>
                  )}
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        </TabPanel>
      </Card>

      {/* 设置对话框 */}
      <Dialog open={settingsOpen} onClose={() => setSettingsOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>性能监控设置</DialogTitle>
        <DialogContent>
          <Typography variant="body2" color="text.secondary">
            设置功能将在后续版本中提供
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setSettingsOpen(false)}>关闭</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default QuantumPerformanceDashboard;
