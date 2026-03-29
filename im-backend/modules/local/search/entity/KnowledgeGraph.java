package com.im.backend.modules.local.search.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 本地生活知识图谱实体
 * 存储POI实体及其关系信息
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("local_knowledge_graph")
public class KnowledgeGraph {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 实体ID (POI ID)
     */
    private Long entityId;
    
    /**
     * 实体类型
     * RESTAURANT - 餐厅
     * SHOP - 商店
     * ENTERTAINMENT - 娱乐场所
     * SERVICE - 服务
     * SCENIC - 景点
     * HOTEL - 酒店
     */
    private String entityType;
    
    /**
     * 实体名称
     */
    private String entityName;
    
    /**
     * 实体别名 (JSON格式)
     * 如：["海底捞火锅", "海底捞"]
     */
    private String aliases;
    
    /**
     * 分类标签 (JSON格式)
     * 如：["火锅", "川菜", "自助餐"]
     */
    private String categories;
    
    /**
     * 属性信息 (JSON格式)
     * 如：{"price_per_person": 120, "business_hours": "09:00-22:00"}
     */
    private String attributes;
    
    /**
     * 关联的商圈ID
     */
    private Long businessDistrictId;
    
    /**
     * 商圈名称
     */
    private String businessDistrictName;
    
    /**
     * 地理位置 - 经度
     */
    private Double longitude;
    
    /**
     * 地理位置 - 纬度
     */
    private Double latitude;
    
    /**
     * GeoHash编码
     */
    private String geoHash;
    
    /**
     * 关联商户ID列表 (JSON格式)
     * 同品牌/连锁店
     */
    private String relatedMerchantIds;
    
    /**
     * 相似POI ID列表 (JSON格式)
     * 相似类型/价位/风格的POI
     */
    private String similarPoiIds;
    
    /**
     * 竞争POI ID列表 (JSON格式)
     * 同商圈同类型的竞争对手
     */
    private String competitorIds;
    
    /**
     * 互补POI ID列表 (JSON格式)
     * 可组合消费的POI（如餐厅+KTV）
     */
    private String complementaryIds;
    
    /**
     * 热门活动ID列表 (JSON格式)
     */
    private String activityIds;
    
    /**
     * 用户标签云 (JSON格式)
     * 用户生成标签及其权重
     * 如：{"适合约会": 0.85, "服务好": 0.92}
     */
    private String userTags;
    
    /**
     * 评分信息 (JSON格式)
     * 如：{"overall": 4.5, "taste": 4.6, "service": 4.3}
     */
    private String ratings;
    
    /**
     * 搜索热度
     */
    private Integer searchHeat;
    
    /**
     * 访问热度
     */
    private Integer visitHeat;
    
    /**
     * 热度更新时间
     */
    private LocalDateTime heatUpdateTime;
    
    /**
     * 知识图谱版本
     */
    private String version;
    
    /**
     * 数据来源
     */
    private String dataSource;
    
    /**
     * 是否有效
     */
    private Boolean isValid;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // ==================== 业务方法 ====================
    
    /**
     * 获取别名列表
     */
    public List<String> getAliasList() {
        if (this.aliases == null) {
            return new java.util.ArrayList<>();
        }
        return parseJsonArray(this.aliases);
    }
    
    /**
     * 获取分类列表
     */
    public List<String> getCategoryList() {
        if (this.categories == null) {
            return new java.util.ArrayList<>();
        }
        return parseJsonArray(this.categories);
    }
    
    /**
     * 获取属性Map
     */
    public Map<String, Object> getAttributeMap() {
        if (this.attributes == null) {
            return new java.util.HashMap<>();
        }
        // 实际项目中使用 JSON 解析
        return new java.util.HashMap<>();
    }
    
    /**
     * 获取关联商户ID列表
     */
    public List<Long> getRelatedMerchantIdList() {
        if (this.relatedMerchantIds == null) {
            return new java.util.ArrayList<>();
        }
        return parseLongList(this.relatedMerchantIds);
    }
    
    /**
     * 获取相似POI ID列表
     */
    public List<Long> getSimilarPoiIdList() {
        if (this.similarPoiIds == null) {
            return new java.util.ArrayList<>();
        }
        return parseLongList(this.similarPoiIds);
    }
    
    /**
     * 获取竞争POI ID列表
     */
    public List<Long> getCompetitorIdList() {
        if (this.competitorIds == null) {
            return new java.util.ArrayList<>();
        }
        return parseLongList(this.competitorIds);
    }
    
    /**
     * 获取互补POI ID列表
     */
    public List<Long> getComplementaryIdList() {
        if (this.complementaryIds == null) {
            return new java.util.ArrayList<>();
        }
        return parseLongList(this.complementaryIds);
    }
    
    /**
     * 获取用户标签Map
     */
    public Map<String, Double> getUserTagMap() {
        if (this.userTags == null) {
            return new java.util.HashMap<>();
        }
        // 实际项目中使用 JSON 解析
        return new java.util.HashMap<>();
    }
    
    /**
     * 获取评分Map
     */
    public Map<String, Double> getRatingMap() {
        if (this.ratings == null) {
            return new java.util.HashMap<>();
        }
        // 实际项目中使用 JSON 解析
        return new java.util.HashMap<>();
    }
    
    /**
     * 计算综合热度
     */
    public double calculateHeatScore() {
        double searchWeight = 0.4;
        double visitWeight = 0.6;
        
        double searchScore = this.searchHeat != null ? Math.log1p(this.searchHeat) : 0;
        double visitScore = this.visitHeat != null ? Math.log1p(this.visitHeat) : 0;
        
        return searchScore * searchWeight + visitScore * visitWeight;
    }
    
    /**
     * 更新热度
     */
    public void updateHeat(int searchIncrement, int visitIncrement) {
        if (this.searchHeat == null) {
            this.searchHeat = 0;
        }
        if (this.visitHeat == null) {
            this.visitHeat = 0;
        }
        
        this.searchHeat += searchIncrement;
        this.visitHeat += visitIncrement;
        this.heatUpdateTime = LocalDateTime.now();
    }
    
    /**
     * 检查是否包含指定分类
     */
    public boolean hasCategory(String category) {
        return getCategoryList().contains(category);
    }
    
    /**
     * 检查是否包含指定属性
     */
    public boolean hasAttribute(String key) {
        return getAttributeMap().containsKey(key);
    }
    
    /**
     * 获取属性值
     */
    public Object getAttribute(String key) {
        return getAttributeMap().get(key);
    }
    
    /**
     * 计算与另一个POI的相似度
     */
    public double calculateSimilarity(KnowledgeGraph other) {
        double score = 0.0;
        int factorCount = 0;
        
        // 分类相似度
        List<String> myCategories = getCategoryList();
        List<String> otherCategories = other.getCategoryList();
        if (!myCategories.isEmpty() && !otherCategories.isEmpty()) {
            int common = (int) myCategories.stream().filter(otherCategories::contains).count();
            score += (double) common / Math.max(myCategories.size(), otherCategories.size());
            factorCount++;
        }
        
        // 商圈相同加分
        if (this.businessDistrictId != null && 
            this.businessDistrictId.equals(other.getBusinessDistrictId())) {
            score += 0.3;
            factorCount++;
        }
        
        // 距离相近加分
        if (this.latitude != null && this.longitude != null && 
            other.getLatitude() != null && other.getLongitude() != null) {
            double distance = calculateDistance(
                this.latitude, this.longitude,
                other.getLatitude(), other.getLongitude()
            );
            if (distance < 1000) { // 1公里内
                score += 0.2;
                factorCount++;
            }
        }
        
        return factorCount > 0 ? score / factorCount : 0.0;
    }
    
    /**
     * 计算两个坐标之间的距离（米）
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // 地球半径（米）
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    /**
     * 获取推荐的相关POI列表
     */
    public List<Long> getRecommendedRelatedPois(int limit) {
        List<Long> recommendations = new java.util.ArrayList<>();
        
        // 先添加互补POI
        recommendations.addAll(getComplementaryIdList());
        
        // 再添加相似POI
        for (Long id : getSimilarPoiIdList()) {
            if (!recommendations.contains(id)) {
                recommendations.add(id);
            }
        }
        
        // 最后添加同品牌POI
        for (Long id : getRelatedMerchantIdList()) {
            if (!recommendations.contains(id)) {
                recommendations.add(id);
            }
        }
        
        return recommendations.stream()
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }
    
    // ==================== 辅助方法 ====================
    
    private List<String> parseJsonArray(String json) {
        try {
            String cleaned = json.replace("[", "").replace("]", "").replace("\"", "");
            if (cleaned.isEmpty()) {
                return new java.util.ArrayList<>();
            }
            return java.util.Arrays.asList(cleaned.split(","));
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }
    
    private List<Long> parseLongList(String json) {
        try {
            String cleaned = json.replace("[", "").replace("]", "").replace("\"", "");
            if (cleaned.isEmpty()) {
                return new java.util.ArrayList<>();
            }
            String[] parts = cleaned.split(",");
            List<Long> result = new java.util.ArrayList<>();
            for (String part : parts) {
                result.add(Long.parseLong(part.trim()));
            }
            return result;
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }
}
