// 批量操作类型枚举
import 'package:json_annotation/json_annotation.dart';

part 'batch_operation_type.g.dart';

@JsonEnum(valueField: 'value')
enum BatchOperationType {
  forward('FORWARD', '转发', '将多条消息转发到其他会话'),
  delete('DELETE', '删除', '批量删除消息'),
  recall('RECALL', '撤回', '批量撤回已发送消息'),
  favorite('FAVORITE', '收藏', '批量收藏消息'),
  pin('PIN', '置顶', '批量置顶消息'),
  copy('COPY', '复制', '复制消息内容'),
  move('MOVE', '移动', '将消息移动到其他会话'),
  archive('ARCHIVE', '归档', '批量归档消息'),
  markRead('MARK_READ', '标记已读', '批量标记消息为已读'),
  markUnread('MARK_UNREAD', '标记未读', '批量标记消息为未读'),
  addTag('ADD_TAG', '添加标签', '批量添加消息标签'),
  removeTag('REMOVE_TAG', '移除标签', '批量移除消息标签'),
  export('EXPORT', '导出', '批量导出消息'),
  schedule('SCHEDULE', '定时发送', '批量设置定时发送'),
  remind('REMIND', '设置提醒', '批量设置消息提醒'),
  reaction('REACTION', '添加表情', '批量添加消息表情反应'),
  translate('TRANSLATE', '翻译', '批量翻译消息'),
  summarize('SUMMARIZE', '总结', '批量总结消息内容');

  final String value;
  final String displayName;
  final String description;

  const BatchOperationType(this.value, this.displayName, this.description);

  String toJson() => value;
  static BatchOperationType fromJson(String json) => 
      BatchOperationType.values.firstWhere((e) => e.value == json);

  bool get isForward => this == forward;
  bool get isDelete => this == delete;
  bool get isRecall => this == recall;
  bool get isFavorite => this == favorite;
  bool get isPin => this == pin;
  bool get isCopy => this == copy;
  bool get isMove => this == move;
  bool get isArchive => this == archive;
  bool get isReadOperation => this == markRead || this == markUnread;
  bool get isTagOperation => this == addTag || this == removeTag;
  bool get requiresTarget => this == forward || this == move;
  
  int get maxBatchSize {
    switch (this) {
      case BatchOperationType.markRead:
      case BatchOperationType.markUnread:
      case BatchOperationType.export:
        return 1000;
      case BatchOperationType.delete:
      case BatchOperationType.archive:
      case BatchOperationType.summarize:
        return 500;
      case BatchOperationType.favorite:
      case BatchOperationType.addTag:
      case BatchOperationType.removeTag:
      case BatchOperationType.reaction:
        return 200;
      case BatchOperationType.pin:
      case BatchOperationType.recall:
      case BatchOperationType.schedule:
        return 50;
      default:
        return 100;
    }
  }

  bool get supportsAsync {
    switch (this) {
      case BatchOperationType.delete:
      case BatchOperationType.favorite:
      case BatchOperationType.pin:
      case BatchOperationType.archive:
      case BatchOperationType.markRead:
      case BatchOperationType.markUnread:
      case BatchOperationType.addTag:
      case BatchOperationType.removeTag:
      case BatchOperationType.export:
      case BatchOperationType.remind:
      case BatchOperationType.reaction:
      case BatchOperationType.translate:
      case BatchOperationType.summarize:
        return true;
      default:
        return false;
    }
  }

  String get iconName {
    switch (this) {
      case forward: return 'share';
      case delete: return 'delete';
      case recall: return 'undo';
      case favorite: return 'star';
      case pin: return 'push_pin';
      case copy: return 'content_copy';
      case move: return 'drive_file_move';
      case archive: return 'archive';
      case markRead: return 'check';
      case markUnread: return 'mail';
      case addTag: return 'label';
      case removeTag: return 'label_off';
      case export: return 'download';
      case schedule: return 'schedule';
      case remind: return 'notifications';
      case reaction: return 'emoji_emotions';
      case translate: return 'translate';
      case summarize: return 'summarize';
      default: return 'more_horiz';
    }
  }
}
