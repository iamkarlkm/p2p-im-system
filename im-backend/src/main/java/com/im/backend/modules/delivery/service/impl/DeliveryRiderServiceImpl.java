package com.im.backend.modules.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.modules.delivery.dto.*;
import com.im.backend.modules.delivery.entity.DeliveryRider;
import com.im.backend.modules.delivery.mapper.DeliveryRiderMapper;
import com.im.backend.modules.delivery.service.DeliveryRiderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 骑手服务实现
 * 本地物流配送智能调度引擎
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryRiderServiceImpl extends ServiceImpl<DeliveryRiderMapper, DeliveryRider> implements DeliveryRiderService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String RIDER_GEO_KEY = "delivery:rider:geo";
    private static final String RIDER_STATUS_KEY = "delivery:rider:status";

    @Override
    @Transactional
    public RiderVO register(RiderRegisterDTO dto) {
        DeliveryRider rider = new DeliveryRider();
        rider.setUserId(dto.getUserId());
        rider.setRiderNo("R" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        rider.setRealName(dto.getRealName());
        rider.setPhone(dto.getPhone());
        rider.setStatus("IDLE");
        rider.setStationId(dto.getStationId());
        rider.setMaxOrderCount(dto.getMaxOrderCount());
        rider.setCurrentOrderCount(0);
        rider.setEnabled(true);
        rider.setTotalOrders(0);
        rider.setTodayOrderCount(0);
        rider.setTodayDistance(0);
        rider.setRating(new BigDecimal("5.0"));

        this.save(rider);
        log.info("骑手注册成功: riderId={}, name={}", rider.getId(), rider.getRealName());

        return convertToVO(rider);
    }

    @Override
    public boolean updateLocation(Long riderId, LocationUpdateDTO dto) {
        DeliveryRider rider = this.getById(riderId);
        if (rider == null) {
            return false;
        }

        // 更新数据库
        rider.setCurrentLat(dto.getLat());
        rider.setCurrentLng(dto.getLng());
        rider.setLocationUpdatedAt(LocalDateTime.now());
        this.updateById(rider);

        // 更新Redis Geo
        redisTemplate.opsForGeo().add(
            RIDER_GEO_KEY,
            new Point(dto.getLng().doubleValue(), dto.getLat().doubleValue()),
            riderId.toString()
        );

        log.debug("骑手位置更新: riderId={}, lat={}, lng={}", riderId, dto.getLat(), dto.getLng());
        return true;
    }

    @Override
    public List<RiderVO> findNearbyAvailableRiders(BigDecimal lat, BigDecimal lng, Integer radius) {
        // 使用Redis Geo查询附近骑手
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().radius(
            RIDER_GEO_KEY,
            new Point(lng.doubleValue(), lat.doubleValue()),
            new Distance(radius, RedisGeoCommands.DistanceUnit.METERS)
        );

        if (results == null) {
            return new ArrayList<>();
        }

        return results.getContent().stream()
            .map(geo -> {
                Long riderId = Long.valueOf(geo.getContent().getName());
                DeliveryRider rider = this.getById(riderId);
                if (rider == null || !"IDLE".equals(rider.getStatus())) {
                    return null;
                }
                RiderVO vo = convertToVO(rider);
                vo.setDistance(geo.getDistance().getValue());
                return vo;
            })
            .filter(r -> r != null)
            .collect(Collectors.toList());
    }

    @Override
    public RiderVO findNearestRider(BigDecimal lat, BigDecimal lng, String bizType) {
        // 查询半径递增: 500m -> 1000m -> 2000m -> 5000m
        int[] radiusList = {500, 1000, 2000, 5000};

        for (int radius : radiusList) {
            List<RiderVO> riders = findNearbyAvailableRiders(lat, lng, radius);
            if (!riders.isEmpty()) {
                // 返回最近的骑手
                return riders.get(0);
            }
        }

        return null;
    }

    @Override
    public boolean updateStatus(Long riderId, String status) {
        DeliveryRider rider = this.getById(riderId);
        if (rider == null) {
            return false;
        }

        rider.setStatus(status);
        if ("ONLINE".equals(status)) {
            rider.setLastOnlineAt(LocalDateTime.now());
        }

        this.updateById(rider);
        redisTemplate.opsForHash().put(RIDER_STATUS_KEY, riderId.toString(), status);

        log.info("骑手状态更新: riderId={}, status={}", riderId, status);
        return true;
    }

    @Override
    public boolean batchUpdateLocations(List<LocationUpdateDTO> locations) {
        // TODO: 批量更新实现
        return true;
    }

    @Override
    public RiderLocationVO getCurrentLocation(Long riderId) {
        DeliveryRider rider = this.getById(riderId);
        if (rider == null || rider.getCurrentLat() == null) {
            return null;
        }

        RiderLocationVO vo = new RiderLocationVO();
        vo.setRiderId(riderId);
        vo.setRiderName(rider.getRealName());
        vo.setLat(rider.getCurrentLat());
        vo.setLng(rider.getCurrentLng());
        vo.setUpdatedAt(rider.getLocationUpdatedAt() != null ? rider.getLocationUpdatedAt().toString() : null);
        return vo;
    }

    @Override
    public List<LocationPointVO> getRiderTrajectory(Long riderId, String startTime, String endTime) {
        // TODO: 从轨迹表查询历史轨迹
        return new ArrayList<>();
    }

    @Override
    public Page<RiderVO> pageRiders(RiderQueryDTO query) {
        LambdaQueryWrapper<DeliveryRider> wrapper = new LambdaQueryWrapper<>();

        if (query.getStationId() != null) {
            wrapper.eq(DeliveryRider::getStationId, query.getStationId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(DeliveryRider::getStatus, query.getStatus());
        }
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(DeliveryRider::getRealName, query.getKeyword())
                .or().like(DeliveryRider::getPhone, query.getKeyword()));
        }
        if (query.getEnabled() != null) {
            wrapper.eq(DeliveryRider::getEnabled, query.getEnabled());
        }

        Page<DeliveryRider> page = this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);

        List<RiderVO> voList = page.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());

        Page<RiderVO> voPage = new Page<>();
        voPage.setRecords(voList);
        voPage.setTotal(page.getTotal());
        voPage.setCurrent(page.getCurrent());
        voPage.setSize(page.getSize());

        return voPage;
    }

    @Override
    public RiderStatsVO getRiderStats(Long riderId) {
        DeliveryRider rider = this.getById(riderId);
        if (rider == null) {
            return null;
        }

        RiderStatsVO stats = new RiderStatsVO();
        stats.setRiderId(riderId);
        stats.setRiderName(rider.getRealName());
        stats.setTodayOrders(rider.getTodayOrderCount());
        stats.setTodayDistance(rider.getTodayDistance());
        stats.setTotalOrders(rider.getTotalOrders());
        stats.setAvgRating(rider.getRating());
        // TODO: 统计其他数据
        return stats;
    }

    @Override
    public List<RiderVO> getOnlineRidersByStation(Long stationId) {
        LambdaQueryWrapper<DeliveryRider> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeliveryRider::getStationId, stationId)
            .and(w -> w.eq(DeliveryRider::getStatus, "IDLE")
                .or().eq(DeliveryRider::getStatus, "BUSY"));

        return this.list(wrapper).stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    private RiderVO convertToVO(DeliveryRider rider) {
        RiderVO vo = new RiderVO();
        vo.setId(rider.getId());
        vo.setUserId(rider.getUserId());
        vo.setRiderNo(rider.getRiderNo());
        vo.setRealName(rider.getRealName());
        vo.setPhone(rider.getPhone());
        vo.setStatus(rider.getStatus());
        vo.setCurrentLat(rider.getCurrentLat());
        vo.setCurrentLng(rider.getCurrentLng());
        vo.setLocationUpdatedAt(rider.getLocationUpdatedAt());
        vo.setCurrentOrderCount(rider.getCurrentOrderCount());
        vo.setMaxOrderCount(rider.getMaxOrderCount());
        vo.setGeoHash(rider.getGeoHash());
        vo.setStationId(rider.getStationId());
        vo.setTodayOrderCount(rider.getTodayOrderCount());
        vo.setTodayDistance(rider.getTodayDistance());
        vo.setRating(rider.getRating());
        vo.setTotalOrders(rider.getTotalOrders());
        vo.setEnabled(rider.getEnabled());
        vo.setLastOnlineAt(rider.getLastOnlineAt());
        vo.setCreatedAt(rider.getCreatedAt());
        return vo;
    }
}
