import { useState, useEffect, useCallback } from 'react';
import { chatApi } from '../services/chatApi';

interface Message {
  id: string;
  content: string;
  senderId: string;
  timestamp: number;
}

interface UseChatReturn {
  messages: Message[];
  sendMessage: (content: string) => void;
  loading: boolean;
}

/**
 * 聊天Hook
 * 功能#10: 桌面端聊天界面
 */
export function useChat(conversationId: string): UseChatReturn {
  const [messages, setMessages] = useState<Message[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadMessages();
  }, [conversationId]);

  const loadMessages = async () => {
    setLoading(true);
    try {
      const data = await chatApi.getMessages(conversationId);
      setMessages(data);
    } finally {
      setLoading(false);
    }
  };

  const sendMessage = useCallback((content: string) => {
    const newMessage: Message = {
      id: Date.now().toString(),
      content,
      senderId: 'currentUser',
      timestamp: Date.now(),
    };
    setMessages(prev => [...prev, newMessage]);
    chatApi.sendMessage(conversationId, content);
  }, [conversationId]);

  return { messages, sendMessage, loading };
}
