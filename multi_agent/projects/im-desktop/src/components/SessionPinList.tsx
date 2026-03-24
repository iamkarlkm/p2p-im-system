/**
 * 会话置顶列表组件
 * 显示和管理置顶的会话列表
 */

import React, { useState, useEffect } from 'react';
import { Card, List, Typography, Space, Button, Tag, Empty, Avatar, Tooltip, Badge } from 'antd';
import { PushpinOutlined, DownOutlined, UpOutlined } from '@ant-design/icons';

const { Text } = Typography;

// 置顶会话接口
interface PinnedSession {
    id: number;
    sessionId: string;
    sessionName: string;
    sessionType: 'PRIVATE' | 'GROUP' | 'CHANNEL';
    avatar?: string;
    lastMessage?: string;
    lastMessageTime?: string;
    unreadCount: number;
    pinnedAt: string;
    pinnedBy: string;
    pinIndex: number;
}

interface SessionPinListProps {
    onSessionClick?: (sessionId: string) => void;
    onUnpinSession?: (pinId: number) => void;
    onReorderPins?: (sessionId: string, newIndex: number) => void;
    maxDisplayCount?: number;
    showReorderControls?: boolean;
}

/**
 * 会话置顶列表组件
 */
const SessionPinList: React.FC<SessionPinListProps> = ({
    onSessionClick,
    onUnpinSession,
    onReorderPins,
    maxDisplayCount = 10,
    showReorderControls = false
}) => {
    const [pinnedSessions, setPinnedSessions] = useState<PinnedSession[]>([]);
    const [loading, setLoading] = useState(false);

    // 加载置顶会话
    useEffect(() => {
        loadPinnedSessions();
    }, []);

    const loadPinnedSessions = async () => {
        setLoading(true);
        try {
            // TODO: 调用实际的 API 获取置顶会话
            const mockData: PinnedSession[] = [
                {
                    id: 1,
                    sessionId: 'session_001',
                    sessionName: '产品讨论组',
                    sessionType: 'GROUP',
                    avatar: undefined,
                    lastMessage: '张三：这个功能什么时候上线？',
                    lastMessageTime: '2026-03-22 14:30:00',
                    unreadCount: 5,
                    pinnedAt: '2026-03-22 09:00:00',
                    pinnedBy: 'current_user',
                    pinIndex: 0
                },
                {
                    id: 2,
                    sessionId: 'session_002',
                    sessionName: '李四',
                    sessionType: 'PRIVATE',
                    avatar: undefined,
                    lastMessage: '好的，我明白了',
                    lastMessageTime: '2026-03-22 13:20:00',
                    unreadCount: 0,
                    pinnedAt: '2026-03-22 10:00:00',
                    pinnedBy: 'current_user',
                    pinIndex: 1
                },
                {
                    id: 3,
                    sessionId: 'session_003',
                    sessionName: '技术分享频道',
                    sessionType: 'CHANNEL',
                    avatar: undefined,
                    lastMessage: '本周技术分享主题：微服务架构',
                    lastMessageTime: '2026-03-22 11:00:00',
                    unreadCount: 12,
                    pinnedAt: '2026-03-22 08:30:00',
                    pinnedBy: 'current_user',
                    pinIndex: 2
                }
            ];
            setPinnedSessions(mockData.slice(0, maxDisplayCount));
        } catch (error) {
            console.error('加载置顶会话失败', error);
        } finally {
            setLoading(false);
        }
    };

    // 取消置顶会话
    const handleUnpin = async (pinId: number, sessionId: string) => {
        try {
            if (onUnpinSession) {
                await onUnpinSession(pinId);
            }
            setPinnedSessions(prev => prev.filter(s => s.id !== pinId));
        } catch (error) {
            console.error('取消置顶失败', error);
        }
    };

    // 点击会话
    const handleSessionClick = (session: PinnedSession) => {
        if (onSessionClick) {
            onSessionClick(session.sessionId);
        }
    };

    // 上移置顶顺序
    const handleMoveUp = async (index: number) => {
        if (index === 0 || !onReorderPins) return;
        
        const session = pinnedSessions[index];
        const newIndex = index - 1;
        
        try {
            if (onReorderPins) {
                await onReorderPins(session.sessionId, newIndex);
            }
            
            // 本地更新顺序
            const newList = [...pinnedSessions];
            [newList[index], newList[newIndex]] = [newList[newIndex], newList[index]];
            setPinnedSessions(newList);
        } catch (error) {
            console.error('调整顺序失败', error);
        }
    };

    // 下移置顶顺序
    const handleMoveDown = async (index: number) => {
        if (index === pinnedSessions.length - 1 || !onReorderPins) return;
        
        const session = pinnedSessions[index];
        const newIndex = index + 1;
        
        try {
            if (onReorderPins) {
                await onReorderPins(session.sessionId, newIndex);
            }
            
            // 本地更新顺序
            const newList = [...pinnedSessions];
            [newList[index], newList[newIndex]] = [newList[newIndex], newList[index]];
            setPinnedSessions(newList);
        } catch (error) {
            console.error('调整顺序失败', error);
        }
    };

    // 获取会话类型图标
    const getSessionTypeIcon = (type: string): string => {
        switch (type) {
            case 'PRIVATE':
                return '👤';
            case 'GROUP':
                return '👥';
            case 'CHANNEL':
                return '📢';
            default:
                return '💬';
        }
    };

    // 渲染会话项
    const renderSessionItem = (session: PinnedSession, index: number) => (
        <List.Item
            key={session.id}
            className="session-pin-item"
            onClick={() => handleSessionClick(session)}
            style={{
                cursor: 'pointer',
                padding: '12px 16px',
                transition: 'background 0.2s'
            }}
            actions={showReorderControls ? [
                <Tooltip key="moveUp" title="上移">
                    <Button
                        type="text"
                        icon={<UpOutlined />}
                        onClick={(e) => {
                            e.stopPropagation();
                            handleMoveUp(index);
                        }}
                        size="small"
                        disabled={index === 0}
                    />
                </Tooltip>,
                <Tooltip key="moveDown" title="下移">
                    <Button
                        type="text"
                        icon={<DownOutlined />}
                        onClick={(e) => {
                            e.stopPropagation();
                            handleMoveDown(index);
                        }}
                        size="small"
                        disabled={index === pinnedSessions.length - 1}
                    />
                </Tooltip>,
                <Tooltip key="unpin" title="取消置顶">
                    <Button
                        type="text"
                        danger
                        icon={<PushpinOutlined />}
                        onClick={(e) => {
                            e.stopPropagation();
                            handleUnpin(session.id, session.sessionId);
                        }}
                        size="small"
                    />
                </Tooltip>
            ] : [
                <Tooltip key="unpin" title="取消置顶">
                    <Button
                        type="text"
                        danger
                        icon={<PushpinOutlined />}
                        onClick={(e) => {
                            e.stopPropagation();
                            handleUnpin(session.id, session.sessionId);
                        }}
                        size="small"
                    />
                </Tooltip>
            ]}
        >
            <List.Item.Meta
                avatar={
                    <Badge count={session.unreadCount} offset={[-5, 5]}>
                        <Avatar 
                            size={48} 
                            src={session.avatar}
                            style={{ backgroundColor: '#1890ff' }}
                        >
                            {getSessionTypeIcon(session.sessionType)}
                        </Avatar>
                    </Badge>
                }
                title={
                    <Space>
                        <Text strong>{session.sessionName}</Text>
                        <Tag color="orange" icon={<PushpinOutlined />}>
                            置顶
                        </Tag>
                    </Space>
                }
                description={
                    <Space direction="vertical" size={0} style={{ width: '100%' }}>
                        <Text ellipsis={{ tooltip: session.lastMessage }}>
                            {session.lastMessage}
                        </Text>
                        <Text type="secondary" style={{ fontSize: '12px' }}>
                            {new Date(session.lastMessageTime!).toLocaleString('zh-CN', {
                                month: '2-digit',
                                day: '2-digit',
                                hour: '2-digit',
                                minute: '2-digit'
                            })}
                        </Text>
                    </Space>
                }
            />
        </List.Item>
    );

    return (
        <Card
            title={
                <Space>
                    <PushpinOutlined />
                    <Text strong>置顶会话</Text>
                    <Tag color="blue">{pinnedSessions.length}</Tag>
                </Space>
            }
            size="small"
            style={{
                marginBottom: 8,
                boxShadow: '0 1px 2px 0 rgba(0,0,0,0.05)'
            }}
            bodyStyle={{ padding: 0 }}
        >
            {loading ? (
                <div style={{ textAlign: 'center', padding: 20 }}>
                    <Spin size="small" />
                </div>
            ) : pinnedSessions.length === 0 ? (
                <Empty 
                    image={Empty.PRESENTED_IMAGE_SIMPLE} 
                    description="暂无置顶会话"
                    style={{ padding: '20px 0' }}
                />
            ) : (
                <List
                    dataSource={pinnedSessions}
                    renderItem={renderSessionItem}
                    size="small"
                    split
                />
            )}
        </Card>
    );
};

export default SessionPinList;
