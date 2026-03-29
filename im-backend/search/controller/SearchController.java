package com.im.search.controller;

import com.im.common.response.ApiResponse;
import com.im.search.dto.NaturalLanguageSearchRequestDTO;
import com.im.search.dto.SearchIntentDTO;
import com.im.search.dto.SearchResponseDTO;
import com.im.search.service.IntelligentSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 智能搜索控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Api(tags = "智能搜索服务")
public class SearchController {

    private final IntelligentSearchService searchService;

    /**
     * 自然语言搜索
     */
    @PostMapping("/nlp")
    @ApiOperation("自然语言搜索 - 支持口语化查询")
    public ApiResponse<SearchResponseDTO> naturalLanguageSearch(
            @Valid @RequestBody NaturalLanguageSearchRequestDTO request) {
        log.info("Natural language search: {}", request.getQuery());
        SearchResponseDTO result = searchService.naturalLanguageSearch(request);
        return ApiResponse.success(result);
    }

    /**
     * 语义搜索
     */
    @GetMapping("/semantic")
    @ApiOperation("语义搜索")
    public ApiResponse<SearchResponseDTO> semanticSearch(
            @RequestParam String query,
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam(defaultValue = "5000") Integer radius) {
        log.info("Semantic search: {} at [{}, {}]", query, longitude, latitude);
        SearchResponseDTO result = searchService.semanticSearch(query, longitude, latitude, radius);
        return ApiResponse.success(result);
    }

    /**
     * 附近搜索
     */
    @GetMapping("/nearby")
    @ApiOperation("附近POI搜索")
    public ApiResponse<SearchResponseDTO> nearbySearch(
            @RequestParam String keyword,
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam(defaultValue = "5000") Integer radius,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        log.info("Nearby search: {} at [{}, {}]", keyword, longitude, latitude);
        NaturalLanguageSearchRequestDTO request = NaturalLanguageSearchRequestDTO.builder()
                .query(keyword)
                .longitude(longitude)
                .latitude(latitude)
                .radius(radius)
                .page(page)
                .size(size)
                .build();
        SearchResponseDTO result = searchService.naturalLanguageSearch(request);
        return ApiResponse.success(result);
    }

    /**
     * 解析搜索意图
     */
    @PostMapping("/intent")
    @ApiOperation("解析搜索意图")
    public ApiResponse<SearchIntentDTO> parseIntent(
            @RequestParam String query,
            @RequestParam(required = false) String sessionId) {
        log.info("Parse intent: {}", query);
        SearchIntentDTO result = searchService.parseSearchIntent(query, sessionId);
        return ApiResponse.success(result);
    }

    /**
     * 获取搜索建议
     */
    @GetMapping("/suggestions")
    @ApiOperation("获取搜索建议")
    public ApiResponse<List<String>> getSuggestions(
            @RequestParam String keyword,
            @RequestParam(required = false) String cityCode) {
        List<String> suggestions = searchService.getSearchSuggestions(keyword, cityCode);
        return ApiResponse.success(suggestions);
    }

    /**
     * 获取热门搜索
     */
    @GetMapping("/hot")
    @ApiOperation("获取热门搜索")
    public ApiResponse<List<String>> getHotSearches(
            @RequestParam(required = false) String cityCode) {
        List<String> hotSearches = searchService.getHotSearches(cityCode);
        return ApiResponse.success(hotSearches);
    }

    /**
     * 多轮对话搜索
     */
    @PostMapping("/dialog")
    @ApiOperation("多轮对话搜索")
    public ApiResponse<SearchResponseDTO> dialogSearch(
            @RequestParam String query,
            @RequestParam(required = false) String sessionId,
            @RequestParam(required = false) Long userId) {
        log.info("Dialog search: {}, session: {}", query, sessionId);
        SearchResponseDTO result = searchService.dialogSearch(query, sessionId, userId);
        return ApiResponse.success(result);
    }

    /**
     * 智能问答
     */
    @GetMapping("/qa")
    @ApiOperation("智能问答")
    public ApiResponse<String> intelligentQA(
            @RequestParam String question,
            @RequestParam(required = false) String poiId) {
        String answer = searchService.intelligentQA(question, poiId);
        return ApiResponse.success(answer);
    }
}
