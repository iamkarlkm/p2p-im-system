// components/DoNotDisturbModal.tsx
import React, { useState, useEffect } from 'react';
import {
  Modal,
  Form,
  Input,
  TimePicker,
  Checkbox,
  Switch,
  Space,
  message,
} from 'antd';
import type { Dayjs } from 'dayjs';
import dayjs from 'dayjs';
import { DoNotDisturbPeriod, CreateDoNotDisturbPeriodRequest } from '../types/do-not-disturb';
import { doNotDisturbStore } from '../stores/do-not-disturb-store';

interface DoNotDisturbModalProps {
  visible: boolean;
  period: DoNotDisturbPeriod | null;
  onCancel: () => void;
  onSuccess: () => void;
}

const DAY_OPTIONS = [
  { label: '周一', value: 1 },
  { label: '周二', value: 2 },
  { label: '周三', value: 3 },
  { label: '周四', value: 4 },
  { label: '周五', value: 5 },
  { label: '周六', value: 6 },
  { label: '周日', value: 7 },
];

export const DoNotDisturbModal: React.FC<DoNotDisturbModalProps> = ({
  visible,
  period,
  onCancel,
  onSuccess,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const isEditing = !!period;

  useEffect(() => {
    if (visible && period) {
      form.setFieldsValue({
        name: period.name,
        timeRange: [
          dayjs().hour(period.startHour).minute(period.startMinute),
          dayjs().hour(period.endHour).minute(period.endMinute),
        ],
        activeDays: period.activeDays,
        allowCalls: period.allowCalls,
        allowMentions: period.allowMentions,
      });
    } else if (visible) {
      form.setFieldsValue({
        timeRange: [dayjs('22:00', 'HH:mm'), dayjs('08:00', 'HH:mm')],
        activeDays: [1, 2, 3, 4, 5],
        allowCalls: false,
        allowMentions: true,
      });
    }
  }, [visible, period, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);

      const [startTime, endTime]: [Dayjs, Dayjs] = values.timeRange;
      
      const data: CreateDoNotDisturbPeriodRequest = {
        name: values.name,
        startHour: startTime.hour(),
        startMinute: startTime.minute(),
        endHour: endTime.hour(),
        endMinute: endTime.minute(),
        activeDays: values.activeDays,
        allowCalls: values.allowCalls,
        allowMentions: values.allowMentions,
      };

      if (isEditing && period) {
        await doNotDisturbStore.updatePeriod(period.id, data);
        message.success('更新成功');
      } else {
        await doNotDisturbStore.createPeriod(data);
        message.success('创建成功');
      }

      form.resetFields();
      onSuccess();
    } catch (error) {
      console.error('Submit error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title={isEditing ? '编辑免打扰时段' : '添加免打扰时段'}
      open={visible}
      onOk={handleSubmit}
      onCancel={onCancel}
      confirmLoading={loading}
      okText={isEditing ? '保存' : '创建'}
      width={500}
    >
      <Form
        form={form}
        layout="vertical"
        style={{ marginTop: 16 }}
      >
        <Form.Item
          name="name"
          label="时段名称"
          rules={[{ required: true, message: '请输入时段名称' }]}
        >
          <Input placeholder="例如：睡眠时间" />
        </Form.Item>

        <Form.Item
          name="timeRange"
          label="时间段"
          rules={[{ required: true, message: '请选择时间段' }]}
        >
          <TimePicker.RangePicker
            format="HH:mm"
            style={{ width: '100%' }}
            placeholder={['开始时间', '结束时间']}
          />
        </Form.Item>

        <Form.Item
          name="activeDays"
          label="重复日期"
          rules={[{ required: true, message: '请至少选择一天' }]}
        >
          <Checkbox.Group options={DAY_OPTIONS} />
        </Form.Item>

        <Form.Item label="例外设置">
          <Space direction="vertical">
            <Form.Item
              name="allowCalls"
              valuePropName="checked"
              noStyle
            >
              <Switch
                checkedChildren="允许通话"
                unCheckedChildren="禁止通话"
              />
            </Form.Item>
            <Form.Item
              name="allowMentions"
              valuePropName="checked"
              noStyle
            >
              <Switch
                checkedChildren="允许@提及"
                unCheckedChildren="禁止@提及"
              />
            </Form.Item>
          </Space>
        </Form.Item>
      </Form>
    </Modal>
  );
};
