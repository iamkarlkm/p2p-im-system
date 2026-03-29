package com.im.backend.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 位置推荐请求DTO
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
public class LocationRecommendRequest {
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 纬度
     */
    @NotNull(message = "纬度不能为空")
    @Min(value = -90)
    @Max(value = 90)
    private Double latitude;
    
    /**
     * 经度
     */
    @NotNull(message = "经度不能为空")
    @Min(value = -180)
    @Max(value = 180)
    private Double longitude;
    
    /**
     * 推荐数量
     */
    @Min(1)
    @Max(50)
    private Integer recommendCount = 10;
    
    /**
     * 搜索半径（米）
     */
    @Min(100)
    @Max(50000)
    private Integer radius = 5000;
    
    /**
     * 偏好的POI分类编码列表
     */
    private List<String> preferredCategories;
    
    /**
     * 推荐场景：1-餐饮 2-购物 3-娱乐 4-出行 5-综合
     */
    @Min(1)
    @Max(5)
    private Integer sceneType = 5;
    
    /**
     * 是否考虑营业时间
     */
    private Boolean considerBusinessHours = true;
    
    /**
     * 最低评分要求
     */
    @Min(1)
    @Max(5)
    private Double minRating = 3.5;
    
    /**
     * 推荐算法：1-距离优先 2-评分优先 3-个性化 4-混合
     */
    @Min(1)
    @Max(4)
    private Integer algorithmType = 4;
}
