import React, { useState, useCallback, useEffect, useRef } from 'react';
import { 
  AIConversation, 
  MultimodalMessage, 
  MessageAttachment, 
  AIAssistant,
  StreamChunk,
  MessageStatus 
} from '../types/multimodal';
import { AIAssistantConversationManager } from '../services/multimodal/AIAssistantConversationManager';
import MessageList from './StreamingMessage';
import MultimodalInput from './MultimodalInput';

interface AIConversationViewProps {
  conversation: AIConversation;
  assistant: AIAssistant;
  manager: AIAssistantConversationManager;
  className?: string;
}

/**
 * AI对话视图组件
 * 完整的AI对话界面，包含消息列表、输入框和工具栏
 */
export const AIConversationView: React.FC<AIConversationViewProps> = ({
  conversation,
  assistant,
  manager,
  className = ''
}) => {
  const [messages, setMessages] = useState<MultimodalMessage[]>(conversation.messages);
  const [streamingMessageId, setStreamingMessageId] = useState<string | null>(null);
  const [streamChunks, setStreamChunks] = useState<StreamChunk[]>([]);
  const [isGenerating, setIsGenerating] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showSettings, setShowSettings] = useState(false);
  const viewRef = useRef<HTMLDivElement>(null);

  // 监听消息更新
  useEffect(() => {
    const handleMessageUpdate = (msg: MultimodalMessage) => {
      setMessages(prev => {
        const index = prev.findIndex(m => m.id === msg.id);
        if (index >= 0) {
          const updated = [...prev];
          updated[index] = msg;
          return updated;
        }
        return [...prev, msg];
      });
    };

    const handleStreamChunk = (chunk: StreamChunk) => {
      setStreamChunks(prev => [...prev, chunk]);
      
      if (chunk.isComplete) {
        setIsGenerating(false);
        setStreamingMessageId(null);
        setStreamChunks([]);
      }
    };

    const handleMessageGenerating = (msg: MultimodalMessage) => {
      setStreamingMessageId(msg.id);
      setIsGenerating(true);
      setMessages(prev => [...prev, msg]);
    };

    manager.on('message:updated', handleMessageUpdate);
    manager.on('stream:chunk', handleStreamChunk);
    manager.on('message:generating', handleMessageGenerating);

    return () => {
      manager.off('message:updated', handleMessageUpdate);
      manager.off('stream:chunk', handleStreamChunk);
      manager.off('message:generating', handleMessageGenerating);
    };
  }, [manager]);

  // 发送消息
  const handleSendMessage = useCallback(async (content: string, attachments: MessageAttachment[]) => {
    if (!content.trim() && attachments.length === 0) return;

    setError(null);

    try {
      await manager.sendMessage(conversation.id, content, attachments);
    } catch (err) {
      setError(err instanceof Error ? err.message : '发送失败');
    }
  }, [conversation.id, manager]);

  // 停止生成
  const handleCancelGeneration = useCallback(() => {
    setIsGenerating(false);
    setStreamingMessageId(null);
    setStreamChunks([]);
    // 实际实现中会调用取消API
  }, []);

  // 清空对话
  const handleClearConversation = useCallback(() => {
    if (window.confirm('确定要清空当前对话吗？')) {
      manager.clearConversation(conversation.id);
      setMessages([]);
    }
  }, [conversation.id, manager]);

  // 导出对话
  const handleExportConversation = useCallback(() => {
    const data = manager.exportConversation(conversation.id);
    const blob = new Blob([data], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `conversation_${conversation.id}_${Date.now()}.json`;
    a.click();
    URL.revokeObjectURL(url);
  }, [conversation.id, manager]);

  // 重命名对话
  const [isEditingTitle, setIsEditingTitle] = useState(false);
  const [newTitle, setNewTitle] = useState(conversation.title || '');

  const handleRename = useCallback(() => {
    if (newTitle.trim() && newTitle !== conversation.title) {
      manager.renameConversation(conversation.id, newTitle.trim());
    }
    setIsEditingTitle(false);
  }, [conversation.id, conversation.title, manager, newTitle]);

  // 获取助手头像
  const getAssistantAvatar = () => {
    if (assistant.avatar) return assistant.avatar;
    if (assistant.capabilities.some(c => c.includes('IMAGE'))) return '🎨';
    if (assistant.capabilities.some(c => c.includes('CODE'))) return '👨‍💻';
    return '🤖';
  };

  // 渲染头部
  const renderHeader = () => (
    <div className="conversation-header">
      <div className="header-left">
        <span className="assistant-avatar">{getAssistantAvatar()}</span>
        <div className="header-info">
          {isEditingTitle ? (
            <input
              type="text"
              value={newTitle}
              onChange={(e) => setNewTitle(e.target.value)}
              onBlur={handleRename}
              onKeyDown={(e) => e.key === 'Enter' && handleRename()}
              autoFocus
              className="title-input"
            />
          ) : (
            <h3 
              className="conversation-title"
              onClick={() => setIsEditingTitle(true)}
              title="点击重命名"
            >
              {conversation.title || `与 ${assistant.name} 的对话`}
            </h3>
          )}
          <span className="assistant-name">{assistant.name}</span>
        </div>
      </div>

      <div className="header-actions">
        <button 
          className="action-btn"
          onClick={handleExportConversation}
          title="导出对话"
        >
          📥
        </button>
        <button 
          className="action-btn"
          onClick={handleClearConversation}
          title="清空对话"
        >
          🗑️
        </button>
        <button 
          className={`action-btn ${showSettings ? 'active' : ''}`}
          onClick={() => setShowSettings(!showSettings)}
          title="设置"
        >
          ⚙️
        </button>
      </div>
    </div>
  );

  // 渲染设置面板
  const renderSettingsPanel = () => {
    if (!showSettings) return null;

    return (
      <div className="settings-panel">
        <h4>对话设置</h4>
        
        <div className="setting-item">
          <label>模型提供商</label>
          <span>{assistant.modelProvider}</span>
        </div>

        <div className="setting-item">
          <label>模型名称</label>
          <span>{assistant.modelName}</span>
        </div>

        {assistant.settings?.temperature !== undefined && (
          <div className="setting-item">
            <label>温度 (Temperature)</label>
            <span>{assistant.settings.temperature}</span>
          </div>
        )}

        {assistant.settings?.maxTokens !== undefined && (
          <div className="setting-item">
            <label>最大令牌数</label>
            <span>{assistant.settings.maxTokens}</span>
          </div>
        )}

        <div className="setting-item">
          <label>消息数量</label>
          <span>{messages.length}</span>
        </div>

        <div className="setting-item">
          <label>创建时间</label>
          <span>{new Date(conversation.createdAt).toLocaleString('zh-CN')}</span>
        </div>
      </div>
    );
  };

  // 渲染空状态
  const renderEmptyState = () => (
    <div className="empty-conversation">
      <span className="empty-icon">{getAssistantAvatar()}</span>
      <h3>开始与 {assistant.name} 对话</h3>
      <p>{assistant.description || '我可以帮助您解答问题、生成内容或处理文件。'}</p>
      
      {assistant.capabilities.length > 0 && (
        <div className="capability-hints">
          <span>我可以：</span>
          <ul>
            {assistant.capabilities.slice(0, 4).map(cap => (
              <li key={cap}>{getCapabilityLabel(cap)}</li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );

  // 获取能力标签
  const getCapabilityLabel = (cap: string): string => {
    const labels: Record<string, string> = {
      'TEXT_GENERATION': '生成文本内容',
      'IMAGE_GENERATION': '生成图像',
      'IMAGE_ANALYSIS': '分析图像',
      'AUDIO_TRANSCRIPTION': '语音转文字',
      'AUDIO_SYNTHESIS': '文字转语音',
      'VIDEO_ANALYSIS': '分析视频',
      'CODE_GENERATION': '编写代码',
      'TRANSLATION': '翻译内容',
      'SUMMARIZATION': '总结内容'
    };
    return labels[cap] || cap;
  };

  return (
    <div ref={viewRef} className={`ai-conversation-view ${className}`}>
      {renderHeader()}
      
      {renderSettingsPanel()}

      {error && (
        <div className="error-banner">
          <span>⚠️ {error}</span>
          <button onClick={() => setError(null)}>✕</button>
        </div>
      )}

      <div className="conversation-body">
        {messages.length === 0 ? (
          renderEmptyState()
        ) : (
          <MessageList
            messages={messages}
            currentUserId="user"
            streamingMessageId={streamingMessageId || undefined}
            streamChunks={streamChunks}
          />
        )}
      </div>

      <div className="conversation-footer">
        <MultimodalInput
          onSend={handleSendMessage}
          disabled={isGenerating}
          placeholder={`发送消息给 ${assistant.name}...`}
        />
        
        {isGenerating && (
          <button className="cancel-generation-btn" onClick={handleCancelGeneration}>
            ⏹ 停止生成
          </button>
        )}
      </div>
    </div>
  );
};

export default AIConversationView;
