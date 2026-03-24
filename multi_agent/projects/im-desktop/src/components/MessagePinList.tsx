/**
 * 消息置顶列表组件
 * 显示会话中的置顶消息列表
 */

import React, { useState, useEffect } from 'react';
import { messageExportService } from '../services/messageExportService';
import { Card, List, Typography, Space, Button, Tag, Empty, Spin, Tooltip } from 'antd';
import { PinFilled, PushpinOutlined, DeleteOutlined, JumpOutlined } from '@ant-design/icons';

const { Text } = Typography;

// 置顶消息接口
interface PinnedMessage {
    id: number;
    messageId: number;
    sessionId: string;
    content: string;
    senderName: string;
    senderId: string;
    messageType: string;
    timestamp: string;
    pinnedBy: string;
    pinnedAt: string;
    expiresAt?: string;
    isExpired: boolean;
}

interface MessagePinListProps {
    sessionId: string;
    onJumpToMessage?: (messageId: number) => void;
    onUnpin?: (pinId: number) => void;
    compact?: boolean;
    maxItems?: number;
}

/**
 * 消息置顶列表组件
 */
const MessagePinList: React.FC<MessagePinListProps> = ({
    sessionId,
    onJumpToMessage,
    onUnpin,
    compact = false,
    maxItems = 10
}) => {
    const [pinnedMessages, setPinnedMessages] = useState<PinnedMessage[]>([]);
    const [loading, setLoading] = useState(false);
    const [expanded, setExpanded] = useState(false);

    // 加载置顶消息
    useEffect(() => {
        loadPinnedMessages();
    }, [sessionId]);

    const loadPinnedMessages = async () => {
        setLoading(true);
        try {
            // TODO: 调用实际的 API 获取置顶消息
            // 这里使用模拟数据
            const mockData: PinnedMessage[] = [
                {
                    id: 1,
                    messageId: 1001,
                    sessionId: sessionId,
                    content: '重要通知：本周六下午 3 点开会',
                    senderName: '张三',
                    senderId: 'user_001',
                    messageType: 'TEXT',
                    timestamp: '2026-03-22 10:00:00',
                    pinnedBy: 'admin',
                    pinnedAt: '2026-03-22 10:05:00',
                    isExpired: false
                },
                {
                    id: 2,
                    messageId: 1005,
                    sessionId: sessionId,
                    content: '项目文档链接：https://docs.example.com',
                    senderName: '李四',
                    senderId: 'user_002',
                    messageType: 'LINK',
                    timestamp: '2026-03-22 11:00:00',
                    pinnedBy: 'admin',
                    pinnedAt: '2026-03-22 11:10:00',
                    isExpired: false
                }
            ];
            setPinnedMessages(mockData.slice(0, maxItems));
        } catch (error) {
            console.error('加载置顶消息失败', error);
        } finally {
            setLoading(false);
        }
    };

    // 取消置顶
    const handleUnpin = async (pinId: number, messageId: number) => {
        try {
            // TODO: 调用取消置顶 API
            if (onUnpin) {
                await onUnpin(pinId);
            }
            setPinnedMessages(prev => prev.filter(msg => msg.id !== pinId));
        } catch (error) {
            console.error('取消置顶失败', error);
        }
    };

    // 跳转到消息
    const handleJumpToMessage = (messageId: number) => {
        if (onJumpToMessage) {
            onJumpToMessage(messageId);
        }
        setExpanded(false);
    };

    // 获取消息类型标签颜色
    const getMessageTypeColor = (type: string): string => {
        const colors: Record<string, string> = {
            TEXT: 'blue',
            IMAGE: 'green',
            FILE: 'orange',
            LINK: 'purple',
            VIDEO: 'red',
            AUDIO: 'cyan'
        };
        return colors[type] || 'default';
    };

    // 获取消息类型文本
    const getMessageTypeText = (type: string): string => {
        const texts: Record<string, string> = {
            TEXT: '文本',
            IMAGE: '图片',
            FILE: '文件',
            LINK: '链接',
            VIDEO: '视频',
            AUDIO: '语音'
        };
        return texts[type] || type;
    };

    // 渲染置顶消息项
    const renderMessageItem = (msg: PinnedMessage) => (
        <List.Item
            key={msg.id}
            actions={[
                <Tooltip key="jump" title="跳转到消息">
                    <Button
                        type="text"
                        icon={<JumpOutlined />}
                        onClick={() => handleJumpToMessage(msg.messageId)}
                        size="small"
                    />
                </Tooltip>,
                <Tooltip key="unpin" title="取消置顶">
                    <Button
                        type="text"
                        danger
                        icon={<DeleteOutlined />}
                        onClick={() => handleUnpin(msg.id, msg.messageId)}
                        size="small"
                    />
                </Tooltip>
            ]}
        >
            <List.Item.Meta
                avatar={
                    <Tag color="orange" icon={<PinFilled />}>
                        置顶
                    </Tag>
                }
                title={
                    <Space>
                        <Text strong>{msg.senderName}</Text>
                        <Tag color={getMessageTypeColor(msg.messageType)}>
                            {getMessageTypeText(msg.messageType)}
                        </Tag>
                        {msg.isExpired && (
                            <Tag color="red">已过期</Tag>
                        )}
                    </Space>
                }
                description={
                    <Space direction="vertical" size={0} style={{ width: '100%' }}>
                        <Text ellipsis={{ tooltip: msg.content }}>
                            {msg.content}
                        </Text>
                        <Text type="secondary" style={{ fontSize: '12px' }}>
                            置顶于 {new Date(msg.pinnedAt).toLocaleString('zh-CN')}
                        </Text>
                    </Space>
                }
            />
        </List.Item>
    );

    // 紧凑模式
    if (compact) {
        return (
            <Card
                size="small"
                title={
                    <Space>
                        <PushpinOutlined />
                        <Text strong>置顶消息</Text>
                        <Tag color="blue">{pinnedMessages.length}</Tag>
                    </Space>
                }
                extra={
                    pinnedMessages.length > 0 && (
                        <Button
                            type="link"
                            size="small"
                            onClick={() => setExpanded(!expanded)}
                        >
                            {expanded ? '收起' : '展开'}
                        </Button>
                    )
                }
                style={{ marginBottom: 8 }}
            >
                {loading ? (
                    <Spin size="small" />
                ) : pinnedMessages.length === 0 ? (
                    <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="暂无置顶消息" />
                ) : (
                    <List
                        dataSource={expanded ? pinnedMessages : pinnedMessages.slice(0, 3)}
                        renderItem={renderMessageItem}
                        size="small"
                        pagination={expanded ? {
                            pageSize: 10,
                            showSizeChanger: false
                        } : false}
                    />
                )}
            </Card>
        );
    }

    // 完整模式
    return (
        <Card
            title={
                <Space>
                    <PushpinOutlined />
                    <Text strong>置顶消息列表</Text>
                    <Tag color="blue">{pinnedMessages.length}</Tag>
                </Space>
            }
            extra={
                <Button
                    type="primary"
                    size="small"
                    onClick={loadPinnedMessages}
                    loading={loading}
                >
                    刷新
                </Button>
            }
        >
            {loading ? (
                <div style={{ textAlign: 'center', padding: 20 }}>
                    <Spin size="large" />
                </div>
            ) : pinnedMessages.length === 0 ? (
                <Empty description="暂无置顶消息" />
            ) : (
                <List
                    dataSource={pinnedMessages}
                    renderItem={renderMessageItem}
                    pagination={{
                        pageSize: 10,
                        showSizeChanger: false,
                        showTotal: (total) => `共 ${total} 条`
                    }}
                />
            )}
        </Card>
    );
};

export default MessagePinList;
