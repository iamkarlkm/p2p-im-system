package com.im.backend.controller;

import com.im.backend.dto.SearchRequest;
import com.im.backend.dto.SearchResponse;
import com.im.backend.service.ElasticsearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Elasticsearch搜索控制器
 */
@RestController
@RequestMapping("/api/search")
public class ElasticsearchController {

    private final ElasticsearchService esService;

    public ElasticsearchController(ElasticsearchService esService) {
        this.esService = esService;
    }

    @PostMapping("/messages")
    public ResponseEntity<SearchResponse> searchMessages(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(esService.searchMessages(request));
    }

    @PostMapping("/users")
    public ResponseEntity<SearchResponse> searchUsers(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(esService.searchUsers(request));
    }

    @PostMapping("/universal")
    public ResponseEntity<SearchResponse> universalSearch(@RequestBody SearchRequest request) {
        String index = request.getIndex();
        if ("users".equals(index)) {
            return ResponseEntity.ok(esService.searchUsers(request));
        }
        return ResponseEntity.ok(esService.searchMessages(request));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getClusterHealth() {
        return ResponseEntity.ok(esService.getClusterHealth());
    }

    @GetMapping("/stats/{index}")
    public ResponseEntity<Map<String, Object>> getIndexStats(@PathVariable String index) {
        return ResponseEntity.ok(esService.getIndexStats(index));
    }

    @GetMapping("/suggest")
    public ResponseEntity<List<String>> getSuggestions(@RequestParam String q) {
        return ResponseEntity.ok(esService.expandSynonyms(q));
    }
}
