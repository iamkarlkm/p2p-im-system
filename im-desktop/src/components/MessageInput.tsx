import React, { useState, useRef, useCallback } from 'react';
import { observer } from 'mobx-react-lite';
import { useStore } from '../stores';
import { ChatService } from '../services/ChatService';

interface MessageInputProps {
  conversationId: string;
  placeholder?: string;
  disabled?: boolean;
  onSend?: () => void;
}

/**
 * 消息输入组件
 * 支持文本输入、表情选择、文件上传、快捷回复
 */
export const MessageInput: React.FC<MessageInputProps> = observer(({
  conversationId,
  placeholder = '输入消息...',
  disabled = false,
  onSend
}) => {
  const { chatStore, userStore } = useStore();
  const [text, setText] = useState('');
  const [isSending, setIsSending] = useState(false);
  const [showEmoji, setShowEmoji] = useState(false);
  const [showActions, setShowActions] = useState(false);
  const textareaRef = useRef<HTMLTextAreaElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);
  
  const chatService = ChatService.getInstance();
  
  // 表情列表
  const emojis = ['😀', '😂', '🤣', '😊', '😍', '🤔', '👍', '👎', '❤️', '🎉', '🔥', '👏'];
  
  /**
   * 发送消息
   */
  const handleSend = useCallback(async () => {
    if (!text.trim() || isSending || disabled) return;
    
    setIsSending(true);
    try {
      await chatService.sendMessage({
        conversationId,
        content: text.trim(),
        type: 'text',
        senderId: userStore.currentUser?.id
      });
      
      setText('');
      onSend?.();
      
      // 清空后聚焦
      textareaRef.current?.focus();
    } catch (error) {
      console.error('发送消息失败:', error);
      chatStore.showError('发送失败，请重试');
    } finally {
      setIsSending(false);
    }
  }, [text, conversationId, isSending, disabled, chatService, userStore.currentUser, onSend, chatStore]);
  
  /**
   * 处理键盘事件
   */
  const handleKeyDown = useCallback((e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  }, [handleSend]);
  
  /**
   * 插入表情
   */
  const insertEmoji = useCallback((emoji: string) => {
    const textarea = textareaRef.current;
    if (!textarea) return;
    
    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const newText = text.substring(0, start) + emoji + text.substring(end);
    
    setText(newText);
    setShowEmoji(false);
    
    // 恢复光标位置
    setTimeout(() => {
      textarea.selectionStart = textarea.selectionEnd = start + emoji.length;
      textarea.focus();
    }, 0);
  }, [text]);
  
  /**
   * 处理文件选择
   */
  const handleFileSelect = useCallback(async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    
    setIsSending(true);
    try {
      await chatService.sendFileMessage(conversationId, file);
      onSend?.();
    } catch (error) {
      console.error('发送文件失败:', error);
      chatStore.showError('文件发送失败');
    } finally {
      setIsSending(false);
      // 清空input以允许重复选择同一文件
      e.target.value = '';
    }
  }, [conversationId, chatService, chatStore, onSend]);
  
  /**
   * 触发文件选择
   */
  const triggerFileSelect = useCallback(() => {
    fileInputRef.current?.click();
  }, []);
  
  return (
    <div className="message-input-container">
      {/* 工具栏 */}
      <div className="message-input-toolbar">
        <button
          className={`toolbar-btn ${showEmoji ? 'active' : ''}`}
          onClick={() => setShowEmoji(!showEmoji)}
          title="表情"
        >
          😊
        </button>
        <button
          className="toolbar-btn"
          onClick={triggerFileSelect}
          title="文件"
        >
          📎
        </button>
        <button
          className={`toolbar-btn ${showActions ? 'active' : ''}`}
          onClick={() => setShowActions(!showActions)}
          title="更多"
        >
          ➕
        </button>
        
        <input
          ref={fileInputRef}
          type="file"
          style={{ display: 'none' }}
          onChange={handleFileSelect}
        />
      </div>
      
      {/* 表情选择面板 */}
      {showEmoji && (
        <div className="emoji-panel">
          {emojis.map((emoji, index) => (
            <button
              key={index}
              className="emoji-item"
              onClick={() => insertEmoji(emoji)}
            >
              {emoji}
            </button>
          ))}
        </div>
      )}
      
      {/* 快捷操作面板 */}
      {showActions && (
        <div className="actions-panel">
          <button className="action-item" onClick={triggerFileSelect}>
            <span className="action-icon">🖼️</span>
            <span className="action-text">图片</span>
          </button>
          <button className="action-item">
            <span className="action-icon">🎤</span>
            <span className="action-text">语音</span>
          </button>
          <button className="action-item">
            <span className="action-icon">📹</span>
            <span className="action-text">视频</span>
          </button>
          <button className="action-item">
            <span className="action-icon">📍</span>
            <span className="action-text">位置</span>
          </button>
        </div>
      )}
      
      {/* 输入区域 */}
      <div className="message-input-area">
        <textarea
          ref={textareaRef}
          className="message-textarea"
          value={text}
          onChange={(e) => setText(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder={placeholder}
          disabled={disabled || isSending}
          rows={1}
          style={{
            minHeight: '40px',
            maxHeight: '120px',
            resize: 'none'
          }}
        />
        
        <button
          className={`send-btn ${text.trim() ? 'active' : ''}`}
          onClick={handleSend}
          disabled={!text.trim() || isSending || disabled}
        >
          {isSending ? '发送中...' : '发送'}
        </button>
      </div>
      
      {/* 输入提示 */}
      <div className="input-hints">
        <span className="hint">Enter 发送</span>
        <span className="hint">Shift + Enter 换行</span>
      </div>
      
      <style>{`
        .message-input-container {
          border-top: 1px solid #e0e0e0;
          background: #fff;
          padding: 12px;
        }
        
        .message-input-toolbar {
          display: flex;
          gap: 8px;
          margin-bottom: 8px;
        }
        
        .toolbar-btn {
          padding: 6px 10px;
          border: none;
          background: transparent;
          cursor: pointer;
          border-radius: 4px;
          font-size: 18px;
          transition: background 0.2s;
        }
        
        .toolbar-btn:hover {
          background: #f0f0f0;
        }
        
        .toolbar-btn.active {
          background: #e3f2fd;
        }
        
        .emoji-panel {
          display: flex;
          flex-wrap: wrap;
          gap: 4px;
          padding: 8px;
          background: #f5f5f5;
          border-radius: 8px;
          margin-bottom: 8px;
          max-height: 120px;
          overflow-y: auto;
        }
        
        .emoji-item {
          padding: 4px 8px;
          border: none;
          background: transparent;
          cursor: pointer;
          font-size: 20px;
          border-radius: 4px;
          transition: background 0.2s;
        }
        
        .emoji-item:hover {
          background: #e0e0e0;
        }
        
        .actions-panel {
          display: flex;
          gap: 12px;
          padding: 12px;
          background: #f5f5f5;
          border-radius: 8px;
          margin-bottom: 8px;
        }
        
        .action-item {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 4px;
          padding: 12px;
          border: none;
          background: #fff;
          border-radius: 8px;
          cursor: pointer;
          min-width: 60px;
          transition: background 0.2s;
        }
        
        .action-item:hover {
          background: #e3f2fd;
        }
        
        .action-icon {
          font-size: 24px;
        }
        
        .action-text {
          font-size: 12px;
          color: #666;
        }
        
        .message-input-area {
          display: flex;
          gap: 8px;
          align-items: flex-end;
        }
        
        .message-textarea {
          flex: 1;
          padding: 10px 12px;
          border: 1px solid #ddd;
          border-radius: 8px;
          font-size: 14px;
          line-height: 1.5;
          outline: none;
          transition: border-color 0.2s;
        }
        
        .message-textarea:focus {
          border-color: #1976d2;
        }
        
        .message-textarea:disabled {
          background: #f5f5f5;
          cursor: not-allowed;
        }
        
        .send-btn {
          padding: 10px 24px;
          border: none;
          background: #ccc;
          color: #fff;
          border-radius: 8px;
          cursor: not-allowed;
          font-size: 14px;
          transition: all 0.2s;
        }
        
        .send-btn.active {
          background: #1976d2;
          cursor: pointer;
        }
        
        .send-btn.active:hover {
          background: #1565c0;
        }
        
        .send-btn:disabled {
          opacity: 0.6;
        }
        
        .input-hints {
          display: flex;
          gap: 16px;
          margin-top: 4px;
          padding-left: 4px;
        }
        
        .hint {
          font-size: 12px;
          color: #999;
        }
      `}</style>
    </div>
  );
});

export default MessageInput;
