import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { noteService } from '@/services/noteService';
import type { ConversationNote, NoteTag, MessageAnnotation } from '@/types/notes';

export const useNoteStore = defineStore('note', () => {
  // ==================== State ====================
  const notes = ref<ConversationNote[]>([]);
  const tags = ref<NoteTag[]>([]);
  const annotations = ref<MessageAnnotation[]>([]);
  const pinnedNotes = ref<ConversationNote[]>([]);
  const starredAnnotations = ref<MessageAnnotation[]>([]);
  const currentNote = ref<ConversationNote | null>(null);
  const loading = ref(false);
  const stats = ref<Record<string, any>>({});

  // ==================== Getters ====================
  const noteCount = computed(() => notes.value.length);
  const tagCount = computed(() => tags.value.length);
  const annotationCount = computed(() => annotations.value.length);
  const starredCount = computed(() => starredAnnotations.value.length);

  const notesByConversation = computed(() => {
    const grouped: Record<number, ConversationNote[]> = {};
    notes.value.forEach(note => {
      if (!grouped[note.conversationId]) grouped[note.conversationId] = [];
      grouped[note.conversationId].push(note);
    });
    return grouped;
  });

  const annotationsByConversation = computed(() => {
    const grouped: Record<number, MessageAnnotation[]> = {};
    annotations.value.forEach(annot => {
      if (!grouped[annot.conversationId]) grouped[annot.conversationId] = [];
      grouped[annot.conversationId].push(annot);
    });
    return grouped;
  });

  const tagsByName = computed(() => {
    const map: Record<string, NoteTag> = {};
    tags.value.forEach(tag => {
      map[tag.tagName] = tag;
    });
    return map;
  });

  // ==================== Actions ====================

  async function fetchNotes(options?: {
    conversationId?: number;
    page?: number;
    size?: number;
  }) {
    loading.value = true;
    try {
      const response = await noteService.getNotes(options);
      notes.value = response.content;
      return response;
    } finally {
      loading.value = false;
    }
  }

  async function fetchPinnedNotes() {
    loading.value = true;
    try {
      const response = await noteService.getNotes({ size: 100 });
      pinnedNotes.value = response.content.filter(note => note.pinned);
    } finally {
      loading.value = false;
    }
  }

  async function fetchTags() {
    loading.value = true;
    try {
      tags.value = await noteService.getAllTags();
    } finally {
      loading.value = false;
    }
  }

  async function fetchTopTags(limit: number = 10) {
    loading.value = true;
    try {
      tags.value = await noteService.getTopTags(limit);
    } finally {
      loading.value = false;
    }
  }

  async function fetchAnnotations(options?: {
    conversationId?: number;
    page?: number;
    size?: number;
  }) {
    loading.value = true;
    try {
      const response = await noteService.getAnnotations(options);
      annotations.value = response.content;
      return response;
    } finally {
      loading.value = false;
    }
  }

  async function fetchStarredAnnotations() {
    loading.value = true;
    try {
      const response = await noteService.getStarredMessages();
      starredAnnotations.value = response.content;
    } finally {
      loading.value = false;
    }
  }

  async function fetchStats() {
    loading.value = true;
    try {
      stats.value = await noteService.getStats();
    } finally {
      loading.value = false;
    }
  }

  async function createNote(data: {
    conversationId: number;
    title?: string;
    content?: string;
    color?: string;
    tags?: string[];
  }) {
    loading.value = true;
    try {
      const note = await noteService.createNote(data);
      notes.value.unshift(note);
      if (note.pinned) {
        pinnedNotes.value.unshift(note);
      }
      return note;
    } finally {
      loading.value = false;
    }
  }

  async function updateNote(noteId: number, data: {
    title?: string;
    content?: string;
    color?: string;
    tags?: string[];
  }) {
    loading.value = true;
    try {
      const updated = await noteService.updateNote(noteId, data);
      const index = notes.value.findIndex(n => n.id === noteId);
      if (index !== -1) notes.value[index] = updated;

      const pinnedIndex = pinnedNotes.value.findIndex(n => n.id === noteId);
      if (pinnedIndex !== -1) {
        if (updated.pinned) {
          pinnedNotes.value[pinnedIndex] = updated;
        } else {
          pinnedNotes.value.splice(pinnedIndex, 1);
        }
      }
      return updated;
    } finally {
      loading.value = false;
    }
  }

  async function deleteNote(noteId: number) {
    loading.value = true;
    try {
      await noteService.deleteNote(noteId);
      notes.value = notes.value.filter(n => n.id !== noteId);
      pinnedNotes.value = pinnedNotes.value.filter(n => n.id !== noteId);
    } finally {
      loading.value = false;
    }
  }

  async function togglePinNote(noteId: number, pinned: boolean) {
    loading.value = true;
    try {
      const note = await noteService.pinNote(noteId, pinned);
      const index = notes.value.findIndex(n => n.id === noteId);
      if (index !== -1) notes.value[index] = note;

      if (pinned) {
        pinnedNotes.value.unshift(note);
      } else {
        pinnedNotes.value = pinnedNotes.value.filter(n => n.id !== noteId);
      }
      return note;
    } finally {
      loading.value = false;
    }
  }

  async function createTag(data: { tagName: string; color?: string; icon?: string }) {
    loading.value = true;
    try {
      const tag = await noteService.createTag(data);
      tags.value.push(tag);
      return tag;
    } finally {
      loading.value = false;
    }
  }

  async function annotateMessage(data: {
    messageId: number;
    conversationId: number;
    annotationType?: string;
    starred?: boolean;
    note?: string;
    color?: string;
    emoji?: string;
  }) {
    loading.value = true;
    try {
      const annotation = await noteService.annotateMessage(data);
      annotations.value.unshift(annotation);
      if (annotation.starred) {
        starredAnnotations.value.unshift(annotation);
      }
      return annotation;
    } finally {
      loading.value = false;
    }
  }

  async function toggleStarAnnotation(annotationId: number) {
    loading.value = true;
    try {
      await noteService.toggleStar(annotationId);
      const index = annotations.value.findIndex(a => a.id === annotationId);
      if (index !== -1) {
        annotations.value[index].starred = !annotations.value[index].starred;
        if (annotations.value[index].starred) {
          starredAnnotations.value.unshift(annotations.value[index]);
        } else {
          starredAnnotations.value = starredAnnotations.value.filter(a => a.id !== annotationId);
        }
      }
    } finally {
      loading.value = false;
    }
  }

  function clearStore() {
    notes.value = [];
    tags.value = [];
    annotations.value = [];
    pinnedNotes.value = [];
    starredAnnotations.value = [];
    currentNote.value = null;
    stats.value = {};
  }

  return {
    // State
    notes,
    tags,
    annotations,
    pinnedNotes,
    starredAnnotations,
    currentNote,
    loading,
    stats,

    // Getters
    noteCount,
    tagCount,
    annotationCount,
    starredCount,
    notesByConversation,
    annotationsByConversation,
    tagsByName,

    // Actions
    fetchNotes,
    fetchPinnedNotes,
    fetchTags,
    fetchTopTags,
    fetchAnnotations,
    fetchStarredAnnotations,
    fetchStats,
    createNote,
    updateNote,
    deleteNote,
    togglePinNote,
    createTag,
    annotateMessage,
    toggleStarAnnotation,
    clearStore,
  };
});
