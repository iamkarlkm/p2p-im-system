import { sharedMediaApi } from '../api/api-client';
import type { SharedMedia, MediaPage, MediaStatistics, LinkPreview, MediaRequest } from '../types/shared-media';

class SharedMediaService {
  async getSharedMedia(request: MediaRequest): Promise<MediaPage> {
    const params: Record<string, string | number> = {
      page: request.page ?? 0,
      size: request.size ?? 20,
    };
    if (request.mediaType) params.mediaType = request.mediaType;
    if (request.senderId) params.senderId = request.senderId;
    if (request.startTime) params.startTime = request.startTime;
    if (request.endTime) params.endTime = request.endTime;
    if (request.keyword) params.keyword = request.keyword;

    return sharedMediaApi.get<MediaPage>(
      `/media/conversation/${request.conversationId}`,
      { params }
    );
  }

  async getMediaTimeline(conversationId: string, page = 0, size = 20): Promise<SharedMedia[]> {
    return sharedMediaApi.get<SharedMedia[]>(
      `/media/timeline/${conversationId}`,
      { params: { page, size } }
    );
  }

  async getMediaStatistics(conversationId: string): Promise<MediaStatistics> {
    return sharedMediaApi.get<MediaStatistics>(
      `/media/statistics/${conversationId}`
    );
  }

  async getSharedLinks(conversationId: string, page = 0, size = 20): Promise<LinkPreview[]> {
    return sharedMediaApi.get<LinkPreview[]>(
      `/media/links/${conversationId}`,
      { params: { page, size } }
    );
  }

  async getAlbumMedia(conversationId: string, albumType: string, page = 0, size = 50): Promise<MediaPage> {
    return sharedMediaApi.get<MediaPage>(
      `/media/album/${conversationId}`,
      { params: { albumType, page, size } }
    );
  }

  async deleteMedia(mediaId: number): Promise<void> {
    return sharedMediaApi.delete(`/media/${mediaId}`);
  }

  formatFileSize(bytes: number): string {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    if (bytes < 1024 * 1024 * 1024) return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
    return `${(bytes / (1024 * 1024 * 1024)).toFixed(2)} GB`;
  }

  getMediaTypeIcon(mediaType: string): string {
    const icons: Record<string, string> = {
      IMAGE: '🖼️', VIDEO: '🎬', AUDIO: '🎵', FILE: '📎', LINK: '🔗', VOICE: '🎤'
    };
    return icons[mediaType] ?? '📄';
  }
}

export const sharedMediaService = new SharedMediaService();
