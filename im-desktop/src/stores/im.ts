import { ref, computed } from 'vue';
import { defineStore } from 'pinia';
import { Message, Conversation, ConnectionStatus } from '@/types/im';
import { IMService } from '@/services/IMService';

export const useIMStore = defineStore('im', () => {
  // State
  const connectionStatus = ref<ConnectionStatus>({ status: 'disconnected' });
  const conversations = ref<Conversation[]>([]);
  const currentConversationId = ref<string | null>(null);
  const messages = ref<Map<string, Message[]>>(new Map());
  const unreadTotal = computed(() => {
    return conversations.value.reduce((sum, conv) => sum + conv.unreadCount, 0);
  });

  const imService = new IMService();

  // Getters
  const currentConversation = computed(() => {
    return conversations.value.find(c => c.id === currentConversationId.value);
  });

  const currentMessages = computed(() => {
    if (!currentConversationId.value) return [];
    return messages.value.get(currentConversationId.value) || [];
  });

  // Actions
  async function connect(token: string, userId: string) {
    connectionStatus.value = { status: 'connecting' };
    try {
      await imService.connect(token, userId);
      connectionStatus.value = { status: 'connected' };
      setupListeners();
    } catch (error) {
      connectionStatus.value = { 
        status: 'error', 
        message: error instanceof Error ? error.message : 'Connection failed' 
      };
    }
  }

  function setupListeners() {
    imService.onMessage((message: Message) => {
      const conversationId = message.type === 'group' ? message.to : 
        (message.from === imService.userId ? message.to : message.from);
      
      if (!messages.value.has(conversationId)) {
        messages.value.set(conversationId, []);
      }
      messages.value.get(conversationId)!.push(message);

      // Update conversation last message
      const conversation = conversations.value.find(c => c.targetId === conversationId);
      if (conversation) {
        conversation.lastMessage = getMessagePreview(message);
        conversation.lastMessageTime = message.timestamp;
        if (conversationId !== currentConversationId.value) {
          conversation.unreadCount++;
        }
      }
    });

    imService.onStatusChange((status: ConnectionStatus) => {
      connectionStatus.value = status;
    });
  }

  function getMessagePreview(message: Message): string {
    switch (message.contentType) {
      case 'image': return '[图片]';
      case 'voice': return '[语音]';
      case 'video': return '[视频]';
      case 'file': return '[文件]';
      default: return message.content;
    }
  }

  async function sendMessage(content: string, contentType: string = 'text') {
    if (!currentConversationId.value) return;
    
    const conversation = currentConversation.value;
    if (!conversation) return;

    const message: Message = {
      id: generateMessageId(),
      from: imService.userId!,
      to: conversation.targetId,
      type: conversation.type,
      contentType: contentType as any,
      content,
      timestamp: Date.now(),
    };

    await imService.sendMessage(message);
    
    if (!messages.value.has(currentConversationId.value)) {
      messages.value.set(currentConversationId.value, []);
    }
    messages.value.get(currentConversationId.value)!.push(message);
  }

  function selectConversation(conversationId: string) {
    currentConversationId.value = conversationId;
    const conversation = conversations.value.find(c => c.id === conversationId);
    if (conversation) {
      conversation.unreadCount = 0;
    }
  }

  function disconnect() {
    imService.disconnect();
    connectionStatus.value = { status: 'disconnected' };
  }

  function generateMessageId(): string {
    return `${imService.userId}_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }

  return {
    connectionStatus,
    conversations,
    currentConversationId,
    currentConversation,
    currentMessages,
    unreadTotal,
    connect,
    sendMessage,
    selectConversation,
    disconnect,
  };
});
