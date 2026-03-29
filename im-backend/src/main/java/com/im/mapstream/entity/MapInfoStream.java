package com.im.mapstream.entity;

import com.im.mapstream.enums.InfoType;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 地图信息流实体
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapInfoStream {
    
    /** 信息流ID */
    private String streamId;
    
    /** 发布者用户ID */
    private Long publisherId;
    
    /** 发布者昵称 */
    private String publisherNickname;
    
    /** 发布者头像 */
    private String publisherAvatar;
    
    /** 信息类型 */
    private InfoType infoType;
    
    /** 标题 */
    private String title;
    
    /** 内容描述 */
    private String content;
    
    /** 媒体URL列表(图片/视频) */
    private List<String> mediaUrls;
    
    /** 缩略图 */
    private String thumbnailUrl;
    
    /** 直播流地址 */
    private String liveStreamUrl;
    
    /** 经度 */
    private Double longitude;
    
    /** 纬度 */
    private Double latitude;
    
    /** GeoHash编码 */
    private String geohash;
    
    /** POI ID */
    private String poiId;
    
    /** POI名称 */
    private String poiName;
    
    /** 地址 */
    private String address;
    
    /** 城市编码 */
    private String cityCode;
    
    /** 城市名称 */
    private String cityName;
    
    /** 可见范围: PUBLIC/FRIENDS/PRIVATE */
    private String visibility;
    
    /** 标签列表 */
    private List<String> tags;
    
    /** 扩展属性 */
    private Map<String, Object> extra;
    
    /** 浏览次数 */
    private Long viewCount;
    
    /** 点赞数 */
    private Long likeCount;
    
    /** 评论数 */
    private Long commentCount;
    
    /** 分享数 */
    private Long shareCount;
    
    /** 热度值 */
    private Double heatValue;
    
    /** 是否置顶 */
    private Boolean isPinned;
    
    /** 置顶过期时间 */
    private LocalDateTime pinExpireTime;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 状态: 0-正常 1-隐藏 2-删除 */
    private Integer status;
    
    /**
     * 计算信息热度值
     */
    public void calculateHeatValue() {
        double baseHeat = viewCount * 0.1 + likeCount * 0.5 + commentCount * 1.0 + shareCount * 2.0;
        double timeDecay = calculateTimeDecay();
        this.heatValue = baseHeat * timeDecay;
    }
    
    private double calculateTimeDecay() {
        long hoursAgo = java.time.Duration.between(createTime, LocalDateTime.now()).toHours();
        return Math.exp(-hoursAgo / 24.0); // 24小时衰减
    }
}
