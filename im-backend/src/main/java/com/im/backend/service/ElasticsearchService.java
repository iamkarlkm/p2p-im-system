package com.im.backend.service;

import com.im.backend.dto.SearchRequest;
import com.im.backend.dto.SearchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Elasticsearch搜索服务
 * 模拟实现，实际生产环境需要连接真实的ES集群
 */
@Service
public class ElasticsearchService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 搜索消息
     */
    public SearchResponse searchMessages(SearchRequest request) {
        SearchResponse response = new SearchResponse();
        response.setQuery(request.getQuery());
        response.setIndex("messages");
        response.setFrom(request.getFrom() != null ? request.getFrom() : 0);
        response.setSize(request.getSize() != null ? Math.min(request.getSize(), 100) : 20);
        response.setTookMs((long) (Math.random() * 100) + 5);
        response.setTimedOut(false);

        List<SearchResponse.SearchHit> hits = new ArrayList<>();

        // 模拟搜索结果
        String query = request.getQuery();
        if (query != null && !query.trim().isEmpty()) {
            for (int i = 0; i < Math.min(response.getSize(), 5); i++) {
                SearchResponse.SearchHit hit = new SearchResponse.SearchHit();
                hit.setId("msg_" + UUID.randomUUID().toString().substring(0, 8));
                hit.setScore((double) (100 - i * 10));
                hit.setRank(i + 1);

                Map<String, Object> source = new HashMap<>();
                source.put("messageId", "msg_" + i);
                source.put("conversationId", request.getConversationId() != null ? request.getConversationId() : "conv_123");
                source.put("senderId", 1000L + i);
                source.put("content", query + " 相关消息内容示例 " + i);
                source.put("type", request.getType() != null ? request.getType() : "TEXT");
                source.put("timestamp", System.currentTimeMillis() - i * 3600000);
                source.put("attachmentCount", 0);
                hit.setSource(source);

                Map<String, List<String>> highlight = new HashMap<>();
                highlight.put("content", Collections.singletonList("<em>" + query + "</em> 相关消息内容"));
                hit.setHighlight(highlight);

                hits.add(hit);
            }
        }

        response.setHits(hits);
        response.setTotalHits((long) hits.size());
        response.setMaxScore(hits.isEmpty() ? 0.0 : hits.get(0).getScore());

        // 聚合
        Map<String, Object> aggs = new HashMap<>();
        aggs.put("by_type", Map.of("TEXT", 10L, "IMAGE", 5L, "FILE", 3L));
        aggs.put("by_sender", Map.of("count", 18L));
        response.setAggregations(aggs);

        // 建议
        if (query != null && query.length() > 2) {
            response.setSuggestions(Arrays.asList(query + "相关", query + "查询"));
        }

        return response;
    }

    /**
     * 索引消息文档
     */
    public void indexMessage(Map<String, Object> message) {
        // 模拟索引操作
    }

    /**
     * 批量索引
     */
    public void bulkIndex(List<Map<String, Object>> documents) {
        // 模拟批量索引
    }

    /**
     * 删除文档
     */
    public void deleteDocument(String index, String docId) {
        // 模拟删除
    }

    /**
     * 搜索用户
     */
    public SearchResponse searchUsers(SearchRequest request) {
        SearchResponse response = new SearchResponse();
        response.setQuery(request.getQuery());
        response.setIndex("users");
        response.setFrom(0);
        response.setSize(request.getSize() != null ? Math.min(request.getSize(), 50) : 20);
        response.setTookMs(15L);
        response.setTimedOut(false);

        List<SearchResponse.SearchHit> hits = new ArrayList<>();
        String query = request.getQuery();

        if (query != null && query.length() >= 2) {
            for (int i = 0; i < 3; i++) {
                SearchResponse.SearchHit hit = new SearchResponse.SearchHit();
                hit.setId("user_" + (1000 + i));
                hit.setScore(95.0 - i * 10);
                hit.setRank(i + 1);

                Map<String, Object> source = new HashMap<>();
                source.put("userId", 1000L + i);
                source.put("username", query + "_user" + i);
                source.put("nickname", query + " 用户" + i);
                source.put("avatar", "https://cdn.example.com/avatar/" + i + ".jpg");
                source.put("status", "ONLINE");
                hit.setSource(source);

                Map<String, List<String>> highlight = new HashMap<>();
                highlight.put("username", Collections.singletonList("<em>" + query + "</em>_user" + i));
                hit.setHighlight(highlight);

                hits.add(hit);
            }
        }

        response.setHits(hits);
        response.setTotalHits((long) hits.size());
        response.setMaxScore(hits.isEmpty() ? 0.0 : hits.get(0).getScore());
        return response;
    }

    /**
     * 获取索引健康状态
     */
    public Map<String, Object> getClusterHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "green");
        health.put("cluster_name", "im-cluster");
        health.put("number_of_nodes", 3);
        health.put("active_shards", 15);
        health.put("active_primary_shards", 8);
        return health;
    }

    /**
     * 获取索引统计
     */
    public Map<String, Object> getIndexStats(String index) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("index", index);
        stats.put("doc_count", 1_000_000L);
        stats.put("size_bytes", 500_000_000_000L);
        stats.put("size_human", "465GB");
        stats.put("shards", 3);
        return stats;
    }

    /**
     * 高亮文本片段
     */
    public List<String> highlightText(String text, String query, String preTag, String postTag) {
        if (preTag == null) preTag = "<em>";
        if (postTag == null) postTag = "</em>";
        return Collections.singletonList(text.replace(query, preTag + query + postTag));
    }

    /**
     * 同义词扩展
     */
    public List<String> expandSynonyms(String term) {
        Map<String, List<String>> synonymMap = new HashMap<>();
        synonymMap.put("hello", Arrays.asList("hi", "hey", "greetings"));
        synonymMap.put("图片", Arrays.asList("图片", "photo", "image"));
        return synonymMap.getOrDefault(term.toLowerCase(), Collections.emptyList());
    }
}
