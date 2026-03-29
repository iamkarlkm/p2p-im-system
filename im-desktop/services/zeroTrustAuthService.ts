/**
 * 零信任认证服务
 * Zero Trust Authentication Service
 * 
 * 提供零信任安全网关的桌面端集成
 * - 设备注册和状态管理
 * - 访问请求创建和验证
 * - 风险评估和MFA处理
 */

import { EventEmitter } from 'events';

// ============================================================================
// 类型定义
// ============================================================================

export type AccessStatus = 'ALLOWED' | 'DENIED' | 'MFA_REQUIRED' | 'PENDING';
export type RiskLevel = 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW' | 'MINIMAL';
export type RequestStatus = 'PENDING' | 'APPROVED' | 'DENIED' | 'EXPIRED';
export type ComplianceStatus = 'PASS' | 'FAIL' | 'WARNING' | 'NOT_CHECKED';
export type IsolationStatus = 'CLEAN' | 'QUARANTINE' | 'ISOLATED' | 'RESTRICTED';

export interface DeviceInfo {
  deviceId: string;
  deviceName: string;
  deviceType: string;
  osType: string;
  osVersion: string;
  lastSeen: Date;
  trustScore: number;
  healthScore: number;
  isCompliant: boolean;
  isolationStatus: IsolationStatus;
}

export interface ComplianceCheck {
  checkName: string;
  status: ComplianceStatus;
  details: string;
  lastChecked: Date;
}

export interface DeviceTrustState {
  deviceId: string;
  trustScore: number;
  healthScore: number;
  isolationStatus: IsolationStatus;
  isolationReason?: string;
  complianceChecks: ComplianceCheck[];
  vulnerabilities: string[];
  installedApps: string[];
  encryptionEnabled: boolean;
  firewallEnabled: boolean;
  antivirusEnabled: boolean;
  lastAssessmentTime: Date;
  nextAssessmentTime: Date;
}

export interface AccessRequest {
  requestId: string;
  userId: string;
  deviceId: string;
  resourceId: string;
  resourceName: string;
  action: string;
  status: RequestStatus;
  accessStatus: AccessStatus;
  riskScore: number;
  riskLevel: RiskLevel;
  mfaRequired: boolean;
  mfaMethods?: string[];
  createdAt: Date;
  expiresAt: Date;
  context: AccessContext;
  decisionReason?: string;
}

export interface AccessContext {
  ipAddress: string;
  location?: string;
  geoCountry?: string;
  geoCity?: string;
  userAgent: string;
  timestamp: Date;
  isVpn: boolean;
  isProxy: boolean;
  isTor: boolean;
  timeOfDay: string;
  dayOfWeek: string;
  sessionId?: string;
}

export interface RiskFactors {
  deviceRisk: number;
  networkRisk: number;
  behaviorRisk: number;
  contextRisk: number;
  threatIndicators: string[];
}

export interface ZeroTrustSession {
  sessionId: string;
  userId: string;
  deviceId: string;
  microsegmentId: string;
  createdAt: Date;
  expiresAt: Date;
  lastActivityAt: Date;
  isActive: boolean;
  allowedResources: string[];
  sessionPolicies: string[];
}

export interface MFACredentials {
  method: 'TOTP' | 'SMS' | 'EMAIL' | 'PUSH' | 'BIOMETRIC';
  code?: string;
  token?: string;
}

// ============================================================================
// 零信任认证服务类
// ============================================================================

class ZeroTrustAuthService extends EventEmitter {
  private static instance: ZeroTrustAuthService;
  private baseUrl: string;
  private currentDevice: DeviceInfo | null = null;
  private currentSession: ZeroTrustSession | null = null;
  private pendingRequest: AccessRequest | null = null;
  private deviceTrustState: DeviceTrustState | null = null;
  private accessHistory: AccessRequest[] = [];
  private isInitialized: boolean = false;

  private constructor() {
    super();
    this.baseUrl = process.env.REACT_APP_API_URL || 'http://localhost:8080';
    this.loadStoredData();
  }

  public static getInstance(): ZeroTrustAuthService {
    if (!ZeroTrustAuthService.instance) {
      ZeroTrustAuthService.instance = new ZeroTrustAuthService();
    }
    return ZeroTrustAuthService.instance;
  }

  // ============================================================================
  // 初始化和设备注册
  // ============================================================================

  public async initialize(): Promise<boolean> {
    try {
      // 加载或创建设备信息
      await this.loadOrCreateDevice();
      
      // 获取设备信任状态
      await this.refreshDeviceTrustState();
      
      this.isInitialized = true;
      this.emit('initialized', { device: this.currentDevice });
      return true;
    } catch (error) {
      console.error('ZeroTrust initialization failed:', error);
      this.emit('error', { type: 'initialization', error });
      return false;
    }
  }

  private async loadOrCreateDevice(): Promise<void> {
    const stored = localStorage.getItem('zt_device_info');
    
    if (stored) {
      this.currentDevice = JSON.parse(stored);
      // 更新最后活跃时间
      await this.updateDeviceLastSeen();
    } else {
      // 注册新设备
      await this.registerDevice();
    }
  }

  private async registerDevice(): Promise<void> {
    const deviceInfo = this.collectDeviceInfo();
    
    try {
      const response = await fetch(`${this.baseUrl}/api/v1/zero-trust/device/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(deviceInfo)
      });

      if (response.ok) {
        const data = await response.json();
        this.currentDevice = {
          ...deviceInfo,
          deviceId: data.deviceId,
          trustScore: data.trustScore,
          healthScore: data.healthScore,
          isCompliant: data.isCompliant,
          isolationStatus: data.isolationStatus
        };
        
        localStorage.setItem('zt_device_info', JSON.stringify(this.currentDevice));
        this.emit('deviceRegistered', this.currentDevice);
      }
    } catch (error) {
      console.error('Device registration failed:', error);
      throw error;
    }
  }

  private collectDeviceInfo(): Omit<DeviceInfo, 'deviceId' | 'trustScore' | 'healthScore' | 'isCompliant' | 'isolationStatus'> {
    const platform = this.detectPlatform();
    
    return {
      deviceName: this.getDeviceName(),
      deviceType: platform.type,
      osType: platform.os,
      osVersion: platform.version,
      lastSeen: new Date()
    };
  }

  private detectPlatform(): { type: string; os: string; version: string } {
    const userAgent = navigator.userAgent;
    let type = 'DESKTOP';
    let os = 'Unknown';
    let version = 'Unknown';

    if (userAgent.indexOf('Win') !== -1) {
      os = 'WINDOWS';
      const match = userAgent.match(/Windows NT (\d+\.\d+)/);
      version = match ? match[1] : 'Unknown';
    } else if (userAgent.indexOf('Mac') !== -1) {
      os = 'MACOS';
      type = 'LAPTOP';
    } else if (userAgent.indexOf('Linux') !== -1) {
      os = 'LINUX';
    }

    return { type, os, version };
  }

  private getDeviceName(): string {
    const platform = navigator.platform;
    const hostname = window.location.hostname;
    return `${platform}-${hostname}-${Date.now().toString(36).slice(-4)}`;
  }

  private async updateDeviceLastSeen(): Promise<void> {
    if (!this.currentDevice) return;
    
    try {
      await fetch(`${this.baseUrl}/api/v1/zero-trust/device/${this.currentDevice.deviceId}/heartbeat`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
      });
      
      this.currentDevice.lastSeen = new Date();
      localStorage.setItem('zt_device_info', JSON.stringify(this.currentDevice));
    } catch (error) {
      console.warn('Failed to update device heartbeat:', error);
    }
  }

  // ============================================================================
  // 设备信任状态管理
  // ============================================================================

  public async refreshDeviceTrustState(): Promise<DeviceTrustState | null> {
    if (!this.currentDevice) return null;

    try {
      const response = await fetch(
        `${this.baseUrl}/api/v1/zero-trust/device/${this.currentDevice.deviceId}/trust-state`
      );

      if (response.ok) {
        this.deviceTrustState = await response.json();
        this.emit('trustStateUpdated', this.deviceTrustState);
        return this.deviceTrustState;
      }
    } catch (error) {
      console.error('Failed to refresh device trust state:', error);
    }
    
    return null;
  }

  public getDeviceTrustState(): DeviceTrustState | null {
    return this.deviceTrustState;
  }

  public async updateDeviceHealth(healthData: Partial<DeviceTrustState>): Promise<boolean> {
    if (!this.currentDevice) return false;

    try {
      const response = await fetch(
        `${this.baseUrl}/api/v1/zero-trust/device/${this.currentDevice.deviceId}/health`,
        {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(healthData)
        }
      );

      if (response.ok) {
        await this.refreshDeviceTrustState();
        this.emit('healthUpdated', this.deviceTrustState);
        return true;
      }
    } catch (error) {
      console.error('Failed to update device health:', error);
    }
    
    return false;
  }

  // ============================================================================
  // 访问请求管理
  // ============================================================================

  public async requestAccess(resourceId: string, action: string): Promise<AccessRequest> {
    if (!this.currentDevice) {
      throw new Error('Device not initialized');
    }

    const context = this.buildAccessContext();
    
    try {
      const response = await fetch(`${this.baseUrl}/api/v1/zero-trust/access/evaluate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          deviceId: this.currentDevice.deviceId,
          resourceId,
          action,
          context
        })
      });

      const result: AccessRequest = await response.json();
      this.pendingRequest = result;
      
      // 存储访问历史
      this.accessHistory.unshift(result);
      if (this.accessHistory.length > 100) {
        this.accessHistory = this.accessHistory.slice(0, 100);
      }
      
      this.emit('accessRequested', result);
      
      // 处理MFA要求
      if (result.accessStatus === 'MFA_REQUIRED') {
        this.emit('mfaRequired', result);
      }
      
      return result;
    } catch (error) {
      console.error('Access request failed:', error);
      throw error;
    }
  }

  private buildAccessContext(): AccessContext {
    const now = new Date();
    
    return {
      ipAddress: '0.0.0.0', // 由后端获取
      userAgent: navigator.userAgent,
      timestamp: now,
      isVpn: false,
      isProxy: false,
      isTor: false,
      timeOfDay: `${now.getHours()}:${now.getMinutes()}`,
      dayOfWeek: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'][now.getDay()],
      sessionId: this.currentSession?.sessionId
    };
  }

  public async verifyMFA(credentials: MFACredentials): Promise<AccessRequest> {
    if (!this.pendingRequest) {
      throw new Error('No pending access request');
    }

    try {
      const response = await fetch(
        `${this.baseUrl}/api/v1/zero-trust/access/${this.pendingRequest.requestId}/verify-mfa`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(credentials)
        }
      );

      const result: AccessRequest = await response.json();
      this.pendingRequest = result.accessStatus === 'MFA_REQUIRED' ? result : null;
      
      this.emit('mfaVerified', result);
      return result;
    } catch (error) {
      console.error('MFA verification failed:', error);
      throw error;
    }
  }

  public async getAccessHistory(limit: number = 50): Promise<AccessRequest[]> {
    if (!this.currentDevice) return [];

    try {
      const response = await fetch(
        `${this.baseUrl}/api/v1/zero-trust/access/history?deviceId=${this.currentDevice.deviceId}&limit=${limit}`
      );

      if (response.ok) {
        this.accessHistory = await response.json();
        return this.accessHistory;
      }
    } catch (error) {
      console.error('Failed to fetch access history:', error);
    }
    
    return this.accessHistory;
  }

  public getPendingRequest(): AccessRequest | null {
    return this.pendingRequest;
  }

  // ============================================================================
  // 会话管理
  // ============================================================================

  public async createSession(microsegmentId: string): Promise<ZeroTrustSession | null> {
    if (!this.currentDevice) return null;

    try {
      const response = await fetch(`${this.baseUrl}/api/v1/zero-trust/session/create`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          deviceId: this.currentDevice.deviceId,
          microsegmentId
        })
      });

      if (response.ok) {
        this.currentSession = await response.json();
        localStorage.setItem('zt_session', JSON.stringify(this.currentSession));
        this.emit('sessionCreated', this.currentSession);
        return this.currentSession;
      }
    } catch (error) {
      console.error('Failed to create session:', error);
    }
    
    return null;
  }

  public async terminateSession(): Promise<boolean> {
    if (!this.currentSession) return true;

    try {
      await fetch(`${this.baseUrl}/api/v1/zero-trust/session/${this.currentSession.sessionId}/terminate`, {
        method: 'POST'
      });
      
      this.currentSession = null;
      localStorage.removeItem('zt_session');
      this.emit('sessionTerminated');
      return true;
    } catch (error) {
      console.error('Failed to terminate session:', error);
      return false;
    }
  }

  public getCurrentSession(): ZeroTrustSession | null {
    return this.currentSession;
  }

  // ============================================================================
  // 微隔离管理
  // ============================================================================

  public async isolateDevice(reason: string): Promise<boolean> {
    if (!this.currentDevice) return false;

    try {
      const response = await fetch(
        `${this.baseUrl}/api/v1/zero-trust/device/${this.currentDevice.deviceId}/isolate`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ reason })
        }
      );

      if (response.ok) {
        await this.refreshDeviceTrustState();
        this.emit('deviceIsolated', { reason });
        return true;
      }
    } catch (error) {
      console.error('Failed to isolate device:', error);
    }
    
    return false;
  }

  public async unisolateDevice(): Promise<boolean> {
    if (!this.currentDevice) return false;

    try {
      const response = await fetch(
        `${this.baseUrl}/api/v1/zero-trust/device/${this.currentDevice.deviceId}/unisolate`,
        { method: 'POST' }
      );

      if (response.ok) {
        await this.refreshDeviceTrustState();
        this.emit('deviceUnisolated');
        return true;
      }
    } catch (error) {
      console.error('Failed to unisolate device:', error);
    }
    
    return false;
  }

  // ============================================================================
  // 工具方法
  // ============================================================================

  private loadStoredData(): void {
    const session = localStorage.getItem('zt_session');
    if (session) {
      this.currentSession = JSON.parse(session);
    }
  }

  public getDeviceInfo(): DeviceInfo | null {
    return this.currentDevice;
  }

  public isDeviceCompliant(): boolean {
    return this.currentDevice?.isCompliant ?? false;
  }

  public getTrustScore(): number {
    return this.currentDevice?.trustScore ?? 0;
  }

  public getHealthScore(): number {
    return this.currentDevice?.healthScore ?? 0;
  }

  public isInitialized(): boolean {
    return this.isInitialized;
  }

  public logout(): void {
    this.terminateSession();
    localStorage.removeItem('zt_device_info');
    localStorage.removeItem('zt_session');
    this.currentDevice = null;
    this.currentSession = null;
    this.pendingRequest = null;
    this.deviceTrustState = null;
    this.isInitialized = false;
    this.emit('loggedOut');
  }
}

// 导出单例实例
export const zeroTrustAuthService = ZeroTrustAuthService.getInstance();
export default zeroTrustAuthService;
