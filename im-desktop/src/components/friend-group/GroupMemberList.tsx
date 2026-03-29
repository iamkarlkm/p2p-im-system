import React, { useState } from 'react';
import {
  Box,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  Avatar,
  IconButton,
  Typography,
  Menu,
  MenuItem,
  Checkbox,
  Tooltip
} from '@mui/material';
import {
  Star as StarIcon,
  StarBorder as StarBorderIcon,
  VolumeOff as MuteIcon,
  VolumeUp as UnmuteIcon,
  MoreVert as MoreVertIcon,
  Chat as ChatIcon,
  ArrowForward as MoveIcon
} from '@mui/icons-material';
import type { FriendGroupMember, FriendGroup } from '../../types/friendGroup';
import * as friendGroupService from '../../services/friendGroupService';

interface GroupMemberListProps {
  groupId: string;
  members: FriendGroupMember[];
  groups: FriendGroup[];
  onMembersChange: () => void;
  onStartChat: (friendId: string) => void;
}

export const GroupMemberList: React.FC<GroupMemberListProps> = ({
  groupId,
  members,
  groups,
  onMembersChange,
  onStartChat
}) => {
  const [menuAnchor, setMenuAnchor] = useState<null | HTMLElement>(null);
  const [selectedMember, setSelectedMember] = useState<FriendGroupMember | null>(null);
  const [moveMenuAnchor, setMoveMenuAnchor] = useState<null | HTMLElement>(null);

  const handleMenuOpen = (event: React.MouseEvent<HTMLElement>, member: FriendGroupMember) => {
    event.stopPropagation();
    setMenuAnchor(event.currentTarget);
    setSelectedMember(member);
  };

  const handleMenuClose = () => {
    setMenuAnchor(null);
  };

  const handleStarToggle = async () => {
    if (selectedMember) {
      try {
        await friendGroupService.updateGroupMember(
          groupId, 
          selectedMember.friendId, 
          { isStarred: !selectedMember.isStarred }
        );
        onMembersChange();
      } catch (error) {
        console.error('更新星标失败:', error);
      }
    }
    handleMenuClose();
  };

  const handleMuteToggle = async () => {
    if (selectedMember) {
      try {
        await friendGroupService.updateGroupMember(
          groupId, 
          selectedMember.friendId, 
          { isMuted: !selectedMember.isMuted }
        );
        onMembersChange();
      } catch (error) {
        console.error('更新静音设置失败:', error);
      }
    }
    handleMenuClose();
  };

  const handleMoveClick = (event: React.MouseEvent) => {
    event.stopPropagation();
    setMoveMenuAnchor(event.currentTarget as HTMLElement);
    handleMenuClose();
  };

  const handleMoveToGroup = async (targetGroupId: string) => {
    if (selectedMember) {
      try {
        await friendGroupService.moveFriendToGroup({
          friendId: selectedMember.friendId,
          targetGroupId
        });
        onMembersChange();
      } catch (error) {
        console.error('移动好友失败:', error);
      }
    }
    setMoveMenuAnchor(null);
  };

  const handleRemoveFromGroup = async () => {
    if (selectedMember) {
      try {
        await friendGroupService.removeFriendFromGroup(groupId, selectedMember.friendId);
        onMembersChange();
      } catch (error) {
        console.error('移除好友失败:', error);
      }
    }
    handleMenuClose();
  };

  const sortedMembers = [...members].sort((a, b) => {
    if (a.isStarred !== b.isStarred) return a.isStarred ? -1 : 1;
    return a.sortOrder - b.sortOrder;
  });

  return (
    <Box sx={{ height: '100%' }}>
      <List sx={{ py: 0 }}>
        {sortedMembers.map((member) => (
          <ListItem
            key={member.id}
            sx={{
              '&:hover': { backgroundColor: 'action.hover' },
              py: 1
            }}
            secondaryAction={
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                {member.isStarred && (
                  <Tooltip title="星标好友">
                    <StarIcon fontSize="small" color="warning" />
                  </Tooltip>
                )}
                {member.isMuted && (
                  <Tooltip title="消息免打扰">
                    <MuteIcon fontSize="small" color="action" />
                  </Tooltip>
                )}
                <Tooltip title="发送消息">
                  <IconButton 
                    size="small" 
                    onClick={() => onStartChat(member.friendId)}
                  >
                    <ChatIcon fontSize="small" />
                  </IconButton>
                </Tooltip>
                <IconButton
                  size="small"
                  onClick={(e) => handleMenuOpen(e, member)}
                >
                  <MoreVertIcon fontSize="small" />
                </IconButton>
              </Box>
            }
          >
            <ListItemAvatar>
              <Avatar src={member.friendAvatar} alt={member.friendName}>
                {member.friendName.charAt(0)}
              </Avatar>
            </ListItemAvatar>
            <ListItemText
              primary={
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <Typography variant="body1">{member.friendName}</Typography>
                </Box>
              }
            />
          </ListItem>
        ))}
      </List>

      {members.length === 0 && (
        <Box sx={{ p: 4, textAlign: 'center' }}>
          <Typography color="text.secondary">
            该分组暂无好友
          </Typography>
          <Typography variant="caption" color="text.secondary" display="block" sx={{ mt: 1 }}>
            从好友列表拖拽好友到此处添加
          </Typography>
        </Box>
      )}

      <Menu anchorEl={menuAnchor} open={Boolean(menuAnchor)} onClose={handleMenuClose}>
        <MenuItem onClick={handleStarToggle}>
          {selectedMember?.isStarred ? (
            <><StarBorderIcon fontSize="small" sx={{ mr: 1 }} /> 取消星标</>
          ) : (
            <><StarIcon fontSize="small" sx={{ mr: 1 }} /> 设为星标</>
          )}
        </MenuItem>
        <MenuItem onClick={handleMuteToggle}>
          {selectedMember?.isMuted ? (
            <><UnmuteIcon fontSize="small" sx={{ mr: 1 }} /> 取消免打扰</>
          ) : (
            <><MuteIcon fontSize="small" sx={{ mr: 1 }} /> 消息免打扰</>
          )}
        </MenuItem>
        <MenuItem onClick={handleMoveClick}>
          <MoveIcon fontSize="small" sx={{ mr: 1 }} />
          移动到其他分组
        </MenuItem>
        <MenuItem onClick={handleRemoveFromGroup}>
          <Typography color="error">从分组移除</Typography>
        </MenuItem>
      </Menu>

      <Menu
        anchorEl={moveMenuAnchor}
        open={Boolean(moveMenuAnchor)}
        onClose={() => setMoveMenuAnchor(null)}
      >
        {groups
          .filter((g) => g.id !== groupId)
          .map((group) => (
            <MenuItem key={group.id} onClick={() => handleMoveToGroup(group.id)}>
              移动到 "{group.name}"
            </MenuItem>
          ))}
      </Menu>
    </Box>
  );
};
