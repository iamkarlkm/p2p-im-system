package com.im.mapstream.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 发布地图信息流请求
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-30
 */
@Data
public class PublishStreamRequest {
    
    @NotNull(message = "信息类型不能为空")
    private Integer infoType;
    
    @NotBlank(message = "标题不能为空")
    private String title;
    
    private String content;
    
    private List<String> mediaUrls;
    
    private String thumbnailUrl;
    
    private String liveStreamUrl;
    
    @NotNull(message = "经度不能为空")
    private Double longitude;
    
    @NotNull(message = "纬度不能为空")
    private Double latitude;
    
    private String poiId;
    
    private String poiName;
    
    private String address;
    
    private String cityCode;
    
    private String visibility = "PUBLIC";
    
    private List<String> tags;
    
    private Map<String, Object> extra;
}
