/**
 * 媒体库服务 - 对话共同媒体库 (Desktop)
 * 提供聊天中的图片/视频/文件汇总展示
 */

import { BaseService, apiClient } from './base';

export interface MediaFile {
  fileId: string;
  fileName: string;
  fileType: 'image' | 'video' | 'audio' | 'document' | 'other';
  fileSize: number;
  mimeType: string;
  downloadUrl: string;
  thumbnailUrl?: string;
  uploadTime: string;
  conversationId?: string;
}

export interface MediaLibraryResponse {
  content: MediaFile[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface StorageInfo {
  totalSize: number;
  fileCount: number;
  formattedSize: string;
}

class MediaLibraryService extends BaseService {

  /**
   * 获取会话媒体文件列表
   */
  async getConversationMedia(
    conversationId: string,
    page: number = 0,
    size: number = 20
  ): Promise<MediaLibraryResponse> {
    return apiClient.get<MediaLibraryResponse>(
      `/files/conversation/${conversationId}/media`,
      { page, size }
    );
  }

  /**
   * 获取图片列表
   */
  async getImages(conversationId: string, page: number = 0, size: number = 20): Promise<MediaLibraryResponse> {
    const response = await this.getConversationMedia(conversationId, page, size);
    return {
      ...response,
      content: response.content.filter(f => f.fileType === 'image')
    };
  }

  /**
   * 获取视频列表
   */
  async getVideos(conversationId: string, page: number = 0, size: number = 20): Promise<MediaLibraryResponse> {
    const response = await this.getConversationMedia(conversationId, page, size);
    return {
      ...response,
      content: response.content.filter(f => f.fileType === 'video')
    };
  }

  /**
   * 获取文件列表
   */
  async getDocuments(conversationId: string, page: number = 0, size: number = 20): Promise<MediaLibraryResponse> {
    const response = await this.getConversationMedia(conversationId, page, size);
    return {
      ...response,
      content: response.content.filter(f => f.fileType === 'document')
    };
  }

  /**
   * 上传文件
   */
  async uploadFile(
    file: File,
    conversationId: string,
    onProgress?: (percent: number) => void
  ): Promise<MediaFile> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('conversationId', conversationId);

    return apiClient.uploadFile<MediaFile>(
      '/files/upload',
      formData,
      onProgress
    );
  }

  /**
   * 获取下载URL
   */
  async getDownloadUrl(fileId: string): Promise<string> {
    const response = await apiClient.get<{ downloadUrl: string }>(`/files/${fileId}/download`);
    return response.downloadUrl;
  }

  /**
   * 获取文件信息
   */
  async getFileInfo(fileId: string): Promise<MediaFile> {
    return apiClient.get<MediaFile>(`/files/${fileId}`);
  }

  /**
   * 删除文件
   */
  async deleteFile(fileId: string): Promise<void> {
    return apiClient.delete(`/files/${fileId}`);
  }

  /**
   * 获取用户存储信息
   */
  async getStorageInfo(userId: string): Promise<StorageInfo> {
    return apiClient.get<StorageInfo>(`/files/storage/${userId}`);
  }

  /**
   * 格式化文件大小
   */
  formatFileSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
    return (bytes / (1024 * 1024 * 1024)).toFixed(2) + ' GB';
  }

  /**
   * 获取文件类型图标
   */
  getFileTypeIcon(fileType: string): string {
    const icons: Record<string, string> = {
      image: '🖼️',
      video: '🎬',
      audio: '🎵',
      document: '📄',
      other: '📎'
    };
    return icons[fileType] || icons.other;
  }

  /**
   * 判断是否为图片
   */
  isImage(mimeType: string): boolean {
    return mimeType.startsWith('image/');
  }

  /**
   * 判断是否为视频
   */
  isVideo(mimeType: string): boolean {
    return mimeType.startsWith('video/');
  }

  /**
   * 判断是否为音频
   */
  isAudio(mimeType: string): boolean {
    return mimeType.startsWith('audio/');
  }
}

export const mediaLibraryService = new MediaLibraryService();
