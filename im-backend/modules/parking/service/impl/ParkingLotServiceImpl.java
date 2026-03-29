package com.im.backend.modules.parking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.common.exception.BusinessException;
import com.im.backend.modules.parking.dto.*;
import com.im.backend.modules.parking.entity.ParkingLot;
import com.im.backend.modules.parking.mapper.ParkingLotMapper;
import com.im.backend.modules.parking.service.ParkingLotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 停车场服务实现类
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingLotServiceImpl extends ServiceImpl<ParkingLotMapper, ParkingLot> implements ParkingLotService {

    private final ParkingLotMapper parkingLotMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PARKING_GEO_KEY = "parking:geo";
    private static final String PARKING_SPACE_KEY = "parking:space:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createParkingLot(ParkingLotCreateDTO dto) {
        // 检查编码是否重复
        LambdaQueryWrapper<ParkingLot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParkingLot::getCode, dto.getCode());
        if (count(wrapper) > 0) {
            throw new BusinessException("停车场编码已存在");
        }

        ParkingLot parkingLot = new ParkingLot();
        BeanUtils.copyProperties(dto, parkingLot);

        // 生成GeoHash
        parkingLot.setGeoHash(encodeGeoHash(parkingLot.getLatitude(), parkingLot.getLongitude()));

        // 初始化统计数据
        parkingLot.setRating(BigDecimal.ZERO);
        parkingLot.setRatingCount(0);
        parkingLot.setTodayParkingCount(0);
        parkingLot.setStatus(1);
        parkingLot.setAvailableSpaces(dto.getTotalSpaces());
        parkingLot.setOccupiedSpaces(0);

        save(parkingLot);

        // 添加到Redis Geo
        addToRedisGeo(parkingLot);

        log.info("创建停车场成功: id={}, name={}", parkingLot.getId(), parkingLot.getName());
        return parkingLot.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateParkingLot(Long id, ParkingLotUpdateDTO dto) {
        ParkingLot parkingLot = getById(id);
        if (parkingLot == null) {
            throw new BusinessException("停车场不存在");
        }

        BeanUtils.copyProperties(dto, parkingLot);

        // 更新GeoHash
        if (dto.getLatitude() != null && dto.getLongitude() != null) {
            parkingLot.setGeoHash(encodeGeoHash(dto.getLatitude(), dto.getLongitude()));
            // 更新Redis Geo
            updateRedisGeo(parkingLot);
        }

        boolean result = updateById(parkingLot);
        log.info("更新停车场成功: id={}", id);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteParkingLot(Long id) {
        boolean result = removeById(id);
        if (result) {
            // 从Redis Geo移除
            redisTemplate.opsForGeo().remove(PARKING_GEO_KEY, id.toString());
        }
        log.info("删除停车场成功: id={}", id);
        return result;
    }

    @Override
    public ParkingLotDetailVO getParkingLotDetail(Long id) {
        ParkingLot parkingLot = getById(id);
        if (parkingLot == null) {
            throw new BusinessException("停车场不存在");
        }

        ParkingLotDetailVO vo = new ParkingLotDetailVO();
        BeanUtils.copyProperties(parkingLot, vo);

        // 获取实时空位数
        Integer realTimeSpaces = getRealTimeSpaces(id);
        if (realTimeSpaces != null) {
            vo.setAvailableSpaces(realTimeSpaces);
        }

        return vo;
    }

    @Override
    public Page<ParkingLotListVO> pageParkingLots(ParkingLotQueryDTO dto) {
        LambdaQueryWrapper<ParkingLot> wrapper = new LambdaQueryWrapper<>();

        // 条件过滤
        if (StringUtils.hasText(dto.getName())) {
            wrapper.like(ParkingLot::getName, dto.getName());
        }
        if (dto.getType() != null) {
            wrapper.eq(ParkingLot::getType, dto.getType());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(ParkingLot::getStatus, dto.getStatus());
        }
        if (StringUtils.hasText(dto.getAreaCode())) {
            wrapper.likeRight(ParkingLot::getAreaCode, dto.getAreaCode());
        }
        if (dto.getMerchantId() != null) {
            wrapper.eq(ParkingLot::getMerchantId, dto.getMerchantId());
        }

        // 只查询未删除的
        wrapper.eq(ParkingLot::getDeleted, 0);
        wrapper.orderByDesc(ParkingLot::getWeightScore, ParkingLot::getCreateTime);

        Page<ParkingLot> page = page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);

        // 转换VO
        List<ParkingLotListVO> voList = page.getRecords().stream().map(this::convertToListVO).collect(Collectors.toList());

        Page<ParkingLotListVO> resultPage = new Page<>();
        resultPage.setRecords(voList);
        resultPage.setTotal(page.getTotal());
        resultPage.setSize(page.getSize());
        resultPage.setCurrent(page.getCurrent());

        return resultPage;
    }

    @Override
    public Page<ParkingLotNearbyVO> searchNearbyParkingLots(Double longitude, Double latitude,
                                                           Integer radius, Integer pageNum, Integer pageSize) {
        // 使用Redis Geo查询附近停车场
        Point center = new Point(longitude, latitude);
        Distance distance = new Distance(radius, RedisGeoCommands.DistanceUnit.METERS);

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .sortAscending()
                .limit(pageNum * pageSize);

        var geoResults = redisTemplate.opsForGeo().radius(PARKING_GEO_KEY, center, distance, args);

        List<ParkingLotNearbyVO> voList = new ArrayList<>();
        if (geoResults != null) {
            for (var result : geoResults) {
                String member = result.getContent().getName();
                Double dist = result.getDistance().getValue();
                Long parkingLotId = Long.valueOf(member);

                ParkingLot parkingLot = getById(parkingLotId);
                if (parkingLot != null && parkingLot.getStatus() == 1) {
                    ParkingLotNearbyVO vo = convertToNearbyVO(parkingLot, dist);
                    voList.add(vo);
                }
            }
        }

        // 分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, voList.size());
        List<ParkingLotNearbyVO> pageList = voList.subList(Math.min(start, voList.size()), end);

        Page<ParkingLotNearbyVO> page = new Page<>();
        page.setRecords(pageList);
        page.setTotal(voList.size());
        page.setSize(pageSize);
        page.setCurrent(pageNum);

        return page;
    }

    @Override
    public List<ParkingLotRecommendVO> recommendParkingLots(Double longitude, Double latitude, Integer limit) {
        // 搜索附近的停车场
        Page<ParkingLotNearbyVO> nearbyPage = searchNearbyParkingLots(longitude, latitude, 5000, 1, 50);

        List<ParkingLotRecommendVO> recommendList = new ArrayList<>();

        for (ParkingLotNearbyVO nearby : nearbyPage.getRecords()) {
            ParkingLot parkingLot = getById(nearby.getId());
            if (parkingLot == null || parkingLot.getAvailableSpaces() <= 0) {
                continue;
            }

            // 计算推荐分数
            Integer score = parkingLot.calculateRecommendScore(nearby.getDistance());

            ParkingLotRecommendVO vo = new ParkingLotRecommendVO();
            BeanUtils.copyProperties(nearby, vo);
            vo.setRecommendScore(score);
            vo.setRecommendReason(generateRecommendReason(parkingLot, nearby.getDistance()));

            recommendList.add(vo);
        }

        // 按推荐分数排序
        recommendList.sort((a, b) -> b.getRecommendScore().compareTo(a.getRecommendScore()));

        // 限制数量
        return recommendList.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<ParkingLotNearbyVO> searchParkingLotsByDestination(Double destLongitude, Double destLatitude, Integer radius) {
        return searchNearbyParkingLots(destLongitude, destLatitude, radius, 1, 20).getRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRealTimeSpaces(Long parkingLotId, Integer availableSpaces) {
        ParkingLot parkingLot = getById(parkingLotId);
        if (parkingLot == null) {
            return false;
        }

        parkingLot.setAvailableSpaces(availableSpaces);
        parkingLot.setOccupiedSpaces(parkingLot.getTotalSpaces() - availableSpaces);
        parkingLot.setRealTimeUpdateTime(LocalDateTime.now());

        // 更新Redis
        String key = PARKING_SPACE_KEY + parkingLotId;
        redisTemplate.opsForValue().set(key, availableSpaces);

        return updateById(parkingLot);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateSpaces(List<ParkingSpaceUpdateDTO> spaceDataList) {
        for (ParkingSpaceUpdateDTO dto : spaceDataList) {
            updateRealTimeSpaces(dto.getParkingLotId(), dto.getAvailableSpaces());
        }
    }

    @Override
    public Integer predictAvailableSpaces(Long parkingLotId, Integer minutes) {
        // 基于历史数据预测
        // 简化实现：根据当前时间和历史同期数据预测
        ParkingLot parkingLot = getById(parkingLotId);
        if (parkingLot == null) {
            return 0;
        }

        Integer currentSpaces = parkingLot.getAvailableSpaces();
        // 简单预测：假设未来minutes分钟内减少当前占用的10%
        Integer occupied = parkingLot.getOccupiedSpaces();
        Double predictedChange = occupied * 0.1 * (minutes / 60.0);

        return Math.max(0, currentSpaces - predictedChange.intValue());
    }

    @Override
    public BigDecimal calculateParkingFee(Long parkingLotId, Integer durationMinutes) {
        ParkingLot parkingLot = getById(parkingLotId);
        if (parkingLot == null) {
            throw new BusinessException("停车场不存在");
        }

        return parkingLot.calculateEstimatedFee(durationMinutes);
    }

    @Override
    public List<ParkingPriceCompareVO> compareParkingPrices(List<Long> parkingLotIds) {
        List<ParkingPriceCompareVO> result = new ArrayList<>();

        for (Long id : parkingLotIds) {
            ParkingLot parkingLot = getById(id);
            if (parkingLot == null) {
                continue;
            }

            ParkingPriceCompareVO vo = new ParkingPriceCompareVO();
            vo.setParkingLotId(id);
            vo.setParkingLotName(parkingLot.getName());
            vo.setBasePrice(parkingLot.getBasePrice());
            vo.setUnitPrice(parkingLot.getUnitPrice());
            vo.setUnitDuration(parkingLot.getUnitDuration());
            vo.setDailyCap(parkingLot.getDailyCap());
            vo.setFreeDuration(parkingLot.getFreeDuration());

            // 计算不同停车时长的费用
            vo.setFee1Hour(parkingLot.calculateEstimatedFee(60));
            vo.setFee2Hours(parkingLot.calculateEstimatedFee(120));
            vo.setFee4Hours(parkingLot.calculateEstimatedFee(240));
            vo.setFee8Hours(parkingLot.calculateEstimatedFee(480));

            result.add(vo);
        }

        // 按首小时价格排序
        result.sort(Comparator.comparing(ParkingPriceCompareVO::getBasePrice));

        return result;
    }

    @Override
    public ParkingStatisticsVO getParkingStatistics(Long parkingLotId) {
        ParkingLot parkingLot = getById(parkingLotId);
        if (parkingLot == null) {
            throw new BusinessException("停车场不存在");
        }

        ParkingStatisticsVO vo = new ParkingStatisticsVO();
        vo.setParkingLotId(parkingLotId);
        vo.setParkingLotName(parkingLot.getName());
        vo.setTotalSpaces(parkingLot.getTotalSpaces());
        vo.setAvailableSpaces(parkingLot.getAvailableSpaces());
        vo.setOccupiedSpaces(parkingLot.getOccupiedSpaces());
        vo.setVacancyRate(parkingLot.getVacancyRate());
        vo.setRating(parkingLot.getRating());
        vo.setRatingCount(parkingLot.getRatingCount());
        vo.setTodayParkingCount(parkingLot.getTodayParkingCount());

        // TODO: 从数据库统计更多数据
        vo.setAvgParkingDuration(parkingLot.getAvgParkingDuration());

        return vo;
    }

    @Override
    public Integer syncFromThirdParty(String dataSource, Boolean syncAll, String areaCode) {
        // TODO: 调用第三方API同步数据
        log.info("同步第三方停车场数据: dataSource={}, syncAll={}, areaCode={}", dataSource, syncAll, areaCode);
        return 0;
    }

    @Override
    public boolean toggleParkingLotStatus(Long parkingLotId, Boolean enabled) {
        ParkingLot parkingLot = getById(parkingLotId);
        if (parkingLot == null) {
            return false;
        }

        parkingLot.setStatus(enabled ? 1 : 0);
        return updateById(parkingLot);
    }

    @Override
    public List<ParkingLotListVO> getHotParkingLots(String cityCode, Integer limit) {
        LambdaQueryWrapper<ParkingLot> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(ParkingLot::getAreaCode, cityCode);
        wrapper.eq(ParkingLot::getStatus, 1);
        wrapper.eq(ParkingLot::getDeleted, 0);
        wrapper.orderByDesc(ParkingLot::getTodayParkingCount, ParkingLot::getRating);
        wrapper.last("LIMIT " + limit);

        List<ParkingLot> list = list(wrapper);
        return list.stream().map(this::convertToListVO).collect(Collectors.toList());
    }

    @Override
    public Page<ParkingLotListVO> searchParkingLots(String keyword, String cityCode, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<ParkingLot> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(ParkingLot::getName, keyword)
                    .or()
                    .like(ParkingLot::getAddress, keyword));
        }

        if (StringUtils.hasText(cityCode)) {
            wrapper.likeRight(ParkingLot::getAreaCode, cityCode);
        }

        wrapper.eq(ParkingLot::getStatus, 1);
        wrapper.eq(ParkingLot::getDeleted, 0);
        wrapper.orderByDesc(ParkingLot::getWeightScore);

        Page<ParkingLot> page = page(new Page<>(pageNum, pageSize), wrapper);

        List<ParkingLotListVO> voList = page.getRecords().stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());

        Page<ParkingLotListVO> resultPage = new Page<>();
        resultPage.setRecords(voList);
        resultPage.setTotal(page.getTotal());
        resultPage.setSize(page.getSize());
        resultPage.setCurrent(page.getCurrent());

        return resultPage;
    }

    // ==================== 私有方法 ====================

    private String encodeGeoHash(double latitude, double longitude) {
        // 简化实现，实际应使用GeoHash算法库
        return String.format("%.6f,%.6f", latitude, longitude);
    }

    private void addToRedisGeo(ParkingLot parkingLot) {
        Point point = new Point(parkingLot.getLongitude(), parkingLot.getLatitude());
        redisTemplate.opsForGeo().add(PARKING_GEO_KEY, point, parkingLot.getId().toString());
    }

    private void updateRedisGeo(ParkingLot parkingLot) {
        // 先删除旧位置
        redisTemplate.opsForGeo().remove(PARKING_GEO_KEY, parkingLot.getId().toString());
        // 添加新位置
        addToRedisGeo(parkingLot);
    }

    private Integer getRealTimeSpaces(Long parkingLotId) {
        String key = PARKING_SPACE_KEY + parkingLotId;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? (Integer) value : null;
    }

    private ParkingLotListVO convertToListVO(ParkingLot parkingLot) {
        ParkingLotListVO vo = new ParkingLotListVO();
        BeanUtils.copyProperties(parkingLot, vo);
        return vo;
    }

    private ParkingLotNearbyVO convertToNearbyVO(ParkingLot parkingLot, Double distance) {
        ParkingLotNearbyVO vo = new ParkingLotNearbyVO();
        BeanUtils.copyProperties(parkingLot, vo);
        vo.setDistance(distance);
        vo.setDistanceText(formatDistance(distance));
        return vo;
    }

    private String formatDistance(Double distance) {
        if (distance < 1000) {
            return Math.round(distance) + "米";
        } else {
            return new BigDecimal(distance / 1000).setScale(1, RoundingMode.HALF_UP) + "公里";
        }
    }

    private String generateRecommendReason(ParkingLot parkingLot, Double distance) {
        List<String> reasons = new ArrayList<>();

        if (distance < 200) {
            reasons.add("距离最近");
        }
        if (parkingLot.getAvailableSpaces() > parkingLot.getTotalSpaces() * 0.3) {
            reasons.add("空位充足");
        }
        if (parkingLot.getBasePrice() != null && parkingLot.getBasePrice().compareTo(new BigDecimal("10")) < 0) {
            reasons.add("价格实惠");
        }
        if (parkingLot.getRating() != null && parkingLot.getRating().compareTo(new BigDecimal("4.5")) >= 0) {
            reasons.add("评分很高");
        }

        return String.join("、", reasons);
    }
}
