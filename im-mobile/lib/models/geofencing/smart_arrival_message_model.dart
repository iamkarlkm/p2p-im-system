import 'dart:convert';

/// 智能到店消息模型类
/// 
/// @author IM Development Team
/// @since 2026-03-28
class SmartArrivalMessage {
  final String? id;
  final String messageId;
  final String? geofenceId;
  final String? merchantId;
  final String? merchantName;
  final String? merchantLogo;
  final MessageType messageType;
  final String title;
  final String content;
  final String? subtitle;
  final String? coverImage;
  final String? actionUrl;
  final ActionType? actionType;
  final String? actionButtonText;
  final String? couponId;
  final String? couponName;
  final double? couponAmount;
  final bool memberExclusive;
  final MessageStatus status;
  final DateTime? sendTime;
  final DateTime? readTime;
  final bool isRead;

  SmartArrivalMessage({
    this.id,
    required this.messageId,
    this.geofenceId,
    this.merchantId,
    this.merchantName,
    this.merchantLogo,
    required this.messageType,
    required this.title,
    required this.content,
    this.subtitle,
    this.coverImage,
    this.actionUrl,
    this.actionType,
    this.actionButtonText,
    this.couponId,
    this.couponName,
    this.couponAmount,
    this.memberExclusive = false,
    this.status = MessageStatus.pending,
    this.sendTime,
    this.readTime,
    this.isRead = false,
  });

  factory SmartArrivalMessage.fromJson(Map<String, dynamic> json) {
    return SmartArrivalMessage(
      id: json['id']?.toString(),
      messageId: json['messageId']?.toString() ?? '',
      geofenceId: json['geofenceId']?.toString(),
      merchantId: json['merchantId']?.toString(),
      merchantName: json['merchantName'],
      merchantLogo: json['merchantLogo'],
      messageType: MessageType.values.firstWhere(
        (e) => e.name.toUpperCase() == (json['messageType'] ?? 'WELCOME'),
        orElse: () => MessageType.welcome,
      ),
      title: json['title'] ?? '',
      content: json['content'] ?? '',
      subtitle: json['subtitle'],
      coverImage: json['coverImage'],
      actionUrl: json['actionUrl'],
      actionType: json['actionType'] != null
          ? ActionType.values.firstWhere(
              (e) => e.name.toUpperCase() == json['actionType'],
              orElse: () => ActionType.url,
            )
          : null,
      actionButtonText: json['actionButtonText'],
      couponId: json['couponId']?.toString(),
      couponName: json['couponName'],
      couponAmount: json['couponAmount']?.toDouble(),
      memberExclusive: json['memberExclusive'] ?? false,
      status: MessageStatus.values.firstWhere(
        (e) => e.name.toUpperCase() == (json['status'] ?? 'PENDING'),
        orElse: () => MessageStatus.pending,
      ),
      sendTime: json['sendTime'] != null
          ? DateTime.parse(json['sendTime'])
          : null,
      readTime: json['readTime'] != null
          ? DateTime.parse(json['readTime'])
          : null,
      isRead: json['isRead'] ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'messageId': messageId,
      'geofenceId': geofenceId,
      'merchantId': merchantId,
      'merchantName': merchantName,
      'merchantLogo': merchantLogo,
      'messageType': messageType.name.toUpperCase(),
      'title': title,
      'content': content,
      'subtitle': subtitle,
      'coverImage': coverImage,
      'actionUrl': actionUrl,
      'actionType': actionType?.name.toUpperCase(),
      'actionButtonText': actionButtonText,
      'couponId': couponId,
      'couponName': couponName,
      'couponAmount': couponAmount,
      'memberExclusive': memberExclusive,
      'status': status.name.toUpperCase(),
      'sendTime': sendTime?.toIso8601String(),
      'readTime': readTime?.toIso8601String(),
      'isRead': isRead,
    };
  }

  String get displayTypeText {
    switch (messageType) {
      case MessageType.welcome:
        return '欢迎';
      case MessageType.thanks:
        return '感谢';
      case MessageType.offer:
        return '优惠';
      case MessageType.service:
        return '服务';
      case MessageType.survey:
        return '问卷';
    }
  }

  SmartArrivalMessage markAsRead() {
    return SmartArrivalMessage(
      id: id,
      messageId: messageId,
      geofenceId: geofenceId,
      merchantId: merchantId,
      merchantName: merchantName,
      merchantLogo: merchantLogo,
      messageType: messageType,
      title: title,
      content: content,
      subtitle: subtitle,
      coverImage: coverImage,
      actionUrl: actionUrl,
      actionType: actionType,
      actionButtonText: actionButtonText,
      couponId: couponId,
      couponName: couponName,
      couponAmount: couponAmount,
      memberExclusive: memberExclusive,
      status: MessageStatus.read,
      sendTime: sendTime,
      readTime: DateTime.now(),
      isRead: true,
    );
  }

  @override
  String toString() => jsonEncode(toJson());
}

/// 消息类型枚举
enum MessageType {
  welcome, // 欢迎
  thanks,  // 感谢
  offer,   // 优惠
  service, // 服务
  survey,  // 问卷
}

/// 消息状态枚举
enum MessageStatus {
  pending,   // 待发送
  sent,      // 已发送
  delivered, // 已送达
  read,      // 已读
  failed,    // 失败
}

/// 跳转类型枚举
enum ActionType {
  url,     // 网页
  miniapp, // 小程序
  native,  // 原生页面
  coupon,  // 优惠券
}
