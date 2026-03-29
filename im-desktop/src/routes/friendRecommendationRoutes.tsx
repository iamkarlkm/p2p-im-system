import React from 'react';
import { RouteObject } from 'react-router-dom';
import { FriendRecommendationPanel } from '../components/FriendRecommendation';
import { FriendLayout } from '../layouts/FriendLayout';

export const friendRecommendationRoutes: RouteObject[] = [
  {
    path: '/friends',
    element: <FriendLayout />,
    children: [
      {
        path: 'recommendations',
        element: <FriendRecommendationPanel />
      }
    ]
  }
];

export const friendRecommendationRoute = {
  path: '/friends/recommendations',
  element: <FriendRecommendationPanel />,
  meta: {
    title: '好友推荐',
    requiresAuth: true,
    keepAlive: true
  }
};
