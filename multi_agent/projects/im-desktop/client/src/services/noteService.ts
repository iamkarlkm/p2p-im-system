import { api } from '@/utils/api';
import type { ConversationNote, NoteTag, MessageAnnotation } from '@/types/notes';

export interface CreateNoteRequest {
  conversationId: number;
  title?: string;
  content?: string;
  color?: string;
  tags?: string[];
}

export interface UpdateNoteRequest {
  title?: string;
  content?: string;
  color?: string;
  tags?: string[];
}

export interface CreateTagRequest {
  tagName: string;
  color?: string;
  icon?: string;
}

export interface AnnotateMessageRequest {
  messageId: number;
  conversationId: number;
  annotationType?: string;
  starred?: boolean;
  note?: string;
  color?: string;
  emoji?: string;
}

export class NoteService {
  private baseUrl = '/api/v1/notes';

  // ==================== 笔记 API ====================

  async createNote(data: CreateNoteRequest): Promise<ConversationNote> {
    const response = await api.post(this.baseUrl, data);
    return response.data;
  }

  async getNotes(options?: {
    conversationId?: number;
    page?: number;
    size?: number;
    sortBy?: string;
    sortDir?: string;
  }): Promise<{
    content: ConversationNote[];
    totalPages: number;
    totalElements: number;
  }> {
    const params = new URLSearchParams();
    if (options?.conversationId) params.append('conversationId', options.conversationId.toString());
    if (options?.page) params.append('page', options.page.toString());
    if (options?.size) params.append('size', options.size.toString());
    if (options?.sortBy) params.append('sortBy', options.sortBy);
    if (options?.sortDir) params.append('sortDir', options.sortDir);

    const url = `${this.baseUrl}?${params.toString()}`;
    const response = await api.get(url);
    return response.data;
  }

  async getNote(noteId: number): Promise<ConversationNote> {
    const response = await api.get(`${this.baseUrl}/${noteId}`);
    return response.data;
  }

  async updateNote(noteId: number, data: UpdateNoteRequest): Promise<ConversationNote> {
    const response = await api.put(`${this.baseUrl}/${noteId}`, data);
    return response.data;
  }

  async deleteNote(noteId: number): Promise<void> {
    await api.delete(`${this.baseUrl}/${noteId}`);
  }

  async pinNote(noteId: number, pinned: boolean = true): Promise<ConversationNote> {
    const response = await api.patch(`${this.baseUrl}/${noteId}/pin?pinned=${pinned}`);
    return response.data;
  }

  async searchNotes(keyword: string, page: number = 0, size: number = 20): Promise<any> {
    const response = await api.get(`${this.baseUrl}/search`, {
      params: { keyword, page, size }
    });
    return response.data;
  }

  async getNotesByTag(tag: string, page: number = 0, size: number = 20): Promise<any> {
    const response = await api.get(`${this.baseUrl}/by-tag`, {
      params: { tag, page, size }
    });
    return response.data;
  }

  // ==================== 标签 API ====================

  async createTag(data: CreateTagRequest): Promise<NoteTag> {
    const response = await api.post(`${this.baseUrl}/tags`, data);
    return response.data;
  }

  async getAllTags(): Promise<NoteTag[]> {
    const response = await api.get(`${this.baseUrl}/tags`);
    return response.data;
  }

  async getTopTags(limit: number = 10): Promise<NoteTag[]> {
    const response = await api.get(`${this.baseUrl}/tags/top`, {
      params: { limit }
    });
    return response.data;
  }

  async updateTag(tagId: number, data: Partial<CreateTagRequest>): Promise<NoteTag> {
    const response = await api.put(`${this.baseUrl}/tags/${tagId}`, data);
    return response.data;
  }

  async deleteTag(tagId: number): Promise<void> {
    await api.delete(`${this.baseUrl}/tags/${tagId}`);
  }

  // ==================== 消息标注 API ====================

  async annotateMessage(data: AnnotateMessageRequest): Promise<MessageAnnotation> {
    const response = await api.post(`${this.baseUrl}/annotations`, data);
    return response.data;
  }

  async getAnnotations(options?: {
    conversationId?: number;
    page?: number;
    size?: number;
  }): Promise<any> {
    const params = new URLSearchParams();
    if (options?.conversationId) params.append('conversationId', options.conversationId.toString());
    if (options?.page) params.append('page', options.page.toString());
    if (options?.size) params.append('size', options.size.toString());

    const url = `${this.baseUrl}/annotations?${params.toString()}`;
    const response = await api.get(url);
    return response.data;
  }

  async getStarredMessages(page: number = 0, size: number = 50): Promise<any> {
    const response = await api.get(`${this.baseUrl}/annotations/starred`, {
      params: { page, size }
    });
    return response.data;
  }

  async toggleStar(annotationId: number): Promise<void> {
    await api.patch(`${this.baseUrl}/annotations/${annotationId}/star`);
  }

  async deleteAnnotation(annotationId: number): Promise<void> {
    await api.delete(`${this.baseUrl}/annotations/${annotationId}`);
  }

  // ==================== 统计 API ====================

  async getStats(): Promise<Record<string, any>> {
    const response = await api.get(`${this.baseUrl}/stats`);
    return response.data;
  }

  // ==================== 批量操作 ====================

  async batchDeleteNotes(noteIds: number[]): Promise<void> {
    await Promise.all(noteIds.map(id => this.deleteNote(id)));
  }

  async batchPinNotes(noteIds: number[], pinned: boolean = true): Promise<void> {
    await Promise.all(noteIds.map(id => this.pinNote(id, pinned)));
  }

  async exportNotes(format: 'json' | 'csv' | 'txt' = 'json'): Promise<string> {
    const response = await api.get(`${this.baseUrl}/export`, {
      params: { format },
      responseType: 'blob'
    });
    return response.data;
  }
}

export const noteService = new NoteService();
