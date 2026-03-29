import { ScreenshotEvent, ScreenshotSettings, ScreenshotEventRequest } from '../types/screenshot-notification';

const API_BASE = '/api/screenshot';

class ScreenshotNotificationService {
  private static instance: ScreenshotNotificationService;

  static getInstance(): ScreenshotNotificationService {
    if (!ScreenshotNotificationService.instance) {
      ScreenshotNotificationService.instance = new ScreenshotNotificationService();
    }
    return ScreenshotNotificationService.instance;
  }

  async reportScreenshot(conversationId: number, conversationType: string): Promise<ScreenshotEvent | null> {
    const userId = Number(localStorage.getItem('userId'));
    const username = localStorage.getItem('username') || 'Unknown';

    const request: ScreenshotEventRequest = {
      conversationId,
      conversationType: conversationType as 'private' | 'group',
      deviceType: 'desktop',
      deviceInfo: navigator.userAgent,
    };

    try {
      const response = await fetch(`${API_BASE}/report`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-User-Id': String(userId),
          'X-Username': username,
        },
        body: JSON.stringify(request),
      });
      if (!response.ok) return null;
      return await response.json();
    } catch (error) {
      console.error('Failed to report screenshot:', error);
      return null;
    }
  }

  async getSettings(): Promise<ScreenshotSettings> {
    const userId = Number(localStorage.getItem('userId'));
    try {
      const response = await fetch(`${API_BASE}/settings`, {
        headers: { 'X-User-Id': String(userId) },
      });
      return await response.json();
    } catch {
      return { userId, enableScreenshotNotification: true, notifyOnCapture: true, receiveScreenshotAlerts: true, alertForContacts: true, alertForGroups: true, silentMode: false };
    }
  }

  async updateSettings(settings: Partial<ScreenshotSettings>): Promise<ScreenshotSettings> {
    const userId = Number(localStorage.getItem('userId'));
    const response = await fetch(`${API_BASE}/settings`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', 'X-User-Id': String(userId) },
      body: JSON.stringify({ ...settings, userId }),
    });
    return await response.json();
  }

  async getHistory(limit = 50): Promise<ScreenshotEvent[]> {
    const userId = Number(localStorage.getItem('userId'));
    const response = await fetch(`${API_BASE}/history?limit=${limit}`, {
      headers: { 'X-User-Id': String(userId) },
    });
    return await response.json();
  }
}

export default ScreenshotNotificationService;
