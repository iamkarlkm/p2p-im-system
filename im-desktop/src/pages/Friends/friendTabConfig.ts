import React from 'react';
import { FriendList } from '../components/FriendList/FriendList';
import { FriendRecommendationPanel } from '../components/FriendRecommendation';

interface FriendTab {
  key: string;
  label: string;
  component: React.ReactNode;
}

export const friendTabs: FriendTab[] = [
  {
    key: 'list',
    label: '好友列表',
    component: <FriendList />
  },
  {
    key: 'recommendations',
    label: '好友推荐',
    component: <FriendRecommendationPanel />
  }
];

export const getFriendTabByKey = (key: string): FriendTab | undefined => {
  return friendTabs.find(tab => tab.key === key);
};

export const defaultFriendTab = friendTabs[0];
