/**
 * 身份指纹验证服务
 * 处理安全码验证、二维码扫描、密钥变更通知等客户端逻辑
 */

import { apiClient } from './apiClient';
import { websocketService } from './websocketService';

export interface FingerprintVerification {
  id: number;
  type: 'SAFETY_CODE' | 'QR_SCAN' | 'KEY_CHANGE' | 'DEVICE_FINGERPRINT' | 'BIOMETRIC' | 'BACKUP_CODE';
  status: 'PENDING' | 'VERIFIED' | 'EXPIRED' | 'FAILED' | 'REVOKED' | 'LOCKED';
  verificationCode?: string;
  expiresAt?: string;
  verifiedAt?: string;
  deviceId?: string;
  deviceName?: string;
  deviceType?: 'DESKTOP' | 'MOBILE' | 'WEB' | 'ANY';
  qrImageUrl?: string;
  createdAt: string;
  updatedAt: string;
}

export interface SafetyCodeRequest {
  deviceId?: string;
  deviceName?: string;
  ipAddress?: string;
  userAgent?: string;
}

export interface SafetyCodeVerify {
  verificationCode: string;
  deviceId: string;
}

export interface QrCodeRequest {
  deviceId: string;
  deviceName: string;
}

export interface QrCodeVerify {
  qrData: string;
  scanningDeviceId: string;
}

export interface KeyChangeNotify {
  deviceId: string;
  keyType: string;
  changeReason: string;
}

export interface VerificationStats {
  userId: number;
  totalVerifications: number;
  successfulVerifications: number;
  failedVerifications: number;
  lastVerification: string;
  preferredMethod: string;
  devicesUsed: number;
}

export interface VerificationMethod {
  method: string;
  name: string;
  description: string;
  supportedPlatforms: string[];
  expirationMinutes?: number;
  requiresNetwork?: boolean;
  requiresConfirmation?: boolean;
  automatic?: boolean;
}

export class IdentityFingerprintService {
  private static instance: IdentityFingerprintService;
  
  public static getInstance(): IdentityFingerprintService {
    if (!IdentityFingerprintService.instance) {
      IdentityFingerprintService.instance = new IdentityFingerprintService();
    }
    return IdentityFingerprintService.instance;
  }
  
  /**
   * 请求安全码验证
   */
  async requestSafetyCodeVerification(params: SafetyCodeRequest): Promise<{
    success: boolean;
    fingerprintId: number;
    verificationCode: string;
    expiresAt: string;
  }> {
    try {
      const response = await apiClient.post('/security/fingerprint/safety-code/request', params);
      return response.data;
    } catch (error) {
      console.error('请求安全码验证失败:', error);
      throw new Error('请求安全码验证失败，请检查网络连接');
    }
  }
  
  /**
   * 验证安全码
   */
  async verifySafetyCode(params: SafetyCodeVerify): Promise<{
    success: boolean;
    fingerprintId: number;
    verifiedAt: string;
    status: string;
  }> {
    try {
      const response = await apiClient.post('/security/fingerprint/safety-code/verify', params);
      return response.data;
    } catch (error: any) {
      console.error('验证安全码失败:', error);
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error('验证安全码失败，请重试');
    }
  }
  
  /**
   * 请求二维码验证
   */
  async requestQrCodeVerification(params: QrCodeRequest): Promise<{
    success: boolean;
    fingerprintId: number;
    qrImageUrl: string;
    expiresAt: string;
  }> {
    try {
      const response = await apiClient.post('/security/fingerprint/qr-code/request', params);
      return response.data;
    } catch (error) {
      console.error('请求二维码验证失败:', error);
      throw new Error('请求二维码验证失败');
    }
  }
  
  /**
   * 验证二维码扫描
   */
  async verifyQrCodeScan(params: QrCodeVerify): Promise<{
    success: boolean;
    fingerprintId: number;
    verifiedAt: string;
    status: string;
  }> {
    try {
      const response = await apiClient.post('/security/fingerprint/qr-code/verify', params);
      return response.data;
    } catch (error: any) {
      console.error('验证二维码扫描失败:', error);
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error('验证二维码扫描失败');
    }
  }
  
  /**
   * 发送密钥变更通知
   */
  async notifyKeyChange(params: KeyChangeNotify): Promise<{
    success: boolean;
    fingerprintId: number;
    createdAt: string;
    requiresConfirmation: boolean;
  }> {
    try {
      const response = await apiClient.post('/security/fingerprint/key-change/notify', params);
      return response.data;
    } catch (error) {
      console.error('发送密钥变更通知失败:', error);
      throw new Error('发送密钥变更通知失败');
    }
  }
  
  /**
   * 获取验证历史
   */
  async getVerificationHistory(limit: number = 10): Promise<{
    success: boolean;
    userId: number;
    total: number;
    history: Array<{
      id: number;
      type: string;
      status: string;
      verifiedAt: string;
      deviceName: string;
      deviceType: string;
    }>;
  }> {
    try {
      const response = await apiClient.get('/security/fingerprint/history', {
        params: { limit }
      });
      return response.data;
    } catch (error) {
      console.error('获取验证历史失败:', error);
      throw new Error('获取验证历史失败');
    }
  }
  
  /**
   * 获取待处理验证
   */
  async getPendingVerifications(): Promise<{
    success: boolean;
    userId: number;
    count: number;
    pendingVerifications: Array<{
      id: number;
      type: string;
      createdAt: string;
      expiresAt: string;
      deviceName: string;
      qrImageUrl?: string;
      hasVerificationCode?: boolean;
    }>;
  }> {
    try {
      const response = await apiClient.get('/security/fingerprint/pending');
      return response.data;
    } catch (error) {
      console.error('获取待处理验证失败:', error);
      throw new Error('获取待处理验证失败');
    }
  }
  
  /**
   * 撤销指纹验证
   */
  async revokeFingerprint(fingerprintId: number, reason: string): Promise<{
    success: boolean;
    fingerprintId: number;
    revokedAt: string;
    reason: string;
  }> {
    try {
      const response = await apiClient.post(`/security/fingerprint/${fingerprintId}/revoke`, null, {
        params: { reason }
      });
      return response.data;
    } catch (error) {
      console.error('撤销指纹验证失败:', error);
      throw new Error('撤销指纹验证失败');
    }
  }
  
  /**
   * 获取验证统计
   */
  async getVerificationStats(): Promise<{
    success: boolean;
    stats: VerificationStats;
  }> {
    try {
      const response = await apiClient.get('/security/fingerprint/stats');
      return response.data;
    } catch (error) {
      console.error('获取验证统计失败:', error);
      throw new Error('获取验证统计失败');
    }
  }
  
  /**
   * 检查验证状态
   */
  async checkVerificationStatus(fingerprintId: number): Promise<{
    success: boolean;
    status: {
      fingerprintId: number;
      status: string;
      verifiedAt: string;
      method: string;
      device: string;
    };
  }> {
    try {
      const response = await apiClient.get(`/security/fingerprint/${fingerprintId}/status`);
      return response.data;
    } catch (error) {
      console.error('检查验证状态失败:', error);
      throw new Error('检查验证状态失败');
    }
  }
  
  /**
   * 重新发送验证码
   */
  async resendVerificationCode(fingerprintId: number): Promise<{
    success: boolean;
    fingerprintId: number;
    resentAt: string;
    message: string;
  }> {
    try {
      const response = await apiClient.post(`/security/fingerprint/${fingerprintId}/resend`);
      return response.data;
    } catch (error) {
      console.error('重新发送验证码失败:', error);
      throw new Error('重新发送验证码失败');
    }
  }
  
  /**
   * 获取支持的验证方法
   */
  async getSupportedMethods(): Promise<{
    success: boolean;
    methods: VerificationMethod[];
  }> {
    try {
      const response = await apiClient.get('/security/fingerprint/methods');
      return response.data;
    } catch (error) {
      console.error('获取支持的验证方法失败:', error);
      throw new Error('获取支持的验证方法失败');
    }
  }
  
  /**
   * 健康检查
   */
  async healthCheck(): Promise<{
    service: string;
    status: string;
    timestamp: string;
    version: string;
  }> {
    try {
      const response = await apiClient.get('/security/fingerprint/health');
      return response.data;
    } catch (error) {
      console.error('身份指纹验证服务健康检查失败:', error);
      throw new Error('身份指纹验证服务不可用');
    }
  }
  
  /**
   * 监听WebSocket安全事件
   */
  setupWebSocketListeners() {
    // 监听指纹验证成功事件
    websocketService.subscribe('security', (data: any) => {
      if (data.type === 'FINGERPRINT_VERIFIED') {
        this.handleFingerprintVerified(data);
      } else if (data.type === 'QR_CODE_SCANNED') {
        this.handleQrCodeScanned(data);
      }
    });
  }
  
  /**
   * 处理指纹验证成功事件
   */
  private handleFingerprintVerified(data: any) {
    console.log('指纹验证成功:', data);
    
    // 触发自定义事件
    const event = new CustomEvent('fingerprint-verified', {
      detail: data
    });
    window.dispatchEvent(event);
    
    // 可以在这里更新UI状态
    // 例如：显示验证成功的通知
    this.showNotification('身份验证成功', '您的身份验证已成功完成', 'success');
  }
  
  /**
   * 处理二维码扫描事件
   */
  private handleQrCodeScanned(data: any) {
    console.log('二维码被扫描:', data);
    
    const event = new CustomEvent('qr-code-scanned', {
      detail: data
    });
    window.dispatchEvent(event);
    
    this.showNotification('设备扫描成功', `设备 ${data.scanningDeviceId} 扫描了您的二维码`, 'info');
  }
  
  /**
   * 显示通知
   */
  private showNotification(title: string, message: string, type: 'success' | 'error' | 'info' | 'warning') {
    // 这里可以集成通知系统
    console.log(`[${type.toUpperCase()}] ${title}: ${message}`);
    
    // 简单实现：使用浏览器的通知API
    if ('Notification' in window && Notification.permission === 'granted') {
      new Notification(title, {
        body: message,
        icon: type === 'success' ? '/icons/success.png' : 
               type === 'error' ? '/icons/error.png' :
               type === 'warning' ? '/icons/warning.png' : '/icons/info.png'
      });
    }
  }
  
  /**
   * 生成设备ID
   */
  generateDeviceId(): string {
    // 生成基于浏览器指纹的设备ID
    const userAgent = navigator.userAgent;
    const language = navigator.language;
    const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
    const screenResolution = `${window.screen.width}x${window.screen.height}`;
    
    // 简单的哈希算法
    const data = `${userAgent}-${language}-${timezone}-${screenResolution}`;
    let hash = 0;
    
    for (let i = 0; i < data.length; i++) {
      const char = data.charCodeAt(i);
      hash = ((hash << 5) - hash) + char;
      hash = hash & hash; // 转换为32位整数
    }
    
    return `device_${Math.abs(hash)}`;
  }
  
  /**
   * 获取设备信息
   */
  getDeviceInfo(): {
    deviceId: string;
    deviceName: string;
    deviceType: string;
    userAgent: string;
    ipAddress?: string;
  } {
    const deviceId = this.generateDeviceId();
    const deviceName = this.getDeviceName();
    const deviceType = this.getDeviceType();
    const userAgent = navigator.userAgent;
    
    return {
      deviceId,
      deviceName,
      deviceType,
      userAgent
    };
  }
  
  /**
   * 获取设备名称
   */
  private getDeviceName(): string {
    // 尝试从本地存储获取设备名称
    const savedName = localStorage.getItem('device_name');
    if (savedName) {
      return savedName;
    }
    
    // 根据用户代理推断设备名称
    const ua = navigator.userAgent;
    
    if (ua.includes('Windows')) {
      return 'Windows Desktop';
    } else if (ua.includes('Mac')) {
      return 'Mac Desktop';
    } else if (ua.includes('Linux')) {
      return 'Linux Desktop';
    } else if (ua.includes('Android')) {
      return 'Android Device';
    } else if (ua.includes('iPhone') || ua.includes('iPad')) {
      return 'iOS Device';
    } else {
      return 'Web Browser';
    }
  }
  
  /**
   * 获取设备类型
   */
  private getDeviceType(): string {
    const ua = navigator.userAgent;
    
    if (ua.includes('Mobile') || ua.includes('Android') || ua.includes('iPhone') || ua.includes('iPad')) {
      return 'MOBILE';
    } else {
      return 'DESKTOP';
    }
  }
  
  /**
   * 保存设备名称
   */
  saveDeviceName(name: string): void {
    localStorage.setItem('device_name', name);
  }
  
  /**
   * 生成二维码数据
   */
  generateQrData(): string {
    const timestamp = Date.now();
    const random = Math.random().toString(36).substring(2, 10);
    return `IM_QR_${timestamp}_${random}`;
  }
  
  /**
   * 验证二维码数据格式
   */
  validateQrData(qrData: string): boolean {
    // 检查二维码数据格式
    return qrData.startsWith('IM_QR_') || qrData.startsWith('IM_VERIFY_');
  }
}

export const identityFingerprintService = IdentityFingerprintService.getInstance();