import axios from 'axios';
import {
  Device,
  DeviceRegistrationRequest,
  DeviceUpdateRequest,
  DeviceStats,
  LoginHistoryPage,
  RemoteLogoutRequest,
  DeviceAlert,
} from '../types/device';
import { apiClient } from './api-client';

const API_BASE = '/api/devices';

export class DeviceService {
  async registerDevice(request: DeviceRegistrationRequest): Promise<Device> {
    const response = await apiClient.post<Device>(`${API_BASE}/register`, request);
    return response.data;
  }

  async getUserDevices(): Promise<Device[]> {
    const response = await apiClient.get<Device[]>(API_BASE);
    return response.data;
  }

  async getActiveDevices(): Promise<Device[]> {
    const response = await apiClient.get<Device[]>(`${API_BASE}/active`);
    return response.data;
  }

  async getDeviceStats(): Promise<DeviceStats> {
    const response = await apiClient.get<DeviceStats>(`${API_BASE}/stats`);
    return response.data;
  }

  async getLoginHistory(page?: number, size?: number): Promise<LoginHistoryPage> {
    const params: Record<string, any> = {};
    if (page !== undefined) params.page = page;
    if (size !== undefined) params.size = size;
    const response = await apiClient.get<LoginHistoryPage>(`${API_BASE}/history`, { params });
    return response.data;
  }

  async updateDevice(request: DeviceUpdateRequest): Promise<Device> {
    const response = await apiClient.put<Device>(
      `${API_BASE}/${request.deviceId}`,
      request
    );
    return response.data;
  }

  async deactivateDevice(deviceId: number): Promise<void> {
    await apiClient.post(`${API_BASE}/${deviceId}/deactivate`);
  }

  async removeDevice(deviceId: number): Promise<void> {
    await apiClient.delete(`${API_BASE}/${deviceId}`);
  }

  async trustDevice(deviceId: number): Promise<void> {
    await apiClient.post(`${API_BASE}/${deviceId}/trust`);
  }

  async untrustDevice(deviceId: number): Promise<void> {
    await apiClient.post(`${API_BASE}/${deviceId}/untrust`);
  }

  async setCurrentDevice(deviceId: number): Promise<void> {
    await apiClient.post(`${API_BASE}/${deviceId}/set-current`);
  }

  async remoteLogout(request: RemoteLogoutRequest): Promise<void> {
    await this.deactivateDevice(request.deviceId);
  }

  async getDeviceAlerts(): Promise<DeviceAlert[]> {
    const devices = await this.getUserDevices();
    const alerts: DeviceAlert[] = [];

    for (const device of devices) {
      if (!device.isTrusted && device.isActive) {
        alerts.push({
          type: 'NEW_DEVICE',
          deviceId: device.id,
          deviceName: device.deviceName,
          ipAddress: device.ipAddress,
          location: device.location,
          timestamp: device.createdAt,
          acknowledged: false,
        });
      }
    }
    return alerts;
  }

  async acknowledgeAlert(deviceId: number): Promise<void> {
    await this.trustDevice(deviceId);
  }

  getDeviceIcon(deviceType: string): string {
    const icons: Record<string, string> = {
      DESKTOP: '💻',
      MOBILE: '📱',
      TABLET: '📲',
      WEB: '🌐',
      OTHER: '📟',
    };
    return icons[deviceType] || '📟';
  }

  formatLastActive(timestamp: string): string {
    const date = new Date(timestamp);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMins / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (diffMins < 1) return '刚刚';
    if (diffMins < 60) return `${diffMins}分钟前`;
    if (diffHours < 24) return `${diffHours}小时前`;
    if (diffDays < 7) return `${diffDays}天前`;
    return date.toLocaleDateString('zh-CN');
  }
}

export const deviceService = new DeviceService();
