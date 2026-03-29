package com.im.mapstream.entity;

import com.im.mapstream.enums.ClusterLevel;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 信息聚合簇实体
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfoCluster {
    
    /** 聚合簇ID */
    private String clusterId;
    
    /** 聚合层级 */
    private ClusterLevel clusterLevel;
    
    /** GeoHash前缀 */
    private String geohashPrefix;
    
    /** 聚合中心经度 */
    private Double centerLongitude;
    
    /** 聚合中心纬度 */
    private Double centerLatitude;
    
    /** 聚合半径(米) */
    private Double radius;
    
    /** 包含的信息流ID列表 */
    private List<String> streamIds;
    
    /** 包含的信息流数量 */
    private Integer streamCount;
    
    /** 预览信息流(取最新3条) */
    private List<MapInfoStream> previewStreams;
    
    /** 聚合类型统计 */
    private java.util.Map<String, Integer> typeStats;
    
    /** 聚合热度值 */
    private Double clusterHeat;
    
    /** 聚合标签云 */
    private java.util.Map<String, Integer> tagCloud;
    
    /** 聚合时间 */
    private LocalDateTime clusterTime;
    
    /** 过期时间 */
    private LocalDateTime expireTime;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /**
     * 计算聚合中心
     */
    public void calculateCenter() {
        if (previewStreams == null || previewStreams.isEmpty()) {
            return;
        }
        
        double sumLat = 0, sumLon = 0;
        for (MapInfoStream stream : previewStreams) {
            sumLat += stream.getLatitude();
            sumLon += stream.getLongitude();
        }
        this.centerLatitude = sumLat / previewStreams.size();
        this.centerLongitude = sumLon / previewStreams.size();
    }
    
    /**
     * 计算聚合热度
     */
    public void calculateHeat() {
        if (previewStreams == null || previewStreams.isEmpty()) {
            this.clusterHeat = 0.0;
            return;
        }
        
        this.clusterHeat = previewStreams.stream()
            .mapToDouble(MapInfoStream::getHeatValue)
            .average()
            .orElse(0.0) * Math.log1p(streamCount);
    }
    
    /**
     * 添加信息流
     */
    public void addStream(MapInfoStream stream) {
        this.streamIds.add(stream.getStreamId());
        this.streamCount = streamIds.size();
        
        // 更新类型统计
        String typeName = stream.getInfoType().name();
        this.typeStats.merge(typeName, 1, Integer::sum);
        
        // 更新标签云
        if (stream.getTags() != null) {
            for (String tag : stream.getTags()) {
                this.tagCloud.merge(tag, 1, Integer::sum);
            }
        }
    }
}
