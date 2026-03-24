/**
 * 消息置顶服务
 * 提供消息和会话置顶功能的 API 调用封装
 */

import axios from 'axios';
import { message } from 'antd';

// 置顶类型枚举
export enum PinType {
    SESSION = 'SESSION',
    MESSAGE = 'MESSAGE'
}

// 置顶记录接口
export interface PinRecord {
    id: number;
    pinType: PinType;
    sessionId: string;
    messageId?: number;
    pinIndex: number;
    pinnedBy: string;
    pinnedByName?: string;
    pinnedAt: string;
    expiresAt?: string;
    isExpired: boolean;
    content?: string;
    senderName?: string;
    senderId?: string;
    messageType?: string;
    timestamp?: string;
}

// 创建置顶请求接口
export interface CreatePinRequest {
    pinType: PinType;
    sessionId: string;
    messageId?: number;
    pinIndex?: number;
    expiresAt?: string;
}

// 更新置顶请求接口
export interface UpdatePinRequest {
    pinIndex?: number;
    expiresAt?: string;
}

// 置顶列表查询参数
export interface PinListParams {
    sessionId: string;
    pinType?: PinType;
    page?: number;
    size?: number;
    includeExpired?: boolean;
}

// 分页响应接口
export interface PaginatedResponse<T> {
    success: boolean;
    data: T[];
    page: number;
    size: number;
    totalPages: number;
    totalElements: number;
    first: boolean;
    last: boolean;
}

// API 响应接口
export interface ApiResponse<T = any> {
    success: boolean;
    message: string;
    data?: T;
    timestamp: string;
}

/**
 * 消息置顶服务类
 */
class MessagePinService {
    private baseURL: string;
    private headers: Record<string, string>;

    constructor() {
        this.baseURL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1';
        this.headers = {
            'Content-Type': 'application/json',
        };
    }

    /**
     * 设置认证头
     */
    private setAuthHeaders(): void {
        const token = localStorage.getItem('access_token');
        if (token) {
            this.headers['Authorization'] = `Bearer ${token}`;
        }
    }

    /**
     * 设置用户 ID 头
     */
    private setUserIdHeaders(userId?: number): void {
        const uid = userId || this.getCurrentUserId();
        if (uid) {
            this.headers['X-User-Id'] = uid.toString();
        }
    }

    /**
     * 获取当前用户 ID
     */
    private getCurrentUserId(): number | null {
        const userStr = localStorage.getItem('user');
        if (userStr) {
            try {
                const user = JSON.parse(userStr);
                return user.id || user.userId || null;
            } catch (e) {
                console.error('解析用户信息失败', e);
                return null;
            }
        }
        return null;
    }

    /**
     * 创建置顶记录
     */
    async createPin(request: CreatePinRequest): Promise<PinRecord> {
        try {
            this.setAuthHeaders();
            this.setUserIdHeaders();

            const response = await axios.post<ApiResponse<PinRecord>>(
                `${this.baseURL}/pins/create`,
                request,
                { headers: this.headers }
            );

            if (response.data.success) {
                message.success('置顶成功');
                return response.data.data!;
            } else {
                throw new Error(response.data.message);
            }
        } catch (error: any) {
            console.error('创建置顶失败', error);
            message.error(error.response?.data?.message || '创建置顶失败');
            throw error;
        }
    }

    /**
     * 置顶会话
     */
    async pinSession(sessionId: string, pinIndex?: number): Promise<PinRecord> {
        return this.createPin({
            pinType: PinType.SESSION,
            sessionId,
            pinIndex
        });
    }

    /**
     * 置顶消息
     */
    async pinMessage(sessionId: string, messageId: number, pinIndex?: number, expiresAt?: string): Promise<PinRecord> {
        return this.createPin({
            pinType: PinType.MESSAGE,
            sessionId,
            messageId,
            pinIndex,
            expiresAt
        });
    }

    /**
     * 获取置顶列表
     */
    async getPinList(params: PinListParams): Promise<PaginatedResponse<PinRecord>> {
        try {
            this.setAuthHeaders();
            this.setUserIdHeaders();

            const queryParams: any = { ...params };
            if (params.includeExpired !== undefined) {
                queryParams.includeExpired = params.includeExpired;
            }

            const response = await axios.get<PaginatedResponse<PinRecord>>(
                `${this.baseURL}/pins/list`,
                {
                    headers: this.headers,
                    params: queryParams
                }
            );

            return response.data;
        } catch (error: any) {
            console.error('获取置顶列表失败', error);
            message.error('获取置顶列表失败');
            throw error;
        }
    }

    /**
     * 获取会话的置顶消息列表
     */
    async getSessionPins(sessionId: string, includeExpired: boolean = false): Promise<PinRecord[]> {
        try {
            const response = await this.getPinList({
                sessionId,
                pinType: PinType.MESSAGE,
                includeExpired,
                page: 0,
                size: 100
            });
            return response.data;
        } catch (error) {
            console.error('获取会话置顶消息失败', error);
            return [];
        }
    }

    /**
     * 获取用户的置顶会话列表
     */
    async getUserPinnedSessions(): Promise<PinRecord[]> {
        try {
            // TODO: 调用实际的 API
            const response = await axios.get<ApiResponse<PinRecord[]>>(
                `${this.baseURL}/pins/sessions`,
                {
                    headers: this.headers
                }
            );

            if (response.data.success) {
                return response.data.data!;
            } else {
                throw new Error(response.data.message);
            }
        } catch (error: any) {
            console.error('获取置顶会话失败', error);
            return [];
        }
    }

    /**
     * 更新置顶记录
     */
    async updatePin(pinId: number, request: UpdatePinRequest): Promise<PinRecord> {
        try {
            this.setAuthHeaders();
            this.setUserIdHeaders();

            const response = await axios.put<ApiResponse<PinRecord>>(
                `${this.baseURL}/pins/${pinId}/update`,
                request,
                { headers: this.headers }
            );

            if (response.data.success) {
                message.success('更新成功');
                return response.data.data!;
            } else {
                throw new Error(response.data.message);
            }
        } catch (error: any) {
            console.error('更新置顶失败', error);
            message.error('更新置顶失败');
            throw error;
        }
    }

    /**
     * 调整置顶顺序
     */
    async reorderPin(pinId: number, newIndex: number): Promise<PinRecord> {
        return this.updatePin(pinId, { pinIndex: newIndex });
    }

    /**
     * 取消置顶
     */
    async unpin(pinId: number): Promise<boolean> {
        try {
            this.setAuthHeaders();
            this.setUserIdHeaders();

            const response = await axios.delete<ApiResponse>(
                `${this.baseURL}/pins/${pinId}`,
                { headers: this.headers }
            );

            if (response.data.success) {
                message.success('取消置顶成功');
                return true;
            } else {
                message.error(response.data.message);
                return false;
            }
        } catch (error: any) {
            console.error('取消置顶失败', error);
            message.error('取消置顶失败');
            return false;
        }
    }

    /**
     * 批量取消置顶
     */
    async batchUnpin(pinIds: number[]): Promise<number> {
        try {
            this.setAuthHeaders();
            this.setUserIdHeaders();

            const response = await axios.post<ApiResponse<{ count: number }>>(
                `${this.baseURL}/pins/batch-unpin`,
                { pinIds },
                { headers: this.headers }
            );

            if (response.data.success) {
                const count = response.data.data?.count || 0;
                message.success(`成功取消 ${count} 个置顶`);
                return count;
            } else {
                throw new Error(response.data.message);
            }
        } catch (error: any) {
            console.error('批量取消置顶失败', error);
            message.error('批量取消置顶失败');
            return 0;
        }
    }

    /**
     * 清理过期置顶
     */
    async cleanupExpiredPins(sessionId?: string): Promise<number> {
        try {
            this.setAuthHeaders();
            this.setUserIdHeaders();

            const params: any = {};
            if (sessionId) {
                params.sessionId = sessionId;
            }

            const response = await axios.post<ApiResponse<{ count: number }>>(
                `${this.baseURL}/pins/cleanup`,
                null,
                {
                    headers: this.headers,
                    params
                }
            );

            if (response.data.success) {
                const count = response.data.data?.count || 0;
                if (count > 0) {
                    message.info(`清理了 ${count} 个过期置顶`);
                }
                return count;
            } else {
                throw new Error(response.data.message);
            }
        } catch (error: any) {
            console.error('清理过期置顶失败', error);
            return 0;
        }
    }

    /**
     * 获取置顶统计信息
     */
    async getPinStats(sessionId?: string): Promise<any> {
        try {
            this.setAuthHeaders();
            this.setUserIdHeaders();

            const params: any = {};
            if (sessionId) {
                params.sessionId = sessionId;
            }

            const response = await axios.get<ApiResponse<any>>(
                `${this.baseURL}/pins/stats`,
                {
                    headers: this.headers,
                    params
                }
            );

            if (response.data.success) {
                return response.data.data;
            } else {
                throw new Error(response.data.message);
            }
        } catch (error: any) {
            console.error('获取置顶统计失败', error);
            return null;
        }
    }

    /**
     * 检查消息是否已置顶
     */
    async isMessagePinned(sessionId: string, messageId: number): Promise<boolean> {
        try {
            const pins = await this.getSessionPins(sessionId, false);
            return pins.some(pin => pin.messageId === messageId);
        } catch (error) {
            console.error('检查消息置顶状态失败', error);
            return false;
        }
    }

    /**
     * 检查会话是否已置顶
     */
    async isSessionPinned(sessionId: string): Promise<boolean> {
        try {
            const sessions = await this.getUserPinnedSessions();
            return sessions.some(pin => pin.sessionId === sessionId);
        } catch (error) {
            console.error('检查会话置顶状态失败', error);
            return false;
        }
    }

    /**
     * 获取最大置顶索引
     */
    getMaxPinIndex(pins: PinRecord[]): number {
        if (pins.length === 0) return 0;
        return Math.max(...pins.map(pin => pin.pinIndex));
    }

    /**
     * 格式化置顶时间
     */
    formatPinTime(pinnedAt?: string): string {
        if (!pinnedAt) return '';
        
        try {
            const date = new Date(pinnedAt);
            return date.toLocaleString('zh-CN', {
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            });
        } catch (e) {
            return pinnedAt;
        }
    }

    /**
     * 检查置顶是否过期
     */
    isExpired(expiresAt?: string): boolean {
        if (!expiresAt) return false;
        
        try {
            const expiryDate = new Date(expiresAt);
            return expiryDate < new Date();
        } catch (e) {
            return false;
        }
    }

    /**
     * 获取过期时间剩余（分钟）
     */
    getTimeRemaining(expiresAt?: string): number {
        if (!expiresAt) return -1;
        
        try {
            const expiryDate = new Date(expiresAt);
            const now = new Date();
            const diff = expiryDate.getTime() - now.getTime();
            return Math.max(0, Math.floor(diff / 60000));
        } catch (e) {
            return -1;
        }
    }

    /**
     * 格式化过期时间显示
     */
    formatExpiryTime(expiresAt?: string): string {
        if (!expiresAt) return '';
        
        const remaining = this.getTimeRemaining(expiresAt);
        if (remaining <= 0) return '已过期';
        
        if (remaining < 60) {
            return `剩余 ${remaining} 分钟`;
        } else if (remaining < 1440) {
            return `剩余 ${Math.floor(remaining / 60)} 小时`;
        } else {
            return `剩余 ${Math.floor(remaining / 1440)} 天`;
        }
    }
}

// 导出单例实例
export const messagePinService = new MessagePinService();

export default messagePinService;
