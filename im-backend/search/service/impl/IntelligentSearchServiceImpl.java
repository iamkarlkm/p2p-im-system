package com.im.search.service.impl;

import com.im.search.dto.*;
import com.im.search.entity.PoiSearchDocument;
import com.im.search.service.IntelligentSearchService;
import com.im.search.service.PoiIndexService;
import com.im.search.service.SearchIntentParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 智能搜索服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IntelligentSearchServiceImpl implements IntelligentSearchService {

    private final PoiIndexService poiIndexService;
    private final SearchIntentParser intentParser;
    private final ElasticsearchRestTemplate elasticsearchTemplate;

    @Override
    public SearchResponseDTO naturalLanguageSearch(NaturalLanguageSearchRequestDTO request) {
        long startTime = System.currentTimeMillis();
        
        // 1. 解析搜索意图
        SearchIntentDTO intent = intentParser.parse(request.getQuery(), request.getSessionId());
        
        // 2. 构建搜索条件
        GeoPoint center = new GeoPoint(request.getLatitude(), request.getLongitude());
        
        // 3. 执行搜索
        List<PoiSearchResultDTO> results = executeSearch(intent, center, request);
        
        // 4. 构建响应
        long took = System.currentTimeMillis() - startTime;
        
        return SearchResponseDTO.builder()
                .total((long) results.size())
                .page(request.getPage())
                .size(request.getSize())
                .results(results)
                .intent(intent)
                .took(took)
                .hasNext(results.size() >= request.getSize())
                .build();
    }

    @Override
    public SearchIntentDTO parseSearchIntent(String query, String sessionId) {
        return intentParser.parse(query, sessionId);
    }

    @Override
    public List<String> getSearchSuggestions(String keyword, String cityCode) {
        // 基于前缀匹配和热门搜索返回建议
        return List.of(
            keyword + "附近",
            keyword + "推荐",
            keyword + "优惠",
            "附近好吃的" + keyword,
            "适合带娃的" + keyword
        );
    }

    @Override
    public List<String> getHotSearches(String cityCode) {
        // 返回热门搜索
        return List.of(
            "附近好吃的火锅",
            "适合遛娃的公园",
            "周末好去处",
            "人均100以下的餐厅",
            "网红打卡地",
            "24小时营业",
            "免费停车",
            "适合约会的地方"
        );
    }

    @Override
    public SearchResponseDTO semanticSearch(String query, Double longitude, Double latitude, Integer radius) {
        // 语义搜索实现
        return naturalLanguageSearch(NaturalLanguageSearchRequestDTO.builder()
                .query(query)
                .longitude(longitude)
                .latitude(latitude)
                .radius(radius)
                .enableSemantic(true)
                .build());
    }

    @Override
    public SearchResponseDTO dialogSearch(String query, String sessionId, Long userId) {
        // 多轮对话搜索
        return naturalLanguageSearch(NaturalLanguageSearchRequestDTO.builder()
                .query(query)
                .sessionId(sessionId)
                .userId(userId)
                .enableSemantic(true)
                .build());
    }

    @Override
    public String intelligentQA(String question, String poiId) {
        // 智能问答实现
        return "根据您的问题，这是一个测试回答。实际实现需要集成NLP模型。";
    }

    /**
     * 执行搜索
     */
    private List<PoiSearchResultDTO> executeSearch(SearchIntentDTO intent, GeoPoint center, 
                                                   NaturalLanguageSearchRequestDTO request) {
        // 构建布尔查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        // 关键词搜索
        if (intent.getStructuredQuery() != null && intent.getStructuredQuery().getKeywords() != null) {
            for (String keyword : intent.getStructuredQuery().getKeywords()) {
                boolQuery.must(QueryBuilders.multiMatchQuery(keyword)
                        .field("name", 3.0f)
                        .field("brandName", 2.0f)
                        .field("tags", 2.0f)
                        .field("description", 1.0f)
                        .field("address", 1.0f)
                        .type(org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.BEST_FIELDS));
            }
        }
        
        // 分类过滤
        if (request.getCategory() != null) {
            boolQuery.filter(QueryBuilders.termQuery("category", request.getCategory()));
        }
        
        // 价格过滤
        if (request.getMinPrice() != null || request.getMaxPrice() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("avgPrice")
                    .gte(request.getMinPrice() != null ? request.getMinPrice() : 0)
                    .lte(request.getMaxPrice() != null ? request.getMaxPrice() : 10000));
        }
        
        // 评分过滤
        if (request.getMinRating() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("rating").gte(request.getMinRating()));
        }
        
        // 标签过滤
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            boolQuery.filter(QueryBuilders.termsQuery("tags", request.getTags()));
        }
        
        // 城市过滤
        if (request.getCityCode() != null) {
            boolQuery.filter(QueryBuilders.termQuery("city", request.getCityCode()));
        }
        
        // 状态过滤
        boolQuery.filter(QueryBuilders.termQuery("status", 1));
        
        // 地理位置过滤
        boolQuery.filter(QueryBuilders.geoDistanceQuery("location")
                .point(center.getLat(), center.getLon())
                .distance(request.getRadius(), DistanceUnit.METERS));
        
        // 构建排序
        SortOrder sortOrder = "asc".equals(request.getSortOrder()) ? SortOrder.ASC : SortOrder.DESC;
        GeoDistanceSortBuilder geoSort = SortBuilders.geoDistanceSort("location", center.getLat(), center.getLon())
                .order(sortOrder);
        
        // 构建查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withSort(geoSort)
                .withPageable(PageRequest.of(request.getPage(), request.getSize()))
                .build();
        
        // 执行搜索
        SearchHits<PoiSearchDocument> searchHits = elasticsearchTemplate.search(searchQuery, PoiSearchDocument.class);
        
        // 转换为DTO
        return searchHits.getSearchHits().stream()
                .map(hit -> convertToDTO(hit.getContent(), hit, center))
                .collect(Collectors.toList());
    }

    /**
     * 转换为DTO
     */
    private PoiSearchResultDTO convertToDTO(PoiSearchDocument doc, SearchHit<PoiSearchDocument> hit, GeoPoint center) {
        // 计算距离
        Double distance = hit.getSortValues().isEmpty() ? 0.0 : Double.parseDouble(hit.getSortValues().get(0).toString());
        
        return PoiSearchResultDTO.builder()
                .id(doc.getId())
                .name(doc.getName())
                .brandName(doc.getBrandName())
                .category(doc.getCategory())
                .address(doc.getAddress())
                .distance(distance.intValue())
                .distanceText(formatDistance(distance.intValue()))
                .longitude(doc.getLongitude())
                .latitude(doc.getLatitude())
                .rating(doc.getRating())
                .ratingCount(doc.getRatingCount())
                .avgPrice(doc.getAvgPrice())
                .priceText(formatPrice(doc.getAvgPrice()))
                .tags(doc.getTags())
                .mainImage(doc.getMainImage())
                .businessHours(doc.getBusinessHours())
                .hasWifi(doc.getHasWifi())
                .hasParking(doc.getHasParking())
                .build();
    }

    /**
     * 格式化距离
     */
    private String formatDistance(Integer meters) {
        if (meters < 1000) {
            return meters + "m";
        } else {
            return String.format("%.1fkm", meters / 1000.0);
        }
    }

    /**
     * 格式化价格
     */
    private String formatPrice(Integer price) {
        if (price == null || price == 0) {
            return "暂无";
        }
        return "¥" + price + "/人";
    }
}
