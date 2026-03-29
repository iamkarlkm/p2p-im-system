package com.im.dto.recommendation;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 推荐信息流请求DTO
 * 用户请求个性化推荐信息流的参数封装
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationFeedRequestDTO {
    
    /**
     * 用户ID（从Token中获取，非必填）
     */
    private String userId;
    
    /**
     * 会话ID（用于分页追踪，首次请求可为空）
     */
    private String sessionId;
    
    /**
     * 分页游标（首次请求可为空）
     */
    private String cursor;
    
    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码必须从1开始")
    @Builder.Default
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小至少为1")
    @Max(value = 50, message = "每页大小不能超过50")
    @Builder.Default
    private Integer pageSize = 20;
    
    /**
     * 推荐场景
     * NEARBY: 附近推荐
     * HOME: 首页信息流
     * DISCOVER: 发现页
     * FAVORITE: 猜你喜欢
     * SCENE: 场景化推荐
     * SEARCH: 搜索结果页推荐
     * DETAIL: 详情页推荐
     */
    @NotBlank(message = "推荐场景不能为空")
    @Pattern(regexp = "^(NEARBY|HOME|DISCOVER|FAVORITE|SCENE|SEARCH|DETAIL)$", message = "推荐场景类型不合法")
    private String scene;
    
    /**
     * 用户当前经度
     */
    @NotNull(message = "经度不能为空")
    @DecimalMin(value = "-180.0", message = "经度范围错误")
    @DecimalMax(value = "180.0", message = "经度范围错误")
    private Double longitude;
    
    /**
     * 用户当前纬度
     */
    @NotNull(message = "纬度不能为空")
    @DecimalMin(value = "-90.0", message = "纬度范围错误")
    @DecimalMax(value = "90.0", message = "纬度范围错误")
    private Double latitude;
    
    /**
     * 位置精度（米）
     */
    private Double accuracy;
    
    /**
     * 城市代码
     */
    private String cityCode;
    
    /**
     * 区县代码
     */
    private String districtCode;
    
    /**
     * 搜索半径（米）
     */
    @Min(value = 100, message = "搜索半径至少100米")
    @Max(value = 50000, message = "搜索半径最大50公里")
    @Builder.Default
    private Integer searchRadius = 5000;
    
    /**
     * 分类筛选
     */
    private List<String> categoryFilters;
    
    /**
     * 商户ID筛选
     */
    private List<String> merchantFilters;
    
    /**
     * 价格区间筛选
     * LOW: 低价位
     * MEDIUM: 中价位
     * HIGH: 高价位
     * LUXURY: 豪华
     */
    private Set<String> priceRangeFilters;
    
    /**
     * 推荐类型筛选
     * POI: 兴趣点
     * ACTIVITY: 活动
     * COUPON: 优惠券
     * GROUP: 拼团
     * EVENT: 事件
     * CONTENT: 内容
     */
    private Set<String> itemTypeFilters;
    
    /**
     * 最低评分
     */
    @DecimalMin(value = "0.0", message = "评分不能小于0")
    @DecimalMax(value = "5.0", message = "评分不能大于5")
    private Double minRating;
    
    /**
     * 排序方式
     * RECOMMEND: 综合推荐（默认）
     * DISTANCE: 距离优先
     * RATING: 评分优先
     * HEAT: 热度优先
     * PRICE_ASC: 价格从低到高
     * PRICE_DESC: 价格从高到低
     * NEWEST: 最新发布
     */
    @Pattern(regexp = "^(RECOMMEND|DISTANCE|RATING|HEAT|PRICE_ASC|PRICE_DESC|NEWEST)$", message = "排序方式不合法")
    @Builder.Default
    private String sortBy = "RECOMMEND";
    
    /**
     * 场景标签
     * BREAKFAST: 早餐
     * LUNCH: 午餐
     * DINNER: 晚餐
     * NIGHT: 夜宵
     * WEEKEND: 周末
     * DATING: 约会
     * FAMILY: 亲子
     * BUSINESS: 商务
     * PARTY: 聚会
     */
    private Set<String> sceneTags;
    
    /**
     * 关键词（用于关键词召回）
     */
    private String keyword;
    
    /**
     * 是否排除已消费过的商户
     */
    @Builder.Default
    private Boolean excludeConsumed = false;
    
    /**
     * 是否只显示营业中
     */
    @Builder.Default
    private Boolean onlyOpenNow = false;
    
    /**
     * 是否有优惠券
     */
    private Boolean hasCoupon;
    
    /**
     * 是否支持预订
     */
    private Boolean supportReservation;
    
    /**
     * 设备信息
     */
    private DeviceInfo deviceInfo;
    
    /**
     * 扩展参数
     */
    private Map<String, Object> extraParams;
    
    /**
     * A/B测试分组（服务端分配，客户端无需填写）
     */
    private String abTestGroup;
    
    // ==================== 内部类定义 ====================
    
    /**
     * 设备信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceInfo {
        /**
         * 设备ID
         */
        private String deviceId;
        
        /**
         * 设备类型
         * ANDROID: 安卓
         * IOS: iOS
         * WEB: 网页
         * MINI_PROGRAM: 小程序
         */
        private String deviceType;
        
        /**
         * 设备型号
         */
        private String deviceModel;
        
        /**
         * 操作系统版本
         */
        private String osVersion;
        
        /**
         * 应用版本
         */
        private String appVersion;
        
        /**
         * 网络类型
         * WIFI: WiFi
         * 4G: 4G
         * 5G: 5G
         * 3G: 3G
         * 2G: 2G
         */
        private String networkType;
        
        /**
         * 屏幕分辨率
         */
        private String screenResolution;
    }
}
