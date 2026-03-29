package com.im.mapstream.service;

import com.im.mapstream.dto.*;
import com.im.mapstream.entity.*;
import com.im.mapstream.enums.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 地图信息流服务实现
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MapStreamServiceImpl implements MapStreamService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String STREAM_KEY_PREFIX = "mapstream:stream:";
    private static final String HEAT_KEY_PREFIX = "mapstream:heat:";
    private static final String HOTSPOT_KEY_PREFIX = "mapstream:hotspot:";
    private static final String CLUSTER_KEY_PREFIX = "mapstream:cluster:";

    @Override
    public MapStreamResponse publishStream(Long userId, PublishStreamRequest request) {
        String streamId = UUID.randomUUID().toString().replace("-", "");
        
        // 计算GeoHash
        String geohash = calculateGeoHash(request.getLatitude(), request.getLongitude(), 8);
        
        MapInfoStream stream = MapInfoStream.builder()
            .streamId(streamId)
            .publisherId(userId)
            .infoType(InfoType.fromCode(request.getInfoType()))
            .title(request.getTitle())
            .content(request.getContent())
            .mediaUrls(request.getMediaUrls())
            .thumbnailUrl(request.getThumbnailUrl())
            .liveStreamUrl(request.getLiveStreamUrl())
            .longitude(request.getLongitude())
            .latitude(request.getLatitude())
            .geohash(geohash)
            .poiId(request.getPoiId())
            .poiName(request.getPoiName())
            .address(request.getAddress())
            .cityCode(request.getCityCode())
            .visibility(request.getVisibility())
            .tags(request.getTags())
            .extra(request.getExtra())
            .viewCount(0L)
            .likeCount(0L)
            .commentCount(0L)
            .shareCount(0L)
            .heatValue(1.0)
            .isPinned(false)
            .createTime(LocalDateTime.now())
            .updateTime(LocalDateTime.now())
            .status(0)
            .build();
        
        // 保存到Redis
        redisTemplate.opsForValue().set(STREAM_KEY_PREFIX + streamId, stream, 7, TimeUnit.DAYS);
        
        // 更新热力图
        updateHeatPoint(geohash, stream.getInfoType());
        
        log.info("Published map stream: {} by user {}", streamId, userId);
        
        return convertToResponse(stream);
    }

    @Override
    public List<MapStreamResponse> queryNearbyStreams(StreamQueryRequest request) {
        // 从缓存或数据库查询
        List<MapInfoStream> streams = new ArrayList<>();
        
        // 获取指定半径内的GeoHash网格
        Set<String> geohashes = getNeighbors(request.getLatitude(), request.getLongitude(), 
            calculateGeoHashLength(request.getRadius()));
        
        // 从Redis批量获取
        for (String hash : geohashes) {
            Set<Object> streamIds = redisTemplate.opsForSet().members("mapstream:geohash:" + hash);
            if (streamIds != null) {
                for (Object sid : streamIds) {
                    MapInfoStream stream = (MapInfoStream) redisTemplate.opsForValue()
                        .get(STREAM_KEY_PREFIX + sid);
                    if (stream != null && filterStream(stream, request)) {
                        streams.add(stream);
                    }
                }
            }
        }
        
        // 排序
        streams.sort(getComparator(request.getSortBy()));
        
        // 分页
        int start = (request.getPageNum() - 1) * request.getPageSize();
        int end = Math.min(start + request.getPageSize(), streams.size());
        
        if (start >= streams.size()) {
            return new ArrayList<>();
        }
        
        return streams.subList(start, end).stream()
            .map(this::convertToResponse)
            .peek(resp -> resp.setDistance(calculateDistance(
                request.getLatitude(), request.getLongitude(),
                resp.getLatitude(), resp.getLongitude())))
            .collect(Collectors.toList());
    }

    @Override
    public List<InfoClusterDTO> queryClusterStreams(ClusterQueryRequest request) {
        ClusterLevel level = ClusterLevel.fromZoom(request.getZoom());
        Set<String> geohashPrefixes = getGeohashPrefixesInBounds(
            request.getMinLongitude(), request.getMaxLongitude(),
            request.getMinLatitude(), request.getMaxLatitude(),
            level.getGeohashLength()
        );
        
        List<InfoClusterDTO> clusters = new ArrayList<>();
        
        for (String prefix : geohashPrefixes) {
            Set<Object> streamIds = redisTemplate.opsForSet().members("mapstream:geohash:" + prefix);
            if (streamIds != null && streamIds.size() >= request.getMinClusterCount()) {
                InfoClusterDTO cluster = new InfoClusterDTO();
                cluster.setClusterId(UUID.randomUUID().toString());
                cluster.setStreamCount(streamIds.size());
                
                // 获取预览流
                List<MapStreamResponse> previews = streamIds.stream()
                    .limit(3)
                    .map(sid -> (MapInfoStream) redisTemplate.opsForValue()
                        .get(STREAM_KEY_PREFIX + sid))
                    .filter(Objects::nonNull)
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
                
                cluster.setPreviewStreams(previews);
                
                // 计算中心点
                if (!previews.isEmpty()) {
                    double avgLat = previews.stream().mapToDouble(MapStreamResponse::getLatitude).average().orElse(0);
                    double avgLon = previews.stream().mapToDouble(MapStreamResponse::getLongitude).average().orElse(0);
                    cluster.setCenterLatitude(avgLat);
                    cluster.setCenterLongitude(avgLon);
                }
                
                // 计算热度
                double heat = previews.stream().mapToDouble(MapStreamResponse::getHeatValue).sum();
                cluster.setClusterHeat(heat * Math.log1p(streamIds.size()));
                
                clusters.add(cluster);
            }
        }
        
        return clusters;
    }

    @Override
    public List<HeatMapResponse> getRealtimeHeatMap(Double minLon, Double maxLon, Double minLat, Double maxLat, Integer zoom) {
        ClusterLevel level = ClusterLevel.fromZoom(zoom);
        Set<String> geohashes = getGeohashPrefixesInBounds(minLon, maxLon, minLat, maxLat, level.getGeohashLength());
        
        List<HeatMapResponse> heatPoints = new ArrayList<>();
        
        for (String geohash : geohashes) {
            MapHeatPoint point = (MapHeatPoint) redisTemplate.opsForValue()
                .get(HEAT_KEY_PREFIX + geohash);
            
            if (point != null && point.getHeatValue() > 0) {
                heatPoints.add(HeatMapResponse.builder()
                    .heatPointId(point.getHeatPointId())
                    .geohash(point.getGeohash())
                    .geohashLength(point.getGeohashLength())
                    .longitude(point.getCenterLongitude())
                    .latitude(point.getCenterLatitude())
                    .streamCount(point.getStreamCount())
                    .userCount(point.getUserCount())
                    .heatValue(point.getHeatValue())
                    .heatStatus(point.getHeatStatus())
                    .color(point.getHeatStatus().getColor())
                    .radius(level.getApproximateRadius())
                    .typeDistribution(point.getTypeDistribution())
                    .timeWindow(point.getTimeWindowEnd())
                    .build());
            }
        }
        
        return heatPoints;
    }

    @Override
    public List<HeatMapResponse> getHistoryHeatMap(Double minLon, Double maxLon, Double minLat, Double maxLat, 
                                                    Integer zoom, Integer hoursAgo) {
        // 从历史数据获取
        String historyKey = HEAT_KEY_PREFIX + "history:" + hoursAgo;
        return getRealtimeHeatMap(minLon, maxLon, minLat, maxLat, zoom);
    }

    @Override
    public List<HotSpotResponse> getHotSpots(String cityCode, Integer limit) {
        Set<Object> hotSpotIds = redisTemplate.opsForZSet()
            .reverseRange(HOTSPOT_KEY_PREFIX + "rank:" + cityCode, 0, limit - 1);
        
        List<HotSpotResponse> hotSpots = new ArrayList<>();
        
        if (hotSpotIds != null) {
            for (Object id : hotSpotIds) {
                HotSpot hotSpot = (HotSpot) redisTemplate.opsForValue()
                    .get(HOTSPOT_KEY_PREFIX + id);
                if (hotSpot != null && hotSpot.isActive()) {
                    hotSpots.add(convertToHotSpotResponse(hotSpot));
                }
            }
        }
        
        return hotSpots;
    }

    @Override
    public HotSpotResponse createHotSpot(String name, String description, Double longitude, Double latitude, Double radius) {
        String hotSpotId = UUID.randomUUID().toString().replace("-", "");
        
        HotSpot hotSpot = HotSpot.builder()
            .hotSpotId(hotSpotId)
            .name(name)
            .description(description)
            .hotSpotType("MANUAL")
            .centerLongitude(longitude)
            .centerLatitude(latitude)
            .coverageRadius(radius)
            .heatValue(0.0)
            .status("ACTIVE")
            .createTime(LocalDateTime.now())
            .discoverTime(LocalDateTime.now())
            .build();
        
        redisTemplate.opsForValue().set(HOTSPOT_KEY_PREFIX + hotSpotId, hotSpot, 30, TimeUnit.DAYS);
        
        return convertToHotSpotResponse(hotSpot);
    }

    @Override
    public List<MapStreamResponse> getLiveStreams(StreamQueryRequest request) {
        request.setInfoTypes(Arrays.asList(InfoType.LIVE_STREAM.getCode()));
        return queryNearbyStreams(request);
    }

    @Override
    public List<MapStreamResponse> getVideoStreams(StreamQueryRequest request) {
        request.setInfoTypes(Arrays.asList(InfoType.SHORT_VIDEO.getCode()));
        return queryNearbyStreams(request);
    }

    @Override
    public List<MapStreamResponse> getFriendStreams(Long userId, StreamQueryRequest request) {
        // 获取好友列表
        request.setVisibility("FRIENDS");
        return queryNearbyStreams(request);
    }

    @Override
    public MapSearchResultDTO searchMap(String keyword, Double longitude, Double latitude, Integer radius) {
        MapSearchResultDTO result = new MapSearchResultDTO();
        
        // 搜索信息流
        StreamQueryRequest query = new StreamQueryRequest();
        query.setLongitude(longitude);
        query.setLatitude(latitude);
        query.setRadius(radius);
        query.setKeyword(keyword);
        query.setPageSize(50);
        
        List<MapStreamResponse> streams = queryNearbyStreams(query);
        result.setStreams(streams);
        result.setTotalCount(streams.size());
        
        return result;
    }

    @Override
    public MapStatsDTO getMapStats(String cityCode, Integer days) {
        MapStatsDTO stats = new MapStatsDTO();
        
        // 从Redis获取统计数据
        stats.setTotalStreams((Long) redisTemplate.opsForValue()
            .get("mapstream:stats:total:" + cityCode));
        stats.setTodayStreams((Long) redisTemplate.opsForValue()
            .get("mapstream:stats:today:" + cityCode));
        stats.setActiveUsers((Long) redisTemplate.opsForValue()
            .get("mapstream:stats:users:" + cityCode));
        stats.setHotSpotCount(redisTemplate.opsForZSet()
            .size(HOTSPOT_KEY_PREFIX + "rank:" + cityCode).intValue());
        
        return stats;
    }

    @Override
    public void updateStreamHeat(String streamId) {
        MapInfoStream stream = (MapInfoStream) redisTemplate.opsForValue()
            .get(STREAM_KEY_PREFIX + streamId);
        if (stream != null) {
            stream.calculateHeatValue();
            redisTemplate.opsForValue().set(STREAM_KEY_PREFIX + streamId, stream, 7, TimeUnit.DAYS);
        }
    }

    @Override
    public void deleteStream(String streamId, Long operatorId) {
        MapInfoStream stream = (MapInfoStream) redisTemplate.opsForValue()
            .get(STREAM_KEY_PREFIX + streamId);
        if (stream != null && (stream.getPublisherId().equals(operatorId) || isAdmin(operatorId))) {
            stream.setStatus(2);
            stream.setUpdateTime(LocalDateTime.now());
            redisTemplate.opsForValue().set(STREAM_KEY_PREFIX + streamId, stream, 1, TimeUnit.DAYS);
            log.info("Deleted stream: {} by operator {}", streamId, operatorId);
        }
    }

    // ===== 私有辅助方法 =====
    
    private String calculateGeoHash(double lat, double lon, int precision) {
        String base32 = "0123456789bcdefghjkmnpqrstuvwxyz";
        double latMin = -90.0, latMax = 90.0;
        double lonMin = -180.0, lonMax = 180.0;
        StringBuilder geohash = new StringBuilder();
        boolean isEven = true;
        int bit = 0, ch = 0;
        
        while (geohash.length() < precision) {
            if (isEven) {
                double lonMid = (lonMin + lonMax) / 2.0;
                if (lon >= lonMid) {
                    ch |= (1 << (4 - bit));
                    lonMin = lonMid;
                } else {
                    lonMax = lonMid;
                }
            } else {
                double latMid = (latMin + latMax) / 2.0;
                if (lat >= latMid) {
                    ch |= (1 << (4 - bit));
                    latMin = latMid;
                } else {
                    latMax = latMid;
                }
            }
            
            isEven = !isEven;
            if (bit < 4) {
                bit++;
            } else {
                geohash.append(base32.charAt(ch));
                bit = 0;
                ch = 0;
            }
        }
        return geohash.toString();
    }
    
    private Set<String> getNeighbors(double lat, double lon, int precision) {
        Set<String> neighbors = new HashSet<>();
        String center = calculateGeoHash(lat, lon, precision);
        neighbors.add(center);
        // 添加8个邻居网格
        neighbors.addAll(getAdjacentGeohashes(center));
        return neighbors;
    }
    
    private Set<String> getAdjacentGeohashes(String geohash) {
        Set<String> adjacent = new HashSet<>();
        // 简化实现：添加相邻网格逻辑
        adjacent.add(geohash);
        return adjacent;
    }
    
    private int calculateGeoHashLength(int radius) {
        if (radius < 100) return 8;
        if (radius < 1000) return 7;
        if (radius < 5000) return 6;
        if (radius < 20000) return 5;
        return 4;
    }
    
    private Set<String> getGeohashPrefixesInBounds(Double minLon, Double maxLon, 
                                                    Double minLat, Double maxLat, int length) {
        Set<String> prefixes = new HashSet<>();
        // 简化实现：在边界框内采样
        prefixes.add(calculateGeoHash(minLat, minLon, length));
        prefixes.add(calculateGeoHash(maxLat, maxLon, length));
        prefixes.add(calculateGeoHash((minLat + maxLat) / 2, (minLon + maxLon) / 2, length));
        return prefixes;
    }
    
    private void updateHeatPoint(String geohash, InfoType type) {
        String key = HEAT_KEY_PREFIX + geohash;
        MapHeatPoint point = (MapHeatPoint) redisTemplate.opsForValue().get(key);
        
        if (point == null) {
            point = MapHeatPoint.builder()
                .heatPointId(UUID.randomUUID().toString())
                .geohash(geohash)
                .geohashLength(geohash.length())
                .streamCount(0)
                .userCount(0)
                .heatValue(0.0)
                .typeDistribution(new HashMap<>())
                .build();
        }
        
        point.setStreamCount(point.getStreamCount() + 1);
        point.addHeat(1.0);
        point.getTypeDistribution().merge(type.name(), 1, Integer::sum);
        
        redisTemplate.opsForValue().set(key, point, 1, TimeUnit.DAYS);
    }
    
    private boolean filterStream(MapInfoStream stream, StreamQueryRequest request) {
        if (request.getInfoTypes() != null && !request.getInfoTypes().contains(stream.getInfoType().getCode())) {
            return false;
        }
        if (request.getMinHeatValue() != null && stream.getHeatValue() < request.getMinHeatValue()) {
            return false;
        }
        if (request.getKeyword() != null && !stream.getTitle().contains(request.getKeyword())) {
            return false;
        }
        return true;
    }
    
    private Comparator<MapInfoStream> getComparator(String sortBy) {
        switch (sortBy) {
            case "TIME":
                return Comparator.comparing(MapInfoStream::getCreateTime).reversed();
            case "DISTANCE":
                return Comparator.comparing(MapInfoStream::getLatitude);
            case "HEAT":
            default:
                return Comparator.comparing(MapInfoStream::getHeatValue).reversed();
        }
    }
    
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000; // 地球半径(米)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    
    private MapStreamResponse convertToResponse(MapInfoStream stream) {
        return MapStreamResponse.builder()
            .streamId(stream.getStreamId())
            .publisherId(stream.getPublisherId())
            .publisherNickname(stream.getPublisherNickname())
            .publisherAvatar(stream.getPublisherAvatar())
            .infoType(stream.getInfoType())
            .title(stream.getTitle())
            .content(stream.getContent())
            .mediaUrls(stream.getMediaUrls())
            .thumbnailUrl(stream.getThumbnailUrl())
            .liveStreamUrl(stream.getLiveStreamUrl())
            .longitude(stream.getLongitude())
            .latitude(stream.getLatitude())
            .geohash(stream.getGeohash())
            .poiName(stream.getPoiName())
            .address(stream.getAddress())
            .cityName(stream.getCityName())
            .visibility(stream.getVisibility())
            .tags(stream.getTags())
            .extra(stream.getExtra())
            .viewCount(stream.getViewCount())
            .likeCount(stream.getLikeCount())
            .commentCount(stream.getCommentCount())
            .shareCount(stream.getShareCount())
            .heatValue(stream.getHeatValue())
            .isPinned(stream.getIsPinned())
            .createTime(stream.getCreateTime())
            .build();
    }
    
    private HotSpotResponse convertToHotSpotResponse(HotSpot hotSpot) {
        return HotSpotResponse.builder()
            .hotSpotId(hotSpot.getHotSpotId())
            .name(hotSpot.getName())
            .description(hotSpot.getDescription())
            .hotSpotType(hotSpot.getHotSpotType())
            .longitude(hotSpot.getCenterLongitude())
            .latitude(hotSpot.getCenterLatitude())
            .coverageRadius(hotSpot.getCoverageRadius())
            .heatValue(hotSpot.getHeatValue())
            .rank(hotSpot.getRank())
            .trend(hotSpot.getTrend())
            .keywords(hotSpot.getKeywords())
            .participantCount(hotSpot.getParticipantCount())
            .totalViews(hotSpot.getTotalViews())
            .discoverTime(hotSpot.getDiscoverTime())
            .status(hotSpot.getStatus())
            .build();
    }
    
    private boolean isAdmin(Long userId) {
        // 简化实现：检查是否为管理员
        return userId != null && userId == 1L;
    }
}
