/**
 * 消息引用回复类型定义
 * 支持多级引用、引用链溯源、批量引用
 */

export enum QuoteType {
  SINGLE = 'SINGLE',
  MULTI = 'MULTI',
  NESTED = 'NESTED',
  FORWARD = 'FORWARD'
}

export enum QuoteStatus {
  ACTIVE = 'ACTIVE',
  EDITED = 'EDITED',
  DELETED = 'DELETED',
  RECALLED = 'RECALLED'
}

export interface QuotedMessageInfo {
  messageId: number;
  senderId: number;
  senderName?: string;
  senderAvatar?: string;
  content?: string;
  messageType?: string;
  sentAt?: string;
  mediaUrls?: string[];
}

export interface MessageQuoteReply {
  id: number;
  messageId: number;
  quotedMessageId: number;
  conversationId: number;
  senderId: number;
  senderName?: string;
  senderAvatar?: string;
  replyContent?: string;
  quoteLevel: number;
  rootQuoteId?: number;
  parentQuoteId?: number;
  quoteChain: number[];
  quoteType: QuoteType;
  includeOriginal: boolean;
  highlightKeywords?: string;
  isBatchQuote: boolean;
  batchQuotedMessageIds: number[];
  status: QuoteStatus;
  createdAt: string;
  updatedAt: string;
  quotedMessageInfo?: QuotedMessageInfo;
  quoteChainDetails?: QuotedMessageInfo[];
}

export interface CreateQuoteReplyRequest {
  quotedMessageId: number;
  conversationId: number;
  replyContent: string;
  parentQuoteId?: number;
  batchQuotedMessageIds?: number[];
  includeOriginal?: boolean;
  highlightKeywords?: string;
}

export interface UpdateQuoteReplyRequest {
  content: string;
}

export interface QuoteReplyFilter {
  conversationId?: number;
  senderId?: number;
  quoteType?: QuoteType;
  status?: QuoteStatus;
}

export interface QuoteReplyStats {
  totalQuotes: number;
  activeQuotes: number;
  nestedQuotes: number;
  batchQuotes: number;
  myQuotes: number;
}

export interface QuoteChainNode {
  messageId: number;
  senderName: string;
  content: string;
  sentAt: string;
  level: number;
}

export interface QuoteTree {
  root: MessageQuoteReply;
  children: MessageQuoteReply[];
  maxLevel: number;
}

export function isNestedQuote(quote: MessageQuoteReply): boolean {
  return quote.quoteLevel > 1;
}

export function isActiveQuote(quote: MessageQuoteReply): boolean {
  return quote.status === QuoteStatus.ACTIVE;
}

export function isEditedQuote(quote: MessageQuoteReply): boolean {
  return quote.status === QuoteStatus.EDITED;
}

export function isRecalledQuote(quote: MessageQuoteReply): boolean {
  return quote.status === QuoteStatus.RECALLED;
}

export function getQuoteTypeLabel(type: QuoteType): string {
  const labels: Record<QuoteType, string> = {
    [QuoteType.SINGLE]: '单条引用',
    [QuoteType.MULTI]: '多条引用',
    [QuoteType.NESTED]: '嵌套引用',
    [QuoteType.FORWARD]: '转发引用'
  };
  return labels[type] || '未知类型';
}

export function getQuoteStatusLabel(status: QuoteStatus): string {
  const labels: Record<QuoteStatus, string> = {
    [QuoteStatus.ACTIVE]: '正常',
    [QuoteStatus.EDITED]: '已编辑',
    [QuoteStatus.DELETED]: '已删除',
    [QuoteStatus.RECALLED]: '已撤回'
  };
  return labels[status] || '未知状态';
}

export function getPreviewContent(info?: QuotedMessageInfo, maxLength: number = 50): string {
  if (!info) return '[未知消息]';
  if (info.content) {
    return info.content.length > maxLength
      ? `${info.content.substring(0, maxLength)}...`
      : info.content;
  }
  return `[${info.messageType || '未知类型'}]`;
}

export function buildQuoteChain(quotes: MessageQuoteReply[]): QuoteChainNode[] {
  const nodes: QuoteChainNode[] = [];
  const processed = new Set<number>();

  function addToChain(quote: MessageQuoteReply) {
    if (processed.has(quote.id)) return;
    processed.add(quote.id);

    if (quote.quotedMessageInfo) {
      nodes.push({
        messageId: quote.quotedMessageInfo.messageId,
        senderName: quote.quotedMessageInfo.senderName || '未知用户',
        content: quote.quotedMessageInfo.content || '',
        sentAt: quote.quotedMessageInfo.sentAt || quote.createdAt,
        level: quote.quoteLevel
      });
    }
  }

  quotes.sort((a, b) => a.quoteLevel - b.quoteLevel);
  quotes.forEach(addToChain);

  return nodes;
}

export function buildQuoteTree(quotes: MessageQuoteReply[]): QuoteTree | null {
  if (quotes.length === 0) return null;

  const root = quotes.find(q => q.quoteLevel === 1);
  if (!root) return null;

  const children = quotes.filter(q => q.id !== root.id);
  const maxLevel = Math.max(...quotes.map(q => q.quoteLevel));

  return {
    root,
    children,
    maxLevel
  };
}

export function filterActiveQuotes(quotes: MessageQuoteReply[]): MessageQuoteReply[] {
  return quotes.filter(isActiveQuote);
}

export function filterNestedQuotes(quotes: MessageQuoteReply[]): MessageQuoteReply[] {
  return quotes.filter(isNestedQuote);
}

export function filterTopLevelQuotes(quotes: MessageQuoteReply[]): MessageQuoteReply[] {
  return quotes.filter(q => q.quoteLevel === 1);
}

export function groupQuotesByType(quotes: MessageQuoteReply[]): Record<QuoteType, MessageQuoteReply[]> {
  return quotes.reduce((groups, quote) => {
    const type = quote.quoteType;
    if (!groups[type]) {
      groups[type] = [];
    }
    groups[type].push(quote);
    return groups;
  }, {} as Record<QuoteType, MessageQuoteReply[]>);
}

export function calculateQuoteStats(quotes: MessageQuoteReply[], currentUserId: number): QuoteReplyStats {
  return {
    totalQuotes: quotes.length,
    activeQuotes: quotes.filter(isActiveQuote).length,
    nestedQuotes: quotes.filter(isNestedQuote).length,
    batchQuotes: quotes.filter(q => q.isBatchQuote).length,
    myQuotes: quotes.filter(q => q.senderId === currentUserId).length
  };
}

export function validateCreateQuoteRequest(req: CreateQuoteReplyRequest): string | null {
  if (!req.quotedMessageId) {
    return '必须指定要引用的消息';
  }
  if (!req.conversationId) {
    return '必须指定会话ID';
  }
  if (!req.replyContent || req.replyContent.trim().length === 0) {
    return '回复内容不能为空';
  }
  if (req.batchQuotedMessageIds && req.batchQuotedMessageIds.length === 1) {
    return '批量引用至少需要2条消息';
  }
  return null;
}

export function formatQuoteReply(quote: MessageQuoteReply): string {
  const preview = getPreviewContent(quote.quotedMessageInfo, 30);
  const sender = quote.quotedMessageInfo?.senderName || '未知用户';
  return `「${sender}: ${preview}」\n${quote.replyContent || ''}`;
}
