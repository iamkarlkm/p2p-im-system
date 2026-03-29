package com.im.local.marketing.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 附近优惠券查询请求DTO
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyCouponDTO {
    
    /**
     * 用户经度
     */
    @NotNull(message = "经度不能为空")
    private Double lng;
    
    /**
     * 用户纬度
     */
    @NotNull(message = "纬度不能为空")
    private Double lat;
    
    /**
     * 搜索半径（米），默认5000
     */
    private Integer radius;
    
    /**
     * 城市代码
     */
    private String cityCode;
    
    /**
     * 区县ID
     */
    private String districtId;
    
    /**
     * 分类筛选
     */
    private String category;
    
    /**
     * 排序方式
     * DISTANCE: 按距离排序
     * HOT: 按热度排序
     * NEW: 按最新排序
     * DISCOUNT: 按优惠力度排序
     */
    private String sortBy;
    
    /**
     * 页码
     */
    private Integer page;
    
    /**
     * 每页大小
     */
    private Integer size;
    
    /**
     * 关键词搜索
     */
    private String keyword;
    
    /**
     * 优惠券类型筛选
     */
    private String couponType;
    
    /**
     * 最低优惠金额筛选
     */
    private Double minDiscount;
    
    /**
     * 是否只显示新用户专享
     */
    private Boolean newUserOnly;
    
    /**
     * 获取默认搜索半径
     */
    public Integer getRadius() {
        return radius == null ? 5000 : radius;
    }
    
    /**
     * 获取默认页码
     */
    public Integer getPage() {
        return page == null ? 0 : page;
    }
    
    /**
     * 获取默认每页大小
     */
    public Integer getSize() {
        return size == null ? 20 : size;
    }
}
