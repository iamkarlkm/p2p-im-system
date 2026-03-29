package com.im.search.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * POI搜索索引实体
 * 用于Elasticsearch存储和检索POI信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "poi_search_v1")
@Setting(settingPath = "elasticsearch/poi-settings.json")
public class PoiSearchDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    /** POI名称 */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String name;

    /** 商户名称（如果是连锁店） */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String brandName;

    /** POI分类 */
    @Field(type = FieldType.Keyword)
    private String category;

    /** 分类编码 */
    @Field(type = FieldType.Keyword)
    private String categoryCode;

    /** 分类层级路径 */
    @Field(type = FieldType.Keyword)
    private List<String> categoryPath;

    /** 地理坐标 [lon, lat] */
    @GeoPointField
    private String location;

    /** 经度 */
    @Field(type = FieldType.Double)
    private Double longitude;

    /** 纬度 */
    @Field(type = FieldType.Double)
    private Double latitude;

    /** 详细地址 */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String address;

    /** 所在城市 */
    @Field(type = FieldType.Keyword)
    private String city;

    /** 所在区县 */
    @Field(type = FieldType.Keyword)
    private String district;

    /** 所在商圈 */
    @Field(type = FieldType.Keyword)
    private String businessArea;

    /** 联系电话 */
    @Field(type = FieldType.Keyword)
    private String phone;

    /** 营业时间 */
    @Field(type = FieldType.Keyword)
    private String businessHours;

    /** 人均消费 */
    @Field(type = FieldType.Integer)
    private Integer avgPrice;

    /** 评分 */
    @Field(type = FieldType.Float)
    private Float rating;

    /** 评分数量 */
    @Field(type = FieldType.Integer)
    private Integer ratingCount;

    /** 口味评分 */
    @Field(type = FieldType.Float)
    private Float tasteRating;

    /** 环境评分 */
    @Field(type = FieldType.Float)
    private Float environmentRating;

    /** 服务评分 */
    @Field(type = FieldType.Float)
    private Float serviceRating;

    /** 特色标签 */
    @Field(type = FieldType.Keyword)
    private List<String> tags;

    /** 商户描述 */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String description;

    /** 特色服务 */
    @Field(type = FieldType.Keyword)
    private List<String> services;

    /** 是否支持WiFi */
    @Field(type = FieldType.Boolean)
    private Boolean hasWifi;

    /** 是否支持停车 */
    @Field(type = FieldType.Boolean)
    private Boolean hasParking;

    /** 是否支持外卖 */
    @Field(type = FieldType.Boolean)
    private Boolean supportsDelivery;

    /** 是否支持预约 */
    @Field(type = FieldType.Boolean)
    private Boolean supportsReservation;

    /** 主图URL */
    @Field(type = FieldType.Keyword)
    private String mainImage;

    /** 图片列表 */
    @Field(type = FieldType.Keyword)
    private List<String> images;

    /** 热度分数 */
    @Field(type = FieldType.Float)
    private Float hotScore;

    /** 搜索热度 */
    @Field(type = FieldType.Integer)
    private Integer searchCount;

    /** 点击热度 */
    @Field(type = FieldType.Integer)
    private Integer clickCount;

    /** 访问热度 */
    @Field(type = FieldType.Integer)
    private Integer visitCount;

    /** 数据来源 */
    @Field(type = FieldType.Keyword)
    private String dataSource;

    /** 商户ID（关联关系型数据库） */
    @Field(type = FieldType.Long)
    private Long merchantId;

    /** 创建时间 */
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createTime;

    /** 更新时间 */
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updateTime;

    /** 扩展字段 */
    @Field(type = FieldType.Object, enabled = false)
    private Map<String, Object> extFields;

    /** 状态：0-下架 1-上架 */
    @Field(type = FieldType.Integer)
    private Integer status;
}
