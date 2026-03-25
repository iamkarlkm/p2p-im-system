export interface MessageQuote {
    id: string;
    messageId: string;
    quotedMessageId: string;
    conversationId: string;
    userId: string;
    quotedContent: string;
    quotedSenderId?: string;
    quotedSenderName?: string;
    quoteType: string;
    isDeleted: boolean;
    createdAt: string;
    updatedAt: string;
    quotePreview?: string;
    attachmentCount: number;
    hasAttachment: boolean;
}

export interface MessageQuoteCreateRequest {
    messageId: string;
    quotedMessageId: string;
    conversationId: string;
    userId: string;
    quotedContent?: string;
    quotedSenderId?: string;
    quotedSenderName?: string;
    quoteType?: string;
    quotePreview?: string;
    attachmentCount?: number;
    hasAttachment?: boolean;
}

export interface MessageQuoteUpdateRequest {
    quotedContent?: string;
    quotedSenderId?: string;
    quotedSenderName?: string;
    quoteType?: string;
    quotePreview?: string;
    attachmentCount?: number;
    hasAttachment?: boolean;
    isDeleted?: boolean;
}

export interface MessageQuoteQueryParams {
    quoteType?: string;
    hasAttachment?: boolean;
    isDeleted?: boolean;
    limit?: number;
    offset?: number;
    sortBy?: string;
    sortOrder?: 'asc' | 'desc';
}

export interface MessageQuoteStats {
    totalQuotes: number;
    quotesByType: Record<string, number>;
    quotesByUser: Record<string, number>;
    mostQuotedMessages: Array<{ messageId: string; count: number }>;
    mostQuotedSenders: Array<{ senderId: string; count: number }>;
    averageQuotesPerConversation: number;
}

export type QuoteType = 'TEXT' | 'ATTACHMENT' | 'IMAGE' | 'VIDEO' | 'AUDIO' | 'FILE' | 'MEDIA';

export const QuoteTypeLabels: Record<QuoteType, string> = {
    'TEXT': 'Text',
    'ATTACHMENT': 'Attachment',
    'IMAGE': 'Image',
    'VIDEO': 'Video',
    'AUDIO': 'Audio',
    'FILE': 'File',
    'MEDIA': 'Media',
};

export function formatQuoteType(quoteType: string): string {
    const type = quoteType.toUpperCase() as QuoteType;
    return QuoteTypeLabels[type] || quoteType;
}

export function isTextQuote(quote: MessageQuote): boolean {
    return quote.quoteType.toUpperCase() === 'TEXT';
}

export function isAttachmentQuote(quote: MessageQuote): boolean {
    return quote.quoteType.toUpperCase() === 'ATTACHMENT';
}

export function isMediaQuote(quote: MessageQuote): boolean {
    const mediaTypes = ['IMAGE', 'VIDEO', 'AUDIO', 'MEDIA'];
    return mediaTypes.includes(quote.quoteType.toUpperCase());
}

export function hasAttachments(quote: MessageQuote): boolean {
    return quote.hasAttachment || quote.attachmentCount > 0;
}

export function isQuoteDeleted(quote: MessageQuote): boolean {
    return quote.isDeleted;
}

export function getQuotePreview(quote: MessageQuote, maxLength: number = 100): string {
    if (quote.quotePreview) {
        return quote.quotePreview.length <= maxLength 
            ? quote.quotePreview 
            : quote.quotePreview.substring(0, maxLength) + '...';
    }
    
    if (quote.quotedContent) {
        return quote.quotedContent.length <= maxLength 
            ? quote.quotedContent 
            : quote.quotedContent.substring(0, maxLength) + '...';
    }
    
    return hasAttachments(quote) 
        ? `${quote.attachmentCount} attachment(s)` 
        : 'No preview available';
}

export function createMessageQuote(
    messageId: string,
    quotedMessageId: string,
    conversationId: string,
    userId: string,
    quotedContent: string,
    quotedSenderId: string,
    quotedSenderName: string,
    quoteType: QuoteType = 'TEXT',
    quotePreview?: string,
    attachmentCount: number = 0
): MessageQuoteCreateRequest {
    return {
        messageId,
        quotedMessageId,
        conversationId,
        userId,
        quotedContent,
        quotedSenderId,
        quotedSenderName,
        quoteType,
        quotePreview,
        attachmentCount,
        hasAttachment: attachmentCount > 0,
    };
}

export function validateMessageQuote(request: MessageQuoteCreateRequest): { valid: boolean; errors: string[] } {
    const errors: string[] = [];
    
    if (!request.messageId) {
        errors.push('messageId is required');
    }
    if (!request.quotedMessageId) {
        errors.push('quotedMessageId is required');
    }
    if (!request.conversationId) {
        errors.push('conversationId is required');
    }
    if (!request.userId) {
        errors.push('userId is required');
    }
    
    if (request.attachmentCount !== undefined && request.attachmentCount < 0) {
        errors.push('attachmentCount must be non-negative');
    }
    
    return {
        valid: errors.length === 0,
        errors,
    };
}

export interface MessageQuoteListResponse {
    quotes: MessageQuote[];
    total: number;
    hasMore: boolean;
}

export interface MessageQuoteSearchRequest {
    conversationId?: string;
    userId?: string;
    quoteType?: string;
    keyword?: string;
    dateFrom?: string;
    dateTo?: string;
    hasAttachment?: boolean;
    limit?: number;
    offset?: number;
}

export interface MessageQuoteDeleteResponse {
    deletedCount: number;
    success: boolean;
}

export function createEmptyQuote(): MessageQuote {
    return {
        id: '',
        messageId: '',
        quotedMessageId: '',
        conversationId: '',
        userId: '',
        quotedContent: '',
        quoteType: 'TEXT',
        isDeleted: false,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        attachmentCount: 0,
        hasAttachment: false,
    };
}

export function cloneQuote(quote: MessageQuote): MessageQuote {
    return {
        ...quote,
    };
}

export function updateQuote(quote: MessageQuote, updates: Partial<MessageQuote>): MessageQuote {
    return {
        ...quote,
        ...updates,
        updatedAt: new Date().toISOString(),
    };
}

export function getQuoteAgeInSeconds(quote: MessageQuote): number {
    const createdAt = new Date(quote.createdAt).getTime();
    const now = Date.now();
    return Math.floor((now - createdAt) / 1000);
}

export function getQuoteAgeInMinutes(quote: MessageQuote): number {
    return Math.floor(getQuoteAgeInSeconds(quote) / 60);
}

export function getQuoteAgeInHours(quote: MessageQuote): number {
    return Math.floor(getQuoteAgeInMinutes(quote) / 60);
}

export function getQuoteAgeInDays(quote: MessageQuote): number {
    return Math.floor(getQuoteAgeInHours(quote) / 24);
}

export function isQuoteRecent(quote: MessageQuote, thresholdMinutes: number = 60): boolean {
    return getQuoteAgeInMinutes(quote) < thresholdMinutes;
}

export function isQuoteOld(quote: MessageQuote, thresholdDays: number = 30): boolean {
    return getQuoteAgeInDays(quote) > thresholdDays;
}