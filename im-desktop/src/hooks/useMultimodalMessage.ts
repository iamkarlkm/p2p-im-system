/**
 * 多模态消息处理钩子
 */

import { useState, useCallback, useEffect, useRef } from 'react';
import { 
  MultimodalMessage, 
  MessageType, 
  MessageStatus,
  ProcessingStatus 
} from '../types/multimodal';
import { MultimodalMessageProcessor } from '../services/multimodal/MultimodalMessageProcessor';

interface UseMultimodalMessageOptions {
  onMessageReceived?: (message: MultimodalMessage) => void;
  onMessageSent?: (message: MultimodalMessage) => void;
  onProcessingStart?: (status: ProcessingStatus) => void;
  onProcessingComplete?: (status: ProcessingStatus) => void;
  onProcessingError?: (status: ProcessingStatus) => void;
}

/**
 * 多模态消息处理钩子
 */
export function useMultimodalMessage(options: UseMultimodalMessageOptions = {}) {
  const [messages, setMessages] = useState<MultimodalMessage[]>([]);
  const [processingStatuses, setProcessingStatuses] = useState<Map<string, ProcessingStatus>>(new Map());
  const processorRef = useRef<MultimodalMessageProcessor | null>(null);

  // 初始化处理器
  useEffect(() => {
    const processor = new MultimodalMessageProcessor();
    processorRef.current = processor;

    // 绑定事件
    processor.on('message:received', (msg: MultimodalMessage) => {
      setMessages(prev => [...prev, msg]);
      options.onMessageReceived?.(msg);
    });

    processor.on('message:sent', (msg: MultimodalMessage) => {
      setMessages(prev => [...prev, msg]);
      options.onMessageSent?.(msg);
    });

    processor.on('processing:start', (status: ProcessingStatus) => {
      setProcessingStatuses(prev => new Map(prev.set(status.messageId, status)));
      options.onProcessingStart?.(status);
    });

    processor.on('processing:complete', (status: ProcessingStatus) => {
      setProcessingStatuses(prev => {
        const next = new Map(prev);
        next.set(status.messageId, status);
        return next;
      });
      options.onProcessingComplete?.(status);
    });

    processor.on('processing:error', (status: ProcessingStatus) => {
      setProcessingStatuses(prev => {
        const next = new Map(prev);
        next.set(status.messageId, status);
        return next;
      });
      options.onProcessingError?.(status);
    });

    return () => {
      processor.destroy();
      processorRef.current = null;
    };
  }, []);

  // 接收消息
  const receiveMessage = useCallback((message: MultimodalMessage) => {
    processorRef.current?.receiveMessage(message);
  }, []);

  // 发送消息
  const sendMessage = useCallback((message: MultimodalMessage) => {
    processorRef.current?.sendMessage(message);
  }, []);

  // 获取处理状态
  const getProcessingStatus = useCallback((messageId: string): ProcessingStatus | undefined => {
    return processingStatuses.get(messageId);
  }, [processingStatuses]);

  return {
    messages,
    processingStatuses,
    receiveMessage,
    sendMessage,
    getProcessingStatus
  };
}

/**
 * AI对话钩子
 */
interface UseAIConversationOptions {
  assistantId: string;
  onMessageReceived?: (message: MultimodalMessage) => void;
  onStreamChunk?: (chunk: string) => void;
  onError?: (error: string) => void;
}

export function useAIConversation(options: UseAIConversationOptions) {
  const [messages, setMessages] = useState<MultimodalMessage[]>([]);
  const [isGenerating, setIsGenerating] = useState(false);
  const [streamingContent, setStreamingContent] = useState('');
  const wsRef = useRef<WebSocket | null>(null);

  // 发送消息
  const sendMessage = useCallback(async (content: string, attachments?: any[]) => {
    if (!content.trim() && (!attachments || attachments.length === 0)) return;

    const message: MultimodalMessage = {
      id: `msg_${Date.now()}`,
      conversationId: options.assistantId,
      senderId: 'user',
      type: attachments?.length ? MessageType.MIXED : MessageType.TEXT,
      content,
      attachments,
      status: MessageStatus.SENDING,
      timestamp: Date.now()
    };

    setMessages(prev => [...prev, message]);
    setIsGenerating(true);
    setStreamingContent('');

    // 模拟流式响应
    const responses = [
      '收到您的消息，',
      '我正在思考中...',
      '根据您的问题，',
      '我可以为您提供帮助。'
    ];

    let fullContent = '';
    for (const chunk of responses) {
      await new Promise(r => setTimeout(r, 300));
      fullContent += chunk;
      setStreamingContent(fullContent);
      options.onStreamChunk?.(chunk);
    }

    const aiMessage: MultimodalMessage = {
      id: `ai_${Date.now()}`,
      conversationId: options.assistantId,
      senderId: options.assistantId,
      senderName: 'AI助手',
      type: MessageType.TEXT,
      content: fullContent,
      status: MessageStatus.SENT,
      timestamp: Date.now()
    };

    setMessages(prev => [...prev, aiMessage]);
    setIsGenerating(false);
    setStreamingContent('');
    options.onMessageReceived?.(aiMessage);

  }, [options]);

  // 停止生成
  const stopGeneration = useCallback(() => {
    setIsGenerating(false);
  }, []);

  return {
    messages,
    isGenerating,
    streamingContent,
    sendMessage,
    stopGeneration
  };
}

/**
 * 文件上传钩子
 */
interface UseFileUploadOptions {
  maxFileSize?: number;
  allowedTypes?: string[];
  onError?: (error: string) => void;
}

export function useFileUpload(options: UseFileUploadOptions = {}) {
  const [uploading, setUploading] = useState(false);
  const [progress, setProgress] = useState(0);

  const uploadFile = useCallback(async (file: File): Promise<string> => {
    const { maxFileSize = 50 * 1024 * 1024, allowedTypes = [] } = options;

    // 检查文件大小
    if (file.size > maxFileSize) {
      throw new Error(`文件过大: ${(file.size / 1024 / 1024).toFixed(1)}MB`);
    }

    // 检查文件类型
    if (allowedTypes.length > 0) {
      const isAllowed = allowedTypes.some(type => {
        if (type.endsWith('/*')) {
          return file.type.startsWith(type.replace('/*', ''));
        }
        return file.type === type;
      });

      if (!isAllowed) {
        throw new Error('不支持的文件类型');
      }
    }

    setUploading(true);
    setProgress(0);

    try {
      // 模拟上传
      for (let i = 0; i <= 100; i += 10) {
        await new Promise(r => setTimeout(r, 100));
        setProgress(i);
      }

      // 返回模拟URL
      return URL.createObjectURL(file);
    } catch (error) {
      options.onError?.(error instanceof Error ? error.message : '上传失败');
      throw error;
    } finally {
      setUploading(false);
      setProgress(0);
    }
  }, [options]);

  return {
    uploading,
    progress,
    uploadFile
  };
}
