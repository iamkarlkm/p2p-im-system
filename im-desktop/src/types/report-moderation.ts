export interface Report {
  reportId: string;
  reporterUserId: number;
  reporterUsername: string;
  reportedMessageId: number;
  reportedUserId: number;
  conversationId: number;
  conversationType: 'private' | 'group';
  reportReason: string;
  reportCategory: string;
  description: string;
  status: 'PENDING' | 'REVIEWING' | 'RESOLVED' | 'DISMISSED' | 'ESCALATED';
  reviewerNote: string;
  createdAt: string;
  reviewedAt: string;
}

export interface ReportRequest {
  reportedMessageId: number;
  reportedUserId: number;
  conversationId: number;
  conversationType: 'private' | 'group';
  reportReason: string;
  reportCategory: string;
  description: string;
  evidence: string;
}

export interface ModerationSettings {
  userId: number;
  enableAutoModeration: boolean;
  enableKeywordFilter: boolean;
  enableImageModeration: boolean;
  enableSpamDetection: boolean;
  allowAnonymousReports: boolean;
  maxReportsPerDay: number;
}

export const REPORT_REASONS = [
  '色情内容', '暴力血腥', '诈骗信息', '仇恨言论',
  '垃圾广告', '侵犯隐私', '谣言虚假信息', '其他违规'
] as const;

export type ReportReason = typeof REPORT_REASONS[number];
