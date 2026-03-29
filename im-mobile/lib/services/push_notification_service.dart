import 'dart:async';
import 'dart:convert';
import 'dart:math';
import 'package:flutter/foundation.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/push_message.dart';

class PushNotificationService extends ChangeNotifier {
  static final PushNotificationService _instance = PushNotificationService._internal();
  factory PushNotificationService() => _instance;
  PushNotificationService._internal();

  final FlutterLocalNotificationsPlugin _notifications = FlutterLocalNotificationsPlugin();
  
  bool _isInitialized = false;
  bool _notificationsEnabled = true;
  bool _soundEnabled = true;
  bool _vibrationEnabled = true;
  String _notificationPriority = 'normal';
  
  final StreamController<PushMessage> _messageController = StreamController<PushMessage>.broadcast();
  final List<PushMessage> _pendingMessages = [];
  final Set<String> _mutedConversations = {};
  final Set<String> _mutedUsers = {};
  
  Stream<PushMessage> get messageStream => _messageController.stream;
  bool get isInitialized => _isInitialized;
  bool get notificationsEnabled => _notificationsEnabled;
  bool get soundEnabled => _soundEnabled;
  bool get vibrationEnabled => _vibrationEnabled;
  List<PushMessage> get pendingMessages => List.unmodifiable(_pendingMessages);

  static const String _prefsKeyEnabled = 'push_notifications_enabled';
  static const String _prefsKeySound = 'push_sound_enabled';
  static const String _prefsKeyVibration = 'push_vibration_enabled';
  static const String _prefsKeyMutedConv = 'push_muted_conversations';
  static const String _prefsKeyMutedUsers = 'push_muted_users';

  static const AndroidNotificationChannel _channelMessages = AndroidNotificationChannel(
    'im_messages',
    '消息通知',
    description: '接收即时消息通知',
    importance: Importance.high,
    playSound: true,
    enableVibration: true,
  );

  static const AndroidNotificationChannel _channelCalls = AndroidNotificationChannel(
    'im_calls',
    '通话通知',
    description: '接收语音/视频通话通知',
    importance: Importance.max,
    playSound: true,
    enableVibration: true,
  );

  static const AndroidNotificationChannel _channelSystem = AndroidNotificationChannel(
    'im_system',
    '系统通知',
    description: '接收系统消息和更新',
    importance: Importance.defaultImportance,
    playSound: false,
    enableVibration: false,
  );

  Future<void> initialize() async {
    if (_isInitialized) return;

    await _loadSettings();

    const androidSettings = AndroidInitializationSettings('@mipmap/ic_launcher');
    const iosSettings = DarwinInitializationSettings(
      requestAlertPermission: true,
      requestBadgePermission: true,
      requestSoundPermission: true,
    );
    const initSettings = InitializationSettings(
      android: androidSettings,
      iOS: iosSettings,
    );

    await _notifications.initialize(
      initSettings,
      onDidReceiveNotificationResponse: _onNotificationTapped,
    );

    await _createNotificationChannels();

    _isInitialized = true;
    notifyListeners();
  }

  Future<void> _createNotificationChannels() async {
    final androidPlugin = _notifications.resolvePlatformSpecificImplementation<
        AndroidFlutterLocalNotificationsPlugin>();
    
    if (androidPlugin != null) {
      await androidPlugin.createNotificationChannel(_channelMessages);
      await androidPlugin.createNotificationChannel(_channelCalls);
      await androidPlugin.createNotificationChannel(_channelSystem);
    }
  }

  Future<void> _loadSettings() async {
    final prefs = await SharedPreferences.getInstance();
    _notificationsEnabled = prefs.getBool(_prefsKeyEnabled) ?? true;
    _soundEnabled = prefs.getBool(_prefsKeySound) ?? true;
    _vibrationEnabled = prefs.getBool(_prefsKeyVibration) ?? true;
    
    final mutedConv = prefs.getStringList(_prefsKeyMutedConv);
    if (mutedConv != null) _mutedConversations.addAll(mutedConv);
    
    final mutedUsers = prefs.getStringList(_prefsKeyMutedUsers);
    if (mutedUsers != null) _mutedUsers.addAll(mutedUsers);
  }

  Future<void> _saveSettings() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool(_prefsKeyEnabled, _notificationsEnabled);
    await prefs.setBool(_prefsKeySound, _soundEnabled);
    await prefs.setBool(_prefsKeyVibration, _vibrationEnabled);
    await prefs.setStringList(_prefsKeyMutedConv, _mutedConversations.toList());
    await prefs.setStringList(_prefsKeyMutedUsers, _mutedUsers.toList());
  }

  void _onNotificationTapped(NotificationResponse response) {
    if (response.payload != null) {
      try {
        final data = jsonDecode(response.payload!);
        final message = PushMessage.fromJson(data);
        _messageController.add(message);
      } catch (e) {
        debugPrint('解析通知数据失败: $e');
      }
    }
  }

  Future<void> showMessageNotification(PushMessage message) async {
    if (!_notificationsEnabled) return;
    if (_mutedConversations.contains(message.conversationId)) return;
    if (_mutedUsers.contains(message.senderId)) return;

    final androidDetails = AndroidNotificationDetails(
      _channelMessages.id,
      _channelMessages.name,
      channelDescription: _channelMessages.description,
      importance: Importance.high,
      priority: Priority.high,
      showWhen: true,
      enableVibration: _vibrationEnabled,
      playSound: _soundEnabled,
      icon: '@mipmap/ic_launcher',
      largeIcon: message.senderAvatar != null
          ? FilePathAndroidBitmap(message.senderAvatar!)
          : null,
      styleInformation: BigTextStyleInformation(
        message.content,
        contentTitle: message.senderName,
        summaryText: '新消息',
      ),
      groupKey: message.conversationId,
      setAsGroupSummary: false,
    );

    final iosDetails = DarwinNotificationDetails(
      presentAlert: true,
      presentBadge: true,
      presentSound: _soundEnabled,
    );

    final details = NotificationDetails(
      android: androidDetails,
      iOS: iosDetails,
    );

    await _notifications.show(
      _generateNotificationId(),
      message.senderName,
      message.content,
      details,
      payload: jsonEncode(message.toJson()),
    );

    _pendingMessages.add(message);
    notifyListeners();
  }

  Future<void> showCallNotification({
    required String callerId,
    required String callerName,
    required String callType,
    required String callId,
  }) async {
    if (!_notificationsEnabled) return;

    final androidDetails = AndroidNotificationDetails(
      _channelCalls.id,
      _channelCalls.name,
      channelDescription: _channelCalls.description,
      importance: Importance.max,
      priority: Priority.max,
      fullScreenIntent: true,
      category: AndroidNotificationCategory.call,
      actions: [
        const AndroidNotificationAction(
          'accept_call',
          '接听',
          showsUserInterface: true,
        ),
        const AndroidNotificationAction(
          'decline_call',
          '拒绝',
        ),
      ],
    );

    final iosDetails = const DarwinNotificationDetails(
      presentAlert: true,
      presentBadge: true,
      presentSound: true,
    );

    final details = NotificationDetails(
      android: androidDetails,
      iOS: iosDetails,
    );

    await _notifications.show(
      _generateNotificationId(),
      callType == 'video' ? '视频通话' : '语音通话',
      '$callerName 正在呼叫你',
      details,
      payload: jsonEncode({
        'type': 'call',
        'callId': callId,
        'callerId': callerId,
        'callType': callType,
      }),
    );
  }

  Future<void> showGroupSummary(String groupId, int messageCount) async {
    if (!_notificationsEnabled) return;

    final androidDetails = AndroidNotificationDetails(
      _channelMessages.id,
      _channelMessages.name,
      channelDescription: _channelMessages.description,
      importance: Importance.high,
      priority: Priority.high,
      groupKey: groupId,
      setAsGroupSummary: true,
      styleInformation: InboxStyleInformation(
        [],
        contentTitle: '$messageCount 条新消息',
        summaryText: 'IM应用',
      ),
    );

    final details = NotificationDetails(android: androidDetails);

    await _notifications.show(
      groupId.hashCode,
      '$messageCount 条新消息',
      null,
      details,
    );
  }

  Future<void> cancelNotification(int id) async {
    await _notifications.cancel(id);
  }

  Future<void> cancelAllNotifications() async {
    await _notifications.cancelAll();
    _pendingMessages.clear();
    notifyListeners();
  }

  Future<void> cancelConversationNotifications(String conversationId) async {
    final toRemove = _pendingMessages
        .where((m) => m.conversationId == conversationId)
        .toList();
    
    for (final message in toRemove) {
      _pendingMessages.remove(message);
    }
    
    notifyListeners();
  }

  void setNotificationsEnabled(bool enabled) {
    _notificationsEnabled = enabled;
    _saveSettings();
    notifyListeners();
  }

  void setSoundEnabled(bool enabled) {
    _soundEnabled = enabled;
    _saveSettings();
    notifyListeners();
  }

  void setVibrationEnabled(bool enabled) {
    _vibrationEnabled = enabled;
    _saveSettings();
    notifyListeners();
  }

  void muteConversation(String conversationId, {int? hours}) {
    _mutedConversations.add(conversationId);
    _saveSettings();
    
    if (hours != null) {
      Timer(Duration(hours: hours), () {
        unmuteConversation(conversationId);
      });
    }
    notifyListeners();
  }

  void unmuteConversation(String conversationId) {
    _mutedConversations.remove(conversationId);
    _saveSettings();
    notifyListeners();
  }

  void muteUser(String userId) {
    _mutedUsers.add(userId);
    _saveSettings();
    notifyListeners();
  }

  void unmuteUser(String userId) {
    _mutedUsers.remove(userId);
    _saveSettings();
    notifyListeners();
  }

  bool isConversationMuted(String conversationId) {
    return _mutedConversations.contains(conversationId);
  }

  bool isUserMuted(String userId) {
    return _mutedUsers.contains(userId);
  }

  void clearPendingMessages() {
    _pendingMessages.clear();
    notifyListeners();
  }

  int _generateNotificationId() {
    return Random().nextInt(100000);
  }

  @override
  void dispose() {
    _messageController.close();
    super.dispose();
  }
}
