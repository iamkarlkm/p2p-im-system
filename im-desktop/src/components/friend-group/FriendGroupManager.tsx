import React, { useState, useCallback } from 'react';
import {
  Box,
  Paper,
  Typography,
  Breadcrumbs,
  Link,
  Divider
} from '@mui/material';
import { NavigateNext as NavigateNextIcon } from '@mui/icons-material';
import { FriendGroupList } from './FriendGroupList';
import { CreateGroupDialog } from './CreateGroupDialog';
import { GroupMemberList } from './GroupMemberList';
import type { FriendGroup, FriendGroupWithMembers } from '../../types/friendGroup';

interface FriendGroupManagerProps {
  onStartChat?: (friendId: string) => void;
}

export const FriendGroupManager: React.FC<FriendGroupManagerProps> = ({
  onStartChat
}) => {
  const [selectedGroup, setSelectedGroup] = useState<FriendGroupWithMembers | null>(null);
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [groups, setGroups] = useState<FriendGroup[]>([]);

  const handleSelectGroup = useCallback((group: FriendGroupWithMembers) => {
    setSelectedGroup(group);
  }, []);

  const handleCreateGroup = useCallback(() => {
    setCreateDialogOpen(true);
  }, []);

  const handleCreateSuccess = useCallback(() => {
    // 刷新分组列表
    setCreateDialogOpen(false);
  }, []);

  const handleMembersChange = useCallback(() => {
    // 刷新当前分组成员
    if (selectedGroup) {
      // 重新加载分组详情
    }
  }, [selectedGroup]);

  const handleStartChat = useCallback((friendId: string) => {
    onStartChat?.(friendId);
  }, [onStartChat]);

  return (
    <Box sx={{ height: '100%', display: 'flex' }}>
      <Paper sx={{ width: 280, height: '100%', display: 'flex', flexDirection: 'column' }}>
        <FriendGroupList
          selectedGroupId={selectedGroup?.id}
          onSelectGroup={handleSelectGroup}
          onCreateGroup={handleCreateGroup}
        />
      </Paper>

      <Box sx={{ flex: 1, p: 2, overflow: 'auto' }}>
        {selectedGroup ? (
          <>
            <Breadcrumbs separator={<NavigateNextIcon fontSize="small" />} sx={{ mb: 2 }}>
              <Link color="inherit" href="#" onClick={() => setSelectedGroup(null)}>
                好友分组
              </Link>
              <Typography color="text.primary">{selectedGroup.name}</Typography>
            </Breadcrumbs>

            <Typography variant="h5" gutterBottom>
              {selectedGroup.name}
              <Typography component="span" variant="body2" color="text.secondary" sx={{ ml: 2 }}>
                {selectedGroup.members.length} 位好友
              </Typography>
            </Typography>

            <Divider sx={{ my: 2 }} />

            <Paper sx={{ height: 'calc(100% - 100px)' }}>
              <GroupMemberList
                groupId={selectedGroup.id}
                members={selectedGroup.members}
                groups={groups}
                onMembersChange={handleMembersChange}
                onStartChat={handleStartChat}
              />
            </Paper>
          </>
        ) : (
          <Box sx={{ 
            height: '100%', 
            display: 'flex', 
            flexDirection: 'column', 
            alignItems: 'center', 
            justifyContent: 'center',
            color: 'text.secondary'
          }}>
            <Typography variant="h6" gutterBottom>
              选择一个分组查看好友
            </Typography>
            <Typography variant="body2">
              在左侧列表中点击分组名称查看该分组的好友
            </Typography>
          </Box>
        )}
      </Box>

      <CreateGroupDialog
        open={createDialogOpen}
        onClose={() => setCreateDialogOpen(false)}
        onSuccess={handleCreateSuccess}
      />
    </Box>
  );
};
