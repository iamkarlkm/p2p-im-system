import React, { useState, useEffect } from 'react';
import { UserStatus, userStatusService } from '../../services/userStatus';

interface StatusSelectorProps {
  currentStatus?: UserStatus;
  onStatusChange?: (status: UserStatus['status'], customStatus?: string, message?: string) => void;
  disabled?: boolean;
}

interface StatusOption {
  value: UserStatus['status'];
  label: string;
  color: string;
  icon: string;
}

const STATUS_OPTIONS: StatusOption[] = [
  { value: 'online', label: '在线', color: '#10B981', icon: '🟢' },
  { value: 'busy', label: '忙碌', color: '#EF4444', icon: '🔴' },
  { value: 'away', label: '离开', color: '#F59E0B', icon: '🟡' },
  { value: 'invisible', label: '隐身', color: '#9CA3AF', icon: '⚪' },
];

/**
 * 状态选择器组件
 * 允许用户选择和管理自己的在线状态
 */
export const StatusSelector: React.FC<StatusSelectorProps> = ({
  currentStatus,
  onStatusChange,
  disabled = false,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [selectedStatus, setSelectedStatus] = useState<UserStatus['status']>(currentStatus?.status || 'online');
  const [customMessage, setCustomMessage] = useState(currentStatus?.statusMessage || '');
  const [customStatusText, setCustomStatusText] = useState(currentStatus?.customStatus || '');
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (currentStatus) {
      setSelectedStatus(currentStatus.status);
      setCustomMessage(currentStatus.statusMessage || '');
      setCustomStatusText(currentStatus.customStatus || '');
    }
  }, [currentStatus]);

  const handleStatusSelect = async (status: UserStatus['status']) => {
    setSelectedStatus(status);
    setIsLoading(true);

    try {
      const success = await userStatusService.setStatus(status, customStatusText, customMessage);
      if (success && onStatusChange) {
        onStatusChange(status, customStatusText, customMessage);
      }
    } catch (error) {
      console.error('状态更新失败:', error);
    } finally {
      setIsLoading(false);
      setIsOpen(false);
    }
  };

  const handleCustomMessageSubmit = async () => {
    setIsLoading(true);
    try {
      const success = await userStatusService.setStatus(selectedStatus, customStatusText, customMessage);
      if (success && onStatusChange) {
        onStatusChange(selectedStatus, customStatusText, customMessage);
      }
    } catch (error) {
      console.error('自定义状态更新失败:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const getCurrentStatusOption = () => {
    return STATUS_OPTIONS.find(opt => opt.value === selectedStatus) || STATUS_OPTIONS[0];
  };

  const currentOption = getCurrentStatusOption();

  return (
    <div className="status-selector">
      <button
        className="status-selector-trigger"
        onClick={() => !disabled && setIsOpen(!isOpen)}
        disabled={disabled || isLoading}
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: '8px',
          padding: '8px 12px',
          border: '1px solid #E5E7EB',
          borderRadius: '8px',
          backgroundColor: disabled ? '#F3F4F6' : '#FFFFFF',
          cursor: disabled ? 'not-allowed' : 'pointer',
          fontSize: '14px',
          color: '#374151',
          transition: 'all 0.2s ease',
        }}
      >>
        <span
          className="status-dot"
          style={{
            width: '10px',
            height: '10px',
            borderRadius: '50%',
            backgroundColor: currentOption.color,
            flexShrink: 0,
          }}
        />
        <span className="status-label">{currentOption.label}</span>
        <span className="dropdown-arrow" style={{ marginLeft: 'auto', fontSize: '12px' }}>
          {isOpen ? '▲' : '▼'}
        </span>
      </button>

      {isOpen && (
        <div
          className="status-dropdown"
          style={{
            position: 'absolute',
            top: '100%',
            left: 0,
            marginTop: '4px',
            backgroundColor: '#FFFFFF',
            border: '1px solid #E5E7EB',
            borderRadius: '8px',
            boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
            zIndex: 1000,
            minWidth: '200px',
            padding: '8px 0',
          }}
        >
          <div className="status-options">
            {STATUS_OPTIONS.map((option) => (
              <button
                key={option.value}
                className={`status-option ${selectedStatus === option.value ? 'selected' : ''}`}
                onClick={() => handleStatusSelect(option.value)}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '10px',
                  width: '100%',
                  padding: '10px 16px',
                  border: 'none',
                  backgroundColor: selectedStatus === option.value ? '#F3F4F6' : 'transparent',
                  cursor: 'pointer',
                  textAlign: 'left',
                  fontSize: '14px',
                  color: '#374151',
                  transition: 'background-color 0.15s ease',
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.backgroundColor = '#F9FAFB';
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.backgroundColor = selectedStatus === option.value ? '#F3F4F6' : 'transparent';
                }}
              >
                <span style={{ fontSize: '14px' }}>{option.icon}</span>
                <span>{option.label}</span>
                {selectedStatus === option.value && (
                  <span style={{ marginLeft: 'auto', color: '#10B981' }}>✓</span>
                )}
              </button>
            ))}
          </div>

          <div
            className="status-divider"
            style={{
              height: '1px',
              backgroundColor: '#E5E7EB',
              margin: '8px 16px',
            }}
          />

          <div
            className="status-custom-section"
            style={{ padding: '8px 16px' }}
          >
            <label
              style={{
                display: 'block',
                fontSize: '12px',
                color: '#6B7280',
                marginBottom: '6px',
                fontWeight: 500,
              }}
            >
              自定义状态
            </label>
            <input
              type="text"
              placeholder="例如：开会中、吃饭..."
              value={customStatusText}
              onChange={(e) => setCustomStatusText(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === 'Enter') {
                  handleCustomMessageSubmit();
                }
              }}
              style={{
                width: '100%',
                padding: '8px 10px',
                border: '1px solid #D1D5DB',
                borderRadius: '6px',
                fontSize: '13px',
                outline: 'none',
                boxSizing: 'border-box',
              }}
              onFocus={(e) => {
                e.target.style.borderColor = '#3B82F6';
                e.target.style.boxShadow = '0 0 0 3px rgba(59, 130, 246, 0.1)';
              }}
              onBlur={(e) => {
                e.target.style.borderColor = '#D1D5DB';
                e.target.style.boxShadow = 'none';
              }}
            />
          </div>

          <div
            className="status-message-section"
            style={{ padding: '8px 16px' }}
          >
            <label
              style={{
                display: 'block',
                fontSize: '12px',
                color: '#6B7280',
                marginBottom: '6px',
                fontWeight: 500,
              }}
            >
              状态消息
            </label>
            <input
              type="text"
              placeholder="输入状态消息..."
              value={customMessage}
              onChange={(e) => setCustomMessage(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === 'Enter') {
                  handleCustomMessageSubmit();
                }
              }}
              style={{
                width: '100%',
                padding: '8px 10px',
                border: '1px solid #D1D5DB',
                borderRadius: '6px',
                fontSize: '13px',
                outline: 'none',
                boxSizing: 'border-box',
              }}
              onFocus={(e) => {
                e.target.style.borderColor = '#3B82F6';
                e.target.style.boxShadow = '0 0 0 3px rgba(59, 130, 246, 0.1)';
              }}
              onBlur={(e) => {
                e.target.style.borderColor = '#D1D5DB';
                e.target.style.boxShadow = 'none';
              }}
            />
          </div>
        </div>
      )}

      {isLoading && (
        <div
          className="status-loading"
          style={{
            position: 'absolute',
            top: '50%',
            left: '50%',
            transform: 'translate(-50%, -50%)',
            padding: '4px 8px',
            backgroundColor: 'rgba(0, 0, 0, 0.5)',
            borderRadius: '4px',
            color: 'white',
            fontSize: '12px',
          }}
        >
          保存中...
        </div>
      )}
    </div>
  );
};

export default StatusSelector;
