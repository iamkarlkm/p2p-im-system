/**
 * 批量操作类型定义
 */
export enum BatchOperationType {
  FORWARD = 'FORWARD',
  DELETE = 'DELETE',
  RECALL = 'RECALL',
  FAVORITE = 'FAVORITE',
  PIN = 'PIN',
  COPY = 'COPY',
  MOVE = 'MOVE',
  ARCHIVE = 'ARCHIVE',
  MARK_READ = 'MARK_READ',
  MARK_UNREAD = 'MARK_UNREAD',
  ADD_TAG = 'ADD_TAG',
  REMOVE_TAG = 'REMOVE_TAG',
  EXPORT = 'EXPORT',
  SCHEDULE = 'SCHEDULE',
  REMIND = 'REMIND',
  REACTION = 'REACTION',
  TRANSLATE = 'TRANSLATE',
  SUMMARIZE = 'SUMMARIZE',
}

export interface BatchOperationTypeInfo {
  type: BatchOperationType;
  displayName: string;
  description: string;
  icon: string;
  requiresTarget: boolean;
  supportsAsync: boolean;
  maxBatchSize: number;
}

export const BATCH_OPERATION_TYPES: BatchOperationTypeInfo[] = [
  { type: BatchOperationType.FORWARD, displayName: '转发', description: '将多条消息转发到其他会话', icon: 'Share2', requiresTarget: true, supportsAsync: false, maxBatchSize: 100 },
  { type: BatchOperationType.DELETE, displayName: '删除', description: '批量删除消息', icon: 'Trash2', requiresTarget: false, supportsAsync: true, maxBatchSize: 500 },
  { type: BatchOperationType.RECALL, displayName: '撤回', description: '批量撤回已发送消息', icon: 'RotateCcw', requiresTarget: false, supportsAsync: false, maxBatchSize: 50 },
  { type: BatchOperationType.FAVORITE, displayName: '收藏', description: '批量收藏消息', icon: 'Star', requiresTarget: false, supportsAsync: true, maxBatchSize: 200 },
  { type: BatchOperationType.PIN, displayName: '置顶', description: '批量置顶消息', icon: 'Pin', requiresTarget: false, supportsAsync: true, maxBatchSize: 50 },
  { type: BatchOperationType.COPY, displayName: '复制', description: '复制消息内容', icon: 'Copy', requiresTarget: false, supportsAsync: false, maxBatchSize: 100 },
  { type: BatchOperationType.MOVE, displayName: '移动', description: '将消息移动到其他会话', icon: 'Move', requiresTarget: true, supportsAsync: false, maxBatchSize: 100 },
  { type: BatchOperationType.ARCHIVE, displayName: '归档', description: '批量归档消息', icon: 'Archive', requiresTarget: false, supportsAsync: true, maxBatchSize: 500 },
  { type: BatchOperationType.MARK_READ, displayName: '标记已读', description: '批量标记消息为已读', icon: 'Check', requiresTarget: false, supportsAsync: true, maxBatchSize: 1000 },
  { type: BatchOperationType.MARK_UNREAD, displayName: '标记未读', description: '批量标记消息为未读', icon: 'Mail', requiresTarget: false, supportsAsync: true, maxBatchSize: 1000 },
  { type: BatchOperationType.ADD_TAG, displayName: '添加标签', description: '批量添加消息标签', icon: 'Tag', requiresTarget: false, supportsAsync: true, maxBatchSize: 200 },
  { type: BatchOperationType.REMOVE_TAG, displayName: '移除标签', description: '批量移除消息标签', icon: 'TagOff', requiresTarget: false, supportsAsync: true, maxBatchSize: 200 },
  { type: BatchOperationType.EXPORT, displayName: '导出', description: '批量导出消息', icon: 'Download', requiresTarget: false, supportsAsync: true, maxBatchSize: 1000 },
  { type: BatchOperationType.SCHEDULE, displayName: '定时发送', description: '批量设置定时发送', icon: 'Clock', requiresTarget: false, supportsAsync: false, maxBatchSize: 50 },
  { type: BatchOperationType.REMIND, displayName: '设置提醒', description: '批量设置消息提醒', icon: 'Bell', requiresTarget: false, supportsAsync: true, maxBatchSize: 100 },
  { type: BatchOperationType.REACTION, displayName: '添加表情', description: '批量添加消息表情反应', icon: 'Smile', requiresTarget: false, supportsAsync: true, maxBatchSize: 200 },
  { type: BatchOperationType.TRANSLATE, displayName: '翻译', description: '批量翻译消息', icon: 'Languages', requiresTarget: false, supportsAsync: true, maxBatchSize: 100 },
  { type: BatchOperationType.SUMMARIZE, displayName: '总结', description: '批量总结消息内容', icon: 'FileText', requiresTarget: false, supportsAsync: true, maxBatchSize: 500 },
];

export function getBatchOperationInfo(type: BatchOperationType): BatchOperationTypeInfo | undefined {
  return BATCH_OPERATION_TYPES.find(t => t.type === type);
}

export function requiresTarget(type: BatchOperationType): boolean {
  return getBatchOperationInfo(type)?.requiresTarget ?? false;
}

export function supportsAsync(type: BatchOperationType): boolean {
  return getBatchOperationInfo(type)?.supportsAsync ?? false;
}

export function getMaxBatchSize(type: BatchOperationType): number {
  return getBatchOperationInfo(type)?.maxBatchSize ?? 100;
}
