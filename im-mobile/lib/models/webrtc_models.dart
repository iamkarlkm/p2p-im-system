/// WebRTC实时流媒体传输 - 移动端数据模型
/// 功能#212 - WebRTC Mobile Models
/// 创建时间: 2026-03-27 01:40

import 'dart:convert';

/// 通话类型枚举
enum CallType {
  audio,      // 音频通话
  video,      // 视频通话
  screenShare, // 屏幕共享
}

/// 通话状态枚举
enum CallState {
  idle,           // 空闲
  calling,        // 呼叫中
  ringing,        // 响铃中
  connecting,     // 连接中
  connected,      // 已连接
  reconnecting,   // 重连中
  ended,          // 已结束
  failed,         // 失败
}

/// 网络质量等级
enum NetworkQuality {
  excellent,  // 优秀 (< 100ms)
  good,       // 良好 (100-200ms)
  fair,       // 一般 (200-400ms)
  poor,       // 较差 (400-800ms)
  bad,        // 很差 (> 800ms)
}

/// 美颜滤镜类型
enum BeautyFilter {
  none,       // 无滤镜
  natural,    // 自然
  smooth,     // 磨皮
  whiten,     // 美白
  vintage,    // 复古
  fresh,      // 清新
}

/// 视频质量设置
enum VideoQuality {
  low,        // 低 (480p)
  medium,     // 中 (720p)
  high,       // 高 (1080p)
  ultra,      // 超清 (1440p)
}

/// 会话信息模型
class WebRTCSession {
  final String sessionId;
  final String callerId;
  final String callerName;
  final String? callerAvatar;
  final String calleeId;
  final String calleeName;
  final String? calleeAvatar;
  final CallType callType;
  CallState state;
  DateTime? startTime;
  DateTime? endTime;
  final DateTime createdAt;
  String? roomId;
  Map<String, dynamic>? metadata;

  WebRTCSession({
    required this.sessionId,
    required this.callerId,
    required this.callerName,
    this.callerAvatar,
    required this.calleeId,
    required this.calleeName,
    this.calleeAvatar,
    required this.callType,
    this.state = CallState.idle,
    this.startTime,
    this.endTime,
    required this.createdAt,
    this.roomId,
    this.metadata,
  });

  factory WebRTCSession.fromJson(Map<String, dynamic> json) {
    return WebRTCSession(
      sessionId: json['sessionId'],
      callerId: json['callerId'],
      callerName: json['callerName'],
      callerAvatar: json['callerAvatar'],
      calleeId: json['calleeId'],
      calleeName: json['calleeName'],
      calleeAvatar: json['calleeAvatar'],
      callType: CallType.values.byName(json['callType']),
      state: CallState.values.byName(json['state']),
      startTime: json['startTime'] != null 
          ? DateTime.parse(json['startTime']) 
          : null,
      endTime: json['endTime'] != null 
          ? DateTime.parse(json['endTime']) 
          : null,
      createdAt: DateTime.parse(json['createdAt']),
      roomId: json['roomId'],
      metadata: json['metadata'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'sessionId': sessionId,
      'callerId': callerId,
      'callerName': callerName,
      'callerAvatar': callerAvatar,
      'calleeId': calleeId,
      'calleeName': calleeName,
      'calleeAvatar': calleeAvatar,
      'callType': callType.name,
      'state': state.name,
      'startTime': startTime?.toIso8601String(),
      'endTime': endTime?.toIso8601String(),
      'createdAt': createdAt.toIso8601String(),
      'roomId': roomId,
      'metadata': metadata,
    };
  }

  /// 获取通话时长（秒）
  int? get durationInSeconds {
    if (startTime == null) return null;
    final end = endTime ?? DateTime.now();
    return end.difference(startTime!).inSeconds;
  }

  /// 获取格式化时长
  String get formattedDuration {
    final seconds = durationInSeconds;
    if (seconds == null) return '00:00';
    final minutes = seconds ~/ 60;
    final remainingSeconds = seconds % 60;
    return '${minutes.toString().padLeft(2, '0')}:${remainingSeconds.toString().padLeft(2, '0')}';
  }

  /// 是否是呼出方
  bool isCaller(String userId) => callerId == userId;

  /// 获取对方信息
  String getPeerName(String userId) {
    return isCaller(userId) ? calleeName : callerName;
  }

  String? getPeerAvatar(String userId) {
    return isCaller(userId) ? calleeAvatar : callerAvatar;
  }
}

/// 参与者信息模型
class Participant {
  final String userId;
  final String userName;
  final String? avatar;
  bool isAudioEnabled;
  bool isVideoEnabled;
  bool isScreenSharing;
  NetworkQuality networkQuality;
  DateTime joinedAt;
  Map<String, dynamic>? metadata;

  Participant({
    required this.userId,
    required this.userName,
    this.avatar,
    this.isAudioEnabled = true,
    this.isVideoEnabled = true,
    this.isScreenSharing = false,
    this.networkQuality = NetworkQuality.excellent,
    required this.joinedAt,
    this.metadata,
  });

  factory Participant.fromJson(Map<String, dynamic> json) {
    return Participant(
      userId: json['userId'],
      userName: json['userName'],
      avatar: json['avatar'],
      isAudioEnabled: json['isAudioEnabled'] ?? true,
      isVideoEnabled: json['isVideoEnabled'] ?? true,
      isScreenSharing: json['isScreenSharing'] ?? false,
      networkQuality: NetworkQuality.values.byName(
          json['networkQuality'] ?? 'excellent'),
      joinedAt: DateTime.parse(json['joinedAt']),
      metadata: json['metadata'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'userName': userName,
      'avatar': avatar,
      'isAudioEnabled': isAudioEnabled,
      'isVideoEnabled': isVideoEnabled,
      'isScreenSharing': isScreenSharing,
      'networkQuality': networkQuality.name,
      'joinedAt': joinedAt.toIso8601String(),
      'metadata': metadata,
    };
  }
}

/// ICE服务器配置
class IceServer {
  final String urls;
  final String? username;
  final String? credential;

  IceServer({
    required this.urls,
    this.username,
    this.credential,
  });

  factory IceServer.fromJson(Map<String, dynamic> json) {
    return IceServer(
      urls: json['urls'],
      username: json['username'],
      credential: json['credential'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'urls': urls,
      'username': username,
      'credential': credential,
    };
  }
}

/// WebRTC配置
class WebRTCConfig {
  final List<IceServer> iceServers;
  final bool enableAudio;
  final bool enableVideo;
  final VideoQuality videoQuality;
  final bool enableBeautyFilter;
  final BeautyFilter beautyFilter;
  final bool enableNoiseSuppression;
  final bool enableEchoCancellation;
  final bool enableAutoGainControl;
  final Map<String, dynamic>? metadata;

  WebRTCConfig({
    required this.iceServers,
    this.enableAudio = true,
    this.enableVideo = true,
    this.videoQuality = VideoQuality.medium,
    this.enableBeautyFilter = false,
    this.beautyFilter = BeautyFilter.natural,
    this.enableNoiseSuppression = true,
    this.enableEchoCancellation = true,
    this.enableAutoGainControl = true,
    this.metadata,
  });

  factory WebRTCConfig.defaultConfig() {
    return WebRTCConfig(
      iceServers: [
        IceServer(urls: 'stun:stun.l.google.com:19302'),
        IceServer(urls: 'stun:stun1.l.google.com:19302'),
      ],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'iceServers': iceServers.map((e) => e.toJson()).toList(),
      'enableAudio': enableAudio,
      'enableVideo': enableVideo,
      'videoQuality': videoQuality.name,
      'enableBeautyFilter': enableBeautyFilter,
      'beautyFilter': beautyFilter.name,
      'enableNoiseSuppression': enableNoiseSuppression,
      'enableEchoCancellation': enableEchoCancellation,
      'enableAutoGainControl': enableAutoGainControl,
      'metadata': metadata,
    };
  }

  /// 获取视频分辨率
  Map<String, int> get videoResolution {
    switch (videoQuality) {
      case VideoQuality.low:
        return {'width': 640, 'height': 480};
      case VideoQuality.medium:
        return {'width': 1280, 'height': 720};
      case VideoQuality.high:
        return {'width': 1920, 'height': 1080};
      case VideoQuality.ultra:
        return {'width': 2560, 'height': 1440};
    }
  }

  /// 获取视频码率 (kbps)
  int get videoBitrate {
    switch (videoQuality) {
      case VideoQuality.low:
        return 500;
      case VideoQuality.medium:
        return 1500;
      case VideoQuality.high:
        return 3000;
      case VideoQuality.ultra:
        return 5000;
    }
  }
}

/// 网络统计信息
class NetworkStats {
  final int rtt;                    // 往返延迟 (ms)
  final double packetLossRate;      // 丢包率 (%)
  final int bitrate;                // 码率 (kbps)
  final int jitter;                 // 抖动 (ms)
  final int bytesSent;              // 发送字节数
  final int bytesReceived;          // 接收字节数
  final int audioLevel;             // 音频电平 (0-100)
  final int frameRate;              // 帧率
  final DateTime timestamp;

  NetworkStats({
    required this.rtt,
    required this.packetLossRate,
    required this.bitrate,
    required this.jitter,
    required this.bytesSent,
    required this.bytesReceived,
    required this.audioLevel,
    required this.frameRate,
    required this.timestamp,
  });

  factory NetworkStats.fromJson(Map<String, dynamic> json) {
    return NetworkStats(
      rtt: json['rtt'] ?? 0,
      packetLossRate: (json['packetLossRate'] ?? 0).toDouble(),
      bitrate: json['bitrate'] ?? 0,
      jitter: json['jitter'] ?? 0,
      bytesSent: json['bytesSent'] ?? 0,
      bytesReceived: json['bytesReceived'] ?? 0,
      audioLevel: json['audioLevel'] ?? 0,
      frameRate: json['frameRate'] ?? 0,
      timestamp: DateTime.parse(json['timestamp']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'rtt': rtt,
      'packetLossRate': packetLossRate,
      'bitrate': bitrate,
      'jitter': jitter,
      'bytesSent': bytesSent,
      'bytesReceived': bytesReceived,
      'audioLevel': audioLevel,
      'frameRate': frameRate,
      'timestamp': timestamp.toIso8601String(),
    };
  }

  /// 获取网络质量等级
  NetworkQuality get quality {
    if (rtt < 100 && packetLossRate < 1) {
      return NetworkQuality.excellent;
    } else if (rtt < 200 && packetLossRate < 3) {
      return NetworkQuality.good;
    } else if (rtt < 400 && packetLossRate < 5) {
      return NetworkQuality.fair;
    } else if (rtt < 800 && packetLossRate < 10) {
      return NetworkQuality.poor;
    } else {
      return NetworkQuality.bad;
    }
  }

  /// 获取网络质量描述
  String get qualityDescription {
    switch (quality) {
      case NetworkQuality.excellent:
        return '网络优秀';
      case NetworkQuality.good:
        return '网络良好';
      case NetworkQuality.fair:
        return '网络一般';
      case NetworkQuality.poor:
        return '网络较差';
      case NetworkQuality.bad:
        return '网络很差';
    }
  }
}

/// 美颜滤镜参数
class BeautyParams {
  final double smoothLevel;     // 磨皮程度 (0-1)
  final double whiteLevel;      // 美白程度 (0-1)
  final double thinFaceLevel;   // 瘦脸程度 (0-1)
  final double bigEyeLevel;     // 大眼程度 (0-1)
  final double brightness;      // 亮度 (-1 to 1)
  final double contrast;        // 对比度 (0-2)
  final double saturation;      // 饱和度 (0-2)

  BeautyParams({
    this.smoothLevel = 0.5,
    this.whiteLevel = 0.3,
    this.thinFaceLevel = 0.2,
    this.bigEyeLevel = 0.1,
    this.brightness = 0.0,
    this.contrast = 1.0,
    this.saturation = 1.0,
  });

  factory BeautyParams.defaultParams() {
    return BeautyParams();
  }

  Map<String, dynamic> toJson() {
    return {
      'smoothLevel': smoothLevel,
      'whiteLevel': whiteLevel,
      'thinFaceLevel': thinFaceLevel,
      'bigEyeLevel': bigEyeLevel,
      'brightness': brightness,
      'contrast': contrast,
      'saturation': saturation,
    };
  }
}

/// 弱网适配策略
enum WeakNetworkStrategy {
  auto,           // 自动适配
  reduceQuality,  // 降低质量
  disableVideo,   // 关闭视频
  audioOnly,      // 仅音频
}

/// 弱网适配配置
class WeakNetworkConfig {
  final bool enableAutoAdaptation;
  final WeakNetworkStrategy strategy;
  final int rttThreshold;           // 延迟阈值 (ms)
  final double packetLossThreshold; // 丢包率阈值 (%)
  final int reduceQualityRtt;       // 降质延迟阈值
  final int disableVideoRtt;        // 关视频延迟阈值
  final int audioOnlyRtt;           // 纯音频延迟阈值

  WeakNetworkConfig({
    this.enableAutoAdaptation = true,
    this.strategy = WeakNetworkStrategy.auto,
    this.rttThreshold = 300,
    this.packetLossThreshold = 5.0,
    this.reduceQualityRtt = 300,
    this.disableVideoRtt = 600,
    this.audioOnlyRtt = 1000,
  });

  factory WeakNetworkConfig.defaultConfig() {
    return WeakNetworkConfig();
  }

  Map<String, dynamic> toJson() {
    return {
      'enableAutoAdaptation': enableAutoAdaptation,
      'strategy': strategy.name,
      'rttThreshold': rttThreshold,
      'packetLossThreshold': packetLossThreshold,
      'reduceQualityRtt': reduceQualityRtt,
      'disableVideoRtt': disableVideoRtt,
      'audioOnlyRtt': audioOnlyRtt,
    };
  }
}

/// 信令消息类型
enum SignalingType {
  offer,
  answer,
  iceCandidate,
  join,
  leave,
  mute,
  unmute,
  screenShareStart,
  screenShareStop,
}

/// 信令消息模型
class SignalingMessage {
  final String type;
  final String sessionId;
  final String fromUserId;
  final String toUserId;
  final dynamic data;
  final DateTime timestamp;

  SignalingMessage({
    required this.type,
    required this.sessionId,
    required this.fromUserId,
    required this.toUserId,
    required this.data,
    required this.timestamp,
  });

  factory SignalingMessage.fromJson(Map<String, dynamic> json) {
    return SignalingMessage(
      type: json['type'],
      sessionId: json['sessionId'],
      fromUserId: json['fromUserId'],
      toUserId: json['toUserId'],
      data: json['data'],
      timestamp: DateTime.parse(json['timestamp']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'type': type,
      'sessionId': sessionId,
      'fromUserId': fromUserId,
      'toUserId': toUserId,
      'data': data,
      'timestamp': timestamp.toIso8601String(),
    };
  }
}

/// 通话记录
class CallHistory {
  final String id;
  final String sessionId;
  final String peerId;
  final String peerName;
  final String? peerAvatar;
  final CallType callType;
  final bool isOutgoing;
  final bool isConnected;
  final DateTime startTime;
  final DateTime? endTime;
  final int? duration;
  final String? failureReason;

  CallHistory({
    required this.id,
    required this.sessionId,
    required this.peerId,
    required this.peerName,
    this.peerAvatar,
    required this.callType,
    required this.isOutgoing,
    required this.isConnected,
    required this.startTime,
    this.endTime,
    this.duration,
    this.failureReason,
  });

  factory CallHistory.fromJson(Map<String, dynamic> json) {
    return CallHistory(
      id: json['id'],
      sessionId: json['sessionId'],
      peerId: json['peerId'],
      peerName: json['peerName'],
      peerAvatar: json['peerAvatar'],
      callType: CallType.values.byName(json['callType']),
      isOutgoing: json['isOutgoing'],
      isConnected: json['isConnected'],
      startTime: DateTime.parse(json['startTime']),
      endTime: json['endTime'] != null 
          ? DateTime.parse(json['endTime']) 
          : null,
      duration: json['duration'],
      failureReason: json['failureReason'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'sessionId': sessionId,
      'peerId': peerId,
      'peerName': peerName,
      'peerAvatar': peerAvatar,
      'callType': callType.name,
      'isOutgoing': isOutgoing,
      'isConnected': isConnected,
      'startTime': startTime.toIso8601String(),
      'endTime': endTime?.toIso8601String(),
      'duration': duration,
      'failureReason': failureReason,
    };
  }

  /// 获取格式化时长
  String get formattedDuration {
    if (duration == null) return '00:00';
    final minutes = duration! ~/ 60;
    final seconds = duration! % 60;
    return '${minutes.toString().padLeft(2, '0')}:${seconds.toString().padLeft(2, '0')}';
  }
}

/// 扩展方法：枚举转中文
extension CallTypeExtension on CallType {
  String get displayName {
    switch (this) {
      case CallType.audio:
        return '语音通话';
      case CallType.video:
        return '视频通话';
      case CallType.screenShare:
        return '屏幕共享';
    }
  }
}

extension CallStateExtension on CallState {
  String get displayName {
    switch (this) {
      case CallState.idle:
        return '空闲';
      case CallState.calling:
        return '呼叫中';
      case CallState.ringing:
        return '响铃中';
      case CallState.connecting:
        return '连接中';
      case CallState.connected:
        return '通话中';
      case CallState.reconnecting:
        return '重连中';
      case CallState.ended:
        return '已结束';
      case CallState.failed:
        return '失败';
    }
  }
}

extension NetworkQualityExtension on NetworkQuality {
  String get displayName {
    switch (this) {
      case NetworkQuality.excellent:
        return '优秀';
      case NetworkQuality.good:
        return '良好';
      case NetworkQuality.fair:
        return '一般';
      case NetworkQuality.poor:
        return '较差';
      case NetworkQuality.bad:
        return '很差';
    }
  }

  int get level {
    switch (this) {
      case NetworkQuality.excellent:
        return 5;
      case NetworkQuality.good:
        return 4;
      case NetworkQuality.fair:
        return 3;
      case NetworkQuality.poor:
        return 2;
      case NetworkQuality.bad:
        return 1;
    }
  }
}

extension BeautyFilterExtension on BeautyFilter {
  String get displayName {
    switch (this) {
      case BeautyFilter.none:
        return '无';
      case BeautyFilter.natural:
        return '自然';
      case BeautyFilter.smooth:
        return '磨皮';
      case BeautyFilter.whiten:
        return '美白';
      case BeautyFilter.vintage:
        return '复古';
      case BeautyFilter.fresh:
        return '清新';
    }
  }
}

extension VideoQualityExtension on VideoQuality {
  String get displayName {
    switch (this) {
      case VideoQuality.low:
        return '流畅 (480p)';
      case VideoQuality.medium:
        return '标清 (720p)';
      case VideoQuality.high:
        return '高清 (1080p)';
      case VideoQuality.ultra:
        return '超清 (1440p)';
    }
  }
}
