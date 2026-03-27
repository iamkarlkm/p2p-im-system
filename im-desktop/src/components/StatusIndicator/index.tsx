import React from 'react';
import { UserStatus } from '../../services/userStatus';

interface StatusIndicatorProps {
  status: UserStatus['status'];
  size?: 'small' | 'medium' | 'large';
  showLabel?: boolean;
  customStatus?: string;
}

interface StatusConfig {
  color: string;
  label: string;
  glowColor: string;
}

const STATUS_CONFIG: Record<UserStatus['status'], StatusConfig> = {
  online: {
    color: '#10B981',
    label: '在线',
    glowColor: 'rgba(16, 185, 129, 0.4)',
  },
  offline: {
    color: '#9CA3AF',
    label: '离线',
    glowColor: 'rgba(156, 163, 175, 0.4)',
  },
  away: {
    color: '#F59E0B',
    label: '离开',
    glowColor: 'rgba(245, 158, 11, 0.4)',
  },
  busy: {
    color: '#EF4444',
    label: '忙碌',
    glowColor: 'rgba(239, 68, 68, 0.4)',
  },
  invisible: {
    color: '#6B7280',
    label: '隐身',
    glowColor: 'rgba(107, 114, 128, 0.4)',
  },
};

const SIZE_CONFIG = {
  small: { dot: 8, fontSize: 12 },
  medium: { dot: 12, fontSize: 14 },
  large: { dot: 16, fontSize: 16 },
};

/**
 * 状态指示器组件
 * 显示用户在线状态的彩色指示点
 */
export const StatusIndicator: React.FC<StatusIndicatorProps> = ({
  status,
  size = 'medium',
  showLabel = false,
  customStatus,
}) => {
  const config = STATUS_CONFIG[status] || STATUS_CONFIG.offline;
  const sizeConfig = SIZE_CONFIG[size];

  return (
    <div
      className="status-indicator"
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        gap: size === 'small' ? '4px' : '6px',
      }}
      title={customStatus || config.label}
    >
      <span
        className={`status-dot status-${status}`}
        style={{
          width: `${sizeConfig.dot}px`,
          height: `${sizeConfig.dot}px`,
          borderRadius: '50%',
          backgroundColor: config.color,
          boxShadow: status === 'online' ? `0 0 6px ${config.glowColor}` : 'none',
          flexShrink: 0,
          position: 'relative',
          transition: 'all 0.3s ease',
        }}
      >
        {status === 'online' && (
          <span
            className="status-pulse"
            style={{
              position: 'absolute',
              top: '50%',
              left: '50%',
              transform: 'translate(-50%, -50%)',
              width: `${sizeConfig.dot * 2}px`,
              height: `${sizeConfig.dot * 2}px`,
              borderRadius: '50%',
              backgroundColor: config.glowColor,
              animation: 'status-pulse 2s infinite',
              zIndex: -1,
            }}
          />
        )}
      </span>

      {showLabel && (
        <span
          className="status-label"
          style={{
            fontSize: `${sizeConfig.fontSize}px`,
            color: '#6B7280',
            fontWeight: 400,
          }}
        >
          {customStatus || config.label}
        </span>
      )}
    </div>
  );
};

/**
 * 带边框的状态指示器
 * 适用于头像周围的状态显示
 */
export const AvatarStatusIndicator: React.FC<{
  status: UserStatus['status'];
  avatarSize?: number;
  borderWidth?: number;
}> = ({ status, avatarSize = 40, borderWidth = 2 }) => {
  const config = STATUS_CONFIG[status] || STATUS_CONFIG.offline;
  const indicatorSize = Math.max(10, avatarSize * 0.3);

  return (
    <span
      className="avatar-status-indicator"
      style={{
        position: 'absolute',
        bottom: borderWidth,
        right: borderWidth,
        width: `${indicatorSize}px`,
        height: `${indicatorSize}px`,
        borderRadius: '50%',
        backgroundColor: config.color,
        border: `${borderWidth}px solid #FFFFFF`,
        boxShadow: `0 1px 3px rgba(0, 0, 0, 0.2), 0 0 0 1px ${config.color}20`,
        zIndex: 10,
        transition: 'all 0.3s ease',
      }}
      title={config.label}
    />
  );
};

/**
 * 好友列表项状态
 * 包含状态点和最后在线时间
 */
export const ContactStatus: React.FC<{
  status: UserStatus;
  showLastSeen?: boolean;
}> = ({ status, showLastSeen = true }) => {
  const config = STATUS_CONFIG[status.status] || STATUS_CONFIG.offline;

  const formatLastSeen = (date?: Date): string => {
    if (!date) return '';
    const now = new Date();
    const diff = now.getTime() - new Date(date).getTime();
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(diff / 3600000);
    const days = Math.floor(diff / 86400000);

    if (minutes < 1) return '刚刚';
    if (minutes < 60) return `${minutes}分钟前`;
    if (hours < 24) return `${hours}小时前`;
    if (days < 7) return `${days}天前`;
    return new Date(date).toLocaleDateString('zh-CN');
  };

  return (
    <div
      className="contact-status"
      style={{
        display: 'flex',
        alignItems: 'center',
        gap: '6px',
      }}
    >
      <StatusIndicator status={status.status} size="small" />

      {showLastSeen && status.status === 'offline' && status.lastSeen && (
        <span
          className="last-seen"
          style={{
            fontSize: '11px',
            color: '#9CA3AF',
          }}
        >
          {formatLastSeen(status.lastSeen)}
        </span>
      )}

      {status.customStatus && (
        <span
          className="custom-status-text"
          style={{
            fontSize: '12px',
            color: '#6B7280',
            marginLeft: '4px',
            maxWidth: '120px',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap',
          }}
          title={status.customStatus}
        >
          — {status.customStatus}
        </span>
      )}
    </div>
  );
};

export default StatusIndicator;
