/**
 * 消息批量导出服务
 * 提供消息导出功能的API调用封装
 */

import axios from 'axios';
import { message } from 'antd';

// 导出格式枚举
export enum ExportFormat {
    JSON = 'JSON',
    CSV = 'CSV',
    TXT = 'TXT',
    PDF = 'PDF'
}

// 导出状态枚举
export enum ExportStatus {
    PENDING = 'PENDING',
    PROCESSING = 'PROCESSING',
    COMPLETED = 'COMPLETED',
    FAILED = 'FAILED',
    CANCELLED = 'CANCELLED'
}

// 会话类型枚举
export enum SessionType {
    PRIVATE_CHAT = 'PRIVATE_CHAT',
    GROUP_CHAT = 'GROUP_CHAT',
    CHANNEL = 'CHANNEL',
    TOPIC = 'TOPIC'
}

// 导出任务接口
export interface MessageExportTask {
    id: number;
    userId: number;
    exportName: string;
    description?: string;
    exportFormat: ExportFormat;
    status: ExportStatus;
    sessionId?: string;
    sessionType?: SessionType;
    startTime?: string;
    endTime?: string;
    messageCount: number;
    fileSize?: number;
    filePath?: string;
    exportOptions?: string;
    progress: number;
    progressMessage?: string;
    createdTime: string;
    updatedTime?: string;
    completedTime?: string;
    errorMessage?: string;
    exportStats?: string;
}

// 导出选项接口
export interface ExportOptions {
    includeAttachments?: boolean;
    includeReactions?: boolean;
    includeMetadata?: boolean;
    formatOptions?: Record<string, any>;
    compression?: 'none' | 'gzip';
    [key: string]: any;
}

// 创建导出请求接口
export interface CreateExportRequest {
    exportName: string;
    description?: string;
    format: ExportFormat;
    sessionId?: string;
    sessionType?: SessionType;
    startTime?: string;
    endTime?: string;
    exportOptions?: ExportOptions;
}

// 批量导出请求接口
export interface BatchExportRequest {
    sessionIds: string[];
    format: ExportFormat;
    exportOptions?: ExportOptions;
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

// API响应接口
export interface ApiResponse<T = any> {
    success: boolean;
    message: string;
    data?: T;
    timestamp: string;
}

// 导出统计接口
export interface ExportStats {
    totalExports?: number;
    totalMessages?: number;
    totalFileSize?: number;
    avgProgress?: number;
    statusDistribution?: Record<string, number>;
    formatDistribution?: Record<string, number>;
}

/**
 * 消息导出服务类
 */
class MessageExportService {
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
     * 设置用户ID头
     */
    private setUserIdHeaders(userId?: number): void {
        const uid = userId || this.getCurrentUserId();
        if (uid) {
            this.headers['X-User-Id'] = uid.toString();
        }
    }

    /**
     * 获取当前用户ID
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
     * 创建导出任务
     */
    async createExport(request: CreateExportRequest): Promise<MessageExportTask> {
        try {
            this.setAuthHeaders();
            this.setUserIdHeaders();

            const response = await axios.post<ApiResponse<MessageExportTask>>(
                `${this.baseURL}/exports/create`,
                request,
                { headers: this.headers }
            );

            if (response.data.success) {
                message.success('导出任务创建成功');
                return response.data.data!;
            } else {
                throw new Error(response.data.message);
            }
        } catch (error: any) {
            console.error('创建导出任务失败', error);
            message.error(error.response?.data?.message || '创建导出任务失败');
            throw error;
        }
    }

    /**
     * 获取导出任务列表
     */
    async listExports(
        page: number = 0,
        size: number = 20,
        status?: ExportStatus,
        format?: ExportFormat,
        sessionId?: string
    ): Promise<PaginatedResponse<MessageExportTask>> {
        try {
            this.setAuthHeaders();
            this.setUserIdHeaders();

            const params: any = { page, size };
            if (status) params.status = status;
            if (format) params.format = format;
            if (sessionId) params.sessionId = sessionId;

            const response = await axios.get<PaginatedResponse<MessageExportTask>>(
                `${this.baseURL}/exports/list`,
                {
                    headers: this.headers,
                    params
                }
            );

            return response.data;
        } catch (error: any) {
            console.error('获取导出列表失败', error);
            message.error('获取导出列表失败');
            throw error;
        }
    }

    /**
     * 获取导出任务详情
     */
    async getExport(exportId: number): Promise<MessageExportTask> {
        try {
            this.setAuthHeaders();
            this.setUserIdHeaders();

            const response = await axios.get<ApiResponse<MessageExportTask>>(
                `${this.baseURL}/exports/${exportId}`,
                { headers: this.headers }
            );

            if (response.data.success) {
                return response.data.data!;
            } else {
                throw new Error(response.data.message);
            }
        } catch (error: any) {
            console.error('获取导出详情失败', error);
            message.error('获取导出详情失败');
            throw error;
        }
    }

    /**
     * 下载导出文件
     */
    async downloadExport(exportId: number): Promise<void> {
        try {
            this.setAuthHeaders();
            this.setUserIdHeaders();

            const response = await axios.get(
                `${this.baseURL}/exports/${exportId}/download`,
                {
                    headers: this.headers,
                    responseType: 'blob'
                }
            );

            // 从响应头获取文件名
            const contentDisposition = response.headers['content-disposition'];
            let filename = `export_${exportId}`;
            
            if (contentDisposition) {
                const filenameMatch = contentDisposition.match(/filename="(.+)"/);
                if (filenameMatch && filenameMatch[1]) {
                    filename = filenameMatch[1];
                }
            }

            // 创建下载链接
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', filename);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);

            message.success('文件下载开始');
        } catch (error: any) {
            console.error('下载导出文件失败', error);
            
            if (error.response?.status === 403) {
                message.error('无权下载该文件');
            } else if (error.response?.status === 404) {
                message.error('文件不存在');
            } else {
                message.error('下载文件失败');
            }
            throw error;
        }
    }

    /**
     * 取消导出任务
     */
    async cancelExport(exportId: number): Promise<boolean> {
        try {
            this.setAuthHeaders();
            this.setUserIdHeaders();

            const response = await axios.post<ApiResponse>(
                `${this.baseURL}/exports/${exportId}/cancel`,
                {},
                { headers: this.headers }
            );

            if (response.data.success) {
                message.success('导出任务已取消');
                return true;
            } else {
                message.error(response.data.message);
                return false;
            }
        } catch (error: any) {
            console.error('取消导出任务失败', error);
            message.error('取消导出任务失败');
            return false;
        }
    }

    /**
     * 删除导出任务
     */
    async deleteExport(exportId: number): Promise<boolean> {
        try {
            this.setAuthHeaders();
            this.setUserIdHeaders();

            const response = await axios.delete<ApiResponse>(
                `${this.baseURL}/exports/${exportId}`,
                { headers: this.headers }
            );

            if (response.data.success) {
                message.success('导出任务已删除');
                return true;
            } else {
                message.error(response.data.message);
                return false;
            }
        } catch (error: any) {
            console.error('删除导出任务失败', error);
            message.error('删除导出任务失败');
            return false;
        }
    }

    /**
     * 批量导出多个会话
     */
    async batchExport(request: BatchExportRequest): Promise<MessageExportTask[]> {
        try {
            this.setAuthHeaders();
            this.setUserIdHeaders();

            const response = await axios.post<ApiResponse<MessageExportTask[]>>(
                `${this.baseURL}/exports/batch`,
                request,
                { headers: this.headers }
            );

            if (response.data.success) {
                message.success(`创建了 ${response.data.data!.length} 个导出任务`);
                return response.data.data!;
            } else {
                throw new Error(response.data.message);
            }
        } catch (error: any) {
            console.error('批量导出失败', error);
            message.error(error.response?.data?.message || '批量导出失败');
            throw error;
        }
    }

    /**
     * 获取导出统计信息
     */
    async getStats(): Promise<ExportStats> {
        try {
            this.setAuthHeaders();
            this.setUserIdHeaders();

            const response = await axios.get<ApiResponse<ExportStats>>(
                `${this.baseURL}/exports/stats`,
                { headers: this.headers }
            );

            if (response.data.success) {
                return response.data.data!;
            } else {
                throw new Error(response.data.message);
            }
        } catch (error: any) {
            console.error('获取导出统计失败', error);
            message.error('获取导出统计失败');
            throw error;
        }
    }

    /**
     * 获取支持的导出格式
     */
    async getSupportedFormats(): Promise<Record<string, string>> {
        try {
            this.setAuthHeaders();
            this.setUserIdHeaders();

            const response = await axios.get<ApiResponse<{ formats: Record<string, string> }>>(
                `${this.baseURL}/exports/formats`,
                { headers: this.headers }
            );

            if (response.data.success) {
                return response.data.data!.formats;
            } else {
                throw new Error(response.data.message);
            }
        } catch (error: any) {
            console.error('获取导出格式失败', error);
            return {
                JSON: 'JSON格式',
                CSV: 'CSV格式',
                TXT: '文本格式',
                PDF: 'PDF格式'
            };
        }
    }

    /**
     * 验证导出选项
     */
    async validateOptions(options: ExportOptions): Promise<ExportOptions> {
        try {
            this.setAuthHeaders();

            const response = await axios.post<ApiResponse<ExportOptions>>(
                `${this.baseURL}/exports/validate-options`,
                options,
                { headers: this.headers }
            );

            if (response.data.success) {
                return response.data.data!;
            } else {
                throw new Error(response.data.message);
            }
        } catch (error: any) {
            console.error('验证导出选项失败', error);
            return options; // 验证失败时返回原选项
        }
    }

    /**
     * 清理过期导出记录（仅管理员）
     */
    async cleanupExports(days: number = 30): Promise<{ cleanedCount: number }> {
        try {
            this.setAuthHeaders();
            this.setUserIdHeaders();

            const response = await axios.post<ApiResponse<{ cleanedCount: number }>>(
                `${this.baseURL}/exports/cleanup`,
                null,
                {
                    headers: this.headers,
                    params: { days }
                }
            );

            if (response.data.success) {
                message.success(`清理了 ${response.data.data!.cleanedCount} 条过期记录`);
                return response.data.data!;
            } else {
                throw new Error(response.data.message);
            }
        } catch (error: any) {
            console.error('清理导出记录失败', error);
            message.error('清理导出记录失败');
            throw error;
        }
    }

    /**
     * 上传导出模板
     */
    async uploadTemplate(file: File, templateType: string): Promise<any> {
        try {
            this.setAuthHeaders();

            const formData = new FormData();
            formData.append('file', file);
            formData.append('templateType', templateType);

            const response = await axios.post<ApiResponse>(
                `${this.baseURL}/exports/upload-template`,
                formData,
                {
                    headers: {
                        ...this.headers,
                        'Content-Type': 'multipart/form-data'
                    }
                }
            );

            if (response.data.success) {
                message.success('模板上传成功');
                return response.data;
            } else {
                throw new Error(response.data.message);
            }
        } catch (error: any) {
            console.error('上传模板失败', error);
            message.error(error.response?.data?.message || '上传模板失败');
            throw error;
        }
    }

    /**
     * 格式化文件大小
     */
    formatFileSize(bytes?: number): string {
        if (!bytes) return '0 B';
        
        const units = ['B', 'KB', 'MB', 'GB', 'TB'];
        let size = bytes;
        let unitIndex = 0;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return `${size.toFixed(2)} ${units[unitIndex]}`;
    }

    /**
     * 格式化时间
     */
    formatDateTime(dateTime?: string): string {
        if (!dateTime) return '';
        
        try {
            const date = new Date(dateTime);
            return date.toLocaleString('zh-CN', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            });
        } catch (e) {
            return dateTime;
        }
    }

    /**
     * 获取状态颜色
     */
    getStatusColor(status: ExportStatus): string {
        switch (status) {
            case ExportStatus.PENDING:
                return 'blue';
            case ExportStatus.PROCESSING:
                return 'orange';
            case ExportStatus.COMPLETED:
                return 'green';
            case ExportStatus.FAILED:
                return 'red';
            case ExportStatus.CANCELLED:
                return 'gray';
            default:
                return 'default';
        }
    }

    /**
     * 获取状态文本
     */
    getStatusText(status: ExportStatus): string {
        switch (status) {
            case ExportStatus.PENDING:
                return '待处理';
            case ExportStatus.PROCESSING:
                return '处理中';
            case ExportStatus.COMPLETED:
                return '已完成';
            case ExportStatus.FAILED:
                return '失败';
            case ExportStatus.CANCELLED:
                return '已取消';
            default:
                return '未知';
        }
    }

    /**
     * 获取导出进度
     */
    getProgress(exportTask: MessageExportTask): number {
        return exportTask.progress || 0;
    }

    /**
     * 是否可以下载
     */
    canDownload(exportTask: MessageExportTask): boolean {
        return exportTask.status === ExportStatus.COMPLETED && 
               exportTask.filePath != null;
    }

    /**
     * 是否可以取消
     */
    canCancel(exportTask: MessageExportTask): boolean {
        return exportTask.status === ExportStatus.PENDING || 
               exportTask.status === ExportStatus.PROCESSING;
    }

    /**
     * 是否可以删除
     */
    canDelete(exportTask: MessageExportTask): boolean {
        return true; // 所有状态都可以删除
    }

    /**
     * 获取默认导出选项
     */
    getDefaultExportOptions(): ExportOptions {
        return {
            includeAttachments: true,
            includeReactions: true,
            includeMetadata: true,
            formatOptions: {},
            compression: 'none'
        };
    }
}

// 导出单例实例
export const messageExportService = new MessageExportService();

export default messageExportService;