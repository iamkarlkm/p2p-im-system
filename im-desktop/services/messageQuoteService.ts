import axios, { AxiosResponse } from 'axios';
import { MessageQuote, MessageQuoteCreateRequest, MessageQuoteUpdateRequest, MessageQuoteStats, MessageQuoteQueryParams } from '../types/messageQuote';

const API_BASE_URL = '/api/v1/message-quotes';

class MessageQuoteService {
    private static instance: MessageQuoteService;

    private constructor() {}

    public static getInstance(): MessageQuoteService {
        if (!MessageQuoteService.instance) {
            MessageQuoteService.instance = new MessageQuoteService();
        }
        return MessageQuoteService.instance;
    }

    // Basic CRUD operations
    async createQuote(request: MessageQuoteCreateRequest): Promise<MessageQuote> {
        const response = await axios.post<MessageQuote>(API_BASE_URL, request);
        return response.data;
    }

    async getQuoteById(quoteId: string): Promise<MessageQuote> {
        const response = await axios.get<MessageQuote>(`${API_BASE_URL}/${quoteId}`);
        return response.data;
    }

    async getQuoteByIdAndConversationId(quoteId: string, conversationId: string): Promise<MessageQuote> {
        const response = await axios.get<MessageQuote>(`${API_BASE_URL}/${quoteId}/conversation/${conversationId}`);
        return response.data;
    }

    async getQuoteByMessageIdAndConversationId(messageId: string, conversationId: string): Promise<MessageQuote> {
        const response = await axios.get<MessageQuote>(`${API_BASE_URL}/message/${messageId}/conversation/${conversationId}`);
        return response.data;
    }

    async updateQuote(quoteId: string, request: MessageQuoteUpdateRequest): Promise<MessageQuote> {
        const response = await axios.put<MessageQuote>(`${API_BASE_URL}/${quoteId}`, request);
        return response.data;
    }

    async deleteQuote(quoteId: string): Promise<void> {
        await axios.delete(`${API_BASE_URL}/${quoteId}`);
    }

    // Query operations
    async getQuotesByMessageId(messageId: string): Promise<MessageQuote[]> {
        const response = await axios.get<MessageQuote[]>(`${API_BASE_URL}/message/${messageId}`);
        return response.data;
    }

    async getQuotesByQuotedMessageId(quotedMessageId: string): Promise<MessageQuote[]> {
        const response = await axios.get<MessageQuote[]>(`${API_BASE_URL}/quoted/${quotedMessageId}`);
        return response.data;
    }

    async getQuotesByConversationId(conversationId: string, params?: MessageQuoteQueryParams): Promise<MessageQuote[]> {
        const response = await axios.get<MessageQuote[]>(`${API_BASE_URL}/conversation/${conversationId}`, { params });
        return response.data;
    }

    async getQuotesByUserId(userId: string, params?: MessageQuoteQueryParams): Promise<MessageQuote[]> {
        const response = await axios.get<MessageQuote[]>(`${API_BASE_URL}/user/${userId}`, { params });
        return response.data;
    }

    async getQuotesByConversationIdAndUserId(conversationId: string, userId: string): Promise<MessageQuote[]> {
        const response = await axios.get<MessageQuote[]>(`${API_BASE_URL}/conversation/${conversationId}/user/${userId}`);
        return response.data;
    }

    async getQuotesForMessage(conversationId: string, quotedMessageId: string): Promise<MessageQuote[]> {
        const response = await axios.get<MessageQuote[]>(`${API_BASE_URL}/conversation/${conversationId}/message/${quotedMessageId}`);
        return response.data;
    }

    async getQuotesByQuotedSenderId(quotedSenderId: string): Promise<MessageQuote[]> {
        const response = await axios.get<MessageQuote[]>(`${API_BASE_URL}/sender/${quotedSenderId}`);
        return response.data;
    }

    async getQuotesByConversationIdAndQuotedSenderId(conversationId: string, quotedSenderId: string): Promise<MessageQuote[]> {
        const response = await axios.get<MessageQuote[]>(`${API_BASE_URL}/conversation/${conversationId}/sender/${quotedSenderId}`);
        return response.data;
    }

    async getQuotesByQuoteType(quoteType: string): Promise<MessageQuote[]> {
        const response = await axios.get<MessageQuote[]>(`${API_BASE_URL}/type/${quoteType}`);
        return response.data;
    }

    async getQuotesByConversationIdAndQuoteType(conversationId: string, quoteType: string): Promise<MessageQuote[]> {
        const response = await axios.get<MessageQuote[]>(`${API_BASE_URL}/conversation/${conversationId}/type/${quoteType}`);
        return response.data;
    }

    // Statistics methods
    async countQuotesForMessage(quotedMessageId: string): Promise<number> {
        const response = await axios.get<{ count: number }>(`${API_BASE_URL}/quoted/${quotedMessageId}/count`);
        return response.data.count;
    }

    async countQuotesForMessageInConversation(conversationId: string, quotedMessageId: string): Promise<number> {
        const response = await axios.get<{ count: number }>(`${API_BASE_URL}/conversation/${conversationId}/quoted/${quotedMessageId}/count`);
        return response.data.count;
    }

    async countQuotesInConversation(conversationId: string): Promise<number> {
        const response = await axios.get<{ count: number }>(`${API_BASE_URL}/conversation/${conversationId}/count`);
        return response.data.count;
    }

    async countQuotesByUser(userId: string): Promise<number> {
        const response = await axios.get<{ count: number }>(`${API_BASE_URL}/user/${userId}/count`);
        return response.data.count;
    }

    async countQuotesByUserInConversation(conversationId: string, userId: string): Promise<number> {
        const response = await axios.get<{ count: number }>(`${API_BASE_URL}/conversation/${conversationId}/user/${userId}/count`);
        return response.data.count;
    }

    async countQuotesByQuotedMessage(conversationId: string): Promise<Array<{ quotedMessageId: string; count: number }>> {
        const response = await axios.get<Array<{ quotedMessageId: string; count: number }>>(`${API_BASE_URL}/conversation/${conversationId}/stats/by-quoted-message`);
        return response.data;
    }

    async countQuotesByQuotedSender(conversationId: string): Promise<Array<{ quotedSenderId: string; count: number }>> {
        const response = await axios.get<Array<{ quotedSenderId: string; count: number }>>(`${API_BASE_URL}/conversation/${conversationId}/stats/by-quoted-sender`);
        return response.data;
    }

    // Delete operations
    async markQuoteAsDeleted(messageId: string): Promise<void> {
        await axios.delete(`${API_BASE_URL}/message/${messageId}/mark-deleted`);
    }

    async markQuotesForDeletedMessage(quotedMessageId: string): Promise<void> {
        await axios.delete(`${API_BASE_URL}/quoted/${quotedMessageId}/mark-deleted`);
    }

    async cleanupDeletedQuotes(conversationId: string, threshold: string): Promise<number> {
        const response = await axios.delete<{ deletedCount: number }>(
            `${API_BASE_URL}/conversation/${conversationId}/cleanup-deleted`,
            { params: { threshold } }
        );
        return response.data.deletedCount;
    }

    async cleanupOldQuotes(conversationId: string, threshold: string): Promise<number> {
        const response = await axios.delete<{ deletedCount: number }>(
            `${API_BASE_URL}/conversation/${conversationId}/cleanup-old`,
            { params: { threshold } }
        );
        return response.data.deletedCount;
    }

    // Search operations
    async searchQuotesByPreview(conversationId: string, keyword: string): Promise<MessageQuote[]> {
        const response = await axios.get<MessageQuote[]>(`${API_BASE_URL}/conversation/${conversationId}/search`, {
            params: { keyword }
        });
        return response.data;
    }

    async searchQuotesByContent(keyword: string): Promise<MessageQuote[]> {
        const response = await axios.get<MessageQuote[]>(`${API_BASE_URL}/search`, {
            params: { keyword }
        });
        return response.data;
    }

    // Batch operations
    async getQuotesByMessageIds(messageIds: string[]): Promise<MessageQuote[]> {
        const response = await axios.post<MessageQuote[]>(`${API_BASE_URL}/batch/by-message-ids`, messageIds);
        return response.data;
    }

    async getQuotesByQuotedMessageIds(quotedMessageIds: string[]): Promise<MessageQuote[]> {
        const response = await axios.post<MessageQuote[]>(`${API_BASE_URL}/batch/by-quoted-message-ids`, quotedMessageIds);
        return response.data;
    }

    async getQuotesInConversationByDateRange(conversationId: string, start: string, end: string): Promise<MessageQuote[]> {
        const response = await axios.get<MessageQuote[]>(`${API_BASE_URL}/conversation/${conversationId}/date-range`, {
            params: { start, end }
        });
        return response.data;
    }

    async getQuotesByUserInDateRange(userId: string, start: string, end: string): Promise<MessageQuote[]> {
        const response = await axios.get<MessageQuote[]>(`${API_BASE_URL}/user/${userId}/date-range`, {
            params: { start, end }
        });
        return response.data;
    }

    // Advanced queries
    async getQuotesByConversationIdAndHasAttachment(conversationId: string, hasAttachment: boolean): Promise<MessageQuote[]> {
        const response = await axios.get<MessageQuote[]>(`${API_BASE_URL}/conversation/${conversationId}/has-attachment`, {
            params: { hasAttachment }
        });
        return response.data;
    }

    async getQuotesByConversationIdAndAttachmentCountGreaterThan(conversationId: string, attachmentCount: number): Promise<MessageQuote[]> {
        const response = await axios.get<MessageQuote[]>(`${API_BASE_URL}/conversation/${conversationId}/attachment-count-greater-than`, {
            params: { attachmentCount }
        });
        return response.data;
    }

    async getQuotesByUserAfter(conversationId: string, userId: string, createdAt: string): Promise<MessageQuote[]> {
        const response = await axios.get<MessageQuote[]>(`${API_BASE_URL}/conversation/${conversationId}/user/${userId}/after`, {
            params: { createdAt }
        });
        return response.data;
    }

    async getQuotesByUserBefore(conversationId: string, userId: string, createdAt: string): Promise<MessageQuote[]> {
        const response = await axios.get<MessageQuote[]>(`${API_BASE_URL}/conversation/${conversationId}/user/${userId}/before`, {
            params: { createdAt }
        });
        return response.data;
    }

    async getQuotesByQuotedSenderAfter(conversationId: string, quotedSenderId: string, createdAt: string): Promise<MessageQuote[]> {
        const response = await axios.get<MessageQuote[]>(`${API_BASE_URL}/conversation/${conversationId}/sender/${quotedSenderId}/after`, {
            params: { createdAt }
        });
        return response.data;
    }

    async getQuotesByQuotedSenderBefore(conversationId: string, quotedSenderId: string, createdAt: string): Promise<MessageQuote[]> {
        const response = await axios.get<MessageQuote[]>(`${API_BASE_URL}/conversation/${conversationId}/sender/${quotedSenderId}/before`, {
            params: { createdAt }
        });
        return response.data;
    }

    // Helper methods
    async createTextQuote(
        messageId: string,
        quotedMessageId: string,
        conversationId: string,
        userId: string,
        quotedContent: string,
        quotedSenderId: string,
        quotedSenderName: string,
        quotePreview: string
    ): Promise<MessageQuote> {
        const request: MessageQuoteCreateRequest = {
            messageId,
            quotedMessageId,
            conversationId,
            userId,
            quotedContent,
            quotedSenderId,
            quotedSenderName,
            quoteType: 'TEXT',
            quotePreview,
            hasAttachment: false,
            attachmentCount: 0
        };
        return this.createQuote(request);
    }

    async createAttachmentQuote(
        messageId: string,
        quotedMessageId: string,
        conversationId: string,
        userId: string,
        quotedContent: string,
        quotedSenderId: string,
        quotedSenderName: string,
        quotePreview: string,
        attachmentCount: number
    ): Promise<MessageQuote> {
        const request: MessageQuoteCreateRequest = {
            messageId,
            quotedMessageId,
            conversationId,
            userId,
            quotedContent,
            quotedSenderId,
            quotedSenderName,
            quoteType: 'ATTACHMENT',
            quotePreview,
            hasAttachment: true,
            attachmentCount
        };
        return this.createQuote(request);
    }
}

export default MessageQuoteService;