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
 * 智能搜索查询实体
 * 记录用户的搜索查询历史与上下文
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("local_search_query")
public class LocalSearchQuery {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 原始查询文本
     */
    private String rawQuery;
    
    /**
     * 标准化查询文本
     */
    private String normalizedQuery;
    
    /**
     * 查询意图类型
     * NAVIGATION - 导航
     * GROUPON - 团购
     * RESERVATION - 预约
     * COMPARISON - 比价
     * INFO - 了解详情
     */
    private String intentType;
    
    /**
     * 意图置信度 (0.0 - 1.0)
     */
    private Double intentConfidence;
    
    /**
     * 搜索实体类型
     * RESTAURANT - 餐厅
     * ENTERTAINMENT - 娱乐
     * SHOPPING - 购物
     * SERVICE - 服务
     * POI - 兴趣点
     */
    private String entityType;
    
    /**
     * 地理位置 - 经度
     */
    private Double longitude;
    
    /**
     * 地理位置 - 纬度
     */
    private Double latitude;
    
    /**
     * 搜索半径 (米)
     */
    private Integer radius;
    
    /**
     * 地理位置名称
     */
    private String locationName;
    
    /**
     * 是否语音搜索
     */
    private Boolean isVoiceQuery;
    
    /**
     * 语音识别的方言类型
     */
    private String dialectType;
    
    /**
     * 是否为多轮对话
     */
    private Boolean isMultiTurn;
    
    /**
     * 对话会话ID
     */
    private String conversationId;
    
    /**
     * 多轮对话轮次
     */
    private Integer turnNumber;
    
    /**
     * 上下文信息 (JSON格式)
     * 包含上一轮的查询条件、筛选偏好等
     */
    private String contextJson;
    
    /**
     * 提取的查询参数 (JSON格式)
     * 如：价格范围、评分要求、营业时间等
     */
    private String extractedParams;
    
    /**
     * 是否进行了查询纠错
     */
    private Boolean isCorrected;
    
    /**
     * 原始查询（纠错前）
     */
    private String originalQuery;
    
    /**
     * 搜索结果数量
     */
    private Integer resultCount;
    
    /**
     * 用户点击的结果索引
     */
    private Integer clickedIndex;
    
    /**
     * 点击的POI ID
     */
    private Long clickedPoiId;
    
    /**
     * 响应时间 (毫秒)
     */
    private Long responseTime;
    
    /**
     * 搜索来源
     * APP - 移动端
     * MINI_PROGRAM - 小程序
     * H5 - H5页面
     */
    private String source;
    
    /**
     * 设备类型
     */
    private String deviceType;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // ==================== 业务方法 ====================
    
    /**
     * 判断查询是否为导航意图
     */
    public boolean isNavigationIntent() {
        return "NAVIGATION".equals(this.intentType);
    }
    
    /**
     * 判断查询是否为团购意图
     */
    public boolean isGrouponIntent() {
        return "GROUPON".equals(this.intentType);
    }
    
    /**
     * 判断查询是否为预约意图
     */
    public boolean isReservationIntent() {
        return "RESERVATION".equals(this.intentType);
    }
    
    /**
     * 获取查询上下文
     */
    public Map<String, Object> getContext() {
        // 实际项目中使用 JSON 解析
        return new java.util.HashMap<>();
    }
    
    /**
     * 获取提取的参数
     */
    public Map<String, Object> getExtractedParams() {
        // 实际项目中使用 JSON 解析
        return new java.util.HashMap<>();
    }
    
    /**
     * 创建新的多轮查询
     */
    public LocalSearchQuery createFollowUpQuery(String followUpQuery) {
        return LocalSearchQuery.builder()
                .userId(this.userId)
                .rawQuery(followUpQuery)
                .conversationId(this.conversationId)
                .turnNumber(this.turnNumber + 1)
                .isMultiTurn(true)
                .longitude(this.longitude)
                .latitude(this.latitude)
                .radius(this.radius)
                .source(this.source)
                .deviceType(this.deviceType)
                .build();
    }
    
    /**
     * 更新上下文信息
     */
    public void updateContext(Map<String, Object> context) {
        // 实际项目中使用 JSON 序列化
        this.contextJson = context.toString();
    }
    
    /**
     * 记录点击结果
     */
    public void recordClick(int index, Long poiId) {
        this.clickedIndex = index;
        this.clickedPoiId = poiId;
    }
    
    /**
     * 计算查询质量分数
     */
    public double calculateQualityScore() {
        double score = 0.0;
        if (this.resultCount != null && this.resultCount > 0) {
            score += 0.5;
        }
        if (this.clickedIndex != null) {
            score += 0.3;
        }
        if (this.intentConfidence != null) {
            score += this.intentConfidence * 0.2;
        }
        return score;
    }
    
    /**
     * 构建地理位置GeoHash
     */
    public String buildGeoHash() {
        if (this.latitude == null || this.longitude == null) {
            return null;
        }
        // 使用标准的GeoHash算法，精度为7位（约150米）
        return encodeGeoHash(this.latitude, this.longitude, 7);
    }
    
    /**
     * GeoHash编码算法
     */
    private String encodeGeoHash(double lat, double lon, int precision) {
        String base32 = "0123456789bcdefghjkmnpqrstuvwxyz";
        double latMin = -90, latMax = 90;
        double lonMin = -180, lonMax = 180;
        StringBuilder geohash = new StringBuilder();
        boolean isEven = true;
        int bit = 0, ch = 0;
        
        while (geohash.length() < precision) {
            if (isEven) {
                double lonMid = (lonMin + lonMax) / 2;
                if (lon >= lonMid) {
                    ch |= (1 << (4 - bit));
                    lonMin = lonMid;
                } else {
                    lonMax = lonMid;
                }
            } else {
                double latMid = (latMin + latMax) / 2;
                if (lat >= latMid) {
                    ch |= (1 << (4 - bit));
                    latMin = latMid;
                } else {
                    latMax = latMid;
                }
            }
            
            isEven = !isEven;
            if (bit < 4) {
                bit++;
            } else {
                geohash.append(base32.charAt(ch));
                bit = 0;
                ch = 0;
            }
        }
        
        return geohash.toString();
    }
    
    /**
     * 构建搜索缓存Key
     */
    public String buildCacheKey() {
        String geoHash = buildGeoHash();
        String normalized = this.normalizedQuery != null ? this.normalizedQuery : this.rawQuery;
        return String.format("search:%s:%s:%s:%s", 
                this.intentType, 
                normalized, 
                geoHash != null ? geoHash : "nogeo",
                this.entityType != null ? this.entityType : "all");
    }
    
    /**
     * 检查是否为有效搜索
     */
    public boolean isValidSearch() {
        return this.rawQuery != null && !this.rawQuery.trim().isEmpty();
    }
    
    /**
     * 获取查询关键词列表
     */
    public List<String> extractKeywords() {
        if (this.rawQuery == null) {
            return new java.util.ArrayList<>();
        }
        // 分词处理，实际项目中使用 NLP 分词工具
        String[] tokens = this.rawQuery.split("[\\s,，。！!？?]+");
        return java.util.Arrays.stream(tokens)
                .filter(s -> !s.isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }
}
