/**
 * 用户状态WebSocket钩子
 * 处理在线状态的实时更新
 */

import { ref, onMounted, onUnmounted } from 'vue';
import { useUserProfileStore } from '../stores/user-profile-store';
import { OnlineStatus } from '../types/user-profile';

const WS_URL = 'ws://localhost:8080/ws/profile';
const PING_INTERVAL = 30000;

export function useProfileWebSocket() {
  const store = useUserProfileStore();
  const isConnected = ref(false);
  let ws: WebSocket | null = null;
  let pingTimer: ReturnType<typeof setInterval> | null = null;
  let reconnectTimer: ReturnType<typeof setInterval> | null = null;

  function connect(userId: number) {
    if (ws && ws.readyState === WebSocket.OPEN) return;

    try {
      ws = new WebSocket(`${WS_URL}?userId=${userId}`);

      ws.onopen = () => {
        isConnected.value = true;
        console.log('[ProfileWS] Connected');
        startPing();
      };

      ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          handleMessage(data);
        } catch (e) {
          console.error('[ProfileWS] Parse error:', e);
        }
      };

      ws.onclose = () => {
        isConnected.value = false;
        stopPing();
        console.log('[ProfileWS] Disconnected, reconnecting...');
        scheduleReconnect(userId);
      };

      ws.onerror = (error) => {
        console.error('[ProfileWS] Error:', error);
        ws?.close();
      };
    } catch (e) {
      console.error('[ProfileWS] Connection failed:', e);
      scheduleReconnect(userId);
    }
  }

  function disconnect() {
    stopPing();
    clearReconnect();
    if (ws) {
      ws.close();
      ws = null;
    }
    isConnected.value = false;
  }

  function handleMessage(data: any) {
    switch (data.type) {
      case 'STATUS_CHANGED':
      case 'USER_STATUS_CHANGED':
        store.handleStatusChange(
          data.userId,
          data.status as OnlineStatus,
          data.statusText
        );
        break;
      case 'PONG':
        // 心跳响应
        break;
    }
  }

  function sendStatusUpdate(status: OnlineStatus, statusText?: string) {
    if (ws && ws.readyState === WebSocket.OPEN) {
      ws.send(JSON.stringify({
        type: 'STATUS_UPDATE',
        status,
        statusText,
      }));
    }
  }

  function startPing() {
    stopPing();
    pingTimer = setInterval(() => {
      if (ws && ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify({ type: 'PING' }));
      }
    }, PING_INTERVAL);
  }

  function stopPing() {
    if (pingTimer) {
      clearInterval(pingTimer);
      pingTimer = null;
    }
  }

  function scheduleReconnect(userId: number) {
    clearReconnect();
    reconnectTimer = setTimeout(() => {
      connect(userId);
    }, 3000);
  }

  function clearReconnect() {
    if (reconnectTimer) {
      clearTimeout(reconnectTimer);
      reconnectTimer = null;
    }
  }

  onMounted(() => {
    // 连接将在组件中调用
  });

  onUnmounted(() => {
    disconnect();
  });

  return {
    isConnected,
    connect,
    disconnect,
    sendStatusUpdate,
  };
}
