import { UserAddOutlined } from '@ant-design/icons';
import type { MenuItem } from '../types/menu';

export const friendMenuItems: MenuItem[] = [
  {
    key: 'friends',
    icon: <UserAddOutlined />,
    label: '好友管理',
    children: [
      {
        key: 'friend-list',
        label: '好友列表',
        path: '/friends/list'
      },
      {
        key: 'friend-recommendations',
        label: '好友推荐',
        path: '/friends/recommendations',
        badge: 'new'
      },
      {
        key: 'friend-requests',
        label: '好友申请',
        path: '/friends/requests'
      },
      {
        key: 'friend-groups',
        label: '分组管理',
        path: '/friends/groups'
      }
    ]
  }
];
