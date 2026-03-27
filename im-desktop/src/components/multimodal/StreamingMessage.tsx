import React, { useEffect, useRef, useState, useCallback } from 'react';
import { MultimodalMessage, MessageType, MessageStatus, StreamChunk } from '../types/multimodal';
import MediaPreview from './MediaPreview';

interface StreamingMessageProps {
  message: MultimodalMessage;
  isStreaming: boolean;
  streamChunks: StreamChunk[];
  onCancel?: () => void;
  className?: string;
}

interface MessageBubbleProps {
  message: MultimodalMessage;
  showAvatar?: boolean;
  className?: string;
}

/**
 * 消息气泡组件
 */
export const MessageBubble: React.FC<MessageBubbleProps> = ({
  message,
  showAvatar = true,
  className = ''
}) => {
  const isUser = message.senderId === 'user';
  const [imageLoaded, setImageLoaded] = useState<Record<string, boolean>>({});

  // 格式化时间
  const formatTime = (timestamp: number): string => {
    return new Date(timestamp).toLocaleTimeString('zh-CN', {
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  // 渲染消息内容
  const renderContent = () => {
    switch (message.type) {
      case MessageType.IMAGE:
        return renderImageContent();
      case MessageType.AUDIO:
        return renderAudioContent();
      case MessageType.VIDEO:
        return renderVideoContent();
      case MessageType.FILE:
        return renderFileContent();
      default:
        return renderTextContent();
    }
  };

  // 渲染文本内容
  const renderTextContent = () => {
    if (!message.content && !message.renderedContent) {
      return <span className="empty-content">空消息</span>;
    }

    return (
      <div 
        className="text-content"
        dangerouslySetInnerHTML={{ 
          __html: message.renderedContent || message.content 
        }}
      />
    );
  };

  // 渲染图像内容
  const renderImageContent = () => {
    return (
      <div className="image-content">
        {message.attachments?.map(att => (
          <div key={att.id} className="image-wrapper">
            {!imageLoaded[att.id] && (
              <div className="image-loading">
                <span className="spinner">⟳</span>
              </div>
            )}
            <img
              src={att.url}
              alt={att.name}
              onLoad={() => setImageLoaded(prev => ({ ...prev, [att.id]: true }))}
              onClick={() => window.open(att.url, '_blank')}
              style={{ opacity: imageLoaded[att.id] ? 1 : 0 }}
            />
          </div>
        ))}
        {message.content && <p className="image-caption">{message.content}</p>}
      </div>
    );
  };

  // 渲染音频内容
  const renderAudioContent = () => {
    return (
      <div className="audio-content">
        {message.attachments?.map(att => (
          <audio key={att.id} controls src={att.url}>
            您的浏览器不支持音频播放
          </audio>
        ))}
        {message.content && <p className="audio-transcript">{message.content}</p>}
      </div>
    );
  };

  // 渲染视频内容
  const renderVideoContent = () => {
    return (
      <div className="video-content">
        {message.attachments?.map(att => (
          <video key={att.id} controls src={att.url} poster={att.thumbnail}>
            您的浏览器不支持视频播放
          </video>
        ))}
      </div>
    );
  };

  // 渲染文件内容
  const renderFileContent = () => {
    return (
      <div className="file-content">
        {message.attachments?.map(att => {
          const icon = getFileIcon(att.mimeType);
          return (
            <a 
              key={att.id}
              href={att.url}
              download={att.name}
              className="file-item"
            >
              <span className="file-icon">{icon}</span>
              <div className="file-info">
                <span className="file-name">{att.name}</span>
                <span className="file-size">{formatFileSize(att.size)}</span>
              </div>
              <span className="download-icon">⬇️</span>
            </a>
          );
        })}
      </div>
    );
  };

  // 获取文件图标
  const getFileIcon = (mimeType: string): string => {
    if (mimeType.startsWith('image/')) return '🖼️';
    if (mimeType.startsWith('audio/')) return '🎵';
    if (mimeType.startsWith('video/')) return '🎬';
    if (mimeType.includes('pdf')) return '📄';
    if (mimeType.includes('doc')) return '📝';
    if (mimeType.includes('xls')) return '📊';
    if (mimeType.includes('zip')) return '📦';
    return '📎';
  };

  // 格式化文件大小
  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  // 渲染状态指示器
  const renderStatus = () => {
    if (!isUser) return null;

    switch (message.status) {
      case MessageStatus.SENDING:
        return <span className="status-icon sending">⟳</span>;
      case MessageStatus.SENT:
        return <span className="status-icon sent">✓</span>;
      case MessageStatus.DELIVERED:
        return <span className="status-icon delivered">✓✓</span>;
      case MessageStatus.READ:
        return <span className="status-icon read">✓✓</span>;
      case MessageStatus.FAILED:
        return <span className="status-icon failed">⚠️</span>;
      default:
        return null;
    }
  };

  return (
    <div className={`message-bubble ${isUser ? 'user' : 'ai'} ${className}`}>
      {showAvatar && !isUser && (
        <div className="message-avatar">
          <img 
            src={message.senderAvatar || '/default-avatar.png'} 
            alt={message.senderName}
          />
        </div>
      )}

      <div className="message-body">
        {showAvatar && !isUser && message.senderName && (
          <span className="sender-name">{message.senderName}</span>
        )}

        <div className="message-content-wrapper">
          <div className={`message-content ${message.type.toLowerCase()}`}>
            {renderContent()}
          </div>

          <div className="message-meta">
            <span className="message-time">{formatTime(message.timestamp)}</span>
            {renderStatus()}
          </div>
        </div>
      </div>
    </div>
  );
};

/**
 * 流式消息组件
 * 显示AI正在生成的流式响应
 */
export const StreamingMessage: React.FC<StreamingMessageProps> = ({
  message,
  isStreaming,
  streamChunks,
  onCancel,
  className = ''
}) => {
  const contentRef = useRef<HTMLDivElement>(null);
  const [displayedContent, setDisplayedContent] = useState('');
  const [cursorVisible, setCursorVisible] = useState(true);

  // 光标闪烁效果
  useEffect(() => {
    if (!isStreaming) return;
    
    const interval = setInterval(() => {
      setCursorVisible(v => !v);
    }, 530);

    return () => clearInterval(interval);
  }, [isStreaming]);

  // 更新显示内容
  useEffect(() => {
    if (streamChunks.length === 0) return;

    const fullContent = streamChunks.map(c => c.content).join('');
    setDisplayedContent(fullContent);

    // 自动滚动到底部
    if (contentRef.current) {
      contentRef.current.scrollTop = contentRef.current.scrollHeight;
    }
  }, [streamChunks]);

  // 格式化内容（高亮代码块等）
  const formatContent = useCallback((content: string): string => {
    // 简单的代码块检测和格式化
    return content
      .replace(/```(\w+)?\n([\s\S]*?)```/g, '<pre class="code-block"><code>$2</code></pre>')
      .replace(/`([^`]+)`/g, '<code class="inline-code">$1</code>')
      .replace(/\n/g, '<br/>');
  }, []);

  return (
    <div className={`streaming-message ${className}`}>
      <MessageBubble message={message} showAvatar={true} />

      {isStreaming && (
        <div className="streaming-indicator">
          <div ref={contentRef} className="streaming-content">
            <div 
              className="content-text"
              dangerouslySetInnerHTML={{ 
                __html: formatContent(displayedContent) + (cursorVisible ? '<span class="cursor">▋</span>' : '')
              }}
            />
          </div>

          <div className="streaming-actions">
            <span className="typing-indicator">
              <span className="dot" />
              <span className="dot" />
              <span className="dot" />
            </span>
            
            {onCancel && (
              <button className="cancel-btn" onClick={onCancel}>
                停止生成
              </button>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

/**
 * 消息列表组件
 */
interface MessageListProps {
  messages: MultimodalMessage[];
  currentUserId: string;
  streamingMessageId?: string;
  streamChunks?: StreamChunk[];
  onLoadMore?: () => void;
  hasMore?: boolean;
  className?: string;
}

export const MessageList: React.FC<MessageListProps> = ({
  messages,
  currentUserId,
  streamingMessageId,
  streamChunks = [],
  onLoadMore,
  hasMore = false,
  className = ''
}) => {
  const listRef = useRef<HTMLDivElement>(null);
  const [autoScroll, setAutoScroll] = useState(true);

  // 滚动到底部
  const scrollToBottom = useCallback(() => {
    if (listRef.current && autoScroll) {
      listRef.current.scrollTop = listRef.current.scrollHeight;
    }
  }, [autoScroll]);

  // 监听消息变化自动滚动
  useEffect(() => {
    scrollToBottom();
  }, [messages.length, streamChunks.length, scrollToBottom]);

  // 处理滚动事件
  const handleScroll = useCallback(() => {
    if (!listRef.current) return;

    const { scrollTop, scrollHeight, clientHeight } = listRef.current;
    const isNearBottom = scrollHeight - scrollTop - clientHeight < 100;
    setAutoScroll(isNearBottom);

    // 加载更多历史消息
    if (scrollTop === 0 && hasMore && onLoadMore) {
      onLoadMore();
    }
  }, [hasMore, onLoadMore]);

  // 渲染消息
  const renderMessages = () => {
    return messages.map((message, index) => {
      const isStreaming = message.id === streamingMessageId;
      const showAvatar = index === 0 || messages[index - 1].senderId !== message.senderId;

      if (isStreaming) {
        return (
          <StreamingMessage
            key={message.id}
            message={message}
            isStreaming={true}
            streamChunks={streamChunks}
          />
        );
      }

      return (
        <MessageBubble
          key={message.id}
          message={message}
          showAvatar={showAvatar}
        />
      );
    });
  };

  return (
    <div 
      ref={listRef}
      className={`message-list ${className}`}
      onScroll={handleScroll}
    >
      {hasMore && (
        <div className="load-more">
          <button onClick={onLoadMore}>加载更多</button>
        </div>
      )}
      
      {renderMessages()}
      
      {!autoScroll && (
        <button 
          className="scroll-to-bottom"
          onClick={() => {
            setAutoScroll(true);
            scrollToBottom();
          }}
        >
          ↓ 新消息
        </button>
      )}
    </div>
  );
};

export default StreamingMessage;
