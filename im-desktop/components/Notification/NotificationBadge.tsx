import React, { useEffect, useState, useCallback } from 'react';
import { notificationService, UnreadStats } from '../../services/notificationService';
import './NotificationBadge.css';

interface NotificationBadgeProps {
  onClick: () => void;
  /** 每隔多少毫秒轮询未读数，默认 30000 (30s) */
  pollInterval?: number;
}

const NotificationBadge: React.FC<NotificationBadgeProps> = ({
  onClick,
  pollInterval = 30000,
}) => {
  const [unreadCount, setUnreadCount] = useState(0);
  const [stats, setStats] = useState<UnreadStats | null>(null);

  const fetchUnread = useCallback(async () => {
    try {
      const data = await notificationService.getUnreadCount();
      setUnreadCount(data.total);
      setStats(data);
    } catch (err) {
      console.error('[NotificationBadge] Failed to fetch unread count:', err);
    }
  }, []);

  useEffect(() => {
    fetchUnread();
    const interval = setInterval(fetchUnread, pollInterval);
    return () => clearInterval(interval);
  }, [fetchUnread, pollInterval]);

  // 暴露刷新方法给父组件
  useEffect(() => {
    const handler = () => fetchUnread();
    window.addEventListener('notification-updated', handler);
    return () => window.removeEventListener('notification-updated', handler);
  }, [fetchUnread]);

  const displayCount = unreadCount > 99 ? '99+' : unreadCount;

  return (
    <div className="notification-badge-wrapper" title="通知中心">
      <button
        className="notification-badge-btn"
        onClick={onClick}
        aria-label={`通知中心, ${unreadCount} 条未读`}
      >
        <span className="notification-badge-icon">🔔</span>
        {unreadCount > 0 && (
          <span className="notification-badge-count">{displayCount}</span>
        )}
      </button>

      {/* 悬浮显示各类型未读数 */}
      {stats && unreadCount > 0 && (
        <div className="notification-badge-tooltip">
          <div className="tooltip-title">未读通知</div>
          {Object.entries(stats.byType).map(([type, count]) => (
            <div key={type} className="tooltip-row">
              <span>{notificationService.getTypeIcon(type)}</span>
              <span>{notificationService.getTypeLabel(type)}</span>
              <span className="tooltip-count">{count}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default NotificationBadge;
