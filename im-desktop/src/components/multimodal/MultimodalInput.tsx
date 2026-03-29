import React, { useState, useRef, useCallback, useEffect } from 'react';
import { MessageAttachment, MessageType, ProcessingStatus } from '../types/multimodal';
import MediaPreview from './MediaPreview';

interface MultimodalInputProps {
  onSend: (content: string, attachments: MessageAttachment[]) => void;
  onTyping?: (isTyping: boolean) => void;
  disabled?: boolean;
  placeholder?: string;
  maxLength?: number;
  maxFileSize?: number;
  allowedTypes?: string[];
  processingStatus?: ProcessingStatus;
  className?: string;
}

/**
 * 多模态消息输入组件
 * 支持文本输入、文件拖拽、粘贴、录音
 */
export const MultimodalInput: React.FC<MultimodalInputProps> = ({
  onSend,
  onTyping,
  disabled = false,
  placeholder = '输入消息...',
  maxLength = 4000,
  maxFileSize = 50 * 1024 * 1024,
  allowedTypes = ['image/*', 'audio/*', 'video/*', 'application/*'],
  processingStatus,
  className = ''
}) => {
  const [content, setContent] = useState('');
  const [attachments, setAttachments] = useState<MessageAttachment[]>([]);
  const [isDragging, setIsDragging] = useState(false);
  const [isRecording, setIsRecording] = useState(false);
  const [showEmojiPicker, setShowEmojiPicker] = useState(false);
  const textareaRef = useRef<HTMLTextAreaElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  // 自动调整文本框高度
  useEffect(() => {
    if (textareaRef.current) {
      textareaRef.current.style.height = 'auto';
      textareaRef.current.style.height = `${Math.min(textareaRef.current.scrollHeight, 200)}px`;
    }
  }, [content]);

  // 处理文本变化
  const handleContentChange = useCallback((e: React.ChangeEvent<HTMLTextAreaElement>) => {
    const newContent = e.target.value;
    if (newContent.length <= maxLength) {
      setContent(newContent);
      onTyping?.(newContent.length > 0);
    }
  }, [maxLength, onTyping]);

  // 发送消息
  const handleSend = useCallback(() => {
    if (disabled) return;
    
    const trimmedContent = content.trim();
    if (!trimmedContent && attachments.length === 0) return;

    onSend(trimmedContent, attachments);
    setContent('');
    setAttachments([]);
    onTyping?.(false);

    // 重置文本框高度
    if (textareaRef.current) {
      textareaRef.current.style.height = 'auto';
    }
  }, [content, attachments, disabled, onSend, onTyping]);

  // 键盘快捷键
  const handleKeyDown = useCallback((e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  }, [handleSend]);

  // 处理文件选择
  const handleFileSelect = useCallback((files: FileList | null) => {
    if (!files) return;

    const newAttachments: MessageAttachment[] = [];
    const errors: string[] = [];

    Array.from(files).forEach(file => {
      // 检查文件大小
      if (file.size > maxFileSize) {
        errors.push(`${file.name}: 文件过大 (${(file.size / 1024 / 1024).toFixed(1)}MB)`);
        return;
      }

      // 检查文件类型
      const isAllowed = allowedTypes.some(type => {
        if (type.endsWith('/*')) {
          return file.type.startsWith(type.replace('/*', ''));
        }
        return file.type === type;
      });

      if (!isAllowed && allowedTypes.length > 0) {
        errors.push(`${file.name}: 不支持的文件类型`);
        return;
      }

      // 创建附件对象
      const attachment: MessageAttachment = {
        id: `att_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
        name: file.name,
        size: file.size,
        mimeType: file.type,
        url: URL.createObjectURL(file)
      };

      newAttachments.push(attachment);
    });

    if (errors.length > 0) {
      console.warn('文件添加失败:', errors);
    }

    setAttachments(prev => [...prev, ...newAttachments]);
  }, [maxFileSize, allowedTypes]);

  // 处理拖拽
  const handleDragOver = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(true);
  }, []);

  const handleDragLeave = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
  }, []);

  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
    handleFileSelect(e.dataTransfer.files);
  }, [handleFileSelect]);

  // 处理粘贴
  const handlePaste = useCallback((e: React.ClipboardEvent) => {
    const items = e.clipboardData.items;
    const files: File[] = [];

    for (let i = 0; i < items.length; i++) {
      if (items[i].kind === 'file') {
        const file = items[i].getAsFile();
        if (file) files.push(file);
      }
    }

    if (files.length > 0) {
      e.preventDefault();
      const dataTransfer = new DataTransfer();
      files.forEach(file => dataTransfer.items.add(file));
      handleFileSelect(dataTransfer.files);
    }
  }, [handleFileSelect]);

  // 移除附件
  const handleRemoveAttachment = useCallback((attachmentId: string) => {
    setAttachments(prev => {
      const attachment = prev.find(a => a.id === attachmentId);
      if (attachment?.url.startsWith('blob:')) {
        URL.revokeObjectURL(attachment.url);
      }
      return prev.filter(a => a.id !== attachmentId);
    });
  }, []);

  // 开始录音
  const startRecording = useCallback(() => {
    setIsRecording(true);
    // 实际实现中会调用录音API
    console.log('开始录音...');
  }, []);

  // 停止录音
  const stopRecording = useCallback(() => {
    setIsRecording(false);
    // 实际实现中会停止录音并生成音频文件
    console.log('停止录音');
  }, []);

  // 触发文件选择
  const triggerFileInput = useCallback(() => {
    fileInputRef.current?.click();
  }, []);

  // 插入表情
  const insertEmoji = useCallback((emoji: string) => {
    setContent(prev => prev + emoji);
    setShowEmojiPicker(false);
    textareaRef.current?.focus();
  }, []);

  // 常用表情
  const commonEmojis = ['😀', '😂', '🥰', '😎', '🤔', '👍', '❤️', '🎉', '🔥', '👏'];

  const canSend = !disabled && (content.trim().length > 0 || attachments.length > 0);

  return (
    <div 
      className={`multimodal-input ${className} ${isDragging ? 'dragging' : ''} ${disabled ? 'disabled' : ''}`}
      onDragOver={handleDragOver}
      onDragLeave={handleDragLeave}
      onDrop={handleDrop}
    >
      {/* 拖拽提示 */}
      {isDragging && (
        <div className="drag-overlay">
          <span>📎 释放以上传文件</span>
        </div>
      )}

      {/* 附件预览 */}
      <MediaPreview
        attachments={attachments}
        onRemove={handleRemoveAttachment}
        processingStatus={processingStatus}
      />

      {/* 输入区域 */}
      <div className="input-area">
        <div className="input-toolbar">
          <button 
            className="toolbar-btn"
            onClick={triggerFileInput}
            title="添加文件"
            disabled={disabled}
          >
            📎
          </button>
          
          <button 
            className={`toolbar-btn ${isRecording ? 'recording' : ''}`}
            onMouseDown={startRecording}
            onMouseUp={stopRecording}
            onMouseLeave={isRecording ? stopRecording : undefined}
            title="按住录音"
            disabled={disabled}
          >
            {isRecording ? '🔴' : '🎙️'}
          </button>

          <div className="emoji-picker-container">
            <button 
              className="toolbar-btn"
              onClick={() => setShowEmojiPicker(!showEmojiPicker)}
              title="表情"
              disabled={disabled}
            >
              😊
            </button>
            
            {showEmojiPicker && (
              <div className="emoji-picker">
                {commonEmojis.map(emoji => (
                  <button 
                    key={emoji}
                    className="emoji-btn"
                    onClick={() => insertEmoji(emoji)}
                  >
                    {emoji}
                  </button>
                ))}
              </div>
            )}
          </div>
        </div>

        <textarea
          ref={textareaRef}
          value={content}
          onChange={handleContentChange}
          onKeyDown={handleKeyDown}
          onPaste={handlePaste}
          placeholder={placeholder}
          disabled={disabled}
          rows={1}
        />

        <div className="input-actions">
          <span className={`char-count ${content.length > maxLength * 0.9 ? 'warning' : ''}`}>
            {content.length}/{maxLength}
          </span>
          
          <button 
            className={`send-btn ${canSend ? 'active' : ''}`}
            onClick={handleSend}
            disabled={!canSend}
          >
            {processingStatus?.stage === 'uploading' ? (
              <span className="spinner">⟳</span>
            ) : (
              '➤'
            )}
          </button>
        </div>
      </div>

      {/* 隐藏的文件输入 */}
      <input
        ref={fileInputRef}
        type="file"
        multiple
        accept={allowedTypes.join(',')}
        onChange={(e) => handleFileSelect(e.target.files)}
        style={{ display: 'none' }}
      />

      {/* 录音状态 */}
      {isRecording && (
        <div className="recording-indicator">
          <span className="recording-pulse" />
          <span>正在录音...</span>
          <span className="recording-hint">松开停止</span>
        </div>
      )}
    </div>
  );
};

export default MultimodalInput;
