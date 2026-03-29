package com.im.backend.modules.geofencing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.common.result.PageResult;
import com.im.backend.modules.geofencing.entity.GeofenceZone;
import com.im.backend.modules.geofencing.mapper.GeofenceZoneMapper;
import com.im.backend.modules.geofencing.service.GeofenceService;
import com.im.backend.modules.geofencing.dto.*;
import com.im.backend.common.util.GeoHashUtil;
import com.im.backend.common.util.GeoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 地理围栏服务实现类
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Slf4j
@Service
public class GeofenceServiceImpl extends ServiceImpl<GeofenceZoneMapper, GeofenceZone> implements GeofenceService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private GeofenceZoneMapper geofenceZoneMapper;
    
    private static final String GEOFENCE_CACHE_KEY = "geofence:zone:";
    private static final String GEOFENCE_GEOHASH_INDEX = "geofence:geohash:";
    private static final double EARTH_RADIUS = 6371000; // 地球半径（米）

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createGeofence(GeofenceCreateDTO dto) {
        log.info("Creating geofence: {}", dto.getName());
        
        GeofenceZone geofence = new GeofenceZone();
        BeanUtils.copyProperties(dto, geofence);
        
        // 计算地理哈希
        if (dto.getCenterLongitude() != null && dto.getCenterLatitude() != null) {
            String geoHash = GeoHashUtil.encode(dto.getCenterLatitude().doubleValue(), 
                                                dto.getCenterLongitude().doubleValue(), 12);
            geofence.setGeoHash(geoHash);
            geofence.setMinGeoHashPrecision(4);
            geofence.setMaxGeoHashPrecision(12);
        }
        
        // 计算边界框
        calculateBoundingBox(geofence, dto);
        
        // 计算面积和周长
        calculateAreaAndPerimeter(geofence, dto);
        
        geofence.setEnabled(true);
        geofence.setStatus("ACTIVE");
        
        this.save(geofence);
        
        // 缓存围栏数据
        cacheGeofence(geofence);
        
        // 添加到GeoHash索引
        addToGeoHashIndex(geofence);
        
        log.info("Geofence created successfully: id={}", geofence.getId());
        return geofence.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGeofence(Long id, GeofenceUpdateDTO dto) {
        log.info("Updating geofence: id={}", id);
        
        GeofenceZone geofence = this.getById(id);
        if (geofence == null) {
            throw new RuntimeException("围栏不存在");
        }
        
        // 清除旧索引
        removeFromGeoHashIndex(geofence);
        
        BeanUtils.copyProperties(dto, geofence);
        
        // 重新计算边界和索引
        calculateBoundingBox(geofence, dto);
        calculateAreaAndPerimeter(geofence, dto);
        
        if (dto.getCenterLongitude() != null && dto.getCenterLatitude() != null) {
            String geoHash = GeoHashUtil.encode(dto.getCenterLatitude().doubleValue(),
                                                dto.getCenterLongitude().doubleValue(), 12);
            geofence.setGeoHash(geoHash);
        }
        
        this.updateById(geofence);
        
        // 更新缓存
        cacheGeofence(geofence);
        addToGeoHashIndex(geofence);
        
        log.info("Geofence updated successfully: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGeofence(Long id) {
        log.info("Deleting geofence: id={}", id);
        
        GeofenceZone geofence = this.getById(id);
        if (geofence != null) {
            removeFromGeoHashIndex(geofence);
            redisTemplate.delete(GEOFENCE_CACHE_KEY + id);
        }
        
        this.removeById(id);
        log.info("Geofence deleted successfully: id={}", id);
    }

    @Override
    public GeofenceDetailVO getGeofenceDetail(Long id) {
        String cacheKey = GEOFENCE_CACHE_KEY + id;
        GeofenceDetailVO vo = (GeofenceDetailVO) redisTemplate.opsForValue().get(cacheKey);
        
        if (vo == null) {
            GeofenceZone geofence = this.getById(id);
            if (geofence == null) {
                return null;
            }
            
            vo = new GeofenceDetailVO();
            BeanUtils.copyProperties(geofence, vo);
            
            // 解析多边形坐标
            if (geofence.getPolygonPoints() != null) {
                vo.setPolygonCoordinates(parsePolygonPoints(geofence.getPolygonPoints()));
            }
            
            redisTemplate.opsForValue().set(cacheKey, vo);
        }
        
        return vo;
    }

    @Override
    public PageResult<GeofenceListVO> queryGeofencePage(GeofenceQueryDTO query) {
        Page<GeofenceZone> page = new Page<>(query.getPage(), query.getSize());
        
        LambdaQueryWrapper<GeofenceZone> wrapper = new LambdaQueryWrapper<>();
        
        if (query.getMerchantId() != null) {
            wrapper.eq(GeofenceZone::getMerchantId, query.getMerchantId());
        }
        if (query.getPoiId() != null) {
            wrapper.eq(GeofenceZone::getPoiId, query.getPoiId());
        }
        if (query.getType() != null) {
            wrapper.eq(GeofenceZone::getType, query.getType());
        }
        if (query.getLevel() != null) {
            wrapper.eq(GeofenceZone::getLevel, query.getLevel());
        }
        if (query.getStatus() != null) {
            wrapper.eq(GeofenceZone::getStatus, query.getStatus());
        }
        if (query.getEnabled() != null) {
            wrapper.eq(GeofenceZone::getEnabled, query.getEnabled());
        }
        
        wrapper.orderByDesc(GeofenceZone::getCreateTime);
        
        Page<GeofenceZone> resultPage = this.page(page, wrapper);
        
        List<GeofenceListVO> list = resultPage.getRecords().stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
        
        return new PageResult<>(resultPage.getTotal(), list);
    }

    @Override
    public List<GeofenceListVO> getMerchantGeofences(Long merchantId) {
        LambdaQueryWrapper<GeofenceZone> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GeofenceZone::getMerchantId, merchantId)
               .eq(GeofenceZone::getEnabled, true)
               .orderByDesc(GeofenceZone::getCreateTime);
        
        return this.list(wrapper).stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<GeofenceListVO> getPoiGeofences(Long poiId) {
        LambdaQueryWrapper<GeofenceZone> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GeofenceZone::getPoiId, poiId)
               .eq(GeofenceZone::getEnabled, true);
        
        return this.list(wrapper).stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isPointInGeofence(Long geofenceId, BigDecimal longitude, BigDecimal latitude) {
        GeofenceZone geofence = this.getById(geofenceId);
        if (geofence == null || !geofence.getEnabled()) {
            return false;
        }
        
        return checkPointInGeofence(geofence, longitude, latitude);
    }

    @Override
    public List<Long> findGeofencesByPoint(BigDecimal longitude, BigDecimal latitude) {
        // 1. 使用GeoHash快速筛选候选围栏
        String geoHash = GeoHashUtil.encode(latitude.doubleValue(), longitude.doubleValue(), 6);
        Set<Object> candidateIds = redisTemplate.opsForSet().members(GEOFENCE_GEOHASH_INDEX + geoHash);
        
        if (CollectionUtils.isEmpty(candidateIds)) {
            // 从数据库查询
            return findGeofencesFromDb(longitude, latitude);
        }
        
        // 2. 精确检测
        List<Long> result = new ArrayList<>();
        for (Object id : candidateIds) {
            Long geofenceId = Long.valueOf(id.toString());
            if (isPointInGeofence(geofenceId, longitude, latitude)) {
                result.add(geofenceId);
            }
        }
        
        return result;
    }

    @Override
    public List<GeofenceListVO> findNearbyGeofences(BigDecimal longitude, BigDecimal latitude, Integer radius) {
        // 计算边界框
        double lat = latitude.doubleValue();
        double lng = longitude.doubleValue();
        double r = radius.doubleValue();
        
        double deltaLat = Math.toDegrees(r / EARTH_RADIUS);
        double deltaLng = Math.toDegrees(r / (EARTH_RADIUS * Math.cos(Math.toRadians(lat))));
        
        BigDecimal minLat = BigDecimal.valueOf(lat - deltaLat);
        BigDecimal maxLat = BigDecimal.valueOf(lat + deltaLat);
        BigDecimal minLng = BigDecimal.valueOf(lng - deltaLng);
        BigDecimal maxLng = BigDecimal.valueOf(lng + deltaLng);
        
        // 查询边界框内的围栏
        LambdaQueryWrapper<GeofenceZone> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(GeofenceZone::getMinLatitude, minLat)
               .le(GeofenceZone::getMaxLatitude, maxLat)
               .ge(GeofenceZone::getMinLongitude, minLng)
               .le(GeofenceZone::getMaxLongitude, maxLng)
               .eq(GeofenceZone::getEnabled, true);
        
        List<GeofenceZone> candidates = this.list(wrapper);
        
        // 精确计算距离
        return candidates.stream()
                .filter(g -> calculateDistance(latitude, longitude, 
                        g.getCenterLatitude(), g.getCenterLongitude()) <= radius)
                .map(this::convertToListVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableGeofence(Long id) {
        GeofenceZone geofence = new GeofenceZone();
        geofence.setId(id);
        geofence.setEnabled(true);
        geofence.setStatus("ACTIVE");
        this.updateById(geofence);
        
        // 更新缓存
        GeofenceZone updated = this.getById(id);
        cacheGeofence(updated);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableGeofence(Long id) {
        GeofenceZone geofence = new GeofenceZone();
        geofence.setId(id);
        geofence.setEnabled(false);
        geofence.setStatus("PAUSED");
        this.updateById(geofence);
        
        redisTemplate.delete(GEOFENCE_CACHE_KEY + id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long copyGeofence(Long id, Long targetPoiId) {
        GeofenceZone source = this.getById(id);
        if (source == null) {
            throw new RuntimeException("源围栏不存在");
        }
        
        GeofenceZone copy = new GeofenceZone();
        BeanUtils.copyProperties(source, copy);
        copy.setId(null);
        copy.setPoiId(targetPoiId);
        copy.setName(source.getName() + " (复制)");
        copy.setCreateTime(null);
        copy.setUpdateTime(null);
        
        this.save(copy);
        
        return copy.getId();
    }

    @Override
    public List<GeofenceTreeVO> getGeofenceTree(Long merchantId) {
        List<GeofenceZone> allGeofences = this.lambdaQuery()
                .eq(GeofenceZone::getMerchantId, merchantId)
                .eq(GeofenceZone::getEnabled, true)
                .list();
        
        // 构建树形结构
        Map<Long, GeofenceTreeVO> voMap = new HashMap<>();
        List<GeofenceTreeVO> roots = new ArrayList<>();
        
        for (GeofenceZone g : allGeofences) {
            GeofenceTreeVO vo = new GeofenceTreeVO();
            vo.setId(g.getId());
            vo.setName(g.getName());
            vo.setLevel(g.getLevel());
            vo.setType(g.getType());
            vo.setChildren(new ArrayList<>());
            voMap.put(g.getId(), vo);
        }
        
        for (GeofenceZone g : allGeofences) {
            GeofenceTreeVO vo = voMap.get(g.getId());
            if (g.getParentId() != null && voMap.containsKey(g.getParentId())) {
                voMap.get(g.getParentId()).getChildren().add(vo);
            } else {
                roots.add(vo);
            }
        }
        
        return roots;
    }

    @Override
    public BigDecimal calculateArea(Long geofenceId) {
        GeofenceZone geofence = this.getById(geofenceId);
        if (geofence == null) {
            return BigDecimal.ZERO;
        }
        return geofence.getArea();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCreateFromPois(List<Long> poiIds, Integer radius) {
        for (Long poiId : poiIds) {
            // 查询POI信息并创建围栏
            GeofenceCreateDTO dto = new GeofenceCreateDTO();
            dto.setPoiId(poiId);
            dto.setType("CIRCLE");
            dto.setRadius(radius);
            dto.setName("POI围栏_" + poiId);
            dto.setLevel(3);
            
            // TODO: 从POI服务获取坐标
            // 这里简化处理
            createGeofence(dto);
        }
    }

    @Override
    public GeofenceStatisticsVO getGeofenceStatistics(Long merchantId) {
        LambdaQueryWrapper<GeofenceZone> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GeofenceZone::getMerchantId, merchantId);
        
        long total = this.count(wrapper);
        long active = this.count(wrapper.clone().eq(GeofenceZone::getEnabled, true));
        long paused = this.count(wrapper.clone().eq(GeofenceZone::getEnabled, false));
        
        GeofenceStatisticsVO vo = new GeofenceStatisticsVO();
        vo.setTotalCount((int) total);
        vo.setActiveCount((int) active);
        vo.setPausedCount((int) paused);
        
        return vo;
    }

    // ======================== 私有方法 ========================

    private void calculateBoundingBox(GeofenceZone geofence, Object dto) {
        // 根据围栏类型计算边界框
        if (geofence.getType().equals("CIRCLE") && geofence.getCenterLongitude() != null) {
            double lat = geofence.getCenterLatitude().doubleValue();
            double lng = geofence.getCenterLongitude().doubleValue();
            double r = geofence.getRadius();
            
            double deltaLat = Math.toDegrees(r / EARTH_RADIUS);
            double deltaLng = Math.toDegrees(r / (EARTH_RADIUS * Math.cos(Math.toRadians(lat))));
            
            geofence.setMinLatitude(BigDecimal.valueOf(lat - deltaLat));
            geofence.setMaxLatitude(BigDecimal.valueOf(lat + deltaLat));
            geofence.setMinLongitude(BigDecimal.valueOf(lng - deltaLng));
            geofence.setMaxLongitude(BigDecimal.valueOf(lng + deltaLng));
        }
        // 多边形围栏的边界框计算省略...
    }

    private void calculateAreaAndPerimeter(GeofenceZone geofence, Object dto) {
        if ("CIRCLE".equals(geofence.getType()) && geofence.getRadius() != null) {
            double r = geofence.getRadius();
            double area = Math.PI * r * r;
            double perimeter = 2 * Math.PI * r;
            
            geofence.setArea(BigDecimal.valueOf(area).setScale(2, RoundingMode.HALF_UP));
            geofence.setPerimeter(BigDecimal.valueOf(perimeter).setScale(2, RoundingMode.HALF_UP));
        }
    }

    private boolean checkPointInGeofence(GeofenceZone geofence, BigDecimal longitude, BigDecimal latitude) {
        // 快速边界框检测
        if (longitude.compareTo(geofence.getMinLongitude()) < 0 ||
            longitude.compareTo(geofence.getMaxLongitude()) > 0 ||
            latitude.compareTo(geofence.getMinLatitude()) < 0 ||
            latitude.compareTo(geofence.getMaxLatitude()) > 0) {
            return false;
        }
        
        // 根据围栏类型进行精确检测
        if ("CIRCLE".equals(geofence.getType())) {
            double distance = calculateDistance(latitude, longitude, 
                    geofence.getCenterLatitude(), geofence.getCenterLongitude());
            return distance <= geofence.getRadius();
        } else if ("POLYGON".equals(geofence.getType())) {
            return isPointInPolygon(longitude, latitude, geofence.getPolygonPoints());
        }
        
        return false;
    }

    private boolean isPointInPolygon(BigDecimal longitude, BigDecimal latitude, String polygonPoints) {
        // 射线法判断点是否在多边形内
        List<Map<String, Double>> points = parsePolygonPoints(polygonPoints);
        if (points == null || points.size() < 3) {
            return false;
        }
        
        boolean inside = false;
        double x = longitude.doubleValue();
        double y = latitude.doubleValue();
        
        for (int i = 0, j = points.size() - 1; i < points.size(); j = i++) {
            double xi = points.get(i).get("lng");
            double yi = points.get(i).get("lat");
            double xj = points.get(j).get("lng");
            double yj = points.get(j).get("lat");
            
            if (((yi > y) != (yj > y)) && (x < (xj - xi) * (y - yi) / (yj - yi) + xi)) {
                inside = !inside;
            }
        }
        
        return inside;
    }

    private double calculateDistance(BigDecimal lat1, BigDecimal lng1, 
                                     BigDecimal lat2, BigDecimal lng2) {
        // Haversine公式计算球面距离
        double dLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double dLng = Math.toRadians(lng2.doubleValue() - lng1.doubleValue());
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1.doubleValue())) *
                   Math.cos(Math.toRadians(lat2.doubleValue())) *
                   Math.sin(dLng / 2) * Math.sin(dLng / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }

    private List<Map<String, Double>> parsePolygonPoints(String polygonPoints) {
        // JSON解析多边形坐标点
        try {
            // 简化处理，实际应使用Jackson/Gson解析
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Failed to parse polygon points: {}", polygonPoints, e);
            return null;
        }
    }

    private void cacheGeofence(GeofenceZone geofence) {
        String key = GEOFENCE_CACHE_KEY + geofence.getId();
        redisTemplate.opsForValue().set(key, geofence);
    }

    private void addToGeoHashIndex(GeofenceZone geofence) {
        if (geofence.getGeoHash() == null) return;
        
        // 添加多精度GeoHash索引
        for (int precision = 4; precision <= 8; precision++) {
            String hash = geofence.getGeoHash().substring(0, Math.min(precision, geofence.getGeoHash().length()));
            redisTemplate.opsForSet().add(GEOFENCE_GEOHASH_INDEX + hash, geofence.getId());
        }
    }

    private void removeFromGeoHashIndex(GeofenceZone geofence) {
        if (geofence.getGeoHash() == null) return;
        
        for (int precision = 4; precision <= 8; precision++) {
            String hash = geofence.getGeoHash().substring(0, Math.min(precision, geofence.getGeoHash().length()));
            redisTemplate.opsForSet().remove(GEOFENCE_GEOHASH_INDEX + hash, geofence.getId());
        }
    }

    private GeofenceListVO convertToListVO(GeofenceZone geofence) {
        GeofenceListVO vo = new GeofenceListVO();
        BeanUtils.copyProperties(geofence, vo);
        return vo;
    }

    private List<Long> findGeofencesFromDb(BigDecimal longitude, BigDecimal latitude) {
        // 数据库查询兜底
        LambdaQueryWrapper<GeofenceZone> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GeofenceZone::getEnabled, true);
        
        List<GeofenceZone> all = this.list(wrapper);
        return all.stream()
                .filter(g -> checkPointInGeofence(g, longitude, latitude))
                .map(GeofenceZone::getId)
                .collect(Collectors.toList());
    }
}
