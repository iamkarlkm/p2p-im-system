// 2FA Service for IM Desktop
import type {
  TwoFactorSetupRequest,
  TwoFactorSetupResponse,
  TwoFactorVerifyRequest,
  TwoFactorVerifyResponse,
  TwoFactorStatusResponse,
  TwoFactorState,
  TwoFactorStep,
} from '../types/two-factor-auth';

const API_BASE = '/api/v1/2fa';

export class TwoFactorAuthService {
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

  async setup2FA(request: TwoFactorSetupRequest): Promise<TwoFactorSetupResponse> {
    const response = await fetch(`${this.baseUrl}${API_BASE}/setup`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify(request),
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to setup 2FA');
    }
    return response.json();
  }

  async verify2FA(request: TwoFactorVerifyRequest): Promise<TwoFactorVerifyResponse> {
    const response = await fetch(`${this.baseUrl}${API_BASE}/verify`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify(request),
    });
    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.message || 'Verification failed');
    }
    return data;
  }

  async enable2FA(code: string): Promise<TwoFactorSetupResponse> {
    const response = await fetch(`${this.baseUrl}${API_BASE}/enable`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({ code }),
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to enable 2FA');
    }
    return response.json();
  }

  async disable2FA(password: string, code: string): Promise<void> {
    const response = await fetch(`${this.baseUrl}${API_BASE}/disable`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({ password, code }),
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to disable 2FA');
    }
  }

  async regenerateBackupCodes(code: string): Promise<string[]> {
    const response = await fetch(`${this.baseUrl}${API_BASE}/backup-codes/regenerate`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({ code }),
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to regenerate backup codes');
    }
    return response.json();
  }

  async getStatus(): Promise<TwoFactorStatusResponse> {
    const response = await fetch(`${this.baseUrl}${API_BASE}/status`, {
      method: 'GET',
      headers: this.getHeaders(),
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to get 2FA status');
    }
    return response.json();
  }

  async check2FARequired(userId: number): Promise<boolean> {
    const response = await fetch(`${this.baseUrl}${API_BASE}/check/${userId}`, {
      method: 'GET',
      headers: this.getHeaders(),
    });
    if (!response.ok) return false;
    const data = await response.json();
    return data.required;
  }

  async authenticate2FA(code: string): Promise<TwoFactorVerifyResponse> {
    return this.verify2FA({
      code,
      isBackupCode: code.length > 6,
      deviceName: navigator.userAgent,
    });
  }

  parseBackupCode(code: string): string {
    return code.replace(/[^0-9]/g, '').substring(0, 8);
  }

  validateCode(code: string): boolean {
    const clean = code.replace(/[^0-9]/g, '');
    return clean.length === 6 || (clean.length === 8 && clean.startsWith('USED:'));
  }

  isBackupCode(code: string): boolean {
    return code.length === 8 && !code.startsWith('0');
  }

  get2FAState(): TwoFactorState {
    const stateJson = localStorage.getItem('twoFactorState');
    if (stateJson) {
      try {
        return JSON.parse(stateJson);
      } catch {
        return this.getDefaultState();
      }
    }
    return this.getDefaultState();
  }

  save2FAState(state: TwoFactorState): void {
    localStorage.setItem('twoFactorState', JSON.stringify(state));
  }

  private getDefaultState(): TwoFactorState {
    return {
      isEnabled: false,
      isVerified: false,
      setupInProgress: false,
      verifyPending: false,
      backupCodes: [],
      lastVerifiedAt: undefined,
    };
  }

  getStep(state: TwoFactorState): TwoFactorStep {
    if (!state.setupInProgress && !state.isEnabled) {
      return 'idle';
    }
    if (state.setupInProgress && !state.isVerified) {
      return 'scan_qr';
    }
    if (state.setupInProgress && state.isVerified && state.backupCodes.length > 0) {
      return 'verify_code';
    }
    if (state.setupInProgress && state.isVerified) {
      return 'backup_codes';
    }
    if (state.isEnabled && state.isVerified) {
      return 'complete';
    }
    return 'idle';
  }
}

export const twoFactorAuthService = new TwoFactorAuthService();
export default twoFactorAuthService;
