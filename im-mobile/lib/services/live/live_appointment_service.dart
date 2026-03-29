import 'dart:async';
import 'package:flutter/material.dart';
import '../../models/live/live_room_model.dart';
import '../../services/live/mini_program_live_service.dart';
import '../../services/live/live_commerce_service.dart';
import '../../utils/logger.dart';

/// 直播预约服务
/// 提供预约直播、开播提醒、直播回放等功能
class LiveAppointmentService extends ChangeNotifier {
  static final LiveAppointmentService _instance = LiveAppointmentService._internal();
  factory LiveAppointmentService() => _instance;
  LiveAppointmentService._internal();

  // 直播服务实例
  final MiniProgramLiveService _liveService = MiniProgramLiveService();
  final LiveCommerceService _commerceService = LiveCommerceService();

  // 预约列表
  List<LiveAppointmentModel> _appointments = [];
  List<LiveAppointmentModel> get appointments => List.unmodifiable(_appointments);

  // 即将开播的预约
  List<LiveAppointmentModel> get upcomingAppointments {
    final now = DateTime.now();
    return _appointments
        .where((a) => a.scheduledStartTime.isAfter(now) && !a.isReminded)
        .toList()
      ..sort((a, b) => a.scheduledStartTime.compareTo(b.scheduledStartTime));
  }

  // 历史预约
  List<LiveAppointmentModel> get historyAppointments {
    final now = DateTime.now();
    return _appointments
        .where((a) => a.scheduledStartTime.isBefore(now) || a.isReminded)
        .toList()
      ..sort((a, b) => b.scheduledStartTime.compareTo(a.scheduledStartTime));
  }

  // 提醒定时器
  Timer? _reminderTimer;

  // 错误信息
  String? _errorMessage;
  String? get errorMessage => _errorMessage;

  // 加载状态
  bool _isLoading = false;
  bool get isLoading => _isLoading;

  /// 初始化服务
  Future<void> initialize() async {
    Logger.log('LiveAppointmentService', 'Initializing appointment service...');
    
    // 加载预约列表
    await loadAppointments();
    
    // 启动提醒定时器
    _startReminderTimer();
    
    Logger.log('LiveAppointmentService', 'Appointment service initialized');
  }

  /// 加载预约列表
  Future<void> loadAppointments() async {
    _isLoading = true;
    notifyListeners();

    try {
      // 从服务器获取预约列表
      // TODO: 实现API调用
      // final response = await http.get(...);
      
      // 模拟数据
      _appointments = [];
      
      Logger.log('LiveAppointmentService', 'Appointments loaded: ${_appointments.length}');
    } catch (e, stackTrace) {
      Logger.error('LiveAppointmentService', 'Failed to load appointments', e, stackTrace);
      _errorMessage = '加载预约列表失败';
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  /// 预约直播
  Future<bool> createAppointment({
    required String roomId,
    required String title,
    required DateTime scheduledStartTime,
    String? coverImage,
    String? description,
    String? streamerId,
    String? streamerName,
    DateTime? remindTime,
  }) async {
    Logger.log('LiveAppointmentService', 'Creating appointment for room: $roomId');
    
    try {
      // 检查是否已预约
      final existing = _appointments.any((a) => a.roomId == roomId);
      if (existing) {
        _errorMessage = '您已预约过该直播';
        notifyListeners();
        return false;
      }

      // 创建预约
      final appointment = LiveAppointmentModel(
        id: DateTime.now().millisecondsSinceEpoch.toString(),
        roomId: roomId,
        title: title,
        coverImage: coverImage,
        scheduledStartTime: scheduledStartTime,
        description: description,
        streamerId: streamerId,
        streamerName: streamerName,
        isReminded: false,
        remindTime: remindTime ?? scheduledStartTime.subtract(Duration(minutes: 15)),
        createdAt: DateTime.now(),
        updatedAt: DateTime.now(),
      );

      // 保存到服务器
      // TODO: 实现API调用
      // await http.post(...);

      _appointments.add(appointment);
      notifyListeners();

      Logger.log('LiveAppointmentService', 'Appointment created: ${appointment.id}');
      return true;
    } catch (e, stackTrace) {
      Logger.error('LiveAppointmentService', 'Failed to create appointment', e, stackTrace);
      _errorMessage = '预约失败: $e';
      notifyListeners();
      return false;
    }
  }

  /// 取消预约
  Future<bool> cancelAppointment(String appointmentId) async {
    Logger.log('LiveAppointmentService', 'Cancelling appointment: $appointmentId');
    
    try {
      // 删除服务器预约
      // TODO: 实现API调用
      // await http.delete(...);

      _appointments.removeWhere((a) => a.id == appointmentId);
      notifyListeners();

      Logger.log('LiveAppointmentService', 'Appointment cancelled: $appointmentId');
      return true;
    } catch (e, stackTrace) {
      Logger.error('LiveAppointmentService', 'Failed to cancel appointment', e, stackTrace);
      _errorMessage = '取消预约失败';
      notifyListeners();
      return false;
    }
  }

  /// 启动提醒定时器
  void _startReminderTimer() {
    _reminderTimer?.cancel();
    _reminderTimer = Timer.periodic(Duration(minutes: 1), (timer) {
      _checkAndSendReminders();
    });
  }

  /// 检查并发送提醒
  void _checkAndSendReminders() {
    final now = DateTime.now();
    
    for (final appointment in _appointments) {
      if (appointment.isReminded) continue;
      
      final remindTime = appointment.remindTime ?? 
          appointment.scheduledStartTime.subtract(Duration(minutes: 15));
      
      if (now.isAfter(remindTime) && now.isBefore(appointment.scheduledStartTime)) {
        // 发送提醒
        _sendReminder(appointment);
      }
    }
  }

  /// 发送提醒
  void _sendReminder(LiveAppointmentModel appointment) {
    Logger.log('LiveAppointmentService', 'Sending reminder for: ${appointment.title}');
    
    // 本地通知
    // TODO: 调用本地通知插件
    
    // 标记已提醒
    final index = _appointments.indexWhere((a) => a.id == appointment.id);
    if (index >= 0) {
      _appointments[index] = LiveAppointmentModel(
        id: appointment.id,
        roomId: appointment.roomId,
        title: appointment.title,
        coverImage: appointment.coverImage,
        scheduledStartTime: appointment.scheduledStartTime,
        description: appointment.description,
        streamerId: appointment.streamerId,
        streamerName: appointment.streamerName,
        isReminded: true,
        remindTime: appointment.remindTime,
        createdAt: appointment.createdAt,
        updatedAt: DateTime.now(),
      );
      notifyListeners();
    }
  }

  /// 获取直播回放列表
  Future<List<LiveReplay>> getLiveReplays(String roomId) async {
    Logger.log('LiveAppointmentService', 'Getting live replays for room: $roomId');
    
    try {
      // TODO: 实现API调用
      // final response = await http.get(...);
      
      // 模拟数据
      return [];
    } catch (e, stackTrace) {
      Logger.error('LiveAppointmentService', 'Failed to get live replays', e, stackTrace);
      return [];
    }
  }

  /// 获取精彩片段
  Future<List<LiveHighlight>> getLiveHighlights(String roomId) async {
    Logger.log('LiveAppointmentService', 'Getting live highlights for room: $roomId');
    
    try {
      // TODO: 实现API调用
      // final response = await http.get(...);
      
      // 模拟数据
      return [];
    } catch (e, stackTrace) {
      Logger.error('LiveAppointmentService', 'Failed to get live highlights', e, stackTrace);
      return [];
    }
  }

  /// 清理资源
  @override
  void dispose() {
    _reminderTimer?.cancel();
    super.dispose();
  }
}

/// 直播回放
class LiveReplay {
  final String id;
  final String roomId;
  final String title;
  final String? coverImage;
  final String? videoUrl;
  final Duration duration;
  final int viewCount;
  final int likeCount;
  final DateTime createdAt;

  LiveReplay({
    required this.id,
    required this.roomId,
    required this.title,
    this.coverImage,
    this.videoUrl,
    required this.duration,
    this.viewCount = 0,
    this.likeCount = 0,
    required this.createdAt,
  });

  factory LiveReplay.fromJson(Map<String, dynamic> json) {
    return LiveReplay(
      id: json['id'] ?? '',
      roomId: json['roomId'] ?? '',
      title: json['title'] ?? '',
      coverImage: json['coverImage'],
      videoUrl: json['videoUrl'],
      duration: Duration(seconds: json['duration'] ?? 0),
      viewCount: json['viewCount'] ?? 0,
      likeCount: json['likeCount'] ?? 0,
      createdAt: DateTime.parse(json['createdAt']),
    );
  }
}

/// 直播精彩片段
class LiveHighlight {
  final String id;
  final String roomId;
  final String title;
  final String? coverImage;
  final String? videoUrl;
  final Duration startTime;
  final Duration endTime;
  final Duration duration;
  final String? description;
  final DateTime createdAt;

  LiveHighlight({
    required this.id,
    required this.roomId,
    required this.title,
    this.coverImage,
    this.videoUrl,
    required this.startTime,
    required this.endTime,
    required this.duration,
    this.description,
    required this.createdAt,
  });

  factory LiveHighlight.fromJson(Map<String, dynamic> json) {
    return LiveHighlight(
      id: json['id'] ?? '',
      roomId: json['roomId'] ?? '',
      title: json['title'] ?? '',
      coverImage: json['coverImage'],
      videoUrl: json['videoUrl'],
      startTime: Duration(seconds: json['startTime'] ?? 0),
      endTime: Duration(seconds: json['endTime'] ?? 0),
      duration: Duration(seconds: json['duration'] ?? 0),
      description: json['description'],
      createdAt: DateTime.parse(json['createdAt']),
    );
  }
}
