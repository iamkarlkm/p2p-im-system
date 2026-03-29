// pages/DoNotDisturbPage.tsx
import React, { useEffect, useState } from 'react';
import { observer } from 'mobx-react-lite';
import {
  Card,
  Switch,
  Button,
  List,
  Tag,
  Space,
  Empty,
  message,
  Popconfirm,
  Badge,
  Tooltip,
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  ClockCircleOutlined,
  CalendarOutlined,
  PhoneOutlined,
  AtSignOutlined,
  NotificationOutlined,
} from '@ant-design/icons';
import { doNotDisturbStore } from '../stores/do-not-disturb-store';
import { DoNotDisturbPeriod } from '../types/do-not-disturb';
import { DoNotDisturbModal } from '../components/DoNotDisturbModal';

export const DoNotDisturbPage: React.FC = observer(() => {
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingPeriod, setEditingPeriod] = useState<DoNotDisturbPeriod | null>(null);

  useEffect(() => {
    doNotDisturbStore.fetchPeriods();
  }, []);

  const handleCreate = () => {
    setEditingPeriod(null);
    setIsModalVisible(true);
  };

  const handleEdit = (period: DoNotDisturbPeriod) => {
    setEditingPeriod(period);
    setIsModalVisible(true);
  };

  const handleDelete = async (periodId: string) => {
    try {
      await doNotDisturbStore.deletePeriod(periodId);
      message.success('删除成功');
    } catch {
      message.error('删除失败');
    }
  };

  const handleToggle = async (period: DoNotDisturbPeriod) => {
    try {
      await doNotDisturbStore.togglePeriod(period.id, !period.isEnabled);
      message.success(period.isEnabled ? '已禁用' : '已启用');
    } catch {
      message.error('操作失败');
    }
  };

  const handleModalSuccess = () => {
    setIsModalVisible(false);
    setEditingPeriod(null);
    doNotDisturbStore.fetchPeriods();
  };

  return (
    <div style={{ padding: '24px' }}>
      <Card
        title={
          <Space>
            <NotificationOutlined />
            <span>免打扰设置</span>
            {doNotDisturbStore.isInDoNotDisturbMode && (
              <Badge status="processing" text="生效中" color="orange" />
            )}
          </Space>
        }
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
            添加时段
          </Button>
        }
      >
        {doNotDisturbStore.isInDoNotDisturbMode && (
          <Card
            size="small"
            style={{
              marginBottom: 16,
              backgroundColor: '#fff7e6',
              borderColor: '#ffd591',
            }}
          >
            <Space>
              <ClockCircleOutlined style={{ color: '#fa8c16', fontSize: 20 }} />
              <span>
                当前处于免打扰模式，{doNotDisturbStore.activePeriods.length}个时段生效中
                {!doNotDisturbStore.shouldAllowCalls && ' · 通话已静音'}
              </span>
            </Space>
          </Card>
        )}

        {doNotDisturbStore.periods.length === 0 ? (
          <Empty
            description="还没有设置免打扰时段"
            image={Empty.PRESENTED_IMAGE_SIMPLE}
          >
            <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
              立即添加
            </Button>
          </Empty>
        ) : (
          <List
            dataSource={doNotDisturbStore.periods}
            renderItem={(period) => (
              <List.Item
                actions={[
                  <Switch
                    checked={period.isEnabled}
                    onChange={() => handleToggle(period)}
                    checkedChildren="开启"
                    unCheckedChildren="关闭"
                  />,
                  <Tooltip title="编辑">
                    <Button
                      icon={<EditOutlined />}
                      onClick={() => handleEdit(period)}
                    />
                  </Tooltip>,
                  <Popconfirm
                    title="确认删除"
                    description="确定要删除这个免打扰时段吗？"
                    onConfirm={() => handleDelete(period.id)}
                    okText="删除"
                    cancelText="取消"
                    okButtonProps={{ danger: true }}
                  >
                    <Tooltip title="删除">
                      <Button danger icon={<DeleteOutlined />} />
                    </Tooltip>
                  </Popconfirm>,
                ]}
              >
                <List.Item.Meta
                  title={
                    <Space>
                      <span>{period.name}</span>
                      {period.isCurrentlyActive && (
                        <Tag color="orange">生效中</Tag>
                      )}
                    </Space>
                  }
                  description={
                    <Space direction="vertical" size={4} style={{ marginTop: 8 }}>
                      <Space>
                        <ClockCircleOutlined />
                        <Tag color="blue">
                          {String(period.startHour).padStart(2, '0')}:
                          {String(period.startMinute).padStart(2, '0')} - 
                          {String(period.endHour).padStart(2, '0')}:
                          {String(period.endMinute).padStart(2, '0')}
                        </Tag>
                      </Space>
                      <Space>
                        <CalendarOutlined />
                        <span>{getActiveDaysText(period.activeDays)}</span>
                      </Space>
                      <Space size={8}>
                        {period.allowCalls && (
                          <Tag icon={<PhoneOutlined />} color="green">
                            允许通话
                          </Tag>
                        )}
                        {period.allowMentions && (
                          <Tag icon={<AtSignOutlined />} color="blue">
                            允许@提及
                          </Tag>
                        )}
                      </Space>
                    </Space>
                  }
                />
              </List.Item>
            )}
          />
        )}
      </Card>

      <DoNotDisturbModal
        visible={isModalVisible}
        period={editingPeriod}
        onCancel={() => {
          setIsModalVisible(false);
          setEditingPeriod(null);
        }}
        onSuccess={handleModalSuccess}
      />
    </div>
  );
});

function getActiveDaysText(activeDays: number[]): string {
  if (activeDays.length === 7) return '每天';
  if (activeDays.length === 5 && activeDays.every(d => d >= 1 && d <= 5)) {
    return '工作日';
  }
  if (activeDays.length === 2 && activeDays.every(d => d === 6 || d === 7)) {
    return '周末';
  }
  const dayNames = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
  return activeDays.map(d => dayNames[(d - 1) % 7]).join('、');
}
