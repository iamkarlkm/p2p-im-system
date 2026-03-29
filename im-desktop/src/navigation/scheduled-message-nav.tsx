import React from 'react';
import { Menu } from 'antd';
import { ClockCircleOutlined } from '@ant-design/icons';
import { Link, useLocation } from 'react-router-dom';

/**
 * 定时消息导航组件
 */
export const ScheduledMessageNav: React.FC = () => {
  const location = useLocation();

  return (
    <Menu.Item
      key="/scheduled-messages"
      icon={<ClockCircleOutlined />}
      selected={location.pathname === '/scheduled-messages'}
    >
      <Link to="/scheduled-messages">定时消息</Link>
    </Menu.Item>
  );
};
