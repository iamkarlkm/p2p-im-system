package com.im.backend.service.impl;

import com.im.backend.dto.GeoHashQueryRequest;
import com.im.backend.entity.GeoHashGrid;
import com.im.backend.entity.LocationHeatmap;
import com.im.backend.service.IGeoHashService;
import com.im.backend.utils.GeoHashUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * GeoHash服务实现
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Slf4j
@Service
public class GeoHashServiceImpl implements IGeoHashService {
    
    /** 内存网格缓存 */
    private static final Map<String, GeoHashGrid> gridCache = new ConcurrentHashMap<>();
    
    @Override
    public String encode(double latitude, double longitude, int precision) {
        return GeoHashUtils.encode(latitude, longitude, precision);
    }
    
    @Override
    public double[] decode(String geohash) {
        return GeoHashUtils.decode(geohash);
    }
    
    @Override
    public String[] getNeighbors(String geohash) {
        return GeoHashUtils.getNeighbors(geohash);
    }
    
    @Override
    public double calculateDistance(String geohash1, String geohash2) {
        return GeoHashUtils.calculateDistance(geohash1, geohash2);
    }
    
    @Override
    public GeoHashGrid createOrUpdateGrid(String geohash, int precision) {
        return gridCache.compute(geohash, (k, v) -> {
            if (v == null) {
                GeoHashGrid grid = new GeoHashGrid(geohash, precision);
                double[] center = GeoHashUtils.decode(geohash);
                grid.setCenterLat(center[0]);
                grid.setCenterLon(center[1]);
                double[] bounds = GeoHashUtils.decodeBounds(geohash);
                grid.setMinLat(bounds[0]);
                grid.setMaxLat(bounds[1]);
                grid.setMinLon(bounds[2]);
                grid.setMaxLon(bounds[3]);
                return grid;
            }
            v.setUpdateTime(java.time.LocalDateTime.now());
            return v;
        });
    }
    
    @Override
    public GeoHashGrid getGridInfo(String geohash) {
        return gridCache.get(geohash);
    }
    
    @Override
    public List<GeoHashGrid> queryGrids(GeoHashQueryRequest request) {
        List<GeoHashGrid> result = new ArrayList<>();
        String baseGeohash = request.getGeohash();
        
        if (Boolean.TRUE.equals(request.getIncludeNeighbors())) {
            result.add(getGridInfo(baseGeohash));
            String[] neighbors = getNeighbors(baseGeohash);
            for (String neighbor : neighbors) {
                GeoHashGrid grid = getGridInfo(neighbor);
                if (grid != null) result.add(grid);
            }
        } else {
            GeoHashGrid grid = getGridInfo(baseGeohash);
            if (grid != null) result.add(grid);
        }
        
        return result;
    }
    
    @Override
    public List<GeoHashGrid> getHotGrids(int topN) {
        return gridCache.values().stream()
                .sorted(Comparator.comparing(GeoHashGrid::getHeatScore).reversed())
                .limit(topN)
                .toList();
    }
    
    @Override
    public Map<String, Object> getGridStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalGrids", gridCache.size());
        stats.put("hotGrids", gridCache.values().stream().filter(GeoHashGrid::isHotGrid).count());
        long totalPoints = gridCache.values().stream().mapToLong(GeoHashGrid::getPointCount).sum();
        stats.put("totalPoints", totalPoints);
        stats.put("avgPointsPerGrid", gridCache.isEmpty() ? 0 : totalPoints / gridCache.size());
        return stats;
    }
    
    @Override
    public List<LocationHeatmap> getHeatmapData(String geohash, int precision) {
        List<LocationHeatmap> heatmapList = new ArrayList<>();
        String[] targets = getNeighbors(geohash);
        
        for (String target : targets) {
            GeoHashGrid grid = getGridInfo(target);
            if (grid != null) {
                LocationHeatmap heatmap = new LocationHeatmap(target, precision);
                heatmap.setCenterLat(grid.getCenterLat());
                heatmap.setCenterLon(grid.getCenterLon());
                heatmap.setHeatValue(grid.getHeatScore());
                heatmap.setPointCount(grid.getPointCount());
                heatmap.updateHeatLevel();
                heatmapList.add(heatmap);
            }
        }
        
        return heatmapList;
    }
    
    @Override
    public void refreshGridData(String geohash) {
        GeoHashGrid grid = gridCache.get(geohash);
        if (grid != null) {
            grid.setUpdateTime(java.time.LocalDateTime.now());
            log.info("Grid refreshed: {}", geohash);
        }
    }
    
    @Override
    public void batchRefreshGrids(List<String> geohashes) {
        for (String geohash : geohashes) {
            refreshGridData(geohash);
        }
    }
    
    @Override
    public Long countPoisInGrid(String geohash) {
        GeoHashGrid grid = gridCache.get(geohash);
        return grid != null ? grid.getPoiCount().longValue() : 0L;
    }
    
    @Override
    public Long countUsersInGrid(String geohash) {
        GeoHashGrid grid = gridCache.get(geohash);
        return grid != null ? grid.getPointCount().longValue() : 0L;
    }
    
    @Override
    public int getRecommendedPrecision(double distanceMeters) {
        return GeoHashUtils.calculatePrecision(distanceMeters);
    }
}
