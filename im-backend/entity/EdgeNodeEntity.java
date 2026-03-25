package com.im.system.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 边缘节点实体
 * 用于管理和监控边缘计算节点的状态、资源和性能
 */
@Entity
@Table(name = "edge_nodes")
public class EdgeNodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "node_id", nullable = false, unique = true)
    private String nodeId;

    @Column(name = "node_name", nullable = false)
    private String nodeName;

    @Column(name = "node_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NodeType nodeType;

    @Column(name = "geographic_location")
    private String geographicLocation;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "port", nullable = false)
    private Integer port = 8080;

    @Column(name = "api_endpoint")
    private String apiEndpoint;

    @Column(name = "health_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private HealthStatus healthStatus = HealthStatus.HEALTHY;

    @Column(name = "connection_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ConnectionStatus connectionStatus = ConnectionStatus.OFFLINE;

    @Column(name = "last_heartbeat")
    private LocalDateTime lastHeartbeat;

    @Column(name = "cpu_cores")
    private Integer cpuCores;

    @Column(name = "cpu_usage_percent")
    private Double cpuUsagePercent;

    @Column(name = "total_memory_mb")
    private Long totalMemoryMb;

    @Column(name = "used_memory_mb")
    private Long usedMemoryMb;

    @Column(name = "total_disk_gb")
    private Long totalDiskGb;

    @Column(name = "used_disk_gb")
    private Long usedDiskGb;

    @Column(name = "network_bandwidth_mbps")
    private Double networkBandwidthMbps;

    @Column(name = "network_latency_ms")
    private Integer networkLatencyMs;

    @Column(name = "gpu_available")
    private Boolean gpuAvailable = false;

    @Column(name = "gpu_type")
    private String gpuType;

    @Column(name = "gpu_memory_gb")
    private Integer gpuMemoryGb;

    @Column(name = "supported_video_codecs")
    private String supportedVideoCodecs;

    @Column(name = "supported_audio_codecs")
    private String supportedAudioCodecs;

    @Column(name = "max_concurrent_sessions")
    private Integer maxConcurrentSessions = 100;

    @Column(name = "current_sessions")
    private Integer currentSessions = 0;

    @Column(name = "video_processing_capacity")
    private Integer videoProcessingCapacity;

    @Column(name = "audio_processing_capacity")
    private Integer audioProcessingCapacity;

    @Column(name = "ai_acceleration_supported")
    private Boolean aiAccelerationSupported = false;

    @Column(name = "ai_model_types")
    private String aiModelTypes;

    @Column(name = "bandwidth_optimization_supported")
    private Boolean bandwidthOptimizationSupported = true;

    @Column(name = "real_time_transcoding_supported")
    private Boolean realTimeTranscodingSupported = true;

    @Column(name = "security_level")
    @Enumerated(EnumType.STRING)
    private SecurityLevel securityLevel = SecurityLevel.STANDARD;

    @Column(name = "ssl_enabled")
    private Boolean sslEnabled = true;

    @Column(name = "certificate_expiry")
    private LocalDateTime certificateExpiry;

    @Column(name = "maintenance_mode")
    private Boolean maintenanceMode = false;

    @Column(name = "scheduled_maintenance_start")
    private LocalDateTime scheduledMaintenanceStart;

    @Column(name = "scheduled_maintenance_end")
    private LocalDateTime scheduledMaintenanceEnd;

    @Column(name = "software_version")
    private String softwareVersion;

    @Column(name = "last_software_update")
    private LocalDateTime lastSoftwareUpdate;

    @Column(name = "tags")
    private String tags;

    @Column(name = "metadata_json")
    @Lob
    private String metadataJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_performance_report")
    private LocalDateTime lastPerformanceReport;

    // 构造函数
    public EdgeNodeEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public EdgeNodeEntity(String nodeId, String nodeName, NodeType nodeType, String ipAddress) {
        this();
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.ipAddress = ipAddress;
    }

    // 枚举类型定义
    public enum NodeType {
        CLOUD_EDGE,
        REGIONAL_EDGE,
        LOCAL_EDGE,
        MOBILE_EDGE,
        IOT_EDGE,
        HYBRID_EDGE,
        FOG_COMPUTING
    }

    public enum HealthStatus {
        HEALTHY,
        WARNING,
        CRITICAL,
        DEGRADED,
        UNKNOWN
    }

    public enum ConnectionStatus {
        ONLINE,
        OFFLINE,
        CONNECTING,
        DISCONNECTED,
        UNREACHABLE
    }

    public enum SecurityLevel {
        MINIMAL,
        STANDARD,
        ENHANCED,
        HIGH,
        MISSION_CRITICAL
    }

    // Getter 和 Setter 方法
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public String getGeographicLocation() {
        return geographicLocation;
    }

    public void setGeographicLocation(String geographicLocation) {
        this.geographicLocation = geographicLocation;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public HealthStatus getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(HealthStatus healthStatus) {
        this.healthStatus = healthStatus;
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public LocalDateTime getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(LocalDateTime lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public Integer getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(Integer cpuCores) {
        this.cpuCores = cpuCores;
    }

    public Double getCpuUsagePercent() {
        return cpuUsagePercent;
    }

    public void setCpuUsagePercent(Double cpuUsagePercent) {
        this.cpuUsagePercent = cpuUsagePercent;
    }

    public Long getTotalMemoryMb() {
        return totalMemoryMb;
    }

    public void setTotalMemoryMb(Long totalMemoryMb) {
        this.totalMemoryMb = totalMemoryMb;
    }

    public Long getUsedMemoryMb() {
        return usedMemoryMb;
    }

    public void setUsedMemoryMb(Long usedMemoryMb) {
        this.usedMemoryMb = usedMemoryMb;
    }

    public Long getTotalDiskGb() {
        return totalDiskGb;
    }

    public void setTotalDiskGb(Long totalDiskGb) {
        this.totalDiskGb = totalDiskGb;
    }

    public Long getUsedDiskGb() {
        return usedDiskGb;
    }

    public void setUsedDiskGb(Long usedDiskGb) {
        this.usedDiskGb = usedDiskGb;
    }

    public Double getNetworkBandwidthMbps() {
        return networkBandwidthMbps;
    }

    public void setNetworkBandwidthMbps(Double networkBandwidthMbps) {
        this.networkBandwidthMbps = networkBandwidthMbps;
    }

    public Integer getNetworkLatencyMs() {
        return networkLatencyMs;
    }

    public void setNetworkLatencyMs(Integer networkLatencyMs) {
        this.networkLatencyMs = networkLatencyMs;
    }

    public Boolean getGpuAvailable() {
        return gpuAvailable;
    }

    public void setGpuAvailable(Boolean gpuAvailable) {
        this.gpuAvailable = gpuAvailable;
    }

    public String getGpuType() {
        return gpuType;
    }

    public void setGpuType(String gpuType) {
        this.gpuType = gpuType;
    }

    public Integer getGpuMemoryGb() {
        return gpuMemoryGb;
    }

    public void setGpuMemoryGb(Integer gpuMemoryGb) {
        this.gpuMemoryGb = gpuMemoryGb;
    }

    public String getSupportedVideoCodecs() {
        return supportedVideoCodecs;
    }

    public void setSupportedVideoCodecs(String supportedVideoCodecs) {
        this.supportedVideoCodecs = supportedVideoCodecs;
    }

    public String getSupportedAudioCodecs() {
        return supportedAudioCodecs;
    }

    public void setSupportedAudioCodecs(String supportedAudioCodecs) {
        this.supportedAudioCodecs = supportedAudioCodecs;
    }

    public Integer getMaxConcurrentSessions() {
        return maxConcurrentSessions;
    }

    public void setMaxConcurrentSessions(Integer maxConcurrentSessions) {
        this.maxConcurrentSessions = maxConcurrentSessions;
    }

    public Integer getCurrentSessions() {
        return currentSessions;
    }

    public void setCurrentSessions(Integer currentSessions) {
        this.currentSessions = currentSessions;
    }

    public Integer getVideoProcessingCapacity() {
        return videoProcessingCapacity;
    }

    public void setVideoProcessingCapacity(Integer videoProcessingCapacity) {
        this.videoProcessingCapacity = videoProcessingCapacity;
    }

    public Integer getAudioProcessingCapacity() {
        return audioProcessingCapacity;
    }

    public void setAudioProcessingCapacity(Integer audioProcessingCapacity) {
        this.audioProcessingCapacity = audioProcessingCapacity;
    }

    public Boolean getAiAccelerationSupported() {
        return aiAccelerationSupported;
    }

    public void setAiAccelerationSupported(Boolean aiAccelerationSupported) {
        this.aiAccelerationSupported = aiAccelerationSupported;
    }

    public String getAiModelTypes() {
        return aiModelTypes;
    }

    public void setAiModelTypes(String aiModelTypes) {
        this.aiModelTypes = aiModelTypes;
    }

    public Boolean getBandwidthOptimizationSupported() {
        return bandwidthOptimizationSupported;
    }

    public void setBandwidthOptimizationSupported(Boolean bandwidthOptimizationSupported) {
        this.bandwidthOptimizationSupported = bandwidthOptimizationSupported;
    }

    public Boolean getRealTimeTranscodingSupported() {
        return realTimeTranscodingSupported;
    }

    public void setRealTimeTranscodingSupported(Boolean realTimeTranscodingSupported) {
        this.realTimeTranscodingSupported = realTimeTranscodingSupported;
    }

    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }

    public Boolean getSslEnabled() {
        return sslEnabled;
    }

    public void setSslEnabled(Boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    public LocalDateTime getCertificateExpiry() {
        return certificateExpiry;
    }

    public void setCertificateExpiry(LocalDateTime certificateExpiry) {
        this.certificateExpiry = certificateExpiry;
    }

    public Boolean getMaintenanceMode() {
        return maintenanceMode;
    }

    public void setMaintenanceMode(Boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
    }

    public LocalDateTime getScheduledMaintenanceStart() {
        return scheduledMaintenanceStart;
    }

    public void setScheduledMaintenanceStart(LocalDateTime scheduledMaintenanceStart) {
        this.scheduledMaintenanceStart = scheduledMaintenanceStart;
    }

    public LocalDateTime getScheduledMaintenanceEnd() {
        return scheduledMaintenanceEnd;
    }

    public void setScheduledMaintenanceEnd(LocalDateTime scheduledMaintenanceEnd) {
        this.scheduledMaintenanceEnd = scheduledMaintenanceEnd;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public LocalDateTime getLastSoftwareUpdate() {
        return lastSoftwareUpdate;
    }

    public void setLastSoftwareUpdate(LocalDateTime lastSoftwareUpdate) {
        this.lastSoftwareUpdate = lastSoftwareUpdate;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getLastPerformanceReport() {
        return lastPerformanceReport;
    }

    public void setLastPerformanceReport(LocalDateTime lastPerformanceReport) {
        this.lastPerformanceReport = lastPerformanceReport;
    }

    // 业务方法
    public void updateHeartbeat() {
        this.lastHeartbeat = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markOnline() {
        this.connectionStatus = ConnectionStatus.ONLINE;
        this.updatedAt = LocalDateTime.now();
    }

    public void markOffline() {
        this.connectionStatus = ConnectionStatus.OFFLINE;
        this.updatedAt = LocalDateTime.now();
    }

    public void startMaintenance(LocalDateTime startTime, LocalDateTime endTime) {
        this.maintenanceMode = true;
        this.scheduledMaintenanceStart = startTime;
        this.scheduledMaintenanceEnd = endTime;
        this.updatedAt = LocalDateTime.now();
    }

    public void endMaintenance() {
        this.maintenanceMode = false;
        this.scheduledMaintenanceStart = null;
        this.scheduledMaintenanceEnd = null;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAvailableForProcessing() {
        return connectionStatus == ConnectionStatus.ONLINE 
                && !maintenanceMode 
                && healthStatus != HealthStatus.CRITICAL
                && currentSessions < maxConcurrentSessions;
    }

    public double getAvailableCapacityPercentage() {
        if (maxConcurrentSessions == null || maxConcurrentSessions == 0) {
            return 0.0;
        }
        return ((double) (maxConcurrentSessions - currentSessions) / maxConcurrentSessions) * 100.0;
    }

    public void incrementSessionCount() {
        if (this.currentSessions == null) {
            this.currentSessions = 0;
        }
        this.currentSessions++;
        this.updatedAt = LocalDateTime.now();
    }

    public void decrementSessionCount() {
        if (this.currentSessions == null || this.currentSessions <= 0) {
            this.currentSessions = 0;
        } else {
            this.currentSessions--;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void updateResourceMetrics(Double cpuUsage, Long usedMemory, Long usedDisk, 
                                     Double networkBandwidth, Integer latency) {
        this.cpuUsagePercent = cpuUsage;
        this.usedMemoryMb = usedMemory;
        this.usedDiskGb = usedDisk;
        this.networkBandwidthMbps = networkBandwidth;
        this.networkLatencyMs = latency;
        
        // 基于资源使用情况更新健康状态
        updateHealthStatus();
        this.updatedAt = LocalDateTime.now();
    }

    private void updateHealthStatus() {
        if (cpuUsagePercent != null && cpuUsagePercent > 90) {
            this.healthStatus = HealthStatus.CRITICAL;
        } else if (cpuUsagePercent != null && cpuUsagePercent > 75) {
            this.healthStatus = HealthStatus.WARNING;
        } else if (usedMemoryMb != null && totalMemoryMb != null && 
                  (double) usedMemoryMb / totalMemoryMb > 0.9) {
            this.healthStatus = HealthStatus.CRITICAL;
        } else if (usedMemoryMb != null && totalMemoryMb != null && 
                  (double) usedMemoryMb / totalMemoryMb > 0.75) {
            this.healthStatus = HealthStatus.WARNING;
        } else {
            this.healthStatus = HealthStatus.HEALTHY;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "EdgeNodeEntity{" +
                "id=" + id +
                ", nodeId='" + nodeId + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", nodeType=" + nodeType +
                ", connectionStatus=" + connectionStatus +
                ", healthStatus=" + healthStatus +
                ", currentSessions=" + currentSessions +
                ", maxConcurrentSessions=" + maxConcurrentSessions +
                '}';
    }
}