package com.im.mapstream.dto;

import com.im.mapstream.enums.InfoType;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 信息流响应
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-30
 */
@Data
@Builder
public class MapStreamResponse {
    
    private String streamId;
    private Long publisherId;
    private String publisherNickname;
    private String publisherAvatar;
    private InfoType infoType;
    private String title;
    private String content;
    private List<String> mediaUrls;
    private String thumbnailUrl;
    private String liveStreamUrl;
    private Double longitude;
    private Double latitude;
    private String geohash;
    private String poiName;
    private String address;
    private String cityName;
    private String visibility;
    private List<String> tags;
    private Map<String, Object> extra;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Long shareCount;
    private Double heatValue;
    private Boolean isPinned;
    private LocalDateTime createTime;
    private Double distance;
}
