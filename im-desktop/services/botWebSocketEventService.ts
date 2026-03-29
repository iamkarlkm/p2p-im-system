import axios, { AxiosResponse } from 'axios';
import { BotWebSocketEvent, BotWebSocketEventCreateRequest, BotWebSocketEventUpdateRequest, BotWebSocketEventStats, BotWebSocketEventQueryParams, BotWebSocketEventBatchResponse } from '../types/botWebSocketEvent';

const API_BASE_URL = '/api/v1/bot-websocket-events';

class BotWebSocketEventService {
    private static instance: BotWebSocketEventService;

    private constructor() {}

    public static getInstance(): BotWebSocketEventService {
        if (!BotWebSocketEventService.instance) {
            BotWebSocketEventService.instance = new BotWebSocketEventService();
        }
        return BotWebSocketEventService.instance;
    }

    // Basic CRUD operations
    async createEvent(request: BotWebSocketEventCreateRequest): Promise<BotWebSocketEvent> {
        const response = await axios.post<BotWebSocketEvent>(API_BASE_URL, request);
        return response.data;
    }

    async getEventById(eventId: string): Promise<BotWebSocketEvent> {
        const response = await axios.get<BotWebSocketEvent>(`${API_BASE_URL}/${eventId}`);
        return response.data;
    }

    async getEventByIdAndBotId(eventId: string, botId: string): Promise<BotWebSocketEvent> {
        const response = await axios.get<BotWebSocketEvent>(`${API_BASE_URL}/${eventId}/bot/${botId}`);
        return response.data;
    }

    async updateEvent(eventId: string, request: BotWebSocketEventUpdateRequest): Promise<BotWebSocketEvent> {
        const response = await axios.put<BotWebSocketEvent>(`${API_BASE_URL}/${eventId}`, request);
        return response.data;
    }

    async deleteEvent(eventId: string): Promise<void> {
        await axios.delete(`${API_BASE_URL}/${eventId}`);
    }

    async deleteEventByIdAndBotId(eventId: string, botId: string): Promise<void> {
        await axios.delete(`${API_BASE_URL}/${eventId}/bot/${botId}`);
    }

    // Query operations
    async getEventsByBotId(botId: string, params?: BotWebSocketEventQueryParams): Promise<BotWebSocketEvent[]> {
        const response = await axios.get<BotWebSocketEvent[]>(`${API_BASE_URL}/bot/${botId}`, { params });
        return response.data;
    }

    async getEventsBySessionId(sessionId: string, params?: BotWebSocketEventQueryParams): Promise<BotWebSocketEvent[]> {
        const response = await axios.get<BotWebSocketEvent[]>(`${API_BASE_URL}/session/${sessionId}`, { params });
        return response.data;
    }

    async getEventsByEventType(eventType: string, params?: BotWebSocketEventQueryParams): Promise<BotWebSocketEvent[]> {
        const response = await axios.get<BotWebSocketEvent[]>(`${API_BASE_URL}/type/${eventType}`, { params });
        return response.data;
    }

    async getEventsByStatus(status: string, params?: BotWebSocketEventQueryParams): Promise<BotWebSocketEvent[]> {
        const response = await axios.get<BotWebSocketEvent[]>(`${API_BASE_URL}/status/${status}`, { params });
        return response.data;
    }

    async getEventsByBotIdAndStatus(botId: string, status: string, params?: BotWebSocketEventQueryParams): Promise<BotWebSocketEvent[]> {
        const response = await axios.get<BotWebSocketEvent[]>(`${API_BASE_URL}/bot/${botId}/status/${status}`, { params });
        return response.data;
    }

    // Event creation helper methods
    async createMessageEvent(
        botId: string,
        sessionId: string,
        sourceMessageId: string,
        sourceUserId: string,
        sourceConversationId: string,
        payload: string,
        metadata?: string
    ): Promise<BotWebSocketEvent> {
        const request: BotWebSocketEventCreateRequest = {
            botId,
            sessionId,
            eventType: 'MESSAGE_RECEIVED',
            eventSubtype: 'TEXT',
            payload,
            metadata: metadata || '',
            sourceMessageId,
            sourceUserId,
            sourceConversationId,
            priority: 1
        };
        return this.createEvent(request);
    }

    async createMessageEditedEvent(
        botId: string,
        sessionId: string,
        sourceMessageId: string,
        sourceUserId: string,
        sourceConversationId: string,
        oldContent: string,
        newContent: string
    ): Promise<BotWebSocketEvent> {
        const request: BotWebSocketEventCreateRequest = {
            botId,
            sessionId,
            eventType: 'MESSAGE_EDITED',
            eventSubtype: 'TEXT',
            payload: newContent,
            metadata: JSON.stringify({ old_content: oldContent }),
            sourceMessageId,
            sourceUserId,
            sourceConversationId,
            priority: 2
        };
        return this.createEvent(request);
    }

    async createMessageDeletedEvent(
        botId: string,
        sessionId: string,
        sourceMessageId: string,
        sourceUserId: string,
        sourceConversationId: string
    ): Promise<BotWebSocketEvent> {
        const request: BotWebSocketEventCreateRequest = {
            botId,
            sessionId,
            eventType: 'MESSAGE_DELETED',
            payload: 'Message deleted',
            sourceMessageId,
            sourceUserId,
            sourceConversationId,
            priority: 2
        };
        return this.createEvent(request);
    }

    async createConversationCreatedEvent(
        botId: string,
        sessionId: string,
        sourceConversationId: string,
        sourceUserId: string,
        conversationName: string
    ): Promise<BotWebSocketEvent> {
        const request: BotWebSocketEventCreateRequest = {
            botId,
            sessionId,
            eventType: 'CONVERSATION_CREATED',
            payload: conversationName,
            sourceConversationId,
            sourceUserId,
            priority: 3
        };
        return this.createEvent(request);
    }

    async createUserJoinedEvent(
        botId: string,
        sessionId: string,
        sourceConversationId: string,
        sourceUserId: string,
        username: string
    ): Promise<BotWebSocketEvent> {
        const request: BotWebSocketEventCreateRequest = {
            botId,
            sessionId,
            eventType: 'USER_JOINED',
            payload: username,
            sourceConversationId,
            sourceUserId,
            priority: 3
        };
        return this.createEvent(request);
    }

    async createUserLeftEvent(
        botId: string,
        sessionId: string,
        sourceConversationId: string,
        sourceUserId: string,
        username: string
    ): Promise<BotWebSocketEvent> {
        const request: BotWebSocketEventCreateRequest = {
            botId,
            sessionId,
            eventType: 'USER_LEFT',
            payload: username,
            sourceConversationId,
            sourceUserId,
            priority: 3
        };
        return this.createEvent(request);
    }

    async createWebhookEvent(
        botId: string,
        webhookUrl: string,
        eventType: string,
        payload: string,
        metadata?: string
    ): Promise<BotWebSocketEvent> {
        const request: BotWebSocketEventCreateRequest = {
            botId,
            eventType,
            webhookUrl,
            payload,
            metadata: metadata || '',
            priority: 1
        };
        return this.createEvent(request);
    }

    async createCustomEvent(
        botId: string,
        sessionId: string,
        eventType: string,
        eventSubtype: string,
        payload: string,
        metadata?: string,
        priority?: number,
        tags?: string
    ): Promise<BotWebSocketEvent> {
        const request: BotWebSocketEventCreateRequest = {
            botId,
            sessionId,
            eventType,
            eventSubtype,
            payload,
            metadata: metadata || '',
            priority: priority || 1,
            tags
        };
        return this.createEvent(request);
    }

    // Processing methods
    async getReadyForProcessing(): Promise<BotWebSocketEvent[]> {
        const response = await axios.get<BotWebSocketEvent[]>(`${API_BASE_URL}/ready`);
        return response.data;
    }

    async getPendingEventsByBotId(botId: string): Promise<BotWebSocketEvent[]> {
        const response = await axios.get<BotWebSocketEvent[]>(`${API_BASE_URL}/bot/${botId}/pending`);
        return response.data;
    }

    async getRetryableFailedEvents(): Promise<BotWebSocketEvent[]> {
        const response = await axios.get<BotWebSocketEvent[]>(`${API_BASE_URL}/retryable`);
        return response.data;
    }

    async markEventsForRetry(eventIds: string[], nextRetryAt: string): Promise<void> {
        await axios.post(`${API_BASE_URL}/batch/retry`, {
            eventIds,
            nextRetryAt
        });
    }

    async markEventsAsDelivered(eventIds: string[]): Promise<void> {
        await axios.post(`${API_BASE_URL}/batch/delivered`, { eventIds });
    }

    async markEventsAsAcknowledged(eventIds: string[]): Promise<void> {
        await axios.post(`${API_BASE_URL}/batch/acknowledged`, { eventIds });
    }

    async markEventsAsProcessed(eventIds: string[]): Promise<void> {
        await axios.post(`${API_BASE_URL}/batch/processed`, { eventIds });
    }

    async markEventsAsFailed(eventIds: string[], errorMessage: string): Promise<void> {
        await axios.post(`${API_BASE_URL}/batch/failed`, { eventIds, errorMessage });
    }

    async markEventAsFailed(eventId: string, errorMessage: string): Promise<void> {
        await this.markEventsAsFailed([eventId], errorMessage);
    }

    async updateWebhookResponse(eventId: string, responseCode: number, responseBody: string): Promise<void> {
        await axios.patch(`${API_BASE_URL}/${eventId}/webhook-response`, {
            responseCode,
            responseBody
        });
    }

    // Cleanup methods
    async cleanupOldAcknowledgedEvents(botId: string, threshold: string): Promise<number> {
        const response = await axios.delete<{ deletedCount: number }>(
            `${API_BASE_URL}/cleanup/acknowledged`,
            { params: { botId, threshold } }
        );
        return response.data.deletedCount;
    }

    async cleanupPermanentlyFailedEvents(threshold: string): Promise<number> {
        const response = await axios.delete<{ deletedCount: number }>(
            `${API_BASE_URL}/cleanup/failed`,
            { params: { threshold } }
        );
        return response.data.deletedCount;
    }

    // Statistics methods
    async countEventsByBotIdAndStatus(botId: string, status: string): Promise<number> {
        const response = await axios.get<{ count: number }>(`${API_BASE_URL}/bot/${botId}/status/${status}/count`);
        return response.data.count;
    }

    async countEventsBySessionIdAndStatus(sessionId: string, status: string): Promise<number> {
        const response = await axios.get<{ count: number }>(`${API_BASE_URL}/session/${sessionId}/status/${status}/count`);
        return response.data.count;
    }

    async countPermanentlyFailedEventsByBot(botId: string): Promise<number> {
        const response = await axios.get<{ count: number }>(`${API_BASE_URL}/bot/${botId}/failed/permanent/count`);
        return response.data.count;
    }

    async getEventStatusCountsForBot(botId: string): Promise<Array<{ status: string; count: number }>> {
        const response = await axios.get<Array<{ status: string; count: number }>>(`${API_BASE_URL}/bot/${botId}/stats/status`);
        return response.data;
    }

    async getEventTypeCountsForBot(botId: string): Promise<Array<{ eventType: string; count: number }>> {
        const response = await axios.get<Array<{ eventType: string; count: number }>>(`${API_BASE_URL}/bot/${botId}/stats/type`);
        return response.data;
    }

    async getEventDayCountsForBot(botId: string, startDate: string): Promise<Array<{ date: string; count: number }>> {
        const response = await axios.get<Array<{ date: string; count: number }>>(
            `${API_BASE_URL}/bot/${botId}/stats/daily`,
            { params: { startDate } }
        );
        return response.data;
    }

    // Advanced query methods
    async getEventsByBotAndTag(botId: string, tag: string, params?: BotWebSocketEventQueryParams): Promise<BotWebSocketEvent[]> {
        const response = await axios.get<BotWebSocketEvent[]>(`${API_BASE_URL}/bot/${botId}/tag/${tag}`, { params });
        return response.data;
    }

    async getEventsByBotAndMessage(botId: string, messageId: string, params?: BotWebSocketEventQueryParams): Promise<BotWebSocketEvent[]> {
        const response = await axios.get<BotWebSocketEvent[]>(`${API_BASE_URL}/bot/${botId}/message/${messageId}`, { params });
        return response.data;
    }

    async getEventsByBotAndConversation(botId: string, conversationId: string, params?: BotWebSocketEventQueryParams): Promise<BotWebSocketEvent[]> {
        const response = await axios.get<BotWebSocketEvent[]>(`${API_BASE_URL}/bot/${botId}/conversation/${conversationId}`, { params });
        return response.data;
    }

    async getEventsByBotAndUser(botId: string, userId: string, params?: BotWebSocketEventQueryParams): Promise<BotWebSocketEvent[]> {
        const response = await axios.get<BotWebSocketEvent[]>(`${API_BASE_URL}/bot/${botId}/user/${userId}`, { params });
        return response.data;
    }

    async getEventsByBotAndDevice(botId: string, deviceId: string, params?: BotWebSocketEventQueryParams): Promise<BotWebSocketEvent[]> {
        const response = await axios.get<BotWebSocketEvent[]>(`${API_BASE_URL}/bot/${botId}/device/${deviceId}`, { params });
        return response.data;
    }

    async getEventsByBotAndSession(botId: string, sessionId: string, params?: BotWebSocketEventQueryParams): Promise<BotWebSocketEvent[]> {
        const response = await axios.get<BotWebSocketEvent[]>(`${API_BASE_URL}/bot/${botId}/session/${sessionId}`, { params });
        return response.data;
    }

    async getPendingWebhookEventsByBot(botId: string): Promise<BotWebSocketEvent[]> {
        const response = await axios.get<BotWebSocketEvent[]>(`${API_BASE_URL}/bot/${botId}/webhook/pending`);
        return response.data;
    }

    async getUnacknowledgedWebhookEventsByBot(botId: string): Promise<BotWebSocketEvent[]> {
        const response = await axios.get<BotWebSocketEvent[]>(`${API_BASE_URL}/bot/${botId}/webhook/unacknowledged`);
        return response.data;
    }

    async getRetryableWebhookEventsByBot(botId: string): Promise<BotWebSocketEvent[]> {
        const response = await axios.get<BotWebSocketEvent[]>(`${API_BASE_URL}/bot/${botId}/webhook/retryable`);
        return response.data;
    }

    async getPendingDirectEventsByBot(botId: string): Promise<BotWebSocketEvent[]> {
        const response = await axios.get<BotWebSocketEvent[]>(`${API_BASE_URL}/bot/${botId}/direct/pending`);
        return response.data;
    }

    async getDistinctEventTypesByBot(botId: string): Promise<string[]> {
        const response = await axios.get<string[]>(`${API_BASE_URL}/bot/${botId}/event-types`);
        return response.data;
    }

    async getDistinctEventSubtypesByBot(botId: string): Promise<string[]> {
        const response = await axios.get<string[]>(`${API_BASE_URL}/bot/${botId}/event-subtypes`);
        return response.data;
    }

    // Batch operations
    async processBatchOfEvents(eventIds: string[], processor: (event: BotWebSocketEvent) => Promise<void>): Promise<void> {
        const events = await Promise.all(eventIds.map(id => this.getEventById(id)));
        
        for (const event of events) {
            try {
                await processor(event);
                await this.markEventsAsProcessed([event.id]);
            } catch (error) {
                const errorMessage = error instanceof Error ? error.message : 'Unknown error';
                await this.markEventAsFailed(event.id, errorMessage);
            }
        }
    }

    async retryFailedEvents(retryThreshold: string): Promise<void> {
        await axios.post(`${API_BASE_URL}/retry-failed`, { retryThreshold });
    }

    // Health check methods
    async isBotEventQueueHealthy(botId: string): Promise<boolean> {
        try {
            const response = await axios.get<{ healthy: boolean }>(`${API_BASE_URL}/bot/${botId}/health`);
            return response.data.healthy;
        } catch (error) {
            console.error('Health check failed:', error);
            return false;
        }
    }

    async getBotEventQueueStatus(botId: string): Promise<string> {
        try {
            const response = await axios.get<{ status: string }>(`${API_BASE_URL}/bot/${botId}/queue-status`);
            return response.data.status;
        } catch (error) {
            console.error('Failed to get queue status:', error);
            return 'UNKNOWN';
        }
    }

    // WebSocket integration
    async subscribeToBotEvents(botId: string, callback: (event: BotWebSocketEvent) => void): Promise<WebSocket> {
        const wsUrl = `${window.location.protocol === 'https:' ? 'wss:' : 'ws:'}//${window.location.host}/ws/bot-events/${botId}`;
        const ws = new WebSocket(wsUrl);
        
        ws.onmessage = (message) => {
            try {
                const event: BotWebSocketEvent = JSON.parse(message.data);
                callback(event);
            } catch (error) {
                console.error('Failed to parse WebSocket message:', error);
            }
        };
        
        ws.onopen = () => {
            console.log(`WebSocket connected for bot ${botId}`);
        };
        
        ws.onclose = () => {
            console.log(`WebSocket disconnected for bot ${botId}`);
        };
        
        return ws;
    }
}

export default BotWebSocketEventService;