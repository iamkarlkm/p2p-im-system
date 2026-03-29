package com.im.backend.modules.local.search.controller;

import com.im.backend.common.Result;
import com.im.backend.modules.local.search.dto.*;
import com.im.backend.modules.local.search.service.ISmartSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 智能搜索控制器
 */
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Api(tags = "智能搜索")
public class SmartSearchController {

    private final ISmartSearchService smartSearchService;

    /**
     * 语义搜索
     */
    @PostMapping("/semantic")
    @ApiOperation("语义搜索")
    public Result<SemanticSearchResponse> semanticSearch(@RequestBody @Validated SemanticSearchRequest request) {
        return Result.success(smartSearchService.semanticSearch(request));
    }

    /**
     * 自然语言理解解析
     */
    @PostMapping("/nlu/parse")
    @ApiOperation("自然语言理解解析")
    public Result<NluParseResponse> parseNaturalLanguage(@RequestBody @Validated NluParseRequest request) {
        return Result.success(smartSearchService.parseNaturalLanguage(request));
    }

    /**
     * 搜索建议
     */
    @GetMapping("/suggestions")
    @ApiOperation("搜索建议")
    public Result<List<String>> getSearchSuggestions(@Validated SearchSuggestionRequest request) {
        return Result.success(smartSearchService.getSearchSuggestions(request));
    }

    /**
     * 热门搜索词
     */
    @GetMapping("/hot-keywords")
    @ApiOperation("热门搜索词")
    public Result<HotKeywordResponse> getHotKeywords(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "20") Integer limit) {
        return Result.success(smartSearchService.getHotKeywords(category, limit));
    }

    /**
     * 知识图谱查询
     */
    @GetMapping("/knowledge-graph/{entityName}")
    @ApiOperation("知识图谱查询")
    public Result<KnowledgeGraphResponse> queryKnowledgeGraph(@PathVariable String entityName) {
        KnowledgeGraphResponse response = smartSearchService.queryKnowledgeGraph(entityName);
        return response != null ? Result.success(response) : Result.error("实体不存在");
    }

    /**
     * 搜索纠错
     */
    @PostMapping("/correct")
    @ApiOperation("搜索纠错")
    public Result<String> correctSearchQuery(@RequestParam String query) {
        return Result.success(smartSearchService.correctSearchQuery(query));
    }

    /**
     * 同义词扩展
     */
    @GetMapping("/synonyms")
    @ApiOperation("同义词扩展")
    public Result<List<String>> expandSynonyms(@RequestParam String keyword) {
        return Result.success(smartSearchService.expandSynonyms(keyword));
    }
}
