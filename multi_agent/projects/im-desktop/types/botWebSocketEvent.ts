export interface BotWebSocketEvent {
    id: string;
    botId: string;
    sessionId?: string;
    eventType: string;
    eventSubtype?: string;
    payload: string;
    metadata?: string;
    status: string;
    deliveryAttempts: number;
    maxAttempts: number;
    nextRetryAt?: string;
    deliveredAt?: string;
    acknowledgedAt?: string;
    createdAt: string;
    updatedAt: string;
    processedAt?: string;
    webhookUrl?: string;
    webhookResponseCode?: number;
    webhookResponseBody?: string;
    priority: number;
    errorMessage?: string;
    sourceUserId?: string;
    sourceDeviceId?: string;
    sourceMessageId?: string;
    sourceConversationId?: string;
    tags?: string;
}

export interface BotWebSocketEventCreateRequest {
    botId: string;
    sessionId?: string;
    eventType: string;
    eventSubtype?: string;
    payload: string;
    metadata?: string;
    sourceMessageId?: string;
    sourceUserId?: string;
    sourceConversationId?: string;
    sourceDeviceId?: string;
    webhookUrl?: string;
    priority?: number;
    tags?: string;
}

export interface BotWebSocketEventUpdateRequest {
    sessionId?: string;
    eventSubtype?: string;
    payload?: string;
    metadata?: string;
    status?: string;
    deliveryAttempts?: number;
    maxAttempts?: number;
    nextRetryAt?: string;
    priority?: number;
    tags?: string;
    errorMessage?: string;
    webhookUrl?: string;
    webhookResponseCode?: number;
    webhookResponseBody?: string;
}

export interface BotWebSocketEventQueryParams {
    status?: string;
    eventType?: string;
    eventSubtype?: string;
    priority?: number;
    tags?: string;
    sourceUserId?: string;
    sourceDeviceId?: string;
    sourceMessageId?: string;
    sourceConversationId?: string;
    limit?: number;
    offset?: number;
    sortBy?: string;
    sortOrder?: 'asc' | 'desc';
}

export interface BotWebSocketEventStats {
    totalEvents: number;
    pendingEvents: number;
    deliveredEvents: number;
    acknowledgedEvents: number;
    failedEvents: number;
    processedEvents: number;
    permanentlyFailedEvents: number;
    averageDeliveryTime: number;
    successRate: number;
}

export interface BotWebSocketEventBatchResponse {
    processedCount: number;
    successCount: number;
    failureCount: number;
    eventIds: string[];
    errors?: Array<{
        eventId: string;
        error: string;
    }>;
}

export interface BotWebSocketEventHealthStatus {
    healthy: boolean;
    pendingCount: number;
    failedCount: number;
    lastProcessedAt?: string;
    queueStatus: string;
}

export interface BotWebSocketEventStatusCount {
    status: string;
    count: number;
}

export interface BotWebSocketEventTypeCount {
    eventType: string;
    count: number;
}

export interface BotWebSocketEventDailyCount {
    date: string;
    count: number;
}

export interface BotWebSocketEventRetryRequest {
    eventIds: string[];
    nextRetryAt: string;
}

export interface BotWebSocketEventBatchRequest {
    eventIds: string[];
}

export interface BotWebSocketEventFailedBatchRequest {
    eventIds: string[];
    errorMessage: string;
}

export interface BotWebSocketEventWebhookResponseRequest {
    responseCode: number;
    responseBody: string;
}

export interface BotWebSocketEventCleanupResponse {
    deletedCount: number;
}

export interface BotWebSocketEventCountResponse {
    count: number;
}

export interface BotWebSocketEventQueueStatusResponse {
    status: string;
}

export interface BotWebSocketEventHealthResponse {
    healthy: boolean;
}

export type BotWebSocketEventType = 
    | 'MESSAGE_RECEIVED'
    | 'MESSAGE_EDITED'
    | 'MESSAGE_DELETED'
    | 'CONVERSATION_CREATED'
    | 'USER_JOINED'
    | 'USER_LEFT'
    | 'WEBHOOK_TRIGGER'
    | 'CUSTOM';

export type BotWebSocketEventStatus = 
    | 'PENDING'
    | 'DELIVERED'
    | 'ACKNOWLEDGED'
    | 'FAILED'
    | 'PROCESSED';

export type BotWebSocketEventPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export const BotWebSocketEventPriorityMap: Record<string, number> = {
    'LOW': 1,
    'MEDIUM': 2,
    'HIGH': 3,
    'CRITICAL': 4,
};

export function getPriorityName(priority: number): string {
    const entries = Object.entries(BotWebSocketEventPriorityMap);
    const entry = entries.find(([, value]) => value === priority);
    return entry ? entry[0] : 'UNKNOWN';
}

export function getPriorityValue(name: string): number {
    return BotWebSocketEventPriorityMap[name.toUpperCase()] || 1;
}

export interface BotWebSocketEventSubscription {
    botId: string;
    eventTypes?: string[];
    status?: string[];
    callback: (event: BotWebSocketEvent) => void;
}

export interface BotWebSocketEventFilter {
    botId?: string;
    sessionId?: string;
    eventTypes?: string[];
    status?: string[];
    priority?: number;
    tags?: string[];
    dateFrom?: string;
    dateTo?: string;
}

export function createBotWebSocketEventFilter(
    botId?: string,
    sessionId?: string,
    eventTypes?: string[],
    status?: string[],
    priority?: number,
    tags?: string[],
    dateFrom?: string,
    dateTo?: string
): BotWebSocketEventFilter {
    return {
        botId,
        sessionId,
        eventTypes,
        status,
        priority,
        tags,
        dateFrom,
        dateTo,
    };
}

export function isEventPending(event: BotWebSocketEvent): boolean {
    return event.status === 'PENDING';
}

export function isEventDelivered(event: BotWebSocketEvent): boolean {
    return event.status === 'DELIVERED';
}

export function isEventAcknowledged(event: BotWebSocketEvent): boolean {
    return event.status === 'ACKNOWLEDGED';
}

export function isEventFailed(event: BotWebSocketEvent): boolean {
    return event.status === 'FAILED';
}

export function isEventProcessed(event: BotWebSocketEvent): boolean {
    return event.status === 'PROCESSED';
}

export function canRetryEvent(event: BotWebSocketEvent): boolean {
    return event.status === 'FAILED' && event.deliveryAttempts < event.maxAttempts;
}

export function isEventRetryable(event: BotWebSocketEvent): boolean {
    if (!canRetryEvent(event)) {
        return false;
    }
    if (!event.nextRetryAt) {
        return true;
    }
    return new Date(event.nextRetryAt) <= new Date();
}

export function getEventAgeInSeconds(event: BotWebSocketEvent): number {
    const createdAt = new Date(event.createdAt).getTime();
    const now = Date.now();
    return Math.floor((now - createdAt) / 1000);
}

export function getEventAgeInMinutes(event: BotWebSocketEvent): number {
    return Math.floor(getEventAgeInSeconds(event) / 60);
}

export function getEventAgeInHours(event: BotWebSocketEvent): number {
    return Math.floor(getEventAgeInMinutes(event) / 60);
}

export function formatEventStatus(status: string): string {
    const statusMap: Record<string, string> = {
        'PENDING': 'Pending',
        'DELIVERED': 'Delivered',
        'ACKNOWLEDGED': 'Acknowledged',
        'FAILED': 'Failed',
        'PROCESSED': 'Processed',
    };
    return statusMap[status.toUpperCase()] || status;
}

export function formatEventType(eventType: string): string {
    return eventType.split('_').map(word => 
        word.charAt(0) + word.slice(1).toLowerCase()
    ).join(' ');
}