/**
 * 已删除消息服务 - TypeScript API客户端
 * 提供已删除消息的查询、审核和管理功能
 */

import apiClient from './apiClient';

// ========== 类型定义 ==========

export enum MessageType {
    TEXT = 'TEXT',
    IMAGE = 'IMAGE',
    VOICE = 'VOICE',
    VIDEO = 'VIDEO',
    FILE = 'FILE',
    LOCATION = 'LOCATION',
    CONTACT = 'CONTACT',
    LINK = 'LINK',
    SYSTEM = 'SYSTEM',
    RICH_TEXT = 'RICH_TEXT',
    FORWARD = 'FORWARD',
    REPLY = 'REPLY',
    QUOTE = 'QUOTE',
    REACTION = 'REACTION',
    POLL = 'POLL',
    EVENT = 'EVENT',
    CUSTOM = 'CUSTOM'
}

export enum ReceiverType {
    USER = 'USER',
    GROUP = 'GROUP',
    CHANNEL = 'CHANNEL',
    TOPIC = 'TOPIC'
}

export enum DeleteReason {
    USER_SELF_DELETE = 'USER_SELF_DELETE',
    GROUP_ADMIN_DELETE = 'GROUP_ADMIN_DELETE',
    SYSTEM_ADMIN_DELETE = 'SYSTEM_ADMIN_DELETE',
    AUTOMATIC_CLEANUP = 'AUTOMATIC_CLEANUP',
    CONTENT_VIOLATION = 'CONTENT_VIOLATION',
    USER_REPORTED = 'USER_REPORTED',
    LEGAL_REQUEST = 'LEGAL_REQUEST',
    DATA_CORRUPTION = 'DATA_CORRUPTION',
    MIGRATION_CLEANUP = 'MIGRATION_CLEANUP',
    OTHER = 'OTHER'
}

export enum DeletedByType {
    USER = 'USER',
    ADMIN = 'ADMIN',
    SYSTEM = 'SYSTEM',
    BOT = 'BOT'
}

export enum AuditStatus {
    PENDING = 'PENDING',
    APPROVED = 'APPROVED',
    REJECTED = 'REJECTED',
    REVIEWED = 'REVIEWED',
    ESCALATED = 'ESCAPED',
    IGNORED = 'IGNORED'
}

export interface DeletedMessage {
    id: number;
    originalMessageId: string;
    messageType: MessageType;
    originalContent: string;
    contentHash: string;
    senderId: string;
    receiverId: string;
    receiverType: ReceiverType;
    deletedAt: string; // ISO日期时间
    deleteReason: DeleteReason;
    deletedByUserId: string;
    deletedByType: DeletedByType;
    adminVisible: boolean;
    auditStatus: AuditStatus;
    auditNotes?: string;
    auditedAt?: string;
    auditedByUserId?: string;
    operationLogId?: string;
    permanentlyDeleted: boolean;
    permanentDeleteAt?: string;
    retentionDays: number;
    expireDeleteAt: string;
    metadata?: string;
}

export interface DeletedMessageRequest {
    originalMessageId: string;
    senderId: string;
    receiverId: string;
    receiverType: ReceiverType;
    deletedByUserId: string;
    deleteReason: DeleteReason;
    originalContent?: string;
    messageType?: MessageType;
}

export interface AuditRequest {
    messageId: number;
    status: AuditStatus;
    notes?: string;
    auditorId: string;
}

export interface BatchAuditRequest {
    messageIds: number[];
    status: AuditStatus;
    notes?: string;
    auditorId: string;
}

export interface TimeRange {
    start: string; // ISO日期时间
    end: string;   // ISO日期时间
}

export interface SearchParams {
    senderId?: string;
    receiverId?: string;
    deleteReason?: DeleteReason;
    auditStatus?: AuditStatus;
    startDate: string;
    endDate: string;
}

export interface PaginatedResponse<T> {
    content: T[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
}

export interface StatsResponse {
    count: number;
}

export interface DistributionItem {
    key: string;
    count: number;
}

export interface HealthCheck {
    status: string;
    service: string;
    timestamp: string;
    recent_records: number;
    database: string;
}

// ========== API服务 ==========

class DeletedMessageService {
    private basePath = '/api/deleted-messages';

    // ========== 基本CRUD操作 ==========

    /**
     * 记录已删除消息
     */
    async recordDeletedMessage(request: DeletedMessageRequest): Promise<DeletedMessage> {
        const params = new URLSearchParams();
        params.append('originalMessageId', request.originalMessageId);
        params.append('senderId', request.senderId);
        params.append('receiverId', request.receiverId);
        params.append('receiverType', request.receiverType);
        params.append('deletedByUserId', request.deletedByUserId);
        params.append('deleteReason', request.deleteReason);
        
        if (request.originalContent) {
            params.append('originalContent', request.originalContent);
        }
        
        params.append('messageType', request.messageType || MessageType.TEXT);

        const response = await apiClient.post<DeletedMessage>(`${this.basePath}/record?${params.toString()}`);
        return response.data;
    }

    /**
     * 根据原始消息ID查询
     */
    async findByOriginalMessageId(originalMessageId: string): Promise<DeletedMessage | null> {
        try {
            const response = await apiClient.get<DeletedMessage>(
                `${this.basePath}/by-original-id/${originalMessageId}`
            );
            return response.data;
        } catch (error: any) {
            if (error.response?.status === 404) {
                return null;
            }
            throw error;
        }
    }

    /**
     * 检查消息是否存在
     */
    async existsByOriginalMessageId(originalMessageId: string): Promise<boolean> {
        const response = await apiClient.get<{ exists: boolean }>(
            `${this.basePath}/exists/${originalMessageId}`
        );
        return response.data.exists;
    }

    // ========== 查询操作 ==========

    /**
     * 按发送者查询
     */
    async getBySender(
        senderId: string, 
        page: number = 0, 
        size: number = 20
    ): Promise<PaginatedResponse<DeletedMessage>> {
        
        const response = await apiClient.get<DeletedMessage[]>(
            `${this.basePath}/by-sender/${senderId}?page=${page}&size=${size}`
        );

        // 注意：这里需要从响应头获取分页信息，或者后端返回完整的分页响应
        // 这里简化为返回包装的响应
        return {
            content: response.data,
            page,
            size,
            totalElements: response.data.length,
            totalPages: Math.ceil(response.data.length / size)
        };
    }

    /**
     * 按接收者查询
     */
    async getByReceiver(
        receiverId: string, 
        page: number = 0, 
        size: number = 20
    ): Promise<PaginatedResponse<DeletedMessage>> {
        
        const response = await apiClient.get<DeletedMessage[]>(
            `${this.basePath}/by-receiver/${receiverId}?page=${page}&size=${size}`
        );

        return {
            content: response.data,
            page,
            size,
            totalElements: response.data.length,
            totalPages: Math.ceil(response.data.length / size)
        };
    }

    /**
     * 按删除者查询
     */
    async getByDeleter(
        deletedByUserId: string, 
        page: number = 0, 
        size: number = 20
    ): Promise<PaginatedResponse<DeletedMessage>> {
        
        const response = await apiClient.get<DeletedMessage[]>(
            `${this.basePath}/by-deleter/${deletedByUserId}?page=${page}&size=${size}`
        );

        return {
            content: response.data,
            page,
            size,
            totalElements: response.data.length,
            totalPages: Math.ceil(response.data.length / size)
        };
    }

    /**
     * 按删除原因查询
     */
    async getByDeleteReason(
        deleteReason: DeleteReason, 
        page: number = 0, 
        size: number = 20
    ): Promise<PaginatedResponse<DeletedMessage>> {
        
        const response = await apiClient.get<DeletedMessage[]>(
            `${this.basePath}/by-reason/${deleteReason}?page=${page}&size=${size}`
        );

        return {
            content: response.data,
            page,
            size,
            totalElements: response.data.length,
            totalPages: Math.ceil(response.data.length / size)
        };
    }

    /**
     * 按时间范围查询
     */
    async getByTimeRange(timeRange: TimeRange): Promise<DeletedMessage[]> {
        const response = await apiClient.get<DeletedMessage[]>(
            `${this.basePath}/by-time-range?start=${encodeURIComponent(timeRange.start)}&end=${encodeURIComponent(timeRange.end)}`
        );
        return response.data;
    }

    // ========== 审核管理 ==========

    /**
     * 获取待审核消息
     */
    async getPendingReview(): Promise<DeletedMessage[]> {
        const response = await apiClient.get<DeletedMessage[]>(`${this.basePath}/pending-review`);
        return response.data;
    }

    /**
     * 获取需要审核的消息
     */
    async getNeedingReview(): Promise<DeletedMessage[]> {
        const response = await apiClient.get<DeletedMessage[]>(`${this.basePath}/needing-review`);
        return response.data;
    }

    /**
     * 审核消息
     */
    async auditMessage(request: AuditRequest): Promise<DeletedMessage> {
        const params = new URLSearchParams();
        params.append('status', request.status);
        params.append('auditorId', request.auditorId);
        
        if (request.notes) {
            params.append('notes', request.notes);
        }

        const response = await apiClient.post<DeletedMessage>(
            `${this.basePath}/audit/${request.messageId}?${params.toString()}`
        );
        return response.data;
    }

    /**
     * 批量审核
     */
    async batchAudit(request: BatchAuditRequest): Promise<DeletedMessage[]> {
        const params = new URLSearchParams();
        params.append('status', request.status);
        params.append('auditorId', request.auditorId);
        
        if (request.notes) {
            params.append('notes', request.notes);
        }

        const response = await apiClient.post<DeletedMessage[]>(
            `${this.basePath}/batch-audit?${params.toString()}`,
            request.messageIds
        );
        return response.data;
    }

    // ========== 清理操作 ==========

    /**
     * 标记为彻底删除
     */
    async markAsPermanentlyDeleted(messageId: number): Promise<DeletedMessage> {
        const response = await apiClient.post<DeletedMessage>(
            `${this.basePath}/permanent-delete/${messageId}`
        );
        return response.data;
    }

    /**
     * 批量标记彻底删除
     */
    async batchMarkAsPermanentlyDeleted(messageIds: number[]): Promise<DeletedMessage[]> {
        const response = await apiClient.post<DeletedMessage[]>(
            `${this.basePath}/batch-permanent-delete`,
            messageIds
        );
        return response.data;
    }

    /**
     * 清理过期消息
     */
    async cleanupExpiredMessages(): Promise<{ message: string; count: number; timestamp: string }> {
        const response = await apiClient.post<{ message: string; count: number; timestamp: string }>(
            `${this.basePath}/cleanup-expired`
        );
        return response.data;
    }

    /**
     * 清理旧的彻底删除记录
     */
    async cleanupOldPermanentlyDeleted(): Promise<{ message: string; count: number; timestamp: string }> {
        const response = await apiClient.post<{ message: string; count: number; timestamp: string }>(
            `${this.basePath}/cleanup-old-permanent`
        );
        return response.data;
    }

    // ========== 统计操作 ==========

    /**
     * 获取发送者统计
     */
    async getStatsBySender(senderId: string): Promise<StatsResponse> {
        const response = await apiClient.get<StatsResponse>(`${this.basePath}/stats/by-sender/${senderId}`);
        return response.data;
    }

    /**
     * 获取接收者统计
     */
    async getStatsByReceiver(receiverId: string): Promise<StatsResponse> {
        const response = await apiClient.get<StatsResponse>(`${this.basePath}/stats/by-receiver/${receiverId}`);
        return response.data;
    }

    /**
     * 获取删除者统计
     */
    async getStatsByDeleter(deletedByUserId: string): Promise<StatsResponse> {
        const response = await apiClient.get<StatsResponse>(`${this.basePath}/stats/by-deleter/${deletedByUserId}`);
        return response.data;
    }

    /**
     * 获取时间范围统计
     */
    async getStatsByTimeRange(timeRange: TimeRange): Promise<StatsResponse> {
        const response = await apiClient.get<StatsResponse>(
            `${this.basePath}/stats/by-time-range?start=${encodeURIComponent(timeRange.start)}&end=${encodeURIComponent(timeRange.end)}`
        );
        return response.data;
    }

    /**
     * 获取删除原因分布
     */
    async getDeleteReasonDistribution(timeRange: TimeRange): Promise<DistributionItem[]> {
        const response = await apiClient.get<[string, number][]>(
            `${this.basePath}/stats/distribution/reason?start=${encodeURIComponent(timeRange.start)}&end=${encodeURIComponent(timeRange.end)}`
        );
        
        // 转换响应格式
        return response.data.map(([key, count]) => ({
            key: key as string,
            count: count as number
        }));
    }

    /**
     * 获取删除类型分布
     */
    async getDeleteTypeDistribution(timeRange: TimeRange): Promise<DistributionItem[]> {
        const response = await apiClient.get<[string, number][]>(
            `${this.basePath}/stats/distribution/type?start=${encodeURIComponent(timeRange.start)}&end=${encodeURIComponent(timeRange.end)}`
        );
        
        return response.data.map(([key, count]) => ({
            key: key as string,
            count: count as number
        }));
    }

    // ========== 高级功能 ==========

    /**
     * 高级搜索
     */
    async advancedSearch(params: SearchParams): Promise<DeletedMessage[]> {
        const queryParams = new URLSearchParams();
        
        if (params.senderId) queryParams.append('senderId', params.senderId);
        if (params.receiverId) queryParams.append('receiverId', params.receiverId);
        if (params.deleteReason) queryParams.append('deleteReason', params.deleteReason);
        if (params.auditStatus) queryParams.append('auditStatus', params.auditStatus);
        
        queryParams.append('startDate', params.startDate);
        queryParams.append('endDate', params.endDate);

        const response = await apiClient.get<DeletedMessage[]>(
            `${this.basePath}/advanced-search?${queryParams.toString()}`
        );
        return response.data;
    }

    /**
     * 导出数据
     */
    async exportData(timeRange: TimeRange): Promise<any[]> {
        const response = await apiClient.get<any[]>(
            `${this.basePath}/export?start=${encodeURIComponent(timeRange.start)}&end=${encodeURIComponent(timeRange.end)}`
        );
        return response.data;
    }

    /**
     * 获取审计记录
     */
    async getAuditedRecords(): Promise<any[]> {
        const response = await apiClient.get<any[]>(`${this.basePath}/audited-records`);
        return response.data;
    }

    /**
     * 获取清理候选
     */
    async getCleanupCandidates(): Promise<DeletedMessage[]> {
        const response = await apiClient.get<DeletedMessage[]>(`${this.basePath}/cleanup-candidates`);
        return response.data;
    }

    /**
     * 获取短期保留消息
     */
    async getShortRetentionMessages(daysRemaining: number = 7): Promise<DeletedMessage[]> {
        const response = await apiClient.get<DeletedMessage[]>(
            `${this.basePath}/short-retention?daysRemaining=${daysRemaining}`
        );
        return response.data;
    }

    // ========== 健康检查 ==========

    /**
     * 健康检查
     */
    async healthCheck(): Promise<HealthCheck> {
        const response = await apiClient.get<HealthCheck>(`${this.basePath}/health`);
        return response.data;
    }

    // ========== 辅助方法 ==========

    /**
     * 获取删除原因描述
     */
    getDeleteReasonDescription(reason: DeleteReason): string {
        const descriptions: Record<DeleteReason, string> = {
            [DeleteReason.USER_SELF_DELETE]: '用户自行删除',
            [DeleteReason.GROUP_ADMIN_DELETE]: '群管理员删除',
            [DeleteReason.SYSTEM_ADMIN_DELETE]: '系统管理员删除',
            [DeleteReason.AUTOMATIC_CLEANUP]: '自动清理',
            [DeleteReason.CONTENT_VIOLATION]: '内容违规',
            [DeleteReason.USER_REPORTED]: '用户举报',
            [DeleteReason.LEGAL_REQUEST]: '法律要求',
            [DeleteReason.DATA_CORRUPTION]: '数据损坏',
            [DeleteReason.MIGRATION_CLEANUP]: '迁移清理',
            [DeleteReason.OTHER]: '其他原因'
        };
        return descriptions[reason] || '未知原因';
    }

    /**
     * 获取审核状态描述
     */
    getAuditStatusDescription(status: AuditStatus): string {
        const descriptions: Record<AuditStatus, string> = {
            [AuditStatus.PENDING]: '待审核',
            [AuditStatus.APPROVED]: '审核通过',
            [AuditStatus.REJECTED]: '审核拒绝',
            [AuditStatus.REVIEWED]: '已审查',
            [AuditStatus.ESCALATED]: '已升级',
            [AuditStatus.IGNORED]: '已忽略'
        };
        return descriptions[status] || '未知状态';
    }

    /**
     * 获取消息类型图标
     */
    getMessageTypeIcon(messageType: MessageType): string {
        const icons: Record<MessageType, string> = {
            [MessageType.TEXT]: '📝',
            [MessageType.IMAGE]: '🖼️',
            [MessageType.VOICE]: '🎤',
            [MessageType.VIDEO]: '🎬',
            [MessageType.FILE]: '📎',
            [MessageType.LOCATION]: '📍',
            [MessageType.CONTACT]: '👤',
            [MessageType.LINK]: '🔗',
            [MessageType.SYSTEM]: '⚙️',
            [MessageType.RICH_TEXT]: '📄',
            [MessageType.FORWARD]: '↪️',
            [MessageType.REPLY]: '↩️',
            [MessageType.QUOTE]: '💬',
            [MessageType.REACTION]: '👍',
            [MessageType.POLL]: '📊',
            [MessageType.EVENT]: '📅',
            [MessageType.CUSTOM]: '🔧'
        };
        return icons[messageType] || '📦';
    }

    /**
     * 格式化时间
     */
    formatDateTime(dateTime: string): string {
        const date = new Date(dateTime);
        return date.toLocaleString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });
    }

    /**
     * 获取安全预览内容
     */
    getSafePreview(content: string | undefined, maxLength: number = 50): string {
        if (!content) return '[无内容]';
        if (content.length <= maxLength) return content;
        return content.substring(0, maxLength) + '...';
    }

    /**
     * 检查消息是否过期
     */
    isExpired(expireDeleteAt: string): boolean {
        const expireTime = new Date(expireDeleteAt);
        const now = new Date();
        return now > expireTime;
    }

    /**
     * 获取剩余天数
     */
    getRemainingDays(expireDeleteAt: string): number {
        const expireTime = new Date(expireDeleteAt);
        const now = new Date();
        const diffTime = expireTime.getTime() - now.getTime();
        return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    }
}

// 导出单例实例
const deletedMessageService = new DeletedMessageService();
export default deletedMessageService;

// 也导出类型
export {
    DeletedMessageService,
    MessageType,
    ReceiverType,
    DeleteReason,
    DeletedByType,
    AuditStatus
};