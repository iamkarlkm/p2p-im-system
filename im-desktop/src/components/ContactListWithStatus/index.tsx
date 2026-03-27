import React, { useState, useEffect, useCallback } from 'react';
import { UserStatus, userStatusService } from '../../services/userStatus';
import { StatusIndicator, ContactStatus } from '../StatusIndicator';

interface Contact {
  id: string;
  nickname: string;
  avatar?: string;
  status?: UserStatus;
}

interface ContactListWithStatusProps {
  contacts: Contact[];
  currentUserId: string;
  onContactClick?: (contact: Contact) => void;
  onContactContextMenu?: (contact: Contact, event: React.MouseEvent) => void;
  selectedContactId?: string;
  filterStatus?: UserStatus['status'] | 'all';
  searchQuery?: string;
  showOfflineContacts?: boolean;
}

/**
 * 带状态的好友列表组件
 * 显示好友列表及其在线状态
 */
export const ContactListWithStatus: React.FC<ContactListWithStatusProps> = ({
  contacts,
  currentUserId,
  onContactClick,
  onContactContextMenu,
  selectedContactId,
  filterStatus = 'all',
  searchQuery = '',
  showOfflineContacts = true,
}) => {
  const [userStatuses, setUserStatuses] = useState<Map<string, UserStatus>>(new Map());
  const [isLoading, setIsLoading] = useState(false);
  const [sortBy, setSortBy] = useState<'status' | 'name' | 'recent'>('status');

  // 加载好友状态
  const loadContactStatuses = useCallback(async () => {
    if (contacts.length === 0) return;

    setIsLoading(true);
    const userIds = contacts.map(c => c.id);
    
    try {
      const statuses = await userStatusService.getUserStatuses(userIds);
      const statusMap = new Map<string, UserStatus>();
      statuses.forEach(status => {
        statusMap.set(status.userId, status);
      });
      setUserStatuses(statusMap);
    } catch (error) {
      console.error('加载好友状态失败:', error);
    } finally {
      setIsLoading(false);
    }
  }, [contacts]);

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

  // 初始加载和定时刷新
  useEffect(() => {
    loadContactStatuses();
    
    // 每30秒刷新一次状态
    const interval = setInterval(loadContactStatuses, 30000);
    return () => clearInterval(interval);
  }, [loadContactStatuses]);

  // 筛选和排序联系人
  const filteredAndSortedContacts = React.useMemo(() => {
    let result = contacts.map(contact => ({
      ...contact,
      status: userStatuses.get(contact.id) || {
        userId: contact.id,
        status: 'offline' as const,
        isVisible: true,
      },
    }));

    // 搜索筛选
    if (searchQuery) {
      const query = searchQuery.toLowerCase();
      result = result.filter(c => 
        c.nickname.toLowerCase().includes(query) ||
        c.id.toLowerCase().includes(query)
      );
    }

    // 状态筛选
    if (filterStatus !== 'all') {
      result = result.filter(c => c.status?.status === filterStatus);
    }

    // 是否显示离线用户
    if (!showOfflineContacts) {
      result = result.filter(c => c.status?.status !== 'offline');
    }

    // 排序
    result.sort((a, b) => {
      switch (sortBy) {
        case 'status':
          const statusOrder = { online: 0, busy: 1, away: 2, invisible: 3, offline: 4 };
          const statusDiff = (statusOrder[a.status?.status || 'offline'] || 4) - 
                           (statusOrder[b.status?.status || 'offline'] || 4);
          if (statusDiff !== 0) return statusDiff;
          return a.nickname.localeCompare(b.nickname, 'zh-CN');
        
        case 'name':
          return a.nickname.localeCompare(b.nickname, 'zh-CN');
        
        case 'recent':
          const timeA = a.status?.lastSeen ? new Date(a.status.lastSeen).getTime() : 0;
          const timeB = b.status?.lastSeen ? new Date(b.status.lastSeen).getTime() : 0;
          return timeB - timeA;
        
        default:
          return 0;
      }
    });

    return result;
  }, [contacts, userStatuses, filterStatus, searchQuery, showOfflineContacts, sortBy]);

  // 按状态分组
  const groupedContacts = React.useMemo(() => {
    const groups: { title: string; contacts: typeof filteredAndSortedContacts }[] = [
      { title: '在线', contacts: [] },
      { title: '忙碌/离开', contacts: [] },
      { title: '离线', contacts: [] },
    ];

    filteredAndSortedContacts.forEach(contact => {
      const status = contact.status?.status;
      if (status === 'online') {
        groups[0].contacts.push(contact);
      } else if (status === 'busy' || status === 'away') {
        groups[1].contacts.push(contact);
      } else {
        groups[2].contacts.push(contact);
      }
    });

    return groups.filter(g => g.contacts.length > 0);
  }, [filteredAndSortedContacts]);

  const handleContactClick = (contact: Contact) => {
    if (onContactClick) {
      onContactClick(contact);
    }
  };

  const handleContextMenu = (contact: Contact, event: React.MouseEvent) => {
    event.preventDefault();
    if (onContactContextMenu) {
      onContactContextMenu(contact, event);
    }
  };

  if (contacts.length === 0) {
    return (
      <div className="contact-list-empty" style={{ padding: '40px', textAlign: 'center', color: '#9CA3AF' }}>
        <p>暂无好友</p>
      </div>
    );
  }

  return (
    <div className="contact-list-with-status" style={{ height: '100%', overflow: 'auto' }}>
      {/* 工具栏 */}
      <div
        className="contact-list-toolbar"
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          padding: '12px 16px',
          borderBottom: '1px solid #E5E7EB',
          backgroundColor: '#F9FAFB',
        }}
      >
        <span style={{ fontSize: '14px', fontWeight: 500, color: '#374151' }}>
          好友 ({filteredAndSortedContacts.length})
        </span>
        
        <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
          <label style={{ fontSize: '12px', color: '#6B7280', display: 'flex', alignItems: 'center', gap: '4px' }}>
            <input
              type="checkbox"
              checked={showOfflineContacts}
              onChange={(e) => {
                // 通过 props 控制，实际应在父组件处理
              }}
              style={{ cursor: 'pointer' }}
            />
            显示离线
          </label>
          
          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value as typeof sortBy)}
            style={{
              padding: '4px 8px',
              border: '1px solid #D1D5DB',
              borderRadius: '4px',
              fontSize: '12px',
              cursor: 'pointer',
            }}
          >
            <option value="status">按状态</option>
            <option value="name">按名称</option>
            <option value="recent">按最近</option>
          </select>
        </div>
      </div>

      {/* 好友列表 */}
      <div className="contact-list-content">
        {isLoading && filteredAndSortedContacts.length === 0 ? (
          <div style={{ padding: '40px', textAlign: 'center', color: '#9CA3AF' }}>
            <p>加载中...</p>
          </div>
        ) : filteredAndSortedContacts.length === 0 ? (
          <div style={{ padding: '40px', textAlign: 'center', color: '#9CA3AF' }}>
            <p>没有找到匹配的好友</p>
          </div>
        ) : (
          groupedContacts.map((group, groupIndex) => (
            <div key={group.title} className="contact-group">
              <div
                className="contact-group-header"
                style={{
                  padding: '8px 16px',
                  backgroundColor: '#F3F4F6',
                  fontSize: '12px',
                  fontWeight: 600,
                  color: '#6B7280',
                  textTransform: 'uppercase',
                  letterSpacing: '0.5px',
                }}
              >
                {group.title} ({group.contacts.length})
              </div>
              
              {group.contacts.map((contact) => (
                <div
                  key={contact.id}
                  className={`contact-item ${selectedContactId === contact.id ? 'selected' : ''}`}
                  onClick={() => handleContactClick(contact)}
                  onContextMenu={(e) => handleContextMenu(contact, e)}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    padding: '12px 16px',
                    cursor: 'pointer',
                    backgroundColor: selectedContactId === contact.id ? '#EFF6FF' : 'transparent',
                    borderBottom: '1px solid #F3F4F6',
                    transition: 'background-color 0.15s ease',
                  }}
                  onMouseEnter={(e) => {
                    if (selectedContactId !== contact.id) {
                      e.currentTarget.style.backgroundColor = '#F9FAFB';
                    }
                  }}
                  onMouseLeave={(e) => {
                    if (selectedContactId !== contact.id) {
                      e.currentTarget.style.backgroundColor = 'transparent';
                    }
                  }}
                >
                  {/* 头像 */}
                  <div
                    className="contact-avatar"
                    style={{
                      position: 'relative',
                      width: '40px',
                      height: '40px',
                      borderRadius: '50%',
                      backgroundColor: contact.avatar ? 'transparent' : '#3B82F6',
                      backgroundImage: contact.avatar ? `url(${contact.avatar})` : 'none',
                      backgroundSize: 'cover',
                      backgroundPosition: 'center',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      color: 'white',
                      fontWeight: 600,
                      fontSize: '16px',
                      marginRight: '12px',
                      flexShrink: 0,
                    }}
                  >
                    {!contact.avatar && contact.nickname.charAt(0).toUpperCase()}
                    <span
                      style={{
                        position: 'absolute',
                        bottom: '2px',
                        right: '2px',
                        width: '10px',
                        height: '10px',
                        borderRadius: '50%',
                        backgroundColor: contact.status?.status === 'online' ? '#10B981' :
                                        contact.status?.status === 'busy' ? '#EF4444' :
                                        contact.status?.status === 'away' ? '#F59E0B' : '#9CA3AF',
                        border: '2px solid white',
                      }}
                    />
                  </div>

                  {/* 信息 */}
                  <div className="contact-info" style={{ flex: 1, minWidth: 0 }}>
                    <div
                      className="contact-name"
                      style={{
                        fontSize: '14px',
                        fontWeight: 500,
                        color: '#374151',
                        marginBottom: '4px',
                        whiteSpace: 'nowrap',
                        overflow: 'hidden',
                        textOverflow: 'ellipsis',
                      }}
                    >
                      {contact.nickname}
                    </div>
                    
                    <ContactStatus
                      status={contact.status || { userId: contact.id, status: 'offline', isVisible: true }}
                      showLastSeen={true}
                    />
                  </div>
                </div>
              ))}
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default ContactListWithStatus;
