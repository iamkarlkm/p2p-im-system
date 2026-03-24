import React, { useEffect, useState, useCallback } from 'react';
import { notificationService, NotificationItem } from '../../services/notificationService';
import './NotificationPanel.css';

interface NotificationPanelProps {
  isOpen: boolean;
  onClose: () => void;
}

type FilterType = 'ALL' | 'SYSTEM' | 'FRIEND_REQUEST' | 'GROUP_INVITE' | 'VOTE' | 'ANNOUNCEMENT' | 'SECURITY';

const NotificationPanel: React.FC<NotificationPanelProps> = ({ isOpen, onClose }) => {
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [filter, setFilter] = useState<FilterType>('ALL');
  const [selectedIds, setSelectedIds] = useState<Set<number>>(new Set());

  const fetchNotifications = useCallback(async (pageNum: number, reset: boolean = false) => {
    if (loading) return;
    setLoading(true);
    try {
      const params: { page: number; size: number; type?: string } = {
        page: pageNum,
        size: 20,
      };
      if (filter !== 'ALL') params.type = filter;

      const data = await notificationService.getNotifications(params);
      if (reset) {
        setNotifications(data);
      } else {
        setNotifications(prev => [...prev, ...data]);
      }
      setHasMore(data.length === 20);
      setPage(pageNum);
    } catch (err) {
      console.error('[NotificationPanel] Failed to fetch notifications:', err);
    } finally {
      setLoading(false);
    }
  }, [filter, loading]);

  useEffect(() => {
    if (isOpen) {
      setNotifications([]);
      setPage(0);
      setHasMore(true);
      setSelectedIds(new Set());
      fetchNotifications(0, true);
    }
  }, [isOpen, filter]);

  useEffect(() => {
    if (isOpen) {
      window.dispatchEvent(new CustomEvent('notification-updated'));
    }
  }, [notifications]);

  const handleMarkAsRead = async (id: number) => {
    await notificationService.markAsRead(id);
    setNotifications(prev =>
      prev.map(n => n.id === id ? { ...n, isRead: true } : n)
    );
  };

  const handleMarkAllRead = async () => {
    await notificationService.markAllAsRead();
    setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
    window.dispatchEvent(new CustomEvent('notification-updated'));
  };

  const handleAccept = async (id: number) => {
    await notificationService.handleNotification(id, 'ACCEPTED');
    setNotifications(prev =>
      prev.map(n => n.id === id ? { ...n, isHandled: true, handleResult: 'ACCEPTED' } : n)
    );
    window.dispatchEvent(new CustomEvent('notification-updated'));
  };

  const handleReject = async (id: number) => {
    await notificationService.handleNotification(id, 'REJECTED');
    setNotifications(prev =>
      prev.map(n => n.id === id ? { ...n, isHandled: true, handleResult: 'REJECTED' } : n)
    );
    window.dispatchEvent(new CustomEvent('notification-updated'));
  };

  const toggleSelect = (id: number) => {
    setSelectedIds(prev => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  };

  const handleBatchMarkRead = async () => {
    if (selectedIds.size === 0) return;
    await notificationService.batchMarkAsRead(Array.from(selectedIds));
    setNotifications(prev =>
      prev.map(n => selectedIds.has(n.id) ? { ...n, isRead: true } : n)
    );
    setSelectedIds(new Set());
    window.dispatchEvent(new CustomEvent('notification-updated'));
  };

  if (!isOpen) return null;

  const filters: { key: FilterType; label: string }[] = [
    { key: 'ALL', label: '全部' },
    { key: 'SYSTEM', label: '系统' },
    { key: 'FRIEND_REQUEST', label: '好友' },
    { key: 'GROUP_INVITE', label: '群邀请' },
    { key: 'VOTE', label: '投票' },
    { key: 'ANNOUNCEMENT', label: '公告' },
    { key: 'SECURITY', label: '安全' },
  ];

  return (
    <div className="notification-panel-overlay" onClick={onClose}>
      <div className="notification-panel" onClick={e => e.stopPropagation()}>
        <div className="notification-panel-header">
          <h2>通知中心</h2>
          <div className="notification-panel-actions">
            {selectedIds.size > 0 && (
              <button className="btn-mark-batch" onClick={handleBatchMarkRead}>
                标记已读 ({selectedIds.size})
              </button>
            )}
            <button className="btn-mark-all" onClick={handleMarkAllRead}>
              全部已读
            </button>
            <button className="btn-close" onClick={onClose}>✕</button>
          </div>
        </div>

        <div className="notification-filter-tabs">
          {filters.map(f => (
            <button
              key={f.key}
              className={`filter-tab ${filter === f.key ? 'active' : ''}`}
              onClick={() => setFilter(f.key)}
            >
              {f.label}
            </button>
          ))}
        </div>

        <div className="notification-list">
          {notifications.length === 0 && !loading && (
            <div className="notification-empty">
              <span>📭</span>
              <p>暂无通知</p>
            </div>
          )}

          {notifications.map(notification => (
            <div
              key={notification.id}
              className={`notification-item ${notification.isRead ? 'read' : 'unread'} ${selectedIds.has(notification.id) ? 'selected' : ''}`}
            >
              <div className="notification-select" onClick={() => toggleSelect(notification.id)}>
                <input
                  type="checkbox"
                  checked={selectedIds.has(notification.id)}
                  onChange={() => toggleSelect(notification.id)}
                />
              </div>

              <div className="notification-icon">
                {notificationService.getTypeIcon(notification.type)}
              </div>

              <div className="notification-content" onClick={() => !notification.isRead && handleMarkAsRead(notification.id)}>
                <div className="notification-title-row">
                  <span className="notification-title">{notification.title}</span>
                  {!notification.isRead && <span className="unread-dot" />}
                  {notification.isHandled && (
                    <span className={`handled-badge ${notification.handleResult?.toLowerCase()}`}>
                      {notification.handleResult === 'ACCEPTED' ? '已接受' : '已拒绝'}
                    </span>
                  )}
                </div>
                <div className="notification-body">{notification.content}</div>
                <div className="notification-meta">
                  <span className="notification-type">{notificationService.getTypeLabel(notification.type)}</span>
                  <span className="notification-time">{notificationService.formatTime(notification.createdAt)}</span>
                </div>

                {/* 可交互操作按钮 */}
                {(notification.type === 'FRIEND_REQUEST' || notification.type === 'GROUP_INVITE') && !notification.isHandled && (
                  <div className="notification-actions">
                    <button className="btn-accept" onClick={() => handleAccept(notification.id)}>接受</button>
                    <button className="btn-reject" onClick={() => handleReject(notification.id)}>拒绝</button>
                  </div>
                )}
              </div>
            </div>
          ))}

          {loading && <div className="notification-loading">加载中...</div>}

          {hasMore && !loading && (
            <button className="btn-load-more" onClick={() => fetchNotifications(page + 1)}>
              加载更多
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default NotificationPanel;
