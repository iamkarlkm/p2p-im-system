import React, { useState, useEffect, useRef, useCallback } from 'react';
import styled from 'styled-components';
import { MessageList } from './MessageList';
import { MessageInput } from './MessageInput';
import { ConversationHeader } from './ConversationHeader';
import { useIMClient } from '../hooks/useIMClient';
import { useMessageStore } from '../stores/messageStore';
import { Message, MessageType } from '../types/Message';
import { User } from '../types/User';

/**
 * ChatWindow Component - 功能#10: 桌面端聊天界面
 * 提供完整的桌面端聊天功能界面
 * 
 * @author IM Development Team
 * @since 1.0.0
 */

interface ChatWindowProps {
  conversationId: string;
  conversationName: string;
  conversationType: 'single' | 'group';
  currentUser: User;
  onBack?: () => void;
  onClose?: () => void;
}

const ChatWindowContainer = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
`;

const MessagesContainer = styled.div`
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #fafafa;
`;

const InputContainer = styled.div`
  padding: 12px 16px;
  background: #ffffff;
  border-top: 1px solid #e0e0e0;
`;

const LoadingOverlay = styled.div`
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
`;

export const ChatWindow: React.FC<ChatWindowProps> = ({
  conversationId,
  conversationName,
  conversationType,
  currentUser,
  onBack,
  onClose,
}) => {
  const [isLoading, setIsLoading] = useState(false);
  const [hasMoreMessages, setHasMoreMessages] = useState(true);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const messagesContainerRef = useRef<HTMLDivElement>(null);
  
  const { imClient, isConnected } = useIMClient();
  const { messages, addMessage, updateMessage, loadMessages } = useMessageStore();
  
  const conversationMessages = messages[conversationId] || [];

  // 滚动到底部
  const scrollToBottom = useCallback(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, []);

  // 加载历史消息
  const loadHistoryMessages = useCallback(async () => {
    if (isLoading || !hasMoreMessages) return;
    
    setIsLoading(true);
    try {
      const oldestMessage = conversationMessages[0];
      const beforeTimestamp = oldestMessage?.timestamp || Date.now();
      
      const historyMessages = await loadMessages(conversationId, beforeTimestamp, 20);
      
      if (historyMessages.length < 20) {
        setHasMoreMessages(false);
      }
    } catch (error) {
      console.error('Failed to load history messages:', error);
    } finally {
      setIsLoading(false);
    }
  }, [conversationId, conversationMessages, hasMoreMessages, isLoading, loadMessages]);

  // 发送消息
  const handleSendMessage = useCallback(async (
    content: string,
    messageType: MessageType = MessageType.TEXT
  ) => {
    if (!content.trim() || !isConnected) return;

    const newMessage: Message = {
      messageId: generateMessageId(),
      conversationId,
      senderId: currentUser.id,
      senderName: currentUser.name,
      senderAvatar: currentUser.avatar,
      type: messageType,
      content,
      timestamp: Date.now(),
      status: 'sending',
    };

    // 立即添加到本地
    addMessage(conversationId, newMessage);
    
    // 滚动到底部
    setTimeout(scrollToBottom, 100);

    try {
      // 发送到服务器
      await imClient.sendMessage({
        conversationId,
        type: messageType,
        content,
      });

      // 更新消息状态
      updateMessage(conversationId, newMessage.messageId, { status: 'sent' });
    } catch (error) {
      console.error('Failed to send message:', error);
      updateMessage(conversationId, newMessage.messageId, { status: 'failed' });
    }
  }, [conversationId, currentUser, imClient, isConnected, addMessage, updateMessage, scrollToBottom]);

  // 发送图片
  const handleSendImage = useCallback(async (file: File) => {
    if (!isConnected) return;

    const imageUrl = URL.createObjectURL(file);
    const newMessage: Message = {
      messageId: generateMessageId(),
      conversationId,
      senderId: currentUser.id,
      senderName: currentUser.name,
      senderAvatar: currentUser.avatar,
      type: MessageType.IMAGE,
      content: imageUrl,
      timestamp: Date.now(),
      status: 'sending',
    };

    addMessage(conversationId, newMessage);
    setTimeout(scrollToBottom, 100);

    try {
      // 上传图片
      const uploadedUrl = await imClient.uploadImage(file);
      
      // 发送图片消息
      await imClient.sendMessage({
        conversationId,
        type: MessageType.IMAGE,
        content: uploadedUrl,
      });

      updateMessage(conversationId, newMessage.messageId, { 
        status: 'sent',
        content: uploadedUrl 
      });
    } catch (error) {
      console.error('Failed to send image:', error);
      updateMessage(conversationId, newMessage.messageId, { status: 'failed' });
    }
  }, [conversationId, currentUser, imClient, isConnected, addMessage, updateMessage, scrollToBottom]);

  // 撤回消息
  const handleRecallMessage = useCallback(async (messageId: string) => {
    try {
      await imClient.recallMessage(messageId);
      updateMessage(conversationId, messageId, { isRecalled: true });
    } catch (error) {
      console.error('Failed to recall message:', error);
    }
  }, [conversationId, imClient, updateMessage]);

  // 删除消息
  const handleDeleteMessage = useCallback((messageId: string) => {
    // 本地删除，不同步到服务器
    const updatedMessages = conversationMessages.filter(m => m.messageId !== messageId);
    // 更新store
  }, [conversationMessages]);

  // 重发失败的消息
  const handleResendMessage = useCallback(async (messageId: string) => {
    const message = conversationMessages.find(m => m.messageId === messageId);
    if (!message) return;

    updateMessage(conversationId, messageId, { status: 'sending' });

    try {
      await imClient.sendMessage({
        conversationId,
        type: message.type,
        content: message.content,
      });

      updateMessage(conversationId, messageId, { status: 'sent' });
    } catch (error) {
      console.error('Failed to resend message:', error);
      updateMessage(conversationId, messageId, { status: 'failed' });
    }
  }, [conversationId, conversationMessages, imClient, updateMessage]);

  // 滚动监听（加载更多）
  useEffect(() => {
    const container = messagesContainerRef.current;
    if (!container) return;

    const handleScroll = () => {
      if (container.scrollTop === 0 && hasMoreMessages && !isLoading) {
        loadHistoryMessages();
      }
    };

    container.addEventListener('scroll', handleScroll);
    return () => container.removeEventListener('scroll', handleScroll);
  }, [hasMoreMessages, isLoading, loadHistoryMessages]);

  // 初始加载消息
  useEffect(() => {
    if (conversationMessages.length === 0) {
      loadHistoryMessages();
    }
  }, [conversationId]);

  // 新消息自动滚动
  useEffect(() => {
    const lastMessage = conversationMessages[conversationMessages.length - 1];
    if (lastMessage?.senderId === currentUser.id) {
      scrollToBottom();
    }
  }, [conversationMessages, currentUser.id, scrollToBottom]);

  return (
    <ChatWindowContainer>
      <ConversationHeader
        conversationName={conversationName}
        conversationType={conversationType}
        isOnline={isConnected}
        memberCount={conversationType === 'group' ? 0 : undefined}
        onBack={onBack}
        onClose={onClose}
      />

      <MessagesContainer ref={messagesContainerRef}>
        {isLoading && (
          <LoadingOverlay>
            <span>加载中...</span>
          </LoadingOverlay>
        )}

        <MessageList
          messages={conversationMessages}
          currentUserId={currentUser.id}
          onRecallMessage={handleRecallMessage}
          onDeleteMessage={handleDeleteMessage}
          onResendMessage={handleResendMessage}
        />

        <div ref={messagesEndRef} />
      </MessagesContainer>

      <InputContainer>
        <MessageInput
          onSendMessage={handleSendMessage}
          onSendImage={handleSendImage}
          disabled={!isConnected}
          placeholder={isConnected ? '输入消息...' : '连接中...'}
        />
      </InputContainer>
    </ChatWindowContainer>
  );
};

// 生成消息ID
function generateMessageId(): string {
  return `msg_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
}

export default ChatWindow;
