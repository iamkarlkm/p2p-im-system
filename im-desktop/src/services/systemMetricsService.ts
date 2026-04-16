/**
 * 系统指标监控服务 - SystemMetricsService
 * 功能#29 - 实时监控大屏
 * 模块: im-desktop
 */

import { useState, useEffect, useCallback, useRef } from 'react';
import { api } from '@/lib/api';

// 系统指标接口
export interface SystemMetrics {
  cpu: {
    usage: number;
    cores: number;
    temperature: number;
  };
  memory: {
    used: number;
    total: number;
    usage: number;
  };
  disk: {
    readSpeed: number;
    writeSpeed: number;
    usage: number;
  };
  network: {
    inbound: number;
    outbound: number;
    connections: number;
  };
  timestamp: string;
}

// 服务状态接口
export interface ServiceHealth {
  id: string;
  name: string;
  status: 'healthy' | 'degraded' | 'unhealthy';
  latency: number;
  uptime: number;
  lastCheck: string;
  errorRate: number;
}

// 业务指标接口
export interface BusinessMetrics {
  onlineUsers: number;
  activeSessions: number;
  messagesPerSecond: number;
  errorRate: number;
  throughput: number;
}

// 告警接口
export interface Alert {
  id: string;
  severity: 'info' | 'warning' | 'critical';
  message: string;
  service: string;
  timestamp: string;
  acknowledged: boolean;
}

// WebSocket连接管理
class MetricsWebSocket {
  private ws: WebSocket | null = null;
  private url: string;
  private reconnectInterval: number = 5000;
  private reconnectTimer: NodeJS.Timeout | null = null;
  private listeners: Map<string, ((data: any) => void)[]> = new Map();

  constructor(url: string) {
    this.url = url;
  }

  connect() {
    try {
      this.ws = new WebSocket(this.url);

      this.ws.onopen = () => {
        console.log('Metrics WebSocket connected');
        if (this.reconnectTimer) {
          clearTimeout(this.reconnectTimer);
          this.reconnectTimer = null;
        }
      };

      this.ws.onmessage = (event) => {
        const data = JSON.parse(event.data);
        this.notifyListeners(data.type, data.payload);
      };

      this.ws.onclose = () => {
        console.log('Metrics WebSocket closed, reconnecting...');
        this.scheduleReconnect();
      };

      this.ws.onerror = (error) => {
        console.error('Metrics WebSocket error:', error);
      };
    } catch (error) {
      console.error('Failed to connect WebSocket:', error);
      this.scheduleReconnect();
    }
  }

  private scheduleReconnect() {
    if (!this.reconnectTimer) {
      this.reconnectTimer = setTimeout(() => {
        this.connect();
      }, this.reconnectInterval);
    }
  }

  subscribe(event: string, callback: (data: any) => void) {
    if (!this.listeners.has(event)) {
      this.listeners.set(event, []);
    }
    this.listeners.get(event)?.push(callback);
  }

  unsubscribe(event: string, callback: (data: any) => void) {
    const callbacks = this.listeners.get(event);
    if (callbacks) {
      const index = callbacks.indexOf(callback);
      if (index > -1) {
        callbacks.splice(index, 1);
      }
    }
  }

  private notifyListeners(event: string, data: any) {
    const callbacks = this.listeners.get(event);
    if (callbacks) {
      callbacks.forEach(callback => callback(data));
    }
  }

  disconnect() {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
    }
    this.ws?.close();
  }
}

// 单例模式
let wsInstance: MetricsWebSocket | null = null;

const getWebSocket = (): MetricsWebSocket => {
  if (!wsInstance) {
    wsInstance = new MetricsWebSocket('wss://api.im-system.com/metrics/stream');
  }
  return wsInstance;
};

/**
 * React Hook: 使用系统指标
 */
export function useSystemMetrics(refreshInterval: number = 5000) {
  const [metrics, setMetrics] = useState<SystemMetrics | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);
  const intervalRef = useRef<NodeJS.Timeout | null>(null);

  const fetchMetrics = useCallback(async () => {
    try {
      const response = await api.get<SystemMetrics>('/api/monitoring/system-metrics');
      setMetrics(response.data);
      setError(null);
    } catch (err) {
      setError(err as Error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchMetrics();
    intervalRef.current = setInterval(fetchMetrics, refreshInterval);

    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, [fetchMetrics, refreshInterval]);

  return { metrics, loading, error, refetch: fetchMetrics };
}

/**
 * React Hook: 使用实时业务指标 (WebSocket)
 */
export function useRealtimeBusinessMetrics() {
  const [metrics, setMetrics] = useState<BusinessMetrics>({
    onlineUsers: 0,
    activeSessions: 0,
    messagesPerSecond: 0,
    errorRate: 0,
    throughput: 0
  });
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    const ws = getWebSocket();
    
    const handleMetrics = (data: BusinessMetrics) => {
      setMetrics(data);
    };

    const handleConnection = (data: { connected: boolean }) => {
      setConnected(data.connected);
    };

    ws.subscribe('business-metrics', handleMetrics);
    ws.subscribe('connection', handleConnection);
    ws.connect();

    return () => {
      ws.unsubscribe('business-metrics', handleMetrics);
      ws.unsubscribe('connection', handleConnection);
    };
  }, []);

  return { metrics, connected };
}

/**
 * React Hook: 使用服务健康状态
 */
export function useServiceHealth(pollInterval: number = 10000) {
  const [services, setServices] = useState<ServiceHealth[]>([]);
  const [loading, setLoading] = useState(true);
  const intervalRef = useRef<NodeJS.Timeout | null>(null);

  const fetchServices = useCallback(async () => {
    try {
      const response = await api.get<ServiceHealth[]>('/api/monitoring/services');
      setServices(response.data);
    } catch (error) {
      console.error('Failed to fetch services:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchServices();
    intervalRef.current = setInterval(fetchServices, pollInterval);

    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, [fetchServices, pollInterval]);

  return { services, loading, refetch: fetchServices };
}

/**
 * React Hook: 使用告警信息
 */
export function useAlerts(autoRefresh: boolean = true) {
  const [alerts, setAlerts] = useState<Alert[]>([]);
  const [unacknowledgedCount, setUnacknowledgedCount] = useState(0);

  useEffect(() => {
    const ws = getWebSocket();

    const handleAlert = (data: Alert) => {
      setAlerts(prev => {
        const exists = prev.find(a => a.id === data.id);
        if (exists) {
          return prev.map(a => a.id === data.id ? data : a);
        }
        return [data, ...prev].slice(0, 100); // 只保留最近100条
      });
    };

    ws.subscribe('alert', handleAlert);

    // 计算未确认告警数
    const unack = alerts.filter(a => !a.acknowledged).length;
    setUnacknowledgedCount(unack);

    return () => {
      ws.unsubscribe('alert', handleAlert);
    };
  }, [alerts]);

  const acknowledgeAlert = async (alertId: string) => {
    try {
      await api.post(`/api/monitoring/alerts/${alertId}/acknowledge`);
      setAlerts(prev => 
        prev.map(a => a.id === alertId ? { ...a, acknowledged: true } : a)
      );
    } catch (error) {
      console.error('Failed to acknowledge alert:', error);
    }
  };

  return { alerts, unacknowledgedCount, acknowledgeAlert };
}

export default {
  useSystemMetrics,
  useRealtimeBusinessMetrics,
  useServiceHealth,
  useAlerts
};
