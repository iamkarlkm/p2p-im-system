import React, { useState, useEffect } from 'react';
import { List, Avatar, Badge, Input, Empty } from 'antd';
import { UserOutlined, SearchOutlined } from '@ant-design/icons';

interface User {
  id: string;
  username: string;
  nickname?: string;
  avatar?: string;
  status?: 'online' | 'offline' | 'away' | 'busy';
  lastMessage?: string;
  lastMessageTime?: string;
  unreadCount?: number;
}

interface UserListProps {
  users?: User[];
  selectedUserId?: string;
  onSelectUser?: (user: User) => void;
  loading?: boolean;
}

/**
 * 用户列表组件 - 显示好友或会话列表
 */
export const UserList: React.FC<UserListProps> = ({
  users = [],
  selectedUserId,
  onSelectUser,
  loading = false,
}) => {
  const [searchText, setSearchText] = useState('');
  const [filteredUsers, setFilteredUsers] = useState<User[]>(users);

  useEffect(() => {
    if (!searchText.trim()) {
      setFilteredUsers(users);
    } else {
      const filtered = users.filter(
        (user) =>
          user.username.toLowerCase().includes(searchText.toLowerCase()) ||
          user.nickname?.toLowerCase().includes(searchText.toLowerCase())
      );
      setFilteredUsers(filtered);
    }
  }, [users, searchText]);

  const getStatusColor = (status?: string) => {
    switch (status) {
      case 'online':
        return '#52c41a';
      case 'away':
        return '#faad14';
      case 'busy':
        return '#f5222d';
      default:
        return '#d9d9d9';
    }
  };

  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* 搜索框 */}
      <div style={{ padding: '12px 16px', borderBottom: '1px solid #f0f0f0' }}>
        <Input
          prefix={<SearchOutlined />}
          placeholder="Search users..."
          value={searchText}
          onChange={(e) => setSearchText(e.target.value)}
          allowClear
        />
      </div>

      {/* 用户列表 */}
      <div style={{ flex: 1, overflow: 'auto' }}>
        {loading ? (
          <div style={{ padding: 20, textAlign: 'center' }}>Loading...</div>
        ) : filteredUsers.length === 0 ? (
          <Empty description="No users found" style={{ marginTop: 40 }} />
        ) : (
          <List
            dataSource={filteredUsers}
            renderItem={(user) => (
              <List.Item
                key={user.id}
                onClick={() => onSelectUser?.(user)}
                style={{
                  cursor: 'pointer',
                  backgroundColor: selectedUserId === user.id ? '#e6f7ff' : 'transparent',
                  padding: '12px 16px',
                  transition: 'background-color 0.3s',
                }}
                onMouseEnter={(e) => {
                  if (selectedUserId !== user.id) {
                    e.currentTarget.style.backgroundColor = '#f5f5f5';
                  }
                }}
                onMouseLeave={(e) => {
                  if (selectedUserId !== user.id) {
                    e.currentTarget.style.backgroundColor = 'transparent';
                  }
                }}
              >
                <List.Item.Meta
                  avatar={
                    <Badge
                      dot
                      color={getStatusColor(user.status)}
                      offset={[-4, 32]}
                    >
                      <Avatar
                        src={user.avatar}
                        icon={!user.avatar && <UserOutlined />}
                        size={40}
                      >
                        {user.nickname?.[0] || user.username[0]}
                      </Avatar>
                    </Badge>
                  }
                  title={
                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                      <span style={{ fontWeight: 500 }}>
                        {user.nickname || user.username}
                      </span>
                      {user.lastMessageTime && (
                        <span style={{ fontSize: 12, color: '#999' }}>
                          {user.lastMessageTime}
                        </span>
                      )}
                    </div>
                  }
                  description={
                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                      <span
                        style={{
                          color: user.unreadCount ? '#262626' : '#999',
                          fontWeight: user.unreadCount ? 500 : 'normal',
                        }}
                      >
                        {user.lastMessage || 'No messages yet'}
                      </span>
                      {user.unreadCount ? (
                        <span
                          style={{
                            backgroundColor: '#ff4d4f',
                            color: '#fff',
                            borderRadius: 10,
                            padding: '0 8px',
                            fontSize: 12,
                            minWidth: 20,
                            textAlign: 'center',
                          }}
                        >
                          {user.unreadCount > 99 ? '99+' : user.unreadCount}
                        </span>
                      ) : null}
                    </div>
                  }
                />
              </List.Item>
            )}
          />
        )}
      </div>
    </div>
  );
};

export default UserList;
