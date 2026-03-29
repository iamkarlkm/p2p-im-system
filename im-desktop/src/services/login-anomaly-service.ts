import { apiClient } from './api-client';
import type { LoginAnomalyAlert, LoginAnomalySettings } from '../types/login-anomaly-alert';

export class LoginAnomalyService {
  async getAlerts(): Promise<LoginAnomalyAlert[]> {
    return apiClient.get<LoginAnomalyAlert[]>('/api/v1/security/login-alerts');
  }

  async getPendingAlerts(): Promise<LoginAnomalyAlert[]> {
    return apiClient.get<LoginAnomalyAlert[]>('/api/v1/security/login-alerts/pending');
  }

  async confirmAlert(alertId: number): Promise<LoginAnomalyAlert> {
    return apiClient.post<LoginAnomalyAlert>(
      `/api/v1/security/login-alerts/${alertId}/confirm`,
      {}
    );
  }

  async dismissAlert(alertId: number): Promise<LoginAnomalyAlert> {
    return apiClient.post<LoginAnomalyAlert>(
      `/api/v1/security/login-alerts/${alertId}/dismiss`,
      {}
    );
  }

  async getSettings(): Promise<LoginAnomalySettings> {
    return apiClient.get<LoginAnomalySettings>('/api/v1/security/login-alerts/settings');
  }

  async updateSettings(settings: Partial<LoginAnomalySettings>): Promise<LoginAnomalySettings> {
    return apiClient.put<LoginAnomalySettings>(
      '/api/v1/security/login-alerts/settings',
      settings
    );
  }
}

export const loginAnomalyService = new LoginAnomalyService();
