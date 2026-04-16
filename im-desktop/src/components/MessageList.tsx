/**
 * 消息列表组件 - 功能#10 桌面端聊天界面
 * 时间: 2026-04-01 09:30
 */

import React, { useEffect, useRef, useCallback } from 'react';

export interface MessageItem {
  messageId: string;
  senderId: string;
  senderName: string;
  senderAvatar?: string;
  content: string;
  messageType: 'TEXT' | 'IMAGE' | 'FILE' | 'VOICE';
  timestamp: number;
  isSelf: boolean;
  status: 'sending' | 'sent' | 'delivered' | 'read' | 'failed';
}

interface MessageListProps {
  messages: MessageItem[];
  currentUserId: string;
  onLoadMore?: () => void;
  hasMore?: boolean;
  loading?: boolean;
}

const MessageList: React.FC<MessageListProps> = ({
  messages,
  currentUserId,
  onLoadMore,
  hasMore = false,
  loading = false,
}) => {
  const listRef = useRef<HTMLDivElement>(null);
  const scrollPositionRef = useRef<number>(0);
  const shouldScrollToBottomRef = useRef<boolean>(true);

  // 格式化时间
  const formatTime = useCallback((timestamp: number): string => {
    const date = new Date(timestamp);
    const now = new Date();
    const isToday = date.toDateString() === now.toDateString();
    
    if (isToday) {
      return date.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      });
    }
    
    const yesterday = new Date(now);
    yesterday.setDate(yesterday.getDate() - 1);
    if (date.toDateString() === yesterday.toDateString()) {
      return `昨天 ${date.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })}`;
    }
    
    return date.toLocaleString('zh-CN', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }, []);

  // 滚动到底部
  const scrollToBottom = useCallback(() => {
    if (listRef.current && shouldScrollToBottomRef.current) {
      listRef.current.scrollTop = listRef.current.scrollHeight;
    }
  }, []);

  // 处理滚动事件
  const handleScroll = useCallback(() => {
    if (!listRef.current) return;
    
    const { scrollTop, scrollHeight, clientHeight } = listRef.current;
    
    // 判断是否滚动到底部附近
    shouldScrollToBottomRef.current = scrollHeight - scrollTop - clientHeight < 100;
    
    // 判断是否滚动到顶部，加载更多
    if (scrollTop < 50 && hasMore && !loading && onLoadMore) {
      scrollPositionRef.current = scrollHeight - scrollTop;
      onLoadMore();
    }
  }, [hasMore, loading, onLoadMore]);

  // 消息变化时滚动
  useEffect(() => {
    scrollToBottom();
  }, [messages, scrollToBottom]);

  // 渲染消息内容
  const renderMessageContent = (message: MessageItem) => {
    switch (message.messageType) {
      case 'IMAGE':
        return (
          <div className="message-image">
            <img 
              src={message.content} 
              alt="图片" 
              style={{ maxWidth: '200px', maxHeight: '200px', borderRadius: '8px', cursor: 'pointer' }}
              onClick={() => window.open(message.content, '_blank')}
            />
          </div>
        );
      case 'FILE':
        return (
          <div className="message-file" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontSize: '24px' }}>📎</span>
            <a href={message.content} target="_blank" rel="noopener noreferrer">
              下载文件
            </a>
          </div>
        );
      case 'VOICE':
        return (
          <div className="message-voice" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontSize: '20px' }}>🎤</span>
            <span>语音消息</span>
          </div>
        );
      case 'TEXT':
      default:
        return <div className="message-text">{message.content}</div>;
    }
  };

  // 渲染状态指示器
  const renderStatus = (message: MessageItem) => {
    if (!message.isSelf) return null;
    
    const statusIcons: Record<string, string> = {
      sending: '⏳',
      sent: '✓',
      delivered: '✓✓',
      read: '✓✓',
      failed: '❌',
    };
    
    const statusColors: Record<string, string> = {
      sending: '#999',
      sent: '#999',
      delivered: '#999',
      read: '#1890ff',
      failed: '#ff4d4f',
    };

    return (
      <span 
        className="message-status"
        style={{ 
          fontSize: '12px', 
          color: statusColors[message.status],
          marginLeft: '4px',
        }}
        title={message.status}
      >
        {statusIcons[message.status] || ''}
      </span>
    );
  };

  const containerStyle: React.CSSProperties = {
    flex: 1,
    overflowY: 'auto',
    padding: '16px',
    backgroundColor: '#f5f5f5',
  };

  const messageItemStyle = (isSelf: boolean): React.CSSProperties => ({
    display: 'flex',
    flexDirection: isSelf ? 'row-reverse' : 'row',
    alignItems: 'flex-start',
    marginBottom: '16px',
    gap: '12px',
  });

  const avatarStyle: React.CSSProperties = {
    width: '40px',
    height: '40px',
    borderRadius: '50%',
    backgroundColor: '#1890ff',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    color: 'white',
    fontSize: '16px',
    fontWeight: 'bold',
    flexShrink: 0,
  };

  const contentStyle = (isSelf: boolean): React.CSSProperties => ({
    maxWidth: '60%',
    display: 'flex',
    flexDirection: 'column',
    alignItems: isSelf ? 'flex-end' : 'flex-start',
  });

  const bubbleStyle = (isSelf: boolean): React.CSSProperties => ({
    backgroundColor: isSelf ? '#95ec69' : '#ffffff',
    padding: '10px 14px',
    borderRadius: isSelf ? '12px 12px 4px 12px' : '12px 12px 12px 4px',
    boxShadow: '0 1px 2px rgba(0,0,0,0.1)',
    wordBreak: 'break-word',
  });

  const senderStyle: React.CSSProperties = {
    fontSize: '12px',
    color: '#666',
    marginBottom: '4px',
  };

  const timeStyle: React.CSSProperties = {
    fontSize: '11px',
    color: '#999',
    marginTop: '4px',
  };

  return (
    <div 
      ref={listRef}
      style={containerStyle}
      onScroll={handleScroll}
    >
      {/* 加载更多指示器 */}
      {loading && (
        <div style={{ textAlign: 'center', padding: '10px', color: '#999' }}>
          加载中...
        </div>
      )}
      
      {hasMore && !loading && (
        <div style={{ textAlign: 'center', padding: '10px', color: '#1890ff', cursor: 'pointer' }}>
          点击加载更多
        </div>
      )}

      {/* 消息列表 */}
      {messages.map((message) => (
        <div key={message.messageId} style={messageItemStyle(message.isSelf)}>
          {/* 头像 */}
          <div style={avatarStyle}>
            {message.senderAvatar ? (
              <img 
                src={message.senderAvatar} 
                alt={message.senderName}
                style={{ width: '100%', height: '100%', borderRadius: '50%' }}
              />
            ) : (
              message.senderName.charAt(0).toUpperCase()
            )}
          </div>

          {/* 消息内容 */}
          <div style={contentStyle(message.isSelf)}>
            {!message.isSelf && (
              <div style={senderStyle}>{message.senderName}</div>
            )}
            <div style={bubbleStyle(message.isSelf)}>
              {renderMessageContent(message)}
            </div>
            <div style={timeStyle}>
              {formatTime(message.timestamp)}
              {renderStatus(message)}
            </div>
          </div>
        </div>
      ))}

      {/* 空状态 */}
      {messages.length === 0 && (
        <div style={{ textAlign: 'center', padding: '40px', color: '#999' }}>
          暂无消息，开始聊天吧
        </div>
      )}
    </div>
  );
};

export default MessageList;
