import 'package:flutter/material.dart';
import '../../models/user_status.dart';

class StatusIndicator extends StatelessWidget {
  final String? status;
  final double size;
  final bool showBorder;
  final Color? borderColor;
  final bool pulse;

  const StatusIndicator({
    super.key,
    this.status,
    this.size = 12,
    this.showBorder = true,
    this.borderColor,
    this.pulse = false,
  });

  factory StatusIndicator.online({
    double size = 12,
    bool showBorder = true,
    Color? borderColor,
  }) {
    return StatusIndicator(
      status: 'online',
      size: size,
      showBorder: showBorder,
      borderColor: borderColor,
    );
  }

  factory StatusIndicator.away({
    double size = 12,
    bool showBorder = true,
    Color? borderColor,
  }) {
    return StatusIndicator(
      status: 'away',
      size: size,
      showBorder: showBorder,
      borderColor: borderColor,
    );
  }

  factory StatusIndicator.busy({
    double size = 12,
    bool showBorder = true,
    Color? borderColor,
  }) {
    return StatusIndicator(
      status: 'busy',
      size: size,
      showBorder: showBorder,
      borderColor: borderColor,
    );
  }

  factory StatusIndicator.offline({
    double size = 12,
    bool showBorder = true,
    Color? borderColor,
  }) {
    return StatusIndicator(
      status: 'offline',
      size: size,
      showBorder: showBorder,
      borderColor: borderColor,
    );
  }

  Color get _statusColor {
    switch (status) {
      case 'online':
        return Colors.green;
      case 'away':
        return Colors.orange;
      case 'busy':
        return Colors.red;
      case 'invisible':
        return Colors.grey;
      default:
        return Colors.grey.shade400;
    }
  }

  IconData? get _statusIcon {
    switch (status) {
      case 'online':
        return null;
      case 'away':
        return Icons.access_time;
      case 'busy':
        return Icons.remove;
      case 'invisible':
        return null;
      default:
        return null;
    }
  }

  String get _statusLabel {
    switch (status) {
      case 'online':
        return '在线';
      case 'away':
        return '离开';
      case 'busy':
        return '忙碌';
      case 'invisible':
        return '隐身';
      default:
        return '离线';
    }
  }

  @override
  Widget build(BuildContext context) {
    Widget indicator;

    if (_statusIcon != null) {
      indicator = Container(
        width: size,
        height: size,
        decoration: BoxDecoration(
          color: _statusColor,
          shape: BoxShape.circle,
          border: showBorder
              ? Border.all(
                  color: borderColor ?? Colors.white,
                  width: size * 0.15,
                )
              : null,
        ),
        child: Center(
          child: Icon(
            _statusIcon,
            size: size * 0.5,
            color: Colors.white,
          ),
        ),
      );
    } else {
      indicator = Container(
        width: size,
        height: size,
        decoration: BoxDecoration(
          color: status == 'offline' || status == null
              ? Colors.grey.shade300
              : _statusColor,
          shape: BoxShape.circle,
          border: showBorder
              ? Border.all(
                  color: borderColor ?? Colors.white,
                  width: size * 0.15,
                )
              : null,
        ),
      );
    }

    if (pulse && status == 'online') {
      return Stack(
        alignment: Alignment.center,
        children: [
          _buildPulseRing(),
          indicator,
        ],
      );
    }

    return indicator;
  }

  Widget _buildPulseRing() {
    return TweenAnimationBuilder<double>(
      tween: Tween(begin: 0.0, end: 1.0),
      duration: const Duration(seconds: 2),
      curve: Curves.easeOut,
      builder: (context, value, child) {
        return Container(
          width: size * (1 + value * 0.8),
          height: size * (1 + value * 0.8),
          decoration: BoxDecoration(
            color: _statusColor.withOpacity((1 - value) * 0.4),
            shape: BoxShape.circle,
          ),
        );
      },
      onEnd: () {
        // 循环动画由上层重建触发
      },
    );
  }
}

class StatusIndicatorWithLabel extends StatelessWidget {
  final String? status;
  final String? label;
  final double indicatorSize;
  final TextStyle? labelStyle;
  final bool showLastSeen;
  final DateTime? lastSeen;

  const StatusIndicatorWithLabel({
    super.key,
    this.status,
    this.label,
    this.indicatorSize = 10,
    this.labelStyle,
    this.showLastSeen = false,
    this.lastSeen,
  });

  String get _displayLabel {
    if (label != null && label!.isNotEmpty) {
      return label!;
    }
    switch (status) {
      case 'online':
        return '在线';
      case 'away':
        return '离开';
      case 'busy':
        return '忙碌';
      case 'invisible':
        return '隐身';
      default:
        return '离线';
    }
  }

  String? get _lastSeenText {
    if (!showLastSeen || lastSeen == null || status == 'online') {
      return null;
    }
    
    final now = DateTime.now();
    final diff = now.difference(lastSeen!);
    
    if (diff.inMinutes < 1) {
      return '刚刚';
    } else if (diff.inMinutes < 60) {
      return '${diff.inMinutes}分钟前';
    } else if (diff.inHours < 24) {
      return '${diff.inHours}小时前';
    } else {
      return '${lastSeen!.month}月${lastSeen!.day}日';
    }
  }

  @override
  Widget build(BuildContext context) {
    final lastSeenText = _lastSeenText;
    
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        StatusIndicator(status: status, size: indicatorSize),
        const SizedBox(width: 6),
        Flexible(
          child: Text(
            _displayLabel,
            style: labelStyle ?? TextStyle(
              fontSize: 12,
              color: Colors.grey.shade600,
            ),
            overflow: TextOverflow.ellipsis,
          ),
        ),
        if (lastSeenText != null) ...[
          const SizedBox(width: 4),
          Text(
            '· $lastSeenText',
            style: TextStyle(
              fontSize: 11,
              color: Colors.grey.shade400,
            ),
          ),
        ],
      ],
    );
  }
}

class UserAvatarWithStatus extends StatelessWidget {
  final String? avatarUrl;
  final String? name;
  final String? status;
  final double size;
  final VoidCallback? onTap;
  final bool showStatus;

  const UserAvatarWithStatus({
    super.key,
    this.avatarUrl,
    this.name,
    this.status,
    this.size = 48,
    this.onTap,
    this.showStatus = true,
  });

  @override
  Widget build(BuildContext context) {
    final indicatorSize = size * 0.35;
    
    return GestureDetector(
      onTap: onTap,
      child: Stack(
        children: [
          CircleAvatar(
            radius: size / 2,
            backgroundColor: Colors.grey.shade200,
            backgroundImage: avatarUrl != null ? NetworkImage(avatarUrl!) : null,
            child: avatarUrl == null
                ? Text(
                    name?.isNotEmpty == true ? name![0].toUpperCase() : '?',
                    style: TextStyle(
                      fontSize: size * 0.4,
                      color: Colors.grey.shade600,
                    ),
                  )
                : null,
          ),
          if (showStatus && status != null)
            Positioned(
              right: 0,
              bottom: 0,
              child: StatusIndicator(
                status: status,
                size: indicatorSize,
                showBorder: true,
              ),
            ),
        ],
      ),
    );
  }
}
