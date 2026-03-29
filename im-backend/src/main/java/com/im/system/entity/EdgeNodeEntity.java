package com.im.system.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 边缘计算节点实体
 * 用于管理和存储边缘计算节点的状态和信息
 */
@Entity
@Table(name = "edge_nodes", indexes = {
    @Index(name = "idx_edge_node_status", columnList = "status"),
    @Index(name = "idx_edge_node_region", columnList = "region"),
    @Index(name = "idx_edge_node_capacity", columnList = "available_capacity")
})
public class EdgeNodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "node_name", nullable = false, length = 100)
    private String nodeName;

    @Column(name = "node_ip", nullable = false, length = 45)
    private String nodeIp;

    @Column(name = "node_port", nullable = false)
    private Integer nodePort;

    @Column(name = "region", nullable = false, length = 50)
    private String region;

    @Column(name = "zone", length = 50)
    private String zone;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // ONLINE, OFFLINE, MAINTENANCE, ERROR

    @Column(name = "node_type", nullable = false, length = 30)
    private String nodeType; // COMPUTE, CACHE, HYBRID, GATEWAY

    @Column(name = "compute_capacity")
    private Integer computeCapacity; // 计算能力（虚拟CPU核心数）

    @Column(name = "memory_capacity")
    private Long memoryCapacity; // 内存容量（字节）

    @Column(name = "storage_capacity")
    private Long storageCapacity; // 存储容量（字节）

    @Column(name = "network_bandwidth")
    private Long networkBandwidth; // 网络带宽（bps）

    @Column(name = "available_capacity")
    private Integer availableCapacity; // 可用容量百分比

    @Column(name = "current_load")
    private Double currentLoad; // 当前负载（0.0-1.0）

    @Column(name = "latency_ms")
    private Integer latencyMs; // 到中心服务器的延迟（毫秒）

    @Column(name = "last_heartbeat")
    private LocalDateTime lastHeartbeat;

    @Column(name = "heartbeat_interval")
    private Integer heartbeatInterval = 30; // 心跳间隔（秒）

    @Column(name = "version", length = 20)
    private String version;

    @Column(name = "supported_protocols", length = 200)
    private String supportedProtocols; // 逗号分隔的协议列表

    @Column(name = "capabilities", length = 500)
    private String capabilities; // JSON格式的能力描述

    @Column(name = "geo_latitude")
    private Double geoLatitude;

    @Column(name = "geo_longitude")
    private Double geoLongitude;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @Column(name = "security_level", length = 20)
    private String securityLevel; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(name = "encryption_enabled", nullable = false)
    private Boolean encryptionEnabled = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_sync_time")
    private LocalDateTime lastSyncTime;

    @Column(name = "sync_status", length = 20)
    private String syncStatus; // SYNCED, PENDING, FAILED

    @Column(name = "error_count")
    private Integer errorCount = 0;

    @Column(name = "last_error", length = 500)
    private String lastError;

    @Column(name = "health_score")
    private Double healthScore = 1.0; // 健康评分（0.0-1.0）

    @Column(name = "priority_level")
    private Integer priorityLevel = 5; // 优先级（1-10，1最高）

    @Column(name = "tags", length = 300)
    private String tags; // 逗号分隔的标签

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON格式的元数据

    // 构造函数
    public EdgeNodeEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public EdgeNodeEntity(String nodeName, String nodeIp, Integer nodePort, String region, String nodeType) {
        this();
        this.nodeName = nodeName;
        this.nodeIp = nodeIp;
        this.nodePort = nodePort;
        this.region = region;
        this.nodeType = nodeType;
        this.status = "ONLINE";
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }

    public String getNodeIp() { return nodeIp; }
    public void setNodeIp(String nodeIp) { this.nodeIp = nodeIp; }

    public Integer getNodePort() { return nodePort; }
    public void setNodePort(Integer nodePort) { this.nodePort = nodePort; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNodeType() { return nodeType; }
    public void setNodeType(String nodeType) { this.nodeType = nodeType; }

    public Integer getComputeCapacity() { return computeCapacity; }
    public void setComputeCapacity(Integer computeCapacity) { this.computeCapacity = computeCapacity; }

    public Long getMemoryCapacity() { return memoryCapacity; }
    public void setMemoryCapacity(Long memoryCapacity) { this.memoryCapacity = memoryCapacity; }

    public Long getStorageCapacity() { return storageCapacity; }
    public void setStorageCapacity(Long storageCapacity) { this.storageCapacity = storageCapacity; }

    public Long getNetworkBandwidth() { return networkBandwidth; }
    public void setNetworkBandwidth(Long networkBandwidth) { this.networkBandwidth = networkBandwidth; }

    public Integer getAvailableCapacity() { return availableCapacity; }
    public void setAvailableCapacity(Integer availableCapacity) { this.availableCapacity = availableCapacity; }

    public Double getCurrentLoad() { return currentLoad; }
    public void setCurrentLoad(Double currentLoad) { this.currentLoad = currentLoad; }

    public Integer getLatencyMs() { return latencyMs; }
    public void setLatencyMs(Integer latencyMs) { this.latencyMs = latencyMs; }

    public LocalDateTime getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(LocalDateTime lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }

    public Integer getHeartbeatInterval() { return heartbeatInterval; }
    public void setHeartbeatInterval(Integer heartbeatInterval) { this.heartbeatInterval = heartbeatInterval; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getSupportedProtocols() { return supportedProtocols; }
    public void setSupportedProtocols(String supportedProtocols) { this.supportedProtocols = supportedProtocols; }

    public String getCapabilities() { return capabilities; }
    public void setCapabilities(String capabilities) { this.capabilities = capabilities; }

    public Double getGeoLatitude() { return geoLatitude; }
    public void setGeoLatitude(Double geoLatitude) { this.geoLatitude = geoLatitude; }

    public Double getGeoLongitude() { return geoLongitude; }
    public void setGeoLongitude(Double geoLongitude) { this.geoLongitude = geoLongitude; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public String getSecurityLevel() { return securityLevel; }
    public void setSecurityLevel(String securityLevel) { this.securityLevel = securityLevel; }

    public Boolean getEncryptionEnabled() { return encryptionEnabled; }
    public void setEncryptionEnabled(Boolean encryptionEnabled) { this.encryptionEnabled = encryptionEnabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getLastSyncTime() { return lastSyncTime; }
    public void setLastSyncTime(LocalDateTime lastSyncTime) { this.lastSyncTime = lastSyncTime; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }

    public Integer getErrorCount() { return errorCount; }
    public void setErrorCount(Integer errorCount) { this.errorCount = errorCount; }

    public String getLastError() { return lastError; }
    public void setLastError(String lastError) { this.lastError = lastError; }

    public Double getHealthScore() { return healthScore; }
    public void setHealthScore(Double healthScore) { this.healthScore = healthScore; }

    public Integer getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(Integer priorityLevel) { this.priorityLevel = priorityLevel; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    // 辅助方法
    public boolean isOnline() {
        return "ONLINE".equals(status);
    }

    public boolean hasCapacity() {
        return availableCapacity != null && availableCapacity > 20;
    }

    public boolean isHealthy() {
        return healthScore != null && healthScore > 0.7;
    }

    public void updateHealthScore() {
        if (currentLoad != null && latencyMs != null && errorCount != null) {
            double loadScore = 1.0 - currentLoad; // 负载越低分数越高
            double latencyScore = latencyMs <= 100 ? 1.0 : 
                                 latencyMs <= 200 ? 0.8 : 
                                 latencyMs <= 500 ? 0.6 : 0.3;
            double errorScore = errorCount == 0 ? 1.0 : 
                               errorCount <= 3 ? 0.7 : 
                               errorCount <= 10 ? 0.4 : 0.1;
            
            this.healthScore = (loadScore * 0.4) + (latencyScore * 0.3) + (errorScore * 0.3);
        }
    }

    public void recordHeartbeat() {
        this.lastHeartbeat = LocalDateTime.now();
        this.errorCount = 0; // 心跳成功重置错误计数
        this.lastError = null;
        updateHealthScore();
    }

    public void recordError(String errorMessage) {
        this.errorCount++;
        this.lastError = errorMessage;
        this.healthScore = Math.max(0.0, healthScore - 0.1); // 每次错误降低健康分数
        if (errorCount > 10) {
            this.status = "ERROR";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("EdgeNode[id=%s, name=%s, region=%s, status=%s, load=%.2f, health=%.2f]", 
            id, nodeName, region, status, currentLoad != null ? currentLoad : 0.0, healthScore);
    }
}