import { sharedMediaApi } from '../api/api-client';
import type { Note, NotePage, TagInfo, NoteRequest } from '../types/conversation-note';

class ConversationNoteService {
  async createNote(request: NoteRequest): Promise<Note> {
    return sharedMediaApi.post<Note>('/notes', request);
  }

  async getNotes(conversationId: string, page = 0, size = 20): Promise<NotePage> {
    return sharedMediaApi.get<NotePage>(
      `/notes/conversation/${conversationId}`,
      { params: { page, size } }
    );
  }

  async updateNote(request: NoteRequest): Promise<Note> {
    return sharedMediaApi.put<Note>('/notes', request);
  }

  async deleteNote(noteId: number): Promise<void> {
    return sharedMediaApi.delete(`/notes/${noteId}`);
  }

  async createTag(name: string, color?: string): Promise<TagInfo> {
    return sharedMediaApi.post<TagInfo>('/notes/tags', { name, color });
  }

  async getTags(): Promise<TagInfo[]> {
    return sharedMediaApi.get<TagInfo[]>('/notes/tags');
  }

  async deleteTag(tagId: number): Promise<void> {
    return sharedMediaApi.delete(`/notes/tags/${tagId}`);
  }
}

export const conversationNoteService = new ConversationNoteService();
