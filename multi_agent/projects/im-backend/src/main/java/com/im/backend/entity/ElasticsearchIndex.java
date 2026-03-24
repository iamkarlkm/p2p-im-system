package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ES索引配置实体
 */
@Entity
@Table(name = "es_index_configs")
public class ElasticsearchIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String indexName;

    @Column(nullable = false)
    private String alias;

    @Column(nullable = false)
    private String documentType; // MESSAGE, USER, GROUP, FILE

    private Integer shardCount;

    private Integer replicaCount;

    private String analyzer; // STANDARD, IK_MAX_WORD, PINYIN

    private Boolean enableHighlight;

    private Boolean enableFuzzy;

    private Integer maxResultWindow;

    private String fields; // JSON: field name -> field type mapping

    private String mappings; // JSON: full ES mappings

    private String settings; // JSON: full ES settings

    private String hosts; // comma-separated ES hosts

    private String username;

    private String password;

    private String apiKey;

    private Boolean enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (enabled == null) enabled = true;
        if (shardCount == null) shardCount = 3;
        if (replicaCount == null) replicaCount = 1;
        if (maxResultWindow == null) maxResultWindow = 10000;
        if (enableHighlight == null) enableHighlight = true;
        if (enableFuzzy == null) enableFuzzy = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIndexName() { return indexName; }
    public void setIndexName(String indexName) { this.indexName = indexName; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public Integer getShardCount() { return shardCount; }
    public void setShardCount(Integer shardCount) { this.shardCount = shardCount; }

    public Integer getReplicaCount() { return replicaCount; }
    public void setReplicaCount(Integer replicaCount) { this.replicaCount = replicaCount; }

    public String getAnalyzer() { return analyzer; }
    public void setAnalyzer(String analyzer) { this.analyzer = analyzer; }

    public Boolean getEnableHighlight() { return enableHighlight; }
    public void setEnableHighlight(Boolean enableHighlight) { this.enableHighlight = enableHighlight; }

    public Boolean getEnableFuzzy() { return enableFuzzy; }
    public void setEnableFuzzy(Boolean enableFuzzy) { this.enableFuzzy = enableFuzzy; }

    public Integer getMaxResultWindow() { return maxResultWindow; }
    public void setMaxResultWindow(Integer maxResultWindow) { this.maxResultWindow = maxResultWindow; }

    public String getFields() { return fields; }
    public void setFields(String fields) { this.fields = fields; }

    public String getMappings() { return mappings; }
    public void setMappings(String mappings) { this.mappings = mappings; }

    public String getSettings() { return settings; }
    public void setSettings(String settings) { this.settings = settings; }

    public String getHosts() { return hosts; }
    public void setHosts(String hosts) { this.hosts = hosts; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
