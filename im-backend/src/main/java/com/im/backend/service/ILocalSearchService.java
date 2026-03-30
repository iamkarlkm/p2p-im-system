package com.im.backend.service;

import com.im.backend.dto.SearchRequest;
import com.im.backend.dto.SearchResponse;
import com.im.backend.entity.LocalLifeSearchQuery;

import java.util.List;

/**
 * 本地生活搜索服务接口
 */
public interface ILocalSearchService {

    /**
     * 执行本地生活搜索
     *
     * @param request 搜索请求
     * @param userId  用户ID(可选)
     * @return 搜索结果
     */
    SearchResponse search(SearchRequest request, Long userId);

    /**
     * 获取搜索建议
     *
     * @param keyword  输入关键词
     * @param cityCode 城市编码
     * @return 建议列表
     */
    List<String> getSearchSuggestions(String keyword, String cityCode);

    /**
     * 获取热门搜索词
     *
     * @param cityCode 城市编码
     * @param limit    数量限制
     * @return 热门搜索词列表
     */
    List<String> getHotKeywords(String cityCode, Integer limit);

    /**
     * 获取用户搜索历史
     *
     * @param userId 用户ID
     * @param limit  数量限制
     * @return 搜索历史列表
     */
    List<String> getUserSearchHistory(Long userId, Integer limit);

    /**
     * 清除用户搜索历史
     *
     * @param userId 用户ID
     */
    void clearUserSearchHistory(Long userId);

    /**
     * 获取POI详情
     *
     * @param poiId POI ID
     * @return POI详情
     */
    SearchResponse.SearchResultItem getPoiDetail(Long poiId);

    /**
     * 记录搜索点击
     *
     * @param searchId 搜索记录ID
     * @param poiId    点击的POI ID
     */
    void recordSearchClick(Long searchId, Long poiId);

    /**
     * 获取附近推荐
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @param limit     数量限制
     * @return 推荐列表
     */
    List<SearchResponse.SearchResultItem> getNearbyRecommendations(Double longitude, Double latitude, Integer limit);
}
