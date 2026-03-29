import React, { useEffect, useState } from 'react';
import { observer } from 'mobx-react-lite';
import {
  Card,
  Table,
  Tag,
  Button,
  Space,
  Popconfirm,
  message,
  Badge,
  Empty,
  Spin,
  Tooltip,
  Typography,
  DatePicker,
  Select,
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  StopOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
} from '@ant-design/icons';
import { scheduledMessageStore } from '../stores/scheduled-message-store';
import { ScheduledMessageForm } from '../components/scheduled-message-form';
import { ScheduledMessage, StatusLabel, StatusColor } from '../types/scheduled-message';
import dayjs from 'dayjs';

const { Text } = Typography;
const { Option } = Select;

/**
 * 定时消息管理页面
 */
export const ScheduledMessagePage: React.FC = observer(() => {
  const [formVisible, setFormVisible] = useState(false);
  const [editingMessage, setEditingMessage] = useState<ScheduledMessage | null>(null);

  useEffect(() => {
    scheduledMessageStore.fetchMessages(true);
    scheduledMessageStore.fetchStats();
  }, []);

  const handleCancel = async (id: number) => {
    const success = await scheduledMessageStore.cancelMessage(id);
    if (success) {
      message.success('定时消息已取消');
      scheduledMessageStore.fetchStats();
    } else {
      message.error(scheduledMessageStore.error || '取消失败');
    }
  };

  const handleDelete = async (id: number) => {
    const success = await scheduledMessageStore.deleteMessage(id);
    if (success) {
      message.success('定时消息已删除');
      scheduledMessageStore.fetchStats();
    } else {
      message.error(scheduledMessageStore.error || '删除失败');
    }
  };

  const handleEdit = (record: ScheduledMessage) => {
    setEditingMessage(record);
    setFormVisible(true);
  };

  const handleCreate = () => {
    setEditingMessage(null);
    setFormVisible(true);
  };

  const handleFormSuccess = () => {
    setFormVisible(false);
    setEditingMessage(null);
    scheduledMessageStore.fetchMessages(true);
    scheduledMessageStore.fetchStats();
    message.success(editingMessage ? '更新成功' : '创建成功');
  };

  const columns = [
    {
      title: '接收者',
      dataIndex: 'receiverNickname',
      key: 'receiver',
      render: (text: string, record: ScheduledMessage) => (
        <Space>
          {record.receiverAvatar && (
            <img
              src={record.receiverAvatar}
              alt={text}
              style={{ width: 32, height: 32, borderRadius: '50%' }}
            />
          )}
          <Text>{text || `用户${record.receiverId}`}</Text>
        </Space>
      ),
    },
    {
      title: '消息内容',
      dataIndex: 'content',
      key: 'content',
      ellipsis: true,
      render: (text: string) => (
        <Tooltip title={text}>
          <Text>{text.length > 50 ? text.substring(0, 50) + '...' : text}</Text>
        </Tooltip>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (status: string) => (
        <Tag color={StatusColor[status as keyof typeof StatusColor]}>
          {StatusLabel[status as keyof typeof StatusLabel]}
        </Tag>
      ),
    },
    {
      title: '定时时间',
      dataIndex: 'scheduledTime',
      key: 'scheduledTime',
      width: 180,
      render: (time: string) => (
        <Space>
          <ClockCircleOutlined />
          <Text>{dayjs(time).format('YYYY-MM-DD HH:mm')}</Text>
        </Space>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_: any, record: ScheduledMessage) => (
        <Space size="small">
          {record.status === 'PENDING' && (
            <>
              <Button
                type="text"
                icon={<EditOutlined />}
                onClick={() => handleEdit(record)}
              >
                编辑
              </Button>
              <Popconfirm
                title="确认取消"
                description="确定要取消这条定时消息吗？"
                onConfirm={() => handleCancel(record.id)}
                okText="确定"
                cancelText="取消"
              >
                <Button type="text" danger icon={<StopOutlined />}>
                  取消
                </Button>
              </Popconfirm>
            </>
          )}
          <Popconfirm
            title="确认删除"
            description="确定要删除这条定时消息吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="text" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <Card
      title={
        <Space>
          <ClockCircleOutlined />
          <span>定时消息</span>
          {scheduledMessageStore.pendingCount > 0 && (
            <Badge count={scheduledMessageStore.pendingCount} style={{ backgroundColor: '#1890ff' }} />
          )}
        </Space>
      }
      extra={
        <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
          新建定时消息
        </Button>
      }
    >
      <Space style={{ marginBottom: 16 }}>
        <Select
          placeholder="筛选状态"
          allowClear
          style={{ width: 120 }}
          onChange={(value) => scheduledMessageStore.setFilterStatus(value)}
        >
          <Option value="PENDING">待发送</Option>
          <Option value="SENT">已发送</Option>
          <Option value="CANCELLED">已取消</Option>
          <Option value="FAILED">发送失败</Option>
        </Select>
      </Space>

      <Spin spinning={scheduledMessageStore.loading}>
        {scheduledMessageStore.messages.length === 0 ? (
          <Empty description="暂无定时消息" />
        ) : (
          <Table
            dataSource={scheduledMessageStore.messages}
            columns={columns}
            rowKey="id"
            pagination={{
              current: scheduledMessageStore.page + 1,
              pageSize: scheduledMessageStore.pageSize,
              total: scheduledMessageStore.totalCount,
              onChange: (page) => {
                scheduledMessageStore.page = page - 1;
                scheduledMessageStore.fetchMessages();
              },
            }}
          />
        )}
      </Spin>

      <ScheduledMessageForm
        visible={formVisible}
        onCancel={() => setFormVisible(false)}
        onSuccess={handleFormSuccess}
        initialValues={editingMessage}
      />
    </Card>
  );
});
