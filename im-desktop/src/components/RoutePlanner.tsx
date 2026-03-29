import React, { useState, useEffect, useCallback } from 'react';
import { navigationService, RouteRequest, RouteResponse, TravelMode } from '../services/navigation.service';
import './RoutePlanner.css';

/**
 * 路线规划组件
 * 提供路线搜索、规划、展示功能
 */
interface RoutePlannerProps {
  userId: number;
  onRouteSelect?: (route: RouteResponse) => void;
}

export const RoutePlanner: React.FC<RoutePlannerProps> = ({ userId, onRouteSelect }) => {
  const [startLocation, setStartLocation] = useState('');
  const [endLocation, setEndLocation] = useState('');
  const [travelMode, setTravelMode] = useState<TravelMode>('DRIVE');
  const [routeStrategy, setRouteStrategy] = useState('FASTEST');
  const [routes, setRoutes] = useState<RouteResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedRoute, setSelectedRoute] = useState<RouteResponse | null>(null);
  const [savedRoutes, setSavedRoutes] = useState<RouteResponse[]>([]);

  // 出行方式选项
  const travelModes = [
    { value: 'DRIVE', label: '驾车', icon: '🚗' },
    { value: 'BUS', label: '公交', icon: '🚌' },
    { value: 'RIDE', label: '骑行', icon: '🚴' },
    { value: 'WALK', label: '步行', icon: '🚶' },
  ];

  // 路线策略选项
  const strategies = [
    { value: 'FASTEST', label: '最快' },
    { value: 'SHORTEST', label: '最短' },
    { value: 'AVOID_TRAFFIC', label: '避堵' },
    { value: 'ECONOMIC', label: '经济' },
  ];

  // 加载用户保存的路线
  useEffect(() => {
    loadSavedRoutes();
  }, [userId]);

  const loadSavedRoutes = async () => {
    try {
      const routes = await navigationService.getUserRoutes(userId);
      setSavedRoutes(routes);
    } catch (error) {
      console.error('加载路线失败:', error);
    }
  };

  // 规划路线
  const handlePlanRoute = async () => {
    if (!startLocation || !endLocation) {
      alert('请输入起点和终点');
      return;
    }

    setLoading(true);
    try {
      // 使用地理编码获取坐标(简化版使用模拟坐标)
      const request: RouteRequest = {
        startLongitude: 116.397428,
        startLatitude: 39.90923,
        startName: startLocation,
        endLongitude: 116.397428,
        endLatitude: 39.91523,
        endName: endLocation,
        travelMode,
        routeStrategy,
        userId,
      };

      const routePlans = await navigationService.planMultipleRoutes(request);
      setRoutes(routePlans);
      
      if (routePlans.length > 0) {
        setSelectedRoute(routePlans[0]);
      }
    } catch (error) {
      console.error('规划路线失败:', error);
      alert('规划路线失败，请重试');
    } finally {
      setLoading(false);
    }
  };

  // 选择路线
  const handleSelectRoute = useCallback((route: RouteResponse) => {
    setSelectedRoute(route);
    onRouteSelect?.(route);
  }, [onRouteSelect]);

  // 开始导航
  const handleStartNavigation = () => {
    if (selectedRoute) {
      navigationService.startNavigation(selectedRoute);
      alert(`开始导航: ${selectedRoute.routeName}`);
    }
  };

  // 保存路线
  const handleSaveRoute = async () => {
    if (!selectedRoute) return;
    
    try {
      // 调用保存接口
      await navigationService.useRoute(selectedRoute.routeId);
      alert('路线已保存');
      loadSavedRoutes();
    } catch (error) {
      console.error('保存路线失败:', error);
    }
  };

  // 收藏路线
  const handleFavoriteRoute = async (routeId: number, isFavorite: boolean) => {
    try {
      await navigationService.favoriteRoute(routeId, isFavorite);
      loadSavedRoutes();
    } catch (error) {
      console.error('收藏路线失败:', error);
    }
  };

  // 交换起点终点
  const handleSwapLocations = () => {
    const temp = startLocation;
    setStartLocation(endLocation);
    setEndLocation(temp);
  };

  return (
    <div className="route-planner">
      <div className="planner-header">
        <h3>路线规划</h3>
      </div>

      {/* 搜索区域 */}
      <div className="search-section">
        <div className="location-inputs">
          <div className="input-group">
            <span className="input-icon start">●</span>
            <input
              type="text"
              placeholder="输入起点"
              value={startLocation}
              onChange={(e) => setStartLocation(e.target.value)}
              className="location-input"
            />
          </div>
          
          <button className="swap-btn" onClick={handleSwapLocations} title="交换起点终点">
            ⇅
          </button>
          
          <div className="input-group">
            <span className="input-icon end">●</span>
            <input
              type="text"
              placeholder="输入终点"
              value={endLocation}
              onChange={(e) => setEndLocation(e.target.value)}
              className="location-input"
            />
          </div>
        </div>

        {/* 出行方式选择 */}
        <div className="travel-modes">
          {travelModes.map((mode) => (
            <button
              key={mode.value}
              className={`mode-btn ${travelMode === mode.value ? 'active' : ''}`}
              onClick={() => setTravelMode(mode.value as TravelMode)}
            >
              <span className="mode-icon">{mode.icon}</span>
              <span className="mode-label">{mode.label}</span>
            </button>
          ))}
        </div>

        {/* 路线策略 */}
        <div className="route-strategy">
          {strategies.map((strategy) => (
            <button
              key={strategy.value}
              className={`strategy-btn ${routeStrategy === strategy.value ? 'active' : ''}`}
              onClick={() => setRouteStrategy(strategy.value)}
            >
              {strategy.label}
            </button>
          ))}
        </div>

        {/* 规划按钮 */}
        <button
          className="plan-btn"
          onClick={handlePlanRoute}
          disabled={loading}
        >
          {loading ? '规划中...' : '规划路线'}
        </button>
      </div>

      {/* 路线结果列表 */}
      {routes.length > 0 && (
        <div className="routes-list">
          <h4>推荐路线</h4>
          {routes.map((route, index) => (
            <div
              key={route.routeId}
              className={`route-card ${selectedRoute?.routeId === route.routeId ? 'selected' : ''}`}
              onClick={() => handleSelectRoute(route)}
            >
              <div className="route-header">
                <span className="route-tag">方案{index + 1}</span>
                <div className="route-tags">
                  {route.tags?.map((tag) => (
                    <span key={tag} className="tag">{tag}</span>
                  ))}
                </div>
              </div>
              
              <div className="route-info">
                <div className="info-item">
                  <span className="info-value">{route.estimatedDurationText}</span>
                  <span className="info-label">预计用时</span>
                </div>
                <div className="info-item">
                  <span className="info-value">{route.totalDistanceText}</span>
                  <span className="info-label">总距离</span>
                </div>
                {route.estimatedCost > 0 && (
                  <div className="info-item">
                    <span className="info-value">¥{route.estimatedCost}</span>
                    <span className="info-label">预计费用</span>
                  </div>
                )}
              </div>

              {route.trafficInfo && (
                <div className="traffic-info">
                  <span className={`traffic-status ${route.trafficInfo.overallStatus.toLowerCase()}`}>
                    路况: {route.trafficInfo.overallStatusText}
                  </span>
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {/* 选中的路线详情 */}
      {selectedRoute && (
        <div className="route-detail">
          <h4>路线详情</h4>
          <div className="detail-header">
            <span className="route-name">{selectedRoute.routeName}</span>
            <span className="arrival-time">预计到达: {selectedRoute.estimatedArrivalTime}</span>
          </div>

          {/* 导航步骤 */}
          <div className="route-steps">
            {selectedRoute.steps?.map((step) => (
              <div key={step.stepIndex} className="step-item">
                <span className="step-icon">{step.actionIcon}</span>
                <div className="step-content">
                  <span className="step-instruction">{step.instruction}</span>
                  {(step.distance > 0 || step.duration > 0) && (
                    <span className="step-info">
                      {step.distanceText} · {step.durationText}
                    </span>
                  )}
                </div>
              </div>
            ))}
          </div>

          {/* 操作按钮 */}
          <div className="route-actions">
            <button className="action-btn primary" onClick={handleStartNavigation}>
              开始导航
            </button>
            <button className="action-btn" onClick={handleSaveRoute}>
              保存路线
            </button>
          </div>
        </div>
      )}

      {/* 历史路线 */}
      {savedRoutes.length > 0 && (
        <div className="saved-routes">
          <h4>历史路线</h4>
          {savedRoutes.slice(0, 5).map((route) => (
            <div key={route.routeId} className="saved-route-item">
              <div className="saved-route-info" onClick={() => handleSelectRoute(route)}>
                <span className="saved-route-name">{route.routeName}</span>
                <span className="saved-route-meta">
                  {route.totalDistanceText} · {route.estimatedDurationText}
                </span>
              </div>
              <button
                className={`favorite-btn ${route.isFavorite ? 'active' : ''}`}
                onClick={() => handleFavoriteRoute(route.routeId, !route.isFavorite)}
              >
                {route.isFavorite ? '★' : '☆'}
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

// 添加类型声明
type TravelMode = 'DRIVE' | 'WALK' | 'RIDE' | 'BUS' | 'TRUCK';
