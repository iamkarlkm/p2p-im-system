/**
 * 群公告服务 - 桌面端
 * 负责公告的创建、获取、编辑、删除、已读确认
 */
export class AnnouncementService {
    private baseUrl: string;
    private wsHandler: AnnouncementWebSocketHandler;
    
    constructor(baseUrl: string = '') {
        this.baseUrl = baseUrl;
        this.wsHandler = new AnnouncementWebSocketHandler();
    }
    
    // ============ REST API ============
    
    async createAnnouncement(params: {
        groupId: string;
        authorId: string;
        authorName: string;
        title: string;
        content: string;
        type?: 'NORMAL' | 'IMPORTANT';
    }): Promise<Announcement> {
        const res = await fetch(`${this.baseUrl}/api/announcement`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(params),
        });
        const data = await res.json();
        if (!data.success) throw new Error(data.message);
        return data.data;
    }
    
    async getGroupAnnouncements(groupId: string): Promise<Announcement[]> {
        const res = await fetch(`${this.baseUrl}/api/announcement/group/${groupId}`);
        const data = await res.json();
        if (!data.success) throw new Error(data.message);
        return data.data;
    }
    
    async getAnnouncement(announcementId: string): Promise<Announcement> {
        const res = await fetch(`${this.baseUrl}/api/announcement/${announcementId}`);
        const data = await res.json();
        if (!data.success) throw new Error(data.message);
        return data.data;
    }
    
    async updateAnnouncement(params: {
        announcementId: string;
        title: string;
        content: string;
        userId: string;
    }): Promise<Announcement> {
        const { announcementId, ...body } = params;
        const res = await fetch(`${this.baseUrl}/api/announcement/${announcementId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
        });
        const data = await res.json();
        if (!data.success) throw new Error(data.message);
        return data.data;
    }
    
    async deleteAnnouncement(params: {
        announcementId: string;
        userId: string;
    }): Promise<void> {
        const res = await fetch(`${this.baseUrl}/api/announcement/${params.announcementId}?userId=${params.userId}`, {
            method: 'DELETE',
        });
        const data = await res.json();
        if (!data.success) throw new Error(data.message);
    }
    
    async pinAnnouncement(params: {
        announcementId: string;
        userId: string;
    }): Promise<Announcement> {
        const res = await fetch(`${this.baseUrl}/api/announcement/${params.announcementId}/pin?userId=${params.userId}`, {
            method: 'POST',
        });
        const data = await res.json();
        if (!data.success) throw new Error(data.message);
        return data.data;
    }
    
    async unpinAnnouncement(params: {
        announcementId: string;
        userId: string;
    }): Promise<Announcement> {
        const res = await fetch(`${this.baseUrl}/api/announcement/${params.announcementId}/unpin?userId=${params.userId}`, {
            method: 'POST',
        });
        const data = await res.json();
        if (!data.success) throw new Error(data.message);
        return data.data;
    }
    
    async confirmAnnouncement(params: {
        announcementId: string;
        userId: string;
    }): Promise<boolean> {
        const res = await fetch(`${this.baseUrl}/api/announcement/${params.announcementId}/confirm?userId=${params.userId}`, {
            method: 'POST',
        });
        const data = await res.json();
        if (!data.success) throw new Error(data.message);
        return data.data;
    }
    
    async getAnnouncementsPaged(groupId: string, page: number = 1, pageSize: number = 20): Promise<PagedAnnouncements> {
        const res = await fetch(`${this.baseUrl}/api/announcement/group/${groupId}/paged?page=${page}&pageSize=${pageSize}`);
        const data = await res.json();
        if (!data.success) throw new Error(data.message);
        return data.data;
    }
    
    async getHistory(groupId: string, page: number = 1, pageSize: number = 20): Promise<Announcement[]> {
        const res = await fetch(`${this.baseUrl}/api/announcement/group/${groupId}/history?page=${page}&pageSize=${pageSize}`);
        const data = await res.json();
        if (!data.success) throw new Error(data.message);
        return data.data;
    }
    
    async getStatistics(groupId: string): Promise<AnnouncementStatistics> {
        const res = await fetch(`${this.baseUrl}/api/announcement/group/${groupId}/statistics`);
        const data = await res.json();
        if (!data.success) throw new Error(data.message);
        return data.data;
    }
    
    // ============ WebSocket 实时事件 ============
    
    subscribeToGroup(groupId: string): void {
        this.wsHandler.subscribe(groupId);
    }
    
    unsubscribeFromGroup(groupId: string): void {
        this.wsHandler.unsubscribe(groupId);
    }
    
    onAnnouncementCreated(callback: (announcement: Announcement) => void): void {
        this.wsHandler.on('announcement.created', callback);
    }
    
    onAnnouncementUpdated(callback: (announcement: Announcement) => void): void {
        this.wsHandler.on('announcement.updated', callback);
    }
    
    onAnnouncementDeleted(callback: (data: { announcementId: string }) => void): void {
        this.wsHandler.on('announcement.deleted', callback);
    }
    
    onAnnouncementPinned(callback: (announcement: Announcement) => void): void {
        this.wsHandler.on('announcement.pinned', callback);
    }
    
    onAnnouncementUnpinned(callback: (announcement: Announcement) => void): void {
        this.wsHandler.on('announcement.unpinned', callback);
    }
}

// ============ WebSocket Handler ============

export class AnnouncementWebSocketHandler {
    private ws: WebSocket | null = null;
    private listeners: Map<string, Function[]> = new Map();
    private reconnectAttempts = 0;
    private maxReconnectAttempts = 5;
    private reconnectDelay = 3000;
    private subscribedGroups: Set<string> = new Set();
    
    connect(wsUrl: string): void {
        if (this.ws?.readyState === WebSocket.OPEN) return;
        
        this.ws = new WebSocket(wsUrl);
        
        this.ws.onopen = () => {
            console.log('[AnnouncementWS] Connected');
            this.reconnectAttempts = 0;
            // 重新订阅群组
            for (const groupId of this.subscribedGroups) {
                this.sendSubscribe(groupId);
            }
        };
        
        this.ws.onmessage = (event) => {
            try {
                const msg = JSON.parse(event.data);
                const listeners = this.listeners.get(msg.type) || [];
                for (const cb of listeners) {
                    cb(msg.data);
                }
            } catch (e) {
                console.error('[AnnouncementWS] Parse error', e);
            }
        };
        
        this.ws.onclose = () => {
            console.log('[AnnouncementWS] Disconnected');
            this.attemptReconnect();
        };
        
        this.ws.onerror = (err) => {
            console.error('[AnnouncementWS] Error', err);
        };
    }
    
    private attemptReconnect(): void {
        if (this.reconnectAttempts >= this.maxReconnectAttempts) {
            console.log('[AnnouncementWS] Max reconnect attempts reached');
            return;
        }
        this.reconnectAttempts++;
        setTimeout(() => this.connect(this.ws?.url || ''), this.reconnectDelay * this.reconnectAttempts);
    }
    
    subscribe(groupId: string): void {
        this.subscribedGroups.add(groupId);
        this.sendSubscribe(groupId);
    }
    
    unsubscribe(groupId: string): void {
        this.subscribedGroups.delete(groupId);
        this.send({ type: 'announcement.unsubscribe', groupId });
    }
    
    private sendSubscribe(groupId: string): void {
        this.send({ type: 'announcement.subscribe', groupId });
    }
    
    private send(data: object): void {
        if (this.ws?.readyState === WebSocket.OPEN) {
            this.ws.send(JSON.stringify(data));
        }
    }
    
    on(event: string, callback: Function): void {
        if (!this.listeners.has(event)) {
            this.listeners.set(event, []);
        }
        this.listeners.get(event)!.push(callback);
    }
    
    off(event: string, callback: Function): void {
        const callbacks = this.listeners.get(event) || [];
        const idx = callbacks.indexOf(callback);
        if (idx >= 0) callbacks.splice(idx, 1);
    }
    
    disconnect(): void {
        this.ws?.close();
        this.ws = null;
        this.subscribedGroups.clear();
        this.listeners.clear();
    }
}

// ============ 类型定义 ============

export interface Announcement {
    announcementId: string;
    groupId: string;
    authorId: string;
    authorName: string;
    title: string;
    content: string;
    type: 'NORMAL' | 'IMPORTANT' | 'PINNED';
    pinned: boolean;
    edited: boolean;
    createdAt: string;
    updatedAt?: string;
    pinnedAt?: string;
    viewCount: number;
    confirmCount: number;
    deleted: boolean;
}

export interface PagedAnnouncements {
    announcements: Announcement[];
    total: number;
    page: number;
    pageSize: number;
    totalPages: number;
}

export interface AnnouncementStatistics {
    total: number;
    pinned: number;
    important: number;
    totalViews: number;
}

export default AnnouncementService;
