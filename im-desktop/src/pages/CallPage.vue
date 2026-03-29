<template>
  <div class="call-page" :class="{ 'video-mode': isVideo }">
    <div v-if="!currentCall" class="call-dialer">
      <h2>发起通话</h2>
      <div class="input-group">
        <label>目标用户ID</label>
        <input v-model="targetUserId" type="number" placeholder="输入用户ID" />
      </div>
      <div class="call-type-selector">
        <button :class="{ active: callType === 'AUDIO' }" @click="callType = 'AUDIO'">
          语音通话
        </button>
        <button :class="{ active: callType === 'VIDEO' }" @click="callType = 'VIDEO'">
          视频通话
        </button>
      </div>
      <button class="call-btn" @click="initiateCall">拨打</button>
    </div>

    <div v-else class="call-view">
      <div class="video-container">
        <video
          v-if="isVideo && currentCall.remoteStream"
          ref="remoteVideo"
          autoplay
          :srcObject="currentCall.remoteStream"
        />
        <div v-if="isVideo && currentCall.localStream" class="local-video">
          <video ref="localVideo" autoplay muted :srcObject="currentCall.localStream" />
        </div>
        <div v-if="!isVideo" class="avatar-ring">
          <div class="avatar">{{ remoteName }}</div>
        </div>
      </div>

      <div class="call-info">
        <h3>{{ remoteName || '未知用户' }}</h3>
        <p class="status">{{ statusText }}</p>
        <p class="duration" v-if="callDuration">{{ formatDuration(callDuration) }}</p>
      </div>

      <div class="call-controls">
        <button
          v-if="isVideo"
          :class="{ active: isVideoOff }"
          @click="toggleVideo"
          title="关闭摄像头"
        >
          {{ isVideoOff ? '📷' : '📹' }}
        </button>
        <button :class="{ active: isMuted }" @click="toggleMute" title="静音">
          {{ isMuted ? '🔇' : '🔊' }}
        </button>
        <button class="end-call" @click="endCall" title="结束通话">📞</button>
      </div>

      <div v-if="currentCall.status === 'RINGING'" class="call-actions">
        <button @click="acceptCall">接听</button>
        <button class="reject" @click="rejectCall">拒绝</button>
      </div>
    </div>

    <div v-if="incomingCall" class="incoming-call-overlay">
      <div class="incoming-card">
        <h3>来电</h3>
        <p>{{ incomingCall.callerId }} 发起 {{ incomingCall.callType }} 通话</p>
        <div class="actions">
          <button @click="acceptIncoming">接听</button>
          <button class="reject" @click="rejectIncoming">拒绝</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import webrtcService from '../services/webrtc-signaling-service';
import { CallSession, SignalResponse } from '../types/webrtc-signal';

const targetUserId = ref<number>(0);
const callType = ref<'AUDIO' | 'VIDEO'>('AUDIO');
const currentCall = ref<CallSession | null>(null);
const incomingCall = ref<SignalResponse | null>(null);
const isMuted = ref(false);
const isVideoOff = ref(false);
const callDuration = ref(0);
const remoteName = ref('用户');
const localVideo = ref<HTMLVideoElement | null>(null);
const remoteVideo = ref<HTMLVideoElement | null>(null);
let durationTimer: any = null;

const isVideo = computed(() => currentCall.value?.callType === 'VIDEO');

const statusText = computed(() => {
  switch (currentCall.value?.status) {
    case 'INITIATING': return '正在呼叫...';
    case 'RINGING': return '响铃中...';
    case 'CONNECTING': return '连接中...';
    case 'CONNECTED': return '通话中';
    case 'REJECTED': return '对方已拒绝';
    case 'BUSY': return '对方忙线';
    case 'NO_ANSWER': return '无人接听';
    case 'CANCELLED': return '已取消';
    case 'ENDED': return '通话结束';
    default: return '等待连接...';
  }
});

function formatDuration(seconds: number): string {
  const m = Math.floor(seconds / 60).toString().padStart(2, '0');
  const s = (seconds % 60).toString().padStart(2, '0');
  return `${m}:${s}`;
}

async function initiateCall() {
  if (!targetUserId.value) return;
  const roomId = await webrtcService.initiateCall(targetUserId.value, callType.value);
  currentCall.value = webrtcService.getCurrentSession();
  startDurationTimer();
}

function acceptCall() {
  if (!currentCall.value) return;
  webrtcService.acceptCall(currentCall.value.roomId);
}

async function acceptIncoming() {
  if (!incomingCall.value) return;
  currentCall.value = {
    roomId: incomingCall.value.roomId,
    callerId: incomingCall.value.fromUserId,
    calleeId: incomingCall.value.toUserId,
    callType: (incomingCall.value.callType as 'AUDIO' | 'VIDEO') ?? 'AUDIO',
    status: 'CONNECTING',
    createdAt: new Date(),
  };
  incomingCall.value = null;
  await webrtcService.acceptCall(currentCall.value.roomId);
  startDurationTimer();
}

function rejectIncoming() {
  if (!incomingCall.value) return;
  webrtcService.rejectCall(incomingCall.value.roomId);
  incomingCall.value = null;
}

function endCall() {
  if (currentCall.value) {
    webrtcService.endCall();
  }
  currentCall.value = null;
  stopDurationTimer();
  callDuration.value = 0;
}

function toggleMute() {
  isMuted.value = !isMuted.value;
  webrtcService.toggleMute(isMuted.value);
}

function toggleVideo() {
  isVideoOff.value = !isVideoOff.value;
  webrtcService.toggleVideo(!isVideoOff.value);
}

function startDurationTimer() {
  stopDurationTimer();
  durationTimer = setInterval(() => {
    callDuration.value++;
  }, 1000);
}

function stopDurationTimer() {
  if (durationTimer) {
    clearInterval(durationTimer);
    durationTimer = null;
  }
}

onMounted(() => {
  webrtcService.on('call_invite', (data: SignalResponse) => {
    incomingCall.value = data;
  });
  webrtcService.on('remote_stream', (session: CallSession) => {
    currentCall.value = session;
  });
  webrtcService.on('status_change', (session: CallSession) => {
    currentCall.value = session;
    if (session.status === 'ENDED') {
      stopDurationTimer();
    }
  });
  webrtcService.on('call_ended', () => {
    currentCall.value = null;
    stopDurationTimer();
    callDuration.value = 0;
  });
});

onUnmounted(() => {
  webrtcService.off('call_invite', () => {});
  webrtcService.off('remote_stream', () => {});
  webrtcService.off('status_change', () => {});
  webrtcService.off('call_ended', () => {});
  stopDurationTimer();
});
</script>

<style scoped>
.call-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  background: #1a1a2e;
  color: white;
}
.call-dialer {
  text-align: center;
}
.call-dialer h2 { margin-bottom: 24px; }
.input-group {
  margin-bottom: 16px;
}
.input-group input {
  padding: 10px 16px;
  border-radius: 8px;
  border: 1px solid #444;
  background: #16213e;
  color: white;
  width: 200px;
}
.call-type-selector {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}
.call-type-selector button {
  padding: 8px 20px;
  border-radius: 20px;
  border: 1px solid #444;
  background: transparent;
  color: #aaa;
  cursor: pointer;
}
.call-type-selector button.active {
  background: #4f46e5;
  color: white;
  border-color: #4f46e5;
}
.call-btn {
  padding: 12px 48px;
  border-radius: 24px;
  background: #4f46e5;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 16px;
}
.call-view {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}
.video-container {
  position: relative;
  width: 100%;
  max-width: 640px;
  height: 360px;
  background: #0f0f1a;
  border-radius: 12px;
  overflow: hidden;
}
.video-container video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.local-video {
  position: absolute;
  bottom: 12px;
  right: 12px;
  width: 120px;
  height: 90px;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid white;
}
.local-video video { width: 100%; height: 100%; object-fit: cover; }
.avatar-ring {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}
.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: #4f46e5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
}
.call-info { text-align: center; margin: 16px 0; }
.call-info h3 { margin: 0; }
.status { color: #aaa; margin: 4px 0; }
.duration { color: #4f46e5; font-size: 18px; font-weight: bold; }
.call-controls {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}
.call-controls button {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  border: none;
  background: #2a2a4a;
  font-size: 24px;
  cursor: pointer;
}
.call-controls button.active { background: #e53e3e; }
.call-controls .end-call {
  background: #e53e3e;
  transform: rotate(135deg);
}
.call-actions, .actions {
  display: flex;
  gap: 16px;
  margin-top: 16px;
}
.call-actions button, .actions button {
  padding: 10px 32px;
  border-radius: 24px;
  border: none;
  cursor: pointer;
  background: #4f46e5;
  color: white;
}
.call-actions button.reject, .actions button.reject {
  background: #e53e3e;
}
.incoming-call-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.8);
  display: flex;
  align-items: center;
  justify-content: center;
}
.incoming-card {
  background: #1a1a2e;
  border-radius: 16px;
  padding: 32px;
  text-align: center;
  border: 1px solid #333;
}
.incoming-card h3 { margin-bottom: 8px; }
.incoming-card p { color: #aaa; margin-bottom: 20px; }
</style>
