import { EventEmitter } from 'events';

/**
 * 业务洞察仪表盘组件
 * 功能#27: 业务洞察仪表盘
 */
export class Dashboard {
    private container: HTMLElement;
    private panels: Map<string, Panel> = new Map();
    private eventEmitter: EventEmitter;
    private refreshInterval: number = 5000;
    private intervalId?: number;

    constructor(containerId: string) {
        const container = document.getElementById(containerId);
        if (!container) {
            throw new Error(`Container #${containerId} not found`);
        }
        this.container = container;
        this.eventEmitter = new EventEmitter();
        this.init();
    }

    private init(): void {
        this.render();
        this.startAutoRefresh();
        console.log('[Dashboard] 业务洞察仪表盘已初始化');
    }

    /**
     * 渲染仪表盘布局
     */
    private render(): void {
        this.container.innerHTML = `
            <div class="dashboard-container">
                <div class="dashboard-header">
                    <h2>业务洞察仪表盘</h2>
                    <div class="dashboard-controls">
                        <button id="refresh-btn">刷新</button>
                        <select id="time-range">
                            <option value="1h">最近1小时</option>
                            <option value="24h" selected>最近24小时</option>
                            <option value="7d">最近7天</option>
                            <option value="30d">最近30天</option>
                        </select>
                    </div>
                </div>
                <div class="dashboard-grid">
                    <div class="panel" id="metrics-panel">
                        <h3>核心指标</h3>
                        <div class="metrics-content"></div>
                    </div>
                    <div class="panel" id="chart-panel">
                        <h3>趋势图表</h3>
                        <canvas id="trend-chart"></canvas>
                    </div>
                    <div class="panel" id="alert-panel">
                        <h3>告警概览</h3>
                        <div class="alert-content"></div>
                    </div>
                    <div class="panel" id="top-panel">
                        <h3>TOP统计</h3>
                        <div class="top-content"></div>
                    </div>
                </div>
            </div>
        `;

        // 绑定事件
        document.getElementById('refresh-btn')?.addEventListener('click', () => {
            this.refresh();
        });

        document.getElementById('time-range')?.addEventListener('change', (e) => {
            this.onTimeRangeChange((e.target as HTMLSelectElement).value);
        });
    }

    /**
     * 添加面板
     */
    public addPanel(id: string, panel: Panel): void {
        this.panels.set(id, panel);
        panel.mount(this.container);
    }

    /**
     * 移除面板
     */
    public removePanel(id: string): void {
        const panel = this.panels.get(id);
        if (panel) {
            panel.destroy();
            this.panels.delete(id);
        }
    }

    /**
     * 刷新仪表盘数据
     */
    public async refresh(): Promise<void> {
        console.log('[Dashboard] 刷新数据...');
        
        try {
            // 获取核心指标
            const metrics = await this.fetchMetrics();
            this.updateMetricsPanel(metrics);

            // 获取趋势数据
            const trends = await this.fetchTrends();
            this.updateChartPanel(trends);

            // 获取告警数据
            const alerts = await this.fetchAlerts();
            this.updateAlertPanel(alerts);

            // 获取TOP统计
            const topStats = await this.fetchTopStats();
            this.updateTopPanel(topStats);

            this.eventEmitter.emit('refresh', { metrics, trends, alerts, topStats });
        } catch (error) {
            console.error('[Dashboard] 刷新失败:', error);
        }
    }

    /**
     * 开始自动刷新
     */
    private startAutoRefresh(): void {
        this.intervalId = window.setInterval(() => {
            this.refresh();
        }, this.refreshInterval);
    }

    /**
     * 停止自动刷新
     */
    public stopAutoRefresh(): void {
        if (this.intervalId) {
            clearInterval(this.intervalId);
            this.intervalId = undefined;
        }
    }

    /**
     * 设置刷新间隔
     */
    public setRefreshInterval(intervalMs: number): void {
        this.refreshInterval = intervalMs;
        this.stopAutoRefresh();
        this.startAutoRefresh();
    }

    /**
     * 时间范围变更
     */
    private onTimeRangeChange(range: string): void {
        console.log('[Dashboard] 时间范围变更:', range);
        this.refresh();
    }

    // ============ 数据获取方法 ============

    private async fetchMetrics(): Promise<MetricsData> {
        // 实际实现中从API获取
        return {
            totalUsers: 15234,
            activeUsers: 3456,
            messageCount: 123456,
            onlineUsers: 890
        };
    }

    private async fetchTrends(): Promise<TrendData[]> {
        // 实际实现中从API获取
        return Array.from({ length: 24 }, (_, i) => ({
            time: `${i}:00`,
            value: Math.floor(Math.random() * 1000)
        }));
    }

    private async fetchAlerts(): Promise<AlertData[]> {
        // 实际实现中从API获取
        return [
            { level: 'critical', message: '消息队列积压', count: 2 },
            { level: 'warning', message: '响应时间升高', count: 5 },
            { level: 'info', message: '新用户注册高峰', count: 1 }
        ];
    }

    private async fetchTopStats(): Promise<TopStat[]> {
        // 实际实现中从API获取
        return [
            { name: '北京', value: 3456 },
            { name: '上海', value: 2890 },
            { name: '广州', value: 2345 },
            { name: '深圳', value: 2123 },
            { name: '杭州', value: 1890 }
        ];
    }

    // ============ 更新面板方法 ============

    private updateMetricsPanel(metrics: MetricsData): void {
        const content = this.container.querySelector('.metrics-content');
        if (content) {
            content.innerHTML = `
                <div class="metric-item">
                    <span class="metric-label">总用户</span>
                    <span class="metric-value">${metrics.totalUsers.toLocaleString()}</span>
                </div>
                <div class="metric-item">
                    <span class="metric-label">活跃用户</span>
                    <span class="metric-value">${metrics.activeUsers.toLocaleString()}</span>
                </div>
                <div class="metric-item">
                    <span class="metric-label">消息数</span>
                    <span class="metric-value">${metrics.messageCount.toLocaleString()}</span>
                </div>
                <div class="metric-item">
                    <span class="metric-label">在线用户</span>
                    <span class="metric-value">${metrics.onlineUsers.toLocaleString()}</span>
                </div>
            `;
        }
    }

    private updateChartPanel(trends: TrendData[]): void {
        const canvas = this.container.querySelector('#trend-chart') as HTMLCanvasElement;
        if (canvas) {
            const ctx = canvas.getContext('2d');
            if (ctx) {
                // 简化版图表绘制
                ctx.clearRect(0, 0, canvas.width, canvas.height);
                ctx.beginPath();
                ctx.strokeStyle = '#007acc';
                
                const max = Math.max(...trends.map(t => t.value));
                const stepX = canvas.width / trends.length;
                
                trends.forEach((trend, i) => {
                    const x = i * stepX;
                    const y = canvas.height - (trend.value / max * canvas.height);
                    if (i === 0) {
                        ctx.moveTo(x, y);
                    } else {
                        ctx.lineTo(x, y);
                    }
                });
                
                ctx.stroke();
            }
        }
    }

    private updateAlertPanel(alerts: AlertData[]): void {
        const content = this.container.querySelector('.alert-content');
        if (content) {
            content.innerHTML = alerts.map(alert => `
                <div class="alert-item alert-${alert.level}">
                    <span class="alert-message">${alert.message}</span>
                    <span class="alert-count">${alert.count}</span>
                </div>
            `).join('');
        }
    }

    private updateTopPanel(stats: TopStat[]): void {
        const content = this.container.querySelector('.top-content');
        if (content) {
            content.innerHTML = stats.map((stat, i) => `
                <div class="top-item">
                    <span class="top-rank">${i + 1}</span>
                    <span class="top-name">${stat.name}</span>
                    <span class="top-value">${stat.value.toLocaleString()}</span>
                </div>
            `).join('');
        }
    }

    /**
     * 销毁仪表盘
     */
    public destroy(): void {
        this.stopAutoRefresh();
        this.panels.forEach(panel => panel.destroy());
        this.panels.clear();
        this.eventEmitter.removeAllListeners();
    }
}

// ============ 类型定义 ============

interface Panel {
    mount(container: HTMLElement): void;
    destroy(): void;
}

interface MetricsData {
    totalUsers: number;
    activeUsers: number;
    messageCount: number;
    onlineUsers: number;
}

interface TrendData {
    time: string;
    value: number;
}

interface AlertData {
    level: 'critical' | 'warning' | 'info';
    message: string;
    count: number;
}

interface TopStat {
    name: string;
    value: number;
}

export default Dashboard;
