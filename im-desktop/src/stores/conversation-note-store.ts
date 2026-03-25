import { defineStore } from 'pinia';
import { ref } from 'vue';
import { conversationNoteService } from '../services/conversation-note-service';
import type { Note, NotePage, TagInfo } from '../types/conversation-note';

export const useConversationNoteStore = defineStore('conversationNote', () => {
  const notes = ref<Note[]>([]);
  const currentPage = ref<NotePage | null>(null);
  const tags = ref<TagInfo[]>([]);
  const isLoading = ref(false);
  const activeConversationId = ref<string>('');

  async function loadNotes(conversationId: string) {
    isLoading.value = true;
    activeConversationId.value = conversationId;
    try {
      currentPage.value = await conversationNoteService.getNotes(conversationId, 0, 20);
      notes.value = currentPage.value.items;
    } catch (error) {
      console.error('Failed to load notes:', error);
    } finally {
      isLoading.value = false;
    }
  }

  async function createNote(conversationId: string, content: string, tagIds?: number[]) {
    const note = await conversationNoteService.createNote({
      conversationId, content, tagIds
    });
    notes.value = [note, ...notes.value];
  }

  async function updateNote(noteId: number, content: string, tagIds?: number[]) {
    const updated = await conversationNoteService.updateNote({
      id: noteId, conversationId: activeConversationId.value, content, tagIds
    });
    const idx = notes.value.findIndex(n => n.id === noteId);
    if (idx >= 0) notes.value[idx] = updated;
  }

  async function deleteNote(noteId: number) {
    await conversationNoteService.deleteNote(noteId);
    notes.value = notes.value.filter(n => n.id !== noteId);
  }

  async function loadTags() {
    try {
      tags.value = await conversationNoteService.getTags();
    } catch (error) {
      console.error('Failed to load tags:', error);
    }
  }

  async function createTag(name: string, color?: string) {
    const tag = await conversationNoteService.createTag(name, color);
    tags.value.push(tag);
  }

  async function deleteTag(tagId: number) {
    await conversationNoteService.deleteTag(tagId);
    tags.value = tags.value.filter(t => t.id !== tagId);
  }

  return {
    notes, currentPage, tags, isLoading, activeConversationId,
    loadNotes, createNote, updateNote, deleteNote, loadTags, createTag, deleteTag,
  };
});
