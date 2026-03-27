import React, { useState, useCallback, useMemo } from 'react';
import { AIAssistant, AICapability } from '../types/multimodal';

interface AIAssistantSelectorProps {
  assistants: AIAssistant[];
  selectedId?: string;
  onSelect: (assistant: AIAssistant) => void;
  onCreateNew?: () => void;
  filterByCapability?: AICapability;
  className?: string;
}

interface CapabilityBadgeProps {
  capability: AICapability;
}

/**
 * 能力标签组件
 */
const CapabilityBadge: React.FC<CapabilityBadgeProps> = ({ capability }) => {
  const getCapabilityInfo = (cap: AICapability): { label: string; color: string } => {
    switch (cap) {
      case AICapability.TEXT_GENERATION:
        return { label: '文本', color: '#4CAF50' };
      case AICapability.IMAGE_GENERATION:
        return { label: '生图', color: '#9C27B0' };
      case AICapability.IMAGE_ANALYSIS:
        return { label: '识图', color: '#FF9800' };
      case AICapability.AUDIO_TRANSCRIPTION:
        return { label: '转录', color: '#2196F3' };
      case AICapability.AUDIO_SYNTHESIS:
        return { label: '语音', color: '#00BCD4' };
      case AICapability.VIDEO_ANALYSIS:
        return { label: '视频', color: '#E91E63' };
      case AICapability.CODE_GENERATION:
        return { label: '代码', color: '#607D8B' };
      case AICapability.TRANSLATION:
        return { label: '翻译', color: '#795548' };
      case AICapability.SUMMARIZATION:
        return { label: '摘要', color: '#3F51B5' };
      default:
        return { label: 'AI', color: '#757575' };
    }
  };

  const info = getCapabilityInfo(capability);

  return (
    <span 
      className="capability-badge"
      style={{ backgroundColor: `${info.color}20`, color: info.color }}
    >
      {info.label}
    </span>
  );
};

/**
 * AI助手选择器组件
 * 支持按能力筛选、搜索、排序
 */
export const AIAssistantSelector: React.FC<AIAssistantSelectorProps> = ({
  assistants,
  selectedId,
  onSelect,
  onCreateNew,
  filterByCapability,
  className = ''
}) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [sortBy, setSortBy] = useState<'name' | 'recent' | 'popular'>('recent');
  const [showInactive, setShowInactive] = useState(false);

  // 过滤和排序助手
  const filteredAssistants = useMemo(() => {
    let result = [...assistants];

    // 按能力筛选
    if (filterByCapability) {
      result = result.filter(a => 
        a.capabilities.includes(filterByCapability)
      );
    }

    // 按状态筛选
    if (!showInactive) {
      result = result.filter(a => a.isActive);
    }

    // 搜索过滤
    if (searchQuery.trim()) {
      const query = searchQuery.toLowerCase();
      result = result.filter(a => 
        a.name.toLowerCase().includes(query) ||
        a.description?.toLowerCase().includes(query) ||
        a.personality?.toLowerCase().includes(query)
      );
    }

    // 排序
    result.sort((a, b) => {
      switch (sortBy) {
        case 'name':
          return a.name.localeCompare(b.name);
        case 'recent':
          return b.updatedAt - a.updatedAt;
        case 'popular':
          return (b.capabilities.length || 0) - (a.capabilities.length || 0);
        default:
          return 0;
      }
    });

    return result;
  }, [assistants, filterByCapability, showInactive, searchQuery, sortBy]);

  // 按类别分组
  const groupedAssistants = useMemo(() => {
    const groups: Record<string, AIAssistant[]> = {
      '常用': [],
      '图像': [],
      '音频': [],
      '视频': [],
      '其他': []
    };

    filteredAssistants.forEach(assistant => {
      const caps = assistant.capabilities;
      
      if (caps.includes(AICapability.IMAGE_GENERATION) || 
          caps.includes(AICapability.IMAGE_ANALYSIS)) {
        groups['图像'].push(assistant);
      } else if (caps.includes(AICapability.AUDIO_TRANSCRIPTION) || 
                 caps.includes(AICapability.AUDIO_SYNTHESIS)) {
        groups['音频'].push(assistant);
      } else if (caps.includes(AICapability.VIDEO_ANALYSIS)) {
        groups['视频'].push(assistant);
      } else if (caps.includes(AICapability.TEXT_GENERATION) ||
                 caps.includes(AICapability.CODE_GENERATION)) {
        groups['常用'].push(assistant);
      } else {
        groups['其他'].push(assistant);
      }
    });

    return groups;
  }, [filteredAssistants]);

  // 获取助手头像
  const getAvatar = useCallback((assistant: AIAssistant): string => {
    if (assistant.avatar) return assistant.avatar;
    
    // 默认头像
    if (assistant.capabilities.includes(AICapability.IMAGE_GENERATION)) {
      return '🎨';
    }
    if (assistant.capabilities.includes(AICapability.CODE_GENERATION)) {
      return '👨‍💻';
    }
    if (assistant.capabilities.includes(AICapability.AUDIO_SYNTHESIS)) {
      return '🎙️';
    }
    return '🤖';
  }, []);

  // 获取模型提供商图标
  const getProviderIcon = (provider: string): string => {
    const icons: Record<string, string> = {
      'openai': '⚡',
      'anthropic': '🧠',
      'google': '🔍',
      'azure': '☁️',
      'local': '🖥️',
      'custom': '⚙️'
    };
    return icons[provider.toLowerCase()] || '🤖';
  };

  // 渲染助手卡片
  const renderAssistantCard = (assistant: AIAssistant) => {
    const isSelected = selectedId === assistant.id;
    const isInactive = !assistant.isActive;

    return (
      <div
        key={assistant.id}
        className={`assistant-card ${isSelected ? 'selected' : ''} ${isInactive ? 'inactive' : ''}`}
        onClick={() => onSelect(assistant)}
      >
        <div className="assistant-avatar">
          <span className="avatar-icon">{getAvatar(assistant)}</span>
          {isInactive && <span className="inactive-badge">暂停</span>}
        </div>

        <div className="assistant-info">
          <div className="assistant-header">
            <span className="assistant-name">{assistant.name}</span>
            <span className="provider-icon" title={assistant.modelProvider}>
              {getProviderIcon(assistant.modelProvider)}
            </span>
          </div>

          {assistant.description && (
            <p className="assistant-description" title={assistant.description}>
              {assistant.description}
            </p>
          )}

          {assistant.personality && (
            <span className="assistant-personality">
              性格: {assistant.personality}
            </span>
          )}

          <div className="assistant-capabilities">
            {assistant.capabilities.slice(0, 4).map(cap => (
              <CapabilityBadge key={cap} capability={cap} />
            ))}
            {assistant.capabilities.length > 4 && (
              <span className="more-capabilities">
                +{assistant.capabilities.length - 4}
              </span>
            )}
          </div>
        </div>

        {isSelected && (
          <div className="selected-indicator">
            <span>✓</span>
          </div>
        )}
      </div>
    );
  };

  // 渲染分组列表
  const renderGroupedList = () => {
    return Object.entries(groupedAssistants)
      .filter(([_, items]) => items.length > 0)
      .map(([category, items]) => (
        <div key={category} className="assistant-group">
          <h4 className="group-title">{category} ({items.length})</h4>
          <div className="assistant-list">
            {items.map(renderAssistantCard)}
          </div>
        </div>
      ));
  };

  return (
    <div className={`ai-assistant-selector ${className}`}>
      {/* 搜索和工具栏 */}
      <div className="selector-toolbar">
        <div className="search-box">
          <input
            type="text"
            placeholder="搜索AI助手..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="search-input"
          />
          {searchQuery && (
            <button 
              className="clear-search"
              onClick={() => setSearchQuery('')}
            >
              ✕
            </button>
          )}
        </div>

        <div className="toolbar-actions">
          <select 
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value as any)}
            className="sort-select"
          >
            <option value="recent">最近使用</option>
            <option value="name">名称排序</option>
            <option value="popular">功能最多</option>
          </select>

          <label className="show-inactive">
            <input
              type="checkbox"
              checked={showInactive}
              onChange={(e) => setShowInactive(e.target.checked)}
            />
            显示暂停
          </label>
        </div>
      </div>

      {/* 助手列表 */}
      <div className="selector-content">
        {filteredAssistants.length === 0 ? (
          <div className="empty-state">
            <span className="empty-icon">🔍</span>
            <p>未找到匹配的AI助手</p>
            {searchQuery && (
              <button onClick={() => setSearchQuery('')}>
                清除搜索
              </button>
            )}
          </div>
        ) : (
          renderGroupedList()
        )}
      </div>

      {/* 底部操作栏 */}
      {onCreateNew && (
        <div className="selector-footer">
          <button className="create-new-btn" onClick={onCreateNew}>
            <span>+</span>
            <span>创建新助手</span>
          </button>
        </div>
      )}

      {/* 统计信息 */}
      <div className="selector-stats">
        <span>共 {assistants.length} 个助手</span>
        <span>•</span>
        <span>显示 {filteredAssistants.length} 个</span>
        {filterByCapability && (
          <>
            <span>•</span>
            <span>已筛选</span>
          </>
        )}
      </div>
    </div>
  );
};

export default AIAssistantSelector;
