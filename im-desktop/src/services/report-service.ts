import { Report, ReportRequest, ModerationSettings } from '../types/report-moderation';

const API_BASE = '/api/report';

class ReportService {
  private static instance: ReportService;

  static getInstance(): ReportService {
    if (!ReportService.instance) ReportService.instance = new ReportService();
    return ReportService.instance;
  }

  async submitReport(request: ReportRequest): Promise<Report | null> {
    const userId = Number(localStorage.getItem('userId'));
    const username = localStorage.getItem('username') || 'Unknown';
    try {
      const response = await fetch(`${API_BASE}/submit`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-User-Id': String(userId),
          'X-Username': username,
        },
        body: JSON.stringify(request),
      });
      return response.ok ? await response.json() : null;
    } catch { return null; }
  }

  async getMyReports(): Promise<Report[]> {
    const userId = Number(localStorage.getItem('userId'));
    const response = await fetch(`${API_BASE}/my-reports`, {
      headers: { 'X-User-Id': String(userId) },
    });
    return response.ok ? await response.json() : [];
  }

  async getReports(status?: string, page = 0, size = 20): Promise<Report[]> {
    const url = status
      ? `${API_BASE}/list?status=${status}&page=${page}&size=${size}`
      : `${API_BASE}/list?page=${page}&size=${size}`;
    const response = await fetch(url);
    return response.ok ? await response.json() : [];
  }

  async reviewReport(reportId: string, status: string, reviewNote: string): Promise<Report | null> {
    const reviewerId = localStorage.getItem('username') || 'admin';
    const response = await fetch(`${API_BASE}/${reportId}/review`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'X-User-Id': reviewerId,
      },
      body: JSON.stringify({ status, reviewNote }),
    });
    return response.ok ? await response.json() : null;
  }

  async getSettings(): Promise<ModerationSettings> {
    const userId = Number(localStorage.getItem('userId'));
    const response = await fetch(`${API_BASE}/settings`, {
      headers: { 'X-User-Id': String(userId) },
    });
    return response.ok ? await response.json() : { userId, enableAutoModeration: true, enableKeywordFilter: true, enableImageModeration: false, enableSpamDetection: true, allowAnonymousReports: true, maxReportsPerDay: 10 };
  }

  async updateSettings(settings: Partial<ModerationSettings>): Promise<ModerationSettings> {
    const userId = Number(localStorage.getItem('userId'));
    const response = await fetch(`${API_BASE}/settings`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', 'X-User-Id': String(userId) },
      body: JSON.stringify({ ...settings, userId }),
    });
    return await response.json();
  }

  async getStatistics(): Promise<Record<string, number>> {
    const response = await fetch(`${API_BASE}/statistics`);
    return response.ok ? await response.json() : {};
  }
}

export default ReportService;
