/**
 * 消息快捷操作菜单模型 (Flutter/Dart)
 * 完整数据模型定义
 */

import 'package:flutter/foundation.dart';

/// 菜单类型枚举
enum MenuType {
  messageLongPress,
  messageRightClick,
  inputToolbar,
  quickReply,
  quickEmoji,
  messageActionBar,
  contextualActions,
}

/// 操作类型枚举
enum ActionType {
  reply,
  forward,
  copy,
  delete,
  recall,
  quote,
  multiSelect,
  pin,
  favorite,
  translate,
  speak,
  edit,
  remind,
  schedule,
  reaction,
  thread,
  report,
  custom,
}

/// 快捷操作项模型
@immutable
class QuickActionItem {
  final int id;
  final ActionType actionType;
  final String label;
  final String? icon;
  final String? iconColor;
  final String? shortcutKey;
  final int sortOrder;
  final bool isVisible;
  final bool isEnabled;
  final bool requiresConfirmation;
  final String? confirmationMessage;
  final String? customActionData;
  final String? visibilityCondition;
  final DateTime createdAt;
  final DateTime? updatedAt;

  const QuickActionItem({
    required this.id,
    required this.actionType,
    required this.label,
    this.icon,
    this.iconColor,
    this.shortcutKey,
    required this.sortOrder,
    required this.isVisible,
    required this.isEnabled,
    required this.requiresConfirmation,
    this.confirmationMessage,
    this.customActionData,
    this.visibilityCondition,
    required this.createdAt,
    this.updatedAt,
  });

  factory QuickActionItem.fromJson(Map<String, dynamic> json) {
    return QuickActionItem(
      id: json['id'] as int,
      actionType: ActionType.values.firstWhere(
        (e) => e.toString() == 'ActionType.${json['actionType']}',
      ),
      label: json['label'] as String,
      icon: json['icon'] as String?,
      iconColor: json['iconColor'] as String?,
      shortcutKey: json['shortcutKey'] as String?,
      sortOrder: json['sortOrder'] as int,
      isVisible: json['isVisible'] as bool,
      isEnabled: json['isEnabled'] as bool,
      requiresConfirmation: json['requiresConfirmation'] as bool,
      confirmationMessage: json['confirmationMessage'] as String?,
      customActionData: json['customActionData'] as String?,
      visibilityCondition: json['visibilityCondition'] as String?,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: json['updatedAt'] != null
          ? DateTime.parse(json['updatedAt'] as String)
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'actionType': actionType.toString().split('.').last,
      'label': label,
      'icon': icon,
      'iconColor': iconColor,
      'shortcutKey': shortcutKey,
      'sortOrder': sortOrder,
      'isVisible': isVisible,
      'isEnabled': isEnabled,
      'requiresConfirmation': requiresConfirmation,
      'confirmationMessage': confirmationMessage,
      'customActionData': customActionData,
      'visibilityCondition': visibilityCondition,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }

  QuickActionItem copyWith({
    int? id,
    ActionType? actionType,
    String? label,
    String? icon,
    String? iconColor,
    String? shortcutKey,
    int? sortOrder,
    bool? isVisible,
    bool? isEnabled,
    bool? requiresConfirmation,
    String? confirmationMessage,
    String? customActionData,
    String? visibilityCondition,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return QuickActionItem(
      id: id ?? this.id,
      actionType: actionType ?? this.actionType,
      label: label ?? this.label,
      icon: icon ?? this.icon,
      iconColor: iconColor ?? this.iconColor,
      shortcutKey: shortcutKey ?? this.shortcutKey,
      sortOrder: sortOrder ?? this.sortOrder,
      isVisible: isVisible ?? this.isVisible,
      isEnabled: isEnabled ?? this.isEnabled,
      requiresConfirmation: requiresConfirmation ?? this.requiresConfirmation,
      confirmationMessage: confirmationMessage ?? this.confirmationMessage,
      customActionData: customActionData ?? this.customActionData,
      visibilityCondition: visibilityCondition ?? this.visibilityCondition,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }
}

/// 消息快捷操作菜单模型
@immutable
class MessageQuickActionMenu {
  final int id;
  final int userId;
  final int? conversationId;
  final MenuType menuType;
  final String name;
  final String? description;
  final int sortOrder;
  final bool isEnabled;
  final bool isDefault;
  final String? icon;
  final String? color;
  final List<QuickActionItem> items;
  final DateTime createdAt;
  final DateTime? updatedAt;

  const MessageQuickActionMenu({
    required this.id,
    required this.userId,
    this.conversationId,
    required this.menuType,
    required this.name,
    this.description,
    required this.sortOrder,
    required this.isEnabled,
    required this.isDefault,
    this.icon,
    this.color,
    required this.items,
    required this.createdAt,
    this.updatedAt,
  });

  factory MessageQuickActionMenu.fromJson(Map<String, dynamic> json) {
    return MessageQuickActionMenu(
      id: json['id'] as int,
      userId: json['userId'] as int,
      conversationId: json['conversationId'] as int?,
      menuType: MenuType.values.firstWhere(
        (e) => e.toString() == 'MenuType.${json['menuType']}',
      ),
      name: json['name'] as String,
      description: json['description'] as String?,
      sortOrder: json['sortOrder'] as int,
      isEnabled: json['isEnabled'] as bool,
      isDefault: json['isDefault'] as bool,
      icon: json['icon'] as String?,
      color: json['color'] as String?,
      items: (json['items'] as List<dynamic>)
          .map((e) => QuickActionItem.fromJson(e as Map<String, dynamic>))
          .toList(),
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: json['updatedAt'] != null
          ? DateTime.parse(json['updatedAt'] as String)
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'conversationId': conversationId,
      'menuType': menuType.toString().split('.').last,
      'name': name,
      'description': description,
      'sortOrder': sortOrder,
      'isEnabled': isEnabled,
      'isDefault': isDefault,
      'icon': icon,
      'color': color,
      'items': items.map((e) => e.toJson()).toList(),
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }

  /// 获取启用的菜单项
  List<QuickActionItem> get enabledItems {
    return items
        .where((item) => item.isEnabled && item.isVisible)
        .toList()
      ..sort((a, b) => a.sortOrder.compareTo(b.sortOrder));
  }

  /// 获取特定类型的菜单项
  QuickActionItem? getItemByType(ActionType type) {
    try {
      return items.firstWhere((item) => item.actionType == type);
    } catch (_) {
      return null;
    }
  }

  /// 检查是否有特定操作
  bool hasAction(ActionType type) {
    return items.any((item) => item.actionType == type && item.isEnabled);
  }

  MessageQuickActionMenu copyWith({
    int? id,
    int? userId,
    int? conversationId,
    MenuType? menuType,
    String? name,
    String? description,
    int? sortOrder,
    bool? isEnabled,
    bool? isDefault,
    String? icon,
    String? color,
    List<QuickActionItem>? items,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return MessageQuickActionMenu(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      conversationId: conversationId ?? this.conversationId,
      menuType: menuType ?? this.menuType,
      name: name ?? this.name,
      description: description ?? this.description,
      sortOrder: sortOrder ?? this.sortOrder,
      isEnabled: isEnabled ?? this.isEnabled,
      isDefault: isDefault ?? this.isDefault,
      icon: icon ?? this.icon,
      color: color ?? this.color,
      items: items ?? this.items,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }
}

/// 菜单上下文 - 用于动态过滤
@immutable
class MenuContext {
  final int? messageId;
  final int? conversationId;
  final String? messageType;
  final bool isOwnMessage;
  final bool canRecall;
  final bool canEdit;
  final bool canDelete;
  final bool hasSelectedText;
  final bool isMultiSelectMode;
  final List<String> userPermissions;

  const MenuContext({
    this.messageId,
    this.conversationId,
    this.messageType,
    required this.isOwnMessage,
    required this.canRecall,
    required this.canEdit,
    required this.canDelete,
    required this.hasSelectedText,
    required this.isMultiSelectMode,
    required this.userPermissions,
  });

  /// 根据上下文过滤可见的菜单项
  List<QuickActionItem> filterVisibleItems(List<QuickActionItem> items) {
    return items.where((item) {
      if (!item.isVisible || !item.isEnabled) return false;

      switch (item.actionType) {
        case ActionType.recall:
          return isOwnMessage && canRecall;
        case ActionType.edit:
          return isOwnMessage && canEdit;
        case ActionType.delete:
          return canDelete;
        case ActionType.copy:
          return hasSelectedText;
        default:
          return true;
      }
    }).toList();
  }
}

/// 菜单显示位置
@immutable
class MenuPosition {
  final double x;
  final double y;
  final MenuPlacement placement;

  const MenuPosition({
    required this.x,
    required this.y,
    this.placement = MenuPlacement.auto,
  });
}

/// 菜单位置放置方式
enum MenuPlacement {
  auto,
  top,
  bottom,
  left,
  right,
}

/// 菜单配置
@immutable
class QuickActionMenuConfig {
  final bool showIcons;
  final bool showShortcutKeys;
  final bool showDividers;
  final int maxVisibleItems;
  final bool enableAnimations;
  final MenuTheme theme;

  const QuickActionMenuConfig({
    this.showIcons = true,
    this.showShortcutKeys = true,
    this.showDividers = true,
    this.maxVisibleItems = 10,
    this.enableAnimations = true,
    this.theme = MenuTheme.auto,
  });

  static const defaultConfig = QuickActionMenuConfig();
}

/// 菜单主题
enum MenuTheme {
  light,
  dark,
  auto,
}
