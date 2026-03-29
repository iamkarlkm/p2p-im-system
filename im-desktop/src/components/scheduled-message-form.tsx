import React from 'react';
import { observer } from 'mobx-react-lite';
import { Modal, Form, Input, DatePicker, Select, Button, message } from 'antd';
import { ClockCircleOutlined, SendOutlined } from '@ant-design/icons';
import { scheduledMessageStore } from '../stores/scheduled-message-store';
import { ScheduledMessage } from '../types/scheduled-message';
import dayjs from 'dayjs';

const { TextArea } = Input;
const { Option } = Select;

interface ScheduledMessageFormProps {
  visible: boolean;
  onCancel: () => void;
  onSuccess: () => void;
  initialValues: ScheduledMessage | null;
}

/**
 * 定时消息表单组件
 */
export const ScheduledMessageForm: React.FC<ScheduledMessageFormProps> = observer(({
  visible,
  onCancel,
  onSuccess,
  initialValues,
}) => {
  const [form] = Form.useForm();
  const isEditing = !!initialValues;

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      
      // 转换日期格式
      const scheduledTime = values.scheduledTime.format('YYYY-MM-DDTHH:mm:ss');
      
      const messageData = {
        receiverId: values.receiverId,
        content: values.content,
        scheduledTime,
      };

      let success: boolean;
      if (isEditing) {
        success = await scheduledMessageStore.updateMessage(initialValues.id, messageData);
      } else {
        success = await scheduledMessageStore.createMessage(messageData);
      }

      if (success) {
        form.resetFields();
        onSuccess();
      } else {
        message.error(scheduledMessageStore.error || '操作失败');
      }
    } catch (error) {
      console.error('表单验证失败', error);
    }
  };

  const handleCancel = () => {
    form.resetFields();
    onCancel();
  };

  // 设置初始值
  React.useEffect(() => {
    if (visible && initialValues) {
      form.setFieldsValue({
        receiverId: initialValues.receiverId,
        content: initialValues.content,
        scheduledTime: dayjs(initialValues.scheduledTime),
      });
    } else if (visible) {
      form.resetFields();
      // 默认设置为明天同一时间
      form.setFieldsValue({
        scheduledTime: dayjs().add(1, 'day'),
      });
    }
  }, [visible, initialValues, form]);

  // 禁用过去的时间
  const disabledDate = (current: dayjs.Dayjs) => {
    return current && current < dayjs().startOf('day');
  };

  const disabledTime = (current: dayjs.Dayjs) => {
    const now = dayjs();
    if (current && current.isSame(now, 'day')) {
      return {
        disabledHours: () => Array.from({ length: now.hour() }, (_, i) => i),
        disabledMinutes: () => {
          if (current.hour() === now.hour()) {
            return Array.from({ length: now.minute() }, (_, i) => i);
          }
          return [];
        },
      };
    }
    return {};
  };

  return (
    <Modal
      title={
        <span>
          <ClockCircleOutlined style={{ marginRight: 8 }} />
          {isEditing ? '编辑定时消息' : '新建定时消息'}
        </span>
      }
      open={visible}
      onCancel={handleCancel}
      footer={[
        <Button key="cancel" onClick={handleCancel}>
          取消
        </Button>,
        <Button
          key="submit"
          type="primary"
          icon={<SendOutlined />}
          loading={scheduledMessageStore.loading}
          onClick={handleSubmit}
        >
          {isEditing ? '保存修改' : '创建定时消息'}
        </Button>,
      ]}
      width={560}
    >
      <Form
        form={form}
        layout="vertical"
        style={{ marginTop: 16 }}
      >
        <Form.Item
          name="receiverId"
          label="接收者"
          rules={[{ required: true, message: '请选择接收者' }]}
        >
          <Select
            placeholder="选择好友"
            showSearch
            optionFilterProp="children"
            disabled={isEditing}
          >
            {/* 这里应该从好友列表加载 */}
            <Option value={1}>好友1</Option>
            <Option value={2}>好友2</Option>
          </Select>
        </Form.Item>

        <Form.Item
          name="content"
          label="消息内容"
          rules={[
            { required: true, message: '请输入消息内容' },
            { max: 5000, message: '消息内容不能超过5000字符' },
          ]}
        >
          <TextArea
            rows={4}
            placeholder="请输入要定时发送的消息内容..."
            maxLength={5000}
            showCount
          />
        </Form.Item>

        <Form.Item
          name="scheduledTime"
          label="发送时间"
          rules={[{ required: true, message: '请选择发送时间' }]}
        >
          <DatePicker
            showTime
            format="YYYY-MM-DD HH:mm"
            placeholder="选择发送时间"
            style={{ width: '100%' }}
            disabledDate={disabledDate}
            disabledTime={disabledTime}
          />
        </Form.Item>

        <Form.Item>
          <div style={{ color: '#888', fontSize: 12 }}>
            <ClockCircleOutlined style={{ marginRight: 4 }} />
            消息将在指定时间自动发送给接收者
          </div>
        </Form.Item>
      </Form>
    </Modal>
  );
});
