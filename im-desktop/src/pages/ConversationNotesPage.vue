<template>
  <div class="notes-page">
    <div class="notes-header">
      <h2>对话笔记</h2>
      <button class="btn-primary" @click="showCreateModal = true">+ 新建笔记</button>
    </div>

    <div v-if="tags.length > 0" class="tag-filter">
      <span v-for="tag in tags" :key="tag.id"
        class="tag-chip"
        :style="{ backgroundColor: tag.color + '20', color: tag.color, borderColor: tag.color }">
        {{ tag.name }}
      </span>
    </div>

    <div v-if="isLoading" class="loading">加载中...</div>
    <div v-else-if="notes.length === 0" class="empty">
      <p>暂无笔记</p>
      <small>在此会话中创建个人笔记，记录重要信息</small>
    </div>
    <div v-else class="notes-list">
      <div v-for="note in notes" :key="note.id" class="note-card">
        <div class="note-quote" v-if="note.quotedMessageContent">
          <span class="quote-label">💬 引用消息</span>
          <p>{{ note.quotedMessageContent }}</p>
        </div>
        <div class="note-content">{{ note.content }}</div>
        <div class="note-footer">
          <div class="note-tags">
            <span v-for="tag in note.tags" :key="tag.id"
              class="tag-chip small"
              :style="{ backgroundColor: tag.color + '20', color: tag.color }">
              {{ tag.name }}
            </span>
          </div>
          <div class="note-meta">
            <span>{{ formatTime(note.createdAt) }}</span>
            <button @click="editNote(note)">✏️</button>
            <button @click="removeNote(note.id)">🗑️</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showCreateModal" class="modal-overlay" @click.self="showCreateModal = false">
      <div class="modal">
        <h3>{{ editingNote ? '编辑笔记' : '新建笔记' }}</h3>
        <textarea v-model="noteContent" placeholder="写下你的笔记..." rows="6"></textarea>
        <div class="tag-selector">
          <span v-for="tag in tags" :key="tag.id"
            :class="['tag-chip', 'selectable', { selected: selectedTagIds.includes(tag.id) }]"
            :style="{ borderColor: tag.color, color: tag.color }"
            @click="toggleTag(tag.id)">
            {{ tag.name }}
          </span>
          <button class="btn-sm" @click="showTagCreate = true">+ 标签</button>
        </div>
        <div class="modal-actions">
          <button @click="showCreateModal = false">取消</button>
          <button class="btn-primary" @click="saveNote">{{ editingNote ? '保存' : '创建' }}</button>
        </div>
      </div>
    </div>

    <div v-if="showTagCreate" class="modal-overlay" @click.self="showTagCreate = false">
      <div class="modal small">
        <h3>新建标签</h3>
        <input v-model="newTagName" placeholder="标签名称" />
        <div class="color-picker">
          <span v-for="c in colorOptions" :key="c"
            :class="['color-dot', { selected: newTagColor === c }]"
            :style="{ backgroundColor: c }"
            @click="newTagColor = c" />
        </div>
        <div class="modal-actions">
          <button @click="showTagCreate = false">取消</button>
          <button class="btn-primary" @click="createTag">创建</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useConversationNoteStore } from '../stores/conversation-note-store';
import type { Note } from '../types/conversation-note';

const props = defineProps<{ conversationId: string }>();
const store = useConversationNoteStore();

const showCreateModal = ref(false);
const showTagCreate = ref(false);
const noteContent = ref('');
const editingNote = ref<Note | null>(null);
const selectedTagIds = ref<number[]>([]);
const newTagName = ref('');
const newTagColor = ref('#6366f1');

const colorOptions = ['#6366f1', '#ec4899', '#f59e0b', '#10b981', '#3b82f6', '#8b5cf6', '#ef4444', '#14b8a6'];

onMounted(() => {
  store.loadNotes(props.conversationId);
  store.loadTags();
});

function toggleTag(tagId: number) {
  const idx = selectedTagIds.value.indexOf(tagId);
  if (idx >= 0) selectedTagIds.value.splice(idx, 1);
  else selectedTagIds.value.push(tagId);
}

async function saveNote() {
  if (!noteContent.value.trim()) return;
  if (editingNote.value) {
    await store.updateNote(editingNote.value.id, noteContent.value, selectedTagIds.value);
  } else {
    await store.createNote(props.conversationId, noteContent.value, selectedTagIds.value);
  }
  showCreateModal.value = false;
  noteContent.value = '';
  editingNote.value = null;
  selectedTagIds.value = [];
}

function editNote(note: Note) {
  editingNote.value = note;
  noteContent.value = note.content;
  selectedTagIds.value = note.tags.map(t => t.id);
  showCreateModal.value = true;
}

async function removeNote(noteId: number) {
  if (confirm('确定删除此笔记?')) {
    await store.deleteNote(noteId);
  }
}

async function createTag() {
  if (!newTagName.value.trim()) return;
  await store.createTag(newTagName.value, newTagColor.value);
  newTagName.value = '';
  showTagCreate.value = false;
}

function formatTime(ts: string): string {
  return new Date(ts).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
}
</script>

<style scoped>
.notes-page { padding: 16px; height: 100%; overflow-y: auto; background: var(--bg-primary, #fff); }
.notes-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.notes-header h2 { margin: 0; }
.btn-primary { background: var(--primary, #4f46e5); color: white; border: none; padding: 8px 16px; border-radius: 8px; cursor: pointer; }
.tag-filter { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 12px; }
.tag-chip { padding: 4px 10px; border-radius: 12px; border: 1px solid; font-size: 12px; display: inline-flex; align-items: center; }
.tag-chip.small { padding: 2px 6px; font-size: 11px; }
.tag-chip.selectable { cursor: pointer; opacity: 0.6; }
.tag-chip.selectable.selected { opacity: 1; background: currentColor !important; color: white !important; }
.notes-list { display: flex; flex-direction: column; gap: 12px; }
.note-card { border: 1px solid #e5e7eb; border-radius: 12px; padding: 12px; background: white; }
.note-quote { background: #f9fafb; border-left: 3px solid var(--primary, #4f46e5); padding: 8px; border-radius: 4px; margin-bottom: 8px; }
.quote-label { font-size: 11px; color: #6b7280; }
.note-quote p { margin: 4px 0 0; font-size: 13px; color: #374151; }
.note-content { font-size: 14px; line-height: 1.5; white-space: pre-wrap; }
.note-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 8px; }
.note-tags { display: flex; gap: 4px; flex-wrap: wrap; }
.note-meta { display: flex; gap: 8px; align-items: center; font-size: 12px; color: #9ca3af; }
.note-meta button { background: none; border: none; cursor: pointer; font-size: 14px; }
.loading, .empty { text-align: center; padding: 48px; color: #9ca3af; }
.empty small { display: block; margin-top: 4px; font-size: 12px; }
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 100; }
.modal { background: white; border-radius: 16px; padding: 24px; width: 90%; max-width: 480px; }
.modal.small { max-width: 320px; }
.modal h3 { margin: 0 0 16px; }
.modal textarea { width: 100%; border: 1px solid #e5e7eb; border-radius: 8px; padding: 8px; resize: vertical; font-size: 14px; }
.modal input { width: 100%; border: 1px solid #e5e7eb; border-radius: 8px; padding: 8px; margin-bottom: 12px; font-size: 14px; }
.tag-selector { display: flex; gap: 6px; flex-wrap: wrap; margin: 12px 0; }
.btn-sm { padding: 4px 8px; font-size: 12px; border: 1px dashed #d1d5db; border-radius: 8px; background: white; cursor: pointer; }
.color-picker { display: flex; gap: 8px; margin: 8px 0 16px; }
.color-dot { width: 28px; height: 28px; border-radius: 50%; cursor: pointer; border: 2px solid transparent; }
.color-dot.selected { border-color: #1f2937; transform: scale(1.2); }
.modal-actions { display: flex; gap: 8px; justify-content: flex-end; margin-top: 16px; }
.modal-actions button { padding: 8px 16px; border-radius: 8px; cursor: pointer; }
.modal-actions button:first-child { background: #f3f4f6; border: none; }
</style>
