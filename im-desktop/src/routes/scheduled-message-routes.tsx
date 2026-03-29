import React from 'react';
import { ClockCircleOutlined } from '@ant-design/icons';
import { ScheduledMessagePage } from '../pages/scheduled-message-page';

/**
 * 定时消息路由配置
 */
export const scheduledMessageRoutes = [
  {
    path: '/scheduled-messages',
    element: <ScheduledMessagePage />,
    name: '定时消息',
    icon: <ClockCircleOutlined />,
    auth: true,
  },
];
