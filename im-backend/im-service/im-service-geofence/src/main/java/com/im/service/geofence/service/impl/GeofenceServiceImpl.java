package com.im.service.geofence.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.common.util.GeoUtils;
import com.im.service.geofence.dto.*;
import com.im.service.geofence.entity.Geofence;
import com.im.service.geofence.entity.GeofenceEvent;
import com.im.service.geofence.repository.GeofenceEventRepository;
import com.im.service.geofence.repository.GeofenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 地理围栏服务实现
 */
@Service
public class GeofenceServiceImpl implements GeofenceService {

    private static final Logger logger = LoggerFactory.getLogger(GeofenceServiceImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final GeofenceRepository geofenceRepository;
    private final GeofenceEventRepository eventRepository;
    private final ObjectMapper objectMapper;

    public GeofenceServiceImpl(GeofenceRepository geofenceRepository,
                                GeofenceEventRepository eventRepository,
                                ObjectMapper objectMapper) {
        this.geofenceRepository = geofenceRepository;
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public GeofenceResponse createGeofence(CreateGeofenceRequest request) {
        Geofence geofence = new Geofence();
        geofence.setGeofenceId(generateGeofenceId());
        geofence.setName(request.getName());
        geofence.setDescription(request.getDescription());
        geofence.setFenceType(request.getFenceType());
        geofence.setMerchantId(request.getMerchantId());
        geofence.setPoiId(request.getPoiId());
        geofence.setCenterLatitude(request.getCenterLatitude());
        geofence.setCenterLongitude(request.getCenterLongitude());
        geofence.setRadius(request.getRadius());
        geofence.setLevel(request.getLevel() != null ? request.getLevel() : 1);
        geofence.setParentId(request.getParentId());
        geofence.setTriggerCondition(request.getTriggerCondition());
        geofence.setDwellTime(request.getDwellTime());
        geofence.setBusinessHours(request.getBusinessHours());
        geofence.setEffectiveWeekdays(request.getEffectiveWeekdays());
        geofence.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        geofence.setStatus("ACTIVE");

        // 解析生效时间
        if (request.getEffectiveStartTime() != null) {
            geofence.setEffectiveStartTime(LocalDateTime.parse(request.getEffectiveStartTime()));
        }
        if (request.getEffectiveEndTime() != null) {
            geofence.setEffectiveEndTime(LocalDateTime.parse(request.getEffectiveEndTime()));
        }

        // 处理坐标点
        if (request.getPolygonPoints() != null && !request.getPolygonPoints().isEmpty()) {
            try {
                geofence.setCoordinates(objectMapper.writeValueAsString(request.getPolygonPoints()));
            } catch (JsonProcessingException e) {
                logger.error("Failed to serialize polygon points", e);
            }
        }

        // 计算GeoHash
        if (geofence.getCenterLatitude() != null && geofence.getCenterLongitude() != null) {
            geofence.setGeoHash(GeoUtils.encodeGeoHash(
                geofence.getCenterLongitude(),
                geofence.getCenterLatitude(),
                9
            ));
        }

        geofence.setCreateTime(LocalDateTime.now());
        geofence.setUpdateTime(LocalDateTime.now());

        geofence = geofenceRepository.save(geofence);
        logger.info("Created geofence: {}", geofence.getGeofenceId());

        return toResponse(geofence);
    }

    @Override
    @Transactional
    public GeofenceResponse updateGeofence(String geofenceId, CreateGeofenceRequest request) {
        Geofence geofence = geofenceRepository.findByGeofenceId(geofenceId)
            .orElseThrow(() -> new RuntimeException("Geofence not found: " + geofenceId));

        geofence.setName(request.getName());
        geofence.setDescription(request.getDescription());
        geofence.setFenceType(request.getFenceType());
        geofence.setMerchantId(request.getMerchantId());
        geofence.setPoiId(request.getPoiId());
        geofence.setCenterLatitude(request.getCenterLatitude());
        geofence.setCenterLongitude(request.getCenterLongitude());
        geofence.setRadius(request.getRadius());
        geofence.setLevel(request.getLevel());
        geofence.setParentId(request.getParentId());
        geofence.setTriggerCondition(request.getTriggerCondition());
        geofence.setDwellTime(request.getDwellTime());
        geofence.setBusinessHours(request.getBusinessHours());
        geofence.setEffectiveWeekdays(request.getEffectiveWeekdays());
        
        if (request.getPolygonPoints() != null && !request.getPolygonPoints().isEmpty()) {
            try {
                geofence.setCoordinates(objectMapper.writeValueAsString(request.getPolygonPoints()));
            } catch (JsonProcessingException e) {
                logger.error("Failed to serialize polygon points", e);
            }
        }

        if (geofence.getCenterLatitude() != null && geofence.getCenterLongitude() != null) {
            geofence.setGeoHash(GeoUtils.encodeGeoHash(
                geofence.getCenterLongitude(),
                geofence.getCenterLatitude(),
                9
            ));
        }

        geofence.setUpdateTime(LocalDateTime.now());
        geofence = geofenceRepository.save(geofence);

        return toResponse(geofence);
    }

    @Override
    @Transactional
    public boolean deleteGeofence(String geofenceId) {
        return geofenceRepository.findByGeofenceId(geofenceId)
            .map(geofence -> {
                geofenceRepository.delete(geofence);
                logger.info("Deleted geofence: {}", geofenceId);
                return true;
            })
            .orElse(false);
    }

    @Override
    public GeofenceResponse getGeofenceById(String geofenceId) {
        return geofenceRepository.findByGeofenceId(geofenceId)
            .map(this::toResponse)
            .orElse(null);
    }

    @Override
    public List<GeofenceResponse> getGeofencesByMerchant(String merchantId) {
        return geofenceRepository.findByMerchantId(merchantId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<GeofenceResponse> getGeofencesByPoi(String poiId) {
        return geofenceRepository.findByPoiId(poiId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<GeofenceResponse> getAllActiveGeofences() {
        return geofenceRepository.findAllActive().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GeofenceResponse enableGeofence(String geofenceId) {
        Geofence geofence = geofenceRepository.findByGeofenceId(geofenceId)
            .orElseThrow(() -> new RuntimeException("Geofence not found: " + geofenceId));
        
        geofence.setEnabled(true);
        geofence.setStatus("ACTIVE");
        geofence.setUpdateTime(LocalDateTime.now());
        geofence = geofenceRepository.save(geofence);
        
        return toResponse(geofence);
    }

    @Override
    @Transactional
    public GeofenceResponse disableGeofence(String geofenceId) {
        Geofence geofence = geofenceRepository.findByGeofenceId(geofenceId)
            .orElseThrow(() -> new RuntimeException("Geofence not found: " + geofenceId));
        
        geofence.setEnabled(false);
        geofence.setStatus("INACTIVE");
        geofence.setUpdateTime(LocalDateTime.now());
        geofence = geofenceRepository.save(geofence);
        
        return toResponse(geofence);
    }

    @Override
    @Transactional
    public String copyGeofence(String geofenceId, String targetPoiId) {
        Geofence source = geofenceRepository.findByGeofenceId(geofenceId)
            .orElseThrow(() -> new RuntimeException("Geofence not found: " + geofenceId));

        Geofence copy = new Geofence();
        copy.setGeofenceId(generateGeofenceId());
        copy.setName(source.getName() + " (Copy)");
        copy.setDescription(source.getDescription());
        copy.setFenceType(source.getFenceType());
        copy.setMerchantId(source.getMerchantId());
        copy.setPoiId(targetPoiId);
        copy.setCenterLatitude(source.getCenterLatitude());
        copy.setCenterLongitude(source.getCenterLongitude());
        copy.setRadius(source.getRadius());
        copy.setCoordinates(source.getCoordinates());
        copy.setGeoHash(source.getGeoHash());
        copy.setLevel(source.getLevel());
        copy.setTriggerCondition(source.getTriggerCondition());
        copy.setDwellTime(source.getDwellTime());
        copy.setStatus("ACTIVE");
        copy.setEnabled(true);
        copy.setCreateTime(LocalDateTime.now());
        copy.setUpdateTime(LocalDateTime.now());

        copy = geofenceRepository.save(copy);
        logger.info("Copied geofence {} to POI {}", geofenceId, targetPoiId);

        return copy.getGeofenceId();
    }

    @Override
    public List<GeofenceResponse> getGeofenceTree(String merchantId) {
        List<Geofence> allGeofences = geofenceRepository.findByMerchantId(merchantId);
        
        // 构建树结构
        Map<String, List<Geofence>> parentMap = allGeofences.stream()
            .collect(Collectors.groupingBy(g -> g.getParentId() != null ? g.getParentId() : "root"));
        
        List<GeofenceResponse> roots = allGeofences.stream()
            .filter(g -> g.getParentId() == null)
            .map(this::toResponse)
            .collect(Collectors.toList());
        
        // 递归填充子节点
        for (GeofenceResponse root : roots) {
            fillSubGeofences(root, parentMap);
        }
        
        return roots;
    }

    private void fillSubGeofences(GeofenceResponse parent, Map<String, List<Geofence>> parentMap) {
        List<Geofence> children = parentMap.get(parent.getGeofenceId());
        if (children != null && !children.isEmpty()) {
            List<GeofenceResponse> childResponses = children.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
            parent.setSubGeofences(childResponses);
            
            for (GeofenceResponse child : childResponses) {
                fillSubGeofences(child, parentMap);
            }
        }
    }

    @Override
    public List<GeofenceResponse> findNearbyGeofences(Double longitude, Double latitude, Integer radius) {
        // 获取附近的GeoHash网格
        String centerHash = GeoUtils.encodeGeoHash(longitude, latitude, 6);
        List<String> searchHashes = getNeighborHashes(centerHash);
        searchHashes.add(centerHash);
        
        List<GeofenceResponse> result = new ArrayList<>();
        for (String hash : searchHashes) {
            List<Geofence> geofences = geofenceRepository.findByGeoHashPrefix(hash);
            for (Geofence g : geofences) {
                if (isPointInGeofence(g.getGeofenceId(), longitude, latitude)) {
                    result.add(toResponse(g));
                }
            }
        }
        
        return result;
    }

    @Override
    @Transactional
    public LocationReportResponse reportLocation(LocationReportRequest request) {
        LocationReportResponse response = new LocationReportResponse();
        response.setUserId(request.getUserId());
        response.setLongitude(request.getLongitude());
        response.setLatitude(request.getLatitude());
        response.setTimestamp(LocalDateTime.now().format(FORMATTER));

        // 查找所有激活的围栏
        List<Geofence> activeGeofences = geofenceRepository.findAllActiveWithLocation();
        List<LocationReportResponse.GeofenceHit> hits = new ArrayList<>();

        for (Geofence geofence : activeGeofences) {
            boolean isInside = checkPointInGeofence(geofence, request.getLongitude(), request.getLatitude());
            
            if (isInside) {
                // 检查是否是第一次进入
                GeofenceEvent lastEvent = eventRepository.findLatestByGeofenceIdAndUserId(
                    geofence.getGeofenceId(), request.getUserId()
                );
                
                String eventType = "ENTER";
                if (lastEvent != null && "ENTER".equals(lastEvent.getEventType())) {
                    // 用户已经在围栏内，检查是否触发了DWELL
                    if ("DWELL".equals(geofence.getTriggerCondition())) {
                        // TODO: 检查停留时间
                    }
                    continue; // 跳过重复进入事件
                }

                // 记录事件
                GeofenceEvent event = new GeofenceEvent();
                event.setEventId(generateEventId());
                event.setGeofenceId(geofence.getGeofenceId());
                event.setUserId(request.getUserId());
                event.setEventType(eventType);
                event.setLatitude(request.getLatitude());
                event.setLongitude(request.getLongitude());
                event.setAccuracy(request.getAccuracy());
                event.setSpeed(request.getSpeed());
                event.setBearing(request.getBearing());
                event.setDeviceId(request.getDeviceId());
                event.setAppVersion(request.getAppVersion());
                event.setEventTime(LocalDateTime.now());
                event.setCreateTime(LocalDateTime.now());
                eventRepository.save(event);

                // 添加到响应
                LocationReportResponse.GeofenceHit hit = new LocationReportResponse.GeofenceHit();
                hit.setGeofenceId(geofence.getGeofenceId());
                hit.setGeofenceName(geofence.getName());
                hit.setEventType(eventType);
                
                // 计算距离
                if (geofence.getCenterLatitude() != null && geofence.getCenterLongitude() != null) {
                    hit.setDistance(GeoUtils.calculateDistance(
                        request.getLongitude(), request.getLatitude(),
                        geofence.getCenterLongitude(), geofence.getCenterLatitude()
                    ));
                }
                hits.add(hit);
                
                logger.info("User {} triggered geofence {} ({}), distance: {}m", 
                    request.getUserId(), geofence.getGeofenceId(), eventType, hit.getDistance());
            }
        }

        response.setHitGeofences(hits);
        response.setHitCount(hits.size());

        return response;
    }

    @Override
    public boolean isPointInGeofence(String geofenceId, Double longitude, Double latitude) {
        return geofenceRepository.findByGeofenceId(geofenceId)
            .map(g -> checkPointInGeofence(g, longitude, latitude))
            .orElse(false);
    }

    @Override
    public List<String> findGeofencesByPoint(Double longitude, Double latitude) {
        List<Geofence> activeGeofences = geofenceRepository.findAllActiveWithLocation();
        List<String> result = new ArrayList<>();
        
        for (Geofence geofence : activeGeofences) {
            if (checkPointInGeofence(geofence, longitude, latitude)) {
                result.add(geofence.getGeofenceId());
            }
        }
        
        return result;
    }

    @Override
    public List<GeofenceEvent> getGeofenceEvents(String geofenceId, Integer limit) {
        return eventRepository.findByGeofenceId(geofenceId).stream()
            .limit(limit)
            .collect(Collectors.toList());
    }

    @Override
    public List<GeofenceEvent> getUserGeofenceEvents(String userId, Integer limit) {
        return eventRepository.findByUserIdOrderByEventTimeDesc(userId, 
            org.springframework.data.domain.PageRequest.of(0, limit)).getContent();
    }

    // ==================== 私有方法 ====================

    private boolean checkPointInGeofence(Geofence geofence, Double longitude, Double latitude) {
        String fenceType = geofence.getFenceType();
        
        if ("CIRCLE".equals(fenceType)) {
            return GeoUtils.isInCircle(
                longitude, latitude,
                geofence.getCenterLongitude(), geofence.getCenterLatitude(),
                geofence.getRadius()
            );
        } else if ("POLYGON".equals(fenceType) && geofence.getCoordinates() != null) {
            try {
                List<Map<String, Double>> points = objectMapper.readValue(
                    geofence.getCoordinates(),
                    new TypeReference<List<Map<String, Double>>>() {}
                );
                double[][] polygon = points.stream()
                    .map(p -> new double[]{p.get("lng"), p.get("lat")})
                    .toArray(double[][]::new);
                return GeoUtils.isPointInPolygon(longitude, latitude, polygon);
            } catch (JsonProcessingException e) {
                logger.error("Failed to parse polygon coordinates", e);
                return false;
            }
        }
        
        return false;
    }

    private GeofenceResponse toResponse(Geofence geofence) {
        GeofenceResponse response = new GeofenceResponse();
        response.setId(geofence.getId() != null ? geofence.getId().toString() : null);
        response.setGeofenceId(geofence.getGeofenceId());
        response.setName(geofence.getName());
        response.setDescription(geofence.getDescription());
        response.setFenceType(geofence.getFenceType());
        response.setMerchantId(geofence.getMerchantId());
        response.setPoiId(geofence.getPoiId());
        response.setCenterLatitude(geofence.getCenterLatitude());
        response.setCenterLongitude(geofence.getCenterLongitude());
        response.setRadius(geofence.getRadius());
        response.setGeoHash(geofence.getGeoHash());
        response.setLevel(geofence.getLevel());
        response.setParentId(geofence.getParentId());
        response.setTriggerCondition(geofence.getTriggerCondition());
        response.setDwellTime(geofence.getDwellTime());
        response.setStatus(geofence.getStatus());
        response.setEnabled(geofence.getEnabled());
        
        if (geofence.getCoordinates() != null) {
            try {
                List<Map<String, Double>> points = objectMapper.readValue(
                    geofence.getCoordinates(),
                    new TypeReference<List<Map<String, Double>>>() {}
                );
                List<GeofenceResponse.Coordinate> coords = points.stream()
                    .map(p -> new GeofenceResponse.Coordinate(p.get("lat"), p.get("lng")))
                    .collect(Collectors.toList());
                response.setPolygonPoints(coords);
            } catch (JsonProcessingException e) {
                logger.error("Failed to parse polygon coordinates", e);
            }
        }
        
        if (geofence.getEffectiveStartTime() != null) {
            response.setEffectiveStartTime(geofence.getEffectiveStartTime().format(FORMATTER));
        }
        if (geofence.getEffectiveEndTime() != null) {
            response.setEffectiveEndTime(geofence.getEffectiveEndTime().format(FORMATTER));
        }
        
        response.setBusinessHours(geofence.getBusinessHours());
        response.setEffectiveWeekdays(geofence.getEffectiveWeekdays());
        response.setCreateTime(geofence.getCreateTime() != null ? geofence.getCreateTime().format(FORMATTER) : null);
        response.setUpdateTime(geofence.getUpdateTime() != null ? geofence.getUpdateTime().format(FORMATTER) : null);
        
        return response;
    }

    private String generateGeofenceId() {
        return "GEO_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    private String generateEventId() {
        return "EVT_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    private List<String> getNeighborHashes(String geohash) {
        // 简化实现：返回同前缀的所有8个邻居的GeoHash
        List<String> neighbors = new ArrayList<>();
        String base32 = "0123456789bcdefghjkmnpqrstuvwxyz";
        char lastChar = geohash.charAt(geohash.length() - 1);
        
        for (char c : base32.toCharArray()) {
            if (c != lastChar) {
                neighbors.add(geohash.substring(0, geohash.length() - 1) + c);
            }
        }
        
        return neighbors;
    }
}
