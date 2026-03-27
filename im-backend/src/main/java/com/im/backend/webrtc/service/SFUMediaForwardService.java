package com.im.backend.webrtc;

import lombok.Data;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * SFU (Selective Forwarding Unit) 媒体转发服务
 * 处理音视频流的转发、路由和质量控制
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SFUMediaForwardService {

    private final WebRTCParticipantRepository participantRepository;
    
    // 媒体轨道管理
    private final ConcurrentHashMap<String, MediaTrack> mediaTracks = new ConcurrentHashMap<>();
    
    // 会话路由表: sessionId -> Set<trackIds>
    private final ConcurrentHashMap<String, Set<String>> sessionRoutes = new ConcurrentHashMap<>();
    
    // 订阅关系: subscriberTrackId -> Set<publisherTrackIds>
    private final ConcurrentHashMap<String, Set<String>> subscriptions = new ConcurrentHashMap<>();
    
    // 质量统计
    private final ConcurrentHashMap<String, TrackStats> trackStats = new ConcurrentHashMap<>();
    
    // 带宽估计
    private final ConcurrentHashMap<String, Long> bandwidthEstimates = new ConcurrentHashMap<>();
    
    // Simulcast层级管理
    private final ConcurrentHashMap<String, SimulcastLayer> activeLayers = new ConcurrentHashMap<>();
    
    private ScheduledExecutorService statsExecutor;
    private ScheduledExecutorService cleanupExecutor;

    @PostConstruct
    public void init() {
        log.info("Initializing SFU Media Forward Service");
        
        statsExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "sfu-stats-collector");
            t.setDaemon(true);
            return t;
        });
        
        cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "sfu-cleanup");
            t.setDaemon(true);
            return t;
        });
        
        // 每5秒收集统计
        statsExecutor.scheduleAtFixedRate(this::collectStats, 5, 5, TimeUnit.SECONDS);
        
        // 每30秒清理无效轨道
        cleanupExecutor.scheduleAtFixedRate(this::cleanupTracks, 30, 30, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down SFU Media Forward Service");
        if (statsExecutor != null) {
            statsExecutor.shutdown();
        }
        if (cleanupExecutor != null) {
            cleanupExecutor.shutdown();
        }
    }

    /**
     * 注册媒体轨道
     */
    public MediaTrack registerTrack(String sessionId, String participantId,
                                     String connectionId, TrackType type,
                                     String codec, Map<String, Object> constraints) {
        log.info("Registering {} track for participant {} in session {}", 
            type, participantId, sessionId);

        String trackId = generateTrackId(sessionId, participantId, type);
        
        MediaTrack track = MediaTrack.builder()
            .trackId(trackId)
            .sessionId(sessionId)
            .participantId(participantId)
            .connectionId(connectionId)
            .type(type)
            .codec(codec)
            .constraints(constraints != null ? constraints : new HashMap<>())
            .createdAt(System.currentTimeMillis())
            .status(TrackStatus.ACTIVE)
            .bitrate(0)
            .packetLoss(0)
            .jitter(0)
            .build();

        mediaTracks.put(trackId, track);
        
        // 添加到会话路由
        sessionRoutes.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet())
            .add(trackId);
        
        // 初始化统计
        trackStats.put(trackId, new TrackStats(trackId));
        
        log.info("Track registered: {}", trackId);
        return track;
    }

    /**
     * 注销媒体轨道
     */
    public void unregisterTrack(String trackId) {
        MediaTrack track = mediaTracks.remove(trackId);
        if (track != null) {
            // 从会话路由移除
            Set<String> routes = sessionRoutes.get(track.getSessionId());
            if (routes != null) {
                routes.remove(trackId);
            }
            
            // 清理订阅关系
            subscriptions.remove(trackId);
            subscriptions.values().forEach(s -> s.remove(trackId));
            
            // 清理统计
            trackStats.remove(trackId);
            bandwidthEstimates.remove(trackId);
            activeLayers.remove(trackId);
            
            log.info("Track unregistered: {}", trackId);
        }
    }

    /**
     * 订阅媒体轨道
     */
    public void subscribeTrack(String subscriberTrackId, String publisherTrackId) {
        log.debug("Subscribing {} to {}", subscriberTrackId, publisherTrackId);
        
        subscriptions.computeIfAbsent(subscriberTrackId, k -> ConcurrentHashMap.newKeySet())
            .add(publisherTrackId);
    }

    /**
     * 取消订阅
     */
    public void unsubscribeTrack(String subscriberTrackId, String publisherTrackId) {
        Set<String> subs = subscriptions.get(subscriberTrackId);
        if (subs != null) {
            subs.remove(publisherTrackId);
        }
    }

    /**
     * 转发媒体数据包
     */
    public void forwardPacket(String trackId, MediaPacket packet) {
        MediaTrack sourceTrack = mediaTracks.get(trackId);
        if (sourceTrack == null || sourceTrack.getStatus() != TrackStatus.ACTIVE) {
            return;
        }

        // 更新发送统计
        TrackStats stats = trackStats.get(trackId);
        if (stats != null) {
            stats.incrementPacketsSent();
            stats.addBytesSent(packet.getSize());
        }

        // 获取会话中的所有订阅者
        Set<String> sessionTrackIds = sessionRoutes.get(sourceTrack.getSessionId());
        if (sessionTrackIds == null) {
            return;
        }

        // 转发给订阅者
        for (String subscriberTrackId : sessionTrackIds) {
            if (subscriberTrackId.equals(trackId)) {
                continue; // 跳过自己
            }

            Set<String> subs = subscriptions.get(subscriberTrackId);
            if (subs != null && subs.contains(trackId)) {
                MediaTrack subscriber = mediaTracks.get(subscriberTrackId);
                if (subscriber != null && subscriber.getStatus() == TrackStatus.ACTIVE) {
                    deliverPacket(subscriber, packet);
                }
            }
        }
    }

    /**
     * 设置Simulcast层级
     */
    public void setSimulcastLayer(String trackId, SimulcastLayer layer) {
        activeLayers.put(trackId, layer);
        
        MediaTrack track = mediaTracks.get(trackId);
        if (track != null) {
            track.setCurrentLayer(layer);
            log.debug("Set simulcast layer for {} to {}", trackId, layer);
        }
    }

    /**
     * 选择最佳Simulcast层
     */
    public SimulcastLayer selectOptimalLayer(String trackId, long availableBandwidth) {
        MediaTrack track = mediaTracks.get(trackId);
        if (track == null) {
            return SimulcastLayer.MEDIUM;
        }

        // 根据可用带宽选择
        if (availableBandwidth > 2500000) {
            return SimulcastLayer.HIGH;
        } else if (availableBandwidth > 1000000) {
            return SimulcastLayer.MEDIUM;
        } else {
            return SimulcastLayer.LOW;
        }
    }

    /**
     * 更新带宽估计
     */
    public void updateBandwidthEstimate(String trackId, long bandwidth) {
        bandwidthEstimates.put(trackId, bandwidth);
    }

    /**
     * 暂停轨道
     */
    public void pauseTrack(String trackId) {
        MediaTrack track = mediaTracks.get(trackId);
        if (track != null) {
            track.setStatus(TrackStatus.PAUSED);
            log.info("Track paused: {}", trackId);
        }
    }

    /**
     * 恢复轨道
     */
    public void resumeTrack(String trackId) {
        MediaTrack track = mediaTracks.get(trackId);
        if (track != null) {
            track.setStatus(TrackStatus.ACTIVE);
            log.info("Track resumed: {}", trackId);
        }
    }

    /**
     * 获取会话媒体统计
     */
    public SessionMediaStats getSessionStats(String sessionId) {
        Set<String> trackIds = sessionRoutes.get(sessionId);
        if (trackIds == null) {
            return SessionMediaStats.builder()
                .sessionId(sessionId)
                .totalTracks(0)
                .build();
        }

        int totalTracks = trackIds.size();
        int audioTracks = 0;
        int videoTracks = 0;
        long totalBitrate = 0;
        double avgPacketLoss = 0;

        for (String trackId : trackIds) {
            MediaTrack track = mediaTracks.get(trackId);
            if (track != null) {
                if (track.getType() == TrackType.AUDIO) {
                    audioTracks++;
                } else if (track.getType() == TrackType.VIDEO) {
                    videoTracks++;
                }
                totalBitrate += track.getBitrate();
                avgPacketLoss += track.getPacketLoss();
            }
        }

        if (totalTracks > 0) {
            avgPacketLoss /= totalTracks;
        }

        return SessionMediaStats.builder()
            .sessionId(sessionId)
            .totalTracks(totalTracks)
            .audioTracks(audioTracks)
            .videoTracks(videoTracks)
            .totalBitrate(totalBitrate)
            .averagePacketLoss(avgPacketLoss)
            .build();
    }

    /**
     * 获取会话中的所有轨道
     */
    public List<MediaTrack> getSessionTracks(String sessionId) {
        Set<String> trackIds = sessionRoutes.get(sessionId);
        if (trackIds == null) {
            return Collections.emptyList();
        }

        return trackIds.stream()
            .map(mediaTracks::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * 获取参与者轨道
     */
    public List<MediaTrack> getParticipantTracks(String sessionId, String participantId) {
        return getSessionTracks(sessionId).stream()
            .filter(t -> t.getParticipantId().equals(participantId))
            .collect(Collectors.toList());
    }

    // 内部方法
    private void deliverPacket(MediaTrack subscriber, MediaPacket packet) {
        // 实际实现中会通过网络发送给订阅者
        TrackStats stats = trackStats.get(subscriber.getTrackId());
        if (stats != null) {
            stats.incrementPacketsReceived();
            stats.addBytesReceived(packet.getSize());
        }
    }

    private void collectStats() {
        // 收集并聚合统计信息
        for (Map.Entry<String, TrackStats> entry : trackStats.entrySet()) {
            entry.getValue().snapshot();
        }
    }

    private void cleanupTracks() {
        long now = System.currentTimeMillis();
        long timeout = 60000; // 60秒超时

        Iterator<Map.Entry<String, MediaTrack>> it = mediaTracks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, MediaTrack> entry = it.next();
            MediaTrack track = entry.getValue();
            
            TrackStats stats = trackStats.get(track.getTrackId());
            if (stats != null && (now - stats.getLastActivity()) > timeout) {
                log.warn("Cleaning up inactive track: {}", track.getTrackId());
                unregisterTrack(track.getTrackId());
            }
        }
    }

    private String generateTrackId(String sessionId, String participantId, TrackType type) {
        return String.format("%s_%s_%s_%d", sessionId, participantId, 
            type.name().toLowerCase(), System.currentTimeMillis());
    }

    // 枚举和DTO
    public enum TrackType {
        AUDIO, VIDEO, SCREEN, DATA
    }

    public enum TrackStatus {
        ACTIVE, PAUSED, INACTIVE, ERROR
    }

    public enum SimulcastLayer {
        LOW, MEDIUM, HIGH
    }

    @Data
    @Builder
    public static class MediaTrack {
        private String trackId;
        private String sessionId;
        private String participantId;
        private String connectionId;
        private TrackType type;
        private String codec;
        private Map<String, Object> constraints;
        private long createdAt;
        private TrackStatus status;
        private long bitrate;
        private double packetLoss;
        private double jitter;
        private SimulcastLayer currentLayer;
    }

    @Data
    @Builder
    public static class MediaPacket {
        private byte[] data;
        private int size;
        private long timestamp;
        private int sequenceNumber;
        private boolean isKeyFrame;
        private SimulcastLayer layer;
    }

    @Data
    @Builder
    public static class SessionMediaStats {
        private String sessionId;
        private int totalTracks;
        private int audioTracks;
        private int videoTracks;
        private long totalBitrate;
        private double averagePacketLoss;
    }

    private static class TrackStats {
        private final String trackId;
        private long packetsSent;
        private long packetsReceived;
        private long bytesSent;
        private long bytesReceived;
        private long lastActivity;

        TrackStats(String trackId) {
            this.trackId = trackId;
            this.lastActivity = System.currentTimeMillis();
        }

        void incrementPacketsSent() {
            packetsSent++;
            lastActivity = System.currentTimeMillis();
        }

        void incrementPacketsReceived() {
            packetsReceived++;
            lastActivity = System.currentTimeMillis();
        }

        void addBytesSent(long bytes) {
            bytesSent += bytes;
        }

        void addBytesReceived(long bytes) {
            bytesReceived += bytes;
        }

        void snapshot() {
            // 保存快照，用于历史统计
        }

        long getLastActivity() {
            return lastActivity;
        }
    }
}
