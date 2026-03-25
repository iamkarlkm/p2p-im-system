// Message Reminder Service for IM Desktop
import type { ReminderRequest, ReminderResponse } from '../types/message-reminder';

const API_BASE = '/api/v1/reminders';

export class ReminderService {
  private baseUrl: string;

  constructor(baseUrl: string = '') {
    this.baseUrl = baseUrl;
  }

  private getHeaders(): HeadersInit {
    const userId = localStorage.getItem('userId');
    return {
      'Content-Type': 'application/json',
      'X-User-Id': userId || '',
    };
  }

  async createReminder(request: ReminderRequest): Promise<ReminderResponse> {
    const response = await fetch(`${this.baseUrl}${API_BASE}`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify(request),
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to create reminder');
    }
    return response.json();
  }

  async updateReminder(id: number, request: ReminderRequest): Promise<ReminderResponse> {
    const response = await fetch(`${this.baseUrl}${API_BASE}/${id}`, {
      method: 'PUT',
      headers: this.getHeaders(),
      body: JSON.stringify(request),
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to update reminder');
    }
    return response.json();
  }

  async deleteReminder(id: number): Promise<void> {
    const response = await fetch(`${this.baseUrl}${API_BASE}/${id}`, {
      method: 'DELETE',
      headers: this.getHeaders(),
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to delete reminder');
    }
  }

  async dismissReminder(id: number): Promise<ReminderResponse> {
    const response = await fetch(`${this.baseUrl}${API_BASE}/${id}/dismiss`, {
      method: 'POST',
      headers: this.getHeaders(),
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to dismiss reminder');
    }
    return response.json();
  }

  async getUserReminders(): Promise<ReminderResponse[]> {
    const response = await fetch(`${this.baseUrl}${API_BASE}`, {
      method: 'GET',
      headers: this.getHeaders(),
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to get reminders');
    }
    return response.json();
  }

  async getPendingReminders(): Promise<ReminderResponse[]> {
    const response = await fetch(`${this.baseUrl}${API_BASE}/pending`, {
      method: 'GET',
      headers: this.getHeaders(),
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to get pending reminders');
    }
    return response.json();
  }

  async getPendingCount(): Promise<number> {
    const response = await fetch(`${this.baseUrl}${API_BASE}/count`, {
      method: 'GET',
      headers: this.getHeaders(),
    });
    if (!response.ok) return 0;
    const data = await response.json();
    return data.count || 0;
  }

  setReminderTime(messageId: number, minutesFromNow: number): string {
    const time = new Date(Date.now() + minutesFromNow * 60 * 1000);
    return time.toISOString();
  }

  parseReminderDateTime(dateStr: string): Date {
    return new Date(dateStr);
  }

  formatReminderDisplay(dateStr: string): string {
    const date = new Date(dateStr);
    const now = new Date();
    const diff = date.getTime() - now.getTime();
    const minutes = Math.floor(diff / 60000);
    if (minutes < 60) return `${minutes} min`;
    const hours = Math.floor(minutes / 60);
    if (hours < 24) return `${hours}h`;
    const days = Math.floor(hours / 24);
    return `${days}d`;
  }
}

export const reminderService = new ReminderService();
export default reminderService;
