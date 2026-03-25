package com.im.backend.dto;

import java.util.*;

/**
 * 搜索响应DTO
 */
public class SearchResponse {

    private String query;
    private String index;
    private Long totalHits;
    private Double maxScore;
    private Long tookMs;
    private Integer from;
    private Integer size;
    private List<SearchHit> hits;
    private Map<String, Object> aggregations;
    private List<String> suggestions;
    private Boolean timedOut;

    public SearchResponse() {
        hits = new ArrayList<>();
    }

    // Inner class for search hits
    public static class SearchHit {
        private String id;
        private Double score;
        private Map<String, Object> source;
        private Map<String, List<String>> highlight;
        private Integer rank;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }

        public Map<String, Object> getSource() { return source; }
        public void setSource(Map<String, Object> source) { this.source = source; }

        public Map<String, List<String>> getHighlight() { return highlight; }
        public void setHighlight(Map<String, List<String>> highlight) { this.highlight = highlight; }

        public Integer getRank() { return rank; }
        public void setRank(Integer rank) { this.rank = rank; }
    }

    // Getters and Setters
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getIndex() { return index; }
    public void setIndex(String index) { this.index = index; }

    public Long getTotalHits() { return totalHits; }
    public void setTotalHits(Long totalHits) { this.totalHits = totalHits; }

    public Double getMaxScore() { return maxScore; }
    public void setMaxScore(Double maxScore) { this.maxScore = maxScore; }

    public Long getTookMs() { return tookMs; }
    public void setTookMs(Long tookMs) { this.tookMs = tookMs; }

    public Integer getFrom() { return from; }
    public void setFrom(Integer from) { this.from = from; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public List<SearchHit> getHits() { return hits; }
    public void setHits(List<SearchHit> hits) { this.hits = hits; }

    public Map<String, Object> getAggregations() { return aggregations; }
    public void setAggregations(Map<String, Object> aggregations) { this.aggregations = aggregations; }

    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }

    public Boolean getTimedOut() { return timedOut; }
    public void setTimedOut(Boolean timedOut) { this.timedOut = timedOut; }
}
