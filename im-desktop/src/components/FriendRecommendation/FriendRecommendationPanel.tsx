import React, { useState, useEffect, useCallback } from 'react';
import { observer } from 'mobx-react-lite';
import { Card, Avatar, Button, Spin, Empty, Tabs, Tag, message } from 'antd';
import { UserAddOutlined, CloseOutlined, EyeInvisibleOutlined, TeamOutlined, TagsOutlined, UserOutlined } from '@ant-design/icons';
import { friendRecommendationService } from '../../../services/friendRecommendationService';
import { useUserStore } from '../../../stores/userStore';
import './FriendRecommendationPanel.less';

interface RecommendedUser {
  userId: string;
  nickname: string;
  avatar: string;
  reasonType: 'mutual_friends' | 'interest_tags' | 'group_relation' | 'mixed';
  reasonDescription: string;
  score: number;
  mutualFriendsCount?: number;
  commonTags?: string[];
  commonGroups?: string[];
}

export const FriendRecommendationPanel: React.FC = observer(() => {
  const [recommendations, setRecommendations] = useState<RecommendedUser[]>([]);
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState('all');
  const [ignoredIds, setIgnoredIds] = useState<Set<string>>(new Set());
  const userStore = useUserStore();

  const fetchRecommendations = useCallback(async () => {
    setLoading(true);
    try {
      const response = await friendRecommendationService.getRecommendations({
        page: 1,
        size: 20,
        algorithm: activeTab === 'all' ? 'mixed' : activeTab
      });
      setRecommendations(response.data || []);
    } catch (error) {
      message.error('获取推荐列表失败');
    } finally {
      setLoading(false);
    }
  }, [activeTab]);

  useEffect(() => {
    fetchRecommendations();
  }, [fetchRecommendations]);

  const handleAddFriend = async (userId: string) => {
    try {
      await friendRecommendationService.sendFriendRequest(userId);
      message.success('好友请求已发送');
      setRecommendations(prev => prev.filter(r => r.userId !== userId));
    } catch (error) {
      message.error('发送请求失败');
    }
  };

  const handleIgnore = async (userId: string) => {
    try {
      await friendRecommendationService.ignoreRecommendation(userId);
      setIgnoredIds(prev => new Set(prev).add(userId));
      setRecommendations(prev => prev.filter(r => r.userId !== userId));
      message.success('已忽略此推荐');
    } catch (error) {
      message.error('操作失败');
    }
  };

  const getReasonIcon = (reasonType: string) => {
    switch (reasonType) {
      case 'mutual_friends':
        return <TeamOutlined className="reason-icon mutual" />;
      case 'interest_tags':
        return <TagsOutlined className="reason-icon interest" />;
      case 'group_relation':
        return <UserOutlined className="reason-icon group" />;
      default:
        return <UserAddOutlined className="reason-icon mixed" />;
    }
  };

  const getReasonColor = (reasonType: string) => {
    switch (reasonType) {
      case 'mutual_friends':
        return 'blue';
      case 'interest_tags':
        return 'green';
      case 'group_relation':
        return 'purple';
      default:
        return 'orange';
    }
  };

  const renderRecommendationCard = (user: RecommendedUser) => (
    <Card
      key={user.userId}
      className="recommendation-card"
      actions={[
        <Button
          type="primary"
          icon={<UserAddOutlined />}
          onClick={() => handleAddFriend(user.userId)}
          className="add-btn"
        >
          添加好友
        </Button>,
        <Button
          icon={<EyeInvisibleOutlined />}
          onClick={() => handleIgnore(user.userId)}
          className="ignore-btn"
        >
          忽略
        </Button>
      ]}
    >
      <Card.Meta
        avatar={<Avatar size={64} src={user.avatar} icon={<UserOutlined />} />}
        title={
          <div className="user-header">
            <span className="nickname">{user.nickname}</span>
            <Tag color={getReasonColor(user.reasonType)} className="reason-tag">
              {getReasonIcon(user.reasonType)}
              {user.reasonDescription}
            </Tag>
          </div>
        }
        description={
          <div className="user-details">
            {user.mutualFriendsCount !== undefined && user.mutualFriendsCount > 0 && (
              <div className="detail-item">
                <TeamOutlined /> {user.mutualFriendsCount} 个共同好友
              </div>
            )}
            {user.commonTags && user.commonTags.length > 0 && (
              <div className="detail-item">
                <TagsOutlined /> 共同兴趣: {user.commonTags.slice(0, 3).join(', ')}
              </div>
            )}
            {user.commonGroups && user.commonGroups.length > 0 && (
              <div className="detail-item">
                <UserOutlined /> 同群组: {user.commonGroups.slice(0, 2).join(', ')}
              </div>
            )}
            <div className="match-score">
              匹配度: <span className="score">{Math.round(user.score * 100)}%</span>
            </div>
          </div>
        }
      />
    </Card>
  );

  const tabs = [
    { key: 'all', label: '全部推荐', icon: <UserAddOutlined /> },
    { key: 'mutual_friends', label: '共同好友', icon: <TeamOutlined /> },
    { key: 'interest_tags', label: '兴趣相似', icon: <TagsOutlined /> },
    { key: 'group_relation', label: '群组关系', icon: <UserOutlined /> }
  ];

  return (
    <div className="friend-recommendation-panel">
      <div className="panel-header">
        <h2>
          <UserAddOutlined /> 好友推荐
        </h2>
        <span className="subtitle">根据您的社交网络为您推荐</span>
      </div>

      <Tabs
        activeKey={activeTab}
        onChange={setActiveTab}
        items={tabs.map(tab => ({
          key: tab.key,
          label: (
            <span>
              {tab.icon} {tab.label}
            </span>
          )
        }))}
        className="recommendation-tabs"
      />

      <div className="recommendation-content">
        {loading ? (
          <div className="loading-container">
            <Spin size="large" tip="正在为您寻找好友..." />
          </div>
        ) : recommendations.length === 0 ? (
          <Empty
            image={Empty.PRESENTED_IMAGE_SIMPLE}
            description="暂无推荐好友"
          />
        ) : (
          <div className="recommendation-grid">
            {recommendations.map(renderRecommendationCard)}
          </div>
        )}
      </div>
    </div>
  );
});
