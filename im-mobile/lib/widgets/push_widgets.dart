/**
 * 推送通知组件
 * 
 * 包含通知显示、推送设置、徽章等 Flutter Widget
 */

import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';

// ==================== 通知组件 ====================

/// 通知弹出卡片
class PushNotificationCard extends StatelessWidget {
  final String title;
  final String body;
  final String? avatar;
  final String? tag;
  final String? conversationId;
  final int? unreadCount;
  final DateTime? time;
  final VoidCallback? onTap;
  final VoidCallback? onClose;
  final String type; // chat, system, friend, call

  const PushNotificationCard({
    super.key,
    required this.title,
    required this.body,
    this.avatar,
    this.tag,
    this.conversationId,
    this.unreadCount,
    this.time,
    this.onTap,
    this.onClose,
    this.type = 'chat',
  });

  Color get _borderColor {
    switch (type) {
      case 'chat':
        return const Color(0xFF34C759);
      case 'system':
        return const Color(0xFFFF9500);
      case 'friend':
        return const Color(0xFF007AFF);
      case 'call':
        return const Color(0xFFAF52DE);
      default:
        return const Color(0xFF007AFF);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Dismissible(
      key: Key(tag ?? title + body),
      direction: DismissDirection.endToStart,
      onDismissed: (_) => onClose?.call(),
      background: Container(
        alignment: Alignment.centerRight,
        padding: const EdgeInsets.only(right: 16),
        decoration: BoxDecoration(
          color: Colors.red.shade400,
          borderRadius: BorderRadius.circular(12),
        ),
        child: const Icon(Icons.delete_outline, color: Colors.white),
      ),
      child: GestureDetector(
        onTap: onTap,
        child: Container(
          margin: const EdgeInsets.only(bottom: 8),
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(12),
            border: Border(
              left: BorderSide(color: _borderColor, width: 4),
            ),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.08),
                blurRadius: 8,
                offset: const Offset(0, 2),
              ),
            ],
          ),
          child: Stack(
            children: [
              Padding(
                padding: const EdgeInsets.all(12),
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    _buildAvatar(),
                    const SizedBox(width: 10),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Row(
                            children: [
                              Expanded(
                                child: Text(
                                  title,
                                  style: const TextStyle(
                                    fontSize: 13,
                                    fontWeight: FontWeight.w600,
                                    color: Color(0xFF1a1a1a),
                                  ),
                                  maxLines: 1,
                                  overflow: TextOverflow.ellipsis,
                                ),
                              ),
                              if (time != null) ...[
                                const SizedBox(width: 8),
                                Text(
                                  _formatTime(time!),
                                  style: const TextStyle(
                                    fontSize: 11,
                                    color: Color(0xFF999999),
                                  ),
                                ),
                              ],
                            ],
                          ),
                          const SizedBox(height: 2),
                          Text(
                            body,
                            style: const TextStyle(
                              fontSize: 13,
                              color: Color(0xFF666666),
                              height: 1.4,
                            ),
                            maxLines: 2,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
              if (unreadCount != null && unreadCount! > 1)
                Positioned(
                  top: 8,
                  right: 8,
                  child: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                    decoration: BoxDecoration(
                      color: const Color(0xFFFF3B30),
                      borderRadius: BorderRadius.circular(9),
                    ),
                    child: Text(
                      unreadCount! > 99 ? '99+' : unreadCount.toString(),
                      style: const TextStyle(
                        fontSize: 11,
                        fontWeight: FontWeight.w600,
                        color: Colors.white,
                      ),
                    ),
                  ),
                ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildAvatar() {
    if (avatar != null && avatar!.isNotEmpty) {
      return ClipRRect(
        borderRadius: BorderRadius.circular(22),
        child: Image.network(
          avatar!,
          width: 44,
          height: 44,
          fit: BoxFit.cover,
          errorBuilder: (_, __, ___) => _buildPlaceholder(),
        ),
      );
    }
    return _buildPlaceholder();
  }

  Widget _buildPlaceholder() {
    return Container(
      width: 44,
      height: 44,
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [Color(0xFF667EEA), Color(0xFF764BA2)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(22),
      ),
      child: Center(
        child: Text(
          title.isNotEmpty ? title[0].toUpperCase() : '?',
          style: const TextStyle(
            color: Colors.white,
            fontSize: 18,
            fontWeight: FontWeight.w600,
          ),
        ),
      ),
    );
  }

  String _formatTime(DateTime t) {
    final now = DateTime.now();
    final diff = now.difference(t);

    if (diff.inMinutes < 1) return '刚刚';
    if (diff.inMinutes < 60) return '${diff.inMinutes}分钟前';
    if (diff.inHours < 24 && t.day == now.day) {
      return '${t.hour.toString().padLeft(2, '0')}:${t.minute.toString().padLeft(2, '0')}';
    }
    if (diff.inDays < 7) return '${diff.inDays}天前';
    return '${t.month}/${t.day}';
  }
}

/// 通知列表
class PushNotificationList extends StatelessWidget {
  final List<PushNotificationCard> notifications;
  final VoidCallback? onClearAll;

  const PushNotificationList({
    super.key,
    required this.notifications,
    this.onClearAll,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      constraints: const BoxConstraints(maxWidth: 360),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          if (notifications.isEmpty)
            const Padding(
              padding: EdgeInsets.all(32),
              child: Column(
                children: [
                  Icon(Icons.notifications_none, size: 48, color: Color(0xFFCCCCCC)),
                  SizedBox(height: 8),
                  Text(
                    '暂无通知',
                    style: TextStyle(color: Color(0xFF999999), fontSize: 14),
                  ),
                ],
              ),
            )
          else ...[
            if (onClearAll != null)
              Padding(
                padding: const EdgeInsets.only(bottom: 8),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      '${notifications.length} 条通知',
                      style: const TextStyle(
                        fontSize: 12,
                        color: Color(0xFF999999),
                      ),
                    ),
                    TextButton(
                      onPressed: onClearAll,
                      child: const Text('全部清除', style: TextStyle(fontSize: 12)),
                    ),
                  ],
                ),
              ),
            ...notifications,
          ],
        ],
      ),
    );
  }
}

// ==================== 推送设置面板 ====================

/// 推送设置面板
class PushSettingsPanel extends StatefulWidget {
  final bool pushEnabled;
  final bool soundEnabled;
  final bool vibrationEnabled;
  final bool badgeEnabled;
  final QuietHoursSetting? quietHours;
  final Map<String, bool> channelSettings;
  final Function(bool) onPushChanged;
  final Function(bool) onSoundChanged;
  final Function(bool) onVibrationChanged;
  final Function(bool) onBadgeChanged;
  final Function(QuietHoursSetting?) onQuietHoursChanged;
  final Function(String, bool) onChannelChanged;
  final VoidCallback? onClose;

  const PushSettingsPanel({
    super.key,
    this.pushEnabled = true,
    this.soundEnabled = true,
    this.vibrationEnabled = true,
    this.badgeEnabled = true,
    this.quietHours,
    this.channelSettings = const {},
    required this.onPushChanged,
    required this.onSoundChanged,
    required this.onVibrationChanged,
    required this.onBadgeChanged,
    required this.onQuietHoursChanged,
    required this.onChannelChanged,
    this.onClose,
  });

  @override
  State<PushSettingsPanel> createState() => _PushSettingsPanelState();
}

class _PushSettingsPanelState extends State<PushSettingsPanel> {
  late bool _pushEnabled;
  late bool _soundEnabled;
  late bool _vibrationEnabled;
  late bool _badgeEnabled;
  late QuietHoursSetting? _quietHours;
  late Map<String, bool> _channelSettings;

  @override
  void initState() {
    super.initState();
    _pushEnabled = widget.pushEnabled;
    _soundEnabled = widget.soundEnabled;
    _vibrationEnabled = widget.vibrationEnabled;
    _badgeEnabled = widget.badgeEnabled;
    _quietHours = widget.quietHours;
    _channelSettings = Map.from(widget.channelSettings);
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 320,
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.15),
            blurRadius: 32,
            offset: const Offset(0, 8),
          ),
        ],
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          _buildHeader(),
          Flexible(
            child: SingleChildScrollView(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  _buildSection('基本设置', [
                    _buildSwitch('接收推送通知', _pushEnabled, (v) {
                      setState(() => _pushEnabled = v);
                      widget.onPushChanged(v);
                    }, Icons.notifications_outlined),
                    _buildSwitch('声音', _soundEnabled, (v) {
                      setState(() => _soundEnabled = v);
                      widget.onSoundChanged(v);
                    }, Icons.volume_up_outlined),
                    _buildSwitch('震动', _vibrationEnabled, (v) {
                      setState(() => _vibrationEnabled = v);
                      widget.onVibrationChanged(v);
                    }, Icons.vibration),
                    _buildSwitch('角标', _badgeEnabled, (v) {
                      setState(() => _badgeEnabled = v);
                      widget.onBadgeChanged(v);
                    }, Icons.looks_one_outlined),
                  ]),
                  _buildSection('免打扰', [
                    _buildQuietHours(),
                  ]),
                  _buildSection('通知类别', [
                    _buildChannelSwitch('聊天消息', 'chat'),
                    _buildChannelSwitch('系统通知', 'system'),
                    _buildChannelSwitch('好友通知', 'friend'),
                    _buildChannelSwitch('来电通知', 'call'),
                    _buildChannelSwitch('互动提醒', 'activity'),
                  ]),
                  const SizedBox(height: 16),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildHeader() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: const BoxDecoration(
        border: Border(bottom: BorderSide(color: Color(0xFFF0F0F0))),
      ),
      child: Row(
        children: [
          const Text(
            '通知设置',
            style: TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.w600,
              color: Color(0xFF1a1a1a),
            ),
          ),
          const Spacer(),
          IconButton(
            onPressed: widget.onClose,
            icon: const Icon(Icons.close, size: 20),
            color: const Color(0xFF666666),
            padding: EdgeInsets.zero,
            constraints: const BoxConstraints(minWidth: 28, minHeight: 28),
          ),
        ],
      ),
    );
  }

  Widget _buildSection(String title, List<Widget> children) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.only(top: 16, bottom: 8),
          child: Text(
            title,
            style: const TextStyle(
              fontSize: 12,
              fontWeight: FontWeight.w600,
              color: Color(0xFF999999),
              letterSpacing: 0.5,
            ),
          ),
        ),
        ...children,
      ],
    );
  }

  Widget _buildSwitch(String label, bool value, Function(bool) onChanged, IconData icon) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        children: [
          Container(
            width: 32,
            height: 32,
            decoration: BoxDecoration(
              color: const Color(0xFFF5F5F5),
              borderRadius: BorderRadius.circular(8),
            ),
            child: Icon(icon, size: 16, color: const Color(0xFF666666)),
          ),
          const SizedBox(width: 10),
          Expanded(
            child: Text(
              label,
              style: const TextStyle(
                fontSize: 14,
                color: Color(0xFF1a1a1a),
              ),
            ),
          ),
          Switch(
            value: value,
            onChanged: onChanged,
            activeColor: const Color(0xFF34C759),
          ),
        ],
      ),
    );
  }

  Widget _buildChannelSwitch(String label, String channelId) {
    final enabled = _channelSettings[channelId] ?? true;
    return _buildSwitch(label, enabled, (v) {
      setState(() => _channelSettings[channelId] = v);
      widget.onChannelChanged(channelId, v);
    }, _channelIcon(channelId));
  }

  IconData _channelIcon(String channelId) {
    switch (channelId) {
      case 'chat':
        return Icons.chat_bubble_outline;
      case 'system':
        return Icons.settings_outlined;
      case 'friend':
        return Icons.person_add_outlined;
      case 'call':
        return Icons.phone_outlined;
      case 'activity':
        return Icons.thumb_up_outlined;
      default:
        return Icons.notifications_outlined;
    }
  }

  Widget _buildQuietHours() {
    return Column(
      children: [
        Row(
          children: [
            Container(
              width: 32,
              height: 32,
              decoration: BoxDecoration(
                color: const Color(0xFFF5F5F5),
                borderRadius: BorderRadius.circular(8),
              ),
              child: const Icon(Icons.do_not_disturb_on_outlined, size: 16, color: Color(0xFF666666)),
            ),
            const SizedBox(width: 10),
            Expanded(
              child: Text(
                '免打扰时段',
                style: const TextStyle(fontSize: 14, color: Color(0xFF1a1a1a)),
              ),
            ),
            Switch(
              value: _quietHours?.enabled ?? false,
              onChanged: (v) {
                setState(() {
                  _quietHours = QuietHoursSetting(
                    enabled: v,
                    startHour: _quietHours?.startHour ?? 22,
                    startMinute: _quietHours?.startMinute ?? 0,
                    endHour: _quietHours?.endHour ?? 8,
                    endMinute: _quietHours?.endMinute ?? 0,
                  );
                });
                widget.onQuietHoursChanged(_quietHours);
              },
              activeColor: const Color(0xFF34C759),
            ),
          ],
        ),
        if (_quietHours?.enabled == true)
          Padding(
            padding: const EdgeInsets.only(left: 42, top: 8),
            child: Row(
              children: [
                _buildTimePicker(
                  '${_quietHours!.startHour.toString().padLeft(2, '0')}:${_quietHours!.startMinute.toString().padLeft(2, '0')}',
                  (t) {
                    setState(() => _quietHours = QuietHoursSetting(
                      enabled: true,
                      startHour: t.hour,
                      startMinute: t.minute,
                      endHour: _quietHours!.endHour,
                      endMinute: _quietHours!.endMinute,
                    ));
                  },
                ),
                const Padding(
                  padding: EdgeInsets.symmetric(horizontal: 8),
                  child: Text('至', style: TextStyle(color: Color(0xFF999999))),
                ),
                _buildTimePicker(
                  '${_quietHours!.endHour.toString().padLeft(2, '0')}:${_quietHours!.endMinute.toString().padLeft(2, '0')}',
                  (t) {
                    setState(() => _quietHours = QuietHoursSetting(
                      enabled: true,
                      startHour: _quietHours!.startHour,
                      startMinute: _quietHours!.startMinute,
                      endHour: t.hour,
                      endMinute: t.minute,
                    ));
                  },
                ),
              ],
            ),
          ),
      ],
    );
  }

  Widget _buildTimePicker(String time, Function(TimeOfDay) onChanged) {
    return InkWell(
      onTap: () async {
        final parts = time.split(':');
        final initial = TimeOfDay(
          hour: int.parse(parts[0]),
          minute: int.parse(parts[1]),
        );
        final picked = await showTimePicker(
          context: context,
          initialTime: initial,
        );
        if (picked != null) onChanged(picked);
      },
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
        decoration: BoxDecoration(
          border: Border.all(color: const Color(0xFFE5E5E5)),
          borderRadius: BorderRadius.circular(8),
        ),
        child: Text(time, style: const TextStyle(fontSize: 14, color: Color(0xFF1a1a1a))),
      ),
    );
  }
}

class QuietHoursSetting {
  final bool enabled;
  final int startHour;
  final int startMinute;
  final int endHour;
  final int endMinute;

  QuietHoursSetting({
    this.enabled = false,
    this.startHour = 22,
    this.startMinute = 0,
    this.endHour = 8,
    this.endMinute = 0,
  });
}

// ==================== 角标组件 ====================

/// 推送角标
class PushBadge extends StatelessWidget {
  final int count;
  final bool showDot;
  final double size;

  const PushBadge({
    super.key,
    this.count = 0,
    this.showDot = false,
    this.size = 18,
  });

  @override
  Widget build(BuildContext context) {
    if (count <= 0 && !showDot) return const SizedBox.shrink();

    if (showDot) {
      return Container(
        width: 10,
        height: 10,
        decoration: const BoxDecoration(
          color: Color(0xFFFF3B30),
          shape: BoxShape.circle,
        ),
      );
    }

    return Container(
      constraints: BoxConstraints(minWidth: size),
      height: size,
      padding: const EdgeInsets.symmetric(horizontal: 5),
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [Color(0xFFFF3B30), Color(0xFFFF6B6B)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(size / 2),
        boxShadow: [
          BoxShadow(
            color: const Color(0xFFFF3B30).withOpacity(0.4),
            blurRadius: 4,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Center(
        child: Text(
          count > 99 ? '99+' : count.toString(),
          style: TextStyle(
            color: Colors.white,
            fontSize: size * 0.6,
            fontWeight: FontWeight.w700,
          ),
        ),
      ),
    );
  }
}

// ==================== 本地通知 ====================

/// 本地通知服务封装
class LocalNotificationService {
  static final LocalNotificationService _instance = LocalNotificationService._internal();
  factory LocalNotificationService() => _instance;
  LocalNotificationService._internal();

  final FlutterLocalNotificationsPlugin _plugin = FlutterLocalNotificationsPlugin();

  Future<bool> initialize() async {
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

    return await _plugin.initialize(
      initSettings,
      onDidReceiveNotificationResponse: _onNotificationTap,
    );
  }

  void _onNotificationTap(NotificationResponse response) {
    final payload = response.payload;
    if (payload != null) {
      try {
        final data = Map<String, dynamic>.from(
          Uri.splitQueryString(payload).map((k, v) => MapEntry(k, v)),
        );
        // 处理通知点击
        print('[Notification] Tapped: $data');
      } catch (e) {
        print('[Notification] Parse error: $e');
      }
    }
  }

  Future<void> show({
    required int id,
    required String title,
    required String body,
    String? payload,
    String? channelId,
    String? channelName,
    AndroidNotificationDetails? androidDetails,
  }) async {
    const android = AndroidNotificationDetails(
      'default_channel',
      '默认通知',
      channelDescription: '默认通知渠道',
      importance: Importance.high,
      priority: Priority.high,
    );
    const ios = DarwinNotificationDetails(
      presentAlert: true,
      presentBadge: true,
      presentSound: true,
    );
    const details = NotificationDetails(android: android, iOS: ios);

    await _plugin.show(id, title, body, details, payload: payload);
  }

  Future<void> cancel(int id) async {
    await _plugin.cancel(id);
  }

  Future<void> cancelAll() async {
    await _plugin.cancelAll();
  }
}
