package com.im.backend.service.network;

import com.im.backend.entity.network.*;
import com.im.backend.repository.network.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

/**
 * 自适应网络协议服务
 * 根据网络状况动态调整传输参数和协议策略
 */
@Service
public class AdaptiveNetworkProtocolService {

    @Autowired
    private NetworkMetricsRepository metricsRepository;

    @Autowired
    private ProtocolConfigurationRepository configRepository;

    @Autowired
    private ConnectionProfileRepository profileRepository;

    private final ConcurrentHashMap<String, NetworkMetrics> realTimeMetrics = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ProtocolConfiguration> activeConfigs = new ConcurrentHashMap<>();
    
    // 网络质量等级定义
    private static final double EXCELLENT_RTT = 50;      // ms
    private static final double GOOD_RTT = 100;          // ms
    private static final double FAIR_RTT = 200;          // ms
    
    private static final double MIN_PACKET_LOSS = 0.001;  // 0.1%
    private static final double MAX_PACKET_LOSS = 0.05;   // 5%

    /**
     * 网络质量评估
     */
    public enum NetworkQuality {
        EXCELLENT,  // 优秀
        GOOD,       // 良好
        FAIR,       // 一般
        POOR        // 差
    }

    /**
     * 协议策略类型
     */
    public enum ProtocolStrategy {
        UDP_FAST,       // 快速UDP模式
        UDP_RELIABLE,   // 可靠UDP模式
        TCP_STANDARD,   // 标准TCP模式
        TCP_QUIC,       // QUIC模式
        HYBRID          // 混合模式
    }

    /**
     * 初始化连接
     */
    public ConnectionProfile initializeConnection(String userId, String deviceId, 
                                                   String networkType) {
        ConnectionProfile profile = new ConnectionProfile();
        profile.setUserId(userId);
        profile.setDeviceId(deviceId);
        profile.setNetworkType(networkType);
        profile.setInitialQuality(NetworkQuality.GOOD);
        profile.setCreatedAt(LocalDateTime.now());
        profile.setLastUpdated(LocalDateTime.now());

        // 根据网络类型选择初始协议
        ProtocolConfiguration config = selectInitialProtocol(networkType);
        profile.setProtocolConfig(config);

        return profileRepository.save(profile);
    }

    /**
     * 报告网络指标
     */
    public void reportMetrics(String connectionId, NetworkMetrics metrics) {
        metrics.setConnectionId(connectionId);
        metrics.setTimestamp(LocalDateTime.now());
        
        realTimeMetrics.put(connectionId, metrics);
        
        // 异步分析并调整协议
        CompletableFuture.runAsync(() -> analyzeAndAdjust(connectionId, metrics));
        
        // 持久化历史数据
        metricsRepository.save(metrics);
    }

    /**
     * 获取最佳协议配置
     */
    public ProtocolConfiguration getOptimalConfiguration(String connectionId) {
        return activeConfigs.computeIfAbsent(connectionId, id -> {
            ConnectionProfile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("连接不存在: " + id));
            return profile.getProtocolConfig();
        });
    }

    /**
     * 分析网络状况并调整协议
     */
    private void analyzeAndAdjust(String connectionId, NetworkMetrics metrics) {
        // 评估当前网络质量
        NetworkQuality quality = assessNetworkQuality(metrics);
        
        // 获取当前配置
        ProtocolConfiguration currentConfig = getOptimalConfiguration(connectionId);
        
        // 决定是否需要切换协议
        ProtocolStrategy newStrategy = determineOptimalStrategy(quality, metrics);
        
        if (currentConfig.getStrategy() != newStrategy) {
            // 创建新配置
            ProtocolConfiguration newConfig = createConfiguration(newStrategy, quality, metrics);
            newConfig.setConnectionId(connectionId);
            
            // 平滑过渡：使用双轨运行
            performSmoothTransition(connectionId, currentConfig, newConfig);
            
            // 更新配置
            activeConfigs.put(connectionId, newConfig);
            configRepository.save(newConfig);
            
            // 更新连接档案
            updateConnectionProfile(connectionId, quality, newConfig);
        } else {
            // 微调当前配置参数
            fineTuneConfiguration(currentConfig, metrics);
            configRepository.save(currentConfig);
        }
    }

    /**
     * 评估网络质量
     */
    private NetworkQuality assessNetworkQuality(NetworkMetrics metrics) {
        double rtt = metrics.getRoundTripTime();
        double packetLoss = metrics.getPacketLossRate();
        double jitter = metrics.getJitter();
        double bandwidth = metrics.getBandwidthMbps();

        // 综合评分算法
        int score = 100;
        
        // RTT扣分
        if (rtt > EXCELLENT_RTT) {
            score -= (int) ((rtt - EXCELLENT_RTT) / 10);
        }
        
        // 丢包率扣分
        if (packetLoss > MIN_PACKET_LOSS) {
            score -= (int) ((packetLoss - MIN_PACKET_LOSS) * 1000);
        }
        
        // 抖动扣分
        if (jitter > 20) {
            score -= (int) (jitter / 5);
        }
        
        // 带宽奖励
        if (bandwidth > 10) {
            score += (int) (bandwidth / 5);
        }

        // 根据分数判定等级
        if (score >= 90) return NetworkQuality.EXCELLENT;
        if (score >= 70) return NetworkQuality.GOOD;
        if (score >= 50) return NetworkQuality.FAIR;
        return NetworkQuality.POOR;
    }

    /**
     * 确定最优协议策略
     */
    private ProtocolStrategy determineOptimalStrategy(NetworkQuality quality, 
                                                       NetworkMetrics metrics) {
        double packetLoss = metrics.getPacketLossRate();
        double rtt = metrics.getRoundTripTime();
        boolean isMobile = "MOBILE".equals(metrics.getNetworkType()) || 
                          "4G".equals(metrics.getNetworkType()) ||
                          "5G".equals(metrics.getNetworkType());

        switch (quality) {
            case EXCELLENT:
                // 网络极好时优先使用UDP快速模式
                return isMobile ? ProtocolStrategy.UDP_FAST : ProtocolStrategy.QUIC;
                
            case GOOD:
                // 网络良好时使用可靠UDP或QUIC
                if (packetLoss < 0.01) {
                    return ProtocolStrategy.UDP_RELIABLE;
                }
                return ProtocolStrategy.TCP_QUIC;
                
            case FAIR:
                // 网络一般时使用TCP或混合模式
                if (rtt > 150) {
                    return ProtocolStrategy.HYBRID;
                }
                return ProtocolStrategy.TCP_STANDARD;
                
            case POOR:
                // 网络差时使用标准TCP保证可靠性
                return ProtocolStrategy.TCP_STANDARD;
                
            default:
                return ProtocolStrategy.TCP_STANDARD;
        }
    }

    /**
     * 创建协议配置
     */
    private ProtocolConfiguration createConfiguration(ProtocolStrategy strategy,
                                                       NetworkQuality quality,
                                                       NetworkMetrics metrics) {
        ProtocolConfiguration config = new ProtocolConfiguration();
        config.setStrategy(strategy);
        config.setCreatedAt(LocalDateTime.now());
        config.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        switch (strategy) {
            case UDP_FAST:
                configureUdpFast(config, quality);
                break;
            case UDP_RELIABLE:
                configureUdpReliable(config, quality, metrics);
                break;
            case TCP_QUIC:
                configureQuic(config, quality, metrics);
                break;
            case HYBRID:
                configureHybrid(config, quality, metrics);
                break;
            default:
                configureTcpStandard(config, quality);
        }

        return config;
    }

    /**
     * 配置UDP快速模式
     */
    private void configureUdpFast(ProtocolConfiguration config, NetworkQuality quality) {
        config.setEnableCompression(true);
        config.setCompressionLevel(3);  // 轻度压缩
        config.setPacketSize(1472);     // 接近MTU
        config.setEnableFEC(false);     // 不启用前向纠错
        config.setAckIntervalMs(0);     // 不等待确认
        config.setRetransmitLimit(0);   // 不重传
        config.setEnablePacing(true);
        config.setPacingRateKbps(10000);
    }

    /**
     * 配置可靠UDP模式
     */
    private void configureUdpReliable(ProtocolConfiguration config, 
                                       NetworkQuality quality,
                                       NetworkMetrics metrics) {
        config.setEnableCompression(true);
        config.setCompressionLevel(6);
        config.setPacketSize(1200);
        config.setEnableFEC(true);
        config.setFecRedundancyRatio(0.1);
        config.setAckIntervalMs(25);
        config.setRetransmitLimit(3);
        config.setRetransmitTimeoutMs((int) (metrics.getRoundTripTime() * 1.5));
        config.setCongestionControl("BBR");
    }

    /**
     * 配置QUIC模式
     */
    private void configureQuic(ProtocolConfiguration config, 
                                NetworkQuality quality,
                                NetworkMetrics metrics) {
        config.setEnableCompression(true);
        config.setCompressionLevel(5);
        config.setEnableMultipath(true);
        config.setMaxStreams(100);
        config.setFlowControlWindow(65536);
        config.setEnable0RTT(quality == NetworkQuality.EXCELLENT);
        config.setMigrationEnabled(true);
    }

    /**
     * 配置TCP标准模式
     */
    private void configureTcpStandard(ProtocolConfiguration config, 
                                       NetworkQuality quality) {
        config.setEnableTcpNoDelay(true);
        config.setTcpKeepAliveIntervalSec(30);
        config.setTcpWindowSize(65536);
        config.setEnableSack(true);
        config.setEnableTimestamps(true);
    }

    /**
     * 配置混合模式
     */
    private void configureHybrid(ProtocolConfiguration config,
                                  NetworkQuality quality,
                                  NetworkMetrics metrics) {
        // 混合模式：重要数据用TCP，实时数据用UDP
        config.setPrimaryStrategy(ProtocolStrategy.TCP_STANDARD);
        config.setSecondaryStrategy(ProtocolStrategy.UDP_FAST);
        config.setStrategySelector("latency_based");
        config.setSwitchThresholdMs(100);
    }

    /**
     * 平滑协议切换
     */
    private void performSmoothTransition(String connectionId,
                                          ProtocolConfiguration oldConfig,
                                          ProtocolConfiguration newConfig) {
        // 发送切换通知
        ProtocolSwitchNotification notification = new ProtocolSwitchNotification();
        notification.setConnectionId(connectionId);
        notification.setOldStrategy(oldConfig.getStrategy());
        notification.setNewStrategy(newConfig.getStrategy());
        notification.setSwitchTime(LocalDateTime.now());
        notification.setGracePeriodMs(500);
        
        // 实际实现中会发送到客户端
        System.out.println("协议切换: " + oldConfig.getStrategy() + " -> " + newConfig.getStrategy());
    }

    /**
     * 微调配置参数
     */
    private void fineTuneConfiguration(ProtocolConfiguration config, 
                                        NetworkMetrics metrics) {
        // 根据实时指标微调参数
        if (config.getStrategy() == ProtocolStrategy.UDP_RELIABLE) {
            // 调整重传超时
            int newTimeout = (int) (metrics.getRoundTripTime() * 1.5);
            config.setRetransmitTimeoutMs(Math.max(50, Math.min(1000, newTimeout)));
            
            // 调整FEC冗余度
            double packetLoss = metrics.getPacketLossRate();
            double newFecRatio = Math.min(0.3, packetLoss * 10);
            config.setFecRedundancyRatio(newFecRatio);
        }
        
        // 更新过期时间
        config.setExpiresAt(LocalDateTime.now().plusMinutes(5));
    }

    /**
     * 更新连接档案
     */
    private void updateConnectionProfile(String connectionId,
                                          NetworkQuality quality,
                                          ProtocolConfiguration config) {
        ConnectionProfile profile = profileRepository.findById(connectionId)
            .orElse(null);
        
        if (profile != null) {
            profile.setCurrentQuality(quality);
            profile.setProtocolConfig(config);
            profile.setLastUpdated(LocalDateTime.now());
            profile.setStrategySwitchCount(profile.getStrategySwitchCount() + 1);
            profileRepository.save(profile);
        }
    }

    /**
     * 选择初始协议
     */
    private ProtocolConfiguration selectInitialProtocol(String networkType) {
        ProtocolStrategy strategy;
        
        switch (networkType.toUpperCase()) {
            case "WIFI":
                strategy = ProtocolStrategy.QUIC;
                break;
            case "5G":
                strategy = ProtocolStrategy.UDP_RELIABLE;
                break;
            case "4G":
            case "LTE":
                strategy = ProtocolStrategy.HYBRID;
                break;
            default:
                strategy = ProtocolStrategy.TCP_STANDARD;
        }
        
        return createConfiguration(strategy, NetworkQuality.GOOD, new NetworkMetrics());
    }

    /**
     * 获取全局网络统计
     */
    public Map<String, Object> getGlobalStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 各协议使用统计
        Map<ProtocolStrategy, Long> strategyUsage = new HashMap<>();
        activeConfigs.values().forEach(config -> {
            strategyUsage.merge(config.getStrategy(), 1L, Long::sum);
        });
        stats.put("strategyUsage", strategyUsage);
        
        // 网络质量分布
        Map<NetworkQuality, Long> qualityDistribution = new HashMap<>();
        realTimeMetrics.values().forEach(metric -> {
            NetworkQuality quality = assessNetworkQuality(metric);
            qualityDistribution.merge(quality, 1L, Long::sum);
        });
        stats.put("qualityDistribution", qualityDistribution);
        
        // 平均RTT
        double avgRtt = realTimeMetrics.values().stream()
            .mapToDouble(NetworkMetrics::getRoundTripTime)
            .average()
            .orElse(0);
        stats.put("averageRTT", avgRtt);
        
        // 活跃连接数
        stats.put("activeConnections", activeConfigs.size());
        
        return stats;
    }

    /**
     * 定时清理过期配置
     */
    @Scheduled(fixedRate = 60000) // 每分钟
    public void cleanupExpiredConfigurations() {
        LocalDateTime now = LocalDateTime.now();
        
        activeConfigs.entrySet().removeIf(entry -> {
            if (entry.getValue().getExpiresAt().isBefore(now)) {
                // 通知客户端重新协商
                System.out.println("配置过期: " + entry.getKey());
                return true;
            }
            return false;
        });
        
        // 清理过期的实时指标
        LocalDateTime cutoff = now.minusMinutes(10);
        realTimeMetrics.entrySet().removeIf(entry -> {
            return entry.getValue().getTimestamp().isBefore(cutoff);
        });
    }
}
