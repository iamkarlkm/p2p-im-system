package com.im.backend.modules.navigation.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.common.exception.BusinessException;
import com.im.backend.modules.navigation.dto.RouteRequestDTO;
import com.im.backend.modules.navigation.dto.RouteResponseDTO;
import com.im.backend.modules.navigation.entity.NavigationRoute;
import com.im.backend.modules.navigation.enums.TravelMode;
import com.im.backend.modules.navigation.repository.NavigationRouteRepository;
import com.im.backend.modules.navigation.service.NavigationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 导航服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NavigationServiceImpl implements NavigationService {

    private final NavigationRouteRepository routeRepository;

    @Override
    public RouteResponseDTO planRoute(RouteRequestDTO request) {
        log.info("开始规划路线: 起点({},{}) -> 终点({},{}), 方式:{}",
                request.getStartLongitude(), request.getStartLatitude(),
                request.getEndLongitude(), request.getEndLatitude(),
                request.getTravelMode());

        TravelMode mode = TravelMode.getByCode(request.getTravelMode());
        
        // 计算直线距离
        double distance = calculateDistance(
                request.getStartLatitude().doubleValue(), request.getStartLongitude().doubleValue(),
                request.getEndLatitude().doubleValue(), request.getEndLongitude().doubleValue()
        );

        // 根据路线策略调整
        String strategy = request.getRouteStrategy() != null ? request.getRouteStrategy() : "FASTEST";
        int estimatedDuration = estimateDuration((int) distance, mode, strategy);
        
        // 生成路线点串(简化版直线)
        String polyline = generatePolyline(request);
        
        // 生成步骤
        List<RouteResponseDTO.RouteStepDTO> steps = generateRouteSteps(request, (int) distance, mode);

        // 计算费用
        BigDecimal cost = calculateCost(mode, (int) distance, request.getAvoidToll());

        // 构建响应
        RouteResponseDTO response = RouteResponseDTO.builder()
                .routeId(System.currentTimeMillis())
                .routeName(generateRouteName(request))
                .start(RouteResponseDTO.LocationDTO.builder()
                        .name(request.getStartName())
                        .longitude(request.getStartLongitude())
                        .latitude(request.getStartLatitude())
                        .build())
                .end(RouteResponseDTO.LocationDTO.builder()
                        .name(request.getEndName())
                        .longitude(request.getEndLongitude())
                        .latitude(request.getEndLatitude())
                        .build())
                .travelMode(mode.getCode())
                .routeStrategy(strategy)
                .totalDistance((int) distance)
                .totalDistanceText(formatDistance((int) distance))
                .estimatedDuration(estimatedDuration)
                .estimatedDurationText(formatDuration(estimatedDuration))
                .estimatedArrivalTime(calculateArrivalTime(estimatedDuration))
                .estimatedCost(cost)
                .routePolyline(polyline)
                .steps(steps)
                .trafficInfo(generateTrafficInfo(mode, strategy))
                .tollInfo(generateTollInfo(mode, request.getAvoidToll()))
                .restrictionInfo(checkRestrictionInfo(request.getPlateNumber()))
                .tags(generateRouteTags(mode, strategy))
                .build();

        log.info("路线规划完成: 距离{}米, 预计时间{}秒", (int) distance, estimatedDuration);
        return response;
    }

    @Override
    public List<RouteResponseDTO> planMultipleRoutes(RouteRequestDTO request) {
        List<RouteResponseDTO> routes = new ArrayList<>();
        
        // 最快路线
        request.setRouteStrategy("FASTEST");
        routes.add(planRoute(request));
        
        // 最短路线(仅驾车模式)
        if ("DRIVE".equals(request.getTravelMode()) || "TRUCK".equals(request.getTravelMode())) {
            request.setRouteStrategy("SHORTEST");
            routes.add(planRoute(request));
        }
        
        // 避堵路线
        if ("DRIVE".equals(request.getTravelMode())) {
            request.setRouteStrategy("AVOID_TRAFFIC");
            routes.add(planRoute(request));
        }
        
        return routes;
    }

    @Override
    public RouteResponseDTO reRoute(Long routeId, String reason) {
        log.info("重新规划路线: routeId={}, reason={}", routeId, reason);
        NavigationRoute route = routeRepository.selectById(routeId);
        if (route == null) {
            throw new BusinessException("路线不存在");
        }
        
        RouteRequestDTO request = RouteRequestDTO.builder()
                .startLongitude(route.getStartLongitude())
                .startLatitude(route.getStartLatitude())
                .endLongitude(route.getEndLongitude())
                .endLatitude(route.getEndLatitude())
                .travelMode(route.getTravelMode())
                .routeStrategy(route.getRouteStrategy())
                .build();
        
        return planRoute(request);
    }

    @Override
    @Transactional
    public Long saveRoute(RouteRequestDTO request, RouteResponseDTO response) {
        NavigationRoute route = new NavigationRoute();
        route.setUserId(request.getUserId());
        route.setRouteName(response.getRouteName());
        route.setStartPoiId(response.getStart().getPoiId());
        route.setStartName(response.getStart().getName());
        route.setStartLongitude(response.getStart().getLongitude());
        route.setStartLatitude(response.getStart().getLatitude());
        route.setEndPoiId(response.getEnd().getPoiId());
        route.setEndName(response.getEnd().getName());
        route.setEndLongitude(response.getEnd().getLongitude());
        route.setEndLatitude(response.getEnd().getLatitude());
        route.setTravelMode(response.getTravelMode());
        route.setRouteStrategy(response.getRouteStrategy());
        route.setTotalDistance(response.getTotalDistance());
        route.setEstimatedDuration(response.getEstimatedDuration());
        route.setEstimatedCost(response.getEstimatedCost());
        route.setRoutePolyline(response.getRoutePolyline());
        route.setWaypointCount(response.getWaypoints() != null ? response.getWaypoints().size() : 0);
        route.setIsFavorite(false);
        route.setUsageCount(0);
        route.setStatus("ACTIVE");
        
        routeRepository.insert(route);
        return route.getId();
    }

    @Override
    public RouteResponseDTO getRouteDetail(Long routeId) {
        NavigationRoute route = routeRepository.selectById(routeId);
        if (route == null) {
            throw new BusinessException("路线不存在");
        }
        return convertToDTO(route);
    }

    @Override
    public List<RouteResponseDTO> getUserRoutes(Long userId) {
        List<NavigationRoute> routes = routeRepository.findByUserId(userId);
        return routes.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public IPage<RouteResponseDTO> getUserRoutesPage(Page<NavigationRoute> page, Long userId) {
        IPage<NavigationRoute> routePage = routeRepository.findPageByUserId(page, userId);
        return routePage.convert(this::convertToDTO);
    }

    @Override
    public List<RouteResponseDTO> getFavoriteRoutes(Long userId) {
        List<NavigationRoute> routes = routeRepository.findFavoriteRoutes(userId);
        return routes.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<RouteResponseDTO> getSimilarRoutes(Long userId, Double startLng, Double startLat, 
                                                     Double endLng, Double endLat) {
        List<NavigationRoute> routes = routeRepository.findSimilarRoutes(userId, startLng, startLat, endLng, endLat);
        return routes.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateFavoriteStatus(Long routeId, Boolean isFavorite) {
        routeRepository.updateFavoriteStatus(routeId, isFavorite);
    }

    @Override
    @Transactional
    public void deleteRoute(Long routeId) {
        routeRepository.deleteById(routeId);
    }

    @Override
    @Transactional
    public void useRoute(Long routeId) {
        routeRepository.incrementUsageCount(routeId);
    }

    @Override
    public RouteResponseDTO.TrafficInfoDTO getRealTimeTraffic(String polyline) {
        return generateTrafficInfo(TravelMode.DRIVE, "FASTEST");
    }

    @Override
    public String estimateArrivalTime(Double startLng, Double startLat, 
                                      Double endLng, Double endLat, String travelMode) {
        TravelMode mode = TravelMode.getByCode(travelMode);
        double distance = calculateDistance(startLat, startLng, endLat, endLng);
        int duration = estimateDuration((int) distance, mode, "FASTEST");
        return calculateArrivalTime(duration);
    }

    @Override
    public RouteResponseDTO.RestrictionInfoDTO checkRestriction(String polyline, String plateNumber) {
        return checkRestrictionInfo(plateNumber);
    }

    // ========== 私有辅助方法 ==========

    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6371000; // 地球半径(米)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private int estimateDuration(int distance, TravelMode mode, String strategy) {
        double speed = mode.getDefaultSpeed();
        
        // 路线策略调整
        if ("AVOID_TRAFFIC".equals(strategy)) {
            speed *= 0.9; // 避堵路线略慢
        } else if ("SHORTEST".equals(strategy)) {
            speed *= 0.8; // 最短路线可能更慢
        }
        
        return (int) (distance / speed);
    }

    private String generatePolyline(RouteRequestDTO request) {
        return String.format("%s,%s;%s,%s",
                request.getStartLongitude(), request.getStartLatitude(),
                request.getEndLongitude(), request.getEndLatitude());
    }

    private String generateRouteName(RouteRequestDTO request) {
        String start = request.getStartName() != null ? request.getStartName() : "起点";
        String end = request.getEndName() != null ? request.getEndName() : "终点";
        return start + " -> " + end;
    }

    private List<RouteResponseDTO.RouteStepDTO> generateRouteSteps(RouteRequestDTO request, int totalDistance, TravelMode mode) {
        List<RouteResponseDTO.RouteStepDTO> steps = new ArrayList<>();
        
        // 起点
        steps.add(RouteResponseDTO.RouteStepDTO.builder()
                .stepIndex(0)
                .instruction("从" + (request.getStartName() != null ? request.getStartName() : "起点") + "出发")
                .distance(0)
                .duration(0)
                .turnType("START")
                .actionIcon("start")
                .build());
        
        // 途经点
        if (request.getWaypoints() != null) {
            for (int i = 0; i < request.getWaypoints().size(); i++) {
                RouteRequestDTO.WaypointDTO wp = request.getWaypoints().get(i);
                steps.add(RouteResponseDTO.RouteStepDTO.builder()
                        .stepIndex(i + 1)
                        .instruction("途经 " + (wp.getName() != null ? wp.getName() : "途经点"))
                        .distance(0)
                        .duration(0)
                        .turnType("WAYPOINT")
                        .actionIcon("waypoint")
                        .build());
            }
        }
        
        // 终点
        steps.add(RouteResponseDTO.RouteStepDTO.builder()
                .stepIndex(steps.size())
                .instruction("到达" + (request.getEndName() != null ? request.getEndName() : "终点"))
                .distance(totalDistance)
                .duration(0)
                .turnType("END")
                .actionIcon("end")
                .build());
        
        return steps;
    }

    private BigDecimal calculateCost(TravelMode mode, int distance, Boolean avoidToll) {
        if (!mode.calculateCost() || Boolean.TRUE.equals(avoidToll)) {
            return BigDecimal.ZERO;
        }
        
        if (mode == TravelMode.DRIVE || mode == TravelMode.TRUCK) {
            // 简单计算: 每公里0.5元
            return BigDecimal.valueOf(distance / 1000.0 * 0.5).setScale(2, RoundingMode.HALF_UP);
        }
        
        return BigDecimal.ZERO;
    }

    private String formatDistance(int distance) {
        if (distance < 1000) {
            return distance + "米";
        }
        return String.format("%.1f公里", distance / 1000.0);
    }

    private String formatDuration(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        
        if (hours > 0) {
            return String.format("%d小时%d分钟", hours, minutes);
        }
        return minutes + "分钟";
    }

    private String calculateArrivalTime(int durationSeconds) {
        LocalDateTime arrival = LocalDateTime.now().plusSeconds(durationSeconds);
        return arrival.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private RouteResponseDTO.TrafficInfoDTO generateTrafficInfo(TravelMode mode, String strategy) {
        if (!mode.supportsTraffic()) {
            return null;
        }
        
        return RouteResponseDTO.TrafficInfoDTO.builder()
                .overallStatus("SMOOTH")
                .overallStatusText("畅通")
                .updateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
                .build();
    }

    private RouteResponseDTO.TollInfoDTO generateTollInfo(TravelMode mode, Boolean avoidToll) {
        if (Boolean.TRUE.equals(avoidToll) || !mode.calculateCost()) {
            return RouteResponseDTO.TollInfoDTO.builder()
                    .tollCount(0)
                    .totalTollFee(BigDecimal.ZERO)
                    .build();
        }
        
        return RouteResponseDTO.TollInfoDTO.builder()
                .tollCount(0)
                .totalTollFee(BigDecimal.ZERO)
                .build();
    }

    private RouteResponseDTO.RestrictionInfoDTO checkRestrictionInfo(String plateNumber) {
        return RouteResponseDTO.RestrictionInfoDTO.builder()
                .hasRestriction(false)
                .build();
    }

    private List<String> generateRouteTags(TravelMode mode, String strategy) {
        List<String> tags = new ArrayList<>();
        tags.add(mode.getDesc());
        
        if ("FASTEST".equals(strategy)) {
            tags.add("最快");
        } else if ("SHORTEST".equals(strategy)) {
            tags.add("最短");
        } else if ("AVOID_TRAFFIC".equals(strategy)) {
            tags.add("避堵");
        } else if ("ECONOMIC".equals(strategy)) {
            tags.add("经济");
        }
        
        return tags;
    }

    private RouteResponseDTO convertToDTO(NavigationRoute route) {
        RouteResponseDTO dto = new RouteResponseDTO();
        BeanUtils.copyProperties(route, dto);
        
        dto.setStart(RouteResponseDTO.LocationDTO.builder()
                .name(route.getStartName())
                .longitude(route.getStartLongitude())
                .latitude(route.getStartLatitude())
                .build());
        
        dto.setEnd(RouteResponseDTO.LocationDTO.builder()
                .name(route.getEndName())
                .longitude(route.getEndLongitude())
                .latitude(route.getEndLatitude())
                .build());
        
        dto.setTotalDistanceText(formatDistance(route.getTotalDistance()));
        dto.setEstimatedDurationText(formatDuration(route.getEstimatedDuration()));
        
        return dto;
    }
}
