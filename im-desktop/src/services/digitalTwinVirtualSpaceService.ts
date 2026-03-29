/**
 * 数字孪生虚拟会议空间 API 服务
 * 提供与后端 API 的交互接口
 * 
 * @since 2026-03-23
 * @version 1.0.0
 */

import {
  VirtualSpaceInfo,
  VirtualSpaceConfig,
  VirtualAvatarInfo,
  SpatialAudioConfig,
  ArVrConfig,
  SceneConfig,
  CollaborationSession,
  ApiResponse,
  SpaceStatistics,
  VirtualSpaceEvent,
  VirtualSpaceEventType,
  createSpaceRequest,
  createAvatarRequest
} from '../types/digitalTwinVirtualSpace';

/** API 基础配置 */
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1';
const DIGITAL_TWIN_ENDPOINT = `${API_BASE_URL}/digital-twin`;

/** HTTP 请求工具 */
async function request<T>(
  endpoint: string,
  method: string = 'GET',
  data?: any,
  headers?: Record<string, string>
): Promise<ApiResponse<T>> {
  const url = `${DIGITAL_TWIN_ENDPOINT}${endpoint}`;
  
  const config: RequestInit = {
    method,
    headers: {
      'Content-Type': 'application/json',
      ...headers
    }
  };
  
  if (data && method !== 'GET') {
    config.body = JSON.stringify(data);
  }
  
  try {
    const response = await fetch(url, config);
    const result = await response.json();
    
    if (!response.ok) {
      throw new Error(result.message || 'Request failed');
    }
    
    return result;
  } catch (error) {
    throw new Error(`API request failed: ${error instanceof Error ? error.message : 'Unknown error'}`);
  }
}

/** 虚拟空间服务类 */
export class DigitalTwinService {
  
  /**
   * 创建虚拟空间
   */
  static async createVirtualSpace(
    spaceName: string,
    spaceType: string,
    hostUserId: string,
    config?: Partial<VirtualSpaceConfig>
  ): Promise<ApiResponse<{ spaceId: string; space: VirtualSpaceInfo }>> {
    const requestData = createSpaceRequest(spaceName, spaceType as any, hostUserId, config);
    
    return request('/spaces', 'POST', requestData);
  }
  
  /**
   * 获取虚拟空间详情
   */
  static async getVirtualSpace(spaceId: string): Promise<ApiResponse<{ space: VirtualSpaceInfo }>> {
    return request(`/spaces/${spaceId}`, 'GET');
  }
  
  /**
   * 更新虚拟空间
   */
  static async updateVirtualSpace(
    spaceId: string,
    updates: Partial<VirtualSpaceConfig>
  ): Promise<ApiResponse<{ spaceId: string }>> {
    return request(`/spaces/${spaceId}`, 'PUT', updates);
  }
  
  /**
   * 删除虚拟空间
   */
  static async deleteVirtualSpace(spaceId: string): Promise<ApiResponse<{ spaceId: string }>> {
    return request(`/spaces/${spaceId}`, 'DELETE');
  }
  
  /**
   * 用户加入虚拟空间
   */
  static async joinVirtualSpace(
    spaceId: string,
    userId: string,
    avatarId?: string
  ): Promise<ApiResponse<{ spaceId: string; sessionToken: string }>> {
    const params = new URLSearchParams({ userId });
    if (avatarId) params.append('avatarId', avatarId);
    
    return request(`/spaces/${spaceId}/join?${params}`, 'POST');
  }
  
  /**
   * 用户离开虚拟空间
   */
  static async leaveVirtualSpace(
    spaceId: string,
    userId: string
  ): Promise<ApiResponse<{ spaceId: string }>> {
    return request(`/spaces/${spaceId}/leave`, 'POST', { userId });
  }
  
  /**
   * 配置空间音频
   */
  static async configureSpatialAudio(
    spaceId: string,
    audioConfig: Partial<SpatialAudioConfig>
  ): Promise<ApiResponse<{ audioConfig: SpatialAudioConfig }>> {
    return request(`/spaces/${spaceId}/audio`, 'POST', audioConfig);
  }
  
  /**
   * 创建虚拟化身
   */
  static async createAvatar(
    userId: string,
    avatarName: string,
    config?: Partial<VirtualAvatarInfo>
  ): Promise<ApiResponse<{ avatarId: string; avatar: VirtualAvatarInfo }>> {
    const requestData = createAvatarRequest(userId, avatarName, config);
    
    return request('/avatars', 'POST', requestData);
  }
  
  /**
   * 获取用户的所有化身
   */
  static async getUserAvatars(userId: string): Promise<ApiResponse<{ avatars: VirtualAvatarInfo[] }>> {
    return request(`/avatars?userId=${userId}`, 'GET');
  }
  
  /**
   * 更新虚拟化身
   */
  static async updateAvatar(
    avatarId: string,
    updates: Partial<VirtualAvatarInfo>
  ): Promise<ApiResponse<{ avatarId: string }>> {
    return request(`/avatars/${avatarId}`, 'PUT', updates);
  }
  
  /**
   * 配置 AR/VR 集成
   */
  static async configureArVrIntegration(
    spaceId: string,
    vrConfig: Partial<ArVrConfig>
  ): Promise<ApiResponse<{ vrConfig: ArVrConfig }>> {
    return request(`/spaces/${spaceId}/arvr`, 'POST', vrConfig);
  }
  
  /**
   * 配置场景模拟
   */
  static async configureSceneSimulation(
    spaceId: string,
    sceneConfig: Partial<SceneConfig>
  ): Promise<ApiResponse<{ sceneConfig: SceneConfig }>> {
    return request(`/spaces/${spaceId}/scenes`, 'POST', sceneConfig);
  }
  
  /**
   * 配置协作工具
   */
  static async configureCollaborationTools(
    spaceId: string,
    toolsConfig: { availableTools: string[]; interactiveObjectsEnabled: boolean }
  ): Promise<ApiResponse<{ toolsConfig: any }>> {
    return request(`/spaces/${spaceId}/collaboration`, 'POST', toolsConfig);
  }
  
  /**
   * 搜索虚拟空间
   */
  static async searchSpaces(
    keyword?: string,
    spaceType?: string,
    minCapacity?: number,
    hasArVr?: boolean
  ): Promise<ApiResponse<{ spaces: VirtualSpaceInfo[] }>> {
    const params = new URLSearchParams();
    if (keyword) params.append('keyword', keyword);
    if (spaceType) params.append('spaceType', spaceType);
    if (minCapacity) params.append('minCapacity', minCapacity.toString());
    if (hasArVr !== undefined) params.append('hasArVr', hasArVr.toString());
    
    return request(`/spaces/search?${params}`, 'GET');
  }
  
  /**
   * 获取活跃空间列表
   */
  static async getActiveSpaces(): Promise<ApiResponse<{ spaces: VirtualSpaceInfo[] }>> {
    return request('/spaces/active', 'GET');
  }
  
  /**
   * 获取空间统计信息
   */
  static async getSpaceStatistics(spaceId: string): Promise<ApiResponse<{ statistics: SpaceStatistics }>> {
    return request(`/spaces/${spaceId}/statistics`, 'GET');
  }
  
  /**
   * 获取支持的 VR 设备列表
   */
  static async getSupportedVrDevices(): Promise<ApiResponse<{ devices: any[] }>> {
    return request('/vr-devices', 'GET');
  }
  
  /**
   * 获取支持的场景类型
   */
  static async getSupportedSceneTypes(): Promise<ApiResponse<{ sceneTypes: any[] }>> {
    return request('/scene-types', 'GET');
  }
}

/** WebSocket 连接管理 */
export class VirtualSpaceWebSocket {
  private ws: WebSocket | null = null;
  private spaceId: string | null = null;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 1000;
  private eventListeners: Map<VirtualSpaceEventType, Set<(event: VirtualSpaceEvent) => void>> = new Map();
  
  constructor(private userId: string) {}
  
  /**
   * 连接到虚拟空间
   */
  connect(spaceId: string, token: string): Promise<void> {
    this.spaceId = spaceId;
    const wsUrl = `ws://localhost:8080/ws/digital-twin/${spaceId}?token=${token}&userId=${this.userId}`;
    
    return new Promise((resolve, reject) => {
      try {
        this.ws = new WebSocket(wsUrl);
        
        this.ws.onopen = () => {
          console.log('Connected to virtual space WebSocket');
          this.reconnectAttempts = 0;
          resolve();
        };
        
        this.ws.onmessage = (event) => {
          try {
            const data = JSON.parse(event.data);
            this.handleEvent(data);
          } catch (error) {
            console.error('Failed to parse WebSocket message:', error);
          }
        };
        
        this.ws.onclose = () => {
          console.log('WebSocket connection closed');
          this.attemptReconnect();
        };
        
        this.ws.onerror = (error) => {
          console.error('WebSocket error:', error);
          reject(error);
        };
      } catch (error) {
        reject(error);
      }
    });
  }
  
  /**
   * 断开连接
   */
  disconnect(): void {
    if (this.ws) {
      this.ws.close();
      this.ws = null;
      this.spaceId = null;
    }
  }
  
  /**
   * 发送事件
   */
  sendEvent(eventType: VirtualSpaceEventType, data?: Record<string, any>): void {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) {
      console.warn('WebSocket not connected');
      return;
    }
    
    const event: Partial<VirtualSpaceEvent> = {
      spaceId: this.spaceId!,
      eventType,
      userId: this.userId,
      data,
      timestamp: new Date()
    };
    
    this.ws.send(JSON.stringify(event));
  }
  
  /**
   * 更新用户位置
   */
  updatePosition(position: { x: number; y: number; z: number }, rotation: { x: number; y: number; z: number }): void {
    this.sendEvent(VirtualSpaceEventType.USER_JOINED, {
      position,
      rotation
    });
  }
  
  /**
   * 切换语音状态
   */
  setVoiceActive(active: boolean): void {
    this.sendEvent(VirtualSpaceEventType.USER_JOINED, {
      voiceActive: active
    });
  }
  
  /**
   * 更改化身表情
   */
  setExpression(expression: string): void {
    this.sendEvent(VirtualSpaceEventType.AVATAR_CHANGED, {
      expressionState: expression
    });
  }
  
  /**
   * 切换 VR 模式
   */
  toggleVrMode(enabled: boolean): void {
    this.sendEvent(VirtualSpaceEventType.VR_MODE_TOGGLED, {
      vrEnabled: enabled
    });
  }
  
  /**
   * 监听事件
   */
  on(eventType: VirtualSpaceEventType, callback: (event: VirtualSpaceEvent) => void): void {
    if (!this.eventListeners.has(eventType)) {
      this.eventListeners.set(eventType, new Set());
    }
    this.eventListeners.get(eventType)!.add(callback);
  }
  
  /**
   * 移除事件监听
   */
  off(eventType: VirtualSpaceEventType, callback: (event: VirtualSpaceEvent) => void): void {
    const listeners = this.eventListeners.get(eventType);
    if (listeners) {
      listeners.delete(callback);
    }
  }
  
  /**
   * 处理接收到的事件
   */
  private handleEvent(event: VirtualSpaceEvent): void {
    const listeners = this.eventListeners.get(event.eventType);
    if (listeners) {
      listeners.forEach(callback => callback(event));
    }
    
    // 全局事件处理
    this.eventListeners.forEach((typeListeners, type) => {
      if (type === event.eventType) {
        typeListeners.forEach(callback => callback(event));
      }
    });
  }
  
  /**
   * 尝试重新连接
   */
  private attemptReconnect(): void {
    if (this.reconnectAttempts >= this.maxReconnectAttempts || !this.spaceId) {
      console.log('Max reconnect attempts reached');
      return;
    }
    
    this.reconnectAttempts++;
    const delay = this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1);
    
    console.log(`Attempting to reconnect in ${delay}ms (attempt ${this.reconnectAttempts}/${this.maxReconnectAttempts})`);
    
    setTimeout(() => {
      if (this.spaceId) {
        this.connect(this.spaceId, 'reconnect-token');
      }
    }, delay);
  }
}

export default DigitalTwinService;