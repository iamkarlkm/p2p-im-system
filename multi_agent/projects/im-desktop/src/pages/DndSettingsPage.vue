<template>
  <div class="dnd-settings-page">
    <div class="page-header">
      <button class="back-btn" @click="$emit('back')">←</button>
      <h2>免打扰设置</h2>
    </div>

    <div class="page-content">
      <div class="setting-card">
        <div class="setting-row">
          <div class="setting-info">
            <span class="setting-label">开启免打扰</span>
            <span class="setting-desc">在指定时间段内静默推送</span>
          </div>
          <label class="toggle-switch">
            <input type="checkbox" v-model="localEnabled" @change="onEnabledChange" />
            <span class="toggle-slider"></span>
          </label>
        </div>
      </div>

      <div class="setting-card" v-if="localEnabled">
        <h3>时间段设置</h3>
        <div class="time-range-picker">
          <div class="time-input-group">
            <label>开始时间</label>
            <input type="time" v-model="localStartTime" @change="onTimeChange" />
          </div>
          <div class="time-separator">—</div>
          <div class="time-input-group">
            <label>结束时间</label>
            <input type="time" v-model="localEndTime" @change="onTimeChange" />
          </div>
        </div>
        <div class="timezone-info">时区: {{ settings.timezone }}</div>
      </div>

      <div class="setting-card" v-if="localEnabled">
        <h3>重复设置</h3>
        <div class="repeat-days">
          <button
            v-for="day in weekDays"
            :key="day.value"
            :class="['day-btn', { active: isDayActive(day.value) }]"
            @click="toggleDay(day.value)"
          >
            {{ day.label }}
          </button>
        </div>
        <div class="repeat-presets">
          <button class="preset-btn" @click="setPreset('everyday')">每天</button>
          <button class="preset-btn" @click="setPreset('workday')">工作日</button>
          <button class="preset-btn" @click="setPreset('weekend')">周末</button>
        </div>
      </div>

      <div class="setting-card" v-if="localEnabled">
        <h3>例外设置</h3>
        <div class="setting-row">
          <div class="setting-info">
            <span class="setting-label">允许@提及</span>
            <span class="setting-desc">被@时仍可收到通知</span>
          </div>
          <label class="toggle-switch">
            <input type="checkbox" v-model="localAllowMentions" @change="onTimeChange" />
            <span class="toggle-slider"></span>
          </label>
        </div>
        <div class="setting-row">
          <div class="setting-info">
            <span class="setting-label">允许星标好友</span>
            <span class="setting-desc">星标联系人消息仍可推送</span>
          </div>
          <label class="toggle-switch">
            <input type="checkbox" v-model="localAllowStarred" @change="onTimeChange" />
            <span class="toggle-slider"></span>
          </label>
        </div>
      </div>

      <div class="setting-card" v-if="localEnabled">
        <h3>自动回复</h3>
        <textarea
          v-model="localCustomMessage"
          placeholder="免打扰时自动回复内容（可选）"
          rows="3"
          @blur="onTimeChange"
        ></textarea>
      </div>

      <div class="dnd-status" v-if="status.inDndPeriod">
        <span class="dnd-active-badge">🔕 免打扰时段</span>
        <span class="dnd-status-text">当前处于免打扰状态</span>
      </div>

      <div class="loading-overlay" v-if="loading">
        <div class="spinner"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useDndSettingsStore } from '../stores/dnd-settings-store';
import { formatRepeatDays } from '../services/dnd-settings-service';

const emit = defineEmits(['back']);

const store = useDndSettingsStore();

const localEnabled = ref(false);
const localStartTime = ref('22:00');
const localEndTime = ref('08:00');
const localAllowMentions = ref(true);
const localAllowStarred = ref(true);
const localCustomMessage = ref('');

const settings = computed(() => store.settings);
const status = computed(() => store.status);
const loading = computed(() => store.loading);

const weekDays = [
  { value: 1, label: '一' },
  { value: 2, label: '二' },
  { value: 3, label: '三' },
  { value: 4, label: '四' },
  { value: 5, label: '五' },
  { value: 6, label: '六' },
  { value: 7, label: '日' },
];

onMounted(async () => {
  await store.loadSettings();
  await store.loadStatus();
  syncLocal();
});

function syncLocal() {
  localEnabled.value = settings.value.enabled;
  localStartTime.value = settings.value.startTime || '22:00';
  localEndTime.value = settings.value.endTime || '08:00';
  localAllowMentions.value = settings.value.allowMentions;
  localAllowStarred.value = settings.value.allowStarred;
  localCustomMessage.value = settings.value.customMessage || '';
}

function isDayActive(value: number): boolean {
  const days = (settings.value.repeatDays || '1,2,3,4,5,6,7').split(',').map(d => parseInt(d.trim()));
  return days.includes(value);
}

function toggleDay(value: number) {
  const current = (settings.value.repeatDays || '1,2,3,4,5,6,7').split(',').map(d => parseInt(d.trim()));
  const idx = current.indexOf(value);
  if (idx >= 0) {
    current.splice(idx, 1);
  } else {
    current.push(value);
    current.sort((a, b) => a - b);
  }
  store.updateSettings({ repeatDays: current.join(',') });
}

function setPreset(preset: string) {
  const presets: Record<string, string> = {
    everyday: '1,2,3,4,5,6,7',
    workday: '1,2,3,4,5',
    weekend: '6,7',
  };
  store.updateSettings({ repeatDays: presets[preset] });
}

async function onEnabledChange() {
  await store.updateSettings({ enabled: localEnabled.value });
}

async function onTimeChange() {
  await store.updateSettings({
    startTime: localStartTime.value,
    endTime: localEndTime.value,
    allowMentions: localAllowMentions.value,
    allowStarred: localAllowStarred.value,
    customMessage: localCustomMessage.value,
  });
}
</script>

<style scoped>
.dnd-settings-page {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #f5f5f5;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #fff;
  border-bottom: 1px solid #eee;
}

.page-header h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.back-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  padding: 4px 8px;
  color: #666;
}

.page-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  position: relative;
}

.setting-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
}

.setting-card h3 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.setting-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
}

.setting-info {
  display: flex;
  flex-direction: column;
}

.setting-label {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.setting-desc {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.toggle-switch {
  position: relative;
  display: inline-block;
  width: 48px;
  height: 26px;
  cursor: pointer;
}

.toggle-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-slider {
  position: absolute;
  inset: 0;
  background-color: #ccc;
  border-radius: 26px;
  transition: 0.3s;
}

.toggle-slider::before {
  content: '';
  position: absolute;
  height: 20px;
  width: 20px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  border-radius: 50%;
  transition: 0.3s;
}

.toggle-switch input:checked + .toggle-slider {
  background-color: #07c160;
}

.toggle-switch input:checked + .toggle-slider::before {
  transform: translateX(22px);
}

.time-range-picker {
  display: flex;
  align-items: center;
  gap: 12px;
}

.time-input-group {
  flex: 1;
}

.time-input-group label {
  display: block;
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.time-input-group input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  box-sizing: border-box;
}

.time-separator {
  color: #999;
  font-size: 18px;
  padding-top: 20px;
}

.timezone-info {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}

.repeat-days {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.day-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
  font-size: 13px;
  color: #666;
  transition: all 0.2s;
}

.day-btn.active {
  background: #07c160;
  border-color: #07c160;
  color: #fff;
}

.repeat-presets {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.preset-btn {
  padding: 6px 14px;
  border-radius: 16px;
  border: 1px solid #ddd;
  background: #fff;
  font-size: 13px;
  cursor: pointer;
  color: #666;
}

.preset-btn:hover {
  background: #f0f0f0;
}

textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  resize: none;
  box-sizing: border-box;
  font-family: inherit;
}

.dnd-status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #fffbe6;
  border-radius: 12px;
  margin-bottom: 12px;
}

.dnd-active-badge {
  font-size: 14px;
}

.dnd-status-text {
  font-size: 13px;
  color: #b8860b;
}

.loading-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255,255,255,0.8);
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #07c160;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
