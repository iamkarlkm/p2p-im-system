package com.im.backend.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.dto.SearchRequest;
import com.im.backend.dto.SearchResponse;
import com.im.backend.entity.LocalLifeSearchQuery;
import com.im.backend.entity.SearchResultCache;
import com.im.backend.repository.SearchQueryMapper;
import com.im.backend.service.ILocalSearchService;
import com.im.backend.util.GeoHashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 本地生活搜索服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocalSearchServiceImpl extends ServiceImpl<SearchQueryMapper, LocalLifeSearchQuery> implements ILocalSearchService {

    private final StringRedisTemplate redisTemplate;

    private static final String CACHE_KEY_PREFIX = "search:";
    private static final long CACHE_TTL_MINUTES = 10;

    @Override
    public SearchResponse search(SearchRequest request, Long userId) {
        long startTime = System.currentTimeMillis();
        
        // 1. 构建缓存键
        String cacheKey = buildCacheKey(request);
        
        // 2. 尝试从缓存获取
        SearchResponse cachedResult = getFromCache(cacheKey);
        if (cachedResult != null) {
            cachedResult.setFromCache(true);
            cachedResult.setSearchTime((int) (System.currentTimeMillis() - startTime));
            return cachedResult;
        }

        // 3. 记录搜索查询
        Long searchId = saveSearchQuery(request, userId);

        // 4. 执行搜索逻辑
        List<SearchResponse.SearchResultItem> results = performSearch(request);

        // 5. 构建响应
        SearchResponse response = SearchResponse.builder()
                .keyword(request.getKeyword())
                .totalCount((long) results.size())
                .pageNum(request.getPageNum())
                .pageSize(request.getPageSize())
                .totalPages((int) Math.ceil((double) results.size() / request.getPageSize()))
                .hasNextPage(request.getPageNum() * request.getPageSize() < results.size())
                .results(paginateResults(results, request))
                .searchTime((int) (System.currentTimeMillis() - startTime))
                .fromCache(false)
                .suggestFilters(generateSuggestFilters(results))
                .searchSuggestions(getSearchSuggestions(request.getKeyword(), request.getCityCode()))
                .currentLocation(buildLocationInfo(request))
                .stats(buildSearchStats(results))
                .build();

        // 6. 缓存结果
        cacheSearchResult(cacheKey, response);

        return response;
    }

    @Override
    public List<String> getSearchSuggestions(String keyword, String cityCode) {
        if (!StringUtils.hasText(keyword) || keyword.length() < 1) {
            return Collections.emptyList();
        }

        // 从Redis获取搜索建议
        String cacheKey = "suggest:" + cityCode + ":" + keyword.toLowerCase();
        String cached = redisTemplate.opsForValue().get(cacheKey);
        
        if (cached != null) {
            return JSON.parseArray(cached, String.class);
        }

        // 模拟搜索建议
        List<String> suggestions = Arrays.asList(
                keyword + "附近",
                keyword + "推荐",
                keyword + "优惠",
                "最好的" + keyword,
                keyword + "排名"
        );

        redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(suggestions), 5, TimeUnit.MINUTES);
        return suggestions;
    }

    @Override
    public List<String> getHotKeywords(String cityCode, Integer limit) {
        String cacheKey = "hot:keywords:" + cityCode;
        String cached = redisTemplate.opsForValue().get(cacheKey);
        
        if (cached != null) {
            return JSON.parseArray(cached, String.class);
        }

        // 模拟热门搜索词
        List<String> hotWords = Arrays.asList(
                "火锅", "烧烤", "日料", "咖啡厅", "健身房",
                "电影院", "KTV", "按摩", "美容院", "超市"
        );

        redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(hotWords), 30, TimeUnit.MINUTES);
        return hotWords.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<String> getUserSearchHistory(Long userId, Integer limit) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return baseMapper.selectUserRecentSearches(userId, limit)
                .stream()
                .map(SearchQueryMapper.UserSearchHistory::getKeyword)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }

    @Override
    public void clearUserSearchHistory(Long userId) {
        // 逻辑删除用户搜索历史
        if (userId != null) {
            baseMapper.selectByUserId(userId, 1000)
                    .forEach(query -> removeById(query.getId()));
        }
    }

    @Override
    public SearchResponse.SearchResultItem getPoiDetail(Long poiId) {
        // 模拟POI详情
        return SearchResponse.SearchResultItem.builder()
                .poiId(poiId)
                .name("示例商户" + poiId)
                .type("FOOD")
                .typeName("美食")
                .address("示例地址")
                .rating(4.5)
                .avgPrice(100)
                .phone("13800138000")
                .isOpen(true)
                .tags(Arrays.asList("推荐", "热门"))
                .build();
    }

    @Override
    public void recordSearchClick(Long searchId, Long poiId) {
        LocalLifeSearchQuery query = getById(searchId);
        if (query != null) {
            query.setHasClick(true);
            query.setClickedPoiId(poiId);
            updateById(query);
        }
    }

    @Override
    public List<SearchResponse.SearchResultItem> getNearbyRecommendations(Double longitude, Double latitude, Integer limit) {
        SearchRequest request = SearchRequest.builder()
                .longitude(longitude)
                .latitude(latitude)
                .radius(5000)
                .pageNum(1)
                .pageSize(limit)
                .sortBy("SMART")
                .build();

        return performSearch(request).stream().limit(limit).collect(Collectors.toList());
    }

    // ============ 私有方法 ============

    private String buildCacheKey(SearchRequest request) {
        StringBuilder key = new StringBuilder(CACHE_KEY_PREFIX);
        key.append(request.getKeyword() != null ? request.getKeyword().hashCode() : "all").append(":");
        key.append(request.getCityCode() != null ? request.getCityCode() : "all").append(":");
        if (request.getLongitude() != null && request.getLatitude() != null) {
            key.append(GeoHashUtil.encode(request.getLatitude(), request.getLongitude(), 6)).append(":");
        }
        key.append(request.getSortBy()).append(":");
        key.append(request.getPageNum()).append(":").append(request.getPageSize());
        return key.toString();
    }

    private SearchResponse getFromCache(String cacheKey) {
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                return JSON.parseObject(cached, SearchResponse.class);
            } catch (Exception e) {
                log.warn("Cache parse error: {}", e.getMessage());
            }
        }
        return null;
    }

    private void cacheSearchResult(String cacheKey, SearchResponse response) {
        try {
            redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(response), CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("Cache save error: {}", e.getMessage());
        }
    }

    private Long saveSearchQuery(SearchRequest request, Long userId) {
        String geoHash = null;
        if (request.getLatitude() != null && request.getLongitude() != null) {
            geoHash = GeoHashUtil.encode(request.getLatitude(), request.getLongitude(), 8);
        }

        LocalLifeSearchQuery query = LocalLifeSearchQuery.builder()
                .userId(userId)
                .keyword(request.getKeyword())
                .searchType(request.getSearchType())
                .longitude(request.getLongitude())
                .latitude(request.getLatitude())
                .geoHash(geoHash)
                .radius(request.getRadius())
                .cityCode(request.getCityCode())
                .sortBy(request.getSortBy())
                .source(request.getSource())
                .build();

        save(query);
        return query.getId();
    }

    private List<SearchResponse.SearchResultItem> performSearch(SearchRequest request) {
        // 模拟搜索结果
        List<SearchResponse.SearchResultItem> results = new ArrayList<>();
        
        for (int i = 1; i <= 50; i++) {
            results.add(SearchResponse.SearchResultItem.builder()
                    .poiId((long) i)
                    .name("商户" + i + (request.getKeyword() != null ? "-" + request.getKeyword() : ""))
                    .type("FOOD")
                    .typeName("美食")
                    .address("XX路" + i + "号")
                    .longitude(request.getLongitude() != null ? request.getLongitude() + Math.random() * 0.01 : 116.0)
                    .latitude(request.getLatitude() != null ? request.getLatitude() + Math.random() * 0.01 : 39.9)
                    .distance((int) (Math.random() * 5000))
                    .rating(3.5 + Math.random() * 1.5)
                    .avgPrice((int) (50 + Math.random() * 200))
                    .isOpen(Math.random() > 0.3)
                    .tags(Arrays.asList("推荐", "热门", "新店"))
                    .relevanceScore(Math.random())
                    .build());
        }

        // 排序
        switch (request.getSortBy()) {
            case "DISTANCE":
                results.sort(Comparator.comparing(SearchResponse.SearchResultItem::getDistance));
                break;
            case "RATING":
                results.sort(Comparator.comparing(SearchResponse.SearchResultItem::getRating).reversed());
                break;
            case "POPULAR":
            case "SMART":
            default:
                results.sort(Comparator.comparing(SearchResponse.SearchResultItem::getRelevanceScore).reversed());
                break;
        }

        return results;
    }

    private List<SearchResponse.SearchResultItem> paginateResults(List<SearchResponse.SearchResultItem> results, SearchRequest request) {
        int offset = request.getOffset();
        int end = Math.min(offset + request.getPageSize(), results.size());
        if (offset >= results.size()) {
            return Collections.emptyList();
        }
        return results.subList(offset, end);
    }

    private List<SearchResponse.SuggestFilter> generateSuggestFilters(List<SearchResponse.SearchResultItem> results) {
        List<SearchResponse.SuggestFilter> filters = new ArrayList<>();
        
        // 区域筛选
        filters.add(SearchResponse.SuggestFilter.builder()
                .filterType("TYPE")
                .filterValue("FOOD")
                .displayName("美食")
                .count((int) results.stream().filter(r -> "FOOD".equals(r.getType())).count())
                .build());
        
        filters.add(SearchResponse.SuggestFilter.builder()
                .filterType("RATING")
                .filterValue("4.0+")
                .displayName("4分以上")
                .count((int) results.stream().filter(r -> r.getRating() >= 4.0).count())
                .build());

        return filters;
    }

    private SearchResponse.LocationInfo buildLocationInfo(SearchRequest request) {
        return SearchResponse.LocationInfo.builder()
                .cityName("北京市")
                .districtName("朝阳区")
                .streetName("三里屯")
                .searchLongitude(request.getLongitude())
                .searchLatitude(request.getLatitude())
                .build();
    }

    private SearchResponse.SearchStats buildSearchStats(List<SearchResponse.SearchResultItem> results) {
        return SearchResponse.SearchStats.builder()
                .nearbyMerchantCount(results.size())
                .promotionMerchantCount((int) results.stream().filter(r -> r.getPromotionInfo() != null).count())
                .newMerchantCount(5)
                .openNowMerchantCount((int) results.stream().filter(SearchResponse.SearchResultItem::getIsOpen).count())
                .build();
    }
}
