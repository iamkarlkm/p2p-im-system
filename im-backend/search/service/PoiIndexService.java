package com.im.search.service;

import com.im.search.entity.PoiSearchDocument;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.List;
import java.util.Optional;

/**
 * POI搜索索引服务接口
 */
public interface PoiIndexService {

    /**
     * 保存POI文档到索引
     */
    void save(PoiSearchDocument document);

    /**
     * 批量保存
     */
    void saveAll(List<PoiSearchDocument> documents);

    /**
     * 根据ID查询
     */
    Optional<PoiSearchDocument> findById(String id);

    /**
     * 删除索引
     */
    void deleteById(String id);

    /**
     * 根据商户ID删除
     */
    void deleteByMerchantId(Long merchantId);

    /**
     * 附近搜索
     */
    List<PoiSearchDocument> searchNearby(GeoPoint center, Integer radius, String keyword, Integer size);

    /**
     * 全文搜索
     */
    List<PoiSearchDocument> fullTextSearch(String keyword, String city, Integer page, Integer size);

    /**
     * 分类搜索
     */
    List<PoiSearchDocument> searchByCategory(String category, GeoPoint center, Integer radius, Integer page, Integer size);

    /**
     * 智能搜索（综合排序）
     */
    List<PoiSearchDocument> intelligentSearch(String keyword, GeoPoint center, Integer radius, 
                                              String sortBy, Integer page, Integer size);

    /**
     * 重建索引
     */
    void rebuildIndex();

    /**
     * 索引是否存在
     */
    boolean indexExists();

    /**
     * 创建索引
     */
    void createIndex();
}
