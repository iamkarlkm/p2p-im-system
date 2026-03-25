/// 边缘视频处理模型
/// 用于 Flutter 移动端应用

import 'dart:convert';

/// 媒体类型枚举
enum MediaType {
  videoOnly,
  audioOnly,
  videoWithAudio,
  screenShare,
  videoConference,
  liveStreaming,
  vodProcessing
}

/// 处理状态枚举
enum ProcessingStatus {
  pending,
  queued,
  processing,
  paused,
  completed,
  failed,
  cancelled,
  timeout
}

/// 边缘节点类型枚举
enum NodeType {
  cloudEdge,
  regionalEdge,
  localEdge,
  mobileEdge,
  iotEdge,
  hybridEdge,
  fogComputing
}

/// 健康状态枚举
enum HealthStatus {
  healthy,
  warning,
  critical,
  degraded,
  unknown
}

/// 连接状态枚举
enum ConnectionStatus {
  online,
  offline,
  connecting,
  disconnected,
  unreachable
}

/// 视频处理任务模型
class EdgeVideoProcessingTask {
  final String? id;
  final String taskId;
  final String sessionId;
  final String userId;
  final String edgeNodeId;
  final MediaType mediaType;
  final String inputSource;
  final String? outputDestination;
  final ProcessingStatus processingStatus;
  final String? videoCodec;
  final String? audioCodec;
  final int? resolutionWidth;
  final int? resolutionHeight;
  final int? frameRate;
  final int? bitrateKbps;
  final bool? aiEnhancementsEnabled;
  final String? enhancementType;
  final bool? bandwidthOptimizationEnabled;
  final int? compressionLevel;
  final int? latencyMs;
  final DateTime? processingStartTime;
  final DateTime? processingEndTime;
  final int? processingDurationMs;
  final double? cpuUsagePercent;
  final int? memoryUsageMb;
  final double? networkBandwidthMbps;
  final double? qualityScore;
  final String? errorMessage;
  final int? retryCount;
  final int? maxRetries;
  final int? priorityLevel;
  final DateTime createdAt;
  final DateTime updatedAt;
  final DateTime? expiresAt;
  final Map<String, dynamic>? metadata;

  EdgeVideoProcessingTask({
    this.id,
    required this.taskId,
    required this.sessionId,
    required this.userId,
    required this.edgeNodeId,
    required this.mediaType,
    required this.inputSource,
    this.outputDestination,
    required this.processingStatus,
    this.videoCodec,
    this.audioCodec,
    this.resolutionWidth,
    this.resolutionHeight,
    this.frameRate,
    this.bitrateKbps,
    this.aiEnhancementsEnabled,
    this.enhancementType,
    this.bandwidthOptimizationEnabled,
    this.compressionLevel,
    this.latencyMs,
    this.processingStartTime,
    this.processingEndTime,
    this.processingDurationMs,
    this.cpuUsagePercent,
    this.memoryUsageMb,
    this.networkBandwidthMbps,
    this.qualityScore,
    this.errorMessage,
    this.retryCount,
    this.maxRetries,
    this.priorityLevel,
    required this.createdAt,
    required this.updatedAt,
    this.expiresAt,
    this.metadata,
  });

  factory EdgeVideoProcessingTask.fromJson(Map<String, dynamic> json) {
    return EdgeVideoProcessingTask(
      id: json['id'],
      taskId: json['taskId'] ?? '',
      sessionId: json['sessionId'] ?? '',
      userId: json['userId'] ?? '',
      edgeNodeId: json['edgeNodeId'] ?? '',
      mediaType: _parseMediaType(json['mediaType']),
      inputSource: json['inputSource'] ?? '',
      outputDestination: json['outputDestination'],
      processingStatus: _parseProcessingStatus(json['processingStatus']),
      videoCodec: json['videoCodec'],
      audioCodec: json['audioCodec'],
      resolutionWidth: json['resolutionWidth'],
      resolutionHeight: json['resolutionHeight'],
      frameRate: json['frameRate'],
      bitrateKbps: json['bitrateKbps'],
      aiEnhancementsEnabled: json['aiEnhancementsEnabled'],
      enhancementType: json['enhancementType'],
      bandwidthOptimizationEnabled: json['bandwidthOptimizationEnabled'],
      compressionLevel: json['compressionLevel'],
      latencyMs: json['latencyMs'],
      processingStartTime: json['processingStartTime'] != null
          ? DateTime.parse(json['processingStartTime'])
          : null,
      processingEndTime: json['processingEndTime'] != null
          ? DateTime.parse(json['processingEndTime'])
          : null,
      processingDurationMs: json['processingDurationMs'],
      cpuUsagePercent: json['cpuUsagePercent']?.toDouble(),
      memoryUsageMb: json['memoryUsageMb'],
      networkBandwidthMbps: json['networkBandwidthMbps']?.toDouble(),
      qualityScore: json['qualityScore']?.toDouble(),
      errorMessage: json['errorMessage'],
      retryCount: json['retryCount'],
      maxRetries: json['maxRetries'],
      priorityLevel: json['priorityLevel'],
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'])
          : DateTime.now(),
      updatedAt: json['updatedAt'] != null
          ? DateTime.parse(json['updatedAt'])
          : DateTime.now(),
      expiresAt: json['expiresAt'] != null
          ? DateTime.parse(json['expiresAt'])
          : null,
      metadata: json['metadataJson'] != null
          ? jsonDecode(json['metadataJson'])
          : json['metadata'],
    );
  }

  static MediaType _parseMediaType(String? value) {
    if (value == null) return MediaType.videoWithAudio;
    return MediaType.values.firstWhere(
      (e) => e.toString().split('.').last == value,
      orElse: () => MediaType.videoWithAudio,
    );
  }

  static ProcessingStatus _parseProcessingStatus(String? value) {
    if (value == null) return ProcessingStatus.pending;
    return ProcessingStatus.values.firstWhere(
      (e) => e.toString().split('.').last == value,
      orElse: () => ProcessingStatus.pending,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'taskId': taskId,
      'sessionId': sessionId,
      'userId': userId,
      'edgeNodeId': edgeNodeId,
      'mediaType': mediaType.toString().split('.').last,
      'inputSource': inputSource,
      'outputDestination': outputDestination,
      'processingStatus': processingStatus.toString().split('.').last,
      'videoCodec': videoCodec,
      'audioCodec': audioCodec,
      'resolutionWidth': resolutionWidth,
      'resolutionHeight': resolutionHeight,
      'frameRate': frameRate,
      'bitrateKbps': bitrateKbps,
      'aiEnhancementsEnabled': aiEnhancementsEnabled,
      'enhancementType': enhancementType,
      'bandwidthOptimizationEnabled': bandwidthOptimizationEnabled,
      'compressionLevel': compressionLevel,
      'latencyMs': latencyMs,
      'processingStartTime': processingStartTime?.toIso8601String(),
      'processingEndTime': processingEndTime?.toIso8601String(),
      'processingDurationMs': processingDurationMs,
      'cpuUsagePercent': cpuUsagePercent,
      'memoryUsageMb': memoryUsageMb,
      'networkBandwidthMbps': networkBandwidthMbps,
      'qualityScore': qualityScore,
      'errorMessage': errorMessage,
      'retryCount': retryCount,
      'maxRetries': maxRetries,
      'priorityLevel': priorityLevel,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
      'expiresAt': expiresAt?.toIso8601String(),
      'metadataJson': metadata != null ? jsonEncode(metadata) : null,
    };
  }

  bool get isProcessing => processingStatus == ProcessingStatus.processing;
  bool get isCompleted => processingStatus == ProcessingStatus.completed;
  bool get isFailed => processingStatus == ProcessingStatus.failed;
  bool get isCancelled => processingStatus == ProcessingStatus.cancelled;
  bool get isPending => processingStatus == ProcessingStatus.pending;
  bool get isQueued => processingStatus == ProcessingStatus.queued;
  bool get isPaused => processingStatus == ProcessingStatus.paused;

  String get resolution {
    if (resolutionWidth != null && resolutionHeight != null) {
      return '${resolutionWidth}x${resolutionHeight}';
    }
    return 'Unknown';
  }

  String get durationFormatted {
    if (processingDurationMs == null) return 'N/A';
    final seconds = (processingDurationMs! / 1000).round();
    if (seconds >= 60) {
      final mins = seconds ~/ 60;
      final secs = seconds % 60;
      return '${mins}m ${secs}s';
    }
    return '${seconds}s';
  }

  String get qualityRating {
    if (qualityScore == null) return 'N/A';
    if (qualityScore! >= 90) return 'Excellent';
    if (qualityScore! >= 80) return 'Good';
    if (qualityScore! >= 70) return 'Fair';
    return 'Poor';
  }

  @override
  String toString() {
    return 'EdgeVideoProcessingTask(taskId: $taskId, status: $processingStatus, quality: ${qualityScore ?? 'N/A'})';
  }
}

/// 边缘节点模型
class EdgeNode {
  final String? id;
  final String nodeId;
  final String nodeName;
  final NodeType nodeType;
  final String? geographicLocation;
  final double? latitude;
  final double? longitude;
  final String ipAddress;
  final int port;
  final String? apiEndpoint;
  final HealthStatus healthStatus;
  final ConnectionStatus connectionStatus;
  final DateTime? lastHeartbeat;
  final int? cpuCores;
  final double? cpuUsagePercent;
  final int? totalMemoryMb;
  final int? usedMemoryMb;
  final int? totalDiskGb;
  final int? usedDiskGb;
  final double? networkBandwidthMbps;
  final int? networkLatencyMs;
  final bool? gpuAvailable;
  final String? gpuType;
  final int? gpuMemoryGb;
  final String? supportedVideoCodecs;
  final String? supportedAudioCodecs;
  final int maxConcurrentSessions;
  final int currentSessions;
  final int? videoProcessingCapacity;
  final int? audioProcessingCapacity;
  final bool? aiAccelerationSupported;
  final String? aiModelTypes;
  final bool? bandwidthOptimizationSupported;
  final bool? realTimeTranscodingSupported;
  final String? securityLevel;
  final bool? sslEnabled;
  final DateTime? certificateExpiry;
  final bool? maintenanceMode;
  final DateTime? scheduledMaintenanceStart;
  final DateTime? scheduledMaintenanceEnd;
  final String? softwareVersion;
  final DateTime? lastSoftwareUpdate;
  final String? tags;
  final Map<String, dynamic>? metadata;
  final DateTime createdAt;
  final DateTime updatedAt;
  final DateTime? lastPerformanceReport;

  EdgeNode({
    this.id,
    required this.nodeId,
    required this.nodeName,
    required this.nodeType,
    this.geographicLocation,
    this.latitude,
    this.longitude,
    required this.ipAddress,
    this.port = 8080,
    this.apiEndpoint,
    this.healthStatus = HealthStatus.unknown,
    this.connectionStatus = ConnectionStatus.offline,
    this.lastHeartbeat,
    this.cpuCores,
    this.cpuUsagePercent,
    this.totalMemoryMb,
    this.usedMemoryMb,
    this.totalDiskGb,
    this.usedDiskGb,
    this.networkBandwidthMbps,
    this.networkLatencyMs,
    this.gpuAvailable,
    this.gpuType,
    this.gpuMemoryGb,
    this.supportedVideoCodecs,
    this.supportedAudioCodecs,
    this.maxConcurrentSessions = 100,
    this.currentSessions = 0,
    this.videoProcessingCapacity,
    this.audioProcessingCapacity,
    this.aiAccelerationSupported,
    this.aiModelTypes,
    this.bandwidthOptimizationSupported,
    this.realTimeTranscodingSupported,
    this.securityLevel,
    this.sslEnabled,
    this.certificateExpiry,
    this.maintenanceMode,
    this.scheduledMaintenanceStart,
    this.scheduledMaintenanceEnd,
    this.softwareVersion,
    this.lastSoftwareUpdate,
    this.tags,
    this.metadata,
    required this.createdAt,
    required this.updatedAt,
    this.lastPerformanceReport,
  });

  factory EdgeNode.fromJson(Map<String, dynamic> json) {
    return EdgeNode(
      id: json['id'],
      nodeId: json['nodeId'] ?? '',
      nodeName: json['nodeName'] ?? '',
      nodeType: _parseNodeType(json['nodeType']),
      geographicLocation: json['geographicLocation'],
      latitude: json['latitude']?.toDouble(),
      longitude: json['longitude']?.toDouble(),
      ipAddress: json['ipAddress'] ?? '',
      port: json['port'] ?? 8080,
      apiEndpoint: json['apiEndpoint'],
      healthStatus: _parseHealthStatus(json['healthStatus']),
      connectionStatus: _parseConnectionStatus(json['connectionStatus']),
      lastHeartbeat: json['lastHeartbeat'] != null
          ? DateTime.parse(json['lastHeartbeat'])
          : null,
      cpuCores: json['cpuCores'],
      cpuUsagePercent: json['cpuUsagePercent']?.toDouble(),
      totalMemoryMb: json['totalMemoryMb'],
      usedMemoryMb: json['usedMemoryMb'],
      totalDiskGb: json['totalDiskGb'],
      usedDiskGb: json['usedDiskGb'],
      networkBandwidthMbps: json['networkBandwidthMbps']?.toDouble(),
      networkLatencyMs: json['networkLatencyMs'],
      gpuAvailable: json['gpuAvailable'],
      gpuType: json['gpuType'],
      gpuMemoryGb: json['gpuMemoryGb'],
      supportedVideoCodecs: json['supportedVideoCodecs'],
      supportedAudioCodecs: json['supportedAudioCodecs'],
      maxConcurrentSessions: json['maxConcurrentSessions'] ?? 100,
      currentSessions: json['currentSessions'] ?? 0,
      videoProcessingCapacity: json['videoProcessingCapacity'],
      audioProcessingCapacity: json['audioProcessingCapacity'],
      aiAccelerationSupported: json['aiAccelerationSupported'],
      aiModelTypes: json['aiModelTypes'],
      bandwidthOptimizationSupported: json['bandwidthOptimizationSupported'],
      realTimeTranscodingSupported: json['realTimeTranscodingSupported'],
      securityLevel: json['securityLevel'],
      sslEnabled: json['sslEnabled'],
      certificateExpiry: json['certificateExpiry'] != null
          ? DateTime.parse(json['certificateExpiry'])
          : null,
      maintenanceMode: json['maintenanceMode'],
      scheduledMaintenanceStart: json['scheduledMaintenanceStart'] != null
          ? DateTime.parse(json['scheduledMaintenanceStart'])
          : null,
      scheduledMaintenanceEnd: json['scheduledMaintenanceEnd'] != null
          ? DateTime.parse(json['scheduledMaintenanceEnd'])
          : null,
      softwareVersion: json['softwareVersion'],
      lastSoftwareUpdate: json['lastSoftwareUpdate'] != null
          ? DateTime.parse(json['lastSoftwareUpdate'])
          : null,
      tags: json['tags'],
      metadata: json['metadataJson'] != null
          ? jsonDecode(json['metadataJson'])
          : json['metadata'],
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'])
          : DateTime.now(),
      updatedAt: json['updatedAt'] != null
          ? DateTime.parse(json['updatedAt'])
          : DateTime.now(),
      lastPerformanceReport: json['lastPerformanceReport'] != null
          ? DateTime.parse(json['lastPerformanceReport'])
          : null,
    );
  }

  static NodeType _parseNodeType(String? value) {
    if (value == null) return NodeType.localEdge;
    return NodeType.values.firstWhere(
      (e) => e.toString().split('.').last == value,
      orElse: () => NodeType.localEdge,
    );
  }

  static HealthStatus _parseHealthStatus(String? value) {
    if (value == null) return HealthStatus.unknown;
    return HealthStatus.values.firstWhere(
      (e) => e.toString().split('.').last == value,
      orElse: () => HealthStatus.unknown,
    );
  }

  static ConnectionStatus _parseConnectionStatus(String? value) {
    if (value == null) return ConnectionStatus.offline;
    return ConnectionStatus.values.firstWhere(
      (e) => e.toString().split('.').last == value,
      orElse: () => ConnectionStatus.offline,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'nodeId': nodeId,
      'nodeName': nodeName,
      'nodeType': nodeType.toString().split('.').last,
      'geographicLocation': geographicLocation,
      'latitude': latitude,
      'longitude': longitude,
      'ipAddress': ipAddress,
      'port': port,
      'apiEndpoint': apiEndpoint,
      'healthStatus': healthStatus.toString().split('.').last,
      'connectionStatus': connectionStatus.toString().split('.').last,
      'lastHeartbeat': lastHeartbeat?.toIso8601String(),
      'cpuCores': cpuCores,
      'cpuUsagePercent': cpuUsagePercent,
      'totalMemoryMb': totalMemoryMb,
      'usedMemoryMb': usedMemoryMb,
      'totalDiskGb': totalDiskGb,
      'usedDiskGb': usedDiskGb,
      'networkBandwidthMbps': networkBandwidthMbps,
      'networkLatencyMs': networkLatencyMs,
      'gpuAvailable': gpuAvailable,
      'gpuType': gpuType,
      'gpuMemoryGb': gpuMemoryGb,
      'supportedVideoCodecs': supportedVideoCodecs,
      'supportedAudioCodecs': supportedAudioCodecs,
      'maxConcurrentSessions': maxConcurrentSessions,
      'currentSessions': currentSessions,
      'videoProcessingCapacity': videoProcessingCapacity,
      'audioProcessingCapacity': audioProcessingCapacity,
      'aiAccelerationSupported': aiAccelerationSupported,
      'aiModelTypes': aiModelTypes,
      'bandwidthOptimizationSupported': bandwidthOptimizationSupported,
      'realTimeTranscodingSupported': realTimeTranscodingSupported,
      'securityLevel': securityLevel,
      'sslEnabled': sslEnabled,
      'certificateExpiry': certificateExpiry?.toIso8601String(),
      'maintenanceMode': maintenanceMode,
      'scheduledMaintenanceStart': scheduledMaintenanceStart?.toIso8601String(),
      'scheduledMaintenanceEnd': scheduledMaintenanceEnd?.toIso8601String(),
      'softwareVersion': softwareVersion,
      'lastSoftwareUpdate': lastSoftwareUpdate?.toIso8601String(),
      'tags': tags,
      'metadataJson': metadata != null ? jsonEncode(metadata) : null,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
      'lastPerformanceReport': lastPerformanceReport?.toIso8601String(),
    };
  }

  bool get isAvailable =>
      connectionStatus == ConnectionStatus.online &&
      !maintenanceMode! &&
      healthStatus != HealthStatus.critical &&
      currentSessions < maxConcurrentSessions;

  double get availableCapacityPercentage {
    if (maxConcurrentSessions == 0) return 0.0;
    return ((maxConcurrentSessions - currentSessions) / maxConcurrentSessions) * 100;
  }

  String get healthStatusEmoji {
    switch (healthStatus) {
      case HealthStatus.healthy:
        return '🟢';
      case HealthStatus.warning:
        return '🟡';
      case HealthStatus.critical:
        return '🔴';
      case HealthStatus.degraded:
        return '🟠';
      case HealthStatus.unknown:
        return '⚪';
    }
  }

  String get connectionStatusEmoji {
    switch (connectionStatus) {
      case ConnectionStatus.online:
        return '✅';
      case ConnectionStatus.offline:
        return '❌';
      case ConnectionStatus.connecting:
        return '🔄';
      case ConnectionStatus.disconnected:
        return '⚠️';
      case ConnectionStatus.unreachable:
        return '🚫';
    }
  }

  @override
  String toString() {
    return 'EdgeNode(nodeId: $nodeId, name: $nodeName, status: $connectionStatus $healthStatusEmoji)';
  }
}

/// 处理选项模型
class ProcessingOptions {
  final String? videoCodec;
  final String? audioCodec;
  final int? resolutionWidth;
  final int? resolutionHeight;
  final int? frameRate;
  final int? bitrateKbps;
  final bool? aiEnhancementsEnabled;
  final String? enhancementType;
  final bool? bandwidthOptimizationEnabled;
  final int? compressionLevel;
  final int? priorityLevel;
  final int? maxRetries;

  ProcessingOptions({
    this.videoCodec = 'H.264/AVC',
    this.audioCodec = 'AAC',
    this.resolutionWidth = 1280,
    this.resolutionHeight = 720,
    this.frameRate = 30,
    this.bitrateKbps = 2500,
    this.aiEnhancementsEnabled = false,
    this.enhancementType,
    this.bandwidthOptimizationEnabled = true,
    this.compressionLevel = 5,
    this.priorityLevel = 5,
    this.maxRetries = 3,
  });

  Map<String, dynamic> toJson() {
    return {
      'videoCodec': videoCodec,
      'audioCodec': audioCodec,
      'resolutionWidth': resolutionWidth,
      'resolutionHeight': resolutionHeight,
      'frameRate': frameRate,
      'bitrateKbps': bitrateKbps,
      'aiEnhancementsEnabled': aiEnhancementsEnabled,
      'enhancementType': enhancementType,
      'bandwidthOptimizationEnabled': bandwidthOptimizationEnabled,
      'compressionLevel': compressionLevel,
      'priorityLevel': priorityLevel,
      'maxRetries': maxRetries,
    };
  }

  static ProcessingOptions get defaultOptions => ProcessingOptions();

  static ProcessingOptions get hdOptions => ProcessingOptions(
        videoCodec: 'H.264/AVC',
        audioCodec: 'AAC',
        resolutionWidth: 1920,
        resolutionHeight: 1080,
        frameRate: 30,
        bitrateKbps: 5000,
        aiEnhancementsEnabled: true,
        bandwidthOptimizationEnabled: true,
        compressionLevel: 6,
        priorityLevel: 7,
      );

  static ProcessingOptions get lowBandwidthOptions => ProcessingOptions(
        videoCodec: 'H.264/AVC',
        audioCodec: 'Opus',
        resolutionWidth: 640,
        resolutionHeight: 360,
        frameRate: 15,
        bitrateKbps: 500,
        aiEnhancementsEnabled: false,
        bandwidthOptimizationEnabled: true,
        compressionLevel: 8,
        priorityLevel: 5,
      );
}

/// 系统统计信息模型
class SystemStatistics {
  final int totalTasks;
  final int activeTasks;
  final int completedTasks;
  final int failedTasks;
  final int availableNodes;
  final int totalNodeCapacity;
  final int usedNodeCapacity;
  final double averageProcessingTimeMs;
  final double averageQualityScore;

  SystemStatistics({
    required this.totalTasks,
    required this.activeTasks,
    required this.completedTasks,
    required this.failedTasks,
    required this.availableNodes,
    required this.totalNodeCapacity,
    required this.usedNodeCapacity,
    required this.averageProcessingTimeMs,
    required this.averageQualityScore,
  });

  factory SystemStatistics.fromJson(Map<String, dynamic> json) {
    return SystemStatistics(
      totalTasks: json['totalTasks'] ?? 0,
      activeTasks: json['activeTasks'] ?? 0,
      completedTasks: json['completedTasks'] ?? 0,
      failedTasks: json['failedTasks'] ?? 0,
      availableNodes: json['availableNodes'] ?? 0,
      totalNodeCapacity: json['totalNodeCapacity'] ?? 0,
      usedNodeCapacity: json['usedNodeCapacity'] ?? 0,
      averageProcessingTimeMs: json['averageProcessingTimeMs']?.toDouble() ?? 0.0,
      averageQualityScore: json['averageQualityScore']?.toDouble() ?? 0.0,
    );
  }

  double get successRate {
    if (completedTasks + failedTasks == 0) return 0.0;
    return (completedTasks / (completedTasks + failedTasks)) * 100;
  }

  double get capacityUsagePercentage {
    if (totalNodeCapacity == 0) return 0.0;
    return (usedNodeCapacity / totalNodeCapacity) * 100;
  }
}
