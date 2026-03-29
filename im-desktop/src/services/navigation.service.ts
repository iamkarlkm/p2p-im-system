import { apiClient } from '@/utils/api';
import { EventEmitter } from 'events';

/**
 * 导航服务类
 * 提供路线规划、导航、路况查询等功能
 */
export class NavigationService extends EventEmitter {
  private static instance: NavigationService;
  private currentRoute: RouteResponse | null = null;
  private navigationActive: boolean = false;
  private watchId: number | null = null;

  static getInstance(): NavigationService {
    if (!NavigationService.instance) {
      NavigationService.instance = new NavigationService();
    }
    return NavigationService.instance;
  }

  /**
   * 规划路线
   */
  async planRoute(request: RouteRequest): Promise<RouteResponse> {
    try {
      const response = await apiClient.post<RouteResponse>('/api/v1/navigation/plan', request);
      this.currentRoute = response.data;
      this.emit('routePlanned', response.data);
      return response.data;
    } catch (error) {
      console.error('路线规划失败:', error);
      throw error;
    }
  }

  /**
   * 批量规划多条路线
   */
  async planMultipleRoutes(request: RouteRequest): Promise<RouteResponse[]> {
    try {
      const response = await apiClient.post<RouteResponse[]>('/api/v1/navigation/plan/multiple', request);
      this.emit('routesPlanned', response.data);
      return response.data;
    } catch (error) {
      console.error('批量路线规划失败:', error);
      throw error;
    }
  }

  /**
   * 开始导航
   */
  startNavigation(route: RouteResponse): void {
    this.currentRoute = route;
    this.navigationActive = true;
    
    // 开始位置监听
    this.watchId = navigator.geolocation.watchPosition(
      (position) => this.handlePositionUpdate(position),
      (error) => this.handlePositionError(error),
      { enableHighAccuracy: true, maximumAge: 10000, timeout: 5000 }
    );

    this.emit('navigationStarted', route);
    console.log('导航开始:', route.routeName);
  }

  /**
   * 停止导航
   */
  stopNavigation(): void {
    this.navigationActive = false;
    
    if (this.watchId !== null) {
      navigator.geolocation.clearWatch(this.watchId);
      this.watchId = null;
    }

    this.emit('navigationStopped');
    console.log('导航结束');
  }

  /**
   * 位置更新处理
   */
  private handlePositionUpdate(position: GeolocationPosition): void {
    if (!this.navigationActive || !this.currentRoute) return;

    const { latitude, longitude, speed, heading } = position.coords;
    
    this.emit('positionUpdate', {
      latitude,
      longitude,
      speed,
      heading,
      timestamp: position.timestamp
    });

    // 检查是否偏离路线
    this.checkDeviation(longitude, latitude);
  }

  /**
   * 位置错误处理
   */
  private handlePositionError(error: GeolocationPositionError): void {
    console.error('位置获取失败:', error.message);
    this.emit('positionError', error);
  }

  /**
   * 检查路线偏离
   */
  private checkDeviation(currentLng: number, currentLat: number): void {
    if (!this.currentRoute) return;

    // 简化的偏离检测 - 检查与路线起点的距离
    const distance = this.calculateDistance(
      currentLat, currentLng,
      this.currentRoute.start.latitude, this.currentRoute.start.longitude
    );

    // 如果偏离超过100米，触发偏离事件
    if (distance > 100) {
      this.emit('routeDeviation', { distance, currentLng, currentLat });
    }
  }

  /**
   * 计算两点距离
   */
  private calculateDistance(lat1: number, lng1: number, lat2: number, lng2: number): number {
    const R = 6371000; // 地球半径(米)
    const latDistance = this.toRadians(lat2 - lat1);
    const lngDistance = this.toRadians(lng2 - lng1);
    const a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
              Math.cos(this.toRadians(lat1)) * Math.cos(this.toRadians(lat2)) *
              Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
  }

  private toRadians(degrees: number): number {
    return degrees * Math.PI / 180;
  }

  /**
   * 获取用户路线列表
   */
  async getUserRoutes(userId: number): Promise<RouteResponse[]> {
    try {
      const response = await apiClient.get<RouteResponse[]>(`/api/v1/navigation/routes/user/${userId}`);
      return response.data;
    } catch (error) {
      console.error('获取用户路线失败:', error);
      throw error;
    }
  }

  /**
   * 获取收藏路线
   */
  async getFavoriteRoutes(userId: number): Promise<RouteResponse[]> {
    try {
      const response = await apiClient.get<RouteResponse[]>(`/api/v1/navigation/routes/favorites/${userId}`);
      return response.data;
    } catch (error) {
      console.error('获取收藏路线失败:', error);
      throw error;
    }
  }

  /**
   * 收藏路线
   */
  async favoriteRoute(routeId: number, isFavorite: boolean): Promise<void> {
    try {
      await apiClient.put(`/api/v1/navigation/routes/${routeId}/favorite`, null, {
        params: { isFavorite }
      });
      this.emit('routeFavorited', { routeId, isFavorite });
    } catch (error) {
      console.error('收藏路线失败:', error);
      throw error;
    }
  }

  /**
   * 删除路线
   */
  async deleteRoute(routeId: number): Promise<void> {
    try {
      await apiClient.delete(`/api/v1/navigation/routes/${routeId}`);
      this.emit('routeDeleted', routeId);
    } catch (error) {
      console.error('删除路线失败:', error);
      throw error;
    }
  }

  /**
   * 使用路线
   */
  async useRoute(routeId: number): Promise<void> {
    try {
      await apiClient.post(`/api/v1/navigation/routes/${routeId}/use`);
    } catch (error) {
      console.error('使用路线失败:', error);
    }
  }

  /**
   * 预估到达时间
   */
  async estimateArrivalTime(startLng: number, startLat: number, 
                            endLng: number, endLat: number, 
                            travelMode: string): Promise<string> {
    try {
      const response = await apiClient.get<string>('/api/v1/navigation/estimate-arrival', {
        params: { startLng, startLat, endLng, endLat, travelMode }
      });
      return response.data;
    } catch (error) {
      console.error('预估到达时间失败:', error);
      throw error;
    }
  }

  /**
   * 获取当前路线
   */
  getCurrentRoute(): RouteResponse | null {
    return this.currentRoute;
  }

  /**
   * 导航是否活跃
   */
  isNavigationActive(): boolean {
    return this.navigationActive;
  }
}

// 类型定义
export interface RouteRequest {
  startLongitude: number;
  startLatitude: number;
  startName?: string;
  endLongitude: number;
  endLatitude: number;
  endName?: string;
  travelMode: 'DRIVE' | 'WALK' | 'RIDE' | 'BUS' | 'TRUCK';
  routeStrategy?: 'FASTEST' | 'SHORTEST' | 'AVOID_TRAFFIC' | 'ECONOMIC';
  waypoints?: WaypointRequest[];
  avoidHighway?: boolean;
  avoidToll?: boolean;
  avoidCongestion?: boolean;
  plateNumber?: string;
  userId?: number;
}

export interface WaypointRequest {
  longitude: number;
  latitude: number;
  name?: string;
  required?: boolean;
  stayDuration?: number;
}

export interface RouteResponse {
  routeId: number;
  routeName: string;
  start: LocationInfo;
  end: LocationInfo;
  travelMode: string;
  routeStrategy: string;
  totalDistance: number;
  totalDistanceText: string;
  estimatedDuration: number;
  estimatedDurationText: string;
  estimatedArrivalTime: string;
  estimatedCost: number;
  routePolyline: string;
  steps: RouteStep[];
  segments?: RouteSegment[];
  waypoints?: WaypointInfo[];
  trafficInfo?: TrafficInfo;
  tollInfo?: TollInfo;
  restrictionInfo?: RestrictionInfo;
  tags: string[];
}

export interface LocationInfo {
  poiId?: number;
  name: string;
  longitude: number;
  latitude: number;
  address?: string;
}

export interface RouteStep {
  stepIndex: number;
  instruction: string;
  distance: number;
  distanceText: string;
  duration: number;
  durationText: string;
  turnType: string;
  roadName?: string;
  actionIcon: string;
  polyline?: string;
}

export interface RouteSegment {
  segmentIndex: number;
  segmentType: string;
  start: LocationInfo;
  end: LocationInfo;
  distance: number;
  duration: number;
  roadType?: string;
  roadName?: string;
  trafficStatus: string;
  tollRoad: boolean;
  tollFee: number;
  turnType: string;
  turnInstruction: string;
}

export interface WaypointInfo {
  index: number;
  poiId?: number;
  name: string;
  longitude: number;
  latitude: number;
  distanceFromStart: number;
  estimatedArrivalTime: number;
}

export interface TrafficInfo {
  smoothDistance: number;
  slowDistance: number;
  congestedDistance: number;
  severelyCongestedDistance: number;
  overallStatus: string;
  overallStatusText: string;
  updateTime: string;
}

export interface TollInfo {
  tollCount: number;
  totalTollFee: number;
  tollGates?: TollGate[];
}

export interface TollGate {
  name: string;
  fee: number;
  longitude: number;
  latitude: number;
}

export interface RestrictionInfo {
  hasRestriction: boolean;
  restrictionType?: string;
  restrictionDesc?: string;
  restrictedRoads?: string[];
}

export const navigationService = NavigationService.getInstance();
