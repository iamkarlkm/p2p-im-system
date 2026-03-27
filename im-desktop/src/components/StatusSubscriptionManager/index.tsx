import React, { useState, useEffect } from 'react';
import { UserStatus, userStatusService } from '../../services/userStatus';
import { StatusIndicator } from '../StatusIndicator';

interface SubscriptionManagerProps {
  currentUserId: string;
  contacts: { id: string; nickname: string; avatar?: string }[];
  onSubscriptionChange?: (subscribedUserIds: string[]) => void;
}

interface SubscriptionItem {
  userId: string;
  nickname: string;
  avatar?: string;
  status?: UserStatus;
  isSubscribed: boolean;
}

/**
 * 状态订阅管理器组件
 * 管理用户对其他用户状态的订阅
 */
export const StatusSubscriptionManager: React.FC<SubscriptionManagerProps> = ({
  currentUserId,
  contacts,
  onSubscriptionChange,
}) => {
  const [subscriptions, setSubscriptions] = useState<SubscriptionItem[]>([]);
  const [userStatuses, setUserStatuses] = useState<Map<string, UserStatus>>(new Map());
  const [isLoading, setIsLoading] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [activeFilter, setActiveFilter] = useState<'all' | 'subscribed' | 'unsubscribed'>('all');

  // 初始化订阅列表
  useEffect(() => {
    const loadSubscriptions = async () => {
      setIsLoading(true);
      try {
        const response = await fetch(`http://localhost:8080/api/status/subscriptions/${currentUserId}`);
        if (response.ok) {
          const data = await response.json();
          const subscribedIds = new Set(data.subscriptions?.map((s: any) => s.targetUserId?.toString()) || []);
          
          const items: SubscriptionItem[] = contacts.map(contact => ({
            userId: contact.id,
            nickname: contact.nickname,
            avatar: contact.avatar,
            isSubscribed: subscribedIds.has(contact.id),
          }));
          
          setSubscriptions(items);
          
          // 加载已订阅用户的状态
          if (subscribedIds.size > 0) {
            const userIds = Array.from(subscribedIds) as string[];
            const statuses = await userStatusService.getUserStatuses(userIds);
            const statusMap = new Map<string, UserStatus>();
            statuses.forEach(status => {
              statusMap.set(status.userId, status);
            });
            setUserStatuses(statusMap);
          }
        }
      } catch (error) {
        console.error('加载订阅列表失败:', error);
      } finally {
        setIsLoading(false);
      }
    };

    if (currentUserId) {
      loadSubscriptions();
    }
  }, [currentUserId, contacts]);

  // 监听状态变化
  useEffect(() => {
    const handleStatusChange = (status: UserStatus) => {
      setUserStatuses(prev => {
        const newMap = new Map(prev);
        newMap.set(status.userId, status);
        return newMap;
      });
    };

    userStatusService.on('userStatusChanged', handleStatusChange);
    return () => {
      userStatusService.off('userStatusChanged', handleStatusChange);
    };
  }, []);

  // 订阅/取消订阅
  const toggleSubscription = async (userId: string) => {
    const item = subscriptions.find(s => s.userId === userId);
    if (!item) return;

    setIsLoading(true);
    try {
      if (item.isSubscribed) {
        const success = await userStatusService.unsubscribeUserStatus(userId);
        if (success) {
          setSubscriptions(prev =>
            prev.map(s => s.userId === userId ? { ...s, isSubscribed: false } : s)
          );
        }
      } else {
        const success = await userStatusService.subscribeUserStatus(userId);
        if (success) {
          setSubscriptions(prev =>
            prev.map(s => s.userId === userId ? { ...s, isSubscribed: true } : s)
          );
          // 加载该用户状态
          const status = await userStatusService.getUserStatus(userId);
          if (status) {
            setUserStatuses(prev => {
              const newMap = new Map(prev);
              newMap.set(userId, status);
              return newMap;
            });
          }
        }
      }

      // 通知父组件
      if (onSubscriptionChange) {
        const subscribedIds = subscriptions
          .filter(s => s.userId === userId ? !s.isSubscribed : s.isSubscribed)
          .map(s => s.userId);
        onSubscriptionChange(subscribedIds);
      }
    } catch (error) {
      console.error('切换订阅失败:', error);
    } finally {
      setIsLoading(false);
    }
  };

  // 批量订阅
  const batchSubscribe = async (userIds: string[]) => {
    setIsLoading(true);
    try {
      for (const userId of userIds) {
        await userStatusService.subscribeUserStatus(userId);
      }
      setSubscriptions(prev =>
        prev.map(s => userIds.includes(s.userId) ? { ...s, isSubscribed: true } : s)
      );
    } catch (error) {
      console.error('批量订阅失败:', error);
    } finally {
      setIsLoading(false);
    }
  };

  // 批量取消订阅
  const batchUnsubscribe = async (userIds: string[]) => {
    setIsLoading(true);
    try {
      for (const userId of userIds) {
        await userStatusService.unsubscribeUserStatus(userId);
      }
      setSubscriptions(prev =>
        prev.map(s => userIds.includes(s.userId) ? { ...s, isSubscribed: false } : s)
      );
    } catch (error) {
      console.error('批量取消订阅失败:', error);
    } finally {
      setIsLoading(false);
    }
  };

  // 筛选联系人
  const filteredSubscriptions = React.useMemo(() => {
    let result = subscriptions;

    // 搜索筛选
    if (searchQuery) {
      const query = searchQuery.toLowerCase();
      result = result.filter(s =>
        s.nickname.toLowerCase().includes(query) ||
        s.userId.toLowerCase().includes(query)
      );
    }

    // 订阅状态筛选
    if (activeFilter === 'subscribed') {
      result = result.filter(s => s.isSubscribed);
    } else if (activeFilter === 'unsubscribed') {
      result = result.filter(s => !s.isSubscribed);
    }

    // 按订阅状态和昵称排序
    result.sort((a, b) => {
      if (a.isSubscribed !== b.isSubscribed) {
        return a.isSubscribed ? -1 : 1;
      }
      return a.nickname.localeCompare(b.nickname, 'zh-CN');
    });

    return result;
  }, [subscriptions, searchQuery, activeFilter]);

  const subscribedCount = subscriptions.filter(s => s.isSubscribed).length;

  return (
    <div className="status-subscription-manager" style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* 头部 */}
      <div
        className="subscription-header"
        style={{
          padding: '16px',
          borderBottom: '1px solid #E5E7EB',
          backgroundColor: '#F9FAFB',
        }}
      >
        <h3 style={{ margin: '0 0 12px 0', fontSize: '16px', fontWeight: 600, color: '#374151' }}>
          状态订阅管理
        </h3>
        
        <div style={{ fontSize: '13px', color: '#6B7280', marginBottom: '12px' }}>
          已订阅 {subscribedCount}/{subscriptions.length} 位好友的状态
        </div>

        {/* 搜索框 */}
        <div style={{ position: 'relative', marginBottom: '12px' }}>
          <input
            type="text"
            placeholder="搜索好友..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            style={{
              width: '100%',
              padding: '8px 12px 8px 32px',
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
          <span style={{ position: 'absolute', left: '10px', top: '50%', transform: 'translateY(-50%)', color: '#9CA3AF' }}>
            🔍
          </span>
        </div>

        {/* 筛选按钮 */}
        <div style={{ display: 'flex', gap: '8px' }}>
          {(['all', 'subscribed', 'unsubscribed'] as const).map((filter) => (
            <button
              key={filter}
              onClick={() => setActiveFilter(filter)}
              style={{
                padding: '6px 12px',
                border: activeFilter === filter ? 'none' : '1px solid #D1D5DB',
                borderRadius: '4px',
                fontSize: '12px',
                cursor: 'pointer',
                backgroundColor: activeFilter === filter ? '#3B82F6' : '#FFFFFF',
                color: activeFilter === filter ? '#FFFFFF' : '#374151',
                transition: 'all 0.15s ease',
              }}
            >
              {filter === 'all' ? '全部' : filter === 'subscribed' ? '已订阅' : '未订阅'}
            </button>
          ))}
        </div>

        {/* 批量操作 */}
        {activeFilter === 'unsubscribed' && filteredSubscriptions.length > 0 && (
          <button
            onClick={() => batchSubscribe(filteredSubscriptions.map(s => s.userId))}
            disabled={isLoading}
            style={{
              marginTop: '12px',
              padding: '8px 16px',
              backgroundColor: '#3B82F6',
              color: 'white',
              border: 'none',
              borderRadius: '6px',
              fontSize: '13px',
              cursor: isLoading ? 'not-allowed' : 'pointer',
              opacity: isLoading ? 0.6 : 1,
              width: '100%',
            }}
          >
            批量订阅 ({filteredSubscriptions.length})
          </button>
        )}

        {activeFilter === 'subscribed' && filteredSubscriptions.length > 0 && (
          <button
            onClick={() => batchUnsubscribe(filteredSubscriptions.map(s => s.userId))}
            disabled={isLoading}
            style={{
              marginTop: '12px',
              padding: '8px 16px',
              backgroundColor: '#EF4444',
              color: 'white',
              border: 'none',
              borderRadius: '6px',
              fontSize: '13px',
              cursor: isLoading ? 'not-allowed' : 'pointer',
              opacity: isLoading ? 0.6 : 1,
              width: '100%',
            }}
          >
            批量取消订阅 ({filteredSubscriptions.length})
          </button>
        )}
      </div>

      {/* 订阅列表 */}
      <div className="subscription-list" style={{ flex: 1, overflow: 'auto', padding: '8px 0' }}>
        {isLoading && subscriptions.length === 0 ? (
          <div style={{ padding: '40px', textAlign: 'center', color: '#9CA3AF' }}>
            <p>加载中...</p>
          </div>
        ) : filteredSubscriptions.length === 0 ? (
          <div style={{ padding: '40px', textAlign: 'center', color: '#9CA3AF' }}>
            <p>没有找到匹配的好友</p>
          </div>
        ) : (
          filteredSubscriptions.map((item) => {
            const status = userStatuses.get(item.userId);
            return (
              <div
                key={item.userId}
                className="subscription-item"
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  padding: '10px 16px',
                  borderBottom: '1px solid #F3F4F6',
                  transition: 'background-color 0.15s ease',
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.backgroundColor = '#F9FAFB';
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.backgroundColor = 'transparent';
                }}
              >
                {/* 头像 */}
                <div
                  style={{
                    position: 'relative',
                    width: '36px',
                    height: '36px',
                    borderRadius: '50%',
                    backgroundColor: item.avatar ? 'transparent' : '#3B82F6',
                    backgroundImage: item.avatar ? `url(${item.avatar})` : 'none',
                    backgroundSize: 'cover',
                    backgroundPosition: 'center',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    color: 'white',
                    fontWeight: 600,
                    fontSize: '14px',
                    marginRight: '12px',
                    flexShrink: 0,
                  }}
                >
                  {!item.avatar && item.nickname.charAt(0).toUpperCase()}
                  {status && (
                    <span
                      style={{
                        position: 'absolute',
                        bottom: '0',
                        right: '0',
                        width: '10px',
                        height: '10px',
                        borderRadius: '50%',
                        backgroundColor: status.status === 'online' ? '#10B981' :
                                        status.status === 'busy' ? '#EF4444' :
                                        status.status === 'away' ? '#F59E0B' : '#9CA3AF',
                        border: '2px solid white',
                      }}
                    />
                  )}
                </div>

                {/* 信息 */}
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div
                    style={{
                      fontSize: '14px',
                      fontWeight: 500,
                      color: '#374151',
                      whiteSpace: 'nowrap',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                    }}
                  >
                    {item.nickname}
                  </div>
                  <div style={{ fontSize: '12px', color: '#6B7280' }}>
                    {status ? (
                      <StatusIndicator status={status.status} size="small" showLabel />
                    ) : (
                      <span>未获取状态</span>
                    )}
                  </div>
                </div>

                {/* 订阅按钮 */}
                <button
                  onClick={() => toggleSubscription(item.userId)}
                  disabled={isLoading}
                  style={{
                    padding: '6px 12px',
                    backgroundColor: item.isSubscribed ? '#EF4444' : '#3B82F6',
                    color: 'white',
                    border: 'none',
                    borderRadius: '4px',
                    fontSize: '12px',
                    cursor: isLoading ? 'not-allowed' : 'pointer',
                    opacity: isLoading ? 0.6 : 1,
                    transition: 'all 0.15s ease',
                    whiteSpace: 'nowrap',
                  }}
                >
                  {item.isSubscribed ? '取消订阅' : '订阅'}
                </button>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};

export default StatusSubscriptionManager;
