package com.im.search.service.impl;

import com.im.search.entity.PoiSearchDocument;
import com.im.search.service.PoiIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.index.AliasAction;
import org.springframework.data.elasticsearch.core.index.AliasActionParameters;
import org.springframework.data.elasticsearch.core.index.AliasActions;
import org.springframework.data.elasticsearch.core.index.PutMappingRequest;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * POI索引服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PoiIndexServiceImpl implements PoiIndexService {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private static final String INDEX_NAME = "poi_search_v1";

    @Override
    public void save(PoiSearchDocument document) {
        elasticsearchTemplate.save(document);
    }

    @Override
    public void saveAll(List<PoiSearchDocument> documents) {
        elasticsearchTemplate.save(documents);
    }

    @Override
    public Optional<PoiSearchDocument> findById(String id) {
        PoiSearchDocument doc = elasticsearchTemplate.get(id, PoiSearchDocument.class);
        return Optional.ofNullable(doc);
    }

    @Override
    public void deleteById(String id) {
        elasticsearchTemplate.delete(id, PoiSearchDocument.class);
    }

    @Override
    public void deleteByMerchantId(Long merchantId) {
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("merchantId", merchantId));
        
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(query)
                .build();
        
        elasticsearchTemplate.delete(searchQuery, PoiSearchDocument.class, 
                IndexCoordinates.of(INDEX_NAME));
    }

    @Override
    public List<PoiSearchDocument> searchNearby(GeoPoint center, Integer radius, String keyword, Integer size) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        // 地理位置过滤
        boolQuery.filter(QueryBuilders.geoDistanceQuery("location")
                .point(center.getLat(), center.getLon())
                .distance(radius.toString() + "m"));
        
        // 关键词过滤
        if (keyword != null && !keyword.isEmpty()) {
            boolQuery.must(QueryBuilders.multiMatchQuery(keyword)
                    .field("name", 3.0f)
                    .field("tags", 2.0f)
                    .field("category", 1.0f));
        }
        
        // 状态过滤
        boolQuery.filter(QueryBuilders.termQuery("status", 1));
        
        // 距离排序
        GeoDistanceSortBuilder geoSort = SortBuilders.geoDistanceSort("location", center.getLat(), center.getLon())
                .order(SortOrder.ASC);
        
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withSort(geoSort)
                .withPageable(PageRequest.of(0, size != null ? size : 20))
                .build();
        
        SearchHits<PoiSearchDocument> searchHits = elasticsearchTemplate.search(
                searchQuery, PoiSearchDocument.class);
        
        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public List<PoiSearchDocument> fullTextSearch(String keyword, String city, Integer page, Integer size) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        // 全文搜索
        if (keyword != null && !keyword.isEmpty()) {
            boolQuery.must(QueryBuilders.multiMatchQuery(keyword)
                    .field("name", 3.0f)
                    .field("brandName", 2.0f)
                    .field("description", 1.5f)
                    .field("tags", 2.0f)
                    .field("address", 1.0f)
                    .type(org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.BEST_FIELDS));
        }
        
        // 城市过滤
        if (city != null && !city.isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("city", city));
        }
        
        // 状态过滤
        boolQuery.filter(QueryBuilders.termQuery("status", 1));
        
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .withPageable(PageRequest.of(page != null ? page : 0, size != null ? size : 20))
                .build();
        
        SearchHits<PoiSearchDocument> searchHits = elasticsearchTemplate.search(
                searchQuery, PoiSearchDocument.class);
        
        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public List<PoiSearchDocument> searchByCategory(String category, GeoPoint center, Integer radius, 
                                                     Integer page, Integer size) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        // 分类过滤
        boolQuery.must(QueryBuilders.termQuery("category", category));
        
        // 地理位置过滤
        if (center != null && radius != null) {
            boolQuery.filter(QueryBuilders.geoDistanceQuery("location")
                    .point(center.getLat(), center.getLon())
                    .distance(radius.toString() + "m"));
        }
        
        // 状态过滤
        boolQuery.filter(QueryBuilders.termQuery("status", 1));
        
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(page != null ? page : 0, size != null ? size : 20));
        
        // 如果有地理位置，按距离排序
        if (center != null) {
            GeoDistanceSortBuilder geoSort = SortBuilders.geoDistanceSort("location", center.getLat(), center.getLon())
                    .order(SortOrder.ASC);
            queryBuilder.withSort(geoSort);
        } else {
            queryBuilder.withSort(SortBuilders.fieldSort("hotScore").order(SortOrder.DESC));
        }
        
        SearchHits<PoiSearchDocument> searchHits = elasticsearchTemplate.search(
                queryBuilder.build(), PoiSearchDocument.class);
        
        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public List<PoiSearchDocument> intelligentSearch(String keyword, GeoPoint center, Integer radius, 
                                                      String sortBy, Integer page, Integer size) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            boolQuery.must(QueryBuilders.multiMatchQuery(keyword)
                    .field("name", 3.0f)
                    .field("brandName", 2.0f)
                    .field("tags", 2.0f)
                    .field("description", 1.0f));
        }
        
        // 地理位置过滤
        if (center != null && radius != null) {
            boolQuery.filter(QueryBuilders.geoDistanceQuery("location")
                    .point(center.getLat(), center.getLon())
                    .distance(radius.toString() + "m"));
        }
        
        // 状态过滤
        boolQuery.filter(QueryBuilders.termQuery("status", 1));
        
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(page != null ? page : 0, size != null ? size : 20));
        
        // 根据排序字段选择排序方式
        if ("distance".equals(sortBy) && center != null) {
            GeoDistanceSortBuilder geoSort = SortBuilders.geoDistanceSort("location", center.getLat(), center.getLon())
                    .order(SortOrder.ASC);
            queryBuilder.withSort(geoSort);
        } else if ("rating".equals(sortBy)) {
            queryBuilder.withSort(SortBuilders.fieldSort("rating").order(SortOrder.DESC));
        } else if ("price".equals(sortBy)) {
            queryBuilder.withSort(SortBuilders.fieldSort("avgPrice").order(SortOrder.ASC));
        } else {
            // 默认按热度排序
            queryBuilder.withSort(SortBuilders.fieldSort("hotScore").order(SortOrder.DESC));
        }
        
        SearchHits<PoiSearchDocument> searchHits = elasticsearchTemplate.search(
                queryBuilder.build(), PoiSearchDocument.class);
        
        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public void rebuildIndex() {
        // 删除旧索引
        if (indexExists()) {
            elasticsearchTemplate.indexOps(IndexCoordinates.of(INDEX_NAME)).delete();
        }
        // 创建新索引
        createIndex();
    }

    @Override
    public boolean indexExists() {
        return elasticsearchTemplate.indexOps(IndexCoordinates.of(INDEX_NAME)).exists();
    }

    @Override
    public void createIndex() {
        elasticsearchTemplate.indexOps(IndexCoordinates.of(INDEX_NAME)).create();
    }
}
